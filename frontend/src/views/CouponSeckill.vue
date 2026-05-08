<template>
  <div class="coupon-center-page">
    <!-- 顶部横幅 -->
    <div class="header-banner">
      <div class="banner-content">
        <div class="banner-left">
          <h1 class="banner-title">领券中心</h1>
          <p class="banner-subtitle">超值优惠 券你享受</p>
        </div>
        <div class="banner-search">
          <input 
            type="text" 
            v-model="searchText"
            placeholder="搜索优惠券" 
            class="search-input"
          />
          <button class="search-btn" @click="handleSearch">搜索</button>
        </div>
        <div class="user-info">
          <span v-if="!isLoggedIn" class="login-btn" @click="showLoginDialog = true">登录</span>
          <span v-else class="user-nickname">{{ userNickname }}</span>
        </div>
      </div>
    </div>

    <!-- 分类标签 -->
    <div class="category-tabs">
      <div class="tabs-container">
        <span 
          v-for="(tab, index) in categoryTabs" 
          :key="index"
          :class="['tab-item', { active: activeTab === index }]"
          @click="switchTab(index)"
        >
          {{ tab }}
        </span>
      </div>
    </div>

    <!-- 优惠券列表 -->
    <div class="coupon-list">
      <div 
        v-for="coupon in filteredCoupons" 
        :key="coupon.id" 
        class="coupon-item"
        :class="{ 'grabbed': coupon.status === 0 || coupon.claimed }"
      >
        <div class="coupon-image">
          <img :src="coupon.image" :alt="coupon.name" />
        </div>
        
        <div class="coupon-content">
          <div class="coupon-main">
            <div class="coupon-left">
              <div class="price-section">
                <span class="currency">¥</span>
                <span class="price">{{ coupon.discountAmount }}</span>
              </div>
              <div class="condition">
                {{ coupon.minSpend > 0 ? '满' + coupon.minSpend + '元可用' : '无门槛' }}
              </div>
            </div>
            
            <div class="coupon-right">
              <h3 class="coupon-name">满{{ coupon.minSpend }}减{{ coupon.discountAmount }}</h3>
              <p class="coupon-range">{{ coupon.description }}</p>
              <div class="coupon-meta">
                <span class="stock">剩余{{ coupon.stock }}张</span>
                <span class="divider">|</span>
                <span class="time">有效期{{ coupon.validPeriod }}天</span>
              </div>
              <div class="coupon-timer" v-if="coupon.status === 1 && !coupon.claimed">
                <span class="timer-label">距离结束：</span>
                <span class="timer-value">{{ coupon.countdown }}</span>
              </div>
            </div>
          </div>
          
          <button 
            class="grab-button" 
            :class="{ 
              'disabled': coupon.status === 0 || coupon.stock === 0 || coupon.buttonDisabled || coupon.claimed,
              'grabbing': coupon.grabbing
            }"
            @click="grabCoupon(coupon)"
            :disabled="coupon.status === 0 || coupon.stock === 0 || coupon.buttonDisabled || coupon.claimed"
          >
            <span v-if="coupon.grabbing">抢券中...</span>
            <span v-else-if="coupon.claimed">已领取</span>
            <span v-else>{{ coupon.buttonText || '立即抢' }}</span>
          </button>
          
          <div class="corner-mark" v-if="coupon.claimed">已领取</div>
        </div>
      </div>

      <!-- 无优惠券提示 -->
      <div v-if="filteredCoupons.length === 0" class="empty-tip">
        <p>暂无优惠券</p>
      </div>
    </div>

    <!-- 领取成功提示 -->
    <div v-if="showSuccessToast" class="toast success">
      <span>✓ 领取成功！</span>
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
import { getCouponList, receiveCoupon, getUserCouponList } from '../api/coupon'
import { login, register } from '../api/user'

export default {
  name: 'CouponSeckill',
  data() {
    return {
      searchText: '',
      activeTab: 0,
      categoryTabs: ['全部', '食品生鲜', '数码电器', '户外运动', '美妆护肤'],
      categoryMap: {
        1: '食品生鲜',
        2: '数码电器',
        3: '户外运动',
        4: '美妆护肤'
      },
      showSuccessToast: false,
      allCoupons: [],
      timers: {},
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
      confirmPassword: ''
      ,
      // 当前用户已领取过的优惠券ID（用于刷新后也能置灰/禁用领取按钮）
      claimedCouponIds: []
    }
  },
  computed: {
    filteredCoupons() {
      if (this.activeTab === 0) {
        return this.allCoupons
      }
      return this.allCoupons.filter(coupon => coupon.categoryId === this.activeTab)
    }
  },
  mounted() {
    this.loadCoupons()
    this.checkLoginStatus()
  },
  beforeDestroy() {
    this.clearAllTimers()
  },
  methods: {
    async loadCoupons() {
      try {
        const result = await getCouponList()
        if (result.code === 1) {
          this.allCoupons = result.data.map(coupon => ({
            ...coupon,
            category_id: coupon.categoryId,
            discount_amount: coupon.discountAmount,
            min_purchase: coupon.minSpend,
            valid_days: coupon.validPeriod,
            start_time: new Date(coupon.startTime).getTime(),
            end_time: new Date(coupon.endTime).getTime(),
            grabbing: false,
            claimed: false,
            buttonText: '立即抢',
            buttonDisabled: false
          }))
          this.startAllCountdowns()

          // 如果用户已登录，把“已领取”的券标记出来（刷新页面也生效）
          if (this.isLoggedIn) {
            await this.loadClaimedCoupons()
            this.applyClaimedState()
          }
        }
      } catch (error) {
        console.error('加载优惠券失败:', error)
      }
    },

    async loadClaimedCoupons() {
      const userIdRaw = localStorage.getItem('userId')
      const userId = userIdRaw ? parseInt(userIdRaw) : NaN
      if (!userId || Number.isNaN(userId)) {
        this.claimedCouponIds = []
        return
      }

      try {
        const result = await getUserCouponList(userId)
        if (result.code === 1 && Array.isArray(result.data)) {
          // 后端返回 UserCouponVO 列表：[{couponId: ...}, ...]
          this.claimedCouponIds = result.data
            .map(uc => uc && uc.couponId)
            .filter(id => id != null)
            .map(id => Number(id))
        } else {
          this.claimedCouponIds = []
        }
      } catch (e) {
        console.error('加载用户已领取优惠券失败:', e)
        this.claimedCouponIds = []
      }
    },

    applyClaimedState() {
      if (!Array.isArray(this.allCoupons) || this.allCoupons.length === 0) return
      const claimedSet = new Set(this.claimedCouponIds || [])
      this.allCoupons.forEach(coupon => {
        coupon.claimed = claimedSet.has(Number(coupon.id))
      })
    },

    switchTab(index) {
      this.activeTab = index
    },
    
    getCategoryDescription(categoryId) {
      const descriptions = {
        1: '限食品生鲜部分商品可用',
        2: '限数码电器部分商品可用',
        3: '限户外运动部分商品可用',
        4: '限美妆护肤部分商品可用'
      }
      return descriptions[categoryId] || '全场通用'
    },
    
    getCategoryTitle(categoryId) {
      const categoryName = this.categoryMap[categoryId] || '通用'
      return `${categoryName}专享券`
    },
    
    startAllCountdowns() {
      this.allCoupons.forEach(coupon => {
        if (coupon.status === 1 && !coupon.claimed) {
          this.startCountdown(coupon)
        }
      })
    },
    
    startCountdown(coupon) {
      if (this.timers[coupon.id]) {
        clearInterval(this.timers[coupon.id])
      }
      
      const timer = setInterval(() => {
        const now = new Date().getTime()
        const startTime = coupon.start_time
        const endTime = coupon.end_time
        
        let distance = 0
        let isStartCountdown = false
        
        if (now < startTime) {
          distance = startTime - now
          isStartCountdown = true
        } else if (now < endTime) {
          distance = endTime - now
          isStartCountdown = false
        } else {
          coupon.status = 0
          coupon.countdown = '已结束'
          coupon.buttonText = '已结束'
          coupon.buttonDisabled = true
          clearInterval(timer)
          this.timers[coupon.id] = null
          return
        }
        
        const hours = Math.floor(distance / (1000 * 60 * 60))
        const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60))
        const seconds = Math.floor((distance % (1000 * 60)) / 1000)
        
        coupon.countdown = 
          `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
        
        coupon.countdownLabel = isStartCountdown ? '距离开抢' : '距离结束'
        
        if (isStartCountdown) {
          coupon.buttonText = '即将开抢'
          coupon.buttonDisabled = true
        } else {
          coupon.buttonText = coupon.stock === 0 ? '已抢光' : '立即抢'
          coupon.buttonDisabled = coupon.stock === 0
        }
      }, 1000)
      
      this.timers[coupon.id] = timer
    },
    
    clearAllTimers() {
      Object.values(this.timers).forEach(timer => {
        if (timer) clearInterval(timer)
      })
    },
    
    handleSearch() {
      console.log('搜索:', this.searchText)
    },
    
    checkLoginStatus() {
      const savedLoginState = localStorage.getItem('isLoggedIn')
      const savedNickname = localStorage.getItem('userNickname')
      if (savedLoginState === 'true') {
        this.isLoggedIn = true
        this.userNickname = savedNickname || '用户'

        // 登录状态恢复时，同步“已领取”状态
        this.loadClaimedCoupons().then(() => this.applyClaimedState())
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

          // 登录成功后，立刻把已领取的券置灰
          await this.loadClaimedCoupons()
          this.applyClaimedState()

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
    
    async grabCoupon(coupon) {
      if (coupon.grabbing || coupon.claimed || coupon.status === 0 || coupon.stock === 0 || coupon.buttonDisabled) return
      
      // 检查登录状态
      if (!this.isLoggedIn) {
        this.showLoginDialog = true
        return
      }
      
      coupon.grabbing = true
      
      try {
        const userId = localStorage.getItem('userId')
        if (!userId) {
          this.showLoginDialog = true
          return
        }
        
        // 调用后端领取优惠券接口
        const result = await receiveCoupon({
          userId: parseInt(userId),
          couponId: coupon.id
        })
        
        if (result.code === 1) {
          coupon.claimed = true
          // 同步到本地 claimed 列表，避免本次会话内状态不一致
          if (!this.claimedCouponIds.includes(Number(coupon.id))) {
            this.claimedCouponIds.push(Number(coupon.id))
          }
          coupon.stock = Math.max(0, coupon.stock - 1)
          this.showSuccessToast = true
          
          if (this.timers[coupon.id]) {
            clearInterval(this.timers[coupon.id])
            this.timers[coupon.id] = null
          }
          
          setTimeout(() => {
            this.showSuccessToast = false
          }, 2000)
        } else {
          alert(result.msg || '领取失败')
        }
      } catch (error) {
        console.error('领取优惠券失败:', error)
        alert('领取失败，请稍后重试')
      } finally {
        coupon.grabbing = false
      }
    }
  }
}
</script>

<style scoped>
@import '../styles/variables.css';

.coupon-center-page {
  min-height: 100vh;
  background: var(--color-bg);
  padding-bottom: 40px;
}

/* 顶部横幅 */
.header-banner {
  background: var(--color-primary);
  padding: 28px 0;
  position: relative;
  overflow: hidden;
}

.header-banner::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -10%;
  width: 400px;
  height: 400px;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 50%;
}

.header-banner::after {
  content: '';
  position: absolute;
  bottom: -60%;
  left: 10%;
  width: 300px;
  height: 300px;
  background: rgba(255, 255, 255, 0.04);
  border-radius: 50%;
}

.banner-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
}

.banner-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.banner-title {
  color: white;
  font-size: 32px;
  margin: 0;
  font-weight: 700;
  letter-spacing: 1px;
}

.banner-subtitle {
  color: rgba(255, 255, 255, 0.85);
  font-size: var(--text-base);
  margin: 0;
}

.banner-search {
  display: flex;
  background: white;
  border-radius: var(--radius-full);
  overflow: hidden;
  padding: 4px;
  max-width: 380px;
}

.search-input {
  flex: 1;
  padding: 10px 16px;
  border: none;
  outline: none;
  font-size: var(--text-base);
  background: transparent;
}

.search-input::placeholder {
  color: var(--color-text-tertiary);
}

.search-btn {
  padding: 10px 24px;
  background: var(--color-primary-dark);
  color: white;
  border: none;
  border-radius: var(--radius-full);
  cursor: pointer;
  font-size: var(--text-base);
  font-weight: 600;
  transition: opacity var(--duration-normal) var(--ease-in-out);
}

.search-btn:hover {
  opacity: 0.9;
}

/* 分类标签 */
.category-tabs {
  background: var(--color-bg-white);
  padding: 16px 0;
  margin-bottom: 20px;
  border-bottom: 1px solid var(--color-border-light);
}

.tabs-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  gap: 8px;
}

.tab-item {
  padding: 8px 20px;
  color: var(--color-text-secondary);
  font-size: var(--text-md);
  cursor: pointer;
  border-radius: var(--radius-full);
  transition: all var(--duration-normal) var(--ease-in-out);
  font-weight: 500;
}

.tab-item:hover {
  color: var(--color-primary);
  background: var(--color-primary-light);
}

.tab-item.active {
  color: white;
  background: var(--color-primary);
}

/* 优惠券列表 */
.coupon-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(360px, 1fr));
  gap: 16px;
}

.coupon-item {
  background: var(--color-bg-white);
  border-radius: var(--radius-lg);
  overflow: hidden;
  display: flex;
  position: relative;
  transition: transform var(--duration-normal) var(--ease-out),
              box-shadow var(--duration-normal) var(--ease-out),
              border-color var(--duration-normal) var(--ease-in-out);
  box-shadow: var(--shadow-xs);
  border: 1px solid var(--color-border-light);
}

.coupon-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
  border-color: var(--color-primary);
}

.coupon-item.grabbed {
  opacity: 0.6;
  background: var(--color-bg-stripe);
}

.coupon-image {
  width: 100px;
  flex-shrink: 0;
  background: var(--color-primary-light);
  display: flex;
  align-items: center;
  justify-content: center;
}

.coupon-image img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: var(--radius-md);
}

.coupon-content {
  flex: 1;
  padding: 16px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
}

.coupon-main {
  display: flex;
  gap: 16px;
}

.coupon-left {
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 80px;
}

.price-section {
  display: flex;
  align-items: baseline;
  color: var(--color-primary);
}

.currency {
  font-size: var(--text-lg);
  font-weight: 600;
  margin-right: 2px;
}

.price {
  font-size: 28px;
  font-weight: 700;
  line-height: 1;
}

.condition {
  font-size: var(--text-xs);
  color: var(--color-primary);
  margin-top: 6px;
  white-space: nowrap;
}

.coupon-right {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.coupon-name {
  font-size: var(--text-md);
  color: var(--color-text-primary);
  margin: 0;
  font-weight: 600;
}

.coupon-range {
  font-size: var(--text-xs);
  color: var(--color-text-tertiary);
  margin: 0;
  line-height: 1.4;
}

.coupon-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: var(--text-xs);
  color: var(--color-text-tertiary);
}

.divider {
  color: var(--color-border);
}

.coupon-timer {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 4px;
}

.timer-label {
  font-size: var(--text-xs);
  color: var(--color-text-tertiary);
}

.timer-value {
  font-size: var(--text-sm);
  color: var(--color-primary);
  font-weight: 600;
  font-family: 'Courier New', monospace;
  background: var(--color-primary-light);
  padding: 2px 8px;
  border-radius: var(--radius-sm);
}

.grab-button {
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: var(--radius-full);
  background: var(--color-primary);
  color: white;
  font-size: var(--text-base);
  font-weight: 600;
  cursor: pointer;
  transition: background var(--duration-normal) var(--ease-in-out),
              box-shadow var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
  margin-top: 12px;
}

.grab-button:hover:not(.disabled) {
  background: var(--color-primary-dark);
  box-shadow: var(--shadow-primary);
}

.grab-button:active:not(.disabled) {
  transform: scale(0.98);
}

.grab-button.disabled {
  background: #D0D0D0;
  cursor: not-allowed;
}

.grab-button.grabbing {
  opacity: 0.7;
  cursor: wait;
}

.corner-mark {
  position: absolute;
  top: 0;
  right: 0;
  background: var(--color-primary);
  color: white;
  padding: 4px 20px;
  font-size: var(--text-xs);
  font-weight: 600;
  transform: rotate(45deg) translate(12px, -12px);
  border-radius: 0 0 var(--radius-sm) 0;
}

.empty-tip {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 20px;
  color: var(--color-text-tertiary);
  font-size: var(--text-base);
}

/* 领取成功提示 */
.toast {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.75);
  color: white;
  padding: 16px 32px;
  border-radius: var(--radius-lg);
  font-size: var(--text-lg);
  z-index: 1000;
  animation: fadeInOut 2s ease-in-out;
  backdrop-filter: blur(8px);
}

.toast.success {
  background: rgba(67, 160, 71, 0.9);
}

@keyframes fadeInOut {
  0% { opacity: 0; transform: translate(-50%, -50%) scale(0.9); }
  15% { opacity: 1; transform: translate(-50%, -50%) scale(1); }
  80% { opacity: 1; transform: translate(-50%, -50%) scale(1); }
  100% { opacity: 0; transform: translate(-50%, -50%) scale(0.9); }
}

/* 用户信息区域 */
.user-info {
  display: flex;
  align-items: center;
  margin-left: 20px;
}

.login-btn {
  padding: 8px 20px;
  background: rgba(255, 255, 255, 0.15);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: var(--radius-full);
  cursor: pointer;
  font-size: var(--text-base);
  font-weight: 500;
  transition: background var(--duration-normal) var(--ease-in-out),
              border-color var(--duration-normal) var(--ease-in-out);
}

.login-btn:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.6);
}

.user-nickname {
  color: white;
  font-size: var(--text-base);
  font-weight: 500;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.12);
  border-radius: var(--radius-full);
}

/* 登录弹窗 */
.login-dialog {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--color-bg-overlay);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.login-dialog-content {
  background: var(--color-bg-white);
  border-radius: var(--radius-lg);
  width: 420px;
  max-width: 90%;
  box-shadow: var(--shadow-lg);
  overflow: hidden;
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
  border-radius: var(--radius-md);
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
  transition: background var(--duration-normal) var(--ease-in-out),
              color var(--duration-normal) var(--ease-in-out);
}

.get-code-btn:hover {
  background: var(--color-primary);
  color: white;
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
              box-shadow var(--duration-normal) var(--ease-in-out);
}

.login-btn:hover {
  background: var(--color-primary-dark);
  box-shadow: var(--shadow-primary);
}

.other-login {
  text-align: center;
  padding: 20px 0;
  border-top: 1px solid var(--color-border-light);
  margin-top: 8px;
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
  border-radius: var(--radius-md);
  transition: color var(--duration-normal) var(--ease-in-out),
              border-color var(--duration-normal) var(--ease-in-out),
              background var(--duration-normal) var(--ease-in-out);
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
  margin: 8px 0 0 0;
}

.register-link {
  color: var(--color-primary);
  cursor: pointer;
  font-weight: 500;
  transition: color var(--duration-normal) var(--ease-in-out);
}

.register-link:hover {
  color: var(--color-primary-dark);
}
</style>
