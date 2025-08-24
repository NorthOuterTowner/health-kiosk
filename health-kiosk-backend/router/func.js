const express = require('express')
const router = express.Router()

router.get("/test",(req,res)=>{
    return res.status(200).json({
        code:200,
        msg:"测试成功"
    })
})

module.exports = router