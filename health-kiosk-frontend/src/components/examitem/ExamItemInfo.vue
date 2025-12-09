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
      <label>{{ $t('examitem.add_item.usage') }}:</label>
        <div v-if="props.editable" class="editor-box">
          <!-- 编辑模式：带工具栏 -->
          <Toolbar :editor="editorRef" :defaultConfig="toolbarConfig" style="border-bottom: 1px solid #ccc"/>
          <Editor
            v-model="localItem.usage"
            :defaultConfig="editorConfig"
            @onCreated="handleCreated"
            style="height: 200px; border: 1px solid #ccc"
          />
        </div>
        <div v-else class="editor-box">
          <!-- 查看模式：只读、无工具栏 -->
          <Editor
            v-model="localItem.usage"
            :defaultConfig="readonlyConfig"
            @onCreated="handleCreatedReadonly"
            style="height: 200px; border: 1px solid #ccc"
          />
        </div>
      <div class="buttons">
        <button @click="save">{{ $t('examitem.add_item.add_button') }}</button>
        <button @click="$emit('close')">{{ $t('utils.cancel') }}</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onBeforeMount, onBeforeUnmount, reactive, toRefs, watch } from "vue";
import { addExamItemApi, updateExamItemApi } from "../../api/examitem/examitem";
import { useI18n } from "vue-i18n";
import { Editor, Toolbar } from "@wangeditor/editor-for-vue"
import "@wangeditor/editor/dist/css/style.css"

// 编辑器引用
const editorRef = ref(null)

const readonlyEditorRef = ref(null)

const toolbarConfig = {
  excludeKeys: ["fullScreen","group-video"]
}   // 工具栏配置
const editorConfig = { placeholder: "请输入内容..." }
const readonlyConfig = { readOnly: true }  // 只读配置

const handleCreated = (editor) => {
  editorRef.value = editor
}

const handleCreatedReadonly = (editor) => {
  readonlyEditorRef.value = editor
  editor.disable() // 禁止编辑
}

onBeforeUnmount(() => {
  if (editorRef.value) editorRef.value.destroy()
  if (readonlyEditorRef.value) readonlyEditorRef.value.destroy()
})

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
console.log("localItem:",localItem)

const save = async () => {
  try {
    const { id, name, abbreviation, description, status, usage } = localItem;
    const res = await updateExamItemApi(id, name, status, description, abbreviation, usage); // 调用后端接口
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
