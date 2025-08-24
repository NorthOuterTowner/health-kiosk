import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './style.css'
import i18n from './i18n'
import { create, NButton, NInput, NForm, NFormItem, NMessageProvider } from 'naive-ui';

const naive = create({
  components: [NButton, NInput, NForm, NFormItem, NMessageProvider]
});

const app = createApp(App)

app.use(naive)
app.use(router)
app.use(i18n)

app.mount('#app')
