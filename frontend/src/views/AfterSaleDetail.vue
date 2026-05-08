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
      <div class="aftersale-page" v-if="afterSale">
        <div class="status-banner" :class="'status-' + afterSale.status">
          <div class="status-icon">{{ statusIcon }}</div>
          <div class="status-text">{{ afterSale.statusText }}</div>
        </div>

        <div class="section">
          <div class="section-header">关联订单</div>
          <div class="info-row">
            <span class="label">订单号：</span>
            <span class="value">{{ afterSale.orderNo }}</span>
          </div>
          <div class="info-row">
            <span class="label">申请时间：</span>
            <span class="value">{{ afterSale.createTime }}</span>
          </div>
        </div>

        <div class="section">
          <div class="section-header">售后信息</div>
          <div class="info-row">
            <span class="label">售后原因：</span>
            <span class="value">{{ afterSale.reason }}</span>
          </div>
          <div class="info-row" v-if="afterSale.description">
            <span class="label">问题描述：</span>
            <span class="value">{{ afterSale.description }}</span>
          </div>
          <div class="info-row">
            <span class="label">退款金额：</span>
            <span class="value refund">¥{{ afterSale.refundAmount }}</span>
          </div>
        </div>

        <div class="section" v-if="afterSale.adminRemark">
          <div class="section-header">商家处理</div>
          <div class="admin-remark">{{ afterSale.adminRemark }}</div>
        </div>

        <div class="section">
          <div class="section-header">售后商品</div>
          <div v-for="item in afterSale.items" :key="item.orderItemId" class="product-item">
            <img :src="item.productImage" class="product-image" />
            <div class="product-info">
              <div class="product-name">{{ item.productName }}</div>
              <div class="product-price">¥{{ item.price }} × {{ item.quantity }}</div>
            </div>
            <div class="product-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
          </div>
        </div>

        <div class="action-section" v-if="afterSale.status === 0">
          <button class="cancel-btn" @click="cancelAfterSaleAction">取消售后</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getAfterSaleDetail, cancelAfterSale } from '../api/afterSale'

export default {
  name: 'AfterSaleDetail',
  data() {
    return {
      afterSale: null
    }
  },
  computed: {
    statusIcon() {
      const icons = { 0: '⏳', 1: '✅', 2: '❌', 3: '💰' }
      return icons[this.afterSale?.status] || '❓'
    }
  },
  mounted() {
    this.loadDetail()
  },
  methods: {
    goHome() {
      this.$router.push('/')
    },
    async loadDetail() {
      const id = this.$route.query.id
      if (!id) return
      try {
        const result = await getAfterSaleDetail(id)
        if (result.code === 1) {
          this.afterSale = result.data
        }
      } catch (e) {
        console.error('加载售后详情失败:', e)
      }
    },
    async cancelAfterSaleAction() {
      try {
        await this.$confirm('确定要取消该售后申请吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        const userId = localStorage.getItem('userId')
        const result = await cancelAfterSale(this.afterSale.id, userId)
        if (result.code === 1) {
          this.$message.success('售后已取消')
          this.$router.push('/personal?tab=orders')
        } else {
          this.$message.error(result.msg || '取消失败')
        }
      } catch (e) {
        if (e !== 'cancel') {
          console.error('取消售后失败:', e)
        }
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
.status-banner { text-align: center; padding: 24px; border-radius: 8px; margin-bottom: 20px; }
.status-banner.status-0 { background: #FFF3E0; color: #E65100; }
.status-banner.status-1 { background: #E8F5E9; color: #2E7D32; }
.status-banner.status-2 { background: #FFEBEE; color: #C62828; }
.status-banner.status-3 { background: #E3F2FD; color: #1565C0; }
.status-icon { font-size: 36px; }
.status-text { font-size: 18px; font-weight: bold; margin-top: 8px; }
.section { margin-bottom: 20px; padding-bottom: 16px; border-bottom: 1px solid #f0f0f0; }
.section:last-child { border-bottom: none; }
.section-header { font-size: 16px; font-weight: bold; margin-bottom: 12px; color: #333; }
.info-row { display: flex; margin-bottom: 8px; font-size: 14px; }
.info-row .label { color: #999; width: 80px; flex-shrink: 0; }
.info-row .value { color: #333; }
.refund { color: #E53935; font-size: 18px; font-weight: bold; }
.admin-remark { background: #f9f9f9; padding: 12px; border-radius: 6px; color: #666; font-size: 14px; }
.product-item { display: flex; align-items: center; padding: 10px; border: 1px solid #eee; border-radius: 6px; margin-bottom: 8px; }
.product-image { width: 60px; height: 60px; object-fit: cover; border-radius: 4px; margin-right: 12px; }
.product-info { flex: 1; }
.product-name { font-size: 14px; color: #333; }
.product-price { font-size: 13px; color: #999; margin-top: 4px; }
.product-subtotal { font-size: 14px; color: #E53935; font-weight: bold; }
.action-section { text-align: center; margin-top: 24px; }
.cancel-btn { background: white; color: #E53935; border: 1px solid #E53935; padding: 10px 36px; border-radius: 6px; font-size: 14px; cursor: pointer; }
.cancel-btn:hover { background: #fff5f5; }
</style>
