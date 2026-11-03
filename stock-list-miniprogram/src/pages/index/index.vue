<template>
  <view class="page">
    <view class="search-bar">
      <input
        class="search-input"
        v-model="keyword"
        placeholder="搜索名称/品类"
        confirm-type="search"
        @confirm="loadItems"
      />
      <picker :range="categories" @change="onCategoryChange">
        <view class="filter-btn">{{ activeCategory }}</view>
      </picker>
    </view>

    <view class="item-list">
      <view
        v-for="item in items"
        :key="item.id"
        class="item-card"
        @click="goEdit(item.id)"
      >
        <view class="item-header">
          <text class="item-name">{{ item.name }}</text>
          <view class="tags">
            <text v-if="item.lowStock" class="tag tag-danger">库存不足</text>
            <text v-if="item.expiringSoon" class="tag tag-warning">临期</text>
          </view>
        </view>
        <view class="item-body">
          <image
            v-if="item.photoUrl"
            class="item-thumb"
            :src="fullUrl(item.photoUrl)"
            mode="aspectFill"
            @click.stop="previewPhoto(item)"
          />
          <view class="item-texts">
            <text v-if="item.category" class="item-category">{{ item.category }}</text>
            <text>库存: {{ item.stock }}{{ item.unit }}</text>
            <text v-if="item.location">存放: {{ item.location }}</text>
            <text v-if="item.expiryDate">效期: {{ item.expiryDate }}</text>
          </view>
        </view>
        <view class="item-actions">
          <button class="btn-sm btn-danger" @click.stop="deleteItem(item.id)">删除</button>
        </view>
      </view>

      <view v-if="items.length === 0" class="empty">
        <text>暂无物品，点击右下角 + 添加</text>
      </view>
    </view>

    <view class="fab" @click="goEdit(null)">＋</view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { get, del, fullUrl } from '@/utils/request'

const items = ref([])
const keyword = ref('')
const categories = ref(['全部'])
const activeCategory = ref('全部')

async function loadItems() {
  const cat = activeCategory.value === '全部' ? '' : activeCategory.value
  try {
    items.value = await get('/items?keyword=' + encodeURIComponent(keyword.value) + '&category=' + encodeURIComponent(cat))
  } catch (e) {
    items.value = []
  }
}

async function loadCategories() {
  try {
    const list = await get('/categories')
    categories.value = ['全部', ...list]
  } catch (e) {
    categories.value = ['全部']
  }
}

function onCategoryChange(e) {
  activeCategory.value = categories.value[e.detail.value]
  loadItems()
}

function goEdit(id) {
  uni.navigateTo({ url: '/pages/edit/edit?id=' + (id || '') })
}

function previewPhoto(item) {
  uni.previewImage({ urls: [fullUrl(item.photoUrl)] })
}

function deleteItem(id) {
  uni.showModal({
    title: '确认',
    content: '确定删除该物品？',
    success: async (res) => {
      if (res.confirm) {
        await del('/items/' + id)
        loadItems()
      }
    }
  })
}

onShow(() => {
  loadCategories()
  loadItems()
})
</script>

<style scoped>
.page { padding: 20rpx; padding-bottom: 140rpx; }
.search-bar { display: flex; gap: 16rpx; margin-bottom: 20rpx; }
.search-input { flex: 1; background: #fff; border-radius: 40rpx; padding: 16rpx 24rpx; font-size: 28rpx; }
.filter-btn { background: #fff; border-radius: 40rpx; padding: 16rpx 24rpx; font-size: 26rpx; color: #666; }
.item-card { background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 20rpx; }
.item-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12rpx; }
.item-name { font-size: 32rpx; font-weight: bold; color: #333; }
.tags { display: flex; gap: 8rpx; }
.tag { font-size: 20rpx; padding: 4rpx 12rpx; border-radius: 6rpx; }
.tag-danger { background: #ffebee; color: #e53935; }
.tag-warning { background: #fff8e1; color: #f9a825; }
.item-body { display: flex; gap: 20rpx; margin-bottom: 12rpx; }
.item-thumb { width: 120rpx; height: 120rpx; border-radius: 10rpx; background: #f0f0f0; flex-shrink: 0; }
.item-texts { display: flex; flex-wrap: wrap; gap: 16rpx; align-content: flex-start; font-size: 26rpx; color: #666; flex: 1; }
.item-category { color: #1a73e8; }
.item-actions { display: flex; justify-content: flex-end; }
.btn-sm { font-size: 24rpx; padding: 8rpx 20rpx; border-radius: 8rpx; border: none; line-height: 1.6; }
.btn-danger { background: #ffebee; color: #e53935; }
.empty { text-align: center; color: #999; padding: 80rpx 0; font-size: 28rpx; }
.fab { position: fixed; right: 40rpx; bottom: 120rpx; width: 100rpx; height: 100rpx; border-radius: 50%; background: #1a73e8; color: #fff; font-size: 48rpx; display: flex; align-items: center; justify-content: center; box-shadow: 0 6rpx 20rpx rgba(26, 115, 232, 0.4); }
</style>
