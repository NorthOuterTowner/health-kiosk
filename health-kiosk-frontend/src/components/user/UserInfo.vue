<template>
  <div class="overlay-child">
    <div class="user-info">
      <h3>{{ $t(`user.add_user.${header}`) }}</h3>
      <label>{{ $t('user.add_user.name') }}<input v-model="localUser.name" :disabled="!props.editable" /></label>
      <label>{{ $t('user.add_user.gender') }}<input v-model="localUser.gender" :disabled="!props.editable"/></label>
      <label>{{ $t('user.add_user.age') }}<input v-model="localUser.age" :disabled="!props.editable"/></label>
      <label>{{ $t('user.add_user.height') }}<input v-model="localUser.height":disabled="!props.editable" /></label>
      <label>{{ $t('user.add_user.weight') }}<input v-model="localUser.weight" :disabled="!props.editable"/></label>
      <div class="buttons">
        <button @click="save">{{ $t('utils.save') }}</button>
        <button @click="$emit('close')">{{ $t('utils.cancel') }}</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, toRefs, watch } from "vue";
import { editApi } from "../../api/user/user"; // 调用后端更新接口

const props = defineProps({
  user: Object,
  editable: Boolean
});

const header = props.editable ? "edit_title" : "view_title"

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

// update local status 
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
