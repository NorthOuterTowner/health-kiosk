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

/**
 * @api {get} /examitem/list Get ExamItem List
 * @apiGroup ExamItem
 *
 * @apiQuery {Number} [page=1] Current page number (default: 1).
 * @apiQuery {Number} [limit=20] Number of items per page (default: 20).
 * 
 * @apiSuccess {json} Success Response:
 * {
 *   "code": 200,
 *   "rows": [
 *     {
 *       "id": 1,
 *       "name": "体温",
 *       "abbreviation": "tempor"
 *       "description": "xxx",
 *       "time":"2025-08-28 17:43:30"
 *     },
 *      ... //other exam item information
 *   ]
 * }
 */
router.get("/list",async (req,res) => {
    const page = Number(req.query.page) || 1;
    const limit = Number(req.query.limit) || 10;
    const offset = (page - 1) * limit;
    const searchSQL = "select * from `item` order by `id` limit ? offset ? ;"
    const {err,rows} = await db.async.all(searchSQL,[limit,offset]);
    
    const cntSQL = "select COUNT(*) as cnt from `item`;";
    const { err: cntErr, rows: cntRows } = await db.async.all(cntSQL,[]);

    if(err != null || cntErr != null ){
        return res.status(500).json({
            code:500,
            msg:"服务器错误"
        })
    }else{
        return res.status(200).json({
            code:200,
            count: cntRows[0].cnt,
            rows
        })
    }
})

/**
 * @api {post} /examitem/add Add New ExamItem
 * @apiGroup ExamItem
 *
 * @apiBody {String} name exam item name (required).
 * @apiBody {String} abbreviation exam item abbreviation (required).
 * @apiBody {String} description exam item description.
 *
 * @apiSuccess {json} Success Response:
 * {
 *   "code": 200,
 *   "msg": "上传成功"
 * }
 */
router.post("/add",async (req,res) => {
    console.log(req.body);
    let { name, abbreviation, description, status } = req.body;
    if(status == null){
        status = 0;
    }else{
        status = Number(status)
    }
    console.log(status);
    const selectSQL = "select * from `item` where `name` = ?;";
    const {err,rows} = await db.async.all(selectSQL,[name]);
    if(err == null && rows.length > 0){
        return res.status(200).json({
            code:500,
            msg:"项目已存在"
        });
    }
    try{
        const insertSQL = "insert into `item` (`name`, `abbreviation`, `description`, `status`) values (?, ?, ?, ?) ;";
        await db.async.run(insertSQL, [name,abbreviation,description,status]);
        return res.status(200).json({
            code:200,
            msg:"添加成功"
        })
    }catch(e){
        console.log(e)
        return res.status(500).json({
            code: 500,
            msg: "添加失败"
        });
    }
});

/**
 * @api {post} /examitem/delete Delete examitem
 * @apiGroup Device
 *
 * @apiBody {String} name exam item name (required).
 *  
 * @apiSuccess {json} Success Response:
 * {
 *   "code": 200,
 *   "msg": "删除成功"
 * }
 */
router.post("/delete",async (req,res) => {
    const { id, name } = req.body;
    const delSql = "delete from `item` where `id` = ?;";
    try{
        await db.async.run(delSql,[id]);
        return res.status(200).json({
            code: "200",
            msg: "删除成功"
        });
    }catch(e){
        console.log(e);
        return res.status(500).json({
            code: "500",
            msg: "服务器错误"
        })
    }
})

/**
 * @api {post} /examitem/update Update examitem
 * @apiGroup ExamItem
 *
 * @apiBody {String} id exam item id (required).
 * @apiBody {String} name exam item name (required). 
 * @apiBody {String} status exam item status (required).
 * @apiBody {String} description exam item description (required).
 * @apiBody {String} abbreviation exam item abbreviation (required).
 * 
 * @apiSuccess {json} Success Response:
 * {
 *   "code": 200,
 *   "msg": "更新成功"
 * }
 */
router.post("/update",async(req,res) => {
    const { id, name, status, description, abbreviation, usage } = req.body;

    const updateSql = "update `item` set `name` = ?, `status` = ?, `abbreviation` = ?, `description` = ?, `usage` = ? where `id` = ? ;";
    try{
        await db.async.run(updateSql,[name, status, abbreviation, description, usage, id]);
        return res.status(200).json({
            code: "200",
            msg: "更新成功"
        });
    }catch(e){
        return res.status(500).json({
            code: "500",
            msg: e.msg
        });
    }
});

export default router;