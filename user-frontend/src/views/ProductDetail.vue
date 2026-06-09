<template>
  <div class="product-detail-container">
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
          <span class="action-link" @click="handlePersonalCenter">个人中心</span>
          <span class="divider">|</span>
          <span class="action-link" @click="handleMyOrders">我的订单</span>
          <span class="divider">|</span>
          <span class="action-link" @click="goToCustomerService">联系客服</span>
        </div>
      </div>
    </div>

    <!-- 顶部导航栏 -->
    <div class="header-search">
      <div class="header-content">
        <div class="logo-section">
          <span class="logo-icon">潮</span>
          <h1 class="logo" @click="goHome">潮选优品</h1>
        </div>
        <div class="search-wrapper">
          <div class="search-box">
            <input type="text" placeholder="搜索商品" class="search-input" />
            <button class="search-btn">搜索</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-content">
      <!-- 左侧：商品图片和详情 -->
      <div class="left-section">
        <!-- 店铺信息 -->
        <div class="store-bar" v-if="product.merchantName">
          <div class="store-info">
            <div class="store-avatar">{{ product.merchantName.charAt(0) }}</div>
            <div class="store-meta">
              <span class="store-name">{{ product.merchantName }}</span>
              <div class="store-rating">
                <span class="rating-star">★★★★★</span>
                <span class="rating-score">4.8</span>
              </div>
            </div>
          </div>
          <div class="store-actions">
            <button class="store-btn customer-btn">💬 客服</button>
            <button class="store-btn enter-btn" @click="goToMerchant">进店 ›</button>
          </div>
        </div>

        <!-- 商品图片区域 -->
        <div class="product-gallery">
          <div class="thumbnail-list">
            <div 
              v-for="(img, index) in productImages" 
              :key="index"
              :class="['thumbnail-item', { active: currentImageIndex === index }]"
              @click="currentImageIndex = index"
            >
              <img :src="img" :alt="product.name" />
            </div>
          </div>
          <div class="main-image-container">
            <img :src="productImages[currentImageIndex]" :alt="product.name" class="main-image" />
          </div>
        </div>

        <!-- 商品详情 -->
        <div class="product-detail-section">
          <div class="section-tabs">
            <span 
              :class="['tab-item', { active: activeTab === 'detail' }]"
              @click="activeTab = 'detail'"
            >
              商品详情
            </span>
            <span 
              :class="['tab-item', { active: activeTab === 'reviews' }]"
              @click="activeTab = 'reviews'"
            >
              用户评价 ({{ reviewCount }})
            </span>
          </div>

          <div v-if="activeTab === 'detail'" class="detail-content">
            <!-- 商品详情图 -->
            <div class="detail-images">
              <img v-for="(img, index) in detailImages" :key="index" :src="img" class="detail-image" />
            </div>
          </div>

          <div v-if="activeTab === 'reviews'" class="reviews-content">
            <!-- 评价概览 -->
            <div class="review-summary">
              <div class="summary-score">
                <span class="score-number">{{ averageRating }}</span>
                <span class="score-unit">分</span>
                <div class="score-stars">
                  <span v-for="i in 5" :key="i" class="star" :class="{ filled: i <= Math.round(averageRating) }">★</span>
                </div>
                <span class="score-count">{{ reviewCount }} 条评价</span>
              </div>
              <div class="summary-distribution">
                <div v-for="(pct, i) in ratingDistribution" :key="i" class="dist-row">
                  <span class="dist-label">{{ 5 - i }}星</span>
                  <div class="dist-bar"><div class="dist-fill" :style="{ width: pct + '%' }"></div></div>
                  <span class="dist-pct">{{ pct }}%</span>
                </div>
              </div>
            </div>

            <!-- 发表评论按钮 -->
            <div class="comment-action">
              <button class="comment-btn" @click="showCommentForm = !showCommentForm">
                {{ showCommentForm ? '收起' : '发表评论' }}
              </button>
            </div>

            <!-- 评论表单 -->
            <div v-if="showCommentForm" class="comment-form">
              <div class="form-rating">
                <span class="rating-label">评分：</span>
                <div class="rating-stars">
                  <span v-for="i in 5" :key="i" class="star-select" :class="{ filled: i <= commentForm.rating }" @click="setCommentRating(i)">★</span>
                </div>
              </div>
              <textarea v-model="commentForm.content" placeholder="请输入您的评论..." class="comment-textarea" rows="3"></textarea>
              <div class="form-actions">
                <button class="submit-btn" @click="submitComment">提交评论</button>
              </div>
            </div>

            <!-- 评价标签筛选 -->
            <div class="review-tags">
              <span class="review-tag active">全部</span>
              <span v-for="(tag, index) in reviewTags" :key="index" class="review-tag">{{ tag }}</span>
            </div>

            <!-- 评价列表 -->
            <div class="review-list">
              <div v-if="reviews.length === 0" class="no-reviews">暂无评价</div>
              <div v-for="(review, index) in reviews" :key="index" class="review-item">
                <div class="review-header">
                  <div class="reviewer-avatar">{{ review.name.charAt(0) }}</div>
                  <div class="reviewer-meta">
                    <div class="reviewer-top">
                      <span class="reviewer-name">{{ review.name }}</span>
                      <div class="review-stars">
                        <span v-for="i in 5" :key="i" class="star-sm" :class="{ filled: i <= (review.rating || 5) }">★</span>
                      </div>
                    </div>
                    <span class="review-date">{{ review.date }}</span>
                  </div>
                </div>
                <p class="review-content">{{ review.content }}</p>
                <div v-if="review.image" class="review-image">
                  <img :src="review.image" alt="评价图片" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：购买操作区（固定） -->
      <div class="right-section">
        <div class="purchase-panel">
          <!-- 商品标题 -->
          <h2 class="product-title">{{ product.name }}</h2>

          <!-- 社会证明 -->
          <div class="social-proof">
            <span class="proof-item proof-sales">已售 {{ product.sales || 0 }}+</span>
            <span class="proof-divider">|</span>
            <span class="proof-item proof-review">多人评价"质量不错"</span>
            <span class="proof-divider">|</span>
            <span class="proof-item proof-fans">{{ Math.floor((product.sales || 0) * 0.3) }}人加购</span>
          </div>

          <!-- 价格区域 -->
          <div class="price-panel">
            <div class="price-main">
              <div class="price-left">
                <span class="price-label">店铺优惠后</span>
                <div class="price-row">
                  <span class="price-symbol">¥</span>
                  <span class="price-value">{{ product.price }}</span>
                </div>
                <span class="price-original">优惠前 ¥{{ (product.price * 1.3).toFixed(0) }}</span>
              </div>
              <div class="price-activity">
                <div class="activity-badge">限时特惠</div>
                <div class="activity-time">活动进行中</div>
              </div>
            </div>
          </div>

          <!-- 促销标签 -->
          <div class="promo-tags">
            <span class="promo-tag-item">官方立减{{ (product.price * 0.1).toFixed(0) }}元</span>
          </div>

          <!-- 优惠券领取 -->
          <div class="coupon-bar" @click="goToCouponPage">
            <div class="coupon-left">
              <svg class="coupon-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 12V8H6a2 2 0 01-2-2c0-1.1.9-2 2-2h12v4"/><path d="M4 6v12c0 1.1.9 2 2 2h14v-4"/><path d="M18 12a2 2 0 000 4h4v-4h-4z"/></svg>
              <span>领取优惠券</span>
            </div>
            <span class="coupon-arrow">›</span>
          </div>

          <!-- 服务保障 -->
          <div class="service-info">
            <div class="service-row">
              <svg class="service-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="1" y="3" width="15" height="13" rx="2"/><path d="M16 8h4l3 3v5a2 2 0 01-2 2h-1"/><circle cx="5.5" cy="18.5" r="2.5"/><circle cx="18.5" cy="18.5" r="2.5"/></svg>
              <span>48小时内发货</span>
            </div>
            <span class="service-divider">|</span>
            <div class="service-row">
              <span>快递: 免运费</span>
            </div>
          </div>
          <div class="service-info">
            <div class="service-row">
              <svg class="service-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>
              <span>大促价保</span>
            </div>
            <span class="service-divider">|</span>
            <div class="service-row">
              <span>7天无理由退货</span>
            </div>
            <span class="service-divider">|</span>
            <div class="service-row">
              <span>极速退款</span>
            </div>
          </div>

          <!-- 规格选择 -->
          <div class="spec-section">
            <div class="spec-label">颜色分类</div>
            <div class="spec-options">
              <div 
                v-for="(spec, index) in specs" 
                :key="index"
                :class="['spec-option', { selected: selectedSpec === spec }]"
                @click="selectedSpec = spec"
              >
                {{ spec }}
              </div>
            </div>
          </div>

          <!-- 数量选择 -->
          <div class="quantity-section">
            <div class="quantity-label">数量</div>
            <div class="quantity-control">
              <button 
                class="quantity-btn decrease"
                :disabled="quantity <= 1"
                @click="decreaseQuantity"
              >
                -
              </button>
              <input 
                type="number" 
                v-model.number="quantity" 
                :min="1" 
                :max="product.stock"
                class="quantity-input"
              />
              <button 
                class="quantity-btn increase"
                :disabled="quantity >= product.stock"
                @click="handleIncreaseClick"
              >
                +
              </button>
            </div>
            <span class="stock-text">库存{{ product.stock }}件</span>
          </div>

          <!-- 操作按钮 -->
          <div class="action-buttons">
            <div class="combined-buttons">
              <button class="cart-btn" @click="addToCart">
                <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/><path d="M1 1h4l2.68 13.39a2 2 0 002 1.61h9.72a2 2 0 002-1.61L23 6H6"/></svg>
                <span>加入购物车</span>
              </button>
              <button class="buy-btn" @click="buyNow">
                <span>立即购买</span>
              </button>
            </div>
            <button class="favorite-btn" @click="toggleFavorite" :class="{ active: isFavorited }">
              <svg class="fav-icon" :class="{ filled: isFavorited }" viewBox="0 0 24 24" :fill="isFavorited ? 'currentColor' : 'none'" stroke="currentColor" stroke-width="2"><path d="M20.84 4.61a5.5 5.5 0 00-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 00-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 000-7.78z"/></svg>
            </button>
          </div>

          <!-- 已选信息 -->
          <div class="selected-info">
            <span>已选：{{ selectedSpec }}, {{ quantity }}件</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 登录弹窗 -->
    <div v-if="showLoginDialog" class="login-dialog">
      <div class="login-dialog-content">
        <div class="login-dialog-header">
          <h3>{{ showRegister ? '注册' : '登录' }}</h3>
          <span class="close-btn" @click="showLoginDialog = false">×</span>
        </div>
        <div class="login-dialog-body">
          <!-- 登录表单 -->
          <div v-if="!showRegister">
            <div class="login-tabs">
              <span 
                :class="{ active: loginTab === 'password' }" 
                @click="loginTab = 'password'"
              >
                密码登录
              </span>
              <span 
                :class="{ active: loginTab === 'sms' }" 
                @click="loginTab = 'sms'"
              >
                短信登录
              </span>
            </div>
            
            <div class="login-form">
              <div class="form-group" v-if="loginTab === 'password'">
                <label>用户名</label>
                <input 
                  type="text" 
                  v-model="username" 
                  placeholder="请输入用户名"
                  class="form-input"
                />
              </div>
              <div class="form-group" v-if="loginTab === 'sms'">
                <label>手机号</label>
                <div class="phone-input">
                  <span class="country-code">+86</span>
                  <input 
                    type="tel" 
                    v-model="phone" 
                    placeholder="请输入手机号"
                    class="form-input"
                  />
                </div>
              </div>
              
              <div class="form-group" v-if="loginTab === 'password'">
                <label>密码</label>
                <input 
                  type="password" 
                  v-model="password" 
                  placeholder="请输入密码"
                  class="form-input"
                />
              </div>
              
              <div class="form-group" v-if="loginTab === 'sms'">
                <label>验证码</label>
                <div class="code-input">
                  <input 
                    type="text" 
                    v-model="verificationCode" 
                    placeholder="请输入验证码（默认 123456）"
                    class="form-input"
                  />
                  <button class="get-code-btn">获取验证码</button>
                </div>
              </div>
              
              <button class="login-btn" @click="handleLogin">登录</button>
              
              <div class="other-login">
                <p>其他方式登录</p>
                <div class="login-icons">
                  <span class="login-icon"><i class="ri-wechat-fill" style="color:#07C160;font-size:18px;"></i></span>
                  <span class="login-icon"><i class="ri-weibo-fill" style="color:#E6162D;font-size:18px;"></i></span>
                  <span class="login-icon"><i class="ri-qq-fill" style="color:#12B7F5;font-size:18px;"></i></span>
                </div>
              </div>
              
              <p class="login-tip">
                还没有账号？
                <span class="register-link" @click="showRegister = true">立即注册</span>
              </p>
            </div>
          </div>
          
          <!-- 注册表单 -->
          <div v-else>
            <div class="login-form">
              <div class="form-group">
                <label>用户名</label>
                <input 
                  type="text" 
                  v-model="registerUsername" 
                  placeholder="请输入用户名"
                  class="form-input"
                />
              </div>
              
              <div class="form-group">
                <label>手机号</label>
                <div class="phone-input">
                  <span class="country-code">+86</span>
                  <input 
                    type="tel" 
                    v-model="registerPhone" 
                    placeholder="请输入手机号"
                    class="form-input"
                  />
                </div>
              </div>
              
              <div class="form-group">
                <label>密码</label>
                <input 
                  type="password" 
                  v-model="registerPassword" 
                  placeholder="请输入密码"
                  class="form-input"
                />
              </div>
              
              <div class="form-group">
                <label>确认密码</label>
                <input 
                  type="password" 
                  v-model="confirmPassword" 
                  placeholder="请再次输入密码"
                  class="form-input"
                />
              </div>
              
              <button class="login-btn" @click="handleRegister">注册</button>
              
              <p class="login-tip">
                已有账号？
                <span class="register-link" @click="showRegister = false">立即登录</span>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import CartSidebar from '../components/CartSidebar.vue'
import { getProductDetail } from '@/api/product'
import { login, register, logout } from '../api/user'
import { addFavorite, removeFavorite, isFavorite } from '../api/favorite'
import { addToCart } from '../api/cart'
import { createOrder } from '../api/order'
import { getDefaultAddress } from '../api/address'
import { addComment, getCommentList, getCommentStats } from '../api/comment'

export default {
  name: 'ProductDetail',
  components: {
    CartSidebar
  },
  data() {
    return {
      currentImageIndex: 0,
      activeTab: 'detail',
      quantity: 1,
      isFavorited: false,
      selectedSpec: '经典款',
      productImages: [
        'https://picsum.photos/600/600?random=1',
        'https://picsum.photos/600/600?random=2',
        'https://picsum.photos/600/600?random=3',
        'https://picsum.photos/600/600?random=4'
      ],
      detailImages: [
        'https://picsum.photos/800/600?random=10',
        'https://picsum.photos/800/600?random=11',
        'https://picsum.photos/800/600?random=12'
      ],
      reviewCount: 0,
      averageRating: 0,
      ratingDistribution: [0, 0, 0, 0, 0],
      reviewTags: ['整体感受好', '发货准时', '质量不错', '做工精细', '性价比高'],
      reviews: [],
      specs: ['经典款', '升级款', '豪华款'],
      product: {
        id: 1,
        name: '',
        description: '',
        price: 0,
        sales: 0,
        merchantName: '',
        stock: 999
      },
      productFeatures: [],
      isLoggedIn: false,
      userNickname: '用户',
      showLoginDialog: false,
      showRegister: false,
      loginTab: 'password',
      username: '',
      phone: '',
      password: '',
      verificationCode: '',
      // 注册相关变量
      registerUsername: '',
      registerPhone: '',
      registerPassword: '',
      confirmPassword: '',
      // 评论相关
      showCommentForm: false,
      commentForm: {
        rating: 5,
        content: '',
        images: ''
      }
    }
  },
  mounted() {
    this.loadProductDetail()
    this.checkLoginStatus()
  },
  watch: {
    'product.stock': { 
      handler(newVal) {
        console.log('库存变化:', newVal)
      },
      immediate: true
    },
    quantity: {
      handler(newVal) {
        console.log('数量变化:', newVal, '当前库存:', this.product.stock)
      }
    }
  },
  methods: {
    loadProductDetail() {
      const productId = this.$route.query.id || this.$route.params.id
      if (!productId) {
        this.$message.error('商品ID不能为空')
        return
      }
      getProductDetail(productId)
        .then(response => {
          if (response.code === 1) {
            console.log('商品详情数据:', response.data)
            // 确保 stock 字段存在且有效
            const productData = response.data
            if (!productData.stock || productData.stock <= 0) {
              productData.stock = 999 // 设置默认库存
            }
            this.product = productData

            // 使用数据库中的主图作为轮播图
            if (productData.mainImage) {
              this.productImages = [productData.mainImage]
            }

            // 使用数据库中的详情图
            if (productData.detailImages) {
              try {
                const detailImgs = JSON.parse(productData.detailImages)
                this.detailImages = detailImgs
              } catch (e) {
                console.log('解析详情图失败:', e)
              }
            }

            console.log('最终商品数据 - stock:', this.product.stock)
            // 加载收藏状态
            this.loadFavoriteStatus()
            // 加载评论数据
            this.loadComments()
          }
        })
        .catch(error => {
          console.error('加载商品详情失败:', error)
          this.$message.error('加载商品详情失败')
        })
    },
    goBack() {
      this.$router.back()
    },
    goHome() {
      this.$router.push('/')
    },
    goToMerchant() {
      if (this.product.merchantId) {
        this.$router.push({ path: '/merchant', query: { id: this.product.merchantId } })
      }
    },
    goToCouponPage() {
      this.$router.push('/coupon-seckill')
    },
    increaseQuantity() {
      console.log('点击增加按钮 - 当前数量:', this.quantity, '库存:', this.product.stock)
      if (this.quantity < this.product.stock) {
        this.quantity++
        console.log('增加后数量:', this.quantity)
      } else {
        console.log('已达到库存上限')
      }
    },
    testIncrease() {
      console.log('=== 测试按钮点击 ===')
      console.log('当前数量:', this.quantity)
      console.log('当前库存:', this.product.stock)
      console.log('是否禁用:', this.quantity >= this.product.stock)
      this.increaseQuantity()
    },
    handleIncreaseClick(event) {
      console.log('=== 原生点击事件 ===', event)
      console.log('目标元素:', event.target)
      console.log('当前数量:', this.quantity)
      console.log('当前库存:', this.product.stock)
      this.increaseQuantity()
    },
    decreaseQuantity() {
      console.log('点击减少按钮 - 当前数量:', this.quantity)
      if (this.quantity > 1) {
        this.quantity--
        console.log('减少后数量:', this.quantity)
      }
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

    async handleLogin() {
      try {
        let result
        if (this.loginTab === 'password') {
          result = await login({
            username: this.username,
            password: this.password
          })
        } else {
          result = await login({
            username: this.phone,
            password: this.verificationCode
          })
        }

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
    
    async handleRegister() {
      // 验证表单
      if (!this.registerUsername) {
        this.$message.warning('请输入用户名')
        return
      }
      if (!this.registerPhone) {
        this.$message.warning('请输入手机号')
        return
      }
      if (!this.registerPassword) {
        this.$message.warning('请输入密码')
        return
      }
      if (this.registerPassword !== this.confirmPassword) {
        this.$message.warning('两次输入的密码不一致')
        return
      }
      
      try {
        // 调用注册接口
        const result = await register({
          username: this.registerUsername,
          phone: this.registerPhone,
          password: this.registerPassword
        })
        
        if (result.code === 1) {
          this.$message.success('注册成功，请登录')
          this.showRegister = false
          // 清空注册表单
          this.registerUsername = ''
          this.registerPhone = ''
          this.registerPassword = ''
          this.confirmPassword = ''
        } else {
          this.$message.error(result.msg || '注册失败')
        }
      } catch (error) {
        console.error('注册失败:', error)
        this.$message.error('注册失败，请稍后重试')
      }
    },
    
    async addToCart() {
      // 检查登录状态
      if (!this.isLoggedIn) {
        this.showLoginDialog = true
        return
      }
      
      // 验证数量
      if (!this.quantity || this.quantity < 1) {
        this.$message.warning('请选择购买数量')
        return
      }
      
      console.log('加入购物车 - 商品ID:', this.product.id, '数量:', this.quantity)
      
      try {
        const result = await addToCart({
          userId: this.getUserId(),
          productId: this.product.id,
          quantity: parseInt(this.quantity)
        })
        
        if (result.code === 1) {
          this.$message.success('已加入购物车')
          // 通知购物车组件更新
          window.dispatchEvent(new Event('cartUpdated'))
        } else {
          this.$message.error(result.msg || '加入购物车失败')
        }
      } catch (error) {
        console.error('加入购物车失败:', error)
        this.$message.error('加入购物车失败，请稍后重试')
      }
    },
    async buyNow() {
      // 检查登录状态
      if (!this.isLoggedIn) {
        this.showLoginDialog = true
        return
      }

      // 验证数量
      if (!this.quantity || this.quantity < 1) {
        this.$message.warning('请选择购买数量')
        return
      }

      console.log('立即购买 - 商品ID:', this.product.id, '数量:', this.quantity)

      try {
        const userId = this.getUserId()
        const addressResult = await getDefaultAddress(userId)

        if (addressResult.code !== 1 || !addressResult.data) {
          this.$message.warning('请先设置默认收货地址')
          this.$router.push({ path: '/personal', query: { tab: 'address' } })
          return
        }

        const orderData = {
          userId,
          addressId: addressResult.data.id,
          couponIds: [],
          remark: '',
          items: [{ 
            productId: this.product.id, 
            quantity: parseInt(this.quantity) 
          }]
        }

        console.log('创建订单请求数据:', JSON.stringify(orderData))

        const orderResult = await createOrder(orderData)

        if (orderResult.code === 1 && orderResult.data) {
          this.$router.push(`/order/detail?id=${orderResult.data.id}`)
        } else {
          this.$message.error(orderResult.msg || '立即购买失败')
        }
      } catch (error) {
        console.error('立即购买失败:', error)
        this.$message.error('立即购买失败，请稍后重试')
      }
    },
    toggleFavorite() {
      // 检查登录状态
      if (!this.isLoggedIn) {
        this.showLoginDialog = true
        return
      }

      const userId = this.getUserId()
      const productId = this.product.id

      if (this.isFavorited) {
        // 取消收藏
        removeFavorite(userId, productId)
          .then(response => {
            if (response.code === 1) {
              this.isFavorited = false
              this.$message.success('已取消收藏')
            } else {
              this.$message.error(response.msg || '取消收藏失败')
            }
          })
          .catch(error => {
            console.error('取消收藏失败:', error)
            this.$message.error('取消收藏失败，请稍后重试')
          })
      } else {
        // 收藏商品
        addFavorite({ userId, productId })
          .then(response => {
            if (response.code === 1) {
              this.isFavorited = true
              this.$message.success('收藏成功')
            } else {
              this.$message.error(response.msg || '收藏失败')
            }
          })
          .catch(error => {
            console.error('收藏失败:', error)
            this.$message.error('收藏失败，请稍后重试')
          })
      }
    },

    // 加载收藏状态
    loadFavoriteStatus() {
      if (!this.isLoggedIn) return

      const userId = this.getUserId()
      const productId = this.product.id
      if (!userId || !productId) return

      isFavorite(userId, productId)
        .then(response => {
          if (response.code === 1) {
            this.isFavorited = response.data
          }
        })
        .catch(error => {
          console.error('查询收藏状态失败:', error)
        })
    },

    // 加载评论列表
    loadComments() {
      const productId = Number(this.product.id)
      if (!productId || isNaN(productId)) return

      // 加载评论列表
      getCommentList(productId)
        .then(response => {
          if (response.code === 1) {
            this.reviews = (response.data || []).map(c => ({
              id: c.id,
              name: c.username || '匿名用户',
              date: c.createTime,
              content: c.content,
              rating: c.rating,
              image: c.images || null
            }))
            this.reviewCount = this.reviews.length
          }
        })
        .catch(error => {
          console.error('加载评论列表失败:', error)
        })

      // 加载评论统计
      getCommentStats(productId)
        .then(response => {
          if (response.code === 1 && response.data) {
            this.averageRating = response.data[0] || 0
            this.reviewCount = response.data[1] || 0
          }
        })
        .catch(error => {
          console.error('加载评论统计失败:', error)
        })
    },

    // 提交评论
    submitComment() {
      if (!this.isLoggedIn) {
        this.showLoginDialog = true
        return
      }

      if (!this.commentForm.content.trim()) {
        this.$message.warning('请输入评论内容')
        return
      }

      const userId = this.getUserId()
      const commentData = {
        userId,
        productId: Number(this.product.id),
        rating: this.commentForm.rating,
        content: this.commentForm.content,
        images: this.commentForm.images || ''
      }

      addComment(commentData)
        .then(response => {
          if (response.code === 1) {
            this.$message.success('评论发表成功')
            this.showCommentForm = false
            this.commentForm = { rating: 5, content: '', images: '' }
            this.loadComments()
          } else {
            this.$message.error(response.msg || '评论发表失败')
          }
        })
        .catch(error => {
          console.error('评论发表失败:', error)
          this.$message.error('评论发表失败，请稍后重试')
        })
    },

    // 设置评论评分
    setCommentRating(rating) {
      this.commentForm.rating = rating
    }
  }
}
</script>

<style scoped>
@import '../styles/variables.css';

.product-detail-container {
  min-height: 100vh;
  background: var(--color-bg);
}

/* 顶部小字导航 */
.header-top {
  background: var(--color-bg);
  border-bottom: 1px solid var(--color-border-light);
  height: 32px;
  line-height: 32px;
}
.header-top-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  justify-content: space-between;
}
.left-links, .right-links {
  display: flex;
  align-items: center;
  gap: 10px;
}
.region-link, .theme-link, .action-link {
  font-size: 12px;
  color: #666;
  cursor: pointer;
}
.region-link:hover, .theme-link:hover, .action-link:hover {
  color: var(--color-primary);
}
.login-link {
  font-size: 12px;
  color: var(--color-primary);
  cursor: pointer;
  font-weight: 500;
}
.login-link:hover { text-decoration: underline; }
.register-link {
  font-size: 12px;
  color: #333;
  cursor: pointer;
}
.register-link:hover { color: var(--color-primary); }
.welcome-text {
  font-size: 12px;
  color: #333;
}
.divider {
  color: #ddd;
  font-size: 12px;
}

/* 顶部导航 */
.header-search {
  background: #f5f5f5;
  border-bottom: 1px solid #eee;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo-section {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-icon {
  width: 36px;
  height: 36px;
  background: #ff6b00;
  color: white;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
}

.logo {
  font-size: 24px;
  color: #ff6b00;
  margin: 0;
  font-weight: 700;
  cursor: pointer;
  transition: opacity 0.2s var(--ease-in-out);
}

.logo:hover { opacity: 0.9; }

.search-wrapper {
  flex: 1;
  display: flex;
  justify-content: center;
  padding-left: 100px;
}

.search-box {
  display: flex;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
  background: #fff;
  max-width: 500px;
  width: 100%;
}

.search-input {
  flex: 1;
  padding: 10px 16px;
  border: none;
  outline: none;
  font-size: var(--text-base);
  background: transparent;
}

.search-btn {
  padding: 10px 24px;
  background: #ff6b00;
  color: white;
  border: none;
  cursor: pointer;
  font-size: var(--text-base);
  font-weight: 500;
  transition: opacity 0.2s var(--ease-in-out);
}

.search-btn:hover { opacity: 0.9; }

/* 主体内容 */
.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
  display: flex;
  gap: 24px;
  align-items: flex-start; /* ✅ 关键:防止子项拉伸导致吸顶失效 */
}

/* 左侧区域 */
.left-section {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

/* 商品图片 */
.product-gallery {
  display: flex;
  gap: 12px;
  background: var(--color-bg-white);
  padding: 20px;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border-light);
  box-shadow: var(--shadow-xs);
}

.thumbnail-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.thumbnail-item {
  width: 64px;
  height: 64px;
  border: 2px solid var(--color-border-light);
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: border-color var(--duration-normal) var(--ease-in-out),
              box-shadow var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
}

.thumbnail-item:hover {
  border-color: var(--color-primary);
  transform: scale(1.05);
}

.thumbnail-item.active {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px rgba(255, 77, 79, 0.15);
}

.thumbnail-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.main-image-container {
  flex: 1;
  border-radius: var(--radius-lg);
  overflow: hidden;
  background: var(--color-bg);
  box-shadow: var(--shadow-sm);
}

.main-image {
  width: 100%;
  height: 560px;
  object-fit: cover;
}

/* 商品详情区域 */
.product-detail-section {
  background: var(--color-bg-white);
  border-radius: var(--radius-lg);
  padding: 24px;
  border: 1px solid var(--color-border-light);
}

.section-tabs {
  display: flex;
  gap: 0;
  border-bottom: 1px solid var(--color-border-light);
  margin-bottom: 24px;
}

.tab-item {
  font-size: var(--text-md);
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: 14px 24px;
  position: relative;
  font-weight: 500;
  transition: color var(--duration-normal) var(--ease-in-out);
}

.tab-item:hover {
  color: var(--color-text-primary);
}

.tab-item.active {
  color: var(--color-primary);
  font-weight: 600;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 24px;
  right: 24px;
  height: 3px;
  background: var(--color-primary);
  border-radius: 3px 3px 0 0;
}

.detail-images {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-image {
  width: 100%;
  border-radius: var(--radius-md);
}

/* 商品特色介绍 */
.feature-section {
  margin-bottom: 24px;
}

.feature-title {
  font-size: var(--text-xl);
  color: var(--color-text-primary);
  margin: 0 0 16px 0;
  font-weight: 600;
  padding-left: 12px;
  position: relative;
}
.feature-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 18px;
  background: var(--color-primary);
  border-radius: 2px;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 18px 20px;
  background: var(--color-bg);
  border-radius: var(--radius-md);
  border: 1px solid transparent;
  transition: all var(--duration-normal) var(--ease-in-out);
}
.feature-item:hover {
  background: var(--color-bg-white);
  border-color: var(--color-border-light);
  box-shadow: var(--shadow-sm);
}

.feature-content {
  flex: 1;
}

.feature-subtitle {
  font-size: var(--text-md);
  color: var(--color-text-primary);
  margin: 0 0 8px 0;
  font-weight: 600;
}

.feature-text {
  font-size: var(--text-base);
  color: var(--color-text-secondary);
  line-height: 1.6;
  margin: 0;
}

.feature-image-wrapper {
  width: 200px;
  flex-shrink: 0;
}

.feature-image {
  width: 100%;
  height: 140px;
  object-fit: cover;
  border-radius: var(--radius-md);
}

/* 评价区域 */
.review-summary {
  display: flex;
  gap: 32px;
  padding: 24px;
  background: var(--color-bg);
  border-radius: var(--radius-md);
  margin-bottom: 20px;
  border: 1px solid var(--color-border-light);
}

.summary-score {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 120px;
  gap: 4px;
}

.score-number {
  font-size: 42px;
  font-weight: 700;
  color: var(--color-text-primary);
  line-height: 1;
  letter-spacing: -1px;
}

.score-unit {
  font-size: var(--text-xs);
  color: var(--color-text-tertiary);
  margin-top: -2px;
}

.score-stars {
  display: flex;
  gap: 2px;
  margin: 4px 0;
}

.star {
  font-size: 16px;
  color: var(--color-border);
  transition: color var(--duration-fast) var(--ease-in-out);
}
.star.filled {
  color: #F59E0B;
}

.score-count {
  font-size: var(--text-xs);
  color: var(--color-text-tertiary);
}

.summary-distribution {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 6px;
}

.dist-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.dist-label {
  font-size: var(--text-xs);
  color: var(--color-text-tertiary);
  min-width: 28px;
  text-align: right;
}

.dist-bar {
  flex: 1;
  height: 8px;
  background: var(--color-border-light);
  border-radius: var(--radius-full);
  overflow: hidden;
}

.dist-fill {
  height: 100%;
  background: linear-gradient(90deg, #F59E0B, #FBBF24);
  border-radius: var(--radius-full);
  transition: width 0.6s var(--ease-out);
}

.dist-pct {
  font-size: var(--text-xs);
  color: var(--color-text-tertiary);
  min-width: 32px;
}

.review-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.review-tag {
  padding: 6px 16px;
  background: var(--color-bg);
  color: var(--color-text-secondary);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-full);
  font-size: var(--text-sm);
  cursor: pointer;
  transition: all var(--duration-normal) var(--ease-in-out);
}

.review-tag:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background: var(--color-primary-lighter);
}

.review-tag.active {
  background: var(--color-primary);
  color: white;
  border-color: var(--color-primary);
}

.review-tag:active {
  transform: scale(0.96);
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.review-item {
  padding: 20px 0;
  border-bottom: 1px solid var(--color-border-light);
  transition: background var(--duration-normal) var(--ease-in-out);
}
.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  gap: 12px;
  margin-bottom: 10px;
}

.reviewer-avatar {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-full);
  background: var(--color-primary-light);
  color: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--text-sm);
  font-weight: 600;
  flex-shrink: 0;
}

.reviewer-meta {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.reviewer-top {
  display: flex;
  align-items: center;
  gap: 10px;
}

.reviewer-name {
  font-size: var(--text-base);
  color: var(--color-text-primary);
  font-weight: 500;
}

.review-stars {
  display: flex;
  gap: 1px;
}

.star-sm {
  font-size: 13px;
  color: var(--color-border);
}
.star-sm.filled {
  color: #F59E0B;
}

.review-date {
  font-size: var(--text-xs);
  color: var(--color-text-tertiary);
}

.review-content {
  font-size: var(--text-base);
  color: var(--color-text-secondary);
  line-height: 1.7;
  margin: 0 0 12px;
}

.review-image img {
  width: 100px;
  height: 100px;
  border-radius: var(--radius-md);
  object-fit: cover;
  border: 1px solid var(--color-border-light);
}

/* 评论相关样式 */
.comment-action {
  margin-bottom: 20px;
}

.comment-btn {
  padding: 10px 24px;
  background: var(--color-primary);
  color: white;
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  font-size: var(--text-base);
  font-weight: 500;
  transition: background var(--duration-normal) var(--ease-in-out);
}

.comment-btn:hover {
  background: var(--color-primary-dark);
}

.comment-form {
  background: var(--color-bg);
  border-radius: var(--radius-md);
  padding: 20px;
  margin-bottom: 20px;
  border: 1px solid var(--color-border-light);
}

.form-rating {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}

.rating-label {
  font-size: var(--text-base);
  color: var(--color-text-primary);
  font-weight: 500;
}

.rating-stars {
  display: flex;
  gap: 4px;
}

.star-select {
  font-size: 24px;
  color: var(--color-border);
  cursor: pointer;
  transition: color var(--duration-fast) var(--ease-in-out);
}

.star-select.filled {
  color: #F59E0B;
}

.star-select:hover {
  color: #F59E0B;
}

.comment-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: var(--text-base);
  resize: vertical;
  outline: none;
  box-sizing: border-box;
  transition: border-color var(--duration-normal) var(--ease-in-out);
}

.comment-textarea:focus {
  border-color: var(--color-primary);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

.submit-btn {
  padding: 8px 20px;
  background: var(--color-primary);
  color: white;
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  font-size: var(--text-base);
  font-weight: 500;
  transition: background var(--duration-normal) var(--ease-in-out);
}

.submit-btn:hover {
  background: var(--color-primary-dark);
}

.no-reviews {
  text-align: center;
  padding: 40px 0;
  color: var(--color-text-tertiary);
  font-size: var(--text-base);
}



/* 右侧购买面板区域 - 固定铺满 */
.right-section {
  width: 420px;
  flex-shrink: 0;
  position: sticky;
  top: 80px;
  align-self: flex-start;
  margin-top: 0; /* 与左侧店铺栏顶部对齐 */
}

/* 购买面板 - 内部可滚动 */
.purchase-panel {
  padding-top: 0; /* 移除顶部空白，与店铺栏对齐 */
  background: var(--color-bg-white);
  border-radius: var(--color-radius-lg);
  border: 1px solid var(--color-border-light);
  overflow-y: auto;
  overflow-x: hidden;
}

/* 自定义滚动条样式 */
.purchase-panel::-webkit-scrollbar {
  width: 6px;
}

.purchase-panel::-webkit-scrollbar-track {
  background: var(--color-bg);
  border-radius: var(--radius-full);
}

.purchase-panel::-webkit-scrollbar-thumb {
  background: var(--color-border);
  border-radius: var(--radius-full);
}

.purchase-panel::-webkit-scrollbar-thumb:hover {
  background: var(--color-text-tertiary);
}

/* 店铺信息栏 */
.store-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  margin-bottom: 12px;
  background: var(--color-bg-white);
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border-light);
}

.store-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.store-actions {
  display: flex;
  gap: 10px;
}

.store-btn {
  padding: 8px 16px;
  border-radius: 20px;
  border: 1px solid var(--color-border);
  background: white;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.2s;
}

.customer-btn:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.enter-btn {
  background: var(--color-primary);
  color: white;
  border-color: var(--color-primary);
}

.enter-btn:hover {
  background: var(--color-primary-hover);
}

.store-avatar {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  background: var(--color-primary);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 700;
  flex-shrink: 0;
}

.store-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.store-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-primary);
}

.store-rating {
  display: flex;
  align-items: center;
  gap: 2px;
}

.rating-star {
  color: #FF6B00;
  font-size: 14px;
}

.rating-score {
  font-size: 14px;
  color: #FF6B00;
  font-weight: 600;
  margin-left: 4px;
}

.store-enter-btn {
  padding: 5px 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-full);
  background: white;
  font-size: 12px;
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}

.store-enter-btn:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

/* 商品标题 */
.product-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0;
  padding: 14px 20px 6px;
  line-height: 1.4;
}

/* 社会证明 */
.social-proof {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 20px 12px;
  font-size: 12px;
}

.proof-sales {
  color: var(--color-primary);
  font-weight: 600;
}

.proof-divider {
  color: var(--color-border);
  font-size: 10px;
}

.proof-review,
.proof-fans {
  color: var(--color-text-tertiary);
}

/* 价格面板 - 橙红渐变 */
.price-panel {
  margin: 0;
  padding: 16px 20px;
  background: linear-gradient(135deg, #FF6034, #EE0A24);
  color: white;
}

.price-main {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.price-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.price-label {
  font-size: 12px;
  opacity: 0.85;
}

.price-row {
  display: flex;
  align-items: baseline;
}

.price-symbol {
  font-size: 16px;
  font-weight: 700;
  margin-right: 2px;
}

.price-panel .price-value {
  font-size: 32px;
  font-weight: 800;
  color: white;
  letter-spacing: -1px;
  line-height: 1;
  font-family: 'DIN Alternate', 'Helvetica Neue', Arial, sans-serif;
}

.price-original {
  font-size: 12px;
  opacity: 0.65;
  text-decoration: line-through;
}

.price-activity {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.activity-badge {
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.25);
  border-radius: var(--radius-full);
  font-size: 13px;
  font-weight: 700;
  white-space: nowrap;
}

.activity-time {
  font-size: 11px;
  opacity: 0.8;
}

/* 促销标签 */
.promo-tags {
  display: flex;
  gap: 8px;
  padding: 10px 20px;
  border-bottom: 1px solid var(--color-border-light);
}

.promo-tag-item {
  padding: 3px 10px;
  background: #FFF0F0;
  color: var(--color-primary);
  border: 1px solid #FFDDDD;
  border-radius: var(--radius-xs);
  font-size: 12px;
  font-weight: 500;
}

/* 优惠券领取栏 */
.coupon-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 20px;
  background: #FFF8F8;
  cursor: pointer;
  transition: background 0.2s;
  border-bottom: 1px solid var(--color-border-light);
}

.coupon-bar:hover {
  background: #FFF0F0;
}

.coupon-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--color-primary);
  font-weight: 500;
}

.coupon-icon {
  width: 16px;
  height: 16px;
  color: var(--color-primary);
}

.coupon-arrow {
  color: var(--color-text-tertiary);
  font-size: 16px;
  font-weight: 700;
}

/* 服务保障 - 图标+竖线分隔 */
.service-info {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 0;
  padding: 10px 20px;
  border-bottom: 1px solid var(--color-border-light);
}

.service-row {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: var(--color-text-secondary);
}

.service-icon {
  width: 14px;
  height: 14px;
  color: var(--color-text-tertiary);
  flex-shrink: 0;
}

.service-divider {
  color: var(--color-border);
  font-size: 10px;
  margin: 0 10px;
}

/* 规格选择 - 增加内边距 */
.spec-section {
  padding: 16px 24px;
  margin: 0;
  border-bottom: 1px solid var(--color-border-light);
}

.spec-label {
  font-size: var(--text-base);
  color: var(--color-text-primary);
  margin-bottom: 10px;
  display: block;
  font-weight: 600;
}

.spec-options {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.spec-option {
  padding: 8px 20px;
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-full);
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
  cursor: pointer;
  transition: border-color var(--duration-normal) var(--ease-in-out),
              color var(--duration-normal) var(--ease-in-out),
              background var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
  text-align: center;
}

.spec-option:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.spec-option:active {
  transform: scale(0.96);
}

.spec-option.selected {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background: var(--color-primary-light);
  font-weight: 600;
  box-shadow: 0 0 0 1px rgba(255, 77, 79, 0.1);
}

/* 数量选择 - 增加内边距 */
.quantity-section {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 24px;
  margin: 0;
  border-bottom: 1px solid var(--color-border-light);
}

.quantity-label {
  font-size: var(--text-base);
  color: var(--color-text-primary);
  font-weight: 600;
  min-width: 40px;
}

.quantity-control {
  display: flex;
  align-items: center;
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-md);
  overflow: hidden;
}

.quantity-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: var(--color-bg);
  cursor: pointer;
  font-size: 18px;
  color: var(--color-text-primary);
  transition: background var(--duration-normal) var(--ease-in-out),
              color var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
  display: flex;
  align-items: center;
  justify-content: center;
}

.quantity-btn:hover:not(:disabled) {
  background: var(--color-primary);
  color: white;
}

.quantity-btn:active:not(:disabled) {
  transform: scale(0.9);
}

.quantity-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.quantity-input {
  width: 50px;
  height: 36px;
  border: none;
  border-left: 1px solid var(--color-border);
  border-right: 1px solid var(--color-border);
  text-align: center;
  font-size: var(--text-base);
  font-weight: 500;
  outline: none;
  -moz-appearance: textfield;
}

.quantity-input::-webkit-outer-spin-button,
.quantity-input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.stock-text {
  font-size: var(--text-sm);
  color: var(--color-text-tertiary);
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  gap: 10px;
  padding: 16px 20px;
  margin: 0;
}

.combined-buttons {
  display: flex;
  flex: 1;
  gap: 10px;
}

.btn-icon {
  width: 16px;
  height: 16px;
}

.cart-btn,
.buy-btn {
  flex: 1;
  padding: 0 16px;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  height: 42px;
}

.cart-btn {
  background: white;
  color: var(--color-primary);
  border: 1.5px solid var(--color-primary);
}

.cart-btn:hover {
  background: #FFF0F0;
  box-shadow: 0 2px 8px rgba(229, 57, 53, 0.15);
}

.cart-btn:active {
  transform: scale(0.97);
}

.buy-btn {
  background: linear-gradient(135deg, #FF6034, #EE0A24);
  color: white;
  box-shadow: 0 3px 10px rgba(238, 10, 36, 0.3);
}

.buy-btn:hover {
  box-shadow: 0 4px 14px rgba(238, 10, 36, 0.4);
  transform: translateY(-1px);
}

.buy-btn:active {
  transform: scale(0.97);
}

.favorite-btn {
  width: 42px;
  height: 42px;
  border: 1.5px solid var(--color-border);
  background: white;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  flex-shrink: 0;
}

.fav-icon {
  width: 18px;
  height: 18px;
  color: var(--color-text-tertiary);
  transition: all 0.2s;
}

.fav-icon.filled {
  color: var(--color-primary);
}

.favorite-btn:hover {
  border-color: var(--color-primary);
  transform: scale(1.08);
}

.favorite-btn:hover .fav-icon {
  color: var(--color-primary);
}

.favorite-btn:active {
  transform: scale(0.92);
}

/* 已选信息 - 增加内边距 */
.selected-info {
  padding: 16px 24px;
  margin: 0;
  border-top: 1px solid var(--color-border-light);
  background: var(--color-bg);
  font-size: var(--text-sm);
  color: var(--color-text-tertiary);
}

.selected-info span {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 用户信息区域 */
.user-info {
  display: flex;
  align-items: center;
  margin-left: 20px;
  min-width: 80px;
  justify-content: flex-end;
}

.user-nickname {
  color: var(--color-text-primary);
  font-size: var(--text-base);
  font-weight: 500;
  padding: 8px 16px;
  background: var(--color-bg);
  border-radius: var(--radius-full);
}

/* 登录弹窗 */
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
  padding: 4px;
  line-height: 1;
  transition: color var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
  border-radius: var(--radius-sm);
}

.close-btn:hover {
  color: var(--color-text-primary);
  transform: rotate(90deg);
}

.login-dialog-body {
  padding: 24px;
}

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
              border-color var(--duration-normal) var(--ease-in-out);
}

.login-tabs span:hover {
  color: var(--color-text-primary);
}

.login-tabs span.active {
  color: var(--color-primary);
  border-bottom-color: var(--color-primary);
  font-weight: 600;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

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

.code-input {
  display: flex;
  gap: 10px;
}

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
  color: white;
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

.login-icons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.login-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  cursor: pointer;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-full);
  transition: all 0.2s var(--ease-in-out);
}

.login-icon:hover {
  border-color: var(--color-border-hover);
  background: var(--color-bg);
  transform: translateY(-2px);
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
  transition: color 0.2s var(--ease-in-out);
}

.register-link:hover {
  color: var(--color-primary-dark);
}

/* ===== 视觉优化 ===== */

/* 顶部导航阴影增强 */
.header-search {
  box-shadow: 0 2px 12px rgba(29, 33, 41, 0.06);
}

/* 商品图片区域优化 */
.main-image-container {
  border: 1px solid var(--color-border-light);
}
</style>
