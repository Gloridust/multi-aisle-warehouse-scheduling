<script setup>
import { onMounted, ref, watch } from 'vue'
import * as echarts from 'echarts'
import api from '../api'
import WarehouseGrid from '../components/WarehouseGrid.vue'

const orderId = ref('')
const warehouseId = ref(localStorage.getItem('warehouseId') || '')
const warehouse = ref(null)
const results = ref([])
const orders = ref([])
const warehouses = ref([])
const loading = ref(false)
const chartRef = ref(null)
let chartInstance = null

const loadWarehouses = async () => {
  const { data } = await api.get('/api/warehouse')
  warehouses.value = data
  if (!warehouseId.value && data.length) {
    warehouseId.value = String(data[0].id)
  }
}

const loadOrders = async () => {
  const { data } = await api.get('/api/inbound-order')
  orders.value = data
}

const loadWarehouse = async () => {
  if (!warehouseId.value) return
  const { data } = await api.get(`/api/warehouse/${warehouseId.value}`)
  warehouse.value = data
  localStorage.setItem('warehouseId', warehouseId.value)
}

const runSimulation = async () => {
  if (!orderId.value || !warehouseId.value) return
  await loadWarehouse()
  loading.value = true
  const { data } = await api.post(`/api/simulation/${orderId.value}/run`, null, {
    params: { warehouseId: warehouseId.value }
  })
  results.value = data.results || []
  loading.value = false
  renderChart()
}

const renderChart = () => {
  if (!chartRef.value) return
  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value)
  }
  const labels = results.value.map((r) => r.strategyType)
  const totalDistance = results.value.map((r) => r.totalDistance || 0)
  const spaceUtilization = results.value.map((r) => r.spaceUtilization || 0)
  const computeTime = results.value.map((r) => r.computeTime || 0)
  chartInstance.setOption({
    tooltip: {},
    legend: { data: ['总存取距离(s)', '空间利用率(%)', '算法耗时(ms)'] },
    xAxis: { data: labels },
    yAxis: {},
    series: [
      { name: '总存取距离(s)', type: 'bar', data: totalDistance },
      { name: '空间利用率(%)', type: 'bar', data: spaceUtilization },
      { name: '算法耗时(ms)', type: 'bar', data: computeTime }
    ]
  })
}

watch(results, () => {
  renderChart()
})

onMounted(() => {
  if (chartRef.value) {
    chartInstance = echarts.init(chartRef.value)
  }
  loadWarehouses()
  loadOrders()
})
</script>

<template>
  <div class="panel">
    <div class="section-title">策略仿真</div>
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
      <el-form-item label="订单ID">
        <el-select v-model="orderId" filterable style="width: 220px">
          <el-option
            v-for="item in orders"
            :key="item.id"
            :label="`${item.orderNo || item.id}`"
            :value="String(item.id)"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="runSimulation">运行仿真</el-button>
      </el-form-item>
    </el-form>
  </div>

  <div v-if="warehouse && results.length" class="panel" style="margin-top: 20px">
    <div class="section-title">分配可视化</div>
    <el-row :gutter="20">
      <el-col :span="8" v-for="result in results" :key="result.strategyType">
        <div class="section-title">{{ result.strategyType }}</div>
        <WarehouseGrid :rows="warehouse.totalRows" :cols="warehouse.totalCols" :cells="result.allocations" />
      </el-col>
    </el-row>
  </div>

  <div v-if="results.length" class="panel" style="margin-top: 20px">
    <div class="section-title">指标对比</div>
    <div ref="chartRef" style="width: 100%; height: 320px"></div>
    <el-table :data="results" style="width: 100%; margin-top: 16px">
      <el-table-column prop="strategyType" label="策略" />
      <el-table-column prop="totalDistance" label="总存取距离(s)" />
      <el-table-column prop="avgDistance" label="平均存取距离(s)" />
      <el-table-column prop="spaceUtilization" label="空间利用率(%)" />
      <el-table-column prop="usedLocations" label="使用货位数(个)" />
      <el-table-column prop="computeTime" label="算法耗时(ms)" />
    </el-table>
  </div>
</template>
