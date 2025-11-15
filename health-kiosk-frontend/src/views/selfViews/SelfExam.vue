<template>
    <div class="layout">
        <Sidebar/>
        <div class="self-exam-page">

        </div>
    </div>
</template>

<script setup lang="ts">
import Sidebar from '../../components/Sidebar.vue';


</script>

<style scoped>
.layout {
  display: flex;
  gap: 20px;
}

.self-exam-page {
  margin-left: 16rem;
  flex: 1;
  padding-left: 1em;
  padding-right: 60px;
}
</style>