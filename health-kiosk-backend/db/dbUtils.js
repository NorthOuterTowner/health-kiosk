const mysql = require("mysql2"); // 使用 mysql2 库
const path = require("path");
const Genid = require("../utils/SnowFlake");

const genid = new Genid({WorkerId:1})

// 创建 MySQL 连接池
const pool = mysql.createPool({
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME,
  waitForConnections: true,
  connectionLimit: 100,
  queueLimit: 0,
});

require('events').EventEmitter.defaultMaxListeners = 150;

pool.getConnection((err, connection) => {
  if (err) {
    if (err.code === 'ER_ACCESS_DENIED_ERROR') {
      console.error("数据库账号或密码错误！");
    } else {
      console.error("数据库连接失败：", err.message);
    }
    process.exit(1);
  } else {
    console.log("数据库连接成功！");
    connection.setMaxListeners(150); 
    connection.release();
  }
});

// 封装异步方法
const db = {
  async: {
    // Select data
    all: (sql, params) => {
      return new Promise((resolve, reject) => {
        pool.query(sql, params, (err, rows) => {
          if (err) {
            reject(err);
          } else {
            resolve({ err: null, rows });
          }
        });
      });
    },

    // Insert, Update, Delete data
    run: (sql, params) => {
      return new Promise((resolve, reject) => {
        pool.query(sql, params, (err, result) => {
          if (err) {
            reject(err);
          } else {
            resolve({ err: null, result });
          }
        });
      });
    },
  },
};

module.exports = { db,genid };