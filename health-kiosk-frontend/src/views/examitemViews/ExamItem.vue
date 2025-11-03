<!-- Exam Item Management Page--> 
<template>
    <div class="layout">
        <Sidebar />
        <div class="device-page">
          <div style="display: flex;">
            <h2>{{ $t('examitem.title') }}</h2>
            <n-button style="margin-top: 20px; margin-bottom: 15px; margin-left: 20px; padding-right: 20px; padding-left: 20px; text-align: center; " size="medium" type="primary" 
              @click="addExamItemView = true; editable = true">{{ $t('examitem.add_button') }}</n-button>
          </div>
        
            <n-data-table
                remote
                :columns="columns"
                :data="examItems"
                :pagination="pagination"
                :bordered="true" 
                class="dataTable"
            />
            <AddExamItem
              v-if="addExamItemView"
              @close="addExamItemView = false"
              @update="updatePage"
            />
            <ExamItemInfo 
              v-if="editingItem"
              :examItem="editingItem"
              :editable="editable"
              @close="editingItem=null"
              @update="updateExamItem"
            />
        </div>
    </div>
</template>

<script setup lang = "ts">
import { ref, onMounted, h, reactive } from 'vue';
import Sidebar from "../components/Sidebar.vue";
import { NButton, useMessage, useDialog } from "naive-ui";
import AddExamItem from '../components/AddExamItem.vue';
import { deleteExamItemApi,getExamItemInfoApi, updateExamItemApi } from '../../api/examitem/examitem';
import { useI18n } from 'vue-i18n'
import ExamItemInfo from '../components/ExamItemInfo.vue';

const { t } = useI18n();
const addExamItemView = ref<boolean>(false)

const message = useMessage();
const dialog = useDialog();

const editable = ref(true)
const currentExamItem = ref<any | null>(null);

const editingItem = ref<any | null>(null);

const updatePage = ()=>{
  fetchExamItems();
  addExamItemView.value = false;
}

async function handleDelete(row: any){
  const res = await deleteExamItemApi(row.id,row.name);
  console.log(res.data)
  if(res.data.code === '200'){
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

const columns = [
  {
    title: t('examitem.columns.name'),
    key: "name",
    width: 100,
  },
  {
    title: t('examitem.columns.status'),
    key: "status",
    render(row: any) {
      return row.status == '1' ? t('examitem.add_item.enable') : t('examitem.add_item.disable')
    }
  },
  {
    title: t('examitem.columns.abbreviation'),
    key: "abbreviation",
  },
  {
    title: t('examitem.columns.description'),
    key: "description",
  },
  {
    title: t('examitem.columns.action'),
    key: "actions",
    render(row: any) {
      return [
        h(
          NButton,
          {
            size: "small",
            type: "error",
            style: "margin-right: 6px;",
            onClick: () => confirmDelete(row),
          },
          { default: () => t("utils.delete") }
        ),
        h(
          NButton,
          {
            size: "small",
            type: "info",
            style: "margin-right: 6px;",
            onClick: () => handleView(row),
          },
          { default: () => t("examitem.info") }
        ),
        h(
          NButton,
          {
            size: "small",
            type: "warning",
            style: "margin-right: 6px;",
            onClick: () => handleEdit(row),
          },
          { default: () => t("utils.edit") }
        )
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
    const res = await getExamItemInfoApi(pagination.page, pagination.pageSize);
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