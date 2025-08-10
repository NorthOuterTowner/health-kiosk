<template>
  <div class="register-container">
    <h2>注册新用户</h2>
    <form @submit.prevent="handleRegister">
      <input type="text" placeholder="用户名" v-model="form.username" required />
      <input type="password" placeholder="密码" v-model="form.password" required />
      <button type="submit">注册</button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'

const form = reactive({
  username: '',
  password: ''
})

const handleRegister = () => {
  console.log('注册信息:', form)
  alert('注册成功！')
}
</script>

<style scoped>
.register-container {
  max-width: 400px;
  margin: 5rem auto;
  padding: 2rem;
  border: 1px solid #ccc;
  border-radius: 6px;
}
input, button {
  display: block;
  width: 100%;
  margin-bottom: 1rem;
  padding: 0.6rem;
}
button {
  background: #0077b6;
  color: white;
  border: none;
}
</style>
