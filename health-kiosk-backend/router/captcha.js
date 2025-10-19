// routes/captcha.js
const { v4: uuidv4 } = require('uuid');

const express = require("express");
const router = express.Router();
const svgCaptcha = require("svg-captcha");

const redis = require("redis")
const redisClient = require("../db/redis")

/**
 * @api {get} /captcha Get Captcha
 * @apiName GetCaptcha
 * @apiGroup Captcha
 * @apiPermission Public
 *
 * @apiDescription
 * Generate a new SVG captcha image and store its verification code in Redis.  
 * The captcha is valid for 60 seconds.
 *
 * @apiSuccessExample {json} Success Response:
 * HTTP/1.1 200 OK
 * {
 *   "code": 200,
 *   "data": {
 *     "captchaId": "c6b0d3e4-2f51-4d53-bff1-8f1b94b8d640",
 *     "svg": "<svg xmlns='http://www.w3.org/2000/svg' ...></svg>"
 *   }
 * }
 */
router.get("/", async (req, res) => {
  const captcha = svgCaptcha.create({
    size: 4, // length of verifying code
    noise: 1, // number of noise line
    color: false, // whether has color or not
    background: "#ccf2ff",
    width: 80,  
    height: 40,   
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
