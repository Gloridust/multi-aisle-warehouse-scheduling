<script setup>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import api from '../api'

const route = useRoute()
const order = ref(null)
const items = ref([])
const results = ref([])
const loading = ref(false)

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
  loading.value = true
  const { data } = await api.post(`/api/outbound-order/${route.params.id}/execute`)
  results.value = data
  loading.value = false
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
      <el-descriptions-item label="状态">{{ order.status }}</el-descriptions-item>
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
    <el-button type="primary" :loading="loading" @click="execute">执行出库</el-button>
  </div>

  <div class="panel" style="margin-top: 20px">
    <div class="section-title">分配结果</div>
    <el-table :data="results" style="width: 100%">
      <el-table-column prop="locationId" label="货位ID" width="100" />
      <el-table-column prop="rowNum" label="层" width="80" />
      <el-table-column prop="colNum" label="列" width="80" />
      <el-table-column prop="skuName" label="SKU" />
      <el-table-column prop="pickedQty" label="数量(件)" />
      <el-table-column prop="pickedVolume" label="体积(m³)" />
      <el-table-column prop="accessDistance" label="存取距离(s)" />
    </el-table>
  </div>
</template>
