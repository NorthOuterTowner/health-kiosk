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
        <span v-if="!collapsed">{{ $t( 'sidebar.title' ) }}</span>
      </router-link>
    </div>

    <!-- Menu -->
    <nav class="menu">
      <div v-for="section in menuConfig" :key="section.key" class="menu-section">
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
  SettingsIcon,
  UserStar,
  LayoutGrid,
  Database,
  ChartNoAxesCombined
} from 'lucide-vue-next'
import { useI18n } from 'vue-i18n'

const { locale } = useI18n()

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
  {
    key: 'users',
    title: 'sidebar.users.title',
    icon: UserStar,
    children: [
      { path: '/dashboard/users', name: 'sidebar.users.list', icon: UserStar }
    ]
  },
  {
    key: 'app',
    title: 'sidebar.devices.title',
    icon: LayoutGrid,
    children: [
      { path: '/dashboard/contracts', name: 'sidebar.devices.list', icon: LayoutGrid }
    ]
  },
  {
    key: 'settings',
    title: 'sidebar.data.title',
    icon: Database,
    children: [
      { path: '/dashboard/settings', name: 'sidebar.data.statistics', icon: ChartNoAxesCombined },
      { path: '/dashboard/settings', name: 'sidebar.data.self', icon: Database },
    ]
  },
  {
    key: 'info',
    title: 'sidebar.info.title',
    icon: UserIcon,
    children: [
      { path: '/dashboard/settings', name: 'sidebar.info.watch', icon: UserIcon }
    ]
  },
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
