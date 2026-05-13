<template>
  <div class="shop-layout">
    <div class="sidebar">
      <div class="logo">
        <span>FlowShop</span>
        <span class="sub">商家后台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#001529"
        text-color="#ffffffa6"
        active-text-color="#fff"
      >
        <el-menu-item index="/">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据概览</span>
        </el-menu-item>
        <el-menu-item index="/product">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="/order">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </el-menu-item>
        <el-menu-item index="/after-sale">
          <el-icon><Service /></el-icon>
          <span>售后管理</span>
        </el-menu-item>
      </el-menu>
    </div>
    <div class="main">
      <div class="topbar">
        <span class="shop-name">{{ merchantName }}</span>
      </div>
      <div class="content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { DataAnalysis, Goods, List, Service } from '@element-plus/icons-vue'
import { getMerchantInfo } from '@/api/merchant'

const route = useRoute()
const merchantName = ref('商家后台')

const activeMenu = computed(() => route.path)

onMounted(async () => {
  try {
    const res = await getMerchantInfo()
    if (res.code === 1 && res.data) {
      merchantName.value = res.data.name
    }
  } catch (e) {
    // ignore
  }
})
</script>

<style scoped>
.shop-layout {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 220px;
  background: #001529;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 64px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #ffffff1a;
}

.logo .sub {
  font-size: 12px;
  font-weight: normal;
  color: #ffffffa6;
  margin-top: 2px;
}

.sidebar .el-menu {
  border-right: none;
}

.main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f0f2f5;
}

.topbar {
  height: 64px;
  background: #fff;
  display: flex;
  align-items: center;
  padding: 0 24px;
  box-shadow: 0 1px 4px #00000014;
}

.shop-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}
</style>
