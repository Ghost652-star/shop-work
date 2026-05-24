<template>
  <div class="order-manage">
    <el-card>
      <div class="toolbar">
        <el-select v-model="searchStatus" placeholder="订单状态" clearable style="width: 140px" @change="loadData">
          <el-option label="待付款" :value="0" />
          <el-option label="待发货" :value="1" />
          <el-option label="待收货" :value="2" />
          <el-option label="已完成" :value="3" />
          <el-option label="已取消" :value="4" />
        </el-select>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单号" min-width="200" show-overflow-tooltip />
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column label="金额" width="120">
          <template #default="{ row }">¥{{ row.payAmount }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openDetail(row)">详情</el-button>
            <el-button v-if="row.status === 1" size="small" type="success" link @click="ship(row)">发货</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pagination"
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
      />
    </el-card>

    <el-dialog v-model="detailVisible" title="订单详情" width="600px">
      <el-descriptions :column="2" border v-if="detail">
        <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户">{{ detail.userName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusType(detail.status)">{{ detail.statusText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="金额">¥{{ detail.payAmount }}</el-descriptions-item>
        <el-descriptions-item label="收货人">{{ detail.receiverName }}</el-descriptions-item>
        <el-descriptions-item label="电话">{{ detail.receiverPhone }}</el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ detail.receiverAddress }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="detail?.items || []" style="margin-top: 16px" border size="small">
        <el-table-column prop="productName" label="商品" min-width="160" />
        <el-table-column prop="price" label="单价" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column label="小计" width="100">
          <template #default="{ row }">¥{{ row.totalPrice }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrderList, getOrderDetail, markShipped } from '@/api/adminOrder'
import { getMerchantInfo } from '@/api/merchant'

const loading = ref(false)
const tableData = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const searchStatus = ref(null)
const merchantId = ref(null)

const detailVisible = ref(false)
const detail = ref(null)

function statusType(status) {
  const map = { 0: 'info', 1: 'warning', 2: '', 3: 'success', 4: 'danger' }
  return map[status] || 'info'
}

async function loadData() {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (merchantId.value !== null) params.merchantId = merchantId.value
    if (searchStatus.value !== null && searchStatus.value !== '') params.status = searchStatus.value
    const res = await getOrderList(params)
    if (res.code === 1) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

async function openDetail(row) {
  const res = await getOrderDetail(row.id)
  if (res.code === 1) {
    detail.value = res.data
    detailVisible.value = true
  }
}

async function ship(row) {
  await ElMessageBox.confirm('确认标记该订单为已发货？', '确认发货')
  await markShipped(row.id)
  ElMessage.success('发货成功')
  loadData()
}

onMounted(async () => {
  try {
    const res = await getMerchantInfo()
    if (res.code === 1 && res.data) {
      merchantId.value = res.data.id
    }
  } catch (e) {
    // ignore
  }
  loadData()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
