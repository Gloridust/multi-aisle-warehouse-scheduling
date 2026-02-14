<script setup>
import { computed, onMounted, ref } from 'vue'
import api from '../api'
import WarehouseGrid from '../components/WarehouseGrid.vue'

const warehouseId = ref(localStorage.getItem('warehouseId') || '')
const warehouse = ref(null)
const locations = ref([])
const floor = ref('')

const loadDashboard = async () => {
  if (!warehouseId.value) return
  const { data: wh } = await api.get(`/api/warehouse/${warehouseId.value}`)
  warehouse.value = wh
  const { data } = await api.get(`/api/warehouse/${warehouseId.value}/visualization`)
  locations.value = data
  localStorage.setItem('warehouseId', warehouseId.value)
}

onMounted(loadDashboard)

const filteredLocations = computed(() => {
  if (!floor.value) return locations.value
  return locations.value.filter((item) => item.rowNum === Number(floor.value))
})
</script>

<template>
  <div class="panel">
    <div class="section-title">仓库看板</div>
    <el-form inline>
      <el-form-item label="仓库ID">
        <el-input v-model="warehouseId" style="width: 160px" />
      </el-form-item>
      <el-form-item label="楼层">
        <el-select v-model="floor" clearable style="width: 120px">
          <el-option v-for="row in warehouse?.totalRows || 0" :key="row" :label="row" :value="row" />
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
    <div style="margin-top: 12px">
      <WarehouseGrid :rows="warehouse.totalRows" :cols="warehouse.totalCols" :cells="filteredLocations" />
    </div>
  </div>
</template>
