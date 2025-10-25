<template>
  <div class="home">
      <header class="navbar">
      <div class="logo">{{ $t('home.title') }}</div>
      <nav>
        <a href="#" @click="toggleLang">{{ $t('navbar.language') }}</a>
      </nav>
  </header>
    <main class="main-section">
      <div class="text-content">
        <h1>{{ $t('home.title') }}</h1>
        <p>{{ $t('home.desc') }}</p>
        <div class="left-buttons">
          <button class="btn-primary" @click="change_to_register" v-if="isLogin">{{ $t('home.register') }}</button>
          <button class="btn-primary" @click="change_to_login" v-else>{{ $t('home.login') }}</button>
          <button class="btn-secondary" @click="learnMore">{{ $t('home.learnMore') }}</button>
        </div>
      </div>

      <div class="image-content">
      </div>
    </main>

    <div class="login-box-container">
    <div class="login-box-inner" :class="{flipped: !isLogin}">
    <!-- Login Box: show at the begin time -->
    <div class="login-box front">
      <div class="login-title">
        <h3>{{ $t('login.title') }}</h3>
      </div>
      <div class="inputln">
        <user class="icon" />
        <input
          type="text"
          v-model="username"
          :placeholder="$t('login.username')"
        />
      </div>

      <div class="inputln">
        <key-round class="icon" />
        <input
          type="password"
          v-model="password"
          :placeholder="$t('login.password')"
        />
      </div>

      <div class="inputln">
        <qr-code class="icon" />
        <input
          type="text"
          v-model="captcha"
          :placeholder="$t('login.captcha') || '请输入验证码' "
          style="width: 85px;"
        />
        <div v-html="captchaSvg" @click="getCaptcha" style="cursor:pointer;margin-left:10px;"></div>
      </div>
      <div>
        <input type="checkbox" v-model="rememberMe" style="margin-left: 200px;">{{ $t('login.rememberMe') }}</input>
      </div>
      <div class="buttons">
        <button class="btn-primary" @click="login">{{ $t('login.button') }}</button>
        <button class="btn-primary" @click="forget_pwd">{{ $t('login.forget') }}</button>
      </div>
    </div>
      <!-- Register Box: show after the user click register button -->
    <div class="login-box back">
      <div class="login-title">
        <h3>{{ $t('register.title') }}</h3>
      </div>
      <div class="inputln">
        <user class="icon" />
        <input
          type="text"
          v-model="username"
          :placeholder="$t('register.username')"
        />
      </div>

      <div class="inputln">
        <key-round class="icon" />
        <input
          type="password"
          v-model="password"
          :placeholder="$t('register.password')"
        />
      </div>
      <div>
        <input type="checkbox" v-model="rememberMe" style="margin-left: 200px;">{{ $t('login.rememberMe') }}</input>
      </div>
      <div class="buttons">
        <button class="btn-primary" @click="register">{{ $t('register.button') }}</button>
      </div>
    </div>
    </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n'
import { loginApi, registerApi, testApi, getCaptchaInfoApi } from '../api/auth';
import { resetPasswordApi } from "../api/user";
import { useMessage } from 'naive-ui'
import { User } from 'lucide-vue-next';
import { KeyRound,QrCode } from 'lucide-vue-next';

const router = useRouter();
const { locale } = useI18n()
const toggleLang = () => {
  locale.value = locale.value === 'zh' ? 'en' : 'zh'
}
const username = ref("");
const password = ref("");

const captcha = ref("")
const captchaSvg = ref("")
const captchaId = ref("")

const rememberMe = ref<boolean>(true);

const isLogin = ref(true);

const message = useMessage();

const change_to_register = async () => {
  isLogin.value = false;
}

const change_to_login = async () => {
  isLogin.value = true;
}

const getCaptcha = async () => {
  try{
    const res = await getCaptchaInfoApi();
    console.log(res.data)
    captchaId.value = res.data.data.captchaId;
    captchaSvg.value = res.data.data.svg;
  }catch(e){
    message.error("获取验证码失败")
    console.log(e)
  }
}

const register = async () => {
  try{
    const res = registerApi(username.value,password.value);
    if((await res).data.code === 200){
      message.info("注册成功");
      isLogin.value = true;
    }else{
      message.error("登录失败");
    }
  }catch(e){
    console.log(e)
  }
};

const login = async () =>{
  if(username.value && password.value){
    const res = await loginApi(username.value,password.value,captchaId.value,captcha.value);
    if(res.data.code === 200){
      localStorage.setItem("token",res.data.user.token);
      localStorage.setItem("role",res.data.user.role); // this item is just using for show in frontend
      if(rememberMe.value == true){
        localStorage.setItem("username",username.value);
        localStorage.setItem("password",password.value);
      }else{
        localStorage.removeItem("username");
        localStorage.removeItem("password");
      }
      message.info("登录成功")
      router.push("/selfinfo");
    }else{
      console.log(res.data.msg);
      message.error("登录失败")
    }
  }else {
    alert(locale.value === 'zh' ? '请输入账号和密码' : 'Please enter username and password');
  }
};

const learnMore = async () =>{
  //const res = await testApi();
  //if(res.data.code === 200){
  //  message.info(res.data.msg)
  //}
  router.push("/about");
}

const forget_pwd = async () => {
  message.info("确认邮件已发送至邮箱");
  resetPasswordApi(password.value, username.value);
}

onMounted(()=>{
  getCaptcha();
  username.value = localStorage.getItem("username") || "";
  password.value = localStorage.getItem("password") || "";
  localStorage.setItem("role",'0');
})
</script>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: #fff;
}

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
  display: flex;
  justify-content: center;
  margin-top: 10px;
}

.btn-primary {
  background: #00bfff;
  color: white;
  padding: 0.7rem 1.5rem;
  border: 2px solid white;
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

.login-box h3 {
  margin-bottom: 1rem;
}

.inputln {
  display: flex;
  align-items: center;
  margin: 5px;
}

.inputln input {
  height: 40px;   
  line-height: 40px;
  font-size: 14px;
  padding: 0 8px;
  border: black 1px solid;
  border-left: 0px;
  margin-left: 0px;
}

.inputln .icon {
  margin-left: 20px;
  margin-right: 0px;
  border: black 1px solid;
  padding: 10px;
  width: 20px;
  height: 20px;
  color: #666;
  top: -20px;
}

.login-box-container {
  position: fixed;
  bottom: 100px;
  right: 100px;
  width: 280px;
  height: 280px;
  perspective: 1000px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-box-inner {
  position: relative;
  width: 100%;
  height: 100%;
  transform-style: preserve-3d;
  transition: transform 0.6s ease-in-out;
  transform-origin:center center ;
}

.login-box-inner.flipped {
  transform: rotateY(180deg);
}

.login-box {
  position: absolute;
  width: 100%;
  height: 100%;
  background: white;
  color: black;
  padding: 1.5rem;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  backface-visibility: hidden;
}

.login-box.front {
  transform: rotateY(0deg);
  backface-visibility: hidden;
}

.login-box.back {
  transform: rotateY(180deg);
  backface-visibility: hidden;
}

.left-buttons {
  margin-top: 2rem;
}

.login-title {
  display: flex;
  justify-content: center;
}

</style>
