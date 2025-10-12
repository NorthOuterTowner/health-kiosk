// routes/captcha.js
const { v4: uuidv4 } = require('uuid');

const express = require("express");
const router = express.Router();
const svgCaptcha = require("svg-captcha");

const redis = require("redis")
const redisClient = require("../db/redis")

// Generate verifying code
router.get("/", async (req, res) => {
  const captcha = svgCaptcha.create({
    size: 4, // length of verifying code
    noise: 1, // number of noise line
    color: false, // whether has color or not
    background: "#ccf2ff",
    width: 80,   // ✅ 控制宽度
    height: 40,   // ✅ 控制高度
    fontSize: 40,
  });

  // save verify code in session or Redis
  const captchaId = uuidv4();
  // save in redis and set validity period of 60 seconds
  await redisClient.setEx(`captcha:${captchaId}`, 60, captcha.text.toLowerCase());

  res.type("svg");
  res.status(200).json({
    code: 200,
    data: {
      captchaId,
      svg: captcha.data,
    },
  });
});

module.exports = router;
