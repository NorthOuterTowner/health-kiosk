const express = require('express')
const router = express.Router()

const {db,genid} = require('../db/dbUtils');

/**
 * @api {get} /function/list Get Function List
 * @apiName GetFunctionList
 * @apiGroup Function
 * @apiPermission Authenticated
 *
 * @apiDescription
 * Get all functions (system features) in the system.
 *
 * @apiSuccessExample {json} Success Response:
 * HTTP/1.1 200 OK
 * {
 *   "code": 200,
 *   "rows": [
 *     {
 *       "function_id": 1,
 *       "function_key": "user_manage",
 *       "function_name": "User Management",
 *       "remark": "Manage system users"
 *     }
 *   ],
 *   "cnt": 1
 * }
 */
router.get("/list",async (req,res) => {
  const sql = "select * from `function`;";
  const {err, rows} = await db.async.all(sql,[]);
  if(err == null){
    return res.status(200).json({
      code:200,
      functions:rows
    })
  }else{
    return res.status(200).json({
      code:500,
      msg:"服务器错误"
    })
  }
});

module.exports = router