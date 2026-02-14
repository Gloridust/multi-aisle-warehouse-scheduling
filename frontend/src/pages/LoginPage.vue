<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../api'

const router = useRouter()
const loading = ref(false)
const form = reactive({
  username: '',
  password: ''
})

const login = async () => {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入账号与密码')
    return
  }
  loading.value = true
  try {
    const { data } = await api.post('/api/auth/login', {
      username: form.username,
      password: form.password
    })
    sessionStorage.setItem('token', data.token || '')
    sessionStorage.setItem('username', data.username || form.username)
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    router.replace('/home')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '账号或密码错误')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-title">系统登录</div>
      <el-form :model="form" label-width="80px">
        <el-form-item label="账号">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="login">登录</el-button>
          <!-- <el-button type="text">忘记密码</el-button> -->
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
}

.login-card {
  width: 360px;
  padding: 28px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.06);
}

.login-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  text-align: center;
}
</style>
