<!-- RoleAssign.vue -->
<template>
  <div class="layout">
    <Sidebar />

    <div class="device-page">
      <div style="display: flex; align-items: center;">
        <h2>{{ $t('role.assign.title') }}</h2>
        <n-button
          type="primary"
          size="medium"
          style="margin-left: 20px; margin-top: 20px;"
          @click="handleSave"
        >
          {{ $t('role.assign.save') }}
        </n-button>
      </div>

      <!-- select role -->
      <div style="margin-top: 20px;">
        <label style="margin-right: 10px;">{{ $t('role.assign.select_role') }}:</label>
        <n-select
          v-model:value="selectedRole"
          :options="roleOptions"
          placeholder="选择角色"
          style="width: 250px;"
          @update:value="loadRolePermissions"
        />
      </div>

      <!-- 权限树 -->
      <div style="margin-top: 30px;">
        <h3>{{ $t('role.assign.permission_tree') }}</h3>
        <n-tree
          :data="treeData"
          checkable
          block-line
          expand-on-click
          :checked-keys="checkedKeys"
          @update:checked-keys="checkedKeys = $event"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import Sidebar from "../components/Sidebar.vue";
import { useMessage } from "naive-ui";
import { getRoles } from "../../api/permission/role";
import { getPermissionTree, reassign, getPermissionsConfig } from "../../api/permission/permission";
import { NTree } from "naive-ui";
const message = useMessage();

const selectedRole = ref<string | null>(null);
const roleOptions = ref<{ label: string; value: string }[]>([]);
const treeData = ref<any[]>([]);
const checkedKeys = ref<number[]>([]);

/** 加载所有角色 */
async function loadRoles() {
  try {
    const res = await getRoles();
    if (res.data.roles) {
      roleOptions.value = res.data.roles.map((r: any) => ({
        label: r.role_name,
        value: r.role_id,
      }));
    }
  } catch (err) {
    message.error("加载角色失败");
  }
}

/** 加载权限树 */
async function loadTree() {
  try {
    const res = await getPermissionTree();
    const list = res.data.functions;
    console.log(list)
    // 构造树结构
    const map = new Map<number, any>();
    list.forEach((f: any) =>
      map.set(f.function_id, {
        key: f.function_id,
        label: f.remark,
        children: []
      })
    );

    const roots: any[] = [];
    list.forEach((f: any) => {
      const node = map.get(f.function_id);
      if (f.parent && map.has(f.parent)) {
        map.get(f.parent).children.push(node);
      } else {
        roots.push(node);
      }
    });

    // 清理空 children，防止无子节点也显示展开箭头
    const cleanTree = (nodes: any[]) =>
      nodes.map(n => {
        const cleaned = { ...n };
        if (cleaned.children?.length) {
          cleaned.children = cleanTree(cleaned.children);
        } else {
          delete cleaned.children; // 关键：删除空 children
        }
        return cleaned;
      });

    treeData.value = cleanTree(roots);
  } catch (err) {
    message.error("加载权限树失败");
  }
}

/** 加载当前角色已有权限 */
async function loadRolePermissions(roleIdRaw: string) {
  try {
    if (!roleIdRaw) return;

    const roleIdNum = Number(roleIdRaw);

    const configRes = await getPermissionsConfig();
    if (configRes.data.code !== 200) {
      message.error("加载权限配置失败");
      return;
    }

    // 直接拿 function_id（即 config 中的 role_id 字段）
    const ownedIds = configRes.data.config
      .filter((item: any) => item.permission.includes(roleIdNum))
      .map((item: any) => item.function_id); // 注意这里是 role_id（function 的 id）

    checkedKeys.value = ownedIds;
    console.log("checkedKeys", checkedKeys.value);

  } catch (err) {
    console.error(err);
    message.error("加载角色权限失败");
  }
}

async function handleSave() {
  if (!selectedRole.value) {
    message.warning("请先选择角色");
    return;
  }
  try {
    const res = await reassign(selectedRole.value,checkedKeys.value);
    if (res.data.code === 200) {
      message.success("权限分配成功");
    } else {
      message.error(res.data.msg || "分配失败");
    }
  } catch (err) {
    message.error("服务器错误");
  }
}

onMounted(() => {
  loadRoles();
  loadTree();
});
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

h2 {
  margin-top: 20px;
}

.n-tree {
  margin-top: 10px;
}
</style>
