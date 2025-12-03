import express from 'express';
const router = express.Router();
import { db, genid } from '../db/dbUtils.js';

const SILICON_API_KEY = process.env.SILICON_API_KEY;

async function get_data(startDate, endDate, user_id) {
    const sql = `
        SELECT \`tempor\`,\`alcohol\`,\`spo2\`,\`ppg\`,\`blood_sys\`,\`blood_dia\`,\`blood_hr\`,\`date\`,\`time\`
        FROM data
        WHERE 
        date >= ? 
        AND date <= ? 
        AND user_id = ?;
    `;

    try {
        const { rows, err } = await db.async.all(sql, [startDate, endDate, user_id]);
        if (err) {
            console.error("DB Error:", err);
            return [];
        }
        return rows || [];
    } catch (error) {
        console.error("DB exception:", error);
        return [];
    }
}

function buildPrompt(health_data) {
    return `
你是一名健康监测分析助手。请仅基于下面提供的用户生理指标数据，按照严格的 JSON 格式输出潜在风险，并分析单项结果和对应原因（仅返回 JSON，不要包含额外文字、解释、代码块或思考过程），原因简要返回，每个指标的原因不超过15个字，并返回对用户身体健康的建议，所有内容用中文。

数据字段：
- tempor：体温（℃）
- alcohol：饮酒量（单位）
- spo2：血氧饱和度（%）
- ppg：光电容积脉搏波
- blood_sys：收缩压（mmHg）
- blood_dia：舒张压（mmHg）
- blood_hr：心率（bpm）
- date：日期（YYYY-MM-DD）
- time：时间段（1=上午，2=下午）

分析要求（用于内部判断，不要在输出中体现）：
1. 体温：判断是否发热（≥37.3℃）或异常波动；
2. 血氧：判断是否低于 95% 或持续下降；
3. 血压：判断是否偏高（参考 ≥130/80 为高风险，≥160/100 为严重风险）；
4. 心率：判断是否过快（>100）或过慢（<60）；
5. 酒精影响：判断饮酒是否导致心率/血压上升；
6. PPG：判断是否出现显著异常波动；
7. 多指标关联：如饮酒后心率/血压上升、体温升高伴随 SPO2 降低；
8. 趋势分析：指标是否改善/恶化；
9. 严重风险：若满足 SPO2<90、收缩压≥160、舒张压≥100、心率>130、体温≥39，则加入 potential_risks；否则不加入。

重要输出规则：
- 必须严格返回下面 JSON 结构；
- 若某项指标“正常或基本正常”，对应字段必须返回空字符串 ""；
- 若不存在潜在风险，potential_risks 返回空数组 []；
- recommendations 若无必要则返回 []；
- 输出仅为 JSON，不包含额外内容。

最终输出 JSON 结构如下（必须严格遵守键名）：

{
  "potential_risks": [],
  "detailed_analysis": {
    "temperature": "",
    "spo2": "",
    "blood_pressure": "",
    "heart_rate": "",
    "alcohol": "",
    "ppg": "",
    "correlation": "",
    "trend": "",
    "reason":"",
  },
  "recommendations": []
}

下面是用户的数据（JSON）：
${JSON.stringify(health_data, null, 2)}
`;
}

function cleanJSON(str) {
    // 去掉对象或数组中结尾的多余逗号
    return str
        // 去掉 } 前的逗号
        .replace(/,\s*}/g, "}")
        // 去掉 ] 前的逗号
        .replace(/,\s*]/g, "]");
}

/**
 * @api {post} /chatAi/analyze 健康数据分析
 * @apiName AnalyzeHealthData
 * @apiGroup AI
 * @apiVersion 1.0.0
 * @apiDescription
 * 调用 AI 模型分析用户一段时间的健康监测数据。返回潜在风险、详细指标分析和整体趋势。
 *
 * @apiBody {String} start 开始日期 (YYYY-MM-DD)
 * @apiBody {String} end 结束日期 (YYYY-MM-DD)
 * @apiBody {String} user_id 用户 ID
 * @apiBody {String} [model] 使用的模型，可选，默认 Qwen/Qwen3-8B
 *
 * @apiSuccess {json} 
 * {
 *   "code": 200,
 *   "msg": "AI analysis success",
 *   "data": {
 *     "potential_risks": [],
 *     "detailed_analysis": {
 *       "temperature": "",
 *       "spo2": "",
 *       "blood_pressure": "",
 *       "heart_rate": "",
 *       "alcohol": "",
 *       "ppg": "",
 *       "correlation": "",
 *       "trend": ""
 *     },
 *     "recommendations": []
 *   }
 * }
 */
router.post("/analyze", async (req, res) => {
    const { start, end, user_id, model } = req.body || {};

    const use_model = model || "Qwen/Qwen3-8B";
    //"QwQ-32B"
    
    if (!start || !end || !user_id) {
        return res.status(400).json({
            code: 400,
            msg: "missing required parameters: start, end, user_id"
        });
    }

    const health_data = await get_data(start, end, user_id);

    const prompt = buildPrompt(health_data);

    const url = "https://api.siliconflow.cn/v1/chat/completions";
    const options = {
        method: "POST",
        headers: {
            Authorization: `Bearer ${SILICON_API_KEY}`,
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            model: use_model,
            messages: [{ role: "user", content: prompt }],
            stream: false,
            thinking_budget: 128    
        })
    };

    try {
        const response = await fetch(url, options);
        const data = await response.json();

        const raw = data.choices?.[0]?.message?.content || "{}";
        const cleanFirst = cleanJSON(raw);
        const cleanData = JSON.parse(cleanFirst);
        
        return res.status(200).json({
            code: 200,
            msg: "AI analysis success",
            data: cleanData
        });
    } catch (error) {
        console.error("AI request error:", error);
        return res.status(500).json({
            code: 500,
            msg: "AI request failed",
            error: error.toString()
        });
    }
});

async function search_corpus(req_id) {
    req_id = Number(req_id);
    const sql = "select `request`,`response` from `corpus` where `id` = ? ;";
    const { err, rows } = await db.async.all(sql,[req_id]);

    if(err) {
        return {
            code: 0,
            request: "",
            response: ""
        };
    }else if (rows.length === 0) {
        return {
            code: 1,
            request: "",
            response: ""
        };
    }else{
        const get_req = rows[0].request;
        const get_res = rows[0].response;

        return {
            code: 2,
            request: get_req,
            response: get_res
        }
    }
}

/**
 * @api {post} /chat AI 对话接口
 * @apiName ChatWithAI
 * @apiGroup Chat
 * @apiVersion 1.0.0
 * @apiDescription 调用 SiliconFlow 或查询 corpus 获取回复。
 * @apiBody {String} [model="Qwen/Qwen3-8B"] 使用的模型名
 * @apiBody {String} [req_id] 可选，用于查询 corpus
 * @apiBody {String} input 用户输入文本（AI Prompt）
 *
 * @apiSuccess {Number} code 返回码（200 表成功）
 * @apiSuccess {String} msg 消息
 * @apiSuccess {String} data AI 返回文本内容（raw）
 */
router.post("/chat", async (req,res) => {
    const { model, req_id, input } = req.body || {};
    if(req_id) {
        const corpus_res = await search_corpus(req_id);

        if(corpus_res.code === 0) {
            return res.status(200).json({
                code: 500,
                msg: "corpus",
                data: ""
            })
        }else if(corpus_res.code === 2){
            return res.status(200).json({
                code: 200,
                msg: "corpus",
                data: corpus_res.response
            });
        }
    }
    
    const use_model = model || "Qwen/Qwen3-8B";
    //"QwQ-32B"

    const url = "https://api.siliconflow.cn/v1/chat/completions";
    const options = {
        method: "POST",
        headers: {
            Authorization: `Bearer ${SILICON_API_KEY}`,
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            model: use_model,
            messages: [{ role: "user", content: input }],
            stream: false,
            thinking_budget: 128    
        })
    };

    try {
        const response = await fetch(url, options);
        const data = await response.json();

        const raw = data.choices?.[0]?.message?.content;
        
        return res.status(200).json({
            code: 200,
            msg: "AI analysis success",
            data: raw
        });
    } catch (error) {
        console.error("AI request error:", error);
        return res.status(500).json({
            code: 500,
            msg: "AI request failed",
            error: error.toString()
        });
    }
});

/**
 * @api {get} /getCorpus 获取语料列表
 * @apiName GetCorpus
 * @apiGroup Corpus
 * @apiVersion 1.0.0
 * @apiDescription 分页获取 corpus 数据。建议使用查询参数：`/getCorpus?page=1&limit=10`
 *
 * @apiQuery {Number} page 页码，从 1 开始（默认 1）
 * @apiQuery {Number} limit 每页数量（默认 10）
 *
 * @apiSuccess {json} success 返回结构（成功）
 * {
 *   "code": 200,
 *   "data": [
 *     {
 *       "id": 1,
 *       "request": "你好",
 *       "response": "你好呀！"
 *     }
 *   ]
 * }
 */
router.get("/getCorpus", async (req, res) => {
    const { page = 1, limit = 10 } = req.query; // 建议用 query
    const offset = (page - 1) * limit;

    const select_corpus = "SELECT `id`, `request`, `response` FROM `corpus` LIMIT ? OFFSET ?;";

    const { err, rows } = await db.async.all(select_corpus, [Number(limit), Number(offset)]);

    if (err) {
        return res.status(200).json({
            code: 500,
            msg: "服务器错误"
        });
    } else {
        return res.status(200).json({
            code: 200,
            data: rows
        });
    }
});

/**
 * @api {post} /updateCorpus 更新语料
 * @apiName UpdateCorpus
 * @apiGroup Corpus
 * @apiVersion 1.0.0
 * @apiDescription 根据 id 更新一条语料记录。请求体为 JSON：{ id, request, response }
 *
 * @apiBody {Number} id 语料 ID（必填）
 * @apiBody {String} request 更新后的问题（可选）
 * @apiBody {String} response 更新后的回答（可选）
 *
 * @apiSuccess {json} success 更新成功示例
 * {
 *   "code": 200,
 *   "msg": "更新成功"
 * }
 */
router.post("/updateCorpus", async (req, res) => {
    const { id, request, response } = req.body || {};

    if (!id) {
        return res.status(200).json({
            code: 400,
            msg: "缺少 id"
        });
    }

    const update_sql = `
        UPDATE corpus 
        SET request = ?, response = ?
        WHERE id = ?;
    `;

    const { err } = await db.async.run(update_sql, [request, response, id]);

    if (err) {
        return res.status(200).json({
            code: 500,
            msg: "更新失败"
        });
    }

    return res.status(200).json({
        code: 200,
        msg: "更新成功"
    });
});

/**
 * @api {post} /addCorpus 新增语料
 * @apiName AddCorpus
 * @apiGroup Corpus
 * @apiVersion 1.0.0
 * @apiDescription 添加一条新的语料记录。请求体为 JSON：{ request, response }
 *
 * @apiBody {String} request 问题（必填）
 * @apiBody {String} response 回答（必填）
 *
 * @apiSuccess {json} success 添加成功示例
 * {
 *   "code": 200,
 *   "msg": "添加成功"
 * }
 */
router.post("/addCorpus", async (req, res) => {
    const { request, response } = req.body || {};

    if (!request || !response) {
        return res.status(200).json({
            code: 400,
            msg: "缺少 request 或 response"
        });
    }

    const insert_sql = `
        INSERT INTO corpus (request, response)
        VALUES (?, ?);
    `;

    const { err } = await db.async.run(insert_sql, [request, response]);

    if (err) {
        return res.status(200).json({
            code: 500,
            msg: "添加失败"
        });
    }

    return res.status(200).json({
        code: 200,
        msg: "添加成功"
    });
});

export default router;
