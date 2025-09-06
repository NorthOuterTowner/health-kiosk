<!-- Device Management Page-->
<template>
    <div class="layout">
        <Sidebar />
        <div class="device-page">
            <n-data-table
                remote
                :columns="columns"
                :data="devices"
                :pagination="pagination"
                :bordered="true" 
                class="dataTable"
            />
        </div>
    </div>
</template>

<script setup lang = "ts">
import { ref, onMounted, h, reactive } from "vue";
import Sidebar from "../components/Sidebar.vue";
import { UserListApi, authApi } from "../api/user";
import { getDeviceInfoApi } from "../api/device";
import { NButton } from "naive-ui";

function handleDownload(row: any){
    console.log("download")
}

function handleUpdate(row: any){
    console.log("download")
}

function handleDelete(row: any){
    console.log("download")
}

const devices = ref<any[]>([]);

const columns = [
  {
    title: "版本",
    key: "version",
    width: 100,
  },
  {
    title: "类型",
    key: "type",
  },
  {
    title: "下载量",
    key: "num",
  },
  {
    title: "操作",
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
          { default: () => "下载" }
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
          { default: () => "删除" }
        ) : null,
        Number(localStorage.getItem("role") ?? "0") >= 3 ?
        h(
          NButton,
          {
            size: "small",
            type: "error",
            onClick: () => handleUpdate(row),
          },
          { default: () => "更新" }
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