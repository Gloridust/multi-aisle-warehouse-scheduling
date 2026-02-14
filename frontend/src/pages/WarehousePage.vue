<script setup>
import { onMounted, reactive, ref } from 'vue'
import api from '../api'

const warehouseId = ref(localStorage.getItem('warehouseId') || '')
const form = reactive({
  name: '',
  totalRows: 6,
  totalCols: 8,
  totalSides: 2,
  palletVolume: 1,
  horizontalSpeed: 1,
  verticalSpeed: 1,
  entryRow: 1,
  entryCol: 1,
  entrySide: 0
})

const loading = ref(false)

const loadWarehouse = async () => {
  if (!warehouseId.value) return
  loading.value = true
  const { data } = await api.get(`/api/warehouse/${warehouseId.value}`)
  Object.assign(form, {
    ...data,
    totalSides: data.totalSides || 2,
    entrySide: data.entrySide ?? 0
  })
  loading.value = false
}

const createWarehouse = async () => {
  loading.value = true
  const { data } = await api.post('/api/warehouse', form)
  warehouseId.value = data.id
  localStorage.setItem('warehouseId', data.id)
  loading.value = false
}

const updateWarehouse = async () => {
  if (!warehouseId.value) return
  loading.value = true
  await api.put(`/api/warehouse/${warehouseId.value}`, form)
  loading.value = false
}

onMounted(loadWarehouse)
</script>

<template>
  <div class="panel">
    <div class="section-title">仓库配置</div>
    <el-form :model="form" label-width="120px">
      <el-form-item label="仓库ID">
        <el-input v-model="warehouseId" placeholder="输入仓库ID后点击加载"></el-input>
      </el-form-item>
      <el-form-item label="仓库名称">
        <el-input v-model="form.name"></el-input>
      </el-form-item>
      <el-form-item label="总层数(层)">
        <el-input-number v-model="form.totalRows" :min="1"></el-input-number>
      </el-form-item>
      <el-form-item label="总列数(列)">
        <el-input-number v-model="form.totalCols" :min="1"></el-input-number>
      </el-form-item>
      <el-form-item label="侧面数量(左右)">
        <el-input-number v-model="form.totalSides" :min="2" :max="2"></el-input-number>
      </el-form-item>
      <el-form-item label="托盘容积(m³)">
        <el-input-number v-model="form.palletVolume" :min="0.01" :step="0.1"></el-input-number>
      </el-form-item>
      <el-form-item label="水平速度(s/列)">
        <el-input-number v-model="form.horizontalSpeed" :min="0.1" :step="0.1"></el-input-number>
      </el-form-item>
      <el-form-item label="垂直速度(s/层)">
        <el-input-number v-model="form.verticalSpeed" :min="0.1" :step="0.1"></el-input-number>
      </el-form-item>
      <el-form-item label="入库口层(层)">
        <el-input-number v-model="form.entryRow" :min="1"></el-input-number>
      </el-form-item>
      <el-form-item label="入库口列(列)">
        <el-input-number v-model="form.entryCol" :min="1"></el-input-number>
      </el-form-item>
      <el-form-item label="入库口侧(0/1)">
        <el-select v-model="form.entrySide" style="width: 160px">
          <el-option label="0" :value="0" />
          <el-option label="1" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="createWarehouse">创建仓库</el-button>
        <el-button type="success" :loading="loading" @click="updateWarehouse">更新仓库</el-button>
        <el-button @click="loadWarehouse">加载仓库</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>
