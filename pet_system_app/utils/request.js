import store from '@/store'
import { getApiBaseUrl } from '@/utils/api-base-url'
import { getToken } from '@/utils/auth'
import errorCode from '@/utils/errorCode'
import { toast, showConfirm, tansParams } from '@/utils/common'

let timeout = 10000

function toUserApiMessage(msg) {
  if (msg == null || typeof msg !== 'string') return '系统繁忙，请稍后'
  const m = msg.trim()
  if (m.length > 200) return '系统繁忙，请稍后'
  const lower = m.toLowerCase()
  if (
    lower.includes('nested exception') ||
    lower.includes('handler dispatch') ||
    lower.includes('noclassdeffounderror') ||
    lower.includes('exceptioninitializererror') ||
    m.includes('java.') ||
    m.includes('org.springframework.')
  ) {
    return '系统繁忙，请稍后'
  }
  return m
}

const request = (options) => {
  const baseUrl = getApiBaseUrl()
  const isToken = (options.headers || {}).isToken === false
  options.header = options.header || {}
  if (getToken() && !isToken) {
    options.header['Authorization'] = 'Bearer ' + getToken()
  }
  if (options.params) {
    let url = options.url + '?' + tansParams(options.params)
    url = url.slice(0, -1)
    options.url = url
  }
  const requestUrl = options.baseUrl || baseUrl + options.url
  console.log('请求开始:', {
    url: requestUrl,
    method: options.method || 'get',
    data: options.data,
    header: options.header
  })
  return new Promise((resolve, reject) => {
    uni.request({
        method: options.method || 'get',
        timeout: options.timeout ||  timeout,
        url: requestUrl,
        data: options.data,
        header: options.header,
        dataType: 'json',
        success: (res) => {
          try {
            console.log('请求响应:', res)
            if (!res || res.data === undefined || res.data === null) {
              toast('网络异常')
              reject(new Error('后端接口连接异常'))
              return
            }
            const payload = res.data
            if (typeof payload !== 'object') {
              console.error('接口返回非 JSON 对象:', payload)
              toast('服务异常')
              reject(new Error('接口返回格式异常'))
              return
            }
            const codeNum = Number(payload.code)
            const code = Number.isFinite(codeNum) ? codeNum : 200
            const msg = errorCode[code] || payload.msg || errorCode['default']
            console.log('响应处理:', { code, msg, data: payload })
            if (code === 401) {
              showConfirm('登录状态已过期，您可以继续留在该页面，或者重新登录?').then(res => {
                if (res.confirm) {
                  store.dispatch('LogOut').then(res => {
                    uni.reLaunch({ url: '/pages/login' })
                  })
                }
              })
              reject('无效的会话，或者会话已过期，请重新登录。')
            } else if (code === 500) {
              console.error('业务/系统错误(500)，原始信息:', payload.msg)
              const errorMsg = toUserApiMessage(payload.msg || '服务器内部错误，请稍后重试')
              toast(errorMsg)
              reject(new Error(errorMsg))
            } else if (code !== 200) {
              const raw = typeof msg === 'string' ? msg : String(msg || '')
              const friendly = toUserApiMessage(raw)
              toast(friendly)
              reject(new Error(friendly))
            } else {
              resolve(payload)
            }
          } catch (err) {
            console.error('处理响应时异常', err)
            toast('服务异常')
            reject(err)
          }
        },
        fail: (error) => {
          console.error('请求异常:', error)
          let message = (error && (error.errMsg || error.message)) || ''
          if (message === 'Network Error' || !message) {
            message = '后端接口连接异常'
          } else if (message.includes('timeout')) {
            message = '系统接口请求超时'
          } else if (message.includes('Request failed with status code')) {
            message = '系统接口' + message.substr(message.length - 3) + '异常'
          }
          toast(message)
          reject(error)
        }
      })
  })
}

export default request
