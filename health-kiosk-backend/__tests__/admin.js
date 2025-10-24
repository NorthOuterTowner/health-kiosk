const request = require('supertest');
const app = require('../app');

// Mock 掉外部依赖
jest.mock('../db/dbUtils', () => ({
  db: {
    async: {
      all: jest.fn(),
      run: jest.fn()
    }
  },
  genid: jest.fn()
}));

jest.mock('../db/redis', () => ({
  get: jest.fn(),
  del: jest.fn()
}));

jest.mock('../utils/jwtHelper', () => ({
  generateToken: jest.fn(() => 'mocked-jwt-token')
}));

jest.mock('../middleware/authMiddleware', () => (req, res, next) => {
  req.account = 'mockedUser';
  next();
});

const { db } = require('../db/dbUtils');
const redisClient = require('../db/redis');
const { generateToken } = require('../utils/jwtHelper');

describe(' User Route Tests', () => {

  beforeEach(() => {
    jest.clearAllMocks();
  });

  // 测试账号密码登录成功
  test('POST /user/login - account login success', async () => {
    // 模拟数据库返回的用户数据
    db.async.all.mockResolvedValueOnce({
      err: null,
      rows: [
        {
          account: 'user001',
          pwd: require('crypto').createHash('sha256').update('123456').digest('hex'),
          name: 'Alice',
          role: 1
        }
      ]
    });
    redisClient.get.mockResolvedValueOnce('abcd');
    redisClient.del.mockResolvedValueOnce();

    const res = await request(app)
      .post('/user/login')
      .field('account', 'user001')
      .field('pwd', '123456')
      .field('captcha', 'abcd')
      .field('captchaId', '123')
      .attach('photo', Buffer.from('fake image'), { filename: 'test.jpg' });

    expect(res.status).toBe(200);
    expect(res.body.code).toBe(200);
    expect(res.body.msg).toBe('登录成功');
    expect(res.body.user).toHaveProperty('token', 'mocked-jwt-token');
  });

  // 测试用户不存在
  test('POST /user/login - user not found', async () => {
    db.async.all.mockResolvedValueOnce({ err: null, rows: [] });
    redisClient.get.mockResolvedValueOnce('abcd');
    redisClient.del.mockResolvedValueOnce();

    const res = await request(app)
      .post('/user/login')
      .field('account', 'ghost')
      .field('pwd', 'wrong')
      .field('captcha', 'abcd')
      .field('captchaId', '123');

    expect(res.status).toBe(200);
    expect(res.body.code).toBe(404);
    expect(res.body.msg).toBe('不存在该用户');
  });

  // 测试注册接口
  test('POST /user/register - success', async () => {
    db.async.all.mockResolvedValueOnce({ err: null, rows: [] });
    db.async.run.mockResolvedValueOnce({ err: null });

    const res = await request(app)
      .post('/user/register')
      .field('account', 'newuser')
      .field('pwd', '123456');

    expect(res.status).toBe(200);
    expect(res.body.code).toBe(200);
    expect(res.body.msg).toBe('注册成功');
  });

  // 测试添加头像接口
  test('POST /user/addPicture - success', async () => {
    db.async.all.mockResolvedValueOnce({ err: null, rows: [{ account: 'mockedUser' }] });
    db.async.run.mockResolvedValueOnce({ err: null });

    const res = await request(app)
      .post('/user/addPicture')
      .set('Authorization', 'Bearer mocked-jwt-token')
      .attach('photo', Buffer.from('fakeimage'), { filename: 'avatar.jpg' });

    expect(res.status).toBe(200);
    expect(res.body.code).toBe(200);
    expect(res.body.msg).toBe('添加照片成功');
  });
});
