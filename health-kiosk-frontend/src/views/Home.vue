<template>
  <div class="home">
    <!-- 顶部导航 -->
    <header class="navbar">
      <div class="logo">自助体检机后台管理系统</div>
      <nav>
        <a href="#">系统简介</a>
        <a href="#">功能介绍</a>
        <a href="#">数据统计</a>
        <a href="#">设备管理</a>
        <a href="#">中文 | EN</a>
      </nav>
    </header>

    <!-- 主体内容 -->
    <main class="main-section">
      <div class="text-content">
        <h1>自助体检机后台管理系统</h1>
        <p>
          智能化、可视化、便捷化协助体检管理平台，支持人员管理、设备管理、数据统计等功能。
        </p>
        <div class="buttons">
          <button class="btn-primary">立即注册</button>
          <button class="btn-secondary">了解更多</button>
        </div>
      </div>

      <div class="image-content">
        <img :src="machineImg" alt="体检机插画" />
      </div>
    </main>

    <!-- 右下角登录窗口 -->
    <div class="login-box">
      <h3>登录</h3>
      <input type="text" placeholder="账号" />
      <input type="password" placeholder="密码" />
      <button class="btn-primary">登录</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import machineImg from '@/assets/machine.png'
</script>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: #fff;
}

/* 顶部导航 */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: rgba(0, 0, 0, 0.1);
}

.navbar nav a {
  color: #fff;
  margin-left: 1.5rem;
  text-decoration: none;
}

/* 主体内容占满屏幕剩余空间 */
.main-section {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 2rem;
}

.text-content {
  max-width: 50%;
}

.text-content h1 {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.buttons {
  margin-top: 2rem;
}

.btn-primary {
  background: #00bfff;
  color: white;
  padding: 0.7rem 1.5rem;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.btn-secondary {
  background: transparent;
  border: 2px solid white;
  color: white;
  padding: 0.7rem 1.5rem;
  margin-left: 1rem;
  border-radius: 5px;
  cursor: pointer;
}

.image-content img {
  max-width: 400px;
}

/* 登录窗口 */
.login-box {
  position: fixed;
  bottom: 20px;
  right: 20px;
  background: white;
  color: black;
  padding: 1.5rem;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  width: 250px;
}

.login-box h3 {
  margin-bottom: 1rem;
}

.login-box input {
  width: 100%;
  padding: 0.5rem;
  margin-bottom: 0.8rem;
  border: 1px solid #ccc;
  border-radius: 5px;
}
</style>
