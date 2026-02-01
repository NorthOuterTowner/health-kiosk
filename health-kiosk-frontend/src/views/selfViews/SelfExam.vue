<!-- Self Exam Data Page--> 
<template>
    <div class="layout">
        <Sidebar />
        <div class="device-page">
            <div class="header-section">
                <h2>{{ $t('selfExam.title') }}</h2>
            </div>
        
            <n-data-table
                remote
                :columns="columns"
                :data="examItems"
                :pagination="pagination"
                :bordered="true" 
                class="dataTable"
            />

            <div class="export-footer-zone">
                <div class="export-card" @click="handleDownload">
                    <div class="export-content">
                        <n-icon size="32" class="export-icon">
                            <DownloadOutline /> </n-icon>
                        <div class="export-text">
                            <h3>{{ $t('selfExam.export.h') }}</h3>
                            <p>{{ $t('selfExam.export.p') }}</p>
                        </div>
                    </div>
                    <div class="export-action">
                        <span>{{ $t('selfExam.export.span') }}</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <ExportOption 
      v-if="export_option == true"
      @close="handleClose"
    />
</template>

<script setup lang = "ts">
import { ref, onMounted, h, reactive, computed } from 'vue';
import Sidebar from "../../components/Sidebar.vue";
import { NButton, useMessage, useDialog } from "naive-ui";
import { useI18n } from 'vue-i18n'
import { deleteExamDataApi, downloadDataApi, getInfoApi } from '../../api/self/selfData';
import ExportOption from '../../components/examData/exportOption.vue';
import { DownloadOutline } from '@vicons/ionicons5';

const { t } = useI18n();

const export_option = ref(false);

const message = useMessage();
const dialog = useDialog();

async function handleDelete(row: any){
  const res = await deleteExamDataApi(row.id);
  console.log(res.data)
  if(res.data.code === 200){
    await fetchExamItems()
    message.info("删除成功")
  }else{
    message.error(res.data.msg);
  }
}

const examItems = ref<any[]>([]);

const renderEcgPic = (row: any) => {
  const BASE_URL = "/pic/";
  const relative_path = row.ecg;
  return BASE_URL+relative_path;
}

const columns = computed(() => [
  { title: t('selfExam.columns.tempor'), key: "tempor", width: 100 },
  { title: t('selfExam.columns.alcohol'), key: "alcohol", width: 100 },
  {
    title: t('selfExam.columns.ecg'),
    key: "ecg",
    width: 100,
    render(row: any) {
      return h(
        NButton,
        {
          size: "small",
          type: "success",
          onClick: () => { window.open("/pic/" + row.ecg); }
        },
        { default: () => t('selfExam.info') } // 使用 i18n 里的 "查看详情"
      )
    }
  },
  { title: t('selfExam.columns.sys'), key: "blood_sys", width: 100 },
  { title: t('selfExam.columns.dia'), key: "blood_dia", width: 100 },
  { title: t('selfExam.columns.hr'), key: "blood_hr", width: 100 },
  {
    title: t('selfExam.columns.date'),
    key: "date",
    width: 150,
    render(row: any) { return row.date.split('T')[0]; }
  },
  {
    title: t('selfExam.columns.time'),
    key: "time",
    width: 100,
    render(row: any) { return row.time == 1 ? "AM" : "PM"; }
  },
  {
    title: t('selfExam.columns.action'),
    key: "actions",
    render(row: any) {
      return [
        h(
          NButton,
          {
            size: "small",
            type: "error",
            onClick: () => confirmDelete(row),
          },
          { default: () => t("selfExam.columns.delete") }
        ),
      ];
    },
  },
]);

const handleDownload = () => {
  export_option.value = true;
}

const handleClose = () => {
  export_option.value = false;
}

async function confirmDelete(row: any) {
  try {
    await dialog.warning({
      title: t("utils.confirm"),
      content: t("utils.confirm_delete"),
      positiveText: t("utils.confirm"),
      negativeText: t("utils.cancel"),
      async onPositiveClick(e) {
        await handleDelete(row);
      },
      async onNegativeClick(e) {
        message.info("删除已取消")
      }
    });
  } catch(err) {
    console.log(err);
  }
}

const fetchExamItems = async () => {
  try {
    const res = await getInfoApi(pagination.page, pagination.pageSize);
    console.log(res)
    examItems.value = res.data.rows || [];
    pagination.itemCount = res.data.count || 0; // 后端返回总数
  } catch (err) {
    examItems.value = [];
    pagination.itemCount = 0;
  }
};

const pagination = reactive({
  page: 1,
  pageSize: 10,
  itemCount: 0, // 数据总数
  showSizePicker: true,
  pageSizes: [5, 10, 20],
  onUpdatePage: (page: number) => {
    pagination.page = page;
    fetchExamItems();
  },
  onUpdatePageSize: (pageSize: number) => {
    pagination.pageSize = pageSize;
    pagination.page = 1; 
    fetchExamItems();
  }
});

onMounted(fetchExamItems)
</script>

<style scoped>
.layout {
  display: flex;
  gap: 20px;
}

.device-page {
  margin-left: 16rem;
  flex: 1;
  padding-left: 1em;
  padding-right: 60px;
}

/* 底部导出区域容器 */
.export-footer-zone {
    margin-top: 30px;
    display: flex;
    justify-content: center; /* 居中显示 */
}

/* 导出卡片样式 */
.export-card {
    width: 100%;
    max-width: 800px; /* 限制最大宽度，避免拉得太长 */
    padding: 30px;
    border: 1px dashed #dcdfe6; /* 虚线边框更有“放置区”的感觉 */
    border-radius: 12px;
    background-color: #fafafc;
    display: flex;
    align-items: center;
    justify-content: space-between;
    cursor: pointer;
    transition: all 0.3s ease;
}

.export-card:hover {
    background-color: #f0f7ff;
    border-color: #18a058; /* Naive UI 默认绿色 */
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.export-content {
    display: flex;
    align-items: center;
    gap: 20px;
}

.export-icon {
    color: #18a058;
}

.export-text h3 {
    margin: 0;
    font-size: 18px;
    color: #333;
}

.export-text p {
    margin: 4px 0 0;
    font-size: 14px;
    color: #666;
}

.export-action span {
    font-weight: 600;
    color: #18a058;
    border: 1px solid #18a058;
    padding: 6px 16px;
    border-radius: 20px;
}
</style>