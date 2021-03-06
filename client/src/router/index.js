import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },

  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  },

  {
    path: '',
    component: Layout,
    redirect: 'dashboard',
    name: 'Dashboard',
    children: [{
      path: 'dashboard',
      component: () => import('@/views/dashboard/index'),
      name: '首页',
      meta: { title: '首页', icon: 'dashboard', noCache: true }
    }]

  },
  {
    path: '/draw',
    component: Layout,
    redirect: '/draw/new',
    name: 'draw',
    children: [
      {
        path: 'create',
        name: 'create',
        component: () => import('@/views/draw/create'),
        meta: { title: '新建网络', icon: 'form' }
      }
    ]
  },
  {
    path: '/draw',
    component: Layout,
    redirect: '/draw/new',
    name: 'draw',
    children: [
      {
        path: 'network',
        name: 'network',
        component: () => import('@/views/draw/network'),
        meta: { title: '我的网络', icon: 'table' }
      }
    ]
  },
  {
    path: '/draw',
    component: Layout,
    redirect: '/draw/new',
    name: 'draw',
    children: [
      {
        path: 'dataset',
        name: 'dataset',
        component: () => import('@/views/draw/dataset'),
        meta: { title: '数据管理', icon: 'table' }
      },

    ]
  },
  {
    path: '/draw',
    component: Layout,
    redirect: '/draw/new',
    name: 'draw',
    children: [
      {
        path: 'index',
        name: 'index',
        component: () => import('@/views/bayes/draw'),
        // component: () => import('@/views/bayes/bayes'),
        meta: { title: '画网络图', icon: 'table' },
        hidden: true
      },

    ]
  },
  {
    path: '/draw',
    component: Layout,
    redirect: '/draw/new',
    name: 'draw',
    children: [
      {
        path: 'useData',
        name: 'useData',
        // component: () => import('@/views/draw/index'),
        component: () => import('@/views/bayes/bayes'),
        meta: { title: '创建网络', icon: 'table' },
        hidden: true
      },

    ]
  },
  {
    path: '/draw',
    component: Layout,
    redirect: '/draw/new',
    name: 'draw',
    children: [
      {
        path: 'inference',
        name: 'inference',
        // component: () => import('@/views/draw/index'),
        component: () => import('@/views/bayes/inference'),
        meta: { title: '网络推理', icon: 'table' },
        hidden: true
      },

    ]
  },
  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
