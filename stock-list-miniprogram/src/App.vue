<script setup>
import { useAppStore } from '@/store/index'
import { onLaunch } from '@dcloudio/uni-app'

onLaunch(() => {
  const store = useAppStore()
  store.loadSettings()
})
</script>

<style>
page {
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}
</style>
