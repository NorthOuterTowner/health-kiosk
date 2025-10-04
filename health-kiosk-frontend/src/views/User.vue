<!--User Management Page-->
<template>
  <div class="layout">
    <Sidebar />
    <div class="user-page">
      <div style="display: flex;">
        <h2>{{ $t('user.title') }}</h2>
      
        <n-button style="margin-top: 20px; margin-bottom: 15px; margin-left: 50px;" size="medium" type="primary" @click="fetchUsers">{{ $t('user.fetch') }}</n-button>
        <n-button style="margin-top: 20px; margin-bottom: 15px; margin-left: 20px; padding-right: 20px; padding-left: 20px; text-align: center; " size="medium" type="primary" 
          @click="addUserView = true"
          >{{ $t('user.add_user_button_text') }}</n-button>
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

      <AddUser v-if="addUserView"
          @close="addUserView = false"
          @update="updateUserAfterAdd"/>
      <!-- 查看/编辑用户信息弹窗 -->
      <UserInfo
        v-if="editingUser"
        :user="editingUser"
        :editable = editable
        @close="editingUser=null"
        @update="updateUser"
      />

      <!-- 授权对话框 -->
      <n-modal v-model:show="authDialogVisible" preset="dialog" :title="$t('user.title')">
        <n-select
          v-model:value="selectedRole"
          :options="roleOptions"
          placeholder="{{ $t('user.auth.placeholder') }}"
          style="margin-bottom: 16px; width: 200px;"
        />
        <template #action>
          <n-button @click="authDialogVisible=false">{{ t('utils.cancel') }}</n-button>
          <n-button type="primary" @click="confirmAuth">{{ t('utils.confirm') }}</n-button>
        </template>
      </n-modal>
 
      </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, h, reactive } from "vue";
import Sidebar from "../components/Sidebar.vue";
import { UserListApi, authApi,deleteUserApi } from "../api/user";
import { NButton } from "naive-ui";
import UserInfo from "../components/UserInfo.vue";
import { useMessage } from 'naive-ui'
import UserBarchart from "../components/UserBarchart.vue";
import AddUser from "../components/AddUser.vue";
import { useI18n } from 'vue-i18n'

const { t } = useI18n();
const message = useMessage();

const users = ref<any[]>([]);

// user editting currently
const editingUser = ref<any | null>(null);
const addUserView = ref<boolean>(false);

// user suthorized currently
const authUser = ref<any | null>(null);
const authDialogVisible = ref(false);
const selectedRole = ref<number | null>(null);

const adminRole: number | null = Number(localStorage.getItem("role")).valueOf();

// using for n-select
const roleOptions = [
  { label: t('user.role.0'), value: 0, disabled: adminRole < 0 },
  { label: t('user.role.1'), value: 1, disabled: adminRole < 1 },
  { label: t('user.role.2'), value: 2, disabled: adminRole < 2 },
  { label: t('user.role.3'), value: 3, disabled: adminRole < 3 },
  { label: t('user.role.4'), value: 4, disabled: adminRole < 4 },
  { label: t('user.role.5'), value: 5, disabled: adminRole < 5 }
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
    title: t('user.columns.name'),
    key: "name",
  },
  {
    title: t('user.columns.email'),
    key: "email",
  },
  {
    title: t('user.columns.role'),
    key: "role",
    render(row: any) {
      return roleText(row.role);
    }
  },
  {
    title: t('user.columns.action'),
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
          { default: () => t('user.columns.auth') }
        ),
        h(
          NButton,
          {
            size: "small",
            type: "info",
            style: "margin-right: 6px;",
            onClick: () => handleView(row),
          },
          { default: () => t('user.columns.watch') }
        ),
        h(
          NButton,
          {
            size: "small",
            type: "info",
            style: "margin-right: 6px;",
            onClick: () => handleEdit(row),
          },
          { default: () => t('utils.edit') }
        ),
        h(
          NButton,
          {
            size: "small",
            type: "error",
            onClick: () => handleDelete(row),
          },
          { default: () => t('utils.delete') }
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

// Operation function
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
  console.log(updatedUser)
  const index = users.value.findIndex(u => u.account === updatedUser.account);
  if(index !== -1) {
    users.value[index] = updatedUser;
  }
  editingUser.value = null;
}

const updateUserAfterAdd = () => {
  fetchUsers();
  addUserView.value = false;
}

const handleDelete = (row: any) => {
  console.log(row)
  deleteUserApi(row.account)
  fetchUsers();
};

// get users' information
const fetchUsers = async () => {
  try {
    const res = await UserListApi(pagination.page, pagination.pageSize);
    users.value = res.data.rows || [];
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
