<template>
  <view class="page">
    <view class="form">
      <view class="form-item">
        <text class="label">名称 *</text>
        <input class="input" v-model="form.name" placeholder="物品名称" />
      </view>

      <view class="form-item">
        <text class="label">品类</text>
        <view class="row">
          <input class="input flex1" v-model="form.category" placeholder="如：食品/日化/医药" />
          <picker v-if="categories.length" :range="categories" @change="onCategoryPick">
            <view class="pick-btn">选择</view>
          </picker>
        </view>
      </view>

      <view class="form-item">
        <text class="label">库存数量</text>
        <input class="input" type="number" v-model="form.stock" placeholder="0" />
      </view>

      <view class="form-item">
        <text class="label">单位</text>
        <input class="input" v-model="form.unit" placeholder="件/箱/瓶" />
      </view>

      <view class="form-item">
        <text class="label">有效期</text>
        <picker mode="date" :value="form.expiryDate" start="2000-01-01" end="2100-12-31" @change="onDateChange">
          <view class="input picker-display">
            <text :class="{ placeholder: !form.expiryDate }">{{ form.expiryDate || '点击选择日期' }}</text>
          </view>
        </picker>
        <view v-if="form.expiryDate" class="clear-link" @click="form.expiryDate = ''">清除日期</view>
      </view>

      <view class="form-item">
        <text class="label">存放地点</text>
        <input class="input" v-model="form.location" placeholder="如：冰箱/阳台柜子" />
      </view>

      <view class="form-item">
        <text class="label">库存阈值</text>
        <input class="input" type="number" v-model="form.threshold" placeholder="留空使用全局默认阈值" />
        <text class="hint">当库存 ≤ 阈值时会出现在「待购」清单；留空则使用全局设置</text>
      </view>

      <view class="form-item">
        <text class="label">备注</text>
        <textarea class="textarea" v-model="form.remark" placeholder="备注信息" />
      </view>

      <view class="form-item">
        <text class="label">照片</text>
        <view v-if="photoPreview" class="photo-box">
          <image class="photo-preview" :src="photoPreview" mode="aspectFill" @click="previewPhoto" />
          <view class="photo-remove" @click="removePhoto">× 移除照片</view>
        </view>
        <view v-else class="photo-add" @click="choosePhoto">＋ 选择 / 拍摄照片</view>
        <text class="hint">支持 jpg/png/webp，不超过 5MB，仅一张主图</text>
      </view>

      <button class="btn-primary" :loading="saving" @click="save">保存</button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { get, post, put, fullUrl, BASE_ORIGIN } from '@/utils/request'

const form = ref({
  name: '',
  category: '',
  stock: 0,
  unit: '件',
  expiryDate: '',
  location: '',
  threshold: '',
  remark: '',
  photoUrl: ''
})

const photoPreview = ref('')

const categories = ref([])
const editId = ref(null)
const saving = ref(false)

onLoad((options) => {
  editId.value = options && options.id ? options.id : null
  if (editId.value) {
    loadItem(editId.value)
  }
})

onMounted(async () => {
  try {
    categories.value = await get('/categories')
  } catch (e) {
    categories.value = []
  }
})

async function loadItem(id) {
  const data = await get('/items/' + id)
  form.value = {
    name: data.name || '',
    category: data.category || '',
    stock: data.stock ?? 0,
    unit: data.unit || '件',
    expiryDate: data.expiryDate || '',
    location: data.location || '',
    threshold: data.threshold === null || data.threshold === undefined ? '' : data.threshold,
    remark: data.remark || '',
    photoUrl: data.photoUrl || ''
  }
  photoPreview.value = fullUrl(data.photoUrl)
}

function choosePhoto() {
  uni.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    success: (res) => {
      uni.showLoading({ title: '上传中...' })
      uni.uploadFile({
        url: BASE_ORIGIN + '/api/upload',
        filePath: res.tempFilePaths[0],
        name: 'file',
        success: (r) => {
          let data = null
          try {
            data = JSON.parse(r.data)
          } catch (e) {
            uni.showToast({ title: '上传返回异常', icon: 'none' })
            return
          }
          if (data.code === 0) {
            form.value.photoUrl = data.data
            photoPreview.value = fullUrl(data.data)
            uni.showToast({ title: '照片已上传', icon: 'success' })
          } else {
            uni.showToast({ title: data.message || '上传失败', icon: 'none' })
          }
        },
        fail: () => uni.showToast({ title: '上传失败，请确认后端已启动', icon: 'none' }),
        complete: () => uni.hideLoading()
      })
    }
  })
}

function removePhoto() {
  form.value.photoUrl = ''
  photoPreview.value = ''
}

function previewPhoto() {
  uni.previewImage({ urls: [photoPreview.value] })
}

function onCategoryPick(e) {
  form.value.category = categories.value[e.detail.value]
}

function onDateChange(e) {
  form.value.expiryDate = e.detail.value
}

async function save() {
  if (!form.value.name) {
    uni.showToast({ title: '名称不能为空', icon: 'none' })
    return
  }
  const payload = {
    name: form.value.name,
    category: form.value.category || null,
    stock: Number(form.value.stock) || 0,
    unit: form.value.unit || '件',
    expiryDate: form.value.expiryDate || null,
    location: form.value.location || null,
    threshold: form.value.threshold === '' || form.value.threshold === null ? null : Number(form.value.threshold),
    remark: form.value.remark || null,
    photoUrl: form.value.photoUrl || null
  }
  saving.value = true
  try {
    if (editId.value) {
      await put('/items/' + editId.value, payload)
    } else {
      await post('/items', payload)
    }
    uni.showToast({ title: '保存成功', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 800)
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.page { padding: 30rpx; }
.form-item { margin-bottom: 30rpx; }
.label { font-size: 26rpx; color: #333; margin-bottom: 10rpx; display: block; font-weight: 500; }
.input { background: #fff; border-radius: 10rpx; padding: 20rpx; font-size: 28rpx; border: 1rpx solid #eee; }
.row { display: flex; gap: 16rpx; align-items: center; }
.flex1 { flex: 1; }
.pick-btn { background: #eaf1fb; color: #1a73e8; border-radius: 10rpx; padding: 20rpx 28rpx; font-size: 26rpx; }
.picker-display { min-height: 40rpx; }
.placeholder { color: #999; }
.clear-link { font-size: 24rpx; color: #e53935; margin-top: 10rpx; display: inline-block; }
.textarea { background: #fff; border-radius: 10rpx; padding: 20rpx; font-size: 28rpx; border: 1rpx solid #eee; width: 100%; height: 160rpx; box-sizing: border-box; }
.hint { font-size: 22rpx; color: #999; margin-top: 8rpx; display: block; }
.btn-primary { margin-top: 40rpx; background: #1a73e8; color: #fff; border-radius: 12rpx; font-size: 32rpx; padding: 20rpx; }
.photo-add { border: 2rpx dashed #bbb; border-radius: 12rpx; padding: 40rpx 0; text-align: center; color: #999; font-size: 28rpx; background: #fff; }
.photo-box { display: flex; align-items: center; gap: 20rpx; }
.photo-preview { width: 200rpx; height: 200rpx; border-radius: 12rpx; background: #eee; }
.photo-remove { font-size: 26rpx; color: #e53935; padding: 12rpx 20rpx; }
</style>
