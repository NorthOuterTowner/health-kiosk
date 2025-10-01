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
              :item="currentExamItem"
              :editable="editable"
              @close="addExamItemView = false"
              @update="updatePage"
            />
        </div>
    </div>
</template>

<script setup lang = "ts">
import { ref, onMounted, h, reactive } from 'vue';
import Sidebar from "../components/Sidebar.vue";
import { NButton, useMessage } from "naive-ui";
import AddExamItem from '../components/AddExamItem.vue';
import { deleteExamItemApi,getExamItemInfoApi,updateExamItemApi } from '../api/examitem';
import { useI18n } from 'vue-i18n'

const { t } = useI18n();
const addExamItemView = ref<boolean>(false)

const message = useMessage();
const editable = ref(true)
const currentExamItem = ref<any | null>(null);

const updatePage = ()=>{
  fetchExamItems();
  addExamItemView.value = false;
}

async function handleDelete(row: any){
    const res = await deleteExamItemApi(row.id,row.name);
    if(res.data.code === 200){
      fetchExamItems()
      message.info("删除成功")
    }else{
      message.error(res.data.msg);
    }
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
      return row.status == '1' ? "enable" : "disable"
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
            type: "info",
            style: "margin-right: 6px;",
            onClick: () => handleDelete(row),
          },
          { default: () => t("utils.delete") }
        )
      ];
    },
  },
];

const fetchExamItems = async () => {
  try {
    const res = await getExamItemInfoApi(pagination.page, pagination.pageSize);
    examItems.value = res.data.rows || [];
    console.log(res.data)
    pagination.itemCount = res.data.count || 0; // 后端返回总数
  } catch (err) {
    examItems.value = [];
    pagination.itemCount = 0;
  }
};

const pagination = reactive({
  page: 1,
  pageSize: 5,
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