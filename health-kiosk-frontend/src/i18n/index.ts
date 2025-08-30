import { createI18n } from 'vue-i18n'

const messages = {
  zh: {
    navbar: {
      intro: "系统简介",
      features: "功能介绍",
      language: "中文 | EN"
    },
    home: {
      title: "自助体检机后台管理系统",
      desc: "智能化、可视化、便捷化协助体检管理平台，支持人员管理、设备管理、数据统计等功能。",
      register: "立即注册",
      learnMore: "了解更多",
      login: "前往登录"
    },
    login: {
      title: "登录",
      username: "账号",
      password: "密码",
      button: "登录",
      alert: "请输入账号和密码"
    },
    register: {
      title: "注册",
      username: "账号",
      password: "密码",
      button: "注册",
      alert: "请输入账号和密码"
    }
  },
  en: {
    navbar: {
      intro: "Introduction",
      features: "Features",
      language: "EN | 中文"
    },
    home: {
      title: "Self-Service Health Check Management System",
      desc: "An intelligent, visual, and convenient platform supporting personnel management, device management, and data statistics.",
      register: "Register Now",
      learnMore: "Learn More",
      login: "Login Now"
    },
    login: {
      title: "Login",
      username: "Username",
      password: "Password",
      button: "Login",
      alert: "Please enter username and password"
    },
    register: {
      title: "Register",
      username: "Username",
      password: "Password",
      button: "Register",
      alert: "Please enter username and password"
    }
  }
}


const i18n = createI18n({
  legacy: false, // 使用Composition API模式
  locale: 'zh', // 默认语言
  fallbackLocale: 'en', // 备用语言
  messages
})

export default i18n