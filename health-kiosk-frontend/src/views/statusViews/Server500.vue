<template>
  <Sidebar />
  <div class="error-page">
    <div class="card">
      <div class="icon purple">
        <!-- SVG 保留 -->
        <svg xmlns="http://www.w3.org/2000/svg" 
            viewBox="0 0 16 16" 
            fill="currentColor" 
            width="50" 
            height="50">
          <path d="M5.354 2a2 2 0 0 0-1.857 1.257l-.338.845C3.43 4.035 3.71 4 4 4h8c.29 0 .571.035.84.102l-.337-.845A2 2 0 0 0 10.646 2H5.354Z" />
          <path fill-rule="evenodd" d="M2 13a2 2 0 0 1 2-2h8a2 2 0 1 1 0 4H4a2 2 0 0 1-2-2Zm10.75 0a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0ZM9 13.75a.75.75 0 1 0 0-1.5.75.75 0 0 0 0 1.5ZM4 5.5a2 2 0 1 0 0 4h8a2 2 0 1 0 0-4H4Zm8 2.75a.75.75 0 1 0 0-1.5.75.75 0 0 0 0 1.5ZM9.75 7.5a.75.75 0 1 1-1.5 0 .75.75 0 0 1 1.5 0Z" clip-rule="evenodd" />
        </svg>
      </div>

      <div class="title">
        <h2>{{ $t('server.title.code') }}</h2>
        <h2>{{ $t('server.title.text') }}</h2>
        <p>{{ $t('server.title.desc') }}</p>
      </div>

      <div class="divider"></div>

      <div class="message purple-bg">
        {{ $t('server.message') }}
      </div>

      <div class="buttons">
        <button class="btn primary" @click="goHome">{{ $t('server.buttons.home') }}</button>
        <button class="btn secondary" @click="goBack">{{ $t('server.buttons.retry') }}</button>
      </div>
    </div>

    <div class="float-circle one"></div>
    <div class="float-circle two"></div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import Sidebar from "../../components/Sidebar.vue";
import { useI18n } from "vue-i18n";
const router = useRouter();
const goHome = () => router.push("/");
const goBack = () => router.back();
const t = useI18n();
</script>

<style scoped>
.error-page {
  margin-left: 500px;
  margin-top: 50px;
}
.card {
  background: #fff;
  border-radius: 20px;
  padding: 3rem 2rem;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3), 0 0 0 1px rgba(255, 255, 255, 0.1) inset;
  width: 100%;
  max-width: 400px;
  max-height: 500px;
  text-align: center;
  transform: perspective(1000px) translateZ(0);
  transition: transform 0.3s ease;
}
.card:hover {
  transform: perspective(1000px) translateZ(20px) scale(1.05);
}

.icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 1.5rem;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
}
.icon.red {
  background: linear-gradient(135deg, #ff6b6b, #e63946);
  box-shadow: 0 0 25px rgba(230, 57, 70, 0.5);
}
.icon-svg {
  width: 48px;
  height: 48px;
  color: white;
}

.title h1 {
  font-size: 4.5rem;
  background: linear-gradient(90deg, #ff6b6b, #e63946);
  -webkit-background-clip: text;
  color: transparent;
  margin-bottom: 0.3rem;
}
.title h2 {
  color: #333;
  margin-bottom: 0.5rem;
}
.title p {
  color: #666;
  font-size: 0.9rem;
}

.divider {
  height: 1px;
  background: linear-gradient(to right, transparent, #ccc, transparent);
  margin: 1.5rem 0;
}

.message {
  border-radius: 10px;
  padding: 1rem;
  margin-bottom: 1.5rem;
  font-size: 0.9rem;
  color: #333;
  border: 1px solid transparent;
}
.message.red-bg {
  background: linear-gradient(135deg, #ffe5e5, #fff1e1);
  border-color: #ffd1d1;
}

.buttons {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}
.btn {
  border: none;
  border-radius: 10px;
  padding: 0.8rem;
  font-weight: bold;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
}
.btn.primary {
  background: linear-gradient(90deg, #4facfe, #00f2fe);
  color: white;
}
.btn.primary:hover {
  transform: scale(1.03);
  box-shadow: 0 5px 20px rgba(79, 172, 254, 0.5);
}
.btn.secondary {
  border: 2px solid #ccc;
  background: white;
  color: #555;
}
.btn.secondary:hover {
  background: #f7f7f7;
}

.float-circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(60px);
}
.float-circle.one {
  top: 60px;
  left: 60px;
  width: 120px;
  height: 120px;
  background: rgba(255, 255, 255, 0.15);
}
.float-circle.two {
  bottom: 100px;
  right: 80px;
  width: 180px;
  height: 180px;
  background: rgba(255, 255, 255, 0.08);
}

.icon.purple {
  background: linear-gradient(135deg, #a78bfa, #7e22ce);
  box-shadow: 0 0 25px rgba(126, 34, 206, 0.5);
}
.message.purple-bg {
  background: linear-gradient(135deg, #f3e8ff, #ede9fe);
  border-color: #ddd6fe;
}
</style>
