const {db,genId} = require("../db/dbUtils")
const express = require('express')
const router = express.Router()

function calRole(role){
    switch(role){
        case 0:
            return "游客"
        case 1:
            return "访客"
        case 2:
            return "用户"
        case 3:
            return "管理员"
        case 4:
            return "超级管理员"
        case 5:
            return "开发者"
        default:
            return "unknown"
    }
}

/**
 * @api {get} /user/userRegister Get User Registration Statistics
 * @apiGroup User
 * 
 * @apiHeader {String} Authorization User login token in the format: "Bearer <token>"
 * 
 * @apiSuccess {Object[]} register_counts Array of registration counts in the last 7 days
 * @apiSuccess {String} register_counts.date Date of registration
 * @apiSuccess {Number} register_counts.cnt Number of users registered on that date
 * 
 * @apiSuccess {Object[]} role_rate Array of user role rates
 * @apiSuccess {String} role_rate.role Role name
 * @apiSuccess {Number} role_rate.rate Percentage of total users for this role (0~1)
 * 
 * @apiSuccess {Object} Response Example (Success):
 * {
 *   "code": 200,
 *   "register_counts": [
 *     { "date": "2025-09-07", "cnt": 5 },
 *     { "date": "2025-09-08", "cnt": 12 },
 *      ...
 *   ],
 *   "role_rate": [
 *     { "role": "Admin", "rate": 0.1 },
 *     { "role": "User", "rate": 0.2 },
 *      ...
 *   ]
 * }
 */
router.get("/userRegister",async (req,res) => {
    const register_cnt_sql = `SELECT 
        DATE(register_time) AS date,
        COUNT(*) AS cnt
        FROM user
        WHERE register_time >= CURDATE() - INTERVAL 6 DAY
        GROUP BY DATE(register_time)
        ORDER BY date;
    `;
    const { err: register_cnt_err, rows: register_cnt_row } = await db.async.all(register_cnt_sql,[]);
    
    const role_rate_sql = `SELECT
        role, COUNT(*) AS cnt
        FROM user 
        GROUP BY role;
    `;
    const { err: role_rate_err, rows: role_rate_raw_result } = await db.async.all(role_rate_sql,[]);
    
    let totalCnt = 0;
    for(let row of role_rate_raw_result){
        totalCnt += row.cnt;
    }
    
    let role_rate = role_rate_raw_result.map(row => {
        //let roleStr = calRole(row.role);
        return {
            role: row.role,
            rate: row.cnt / totalCnt
        }
    });

    if(register_cnt_err == null && role_rate_err == null){
        console.log(register_cnt_row)
        console.log(role_rate)
        return res.status(200).json({
            code:200,
            register_counts:register_cnt_row,
            role_rate
        })
    }else{
        return res.status(200).json({
            code:500,
            msg:err.message
        })
    }
});

/**
 * @api {get} /device/downloadnum Get Device Download Counts
 * @apiGroup Device
 * 
 * @apiHeader {String} Authorization User login token in the format: "Bearer <token>"
 * 
 * @apiSuccess {Object[]} rows Array of device download counts
 * @apiSuccess {String} rows.version Device version
 * @apiSuccess {Number} rows.cnt Number of downloads for this version
 * 
 * @apiSuccess {Object} Response Example (Success):
 * {
 *   "code": 200,
 *   "rows": [
 *     { "version": "1.0.0", "cnt": 120 },
 *     { "version": "1.1.0", "cnt": 85 }
 *   ]
 * }
 */
router.get("/device/downloadnum", async(req,res)=> {
    const load_num_sql = "select `version`, `num` as cnt from `device`;";
    const {err,rows} = await db.async.all(load_num_sql,[]);
    if(err == null){
        return res.status(200).json({
            code:200,
            rows
        })
    }else {
        return res.status(500).json({
            code:500,
            msg:"服务器错误"
        })
    }
});

module.exports = router