//近七日用户注册量、用户身份比例
router.get("/userRegister",(req,res) => {
    
    return res.status(200).json({
        code:200
    })
});

//各版本设备下载量
router.get("/device/downloadnum",(req,res)=> {

    return res.status(200).json({
        code:200
    })
})