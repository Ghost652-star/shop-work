<template>
  <div class="merchant-page">
    <!-- 购物车侧边栏 -->
    <CartSidebar ref="cartSidebar" />

    <!-- 顶部小字导航 -->
    <div class="header-top">
      <div class="header-top-content">
        <div class="left-links">
          <span class="region-link">中国大陆 ▾</span>
          <span class="divider">|</span>
          <template v-if="!isLoggedIn">
            <span class="login-link" @click="showLoginDialog = true">亲，请登录</span>
            <span class="register-link" @click="showLoginDialog = true; showRegister = true">免费注册</span>
          </template>
          <template v-else>
            <span class="welcome-text">你好，{{ userNickname }}</span>
            <span class="action-link" @click="handleLogout">退出</span>
          </template>
          <span class="divider">|</span>
          <span class="theme-link">选择主题 ▾</span>
        </div>
        <div class="right-links">
          <span class="action-link" @click="goHome">首页</span>
          <span class="divider">|</span>
          <span class="action-link" @click="$router.push('/personal')">个人中心</span>
          <span class="divider">|</span>
          <span class="action-link" @click="$router.push('/personal?tab=orders')">我的订单</span>
          <span class="divider">|</span>
          <span class="action-link" @click="$router.push('/customer-service')">联系客服</span>
        </div>
      </div>
    </div>

    <!-- 顶部导航栏 -->
    <header class="header-bar">
      <div class="header-content">
        <div class="logo-area">
          <span class="logo-text" @click="$router.push('/')" style="cursor:pointer">潮品优选</span>
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

    <!-- 登录对话框 -->
    <div v-if="showLoginDialog" class="login-dialog-overlay" @click.self="showLoginDialog = false">
      <div class="login-dialog">
        <div class="login-header">
          <h3>登录</h3>
          <span class="close-btn" @click="showLoginDialog = false">×</span>
        </div>
        <div class="login-tabs">
          <span :class="{ active: loginTab === 'password' }" @click="loginTab = 'password'">密码登录</span>
          <span :class="{ active: loginTab === 'sms' }" @click="loginTab = 'sms'">短信登录</span>
        </div>
        <div class="login-form">
          <div class="form-group">
            <label>用户名</label>
            <input v-model="username" type="text" placeholder="请输入用户名" />
          </div>
          <div class="form-group">
            <label>密码</label>
            <input v-model="password" type="password" placeholder="请输入密码" />
          </div>
          <button class="login-btn" @click="handleLogin">登录</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getMerchantDetail, getMerchantProducts } from '@/api/merchant'
import { login, logout } from '@/api/user'
import CartSidebar from '@/components/CartSidebar.vue'

export default {
  name: 'MerchantDetail',
  components: { CartSidebar },
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
      categories: ['分类1', '分类2', '分类3', '分类4', '分类5'],
      isLoggedIn: false,
      userNickname: '用户',
      showLoginDialog: false,
      showRegister: false,
      loginTab: 'password',
      username: '',
      password: ''
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.total / this.pageSize)
    }
  },
  created() {
    this.checkLoginStatus()
    this.merchantId = this.$route.params.id
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
    },
    checkLoginStatus() {
      const savedLoginUser = JSON.parse(localStorage.getItem('loginUser'))
      if (savedLoginUser && savedLoginUser.user) {
        this.isLoggedIn = true
        this.userNickname = savedLoginUser.user.nickname || '用户'
      }
    },
    getUserId() {
      const loginUser = JSON.parse(localStorage.getItem('loginUser'))
      return loginUser && loginUser.user ? loginUser.user.id : null
    },
    async handleLogin() {
      try {
        const result = await login({ username: this.username, password: this.password })
        if (result.code === 1) {
          const loginUser = result.data
          this.isLoggedIn = true
          this.userNickname = loginUser.user.nickname
          localStorage.setItem('loginUser', JSON.stringify(loginUser))
          localStorage.setItem('userId', loginUser.user.id)
          this.showLoginDialog = false
          this.$message.success('登录成功')
        } else {
          this.$message.error(result.msg || '登录失败')
        }
      } catch (error) {
        console.error('登录失败:', error)
        this.$message.error('登录失败，请稍后重试')
      }
    },
    async handleLogout() {
      try {
        const result = await logout()
        if (result.code === 1) {
          this.isLoggedIn = false
          this.userNickname = '用户'
          localStorage.removeItem('loginUser')
          localStorage.removeItem('userId')
          this.$message.success('退出登录成功')
        } else {
          this.$message.error(result.msg || '退出登录失败')
        }
      } catch (error) {
        console.error('退出登录失败:', error)
        this.$message.error('退出登录失败，请稍后重试')
      }
    },
    goHome() {
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.merchant-page {
  min-height: 100vh;
  background: #f5f5f5;
}

/* 顶部小字导航 */
.header-top {
  background: #f5f5f5;
  border-bottom: 1px solid #eee;
  font-size: 12px;
  color: #666;
}
.header-top-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 6px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.left-links, .right-links {
  display: flex;
  align-items: center;
  gap: 6px;
}
.login-link, .register-link, .action-link {
  color: #E53935;
  cursor: pointer;
}
.login-link:hover, .register-link:hover, .action-link:hover {
  text-decoration: underline;
}
.welcome-text { color: #333; }
.region-link, .theme-link { cursor: pointer; }
.divider { color: #ccc; }

/* 顶部导航栏 */
.header-bar {
  background: #f5f5f5;
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
  color: #ff6b00;
}

.search-area {
  flex: 1;
  max-width: 500px;
  margin-left: 40px;
}

.search-box {
  display: flex;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
  background: #fff;
}

.search-box input {
  flex: 1;
  border: none;
  padding: 10px 16px;
  font-size: 14px;
  outline: none;
}

.search-btn {
  background: #ff6b00;
  border: none;
  padding: 10px 24px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
}

.search-btn:hover {
  background: #e65c00;
}

.search-icon {
  font-size: 16px;
}

/* 店铺头部 */
.merchant-header {
  background: #fff;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
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
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
  border: 1px solid #eee;
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
  color: #ff6b00;
}

.merchant-meta {
  color: #333;
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
  color: #999;
}

.score-value {
  background: #fff0f0;
  color: #ff6b00;
  padding: 2px 10px;
  border-radius: 12px;
}

.merchant-desc {
  margin: 8px 0 0 0;
  font-size: 13px;
  color: #666;
}

.merchant-actions {
  display: flex;
  gap: 12px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 4px;
  border: 1px solid #ddd;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
  background: #fff;
  color: #333;
}

.customer-btn:hover {
  border-color: #ff6b00;
  color: #ff6b00;
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

/* 登录对话框 */
.login-dialog-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.login-dialog {
  background: #fff;
  border-radius: 8px;
  width: 380px;
  padding: 24px;
}
.login-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.login-header h3 { margin: 0; font-size: 18px; }
.close-btn { font-size: 24px; cursor: pointer; color: #999; }
.login-tabs {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}
.login-tabs span { cursor: pointer; color: #999; padding-bottom: 6px; }
.login-tabs span.active { color: #E53935; border-bottom: 2px solid #E53935; }
.form-group { margin-bottom: 14px; }
.form-group label { display: block; font-size: 13px; color: #666; margin-bottom: 4px; }
.form-group input {
  width: 100%; padding: 10px 12px; border: 1px solid #ddd; border-radius: 4px; font-size: 14px; box-sizing: border-box;
}
.login-btn {
  width: 100%; padding: 10px; background: #E53935; color: #fff; border: none; border-radius: 4px; font-size: 16px; cursor: pointer; margin-top: 8px;
}
.login-btn:hover { background: #c62828; }
</style>
