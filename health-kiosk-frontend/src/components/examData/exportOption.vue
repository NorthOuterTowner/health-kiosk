<template>
  <div class="overlay-child">
    <div class="user-info">
      <h3>{{ t('export.title') }}</h3>

      <!-- 开始日期 -->
      <label>
        {{ t('export.startDate') }}
        <n-date-picker
          v-model:value="startDate"
          type="date"
          clearable
        />
      </label>

      <!-- 结束日期 -->
      <label>
        {{ t('export.endDate') }}
        <n-date-picker
          v-model:value="endDate"
          type="date"
          clearable
        />
      </label>

      <!-- 文件类型 -->
      <label>
        {{ t('export.fileType') }}
        <n-select
          v-model:value="fileType"
          :options="fileTypeOptions"
        />
      </label>

      <div class="buttons">
        <button @click="save">{{ t('export.export') }}</button>
        <button @click="$emit('close')">{{ t('utils.cancel') }}</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useI18n } from "vue-i18n";
import { downloadDataApi } from "../../api/self/selfData";
import { NDatePicker, NSelect } from "naive-ui";

const { t } = useI18n();
defineEmits(["close"]);

const startDate = ref<number | null>(null);
const endDate = ref<number | null>(null);

const fileType = ref<"csv" | "excel" | "docx">("csv");

const fileTypeOptions = [
  { label: "CSV", value: "csv" },
  { label: "Excel", value: "excel" },
  { label: "Word", value: "docx" }
];

const formatDate = (ts: number) => {
  const d = new Date(ts);
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${d.getFullYear()}-${m}-${day}`;
};

const save = async () => {
  if (!startDate.value || !endDate.value) return;

  const res = await downloadDataApi(
    formatDate(startDate.value),
    formatDate(endDate.value),
    fileType.value
  );

  const typeMap: Record<string, string> = {
    csv: "text/csv",
    excel: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    docx: "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
  };

  const blob = new Blob([res.data], {
    type: typeMap[fileType.value]
  });

  const url = URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = `健康档案.${fileType.value}`;
  a.click();
  URL.revokeObjectURL(url);
};
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
