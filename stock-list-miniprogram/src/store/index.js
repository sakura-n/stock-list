import { defineStore } from 'pinia'
import { get, put } from '@/utils/request'

export const useAppStore = defineStore('app', {
  state: () => ({
    settings: {
      defaultThreshold: 1,
      expireWarnDays: 7
    }
  }),
  actions: {
    async loadSettings() {
      try {
        this.settings = await get('/settings')
      } catch (e) {
        console.warn('加载设置失败，使用默认值', e)
      }
    },
    async saveSettings(data) {
      this.settings = await put('/settings', data)
    }
  }
})
