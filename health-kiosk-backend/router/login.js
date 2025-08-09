const express = require('express')
const router = express.Router()
const {db,genid} = require('../db/dbUtils')
router.post("/login",async (req,res)=>{
    //console.log(req.body)
    const {name,pwd} = req.body

    try{
        const {err,rows} = db.async.all("select * from `user` where `name` = ? and `pwd` = ?",[name,pwd])

        if(err==null && rows > 0){
            return res.status(200).json({
                code:200,
                msg:"登录成功"
            })
        }else if (rows == 0){
            const {err:nameErr,rows:nameRow} = db.async.all("select * from `user` where `name` = ?",[name])
            if(nameRow > 0){
                return res.status(200).json({
                    code:500,
                    msg:"密码错误"
                })
            }else{
                return res.status(200).json({
                    code:500,
                    msg:"不存在该用户"
                })
            }
        }else{
            return res.status(500).json({
                code:500,
                msg:"服务器错误",
                err:err
            })
        }
    }catch(e){
        return res.status(500).json({
            code: 500,
            msg: "服务器错误",
            err: e.msg
        })
    }

})

router.post("/register",async (req,res)=>{
    
})
module.exports = router