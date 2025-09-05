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

//近七日用户注册量、用户身份比例
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
    for(row of role_rate_raw_result){
        totalCnt += row.cnt;
    }
    
    let role_rate = role_rate_raw_result.map(row => {
        let roleStr = calRole(row.role);
        return {
            role: roleStr,
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

//各版本设备下载量
router.get("/device/downloadnum", async(req,res)=> {
    const load_num_sql = "select `version`, `typecount`, COUNT(*) as cnt from `device`;";
    const {err,rows} = await db.async.all(load_num_sql,[]);
    if(err==null){
        return res.status(200).json({
            code:200,
            rows
        })
    }
})

module.exports = router