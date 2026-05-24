<template>
  <div class="merchant-page">
    <!-- 顶部导航栏 -->
    <header class="header-bar">
      <div class="header-content">
        <div class="logo-area">
          <span class="logo-text">潮品优选</span>
        </div>
        <div class="search-area">
          <div class="search-box">
            <input
              type="text"
              v-model="searchKeyword"
              placeholder="搜索本店商品"
              @keyup.enter="handleSearch"
            />
            <button class="search-btn" @click="handleSearch">
              <span class="search-icon">🔍</span>
            </button>
          </div>
        </div>
      </div>
    </header>

    <!-- 店铺头部 -->
    <div class="merchant-header">
      <div class="merchant-header-content">
        <div class="merchant-info">
          <div class="merchant-logo">
            <img v-if="merchant.logo" :src="merchant.logo" alt="店铺Logo" />
            <span v-else class="logo-placeholder">{{ merchant.name?.charAt(0) }}</span>
          </div>
          <div class="merchant-meta">
            <h1 class="merchant-name">{{ merchant.name }}</h1>
            <div class="merchant-score">
              <span class="score-label">店铺评分</span>
              <span class="score-value">★ {{ merchant.score || '4.8' }}</span>
            </div>
            <p class="merchant-desc" v-if="merchant.description">{{ merchant.description }}</p>
          </div>
        </div>
        <div class="merchant-actions">
          <button class="action-btn customer-btn">
            <span class="btn-icon">💬</span>
            <span>客服</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-container">
      <!-- 左侧分类导航 -->
      <div class="sidebar">
        <div class="sidebar-title">全部宝贝</div>
        <ul class="category-list">
          <li
            v-for="(cat, index) in categories"
            :key="index"
            :class="{ active: activeCategory === index }"
            @click="activeCategory = index"
          >
            {{ cat }}
          </li>
        </ul>
      </div>

      <!-- 右侧商品区 -->
      <div class="product-area">
        <!-- 排序栏 -->
        <div class="sort-bar">
          <button
            :class="['sort-btn', { active: sortBy === 'composite' }]"
            @click="changeSort('composite')"
          >
            综合
          </button>
          <button
            :class="['sort-btn', { active: sortBy === 'sales' }]"
            @click="changeSort('sales')"
          >
            销量
          </button>
        </div>

        <!-- 商品列表 -->
        <div class="product-grid" v-loading="loading">
          <div
            v-for="product in products"
            :key="product.id"
            class="product-card"
            @click="goToProduct(product.id)"
          >
            <div class="product-image">
              <img :src="product.mainImage" :alt="product.name" />
            </div>
            <div class="product-info">
              <h3 class="product-title">{{ product.name }}</h3>
              <div class="product-price">
                <span class="price-symbol">¥</span>
                <span class="price-value">{{ product.price }}</span>
              </div>
              <div class="product-sales">{{ product.sales }}人付款</div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="!loading && products.length === 0" class="empty-state">
          暂无商品
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="total > pageSize">
          <button
            :disabled="currentPage <= 1"
            @click="changePage(currentPage - 1)"
          >
            上一页
          </button>
          <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
          <button
            :disabled="currentPage >= totalPages"
            @click="changePage(currentPage + 1)"
          >
            下一页
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getMerchantDetail, getMerchantProducts } from '@/api/merchant'

export default {
  name: 'MerchantDetail',
  data() {
    return {
      merchantId: null,
      merchant: {},
      products: [],
      loading: false,
      sortBy: 'composite',
      currentPage: 1,
      pageSize: 20,
      total: 0,
      searchKeyword: '',
      activeCategory: 0,
      categories: ['分类1', '分类2', '分类3', '分类4', '分类5']
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.total / this.pageSize)
    }
  },
  created() {
    this.merchantId = this.$route.query.id
    if (this.merchantId) {
      this.loadMerchantInfo()
      this.loadProducts()
    }
  },
  methods: {
    async loadMerchantInfo() {
      try {
        const res = await getMerchantDetail(this.merchantId)
        if (res.code === 1) {
          this.merchant = res.data
        }
      } catch (error) {
        console.error('加载商家信息失败', error)
      }
    },
    async loadProducts() {
      this.loading = true
      try {
        const res = await getMerchantProducts(this.merchantId, {
          sort: this.sortBy,
          pageNum: this.currentPage,
          pageSize: this.pageSize
        })
        if (res.code === 1) {
          this.products = res.data.records
          this.total = res.data.total
        }
      } catch (error) {
        console.error('加载商品列表失败', error)
      } finally {
        this.loading = false
      }
    },
    changeSort(sort) {
      this.sortBy = sort
      this.currentPage = 1
      this.loadProducts()
    },
    changePage(page) {
      this.currentPage = page
      this.loadProducts()
      window.scrollTo(0, 0)
    },
    goToProduct(id) {
      this.$router.push({ path: '/product', query: { id } })
    },
    handleSearch() {
      // 搜索功能暂不实现
      console.log('搜索:', this.searchKeyword)
    }
  }
}
</script>

<style scoped>
.merchant-page {
  min-height: 100vh;
  background: #f5f5f5;
}

/* 顶部导航栏 */
.header-bar {
  background: #fff;
  border-bottom: 1px solid #eee;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 12px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo-area {
  display: flex;
  align-items: center;
}

.logo-text {
  font-size: 24px;
  font-weight: bold;
  color: #E53935;
}

.search-area {
  flex: 1;
  max-width: 500px;
  margin-left: 40px;
}

.search-box {
  display: flex;
  border: 2px solid #E53935;
  border-radius: 24px;
  overflow: hidden;
}

.search-box input {
  flex: 1;
  border: none;
  padding: 10px 16px;
  font-size: 14px;
  outline: none;
}

.search-btn {
  background: #E53935;
  border: none;
  padding: 10px 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.search-btn:hover {
  background: #c62828;
}

.search-icon {
  font-size: 16px;
}

/* 店铺头部 */
.merchant-header {
  background: linear-gradient(135deg, #E53935 0%, #ff6f60 100%);
  padding: 30px 0;
}

.merchant-header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.merchant-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.merchant-logo {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.merchant-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.logo-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  font-weight: bold;
  color: #E53935;
}

.merchant-meta {
  color: #fff;
}

.merchant-name {
  font-size: 24px;
  font-weight: bold;
  margin: 0 0 8px 0;
}

.merchant-score {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.score-label {
  opacity: 0.9;
}

.score-value {
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 10px;
  border-radius: 12px;
}

.merchant-desc {
  margin: 8px 0 0 0;
  font-size: 13px;
  opacity: 0.85;
}

.merchant-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  border-radius: 20px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.customer-btn {
  background: #fff;
  color: #E53935;
}

.customer-btn:hover {
  background: #fff3f3;
}

.btn-icon {
  font-size: 16px;
}

/* 主体容器 */
.main-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
  display: flex;
  gap: 20px;
}

/* 左侧边栏 */
.sidebar {
  width: 200px;
  background: #fff;
  border-radius: 8px;
  padding: 16px 0;
  flex-shrink: 0;
}

.sidebar-title {
  font-size: 16px;
  font-weight: bold;
  color: #E53935;
  padding: 0 20px 12px;
  border-bottom: 1px solid #eee;
}

.category-list {
  list-style: none;
  margin: 0;
  padding: 8px 0;
}

.category-list li {
  padding: 10px 20px;
  cursor: pointer;
  font-size: 14px;
  color: #333;
  transition: all 0.2s;
}

.category-list li:hover {
  background: #fff5f5;
  color: #E53935;
}

.category-list li.active {
  background: #fff5f5;
  color: #E53935;
  font-weight: bold;
}

/* 右侧商品区 */
.product-area {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

/* 排序栏 */
.sort-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.sort-btn {
  padding: 8px 20px;
  border: 1px solid #ddd;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.sort-btn:hover {
  border-color: #E53935;
  color: #E53935;
}

.sort-btn.active {
  background: #E53935;
  border-color: #E53935;
  color: #fff;
}

/* 商品网格 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.product-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

.product-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.product-image {
  width: 100%;
  aspect-ratio: 1;
  background: #f5f5f5;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 12px;
}

.product-title {
  font-size: 13px;
  font-weight: normal;
  color: #333;
  margin: 0 0 8px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-price {
  color: #E53935;
  font-weight: bold;
}

.price-symbol {
  font-size: 12px;
}

.price-value {
  font-size: 18px;
}

.product-sales {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 14px;
}

/* 分页 */
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.pagination button {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.pagination button:hover:not(:disabled) {
  border-color: #E53935;
  color: #E53935;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #666;
}
</style>
