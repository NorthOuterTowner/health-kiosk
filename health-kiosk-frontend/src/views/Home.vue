<template>
  <div class="home">
    <!-- 顶部导航 -->
    <header class="navbar">
      <div class="logo">{{ $t('home.title') }}</div>
      <nav>
        <a href="#">{{ $t('navbar.intro') }}</a>
        <a href="#">{{ $t('navbar.features') }}</a>
        <a href="#" @click="toggleLang">{{ $t('navbar.language') }}</a>
      </nav>
    </header>

    <!-- 主体内容 -->
    <main class="main-section">
      <div class="text-content">
        <h1>{{ $t('home.title') }}</h1>
        <p>{{ $t('home.desc') }}</p>
        <div class="buttons">
          <button class="btn-primary" @click="register">{{ $t('home.register') }}</button>
          <button class="btn-secondary" @click="learnMore">{{ $t('home.learnMore') }}</button>
        </div>
      </div>

      <div class="image-content">
        <!--<img :src="machineImg" alt="体检机插画" />-->
      </div>
    </main>

    <!-- 右下角登录窗口 -->
    <div class="login-box">
      <h3>{{ $t('login.title') }}</h3>
      <input type="text" v-model="username" :placeholder="$t('login.username')" />
      <input type="password" v-model="password" :placeholder="$t('login.password')" />
      <button class="btn-primary" @click="login">{{ $t('login.button') }}</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n'
import { loginApi,registerApi, testApi } from "../api/auth";
import { useMessage } from 'naive-ui'

const router = useRouter();
const { locale } = useI18n()

const username = ref("");
const password = ref("");

const message = useMessage();

const register = async () => {
  router.push("/Register")
}

const login = async () =>{
  if(username.value && password.value){
    const res = await loginApi(username.value,password.value);
    if(res.data.code === 200){
      localStorage.setItem("token",res.data.user.token);
      message.info("登录成功")
      router.push("/User");
    }else{
      message.error("登录失败")
    }
  }else {
    alert(locale.value === 'zh' ? '请输入账号和密码' : 'Please enter username and password');
  }
};

const learnMore = async () =>{
  const res = await testApi();
  if(res.data.code === 200){
    message.info(res.data.msg)
  }
}
// 切换语言
const toggleLang = () => {
  locale.value = locale.value === 'zh' ? 'en' : 'zh'
}
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
  margin-right: 400px;
}

/* 登录窗口 */
.login-box {
  position: fixed;
  bottom: 100px;
  right: 100px;
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
