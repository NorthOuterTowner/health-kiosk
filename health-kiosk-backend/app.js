import 'dotenv/config.js';

import express from 'express';
import cors from 'cors';
import { db, genid } from './db/dbUtils.js';
import redis from 'redis';
import redisClient from './db/redis.js';
import multer from 'multer';
const upload = multer({ dest: 'uploads/' });
import { spawn } from 'child_process';
import rateLimit from 'express-rate-limit';
import path from 'path';
import { verifyToken } from './utils/jwtHelper.js';

const app = express();
const port = process.env.PORT || 3000;

app.use(express.json());

/*app.use((req, res, next) => {
  console.log('\n=== 开始 ===');
  console.log(`方法: ${req.method}`);
  console.log(`URL: ${req.originalUrl}`);
  console.log('请求体:', req.body);
  console.log('=== 结束 ===\n');
  next();
});*/

// 启动常驻的子进程
const py = spawn("python", ["../face_recognition/face_service.py"]);

const callbacks= [];

py.stdout.on("data", (data) => {
  if (callbacks.length === 0) return;
  const cb = callbacks.shift();
  cb(data.toString());
});

py.stderr.on("data", (data) => {
  console.error("[Python ERROR]", data.toString());
});

/**
 * TODO: 
 * this middleware just used when request is about login function.
 */
app.use((req, res, next) => {
  req.py = py;
  req.pyCallbacks = callbacks;
  next();
});

/* API rate limit */
const limiter = rateLimit({
	windowMs: 1000, // 1 second
	limit: 10, // Limit each IP to 10 requests per `window` (here, per 1 second).
	standardHeaders: 'draft-8', // draft-6: `RateLimit-*` headers; draft-7 & draft-8: combined `RateLimit` header
	legacyHeaders: false, // Disable the `X-RateLimit-*` headers.
})

/* Cross-Origin Requests */
app.use((req, res, next) => {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers","*");
    res.header("Access-Control-Allow-Methods","DELETE,PUT,POST,GET,OPTIONS");
    res.header("Access-Control-Allow-Credentials", "true");
    if(req.method == "OPTIONS") res.sendStatus(200);
    else next();
});

// Debug HTTP information send by Android conveniently
app.use((req,res,next) => {
  console.log("======= Received HTTP Request =======");
  console.log(req.originalUrl);
  console.log(req.body);
  next();
});

app.use(limiter);

app.set('view engine','ejs');
app.set('views', path.join(process.cwd(), 'views'));

import adminRouter from './router/admin.js';
import funcRouter from './router/func.js';
import userRouter from './router/userManage.js';
import deviceRouter from './router/deviceManage.js';
import statisticsRouter from './router/statistics.js';
import examItemRouter from './router/examItemManage.js';
import captchaRouter from './router/captcha.js';
import searchRouter from './router/search.js';
import roleRouter from './router/role.js';
import permissionRouter from './router/permission.js';
import examDataRouter from './router/examData.js';
import LLMRouter from './router/llm.js';
import testRouter from './router/testRouter.js';

app.use("/admin", adminRouter);
app.use("/func", funcRouter);
app.use("/user", userRouter);
app.use("/device", deviceRouter);
app.use("/statistics", statisticsRouter);
app.use("/examitem", examItemRouter);
app.use("/captcha", captchaRouter);
app.use("/search", searchRouter);
app.use("/role", roleRouter);
app.use("/permission", permissionRouter);
app.use("/examData",examDataRouter);
app.use("/llm",LLMRouter);
app.use("/test",testRouter)

app.listen(port, '0.0.0.0', () => {
  console.log(`health-kiosk-backend listening at http://0.0.0.0:${port}`);
});
