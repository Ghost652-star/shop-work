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

      <!-- AI助手 -->
      <div class="nav-item" @click="toggleAiAssistant" @mouseenter="hoverIndex = 3" @mouseleave="hoverIndex = -1">
        <div class="ai-hint-bubble" v-if="showAiHint">
          <div class="ai-hint-content">
            <div class="ai-hint-title">🛍️ AI智能购物助手</div>
            <div class="ai-hint-desc">发张图片，一句话完成购物</div>
            <div class="ai-hint-features">
              <span>识图找商品</span>
              <span>智能推荐</span>
              <span>快速下单</span>
            </div>
          </div>
          <button class="ai-hint-action" @click.stop="toggleAiAssistant">立即体验</button>
          <span class="ai-hint-close" @click.stop="showAiHint = false">×</span>
        </div>
        <div class="nav-icon">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M12 2a2 2 0 0 1 2 2c0 .74-.4 1.39-1 1.73V7h1a7 7 0 0 1 7 7h1a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1h-1.27a7 7 0 0 1-12.46 0H3a1 1 0 0 1-1-1v-3a1 1 0 0 1 1-1h1a7 7 0 0 1 7-7h1V5.73c-.6-.34-1-.99-1-1.73a2 2 0 0 1 2-2z"/>
            <circle cx="9" cy="15" r="1"/>
            <circle cx="15" cy="15" r="1"/>
          </svg>
        </div>
        <div class="nav-tooltip">AI助手</div>
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
          <div class="empty-icon">购物车</div>
          <p class="empty-text">购物车是空的</p>
          <button class="go-shopping-btn" @click="goShopping">去逛逛</button>
        </div>

        <!-- 购物车列表 -->
        <div v-else class="cart-body">
          <div class="cart-list">
            <div v-for="group in groupedCartItems" :key="group.merchantId" class="merchant-group">
            <div class="merchant-header">
              <span class="merchant-name-tag">{{ group.merchantName }}</span>
            </div>
            <div v-for="item in group.items" :key="item.id" class="cart-item">
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

    <!-- AI助手弹窗 -->
    <div v-if="aiVisible" class="ai-sidebar-overlay" @click="closeAiAssistant"></div>
    <transition name="slide-right">
      <div v-if="aiVisible" class="ai-sidebar" :style="{ width: aiSidebarWidth + 'px' }">
        <!-- 拖拽手柄 -->
        <div class="ai-resize-handle" :class="{ active: aiResizing }" @mousedown="onAiResizeStart"></div>
        <!-- 头部 -->
        <div class="ai-header">
          <h3 class="ai-title">AI助手</h3>
          <span class="ai-clear-btn" @click="clearAiHistory" title="清空聊天记录">清空</span>
          <button class="close-btn" @click="closeAiAssistant">×</button>
        </div>

        <!-- 消息区域 -->
        <div class="ai-messages" ref="aiMessagesContainer">
          <div class="ai-messages-inner">
            <div class="ai-welcome">
              <div class="ai-avatar">
                <svg viewBox="0 0 24 24" width="24" height="24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M12 2a2 2 0 0 1 2 2c0 .74-.4 1.39-1 1.73V7h1a7 7 0 0 1 7 7h1a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1h-1.27a7 7 0 0 1-12.46 0H3a1 1 0 0 1-1-1v-3a1 1 0 0 1 1-1h1a7 7 0 0 1 7-7h1V5.73c-.6-.34-1-.99-1-1.73a2 2 0 0 1 2-2z"/>
                  <circle cx="9" cy="15" r="1"/>
                  <circle cx="15" cy="15" r="1"/>
                </svg>
              </div>
              <div class="ai-bubble ai-welcome-bubble">
                <div class="welcome-header">
                  <strong>您好！我是潮选优品AI购物助手 🛍️</strong>
                </div>
                <div class="welcome-features">
                  <div class="welcome-feature">
                    <span class="feature-icon">🔍</span>
                    <span><strong>识图找商品</strong> - 发送商品图片，帮您快速找到同款</span>
                  </div>
                  <div class="welcome-feature">
                    <span class="feature-icon">💬</span>
                    <span><strong>智能推荐</strong> - 描述您的需求，为您推荐最合适的商品</span>
                  </div>
                  <div class="welcome-feature">
                    <span class="feature-icon">🛒</span>
                    <span><strong>一句话下单</strong> - 告诉我想要什么，快速完成加购</span>
                  </div>
                  <div class="welcome-feature">
                    <span class="feature-icon">📦</span>
                    <span><strong>订单管理</strong> - 查询订单状态、物流信息</span>
                  </div>
                  <div class="welcome-feature">
                    <span class="feature-icon">🔧</span>
                    <span><strong>售后服务</strong> - 申请退货退款、查询售后进度</span>
                  </div>
                </div>
                <div class="welcome-hint">
                  💡 试试发送一张商品图片，或直接告诉我您想买什么？
                </div>
              </div>
            </div>
            <div v-for="msg in aiMessages" :key="msg.id" class="ai-message-row" :class="{ 'msg-self': msg.role === 'user' }">
              <div v-if="msg.role === 'ai'" class="ai-avatar">
                <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M12 2a2 2 0 0 1 2 2c0 .74-.4 1.39-1 1.73V7h1a7 7 0 0 1 7 7h1a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1h-1.27a7 7 0 0 1-12.46 0H3a1 1 0 0 1-1-1v-3a1 1 0 0 1 1-1h1a7 7 0 0 1 7-7h1V5.73c-.6-.34-1-.99-1-1.73a2 2 0 0 1 2-2z"/>
                  <circle cx="9" cy="15" r="1"/>
                  <circle cx="15" cy="15" r="1"/>
                </svg>
              </div>
              <div class="ai-bubble" :class="{ 'bubble-user': msg.role === 'user' }">
                <div v-if="msg.role === 'ai'" class="bubble-text" v-html="renderAiMarkdown(msg.content)"></div>
                <template v-else>
                  <div v-if="msg.image" class="bubble-image">
                    <img :src="msg.image" alt="发送的图片" @click="previewAiImage(msg.image)" />
                  </div>
                  <div v-if="msg.content" class="bubble-text">{{ msg.content }}</div>
                </template>
              </div>
            </div>
            <div v-if="aiTyping" class="ai-message-row">
              <div class="ai-avatar">
                <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M12 2a2 2 0 0 1 2 2c0 .74-.4 1.39-1 1.73V7h1a7 7 0 0 1 7 7h1a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1h-1.27a7 7 0 0 1-12.46 0H3a1 1 0 0 1-1-1v-3a1 1 0 0 1 1-1h1a7 7 0 0 1 7-7h1V5.73c-.6-.34-1-.99-1-1.73a2 2 0 0 1 2-2z"/>
                  <circle cx="9" cy="15" r="1"/>
                  <circle cx="15" cy="15" r="1"/>
                </svg>
              </div>
              <div class="ai-bubble bubble-ai">
                <div class="typing-indicator">
                  <span></span><span></span><span></span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="ai-input-area">
          <div class="ai-quick-tags">
            <span class="ai-quick-tag" @click="sendQuickMessage('商品推荐')">商品推荐</span>
            <span class="ai-quick-tag" @click="sendQuickMessage('订单查询')">订单查询</span>
            <span class="ai-quick-tag" @click="sendQuickMessage('售后服务')">售后服务</span>
          </div>
          <div v-if="aiPreviewImage" class="ai-image-preview">
            <img :src="aiPreviewImage" alt="预览" />
            <span class="ai-preview-close" @click="clearAiImage">×</span>
          </div>
          <div class="ai-input-wrapper">
            <input
              ref="aiFileInput"
              type="file"
              accept="image/*"
              style="display:none"
              @change="onAiImageSelected"
            />
            <button class="ai-upload-btn" @click="$refs.aiFileInput.click()" title="发送图片">
              <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <rect x="3" y="3" width="18" height="18" rx="2" ry="2"/>
                <circle cx="8.5" cy="8.5" r="1.5"/>
                <polyline points="21 15 16 10 5 21"/>
              </svg>
            </button>
            <input
              ref="aiInputRef"
              v-model="aiInputText"
              type="text"
              class="ai-msg-input"
              placeholder="输入您的问题..."
              @keydown.enter="sendAiMessage"
            />
            <button
              class="ai-send-btn"
              :class="{ active: aiInputText.trim() || aiPreviewImage }"
              @click="sendAiMessage"
            >
              <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="22" y1="2" x2="11" y2="13"/>
                <polygon points="22 2 15 22 11 13 2 9 22 2"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { marked } from 'marked'
import { addToCart, getCartList, updateQuantity, deleteCart, batchDelete, getCartCount, updateChecked, checkAll } from '../api/cart'

marked.setOptions({ breaks: true, gfm: true })

export default {
  name: 'CartSidebar',
  data() {
    return {
      visible: false,
      hoverIndex: -1,
      cartItems: [],
      msgCount: 0,
      aiVisible: false,
      aiInputText: '',
      aiMessages: [],
      aiTyping: false,
      aiSidebarWidth: 380,
      aiResizing: false,
      aiPreviewImage: '',
      aiImageBase64: '',
      aiWelcomeShown: false,
      showAiHint: false,
      aiHintTimer: null
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
    },
    groupedCartItems() {
      const groups = {}
      this.cartItems.forEach(item => {
        const key = item.merchantId || 0
        if (!groups[key]) {
          groups[key] = {
            merchantId: item.merchantId,
            merchantName: item.merchantName || '未知商家',
            items: []
          }
        }
        groups[key].items.push(item)
      })
      return Object.values(groups)
    }
  },
  mounted() {
    this.loadCartData()
    window.addEventListener('cartUpdated', this.loadCartData)
    // 首页加载后短暂展示AI助手提示
    if (this.$route.path === '/') {
      this.aiHintTimer = setTimeout(() => {
        this.showAiHint = true
      }, 1500)
      setTimeout(() => { this.showAiHint = false }, 6000)
    }
  },
  beforeDestroy() {
    window.removeEventListener('cartUpdated', this.loadCartData)
    if (this.aiHintTimer) clearTimeout(this.aiHintTimer)
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
    toggleAiAssistant() {
      this.aiVisible = !this.aiVisible
      if (this.aiVisible) {
        this.loadAiHistory()
        this.$nextTick(() => {
          this.scrollAiToBottom()
        })
      }
    },
    openAiAssistant() {
      this.aiVisible = true
      this.loadAiHistory()
      this.$nextTick(() => {
        this.scrollAiToBottom()
      })
    },
    closeAiAssistant() {
      this.aiVisible = false
    },
    onAiImageSelected(e) {
      const file = e.target.files[0]
      if (!file || !file.type.startsWith('image/')) return
      const reader = new FileReader()
      reader.onload = (ev) => {
        this.aiPreviewImage = ev.target.result
        this.aiImageBase64 = ev.target.result.split(',')[1]
      }
      reader.readAsDataURL(file)
      e.target.value = ''
    },
    clearAiImage() {
      this.aiPreviewImage = ''
      this.aiImageBase64 = ''
    },
    previewAiImage(src) {
      if (src) {
        window.open(src, '_blank')
      }
    },
    renderAiMarkdown(text) {
      if (!text) return ''
      return marked.parse(text)
    },
    /* AI侧边栏拖拽调整宽度 */
    onAiResizeStart(e) {
      this.aiResizing = true
      const startX = e.clientX
      const startWidth = this.aiSidebarWidth
      const onMove = (ev) => {
        const diff = startX - ev.clientX
        this.aiSidebarWidth = Math.max(300, Math.min(600, startWidth + diff))
      }
      const onEnd = () => {
        this.aiResizing = false
        document.removeEventListener('mousemove', onMove)
        document.removeEventListener('mouseup', onEnd)
        document.body.style.cursor = ''
        document.body.style.userSelect = ''
      }
      document.addEventListener('mousemove', onMove)
      document.addEventListener('mouseup', onEnd)
      document.body.style.cursor = 'col-resize'
      document.body.style.userSelect = 'none'
    },
    /* 加载AI聊天历史 */
    async loadAiHistory() {
      const userId = localStorage.getItem('userId')
      if (!userId) return
      try {
        const response = await fetch(`/api/shop/customer-service/history/${userId}`)
        const data = await response.json()
        if (data.code === 1 && data.data && data.data.length > 0) {
          this.aiMessages = data.data.map(m => {
            let image = null
            if (m.image) {
              // 确保图片有正确的前缀
              image = m.image.startsWith('data:') ? m.image : `data:image/png;base64,${m.image}`
            }
            return {
              id: Date.now() + Math.random(),
              role: m.role === 'user' ? 'user' : 'ai',
              content: m.content,
              image: image
            }
          })
        }
      } catch {}
    },
    async clearAiHistory() {
      const userId = localStorage.getItem('userId')
      if (!userId) return
      try {
        await fetch(`/api/shop/customer-service/history/${userId}`, { method: 'DELETE' })
      } catch {}
      this.aiMessages = []
    },
    async sendAiMessage() {
      const text = this.aiInputText.trim()
      const hasImage = !!this.aiImageBase64
      if (!text && !hasImage) return

      const displayContent = hasImage && !text ? '[发送了一张图片]' : text
      const currentPreviewImage = this.aiPreviewImage || null

      this.aiMessages.push({
        id: Date.now(),
        role: 'user',
        content: displayContent,
        image: currentPreviewImage
      })

      const sendText = text || ''
      const currentImage = this.aiImageBase64 || null

      this.aiInputText = ''
      this.clearAiImage()
      this.scrollAiToBottom()

      this.aiTyping = true
      this.scrollAiToBottom()

      try {
        const userId = localStorage.getItem('userId') || 'default_user'
        const body = { message: sendText, user_id: userId }
        if (currentImage) body.image = currentImage

        const response = await fetch('/api/shop/customer-service/process', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(body)
        })
        const data = await response.json()
        this.aiTyping = false

        let reply = '抱歉，我暂时无法回答这个问题。'
        if (data.code === 1 && data.data) {
          reply = data.data.processed_message || reply
        }
        this.aiMessages.push({
          id: Date.now() + 1,
          role: 'ai',
          content: reply
        })
        this.scrollAiToBottom()
      } catch (error) {
        this.aiTyping = false
        this.aiMessages.push({
          id: Date.now() + 1,
          role: 'ai',
          content: '网络错误，请稍后重试。'
        })
        this.scrollAiToBottom()
      }
    },
    sendQuickMessage(text) {
      this.aiInputText = text
      this.sendAiMessage()
    },
    scrollAiToBottom() {
      if (this.$refs.aiMessagesContainer) {
        this.$refs.aiMessagesContainer.scrollTop = this.$refs.aiMessagesContainer.scrollHeight
      }
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
        categoryId: item.categoryId,
        merchantId: item.merchantId,
        merchantName: item.merchantName
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

/* AI助手气泡提示 */
.ai-hint-bubble {
  position: absolute;
  right: 52px;
  top: 50%;
  transform: translateY(-50%);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-size: 13px;
  padding: 16px 20px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
  white-space: nowrap;
  z-index: 1002;
  cursor: default;
  animation: hintFadeIn 0.3s ease;
}
.ai-hint-bubble::after {
  content: '';
  position: absolute;
  right: -6px;
  top: 50%;
  transform: translateY(-50%);
  border: 6px solid transparent;
  border-left-color: #667eea;
}
.ai-hint-content {
  margin-bottom: 10px;
}
.ai-hint-title {
  font-size: 15px;
  font-weight: 600;
  margin-bottom: 4px;
}
.ai-hint-desc {
  font-size: 12px;
  opacity: 0.9;
  margin-bottom: 8px;
}
.ai-hint-features {
  display: flex;
  gap: 6px;
  flex-wrap: nowrap;
}
.ai-hint-features span {
  background: rgba(255,255,255,0.2);
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
}
.ai-hint-action {
  width: 100%;
  padding: 8px 16px;
  background: white;
  color: #667eea;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}
.ai-hint-action:hover {
  transform: scale(1.02);
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}
@keyframes hintFadeIn {
  from { opacity: 0; transform: translateY(-50%) translateX(8px); }
  to { opacity: 1; transform: translateY(-50%) translateX(0); }
}
.ai-hint-close {
  position: absolute;
  top: 8px;
  right: 8px;
  color: white;
  font-size: 16px;
  line-height: 1;
  cursor: pointer;
  padding: 2px;
  opacity: 0.7;
}
.ai-hint-close:hover { opacity: 1; }

/* AI消息中的图片 */
.bubble-image {
  margin-bottom: 8px;
}
.bubble-image img {
  max-width: 200px;
  max-height: 200px;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.2s;
  display: block;
}
.bubble-image img:hover {
  transform: scale(1.05);
}

/* 欢迎气泡样式 */
.ai-welcome-bubble {
  max-width: 85% !important;
}
.welcome-header {
  margin-bottom: 10px;
  font-size: 14px;
}
.welcome-features {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 10px;
}
.welcome-feature {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  font-size: 13px;
  line-height: 1.4;
}
.feature-icon {
  flex-shrink: 0;
}
.welcome-hint {
  background: rgba(229, 57, 53, 0.08);
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 12px;
  color: var(--color-primary);
}

/* AI图片预览 */
.ai-image-preview {
  position: relative;
  display: inline-block;
  margin: 4px 12px;
}
.ai-image-preview img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #eee;
}
.ai-preview-close {
  position: absolute;
  top: -6px;
  right: -6px;
  width: 18px;
  height: 18px;
  background: rgba(0,0,0,0.5);
  color: #fff;
  border-radius: 50%;
  font-size: 13px;
  line-height: 18px;
  text-align: center;
  cursor: pointer;
}
.ai-upload-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  background: none;
  color: #999;
  cursor: pointer;
  flex-shrink: 0;
  border-radius: 6px;
  transition: all 0.2s;
}
.ai-upload-btn:hover {
  color: var(--color-primary);
  background: var(--color-primary-light);
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

.merchant-group {
  margin-bottom: 16px;
}

.merchant-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: var(--color-bg);
  border-radius: var(--radius-md);
  margin-bottom: 8px;
}

.merchant-name-tag {
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--color-text-primary);
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

/* ===== AI助手侧边栏 ===== */
.ai-sidebar-overlay {
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

.ai-sidebar {
  position: fixed;
  top: 0;
  right: 0;
  width: 380px;
  height: 100vh;
  background: var(--color-bg-white);
  z-index: 1001;
  box-shadow: var(--shadow-lg);
  display: flex;
  flex-direction: column;
}

/* AI侧边栏拖拽手柄 */
.ai-resize-handle {
  position: absolute;
  left: -3px;
  top: 0;
  width: 6px;
  height: 100%;
  cursor: col-resize;
  z-index: 10;
  transition: background 0.15s;
}

.ai-resize-handle::after {
  content: '';
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 2px;
  height: 32px;
  border-radius: 1px;
  background: var(--color-border);
  opacity: 0;
  transition: opacity 0.15s;
}

.ai-resize-handle:hover {
  background: var(--color-primary-light);
}

.ai-resize-handle:hover::after,
.ai-resize-handle.active::after {
  opacity: 1;
  background: var(--color-primary);
}

/* 清空按钮 */
.ai-clear-btn {
  margin-left: auto;
  margin-right: 8px;
  font-size: var(--text-xs);
  color: var(--color-text-tertiary);
  cursor: pointer;
  padding: 2px 8px;
  border-radius: var(--radius-sm);
  transition: all 0.15s;
}

.ai-clear-btn:hover {
  color: var(--color-primary);
  background: var(--color-primary-light);
}

/* AI消息markdown渲染 */
.ai-bubble .bubble-text {
  white-space: normal;
}

.ai-bubble .bubble-text p {
  margin: 0 0 4px;
  line-height: 1.5;
}

.ai-bubble .bubble-text p:last-child {
  margin-bottom: 0;
}

.ai-bubble .bubble-text strong {
  font-weight: 600;
}

.ai-bubble .bubble-text table {
  width: 100%;
  border-collapse: collapse;
  margin: 6px 0;
  font-size: 12px;
}

.ai-bubble .bubble-text th {
  background: var(--color-bg);
  border: 1px solid var(--color-border-light);
  padding: 4px 6px;
  text-align: left;
  font-weight: 600;
}

.ai-bubble .bubble-text td {
  border: 1px solid var(--color-border-light);
  padding: 3px 6px;
}

.ai-bubble .bubble-text tr:nth-child(even) {
  background: rgba(0,0,0,0.02);
}

.ai-bubble .bubble-text ul,
.ai-bubble .bubble-text ol {
  margin: 4px 0;
  padding-left: 16px;
}

.ai-bubble .bubble-text li {
  margin: 1px 0;
}

.ai-bubble .bubble-text hr {
  border: none;
  border-top: 1px solid var(--color-border-light);
  margin: 6px 0;
}

.ai-bubble .bubble-text code {
  background: var(--color-bg);
  padding: 1px 3px;
  border-radius: 2px;
  font-size: 0.9em;
}

.ai-bubble .bubble-text pre {
  background: var(--color-bg);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-sm);
  padding: 6px 8px;
  margin: 4px 0;
  overflow-x: auto;
}

.ai-bubble .bubble-text pre code {
  background: none;
  padding: 0;
}

.ai-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid var(--color-border-light);
}

.ai-title {
  font-size: var(--text-xl);
  color: var(--color-text-primary);
  margin: 0;
  font-weight: 600;
}

.ai-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.ai-messages-inner {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.ai-welcome {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.ai-avatar {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-full);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.ai-message-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.ai-message-row.msg-self {
  flex-direction: row-reverse;
}

.ai-bubble {
  max-width: 75%;
  padding: 12px 16px;
  border-radius: var(--radius-lg);
  background: var(--color-bg);
  color: var(--color-text-primary);
  font-size: var(--text-base);
  line-height: 1.5;
}

.ai-bubble.bubble-user {
  background: var(--color-primary);
  color: white;
}

.ai-bubble.bubble-ai {
  background: var(--color-bg);
}

.ai-bubble p {
  margin: 0;
}

.ai-input-area {
  padding: 16px 20px;
  border-top: 1px solid var(--color-border-light);
}

.ai-quick-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.ai-quick-tag {
  padding: 6px 12px;
  border-radius: var(--radius-full);
  background: var(--color-bg);
  color: var(--color-text-secondary);
  font-size: var(--text-sm);
  cursor: pointer;
  transition: all var(--duration-normal) var(--ease-in-out);
  border: 1px solid var(--color-border-light);
}

.ai-quick-tag:hover {
  background: var(--color-primary-light);
  color: var(--color-primary);
  border-color: var(--color-primary);
}

.ai-input-wrapper {
  display: flex;
  gap: 12px;
  align-items: center;
}

.ai-msg-input {
  flex: 1;
  height: 44px;
  border: 1.5px solid var(--color-border);
  border-radius: var(--radius-full);
  padding: 0 16px;
  font-size: var(--text-base);
  color: var(--color-text-primary);
  background: var(--color-bg-white);
  transition: border-color var(--duration-normal) var(--ease-in-out),
              box-shadow var(--duration-normal) var(--ease-in-out);
}

.ai-msg-input:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(229, 57, 53, 0.1);
}

.ai-msg-input::placeholder {
  color: var(--color-text-tertiary);
}

.ai-send-btn {
  width: 44px;
  height: 44px;
  border: none;
  border-radius: var(--radius-full);
  background: var(--color-border);
  color: white;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--duration-normal) var(--ease-in-out);
}

.ai-send-btn.active {
  background: var(--color-primary);
}

.ai-send-btn:hover:not(:disabled) {
  transform: scale(1.05);
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 4px 0;
}

.typing-indicator span {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--color-text-tertiary);
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) { animation-delay: 0s; }
.typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
.typing-indicator span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
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

  .ai-sidebar {
    width: 100%;
  }
}
</style>
