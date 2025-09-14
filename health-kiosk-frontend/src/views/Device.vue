<!-- Device Management Page--> 
<template>
    <div class="layout">
        <Sidebar />
        <div class="device-page">
          <div style="display: flex;">
            <h2>{{ $t('device.title') }}</h2>
            <n-button style="margin-top: 20px; margin-bottom: 15px; margin-left: 20px; padding-right: 20px; padding-left: 20px; text-align: center; " size="medium" type="primary" 
              @click="addDeviceView = true; editable = true">{{ $t('device.add_button') }}</n-button>
          </div>
        
            <n-data-table
                remote
                :columns="columns"
                :data="devices"
                :pagination="pagination"
                :bordered="true" 
                class="dataTable"
            />
            <AddDevice
              v-if="addDeviceView"
              :device="currentDevice"
              :editable="editable"
              @close="addDeviceView = false"
              @update="updatePage"
            />
        </div>
    </div>
</template>

<script setup lang = "ts">
import { ref, onMounted, h, reactive } from 'vue';
import Sidebar from "../components/Sidebar.vue";
import { getDeviceInfoApi,downloadDeviceApi,deleteDeviceApi } from "../api/device";
import { NButton, useMessage } from "naive-ui";
import AddDevice from "../components/AddDevice.vue";
import { useI18n } from 'vue-i18n'

const { t } = useI18n();
const addDeviceView = ref<boolean>(false)

const message = useMessage();
const editable = ref(true)
const currentDevice = ref<any | null>(null);

const updatePage = ()=>{
  fetchDevices();
  addDeviceView.value = false;
}

async function handleDownload(row: any) {
  try {
    const res = await downloadDeviceApi(row.version, row.type);

    const blob = new Blob([res.data]);
    const url = window.URL.createObjectURL(blob);

    const a = document.createElement('a');
    a.href = url;
    const typeStr = row.type === '1' ? "release" : "debug";
    a.download = `healthKiosk_${typeStr}_${row.version}.apk`; // the name of download file
    a.click();

    window.URL.revokeObjectURL(url);

    message.info("已开始下载");
  } catch (err: any) {
    console.error("下载出错:", err);
    message.error("下载失败");
  }
}

async function handleUpdate(row: any){
  currentDevice.value = { ...row };
  editable.value = false;
  addDeviceView.value = true;
}

async function handleDelete(row: any){
    const res = await deleteDeviceApi(row.version,row.type);
    if(res.data.code === 200){
      fetchDevices()
      message.info("删除成功")
    }else{
      message.error(res.data.msg);
    }
}

const devices = ref<any[]>([]);

const columns = [
  {
    title: t('device.columns.version'),
    key: "version",
    width: 100,
  },
  {
    title: t('device.columns.type'),
    key: "type",
    render(row: any) {
      return row.type === '1' ? "release" : "debug"
    }
  },
  {
    title: t('device.columns.num'),
    key: "num",
  },
  {
    title: t('device.columns.action'),
    key: "actions",
    render(row: any) {
      return [
        h(
          NButton,
          {
            size: "small",
            type: "success",
            style: "margin-right: 6px;",
            onClick: () => handleDownload(row),
          },
          { default: () => t("utils.download") }
        ),
        Number(localStorage.getItem("role") ?? "0") >= 3 ?
        h(
          NButton,
          {
            size: "small",
            type: "info",
            style: "margin-right: 6px;",
            onClick: () => handleDelete(row),
          },
          { default: () => t("utils.delete") }
        ) : null,
        Number(localStorage.getItem("role") ?? "0") >= 3 ?
        h(
          NButton,
          {
            size: "small",
            type: "error",
            onClick: () => handleUpdate(row),
          },
          { default: () => t("utils.update") }
        ) : null
      ];
    },
  },
];

const fetchDevices = async () => {
  try {
    const res = await getDeviceInfoApi(pagination.page, pagination.pageSize);
    devices.value = res.data.rows || [];
    pagination.itemCount = res.data.count || 0; // 后端返回总数
  } catch (err) {
    devices.value = [];
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
    fetchDevices();
  },
  onUpdatePageSize: (pageSize: number) => {
    pagination.pageSize = pageSize;
    pagination.page = 1; 
    fetchDevices();
  }
});

onMounted(fetchDevices)
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