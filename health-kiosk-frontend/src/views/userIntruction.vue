<template>
  <div class="layout">
    <Sidebar />
    <div class="user-intruction-page">
      <h2 class="page-title">体检系统用户操作指南</h2>

      <div class="scroll-container">
        <div class="content">
          <div >
            操作指南
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import Sidebar from '../components/Sidebar.vue'
</script>

<style scoped>
.layout {
  display: flex;
  gap: 20px;
  min-height: 100vh;
}

.user-intruction-page {
  margin-left: 16rem;
  flex: 1;
  padding: 2rem;
  display: flex;
  flex-direction: column;
}

.page-title {
  font-size: 1.8rem;
  font-weight: 600;
  color: #003366;
  text-align: center;
  margin-bottom: 1rem;
}

.scroll-container {
  flex: 1; 
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  padding: 1.5rem;
  max-height: calc(100vh - 150px); 
}

.content {
  line-height: 1.8;
  color: #333;
}
</style>
