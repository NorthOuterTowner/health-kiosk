<template>
  <div class="overlay-child">
    <div class="user-info">
      <h3>更改用户数据</h3>
      <label>用户名:<input v-model="localUser.name" /></label>
      <label>性别:<input v-model="localUser.gender" /></label>
      <label>年龄:<input v-model="localUser.age" /></label>
      <label>身高:<input v-model="localUser.height" /></label>
      <label>体重:<input v-model="localUser.weight" /></label>
      <div class="buttons">
        <button @click="save">保存</button>
        <button @click="$emit('close')">取消</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, toRefs, watch } from "vue";
import { editApi } from "../api/user"; // 调用后端更新接口

const props = defineProps({
  user: Object
});

const emit = defineEmits(["close", "update"]);

const localUser = reactive({ ...props.user });

const save = async () => {
  try {
    await editApi(localUser); // 调用后端接口
    emit("update", { ...localUser }); // 通知父组件更新表格
  } catch (err) {
    console.error("更新失败", err);
  }
};

// 当 props.user 改变时，更新本地状态
watch(() => props.user, (newVal) => {
  Object.assign(localUser, newVal);
});
</script>

<style scoped>
.overlay-child {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0,0,0,0.3);
  display: flex;
  justify-content: center;
  align-items: flex-start;
  padding-top: 40px;
  z-index: 1000;
  color: black;
}

.user-info {
  background-color: #fff;
  padding: 24px;
  border-radius: 8px;
  min-width: 400px;
}

label {
  display: block;
  margin-bottom: 12px;
}

input {
  width: 100%;
  padding: 6px 8px;
  margin-top: 4px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

.buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 16px;
}

.buttons button {
  padding: 6px 12px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  color: #fff;
}

.buttons button:first-child { background-color: #4caf50; }
.buttons button:last-child { background-color: #f44336; }
</style>
