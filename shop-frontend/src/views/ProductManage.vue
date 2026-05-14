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
        <el-button type="success" style="margin-left: auto" @click="openDialog(null)">新增商品</el-button>
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
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button size="small" :type="row.status === 1 ? 'warning' : 'success'" link @click="toggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button size="small" type="info" link @click="openStockDialog(row)">库存</el-button>
            <el-popconfirm title="确定删除该商品吗？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button size="small" type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入商品描述" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="图片链接" prop="mainImage">
          <el-input v-model="form.mainImage" placeholder="请输入商品图片URL" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 修改库存弹窗 -->
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
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getProductList, getProductDetail, addProduct, updateProduct, deleteProduct, updateProductStatus, updateProductStock } from '@/api/adminProduct'
import request from '@/utils/request'

const loading = ref(false)
const tableData = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const searchName = ref('')
const searchStatus = ref(null)

// 新增/编辑
const dialogVisible = ref(false)
const dialogTitle = computed(() => form.value.id ? '编辑商品' : '新增商品')
const formRef = ref(null)
const form = ref({ id: null, name: '', description: '', price: 0, stock: 0, categoryId: null, mainImage: '' })
const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
}
const categories = ref([])

// 库存
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

async function loadCategories() {
  const res = await request.get('/category/list')
  if (res.code === 1) categories.value = res.data
}

function openDialog(row) {
  if (row) {
    getProductDetail(row.id).then(res => {
      if (res.code === 1) {
        form.value = {
          id: res.data.id,
          name: res.data.name,
          description: res.data.description || '',
          price: res.data.price,
          stock: res.data.stock,
          categoryId: res.data.categoryId,
          mainImage: res.data.mainImage || ''
        }
      }
    })
  } else {
    form.value = { id: null, name: '', description: '', price: 0, stock: 0, categoryId: null, mainImage: '' }
  }
  dialogVisible.value = true
}

function resetForm() {
  formRef.value?.resetFields()
}

async function submitForm() {
  await formRef.value.validate()
  if (form.value.id) {
    await updateProduct(form.value)
    ElMessage.success('修改成功')
  } else {
    await addProduct(form.value)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
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

async function handleDelete(productId) {
  await deleteProduct(productId)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadData()
  loadCategories()
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
