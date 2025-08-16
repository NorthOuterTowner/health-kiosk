<template>
  <aside :class="['sidebar', { collapsed }]">
    <!-- Header -->
    <div class="sidebar-header">
      <button class="hamburger" @click="toggleCollapse">
        <span class="line" :class="{ active: collapsed }"></span>
        <span class="line" :class="{ active: collapsed }"></span>
        <span class="line" :class="{ active: collapsed }"></span>
      </button>
      <router-link to="/dashboard" class="logo">
        <span v-if="!collapsed">铁路司机用健康检测系统</span>
      </router-link>
    </div>

    <!-- Menu -->
    <nav class="menu">
      <div v-for="section in menuConfig" :key="section.key" class="menu-section">
        <button class="menu-title" @click="toggleSection(section.key)">
          <component :is="section.icon" class="icon" />
          <span v-if="!collapsed">{{ section.title }}</span>
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
              <span>{{ item.name }}</span>
            </router-link>
          </div>
        </transition>
      </div>
    </nav>
  </aside>
</template>

<script setup>
import { ref, reactive } from 'vue'
import {
  ChevronDownIcon,
  InboxIcon,
  MapPinIcon,
  TrendingUpIcon,
  AlertTriangleIcon,
  ShieldIcon,
  UserIcon,
  FileTextIcon,
  SettingsIcon
} from 'lucide-vue-next'

const props = defineProps({ collapsed: Boolean })
const emit = defineEmits(['toggle-collapse'])

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

const menuConfig = [
  //此处根据功能需求进行展示
  {
    key: 'predict',
    title: '预测显示',
    icon: InboxIcon,
    children: [
      { path: '/dashboard/mapView', name: '预测地图', icon: MapPinIcon },
      { path: '/dashboard/predict', name: '预测结果', icon: TrendingUpIcon }
    ]
  },
  {
    key: 'monitor',
    title: '风险监测',
    icon: AlertTriangleIcon,
    children: [
      { path: '/dashboard/monitor', name: '监测看板', icon: ShieldIcon }
    ]
  },
  {
    key: 'users',
    title: '用户管理',
    icon: UserIcon,
    children: [
      { path: '/dashboard/users', name: '用户列表', icon: UserIcon }
    ]
  },
  {
    key: 'contracts',
    title: '合同管理',
    icon: FileTextIcon,
    children: [
      { path: '/dashboard/contracts', name: '合同列表', icon: FileTextIcon }
    ]
  },
  {
    key: 'settings',
    title: '系统设置',
    icon: SettingsIcon,
    children: [
      { path: '/dashboard/settings', name: '设置项', icon: SettingsIcon }
    ]
  }
]
</script>

<style scoped>
.sidebar {
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
  justify-content: space-between;
  height: 4rem;
  padding: 0 1rem;
  border-bottom: 1px solid #334155;
}
.logo {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
.logo img {
  height: 2rem;
}
.logo span {
  font-weight: bold;
  font-size: 0.9rem;
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
