<template>
  <div class="product-manage">
    <el-card>
      <div class="toolbar">
        <el-input v-model="searchName" placeholder="商品名称" clearable style="width: 200px" @clear="loadData" @keyup.enter="loadData" />
        <el-select v-model="searchStatus" placeholder="商品状态" clearable style="width: 140px; margin-left: 12px" @change="loadData">
          <el-option label="上架" :value="1" />
          <el-option label="下架" :value="0" />
        </el-select>
        <el-button type="primary" style="margin-left: 12px" @click="loadData">搜索</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column label="商品图片" width="90">
          <template #default="{ row }">
            <el-image :src="row.mainImage" style="width: 60px; height: 60px" fit="cover" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" link @click="toggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button size="small" type="primary" link @click="openStockDialog(row)">修改库存</el-button>
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

    <el-dialog v-model="stockDialogVisible" title="修改库存" width="400px">
      <el-form label-width="60px">
        <el-form-item label="库存">
          <el-input-number v-model="editStock" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="stockDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStock">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getProductList, updateProductStatus, updateProductStock } from '@/api/adminProduct'

const loading = ref(false)
const tableData = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const searchName = ref('')
const searchStatus = ref(null)

const stockDialogVisible = ref(false)
const editProductId = ref(null)
const editStock = ref(0)

async function loadData() {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (searchName.value) params.name = searchName.value
    if (searchStatus.value !== null && searchStatus.value !== '') params.status = searchStatus.value
    const res = await getProductList(params)
    if (res.code === 1) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  await updateProductStatus({ productId: row.id, status: newStatus })
  ElMessage.success(newStatus === 1 ? '已上架' : '已下架')
  loadData()
}

function openStockDialog(row) {
  editProductId.value = row.id
  editStock.value = row.stock
  stockDialogVisible.value = true
}

async function submitStock() {
  await updateProductStock({ productId: editProductId.value, stock: editStock.value })
  ElMessage.success('库存已更新')
  stockDialogVisible.value = false
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
</style>
