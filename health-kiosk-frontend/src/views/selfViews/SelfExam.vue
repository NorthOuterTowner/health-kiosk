<!-- Self Exam Data Page--> 
<template>
    <div class="layout">
        <Sidebar />
        <div class="device-page">
          <div style="display: flex;">
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

        </div>
    </div>
</template>

<script setup lang = "ts">
import { ref, onMounted, h, reactive, render } from 'vue';
import Sidebar from "../../components/Sidebar.vue";
import { NButton, useMessage, useDialog } from "naive-ui";
import { useI18n } from 'vue-i18n'
import { deleteExamDataApi, getInfoApi } from '../../api/self/selfData';

const { t } = useI18n();

const message = useMessage();
const dialog = useDialog();

const editable = ref(true)

const editingItem = ref<any | null>(null);

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

async function handleView(row: any) {
  editingItem.value = { ...row };
  editable.value = false;
}

async function handleEdit(row: any) {
  editingItem.value = { ...row };
  editable.value = true;
}

const updateExamItem = (updatedExamItem: any) => {
  const index = examItems.value.findIndex(u => u.id === updatedExamItem.id);
  if(index !== -1) {
    examItems.value[index] = updatedExamItem;
  }
  editingItem.value = null;
}

const examItems = ref<any[]>([]);

const renderEcgPic = (row: any) => {
  const BASE_URL = "/pic/";
  const relative_path = row.ecg;
  return BASE_URL+relative_path;
}

const columns = [
  {
    title: t('selfExam.columns.tempor'),
    key: "tempor",
    width: 100,
  },
  {
    title: t('selfExam.columns.alcohol'),
    key: "alcohol",
    width: 100,
  },
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
          onClick: () => {
            renderEcgPic(row);
          }
        },
        {
          default: () => {
            return  "查看图像"
          }
        }
      )
    }
  },
  {
    title: t('selfExam.columns.sys'),
    key: "blood_sys",
    width: 100,
  },
  {
    title: t('selfExam.columns.dia'),
    key: "blood_dia",
    width: 100,
  },
  {
    title: t('selfExam.columns.hr'),
    key: "blood_hr",
    width: 100,
  },
  {
    title: t('selfExam.columns.date'),
    key: "date",
    width: 150,
    render(row: any) {
      return row.date.split('T')[0];
    }
  },
  {
    title: t('selfExam.columns.time'),
    key: "time",
    width: 100,
    render(row: any){
      if(row.time == 1) {
        return "上午"
      }else {
        return "下午"
      }
    }
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
            type: "info",
            style: "margin-right: 6px;",
            onClick: () => confirmDelete(row),
          },
          { default: () => t("selfExam.columns.delete") }
        ),
      ];
    },
  },
];

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
</style>