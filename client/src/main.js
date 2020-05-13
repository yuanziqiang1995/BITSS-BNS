/* eslint-disable no-unused-vars */
import Vue from 'vue'
import 'normalize.css/normalize.css' // A modern alternative to CSS resets

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import locale from 'element-ui/lib/locale/lang/en' // lang i18n

import '@/styles/index.scss' // global css
import go from 'gojs'

import VueContextMenu from 'vue-contextmenu'

Vue.config.productionTip = false
import echarts from 'echarts'

Vue.prototype.$echarts = echarts
Vue.prototype.$axios = axios
Vue.prototype.$go = go
import App from './App'
import store from './store'
import router from './router'
import axios from 'axios'

import '@/icons' // icon
import '@/permission' // permission control

import $ from 'jquery'

import drawnetwork from './components/designer/index.js'
Vue.use(drawnetwork)
Vue.prototype.$request = {
  get(url) { return axios.get('/myapi/' + url) },
  post(url, data) { 
    let param = new FormData()
  for (let i in data) {
    param.append(i, data[i])
  }
    return axios.post('/myapi/' + url, param) 
  },
  postJSON(url, data) { 
    return axios.post('/myapi/' + url, data) 
  },
  upload(url, data = {}, config = {}) {
    let param = new FormData()
    for (let i in data) {
      param.append(i, data[i])
    }
    return axios.post('/myapi/'+url,param)
  }
}

/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online! ! !
 */
import { mockXHR } from '../mock'
if (process.env.NODE_ENV === 'production') {
  // mockXHR()
}
mockXHR()
// set ElementUI lang to EN
Vue.use(ElementUI, { locale })
Vue.use(VueContextMenu)
Vue.config.productionTip = false

new Vue({
  el: '#app',
  router,
  store,
  render: h => h(App)
})
