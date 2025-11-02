const express = require('express')
const router = express.Router()

const {db,genid} = require('../db/dbUtils');

/**
 * @api {get} /role/list Get Role List
 * @apiName GetRoleList
 * @apiGroup Role
 * @apiPermission Authenticated
 *
 * @apiDescription
 * Get all roles in the system.
 *
 * @apiSuccessExample {json} Success Response:
 * HTTP/1.1 200 OK
 * {
 *   "code": 200,
 *   "roles": [
 *     {
 *       "role_id": "admin",
 *       "role_name": "Administrator",
 *       "remark": "System superuser",
 *       "use": 1
 *     }
 *   ],
 *   "cnt": 1
 * }
 */
router.get("/list",async (req,res) => {
  const sql = "select * from `role`;";
  const {err, rows} = await db.async.all(sql,[]);
  if(err == null){
    return res.status(200).json({
      code:200,
      roles:rows,
      cnt:rows.length
    })
  }else{
    return res.status(200).json({
      code:500,
      msg:"服务器错误"
    })
  }
});

/**
 * @api {post} /role/add Add Role
 * @apiName AddRole
 * @apiGroup Role
 * @apiPermission Authenticated
 *
 * @apiDescription
 * Add a new role to the system.
 *
 * @apiBody {String} role_id Role ID
 * @apiBody {String} role_name Role name
 * @apiBody {String} remark Remark or note
 * @apiBody {Number} use Role usage status (1=enabled, 0=disabled)
 *
 * @apiSuccessExample {json} Success Response:
 * HTTP/1.1 200 OK
 * {
 *   "code": 200,
 *   "msg": "添加成功"
 * }
 */
router.post("/add",async (req,res)=> {
  const { role_id, role_name, remark, use } = req.body;
  const search = "select `role_id`, `role_name` from `role` ;";
  const {err, rows} = await db.async.all(search,[]);
  const ids = rows.map(row => {
    return row.role_id;
  })
  const names = rows.map(row => {
    return row.role_name;
  })
  if(ids.includes(role_id) || names.includes(role_name)){
    return res.status(200).json({
      code:409,
      msg:"角色已存在"
    });
  }else{
    const sql = "insert into `role` (`role_id`,`role_name`,`remark`,`use`) values (?,?,?,?);";
    try{
      await db.async.run(sql,[role_id, role_name, remark, use]);
      return res.status(200).json({
        code:200,
        msg:"添加成功"
      })
    }catch(err){
      return res.status(200).json({
        code:500,
        msg:"服务器错误"
      })
    }
  }
});

/**
 * @api {post} /role/useChange Change Role Status
 * @apiName ChangeRoleStatus
 * @apiGroup Role
 * @apiPermission Authenticated
 *
 * @apiDescription
 * Change the usage status (enabled/disabled) of a role.
 *
 * @apiBody {String} role_name Role name
 * @apiBody {Number} use New status (1=enable, 0=disable)
 *
 * @apiSuccessExample {json} Success Response:
 * HTTP/1.1 200 OK
 * {
 *   "code": 200,
 *   "msg": "启用成功"
 * }
 */
router.post("/useChange", async (req,res)=> {
  let { role_name, use } = req.body;
  use = Number(use);
  const confirmSql = "select * from `role` where `role_name` = ?;";
  const {err,rows} = await db.async.all(confirmSql,[role_name]);
  if(err != null) {
    return res.status(200).json({
        code:500,
        msg:"服务器错误"
      })
  }else if(rows.length === 0){
    return res.status(200).json({
      code:200,
      msg:"没有该角色"
    });
  }
  const currentUse = rows[0].use;
  if(currentUse === use){
    const useMsg = currentUse === 1 ? "启用" : "停用";
    const msg = `该角色已${useMsg}`;
    return res.status(200).json({
      code:409,
      msg
    })
  }
  const sql = "update `role` set `use` = ? where `role_name` = ? ;";
  try {
    await db.async.run(sql,[use,role_name]);
    const newMsg = use === 1 ? "启用" : "停用";
    return res.status(200).json({
        code:200,
        msg:`${newMsg}成功`
      })
  }catch(err){
    return res.status(200).json({
        code:500,
        msg:"服务器错误"
      })
  }
});

/**
 * @api {post} /role/delete Delete Role
 * @apiName DeleteRole
 * @apiGroup Role
 * @apiPermission Authenticated
 *
 * @apiDescription
 * Delete a role from the system by its name.
 *
 * @apiBody {String} name Role name to delete
 *
 * @apiSuccessExample {json} Success Response:
 * HTTP/1.1 200 OK
 * {
 *   "code": 200,
 *   "msg": "删除成功"
 * }
 */
router.post("/delete", async (req,res) => {
  const name = req.body.name;
  const searchSql = "select 1 from `role` where `role_name` = ? ;";
  try {
    const { err, rows } = await db.async.all(searchSql,[name]);
    if (rows.length == 0) {
      return res.status(200).json({
        code:400,
        msg:"该角色不存在"
      })
    }
    if (err!=null){
      return res.status(200).json({
        code:500,
        msg: "服务器错误"
    })
    }
  }catch(err) {
    return res.status(200).json({
      code:500,
      msg: "服务器错误"
    })
  }
  const delSql = "delete from `role` where `role_name` = ?;";
  try {
    await db.async.run(delSql,[name]);
    return res.status(200).json({
      code:200,
      msg:"删除成功"
    });
  }catch(err){
    return res.status(200).json({
      code:500,
      msg:"服务器错误"
    })
  }
})

/**
 * @api {post} /role/update Update Role
 * @apiName UpdateRole
 * @apiGroup Role
 * @apiPermission Authenticated
 *
 * @apiDescription
 * Update role information, including name, remark, and usage status.
 *
 * @apiBody {String} role_id Role ID to update
 * @apiBody {String} role_name Updated role name
 * @apiBody {String} remark Updated remark
 * @apiBody {Number} use Updated status (1=enabled, 0=disabled)
 *
 * @apiSuccessExample {json} Success Response:
 * HTTP/1.1 200 OK
 * {
 *   "code": 200,
 *   "msg": "更新成功"
 * }
 */
router.post("/update", async (req,res) => {
  const { role_id, role_name, remark, use } = req.body;

  const searchSql = "select 1 from `role` where `role_id` = ? ;";
  try {
    const { err, rows } = await db.async.all(searchSql,[role_id]);
    if (rows.length == 0) {
      return res.status(200).json({
        code:400,
        msg:"该角色不存在"
      })
    }
    if (err!=null){
      return res.status(200).json({
        code:500,
        msg: "服务器错误"
    })
    }
  }catch(err) {
    return res.status(200).json({
      code:500,
      msg: "服务器错误"
    })
  }

  const updateSql = "update `role` set `role_name` = ?, `remark` = ?, `use` = ? where `role_id` = ? ;";
  try {
    await db.async.run(updateSql,[role_name,remark,use,role_id]);
    return res.status(200).json({
      code:200,
      msg:"更新成功"
    })
  }catch (err) {
    return res.status(200).json({
      code:500,
      msg: "服务器错误"
    })
  }
})

module.exports = router