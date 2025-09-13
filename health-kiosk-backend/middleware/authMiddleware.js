// middleware/authMiddleware.js
const { verifyToken,decodeToken } = require("../utils/jwtHelper");
const {db,genId} = require("../db/dbUtils");

async function authMiddleware(req, res, next) {
  const authorization = req.headers.authorization;
  if (!authorization) {
    return res.status(200).json({
      code: 401,
      msg: "无访问权限"
    });
  }

  const token = authorization.split(" ")[1];

  try {
    const decoded = decodeToken(token);
    const account = decoded.data.account;
    const roleSQL = "select `role` from `user` where `account` = ? ;"
    const {err, rows} = await db.async.all(roleSQL,[account])

    if(err == null && rows.length > 0){
      req.role = rows[0].role
      req.account = account
    }else{
      return res.status(200).json({
        code: 401,
        msg: "无访问权限"
      });
    }
    next();
  } catch (err) {
    console.error("token 验证失败:", err);
    return res.status(200).json({
      code: 401,
      msg: "无效的 token"
    });
  }
}

module.exports = authMiddleware;
