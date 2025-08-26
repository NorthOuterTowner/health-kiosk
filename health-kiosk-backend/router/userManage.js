/**
 * logic of user manage:
 *  when the user try to visit user manage page,
 *  front-end should push different router
 *  accourding to whether the user get admin permission,
 *  and using different request API to get data.
 */
const express = require('express')
const router = express.Router()
const {db,genid} = require('../db/dbUtils');
const { decodeToken } = require('../utils/jwtHelper');

const crypto = require('crypto');
const fs = require('fs');
const nodemailer = require("nodemailer");
const redis = require("redis")
const redisClient = require("../db/redis")
const ejs = require('ejs');
const path = require('path');

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

//get all users' information
router.get("/", async (req,res)=>{
    const page = req.page || 1;
    const limit = req.limit || 20;
    const searchSQL = "select * from `user` order by `account` limit ? offset ( ? - 1 ) * 10 ;"
    const {err,rows} = await db.async.all(searchSQL,[limit,page]);
    if(err!=null){
        return res.status(500).json({
            code:500,
            msg:"服务器错误"
        })
    }else{
        return res.status(200).json({
            code:200,
            rows
        })
    }
});

router.get("/info", async (req,res)=>{
    const token = req.headers.authorization.split(' ')[1];
    const account = decodeToken(token).data.account;
    const searchSQL = "select * from `user` where account = ? ;"
    const {err,rows} = await db.async.all(searchSQL,[account]);
    if(err == null && rows.length > 0){
        return res.status(200).json({
            code:200,
            rows
        });
    }else if(rows.length == 0){
        return res.status(200).json({
            code:200,
            msg:"无该用户"
        });
    }else{
        return res.status(500).json({
            code:200,
            msg:"服务器错误"
        });
    }
});

router.post("/change",async (req,res) => {
    const account = decodeToken(req.headers.authorization.split(' ')[1]).data.account;
    const searchSQL = "select * from `user` where `account` = ? ;"
    const {err,rows} = await db.async.all(searchSQL,[account]);

    if(err == null && rows.length > 0){
        let {name,age,gender,height,weight,email} = rows[0];
        
        name   = req.body.name   ?? name   ;
        age    = req.body.age    ?? age    ;
        gender = req.body.gender ?? gender ;
        height = req.body.height ?? height ;
        weight = req.body.weight ?? weight ;
        email  = req.body.email  ?? email  ;

        if(!emailRegex.test(email)){
            return res.status(200).json({
                code:422,//unprocessable entity
                msg:"邮箱格式不合法"
            })
        }
        const updateSQL = "update `user` set name = ?, age = ?, gender = ?, height = ?, weight = ?, email = ? where account = ? ;"
        try{
            await db.async.run(updateSQL,[name,age,gender,height,weight,email,account]);
            return res.status(200).json({
                code:200,
                msg:"修改成功"
            })
        }catch(e){
            return res.status(200).json({
                code:500,
                msg:"修改失败"
            })
        }
    }else{
        return res.status(200).json({
            code:404,
            msg:"未找到该用户"
        })
    } 
});

router.post('/reset/pwd', async (req, res) => {
    const account = decodeToken(req.headers.authorization.split(' ')[1]).data.account;
    const searchSQL = "select `email` from `user` where `account` = ? ;"
    const {err,rows} = await db.async.all(searchSQL,[account]);
    let email;
    if(err == null && rows.length > 0){
        email = rows[0].email;
        if(email==null){
            return res.status(200).json({
                code:412,//pre condition failed
                msg:"请先设置邮箱内容"
            });
        }
    }else if(rows.length == 0){
        return res.status(200).json({
            code:404,
            msg:"无该用户"
        })
    }else{
        return res.status(500).json({
            code:500,
            msg:"服务器错误"
        })
    }
    const { newPassword } = req.body;

    if (!newPassword) {
        return res.status(200).json({ 
            code: 400, 
            msg: "密码不能为空" 
        });
    }

    // genearte verify code and hash password
    const verifyCode = crypto.randomBytes(16).toString("hex");
    const expiresAt = 60 * 30; //(expires at 30 minutes)

    const hash = crypto.createHash('sha256');
    hash.update(newPassword);
    const hashedPassword = hash.digest('hex');

    try{
        // store verify code and hashed password to redis
        await redisClient.setEx(
            `reset-password:${verifyCode}`, 
            expiresAt, 
            JSON.stringify({ email, newPassword: hashedPassword })
        );

        // send email
        const transporter = nodemailer.createTransport({
            service: '163',
            auth: {
            user: process.env.EMAIL_USER,
            pass: process.env.EMAIL_PASS
            }
        });

        const resetUrl = `http://localhost:3000/reset/verify?code=${verifyCode}`;

        const templatePath = path.join(__dirname, '../views/resetPasswordEmail.ejs');
        const htmlContent = await ejs.renderFile(templatePath, { resetUrl });

        await transporter.sendMail({
            from: process.env.EMAIL_USER,
            to: email,
            subject: '密码重置确认',
            html: htmlContent
        });

        return res.status(200).send({ 
            code: 200, 
            msg: "重置验证邮件已发送，请查收邮箱" 
        });
    }catch(err){
        return res.code(200).json({
            code:500,
            msg:err.msg
        })
    }
});

module.exports = router