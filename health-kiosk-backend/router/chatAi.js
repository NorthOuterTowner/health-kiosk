import express from 'express';
const router = express.Router();
import { db, genid } from '../db/dbUtils.js';

const SILICON_API_KEY = process.env.SILICON_API_KEY;

// 查询数据函数，根据完整字段
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
你是一名专业健康监测数据分析助手，擅长根据多维生理指标评估用户的健康状况、潜在风险，并提供科学的改善建议。

【数据库字段说明】
以下为单条数据记录的字段含义：
- tempor: 体温（℃）
- alcohol: 酒精摄入量（单位：ml 或标准杯）
- spo2: 血氧饱和度（%）
- ppg: 光电容积脉搏波值（用于判断血流情况）
- blood_sys: 收缩压（mmHg）
- blood_dia: 舒张压（mmHg）
- blood_hr: 心率（bpm）
- date: 测试日期
- time: 测试时间段（例如 1=早晨，2=下午... 由系统定义）

【分析任务要求】
请根据给定的全部用户监测数据，完成如下分析：

1. 体温评估
   - 是否发热（≥37.3℃）、低热、或体温异常波动
   - 是否可能存在感染、炎症反应或代谢异常

2. 血氧 SPO2 分析
   - 是否低于 95%，是否可能存在呼吸系统或循环系统风险
   - 是否存在波动较大或长期偏低情况

3. 血压分析（blood_sys / blood_dia）
   - 是否存在高血压等级（根据国际参考：≥130/80）
   - 是否存在低血压风险（<90/60）
   - 压差（脉压）是否正常

4. 心率分析（blood_hr）
   - 是否 tachycardia（>100 bpm）或 bradycardia（<60 bpm）
   - 心率是否随酒精或体温波动出现异常

5. 酒精摄入量影响
   - 是否存在高频或高剂量饮酒
   - 判断是否影响心率、血压或 SPO2

6. 光电容积脉搏波 PPG
   - 判断血流强弱变化趋势（无需具体医疗诊断）

7. 多维交叉健康分析
   - 例如体温升高 + SPO2 下降
   - 酒精摄入 + 心率上升
   - 高血压 + 心率异常

8. 趋势分析
   - 判断该用户在此时间段内指标总体是改善、恶化还是稳定

9. 严重风险检测
   - SPO2 < 90
   - 高热 ≥ 39℃
   - 收缩压 ≥ 160 或 舒张压 ≥ 100
   - 心率 > 130 或 < 45
   如出现请特别提醒，但不要给出明确医学诊断。

【输出格式要求】
请严格按以下 JSON 结构输出：
{
  "summary": "总体健康状况总结",
  "potential_risks": ["风险1", "风险2", ...],
  "detailed_analysis": {
      "temperature": "体温分析",
      "spo2": "血氧分析",
      "blood_pressure": "血压分析",
      "heart_rate": "心率分析",
      "alcohol": "酒精影响分析",
      "ppg": "PPG 分析",
      "correlation": "交叉关联分析",
      "trend": "趋势分析"
  },
  "recommendations": ["建议1", "建议2", "建议3"]
}

下面是用户的实际健康数据：
${JSON.stringify(health_data, null, 2)}
`;
}

/**
 * {
    "code": 200,
    "msg": "AI analysis success",
    "data": {
        "id": "019ad8839e3b7c069134a456c2dff976",
        "object": "chat.completion",
        "created": 1764569096,
        "model": "Qwen/QwQ-32B",
        "choices": [
            {
                "index": 0,
                "message": {
                    "role": "assistant",
                    "content": "\n\n```json\n{\n  \"summary\": \"用户近期健康指标整体处于可控范围内，但出现血压升高、单日低体温波动及心率异常变化，需关注长期趋势和潜在风险因素。\",\n  \"potential_risks\": [\n    \"收缩压持续升高（最高达140mmHg）\",\n    \"心率异常波动（最高达105bpm）\",\n    \"体温接近发热临界值（最高达37.3℃）\",\n    \"SPO2偶发下降（最低至96%）\"\n  ],\n  \"detailed_analysis\": {\n    \"temperature\": \"体温整体在36.4-37.3℃之间波动，其中11/27（37.3℃）、11/30（37.2℃）接近发热临界值，建议监测伴随症状（如咳嗽/乏力）。体温波动与当日饮酒无明显对应关系。\",\n    \"spo2\": \"平均98%，两日出现轻度下降（11/25/SPO2 96%，11/30/SPO2 96%）。需结合呼吸系统症状（如胸闷）进一步观察，无长期低氧风险但存在短期波动。\",\n    \"blood_pressure\": \"收缩压逐步升高（首日118→末日140mmHg），第10日收缩压140/92mmHg达到高血压标准（≥130/80mmHg），脉压差正常（约48-52mmHg）。需关注血压升高的诱因（含当日饮酒因素）。\",\n    \"heart_rate\": \"心率分布在68-105bpm，第10日达105bpm（tachycardia）。心率上升与当日高剂量饮酒（40ml）和血压升高呈正相关。\",\n    \"alcohol\": \"单日最大饮酒量40ml，3日有饮酒记录但频次未达高频。饮酒后（如11/24、11/27、11/30）血压、心率均出现显著上升，建议避免饮酒与血压监测时段重叠。\",\n    \"ppg\": \"最新PPG值在71-84区间，数值波动与心率变化方向一致，提示血流状态可能受应激性升高影响（如心率增快时PPG上升）。\",\n    \"ecg\": \"未提供心电波形数据，但结合心率-血压连动模式（饮酒后心率快/血压高），建议关注是否存在窦性心动过速等节律异常。\",\n    \"correlation\": \"存在交叉风险组合：①高温日伴随SPO2下降（11/25）；②饮酒量与血压/心率正相关（R≈0.65）；③最后两日体温升高/血压增压的叠加效应。\",\n    \"trend\": \"10日期间收缩压均值上升6%，舒张压升高4%，心率波动范围扩大（标准差+23%）。建议缩短监测间隔至1-2日/次评估改善效果。\"\n  },\n  \"recommendations\": [\n    \"早晨空腹测量血压以排除饮酒对数据的影响\",\n    \"控制单日酒精摄入不超过20ml，尤其注意饮酒与血压监测时间间隔\",\n    \"连续3日体温≥37.0℃时需就诊排查感染诱因\",\n    \"监测静息心率是否持续超过85bpm并记录对应环境因素\"\n  ]\n}\n```",
                },
                "finish_reason": "stop"
            }
        ],
        "usage": {
            "prompt_tokens": 1846,
            "completion_tokens": 1878,
            "total_tokens": 3724,
            "completion_tokens_details": {
                "reasoning_tokens": 1108
            }
        },
        "system_fingerprint": ""
    }
}
 */
router.post("/analyze", async (req, res) => {
    console.log("received AI request");

    const { start, end, user_id } = req.body || {};

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
            model: "Qwen/Qwen3-8B",
            messages: [{ role: "user", content: prompt }],
            stream: true,
            enable_thinking: false,
        })
    };

    try {
        const response = await fetch(url, options);
        const data = await response.json();

        return res.status(200).json({
            code: 200,
            msg: "AI analysis success",
            data
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

export default router;
