const express = require('express');
const cors = require('cors');
const app = express();
const port = process.env.PORT || 3000;

app.use(express.json());
app.use(cors())

// 测试接口
app.get('/health', (req, res) => {
  console.log("from phone")
  res.json({ status: 'ok', timestamp: Date.now() });
});


app.use("/admin",require("./router/login"))

app.listen(port,'0.0.0.0', () => {
  console.log(`health-kiosk-backend listening at http://localhost:${port}`);
});
