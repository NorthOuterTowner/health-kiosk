const express = require('express')
const router = express.Router()
const {db,genid} = require('../db/dbUtils')

const authMiddleware = require('../middleware/authMiddleware')

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
	
	const searchSql = `select * from \`user\` where ${whereClause};`;
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
	
	const searchSql = `select * from \`device\` where ${whereClause};`;
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
	
	const searchSql = `select * from \`item\` where ${whereClause};`;
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


module.exports = router;