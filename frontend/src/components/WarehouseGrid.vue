<script setup>
import { computed } from 'vue'

const props = defineProps({
  rows: { type: Number, required: true },
  cols: { type: Number, required: true },
  cells: { type: Array, required: true }
})

const gridStyle = computed(() => ({
  gridTemplateColumns: `repeat(${props.cols}, 22px)`
}))

const cellMap = computed(() => {
  const map = new Map()
  props.cells.forEach((cell) => {
    map.set(`${cell.rowNum}-${cell.colNum}`, cell)
  })
  return map
})

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
</script>

<template>
  <div class="grid" :style="gridStyle">
    <template v-for="row in rows" :key="row">
      <template v-for="col in cols" :key="`${row}-${col}`">
        <el-tooltip placement="top" :disabled="!cellMap.get(`${row}-${col}`)">
          <template #content>
            <div v-if="cellMap.get(`${row}-${col}`)">
              <div>坐标: {{ row }}-{{ col }}</div>
              <div>SKU: {{ cellMap.get(`${row}-${col}`).skuName || '-' }}</div>
              <div>数量(件): {{ cellMap.get(`${row}-${col}`).currentQty || cellMap.get(`${row}-${col}`).allocatedQty || 0 }}</div>
              <div>占用体积(m³): {{ cellMap.get(`${row}-${col}`).usedVolume || cellMap.get(`${row}-${col}`).allocatedVolume || 0 }}</div>
              <div>存取距离(s): {{ cellMap.get(`${row}-${col}`).accessDistance || 0 }}</div>
            </div>
          </template>
          <div
            class="grid-cell"
            :class="[
              !cellMap.get(`${row}-${col}`) || !cellMap.get(`${row}-${col}`).skuId ? 'empty' : '',
              statusClass(cellMap.get(`${row}-${col}`)),
              cellMap.get(`${row}-${col}`) && cellMap.get(`${row}-${col}`).skuId ? `category-${colorIndex(cellMap.get(`${row}-${col}`))}` : ''
            ]"
          ></div>
        </el-tooltip>
      </template>
    </template>
  </div>
</template>
