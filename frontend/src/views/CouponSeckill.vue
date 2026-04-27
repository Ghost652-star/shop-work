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
.coupon-center-page {
  min-height: 100vh;
  background: #FFF9F9;
  padding-bottom: 40px;
}

/* 顶部横幅 */
.header-banner {
  background: linear-gradient(135deg, #FF3030 0%, #FF6B8A 100%);
  padding: 24px 0;
  box-shadow: 0 2px 8px rgba(255, 48, 48, 0.2);
}

.banner-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.banner-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.banner-title {
  color: white;
  font-size: 36px;
  margin: 0;
  font-weight: 700;
  letter-spacing: 2px;
}

.banner-subtitle {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  margin: 0;
}

.banner-search {
  display: flex;
  gap: 0;
  background: white;
  border-radius: 24px;
  overflow: hidden;
  padding: 4px;
  max-width: 400px;
}

.search-input {
  flex: 1;
  padding: 10px 16px;
  border: none;
  outline: none;
  font-size: 14px;
  background: transparent;
}

.search-btn {
  padding: 10px 24px;
  background: linear-gradient(135deg, #FF3030 0%, #FF6B8A 100%);
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.2s;
}

.search-btn:hover {
  opacity: 0.9;
  transform: scale(1.05);
}

/* 分类标签 */
.category-tabs {
  background: white;
  padding: 16px 0;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.tabs-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  gap: 24px;
}

.tab-item {
  padding: 8px 16px;
  color: #666;
  font-size: 15px;
  cursor: pointer;
  border-radius: 20px;
  transition: all 0.2s;
  font-weight: 500;
}

.tab-item:hover {
  color: #FF3030;
  background: #FFF0F0;
}

.tab-item.active {
  color: #FF3030;
  background: linear-gradient(135deg, #FF3030 0%, #FF6B8A 100%);
  color: white;
}

/* 优惠券列表 */
.coupon-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 16px;
}

.coupon-item {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  display: flex;
  position: relative;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border: 1px solid #FFE0E0;
}

.coupon-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(255, 48, 48, 0.15);
  border-color: #FF3030;
}

.coupon-item.grabbed {
  opacity: 0.7;
  background: #FAFAFA;
}

.coupon-image {
  width: 100px;
  flex-shrink: 0;
  background: #FFF0F0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.coupon-image img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
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
  color: #FF3030;
}

.currency {
  font-size: 16px;
  font-weight: 600;
  margin-right: 2px;
}

.price {
  font-size: 32px;
  font-weight: 700;
  line-height: 1;
}

.condition {
  font-size: 12px;
  color: #FF3030;
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
  font-size: 15px;
  color: #333;
  margin: 0;
  font-weight: 600;
}

.coupon-range {
  font-size: 12px;
  color: #999;
  margin: 0;
  line-height: 1.4;
}

.coupon-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #999;
}

.divider {
  color: #DDD;
}

.coupon-timer {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 4px;
}

.timer-label {
  font-size: 12px;
  color: #999;
}

.timer-value {
  font-size: 13px;
  color: #FF3030;
  font-weight: 600;
  font-family: monospace;
  background: #FFF0F0;
  padding: 2px 6px;
  border-radius: 4px;
}

.grab-button {
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 20px;
  background: linear-gradient(135deg, #FF3030 0%, #FF6B8A 100%);
  color: white;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  margin-top: 12px;
}

.grab-button:hover:not(.disabled) {
  opacity: 0.9;
  transform: translateY(-2px);
}

.grab-button.disabled {
  background: linear-gradient(135deg, #E0E0E0 0%, #CCCCCC 100%);
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
  background: linear-gradient(135deg, #FF6B8A 0%, #FF3030 100%);
  color: white;
  padding: 4px 20px;
  font-size: 12px;
  font-weight: 600;
  transform: rotate(45deg) translate(12px, -12px);
  border-radius: 0 0 4px 0;
}

.empty-tip {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 14px;
}

/* 领取成功提示 */
.toast {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 16px 32px;
  border-radius: 8px;
  font-size: 16px;
  z-index: 1000;
  animation: fadeInOut 2s ease-in-out;
}

.toast.success {
  background: rgba(76, 175, 80, 0.9);
}

@keyframes fadeInOut {
  0% { opacity: 0; transform: translate(-50%, -50%) scale(0.8); }
  20% { opacity: 1; transform: translate(-50%, -50%) scale(1); }
  80% { opacity: 1; transform: translate(-50%, -50%) scale(1); }
  100% { opacity: 0; transform: translate(-50%, -50%) scale(0.8); }
}

/* 用户信息区域 */
.user-info {
  display: flex;
  align-items: center;
  margin-left: 20px;
}

.login-btn {
  padding: 8px 16px;
  background: white;
  color: #FF3030;
  border: 1px solid #FF3030;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.2s;
}

.login-btn:hover {
  background: #FF3030;
  color: white;
}

.user-nickname {
  color: white;
  font-size: 14px;
  font-weight: 500;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 20px;
  backdrop-filter: blur(10px);
}

/* 登录弹窗 */
.login-dialog {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.login-dialog-content {
  background: #fff;
  border-radius: 8px;
  width: 400px;
  max-width: 90%;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.login-dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
}

.login-dialog-header h3 {
  margin: 0;
  color: #333;
  font-size: 16px;
  font-weight: 500;
}

.close-btn {
  font-size: 20px;
  color: #999;
  cursor: pointer;
  padding: 4px;
  line-height: 1;
  transition: color 0.2s;
}

.close-btn:hover {
  color: #666;
}

.login-dialog-body {
  padding: 20px;
}

.login-tabs {
  display: flex;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}

.login-tabs span {
  flex: 1;
  text-align: center;
  padding: 10px 0;
  cursor: pointer;
  color: #666;
  font-size: 14px;
  border-bottom: 2px solid transparent;
  transition: all 0.2s;
}

.login-tabs span.active {
  color: #e43932;
  border-bottom-color: #e43932;
  font-weight: 500;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.phone-input {
  display: flex;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
}

.country-code {
  padding: 10px 12px;
  background: #f5f5f5;
  border-right: 1px solid #ddd;
  color: #666;
  font-size: 14px;
}

.code-input {
  display: flex;
  gap: 10px;
}

.form-input {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.form-input:focus {
  border-color: #e43932;
}

.get-code-btn {
  padding: 0 16px;
  background: #f5f5f5;
  color: #e43932;
  border: 1px solid #e43932;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.get-code-btn:hover {
  background: #e43932;
  color: #fff;
}

.login-btn {
  padding: 12px;
  background: #e43932;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}

.login-btn:hover {
  background: #c81623;
}

.other-login {
  text-align: center;
  padding: 16px 0;
  border-top: 1px solid #eee;
  margin-top: 8px;
}

.other-login p {
  margin: 0 0 12px 0;
  color: #999;
  font-size: 14px;
}

.login-icons {
  display: flex;
  justify-content: center;
  gap: 24px;
}

.login-icon {
  font-size: 14px;
  color: #666;
  cursor: pointer;
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  transition: all 0.2s;
}

.login-icon:hover {
  color: #e43932;
  border-color: #e43932;
}

.login-tip {
  text-align: center;
  font-size: 12px;
  color: #999;
  margin: 8px 0 0 0;
}

.register-link {
  color: #e43932;
  cursor: pointer;
  font-weight: 500;
  transition: color 0.2s;
}

.register-link:hover {
  color: #c81623;
}
</style>
