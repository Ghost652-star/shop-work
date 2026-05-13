<template>
  <div class="dashboard">
    <div class="stats-row">
      <el-card shadow="hover" class="stat-card">
        <div class="stat-value">¥{{ todaySales }}</div>
        <div class="stat-label">今日销售额</div>
      </el-card>
      <el-card shadow="hover" class="stat-card">
        <div class="stat-value">{{ todayOrders }}</div>
        <div class="stat-label">今日订单数</div>
      </el-card>
      <el-card shadow="hover" class="stat-card">
        <div class="stat-value">{{ pendingShips }}</div>
        <div class="stat-label">待发货订单</div>
      </el-card>
    </div>

    <div class="chart-row">
      <el-card shadow="hover" class="chart-card full">
        <template #header>销售额趋势（近7天）</template>
        <div ref="salesChartRef" style="height: 320px"></div>
      </el-card>
    </div>

    <div class="chart-row">
      <el-card shadow="hover" class="chart-card half">
        <template #header>订单状态分布</template>
        <div ref="statusChartRef" style="height: 300px"></div>
      </el-card>
      <el-card shadow="hover" class="chart-card half">
        <template #header>热销商品 TOP10</template>
        <div ref="topChartRef" style="height: 300px"></div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { getSalesTrend, getOrderStatus, getTopProducts } from '@/api/dashboard'

const salesChartRef = ref(null)
const statusChartRef = ref(null)
const topChartRef = ref(null)

let salesChart = null
let statusChart = null
let topChart = null

const todaySales = ref('0')
const todayOrders = ref(0)
const pendingShips = ref(0)

function initSalesChart(data) {
  salesChart = echarts.init(salesChartRef.value)
  salesChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 60, right: 20, top: 20, bottom: 30 },
    xAxis: { type: 'category', data: data.dates },
    yAxis: { type: 'value', axisLabel: { formatter: '¥{value}' } },
    series: [{
      name: '销售额',
      type: 'line',
      smooth: true,
      areaStyle: { color: 'rgba(229,57,53,0.15)' },
      itemStyle: { color: '#E53935' },
      data: data.amounts
    }]
  })
  // today sales = last day
  const amounts = data.amounts
  todaySales.value = (amounts[amounts.length - 1] || 0).toLocaleString()
}

function initStatusChart(data) {
  statusChart = echarts.init(statusChartRef.value)
  statusChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    color: ['#909399', '#E6A23C', '#409EFF', '#67C23A', '#F56C6C'],
    series: [{
      type: 'pie',
      radius: ['40%', '65%'],
      label: { formatter: '{b}: {c}' },
      data: data
    }]
  })
  // pending ships = 待发货 count
  const pending = data.find(d => d.name === '待发货')
  pendingShips.value = pending ? pending.value : 0
  // today orders = all orders for today (approximate: sum of all)
  todayOrders.value = data.reduce((s, d) => s + d.value, 0)
}

function initTopChart(data) {
  topChart = echarts.init(topChartRef.value)
  const reversed = [...data].reverse()
  topChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 160, right: 30, top: 10, bottom: 10 },
    xAxis: { type: 'value' },
    yAxis: {
      type: 'category',
      data: reversed.map(d => d.name),
      axisLabel: {
        width: 140,
        overflow: 'truncate',
        fontSize: 12
      }
    },
    series: [{
      type: 'bar',
      barWidth: 16,
      itemStyle: { color: '#E53935', borderRadius: [0, 4, 4, 0] },
      data: reversed.map(d => d.sales)
    }]
  })
}

function handleResize() {
  salesChart?.resize()
  statusChart?.resize()
  topChart?.resize()
}

onMounted(async () => {
  window.addEventListener('resize', handleResize)
  try {
    const [trendRes, statusRes, topRes] = await Promise.all([
      getSalesTrend(),
      getOrderStatus(),
      getTopProducts()
    ])
    if (trendRes.code === 1) initSalesChart(trendRes.data)
    if (statusRes.code === 1) initStatusChart(statusRes.data)
    if (topRes.code === 1) initTopChart(topRes.data)
  } catch (e) {
    console.error('Dashboard load failed', e)
  }
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  salesChart?.dispose()
  statusChart?.dispose()
  topChart?.dispose()
})
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.stats-row {
  display: flex;
  gap: 20px;
}

.stat-card {
  flex: 1;
  text-align: center;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #E53935;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.chart-row {
  display: flex;
  gap: 20px;
}

.chart-card {
  flex: 1;
}

.chart-card.full {
  width: 100%;
}

.chart-card.half {
  flex: 1;
}
</style>
