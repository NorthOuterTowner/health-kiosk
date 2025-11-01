<template>
  <div class="overlay-child">
    <div class="user-info">
      <h3>{{ $t(`role.add_item.${header}`) }}</h3>
      <label>{{ $t('role.add_item.name') }}:   <input v-model="localItem.name" /></label>
      <label>{{ $t('role.add_item.status') }}:   
        <n-select 
          v-model:value="localItem.status"
          :options="statusOptions"
          :placeholder="$t('role.add_item.placeholder')"
          style="margin-bottom: 16px; width: 200px;"
        />
      </label>
      <label>{{ $t('role.add_item.description') }}:   <input v-model="localItem.description"/></label>
      <div class="buttons">
        <button @click="save">{{ $t('role.add_item.add_button') }}</button>
        <button @click="$emit('close')">{{ $t('utils.cancel') }}</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, toRefs, watch } from "vue";
import { addExamItemApi } from "../api/examitem";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
const props = defineProps({
  examItem: Object,
  editable: Boolean
});

const statusOptions = [
    {label: t('role.add_item.enable'), value: 1},
    {label: t('role.add_item.disable'), value: 0},
]

const header = props.editable ? "edit_title" : "view_title"

const emit = defineEmits(["close", "update"]);

const localItem = reactive({ ...props.examItem });

const save = async () => {
  try {
    const { name, abbreviation, description, status } = { ... localItem}
    const res = await addExamItemApi(name, abbreviation, description, status); // 调用后端接口
    if(res.data.code == 200){
        console.log("add success")
    }else{
        console.log(res)
    }
    emit("update"); // 通知父组件更新表格
  } catch (err) {
    console.error("更新失败", err);
  }
};

// 当 props.user 改变时，更新本地状态
watch(() => props.examItem, (newVal) => {
  Object.assign(localItem, newVal);
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
