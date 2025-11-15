// routes/captcha.js
import { v4 as uuidv4 } from 'uuid';

import express from "express";
const router = express.Router();
import svgCaptcha from "svg-captcha";

import redis from "redis";
import redisClient from "../db/redis.js";

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

export default router;