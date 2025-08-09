const express = require('express')
const router = express.Router()

router.post("/login",async (req,res)=>{
    console.log(req.body)
    const {name,pwd} = req.body

    if(name == 'admin' && pwd == '111'){
        console.log("true")
        return res.status(200).json({
            code:200,
            msg:"login success"
        })
    }else{
        console.log("false")
    }

})

module.exports = router