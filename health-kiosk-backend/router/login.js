const express = require('express')
const router = express.Router()
const {db,genid} = require('../db/dbUtils')
const User = require('../entity/User')

/**
 * @api {post} /login 用户登录
 * @apiName UserLogin
 * @apiGroup User
 * @apiVersion 1.1.0
 * 
 * @apidescription 用户通过用户名和密码进行登录。
 * 
 * @apiBody {String} name 用户名
 * @apiBody {String} pwd 密码（明文或已加密，根据系统实现）
 * 
 * @apiSuccess {Number} code 状态码（200 表示成功）
 * @apiSuccess {String} msg 提示信息
 * 
 * @apiSuccessExample {json} 成功响应:
 * HTTP/1.1 200 OK
 * {
 *   "code": 200,
 *   "msg": "登录成功"
 * }
 * 
 */
router.post("/login", async (req, res) => {
    const { account, pwd } = req.body;

    try {
        const { err, rows } = await db.async.all(
            "SELECT * FROM `user` WHERE `account` = ?",
            [account]
        );

        if (err) {
            return res.status(500).json({
                code: 500,
                msg: "服务器错误",
                err
            });
        }

        if (rows.length === 0) {
            return res.status(404).json({
                code: 404,
                msg: "不存在该用户"
            });
        }

        const user = rows[0];
        if (user.pwd === pwd) {
            return res.status(200).json({
                code: 200,
                msg: "登录成功"
            });
        } else {
            return res.status(401).json({
                code: 401,
                msg: "密码错误"
            });
        }
    } catch (e) {
        return res.status(500).json({
            code: 500,
            msg: "服务器错误",
            err: e.message
        });
    }
});

/**
 * @api {post} /register 用户注册
 * @apiName UserRegister
 * @apiGroup User
 * @apiVersion 1.0.0
 * 
 * @apidescription 用户通过提交account、用户名和密码进行注册。
 * 
 * @apiBody {String} account 用户account，唯一标识
 * @apiBody {String} name 用户名
 * @apiBody {String} pwd 密码（明文或已加密，根据系统实现）
 * 
 * @apiSuccess {Number} code 状态码（201 表示注册成功）
 * @apiSuccess {String} msg 提示信息
 * 
 * @apiSuccessExample {json} 成功响应:
 * HTTP/1.1 201 Created
 * {
 *   "code": 201,
 *   "msg": "注册成功"
 * }
 */

router.post("/register", async (req, res) => {
    const user = new User(req.body);

    if (!user.account || !user.pwd) {
        return res.status(400).json({
            code: 400,
            msg: "缺少必填字段：account 或 pwd"
        });
    }

    const searchSQL = "SELECT * FROM `user` WHERE `account` = ?;";
    const insertSql = "INSERT INTO `user` (`account`, `pwd`) VALUES (?, ?);";

    try {
        const { err: searchErr, rows } = await db.async.all(searchSQL, 
            [user.account]
        );

        if (searchErr) {
            return res.status(500).json({
                code: 500,
                msg: "查询用户失败",
                err: searchErr
            });
        }

        if (rows.length > 0) {
            return res.status(409).json({
                code: 409,
                msg: "用户account已存在"
            });
        }

        const { err: insertErr } = await db.async.run(insertSql, 
            [user.account, user.pwd]
        );

        if (insertErr) {
            return res.status(500).json({
                code: 500,
                msg: "注册失败",
                err: insertErr
            });
        }

        return res.status(201).json({
            code: 201,
            msg: "注册成功"
        });

    } catch (e) {
        return res.status(500).json({
            code: 500,
            msg: "服务器错误",
            err: e.message
        });
    }
});

module.exports = router