import express from 'express';
const router = express.Router();
import { db, genid } from '../db/dbUtils.js';
import User from '../entity/User.js';
import crypto from 'crypto';
import { v4 as uuidv4 } from 'uuid';
import multer from 'multer';
import path from 'path';
import fs from 'fs';
import { generateToken, decodeToken } from '../utils/jwtHelper.js';
import authMiddleware from '../middleware/authMiddleware.js'
import redisClient from '../db/redis.js';

router.get("/test",(req,res)=>{
    console.log("test API has received request and it has finished authorization")
    return res.status(200).json({
        code:200,
        msg:"测试成功"
    })
})

export default router;