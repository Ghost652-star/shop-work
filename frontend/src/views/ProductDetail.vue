<template>
  <div class="product-detail-container">
    <!-- 购物车侧边栏 -->
    <CartSidebar ref="cartSidebar" />

    <!-- 顶部导航栏 -->
    <div class="header-search">
      <div class="header-content">
        <div class="logo-section">
          <h1 class="logo" @click="goHome">电商平台</h1>
        </div>
        <div class="search-wrapper">
          <div class="search-box">
            <input type="text" placeholder="搜索商品" class="search-input" />
            <button class="search-btn">搜索</button>
          </div>
        </div>
        <div class="user-info">
          <span v-if="!isLoggedIn" class="login-btn" @click="showLoginDialog = true">登录</span>
          <span v-else class="user-nickname">{{ userNickname }}</span>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-content">
      <!-- 左侧：商品图片和详情 -->
      <div class="left-section">
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
            <!-- 商品详细介绍 -->
            <div class="feature-section">
              <h3 class="feature-title">为你推荐</h3>
              <div class="feature-list">
                <div v-for="(feature, index) in productFeatures" :key="index" class="feature-item">
                  <div class="feature-content">
                    <h4 class="feature-subtitle">{{ feature.title }}</h4>
                    <p class="feature-text">{{ feature.content }}</p>
                  </div>
                  <div class="feature-image-wrapper">
                    <img :src="feature.image" :alt="feature.title" class="feature-image" />
                  </div>
                </div>
              </div>
            </div>
            
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
          
          <!-- 副标题 -->
          <p class="product-subtitle">{{ product.description }}</p>

          <!-- 价格区域 -->
          <div class="price-panel">
            <div class="current-price">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ product.price }}</span>
              <span class="original-price">¥{{ (product.price * 1.3).toFixed(2) }}</span>
            </div>
          </div>

          <!-- 促销信息 -->
          <div class="promotion-info">
            <div class="promotion-tag">超级 88</div>
            <span class="promotion-text">官方立减{{ (product.price * 0.1).toFixed(1) }}元</span>
          </div>

          <!-- 服务保障 -->
          <div class="service-info">
            <div class="service-item">
              <span class="service-icon">⚡</span>
              <span>24 小时发货</span>
            </div>
            <div class="service-item">
              <span class="service-icon">📦</span>
              <span>免运费</span>
            </div>
            <div class="service-item">
              <span class="service-icon">✓</span>
              <span>7 天无理由退换</span>
            </div>
            <div class="service-item coupon-entry" @click="goToCouponPage">
              <span class="service-icon">🎫</span>
              <span>领取优惠券</span>
              <span class="coupon-arrow">›</span>
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
                <span class="cart-icon">🛒</span>
                <span>加入购物车</span>
              </button>
              <button class="buy-btn" @click="buyNow">
                <span>立即购买</span>
              </button>
            </div>
            <button class="favorite-btn" @click="toggleFavorite" :class="{ active: isFavorited }">
              <span class="favorite-icon">{{ isFavorited ? '★' : '☆' }}</span>
              <span>收藏</span>
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
                  <span class="login-icon">微信</span>
                  <span class="login-icon">微博</span>
                  <span class="login-icon">QQ</span>
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
import { login, register } from '../api/user'
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
      const savedLoginState = localStorage.getItem('isLoggedIn')
      const savedNickname = localStorage.getItem('userNickname')
      if (savedLoginState === 'true') {
        this.isLoggedIn = true
        this.userNickname = savedNickname || '用户'
      }
    },
    
    async handleLogin() {
      try {
        let result
        if (this.loginTab === 'password') {
          // 用户名密码登录
          result = await login({
            username: this.username,
            password: this.password
          })
        } else {
          // 手机号验证码登录
          result = await login({
            username: this.phone,
            password: this.verificationCode
          })
        }
        
        if (result.code === 1) {
          const user = result.data
          this.isLoggedIn = true
          this.userNickname = user.nickname
          localStorage.setItem('isLoggedIn', 'true')
          localStorage.setItem('userNickname', user.nickname)
          localStorage.setItem('userId', user.id)
          this.showLoginDialog = false
          alert('登录成功')
        } else {
          alert(result.msg || '登录失败')
        }
      } catch (error) {
        console.error('登录失败:', error)
        alert('登录失败，请稍后重试')
      }
    },
    
    async handleRegister() {
      // 验证表单
      if (!this.registerUsername) {
        alert('请输入用户名')
        return
      }
      if (!this.registerPhone) {
        alert('请输入手机号')
        return
      }
      if (!this.registerPassword) {
        alert('请输入密码')
        return
      }
      if (this.registerPassword !== this.confirmPassword) {
        alert('两次输入的密码不一致')
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
          alert('注册成功，请登录')
          this.showRegister = false
          // 清空注册表单
          this.registerUsername = ''
          this.registerPhone = ''
          this.registerPassword = ''
          this.confirmPassword = ''
        } else {
          alert(result.msg || '注册失败')
        }
      } catch (error) {
        console.error('注册失败:', error)
        alert('注册失败，请稍后重试')
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
          userId: parseInt(localStorage.getItem('userId')),
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
        const userId = parseInt(localStorage.getItem('userId'))
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

      const userId = localStorage.getItem('userId')
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

      const userId = localStorage.getItem('userId')
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

      const userId = parseInt(localStorage.getItem('userId'))
      const commentData = {
        userId,
        productId: Number(this.product.id),
        orderId: 0, // 临时值，实际应从订单关联获取
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

/* 顶部导航 */
.header-search {
  background: var(--color-bg-white);
  border-bottom: 1px solid var(--color-border-light);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 16px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo-section {
  flex-shrink: 0;
}

.logo {
  font-size: var(--text-3xl);
  color: var(--color-primary);
  margin: 0;
  font-weight: 700;
  cursor: pointer;
  transition: opacity 0.2s var(--ease-in-out);
}

.logo:hover { opacity: 0.9; }

.search-wrapper {
  flex: 1;
  max-width: 480px;
  margin-left: auto;
}

.search-box {
  display: flex;
  border: 2px solid var(--color-border);
  border-radius: var(--radius-full);
  overflow: hidden;
  background: var(--color-bg-white);
  transition: all 0.2s var(--ease-in-out);
  box-shadow: var(--shadow-xs);
}

.search-box:focus-within {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(229, 57, 53, 0.1);
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
  background: var(--color-primary);
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
  gap: 20px;
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
  box-shadow: 0 0 0 2px rgba(229, 57, 53, 0.15);
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
  top: 0;  /* ← 改为 0，从视口顶部开始 */
  height: 100vh;  /* ← 新增：高度撑满整个视口 */
}

/* 购买面板 - 内部可滚动 */
.purchase-panel {
  height: 100%;  /* ← 改为 100%，填满父容器 */
  padding-top: 72px;  /* ← 新增：顶部留出导航栏高度的空间 */
  box-sizing: border-box;  /* ← 新增：让 padding 不增加总高度 */
  background: var(--color-bg-white);
  overflow-y: auto;
  overflow-x: hidden;
  border-left: 1px solid var(--color-border-light);  /* ← 只保留左边框作为分隔 */
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

/* 商品标题 - 增加内边距 */
.product-title {
  font-size: var(--text-xl);
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0;
  padding: 20px 24px 12px;
  line-height: 1.4;
}

/* 商品副标题 - 增加内边距 */
.product-subtitle {
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
  margin: 0;
  padding: 0 24px 16px;
  line-height: 1.5;
}

/* 价格面板 - 增加内边距 */
.price-panel {
  padding: 20px 24px;
  border-bottom: 1px solid var(--color-border-light);
  background: var(--color-primary-lighter);
  margin: 0 -24px;
}

.current-price {
  display: flex;
  align-items: flex-end;
  gap: 6px;
  margin-bottom: 6px;
}

.price-symbol {
  font-size: var(--text-xl);
  font-weight: 700;
  color: var(--color-primary);
  line-height: 1;
  margin-bottom: 2px;
}

.price-value {
  font-size: 36px;
  font-weight: 800;
  color: var(--color-primary);
  letter-spacing: -1px;
  line-height: 1;
}

.original-price {
  font-size: var(--text-sm);
  color: var(--color-text-tertiary);
  text-decoration: line-through;
  margin-left: 8px;
  line-height: 1;
}

/* 促销信息 - 增加内边距 */
.promotion-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 24px;
  background: var(--color-primary-lighter);
  border-radius: 0;
  margin: 0;
  border-bottom: 1px solid var(--color-border-light);
}

.promotion-tag {
  padding: 3px 10px;
  background: var(--color-primary);
  color: white;
  border-radius: var(--radius-xs, 2px);
  font-size: 11px;
  font-weight: 700;
  white-space: nowrap;
}

.promotion-text {
  font-size: var(--text-sm);
  color: var(--color-primary);
  font-weight: 500;
}

/* 服务保障 - 增加内边距 */
.service-info {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 16px 24px;
  border-bottom: 1px solid var(--color-border-light);
}

.service-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: var(--text-xs);
  color: var(--color-text-secondary);
  background: var(--color-bg);
  padding: 4px 10px;
  border-radius: var(--radius-full);
}

.service-icon {
  font-size: var(--text-base);
}

.coupon-entry {
  cursor: pointer;
  color: var(--color-primary);
  transition: background var(--duration-normal) var(--ease-in-out),
              color var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
  background: var(--color-primary-light);
}

.coupon-entry:hover {
  background: var(--color-primary);
  color: white;
  transform: scale(1.02);
}

.coupon-entry:active {
  transform: scale(0.98);
}

.coupon-arrow {
  margin-left: 2px;
  font-weight: 700;
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
  box-shadow: 0 0 0 1px rgba(229, 57, 53, 0.1);
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

/* 操作按钮 - 增加内边距 */
.action-buttons {
  display: flex;
  gap: 10px;
  padding: 20px 24px;
  margin: 0;
  border-bottom: 1px solid var(--color-border-light);
}

.combined-buttons {
  display: flex;
  flex: 1;
  gap: 10px;
}

.cart-btn,
.buy-btn {
  flex: 1;
  padding: 12px 16px;
  border: none;
  border-radius: var(--radius-full);
  cursor: pointer;
  font-size: var(--text-base);
  font-weight: 600;
  transition: background var(--duration-slow) var(--ease-out),
              box-shadow var(--duration-slow) var(--ease-out),
              transform var(--duration-fast) var(--ease-in-out);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  height: 46px;
  letter-spacing: 0.5px;
}

.cart-btn {
  background: white;
  color: var(--color-primary);
  border: 1.5px solid var(--color-primary);
}

.cart-btn:hover {
  background: var(--color-primary-light);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(229, 57, 53, 0.15);
}

.cart-btn:active {
  transform: scale(0.97);
}

.buy-btn {
  background: var(--color-primary);
  color: white;
  box-shadow: 0 4px 12px rgba(229, 57, 53, 0.3);
}

.buy-btn:hover {
  background: var(--color-primary-dark);
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(229, 57, 53, 0.4);
}

.buy-btn:active {
  transform: scale(0.97);
  box-shadow: 0 2px 8px rgba(229, 57, 53, 0.3);
}

.favorite-btn {
  width: 46px;
  height: 46px;
  border: 1.5px solid var(--color-border);
  background: white;
  border-radius: var(--radius-full);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1px;
  transition: border-color var(--duration-normal) var(--ease-in-out),
              background var(--duration-normal) var(--ease-in-out),
              color var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
  padding: 4px;
  flex-shrink: 0;
}

.favorite-btn:hover {
  border-color: var(--color-primary);
  background: var(--color-primary-light);
  transform: scale(1.05);
}

.favorite-btn:active {
  transform: scale(0.92);
}

.favorite-btn.active {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background: var(--color-primary-light);
}

.favorite-icon {
  font-size: var(--text-lg);
}

.favorite-btn span:last-child {
  font-size: 10px;
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
}

.login-btn {
  padding: 8px 16px;
  background: var(--color-primary);
  color: white;
  border: none;
  border-radius: var(--radius-full);
  cursor: pointer;
  font-size: var(--text-base);
  font-weight: 500;
  transition: opacity 0.2s var(--ease-in-out);
}

.login-btn:hover { opacity: 0.9; }

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
  font-size: var(--text-base);
  color: var(--color-text-secondary);
  cursor: pointer;
  padding: 8px 16px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  transition: all 0.2s var(--ease-in-out);
}

.login-icon:hover {
  color: var(--color-primary);
  border-color: var(--color-primary);
  background: var(--color-primary-light);
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
</style>
