<template>
  <div class="layout">
    <Sidebar />

    <div class="page">
      <h2>健康监测分析</h2>

      <n-card title="参数设置" class="param-card">
        <n-form :model="queryParams" label-width="80">
          <n-form-item label="开始日期">
            <n-date-picker v-model:value="queryParams.start" type="date" />
          </n-form-item>

          <n-form-item label="结束日期">
            <n-date-picker v-model:value="queryParams.end" type="date" />
          </n-form-item>

          <n-form-item lable="用户账户" v-if="curRole >= 3">
            <n-input v-model:value="queryParams.user" />
          </n-form-item>

          <n-button type="primary" @click="fetchAnalysis" :loading="loading">
            开始分析
          </n-button>
        </n-form>
      </n-card>

      <n-spin :show="loading">
        <div v-if="analysisData">
          
          <!-- Potential Risks -->
          <n-card title="潜在风险" class="result-card">
            <template v-if="analysisData.potential_risks.length === 0">
              <n-tag type="success">无明显风险</n-tag>
            </template>
            <template v-else>
              <n-tag v-for="r in analysisData.potential_risks" :key="r" type="warning" style="margin-right:5px">
                {{ r }}
              </n-tag>
            </template>
          </n-card>

          <!-- Detailed Analysis -->
          <n-card title="详细分析" class="result-card">
            <n-data-table
              :columns="columns"
              :data="tableRows"
              :bordered="true"
            />
          </n-card>

          <!-- Recommendations -->
          <n-card title="建议" class="result-card">
            <template v-if="analysisData.recommendations.length === 0">
              <n-tag type="success">无特别建议</n-tag>
            </template>
            <template v-else>
              <ul>
                <li v-for="(item, idx) in analysisData.recommendations" :key="idx">
                  {{ item }}
                </li>
              </ul>
            </template>
          </n-card>

        </div>
      </n-spin>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref,h } from "vue";
import Sidebar from "../../components/Sidebar.vue";
import { analyzeHealthApi } from "../../api/self/analyze";
import {
  NCard,
  NForm,
  NFormItem,
  NDatePicker,
  NInput,
  NButton,
  NDataTable,
  NTag,
  NSpin,
  useMessage
} from "naive-ui";

const message = useMessage();

const curUser = localStorage.getItem("username");
const curRole = Number(localStorage.getItem("role")) || 0;

// 查询参数
const queryParams = ref({
  start: null as number | null,
  end: null as number | null,
  user: curUser as string | null
});

const loading = ref(false);
const analysisData = ref<any | null>(null);

// 转换详细分析为表格数据
const tableRows = ref<any[]>([]);

const columns = [
  { title: "指标", key: "metric", width: 120 },
  {
    title: "分析结果",
    key: "value",
    render(row: any) {
      if (row.value === "") {
        return h(
          "div",
          {
            style: "background:#d3f9d8;padding:6px 10px;border-radius:6px;color:#2f9e44;"
          },
          "正常"
        );
      }
      return h(
        "div",
        {
          style: "background:#fff3bf;padding:6px 10px;border-radius:6px;color:#e67700;"
        },
        row.value
      );
    }
  }
];

function tsToDate(ts: number) {
  const d = new Date(ts);
  const year = d.getFullYear();
  const month = `${d.getMonth() + 1}`.padStart(2, "0");
  const day = `${d.getDate()}`.padStart(2, "0");
  return `${year}-${month}-${day}`;
}

async function fetchAnalysis() {
  if (!queryParams.value.start || !queryParams.value.end) {
    message.error("请选择时间范围！");
    return;
  }

  loading.value = true;

  const startDate = tsToDate(queryParams.value.start);
  const endDate = tsToDate(queryParams.value.end);

  const curUser = localStorage.getItem("username");//username here is "account" actually which could mark user only. 
  try {
    const res = await analyzeHealthApi(
      startDate,
      endDate,
      curUser
    );

    analysisData.value = res.data.data;

    // 转换 detailed_analysis 为表格式
    const detail = analysisData.value.detailed_analysis;

    tableRows.value = Object.entries(detail).map(([k, v]) => ({
      metric: k,
      value: v
    }));

  } catch (err) {
    message.error("分析失败，请稍后重试");
    console.error(err);
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.layout {
  display: flex;
}
.page {
  margin-left: 16rem;
  padding: 20px;
  width: 100%;
}
.param-card {
  margin-bottom: 20px;
}
.result-card {
  margin-top: 20px;
}
</style>
