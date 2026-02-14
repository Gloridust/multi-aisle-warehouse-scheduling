<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import api from '../api'

const skus = ref([])
const dialogVisible = ref(false)
const editingId = ref(null)
const form = reactive({
  name: '',
  category: '',
  unitVolume: 0.1,
  unitWeight: 1
})

const categoryOptions = computed(() => {
  const options = new Set()
  skus.value.forEach((sku) => {
    if (sku.category) {
      options.add(sku.category)
    }
  })
  return Array.from(options)
})

const loadSkus = async () => {
  const { data } = await api.get('/api/sku')
  skus.value = data
}

const openCreate = () => {
  editingId.value = null
  Object.assign(form, { name: '', category: '', unitVolume: 0.1, unitWeight: 1 })
  dialogVisible.value = true
}

const openEdit = (row) => {
  editingId.value = row.id
  Object.assign(form, row)
  dialogVisible.value = true
}

const saveSku = async () => {
  if (editingId.value) {
    await api.put(`/api/sku/${editingId.value}`, form)
  } else {
    await api.post('/api/sku', form)
  }
  dialogVisible.value = false
  await loadSkus()
}

const deleteSku = async (row) => {
  await api.delete(`/api/sku/${row.id}`)
  await loadSkus()
}

onMounted(loadSkus)
</script>

<template>
  <div class="panel">
    <div class="section-title">商品管理</div>
    <el-button type="primary" @click="openCreate">新增商品</el-button>
    <el-table :data="skus" style="width: 100%; margin-top: 16px">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" min-width="160" />
      <el-table-column prop="category" label="分类" min-width="160" />
      <el-table-column prop="unitVolume" label="体积(m³)" min-width="120" />
      <el-table-column prop="unitWeight" label="重量(kg)" min-width="120" />
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteSku(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>

  <el-dialog v-model="dialogVisible" title="商品信息" width="400px">
    <el-form :model="form" label-width="80px">
      <el-form-item label="名称">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="form.category" filterable allow-create default-first-option clearable>
          <el-option v-for="option in categoryOptions" :key="option" :label="option" :value="option" />
        </el-select>
      </el-form-item>
      <el-form-item label="体积(m³)">
        <el-input-number v-model="form.unitVolume" :min="0.01" :step="0.01" />
      </el-form-item>
      <el-form-item label="重量(kg)">
        <el-input-number v-model="form.unitWeight" :min="0.01" :step="0.01" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="saveSku">保存</el-button>
    </template>
  </el-dialog>
</template>
