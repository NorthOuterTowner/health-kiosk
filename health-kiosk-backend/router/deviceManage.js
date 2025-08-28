const express = require('express')
const router = express.Router()
const {db,genid} = require('../db/dbUtils');
const { decodeToken } = require('../utils/jwtHelper');

const path = require('path')
const fs = require("fs");

function calculate_adk_position(version,type){
    let type_string,relative_str,apk_name;
    if(type === '1'){
        type_string = "release"
    }else{
        type_string = "debug"
    }
    apk_name = "HealthKiosk-" + type_string + "-v" + version + ".apk";
    relative_str = "../../health-kiosk-android/app/release/"+apk_name;
    const apk_position = path.join(__dirname,relative_str);
    return apk_position;
}

router.get("/list",async (req,res) => {
    const page = req.query.page || 1;
    const limit = req.query.limit || 20;
    const offset = (page - 1) * limit;
    const searchSQL = "select * from `device` order by `version` limit ? offset ? ;"
    const {err,rows} = await db.async.all(searchSQL,[limit,offset]);
    if(err!=null){
        return res.status(500).json({
            code:500,
            msg:"服务器错误"
        })
    }else{
        return res.status(200).json({
            code:200,
            rows
        })
    }
})

router.get("/download",async (req,res) => {
    const {version,type} = req.query;
    const apk_position = calculate_adk_position(version,type);

    fs.access(apk_position, fs.constants.F_OK, (err) => {
        if (err) {
            return res.status(200).json({
                code: 404,
                msg: "文件不存在"
            });
        }
        
        res.download(apk_position, (err) => {
            if (err) {
                console.error("下载出错:", err);
                res.status(500).json({
                    code: 500,
                    msg: "文件下载失败"
                });
            }
        });
    });
});

module.exports = router