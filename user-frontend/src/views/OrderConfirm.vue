<template>
  <div class="order-confirm-container">
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
      <div class="order-confirm-page">
        <!-- 步骤条 -->
        <div class="steps">
          <div class="step active">
            <div class="step-icon">1</div>
            <div class="step-text">确认订单</div>
          </div>
          <div class="step">
            <div class="step-icon">2</div>
            <div class="step-text">支付</div>
          </div>
          <div class="step">
            <div class="step-icon">3</div>
            <div class="step-text">完成</div>
          </div>
        </div>

        <!-- 收货地址 -->
        <div class="address-section">
          <h2 class="section-title">收货地址</h2>
          <div class="address-content" v-if="selectedAddress">
            <div class="receiver-info">
              <span class="receiver-name">{{ selectedAddress.name }}</span>
              <span class="receiver-phone">{{ selectedAddress.phone }}</span>
              <span v-if="selectedAddress.isDefault" class="default-tag">默认</span>
            </div>
            <p class="address-detail">
              {{ selectedAddress.province }}{{ selectedAddress.city }}{{ selectedAddress.district }}{{ selectedAddress.detailAddress }}
            </p>
            <button class="change-address-btn" @click="showAddressDialog = true">
              更换地址
            </button>
          </div>
          <div class="address-empty" v-else>
            <div class="empty-icon">📍</div>
            <p>请选择收货地址</p>
            <button class="add-address-btn" @click="showAddressDialog = true">
              新增地址
            </button>
          </div>
        </div>

        <!-- 商品列表 -->
        <div class="product-list-section">
          <h2 class="section-title">商品信息</h2>
          <div class="product-list">
            <div 
              v-for="item in selectedItems" 
              :key="item.cartItemId || item.productId"
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
                  <span class="price">¥{{ item.price }}</span>
                </div>
              </div>

              <!-- 数量 -->
              <div class="product-quantity">
                <span class="quantity-value">x{{ item.quantity }}</span>
              </div>

              <!-- 小计 -->
              <div class="product-subtotal">
                <span class="subtotal-price">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 优惠券 -->
        <div class="coupon-section">
          <h2 class="section-title">优惠券</h2>
          <div class="coupon-content">
            <div class="coupon-selected" @click="openCouponDialog">
              <div class="coupon-info">
                <span class="coupon-label">已选择优惠券：</span>
                <span class="coupon-value" v-if="selectedCoupons.length > 0">
                  {{ selectedCoupons.length }}张，抵扣¥{{ totalCouponAmount.toFixed(2) }}
                </span>
                <span class="coupon-value" v-else>未选择</span>
              </div>
              <span class="arrow">▶</span>
            </div>
          </div>
        </div>

        <!-- 订单备注 -->
        <div class="remark-section">
          <h2 class="section-title">订单备注</h2>
          <input 
            type="text" 
            v-model="orderRemark" 
            placeholder="请输入订单备注（选填）" 
            class="remark-input"
          />
        </div>

        <!-- 金额明细 -->
        <div class="amount-section">
          <div class="amount-row">
            <span class="amount-label">商品总额：</span>
            <span class="amount-value">¥{{ totalAmount.toFixed(2) }}</span>
          </div>
          <div class="amount-row">
            <span class="amount-label">运费：</span>
            <span class="amount-value">¥{{ freightAmount.toFixed(2) }}</span>
          </div>
          <div class="amount-row">
            <span class="amount-label">优惠券抵扣：</span>
            <span class="amount-value">-¥{{ totalCouponAmount.toFixed(2) }}</span>
          </div>
          <div class="amount-row total">
            <span class="amount-label">实付款：</span>
            <span class="amount-value">¥{{ payAmount.toFixed(2) }}</span>
          </div>
        </div>

        <!-- 底部操作栏 -->
        <div class="action-bar">
          <div class="action-info">
            <div class="total-display">
              <span>应付金额：</span>
              <span class="price">¥{{ payAmount.toFixed(2) }}</span>
            </div>
          </div>
          <div class="action-buttons">
            <button class="btn-cancel" @click="goBack">
              取消
            </button>
            <button class="btn-submit" @click="submitOrder" :disabled="!canSubmit">
              提交订单
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 地址选择对话框 -->
    <el-dialog
      v-model="showAddressDialog"
      title="选择收货地址"
      width="600px"
    >
      <div v-if="addressList.length > 0">
        <div 
          v-for="address in addressList" 
          :key="address.id"
          :class="['address-item', { 'address-selected': selectedAddress && selectedAddress.id === address.id }]"
          @click="selectAddress(address)"
        >
          <div class="address-header">
            <span class="address-name">{{ address.name }}</span>
            <span class="address-phone">{{ address.phone }}</span>
            <span v-if="address.isDefault" class="default-tag">默认</span>
          </div>
          <div class="address-body">
            {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detailAddress }}
          </div>
        </div>
      </div>
      <div v-else class="empty-address">
        <div class="empty-icon">📍</div>
        <p>暂无收货地址</p>
        <button class="add-address-btn" @click="showAddressDialog = false; $router.push('/personal?tab=address')">
          去添加地址
        </button>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddressDialog = false">取消</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 优惠券选择对话框 -->
    <el-dialog
      v-model="showCouponDialog"
      title="选择优惠券"
      width="800px"
    >
      <div v-if="availableCoupons.available && availableCoupons.available.length > 0">
        <h3 class="coupon-section-title">可用优惠券</h3>
        <div class="coupon-list">
          <div 
            v-for="coupon in availableCoupons.available" 
            :key="coupon.userCouponId"
            :class="['coupon-item', { 'coupon-selected': isCouponSelected(coupon.userCouponId) }]"
            @click="toggleCoupon(coupon.userCouponId)"
          >
            <div class="coupon-left">
              <div class="coupon-amount">
                <span class="amount-symbol">¥</span>
                <span class="amount-value">{{ coupon.discountAmount }}</span>
              </div>
              <div class="coupon-condition">
                {{ coupon.minSpend > 0 ? '满' + coupon.minSpend + '元可用' : '无门槛' }}
              </div>
            </div>
            <div class="coupon-right">
              <h4 class="coupon-name">{{ coupon.description }}</h4>
              <div class="coupon-info">
                <div class="coupon-category">
                  {{ coupon.categoryId ? '限指定分类' : '全品类' }}
                </div>
                <div class="coupon-time">
                  有效期至：{{ formatDate(coupon.expireTime) }}
                </div>
              </div>
              <div class="coupon-actual">
                实际抵扣：¥{{ coupon.actualDiscount.toFixed(2) }}
              </div>
            </div>
            <div class="coupon-checkbox">
              <input 
                type="checkbox" 
                :checked="isCouponSelected(coupon.userCouponId)"
                @change="toggleCoupon(coupon.userCouponId)"
              />
            </div>
          </div>
        </div>
      </div>
      <div v-else class="empty-coupons">
        <div class="empty-icon">🎫</div>
        <p>暂无可用优惠券</p>
      </div>
      <div v-if="availableCoupons.unavailable && availableCoupons.unavailable.length > 0">
        <h3 class="coupon-section-title">不可用优惠券</h3>
        <div class="coupon-list">
          <div 
            v-for="coupon in availableCoupons.unavailable" 
            :key="coupon.userCouponId"
            class="coupon-item coupon-unavailable"
          >
            <div class="coupon-left">
              <div class="coupon-amount">
                <span class="amount-symbol">¥</span>
                <span class="amount-value">{{ getUnavailableCouponAmount(coupon) }}</span>
              </div>
            </div>
            <div class="coupon-right">
              <h4 class="coupon-name">{{ coupon.description }}</h4>
              <div class="coupon-reason">
                {{ coupon.reason }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showCouponDialog = false">取消</el-button>
          <el-button type="primary" @click="confirmCoupons">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getAddressList } from '../api/address'
import { getAvailableCoupons, createOrder } from '../api/order'
import { getProductDetail } from '../api/product'

export default {
  name: 'OrderConfirm',
  data() {
    return {
      isScrolled: false,
      showAddressDialog: false,
      showCouponDialog: false,
      addressList: [],
      selectedAddress: null,
      selectedItems: [],
      availableCoupons: {
        available: [],
        unavailable: []
      },
      selectedCoupons: [],
      orderRemark: '',
      totalAmount: 0,
      freightAmount: 0,
      totalCouponAmount: 0,
      payAmount: 0
    }
  },
  mounted() {
    window.addEventListener('scroll', this.handleScroll)
    this.loadData()
  },
  beforeDestroy() {
    window.removeEventListener('scroll', this.handleScroll)
  },
  computed: {
    canSubmit() {
      return this.selectedAddress && this.selectedItems.length > 0
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
    async loadData() {
      // 从 localStorage 获取选中的商品
      const selectedItems = JSON.parse(localStorage.getItem('selectedCartItems') || '[]')
      if (selectedItems.length === 0) {
        this.$message.warning('请先选择商品')
        this.$router.push('/')
        return
      }
      
      this.selectedItems = selectedItems
      
      // 计算商品总额
      this.totalAmount = selectedItems.reduce((sum, item) => sum + item.price * item.quantity, 0)
      this.freightAmount = 0 // 暂定运费为0
      this.calculatePayAmount()
      
      // 加载地址列表
      await this.loadAddressList()
      
      // 加载可用优惠券
      await this.loadAvailableCoupons()
    },
    async loadAddressList() {
      const userId = localStorage.getItem('userId')
      if (!userId) {
        this.$message.warning('请先登录')
        this.$router.push('/')
        return
      }
      
      try {
        const result = await getAddressList(parseInt(userId))
        if (result.code === 1) {
          this.addressList = result.data || []
          // 自动选择默认地址
          this.selectedAddress = this.addressList.find(addr => addr.isDefault === 1) || this.addressList[0]
        }
      } catch (error) {
        console.error('加载地址失败:', error)
        this.$message.error('加载地址失败')
      }
    },
    async loadAvailableCoupons() {
      const userId = localStorage.getItem('userId')
      if (!userId) return
      
      try {
        await this.ensureCategoryIds()
        // 构建商品项数据
        const items = this.selectedItems.map(item => ({
          productId: item.productId,
          categoryId: item.categoryId,
          price: item.price,
          quantity: item.quantity
        }))
        
        const result = await getAvailableCoupons({
          userId: parseInt(userId),
          items
        })
        
        if (result.code === 1) {
          this.availableCoupons = result.data
        } else {
          this.$message.error(result.msg || '加载优惠券失败')
        }
      } catch (error) {
        console.error('加载优惠券失败:', error)
        this.$message.error('加载优惠券失败，请稍后重试')
      }
    },
    async ensureCategoryIds() {
      const missingItems = this.selectedItems.filter(item => item.categoryId === undefined || item.categoryId === null || item.categoryId === 0)
      if (missingItems.length === 0) return

      await Promise.all(missingItems.map(async (item) => {
        try {
          const result = await getProductDetail(item.productId)
          if (result && result.code === 1 && result.data && result.data.categoryId != null) {
            item.categoryId = result.data.categoryId
          }
        } catch (error) {
          console.error('加载商品分类失败:', error)
        }
      }))
    },
    async openCouponDialog() {
      const userId = localStorage.getItem('userId')
      if (!userId) {
        this.$message.warning('请先登录')
        this.$router.push('/')
        return
      }
      
      try {
        // 先加载可用优惠券
        await this.loadAvailableCoupons()
        // 然后打开弹窗
        this.showCouponDialog = true
      } catch (error) {
        console.error('打开优惠券弹窗失败:', error)
        this.$message.error('打开优惠券弹窗失败，请稍后重试')
      }
    },
    selectAddress(address) {
      this.selectedAddress = address
    },
    isCouponSelected(userCouponId) {
      return this.selectedCoupons.includes(userCouponId)
    },
    toggleCoupon(userCouponId) {
      const index = this.selectedCoupons.indexOf(userCouponId)
      if (index > -1) {
        this.selectedCoupons.splice(index, 1)
      } else {
        this.selectedCoupons.push(userCouponId)
      }
    },
    confirmCoupons() {
      // 计算优惠券抵扣金额
      this.totalCouponAmount = this.selectedCoupons.reduce((sum, userCouponId) => {
        const coupon = this.availableCoupons.available.find(c => c.userCouponId === userCouponId)
        return sum + (coupon ? coupon.actualDiscount : 0)
      }, 0)
      this.calculatePayAmount()
      this.showCouponDialog = false
    },
    calculatePayAmount() {
      this.payAmount = this.totalAmount + this.freightAmount - this.totalCouponAmount
      if (this.payAmount < 0) {
        this.payAmount = 0
      }
    },
    async submitOrder() {
      if (!this.canSubmit) {
        this.$message.warning('请完成订单信息')
        return
      }
      
      const userId = localStorage.getItem('userId')
      if (!userId) {
        this.$message.warning('请先登录')
        return
      }

      const cartItemIds = this.selectedItems
        .map(item => item.cartItemId || item.id)
        .filter(id => id != null)

      if (cartItemIds.length === 0) {
        this.$message.warning('未找到可结算的购物车商品')
        return
      }

      try {
        const orderData = {
          userId: parseInt(userId),
          addressId: this.selectedAddress.id,
          couponIds: this.selectedCoupons,
          remark: this.orderRemark,
          cartItemIds
        }
        
        const result = await createOrder(orderData)
        if (result.code === 1) {
          // 清空选中的购物车商品
          localStorage.removeItem('selectedCartItems')
          
          // 跳转到支付页面
          this.$router.push(`/payment/${result.data.id}`)
        } else {
          this.$message.error(result.msg || '创建订单失败')
        }
      } catch (error) {
        console.error('创建订单失败:', error)
        this.$message.error('创建订单失败，请稍后重试')
      }
    },
    formatDate(date) {
      const d = new Date(date)
      return d.toLocaleDateString('zh-CN')
    },
    getUnavailableCouponAmount(coupon) {
      const description = coupon && coupon.description ? String(coupon.description) : ''
      const match = description.match(/\d+(?:\.\d+)?/)
      return match ? match[0] : '0'
    }
  }
}
</script>

<style scoped>
@import '../styles/variables.css';

.order-confirm-container {
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

/* 订单确认页面 */
.order-confirm-page {
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

/* 收货地址 */
.address-section {
  padding: 20px 0;
}

.address-content {
  padding: 20px;
  background: var(--color-bg);
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border-light);
  position: relative;
}

.receiver-info {
  display: flex;
  gap: 20px;
  margin-bottom: 10px;
  align-items: center;
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

.default-tag {
  background: var(--color-primary);
  color: white;
  padding: 2px 8px;
  border-radius: var(--radius-sm);
  font-size: var(--text-xs);
  font-weight: 500;
}

.address-detail {
  font-size: var(--text-base);
  color: var(--color-text-secondary);
  line-height: 1.6;
  margin: 0;
}

.change-address-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  padding: 6px 16px;
  background: var(--color-bg-white);
  color: var(--color-primary);
  border: 1px solid var(--color-primary);
  border-radius: var(--radius-sm);
  font-size: var(--text-sm);
  cursor: pointer;
  transition: all 0.2s var(--ease-in-out);
}

.change-address-btn:hover {
  background: var(--color-primary);
  color: white;
}

.address-empty {
  padding: 40px;
  text-align: center;
  background: var(--color-bg);
  border-radius: var(--radius-md);
  border: 2px dashed var(--color-border);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.address-empty p {
  font-size: var(--text-base);
  color: var(--color-text-tertiary);
  margin: 0 0 20px 0;
}

.add-address-btn {
  padding: 10px 24px;
  background: var(--color-primary);
  color: white;
  border: none;
  border-radius: var(--radius-md);
  font-size: var(--text-base);
  cursor: pointer;
  transition: opacity 0.2s var(--ease-in-out);
}

.add-address-btn:hover {
  opacity: 0.9;
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

.quantity-value {
  font-size: var(--text-base);
  color: var(--color-text-secondary);
  font-weight: 500;
}

/* 小计 */
.product-subtotal {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.subtotal-price {
  font-size: var(--text-2xl);
  color: var(--color-primary);
  font-weight: 600;
}

/* 优惠券 */
.coupon-section {
  padding: 20px 0;
}

.coupon-content {
  padding: 20px;
  background: var(--color-bg);
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border-light);
}

.coupon-selected {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  padding: 10px;
  border-radius: var(--radius-sm);
  transition: background 0.2s var(--ease-in-out);
}

.coupon-selected:hover {
  background: var(--color-bg-white);
}

.coupon-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.coupon-label {
  font-size: var(--text-base);
  color: var(--color-text-secondary);
}

.coupon-value {
  font-size: var(--text-base);
  color: var(--color-primary);
  font-weight: 500;
}

.arrow {
  font-size: var(--text-lg);
  color: var(--color-text-tertiary);
  transition: transform 0.2s var(--ease-in-out);
}

.coupon-selected:hover .arrow {
  transform: translateX(5px);
}

/* 订单备注 */
.remark-section {
  padding: 20px 0;
}

.remark-input {
  width: 100%;
  padding: 12px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: var(--text-base);
  outline: none;
  transition: border-color 0.2s var(--ease-in-out);
}

.remark-input:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(229, 57, 53, 0.1);
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
.btn-submit {
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

.btn-submit {
  background: var(--color-primary);
  color: white;
  box-shadow: 0 2px 8px rgba(229, 57, 53, 0.2);
}

.btn-submit:hover:not(:disabled) {
  background: var(--color-primary-hover);
  box-shadow: 0 4px 14px rgba(229, 57, 53, 0.3);
  transform: translateY(-1px);
}

.btn-submit:disabled {
  background: var(--color-text-tertiary);
  cursor: not-allowed;
}

/* 地址选择对话框 */
.address-item {
  padding: 20px;
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md);
  margin-bottom: 16px;
  cursor: pointer;
  transition: all 0.2s var(--ease-in-out);
}

.address-item:hover {
  border-color: var(--color-primary);
  box-shadow: var(--shadow-xs);
}

.address-item.address-selected {
  border-color: var(--color-primary);
  background: var(--color-primary-light);
}

.address-header {
  display: flex;
  gap: 16px;
  margin-bottom: 10px;
  align-items: center;
}

.address-name {
  font-size: var(--text-lg);
  color: var(--color-text-primary);
  font-weight: 600;
}

.address-phone {
  font-size: var(--text-base);
  color: var(--color-text-secondary);
}

.address-body {
  font-size: var(--text-base);
  color: var(--color-text-secondary);
  line-height: 1.6;
}

.empty-address {
  padding: 40px;
  text-align: center;
}

/* 优惠券选择对话框 */
.coupon-section-title {
  font-size: var(--text-lg);
  color: var(--color-text-primary);
  margin: 0 0 16px 0;
  font-weight: 600;
}

.coupon-list {
  max-height: 400px;
  overflow-y: auto;
  margin-bottom: 20px;
}

.coupon-item {
  display: grid;
  grid-template-columns: 150px 1fr auto;
  gap: 20px;
  padding: 20px;
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md);
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.2s var(--ease-in-out);
  background: var(--color-bg-white);
}

.coupon-item:hover {
  border-color: var(--color-primary);
  box-shadow: var(--shadow-xs);
}

.coupon-item.coupon-selected {
  border-color: var(--color-primary);
  background: var(--color-primary-light);
}

.coupon-item.coupon-unavailable {
  opacity: 0.6;
  cursor: not-allowed;
  background: var(--color-bg);
}

.coupon-left {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  color: white;
  border-radius: var(--radius-md);
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100px;
}

.coupon-amount {
  display: flex;
  align-items: baseline;
  gap: 5px;
  margin-bottom: 8px;
}

.amount-symbol {
  font-size: var(--text-lg);
  font-weight: 600;
}

.amount-value {
  font-size: var(--text-3xl);
  font-weight: 700;
}

.coupon-condition {
  font-size: var(--text-sm);
  opacity: 0.9;
}

.coupon-right {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.coupon-name {
  font-size: var(--text-lg);
  color: var(--color-text-primary);
  margin: 0;
  font-weight: 600;
}

.coupon-info {
  display: flex;
  gap: 20px;
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
}

.coupon-actual {
  font-size: var(--text-sm);
  color: var(--color-primary);
  font-weight: 500;
  margin-top: 8px;
}

.coupon-reason {
  font-size: var(--text-sm);
  color: var(--color-text-tertiary);
  margin-top: 8px;
}

.coupon-checkbox {
  display: flex;
  align-items: center;
  justify-content: center;
}

.coupon-checkbox input[type="checkbox"] {
  width: 20px;
  height: 20px;
  cursor: pointer;
}

.empty-coupons {
  padding: 40px;
  text-align: center;
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

  .action-bar {
    flex-direction: column;
    gap: 15px;
  }

  .action-buttons {
    width: 100%;
  }

  .btn-cancel,
  .btn-submit {
    flex: 1;
  }

  .coupon-item {
    grid-template-columns: 120px 1fr auto;
    gap: 15px;
  }
}
</style>