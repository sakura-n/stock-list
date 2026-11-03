// #ifdef H5
// H5 打包为内嵌网页时同源访问（自动跟随实际端口）；H5 本地开发（vite:5173）后端在 8090
const BASE_ORIGIN =
  process.env.NODE_ENV === 'development'
    ? 'http://localhost:8090'
    : window.location.origin
// #endif
// #ifndef H5
// 小程序无同源概念，固定指向本地后端
const BASE_ORIGIN = 'http://localhost:8090'
// #endif
const BASE_URL = BASE_ORIGIN + '/api'

// 把后端返回的相对路径（如 /uploads/xx.jpg）拼成完整地址
export function fullUrl(path) {
  if (!path) return ''
  return path.startsWith('http') ? path : BASE_ORIGIN + path
}

export function request(url, method = 'GET', data = null) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + url,
      method,
      data,
      header: { 'Content-Type': 'application/json' },
      success: (res) => {
        if (res.statusCode === 200 && res.data.code === 0) {
          resolve(res.data.data)
        } else {
          const msg = (res.data && res.data.message) || '请求失败'
          uni.showToast({ title: msg, icon: 'none' })
          reject(res.data)
        }
      },
      fail: (err) => {
        uni.showToast({ title: '网络错误，请确认后端已启动', icon: 'none' })
        reject(err)
      }
    })
  })
}

export const get = (url) => request(url, 'GET')
export const post = (url, data) => request(url, 'POST', data)
export const put = (url, data) => request(url, 'PUT', data)
export const del = (url) => request(url, 'DELETE')
export { BASE_ORIGIN }
