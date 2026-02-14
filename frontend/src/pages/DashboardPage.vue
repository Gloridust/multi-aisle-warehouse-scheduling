<script setup>
import { computed, onMounted, ref } from 'vue'
import api from '../api'
import WarehouseGrid from '../components/WarehouseGrid.vue'

const warehouseId = ref(localStorage.getItem('warehouseId') || '')
const warehouse = ref(null)
const locations = ref([])
const warehouses = ref([])

const loadDashboard = async () => {
  if (!warehouseId.value) return
  const { data: wh } = await api.get(`/api/warehouse/${warehouseId.value}`)
  warehouse.value = wh
  const { data } = await api.get(`/api/warehouse/${warehouseId.value}/visualization`)
  locations.value = data
  localStorage.setItem('warehouseId', warehouseId.value)
}

const loadWarehouses = async () => {
  const { data } = await api.get('/api/warehouse')
  warehouses.value = data
  if (!warehouseId.value && data.length) {
    warehouseId.value = String(data[0].id)
  }
}

onMounted(() => {
  loadWarehouses()
  loadDashboard()
})

const sideLocations = computed(() => {
  return {
    left: locations.value.filter((item) => (item.sideNum ?? 0) === 0),
    right: locations.value.filter((item) => (item.sideNum ?? 0) === 1)
  }
})
</script>

<template>
  <div class="panel">
    <div class="section-title">仓库看板</div>
    <el-form inline>
      <el-form-item label="仓库ID">
        <el-select v-model="warehouseId" filterable style="width: 200px">
          <el-option
            v-for="item in warehouses"
            :key="item.id"
            :label="`${item.id} - ${item.name || '未命名仓库'}`"
            :value="String(item.id)"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadDashboard">加载</el-button>
      </el-form-item>
    </el-form>
  </div>

  <div v-if="warehouse" class="panel" style="margin-top: 20px">
    <div class="section-title">状态说明</div>
    <el-tag type="primary" style="margin-right: 8px">空置</el-tag>
    <el-tag type="success" style="margin-right: 8px">占用</el-tag>
    <el-tag type="warning" style="margin-right: 8px">待入库</el-tag>
    <el-tag type="info" style="margin-right: 8px">待出库</el-tag>
    <el-tag type="danger">锁定</el-tag>
    <el-row :gutter="20" style="margin-top: 12px">
      <el-col :span="12">
        <div class="section-title">左侧</div>
        <WarehouseGrid :rows="warehouse.totalRows" :cols="warehouse.totalCols" :cells="sideLocations.left" :side="0" />
      </el-col>
      <el-col :span="12">
        <div class="section-title">右侧</div>
        <WarehouseGrid :rows="warehouse.totalRows" :cols="warehouse.totalCols" :cells="sideLocations.right" :side="1" />
      </el-col>
    </el-row>
  </div>
</template>
