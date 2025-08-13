import { createI18n } from 'vue-i18n'

// 准备语言环境信息
const messages = {
  zh: {

  },
  en: {

  }
}

const i18n = createI18n({
  legacy: false, // 使用Composition API模式
  locale: 'zh', // 默认语言
  fallbackLocale: 'en', // 备用语言
  messages
})

export default i18n