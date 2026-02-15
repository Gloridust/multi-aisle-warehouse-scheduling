<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
import api from '../api'

const route = useRoute()
const order = ref(null)
const items = ref([])
const results = ref([])
const loading = ref(false)
const isExecuted = computed(() => order.value?.status === 2)

const statusLabel = (value) => {
  if (value === 0) return '待执行'
  if (value === 1) return '处理中'
  if (value === 2) return '已完成'
  return '-'
}

const loadDetail = async () => {
  const { data } = await api.get(`/api/outbound-order/${route.params.id}`)
  order.value = data.order
  items.value = data.items
}

const loadResults = async () => {
  const { data } = await api.get(`/api/outbound-order/${route.params.id}/result`)
  results.value = data
}

const execute = async () => {
  if (isExecuted.value) {
    ElMessage.warning('出库订单已执行')
    return
  }
  loading.value = true
  try {
    const { data } = await api.post(`/api/outbound-order/${route.params.id}/execute`)
    results.value = data
    await loadDetail()
    ElMessage.success('出库执行成功')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '出库执行失败')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadDetail()
  await loadResults()
})
</script>

<template>
  <div class="panel">
    <div class="section-title">订单信息</div>
    <el-descriptions :column="2" v-if="order">
      <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ statusLabel(order.status) }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ order.createTime }}</el-descriptions-item>
    </el-descriptions>
  </div>

  <div class="panel" style="margin-top: 20px">
    <div class="section-title">订单项</div>
    <el-table :data="items" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="skuId" label="SKU" />
      <el-table-column prop="quantity" label="数量(件)" />
    </el-table>
  </div>

  <div class="panel" style="margin-top: 20px">
    <div class="section-title">执行出库</div>
    <el-button type="primary" :loading="loading" :disabled="isExecuted" @click="execute">执行出库</el-button>
  </div>

  <div class="panel" style="margin-top: 20px">
    <div class="section-title">分配结果</div>
    <el-table :data="results" style="width: 100%">
      <el-table-column prop="locationId" label="货位ID" width="100" />
      <el-table-column prop="rowNum" label="层" width="80" />
      <el-table-column prop="colNum" label="列" width="80" />
      <el-table-column prop="sideNum" label="侧" width="80" />
      <el-table-column prop="skuName" label="SKU" />
      <el-table-column prop="pickedQty" label="数量(件)" />
      <el-table-column prop="pickedVolume" label="体积(m³)" />
      <el-table-column prop="accessDistance" label="访问距离(s)" />
    </el-table>
  </div>
</template>
