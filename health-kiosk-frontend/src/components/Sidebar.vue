<template>
  <aside :class="['sidebar', { collapsed }]">
    <!-- Header -->
    <div class="sidebar-header">
      <button class="hamburger" @click="toggleCollapse">
        <span class="line" :class="{ active: collapsed }"></span>
        <span class="line" :class="{ active: collapsed }"></span>
        <span class="line" :class="{ active: collapsed }"></span>
      </button>
      <router-link to="/" class="logo">
        <span v-if="!collapsed">{{ $t( 'sidebar.title' ) }}</span>
      </router-link>
    </div>

    <!-- Menu -->
    <nav class="menu">
      <div v-for="section in visibleMenuConfig" :key="section.key" class="menu-section">
        <button class="menu-title" @click="toggleSection(section.key)">
          <component :is="section.icon" class="icon" />
          <span v-if="!collapsed">{{ $t(section.title) }}</span>
          <ChevronDownIcon
            v-if="!collapsed"
            class="chevron"
            :class="{ open: openSections[section.key] }"
          />
        </button>

        <transition name="submenu">
          <div v-show="openSections[section.key] && !collapsed" class="submenu">
            <router-link
              v-for="item in section.children"
              :key="item.path"
              :to="item.path"
              class="submenu-item"
              active-class="active"
            >
              <component :is="item.icon" class="icon" />
              <span>{{ $t( item.name ) }}</span>
            </router-link>
          </div>
        </transition>
      </div>
    </nav>
  </aside>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import {
  ChevronDownIcon,
  InboxIcon,
  MapPinIcon,
  TrendingUpIcon,
  AlertTriangleIcon,
  ShieldIcon,
  UserIcon,
  FileTextIcon,
  SettingsIcon,
  UserStar,
  LayoutGrid,
  Database,
  ChartNoAxesCombined,
  HeartPulse,
  FilePenLine,
  FileText,
  FileUser,
  CircleChevronRight,
  TicketCheck,
  User,
  ShieldUser,
  Info,
  ChartBarBig
} from 'lucide-vue-next'
import { useI18n } from 'vue-i18n'
import { getPermissions } from '../api/permission/permission'

let permissions = {};

const { locale } = useI18n()

const props = defineProps({ collapsed: Boolean })
const emit = defineEmits(['toggle-collapse'])

const visitRight = Number(localStorage.getItem("role")) ?? 0;

const openSections = reactive({
  predict: false,
  monitor: false,
  users: false,
  contracts: false,
  settings: false
})

const toggleCollapse = () => {
  emit('toggle-collapse')
}

const toggleSection = (section) => {
  openSections[section] = !openSections[section]
}

/* role:
 * 0 => 访客（未注册的用户）
 * 1 => 游客（前端注册的用户）
 * 2 => 用户（Android设备端注册的用户）
 * 3 => 管理员（可进行用户授权和相关后台管理内容）
 * 4 => 超级管理员（可进行管理员授权）
 * 5 => 开发者（可访问测试和开发期的内容）
 */
let menuConfig = [
  {
    // 用户管理模块
    key: 'users',
    title: 'sidebar.users.title',
    icon: User,
    role: [3,4,5],
    children: [
      { key: 'users:list',path: '/user', name: 'sidebar.users.list', icon: UserStar, role: [3,4,5] },
      { key: 'users:role', path: '/role', name: 'sidebar.role.role', icon: CircleChevronRight, role: [3,4,5]},
      { key: 'users:permission', path: '/roleAssign', name: 'sidebar.role.permission' , icon: TicketCheck, role: [3,4,5]}
    ]
  },
  { 
    // 软件管理模块
    key: 'app',
    title: 'sidebar.devices.title',
    icon: LayoutGrid,
    role: [0,1,2,3,4,5],
    children: [
      { key: 'app:list', path: '/device', name: 'sidebar.devices.list', icon: LayoutGrid, role: [0,1,2,3,4,5] }
    ]
  },
  {
    // 体检数据趋势查询
    key: 'examdata',
    title: 'sidebar.data.title',
    icon: Database,
    role: [2,3,4,5],
    children: [
      { key: 'examdata:list', path: '/examdata', name: 'sidebar.data.statistics', icon: ChartNoAxesCombined, role: [2,3,4,5]}
    ]
  },
  {
    // 个人信息设置
    key: 'info',
    title: 'sidebar.info.title',
    icon: ShieldUser,
    role: [1,2,3,4,5],
    children: [
      { key: 'info:selfinfo', path: '/selfinfo', name: 'sidebar.info.watch', icon: Info, role: [1,2,3,4,5] },
      { key: 'info:selfexam', path: '/selfexam', name: 'sidebar.info.exam', icon: Database, role: [2,3,4,5] },
      { key: 'info:selfAnalyze', path: '/selfanalyze', name: 'sidebar.info.analyze', icon: ChartBarBig, role: [2,3,4,5]}
    ]
  },
  {
    // 体检项目管理
    key: 'item',
    title: 'sidebar.examItem.title',
    icon: HeartPulse,
    role: [3,4,5],
    children: [
      { key: 'item:list', path: '/ExamItem', name: 'sidebar.examItem.list', icon: HeartPulse, role: [3,4,5] }
    ]
  },
  {
    // 使用说明
    key: 'use',
    title: 'sidebar.use.title',
    icon:FileText,
    role:[0,1,2,3,4,5],
    children: [
      { key: 'use:instruction', path: '/userintruction', name: 'sidebar.use.userintruction', icon: FileText , role: [0,1,2,3,4,5] },
      { key: 'use:about', path: '/about', name: 'sidebar.use.about', icon: FileText, role: [0,1,2,3,4,5]},
      { key: 'use:admin', path: '/adminintruction', name: 'sidebar.use.adminintruction', icon: FileUser, role: [3,4,5] }
    ]
  }
]

function filterMenu(config, role) {
  return config
    .filter(section => !section.role || section.role.includes(role))
    .map(section => {
      const children = section.children
        ? section.children.filter(item => !item.role || item.role.includes(role))
        : []
      return { ...section, children }
    })
    .filter(section => section.children.length > 0 || !section.children)
}

const visibleMenuConfig = ref([])
visibleMenuConfig.value = filterMenu(menuConfig, visitRight);

onMounted(async () => {
  // 获取权限数据
  const res = await getPermissions();
  if (!res || !res.data.config) return;

  const permissionList = res.data.config; // [{key, permission}, ...]
  console.log(permissionList)
  // 重置每个菜单及子菜单的 role
  for (let menu of menuConfig) {
    menu.role = [];
    if (menu.children) {
      for (let sub of menu.children) {
        sub.role = [];
      }
    }
  }

  // 根据 permissionList 填充 role
  for (const { key, permission } of permissionList) {
    // 先匹配一级菜单
    const foundMenu = menuConfig.find(m => m.key === key);
    if (foundMenu) {
      console.log("setset")
      foundMenu.role = permission;
      continue;
    }

    // 再匹配子菜单
    for (let menu of menuConfig) {
      if (!menu.children) continue;
      const sub = menu.children.find(child => child.key === key);
      if (sub) {
        sub.role = permission;
        break;
      }
    }
  }
  visibleMenuConfig.value = filterMenu(menuConfig, visitRight);
});

</script>

<style scoped>
.sidebar {
  position: fixed;
  width: 16rem;
  height: 100vh;
  background: linear-gradient(to bottom, #0f172a, #1e293b);
  color: #e2e8f0;
  transition: width 0.3s ease-in-out;
  display: flex;
  flex-direction: column;
}
.sidebar.collapsed {
  width: 4rem;
}

/* Header */
.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: center center;
  height: 4rem;
  padding: 0 1rem;
  border-bottom: 1px solid #334155;
}
.logo {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: rgb(241, 229, 208);
  text-decoration: none;
}
.logo img {
  height: 2rem;
}
.logo span {
  font-weight: bold;
  font-size: 1.1rem;
  white-space: nowrap;
}

/* Hamburger */
.hamburger {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  border: none;
  background: none;
  cursor: pointer;
  margin-right: 15px;
}
.line {
  width: 1.5rem;
  height: 0.2rem;
  background: #e2e8f0;
  border-radius: 2px;
  transition: transform 0.3s, opacity 0.3s;
}
.line.active:nth-child(1) {
  transform: rotate(45deg) translate(0.3rem, 0.3rem);
}
.line.active:nth-child(2) {
  opacity: 0;
}
.line.active:nth-child(3) {
  transform: rotate(-45deg) translate(0.3rem, -0.3rem);
}

/* Menu */
.menu {
  flex: 1;
  overflow-y: auto;
  padding: 1rem 0.5rem;
}
.menu-section {
  margin-bottom: 0.5rem;
}
.menu-title {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 0.5rem;
  background: none;
  border: none;
  color: inherit;
  cursor: pointer;
  border-radius: 0.5rem;
  transition: background 0.2s;
}
.menu-title:hover {
  background: #334155;
}
.icon {
  width: 1.25rem;
  height: 1.25rem;
}
.menu-title span {
  margin-left: 0.75rem;
}
.chevron {
  margin-left: auto;
  transition: transform 0.3s;
}
.chevron.open {
  transform: rotate(180deg);
}

/* Submenu */
.submenu {
  margin-left: 2rem;
  margin-top: 0.25rem;
  display: flex;
  flex-direction: column;
}
.submenu-item {
  display: flex;
  align-items: center;
  padding: 0.4rem 0.5rem;
  color: #cbd5e1;
  text-decoration: none;
  border-radius: 0.5rem;
  transition: background 0.2s, color 0.2s;
}
.submenu-item:hover {
  background: #475569;
  color: white;
}
.submenu-item.active {
  background: #1e40af;
  color: white;
}
.submenu-item .icon {
  margin-right: 0.5rem;
}

/* Submenu Animation */
.submenu-enter-active,
.submenu-leave-active {
  transition: all 0.3s ease;
  overflow: hidden;
}
.submenu-enter-from,
.submenu-leave-to {
  max-height: 0;
  opacity: 0;
}
.submenu-enter-to,
.submenu-leave-from {
  max-height: 500px;
  opacity: 1;
}
</style>
