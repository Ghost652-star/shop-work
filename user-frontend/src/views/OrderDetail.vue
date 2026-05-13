<template>
  <div class="order-detail-container">
    <!-- 顶部导航栏 -->
    <div class="header-search" :class="{ 'header-search-fixed': isScrolled }">
      <div class="header-content">
        <div class="logo-section">
          <h1 class="logo" @click="goHome">电商平台</h1>
        </div>
      </div>
    </div>

    <!-- 主体内容 -->
    <div class="main-content">
      <div class="order-detail-page">
        <!-- 订单状态 -->
        <div class="order-status" :class="'status-' + order.status">
          <div class="status-icon">{{ statusIcon }}</div>
          <h1 class="status-title">{{ statusText }}</h1>
          <p class="status-description">{{ statusDescription }}</p>
        </div>

        <!-- 订单信息 -->
        <div class="order-info-section">
          <h2 class="section-title">订单信息</h2>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">订单编号：</span>
              <span class="info-value">{{ order.orderNo }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">下单时间：</span>
              <span class="info-value">{{ order.createTime }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">支付方式：</span>
              <span class="info-value">{{ order.paymentType }}</span>
            </div>
          </div>
        </div>

        <!-- 收货地址 -->
        <div class="address-section">
          <h2 class="section-title">收货地址</h2>
          <div class="address-content">
            <div class="receiver-info">
              <span class="receiver-name">{{ order.receiverName }}</span>
              <span class="receiver-phone">{{ order.receiverPhone }}</span>
            </div>
            <p class="address-detail">
              {{ order.receiverProvince }}{{ order.receiverCity }}{{ order.receiverDistrict }}{{ order.receiverDetailAddress }}
            </p>
          </div>
        </div>

        <!-- 商品列表 -->
        <div class="product-list-section">
          <h2 class="section-title">商品信息</h2>
          <div class="product-list">
            <div 
              v-for="item in order.items" 
              :key="item.id" 
              class="product-item"
            >
              <!-- 商品图片 -->
              <div class="product-image">
                <img :src="item.productImage" :alt="item.productName" />
              </div>

              <!-- 商品信息 -->
              <div class="product-info">
                <h3 class="product-name">{{ item.productName }}</h3>
                <p class="product-subtitle">{{ item.productDescription }}</p>
                <div class="product-price">
                  <span class="price-label">单价：</span>
                  <span class="price">¥{{ item.price }}</span>
                </div>
              </div>

              <!-- 数量 -->
              <div class="product-quantity">
                <span class="quantity-label">数量：</span>
                <span class="quantity-value">x{{ item.quantity }}</span>
              </div>

              <!-- 小计 -->
              <div class="product-subtotal">
                <span class="subtotal-label">小计：</span>
                <span class="subtotal-price">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 金额明细 -->
        <div class="amount-section">
          <div class="amount-row">
            <span class="amount-label">商品总额：</span>
            <span class="amount-value">¥{{ order.totalAmount }}</span>
          </div>
          <div class="amount-row">
            <span class="amount-label">运费：</span>
            <span class="amount-value">¥{{ order.freightAmount }}</span>
          </div>
          <div class="amount-row total">
            <span class="amount-label">实付款：</span>
            <span class="amount-value">¥{{ order.payAmount }}</span>
          </div>
        </div>

        <!-- 底部操作栏 -->
        <div class="action-bar">
          <div class="action-info">
            <div class="total-display">
              <span>应付金额：</span>
              <span class="price">¥{{ order.payAmount }}</span>
            </div>
          </div>
          <div class="action-buttons">
            <button
              v-if="order.status === 0 || order.status === 1 || order.status === 2"
              class="btn-cancel"
              @click="cancelOrder"
            >
              取消订单
            </button>
            <button
              v-if="order.status === 0"
              class="btn-pay"
              @click="payOrder"
            >
              去支付
            </button>
            <button
              v-if="order.status === 2"
              class="btn-confirm"
              @click="confirmReceipt"
            >
              确认收货
            </button>
            <button
              v-if="(order.status === 2 || order.status === 3) && order.afterSaleStatus !== 1"
              class="btn-aftersale"
              @click="goToAfterSale"
            >
              申请售后
            </button>
            <button
              v-if="order.afterSaleStatus === 1"
              class="btn-aftersale"
              @click="goToAfterSaleDetail"
            >
              查看售后
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getOrderDetail, cancelOrder, confirmOrder } from '../api/order'
import { getAfterSaleList } from '../api/afterSale'

export default {
  name: 'OrderDetail',
  data() {
    return {
      isScrolled: false,
      order: {
        id: 0,
        orderNo: '',
        status: 0, // 0-待付款，1-待发货，2-待收货，3-已完成，4-已取消
        createTime: '',
        paymentType: '在线支付',
        receiverName: '',
        receiverPhone: '',
        receiverProvince: '',
        receiverCity: '',
        receiverDistrict: '',
        receiverDetailAddress: '',
        totalAmount: 0,
        freightAmount: 0,
        payAmount: 0,
        items: []
      }
    }
  },
  mounted() {
    window.addEventListener('scroll', this.handleScroll);
    this.loadOrderData();
  },
  beforeDestroy() {
    window.removeEventListener('scroll', this.handleScroll);
  },
  computed: {
    statusIcon() {
      const icons = {
        0: '💳', // 待付款
        1: '📦', // 待发货
        2: '🚚', // 待收货
        3: '✅', // 已完成
        4: '❌'  // 已取消
      };
      return icons[this.order.status] || '📋';
    },
    statusText() {
      const texts = {
        0: '待付款',
        1: '待发货',
        2: '待收货',
        3: '已完成',
        4: '已取消'
      };
      return texts[this.order.status] || '未知状态';
    },
    statusDescription() {
      const descriptions = {
        0: '请尽快完成支付',
        1: '商家正在准备发货',
        2: '商品已发出，请注意查收',
        3: '交易已完成，感谢您的支持',
        4: '订单已取消'
      };
      return descriptions[this.order.status] || '';
    }
  },
  methods: {
    handleScroll() {
      this.isScrolled = window.scrollY > 50;
    },
    goHome() {
      this.$router.push('/');
    },
    async loadOrderData() {
      const orderId = this.$route.query.id || this.$route.params.id;
      if (!orderId) {
        this.$message.error('订单ID不存在');
        this.$router.push('/');
        return;
      }
      
      const userId = localStorage.getItem('userId');
      if (!userId) {
        this.$message.error('用户未登录');
        this.$router.push('/');
        return;
      }
      
      try {
        const result = await getOrderDetail(orderId, userId);
        if (result.code === 1) {
          this.order = result.data;
        } else {
          this.$message.error(result.msg || '加载订单失败');
        }
      } catch (error) {
        console.error('加载订单失败:', error);
        this.$message.error('加载订单失败，请稍后重试');
      }
    },
    async cancelOrder() {
      this.$confirm('确定要取消该订单吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        const userId = localStorage.getItem('userId');
        if (!userId) {
          this.$message.error('用户未登录');
          this.$router.push('/');
          return;
        }
        
        try {
          const result = await cancelOrder(this.order.id, userId);
          if (result.code === 1) {
            this.order.status = 4; // 已取消
            this.$message.success('订单已取消');
          } else {
            this.$message.error(result.msg || '取消订单失败');
          }
        } catch (error) {
          console.error('取消订单失败:', error);
          this.$message.error('取消订单失败，请稍后重试');
        }
      }).catch(() => {});
    },
    payOrder() {
      this.$router.push(`/payment/${this.order.id}`);
    },
    async confirmReceipt() {
      try {
        await this.$confirm('确定已收到商品吗？', '确认收货', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'info'
        })
        const userId = localStorage.getItem('userId')
        const result = await confirmOrder(this.$route.query.id, userId)
        if (result.code === 1) {
          this.$message.success('已确认收货')
          this.loadOrderData()
        } else {
          this.$message.error(result.msg || '确认收货失败')
        }
      } catch (e) {
        if (e !== 'cancel') {
          console.error('确认收货失败:', e)
        }
      }
    },
    goToAfterSale() {
      this.$router.push({ path: '/after-sale/apply', query: { orderId: this.$route.query.id } })
    },
    async goToAfterSaleDetail() {
      const userId = localStorage.getItem('userId')
      try {
        const result = await getAfterSaleList(userId)
        if (result.code === 1) {
          const found = (result.data || []).find(a => a.orderId === this.order.id)
          if (found) {
            this.$router.push({ path: '/after-sale/detail', query: { id: found.id } })
          }
        }
      } catch (e) {
        console.error(e)
      }
    }
  }
}
</script>

<style scoped>
@import '../styles/variables.css';

.order-detail-container {
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

/* 主体内容 */
.main-content {
  max-width: 1200px;
  margin: 24px auto;
  padding: 0 24px;
}

/* 订单详情页面 */
.order-detail-page {
  background: var(--color-bg-white);
  border-radius: var(--radius-md);
  padding: 32px;
  display: flex;
  flex-direction: column;
  gap: 28px;
  border: 1px solid var(--color-border-light);
}

/* 订单状态 - 去渐变化 */
.order-status {
  padding: 32px;
  border-radius: var(--radius-md);
  text-align: center;
  color: white;
}

.order-status.status-0 {
  background: var(--color-primary); /* ✅ 纯色替代渐变 */
}

.order-status.status-1 {
  background: var(--color-info);
}

.order-status.status-2 {
  background: #597EF7;
}

.order-status.status-3 {
  background: var(--color-success);
}

.order-status.status-4 {
  background: var(--color-text-tertiary);
}

.status-icon {
  font-size: 60px;
  margin-bottom: 16px;
}

.status-title {
  font-size: var(--text-3xl);
  margin: 0 0 10px 0;
  font-weight: 600;
}

.status-description {
  font-size: var(--text-base);
  margin: 0;
  opacity: 0.9;
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

/* 收货地址 */
.address-section {
  padding: 20px 0;
}

.address-content {
  padding: 20px;
  background: var(--color-bg);
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border-light);
}

.receiver-info {
  display: flex;
  gap: 20px;
  margin-bottom: 10px;
}

.receiver-name {
  font-size: var(--text-lg);
  color: var(--color-text-primary);
  font-weight: 600;
}

.receiver-phone {
  font-size: var(--text-base);
  color: var(--color-text-secondary);
}

.address-detail {
  font-size: var(--text-base);
  color: var(--color-text-secondary);
  line-height: 1.6;
  margin: 0;
}

/* 商品列表 */
.product-list-section {
  padding: 20px 0;
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 商品项 */
.product-item {
  display: grid;
  grid-template-columns: 100px 1fr auto auto;
  gap: 20px;
  padding: 20px;
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md);
  align-items: center;
  transition: all 0.2s var(--ease-in-out);
  background: var(--color-bg-white);
}

.product-item:hover {
  border-color: var(--color-primary);
  box-shadow: var(--shadow-xs);
}

/* 商品图片 */
.product-image {
  width: 100px;
  height: 100px;
  border-radius: var(--radius-md);
  overflow: hidden;
  background: var(--color-bg);
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 商品信息 */
.product-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.product-name {
  font-size: var(--text-lg);
  color: var(--color-text-primary);
  margin: 0;
  font-weight: 600;
}

.product-subtitle {
  font-size: var(--text-xs);
  color: var(--color-text-tertiary);
  margin: 0;
}

.product-price {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 8px;
}

.price-label {
  font-size: var(--text-xs);
  color: var(--color-text-tertiary);
}

.price {
  font-size: var(--text-2xl);
  color: var(--color-primary);
  font-weight: 600;
}

/* 数量 */
.product-quantity {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.quantity-label,
.quantity-value {
  font-size: var(--text-base);
  color: var(--color-text-secondary);
}

.quantity-value {
  font-weight: 500;
}

/* 小计 */
.product-subtotal {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.subtotal-label,
.subtotal-price {
  font-size: var(--text-base);
  color: var(--color-text-secondary);
}

.subtotal-price {
  font-size: var(--text-2xl);
  color: var(--color-primary);
  font-weight: 600;
}

/* 金额明细 */
.amount-section {
  padding: 20px;
  background: var(--color-bg);
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border-light);
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.amount-row {
  display: flex;
  justify-content: flex-end;
  gap: 20px;
  align-items: center;
}

.amount-label {
  font-size: var(--text-base);
  color: var(--color-text-secondary);
}

.amount-value {
  font-size: var(--text-lg);
  color: var(--color-text-primary);
  font-weight: 500;
  min-width: 100px;
  text-align: right;
}

.amount-row.total {
  padding-top: 12px;
  border-top: 1px solid var(--color-border);
  margin-top: 12px;
}

.amount-row.total .amount-label {
  font-size: var(--text-lg);
  font-weight: 600;
  color: var(--color-text-primary);
}

.amount-row.total .amount-value {
  font-size: var(--text-3xl);
  color: var(--color-primary);
  font-weight: 700;
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
}

.btn-pay:hover {
  opacity: 0.9;
}

.btn-pay:active {
  transform: scale(0.98);
}

.btn-confirm {
  padding: 12px 32px;
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  font-size: var(--text-base);
  font-weight: 500;
  background: #4CAF50;
  color: white;
}

.btn-confirm:hover {
  background: #43A047;
}

.btn-aftersale {
  padding: 12px 32px;
  border: 1px solid var(--color-primary);
  border-radius: var(--radius-md);
  cursor: pointer;
  font-size: var(--text-base);
  font-weight: 500;
  background: white;
  color: var(--color-primary);
}

.btn-aftersale:hover {
  background: #fff5f5;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-item {
    grid-template-columns: 80px 1fr;
    grid-template-rows: auto auto auto;
    gap: 15px;
  }

  .product-image {
    width: 80px;
    height: 80px;
  }

  .product-quantity,
  .product-subtotal {
    grid-column: 1 / -1;
    justify-self: center;
  }

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
}
</style>
