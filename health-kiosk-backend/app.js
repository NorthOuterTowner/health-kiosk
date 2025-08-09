const express = require('express');
const cors = require('cors');
const app = express();
const port = process.env.PORT || 3000;

app.use(express.json());
app.use(cors())
// 简单健康检查接口
app.get('/health', (req, res) => {
  console.log("from phone")
  res.json({ status: 'ok', timestamp: Date.now() });
});

// 示例 POST 接口
app.post('/checkin', (req, res) => {
  const body = req.body;
  // TODO: 处理体温、身份等
  res.status(201).json({ message: 'received', data: body });
});

app.use("/",require("./router/login"))

app.listen(port,'0.0.0.0', () => {
  console.log(`health-kiosk-backend listening at http://localhost:${port}`);
});
