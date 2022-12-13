import Vue from 'vue'
import VueRouter from 'vue-router'
// import Login from '../components/Login'
// 懒加载的模式
const Login = () => import(/* webpackChunkName: "login_home_welcome" */ '../components/Login')
// import Home from '../components/Home'
const Home = () => import(/* webpackChunkName: "login_home_welcome" */ '../components/Home')
// import Welcome from '../components/Welcome'
const Welcome = () => import(/* webpackChunkName: "login_home_welcome" */ '../components/Welcome')
// import DataSource from "@/components/datasource/DataSource";
const DataSource = () => import(/* webpackChunkName: "Data_Source" */ '../components/datasource/DataSource')
const MetaData = () => import(/* webpackChunkName: "Meta_Data" */ '../components/metadata/MetaData')
const Cdc = () => import(/* webpackChunkName: "Cdc" */ '../components/cdc/Cdc')
const FlinkTask = () => import(/* webpackChunkName: "FlinkTask" */ '../components/flinktask/FlinkTask')


Vue.use(VueRouter)


const routes = [
  // 重定向，如果用户访问的是 / ，那么就重定向到 /login 上
  {path: '/', redirect: '/login'},
  // 当用户访问 /login 地址，通过 component 属性来展示 Login 组件
  {path: '/login', component: Login},
  {
    path: '/home',
    component: Home,
    // 只要访问了 /home 就会路由到 /welcome
    redirect: '/welcome',
    // children 是 Home 的子路由
    children: [
      {path: '/welcome', component: Welcome},
      {path: '/datasource', component: DataSource},
      {path: '/metadata', component: MetaData},
      {path: '/cdc', component: Cdc},
      {path: '/flinktask', component: FlinkTask},
    ]
  }
]

const router = new VueRouter({
  routes
})

// 挂载路由导航守卫
// TODO 如果用户没有登录，但是直接通过URL访问特定页面，需要重新导航到登录页面。
// 为路由对象添加 beforeEach 导航守卫
router.beforeEach((to, from, next) => {
  // to 将要访问的路径
  // from 代表从哪个路径跳转
  // next 是一个函数， 表示放行
  //   next() 放行  next('/login') 强制跳转
  if (to.path === '/login') return next()
  // TODO 获取 token
  // const tokenStr = window.sessionStorage.getItem('token')
  // if (!tokenStr) return next('/login')
  next()
})

export default router
