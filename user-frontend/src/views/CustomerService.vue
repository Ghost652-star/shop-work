<template>
  <div class="cs-page">
    <div class="cs-container">

      <div class="sidebar-left">
        <div class="sidebar-header">
          <h2 class="sidebar-title">消息</h2>
        </div>
        <div class="sidebar-search">
          <svg class="search-icon" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
          <input type="text" class="search-input" placeholder="搜索联系人" />
        </div>
        <div class="contact-list">
          <div
            class="contact-item"
            :class="{ active: activeContact === 'official' }"
            @click="activeContact = 'official'"
          >
            <div class="contact-avatar official">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            </div>
            <div class="contact-info">
              <div class="contact-top">
                <span class="contact-name">官方客服</span>
                <span class="contact-time">{{ lastMsgTime }}</span>
              </div>
              <span class="contact-preview">您好，有什么可以帮您？</span>
            </div>
          </div>
        </div>
      </div>

      <div class="chat-main">
        <div class="chat-header">
          <span class="chat-title">官方客服</span>
          <span class="chat-status">
            <span class="status-dot"></span>
            在线
          </span>
        </div>

        <div class="messages-area" ref="messagesContainer">
          <div class="messages-inner">
            <div class="time-divider">
              <span>{{ currentTime }}</span>
            </div>

            <transition-group name="msg">
              <div
                v-for="msg in messages"
                :key="msg.id"
                class="message-row"
                :class="{ 'msg-self': msg.role === 'user', 'msg-agent': msg.role === 'agent' }"
              >
                <div v-if="msg.role === 'agent'" class="avatar-msg">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                </div>
                <div class="bubble" :class="{ 'bubble-agent': msg.role === 'agent', 'bubble-user': msg.role === 'user' }">
                  <div class="bubble-text" v-html="formatMessage(msg.content)"></div>
                </div>
              </div>
            </transition-group>

            <div v-if="isTyping" class="message-row msg-agent">
              <div class="avatar-msg">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
              </div>
              <div class="bubble bubble-agent">
                <div class="typing-indicator">
                  <span></span><span></span><span></span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="input-area">
          <div class="quick-tags">
            <span class="quick-tag" :class="{ active: selectedService === '订单咨询' }" @click="openOrderSelect">订单咨询</span>
            <span class="quick-tag" :class="{ active: selectedService === '售后服务' }" @click="selectService('售后服务')">售后服务</span>
            <span class="quick-tag" :class="{ active: selectedService === '优惠券咨询' }" @click="selectService('优惠券咨询')">优惠券</span>
            <span class="quick-tag" :class="{ active: selectedService === '商品咨询' }" @click="selectService('商品咨询')">商品咨询</span>
          </div>
          <div v-if="selectedOrder" class="selected-order-tag">
            <span>已选：{{ selectedOrder.orderNo }}</span>
            <span class="tag-close" @click="selectedOrder = null">×</span>
          </div>
          <div class="input-wrapper">
            <input
              ref="inputRef"
              v-model="inputText"
              type="text"
              class="msg-input"
              placeholder="请输入您想要咨询的内容..."
              @keydown.enter="sendMessage"
            />
            <button
              class="send-btn"
              :class="{ active: inputText.trim() }"
              @click="sendMessage"
              :disabled="!inputText.trim()"
            >
              发送
            </button>
          </div>
        </div>
      </div>

      <div class="sidebar-right">
        <div class="order-tabs">
          <span
            v-for="tab in orderTabs"
            :key="tab.key"
            class="order-tab"
            :class="{ active: activeTab === tab.key }"
            @click="activeTab = tab.key"
          >{{ tab.label }}</span>
        </div>

        <div v-if="activeTab === 'orders'" class="order-list">
          <div v-if="orders.length === 0" class="empty-tip">
            <span>暂无订单数据</span>
          </div>
          <div
            v-for="(order, oIdx) in orders"
            :key="order.id"
            class="order-card"
            :class="['card-' + order.status, oIdx % 2 === 1 ? 'card-alt' : '']"
          >
            <div class="order-card-header">
              <span class="order-no">{{ order.orderNo }}</span>
              <span class="order-status" :class="'status-' + order.status">{{ order.statusText }}</span>
            </div>
            <div
              v-for="item in order.items"
              :key="item.id"
              class="order-card-body"
            >
              <div class="order-product-img">
                <img v-if="item.productImage" :src="item.productImage" :alt="item.productName" />
                <div v-else class="img-placeholder">{{ item.productName.charAt(0) }}</div>
              </div>
              <div class="order-product-info">
                <span class="order-product-name">{{ item.productName }}</span>
                <span class="order-product-spec">x{{ item.quantity }}</span>
              </div>
              <div class="order-price-col">
                <span class="order-price">¥{{ item.totalPrice }}</span>
              </div>
            </div>
            <div class="order-card-footer">
              <span class="order-id-text">{{ formatTime(order.createTime) }}</span>
              <span class="order-total">实付：<span class="total-price">¥{{ order.payAmount }}</span></span>
            </div>
          </div>
        </div>

        <div v-if="activeTab === 'cart'" class="order-list">
          <div v-if="cartItems.length === 0" class="empty-tip">
            <span>购物车是空的</span>
          </div>
          <div
            v-for="item in cartItems"
            :key="item.id"
            class="order-card"
          >
            <div class="order-card-body">
              <div class="order-product-img">
                <img v-if="item.productImage" :src="item.productImage" :alt="item.productName" />
                <div v-else class="img-placeholder">{{ item.productName.charAt(0) }}</div>
              </div>
              <div class="order-product-info">
                <span class="order-product-name">{{ item.productName }}</span>
                <span class="order-product-spec">x{{ item.quantity }}</span>
              </div>
              <div class="order-price-col">
                <span class="order-price">¥{{ item.subtotal }}</span>
              </div>
            </div>
          </div>
        </div>

        <div v-if="activeTab !== 'orders' && activeTab !== 'cart'" class="empty-tip">
          <span>功能开发中</span>
        </div>
      </div>

    </div>
  </div>

  <el-dialog v-model="showOrderDialog" title="选择订单咨询" width="520px" :close-on-click-modal="true">
    <div v-if="orders.length === 0" class="empty-tip">
      <span>暂无订单数据</span>
    </div>
    <div v-else class="order-select-list">
      <div
        v-for="order in orders"
        :key="order.id"
        class="order-select-item"
        @click="selectOrder(order)"
      >
        <div class="select-item-left">
          <span class="select-order-no">{{ order.orderNo }}</span>
          <span class="select-status" :class="'status-' + order.status">{{ order.statusText }}</span>
        </div>
        <div class="select-item-center">
          <span class="select-product-name">{{ order.items && order.items[0] ? order.items[0].productName : '' }}</span>
          <span v-if="order.items && order.items.length > 1" class="select-more">等{{ order.items.length }}件</span>
        </div>
        <span class="select-price">¥{{ order.payAmount }}</span>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, nextTick, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getOrderList } from '../api/order'
import { getCartList } from '../api/cart'
import { processMessage } from '../api/customerService'

const router = useRouter()
const inputText = ref('')
const messagesContainer = ref(null)
const inputRef = ref(null)
const isTyping = ref(false)
const activeContact = ref('official')
const activeTab = ref('orders')
const msgIdCounter = ref(0)
const orders = ref([])
const cartItems = ref([])
const selectedOrder = ref(null)
const selectedService = ref(null)
const showOrderDialog = ref(false)

const now = new Date()
const currentTime = ref(
  `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
)
const lastMsgTime = ref(currentTime.value)

const orderTabs = [
  { key: 'orders', label: '订单' },
  { key: 'browse', label: '浏览' },
  { key: 'follow', label: '关注' },
  { key: 'cart', label: '购物车' }
]

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const d = new Date(timeStr)
  const mm = (d.getMonth() + 1).toString().padStart(2, '0')
  const dd = d.getDate().toString().padStart(2, '0')
  const hh = d.getHours().toString().padStart(2, '0')
  const min = d.getMinutes().toString().padStart(2, '0')
  return `${mm}-${dd} ${hh}:${min}`
}

const loadOrders = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  try {
    const result = await getOrderList(userId)
    if (result.code === 1) {
      orders.value = result.data || []
    }
  } catch (e) {
    console.error('加载订单失败:', e)
  }
}

const loadCartItems = async () => {
  const userId = localStorage.getItem('userId')
  if (!userId) return
  try {
    const result = await getCartList(userId)
    if (result.code === 1) {
      cartItems.value = result.data || []
    }
  } catch (e) {
    console.error('加载购物车失败:', e)
  }
}

const openOrderSelect = () => {
  if (orders.value.length === 0) loadOrders()
  showOrderDialog.value = true
}

const selectOrder = (order) => {
  selectedOrder.value = order
  selectedService.value = '订单咨询'
  showOrderDialog.value = false
}

const selectService = (service) => {
  selectedService.value = selectedService.value === service ? null : service
  inputRef.value?.focus()
}

watch(activeTab, (tab) => {
  if (tab === 'orders') loadOrders()
  else if (tab === 'cart') loadCartItems()
})

const messages = ref([
  {
    id: ++msgIdCounter.value,
    role: 'agent',
    content: '您好，欢迎来到我们的客服中心！\n我是您的专属客服小助手，请问有什么可以帮您？'
  }
])

const goBack = () => {
  router.push('/')
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

const formatMessage = (text) => {
  return text.replace(/\n/g, '<br>')
}

const sendMessage = async () => {
  const text = inputText.value.trim()
  if (!text) return

  const orderNo = selectedOrder.value ? selectedOrder.value.orderNo : null
  const service = selectedService.value

  const displayParts = []
  if (service) displayParts.push(`[${service}]`)
  if (orderNo) displayParts.push(`[订单 ${orderNo}]`)
  displayParts.push(text)

  messages.value.push({
    id: ++msgIdCounter.value,
    role: 'user',
    content: displayParts.join(' ')
  })

  inputText.value = ''
  selectedOrder.value = null
  selectedService.value = null
  scrollToBottom()

  const userId = localStorage.getItem('userId')
  if (!userId) {
    messages.value.push({
      id: ++msgIdCounter.value,
      role: 'agent',
      content: '请先登录后再使用客服功能。'
    })
    scrollToBottom()
    return
  }

  isTyping.value = true
  scrollToBottom()

  try {
    const res = await processMessage(text, userId, orderNo)
    isTyping.value = false

    if (res.code === 1 && res.data && res.data.processed_message) {
      messages.value.push({
        id: ++msgIdCounter.value,
        role: 'agent',
        content: res.data.processed_message
      })
    } else {
      messages.value.push({
        id: ++msgIdCounter.value,
        role: 'agent',
        content: '抱歉，暂时无法处理您的请求，请稍后再试。'
      })
    }
  } catch (e) {
    console.error('客服接口请求失败:', e)
    isTyping.value = false
    messages.value.push({
      id: ++msgIdCounter.value,
      role: 'agent',
      content: '网络异常，请稍后再试。如需紧急帮助，请拨打客服热线：400-XXX-XXXX。'
    })
  }

  scrollToBottom()
}

onMounted(() => {
  scrollToBottom()
  inputRef.value?.focus()
  loadOrders()
})
</script>

<style scoped>
.cs-page {
  width: 100vw;
  height: 100vh;
  background: var(--color-bg);
}

.cs-container {
  width: 100%;
  height: 100vh;
  display: flex;
}

.sidebar-left {
  width: 240px;
  flex-shrink: 0;
  background: var(--color-bg-white);
  border-right: 1px solid var(--color-border-light);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-header {
  padding: 20px 20px 12px;
}

.sidebar-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin: 0;
}

.sidebar-search {
  padding: 0 16px 12px;
  position: relative;
}

.sidebar-search .search-icon {
  position: absolute;
  left: 28px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--color-text-tertiary);
}

.sidebar-search .search-input {
  width: 100%;
  height: 32px;
  padding: 0 12px 0 34px;
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  color: var(--color-text-primary);
  background: var(--color-bg);
  outline: none;
  transition: border-color var(--duration-normal) var(--ease-in-out),
              box-shadow var(--duration-normal) var(--ease-in-out);
}

.sidebar-search .search-input:focus {
  border-color: var(--color-primary);
  box-shadow: var(--input-focus-shadow);
}

.sidebar-search .search-input::placeholder {
  color: var(--color-text-tertiary);
}

.contact-list {
  flex: 1;
  overflow-y: auto;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background var(--duration-fast) var(--ease-in-out),
              border-left-color var(--duration-normal) var(--ease-in-out);
  border-left: 3px solid transparent;
}

.contact-item:hover {
  background: var(--color-bg);
}

.contact-item.active {
  background: var(--color-primary-light);
  border-left-color: var(--color-primary);
}

.contact-avatar {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.contact-avatar.official {
  background: var(--color-primary-light);
  color: var(--color-primary);
}

.contact-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.contact-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.contact-name {
  font-size: var(--text-sm);
  font-weight: 600;
  color: var(--color-text-primary);
}

.contact-time {
  font-size: 11px;
  color: var(--color-text-tertiary);
  flex-shrink: 0;
}

.contact-preview {
  font-size: var(--text-xs);
  color: var(--color-text-tertiary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: var(--color-bg-white);
  min-width: 0;
}

.chat-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 24px;
  border-bottom: 1px solid var(--color-border-light);
  flex-shrink: 0;
}

.chat-title {
  font-size: var(--text-base);
  font-weight: 600;
  color: var(--color-text-primary);
}

.chat-status {
  font-size: var(--text-xs);
  color: var(--color-text-tertiary);
  display: flex;
  align-items: center;
  gap: 4px;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: var(--radius-full);
  background: var(--color-success);
  animation: statusPulse 2s infinite ease-in-out;
}

@keyframes statusPulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.messages-area {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
  scroll-behavior: smooth;
}

.messages-inner {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.time-divider {
  text-align: center;
  padding: 4px 0 8px;
}

.time-divider span {
  font-size: 11px;
  color: var(--color-text-tertiary);
  background: var(--color-bg);
  padding: 2px 10px;
  border-radius: var(--radius-full);
}

.message-row {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  max-width: 70%;
}

.msg-self {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.msg-agent {
  align-self: flex-start;
}

.avatar-msg {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-full);
  background: var(--color-primary-light);
  color: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  margin-top: 2px;
}

.bubble {
  padding: 10px 14px;
  border-radius: var(--radius-lg);
  font-size: var(--text-sm);
  line-height: 1.6;
  word-break: break-word;
}

.bubble-agent {
  background: var(--color-bg);
  color: var(--color-text-primary);
  border-top-left-radius: var(--radius-sm);
}

.bubble-user {
  background: var(--color-primary);
  color: #fff;
  border-top-right-radius: var(--radius-sm);
}

.bubble-text {
  white-space: pre-wrap;
}

.typing-indicator {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 0;
}

.typing-indicator span {
  width: 6px;
  height: 6px;
  border-radius: var(--radius-full);
  background: var(--color-text-tertiary);
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% { opacity: 0.3; transform: translateY(0); }
  30% { opacity: 1; transform: translateY(-3px); }
}

.input-area {
  padding: 12px 24px 16px;
  border-top: 1px solid var(--color-border-light);
  flex-shrink: 0;
}

.quick-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
}

.quick-tag {
  padding: 5px 12px;
  font-size: var(--text-xs);
  color: var(--color-text-secondary);
  background: var(--color-bg);
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-in-out);
  white-space: nowrap;
}

.quick-tag:hover {
  color: var(--color-primary);
  border-color: var(--color-primary);
  background: var(--color-primary-light);
}

.quick-tag.active {
  color: #fff;
  border-color: var(--color-primary);
  background: var(--color-primary);
}

.selected-order-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  margin-bottom: 8px;
  font-size: var(--text-xs);
  color: var(--color-primary);
  background: var(--color-primary-light);
  border-radius: var(--radius-sm);
}

.tag-close {
  cursor: pointer;
  font-size: 14px;
  line-height: 1;
  opacity: 0.6;
  transition: opacity var(--duration-fast);
}

.tag-close:hover {
  opacity: 1;
}

.order-select-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.order-select-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--duration-fast) var(--ease-in-out);
}

.order-select-item:hover {
  border-color: var(--color-primary);
  background: var(--color-primary-light);
}

.select-item-left {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.select-order-no {
  font-size: var(--text-xs);
  color: var(--color-text-primary);
  font-family: 'Courier New', monospace;
  white-space: nowrap;
}

.select-status {
  font-size: 11px;
  font-weight: 500;
  white-space: nowrap;
}

.select-item-center {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: center;
  gap: 4px;
}

.select-product-name {
  font-size: var(--text-xs);
  color: var(--color-text-secondary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.select-more {
  font-size: 11px;
  color: var(--color-text-tertiary);
  flex-shrink: 0;
}

.select-price {
  font-size: var(--text-sm);
  color: var(--color-primary);
  font-weight: 600;
  flex-shrink: 0;
}

.input-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
}

.msg-input {
  flex: 1;
  height: 38px;
  padding: 0 14px;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  color: var(--color-text-primary);
  background: var(--color-bg-white);
  outline: none;
  transition: border-color var(--duration-normal) var(--ease-in-out),
              box-shadow var(--duration-normal) var(--ease-in-out);
}

.msg-input:focus {
  border-color: var(--color-primary);
  box-shadow: var(--input-focus-shadow);
}

.msg-input::placeholder {
  color: var(--color-text-tertiary);
}

.send-btn {
  height: 38px;
  padding: 0 20px;
  border: none;
  border-radius: var(--radius-md);
  background: var(--color-bg);
  color: var(--color-text-tertiary);
  font-size: var(--text-sm);
  font-weight: 600;
  cursor: not-allowed;
  transition: background var(--duration-normal) var(--ease-in-out),
              color var(--duration-normal) var(--ease-in-out),
              box-shadow var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
  flex-shrink: 0;
}

.send-btn.active {
  background: var(--color-primary);
  color: #fff;
  cursor: pointer;
}

.send-btn.active:hover {
  background: var(--color-primary-hover);
  box-shadow: var(--shadow-primary);
}

.send-btn.active:active {
  transform: scale(0.96);
  background: var(--color-primary-dark);
}

.msg-enter-active {
  animation: msgIn 0.3s var(--ease-out);
}

@keyframes msgIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.sidebar-right {
  width: 300px;
  flex-shrink: 0;
  background: var(--color-bg-white);
  border-left: 1px solid var(--color-border-light);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.order-tabs {
  display: flex;
  border-bottom: 1px solid var(--color-border-light);
  flex-shrink: 0;
}

.order-tab {
  flex: 1;
  text-align: center;
  padding: 14px 0;
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
  cursor: pointer;
  position: relative;
  transition: color var(--duration-normal) var(--ease-in-out);
}

.order-tab:hover {
  color: var(--color-text-primary);
  background: var(--color-bg-hover);
}

.order-tab.active {
  color: var(--color-primary);
  font-weight: 600;
}

.order-tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 2px;
  background: var(--color-primary);
  border-radius: var(--radius-full);
}

.order-search {
  padding: 12px 16px;
  position: relative;
  flex-shrink: 0;
}

.order-search .search-icon {
  position: absolute;
  left: 28px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--color-text-tertiary);
}

.order-search .search-input {
  width: 100%;
  height: 32px;
  padding: 0 12px 0 34px;
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md);
  font-size: var(--text-sm);
  color: var(--color-text-primary);
  background: var(--color-bg);
  outline: none;
  transition: border-color var(--duration-normal) var(--ease-in-out),
              box-shadow var(--duration-normal) var(--ease-in-out);
}

.order-search .search-input:focus {
  border-color: var(--color-primary);
  box-shadow: var(--input-focus-shadow);
}

.order-search .search-input::placeholder {
  color: var(--color-text-tertiary);
}

.order-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 12px;
}

.order-card {
  border: 1px solid var(--color-border-light);
  border-radius: var(--radius-md);
  margin-bottom: 10px;
  overflow: hidden;
  border-left: 3px solid transparent;
  transition: box-shadow var(--duration-normal) var(--ease-in-out),
              background var(--duration-normal) var(--ease-in-out),
              transform var(--duration-fast) var(--ease-in-out);
  cursor: pointer;
}

.order-card.card-alt {
  background: var(--color-bg-stripe);
}

.order-card:hover {
  box-shadow: var(--shadow-sm);
  transform: translateY(-1px);
}

.card-0 { border-left-color: #E65100; }
.card-1 { border-left-color: #1565C0; }
.card-2 { border-left-color: #2E7D32; }
.card-3 { border-left-color: #9E9E9E; }
.card-4 { border-left-color: #BDBDBD; }

.empty-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: var(--color-text-tertiary);
  font-size: var(--text-sm);
}

.order-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  border-bottom: 1px solid var(--color-border-light);
}

.order-no {
  font-size: 11px;
  color: var(--color-text-tertiary);
  font-family: 'Courier New', monospace;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 180px;
}

.order-status {
  font-size: 11px;
  font-weight: 500;
  flex-shrink: 0;
}

.status-0 { color: #E65100; }
.status-1 { color: #1565C0; }
.status-2 { color: #2E7D32; }
.status-3 { color: var(--color-text-tertiary); }
.status-4 { color: var(--color-text-tertiary); }

.order-card-body {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
}

.order-product-img {
  width: 50px;
  height: 50px;
  border-radius: var(--radius-sm);
  overflow: hidden;
  flex-shrink: 0;
}

.order-product-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.img-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, var(--color-primary-light), #fde8e8);
  color: var(--color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: 700;
}

.order-product-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.order-product-name {
  font-size: var(--text-xs);
  color: var(--color-text-primary);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.order-product-spec {
  font-size: 11px;
  color: var(--color-text-tertiary);
}

.order-price-col {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 2px;
  flex-shrink: 0;
}

.order-price {
  font-size: var(--text-sm);
  color: var(--color-text-primary);
  font-weight: 500;
}

.order-qty {
  font-size: 11px;
  color: var(--color-text-tertiary);
}

.order-card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  border-top: 1px solid var(--color-border-light);
}

.order-id-text {
  font-size: 11px;
  color: var(--color-text-tertiary);
}

.order-total {
  font-size: 11px;
  color: var(--color-text-secondary);
}

.total-price {
  color: var(--color-primary);
  font-weight: 600;
}

.messages-area::-webkit-scrollbar,
.order-list::-webkit-scrollbar,
.contact-list::-webkit-scrollbar {
  width: 4px;
}

.messages-area::-webkit-scrollbar-track,
.order-list::-webkit-scrollbar-track,
.contact-list::-webkit-scrollbar-track {
  background: transparent;
}

.messages-area::-webkit-scrollbar-thumb,
.order-list::-webkit-scrollbar-thumb,
.contact-list::-webkit-scrollbar-thumb {
  background: var(--color-border);
  border-radius: var(--radius-full);
}

@media (max-width: 1024px) {
  .sidebar-left {
    width: 200px;
  }
  .sidebar-right {
    width: 260px;
  }
}

@media (max-width: 768px) {
  .sidebar-left,
  .sidebar-right {
    display: none;
  }
}
</style>
