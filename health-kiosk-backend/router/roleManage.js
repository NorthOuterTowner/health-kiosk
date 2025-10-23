const express = require('express')
const router = express.Router()

const {db,genid} = require('../db/dbUtils');

router.get("/permissionList", async (req, res) => {
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
      message: "数据库查询出错"
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
 * TODO: 角色管理、分配权限
 */
module.exports = router