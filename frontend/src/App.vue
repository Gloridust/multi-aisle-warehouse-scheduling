<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const isLoginPage = computed(() => route.path === '/login')

const logout = () => {
  sessionStorage.removeItem('token')
  sessionStorage.removeItem('username')
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  router.push('/login')
}
</script>

<template>
  <router-view v-if="isLoginPage" />
  <el-container v-else class="layout">
    <el-aside width="220px">
      <div class="brand">多层货位调度系统</div>
      <el-menu router :default-active="$route.path">
        <el-menu-item index="/home">工作台</el-menu-item>
        <el-sub-menu index="base">
          <template #title>基础管理</template>
          <el-menu-item index="/warehouse">仓库管理</el-menu-item>
          <el-menu-item index="/locations">货位管理</el-menu-item>
          <el-menu-item index="/sku">货物信息</el-menu-item>
          <el-menu-item index="/account">账号设置</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="monitor">
          <template #title>可视化监控</template>
          <el-menu-item index="/dashboard">二维看板</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="task">
          <template #title>作业任务</template>
          <el-menu-item index="/inbound">入库订单</el-menu-item>
          <el-menu-item index="/outbound">出库订单</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="strategy">
          <template #title>算法策略</template>
          <el-menu-item index="/simulation">仿真对比</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-content">
          <div>高空立体仓库货位调度优化系统</div>
          <el-button type="danger" plain @click="logout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout {
  min-height: 100vh;
  width: 100%;
}

.brand {
  font-size: 16px;
  font-weight: 600;
  padding: 16px;
}

.header {
  font-size: 18px;
  font-weight: 600;
  padding: 12px 20px;
  height: auto;
  line-height: 1.4;
  white-space: normal;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.main {
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
  padding: 20px;
  overflow: auto;
}
</style>
