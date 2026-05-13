<template>
  <div class="after-sale-manage">
    <el-card>
      <div class="toolbar">
        <el-select v-model="searchStatus" placeholder="售后状态" clearable style="width: 140px" @change="loadData">
          <el-option label="待处理" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已驳回" :value="2" />
        </el-select>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单号" min-width="200" show-overflow-tooltip />
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="reason" label="原因" width="140" />
        <el-table-column label="退款金额" width="110">
          <template #default="{ row }">¥{{ row.refundAmount }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'warning' : row.status === 1 ? 'success' : 'danger'">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" size="small" type="primary" link @click="openHandle(row)">处理</el-button>
            <span v-else class="handled">已处理</span>
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

    <el-dialog v-model="handleVisible" title="处理售后" width="450px">
      <div class="handle-info" v-if="currentRow">
        <p><strong>订单号：</strong>{{ currentRow.orderNo }}</p>
        <p><strong>售后原因：</strong>{{ currentRow.reason }}</p>
        <p><strong>退款金额：</strong>¥{{ currentRow.refundAmount }}</p>
      </div>
      <el-form label-width="80px" style="margin-top: 16px">
        <el-form-item label="处理结果">
          <el-radio-group v-model="handleStatus">
            <el-radio :label="1">同意退款</el-radio>
            <el-radio :label="2">拒绝退款</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="adminRemark" type="textarea" :rows="3" placeholder="填写处理备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAfterSaleList, handleAfterSale } from '@/api/adminAfterSale'

const loading = ref(false)
const tableData = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const searchStatus = ref(null)

const handleVisible = ref(false)
const currentRow = ref(null)
const handleStatus = ref(1)
const adminRemark = ref('')

async function loadData() {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (searchStatus.value !== null && searchStatus.value !== '') params.status = searchStatus.value
    const res = await getAfterSaleList(params)
    if (res.code === 1) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

function openHandle(row) {
  currentRow.value = row
  handleStatus.value = 1
  adminRemark.value = ''
  handleVisible.value = true
}

async function submitHandle() {
  await handleAfterSale({
    afterSaleId: currentRow.value.id,
    status: handleStatus.value,
    adminRemark: adminRemark.value
  })
  ElMessage.success(handleStatus.value === 1 ? '已同意退款' : '已拒绝退款')
  handleVisible.value = false
  loadData()
}

onMounted(loadData)
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
.handle-info p {
  margin: 6px 0;
}
.handled {
  color: #909399;
  font-size: 13px;
}
</style>
