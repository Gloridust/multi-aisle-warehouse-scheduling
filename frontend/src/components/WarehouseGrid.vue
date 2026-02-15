<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  rows: { type: Number, required: true },
  cols: { type: Number, required: true },
  cells: { type: Array, required: true },
  side: { type: Number, default: null }
})

const gridStyle = computed(() => ({
  gridTemplateColumns: `repeat(${props.cols}, 22px)`
}))

const cellMap = computed(() => {
  const map = new Map()
  props.cells.forEach((cell) => {
    const sideKey = cell.sideNum === undefined || cell.sideNum === null ? 0 : cell.sideNum
    map.set(`${cell.rowNum}-${cell.colNum}-${sideKey}`, cell)
  })
  return map
})

const keyFor = (row, col) => {
  const sideKey = props.side === null || props.side === undefined ? 0 : props.side
  return `${row}-${col}-${sideKey}`
}

const colorIndex = (cell) => {
  if (!cell || !cell.skuId) {
    return null
  }
  return cell.skuId % 6
}

const statusClass = (cell) => {
  if (!cell || cell.status === undefined || cell.status === null) {
    return null
  }
  if (cell.status === 0) return 'status-empty'
  if (cell.status === 1) return 'status-occupied'
  if (cell.status === 2) return 'status-locked'
  if (cell.status === 3) return 'status-inbound'
  if (cell.status === 4) return 'status-outbound'
  return null
}

const imageDialogVisible = ref(false)
const imageUrl = ref('')
const imageTitle = ref('')

const openImage = (cell, row, col) => {
  if (!cell || !cell.skuImageBase64) {
    return
  }
  imageUrl.value = cell.skuImageBase64
  imageTitle.value = `货位 ${row}-${col}-${cell.sideNum ?? 0}`
  imageDialogVisible.value = true
}
</script>

<template>
  <div class="grid" :style="gridStyle">
    <template v-for="row in rows" :key="row">
      <template v-for="col in cols" :key="`${row}-${col}`">
        <el-tooltip placement="top" :disabled="!cellMap.get(keyFor(row, col))">
          <template #content>
            <div v-if="cellMap.get(keyFor(row, col))">
              <div>坐标: {{ row }}-{{ col }}-{{ cellMap.get(keyFor(row, col)).sideNum ?? 0 }}</div>
              <div>SKU: {{ cellMap.get(keyFor(row, col)).skuName || '-' }}</div>
              <div>数量(件): {{ cellMap.get(keyFor(row, col)).currentQty || cellMap.get(keyFor(row, col)).allocatedQty || 0 }}</div>
              <div>占用体积(m³): {{ cellMap.get(keyFor(row, col)).usedVolume || cellMap.get(keyFor(row, col)).allocatedVolume || 0 }}</div>
              <div>存取距离(s): {{ cellMap.get(keyFor(row, col)).accessDistance || 0 }}</div>
            </div>
          </template>
          <div
            class="grid-cell"
            :class="[
              !cellMap.get(keyFor(row, col)) || !cellMap.get(keyFor(row, col)).skuId ? 'empty' : '',
              statusClass(cellMap.get(keyFor(row, col))),
              cellMap.get(keyFor(row, col)) && cellMap.get(keyFor(row, col)).skuId ? `category-${colorIndex(cellMap.get(keyFor(row, col)))}` : ''
            ]"
            @click="openImage(cellMap.get(keyFor(row, col)), row, col)"
          ></div>
        </el-tooltip>
      </template>
    </template>
  </div>

  <el-dialog v-model="imageDialogVisible" :title="imageTitle" width="420px">
    <img v-if="imageUrl" :src="imageUrl" alt="货位图片" style="width: 100%; border-radius: 8px" />
  </el-dialog>
</template>
