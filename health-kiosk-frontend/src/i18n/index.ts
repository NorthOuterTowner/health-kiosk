import { createI18n } from 'vue-i18n'
import { navbarMessages, homeMessages, loginMessages, registerMessages } from './modules/home'
import { sidebarMessages } from './modules/sidebar'
import { selfinfoMessages } from './modules/selfinfo'
import { utilsMessages } from './modules/utils'
import { deviceMessages } from './modules/device'
import { userMessages } from './modules/user'
import { examitemMessages } from './modules/examitem'
import { aboutMessages } from './modules/about'
import { serverMessages } from './modules/status/server500'
import { forbiddenMessages } from './modules/status/forbidden403'
import { notfoundMessages } from './modules/status/notFound404'
import { userInstructionMessages } from './modules/userInstruction'
import { adminInstructionMessages } from './modules/adminInstruction'
import { roleMessages } from './modules/role'

const Messages = {
  zh: {
    navbar: navbarMessages.zh,
    home: homeMessages.zh,
    login: loginMessages.zh,
    register: registerMessages.zh,
    sidebar:sidebarMessages.zh,
    selfinfo: selfinfoMessages.zh,
    device: deviceMessages.zh,
    user: userMessages.zh,
    examitem: examitemMessages.zh,
    utils: utilsMessages.zh,
    about: aboutMessages.zh,
    server: serverMessages.zh,
    forbidden: forbiddenMessages.zh,
    notfound: notfoundMessages.zh,
    userInstruction: userInstructionMessages.zh,
    adminInstruction: adminInstructionMessages.zh,
    role: roleMessages.zh
  },
  en: {
    navbar: navbarMessages.en,
    home: homeMessages.en,
    login: loginMessages.en,
    register: registerMessages.en,
    sidebar: sidebarMessages.en,
    selfinfo: selfinfoMessages.en,
    device: deviceMessages.en,
    user: userMessages.en,
    examitem: examitemMessages.en,
    utils: utilsMessages.en,
    about: aboutMessages.en,
    server: serverMessages.en,
    forbidden: forbiddenMessages.en,
    notfound: notfoundMessages.en,
    userInstruction: userInstructionMessages.en,
    adminInstruction: adminInstructionMessages.en,
    role:roleMessages.en
  }
}

export const i18n = createI18n({
  legacy: false,
  locale: 'zh',
  fallbackLocale: 'en',
  messages: Messages
})
