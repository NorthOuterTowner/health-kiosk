<!-- Role Management Page--> 
<template>
    <div class="layout">
        <Sidebar />
        <div class="device-page">
          <div style="display: flex;">
            <h2>{{ $t('role.title') }}</h2>
            <n-button style="margin-top: 20px; margin-bottom: 15px; margin-left: 20px; padding-right: 20px; padding-left: 20px; text-align: center; " size="medium" type="primary" 
              @click="addRoleView = true; editable = true">{{ $t('role.add_button') }}</n-button>
          </div>
        
            <n-data-table
                remote
                :columns="columns"
                :data="roles"
                :pagination="pagination"
                :bordered="true" 
                class="dataTable"
            />
            <AddRole
              v-if="addRoleView"
              @close="addRoleView = false"
              @update="updatePage"
            />

            <RoleInfo
              v-if="editingItem"
              :role="editingItem"
              :editable="editable"
              @update="updatePage"
              @close="editingItem=null"
            />

        </div>
    </div>
</template>

<script setup lang = "ts">
import { ref, onMounted, h, reactive } from 'vue';
import Sidebar from "../../components/Sidebar.vue";
import { NButton, useMessage, useDialog } from "naive-ui";
import AddRole from '../../components/role/AddRole.vue';
import { deleteRole, enableRole, disableRole, getRoles } from '../../api/permission/role';
import { useI18n } from 'vue-i18n'
import RoleInfo from '../../components/role/roleInfo.vue';

const { t } = useI18n();
const addRoleView = ref<boolean>(false)

const message = useMessage();
const dialog = useDialog();

const editable = ref(true)
const currentRole = ref<any | null>(null);

const editingItem = ref<any | null>(null);

const updatePage = ()=>{
  fetchRoles();
  addRoleView.value = false;
}

async function handleDelete(row: any){
  const res = await deleteRole(row.role_name);
  console.log(res.data)
  if(res.data.code === '200'){
    await fetchRoles()
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

async function handleUseChange(row: any) {
  if(row.use == '1'){
    await disableRole(row.role_name);
  }else if(row.use == '0'){
    await enableRole(row.role_name);
  }else{
    message.error("系统出错啦")
    return ;
  }
  await fetchRoles();
  return;
}

const roles = ref<any[]>([]);

const columns = [
  {
    title: "id",
    key:"role_id",
    width: 50
  },
  {
    title: t('role.columns.name'),
    key: "role_name",
    width: 100,
  },
  {
    title: t('role.columns.status'),
    key: "status",
    render(row: any) {
      return row.use == '1' ? t('role.add_item.enable') : t('role.add_item.disable')
    }
  },
  {
    title: t('role.columns.description'),
    key: "remark",
  },
  {
    title: t('role.columns.action'),
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
          { default: () => t("role.info") }
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
        ),
        h(
          NButton,
          {
            size: "small",
            type: "info",
            style: "margin-right:6px;",
            onClick: () => handleUseChange(row),
          },
          { default: () => t(`role.use${row.use}`)}
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

const fetchRoles = async () => {
  try {
    const res = await getRoles();
    roles.value = res.data.roles || [];
    pagination.itemCount = res.data.count || 0; // 后端返回总数
  } catch (err) {
    roles.value = [];
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
    fetchRoles();
  },
  onUpdatePageSize: (pageSize: number) => {
    pagination.pageSize = pageSize;
    pagination.page = 1; 
    fetchRoles();
  }
});

onMounted(fetchRoles)
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