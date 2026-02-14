import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from '../pages/LoginPage.vue'
import HomePage from '../pages/HomePage.vue'
import WarehousePage from '../pages/WarehousePage.vue'
import SkuPage from '../pages/SkuPage.vue'
import LocationsPage from '../pages/LocationsPage.vue'
import InboundListPage from '../pages/InboundListPage.vue'
import InboundDetailPage from '../pages/InboundDetailPage.vue'
import OutboundListPage from '../pages/OutboundListPage.vue'
import OutboundDetailPage from '../pages/OutboundDetailPage.vue'
import SimulationPage from '../pages/SimulationPage.vue'
import DashboardPage from '../pages/DashboardPage.vue'
import AccountPage from '../pages/AccountPage.vue'

const routes = [
  { path: '/', redirect: '/home' },
  { path: '/login', component: LoginPage },
  { path: '/home', component: HomePage },
  { path: '/warehouse', component: WarehousePage },
  { path: '/sku', component: SkuPage },
  { path: '/locations', component: LocationsPage },
  { path: '/inbound', component: InboundListPage },
  { path: '/inbound/:id', component: InboundDetailPage },
  { path: '/outbound', component: OutboundListPage },
  { path: '/outbound/:id', component: OutboundDetailPage },
  { path: '/simulation', component: SimulationPage },
  { path: '/dashboard', component: DashboardPage },
  { path: '/account', component: AccountPage }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.path === '/login') return true
  const token = sessionStorage.getItem('token')
  if (!token && localStorage.getItem('token')) {
    localStorage.removeItem('token')
    localStorage.removeItem('username')
  }
  if (!token) {
    return '/login'
  }
  return true
})

export default router
