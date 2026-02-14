<script setup>
import { onMounted, ref } from 'vue'
import api from '../api'
import WarehouseGrid from '../components/WarehouseGrid.vue'

const warehouseId = ref(localStorage.getItem('warehouseId') || '')
const rowFilter = ref('')
const locations = ref([])
const warehouse = ref(null)

const loadWarehouse = async () => {
  if (!warehouseId.value) return
  const { data } = await api.get(`/api/warehouse/${warehouseId.value}`)
  warehouse.value = data
  localStorage.setItem('warehouseId', warehouseId.value)
}

const loadLocations = async () => {
  if (!warehouseId.value) return
  await loadWarehouse()
  const params = rowFilter.value ? { row: rowFilter.value } : {}
  const { data } = await api.get(`/api/warehouse/${warehouseId.value}/locations`, { params })
  locations.value = data
}

const updateStatus = async (row, status) => {
  await api.put(`/api/location/${row.id}/status`, null, { params: { status } })
  await loadLocations()
}

onMounted(loadLocations)
</script>

<template>
  <div class="panel">
    <div class="section-title">货位管理</div>
    <el-form inline>
      <el-form-item label="仓库ID">
        <el-input v-model="warehouseId" style="width: 160px" />
      </el-form-item>
      <el-form-item label="层筛选">
        <el-input v-model="rowFilter" style="width: 120px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadLocations">加载</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="locations" style="width: 100%; margin-top: 16px">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="rowNum" label="层" width="80" />
      <el-table-column prop="colNum" label="列" width="80" />
      <el-table-column prop="status" label="状态" width="80" />
      <el-table-column prop="currentSkuId" label="SKU" />
      <el-table-column prop="currentQty" label="数量(件)" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="updateStatus(row, 2)">锁定</el-button>
          <el-button size="small" type="success" @click="updateStatus(row, 0)">解锁</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>

  <div class="panel" style="margin-top: 20px" v-if="warehouse">
    <div class="section-title">货位分布</div>
    <WarehouseGrid :rows="warehouse.totalRows" :cols="warehouse.totalCols" :cells="locations" />
  </div>
</template>
