<template>
  <div class="search-page">
    <!-- 顶部导航 -->
    <div class="header-top">
      <div class="header-content">
        <div class="left-links">
          <span class="region-link">中国大陆 ▾</span>
        </div>
        <div class="right-links">
          <span class="action-link" @click="$router.push('/')">首页</span>
          <span class="divider">|</span>
          <span class="action-link" @click="$router.push('/personal')">个人中心</span>
        </div>
      </div>
    </div>

    <div class="header-search">
      <div class="search-content">
        <div class="logo" @click="$router.push('/')">
          <span class="logo-icon">潮</span>
          <span class="logo-text">潮选优品</span>
        </div>
        <div class="search-center">
          <div class="search-box">
            <input type="text" v-model="searchText" class="search-input" placeholder="搜索商品" @keyup.enter="doSearch" />
            <button class="search-btn" @click="doSearch">搜索</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索结果 -->
    <div class="result-container">
      <div class="result-header" v-if="!loading">
        <span class="result-tip">搜索 "<strong>{{ keyword }}</strong>" 找到 <strong>{{ products.length }}</strong> 个商品</span>
      </div>

      <div class="product-grid" v-loading="loading">
        <div v-for="product in products" :key="product.id" class="product-card" @click="$router.push({ path: '/product', query: { id: product.id } })">
          <div class="product-image">
            <div class="product-tags">
              <span v-if="product.price < 100" class="tag-free">包邮</span>
              <span v-if="product.sales > 500" class="tag-hot">热销</span>
            </div>
            <img v-if="product.mainImage" :src="product.mainImage" :alt="product.name" class="product-image-real" />
            <div v-else class="image-placeholder">商品图片</div>
          </div>
          <div class="product-info">
            <h3 class="product-name">{{ product.name }}</h3>
            <div class="product-bottom">
              <p class="product-price">¥{{ product.price }}</p>
              <p class="product-sales">{{ product.sales > 1000 ? (product.sales / 1000).toFixed(1) + 'k+' : product.sales }}人付款</p>
            </div>
          </div>
        </div>
      </div>

      <div v-if="!loading && products.length === 0" class="empty-state">
        <p>未找到相关商品，换个关键词试试？</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { searchProducts } from '../api/product'

const route = useRoute()
const router = useRouter()
const searchText = ref('')
const keyword = ref('')
const products = ref([])
const loading = ref(false)

const doSearch = () => {
  if (searchText.value.trim()) {
    router.push({ path: '/search', query: { q: searchText.value.trim() } })
  }
}

const loadData = async (q) => {
  if (!q) return
  loading.value = true
  keyword.value = q
  searchText.value = q
  try {
    const result = await searchProducts(q)
    if (result.code === 1) products.value = result.data || []
  } catch (e) {}
  loading.value = false
}

onMounted(() => loadData(route.query.q))

watch(() => route.query.q, (q) => { if (q) loadData(q) })
</script>

<style scoped>
.search-page { min-height: 100vh; background: #f5f5f5; }

.header-top { background: #fff; border-bottom: 1px solid #eee; height: 32px; line-height: 32px; }
.header-content { max-width: 1200px; margin: 0 auto; padding: 0 24px; display: flex; justify-content: space-between; }
.left-links, .right-links { display: flex; align-items: center; gap: 10px; }
.region-link, .action-link { font-size: 12px; color: #666; cursor: pointer; }
.action-link:hover { color: #E53935; }
.divider { color: #ddd; font-size: 12px; }

.header-search { background: #fff; padding: 16px 0; border-bottom: 1px solid #eee; }
.search-content { max-width: 1200px; margin: 0 auto; padding: 0 24px; display: flex; align-items: center; gap: 40px; }
.logo { display: flex; align-items: center; gap: 8px; cursor: pointer; flex-shrink: 0; }
.logo-icon { background: #ff6b00; color: white; width: 40px; height: 40px; display: flex; align-items: center; justify-content: center; font-size: 20px; font-weight: 700; border-radius: 8px; }
.logo-text { font-size: 22px; color: #ff6b00; font-weight: 700; }
.search-center { flex: 1; }
.search-box { display: flex; border: 2px solid #E53935; border-radius: 8px; overflow: hidden; height: 44px; }
.search-input { flex: 1; padding: 0 16px; border: none; outline: none; font-size: 15px; }
.search-btn { padding: 0 32px; background: #E53935; color: white; border: none; cursor: pointer; font-size: 15px; font-weight: 500; }
.search-btn:hover { background: #C62828; }

.result-container { max-width: 1200px; margin: 20px auto; padding: 0 24px; }
.result-header { margin-bottom: 16px; }
.result-tip { font-size: 14px; color: #666; }
.result-tip strong { color: #E53935; }

.product-grid { display: grid; grid-template-columns: repeat(5, 1fr); gap: 16px; }
@media (max-width: 1024px) { .product-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 768px) { .product-grid { grid-template-columns: repeat(2, 1fr); } }

.product-card { background: #fff; border-radius: 12px; overflow: hidden; cursor: pointer; transition: all 0.3s ease; border: 1px solid #eee; }
.product-card:hover { transform: translateY(-4px); box-shadow: 0 8px 24px rgba(0,0,0,0.1); }
.product-image { width: 100%; aspect-ratio: 1; background: #f9f9f9; overflow: hidden; position: relative; }
.product-image-real { width: 100%; height: 100%; object-fit: cover; }
.product-tags { position: absolute; top: 8px; left: 8px; display: flex; gap: 4px; z-index: 2; }
.product-tags span { padding: 2px 6px; border-radius: 4px; font-size: 10px; font-weight: 600; color: white; }
.tag-free { background: #E53935; }
.tag-hot { background: linear-gradient(135deg, #FF7D00, #FF4D4F); }
.product-info { padding: 12px; }
.product-name { font-size: 14px; color: #333; margin: 0 0 8px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-bottom { display: flex; align-items: baseline; justify-content: space-between; }
.product-price { font-size: 18px; color: #E53935; font-weight: 700; margin: 0; }
.product-sales { font-size: 12px; color: #999; margin: 0; }
.image-placeholder { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; color: #ccc; font-size: 14px; }

.empty-state { text-align: center; padding: 80px 0; color: #999; font-size: 16px; }
</style>
