<template>
  <view class="page">
    <view class="tabs">
      <view class="tab" :class="{ active: activeTab === 'low' }" @click="activeTab = 'low'">
        库存不足 ({{ lowStockList.length }})
      </view>
      <view class="tab" :class="{ active: activeTab === 'expiring' }" @click="activeTab = 'expiring'">
        即将过期 ({{ expiringList.length }})
      </view>
    </view>

    <view v-if="activeTab === 'low'" class="list">
      <view v-for="item in lowStockList" :key="item.id" class="card" @click="goEdit(item.id)">
        <view class="card-header">
          <text class="name">{{ item.name }}</text>
          <text class="threshold">阈值 {{ thresholdText(item) }}</text>
        </view>
        <view class="card-info">
          <text>当前库存: {{ item.stock }}{{ item.unit }}</text>
          <text v-if="item.category">品类: {{ item.category }}</text>
          <text v-if="item.location">地点: {{ item.location }}</text>
        </view>
      </view>
      <view v-if="lowStockList.length === 0" class="empty">暂无库存不足的物品</view>
    </view>

    <view v-if="activeTab === 'expiring'" class="list">
      <view v-for="item in expiringList" :key="item.id" class="card card-warning" @click="goEdit(item.id)">
        <view class="card-header">
          <text class="name">{{ item.name }}</text>
          <text class="expiry">{{ item.expiryDate }} 到期</text>
        </view>
        <view class="card-info">
          <text>库存: {{ item.stock }}{{ item.unit }}</text>
          <text v-if="item.location">地点: {{ item.location }}</text>
        </view>
      </view>
      <view v-if="expiringList.length === 0" class="empty">暂无即将过期的物品</view>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { get } from '@/utils/request'

const activeTab = ref('low')
const lowStockList = ref([])
const expiringList = ref([])

function thresholdText(item) {
  return item.threshold === null || item.threshold === undefined ? '全局' : item.threshold
}

function goEdit(id) {
  uni.navigateTo({ url: '/pages/edit/edit?id=' + id })
}

async function loadData() {
  try {
    const data = await get('/purchase-list')
    lowStockList.value = data.lowStock || []
    expiringList.value = data.expiringSoon || []
  } catch (e) {
    lowStockList.value = []
    expiringList.value = []
  }
}

onShow(() => {
  loadData()
})
</script>

<style scoped>
.page { padding: 20rpx; }
.tabs { display: flex; margin-bottom: 24rpx; background: #fff; border-radius: 12rpx; overflow: hidden; }
.tab { flex: 1; text-align: center; padding: 24rpx; font-size: 28rpx; color: #666; }
.tab.active { background: #1a73e8; color: #fff; font-weight: bold; }
.card { background: #fff; border-radius: 12rpx; padding: 24rpx; margin-bottom: 16rpx; border-left: 6rpx solid #e53935; }
.card-warning { border-left-color: #f9a825; }
.card-header { display: flex; justify-content: space-between; margin-bottom: 12rpx; }
.name { font-size: 30rpx; font-weight: bold; color: #333; }
.threshold { font-size: 24rpx; color: #e53935; }
.expiry { font-size: 24rpx; color: #f9a825; }
.card-info { display: flex; gap: 24rpx; font-size: 26rpx; color: #666; flex-wrap: wrap; }
.empty { text-align: center; color: #999; padding: 60rpx 0; }
</style>
