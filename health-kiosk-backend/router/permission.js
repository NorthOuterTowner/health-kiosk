const express = require('express')
const router = express.Router()

const {db,genid} = require('../db/dbUtils');

/**
 * @api {get} /role/config Get Role Permission Config
 * @apiName GetRolePermissionConfig
 * @apiGroup Role
 * @apiPermission Authenticated
 *
 * @apiDescription
 * Retrieve the mapping between function keys and role permissions.
 *
 * @apiSuccessExample {json} Success Response:
 * HTTP/1.1 200 OK
 * {
 *   "code": 200,
 *   "rows": [
 *     {
 *       "key": "system_manage",
 *       "permission": [1, 2, 3]
 *     }
 *   ],
 *   "cnt": 1
 * }
 */
router.get("/config", async (req, res) => {
  const sql = `
    SELECT
      \`function\`.\`function_key\` AS \`key\`,
      GROUP_CONCAT(DISTINCT \`permission\`.\`role_id\` ORDER BY \`permission\`.\`role_id\` ASC) AS permission
    FROM \`function\`
    LEFT JOIN \`permission\` ON \`function\`.\`function_id\` = \`permission\`.\`function_id\`
    GROUP BY \`function\`.\`function_id\`
  `;

  const { err, rows } = await db.async.all(sql, []);

  if (err) {
    console.error(err);
    return res.status(500).json({
      code: 500,
      msg: "数据库查询出错"
    });
  }

  // 格式化返回的数据
  const config = rows.map(row => ({
    key: row.key,
    permission: row.permission
      ? row.permission.split(',').map(id => Number(id))
      : [] // 没权限时返回空数组
  }));

  return res.status(200).json({
    code: 200,
    config
  });
});

/**
 * @api {post} /role/reassign Reassign Role Permissions
 * @apiName ReassignRolePermissions
 * @apiGroup Role
 * @apiPermission Authenticated
 *
 * @apiDescription
 * Reassign permissions for a specific role.  
 * The existing permissions will be cleared and replaced by the provided list.
 *
 * @apiBody {String} assignName Role name to reassign permissions for
 * @apiBody {Number[]} permissions Array of function IDs to assign
 *
 * @apiSuccessExample {json} Success Response:
 * HTTP/1.1 200 OK
 * {
 *   "code": 200,
 *   "msg": "角色权限已重新分配成功"
 * }
 */
router.post("/reassign", async (req, res) => {
  const { assignName, permissions } = req.body; // permissions 是一个数组

  if (!assignName || !Array.isArray(permissions)) {
    return res.status(400).json({ code: 400, msg: "参数错误" });
  }

  // search users' id 
  const findRoleSql = "SELECT `role_id` FROM `role` WHERE `role_name` = ?;";
  const { err: findErr, rows } = await db.async.all(findRoleSql, [assignName]);
  if (findErr) {
    return res.status(200).json({
         code: 500, 
         msg: "服务器错误（查角色失败）" 
        });
  }
  if (rows.length === 0) {
    return res.status(200).json({ 
        code: 404, 
        msg: "未找到该角色" 
    });
  }
  const roleId = rows[0].role_id;

  // delete old permissions
  const delSql = "DELETE FROM `permission` WHERE `role_id` = ?;";
  try {
    await db.async.run(delSql, [roleId]);
  } catch (err) {
    return res.status(500).json({ 
        code: 500, 
        msg: "服务器错误（删除旧权限失败）" 
    });
  }

  // insert new permissions
  if (permissions.length > 0) {
    const values = permissions.map(fid => [roleId, fid]);
    const insertSql = "INSERT INTO `permission`(`role_id`, `function_id`) VALUES ?;";
    try {
      await db.async.run(insertSql, [values]);
    } catch (err) {
      return res.status(200).json({ 
        code: 500, 
        msg: "服务器错误（插入新权限失败）" 
    });
    }
  }

  return res.status(200).json({
    code: 200,
    msg: "角色权限已重新分配成功"
  });
});

module.exports = router