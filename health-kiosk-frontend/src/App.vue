<template>
  <div class="home">
  <header class="navbar">
      <div class="logo">{{ $t('home.title') }}</div>
      <nav>
        <a href="#">{{ $t('navbar.intro') }}</a>
        <a href="#">{{ $t('navbar.features') }}</a>
        <a href="#" @click="toggleLang">{{ $t('navbar.language') }}</a>
      </nav>
    </header>
  <n-message-provider>
    <router-view />
  </n-message-provider>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from 'vue-i18n'

const { locale } = useI18n()

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
</style>
