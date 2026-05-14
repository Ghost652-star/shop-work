<template>
  <div class="personal-container">
    <!-- 主体内容 -->
    <div class="main-container">
      <!-- 左侧导航栏 -->
      <div class="side-nav">
        <div class="back-home" @click="goBack">
          <span>←</span> 返回首页
        </div>
        
        <div class="user-profile">
          <div class="avatar" v-if="userInfo.avatar">
            <img :src="userInfo.avatar" :alt="userInfo.nickname" class="avatar-image" />
          </div>
          <div class="avatar" v-else>
            <span class="avatar-placeholder">{{ userInfo.nickname?.charAt(0) || 'U' }}</span>
          </div>
          <div class="user-info">
            <h3>{{ userInfo.nickname || '用户' }}</h3>
            <p>普通会员</p>
          </div>
        </div>
        
        <div class="nav-menu">
          <div class="nav-item" :class="{ active: activeTab === 'cart' }" @click="navigateTo('cart')">
            <span class="nav-icon">🛒</span>
            <span>购物车</span>
          </div>
          <div class="nav-item" :class="{ active: activeTab === 'orders' }" @click="navigateTo('orders')">
            <span class="nav-icon">📦</span>
            <span>我的订单</span>
          </div>
          <div class="nav-item" :class="{ active: activeTab === 'favorites' }" @click="navigateTo('favorites')">
            <span class="nav-icon">❤️</span>
            <span>我的收藏</span>
          </div>
          <div class="nav-item" :class="{ active: activeTab === 'coupons' }" @click="navigateTo('coupons')">
            <span class="nav-icon">🎁</span>
            <span>优惠券</span>
          </div>
          <div class="nav-item" :class="{ active: activeTab === 'address' }" @click="navigateTo('address')">
            <span class="nav-icon">📍</span>
            <span>地址管理</span>
          </div>
          <div class="nav-item" :class="{ active: activeTab === 'reviews' }" @click="navigateTo('reviews')">
            <span class="nav-icon">💬</span>
            <span>我的评论</span>
          </div>
          <div class="nav-item" :class="{ active: activeTab === 'aftersale' }" @click="navigateTo('aftersale')">
            <span class="nav-icon">🔄</span>
            <span>售后处理</span>
          </div>
          <div class="nav-item" :class="{ active: activeTab === 'settings' }" @click="navigateTo('settings')">
            <span class="nav-icon">⚙️</span>
            <span>个人设置</span>
          </div>
        </div>
      </div>
      
      <!-- 右侧内容区 -->
      <div class="content-area">
        <!-- 我的订单页面 -->
        <div v-if="activeTab === 'orders'">
          <div class="content-header">
            <h2>📦 我的订单</h2>
          </div>
          
          <!-- 统计卡片 -->
          <div class="stats-section">
            <div class="stat-card">
              <div class="stat-value">{{ orderStats.pending }}</div>
              <div class="stat-label">待付款</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ orderStats.shipment }}</div>
              <div class="stat-label">待发货</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ orderStats.receipt }}</div>
              <div class="stat-label">待收货</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ orderStats.completed }}</div>
              <div class="stat-label">待评价</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ orderStats.cancelled }}</div>
              <div class="stat-label">已取消</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">{{ orderStats.aftersale }}</div>
              <div class="stat-label">售后中</div>
            </div>
          </div>
          
          <!-- 订单导航栏 -->
          <div class="order-tabs">
            <div class="order-tab" :class="{ active: orderTab === 'all' }" @click="orderTab = 'all'">全部订单</div>
            <div class="order-tab" :class="{ active: orderTab === 'pending' }" @click="orderTab = 'pending'">待支付</div>
            <div class="order-tab" :class="{ active: orderTab === 'shipment' }" @click="orderTab = 'shipment'">待发货</div>
            <div class="order-tab" :class="{ active: orderTab === 'receipt' }" @click="orderTab = 'receipt'">待收货</div>
            <div class="order-tab" :class="{ active: orderTab === 'completed' }" @click="orderTab = 'completed'">已完成</div>
            <div class="order-tab" :class="{ active: orderTab === 'cancelled' }" @click="orderTab = 'cancelled'">已取消</div>
            <div class="order-tab" :class="{ active: orderTab === 'aftersale' }" @click="orderTab = 'aftersale'">售后中</div>
          </div>
          
          <!-- 订单列表 -->
          <div class="order-list">
            <div v-if="filteredOrders.length === 0" class="empty-state">
              <div class="empty-icon">📦</div>
              <p>暂无订单</p>
            </div>
            <div v-else>
              <div v-for="order in filteredOrders" :key="order.id" class="order-card" @click="goToOrderDetail(order.id)">
                <div class="order-header">
                  <span>订单号：{{ order.orderNo }}</span>
                  <span class="order-status" :class="statusClass(order.status)">{{ order.statusText || formatOrderStatus(order.status) }}</span>
                </div>
                <div class="order-content">
                  <p>{{ getOrderItemSummary(order) }}</p>
                  <p>¥{{ formatAmount(order.payAmount) }}</p>
                </div>
                <div class="order-actions">
                  <button class="action-btn" @click.stop="goToOrderDetail(order.id)">查看详情</button>
                  <button v-if="order.status === 0" class="action-btn primary" @click.stop="goToPay(order.id)">去支付</button>
                  <button v-if="order.status === 0 || order.status === 1 || order.status === 2" class="action-btn" @click.stop="cancelOrderAction(order.id)">取消订单</button>
                  <button v-if="(order.status === 2 || order.status === 3) && order.afterSaleStatus !== 1" class="action-btn" @click.stop="goToAfterSale(order.id)">申请售后</button>
                  <button v-if="order.afterSaleStatus === 1" class="action-btn" @click.stop="goToAfterSaleDetail(order.id)">查看售后</button>
                  <button v-if="order.status === 2" class="action-btn primary" @click.stop="confirmOrderAction(order.id)">确认收货</button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 购物车页面 -->
        <div v-if="activeTab === 'cart'">
          <div class="content-header">
            <h2>🛒 购物车</h2>
          </div>

          <div v-if="cartItems.length === 0" class="empty-state">
            <div class="empty-icon">🛒</div>
            <p>购物车空空如也，快去逛逛吧</p>
            <button class="action-btn primary" @click="$router.push('/')" style="margin-top: 12px;">去逛逛</button>
          </div>

          <div v-else>
            <div class="cart-list">
              <div v-for="item in cartItems" :key="item.id" class="cart-item">
                <div class="cart-item-image" @click="goToProduct(item.productId)">
                  <img :src="item.mainImage" :alt="item.name" />
                </div>
                <div class="cart-item-info">
                  <h4 class="cart-item-name" @click="goToProduct(item.productId)">{{ item.name }}</h4>
                  <p class="cart-item-price">¥{{ item.price }}</p>
                </div>
                <div class="cart-item-qty">
                  <button class="qty-btn" @click="updateCartQty(item, -1)" :disabled="item.quantity <= 1">−</button>
                  <span class="qty-value">{{ item.quantity }}</span>
                  <button class="qty-btn" @click="updateCartQty(item, 1)">+</button>
                </div>
                <div class="cart-item-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
                <button class="cart-item-delete" @click="removeCartItem(item.id)">×</button>
              </div>
            </div>
            <div class="cart-summary">
              <span>共 {{ cartItems.length }} 件商品</span>
              <span>合计：<strong>¥{{ cartTotalPrice }}</strong></span>
              <button class="action-btn primary" @click="$router.push('/')">去结算</button>
            </div>
          </div>
        </div>

        <!-- 我的收藏页面 -->
        <div v-if="activeTab === 'favorites'">
          <div class="content-header">
            <h2>❤️ 我的收藏</h2>
          </div>
          
          <div v-if="favoriteList.length === 0" class="empty-state">
            <div class="empty-icon">❤️</div>
            <p>暂无收藏商品</p>
          </div>
          
          <div v-else class="favorite-grid">
            <div 
              v-for="item in favoriteProducts" 
              :key="item.id" 
              class="favorite-card"
              @click="goToProduct(item.id)"
            >
              <div class="favorite-image">
                <img :src="item.mainImage" :alt="item.name" />
              </div>
              <div class="favorite-info">
                <h4 class="favorite-name">{{ item.name }}</h4>
                <p class="favorite-price">¥{{ item.price }}</p>
                <button class="cancel-fav-btn" @click.stop="cancelFavorite(item.id)">
                  取消收藏
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- 优惠券页面 -->
        <div v-if="activeTab === 'coupons'">
          <div class="content-header">
            <h2>🎁 优惠券</h2>
          </div>
          
          <div v-if="userCouponList.length === 0" class="empty-state">
            <div class="empty-icon">🎁</div>
            <p>暂无优惠券</p>
            <button class="action-btn-primary" @click="goToCouponPage">去领取</button>
          </div>
          
          <div v-else class="coupon-list">
            <div 
              v-for="coupon in userCouponList" 
              :key="coupon.id"
              :class="['coupon-item', {
                'coupon-unused': coupon.status === 0,
                'coupon-used': coupon.status === 1,
                'coupon-expired': coupon.status === 2
              }]"
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
                  <div class="coupon-time">
                    <span>有效期至：{{ formatDate(coupon.expireTime) }}</span>
                  </div>
                  <div class="coupon-status" :class="{
                    'status-unused': coupon.status === 0,
                    'status-used': coupon.status === 1,
                    'status-expired': coupon.status === 2
                  }">
                    {{ formatCouponStatus(coupon.status) }}
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 地址管理页面 -->
        <div v-if="activeTab === 'address'">
          <div class="content-header">
            <h2>📍 收货地址</h2>
            <button class="add-address-btn" @click="showAddressDialog = true">
              <span>+</span> 新增地址
            </button>
          </div>
          
          <div v-if="addressList.length === 0" class="empty-state">
            <div class="empty-icon">📍</div>
            <p>暂无收货地址</p>
            <button class="action-btn-primary" @click="showAddressDialog = true">添加第一个地址</button>
          </div>
          
          <div v-else class="address-list">
            <div 
              v-for="address in addressList" 
              :key="address.id"
              :class="['address-item', { default: address.isDefault }]"
            >
              <div class="address-info">
                <div class="user-info">
                  <span class="name">{{ address.name }}</span>
                  <span class="phone">{{ formatPhone(address.phone) }}</span>
                  <span v-if="address.isDefault" class="default-tag">默认</span>
                </div>
                <div class="address-detail">
                  {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detailAddress }}
                </div>
              </div>
              <div class="address-actions">
                <button v-if="!address.isDefault" class="action-btn" @click="setDefaultAddress(address.id)">设为默认</button>
                <button class="action-btn" @click="editAddress(address)">编辑</button>
                <button class="action-btn delete" @click="deleteAddress(address.id)">删除</button>
              </div>
            </div>
          </div>
          
          <!-- 地址弹窗 -->
          <div v-if="showAddressDialog" class="dialog-overlay" @click="closeAddressDialog">
            <div class="dialog-content" @click.stop>
              <div class="dialog-header">
                <h3>{{ isEditing ? '编辑地址' : '新增地址' }}</h3>
                <button class="close-btn" @click="closeAddressDialog">×</button>
              </div>
              <div class="dialog-body">
                <div class="form-item">
                  <label>收货人 <span class="required">*</span></label>
                  <input v-model="addressForm.name" type="text" placeholder="请输入收货人姓名" maxlength="20" />
                </div>
                <div class="form-item">
                  <label>手机号码 <span class="required">*</span></label>
                  <input v-model="addressForm.phone" type="tel" placeholder="请输入手机号码" maxlength="11" />
                </div>
                <div class="form-item">
                  <label>所在地区 <span class="required">*</span></label>
                  <el-cascader
                    v-model="regionSelected"
                    :options="regionOptions"
                    placeholder="请选择省/市/区"
                    clearable
                    style="width: 100%"
                    @change="handleRegionChange"
                  />
                </div>
                <div class="form-item">
                  <label>详细地址 <span class="required">*</span></label>
                  <textarea v-model="addressForm.detailAddress" placeholder="街道、楼牌号等信息" maxlength="100" rows="3"></textarea>
                </div>
                <div class="form-item">
                  <label class="checkbox-label">
                    <input v-model="addressForm.isDefault" type="checkbox" />
                    <span>设为默认地址</span>
                  </label>
                </div>
              </div>
              <div class="dialog-footer">
                <button class="cancel-btn" @click="closeAddressDialog">取消</button>
                <button class="submit-btn" @click="submitAddress">保存地址</button>
              </div>
            </div>
          </div>
        </div>

        <!-- 我的评论页面 -->
        <div v-if="activeTab === 'reviews'">
          <div class="content-header">
            <h2>我的评论</h2>
          </div>
          <div class="empty-state">
            <p>暂无评论</p>
          </div>
        </div>

        <!-- 售后处理页面 -->
        <div v-if="activeTab === 'aftersale'">
          <div class="content-header">
            <h2>🔄 售后处理</h2>
          </div>
          <div v-if="afterSaleList.length === 0" class="empty-state">
            <div class="empty-icon">🔄</div>
            <p>暂无售后记录</p>
          </div>
          <div v-else class="order-list">
            <div v-for="item in afterSaleList" :key="item.id" class="order-card" @click="goToAfterSaleDetailById(item.id)">
              <div class="order-header">
                <span>订单号：{{ item.orderNo }}</span>
                <span class="order-status" :class="afterSaleStatusClass(item.status)">{{ item.statusText }}</span>
              </div>
              <div class="order-content">
                <p>{{ getAfterSaleItemSummary(item) }}</p>
                <p>退款金额：<span style="color:#E53935;font-weight:bold">¥{{ item.refundAmount }}</span></p>
              </div>
              <div class="order-actions">
                <button class="action-btn" @click.stop="goToAfterSaleDetailById(item.id)">查看详情</button>
              </div>
            </div>
          </div>
        </div>

        <!-- 个人设置页面 -->
        <div v-if="activeTab === 'settings'">
          <div class="content-header settings-header">
            <h2>⚙️ 个人设置</h2>
            <button class="edit-profile-btn" @click="handleEditProfile">编辑资料</button>
          </div>
          
          <div class="settings-card">
            <div class="settings-item">
              <label>用户名</label>
              <span>{{ userInfo.username }}</span>
            </div>
            <div class="settings-item">
              <label>昵称</label>
              <div class="item-value">
                <span>{{ userInfo.nickname }}</span>
              </div>
            </div>
            <div class="settings-item">
              <label>手机号</label>
              <div class="item-value">
                <span>{{ userInfo.phone }}</span>
              </div>
            </div>
            <div class="settings-item">
              <label>邮箱</label>
              <div class="item-value">
                <span>{{ userInfo.email || '未设置' }}</span>
              </div>
            </div>
            <div class="settings-item">
              <label>性别</label>
              <div class="item-value">
                <span>{{ getGenderText(userInfo.gender) }}</span>
              </div>
            </div>
          </div>
          
          <div class="settings-buttons">
            <button class="btn-logout" @click="handleLogout">退出登录</button>
          </div>
          
          <!-- 个人信息编辑弹窗 -->
          <div v-if="showProfileDialog" class="dialog-overlay" @click="closeProfileDialog">
            <div class="dialog-content profile-dialog" @click.stop>
              <div class="dialog-header">
                <h3>编辑个人信息</h3>
                <button class="close-btn" @click="closeProfileDialog">×</button>
              </div>
              <div class="dialog-body">
                <div class="form-item">
                  <label>昵称</label>
                  <input v-model="profileForm.nickname" type="text" placeholder="请输入昵称" maxlength="20" />
                </div>
                <div class="form-item">
                  <label>手机号</label>
                  <input v-model="profileForm.phone" type="tel" placeholder="请输入手机号" maxlength="11" />
                </div>
                <div class="form-item">
                  <label>邮箱</label>
                  <input v-model="profileForm.email" type="email" placeholder="请输入邮箱" />
                </div>
                <div class="form-item">
                  <label>性别</label>
                  <div class="gender-options">
                    <label class="radio-label">
                      <input v-model="profileForm.gender" type="radio" :value="0" />
                      <span>未知</span>
                    </label>
                    <label class="radio-label">
                      <input v-model="profileForm.gender" type="radio" :value="1" />
                      <span>男</span>
                    </label>
                    <label class="radio-label">
                      <input v-model="profileForm.gender" type="radio" :value="2" />
                      <span>女</span>
                    </label>
                  </div>
                </div>
              </div>
              <div class="dialog-footer">
                <button class="cancel-btn" @click="closeProfileDialog">取消</button>
                <button class="submit-btn" @click="submitProfile">保存修改</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserInfo, logout, updateUser } from '../api/user'
import { getAddressList, getDefaultAddress, addAddress, updateAddress, setDefaultAddress as setDefaultAddressApi, deleteAddress as deleteAddressApi } from '../api/address'
import { getFavoriteList, removeFavorite } from '../api/favorite'
import { getUserCouponList } from '../api/coupon'
import { getProductDetail } from '../api/product'
import { getOrderList, cancelOrder as cancelOrderApi, confirmOrder as confirmOrderApi } from '../api/order'
import { getAfterSaleList } from '../api/afterSale'
import { getCartList, updateQuantity as updateCartQuantityApi, deleteCart } from '../api/cart'
import { regionOptions, getNameToCode, getCodeToName } from '../data/regions'

// 获取当前登录用户ID
const getUserId = () => {
  const loginUser = JSON.parse(localStorage.getItem('loginUser'))
  return loginUser?.user?.id
}

// 路由
const router = useRouter()
const route = useRoute()

// 监听路由参数变化
watch(
  () => route.query.tab,
  (newTab) => {
    if (newTab) {
      activeTab.value = newTab
      // 如果是地址管理页面，重新加载地址列表
      if (newTab === 'address') {
        loadAddressList()
      }
      // 如果是收藏页面，重新加载收藏列表
      if (newTab === 'favorites') {
        loadFavoriteList()
      }
      // 如果是优惠券页面，重新加载优惠券列表
      if (newTab === 'coupons') {
        loadUserCouponList()
      }
      if (newTab === 'orders') {
        loadOrderList()
      }
      if (newTab === 'cart') {
        loadCartData()
      }
      if (newTab === 'aftersale') {
        loadAfterSaleList()
      }
    }
  }
)

// 响应式数据
const activeTab = ref('orders')
const orderTab = ref('all')
const showAddressDialog = ref(false)
const isEditing = ref(false)
const addressList = ref([])
const regionSelected = ref([])
const addressForm = ref({
  id: null,
  userId: null,
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: false
})
const userInfo = ref({})
const showProfileDialog = ref(false)
const profileForm = ref({
  nickname: '',
  phone: '',
  email: '',
  avatar: '',
  gender: 0
})

// 收藏相关数据
const favoriteList = ref([]) // 收藏记录列表（包含 productId）
const favoriteProducts = ref([]) // 收藏的商品详情列表

// 优惠券相关数据
const userCouponList = ref([]) // 用户优惠券列表

const orderList = ref([])
const afterSaleList = ref([])

// 购物车相关数据
const cartItems = ref([])
const cartTotalPrice = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + Number(item.price || 0) * item.quantity, 0).toFixed(2)
})

// 方法
const goBack = () => {
  router.push('/')
}

const navigateTo = (tab) => {
  // 特殊页面跳转
  if (tab === 'address') {
    activeTab.value = 'address'
    router.push({ path: '/personal', query: { tab: 'address' } })
    return
  }
  // 其他 tab 仍在个人中心内切换
  activeTab.value = tab
  router.push({ path: '/personal', query: { tab } })
}

// 购物车方法
const loadCartData = async () => {
  const userId = getUserId()
  if (!userId) { cartItems.value = []; return }
  try {
    const result = await getCartList(parseInt(userId))
    if (result.code === 1) {
      cartItems.value = result.data || []
    }
  } catch (e) {
    console.error('加载购物车失败:', e)
  }
}

const updateCartQty = async (item, delta) => {
  const newQty = item.quantity + delta
  if (newQty < 1) return
  try {
    const result = await updateCartQuantityApi({ id: item.id, quantity: newQty })
    if (result.code === 1) { loadCartData() }
  } catch (e) {
    console.error('更新数量失败:', e)
  }
}

const removeCartItem = async (id) => {
  try {
    const result = await deleteCart(id)
    if (result.code === 1) {
      ElMessage.success('已删除')
      loadCartData()
    }
  } catch (e) {
    console.error('删除失败:', e)
  }
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const userId = getUserId()
    if (userId) {
      const result = await getUserInfo(userId)
      if (result.code === 1) {
        userInfo.value = result.data
      }
    }
  } catch (error) {
    // 静默处理错误
  }
}

// 加载用户优惠券列表
const loadUserCouponList = async () => {
  try {
    const userId = getUserId()
    if (!userId) {
      ElMessage.warning('请先登录')
      return
    }
    
    const result = await getUserCouponList(parseInt(userId))
    if (result.code === 1) {
      userCouponList.value = result.data || []
    } else {
      ElMessage.error(result.msg || '加载优惠券列表失败')
    }
  } catch (error) {
    console.error('加载优惠券列表失败:', error)
    ElMessage.error('加载优惠券列表失败')
  }
}

// 格式化优惠券状态
const formatCouponStatus = (status) => {
  switch (status) {
    case 0: return '未使用'
    case 1: return '已使用'
    case 2: return '已过期'
    default: return '未知'
  }
}

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN')
}

// 地址管理方法
// 加载地址列表
const loadAddressList = async () => {
  try {
    const userId = getUserId()
    console.log('当前 userId:', userId)
    if (!userId) {
      ElMessage.warning('请先登录')
      return
    }
    
    const result = await getAddressList(userId)
    console.log('地址列表响应:', result)
    if (result.code === 1) {
      addressList.value = result.data || []
      console.log('地址列表数据:', addressList.value)
    } else {
      ElMessage.error(result.msg || '加载地址列表失败')
    }
  } catch (error) {
    console.error('加载地址列表失败:', error)
    ElMessage.error('加载地址列表失败')
  }
}

const formatPhone = (phone) => {
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}



// 设为默认地址
const setDefaultAddress = async (addressId) => {
  try {
    const userId = getUserId()
    if (!userId) {
      ElMessage.warning('请先登录')
      return
    }
    
    const result = await setDefaultAddressApi(userId, addressId)
    if (result.code === 1) {
      ElMessage.success('设置成功')
      // 重新加载地址列表
      await loadAddressList()
      // 触发地址更新事件
      window.dispatchEvent(new Event('addressUpdated'))
    } else {
      ElMessage.error(result.msg || '设置失败')
    }
  } catch (error) {
    console.error('设置默认地址失败:', error)
    ElMessage.error('设置失败')
  }
}

// 编辑地址
const editAddress = (address) => {
  addressForm.value = { 
    ...address,
    userId: getUserId(),
    isDefault: address.isDefault === 1  // 整数转布尔值,用于 checkbox
  }
  if (address.province && address.city && address.district) {
    regionSelected.value = [
      getNameToCode(address.province),
      getNameToCode(address.city),
      getNameToCode(address.district)
    ]
  } else {
    regionSelected.value = []
  }
  isEditing.value = true
  showAddressDialog.value = true
}

// 省市区级联选择变更
const handleRegionChange = (value) => {
  if (value && value.length === 3) {
    addressForm.province = getCodeToName(value[0])
    addressForm.city = getCodeToName(value[1])
    addressForm.district = getCodeToName(value[2])
  } else {
    addressForm.province = ''
    addressForm.city = ''
    addressForm.district = ''
  }
}

// 删除地址
const deleteAddress = async (addressId) => {
  ElMessageBox.confirm('确定要删除该地址吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const result = await deleteAddressApi(addressId)
      if (result.code === 1) {
        ElMessage.success('删除成功')
        // 重新加载地址列表
        await loadAddressList()
        // 触发地址更新事件
        window.dispatchEvent(new Event('addressUpdated'))
      } else {
        ElMessage.error(result.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除地址失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 保存地址
const submitAddress = async () => {
  // 表单验证
  if (!addressForm.value.name || !addressForm.value.phone || !addressForm.value.province || !addressForm.value.detailAddress) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  if (!/^1[3-9]\d{9}$/.test(addressForm.value.phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  
  try {
    // 准备提交数据,确保类型正确
    const submitData = {
      ...addressForm.value,
      userId: getUserId(),  // 转换为数字
      isDefault: addressForm.value.isDefault ? 1 : 0     // 布尔值转整数
    }
    
    // 编辑时不需要传 id,但新增时 id 应该是 null 或不传
    if (!isEditing.value) {
      delete submitData.id
    }
    
    console.log('提交地址数据:', submitData)
    
    let result
    if (isEditing.value) {
      // 修改地址
      result = await updateAddress(submitData)
    } else {
      // 新增地址
      result = await addAddress(submitData)
    }
    
    console.log('地址保存响应:', result)
    
    if (result.code === 1) {
      ElMessage.success(isEditing.value ? '修改成功' : '添加成功')
      showAddressDialog.value = false
      // 重新加载地址列表
      await loadAddressList()
      // 触发地址更新事件
      window.dispatchEvent(new Event('addressUpdated'))
      // 重置表单
      closeAddressDialog()
    } else {
      ElMessage.error(result.msg || (isEditing.value ? '修改失败' : '添加失败'))
    }
  } catch (error) {
    console.error('保存地址失败:', error)
    ElMessage.error(isEditing.value ? '修改失败' : '添加失败')
  }
}

// 重置表单
const resetAddressForm = () => {
  addressForm.value = {
    id: null,
    userId: null,
    name: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    isDefault: false
  }
  regionSelected.value = []
  isEditing.value = false
}

// 关闭对话框
const closeAddressDialog = () => {
  showAddressDialog.value = false
  resetAddressForm()
}

// 获取性别文本
const getGenderText = (gender) => {
  const genderMap = { 0: '未知', 1: '男', 2: '女' }
  return genderMap[gender] || '未知'
}

// 打开编辑个人信息对话框
const handleEditProfile = () => {
  profileForm.value = {
    nickname: userInfo.value.nickname || '',
    phone: userInfo.value.phone || '',
    email: userInfo.value.email || '',
    avatar: userInfo.value.avatar || '',
    gender: userInfo.value.gender || 0
  }
  showProfileDialog.value = true
}

// 关闭个人信息对话框
const closeProfileDialog = () => {
  showProfileDialog.value = false
}

// 提交个人信息更新
const submitProfile = async () => {
  // 表单验证
  if (!profileForm.value.nickname || !profileForm.value.nickname.trim()) {
    ElMessage.warning('请输入昵称')
    return
  }
  
  if (!profileForm.value.phone || !profileForm.value.phone.trim()) {
    ElMessage.warning('请输入手机号')
    return
  }
  
  if (!/^1[3-9]\d{9}$/.test(profileForm.value.phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  
  if (profileForm.value.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(profileForm.value.email)) {
    ElMessage.warning('请输入正确的邮箱地址')
    return
  }
  
  try {
    const userId = getUserId()
    if (!userId) {
      ElMessage.warning('请先登录')
      return
    }
    
    const updateData = {
      id: parseInt(userId),
      nickname: profileForm.value.nickname.trim(),
      phone: profileForm.value.phone.trim(),
      email: profileForm.value.email?.trim() || null,
      avatar: profileForm.value.avatar || null,
      gender: profileForm.value.gender
    }
    
    const result = await updateUser(updateData)
    if (result.code === 1) {
      ElMessage.success('修改成功')
      showProfileDialog.value = false
      // 重新加载用户信息
      await loadUserInfo()
    } else {
      ElMessage.error(result.msg || '修改失败')
    }
  } catch (error) {
    console.error('更新用户信息失败:', error)
    ElMessage.error('修改失败')
  }
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const result = await logout()
    if (result.code === 1) {
      localStorage.removeItem('loginUser')
      ElMessage.success('退出登录成功')
      router.push('/')
    } else {
      ElMessage.error(result.msg || '退出登录失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退出登录失败:', error)
      ElMessage.error('退出登录失败')
    }
  }
}

// ========== 收藏相关方法 ==========

// 加载收藏列表
const loadFavoriteList = async () => {
  try {
    const userId = getUserId()
    if (!userId) return
    
    const result = await getFavoriteList(userId)
    if (result.code === 1) {
      favoriteList.value = result.data || []
      await loadFavoriteProductDetails()
    }
  } catch (error) {
    console.error('加载收藏列表失败:', error)
  }
}

// 加载收藏商品的详情
const loadFavoriteProductDetails = async () => {
  if (favoriteList.value.length === 0) {
    favoriteProducts.value = []
    return
  }
  
  try {
    const productIds = favoriteList.value.map(fav => fav.productId)
    const productPromises = productIds.map(id => getProductDetail(id))
    const results = await Promise.all(productPromises)
    favoriteProducts.value = results
      .filter(r => r.code === 1)
      .map(r => r.data)
  } catch (error) {
    console.error('加载收藏商品详情失败:', error)
  }
}

// 取消收藏
const cancelFavorite = async (productId) => {
  try {
    const userId = getUserId()
    if (!userId) {
      ElMessage.warning('请先登录')
      return
    }
    
    await ElMessageBox.confirm('确定要取消收藏吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const favModule = await import('../api/favorite')
    const result = await favModule.removeFavorite(userId, productId)
    if (result.code === 1) {
      ElMessage.success('已取消收藏')
      await loadFavoriteList()
    } else {
      ElMessage.error(result.msg || '取消收藏失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏失败:', error)
      ElMessage.error('取消收藏失败')
    }
  }
}

// 跳转到优惠券领取页面
const goToCouponPage = () => {
  router.push('/coupon')
}

// 跳转到商品详情
const goToProduct = (productId) => {
  router.push({ path: '/product', query: { id: productId } })
}

// 加载订单列表
const loadOrderList = async () => {
  try {
    const userId = getUserId()
    if (!userId) {
      orderList.value = []
      return
    }

    const result = await getOrderList(parseInt(userId))
    if (result.code === 1) {
      orderList.value = result.data || []
    } else {
      ElMessage.error(result.msg || '加载订单列表失败')
      orderList.value = []
    }
  } catch (error) {
    console.error('加载订单列表失败:', error)
    ElMessage.error('加载订单列表失败')
    orderList.value = []
  }
}

const statusMap = {
  pending: 0,
  shipment: 1,
  receipt: 2,
  completed: 3,
  cancelled: 4
}

const filteredOrders = computed(() => {
  if (orderTab.value === 'all') {
    return orderList.value
  }
  if (orderTab.value === 'aftersale') {
    return orderList.value.filter(order => order.afterSaleStatus === 1)
  }
  const status = statusMap[orderTab.value]
  return orderList.value.filter(order => order.status === status)
})

const orderStats = computed(() => {
  const stats = { pending: 0, shipment: 0, receipt: 0, completed: 0, cancelled: 0, aftersale: 0 }
  orderList.value.forEach(order => {
    if (order.status === 0) stats.pending += 1
    if (order.status === 1) stats.shipment += 1
    if (order.status === 2) stats.receipt += 1
    if (order.status === 3) stats.completed += 1
    if (order.status === 4) stats.cancelled += 1
    if (order.afterSaleStatus === 1) stats.aftersale += 1
  })
  return stats
})

const formatAmount = (value) => {
  const numberValue = typeof value === 'number' ? value : parseFloat(value || 0)
  return Number.isFinite(numberValue) ? numberValue.toFixed(2) : '0.00'
}

const getOrderItemSummary = (order) => {
  if (!order || !Array.isArray(order.items) || order.items.length === 0) {
    return '暂无商品信息'
  }
  const totalCount = order.items.reduce((sum, item) => sum + (item.quantity || 0), 0)
  const firstItem = order.items[0]
  if (order.items.length === 1) {
    return `${firstItem.productName} × ${firstItem.quantity}`
  }
  return `${firstItem.productName} 等${totalCount}件`
}

const formatOrderStatus = (status) => {
  switch (status) {
    case 0: return '待付款'
    case 1: return '待发货'
    case 2: return '待收货'
    case 3: return '已完成'
    case 4: return '已取消'
    default: return '未知'
  }
}

const statusClass = (status) => {
  switch (status) {
    case 0: return 'status-pending'
    case 1: return 'status-shipping'
    case 2: return 'status-received'
    case 3: return 'status-completed'
    case 4: return 'status-cancelled'
    default: return ''
  }
}

// 跳转到订单详情
const goToOrderDetail = (orderId) => {
  router.push({ path: '/order/detail', query: { id: orderId } })
}

// 跳转到支付页面
const goToPay = (orderId) => {
  router.push(`/payment/${orderId}`)
}

// 取消订单
const cancelOrderAction = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const userId = getUserId()
    if (!userId) {
      ElMessage.warning('请先登录')
      return
    }

    const result = await cancelOrderApi(userId, orderId)
    if (result.code === 1) {
      ElMessage.success('订单已取消')
      await loadOrderList()
    } else {
      ElMessage.error(result.msg || '取消订单失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  }
}

const loadAfterSaleList = async () => {
  const userId = getUserId()
  if (!userId) return
  try {
    const result = await getAfterSaleList(parseInt(userId))
    if (result.code === 1) {
      afterSaleList.value = result.data || []
    }
  } catch (e) {
    console.error('加载售后列表失败:', e)
  }
}

const afterSaleStatusClass = (status) => {
  switch (status) {
    case 0: return 'status-pending'
    case 1: return 'status-completed'
    case 2: return 'status-cancelled'
    case 3: return 'status-completed'
    default: return ''
  }
}

const getAfterSaleItemSummary = (item) => {
  if (!item.items || item.items.length === 0) return '暂无商品信息'
  const first = item.items[0]
  if (item.items.length === 1) return first.productName
  return `${first.productName} 等${item.items.length}件商品`
}

const goToAfterSale = (orderId) => {
  router.push({ path: '/after-sale/apply', query: { orderId } })
}

const goToAfterSaleDetail = async (orderId) => {
  const userId = getUserId()
  if (!userId) return
  try {
    const result = await getAfterSaleList(parseInt(userId))
    if (result.code === 1) {
      const found = (result.data || []).find(a => a.orderId === orderId)
      if (found) {
        router.push({ path: '/after-sale/detail', query: { id: found.id } })
      }
    }
  } catch (e) {
    console.error(e)
  }
}

const goToAfterSaleDetailById = (id) => {
  router.push({ path: '/after-sale/detail', query: { id } })
}

const confirmOrderAction = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定已收到商品吗？', '确认收货', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    })
    const userId = getUserId()
    const result = await confirmOrderApi(orderId, parseInt(userId))
    if (result.code === 1) {
      ElMessage.success('已确认收货')
      await loadOrderList()
    } else {
      ElMessage.error(result.msg || '确认收货失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      console.error('确认收货失败:', e)
    }
  }
}

onMounted(() => {
  // 从路由参数获取当前 tab
  if (route.query.tab) {
    activeTab.value = route.query.tab
  }

  // 加载基础数据
  loadUserInfo()

  if (activeTab.value === 'address') {
    loadAddressList()
  }
  if (activeTab.value === 'favorites') {
    loadFavoriteList()
  }
  if (activeTab.value === 'coupons') {
    loadUserCouponList()
  }
  if (activeTab.value === 'orders') {
    loadOrderList()
  }
  if (activeTab.value === 'cart') {
    loadCartData()
  }
  if (activeTab.value === 'aftersale') {
    loadAfterSaleList()
  }

  window.addEventListener('addressUpdated', loadAddressList)
})

onBeforeUnmount(() => {
  window.removeEventListener('addressUpdated', loadAddressList)
})
</script>

<style scoped>
.personal-container {
          height: 100vh;
          background-color: var(--color-bg, #f5f5f5);
          display: flex;
          flex-direction: column;
          overflow: hidden;
        }

        .main-container {
          display: flex;
          flex: 1;
          max-width: 1060px;
          margin: 0 auto;
          padding: 16px 20px;
          gap: 16px;
          width: 100%;
          min-height: 0;
        }

        .side-nav {
          width: 220px;
          flex-shrink: 0;
          background: var(--color-bg-white, #fff);
          border-radius: var(--radius-lg, 12px);
          padding: 0;
          box-shadow: var(--shadow-sm, 0 1px 4px rgba(0,0,0,0.06));
          overflow: hidden;
          border: 1px solid var(--color-border-light, #f2f6fc);
          position: sticky;
          top: 16px;
          align-self: flex-start;
          max-height: calc(100vh - 32px);
          overflow-y: auto;
        }

        .back-home {
          cursor: pointer;
          padding: 10px 16px;
          margin: 0;
          font-size: var(--text-sm, 13px);
          color: var(--color-text-secondary, #606266);
          transition: background var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1)),
                      color var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1));
          border-bottom: 1px solid var(--color-border-light, #f2f6fc);
        }

        .back-home:hover {
          background-color: var(--color-bg-hover, #f8f8f8);
          color: var(--color-primary, #E53935);
        }

        .user-profile {
          text-align: center;
          padding: 20px 16px 16px;
          border-bottom: 1px solid var(--color-border-light);
          background: linear-gradient(180deg, var(--color-primary-light) 0%, var(--color-bg-white) 100%);
          position: relative;
        }

        .avatar {
          width: 64px;
          height: 64px;
          border-radius: 50%;
          margin: 0 auto 8px;
          overflow: hidden;
          background-color: var(--color-bg);
          display: flex;
          align-items: center;
          justify-content: center;
          border: 3px solid var(--color-bg-white);
          box-shadow: 0 2px 12px rgba(229, 57, 53, 0.15);
        }

        .avatar-image {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .avatar-placeholder {
          font-size: 22px;
          font-weight: bold;
          color: var(--color-text-tertiary, #909399);
        }

        .user-info h3 {
          margin: 0 0 2px 0;
          font-size: var(--text-base, 14px);
          font-weight: 600;
          color: var(--color-text-primary, #1A1A1A);
        }

        .user-info p {
          margin: 0;
          color: var(--color-text-tertiary, #909399);
          font-size: var(--text-xs, 12px);
        }

        .nav-menu {
          display: flex;
          flex-direction: column;
          padding: 6px 8px;
        }

        .nav-item {
          display: flex;
          align-items: center;
          padding: 9px 12px;
          border-radius: var(--radius-md, 8px);
          cursor: pointer;
          font-size: var(--text-sm, 13px);
          color: var(--color-text-secondary, #606266);
          transition: background var(--duration-fast, 150ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1)),
                      color var(--duration-fast, 150ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1)),
                      transform var(--duration-fast, 150ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1));
        }

        .nav-item:hover {
          background-color: var(--color-bg-hover, #f8f8f8);
          color: var(--color-text-primary, #1A1A1A);
          transform: translateX(2px);
        }

        .nav-item.active {
          background-color: var(--color-primary-light, #FFEBEE);
          color: var(--color-primary, #E53935);
          font-weight: 600;
        }

        .nav-icon {
          margin-right: 8px;
          font-size: 16px;
        }

        .content-area {
          flex: 1;
          min-width: 0;
          background: var(--color-bg-white, #fff);
          border-radius: var(--radius-lg, 12px);
          padding: 16px 20px;
          box-shadow: var(--shadow-sm, 0 1px 4px rgba(0,0,0,0.06));
          border: 1px solid var(--color-border-light, #f2f6fc);
          overflow-y: auto;
        }

        .content-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 14px;
          padding-bottom: 12px;
          border-bottom: 1px solid var(--color-border-light, #f2f6fc);
        }

        .content-header h2 {
          margin: 0;
          font-size: var(--text-xl, 18px);
          font-weight: 600;
          color: var(--color-text-primary, #1A1A1A);
        }

        .add-address-btn {
          background: var(--color-primary, #E53935);
          color: white;
          border: none;
          padding: 7px 14px;
          border-radius: var(--radius-md, 8px);
          cursor: pointer;
          display: flex;
          align-items: center;
          gap: 4px;
          font-size: var(--text-sm, 13px);
          font-weight: 500;
          transition: background var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1)),
                      box-shadow var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1)),
                      transform var(--duration-fast, 150ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1));
        }

        .add-address-btn:hover {
          background: var(--color-primary-hover, #D32F2F);
          box-shadow: var(--shadow-primary, 0 4px 14px rgba(229,57,53,0.25));
        }

        .add-address-btn:active {
          transform: scale(0.97);
        }

        .stats-section {
          display: grid;
          grid-template-columns: repeat(5, 1fr);
          gap: 10px;
          margin-bottom: 16px;
        }

        .stat-card {
          text-align: center;
          padding: 14px 8px;
          background: var(--color-bg-white);
          border-radius: var(--radius-md);
          border: 1px solid var(--color-border-light);
          transition: transform var(--duration-fast) var(--ease-in-out),
                      box-shadow var(--duration-normal) var(--ease-in-out),
                      border-color var(--duration-normal) var(--ease-in-out);
          cursor: pointer;
        }

        .stat-card:hover {
          transform: translateY(-2px);
          box-shadow: var(--shadow-sm);
          border-color: var(--color-primary-light);
        }

        .stat-value {
          font-size: 22px;
          font-weight: 700;
          color: var(--color-primary, #E53935);
          margin-bottom: 4px;
          line-height: 1.2;
        }

        .stat-label {
          font-size: var(--text-xs, 12px);
          color: var(--color-text-tertiary, #909399);
        }

        .order-tabs {
          display: flex;
          gap: 2px;
          margin-bottom: 12px;
          border-bottom: 1px solid var(--color-border-light, #f2f6fc);
          padding-bottom: 0;
        }

        .order-tab {
          padding: 8px 14px;
          cursor: pointer;
          border-radius: var(--radius-md, 8px) var(--radius-md, 8px) 0 0;
          font-size: var(--text-sm, 13px);
          color: var(--color-text-secondary, #606266);
          transition: color var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1)),
                      background var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1));
          position: relative;
        }

        .order-tab:hover {
          color: var(--color-text-primary, #1A1A1A);
          background-color: var(--color-bg-hover, #f8f8f8);
        }

        .order-tab.active {
          background-color: var(--color-primary);
          color: white;
          font-weight: 600;
          box-shadow: 0 2px 6px rgba(229, 57, 53, 0.2);
        }

        .order-list {
          display: flex;
          flex-direction: column;
          gap: 10px;
        }

        .order-card {
          border: 1px solid var(--color-border-light);
          border-radius: var(--radius-md);
          padding: 12px 14px;
          cursor: pointer;
          transition: box-shadow var(--duration-normal) var(--ease-in-out),
                      transform var(--duration-fast) var(--ease-in-out),
                      border-color var(--duration-normal) var(--ease-in-out);
          position: relative;
        }

        .order-card::before {
          content: '';
          position: absolute;
          left: 0;
          top: 8px;
          bottom: 8px;
          width: 3px;
          background: var(--color-primary);
          border-radius: 2px;
          opacity: 0;
          transition: opacity var(--duration-normal) var(--ease-in-out);
        }

        .order-card:hover {
          box-shadow: var(--shadow-sm);
          transform: translateY(-1px);
          border-color: var(--color-border-hover);
        }

        .order-card:hover::before {
          opacity: 1;
        }

        .order-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;
          font-size: var(--text-sm, 13px);
          color: var(--color-text-tertiary, #909399);
        }

        .order-status {
          padding: 2px 8px;
          border-radius: var(--radius-full, 9999px);
          font-size: var(--text-xs, 12px);
          font-weight: 500;
        }

        .status-pending {
          background: var(--color-warning-light, #FFF3E0);
          color: var(--color-warning, #FB8C00);
        }

        .status-shipping {
          background: var(--color-info-light, #E3F2FD);
          color: var(--color-info, #1E88E5);
        }

        .status-received {
          background: var(--color-success-light, #E8F5E9);
          color: var(--color-success, #43A047);
        }

        .status-completed {
          background: var(--color-bg-stripe, #FAFAFA);
          color: var(--color-text-tertiary, #909399);
        }

        .status-cancelled {
          background: var(--color-danger-light, #FFEBEE);
          color: var(--color-danger, #E53935);
        }

        .order-content {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 10px;
          font-size: var(--text-base, 14px);
          color: var(--color-text-regular, #333);
        }

        .order-content p {
          margin: 0;
        }

        .order-actions {
          display: flex;
          gap: 8px;
          justify-content: flex-end;
        }

        .action-btn {
          padding: 5px 12px;
          border: 1px solid var(--color-border, #EBEEF5);
          background: var(--color-bg-white, #fff);
          border-radius: var(--radius-md, 8px);
          cursor: pointer;
          font-size: var(--text-sm, 13px);
          color: var(--color-text-secondary, #606266);
          transition: all var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1));
        }

        .action-btn:hover {
          border-color: var(--color-primary, #E53935);
          color: var(--color-primary, #E53935);
        }

        .action-btn:active {
          transform: scale(0.97);
        }

        .action-btn.primary {
          background: var(--color-primary, #E53935);
          color: white;
          border-color: var(--color-primary, #E53935);
        }

        .action-btn.primary:hover {
          background: var(--color-primary-hover, #D32F2F);
          border-color: var(--color-primary-hover, #D32F2F);
          box-shadow: var(--shadow-primary, 0 4px 14px rgba(229,57,53,0.25));
        }

        .action-btn.primary:active {
          transform: scale(0.97);
        }

        .action-btn.delete {
          color: var(--color-danger, #E53935);
          border-color: var(--color-danger, #E53935);
        }

        .action-btn.delete:hover {
          background: var(--color-danger, #E53935);
          color: white;
        }

        .empty-state {
          text-align: center;
          padding: 40px 20px;
          color: var(--color-text-tertiary, #909399);
        }

        .empty-icon {
          font-size: 40px;
          margin-bottom: 12px;
          opacity: 0.6;
        }

        .empty-state p {
          margin: 0;
          font-size: var(--text-sm, 13px);
        }

        .action-btn-primary {
          background: var(--color-primary, #E53935);
          color: white;
          border: none;
          padding: 8px 18px;
          border-radius: var(--radius-md, 8px);
          cursor: pointer;
          margin-top: 12px;
          font-size: var(--text-sm, 13px);
          font-weight: 500;
          transition: background var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1)),
                      box-shadow var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1)),
                      transform var(--duration-fast, 150ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1));
        }

        .action-btn-primary:hover {
          background: var(--color-primary-hover, #D32F2F);
          box-shadow: var(--shadow-primary, 0 4px 14px rgba(229,57,53,0.25));
        }

        .action-btn-primary:active {
          transform: scale(0.97);
        }

        .favorite-grid {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(170px, 1fr));
          gap: 12px;
        }

        .favorite-card {
          border: 1px solid var(--color-border-light, #f2f6fc);
          border-radius: var(--radius-md, 8px);
          overflow: hidden;
          cursor: pointer;
          transition: transform var(--duration-normal, 200ms) var(--ease-out, cubic-bezier(0,0,0.2,1)),
                      box-shadow var(--duration-normal, 200ms) var(--ease-out, cubic-bezier(0,0,0.2,1));
        }

        .favorite-card:hover {
          transform: translateY(-3px);
          box-shadow: var(--shadow-md, 0 4px 12px rgba(0,0,0,0.08));
        }

        .favorite-image {
          width: 100%;
          height: 140px;
          overflow: hidden;
          background: var(--color-bg, #f5f5f5);
        }

        .favorite-image img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: transform var(--duration-slow, 300ms) var(--ease-out, cubic-bezier(0,0,0.2,1));
        }

        .favorite-card:hover .favorite-image img {
          transform: scale(1.05);
        }

        .favorite-info {
          padding: 10px 12px;
        }

        .favorite-name {
          margin: 0 0 6px 0;
          font-size: var(--text-sm, 13px);
          font-weight: 500;
          height: 36px;
          overflow: hidden;
          text-overflow: ellipsis;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          color: var(--color-text-primary, #1A1A1A);
          line-height: 1.4;
        }

        .favorite-price {
          margin: 0 0 8px 0;
          color: var(--color-primary, #E53935);
          font-weight: 600;
          font-size: var(--text-base, 14px);
        }

        .cancel-fav-btn {
          width: 100%;
          padding: 5px;
          background: var(--color-bg, #f5f5f5);
          border: none;
          border-radius: var(--radius-md, 8px);
          cursor: pointer;
          font-size: var(--text-xs, 12px);
          color: var(--color-text-tertiary, #909399);
          transition: background var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1)),
                      color var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1));
        }

        .cancel-fav-btn:hover {
          background: var(--color-danger-light, #FFEBEE);
          color: var(--color-danger, #E53935);
        }

        .coupon-list {
          display: flex;
          flex-direction: column;
          gap: 10px;
        }

        .coupon-item {
          display: flex;
          border: 1px solid var(--color-border-light, #f2f6fc);
          border-radius: var(--radius-md, 8px);
          overflow: hidden;
          transition: box-shadow var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1));
        }

        .coupon-item:hover {
          box-shadow: var(--shadow-sm, 0 1px 4px rgba(0,0,0,0.06));
        }

        .coupon-left {
          width: 110px;
          background: var(--color-primary, #E53935);
          color: white;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          padding: 12px;
          flex-shrink: 0;
        }

        .coupon-amount {
          display: flex;
          align-items: baseline;
        }

        .amount-symbol {
          font-size: var(--text-sm, 13px);
        }

        .amount-value {
          font-size: 22px;
          font-weight: 700;
        }

        .coupon-condition {
          font-size: var(--text-xs, 12px);
          margin-top: 4px;
          opacity: 0.85;
        }

        .coupon-right {
          flex: 1;
          padding: 12px 14px;
          display: flex;
          flex-direction: column;
          justify-content: center;
        }

        .coupon-name {
          margin: 0 0 6px 0;
          font-size: var(--text-base, 14px);
          font-weight: 500;
          color: var(--color-text-primary, #1A1A1A);
        }

        .coupon-info {
          display: flex;
          justify-content: space-between;
          align-items: center;
        }

        .coupon-time {
          font-size: var(--text-xs, 12px);
          color: var(--color-text-tertiary, #909399);
        }

        .coupon-status {
          padding: 2px 8px;
          border-radius: var(--radius-full, 9999px);
          font-size: var(--text-xs, 12px);
          font-weight: 500;
        }

        .status-unused {
          background: var(--color-success-light, #E8F5E9);
          color: var(--color-success, #43A047);
        }

        .status-used {
          background: var(--color-bg-stripe, #FAFAFA);
          color: var(--color-text-tertiary, #909399);
        }

        .status-expired {
          background: var(--color-danger-light, #FFEBEE);
          color: var(--color-danger, #E53935);
        }

        .address-list {
          display: flex;
          flex-direction: column;
          gap: 10px;
        }

        .address-item {
          border: 1px solid var(--color-border-light, #f2f6fc);
          border-radius: var(--radius-md, 8px);
          padding: 12px 14px;
          transition: box-shadow var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1));
        }

        .address-item:hover {
          box-shadow: var(--shadow-xs, 0 1px 2px rgba(0,0,0,0.04));
        }

        .address-item.default {
          border-color: var(--color-primary, #E53935);
          background: var(--color-primary-lighter, #FFF5F5);
        }

        .address-info {
          margin-bottom: 8px;
        }

        .user-info {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 4px;
        }

        .name {
          font-weight: 600;
          font-size: var(--text-base, 14px);
          color: var(--color-text-primary, #1A1A1A);
        }

        .phone {
          color: var(--color-text-tertiary, #909399);
          font-size: var(--text-sm, 13px);
        }

        .default-tag {
          background: var(--color-primary, #E53935);
          color: white;
          padding: 1px 6px;
          border-radius: var(--radius-sm, 4px);
          font-size: var(--text-xs, 12px);
        }

        .address-detail {
          color: var(--color-text-secondary, #606266);
          font-size: var(--text-sm, 13px);
          line-height: 1.5;
        }

        .address-actions {
          display: flex;
          gap: 8px;
          justify-content: flex-end;
        }

        .dialog-overlay {
          position: fixed;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: var(--color-bg-overlay, rgba(0,0,0,0.45));
          display: flex;
          align-items: center;
          justify-content: center;
          z-index: 1000;
        }

        .dialog-content {
          background: var(--color-bg-white, #fff);
          border-radius: var(--radius-lg, 12px);
          width: 90%;
          max-width: 460px;
          max-height: 90vh;
          overflow-y: auto;
          box-shadow: var(--shadow-lg, 0 8px 24px rgba(0,0,0,0.1));
        }

        .dialog-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 14px 20px;
          border-bottom: 1px solid var(--color-border-light, #f2f6fc);
        }

        .dialog-header h3 {
          margin: 0;
          font-size: var(--text-lg, 16px);
          font-weight: 600;
          color: var(--color-text-primary, #1A1A1A);
        }

        .close-btn {
          background: none;
          border: none;
          font-size: 22px;
          cursor: pointer;
          padding: 0;
          width: 30px;
          height: 30px;
          display: flex;
          align-items: center;
          justify-content: center;
          color: var(--color-text-tertiary, #909399);
          border-radius: var(--radius-sm, 4px);
          transition: color var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1)),
                      transform var(--duration-fast, 150ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1));
        }

        .close-btn:hover {
          color: var(--color-text-primary, #1A1A1A);
          transform: rotate(90deg);
        }

        .dialog-body {
          padding: 16px 20px;
        }

        .form-item {
          margin-bottom: 14px;
        }

        .form-item label {
          display: block;
          margin-bottom: 5px;
          font-weight: 500;
          font-size: var(--text-sm, 13px);
          color: var(--color-text-secondary, #606266);
        }

        .required {
          color: var(--color-danger, #E53935);
        }

        .form-item input,
        .form-item textarea {
          width: 100%;
          padding: 8px 12px;
          border: 1px solid var(--color-border, #EBEEF5);
          border-radius: var(--radius-md, 8px);
          font-size: var(--text-sm, 13px);
          transition: border-color var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1)),
                      box-shadow var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1));
        }

        .form-item input:focus,
        .form-item textarea:focus {
          outline: none;
          border-color: var(--color-primary, #E53935);
          box-shadow: 0 0 0 2px rgba(229, 57, 53, 0.12);
        }

        .checkbox-label {
          display: flex;
          align-items: center;
          gap: 6px;
          cursor: pointer;
          font-size: var(--text-sm, 13px);
        }

        .dialog-footer {
          display: flex;
          justify-content: flex-end;
          gap: 8px;
          padding: 12px 20px;
          border-top: 1px solid var(--color-border-light, #f2f6fc);
        }

        .cancel-btn {
          padding: 7px 16px;
          border: 1px solid var(--color-border, #EBEEF5);
          background: var(--color-bg-white, #fff);
          border-radius: var(--radius-md, 8px);
          cursor: pointer;
          font-size: var(--text-sm, 13px);
          color: var(--color-text-secondary, #606266);
          transition: all var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1));
        }

        .cancel-btn:hover {
          border-color: var(--color-text-tertiary, #909399);
          color: var(--color-text-primary, #1A1A1A);
        }

        .cancel-btn:active {
          transform: scale(0.97);
        }

        .submit-btn {
          padding: 7px 16px;
          background: var(--color-primary, #E53935);
          color: white;
          border: none;
          border-radius: var(--radius-md, 8px);
          cursor: pointer;
          font-size: var(--text-sm, 13px);
          font-weight: 500;
          transition: background var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1)),
                      box-shadow var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1)),
                      transform var(--duration-fast, 150ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1));
        }

        .submit-btn:hover {
          background: var(--color-primary-hover, #D32F2F);
          box-shadow: var(--shadow-primary, 0 4px 14px rgba(229,57,53,0.25));
        }

        .submit-btn:active {
          transform: scale(0.97);
        }

        .settings-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
        }

        .edit-profile-btn {
          padding: 7px 14px;
          background: var(--color-primary, #E53935);
          color: white;
          border: none;
          border-radius: var(--radius-md, 8px);
          cursor: pointer;
          font-size: var(--text-sm, 13px);
          font-weight: 500;
          transition: background var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1)),
                      box-shadow var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1)),
                      transform var(--duration-fast, 150ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1));
        }

        .edit-profile-btn:hover {
          background: var(--color-primary-hover, #D32F2F);
          box-shadow: var(--shadow-primary, 0 4px 14px rgba(229,57,53,0.25));
        }

        .edit-profile-btn:active {
          transform: scale(0.97);
        }

        .settings-card {
          border: 1px solid var(--color-border-light, #f2f6fc);
          border-radius: var(--radius-md, 8px);
          padding: 4px 0;
          margin-bottom: 16px;
        }

        .settings-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 10px 16px;
          border-bottom: 1px solid var(--color-border-light, #f2f6fc);
          transition: background var(--duration-fast, 150ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1));
        }

        .settings-item:last-child {
          border-bottom: none;
        }

        .settings-item:hover {
          background: var(--color-bg-hover, #f8f8f8);
        }

        .settings-item label {
          font-weight: 500;
          color: var(--color-text-tertiary, #909399);
          font-size: var(--text-sm, 13px);
          flex-shrink: 0;
        }

        .item-value {
          display: flex;
          align-items: center;
          gap: 8px;
          color: var(--color-text-primary, #1A1A1A);
          font-size: var(--text-sm, 13px);
        }

        .gender-options {
          display: flex;
          gap: 16px;
        }

        .radio-label {
          display: flex;
          align-items: center;
          gap: 4px;
          cursor: pointer;
          font-size: var(--text-sm, 13px);
        }

        .settings-buttons {
          display: flex;
          justify-content: center;
          padding-top: 4px;
        }

        .btn-logout {
          padding: 8px 24px;
          background: var(--color-bg-white, #fff);
          color: var(--color-danger, #E53935);
          border: 1px solid var(--color-danger, #E53935);
          border-radius: var(--radius-md, 8px);
          cursor: pointer;
          font-size: var(--text-sm, 13px);
          font-weight: 500;
          transition: all var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1));
        }

        .btn-logout:hover {
          background: var(--color-danger, #E53935);
          color: white;
        }

        .btn-logout:active {
          transform: scale(0.97);
        }

        .profile-dialog {
          max-width: 400px;
        }

        /* 购物车样式 */
        .cart-list {
          display: flex;
          flex-direction: column;
          gap: 12px;
        }

        .cart-item {
          display: flex;
          align-items: center;
          gap: 16px;
          padding: 16px;
          border: 1px solid var(--color-border-light, #f2f6fc);
          border-radius: var(--radius-md, 8px);
          background: var(--color-bg-white, #fff);
          transition: box-shadow var(--duration-normal, 200ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1));
        }

        .cart-item:hover {
          box-shadow: var(--shadow-sm, 0 1px 4px rgba(0,0,0,0.06));
        }

        .cart-item-image {
          width: 80px;
          height: 80px;
          border-radius: var(--radius-md, 8px);
          overflow: hidden;
          flex-shrink: 0;
          cursor: pointer;
          background: var(--color-bg, #f5f5f5);
        }

        .cart-item-image img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .cart-item-info {
          flex: 1;
          min-width: 0;
        }

        .cart-item-name {
          font-size: var(--text-base, 14px);
          font-weight: 500;
          color: var(--color-text-primary, #1A1A1A);
          margin: 0 0 6px;
          cursor: pointer;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .cart-item-name:hover {
          color: var(--color-primary, #E53935);
        }

        .cart-item-price {
          font-size: var(--text-lg, 16px);
          color: var(--color-primary, #E53935);
          font-weight: 600;
          margin: 0;
        }

        .cart-item-qty {
          display: flex;
          align-items: center;
          gap: 8px;
          flex-shrink: 0;
        }

        .cart-item-qty .qty-btn {
          width: 28px;
          height: 28px;
          border: 1px solid var(--color-border, #EBEEF5);
          background: var(--color-bg-white, #fff);
          border-radius: var(--radius-sm, 4px);
          cursor: pointer;
          font-size: 16px;
          color: var(--color-text-secondary, #606266);
          display: flex;
          align-items: center;
          justify-content: center;
          transition: all var(--duration-fast, 150ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1));
        }

        .cart-item-qty .qty-btn:hover:not(:disabled) {
          border-color: var(--color-primary, #E53935);
          color: var(--color-primary, #E53935);
        }

        .cart-item-qty .qty-btn:disabled {
          opacity: 0.3;
          cursor: not-allowed;
        }

        .cart-item-qty .qty-value {
          font-size: var(--text-base, 14px);
          font-weight: 500;
          min-width: 24px;
          text-align: center;
        }

        .cart-item-subtotal {
          font-size: var(--text-lg, 16px);
          color: var(--color-primary, #E53935);
          font-weight: 600;
          min-width: 80px;
          text-align: right;
          flex-shrink: 0;
        }

        .cart-item-delete {
          width: 28px;
          height: 28px;
          border: none;
          background: transparent;
          border-radius: var(--radius-full, 9999px);
          cursor: pointer;
          color: var(--color-text-tertiary, #909399);
          font-size: 18px;
          display: flex;
          align-items: center;
          justify-content: center;
          transition: all var(--duration-fast, 150ms) var(--ease-in-out, cubic-bezier(0.4,0,0.2,1));
          flex-shrink: 0;
        }

        .cart-item-delete:hover {
          background: var(--color-danger-light, #FFEBEE);
          color: var(--color-danger, #E53935);
        }

        .cart-summary {
          display: flex;
          align-items: center;
          justify-content: flex-end;
          gap: 20px;
          margin-top: 20px;
          padding: 16px 0;
          border-top: 1px solid var(--color-border-light, #f2f6fc);
          font-size: var(--text-base, 14px);
          color: var(--color-text-secondary, #606266);
        }

        .cart-summary strong {
          color: var(--color-primary, #E53935);
          font-size: var(--text-xl, 18px);
          font-weight: 700;
        }
        </style>
