<!--User Management Page-->
<template>
  <div class="layout">
    <Sidebar />
    <div class="user-page">
      <div style="display: flex;">
        <h2>用户管理</h2>
      
        <n-button style="margin-top: 20px; margin-bottom: 15px; margin-left: 50px;" size="medium" type="primary" >刷新用户列表</n-button>
        <n-button style="margin-top: 20px; margin-bottom: 15px; margin-left: 20px; padding-right: 20px; padding-left: 20px; text-align: center; " size="medium" type="primary">添加新用户</n-button>
      </div>
      
      <UserBarchart />

      <n-data-table
        remote
        :columns="columns"
        :data="users"
        :pagination="pagination"
        :bordered="true" 
        class="dataTable"
      />

      <!-- 查看/编辑用户信息弹窗 -->
      <UserInfo
        v-if="editingUser"
        :user="editingUser"
        :editable = editable
        @close="editingUser=null"
        @update="updateUser"
      />

      <!-- 授权对话框 -->
      <n-modal v-model:show="authDialogVisible" preset="dialog" title="设置权限">
        <n-select
          v-model:value="selectedRole"
          :options="roleOptions"
          placeholder="选择权限等级"
          style="margin-bottom: 16px; width: 200px;"
        />
        <template #action>
          <n-button @click="authDialogVisible=false">取消</n-button>
          <n-button type="primary" @click="confirmAuth">确认</n-button>
        </template>
      </n-modal>
 
      </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, h, reactive } from "vue";
import Sidebar from "../components/Sidebar.vue";
import { UserListApi, authApi } from "../api/user";
import { NButton } from "naive-ui";
import UserInfo from "../components/UserInfo.vue";
import { useMessage } from 'naive-ui'
import UserBarchart from "../components/UserBarchart.vue";

const message = useMessage();

const users = ref<any[]>([]);

// user editting currently
const editingUser = ref<any | null>(null);

// user suthorized currently
const authUser = ref<any | null>(null);
const authDialogVisible = ref(false);
const selectedRole = ref<number | null>(null);

const adminRole: number | null = Number(localStorage.getItem("role")).valueOf();

// using for n-select
const roleOptions = [
  { label: "游客", value: 0, disabled: adminRole < 0 },
  { label: "访客", value: 1, disabled: adminRole < 1 },
  { label: "用户", value: 2, disabled: adminRole < 2 },
  { label: "管理员", value: 3, disabled: adminRole < 3 },
  { label: "超级管理员", value: 4, disabled: adminRole < 4 },
  { label: "开发者", value: 5, disabled: adminRole < 5 }
];

// return correspinding text of role when the page should show the role content
const roleText = (role: number) => {
  const option = roleOptions.find(o => o.value === role);
  return option ? option.label : role;
};

const confirmAuth = async () => {
  if (!authUser.value) return;
  try {
    if (authUser.value.account) {
      await authApi(authUser.value.account, String(selectedRole.value ?? ""));
      message.info("授权成功")
    } else {
      message.error("用户账号不存在，无法授权");
      return;
    }
    authUser.value.role = selectedRole.value ?? 0;
    authDialogVisible.value = false;
  } catch (err) {
    message.error("授权失败:"+err);
  }
};

// 表格列
const columns = [
  {
    title: "ID",
    key: "account",
    width: 100,
  },
  {
    title: "用户名",
    key: "name",
  },
  {
    title: "邮箱",
    key: "email",
  },
  {
    title: "角色",
    key: "role",
    render(row: any) {
      return roleText(row.role);
    }
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
            onClick: () => handleAuth(row),
          },
          { default: () => "授权" }
        ),
        h(
          NButton,
          {
            size: "small",
            type: "info",
            style: "margin-right: 6px;",
            onClick: () => handleView(row),
          },
          { default: () => "查看" }
        ),
        h(
          NButton,
          {
            size: "small",
            type: "info",
            style: "margin-right: 6px;",
            onClick: () => handleEdit(row),
          },
          { default: () => "编辑" }
        ),
        h(
          NButton,
          {
            size: "small",
            type: "error",
            onClick: () => handleDelete(row),
          },
          { default: () => "删除" }
        )
      ];
    },
  },
];

// 分页
const pagination = reactive({
  page: 1,
  pageSize: 5,
  itemCount: 0, // 数据总数
  showSizePicker: true,
  pageSizes: [5, 10, 20],
  onUpdatePage: (page: number) => {
    pagination.page = page;
    fetchUsers();
  },
  onUpdatePageSize: (pageSize: number) => {
    pagination.pageSize = pageSize;
    pagination.page = 1; 
    fetchUsers();
  }
});

const editable = ref(false)

// 操作函数
const handleAuth = (row: any) => {
  //openAuthDialog(row);
  authUser.value = row;
  selectedRole.value = row.role;
  authDialogVisible.value = true;
};

const handleEdit = (row: any) => {
  editingUser.value = { ...row }
  editable.value = true
};

const handleView = (row: any) => {
  editingUser.value = { ...row }
  editable.value = false
}

const updateUser = (updatedUser: any) => {
  const index = users.value.findIndex(u => u.account === updatedUser.account);
  if(index !== -1) {
    users.value[index] = updatedUser;
  }
  editingUser.value = null;
}

const handleDelete = (row: any) => {
  console.log("删除:", row);
};

// get users' information
const fetchUsers = async () => {
  try {
    const res = await UserListApi(pagination.page, pagination.pageSize);
    users.value = res.data.rows || [];
    console.log(res.data.count===6)
    pagination.itemCount = res.data.count || 0; // 后端返回总数
  } catch (err) {
    console.error("获取用户失败:", err);
    users.value = [];
    pagination.itemCount = 0;
  }
};

onMounted(fetchUsers);
</script>

<style scoped>
.layout {
  display: flex;
  gap: 20px;
}

.user-page {
  margin-left: 16rem;
  flex: 1;
  padding-left: 1em;
  padding-right: 60px;
}
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0,0,0,0.3);
  padding-top: 40px;
  z-index: 1000;
  color: black;
}
.dataTable {
  width: 1050px;
  left: 4em;
  padding-top: 10px;
}
</style>
