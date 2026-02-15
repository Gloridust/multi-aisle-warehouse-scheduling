<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
import api from '../api'

const route = useRoute()
const order = ref(null)
const items = ref([])
const allocations = ref([])
const strategyType = ref('CATEGORY')
const loading = ref(false)
const isExecuted = computed(() => order.value?.status === 2)

const statusLabel = (value) => {
  if (value === 0) return '待执行'
  if (value === 1) return '处理中'
  if (value === 2) return '已完成'
  return '-'
}

const loadDetail = async () => {
  const { data } = await api.get(`/api/inbound-order/${route.params.id}`)
  order.value = data.order
  items.value = data.items
}

const loadAllocations = async () => {
  const { data } = await api.get(`/api/inbound-order/${route.params.id}/allocation`)
  allocations.value = data
}

const execute = async () => {
  if (isExecuted.value) {
    ElMessage.warning('入库订单已执行')
    return
  }
  loading.value = true
  try {
    const { data } = await api.post(`/api/inbound-order/${route.params.id}/execute`, { strategyType: strategyType.value })
    allocations.value = data
    await loadDetail()
    ElMessage.success('入库执行成功')
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '入库执行失败')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await loadDetail()
  await loadAllocations()
})
</script>

<template>
  <div class="panel">
    <div class="section-title">订单信息</div>
    <el-descriptions :column="2" v-if="order">
      <el-descriptions-item label="订单号">{{ order.orderNo }}</el-descriptions-item>
      <el-descriptions-item label="状态">{{ statusLabel(order.status) }}</el-descriptions-item>
      <el-descriptions-item label="策略">{{ order.strategyType || '-' }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ order.createTime }}</el-descriptions-item>
    </el-descriptions>
  </div>

  <div class="panel" style="margin-top: 20px">
    <div class="section-title">订单项</div>
    <el-table :data="items" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="skuId" label="SKU" />
      <el-table-column prop="quantity" label="数量(件)" />
      <el-table-column label="图片" width="120">
        <template #default="{ row }">
          <img v-if="row.imageBase64" :src="row.imageBase64" alt="图片" class="item-image-preview" />
        </template>
      </el-table-column>
    </el-table>
  </div>

  <div class="panel" style="margin-top: 20px">
    <div class="section-title">执行入库</div>
    <el-form inline>
      <el-form-item label="策略">
        <el-select v-model="strategyType" style="width: 180px">
          <el-option label="分类存储" value="CATEGORY" />
          <el-option label="就近原则" value="NEAREST" />
          <el-option label="遗传算法" value="GENETIC" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" :disabled="isExecuted" @click="execute">执行入库</el-button>
      </el-form-item>
    </el-form>
  </div>

  <div class="panel" style="margin-top: 20px">
    <div class="section-title">分配结果</div>
    <el-table :data="allocations" style="width: 100%">
      <el-table-column prop="locationId" label="货位ID" width="100" />
      <el-table-column prop="rowNum" label="层" width="80" />
      <el-table-column prop="colNum" label="列" width="80" />
      <el-table-column prop="sideNum" label="侧" width="80" />
      <el-table-column prop="skuName" label="SKU" />
      <el-table-column prop="allocatedQty" label="数量(件)" />
      <el-table-column prop="allocatedVolume" label="体积(m³)" />
      <el-table-column prop="accessDistance" label="存取距离(s)" />
    </el-table>
  </div>
</template>

<style scoped>
.item-image-preview {
  width: 36px;
  height: 36px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
}
</style>
