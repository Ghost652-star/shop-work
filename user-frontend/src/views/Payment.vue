<template>
  <div class="payment-container">
    <!-- 顶部导航栏 -->
    <div class="header-search" :class="{ 'header-search-fixed': isScrolled }">
      <div class="header-content">
        <div class="logo-section">
          <h1 class="logo" @click="goHome">电商平台</h1>
        </div>
        <div class="search-wrapper">
          <div class="search-box">
            <span class="search-icon-placeholder">🔍</span>
            <input type="text" placeholder="搜索商品" class="search-input" />
            <button class="search-btn">搜索</button>
          </div>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-content">
      <div class="payment-page">
        <!-- 步骤条 -->
        <div class="steps">
          <div class="step">
            <div class="step-icon">1</div>
            <div class="step-text">确认订单</div>
          </div>
          <div class="step active">
            <div class="step-icon">2</div>
            <div class="step-text">支付</div>
          </div>
          <div class="step">
            <div class="step-icon">3</div>
            <div class="step-text">完成</div>
          </div>
        </div>

        <!-- 订单信息 -->
        <div class="order-info-section">
          <h2 class="section-title">订单信息</h2>
          <div v-for="order in orders" :key="order.id" class="order-brief">
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">商家：</span>
                <span class="info-value">{{ order.merchantName }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">订单号：</span>
                <span class="info-value">{{ order.orderNo }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">金额：</span>
                <span class="info-value price">¥{{ parseFloat(order.payAmount || 0).toFixed(2) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 支付方式 -->
        <div class="payment-method-section">
          <h2 class="section-title">支付方式</h2>
          <div class="payment-methods">
            <div 
              v-for="method in paymentMethods" 
              :key="method.id"
              :class="['payment-method', { 'selected': selectedPaymentMethod === method.id }]"
              @click="selectedPaymentMethod = method.id"
            >
              <div class="method-icon">{{ method.icon }}</div>
              <div class="method-info">
                <h3 class="method-name">{{ method.name }}</h3>
                <p class="method-description">{{ method.description }}</p>
              </div>
              <div class="method-radio">
                <input 
                  type="radio" 
                  :value="method.id" 
                  v-model="selectedPaymentMethod"
                  @change="selectedPaymentMethod = method.id"
                />
              </div>
            </div>
          </div>
        </div>

        <!-- 支付二维码 -->
        <div v-if="showQRCode" class="qr-code-section">
          <h2 class="section-title">扫码支付</h2>
          <div class="qr-code-container">
            <div class="qr-code">
              <!-- 模拟二维码 -->
              <div class="qr-code-simulator">
                <div class="qr-code-grid">
                  <div v-for="i in 25" :key="i" class="qr-code-cell" :class="{ 'black': i % 2 === 0 }"></div>
                </div>
              </div>
              <p class="qr-code-tip">请使用{{ getPaymentMethodName(selectedPaymentMethod) }}扫描二维码支付</p>
              <p class="qr-code-expiry">支付有效期：{{ countdown }}秒</p>
            </div>
          </div>
        </div>

        <!-- 底部操作栏 -->
        <div class="action-bar">
          <div class="action-info">
            <div class="total-display">
              <span>应付金额：</span>
              <span class="price">¥{{ totalPayAmount.toFixed(2) }}</span>
            </div>
          </div>
          <div class="action-buttons">
            <button class="btn-cancel" @click="goBack">
              取消支付
            </button>
            <button class="btn-pay" @click="confirmPayment" :disabled="isProcessing">
              {{ isProcessing ? '处理中...' : '确认支付' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 支付成功弹窗 -->
    <el-dialog
      v-model="showSuccessDialog"
      title="支付成功"
      width="400px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <div class="success-content">
        <div class="success-icon">✅</div>
        <h3 class="success-title">支付成功</h3>
        <p class="success-message">您的订单已支付成功，感谢您的购买！</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button type="primary" @click="goToOrderList">查看订单</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 支付失败弹窗 -->
    <el-dialog
      v-model="showFailedDialog"
      title="支付失败"
      width="400px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <div class="failed-content">
        <div class="failed-icon">❌</div>
        <h3 class="failed-title">支付失败</h3>
        <p class="failed-message">{{ errorMessage }}</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showFailedDialog = false">取消</el-button>
          <el-button type="primary" @click="confirmPayment">重新支付</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { payOrder, getOrderList } from '../api/order'

export default {
  name: 'Payment',
  data() {
    return {
      isScrolled: false,
      batchNo: '',
      orders: [],
      totalPayAmount: 0,
      paymentMethods: [
        {
          id: 1,
          name: '支付宝',
          icon: '💳',
          description: '推荐使用支付宝快捷支付'
        },
        {
          id: 2,
          name: '微信支付',
          icon: '💚',
          description: '使用微信扫码支付'
        },
        {
          id: 3,
          name: '银行卡',
          icon: '💳',
          description: '使用银行卡支付'
        }
      ],
      selectedPaymentMethod: 1,
      showQRCode: false,
      countdown: 900,
      isProcessing: false,
      showSuccessDialog: false,
      showFailedDialog: false,
      errorMessage: ''
    }
  },
  mounted() {
    window.addEventListener('scroll', this.handleScroll)
    this.loadOrderData()
  },
  beforeDestroy() {
    window.removeEventListener('scroll', this.handleScroll)
    if (this.countdownTimer) {
      clearInterval(this.countdownTimer)
    }
  },
  methods: {
    handleScroll() {
      this.isScrolled = window.scrollY > 50
    },
    goHome() {
      this.$router.push('/')
    },
    goBack() {
      this.$router.back()
    },
    goToOrderList() {
      this.$router.push('/personal?tab=orders')
    },
    async loadOrderData() {
      const batchNo = this.$route.params.batchNo
      if (!batchNo) {
        this.$message.error('订单批次号不存在')
        this.$router.push('/')
        return
      }

      const userIdRaw = localStorage.getItem('userId')
      const userId = userIdRaw ? parseInt(userIdRaw) : NaN
      if (!userId || Number.isNaN(userId)) {
        this.$message.warning('请先登录')
        this.$router.push('/')
        return
      }

      this.batchNo = batchNo

      try {
        const result = await getOrderList(userId)
        if (result.code === 1 && result.data) {
          this.orders = result.data.filter(o => o.batchNo === batchNo && o.status === 0)
          if (this.orders.length === 0) {
            this.$message.error('未找到待付款订单')
            this.$router.push('/')
            return
          }
          this.totalPayAmount = this.orders.reduce((sum, o) => {
            const amount = typeof o.payAmount === 'number' ? o.payAmount : parseFloat(o.payAmount || 0)
            return sum + (Number.isFinite(amount) ? amount : 0)
          }, 0)
        }
      } catch (error) {
        console.error('加载订单失败:', error)
        this.$message.error('加载订单失败')
      }
    },

    getPaymentMethodName(id) {
      const method = this.paymentMethods.find(m => m.id === id)
      return method ? method.name : ''
    },
    startCountdown() {
      this.countdownTimer = setInterval(() => {
        if (this.countdown > 0) {
          this.countdown--
        } else {
          clearInterval(this.countdownTimer)
          this.showFailedDialog = true
          this.errorMessage = '支付超时，请重新支付'
        }
      }, 1000)
    },
    async confirmPayment() {
      this.isProcessing = true
      try {
        const userId = parseInt(localStorage.getItem('userId'))
        const result = await payOrder({
          batchNo: this.batchNo,
          userId,
          paymentType: this.getPaymentMethodName(this.selectedPaymentMethod)
        })
        if (result.code === 1) {
          setTimeout(() => {
            this.isProcessing = false
            this.showSuccessDialog = true
            if (this.countdownTimer) clearInterval(this.countdownTimer)
          }, 2000)
        } else {
          this.isProcessing = false
          this.errorMessage = result.msg || '支付失败'
          this.showFailedDialog = true
        }
      } catch (error) {
        this.isProcessing = false
        this.errorMessage = '网络错误，请稍后重试'
        this.showFailedDialog = true
      }
    }
  }
}
</script>

<style scoped>
@import '../styles/variables.css';

.payment-container {
  min-height: 100vh;
  background: var(--color-bg);
  padding-bottom: 80px;
}

/* 顶部导航栏 */
.header-search {
  background: var(--color-bg-white);
  padding: 15px 0;
  border-bottom: 1px solid var(--color-border-light);
  transition: all 0.3s var(--ease-in-out);
  z-index: 100;
}

.header-search-fixed {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  box-shadow: var(--shadow-sm);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  gap: 20px;
}

.logo-section {
  min-width: 140px;
  cursor: pointer;
}

.logo {
  font-size: var(--text-3xl);
  color: var(--color-primary);
  margin: 0;
  font-weight: 700;
  cursor: pointer;
  transition: opacity 0.2s var(--ease-in-out);
}

.logo:hover {
  opacity: 0.9;
}

.search-wrapper {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 15px;
}

.search-box {
  display: flex;
  border: 2px solid var(--color-border);
  border-radius: var(--radius-full);
  overflow: hidden;
  min-width: 400px;
  background: var(--color-bg-white);
  transition: all 0.2s var(--ease-in-out);
}

.search-box:focus-within {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(229, 57, 53, 0.1);
}

.search-icon-placeholder {
  padding: 10px 12px;
  background: var(--color-bg);
  border-right: 1px solid var(--color-border);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--text-lg);
}

.search-input {
  flex: 1;
  padding: 10px 15px;
  border: none;
  outline: none;
  font-size: var(--text-base);
}

.search-btn {
  padding: 10px 30px;
  background: var(--color-primary);
  color: white;
  border: none;
  cursor: pointer;
  font-size: var(--text-base);
  font-weight: 500;
  transition: opacity 0.2s var(--ease-in-out);
}

.search-btn:hover {
  opacity: 0.9;
}

/* 主体内容 */
.main-content {
  max-width: 1200px;
  margin: 24px auto;
  padding: 0 24px;
}

/* 支付页面 */
.payment-page {
  background: var(--color-bg-white);
  border-radius: var(--radius-md);
  padding: 32px;
  display: flex;
  flex-direction: column;
  gap: 28px;
  border: 1px solid var(--color-border-light);
}

/* 步骤条 */
.steps {
  display: flex;
  justify-content: center;
  gap: 60px;
  margin-bottom: 20px;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.step-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--color-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--text-lg);
  font-weight: 600;
  color: var(--color-text-secondary);
  border: 2px solid var(--color-border);
  transition: all 0.3s var(--ease-in-out);
}

.step.active .step-icon {
  background: var(--color-primary);
  color: white;
  border-color: var(--color-primary);
  box-shadow: 0 2px 8px rgba(229, 57, 53, 0.25);
}

.step-text {
  font-size: var(--text-base);
  color: var(--color-text-secondary);
  transition: all 0.3s var(--ease-in-out);
}

.step.active .step-text {
  color: var(--color-primary);
  font-weight: 600;
}

/* 区块标题 */
.section-title {
  font-size: var(--text-xl);
  color: var(--color-text-primary);
  margin: 0 0 16px 0;
  font-weight: 600;
  padding-bottom: 12px;
  border-bottom: 1px solid var(--color-border-light);
  position: relative;
  padding-left: 14px;
}
.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 18px;
  background: var(--color-primary);
  border-radius: 2px;
}

/* 订单信息 */
.order-info-section {
  padding: 20px 0;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  gap: 10px;
  align-items: center;
}

.info-label {
  font-size: var(--text-base);
  color: var(--color-text-tertiary);
}

.info-value {
  font-size: var(--text-base);
  color: var(--color-text-primary);
  font-weight: 500;
}

.info-value.price {
  color: var(--color-primary);
  font-size: var(--text-lg);
  font-weight: 600;
}

/* 支付方式 */
.payment-method-section {
  padding: 20px 0;
}

.payment-methods {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.payment-method {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s var(--ease-in-out);
  background: var(--color-bg-white);
}

.payment-method:hover {
  border-color: var(--color-primary);
  box-shadow: var(--shadow-xs);
}

.payment-method.selected {
  border-color: var(--color-primary);
  background: var(--color-primary-light);
}

.method-icon {
  font-size: 32px;
  min-width: 40px;
}

.method-info {
  flex: 1;
}

.method-name {
  font-size: var(--text-lg);
  color: var(--color-text-primary);
  margin: 0 0 4px 0;
  font-weight: 600;
}

.method-description {
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
  margin: 0;
}

.method-radio {
  display: flex;
  align-items: center;
}

.method-radio input[type="radio"] {
  width: 20px;
  height: 20px;
  cursor: pointer;
}

/* 二维码 */
.qr-code-section {
  padding: 20px 0;
}

.qr-code-container {
  display: flex;
  justify-content: center;
  padding: 40px;
  background: var(--color-bg);
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border-light);
}

.qr-code {
  text-align: center;
}

.qr-code-simulator {
  width: 200px;
  height: 200px;
  background: white;
  border: 1px solid var(--color-border);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
}

.qr-code-grid {
  width: 160px;
  height: 160px;
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  grid-template-rows: repeat(5, 1fr);
  gap: 4px;
}

.qr-code-cell {
  width: 100%;
  height: 100%;
  background: white;
  border: 1px solid var(--color-border);
}

.qr-code-cell.black {
  background: black;
}

.qr-code-tip {
  font-size: var(--text-base);
  color: var(--color-text-primary);
  margin: 0 0 10px 0;
  font-weight: 500;
}

.qr-code-expiry {
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
  margin: 0;
}

/* 底部操作栏 */
.action-bar {
  position: sticky;
  bottom: 20px;
  background: var(--color-bg-white);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md);
  padding: 20px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: var(--shadow-md);
  z-index: 10;
}

.action-info {
  display: flex;
  gap: 30px;
  align-items: center;
}

.total-display {
  font-size: var(--text-base);
  color: var(--color-text-secondary);
}

.total-display .price {
  font-size: var(--text-3xl);
  color: var(--color-primary);
  font-weight: 700;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.btn-cancel,
.btn-pay {
  padding: 12px 32px;
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  font-size: var(--text-base);
  font-weight: 500;
  transition: all 0.2s var(--ease-in-out);
}

.btn-cancel {
  background: var(--color-bg-white);
  color: var(--color-text-secondary);
  border: 1px solid var(--color-border);
}

.btn-cancel:hover {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background: var(--color-primary-light);
}

.btn-pay {
  background: var(--color-primary);
  color: white;
  box-shadow: 0 2px 8px rgba(229, 57, 53, 0.2);
}

.btn-pay:hover:not(:disabled) {
  background: var(--color-primary-hover);
  box-shadow: 0 4px 14px rgba(229, 57, 53, 0.3);
  transform: translateY(-1px);
}

.btn-pay:disabled {
  background: var(--color-text-tertiary);
  cursor: not-allowed;
}

/* 弹窗样式 */
.success-content,
.failed-content {
  text-align: center;
  padding: 20px 0;
}

.success-icon,
.failed-icon {
  font-size: 60px;
  margin-bottom: 20px;
}

.success-title,
.failed-title {
  font-size: var(--text-2xl);
  color: var(--color-text-primary);
  margin: 0 0 10px 0;
  font-weight: 600;
}

.success-message,
.failed-message {
  font-size: var(--text-base);
  color: var(--color-text-secondary);
  margin: 0;
  line-height: 1.6;
}

.order-brief {
  padding: 12px 0;
  border-bottom: 1px solid var(--color-border-light);
}
.order-brief:last-child {
  border-bottom: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }

  .action-bar {
    flex-direction: column;
    gap: 15px;
  }

  .action-buttons {
    width: 100%;
  }

  .btn-cancel,
  .btn-pay {
    flex: 1;
  }

  .payment-method {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .method-radio {
    align-self: flex-end;
  }
}
</style>