<template>
  <div class="home">

  <n-message-provider>
    <router-view />
  </n-message-provider>
  </div>
</template>

<script setup lang="ts">

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
