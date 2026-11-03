<template>
  <view class="page">
    <view class="section">
      <text class="section-title">全局购买提醒设置</text>

      <view class="form-item">
        <text class="label">默认库存阈值</text>
        <input class="input" type="number" v-model="form.defaultThreshold" />
        <text class="hint">物品未单独设置阈值时使用此值；库存 ≤ 此值时提示需要购买</text>
      </view>

      <view class="form-item">
        <text class="label">过期预警天数</text>
        <input class="input" type="number" v-model="form.expireWarnDays" />
        <text class="hint">有效期距今天数 ≤ 此值时提示「临期」</text>
      </view>

      <button class="btn-primary" :loading="saving" @click="save">保存设置</button>
    </view>

    <view class="section tips">
      <text class="section-title">说明</text>
      <text class="tip-line">1. 待购清单 = 库存不足 + 即将过期 两部分</text>
      <text class="tip-line">2. 单个物品可设置自己的阈值，优先级高于全局设置</text>
      <text class="tip-line">3. 后端地址配置在 src/utils/request.js 的 BASE_URL</text>
    </view>
  </view>
</template>

<script setup>
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useAppStore } from '@/store/index'

const store = useAppStore()
const form = ref({ defaultThreshold: 1, expireWarnDays: 7 })
const saving = ref(false)

onShow(async () => {
  await store.loadSettings()
  form.value = { ...store.settings }
})

async function save() {
  const payload = {
    defaultThreshold: Number(form.value.defaultThreshold) || 1,
    expireWarnDays: Number(form.value.expireWarnDays) || 7
  }
  saving.value = true
  try {
    await store.saveSettings(payload)
    uni.showToast({ title: '已保存', icon: 'success' })
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.page { padding: 30rpx; }
.section { background: #fff; border-radius: 16rpx; padding: 30rpx; margin-bottom: 24rpx; }
.section-title { font-size: 32rpx; font-weight: bold; color: #333; margin-bottom: 30rpx; display: block; }
.form-item { margin-bottom: 30rpx; }
.label { font-size: 28rpx; color: #333; display: block; margin-bottom: 12rpx; font-weight: 500; }
.input { background: #f5f5f5; border-radius: 10rpx; padding: 20rpx; font-size: 28rpx; width: 100%; box-sizing: border-box; }
.hint { font-size: 22rpx; color: #999; margin-top: 8rpx; display: block; }
.btn-primary { margin-top: 20rpx; background: #1a73e8; color: #fff; border-radius: 12rpx; font-size: 32rpx; padding: 20rpx; }
.tips { background: #f0f5ff; }
.tip-line { display: block; font-size: 24rpx; color: #555; line-height: 1.9; }
</style>
