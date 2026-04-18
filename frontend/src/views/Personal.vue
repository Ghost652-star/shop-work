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
          <div class="nav-item" :class="{ active: activeTab === 'orders' }" @click="navigateTo('orders')">
            <span class="nav-icon">📦</span>
            <span>我的订单</span>
          </div>
          <div class="nav-item" :class="{ active: activeTab === 'favorites' }" @click="navigateTo('favorites')">

            <span>我的收藏</span>
          </div>
          <div class="nav-item" :class="{ active: activeTab === 'coupons' }" @click="navigateTo('coupons')">

            <span>优惠券</span>
          </div>
          <div class="nav-item" :class="{ active: activeTab === 'address' }" @click="navigateTo('address')">

            <span>地址管理</span>
          </div>
          <div class="nav-item" :class="{ active: activeTab === 'reviews' }" @click="navigateTo('reviews')">

            <span>我的评论</span>
          </div>
          <div class="nav-item" :class="{ active: activeTab === 'settings' }" @click="navigateTo('settings')">

            <span>个人设置</span>
          </div>
        </div>
      </div>
      
      <!-- 右侧内容区 -->
      <div class="content-area">
        <!-- 我的订单页面 -->
        <div v-if="activeTab === 'orders'">
          <div class="content-header">

          </div>
          
          <!-- 统计卡片 -->
          <div class="stats-section">
            <div class="stat-card">
              <div class="stat-value">12</div>
              <div class="stat-label">待付款</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">5</div>
              <div class="stat-label">待发货</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">3</div>
              <div class="stat-label">待收货</div>
            </div>
            <div class="stat-card">
              <div class="stat-value">8</div>
              <div class="stat-label">待评价</div>
            </div>
          </div>
          
          <!-- 订单导航栏 -->
          <div class="order-tabs">
            <div class="order-tab" :class="{ active: orderTab === 'all' }" @click="orderTab = 'all'">全部订单</div>
            <div class="order-tab" :class="{ active: orderTab === 'pending' }" @click="orderTab = 'pending'">待支付</div>
            <div class="order-tab" :class="{ active: orderTab === 'shipment' }" @click="orderTab = 'shipment'">待发货</div>
            <div class="order-tab" :class="{ active: orderTab === 'receipt' }" @click="orderTab = 'receipt'">待收货</div>
            <div class="order-tab" :class="{ active: orderTab === 'completed' }" @click="orderTab = 'completed'">已完成</div>
          </div>
          
          <!-- 订单列表 -->
          <div class="order-list">
            <div class="order-card">
              <div class="order-header">
                <span>订单号：20240408123456</span>
                <span class="order-status">已完成</span>
              </div>
              <div class="order-content">
                <p>智能手机 × 1</p>
                <p>¥2999.00</p>
              </div>
            </div>
            <div class="order-card">
              <div class="order-header">
                <span>订单号：20240407987654</span>
                <span class="order-status">待发货</span>
              </div>
              <div class="order-content">
                <p>无线耳机 × 2</p>
                <p>¥598.00</p>
              </div>
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
          <div class="empty-state">
            <p>暂无优惠券</p>
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
                  <div class="region-input">
                    <input v-model="addressForm.province" type="text" placeholder="请输入省份" />
                    <input v-model="addressForm.city" type="text" placeholder="请输入城市" />
                    <input v-model="addressForm.district" type="text" placeholder="请输入区县" />
                  </div>
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

        <!-- 个人设置页面 -->
        <div v-if="activeTab === 'settings'">
          <div class="content-header">
            <h2>⚙️ 个人设置</h2>
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
                <button class="edit-icon" @click="handleEditProfile">✏️</button>
              </div>
            </div>
            <div class="settings-item">
              <label>手机号</label>
              <div class="item-value">
                <span>{{ userInfo.phone }}</span>
                <button class="edit-icon" @click="handleEditProfile">✏️</button>
              </div>
            </div>
            <div class="settings-item">
              <label>邮箱</label>
              <div class="item-value">
                <span>{{ userInfo.email || '未设置' }}</span>
                <button class="edit-icon" @click="handleEditProfile">✏️</button>
              </div>
            </div>
            <div class="settings-item">
              <label>性别</label>
              <div class="item-value">
                <span>{{ getGenderText(userInfo.gender) }}</span>
                <button class="edit-icon" @click="handleEditProfile">✏️</button>
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
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserInfo, logout, updateUser } from '../api/user'
import { getAddressList, getDefaultAddress, addAddress, updateAddress, setDefaultAddress as setDefaultAddressApi, deleteAddress as deleteAddressApi } from '../api/address'
import { getFavoriteList } from '../api/favorite'
import { getProductDetail } from '../api/product'

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
    }
  }
)

// 响应式数据
const activeTab = ref('orders')
const orderTab = ref('all')
const showAddressDialog = ref(false)
const isEditing = ref(false)
const addressList = ref([])
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

// 方法
const goBack = () => {
  router.push('/')
}

const navigateTo = (tab) => {
  // 特殊页面跳转
  if (tab === 'orders') {
    router.push('/orders')
    return
  }
  if (tab === 'address') {
    activeTab.value = 'address'
    router.push({ path: '/personal', query: { tab: 'address' } })
    return
  }
  // 其他 tab 仍在个人中心内切换
  activeTab.value = tab
  router.push({ path: '/personal', query: { tab } })
}

// 加载用户信息
const loadUserInfo = async () => {
  try {
    const userId = localStorage.getItem('userId')
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

// 地址管理方法
// 加载地址列表
const loadAddressList = async () => {
  try {
    const userId = localStorage.getItem('userId')
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
    const userId = localStorage.getItem('userId')
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
    userId: parseInt(localStorage.getItem('userId')),
    isDefault: address.isDefault === 1  // 整数转布尔值,用于 checkbox
  }
  isEditing.value = true
  showAddressDialog.value = true
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
      userId: parseInt(localStorage.getItem('userId')),  // 转换为数字
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
    const userId = localStorage.getItem('userId')
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
      localStorage.removeItem('isLoggedIn')
      localStorage.removeItem('userNickname')
      localStorage.removeItem('userId')
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
    const userId = localStorage.getItem('userId')
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
    const userId = localStorage.getItem('userId')
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

// 跳转到商品详情
const goToProduct = (productId) => {
  router.push({ path: '/product', query: { id: productId } })
}

// 生命周期
onMounted(() => {
  // 从路由参数获取当前 tab
  if (route.query.tab) {
    activeTab.value = route.query.tab
  }
  
  // 加载用户信息
  loadUserInfo()
  
  // 如果是地址管理页面，加载地址列表
  if (activeTab.value === 'address') {
    loadAddressList()
  }
  
  // 如果是收藏页面，加载收藏列表
  if (activeTab.value === 'favorites') {
    loadFavoriteList()
  }
  
  // 监听购物车事件（如果有的话）
  window.addEventListener('addressUpdated', loadAddressList)
})
</script>

<style scoped>
.personal-container {
  min-height: 100vh;
  background: #f5f5f5;
  display: flex;
  flex-direction: column;
}

/* 顶部导航 */
.top-nav {
  background: #409EFF;
  color: white;
  padding: 12px 0;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.nav-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
  gap: 20px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: rgba(255,255,255,0.2);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.back-btn:hover {
  background: rgba(255,255,255,0.3);
}

.back-icon {
  font-size: 16px;
}

.page-title {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
}

/* 主体内容 */
.main-container {
  flex: 1;
  display: flex;
  background: #f5f5f5;
}

/* 左侧导航栏 */
.side-nav {
  width: 200px;
  min-width: 200px;
  background: #fff;
  border-right: 1px solid #f0f0f0;
  color: #333;
  display: flex;
  flex-direction: column;
}

.back-home {
  padding: 16px 16px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 16px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
}

.back-home:hover {
  background: #f5f5f5;
  color: #e43932;
}

.user-profile {
  padding: 28px 16px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
}

.avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: linear-gradient(135deg, #3a7bd5 0%, #3a96dd 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-placeholder {
  color: white;
  font-size: 32px;
  font-weight: bold;
}

.avatar-image {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.user-info h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.user-info p {
  margin: 6px 0 0 0;
  font-size: 13px;
  color: #999;
}

.nav-menu {
  padding: 20px 0;
}

.nav-item {
  padding: 14px 20px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #666;
  border-left: 3px solid transparent;
  font-weight: 400;
}

.nav-item:hover {
  background: #f5f5f5;
  color: #333;
}

.nav-item.active {
  color: #FF5000;
  background: #FFF9F5;
  border-left-color: #FF5000;
  font-weight: 500;
}

.nav-icon {
  font-size: 18px;
  width: 22px;
  text-align: center;
}

/* 右侧内容区 */
.content-area {
  flex: 1;
  background: #fff;
  padding: 24px;
  overflow-y: auto;
}

.content-header {
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.content-header h2 {
  margin: 0;
  font-size: 18px;
  color: #333;
  font-weight: 600;
}

.settings-card {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  padding: 24px;
}

.settings-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.settings-item:last-child {
  border-bottom: none;
}

.settings-item label {
  font-size: 14px;
  color: #666;
}

.settings-item label::before {
  content: '*';
  color: #e43932;
  margin-right: 4px;
}

.settings-item span {
  font-size: 14px;
  color: #333;
  padding: 6px 12px;
  background: #f9f9f9;
  border: 1px solid #e8e8e8;
  border-radius: 4px;
}

.recent-orders {
  margin-top: 40px;
}

.recent-orders h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  color: #333;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.recent-orders h3::before {
  content: '📋';
  font-size: 16px;
}

.order-card {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  padding: 16px;
  margin-bottom: 12px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f5f5f5;
}

.order-header span {
  font-size: 13px;
  color: #999;
}

.order-status {
  color: #e43932;
  font-size: 13px;
}

.status-pending {
  background: #FFF9F5;
  color: #FF5000;
}

.status-shipping {
  background: #F0FAFF;
  color: #1890FF;
}

.status-received {
  background: #F5F7FF;
  color: #597EF7;
}

.status-completed {
  background: #F6FFED;
  color: #52C41A;
}

.status-cancelled {
  background: #F5F5F5;
  color: #999;
}

.order-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-content p {
  margin: 0;
  font-size: 14px;
  color: #333;
}

.order-content p:last-child {
  color: #e43932;
  font-weight: 500;
}

/* 统计卡片 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  padding: 20px;
  text-align: center;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #e43932;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 13px;
  color: #999;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #999;
  font-size: 16px;
}

.empty-state .empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
  opacity: 0.5;
}

/* 购物车样式 */
.cart-content {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.cart-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.cart-item {
  display: flex;
  gap: 15px;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 8px;
  transition: all 0.2s;
}

.cart-item:hover {
  border-color: #667eea;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
}

.cart-item-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  background: #f5f5f5;
}

.cart-item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cart-item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.cart-item-name {
  font-size: 15px;
  color: #333;
  margin: 0;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.cart-item-spec {
  font-size: 13px;
  color: #999;
  margin: 0;
}

.cart-item-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cart-item-price {
  font-size: 18px;
  color: #ff4757;
  font-weight: bold;
}

.cart-item-quantity {
  display: flex;
  align-items: center;
  gap: 10px;
}

.cart-item-quantity button {
  width: 28px;
  height: 28px;
  border: 1px solid #ddd;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  color: #666;
  transition: all 0.2s;
}

.cart-item-quantity button:hover:not(:disabled) {
  border-color: #667eea;
  color: #667eea;
}

.cart-item-quantity button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.cart-item-quantity span {
  font-size: 14px;
  color: #333;
  min-width: 20px;
  text-align: center;
}

.remove-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 18px;
  padding: 5px;
  border-radius: 4px;
  transition: all 0.2s;
}

.remove-btn:hover {
  background: #fff5f5;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-top: 1px solid #eee;
  margin-top: 15px;
}

.cart-total {
  display: flex;
  flex-direction: column;
  gap: 5px;
  font-size: 14px;
  color: #666;
}

.total-price {
  font-size: 20px;
  color: #ff4757;
  font-weight: bold;
}

.checkout-btn {
  padding: 12px 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.2s;
}

.checkout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.action-btn-primary {
  padding: 12px 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-size: 15px;
  transition: all 0.2s;
}

.action-btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

/* 地址管理样式 */
.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.content-header h2 {
  font-size: 18px;
  color: #333;
  margin: 0;
}

.add-address-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
}

.add-address-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.address-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  transition: all 0.2s;
}

.address-item:hover {
  border-color: #667eea;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
}

.address-item.default {
  border-color: #667eea;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
}

.address-info {
  flex: 1;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.user-info .name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.user-info .phone {
  font-size: 14px;
  color: #666;
}

.user-info .default-tag {
  padding: 3px 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.address-detail {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.address-actions {
  display: flex;
  gap: 10px;
  margin-left: 20px;
}

.address-actions .action-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  background: #f0f0f0;
  color: #666;
  transition: all 0.2s;
}

.address-actions .action-btn:hover {
  background: #667eea;
  color: white;
}

.address-actions .action-btn.delete:hover {
  background: #ff4757;
}

/* 弹窗样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  animation: fadeIn 0.2s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.dialog-content {
  background: #fff;
  border-radius: 16px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
  background: linear-gradient(135deg, #f8f9ff 0%, #ffffff 100%);
}

.dialog-header h3 {
  font-size: 20px;
  color: #333;
  margin: 0;
  font-weight: 600;
}

.close-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: transparent;
  font-size: 32px;
  color: #999;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.close-btn:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
  transform: rotate(90deg);
}

.dialog-body {
  padding: 24px;
}

.form-item {
  margin-bottom: 24px;
}

.form-item:last-child {
  margin-bottom: 0;
}

.form-item label {
  display: block;
  font-size: 14px;
  color: #555;
  margin-bottom: 10px;
  font-weight: 600;
  letter-spacing: 0.3px;
}

.form-item .required {
  color: #ff4757;
  margin-left: 3px;
}

.form-item input[type="text"],
.form-item input[type="tel"],
.form-item input[type="email"],
.form-item textarea {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e8e9ff;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: all 0.3s ease;
  font-family: inherit;
  background: #fafbff;
}

.form-item input:focus,
.form-item textarea:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
  background: white;
}

.form-item textarea {
  resize: vertical;
}

.region-input {
  display: flex;
  gap: 10px;
}

.region-input input {
  flex: 1;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
}

.region-input input:focus {
  border-color: #667eea;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-weight: normal;
}

.checkbox-label input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.dialog-footer {
  display: flex;
  gap: 12px;
  padding: 24px;
  border-top: 1px solid #f0f0f0;
  background: linear-gradient(135deg, #ffffff 0%, #f8f9ff 100%);
}

.cancel-btn,
.submit-btn {
  flex: 1;
  padding: 14px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.cancel-btn {
  background: #f5f5f5;
  color: #666;
}

.cancel-btn:hover {
  background: #e8e8e8;
  transform: translateY(-1px);
}

.submit-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
}

/* 订单标签页 */
.order-tabs {
  display: flex;
  gap: 24px;
  margin-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.order-tab {
  padding: 12px 0;
  color: #666;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  position: relative;
  font-weight: 400;
}

.order-tab:hover {
  color: #333;
}

.order-tab.active {
  color: #FF5000;
  font-weight: 500;
}

.order-tab.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 2px;
  background: #FF5000;
  border-radius: 1px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

/* 个人设置 */
.settings-card {
  background: linear-gradient(135deg, #ffffff 0%, #f8f9ff 100%);
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
  border: 1px solid #e8e9ff;
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.08);
  transition: all 0.3s ease;
}

.settings-card:hover {
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.12);
  transform: translateY(-2px);
}

.settings-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 0;
  border-bottom: 1px solid rgba(102, 126, 234, 0.08);
  transition: all 0.2s ease;
}

.settings-item:hover {
  padding-left: 10px;
  background: rgba(102, 126, 234, 0.02);
  border-radius: 8px;
}

.settings-item:last-child {
  border-bottom: none;
}

.settings-item label {
  font-size: 15px;
  color: #666;
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 8px;
}

.settings-item label::before {
  content: '•';
  color: #667eea;
  font-weight: bold;
}

.settings-item span {
  font-size: 15px;
  color: #333;
  font-weight: 500;
  background: #fff;
  padding: 6px 12px;
  border-radius: 16px;
  border: 1px solid #e8e9ff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.item-value {
  display: flex;
  align-items: center;
  gap: 10px;
}

.edit-icon {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.2s;
}

.edit-icon:hover {
  background: rgba(102, 126, 234, 0.1);
}

.gender-options {
  display: flex;
  gap: 20px;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  font-size: 14px;
  color: #333;
}

.radio-label input[type="radio"] {
  width: 16px;
  height: 16px;
  cursor: pointer;
}

.settings-buttons {
  display: flex;
  gap: 12px;
  margin-top: 24px;
}

.btn-logout {
  flex: 1;
  padding: 12px 0;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  color: white;
  font-weight: 500;
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}

.btn-logout:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(255, 107, 107, 0.4);
}

.profile-dialog {
  max-width: 500px;
}

/* ========== 收藏页面 ========== */
.favorite-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
  padding: 10px 0;
}

.favorite-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.favorite-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.favorite-image {
  width: 100%;
  aspect-ratio: 1;
  overflow: hidden;
  background: #f5f5f5;
}

.favorite-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.favorite-card:hover .favorite-image img {
  transform: scale(1.05);
}

.favorite-info {
  padding: 12px;
}

.favorite-name {
  font-size: 14px;
  color: #333;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.favorite-price {
  font-size: 18px;
  color: #FF5000;
  font-weight: 600;
  margin: 0 0 10px;
}

.cancel-fav-btn {
  width: 100%;
  padding: 6px 0;
  background: white;
  color: #999;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s ease;
}

.cancel-fav-btn:hover {
  border-color: #FF5000;
  color: #FF5000;
}

/* 响应式 */
@media (max-width: 768px) {
  .main-container {
    flex-direction: column;
  }
  
  .side-nav {
    width: 100%;
    min-width: 100%;
    border-right: none;
    border-bottom: 1px solid rgba(255,255,255,0.2);
  }
}
</style>