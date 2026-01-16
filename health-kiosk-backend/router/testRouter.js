import express from 'express';
const router = express.Router();

router.get("/",async (req,res) => {
    console.log("YES");
    return res.status(200).json({
        code:200
    })
});

export default router;