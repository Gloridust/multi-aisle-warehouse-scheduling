<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api'

const profileLoading = ref(false)
const passwordLoading = ref(false)
const logsLoading = ref(false)

const profile = reactive({
  username: sessionStorage.getItem('username') || 'admin',
  nickname: '',
  email: '',
  phone: '',
  avatarBase64: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const logs = ref([])

const loadProfile = async () => {
  profileLoading.value = true
  try {
    const { data } = await api.get('/api/account/profile', {
      params: { username: profile.username }
    })
    Object.assign(profile, data)
  } finally {
    profileLoading.value = false
  }
}

const loadLogs = async () => {
  logsLoading.value = true
  try {
    const { data } = await api.get('/api/account/logs', {
      params: { username: profile.username }
    })
    logs.value = data
  } finally {
    logsLoading.value = false
  }
}

const saveProfile = async () => {
  profileLoading.value = true
  try {
    await api.post('/api/account/profile', {
      username: profile.username,
      nickname: profile.nickname,
      email: profile.email,
      phone: profile.phone,
      avatarBase64: profile.avatarBase64
    })
    ElMessage.success('资料已更新')
    await loadLogs()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '更新失败')
  } finally {
    profileLoading.value = false
  }
}

const savePassword = async () => {
  if (!passwordForm.oldPassword || !passwordForm.newPassword) {
    ElMessage.warning('请输入旧密码与新密码')
    return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.warning('两次输入的新密码不一致')
    return
  }
  passwordLoading.value = true
  try {
    await api.post('/api/auth/password', {
      username: profile.username,
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    ElMessage.success('密码已更新')
    await loadLogs()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '更新失败')
  } finally {
    passwordLoading.value = false
  }
}

const handleAvatarChange = (file) => {
  const raw = file.raw || file
  if (!raw || !raw.type || !raw.type.startsWith('image/')) {
    ElMessage.warning('请上传图片文件')
    return
  }
  const reader = new FileReader()
  reader.onload = () => {
    profile.avatarBase64 = reader.result
  }
  reader.readAsDataURL(raw)
}

onMounted(async () => {
  await loadProfile()
  await loadLogs()
})
</script>

<template>
  <div class="panel">
    <div class="section-title">账号设置</div>
    <el-form :model="profile" label-width="100px" style="max-width: 520px">
      <el-form-item label="账号">
        <el-input v-model="profile.username" disabled />
      </el-form-item>
      <el-form-item label="昵称">
        <el-input v-model="profile.nickname" />
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="profile.email" />
      </el-form-item>
      <el-form-item label="电话">
        <el-input v-model="profile.phone" />
      </el-form-item>
      <el-form-item label="头像">
        <div style="display: flex; align-items: center; gap: 12px">
          <el-avatar :size="64" :src="profile.avatarBase64" />
          <el-upload action="" :show-file-list="false" :auto-upload="false" :on-change="handleAvatarChange">
            <el-button>上传头像</el-button>
          </el-upload>
        </div>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="profileLoading" @click="saveProfile">保存资料</el-button>
      </el-form-item>
    </el-form>
  </div>

  <div class="panel" style="margin-top: 20px">
    <div class="section-title">修改密码</div>
    <el-form :model="passwordForm" label-width="100px" style="max-width: 420px">
      <el-form-item label="旧密码">
        <el-input v-model="passwordForm.oldPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="新密码">
        <el-input v-model="passwordForm.newPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="确认密码">
        <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="passwordLoading" @click="savePassword">保存密码</el-button>
      </el-form-item>
    </el-form>
  </div>

  <div class="panel" style="margin-top: 20px">
    <div class="section-title">操作日志</div>
    <el-table :data="logs" style="width: 100%" v-loading="logsLoading">
      <el-table-column prop="createdAt" label="时间" width="180" />
      <el-table-column prop="action" label="操作" width="120" />
      <el-table-column prop="detail" label="详情" />
    </el-table>
  </div>
</template>
