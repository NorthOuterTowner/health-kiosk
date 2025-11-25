import express from 'express';
const router = express.Router();
import { db, genid } from '../db/dbUtils.js';
import User from '../entity/User.js';
import crypto from 'crypto';
import { v4 as uuidv4 } from 'uuid';
import multer from 'multer';
import path from 'path';
import fs from 'fs';
import { generateToken, decodeToken } from '../utils/jwtHelper.js';
import authMiddleware from '../middleware/authMiddleware.js'
import redisClient from '../db/redis.js';

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

router.post("/setEcg",(req,res)=>{
    /**TODO: Adapt ECG data*/
});

router.post("/set/tempor", async (req,res) => {
    let { data, user } = req.body;
    data = parseFloat(data);// store temport data by float form
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

// --- 1. /set/alcohol 路由 (float) ---
router.post("/set/alcohol", async (req, res) => {
    let { data, user } = req.body;
    data = parseFloat(data);// store alcohol data by float form

    if (isNaN(data)) {
        return res.status(400).json({
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
            return res.status(500).json({
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

// --- 2. /set/spo2 路由 (integer) ---
router.post("/set/spo2", async (req, res) => {
    let { data, user } = req.body;
    data = parseInt(data, 10);// store spo2 data by integer form

    if (isNaN(data)) {
        return res.status(400).json({
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
            return res.status(500).json({
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
            return res.status(500).json({
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

// --- 3. /set/ppg 路由 (integer) ---
router.post("/set/ppg", async (req, res) => {
    let { data, user } = req.body;
    data = parseInt(data, 10);// store ppg data by integer form

    if (isNaN(data)) {
        return res.status(400).json({
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
            return res.status(500).json({
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
            return res.status(500).json({
                code:500,
                msg: "上传 ppg 失败"
            });
        }
    } else {
        return res.status(500).json({
                code:500,
                msg: existRes.msg
            });
    }
});

// --- 4. /set/blood_sys 路由 (integer) ---
router.post("/set/blood_sys", async (req, res) => {
    let { data, user } = req.body;
    data = parseInt(data, 10);// store blood_sys data by integer form

    if (isNaN(data)) {
        return res.status(400).json({
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
            return res.status(500).json({
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
            return res.status(500).json({
                code:500,
                msg: "上传 blood_sys 失败"
            });
        }
    } else {
        return res.status(500).json({
                code:500,
                msg: existRes.msg
            });
    }
});

// --- 5. /set/blood_dia 路由 (integer) ---
router.post("/set/blood_dia", async (req, res) => {
    let { data, user } = req.body;
    data = parseInt(data, 10);// store blood_dia data by integer form

    if (isNaN(data)) {
        return res.status(400).json({
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
            return res.status(500).json({
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
            return res.status(500).json({
                code:500,
                msg: "上传 blood_dia 失败"
            });
        }
    } else {
        return res.status(500).json({
                code:500,
                msg: existRes.msg
            });
    }
});

// --- 6. /set/blood_hr 路由 (integer) ---
router.post("/set/blood_hr", async (req, res) => {
    let { data, user } = req.body;
    data = parseInt(data, 10);// store blood_hr data by integer form

    if (isNaN(data)) {
        return res.status(400).json({
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
            return res.status(500).json({
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
            return res.status(500).json({
                code:500,
                msg: "上传 blood_hr 失败"
            });
        }
    } else {
        return res.status(500).json({
                code:500,
                msg: existRes.msg
            });
    }
});


router.get("/userId", (req, res) => {
    const user_id = String(req.query.user_id);

    const page = Number(req.query.page) || 1;
    const limit = Number(req.query.limit) || 20;
    const offset = (page - 1) * limit;
    
    const select_sql = "select * from `data` where `user_id` = ? order by `id` limit ? offset ? ;";
    const {rows, err} = db.async.all(select_sql,[user_id, limit, offset]);

    const cnt_sql = "select COUNT(*) as cnt from `date` where `user_id` = ? ;";
    const {rows: cntRows, err: cntErr} = db.async.all(cnt_sql,[]);
    if(rows.length > 0 && err == null && cntErr == null) {
        return res.status(200).json({
            code:200,
            rows,
            msg:"查询成功",
            cnt: cntRows[0].cnt;
        });
    }else if (rows.length == 0) {
        return res.status(200).json({
            code:200,
            rows:[]
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

export default router;