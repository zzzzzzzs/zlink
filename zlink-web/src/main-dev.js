import Vue from 'vue'
import App from './App.vue'
import router from './router'
import './plugins/element.js'
// 导入字体图标
import './assets/fonts/iconfont.css'
// 导入全局样式表
import './assets/css/global.css'
import TreeTable from 'vue-table-with-tree-grid'

// 导入富文本编辑器
import VueQuillEditor from 'vue-quill-editor'
// require styles 导入富文本编辑器对应的样式
import 'quill/dist/quill.core.css'
import 'quill/dist/quill.snow.css'
import 'quill/dist/quill.bubble.css'

// 导入 NProgress 包对应的JS和CSS
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

import axios from 'axios'
// 配置请求的根路径
axios.defaults.baseURL = 'http://docker-node4:5465/api/'
// TODO 每次发送请求都会通过拦截器设置 token 令牌
//    请求到达服务器之前先调用回调函数
// 在 request 拦截器中，展示进度条 NProgress.start()
axios.interceptors.request.use(config => {
  // console.log(config)
  NProgress.start()
  // config.headers.Authorization = window.sessionStorage.getItem('token')
  // 在最后必须返回 config
  return config
})

// 在 response 拦截器中，隐藏进度条 NProgress.done()
axios.interceptors.response.use(config => {
  NProgress.done()
  return config
})


Vue.prototype.$http = axios

Vue.config.productionTip = false

// 全局注册
Vue.component('tree-table', TreeTable)
// 将富文本编辑器，注册为全局可用的组件
Vue.use(VueQuillEditor)

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
