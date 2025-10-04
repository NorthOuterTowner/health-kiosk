<template>
  <div class="overlay-child">
    <div class="user-info">
      <h3>{{ $t(`examitem.add_item.${header}`) }}</h3>
      <label>{{ $t('examitem.add_item.name') }}:   <input v-model="localItem.name" :disabled="!props.editable"/></label>
      <label>{{ $t('examitem.add_item.status') }}:   
        <n-select 
          v-model:value="localItem.status"
          :options="statusOptions"
          :placeholder="$t('examitem.add_item.placeholder')"
          style="margin-bottom: 16px; width: 200px;"
        />
      </label>
      <label>{{ $t('examitem.add_item.abbreviation') }}:  <input v-model="localItem.abbreviation" :disabled="!props.editable"/></label>
      <label>{{ $t('examitem.add_item.description') }}:   <input v-model="localItem.description" :disabled="!props.editable"/></label>
      <label>{{ $t('examitem.add_item.usage') }}: 
        <textarea v-if="!props.editable" v-model="localItem.usage" disabled></textarea>
        <!--<input v-model="localItem.usage" :disabled="!props.editable">-->
      </label>
      <div class="buttons">
        <button @click="save">{{ $t('examitem.add_item.add_button') }}</button>
        <button @click="$emit('close')">{{ $t('utils.cancel') }}</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, toRefs, watch } from "vue";
import { addExamItemApi, updateExamItemApi } from "../api/examitem";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

const statusOptions = [
    {label: t('examitem.add_item.enable'), value: 1},
    {label: t('examitem.add_item.disable'), value: 0},
]

const props = defineProps({
  examItem: Object,
  editable: Boolean
});

const header = props.editable ? "edit_title" : "view_title"

const emit = defineEmits(["close", "update"]);

const localItem = reactive({ ...props.examItem });

const save = async () => {
  try {
    const { name, abbreviation, description, status } = localItem;
    const res = await updateExamItemApi(name,abbreviation,description,status); // 调用后端接口
    if(res.data.code == 200){
        console.log("update success")
    }else{
        console.log(res)
    }
    emit("update",{ ...localItem }); // 通知父组件更新表格
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

textarea {
  width: 100%;
  min-height: 100px;
  padding: 8px 10px;
  margin-top: 6px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 14px;
  line-height: 1.5;
  resize: vertical;       /* 允许用户上下拉伸 */
  overflow-y: auto;       /* 内容超出时滚动 */
  transition: border-color 0.2s, box-shadow 0.2s;
}

textarea:focus {
  border-color: #4caf50;
  box-shadow: 0 0 0 2px rgba(76,175,80,0.2);
  outline: none;
}

</style>
