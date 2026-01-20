import express from 'express';
const router = express.Router();
import { db, genid } from '../db/dbUtils.js';
import fs from 'fs';
import authMiddleware from '../middleware/authMiddleware.js';
import Busboy from 'busboy'

function pad(n) {
  return n.toString().padStart(2, '0');
}

function getDateTime() {
  const now = new Date();

  const year = now.getFullYear();
  const month = pad(now.getMonth() + 1);
  const day = pad(now.getDate());

  const hour = pad(now.getHours());
  const minute = pad(now.getMinutes());
  const second = pad(now.getSeconds());

  return {
    date: `${year}-${month}-${day}`,
    time: {
        hour,
        minute,
        second
    }
  };
}

async function decideTime () {
    let { date, time } = getDateTime();
    time.hour = parseInt(time.hour);
    let timePeriod = 1;
    if(time.hour < 12) {
        timePeriod = 1;
    }else if(time.hour > 18) {
        timePeriod = 3;
    }else {
        timePeriod = 2;
    }
    return {
        date,
        time: timePeriod
    }
}

/**
 * @description examine whether the user finished health examination
 * on day and time input.
 * @param {*} date 
 * @param {*} time 
 * @param {*} user 
 * @returns {
 *  exist: boolean,
 *  id: string,
 *  msg: string
 * }
 */
async function examineRowExists(date, time, user) {
    const sql = "select * from `data` where `date` = ? and `time` = ? and `user_id` = ? ;";
    const {err, rows} = await db.async.all(sql,[date,time, user]);
    if(rows.length > 0 && err == null) {
        return {
            exist: true,
            id: rows[0].id,
            msg: "已找到该记录"
        };
    }else if(rows.length == 0) {
        return {
            exist: false,
            id: null,
            msg: "无对应记录"
        }
    }else {
        return {
            exist: null,
            id: null,
            msg: "查找失败"
        }
    };
}

router.post("/set/ecg",(req,res)=>{
    const busboy = Busboy({ headers: req.headers });

    let userId = null;
    let savedFilePath = null;

    const baseDir = path.join("uploads", "ecg");
    if (!fs.existsSync(baseDir)) fs.mkdirSync(baseDir, { recursive: true });

    // 接收字段（meta）
    busboy.on("field", (name, value) => {
    if (name === "account") {
        userId = value;
        console.log("接收到 account =", userId);
    }
    });

    // 接收二进制 ECG 文件（application/octet-stream）
    busboy.on("file", (name, file, info) => {
        if(name != "data") return;

        const { filename, mimeType } = info;
        console.log("接收到文件字段:", name, mimeType);

        const targetName = `${Date.now()}_${userId}.bin`;
        const targetPath = path.join(baseDir, targetName);

        savedFilePath = targetPath;

        const writeStream = fs.createWriteStream(targetPath);
        file.pipe(writeStream);

        writeStream.on("close", () => {
            console.log("ECG 文件已保存：", targetPath);
        });
    });

    // 所有字段 & 文件结束
    busboy.on("close", async () => {
        if (!userId || !savedFilePath) {
            return res.status(200).json({ 
                code: 400, 
                message: "缺少 meta 或 ECG 数据" 
            });
        }

        // insert filename into MySQL
        const {date, time} = await decideTime();
        const existRes = await examineRowExists(date, time, userId);
        console.log(existRes);
        if(existRes.exist == true) {
            const updateSql = "update `data` set `ecg` = ? where id = ? ;";
            try {
                await db.async.run(updateSql,[targetName, existRes.id]);
                return res.status(200).json({
                    code:200,
                    msg: "上传成功"
                });
            }catch(err) {
                console.log(err);
                return res.status(200).json({
                    code:500,
                    msg: "上传失败"
                });
            }
        }else if(existRes.exist == false) {
            const insertSQL = "insert `data` (`user_id`, `ecg`, `date`, `time`) values (?, ?, ?, ?);";
            try {
                await db.async.run(insertSQL,[userId, targetName, date, time]);
                return res.status(200).json({
                    code:200,
                    msg: "上传成功"
                });
            }catch(err) {
                console.log(err);
                return res.status(200).json({
                    code:500,
                    msg: "上传失败"
                });
            }
        }else {
            return res.status(200).json({
                    code:500,
                    msg: existRes.msg
                });
        }
    });

    req.pipe(busboy);
});

/**
 * @api {post} /examData/set/tempor Upload Temperature Data
 * @apiGroup ExamData
 * * @apiParam {Number} data Temperature value (float).
 * @apiParam {String} user User ID.
 * * @apiSuccess {Object} Response:
 * {
 * "code": 200,
 * "msg": "Upload successful"
 * }
 */
router.post("/set/tempor", async (req,res) => {
    let { data, user } = req.body;
    data = parseFloat(data);// store tempor data by float form

    if (isNaN(data)) {
        return res.status(200).json({
            code: 400,
            msg: "数据格式错误，期望 float 类型。"
        });
    }

    const {date, time} = await decideTime();
    const existRes = await examineRowExists(date, time, user);
    console.log(existRes);
    if(existRes.exist == true) {
        const updateSql = "update `data` set `tempor` = ? where id = ? ;";
        try {
            await db.async.run(updateSql,[data, existRes.id]);
            return res.status(200).json({
                code:200,
                msg: "上传成功"
            });
        }catch(err) {
            console.log(err);
            return res.status(200).json({
                code:500,
                msg: "上传失败"
            });
        }
    }else if(existRes.exist == false) {
        const insertSQL = "insert `data` (`user_id`, `tempor`, `date`, `time`) values (?, ?, ?, ?);";
        try {
            await db.async.run(insertSQL,[user, data, date, time]);
            return res.status(200).json({
                code:200,
                msg: "上传成功"
            });
        }catch(err) {
            console.log(err);
            return res.status(200).json({
                code:500,
                msg: "上传失败"
            });
        }
    }else {
        return res.status(200).json({
                code:500,
                msg: existRes.msg
            });
    }
});

/**
 * @api {post} /examData/set/alcohol Upload Alcohol Concentration
 * @apiGroup ExamData
 * * @apiParam {Number} data Alcohol concentration value (float).
 * @apiParam {String} user User ID.
 * * @apiSuccess {Object} Response:
 * {
 * "code": 200,
 * "msg": "Update alcohol successful"
 * }
 */
router.post("/set/alcohol", async (req, res) => {
    let { data, user } = req.body;
    data = parseFloat(data);// store alcohol data by float form

    if (isNaN(data)) {
        return res.status(200).json({
            code: 400,
            msg: "数据格式错误，期望 float 类型。"
        });
    }

    const {date, time} = await decideTime();
    const existRes = await examineRowExists(date, time, user);

    if(existRes.exist == true) {
        const updateSql = "update `data` set `alcohol` = ? where id = ? ;";
        try {
            await db.async.run(updateSql,[data, existRes.id]);
            return res.status(200).json({
                code:200,
                msg: "更新 alcohol 成功"
            });
        } catch(err) {
            return res.status(200).json({
                code:500,
                msg: "上传 alcohol 失败"
            });
        }
    } else if(existRes.exist == false) {
        const insertSQL = "insert `data` (`user_id`, `alcohol`, `date`, `time`) values (?, ?, ?, ?);";
        try {
            await db.async.run(insertSQL,[user, data, date, time]);
            return res.status(200).json({
                code:200,
                msg: "插入 alcohol 成功"
            });
        } catch(err) {
            return res.status(500).json({
                code:500,
                msg: "上传 alcohol 失败"
            });
        }
    } else {
        return res.status(500).json({
                code:500,
                msg: existRes.msg
            });
    }
});

/**
 * @api {post} /examData/set/spo2 Upload SpO2 Data
 * @apiGroup ExamData
 * * @apiParam {Number} data Blood Oxygen Saturation (SpO2) value (integer).
 * @apiParam {String} user User ID.
 * * @apiSuccess {Object} Response:
 * {
 * "code": 200,
 * "msg": "Update spo2 successful"
 * }
 */
router.post("/set/spo2", async (req, res) => {
    let { data, user } = req.body;
    data = parseInt(data, 10);// store spo2 data by integer form

    if (isNaN(data)) {
        return res.status(200).json({
            code: 400,
            msg: "数据格式错误，期望 integer 类型。"
        });
    }

    const {date, time} = await decideTime();
    const existRes = await examineRowExists(date, time, user);

    if(existRes.exist == true) {
        const updateSql = "update `data` set `spo2` = ? where id = ? ;";
        try {
            await db.async.run(updateSql,[data, existRes.id]);
            return res.status(200).json({
                code:200,
                msg: "更新 spo2 成功"
            });
        } catch(err) {
            return res.status(200).json({
                code:500,
                msg: "上传 spo2 失败"
            });
        }
    } else if(existRes.exist == false) {
        const insertSQL = "insert `data` (`user_id`, `spo2`, `date`, `time`) values (?, ?, ?, ?);";
        try {
            await db.async.run(insertSQL,[user, data, date, time]);
            return res.status(200).json({
                code:200,
                msg: "插入 spo2 成功"
            });
        } catch(err) {
            return res.status(200).json({
                code:500,
                msg: "上传 spo2 失败"
            });
        }
    } else {
        return res.status(500).json({
                code:500,
                msg: existRes.msg
            });
    }
});

/**
 * @api {post} /examData/set/ppg Upload PPG Data
 * @apiGroup ExamData
 * * @apiParam {Number} data Photoplethysmography (PPG) value (integer).
 * @apiParam {String} user User ID.
 * * @apiSuccess {Object} Response:
 * {
 * "code": 200,
 * "msg": "Update ppg successful"
 * }
 */
router.post("/set/ppg", async (req, res) => {
    let { data, user } = req.body;
    data = parseInt(data, 10);// store ppg data by integer form

    if (isNaN(data)) {
        return res.status(200).json({
            code: 400,
            msg: "数据格式错误，期望 integer 类型。"
        });
    }

    const {date, time} = await decideTime();
    const existRes = await examineRowExists(date, time, user);

    if(existRes.exist == true) {
        const updateSql = "update `data` set `ppg` = ? where id = ? ;";
        try {
            await db.async.run(updateSql,[data, existRes.id]);
            return res.status(200).json({
                code:200,
                msg: "更新 ppg 成功"
            });
        } catch(err) {
            return res.status(200).json({
                code:500,
                msg: "上传 ppg 失败"
            });
        }
    } else if(existRes.exist == false) {
        const insertSQL = "insert `data` (`user_id`, `ppg`, `date`, `time`) values (?, ?, ?, ?);";
        try {
            await db.async.run(insertSQL,[user, data, date, time]);
            return res.status(200).json({
                code:200,
                msg: "插入 ppg 成功"
            });
        } catch(err) {
            return res.status(200).json({
                code:500,
                msg: "上传 ppg 失败"
            });
        }
    } else {
        return res.status(200).json({
                code:500,
                msg: existRes.msg
            });
    }
});

/**
 * @api {post} /examData/set/blood_sys Upload Systolic Blood Pressure
 * @apiGroup ExamData
 * * @apiParam {Number} data Systolic Blood Pressure value (integer).
 * @apiParam {String} user User ID.
 * * @apiSuccess {Object} Response:
 * {
 * "code": 200,
 * "msg": "Update blood_sys successful"
 * }
 */
router.post("/set/blood_sys", async (req, res) => {
    let { data, user } = req.body;
    data = parseInt(data, 10);// store blood_sys data by integer form

    if (isNaN(data)) {
        return res.status(200).json({
            code: 400,
            msg: "数据格式错误，期望 integer 类型。"
        });
    }

    const {date, time} = await decideTime();
    const existRes = await examineRowExists(date, time, user);

    if(existRes.exist == true) {
        const updateSql = "update `data` set `blood_sys` = ? where id = ? ;";
        try {
            await db.async.run(updateSql,[data, existRes.id]);
            return res.status(200).json({
                code:200,
                msg: "更新 blood_sys 成功"
            });
        } catch(err) {
            return res.status(200).json({
                code:500,
                msg: "上传 blood_sys 失败"
            });
        }
    } else if(existRes.exist == false) {
        const insertSQL = "insert `data` (`user_id`, `blood_sys`, `date`, `time`) values (?, ?, ?, ?);";
        try {
            await db.async.run(insertSQL,[user, data, date, time]);
            return res.status(200).json({
                code:200,
                msg: "插入 blood_sys 成功"
            });
        } catch(err) {
            return res.status(200).json({
                code:500,
                msg: "上传 blood_sys 失败"
            });
        }
    } else {
        return res.status(200).json({
                code:500,
                msg: existRes.msg
            });
    }
});

/**
 * @api {post} /examData/set/blood_dia Upload Diastolic Blood Pressure
 * @apiGroup ExamData
 * * @apiParam {Number} data Diastolic Blood Pressure value (integer).
 * @apiParam {String} user User ID.
 * * @apiSuccess {Object} Response:
 * {
 * "code": 200,
 * "msg": "Update blood_dia successful"
 * }
 */
router.post("/set/blood_dia", async (req, res) => {
    let { data, user } = req.body;
    data = parseInt(data, 10);// store blood_dia data by integer form

    if (isNaN(data)) {
        return res.status(200).json({
            code: 400,
            msg: "数据格式错误，期望 integer 类型。"
        });
    }

    const {date, time} = await decideTime();
    const existRes = await examineRowExists(date, time, user);

    if(existRes.exist == true) {
        const updateSql = "update `data` set `blood_dia` = ? where id = ? ;";
        try {
            await db.async.run(updateSql,[data, existRes.id]);
            return res.status(200).json({
                code:200,
                msg: "更新 blood_dia 成功"
            });
        } catch(err) {
            return res.status(200).json({
                code:500,
                msg: "上传 blood_dia 失败"
            });
        }
    } else if(existRes.exist == false) {
        const insertSQL = "insert `data` (`user_id`, `blood_dia`, `date`, `time`) values (?, ?, ?, ?);";
        try {
            await db.async.run(insertSQL,[user, data, date, time]);
            return res.status(200).json({
                code:200,
                msg: "插入 blood_dia 成功"
            });
        } catch(err) {
            return res.status(200).json({
                code:500,
                msg: "上传 blood_dia 失败"
            });
        }
    } else {
        return res.status(200).json({
                code:500,
                msg: existRes.msg
            });
    }
});

/**
 * @api {post} /examData/set/blood_hr Upload Heart Rate
 * @apiGroup ExamData
 * * @apiParam {Number} data Heart Rate (HR) value (integer).
 * @apiParam {String} user User ID.
 * * @apiSuccess {Object} Response:
 * {
 * "code": 200,
 * "msg": "Update blood_hr successful"
 * }
 */
router.post("/set/blood_hr", async (req, res) => {
    let { data, user } = req.body;
    data = parseInt(data, 10);// store blood_hr data by integer form

    if (isNaN(data)) {
        return res.status(200).json({
            code: 400,
            msg: "数据格式错误，期望 integer 类型。"
        });
    }

    const {date, time} = await decideTime();
    const existRes = await examineRowExists(date, time, user);

    if(existRes.exist == true) {
        const updateSql = "update `data` set `blood_hr` = ? where id = ? ;";
        try {
            await db.async.run(updateSql,[data, existRes.id]);
            return res.status(200).json({
                code:200,
                msg: "更新 blood_hr 成功"
            });
        } catch(err) {
            return res.status(200).json({
                code:500,
                msg: "上传 blood_hr 失败"
            });
        }
    } else if(existRes.exist == false) {
        const insertSQL = "insert `data` (`user_id`, `blood_hr`, `date`, `time`) values (?, ?, ?, ?);";
        try {
            await db.async.run(insertSQL,[user, data, date, time]);
            return res.status(200).json({
                code:200,
                msg: "插入 blood_hr 成功"
            });
        } catch(err) {
            return res.status(200).json({
                code:500,
                msg: "上传 blood_hr 失败"
            });
        }
    } else {
        return res.status(200).json({
                code:500,
                msg: existRes.msg
            });
    }
});

router.get("/get/ecg", async (req, res) => {
    /**TODO: Get ECG data on stream. */
});

/**
 * @api {get} /userId Get User Health Data
 * @apiGroup ExamData
 * * @apiQuery {String} user_id User's unique ID.
 * @apiQuery {Number} [page=1] Page number (starting from 1).
 * @apiQuery {Number} [limit=20] Number of items per page.
 * * @apiSuccess {Object} Response:
 * {
 * "code": 200,
 * "rows": [
 * {
 * "id": 1,
 * "user_id": "user001",
 * "tempor": 36.5,
 * "alcohol": 0.05,
 * "spo2": 98,
 * "ppg": 85,
 * "blood_sys": 120,
 * "blood_dia": 80,
 * "blood_hr": 75,
 * "date": "2023-11-25",
 * "time": 2
 * }
 * ],
 * "msg": "Query successful",
 * "cnt": 100
 * }
 */
router.get("/userId", authMiddleware, async (req, res) => {
    const user_id = req.account;

    const page = Number(req.query.page) || 1;
    const limit = Number(req.query.limit) || 20;
    const offset = (page - 1) * limit;
    
    const select_sql = "select * from `data` where `user_id` = ? order by `id` limit ? offset ? ;";
    const {rows, err} = await db.async.all(select_sql,[user_id, limit, offset]);

    const cnt_sql = "select COUNT(*) as cnt from `data` where `user_id` = ? ;";
    const {rows: cntRows, err: cntErr} = await db.async.all(cnt_sql,[user_id]);
    if(rows.length > 0 && err == null && cntErr == null) {
        return res.status(200).json({
            code:200,
            rows,
            msg:"查询成功",
            cnt: cntRows[0].cnt
        });
    }else if (rows.length == 0) {
        return res.status(200).json({
            code:200,
            rows:[],
            msg:"查询成功",
            cnt:0
        });
    }else {
        return res.status(200).json({
            code:500,
            rows: null,
            msg: err.msg,
            cnt: null
        })
    }
});

router.post("/download", authMiddleware, async (req, res) => {
    try {
        const user_id = req.account;

        const {
            start_date,
            end_date,
            file_type
        } = req.body;

        if (!start_date || !end_date) {
            return res.status(400).json({
                code: 400,
                msg: "缺少时间范围参数"
            });
        }

        // ===== 2. 查询数据库 =====
        const select_sql = `
            SELECT *
            FROM \`data\`
            WHERE \`user_id\` = ?
              AND \`date\` BETWEEN ? AND ?
            ORDER BY \`id\`
        `;

        const { rows, err } = await db.async.all(select_sql, [
            user_id,
            start_date,
            end_date
        ]);

        if (err) {
            return res.status(500).json({
                code: 500,
                msg: err.msg
            });
        }

        if (!rows || rows.length === 0) {
            return res.status(200).json({
                code: 200,
                msg: "无可导出的数据"
            });
        }

        // ===== 3. 生成 CSV 文件内容 =====
        const headers = Object.keys(rows[0]).join(",");

        const csvBody = rows.map(row =>
            Object.values(row)
                .map(v => `"${v ?? ""}"`)
                .join(",")
        ).join("\n");

        const csvContent = headers + "\n" + csvBody;

        // ===== 4. 设置下载响应头 =====
        const filename = `data_${start_date}_${end_date}.csv`;

        res.setHeader("Content-Type", "text/csv; charset=utf-8");
        res.setHeader(
            "Content-Disposition",
            `attachment; filename=${encodeURIComponent(filename)}`
        );

        // ===== 5. 返回文件 =====
        res.send(csvContent);

    } catch (e) {
        res.status(500).json({
            code: 500,
            msg: "服务器内部错误"
        });
    }
});


/**
 * @api {post} /data/delete 删除体检数据记录
 * @apiName DeleteExamRecord
 * @apiGroup ExamData
 * @apiPermission admin
 *
 * @apiDescription 管理员删除指定体检数据记录
 *
 * @apiHeader {String} Authorization 用户登录后的 JWT Token
 *
 * @apiParam {Number} record_id 体检数据记录 ID
 *
 * @apiSuccess {Number} code 状态码
 * @apiSuccess {String} msg 返回信息
 */
router.post("/delete", authMiddleware, async (req, res) => {
    try {
        const { record_id } = req.body;

        if (req.role < 3) {
            return res.status(200).json({
                code: 403,
                msg: "仅管理员权限可以删除记录"
            });
        }

        if (!record_id || isNaN(Number(record_id))) {
            return res.status(200).json({
                code: 400,
                msg: "record_id 参数非法"
            });
        }

        const deleteSql = "DELETE FROM `data` WHERE `id` = ?";

        await db.async.run(deleteSql, [record_id]);

        return res.status(200).json({
            code: 200,
            msg: "删除成功"
        });

    } catch (err) {
        console.error("删除体检数据失败:", err);
        return res.status(200).json({
            code: 500,
            msg: "服务器内部错误"
        });
    }
});

export default router;