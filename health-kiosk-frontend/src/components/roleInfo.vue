<template>
  <div class="overlay-child">
    <div class="user-info">
      <h3>{{ $t(`role.add_item.${header}`) }}</h3>
      <label>{{ $t('role.add_item.name') }}:   <input v-model="localItem.role_name" :disabled="!props.editable"/></label>
      <label>{{ $t('role.add_item.status') }}:   
        <n-select 
          v-model:value="localItem.use"
          :options="statusOptions"
          :placeholder="$t('role.add_item.placeholder')"
          style="margin-bottom: 16px; width: 200px;"
        />
      </label>
      <label>{{ $t('role.add_item.description') }}:   <input v-model="localItem.remark" :disabled="!props.editable"/></label>
      <div class="buttons">
        <button @click="save">{{ $t('role.add_item.add_button') }}</button>
        <button @click="$emit('close')">{{ $t('utils.cancel') }}</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, toRefs, watch } from "vue";
import { updateRole } from "../api/role";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

const statusOptions = [
    {label: t('role.add_item.enable'), value: 1},
    {label: t('role.add_item.disable'), value: 0},
]

const props = defineProps({
  role: Object,
  editable: Boolean
});

const header = props.editable ? "edit_title" : "view_title"

const emit = defineEmits(["close", "update"]);

const localItem = reactive({ ...props.role });

const save = async () => {
  try {
    const { role_id, role_name, remark, use } = localItem;
    const res = await updateRole(role_id, role_name, remark, use); // 调用后端接口
    if(res.data.code == 200){
        console.log("update success")
    }else{
        console.log(res)
    }
    emit("update",{ ...localItem });
    emit("close")
  } catch (err) {
    console.error("更新失败", err);
  }
};

// 当 props.user 改变时，更新本地状态
watch(() => props.role, (newVal) => {
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
  width: 690px;                /* 固定宽度，避免撑满 */
  max-height: 80vh;            /* 最大高度占屏幕80% */
  overflow-y: auto;            /* 超出部分滚动 */
  overflow-x: hidden;
  display: flex;
  flex-direction: column;      /* 纵向排列 */
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

.editor-box {
  width: 100%;
  margin-top: 6px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 14px;
  line-height: 1.5;
  max-height: 220px;           /* 限制编辑器区域高度 */
  overflow-y: auto;            /* 编辑器内部也能滚动 */
}

:deep(.w-e-bar) {
  display: flex;
  gap: 0px;   /* 整体按钮间距 */
}

.buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 16px;
  position: sticky;            /* 按钮固定在底部 */
  bottom: 0;
  background: #fff;            /* 覆盖内容 */
  padding-top: 12px;
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
