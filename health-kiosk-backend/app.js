const express = require('express');
const cors = require('cors');
const app = express();
const port = process.env.PORT || 3000;

const {db,genid} = require("./db/dbUtils")
const redis = require("redis")
const redisClient = require("./db/redis")
const multer = require('multer');
const upload = multer({ dest: 'uploads/' });
const { spawn } = require("child_process");

const rateLimit = require('express-rate-limit');

const path = require('path');

//启动常驻的子进程
const py = spawn("python", ["../face_recognition/face_service.py"]);

const callbacks = [];

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

// The API using for test whether mobile phone could connect to the backend
app.get('/test', (req, res) => {
  console.log("from phone")
  res.json({ status: 'ok', timestamp: Date.now() });
});

/* API rate limit */
const limiter = rateLimit({
	windowMs: 1000, // 1 second
	limit: 10, // Limit each IP to 10 requests per `window` (here, per 1 second).
	standardHeaders: 'draft-8', // draft-6: `RateLimit-*` headers; draft-7 & draft-8: combined `RateLimit` header
	legacyHeaders: false, // Disable the `X-RateLimit-*` headers.
	// store: ... , // Redis, Memcached, etc. See below.
})

/* Cross-Origin Requests */
app.use(function(req,res,next){
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers","*");
    res.header("Access-Control-Allow-Methods","DELETE,PUT,POST,GET,OPTIONS");
    res.header("Access-Control-Allow-Credentials", "true");
    if(req.method == "OPTIONS") res.sendStatus(200);
    else next();
});

app.use(express.json());
app.use(limiter);

app.use("/admin",require("./router/admin"))

app.listen(port,'0.0.0.0', () => {
  console.log(`health-kiosk-backend listening at http://localhost:${port}`);
});
