const express = require('express')
const router = express.Router()
const {db,genid} = require('../db/dbUtils');
const { decodeToken } = require('../utils/jwtHelper');
const multer = require('multer')

const path = require('path')
const fs = require("fs");

const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, '../health-kiosk-android/app/release/'); // store catalog
    },
    filename: function (req, file, cb) {
        const { version, type } = req.body;
        const type_string = (type === '1') ? "release" : "debug";
        const apk_name = "HealthKiosk-" + type_string + "-v" + version + ".apk";
        cb(null, `${apk_name}`);
    }
});

const upload = multer({ storage: storage });

async function calculate_adk_position(version,type){
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

/**
 * @api {get} /device/list Get Device List
 * @apiGroup Device
 *
 * @apiQuery {Number} [page=1] Current page number (default: 1).
 * @apiQuery {Number} [limit=20] Number of items per page (default: 20).
 * 
 * @apiSuccess {json} Success Response:
 * {
 *   "code": 200,
 *   "rows": [
 *     {
 *       "version": 1.0,
 *       "description": "xxx",
 *       "type": "1",
 *       "time":"2025-08-28 17:43:30"
 *     },
 *      ... //other device information
 *   ]
 * }
 */
router.get("/list",async (req,res) => {
    const page = Number(req.query.page) || 1;
    const limit = Number(req.query.limit) || 10;
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

/**
 * @api {post} /device/add Add New Device
 * @apiGroup Device
 *
 * @apiBody {String} version Device version (required).
 * @apiBody {String} description Device description (required).
 * @apiBody {String} type Device type (required).
 *
 * @apiSuccess {json} Success Response:
 * {
 *   "code": 200,
 *   "msg": "上传成功"
 * }
 */
router.post("/add",upload.single('apk'),async (req,res) => {
    const { version, description, type } = req.body;
    const selectSQL = "select * from `device` where `version` = ? and `type` = ?;";
    const {err,rows} = await db.async.all(selectSQL,[version,type]);
    if(err == null && rows.length > 0){
        return res.status(200).json({
            code:500,
            msg:"文件已存在"
        });
    }
    try{
        const insertSQL = "insert into `device` (`version`, `description`, `type`) values (?, ?, ?) ;";
        await db.async.run(insertSQL, [version,description,type]);
        return res.status(200).json({
            code:200,
            msg:"上传成功"
        })
    }catch(e){
        console.log(e)
        return res.status(500).json({
            code: 500,
            msg: "文件上传失败"
        });
    }
});

/**
 * @api {post} /update Upload APK File
 * @apiGroup System
 * 
 * @apiHeader {String} Content-Type multipart/form-data
 * 
 * @apiBody {File} apk APK file to upload (required)
 * 
 * @apiSuccess {Object} Response Example (Success):
 * {
 *   "code": 200,
 *   "msg": "Upload successful"
 * }
 */
router.post("/update",upload.single('apk'),async (req,res) => {
    res.status(200).json({
        code:200,
        msg:"上传成功"
    })
})

/**
 * @api {post} /device/delete Delete Device
 * @apiGroup Device
 *
 * @apiBody {String} version Device version (required).
 * @apiBody {String} type Device type (required).
 * 
 * @apiSuccess {json} Success Response:
 * {
 *   "code": 200,
 *   "msg": "删除成功"
 * }
 */
router.post("/delete",async (req,res) => {
    const { version, type } = req.body;
    const apk_position = await calculate_adk_position(version,type);

    fs.access(apk_position,fs.constants.F_OK, async (err) => {
        if(err) {
            return res.status(200).json({
                code: 404,
                msg: "文件不存在"
            });
        }
        try{
            fs.unlink(apk_position, async (unlinkErr) => {
            if (unlinkErr) {
                return res.status(500).json({
                    code: 500,
                    msg: "删除文件失败",
                });
            }});
            const deleteSQL = "delete from `device` where `version` = ? and `type` = ? ;";
            await db.async.run(deleteSQL, [version, type]);
            res.status(200).json({
                code:200,
                msg:"删除成功"
            })
        }catch(e){
            res.status(500).json({
                    code: 500,
                    msg: "删除失败"
            });
        }
    })
})

/**
 * @api {get} /device/download Download Device File
 * @apiGroup Device
 *
 * @apiQuery {String} version Device version (required).
 * @apiQuery {String} type Device type (required).
 *
 * @apiSuccess {json} File Not Found:
 * {
 *   "code": 404,
 *   "msg": "文件不存在"
 * }
 */
router.get("/download",async (req,res) => {
    const {version,type} = req.query;
    const apk_position = await calculate_adk_position(version,type);

    try{        
        const addNumSql = "update `device` set `num` = `num` + 1 where `version` = ? and `type` = ? ;";
        await db.async.run(addNumSql,[version,type]);
    }catch(err){
        console.log(err)
    }
    
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