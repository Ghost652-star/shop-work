<template>
  <div class="home-container">
    <CartSidebar ref="cartSidebar" />

    <!-- 顶部第一行 - 灰色用户操作栏 -->
    <div class="header-top" :class="{ hidden: isScrolled }">
      <div class="header-content">
        <div class="left-links">
          <span v-if="!isLoggedIn" class="welcome-text">你好，请登录</span>
          <span v-else class="welcome-text">你好，{{ userNickname }}</span>
          <span v-if="!isLoggedIn" class="register-btn" @click="showRegister = true; showLoginDialog = true">免费注册</span>
        </div>
        <div class="right-links">
          <span class="action-link" @click="handlePersonalCenter">个人中心</span>
          <span class="divider">|</span>
          <span class="action-link" @click="handleCart">购物车</span>
          <span class="divider">|</span>
          <span class="action-link" @click="handleMyOrders">我的订单</span>
          <span class="divider">|</span>
          <span class="action-link">收藏夹</span>
          <span class="divider">|</span>
          <span class="action-link">卖家中心</span>
          <span class="divider">|</span>
          <span class="action-link" @click="goToCustomerService">联系客服</span>
          <span class="divider">|</span>
          <span class="action-link">网站导航</span>
          <span class="divider">|</span>
          <span v-if="isLoggedIn" class="action-link" @click="handleLogout">退出</span>
        </div>
      </div>
    </div>

    <!-- 吸顶容器：搜索栏 + 分类导航 -->
    <div class="sticky-wrapper">
      <!-- 顶部第二行 - Logo + 搜索栏 + 热搜词 + 图标 -->
      <div class="header-search">
        <div class="search-content">
          <div class="logo" @click="goHome">
            <span class="logo-icon">潮</span>
            <span class="logo-text">潮选优品</span>
          </div>
          <div class="search-center">
            <div class="search-box">
              <input type="text" v-model="searchText" class="search-input" :placeholder="currentPlaceholderText" />
              <button class="search-btn" @click="handleSearch">搜索</button>
            </div>
            <div class="hot-search">
              <span v-for="(tag, idx) in hotSearchTags" :key="idx" class="hot-tag" @click="searchText = tag">{{ tag }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 顶部第三行 - 红色分类导航 -->
      <div class="category-nav">
        <div class="category-content">
          <span class="category-item all-categories" @click="selectCategory(null)">☰ 全部商品分类</span>
          <span class="category-item" :class="{ active: activeCategoryId === null }" @click="selectCategory(null)">首页</span>
          <span v-for="category in categories" :key="category.id" class="category-item" :class="{ active: activeCategoryId === category.id }" @click="selectCategory(category.id)">
            {{ category.name }}
          </span>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-content">
      <!-- 左侧:热销榜单 -->
      <div class="hot-sales">
        <div class="hot-sales-header">
          <h3>🔥 热销榜单</h3>
          <span class="hot-sales-subtitle">热销商品 TOP10</span>
        </div>
        <div class="hot-sales-list">
          <div v-for="(item, index) in hotSales" :key="index" class="hot-sales-item">
            <div class="item-rank" :class="{ 'top-3': index < 3 }">{{ item.rank }}</div>
            <div class="item-info">
              <span class="item-name">{{ item.name }}</span>
            </div>
            <span class="item-sales">{{ item.sales }}</span>
          </div>
        </div>
      </div>

      <!-- 中间:轮播图 -->
      <div class="banner">
        <div class="carousel-container">
          <div ref="carouselWrapper" class="carousel-wrapper" :style="{ transform: `translateX(-${currentIndex * 100}%)` }" @transitionend="handleTransitionEnd">
            <div v-if="banners.length > 0" class="carousel-item">
              <img :src="banners[banners.length - 1].image" :alt="banners[banners.length - 1].title" class="carousel-image" />
              <div class="carousel-caption">{{ banners[banners.length - 1].title }}</div>
            </div>
            <div v-for="(banner, index) in banners" :key="index" class="carousel-item">
              <img :src="banner.image" :alt="banner.title" class="carousel-image" />
              <div class="carousel-caption">{{ banner.title }}</div>
            </div>
            <div v-if="banners.length > 0" class="carousel-item">
              <img :src="banners[0].image" :alt="banners[0].title" class="carousel-image" />
              <div class="carousel-caption">{{ banners[0].title }}</div>
            </div>
          </div>
          <button class="carousel-btn prev-btn" @click="prevSlide">‹</button>
          <button class="carousel-btn next-btn" @click="nextSlide">›</button>
          <div class="carousel-indicators">
            <span v-for="(banner, index) in banners" :key="index" :class="{ indicator: true, active: index === currentIndex - 1 }" @click="goToSlide(index)"></span>
          </div>
        </div>
      </div>

      <!-- 右侧:促销卡片 -->
      <div class="promo-cards">
        <div class="promo-card promo-orange" @click="selectCategory(null)">
          <div class="promo-text">
            <h4>品质家居</h4>
            <p>超值优惠</p>
          </div>
          <div class="promo-icon">🏠</div>
        </div>
        <div class="promo-card promo-pink" @click="selectCategory(null)">
          <div class="promo-text">
            <h4>精致美妆</h4>
            <p>品质之选</p>
          </div>
          <div class="promo-icon">💄</div>
        </div>
        <div class="promo-card promo-orange2" @click="selectCategory(null)">
          <div class="promo-text">
            <h4>品质五金</h4>
            <p>超值特惠</p>
          </div>
          <div class="promo-icon">🔧</div>
        </div>
        <div class="promo-card promo-blue" @click="selectCategory(null)">
          <div class="promo-text">
            <h4>超值百货</h4>
            <p>省钱省心</p>
          </div>
          <div class="promo-icon">🧴</div>
        </div>
      </div>
    </div>

    <!-- 倒计时条 -->
    <div class="countdown-bar">
      <div class="countdown-content">
        <span class="countdown-label">⏰ 限时秒杀</span>
        <span class="countdown-time">{{ seckillCountdown }}</span>
        <span class="countdown-more" @click="goToCouponSeckill">更多秒杀 ›</span>
      </div>
    </div>



    <!-- 商品列表 -->
    <div class="product-section">
      <h2>推荐商品</h2>
      <div class="product-grid">
        <div v-for="product in filteredProducts" :key="product.id" class="product-card" @click="goToProductDetail(product.id)">
          <div class="product-image">
            <img v-if="product.mainImage" :src="product.mainImage" :alt="product.name" class="product-image-real" />
            <div v-else class="image-placeholder">商品图片</div>
          </div>
          <div class="product-info">
            <h3 class="product-name">{{ product.name }}</h3>
            <p class="product-desc" v-if="product.description">{{ product.description }}</p>
            <div class="product-bottom">
              <p class="product-price">¥{{ product.price }}</p>
              <p class="product-sales">{{ product.sales > 1000 ? (product.sales / 1000).toFixed(1) + 'k+' : product.sales }}人付款</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 登录弹窗 (保持不变) -->
    <div v-if="showLoginDialog" class="login-dialog">
      <div class="login-dialog-content">
        <div class="login-dialog-header">
          <h3>{{ showRegister ? '注册' : '登录' }}</h3>
          <span class="close-btn" @click="closeLoginDialog">×</span>
        </div>
        <div class="login-dialog-body">
          <div v-if="!showRegister">
            <div class="login-tabs">
              <span :class="{ active: loginTab === 'password' }" @click="loginTab = 'password'">密码登录</span>
              <span :class="{ active: loginTab === 'sms' }" @click="loginTab = 'sms'">短信登录</span>
            </div>
            <div class="login-form">
              <div class="form-group" v-if="loginTab === 'password'">
                <label>用户名</label>
                <input type="text" v-model="username" placeholder="请输入用户名" class="form-input" />
              </div>
              <div class="form-group" v-if="loginTab === 'sms'">
                <label>手机号</label>
                <div class="phone-input">
                  <span class="country-code">+86</span>
                  <input type="tel" v-model="phone" placeholder="请输入手机号" class="form-input" />
                </div>
              </div>
              <div class="form-group" v-if="loginTab === 'password'">
                <label>密码</label>
                <input type="password" v-model="password" placeholder="请输入密码" class="form-input" />
              </div>
              <div class="form-group" v-if="loginTab === 'sms'">
                <label>验证码</label>
                <div class="code-input">
                  <input type="text" v-model="verificationCode" placeholder="请输入验证码（默认 123456）" class="form-input" />
                  <button class="get-code-btn">获取验证码</button>
                </div>
              </div>
              <button class="login-btn" @click="handleLogin">登录</button>
              <div class="other-login">
                <p>其他方式登录</p>
                <div class="login-icons">
                  <span class="login-icon">微信</span>
                  <span class="login-icon">微博</span>
                  <span class="login-icon">QQ</span>
                </div>
              </div>
              <p class="login-tip">还没有账号？<span class="register-link" @click="showRegister = true">立即注册</span></p>
            </div>
          </div>
          <div v-else>
            <div class="login-form">
              <div class="form-group">
                <label>用户名</label>
                <input type="text" v-model="registerUsername" placeholder="请输入用户名" class="form-input" />
              </div>
              <div class="form-group">
                <label>手机号</label>
                <div class="phone-input">
                  <span class="country-code">+86</span>
                  <input type="tel" v-model="registerPhone" placeholder="请输入手机号" class="form-input" />
                </div>
              </div>
              <div class="form-group">
                <label>密码</label>
                <input type="password" v-model="registerPassword" placeholder="请输入密码" class="form-input" />
              </div>
              <div class="form-group">
                <label>确认密码</label>
                <input type="password" v-model="confirmPassword" placeholder="请再次输入密码" class="form-input" />
              </div>
              <button class="login-btn" @click="handleRegister">注册</button>
              <p class="login-tip">已有账号？<span class="register-link" @click="showRegister = false">立即登录</span></p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import CartSidebar from '../components/CartSidebar.vue'
import { getCategoryList, getProductList } from '../api/product'
import { login, register, logout } from '../api/user'
import { createOrder } from '../api/order'
import { getDefaultAddress } from '../api/address'

const router = useRouter()
const categories = ref([])
const activeCategoryId = ref(null)
const products = ref([])
const currentIndex = ref(1)
const autoplayTimer = ref(null)
const searchText = ref('')
const isSearchFocused = ref(false)
const seckillCountdown = ref('')
const seckillTimer = ref(null)
const banners = ref([
  { image: new URL('../assets/轮播图/轮播图1.jpg', import.meta.url).href, title: 'HUAWEI Pura90系列上市' },
  { image: new URL('../assets/轮播图/轮播图2.jpg', import.meta.url).href, title: '华为智慧屏,出色精彩' },
  { image: new URL('../assets/轮播图/轮播图3.jpg', import.meta.url).href, title: '问界M6,新锐智慧SUV' },
  { image: 'https://picsum.photos/800/400?random=4', title: '品质生活 - 智能家居套装' },
  { image: 'https://picsum.photos/800/400?random=5', title: '运动健康 - 智能手表运动版' },
  { image: 'https://picsum.photos/800/400?random=6', title: '数码配件 - 蓝牙耳机运动款' }
])
const hotSearchTags = ['华为Mate80', '连衣裙', '夏季新款', '防晒霜', '空调', '冰丝T恤', '运动鞋', '防晒霜']
const searchPlaceholders = ['2024新款连衣裙 夏季', '华为Mate80', '防晒霜SPF50+', '空调2024新款', '冰丝T恤 男', '笔记本电脑推荐']
const currentPlaceholderIndex = ref(0)
const placeholderTimer = ref(null)
const currentPlaceholderText = computed(() => searchPlaceholders[currentPlaceholderIndex.value])
const hotSales = ref([])
const isLoggedIn = ref(false)
const userNickname = ref('用户')
const showLoginDialog = ref(false)
const showRegister = ref(false)
const loginTab = ref('password')
const username = ref('')
const phone = ref('')
const password = ref('')
const verificationCode = ref('')
const registerUsername = ref('')
const registerPhone = ref('')
const registerPassword = ref('')
const confirmPassword = ref('')
const carouselWrapper = ref(null)
const cartSidebar = ref(null)
const isScrolled = ref(false)

const handleScroll = () => {
  isScrolled.value = window.scrollY > 32
}

const filteredProducts = computed(() => {
  if (!activeCategoryId.value) return products.value
  return products.value.filter(p => p.categoryId === activeCategoryId.value)
})

// 分类子项数据
const categorySubItems = {
  '手机数码': ['手机', '笔记本', '平板', '耳机', '充电器'],
  '电脑办公': ['台式机', '显示器', '键盘', '鼠标', '打印机'],
  '家用电器': ['冰箱', '洗衣机', '空调', '电视', '热水器'],
  '服装鞋包': ['男装', '女装', '运动鞋', '背包', '手表'],
  '食品生鲜': ['水果', '零食', '肉类', '海鲜', '乳制品'],
  '家居家装': ['沙发', '床垫', '灯具', '窗帘', '收纳'],
  '美妆个护': ['护肤', '彩妆', '香水', '洗发', '沐浴'],
  '母婴用品': ['奶粉', '尿不湿', '玩具', '童装', '推车'],
  '运动户外': ['跑步鞋', '运动服', '瑜伽垫', '帐篷', '自行车']
}

const getCategorySubItems = (name, idx) => {
  if (categorySubItems[name]) return categorySubItems[name]
  return ['子分类1', '子分类2', '子分类3', '子分类4']
}

const loadCategoryList = async () => {
  try {
    const result = await getCategoryList()
    if (result.code === 1) categories.value = result.data || []
  } catch (error) {}
}

const loadProductList = async () => {
  try {
    const result = await getProductList()
    if (result.code === 1) products.value = result.data || []
  } catch (error) {}
}

const loadHotSales = async () => {
  try {
    const result = await getProductList()
    if (result.code === 1) {
      const topProducts = (result.data || []).slice(0, 10)
      hotSales.value = topProducts.map((p, index) => ({
        rank: index + 1,
        name: p.name.substring(0, 8),
        sales: p.sales > 1000 ? (p.sales / 1000).toFixed(1) + 'k+' : p.sales + '+'
      }))
    }
  } catch (error) {}
}

const selectCategory = (categoryId) => { activeCategoryId.value = categoryId }

const startSeckillCountdown = () => {
  const endTime = new Date().getTime() + 7200000
  const updateCountdown = () => {
    const now = new Date().getTime()
    const distance = endTime - now
    if (distance < 0) {
      seckillCountdown.value = '已结束'
      if (seckillTimer.value) clearInterval(seckillTimer.value)
      return
    }
    const hours = Math.floor(distance / (1000 * 60 * 60))
    const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60))
    const seconds = Math.floor((distance % (1000 * 60)) / 1000)
    seckillCountdown.value = `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
  }
  updateCountdown()
  seckillTimer.value = setInterval(updateCountdown, 1000)
}

const goToCustomerService = () => { router.push('/customer-service') }
const goToCouponSeckill = () => { router.push('/coupon-seckill') }
const handleSearchFocus = () => { isSearchFocused.value = true }
const handleSearchBlur = () => { if (!searchText.value) isSearchFocused.value = false }
const handleSearch = () => { if (searchText.value) ElMessage.success('搜索:' + searchText.value) }
const handleWelcomeClick = () => { showLoginDialog.value = true }
const handlePersonalCenter = () => { if (!isLoggedIn.value) { showLoginDialog.value = true } else { router.push('/personal') } }
const handleMyOrders = () => { if (!isLoggedIn.value) { showLoginDialog.value = true } else { router.push({ path: '/personal', query: { tab: 'orders' } }) } }
const handleCart = () => { if (!isLoggedIn.value) { showLoginDialog.value = true } else { router.push({ path: '/personal', query: { tab: 'cart' } }) } }

const handleLogin = async () => {
  try {
    let result
    if (loginTab.value === 'password') {
      result = await login({ username: username.value, password: password.value })
    } else {
      result = await login({ username: phone.value, password: verificationCode.value })
    }
    if (result.code === 1) {
      const loginUser = result.data
      isLoggedIn.value = true
      userNickname.value = loginUser.user.nickname
      localStorage.setItem('loginUser', JSON.stringify(loginUser))
      showLoginDialog.value = false
      ElMessage.success('登录成功')
    } else {
      ElMessage.error(result.msg || '登录失败')
    }
  } catch (error) { ElMessage.error('登录失败,请稍后重试') }
}

const handleRegister = async () => {
  if (!registerUsername.value) { ElMessage.error('请输入用户名'); return }
  if (!registerPhone.value) { ElMessage.error('请输入手机号'); return }
  if (!registerPassword.value) { ElMessage.error('请输入密码'); return }
  if (registerPassword.value !== confirmPassword.value) { ElMessage.error('两次输入的密码不一致'); return }
  try {
    const result = await register({ username: registerUsername.value, phone: registerPhone.value, password: registerPassword.value })
    if (result.code === 1) {
      ElMessage.success('注册成功,请登录')
      showRegister.value = false
      registerUsername.value = ''; registerPhone.value = ''; registerPassword.value = ''; confirmPassword.value = ''
    } else { ElMessage.error(result.msg || '注册失败') }
  } catch (error) { ElMessage.error('注册失败,请稍后重试') }
}

const handleLogout = async () => {
  try {
    const result = await logout()
    if (result.code === 1) {
      isLoggedIn.value = false; userNickname.value = '用户'
      localStorage.removeItem('loginUser')
      ElMessage.success('退出登录成功')
    } else { ElMessage.error(result.msg || '退出登录失败') }
  } catch (error) { ElMessage.error('退出登录失败') }
}

const closeLoginDialog = () => { showLoginDialog.value = false }
const goHome = () => { router.push('/') }
const goToProductDetail = (productId) => { router.push({ path: '/product', query: { id: productId } }) }

const buyNow = async (product) => {
  if (!isLoggedIn.value) { showLoginDialog.value = true; return }

  try {
    const loginUser = JSON.parse(localStorage.getItem('loginUser'))
    const userId = loginUser.user.id
    const addressResult = await getDefaultAddress(userId)

    if (addressResult.code !== 1 || !addressResult.data) {
      ElMessage.warning('请先设置默认收货地址')
      router.push({ path: '/personal', query: { tab: 'address' } })
      return
    }

    const orderResult = await createOrder({
      userId,
      addressId: addressResult.data.id,
      couponIds: [],
      remark: '',
      items: [{ productId: product.id, quantity: 1 }]
    })

    if (orderResult.code === 1 && orderResult.data) {
      router.push(`/order/detail?id=${orderResult.data.id}`)
    } else {
      ElMessage.error(orderResult.msg || '立即购买失败')
    }
  } catch (error) {
    ElMessage.error('立即购买失败,请稍后重试')
  }
}

const nextSlide = () => { currentIndex.value++; resetAutoplay() }
const prevSlide = () => { currentIndex.value--; resetAutoplay() }

const handleTransitionEnd = () => {
  if (currentIndex.value === banners.value.length + 1) {
    if (carouselWrapper.value) {
      carouselWrapper.value.classList.add('no-transition')
      currentIndex.value = 1
      setTimeout(() => { if (carouselWrapper.value) carouselWrapper.value.classList.remove('no-transition') }, 50)
    }
  }
  if (currentIndex.value === 0) {
    if (carouselWrapper.value) {
      carouselWrapper.value.classList.add('no-transition')
      currentIndex.value = banners.value.length
      setTimeout(() => { if (carouselWrapper.value) carouselWrapper.value.classList.remove('no-transition') }, 50)
    }
  }
}

const goToSlide = (index) => { currentIndex.value = index + 1; resetAutoplay() }

const startAutoplay = () => { autoplayTimer.value = setInterval(() => { nextSlide() }, 4000) }
const resetAutoplay = () => { if (autoplayTimer.value) clearInterval(autoplayTimer.value); startAutoplay() }

// 搜索框提示词轮播
const startPlaceholderAutoplay = () => {
  placeholderTimer.value = setInterval(() => {
    currentPlaceholderIndex.value = (currentPlaceholderIndex.value + 1) % searchPlaceholders.length
  }, 3000)
}

onMounted(() => {
  loadCategoryList(); loadProductList(); loadHotSales()
  const savedLoginUser = JSON.parse(localStorage.getItem('loginUser'))
  if (savedLoginUser && savedLoginUser.user) {
    isLoggedIn.value = true
    userNickname.value = savedLoginUser.user.nickname
  }
  startSeckillCountdown(); startAutoplay(); startPlaceholderAutoplay()
  window.addEventListener('scroll', handleScroll)
})

onBeforeUnmount(() => {
  if (seckillTimer.value) clearInterval(seckillTimer.value)
  if (autoplayTimer.value) clearInterval(autoplayTimer.value)
  if (placeholderTimer.value) clearInterval(placeholderTimer.value)
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
@import '../styles/variables.css';

.home-container { min-height: 100vh; background: var(--color-bg); background-image:
  radial-gradient(circle at 20% 0%, rgba(229, 57, 53, 0.02) 0%, transparent 50%),
  radial-gradient(circle at 80% 100%, rgba(30, 136, 229, 0.02) 0%, transparent 50%); }

/* ===== 顶部第一行 - 灰色用户操作栏 ===== */
.header-top {
  background: var(--color-bg);
  border-bottom: 1px solid var(--color-border-light);
  height: 32px;
  line-height: 32px;
  transition: all 0.3s var(--ease-in-out);
  overflow: hidden;
}
.header-top.hidden { height: 0; padding: 0; border: none; }
.header-top .header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  justify-content: space-between;
}
.left-links { display: flex; align-items: center; gap: 4px; }
.right-links { display: flex; align-items: center; gap: 4px; }
.welcome-text { font-size: var(--text-xs); color: var(--color-text-primary); }
.register-btn {
  font-size: var(--text-xs);
  color: var(--color-primary);
  cursor: pointer;
  font-weight: 500;
}
.register-btn:hover { text-decoration: underline; }
.action-link {
  cursor: pointer;
  transition: color var(--duration-normal) var(--ease-in-out);
  font-size: var(--text-xs);
  color: var(--color-text-secondary);
  white-space: nowrap;
  padding: 2px 4px;
  border-radius: var(--radius-sm);
}
.action-link:hover {
  color: var(--color-primary);
  background: var(--color-primary-light);
}
.divider { color: var(--color-border); font-size: var(--text-xs); }

/* ===== 吸顶容器 ===== */
.sticky-wrapper { position: sticky; top: 0; z-index: 999; }

/* ===== 顶部第二行 - Logo + 搜索栏 (居中布局) ===== */
.header-search {
  background: var(--color-bg-white);
  padding: 20px 0;
  border-bottom: 1px solid var(--color-border-light);
}
.search-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: center; /* ✅ 居中布局 */
  gap: 48px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  flex-shrink: 0;
}
.logo-icon {
  background: var(--color-primary);
  color: white;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--text-3xl);
  font-weight: 700;
  border-radius: var(--radius-md);
  box-shadow: 0 2px 8px rgba(229, 57, 53, 0.2);
  transition: transform var(--duration-normal) var(--ease-out);
}
.logo:hover .logo-icon {
  transform: rotate(-3deg) scale(1.05);
}
.logo-text {
  font-size: var(--text-3xl);
  color: var(--color-primary);
  font-weight: 700;
}
.search-center {
  flex: 1;
  max-width: 640px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.search-box {
  display: flex;
  border: 2px solid var(--color-border);
  border-radius: var(--radius-full);
  overflow: hidden;
  height: 44px;
  width: 100%;
  transition: border-color var(--duration-normal) var(--ease-in-out),
              box-shadow var(--duration-normal) var(--ease-in-out);
  box-shadow: var(--shadow-xs);
}
.search-box:focus-within {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(229, 57, 53, 0.1);
}
.search-input {
  flex: 1;
  padding: 0 20px;
  border: none;
  outline: none;
  font-size: var(--text-base);
}
.search-input::placeholder { color: var(--color-text-tertiary); }
.search-btn {
  padding: 0 32px;
  background: var(--color-primary);
  color: white;
  border: none;
  cursor: pointer;
  font-size: var(--text-md);
  font-weight: 500;
  border-radius: 0 var(--radius-md) var(--radius-md) 0;
  transition: background var(--duration-normal) var(--ease-in-out),
              box-shadow var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
}
.search-btn:hover {
  background: var(--color-primary-hover);
  box-shadow: 0 4px 12px rgba(229, 57, 53, 0.25);
}
.search-btn:active {
  transform: scale(0.97);
  background: var(--color-primary-dark);
} /* ✅ 用透明度替代渐变变化 */
.hot-search {
  display: flex;
  align-items: center;
  gap: 0;
  margin-top: 10px;
  width: 100%;
  justify-content: center;
}
.hot-tag {
  font-size: var(--text-xs);
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.2s var(--ease-in-out);
  white-space: nowrap;
}
.hot-tag:hover { color: var(--color-primary); }
.hot-tag::after { content: '|'; margin-left: 10px; color: var(--color-border); }
.hot-tag:last-child::after { display: none; }
.hot-tag:nth-child(1),
.hot-tag:nth-child(2),
.hot-tag:nth-child(3) { color: var(--color-primary); }

/* ===== 顶部第三行 - 白色分类导航 (简化) ===== */
.category-nav {
  background: var(--color-bg-white);
  border-bottom: 1px solid var(--color-border-light);
}
.category-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  gap: 8px;
  overflow-x: auto;
}
.category-item {
  padding: 10px 16px;
  font-size: var(--text-base);
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: color var(--duration-normal) var(--ease-in-out),
              background var(--duration-normal) var(--ease-in-out);
  font-weight: 400;
  white-space: nowrap;
  border-radius: var(--radius-md);
}
.category-item:hover {
  background: var(--color-bg);
  color: var(--color-text-primary);
}
.category-item.active {
  background: var(--color-primary-light);
  color: var(--color-primary);
  font-weight: 600;
}
.all-categories {
  background: var(--color-primary);
  color: white;
  font-weight: 600;
  margin-right: 8px;
  padding: 10px 16px;
  border-radius: var(--radius-md);
  transition: background var(--duration-normal) var(--ease-in-out),
              box-shadow var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
  box-shadow: 0 2px 6px rgba(229, 57, 53, 0.15);
}
.all-categories:hover {
  background: var(--color-primary-hover);
  box-shadow: 0 2px 8px rgba(229, 57, 53, 0.2);
}
.all-categories:active {
  transform: scale(0.97);
}

/* ===== 主体内容 ===== */
.main-content {
  max-width: 1200px;
  margin: 20px auto;
  display: grid;
  grid-template-columns: 240px 1fr 280px;
  gap: 20px;
  align-items: start;
  padding: 0 24px;
}

/* 热销榜单 - 与轮播图高度一致 */
.hot-sales {
  background: var(--color-bg-white);
  border-radius: var(--radius-md);
  padding: 20px;
  height: 400px;
  overflow: hidden;
  border: 1px solid var(--color-border-light);
  position: relative;
}
.hot-sales::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--color-primary), var(--color-primary-dark));
  border-radius: var(--radius-md) var(--radius-md) 0 0;
}
.hot-sales-header { margin-bottom: 16px; }
.hot-sales-header h3 {
  margin: 0 0 4px 0;
  color: var(--color-primary);
  font-size: var(--text-lg);
  font-weight: 600;
}
.hot-sales-subtitle { font-size: var(--text-xs); color: var(--color-text-tertiary); }
.hot-sales-list { display: flex; flex-direction: column; gap: 4px; }
.hot-sales-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 10px;
  border-radius: var(--radius-sm);
  cursor: pointer;
  transition: background var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
}
.hot-sales-item:hover {
  background: var(--color-primary-light);
  transform: translateX(3px);
}
.item-rank {
  width: 22px;
  height: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg);
  border-radius: var(--radius-sm);
  font-size: var(--text-xs);
  color: var(--color-text-tertiary);
  font-weight: 600;
  flex-shrink: 0;
}
.item-rank.top-3 {
  background: var(--color-primary);
  color: white;
  box-shadow: 0 2px 6px rgba(229, 57, 53, 0.2);
}
.hot-sales-item:nth-child(1) .item-rank.top-3 { background: #FF4757; box-shadow: 0 2px 8px rgba(255, 71, 87, 0.3); }
.hot-sales-item:nth-child(2) .item-rank.top-3 { background: #FF6348; box-shadow: 0 2px 8px rgba(255, 99, 72, 0.25); }
.hot-sales-item:nth-child(3) .item-rank.top-3 { background: #FFA502; box-shadow: 0 2px 8px rgba(255, 165, 2, 0.25); }
.item-info { flex: 1; overflow: hidden; }
.item-name {
  font-size: var(--text-xs);
  color: var(--color-text-primary);
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.item-sales {
  font-size: var(--text-xs);
  color: var(--color-primary);
  font-weight: 600;
  flex-shrink: 0;
}

/* 轮播图 */
.banner {
  background: var(--color-bg-white);
  border-radius: var(--radius-md);
  overflow: hidden;
  border: 1px solid var(--color-border-light);
}
.carousel-container { position: relative; width: 100%; height: 400px; overflow: hidden; }
.carousel-wrapper {
  display: flex;
  transition: transform 0.3s var(--ease-in-out);
  height: 100%;
}
.carousel-item { min-width: 100%; height: 100%; position: relative; }
.carousel-image { width: 100%; height: 100%; object-fit: cover; }
.carousel-caption {
  position: absolute;
  bottom: 40px;
  left: 20px;
  background: rgba(0, 0, 0, 0.55);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
  color: white;
  padding: 10px 20px;
  font-size: var(--text-lg);
  border-radius: var(--radius-md);
  font-weight: 500;
  letter-spacing: 0.5px;
}
.carousel-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255,255,255,0.95);
  color: var(--color-text-primary);
  border: none;
  width: 40px;
  height: 40px;
  border-radius: var(--radius-full);
  font-size: 20px;
  cursor: pointer;
  z-index: 10;
  transition: background var(--duration-normal) var(--ease-in-out),
              box-shadow var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
  box-shadow: var(--shadow-sm);
}
.carousel-btn:hover {
  background: var(--color-bg-white);
  box-shadow: var(--shadow-md);
  transform: translateY(-50%) scale(1.08);
}
.carousel-btn:active {
  transform: translateY(-50%) scale(0.95);
  box-shadow: var(--shadow-xs);
}
.prev-btn { left: 12px; }
.next-btn { right: 12px; }
.carousel-indicators {
  position: absolute;
  bottom: 16px;
  left: 16px;
  display: flex;
  gap: 8px;
  z-index: 10;
}
.indicator {
  width: 8px;
  height: 8px;
  border-radius: var(--radius-full);
  background: rgba(255,255,255,0.5);
  cursor: pointer;
  transition: all 0.2s var(--ease-in-out);
}
.indicator.active {
  background: white;
  width: 24px;
  border-radius: var(--radius-sm);
}

/* 右侧促销卡片 - 与轮播图高度一致 (去渐变化) */
.promo-cards {
  display: flex;
  flex-direction: column;
  gap: 12px;
  height: 400px;
}
.promo-card {
  flex: 1;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 18px;
  cursor: pointer;
  transition: transform var(--duration-normal) var(--ease-in-out),
              box-shadow var(--duration-normal) var(--ease-in-out),
              border-color var(--duration-normal) var(--ease-in-out);
  border: 1px solid var(--color-border-light);
  background: var(--color-bg-white);
}
.promo-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-md);
  border-color: var(--color-primary);
}
.promo-card:active {
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}
.promo-text h4 {
  font-size: var(--text-md);
  font-weight: 600;
  margin: 0 0 4px 0;
  color: var(--color-text-primary);
}
.promo-text p {
  font-size: var(--text-xs);
  margin: 0;
  color: var(--color-text-secondary);
}
.promo-icon { font-size: 32px; }
/* ✅ 移除所有渐变背景,使用浅色背景 + 图标色彩 */
.promo-orange {
  background: linear-gradient(135deg, #FFF8F0, #FFFFFF);
  border-color: #FFE0CC;
}
.promo-orange:hover { border-color: #FF8C42; box-shadow: 0 8px 24px rgba(255, 140, 66, 0.12); }
.promo-pink {
  background: linear-gradient(135deg, #FFF0F3, #FFFFFF);
  border-color: #FFD6E0;
}
.promo-pink:hover { border-color: #FF6B8A; box-shadow: 0 8px 24px rgba(255, 107, 138, 0.12); }
.promo-orange2 {
  background: linear-gradient(135deg, #FFF5EB, #FFFFFF);
  border-color: #FFE4C4;
}
.promo-orange2:hover { border-color: #F0A030; box-shadow: 0 8px 24px rgba(240, 160, 48, 0.12); }
.promo-blue {
  background: linear-gradient(135deg, #F0F7FF, #FFFFFF);
  border-color: #C8E0F4;
}
.promo-blue:hover { border-color: #5BA0D9; box-shadow: 0 8px 24px rgba(91, 160, 217, 0.12); }

/* ===== 倒计时条 ===== */
.countdown-bar {
  max-width: 1200px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, var(--color-primary), #D32F2F);
  border-radius: var(--radius-md);
  overflow: hidden;
  position: relative;
}
.countdown-bar::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -10%;
  width: 200px;
  height: 200px;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 50%;
}
.countdown-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
  padding: 14px 20px;
  position: relative;
  z-index: 1;
}
.countdown-label { color: white; font-size: var(--text-lg); font-weight: 600; }
.countdown-time {
  color: white;
  font-size: var(--text-xl);
  font-weight: 700;
  font-family: 'Courier New', monospace;
  background: rgba(0,0,0,0.15);
  padding: 4px 12px;
  border-radius: var(--radius-sm);
  min-width: 120px;
  text-align: center;
  letter-spacing: 2px;
}
.countdown-more { color: rgba(255,255,255,0.9); font-size: var(--text-base); cursor: pointer; }
.countdown-more:hover { color: white; }

/* ===== 商品列表 - 专业化网格布局 ===== */
.product-section {
  max-width: 1200px;
  margin: 0 auto 24px;
  padding: 24px;
  background: var(--color-bg-white);
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border-light);
}
.product-section h2 {
  margin: 0 0 24px;
  color: var(--color-text-primary);
  font-size: var(--text-2xl);
  font-weight: 600;
  position: relative;
  padding-left: 14px;
}
.product-section h2::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 20px;
  background: var(--color-primary);
  border-radius: 2px;
}
.product-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr); /* ✅ 固定列数,更整齐 */
  gap: 16px; /* ✅ 增加间距 */
}
.product-card {
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: transform var(--duration-slow) var(--ease-out),
              box-shadow var(--duration-slow) var(--ease-out),
              border-color var(--duration-normal) var(--ease-in-out);
  background: var(--color-bg-white);
  box-shadow: var(--shadow-xs);
  position: relative;
}
.product-card::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--color-primary);
  transform: scaleX(0);
  transition: transform var(--duration-normal) var(--ease-in-out);
}
.product-card:hover::after {
  transform: scaleX(1);
}
.product-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-card-hover);
  border-color: var(--color-primary);
}
.product-card:active {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}
.product-image {
  width: 100%;
  aspect-ratio: 1; /* ✅ 保持正方形 */
  background: var(--color-bg);
  overflow: hidden;
}
.product-image-real {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s var(--ease-in-out);
}
.product-card:hover .product-image-real { transform: scale(1.03); } /* ✅ 降低缩放幅度 */
.product-info { padding: 12px; }
.product-name {
  font-size: var(--text-base);
  color: var(--color-text-primary);
  margin: 0 0 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  font-weight: 400;
  line-height: 1.5;
}
.product-desc {
  font-size: var(--text-xs);
  color: var(--color-text-tertiary);
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.product-bottom {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
}
.product-price {
  font-size: var(--text-2xl);
  color: var(--color-primary);
  font-weight: 700;
  margin: 0;
  line-height: 1;
  letter-spacing: -0.5px;
}
.product-sales {
  font-size: var(--text-xs);
  color: var(--color-text-tertiary);
  margin: 0;
  white-space: nowrap;
}

/* ===== 登录弹窗 ===== */
.login-dialog {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease-out;
}
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
.login-dialog-content {
  background: var(--color-bg-white);
  border-radius: var(--radius-lg);
  width: 420px;
  max-width: 90%;
  box-shadow: var(--shadow-lg);
  overflow: hidden;
  animation: slideUp 0.25s var(--ease-out);
}
@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
.login-dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid var(--color-border-light);
}
.login-dialog-header h3 {
  margin: 0;
  color: var(--color-text-primary);
  font-size: var(--text-lg);
  font-weight: 600;
}
.close-btn {
  font-size: 24px;
  color: var(--color-text-tertiary);
  cursor: pointer;
  transition: color var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
  line-height: 1;
  padding: 4px;
  border-radius: var(--radius-sm);
}
.close-btn:hover {
  color: var(--color-text-primary);
  transform: rotate(90deg);
}
.login-dialog-body { padding: 24px; }
.login-tabs {
  display: flex;
  border-bottom: 1px solid var(--color-border-light);
  margin-bottom: 24px;
}
.login-tabs span {
  flex: 1;
  text-align: center;
  padding: 12px 0;
  cursor: pointer;
  color: var(--color-text-secondary);
  font-size: var(--text-base);
  border-bottom: 2px solid transparent;
  transition: color var(--duration-normal) var(--ease-in-out),
              border-color var(--duration-normal) var(--ease-in-out),
              font-weight var(--duration-fast) var(--ease-in-out);
}
.login-tabs span:hover {
  color: var(--color-text-primary);
}
.login-tabs span.active {
  color: var(--color-primary);
  border-bottom-color: var(--color-primary);
  font-weight: 600;
}
.login-form { display: flex; flex-direction: column; gap: 18px; }
.form-group { display: flex; flex-direction: column; gap: 8px; }
.form-group label {
  font-size: var(--text-base);
  color: var(--color-text-primary);
  font-weight: 500;
}
.phone-input {
  display: flex;
  align-items: center;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  overflow: hidden;
}
.country-code {
  padding: 10px 12px;
  background: var(--color-bg);
  border-right: 1px solid var(--color-border);
  color: var(--color-text-secondary);
  font-size: var(--text-base);
}
.code-input { display: flex; gap: 10px; }
.form-input {
  flex: 1;
  padding: 10px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: var(--text-base);
  outline: none;
  transition: border-color var(--duration-normal) var(--ease-in-out),
              box-shadow var(--duration-normal) var(--ease-in-out);
}
.form-input:focus {
  border-color: var(--color-primary);
  box-shadow: var(--input-focus-shadow);
}
.get-code-btn {
  padding: 0 16px;
  background: var(--color-primary-light);
  color: var(--color-primary);
  border: 1px solid var(--color-primary);
  border-radius: var(--radius-md);
  cursor: pointer;
  font-size: var(--text-base);
  white-space: nowrap;
  transition: background var(--duration-normal) var(--ease-in-out),
              color var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
}
.get-code-btn:hover {
  background: var(--color-primary);
  color: white;
}
.get-code-btn:active {
  transform: scale(0.97);
}
.login-btn {
  padding: 12px;
  background: var(--color-primary);
  color: #fff;
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--text-lg);
  font-weight: 600;
  cursor: pointer;
  transition: background var(--duration-normal) var(--ease-in-out),
              box-shadow var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
}
.login-btn:hover {
  background: var(--color-primary-hover);
  box-shadow: var(--shadow-primary);
}
.login-btn:active {
  transform: scale(0.97);
  background: var(--color-primary-dark);
}
.other-login {
  text-align: center;
  padding: 20px 0;
  border-top: 1px solid var(--color-border-light);
}
.other-login p {
  margin: 0 0 12px 0;
  color: var(--color-text-tertiary);
  font-size: var(--text-base);
}
.login-icons { display: flex; justify-content: center; gap: 20px; }
.login-icon {
  font-size: var(--text-base);
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: 8px 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  transition: color var(--duration-normal) var(--ease-in-out),
              border-color var(--duration-normal) var(--ease-in-out),
              background var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
}
.login-icon:hover {
  color: var(--color-primary);
  border-color: var(--color-primary);
  background: var(--color-primary-light);
  transform: translateY(-1px);
}
.login-icon:active {
  transform: scale(0.97);
}
.login-tip {
  text-align: center;
  font-size: var(--text-xs);
  color: var(--color-text-tertiary);
  margin: 12px 0 0 0;
}
.register-link {
  color: var(--color-primary);
  cursor: pointer;
  font-weight: 500;
}
.register-link:hover { color: var(--color-primary-dark); }
.no-transition { transition: none !important; }
</style>
