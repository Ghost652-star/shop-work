<template>
  <div class="aftersale-container">
    <div class="header-search">
      <div class="header-content">
        <div class="logo-section">
          <h1 class="logo" @click="goHome">电商平台</h1>
        </div>
      </div>
    </div>

    <div class="main-content">
      <div class="aftersale-page">
        <h2 class="page-title">申请售后</h2>

        <div class="section order-info" v-if="order">
          <div class="section-header">订单信息</div>
          <div class="order-no">订单号：{{ order.orderNo }}</div>
        </div>

        <div class="section">
          <div class="section-header">选择售后商品</div>
          <div class="product-list">
            <div
              v-for="item in orderItems"
              :key="item.id"
              class="product-item"
              :class="{ selected: selectedItems.includes(item.id) }"
              @click="toggleItem(item.id)"
            >
              <input type="checkbox" :checked="selectedItems.includes(item.id)" @click.stop />
              <img :src="item.productImage" :alt="item.productName" class="product-image" />
              <div class="product-info">
                <div class="product-name">{{ item.productName }}</div>
                <div class="product-price">¥{{ item.price }} × {{ item.quantity }}</div>
              </div>
              <div class="product-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
            </div>
          </div>
          <div class="refund-total">
            退款金额：<span class="refund-amount">¥{{ refundAmount }}</span>
          </div>
        </div>

        <div class="section">
          <div class="section-header">售后原因 <span class="required">*</span></div>
          <select v-model="form.reason" class="reason-select">
            <option value="">请选择售后原因</option>
            <option value="质量问题">质量问题</option>
            <option value="不想要了">不想要了</option>
            <option value="发错货">发错货</option>
            <option value="其他">其他</option>
          </select>
          <input
            v-if="form.reason === '其他'"
            v-model="customReason"
            type="text"
            class="custom-reason"
            placeholder="请输入售后原因"
            maxlength="50"
          />
        </div>

        <div class="section">
          <div class="section-header">问题描述</div>
          <textarea
            v-model="form.description"
            class="description-input"
            placeholder="请详细描述您遇到的问题（选填）"
            maxlength="200"
            rows="4"
          ></textarea>
        </div>

        <div class="section">
          <div class="section-header">凭证图片</div>
          <div class="image-upload">
            <div v-for="(img, index) in imageList" :key="index" class="image-preview">
              <img :src="img" />
              <span class="remove-btn" @click="removeImage(index)">×</span>
            </div>
            <label v-if="imageList.length < 3" class="upload-btn">
              <input type="file" accept="image/*" @change="handleImageUpload" hidden />
              <span>+</span>
            </label>
          </div>
          <p class="upload-tip">最多上传3张图片</p>
        </div>

        <div class="submit-section">
          <button class="submit-btn" @click="submitAfterSale" :disabled="!canSubmit">
            提交申请
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getOrderDetail } from '../api/order'
import { createAfterSale } from '../api/afterSale'

export default {
  name: 'AfterSaleApply',
  data() {
    return {
      order: null,
      orderItems: [],
      selectedItems: [],
      form: {
        reason: '',
        description: ''
      },
      customReason: '',
      imageList: []
    }
  },
  computed: {
    refundAmount() {
      return this.orderItems
        .filter(item => this.selectedItems.includes(item.id))
        .reduce((sum, item) => sum + item.price * item.quantity, 0)
        .toFixed(2)
    },
    canSubmit() {
      return this.selectedItems.length > 0 && (this.form.reason || this.customReason)
    }
  },
  mounted() {
    this.loadOrder()
  },
  methods: {
    goHome() {
      this.$router.push('/')
    },
    async loadOrder() {
      const orderId = this.$route.query.orderId
      const userId = localStorage.getItem('userId')
      if (!orderId || !userId) return
      try {
        const result = await getOrderDetail(orderId, userId)
        if (result.code === 1) {
          this.order = result.data
          this.orderItems = result.data.items || []
        }
      } catch (e) {
        console.error('加载订单失败:', e)
      }
    },
    toggleItem(itemId) {
      const idx = this.selectedItems.indexOf(itemId)
      if (idx > -1) {
        this.selectedItems.splice(idx, 1)
      } else {
        this.selectedItems.push(itemId)
      }
    },
    handleImageUpload(e) {
      const file = e.target.files[0]
      if (!file) return
      const reader = new FileReader()
      reader.onload = (ev) => {
        this.imageList.push(ev.target.result)
      }
      reader.readAsDataURL(file)
    },
    removeImage(index) {
      this.imageList.splice(index, 1)
    },
    async submitAfterSale() {
      const userId = localStorage.getItem('userId')
      if (!userId) {
        this.$message.warning('请先登录')
        return
      }
      const reason = this.form.reason === '其他' ? this.customReason : this.form.reason
      if (!reason) {
        this.$message.warning('请选择售后原因')
        return
      }
      if (this.selectedItems.length === 0) {
        this.$message.warning('请选择要售后的商品')
        return
      }
      try {
        const result = await createAfterSale({
          userId: parseInt(userId),
          orderId: this.order.id,
          reason: reason,
          description: this.form.description,
          images: this.imageList.join(','),
          orderItemIds: this.selectedItems
        })
        if (result.code === 1) {
          this.$message.success('售后申请已提交')
          this.$router.push('/personal?tab=orders')
        } else {
          this.$message.error(result.msg || '提交失败')
        }
      } catch (e) {
        console.error('提交售后申请失败:', e)
        this.$message.error('提交失败')
      }
    }
  }
}
</script>

<style scoped>
.aftersale-container { min-height: 100vh; background: #f5f5f5; }
.header-search { background: #E53935; padding: 15px 0; }
.header-content { max-width: 1200px; margin: 0 auto; padding: 0 20px; }
.logo { color: white; font-size: 24px; cursor: pointer; margin: 0; }
.main-content { max-width: 800px; margin: 20px auto; padding: 0 20px; }
.aftersale-page { background: white; border-radius: 8px; padding: 24px; }
.page-title { font-size: 20px; margin-bottom: 20px; color: #333; }
.section { margin-bottom: 20px; padding-bottom: 20px; border-bottom: 1px solid #f0f0f0; }
.section:last-child { border-bottom: none; }
.section-header { font-size: 16px; font-weight: bold; margin-bottom: 12px; color: #333; }
.required { color: #E53935; }
.order-no { color: #666; font-size: 14px; }
.product-item { display: flex; align-items: center; padding: 10px; border: 1px solid #eee; border-radius: 6px; margin-bottom: 8px; cursor: pointer; transition: border-color 0.2s; }
.product-item.selected { border-color: #E53935; background: #fff5f5; }
.product-image { width: 60px; height: 60px; object-fit: cover; border-radius: 4px; margin: 0 12px; }
.product-info { flex: 1; }
.product-name { font-size: 14px; color: #333; }
.product-price { font-size: 13px; color: #999; margin-top: 4px; }
.product-subtotal { font-size: 14px; color: #E53935; font-weight: bold; }
.refund-total { text-align: right; font-size: 16px; margin-top: 12px; }
.refund-amount { color: #E53935; font-size: 20px; font-weight: bold; }
.reason-select { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px; font-size: 14px; }
.custom-reason { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px; font-size: 14px; margin-top: 8px; }
.description-input { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px; font-size: 14px; resize: vertical; }
.image-upload { display: flex; gap: 8px; flex-wrap: wrap; }
.image-preview { position: relative; width: 80px; height: 80px; }
.image-preview img { width: 100%; height: 100%; object-fit: cover; border-radius: 4px; }
.remove-btn { position: absolute; top: -6px; right: -6px; width: 18px; height: 18px; background: #E53935; color: white; border-radius: 50%; display: flex; align-items: center; justify-content: center; cursor: pointer; font-size: 12px; }
.upload-btn { width: 80px; height: 80px; border: 2px dashed #ddd; border-radius: 4px; display: flex; align-items: center; justify-content: center; cursor: pointer; font-size: 24px; color: #999; }
.upload-tip { color: #999; font-size: 12px; margin-top: 6px; }
.submit-section { text-align: center; margin-top: 24px; }
.submit-btn { background: #E53935; color: white; border: none; padding: 12px 48px; border-radius: 6px; font-size: 16px; cursor: pointer; }
.submit-btn:disabled { background: #ccc; cursor: not-allowed; }
</style>
