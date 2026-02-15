<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import api from '../api'

const router = useRouter()
const orders = ref([])
const skus = ref([])
const dialogVisible = ref(false)
const form = reactive({
  orderNo: '',
  items: []
})

const buildOrderNo = (prefix) => {
  const now = new Date()
  const parts = new Intl.DateTimeFormat('sv-SE', {
    timeZone: 'Asia/Shanghai',
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  }).formatToParts(now)
  const map = Object.fromEntries(parts.map((part) => [part.type, part.value]))
  const time = `${map.year}${map.month}${map.day}${map.hour}${map.minute}${map.second}`
  const suffix = String(now.getMilliseconds()).padStart(3, '0')
  return `${prefix}${time}${suffix}`
}

const loadOrders = async () => {
  const { data } = await api.get('/api/inbound-order')
  orders.value = data
}

const loadSkus = async () => {
  const { data } = await api.get('/api/sku')
  skus.value = data
}

const openCreate = () => {
  form.orderNo = buildOrderNo('IN')
  form.items = []
  dialogVisible.value = true
}

const addItem = () => {
  form.items.push({ skuId: '', quantity: 1, imageBase64: '' })
}

const removeItem = (index) => {
  form.items.splice(index, 1)
}

const statusLabel = (value) => {
  if (value === 0) return '待执行'
  if (value === 1) return '处理中'
  if (value === 2) return '已完成'
  return '-'
}

const handleItemImageChange = (row, file) => {
  const raw = file.raw || file
  if (!raw || !raw.type || !raw.type.startsWith('image/')) {
    ElMessage.warning('请上传图片文件')
    return
  }
  const reader = new FileReader()
  reader.onload = () => {
    row.imageBase64 = reader.result
  }
  reader.readAsDataURL(raw)
}

const createOrder = async () => {
  if (!form.items.length) {
    ElMessage.warning('请添加商品')
    return
  }
  const invalid = form.items.some((item) => !item.skuId || !item.quantity || item.quantity <= 0)
  if (invalid) {
    ElMessage.warning('请完善商品与数量')
    return
  }
  try {
    await api.post('/api/inbound-order', form)
    ElMessage.success('入库订单已创建')
    dialogVisible.value = false
    await loadOrders()
  } catch (error) {
    ElMessage.error(error?.response?.data?.message || '创建入库订单失败')
  }
}

const goDetail = (row) => {
  router.push(`/inbound/${row.id}`)
}

onMounted(async () => {
  await loadOrders()
  await loadSkus()
})
</script>

<template>
  <div class="panel">
    <div class="section-title">入库订单</div>
    <el-button type="primary" @click="openCreate">新建订单</el-button>
    <el-table :data="orders" style="width: 100%; margin-top: 16px">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="orderNo" label="订单号" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">{{ statusLabel(row.status) }}</template>
      </el-table-column>
      <el-table-column prop="strategyType" label="策略" width="120" />
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="goDetail(row)">管理入库</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>

  <el-dialog v-model="dialogVisible" title="新建入库订单" width="600px">
    <el-form :model="form" label-width="80px">
      <el-form-item label="订单号">
        <el-input v-model="form.orderNo" />
      </el-form-item>
      <el-form-item label="订单项">
        <el-button size="small" @click="addItem">添加商品</el-button>
        <el-table :data="form.items" class="order-items-table" style="width: 100%; margin-top: 12px">
          <el-table-column label="商品">
            <template #default="{ row }">
              <el-select v-model="row.skuId" placeholder="选择SKU" style="width: 160px">
                <el-option v-for="sku in skus" :key="sku.id" :label="sku.name" :value="sku.id" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="数量(件)" width="140">
            <template #default="{ row }">
              <el-input-number v-model="row.quantity" :min="1" />
            </template>
          </el-table-column>
          <el-table-column label="图片" width="180">
            <template #default="{ row }">
              <div class="item-image">
                <el-upload action="" :show-file-list="false" :auto-upload="false" :on-change="(file) => handleItemImageChange(row, file)">
                  <el-button size="small">上传</el-button>
                </el-upload>
                <img v-if="row.imageBase64" :src="row.imageBase64" alt="图片" class="item-image-preview" />
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100">
            <template #default="{ $index }">
              <el-button size="small" type="danger" @click="removeItem($index)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="createOrder">提交</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.order-items-table {
  min-width: 0;
}

:deep(.order-items-table .el-table__body-wrapper) {
  overflow-x: auto;
}

:deep(.order-items-table .el-input-number) {
  width: 140px;
}

.item-image {
  display: flex;
  align-items: center;
  gap: 8px;
}

.item-image-preview {
  width: 32px;
  height: 32px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
}
</style>
