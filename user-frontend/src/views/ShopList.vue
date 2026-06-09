<template>
  <div class="shop-list-page">
    <!-- 顶部导航 -->
    <div class="header-top">
      <div class="header-content">
        <div class="left-links">
          <span class="region-link">中国大陆 ▾</span>
        </div>
        <div class="right-links">
          <span class="action-link" @click="$router.push('/')">首页</span>
          <span class="divider">|</span>
          <span class="action-link" @click="$router.push('/personal')">个人中心</span>
        </div>
      </div>
    </div>

    <div class="header-search">
      <div class="search-content">
        <div class="logo" @click="$router.push('/')">
          <span class="logo-icon">潮</span>
          <span class="logo-text">潮选优品</span>
        </div>
        <div class="search-center">
          <h1 class="page-title">全部店铺</h1>
        </div>
      </div>
    </div>

    <!-- 店铺列表 -->
    <div class="shop-container">
      <div class="shop-grid">
        <div v-for="shop in shops" :key="shop.id" class="shop-card" @click="$router.push(`/merchant/${shop.id}`)">
          <div class="shop-card-header">
            <div class="shop-avatar" :style="{ background: getGradient(shop.id) }">
              {{ shop.name.charAt(0) }}
            </div>
            <div class="shop-header-info">
              <h3 class="shop-name">{{ shop.name }}</h3>
              <div class="shop-score">
                <span class="score-star">★</span>
                <span class="score-num">{{ shop.score }}</span>
              </div>
            </div>
          </div>
          <p class="shop-desc">{{ shop.description || '暂无简介' }}</p>
          <div class="shop-footer">
            <span class="shop-enter">进店逛逛 →</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getRecommendMerchants } from '../api/merchant'

const shops = ref([])

const gradients = [
  'linear-gradient(135deg, #FF6B8A, #FF8FAB)',
  'linear-gradient(135deg, #FF8C42, #FF6B35)',
  'linear-gradient(135deg, #5BA0D9, #4A90D9)',
  'linear-gradient(135deg, #F0A030, #E89020)',
  'linear-gradient(135deg, #7C5CFC, #9B7DFF)',
  'linear-gradient(135deg, #36D1DC, #5B86E5)',
  'linear-gradient(135deg, #FF6B6B, #EE5A24)',
  'linear-gradient(135deg, #11998E, #38EF7D)',
  'linear-gradient(135deg, #FC5C7D, #6A82FB)',
  'linear-gradient(135deg, #F7971E, #FFD200)',
]

const getGradient = (id) => gradients[(id - 1) % gradients.length]

onMounted(async () => {
  try {
    const result = await getRecommendMerchants(50)
    if (result.code === 1) shops.value = result.data || []
  } catch (e) {}
})
</script>

<style scoped>
.shop-list-page { min-height: 100vh; background: #f5f5f5; }

.header-top {
  background: #fff;
  border-bottom: 1px solid #eee;
  height: 32px;
  line-height: 32px;
}
.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  justify-content: space-between;
}
.left-links, .right-links { display: flex; align-items: center; gap: 10px; }
.region-link, .action-link { font-size: 12px; color: #666; cursor: pointer; }
.action-link:hover { color: #E53935; }
.divider { color: #ddd; font-size: 12px; }

.header-search {
  background: #f5f5f5;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
}
.search-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  gap: 40px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  flex-shrink: 0;
}
.logo-icon {
  background: #ff6b00;
  color: white;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  border-radius: 8px;
}
.logo-text { font-size: 22px; color: #ff6b00; font-weight: 700; }
.page-title { margin: 0; font-size: 24px; color: #333; font-weight: 600; }

.shop-container {
  max-width: 1200px;
  margin: 24px auto;
  padding: 0 24px;
}
.shop-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
@media (max-width: 1024px) { .shop-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 768px) { .shop-grid { grid-template-columns: repeat(2, 1fr); } }

.shop-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #eee;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.shop-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
  border-color: #E53935;
}
.shop-card-header { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.shop-avatar {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  flex-shrink: 0;
}
.shop-header-info { flex: 1; overflow: hidden; }
.shop-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.shop-score { display: flex; align-items: center; gap: 4px; margin-top: 2px; }
.score-star { color: #FFB800; font-size: 13px; }
.score-num { font-size: 13px; color: #666; font-weight: 600; }
.shop-desc {
  margin: 0;
  font-size: 13px;
  color: #999;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  min-height: 42px;
}
.shop-footer { margin-top: 12px; }
.shop-enter { font-size: 13px; color: #E53935; font-weight: 500; }
.shop-card:hover .shop-enter { text-decoration: underline; }
</style>
