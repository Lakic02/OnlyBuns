import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import eventBus from './eventBus';

const app = createApp(App)
app.config.globalProperties.$eventBus = eventBus;

app.use(router)

app.mount('#app')
