<script setup>
import { onMounted, ref } from 'vue'
import api from '../api'

const skuCount = ref(0)
const inboundCount = ref(0)
const outboundCount = ref(0)
const warehouseId = ref(localStorage.getItem('warehouseId') || '')
const warehouse = ref(null)

const loadStats = async () => {
  const [{ data: skus }, { data: inboundOrders }, { data: outboundOrders }] = await Promise.all([
    api.get('/api/sku'),
    api.get('/api/inbound-order'),
    api.get('/api/outbound-order')
  ])
  skuCount.value = skus.length || 0
  inboundCount.value = inboundOrders.length || 0
  outboundCount.value = outboundOrders.length || 0
  if (warehouseId.value) {
    const { data } = await api.get(`/api/warehouse/${warehouseId.value}`)
    warehouse.value = data
  }
}

onMounted(loadStats)
</script>

<template>
  <div class="panel">
    <div class="section-title">工作台</div>
    <el-row :gutter="16">
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-label">SKU 数量</div>
            <div class="stat-value">{{ skuCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-label">入库订单数</div>
            <div class="stat-value">{{ inboundCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-label">出库订单数</div>
            <div class="stat-value">{{ outboundCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-label">当前仓库</div>
            <div class="stat-value">{{ warehouse?.id || '-' }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card>
          <div class="stat-item">
            <div class="stat-label">货位总数</div>
            <div class="stat-value">
              {{ warehouse ? warehouse.totalRows * warehouse.totalCols * (warehouse.totalSides || 2) : '-' }}
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.stat-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-label {
  color: #909399;
  font-size: 13px;
}

.stat-value {
  font-size: 22px;
  font-weight: 600;
}
</style>
