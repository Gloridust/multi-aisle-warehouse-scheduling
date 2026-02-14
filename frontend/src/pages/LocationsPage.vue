<script setup>
import { onMounted, ref } from 'vue'
import api from '../api'
import WarehouseGrid from '../components/WarehouseGrid.vue'

const warehouseId = ref(localStorage.getItem('warehouseId') || '')
const rowFilter = ref('')
const sideFilter = ref('0')
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
  const params = {
    ...(rowFilter.value ? { row: rowFilter.value } : {}),
    side: sideFilter.value
  }
  const { data } = await api.get(`/api/warehouse/${warehouseId.value}/locations`, { params })
  locations.value = data
}

const updateStatus = async (row, status) => {
  await api.put(`/api/location/${row.id}/status`, null, { params: { status } })
  await loadLocations()
}

const sideLabel = (value) => (value === 1 ? '右侧' : '左侧')

const statusLabel = (value) => {
  if (value === 0) return '空置'
  if (value === 1) return '占用'
  if (value === 2) return '锁定'
  if (value === 3) return '待入库'
  if (value === 4) return '待出库'
  return '-'
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
      <el-form-item label="侧面">
        <el-select v-model="sideFilter" style="width: 120px">
          <el-option
            v-for="s in warehouse?.totalSides || 2"
            :key="s - 1"
            :label="s - 1"
            :value="String(s - 1)"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadLocations">加载</el-button>
      </el-form-item>
    </el-form>
    <el-table :data="locations" style="width: 100%; margin-top: 16px">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="rowNum" label="层" width="90">
        <template #default="{ row }">{{ row.rowNum }} 层</template>
      </el-table-column>
      <el-table-column prop="colNum" label="列" width="90">
        <template #default="{ row }">{{ row.colNum }} 列</template>
      </el-table-column>
      <el-table-column prop="sideNum" label="侧" width="90">
        <template #default="{ row }">{{ sideLabel(row.sideNum) }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">{{ statusLabel(row.status) }}</template>
      </el-table-column>
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
    <WarehouseGrid
      :rows="warehouse.totalRows"
      :cols="warehouse.totalCols"
      :cells="locations"
      :side="Number(sideFilter)"
    />
  </div>
</template>
