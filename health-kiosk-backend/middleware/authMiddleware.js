// middleware/authMiddleware.js
const { verifyToken } = require("../utils/jwtHelper");

function authMiddleware(req, res, next) {
  console.log(req.headers);

  const authorization = req.headers.authorization;
  if (!authorization) {
    return res.status(401).json({
      code: 401,
      msg: "无访问权限"
    });
  }

  const token = authorization.split(" ")[1];

  try {
    // 验证 token
    const decoded = verifyToken(token);
    // 如果需要，可以把解析结果挂载到 req 上，方便后续使用
    req.user = decoded;  
    next();
  } catch (err) {
    console.error("token 验证失败:", err);
    return res.status(401).json({
      code: 401,
      msg: "无效的 token"
    });
  }
}

module.exports = authMiddleware;
