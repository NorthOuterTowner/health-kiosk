import { createI18n } from 'vue-i18n'
import { navbarMessages, homeMessages, loginMessages, registerMessages } from './modules/home'
import { sidebarMessages } from './modules/sidebar'

const Messages = {
  zh: {
    navbar: navbarMessages.zh,
    home: homeMessages.zh,
    login: loginMessages.zh,
    register: registerMessages.zh,
    sidebar:sidebarMessages.zh
  },
  en: {
    navbar: navbarMessages.en,
    home: homeMessages.en,
    login: loginMessages.en,
    register: registerMessages.en,
    sidebar: sidebarMessages.en
  }
}

export const i18n = createI18n({
  legacy: false,
  locale: 'zh',
  fallbackLocale: 'en',
  messages: Messages
})
