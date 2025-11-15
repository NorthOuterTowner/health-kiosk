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
 * @api {get} /search/user Search Users
 * @apiName SearchUser
 * @apiGroup Search
 * @apiPermission Authenticated
 *
 * @apiDescription
 * Perform fuzzy search on user data.  
 * The following fields are matched: account, name and birthday
 *
 * @apiQuery {String} keyword Search keyword
 *
 * @apiSuccessExample {json} Success Response:
 * HTTP/1.1 200 OK
 * {
 *   "code": 200,
 *   "rows": [
 *     {
 *       "id": 1,
 *       "account": "john_doe",
 *       "name": "John Doe",
 *       "birthday": "1995-03-12"
 *     }
 *   ],
 *   "cnt": 1
 * }
 */
router.get("/user", authMiddleware, async (req,res) => {
	const keyword = req.query.keyword;
	
	const page = Number(req.query.page) || 1;
    const limit = Number(req.query.limit) || 10;
    const offset = (page - 1) * limit;

	if (!keyword || keyword.trim() === "") {
	  return res.status(200).json({
	    code: 400,
	    msg: "Missing or empty keyword"
	  });
	}

	const fields = ['account', 'name', 'birthday'];
	// dynamic generate fuzzy matching SQL
	const whereClause = fields.map(f => `${f} LIKE ?`).join(' OR ');
	const params = fields.map(() => `%${keyword}%`);
	params.push(limit);
	params.push(offset);

	const searchSql = `select * from \`user\` where ${whereClause}; order by \`id\` limit ? offset ? ;`;
	try{
		const {err,rows} = await db.async.all(searchSql,params);
		if(err==null && rows.length > 0){
			return res.status(200).json({
				code:200,
				rows,
				cnt:rows.length
			});
		}else if(rows.length == 0){
			return res.status(200).json({
				code:200,
				rows:null,
				cnt:0
			});
		}else{
			return res.status(200).json({
				code:500,
				msg:"Server errror"
			});
		}
	}catch(err){
		return res.status(200).json({
			code:500,
			msg:err.message
		});
	}
});

/**
 * @api {get} /search/device Search Devices
 * @apiName SearchDevice
 * @apiGroup Search
 * @apiPermission Authenticated
 *
 * @apiDescription
 * Perform fuzzy search on device data.  
 * The following fields are matched: version and description.
 *
 * @apiQuery {String} keyword Search keyword
 *
 * @apiSuccessExample {json} Success Response:
 * HTTP/1.1 200 OK
 * {
 *   "code": 200,
 *   "rows": [
 *     {
 *       "id": 2,
 *       "version": "V2.0",
 *       "description": "Smart blood pressure monitor"
 *     }
 *   ],
 *   "cnt": 1
 * }
 */
router.get("/device", authMiddleware, async (req,res) => {
	const keyword = req.query.keyword;
	
	const page = Number(req.query.page) || 1;
    const limit = Number(req.query.limit) || 10;
    const offset = (page - 1) * limit;

	if (!keyword || keyword.trim() === "") {
	  return res.status(200).json({
	    code: 400,
	    msg: "Missing or empty keyword"
	  });
	}

	const fields = ['version', 'description'];
	// dynamic generate fuzzy matching SQL
	const whereClause = fields.map(f => `${f} LIKE ?`).join(' OR ');
	const params = fields.map(() => `%${keyword}%`);
	params.push(limit);
	params.push(offset);

	const searchSql = `select * from \`device\` where ${whereClause}; order by \`version\` limit ? offset ? ;`;
	try{
		const {err,rows} = await db.async.all(searchSql,params);
		if(err==null && rows.length > 0){
			return res.status(200).json({
				code:200,
				rows,
				cnt:rows.length
			});
		}else if(rows.length == 0){
			return res.status(200).json({
				code:200,
				rows:null,
				cnt:0
			});
		}else{
			return res.status(200).json({
				code:500,
				msg:"Server errror"
			});
		}
	}catch(err){
		return res.status(200).json({
			code:500,
			msg:err.message
		});
	}
});

/**
 * @api {get} /search/item Search Items
 * @apiName SearchItem
 * @apiGroup Search
 * @apiPermission Authenticated
 *
 * @apiDescription
 * Perform fuzzy search on health check items.  
 * The following fields are matched: name, abbreviation, description and usage
 *
 * @apiQuery {String} keyword Search keyword
 *
 * @apiSuccessExample {json} Success Response:
 * HTTP/1.1 200 OK
 * {
 *   "code": 200,
 *   "rows": [
 *     {
 *       "id": 5,
 *       "name": "Blood Pressure Test",
 *       "abbreviation": "BP",
 *       "description": "Measures systolic and diastolic pressure",
 *       "usage": "Routine health check"
 *     }
 *   ],
 *   "cnt": 1
 * }
 */
router.get("/item", authMiddleware, async (req,res) => {
	const keyword = req.query.keyword;
	
	const page = Number(req.query.page) || 1;
    const limit = Number(req.query.limit) || 10;
    const offset = (page - 1) * limit;
	
	if (!keyword || keyword.trim() === "") {
	  return res.status(200).json({
	    code: 400,
	    msg: "Missing or empty keyword"
	  });
	}

	const fields = ['name', 'abbreviation', 'description', 'usage'];
	// dynamic generate fuzzy matching SQL
	const whereClause = fields.map(f => `${f} LIKE ?`).join(' OR ');
	const params = fields.map(() => `%${keyword}%`);
	params.push(limit);
	params.push(offset);

	const searchSql = `select * from \`item\` where ${whereClause} order by \`id\` limit ? offset ? ;`;
	try{
		const {err,rows} = await db.async.all(searchSql,params);
		if(err==null && rows.length > 0){
			return res.status(200).json({
				code:200,
				rows,
				cnt:rows.length
			});
		}else if(rows.length == 0){
			return res.status(200).json({
				code:200,
				rows:null,
				cnt:0
			});
		}else{
			return res.status(200).json({
				code:500,
				msg:"Server errror"
			});
		}
	}catch(err){
		return res.status(200).json({
			code:500,
			msg:err.message
		});
	}
});


export default router;