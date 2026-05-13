<template>
  <div v-if="!isPersonalPage" class="side-nav-wrapper">
    <!-- 仿淘宝小型侧边导航栏 -->
    <div class="side-nav">
      <!-- 消息 -->
      <div class="nav-item" @click="handleMessage" @mouseenter="hoverIndex = 0" @mouseleave="hoverIndex = -1">
        <div class="nav-icon">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
          </svg>
          <span v-if="msgCount > 0" class="nav-badge">{{ msgCount > 99 ? '99+' : msgCount }}</span>
        </div>
        <div class="nav-tooltip">消息</div>
      </div>

      <!-- 购物车 -->
      <div class="nav-item" @click="toggleCart" @mouseenter="hoverIndex = 1" @mouseleave="hoverIndex = -1">
        <div class="nav-icon">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="9" cy="21" r="1"></circle>
            <circle cx="20" cy="21" r="1"></circle>
            <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
          </svg>
          <span v-if="cartCount > 0" class="nav-badge">{{ cartCount > 99 ? '99+' : cartCount }}</span>
        </div>
        <div class="nav-tooltip">购物车</div>
      </div>

      <!-- 客服 -->
      <div class="nav-item" @click="handleService" @mouseenter="hoverIndex = 2" @mouseleave="hoverIndex = -1">
        <div class="nav-icon">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 11.5a8.38 8.38 0 0 1-.9 3.8 8.5 8.5 0 0 1-7.6 4.7 8.38 8.38 0 0 1-3.8-.9L3 21l1.9-5.7a8.38 8.38 0 0 1-.9-3.8 8.5 8.5 0 0 1 4.7-7.6 8.38 8.38 0 0 1 3.8-.9h.5a8.48 8.48 0 0 1 8 8v.5z"></path>
          </svg>
        </div>
        <div class="nav-tooltip">客服</div>
      </div>
    </div>

    <!-- 购物车侧边栏（点击购物车时弹出） -->
    <div v-if="visible" class="cart-sidebar-overlay" @click="closeCart"></div>
    <transition name="slide-right">
      <div v-if="visible" class="cart-sidebar">
        <!-- 头部 -->
        <div class="cart-header">
          <h3 class="cart-title">我的购物车</h3>
          <button class="close-btn" @click="closeCart">×</button>
        </div>

        <!-- 空购物车 -->
        <div v-if="cartItems.length === 0" class="empty-cart">
          <div class="empty-icon">🛒</div>
          <p class="empty-text">购物车是空的</p>
          <button class="go-shopping-btn" @click="goShopping">去逛逛</button>
        </div>

        <!-- 购物车列表 -->
        <div v-else class="cart-body">
          <div class="cart-list">
            <div v-for="item in cartItems" :key="item.id" class="cart-item">
              <div class="item-checkbox">
                <input
                  type="checkbox"
                  :checked="item.isChecked === 1"
                  @change="toggleItemChecked(item)"
                />
              </div>
              <div class="item-image" @click="goToProduct(item.productId)">
                <img :src="item.productImage" :alt="item.productName" />
              </div>
              <div class="item-info">
                <h4 class="item-name" @click="goToProduct(item.productId)">{{ item.productName }}</h4>
                <div class="item-price">¥{{ item.price }}</div>
                <div class="item-quantity-control">
                  <button class="qty-btn" :disabled="item.quantity <= 1" @click="decreaseQuantity(item)">-</button>
                  <span class="qty-value">{{ item.quantity }}</span>
                  <button class="qty-btn" :disabled="item.quantity >= item.stock" @click="increaseQuantity(item)">+</button>
                </div>
              </div>
              <button class="remove-item-btn" @click="removeFromCart(item)">
                <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="3 6 5 6 21 6"></polyline>
                  <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                </svg>
              </button>
            </div>
          </div>
        </div>

        <!-- 底部操作栏 -->
        <div v-if="cartItems.length > 0" class="cart-footer">
          <div class="cart-select-all">
            <label>
              <input type="checkbox" :checked="allChecked" @change="toggleAllChecked" />
              <span>全选</span>
            </label>
          </div>
          <div class="cart-total">
            <div class="total-row">
              <span>商品总额：</span>
              <span class="total-price">¥{{ totalPrice }}</span>
            </div>
            <div class="total-row">
              <span>商品数量：</span>
              <span class="total-count">{{ totalCount }} 件</span>
            </div>
          </div>
          <div class="cart-actions">
            <button class="clear-cart-btn" @click="clearCart">清空</button>
            <button class="checkout-btn" @click="checkout">去结算</button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { addToCart, getCartList, updateQuantity, deleteCart, batchDelete, getCartCount, updateChecked, checkAll } from '../api/cart'

export default {
  name: 'CartSidebar',
  data() {
    return {
      visible: false,
      hoverIndex: -1,
      cartItems: [],
      msgCount: 0
    }
  },
  computed: {
    isPersonalPage() {
      return this.$route.path === '/personal'
    },
    cartCount() {
      return this.cartItems.reduce((sum, item) => sum + item.quantity, 0)
    },
    selectedItems() {
      return this.cartItems.filter(item => item.isChecked === 1)
    },
    allChecked() {
      return this.cartItems.length > 0 && this.cartItems.every(item => item.isChecked === 1)
    },
    totalCount() {
      return this.selectedItems.reduce((sum, item) => sum + item.quantity, 0)
    },
    totalPrice() {
      return this.selectedItems.reduce((sum, item) => sum + Number(item.subtotal || 0), 0).toFixed(2)
    }
  },
  mounted() {
    this.loadCartData()
    window.addEventListener('cartUpdated', this.loadCartData)
  },
  beforeDestroy() {
    window.removeEventListener('cartUpdated', this.loadCartData)
  },
  methods: {
    toggleCart() {
      this.visible = !this.visible
      this.loadCartData()
    },
    openCart() {
      this.visible = true
      this.loadCartData()
    },
    closeCart() {
      this.visible = false
    },
    handleMessage() {
      this.$router.push('/customer-service')
    },
    handleService() {
      this.$router.push('/customer-service')
    },
    async loadCartData() {
      const userId = localStorage.getItem('userId')
      if (!userId) {
        this.cartItems = []
        return
      }
      
      try {
        const result = await getCartList(parseInt(userId))
        if (result.code === 1) {
          this.cartItems = result.data || []
        } else {
          this.$message.error(result.msg || '加载购物车失败')
          this.cartItems = []
        }
      } catch (error) {
        console.error('加载购物车失败:', error)
        this.$message.error('加载购物车失败，请稍后重试')
        this.cartItems = []
      }
    },
    goToProduct(productId) {
      this.closeCart()
      this.$router.push({ path: '/product', query: { id: productId } })
    },
    goShopping() {
      this.closeCart()
      this.$router.push('/')
    },
    async increaseQuantity(item) {
      const userId = localStorage.getItem('userId')
      if (!userId) {
        this.$message.warning('请先登录')
        return
      }
      
      try {
        const result = await updateQuantity({
          id: item.id,
          quantity: item.quantity + 1
        })
        if (result.code === 1) {
          this.loadCartData()
        } else {
          this.$message.error(result.msg || '更新数量失败')
        }
      } catch (error) {
        console.error('更新数量失败:', error)
        this.$message.error('更新数量失败，请稍后重试')
      }
    },
    async decreaseQuantity(item) {
      const userId = localStorage.getItem('userId')
      if (!userId) {
        this.$message.warning('请先登录')
        return
      }
      
      if (item.quantity <= 1) {
        this.$confirm('数量已为 1，再减少将移除该商品', '提示', {
          confirmButtonText: '移除',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(async () => {
          await this.removeFromCart(item)
        }).catch(() => {})
      } else {
        try {
          const result = await updateQuantity({
            id: item.id,
            quantity: item.quantity - 1
          })
          if (result.code === 1) {
            this.loadCartData()
          } else {
            this.$message.error(result.msg || '更新数量失败')
          }
        } catch (error) {
          console.error('更新数量失败:', error)
          this.$message.error('更新数量失败，请稍后重试')
        }
      }
    },
    async removeFromCart(item) {
      const userId = localStorage.getItem('userId')
      if (!userId) {
        this.$message.warning('请先登录')
        return
      }
      
      try {
        const result = await deleteCart(item.id)
        if (result.code === 1) {
          this.loadCartData()
          this.$message.success('移除成功')
        } else {
          this.$message.error(result.msg || '移除失败')
        }
      } catch (error) {
        console.error('移除失败:', error)
        this.$message.error('移除失败，请稍后重试')
      }
    },
    async clearCart() {
      const userId = localStorage.getItem('userId')
      if (!userId) {
        this.$message.warning('请先登录')
        return
      }
      
      this.$confirm('确定要清空购物车吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const ids = this.cartItems.map(item => item.id)
          if (ids.length > 0) {
            const result = await batchDelete(ids)
            if (result.code === 1) {
              this.loadCartData()
              this.$message.success('清空成功')
            } else {
              this.$message.error(result.msg || '清空失败')
            }
          } else {
            this.$message.info('购物车已是空的')
          }
        } catch (error) {
          console.error('清空失败:', error)
          this.$message.error('清空失败，请稍后重试')
        }
      }).catch(() => {})
    },
    async toggleItemChecked(item) {
      const userId = localStorage.getItem('userId')
      if (!userId) {
        this.$message.warning('请先登录')
        return
      }

      const nextChecked = item.isChecked === 1 ? 0 : 1
      try {
        const result = await updateChecked({
          id: item.id,
          userId: parseInt(userId),
          isChecked: nextChecked
        })
        if (result.code === 1) {
          this.loadCartData()
        } else {
          this.$message.error(result.msg || '更新选中状态失败')
        }
      } catch (error) {
        console.error('更新选中状态失败:', error)
        this.$message.error('更新选中状态失败，请稍后重试')
      }
    },
    async toggleAllChecked() {
      const userId = localStorage.getItem('userId')
      if (!userId) {
        this.$message.warning('请先登录')
        return
      }

      const nextChecked = this.allChecked ? 0 : 1
      try {
        const result = await checkAll({
          userId: parseInt(userId),
          isChecked: nextChecked
        })
        if (result.code === 1) {
          this.loadCartData()
        } else {
          this.$message.error(result.msg || '更新全选状态失败')
        }
      } catch (error) {
        console.error('更新全选状态失败:', error)
        this.$message.error('更新全选状态失败，请稍后重试')
      }
    },
    checkout() {
      if (this.selectedItems.length === 0) {
        this.$message.warning('请先勾选要结算的商品')
        return
      }
      
      const selectedItems = this.selectedItems.map(item => ({
        cartItemId: item.id,
        productId: item.productId,
        productName: item.productName,
        productImage: item.productImage,
        price: item.price,
        quantity: item.quantity,
        categoryId: item.categoryId
      }))
      
      // 存储选中的商品到 localStorage
      localStorage.setItem('selectedCartItems', JSON.stringify(selectedItems))
      
      this.closeCart()
      this.$router.push('/order-confirm')
    }
  }
}
</script>

<style scoped>
@import '../styles/variables.css';

.side-nav-wrapper {
  position: relative;
}

/* ===== 仿淘宝小型侧边导航栏 ===== */
.side-nav {
  position: fixed;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 999;
  display: flex;
  flex-direction: column;
  gap: 2px;
  background: var(--color-bg-white);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-md);
  padding: 6px 4px;
  border: 1px solid var(--color-border-light);
}

.nav-item {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 50px;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: background var(--duration-normal) var(--ease-in-out),
              color var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
  position: relative;
  color: var(--color-text-secondary);
}

.nav-item:hover {
  background: var(--color-primary-light);
  color: var(--color-primary);
  transform: scale(1.05);
}

.nav-item:active {
  transform: scale(0.95);
}

.nav-item:hover .nav-tooltip {
  opacity: 1;
  transform: translateX(-4px);
  visibility: visible;
}

.nav-icon {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-badge {
  position: absolute;
  top: -4px;
  right: -8px;
  background: var(--color-primary);
  color: white;
  border-radius: var(--radius-full);
  padding: 0 4px;
  font-size: 9px;
  font-weight: 600;
  min-width: 14px;
  height: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  line-height: 1;
  box-shadow: 0 1px 3px rgba(229, 57, 53, 0.3);
}

/* 悬浮提示标签 */
.nav-tooltip {
  position: absolute;
  right: calc(100% + 8px);
  top: 50%;
  transform: translateX(0) translateY(-50%);
  background: var(--color-text-primary);
  color: white;
  padding: 4px 8px;
  border-radius: var(--radius-sm);
  font-size: 11px;
  white-space: nowrap;
  opacity: 0;
  visibility: hidden;
  transition: all 0.2s var(--ease-in-out);
  pointer-events: none;
}

.nav-tooltip::after {
  content: '';
  position: absolute;
  right: -4px;
  top: 50%;
  transform: translateY(-50%);
  border: 4px solid transparent;
  border-left-color: var(--color-text-primary);
}

/* ===== 购物车侧边栏 ===== */
.cart-sidebar-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(2px);
  -webkit-backdrop-filter: blur(2px);
  z-index: 1000;
}

.cart-sidebar {
  position: fixed;
  top: 0;
  right: 0;
  width: 400px;
  height: 100vh;
  background: var(--color-bg-white);
  z-index: 1001;
  box-shadow: var(--shadow-lg);
  display: flex;
  flex-direction: column;
}

.slide-right-enter-active,
.slide-right-leave-active {
  transition: transform 0.35s var(--ease-out);
}

.slide-right-enter,
.slide-right-leave-to {
  transform: translateX(100%);
}

/* 头部 */
.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid var(--color-border-light);
}

.cart-title {
  font-size: var(--text-xl);
  color: var(--color-text-primary);
  margin: 0;
  font-weight: 600;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: transparent;
  font-size: 24px;
  color: var(--color-text-tertiary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--radius-full);
  transition: background var(--duration-normal) var(--ease-in-out),
              color var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
}

.close-btn:hover {
  background: var(--color-bg);
  color: var(--color-text-primary);
  transform: rotate(90deg);
}

/* 空购物车 */
.empty-cart {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 16px;
  opacity: 0.3;
}

.empty-text {
  font-size: var(--text-base);
  color: var(--color-text-tertiary);
  margin: 0 0 24px 0;
}

.go-shopping-btn {
  padding: 10px 32px;
  background: var(--color-primary);
  color: white;
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  font-size: var(--text-base);
  font-weight: 600;
  transition: background var(--duration-normal) var(--ease-in-out),
              box-shadow var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
}

.go-shopping-btn:hover {
  background: var(--color-primary-hover);
  box-shadow: var(--shadow-primary);
}

.go-shopping-btn:active {
  transform: scale(0.97);
  background: var(--color-primary-dark);
}

/* 购物车列表 */
.cart-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px 20px;
}

.cart-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.cart-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md);
  transition: border-color var(--duration-normal) var(--ease-in-out),
              box-shadow var(--duration-normal) var(--ease-in-out);
  background: var(--color-bg-white);
}

.cart-item:hover {
  border-color: var(--color-border-hover);
  box-shadow: var(--shadow-xs);
}

.item-checkbox {
  display: flex;
  align-items: center;
  justify-content: center;
  padding-right: 4px;
}

.item-checkbox input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.item-image {
  width: 72px;
  height: 72px;
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  background: var(--color-bg);
  flex-shrink: 0;
  transition: transform var(--duration-normal) var(--ease-in-out);
}

.item-image:hover {
  transform: scale(1.03);
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.item-name {
  font-size: var(--text-sm);
  color: var(--color-text-primary);
  margin: 0;
  font-weight: 500;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.item-name:hover {
  color: var(--color-primary);
}

.item-price {
  font-size: var(--text-lg);
  color: var(--color-primary);
  font-weight: 600;
}

.item-quantity-control {
  display: flex;
  align-items: center;
  gap: 8px;
}

.qty-btn {
  width: 26px;
  height: 26px;
  border: 1px solid var(--color-border);
  background: var(--color-bg-white);
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: 14px;
  color: var(--color-text-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: border-color var(--duration-normal) var(--ease-in-out),
              color var(--duration-normal) var(--ease-in-out),
              background var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
}

.qty-btn:hover:not(:disabled) {
  border-color: var(--color-primary);
  color: var(--color-primary);
  background: var(--color-primary-light);
}

.qty-btn:active:not(:disabled) {
  transform: scale(0.9);
}

.qty-btn:disabled {
  color: var(--color-text-tertiary);
  cursor: not-allowed;
  border-color: var(--color-border-light);
}

.qty-value {
  font-size: var(--text-sm);
  color: var(--color-text-primary);
  font-weight: 500;
  min-width: 24px;
  text-align: center;
}

.remove-item-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  border-radius: var(--radius-full);
  cursor: pointer;
  color: var(--color-text-tertiary);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background var(--duration-normal) var(--ease-in-out),
              color var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
  flex-shrink: 0;
  align-self: flex-start;
}

.remove-item-btn:hover {
  background: var(--color-danger-light);
  color: var(--color-danger);
  transform: scale(1.1);
}

.remove-item-btn:active {
  transform: scale(0.9);
}

/* 底部操作栏 */
.cart-footer {
  border-top: 1px solid var(--color-border-light);
  padding: 16px 20px;
  background: var(--color-bg);
}

.cart-select-all {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
}

.cart-select-all input[type="checkbox"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.cart-total {
  margin-bottom: 12px;
}

.total-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
}

.total-row:first-child {
  border-bottom: 1px solid var(--color-border-light);
  padding-bottom: 10px;
  margin-bottom: 6px;
}

.total-price {
  font-size: var(--text-xl);
  color: var(--color-primary);
  font-weight: 700;
}

.cart-actions {
  display: flex;
  gap: 10px;
}

.clear-cart-btn,
.checkout-btn {
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: var(--radius-md);
  cursor: pointer;
  font-size: var(--text-sm);
  font-weight: 600;
  transition: background var(--duration-normal) var(--ease-in-out),
              color var(--duration-normal) var(--ease-in-out),
              border-color var(--duration-normal) var(--ease-in-out),
              box-shadow var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
}

.clear-cart-btn {
  background: var(--color-bg-white);
  color: var(--color-text-secondary);
  border: 1px solid var(--color-border);
}

.clear-cart-btn:hover {
  border-color: var(--color-danger);
  color: var(--color-danger);
  background: var(--color-danger-light);
}

.clear-cart-btn:active {
  transform: scale(0.97);
}

.checkout-btn {
  background: var(--color-primary);
  color: white;
}

.checkout-btn:hover {
  background: var(--color-primary-hover);
  box-shadow: var(--shadow-primary);
}

.checkout-btn:active {
  transform: scale(0.97);
  background: var(--color-primary-dark);
}

/* 响应式 */
@media (max-width: 1440px) {
  .side-nav {
    right: 8px;
  }
}

@media (max-width: 768px) {
  .nav-item {
    width: 36px;
    height: 36px;
  }

  .cart-sidebar {
    width: 100%;
  }
}
</style>
