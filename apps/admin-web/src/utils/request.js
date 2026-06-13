import axios from 'axios'
import { getToken, removeToken } from '@/utils/auth'
import { mockRequest } from './mock'

const rootApiPaths = ['/login', '/logout', '/getInfo', '/getRouters', '/captchaImage', '/register']
const USE_MOCK = import.meta.env.VITE_USE_MOCK === 'true'

function normalizeUrl(url = '') {
  if (/^https?:\/\//.test(url)) {
    return url
  }

  const normalizedUrl = url.startsWith('/') ? url : `/${url}`
  const isRootApi = rootApiPaths.some(path => normalizedUrl === path || normalizedUrl.startsWith(`${path}?`))

  if (normalizedUrl.startsWith('/system/') || isRootApi) {
    return normalizedUrl
  }

  return `/system${normalizedUrl}`
}

function redirectToLogin() {
  const currentPath = window.location.pathname
  if (currentPath === '/' || currentPath === '/login') {
    return
  }
  const redirect = encodeURIComponent(`${currentPath}${window.location.search}`)
  window.location.href = `/?redirect=${redirect}`
}

const instance = axios.create({
  baseURL: '/api',
  timeout: 5000
})

instance.interceptors.request.use(
  (config) => {
    config.url = normalizeUrl(config.url)
    const isToken = config.headers?.isToken === false
    if (config.headers) {
      delete config.headers.isToken
    }

    const token = getToken()
    if (token && !isToken) {
      config.headers.Authorization = `Bearer ${token}`
    }

    return config
  },
  (error) => {
    console.error('request error:', error)
    return Promise.reject(error)
  }
)

instance.interceptors.response.use(
  (response) => {
    const data = response.data
    if (data?.code === 401) {
      removeToken()
      redirectToLogin()
    }
    return data
  },
  (error) => {
    console.error('response error:', error)
    if (error.response?.status === 401) {
      removeToken()
      redirectToLogin()
    }
    if (USE_MOCK) {
      console.warn('[mock] Falling back to mock data for:', error.config?.url)
      return mockRequest(error.config?.url, error.config?.method, error.config?.data)
    }
    return Promise.reject(error)
  }
)

const get = (url, params = {}, config = {}) => {
  if (USE_MOCK) {
    return mockRequest(normalizeUrl(url), 'GET', params)
  }
  return instance.get(url, { ...config, params })
}

const post = (url, data = {}, config = {}) => {
  if (USE_MOCK) {
    return mockRequest(normalizeUrl(url), 'POST', data)
  }
  return instance.post(url, data, config)
}

const put = (url, data = {}, config = {}) => {
  if (USE_MOCK) {
    return mockRequest(normalizeUrl(url), 'PUT', data)
  }
  return instance.put(url, data, config)
}

const del = (url, params = {}, config = {}) => {
  if (USE_MOCK) {
    return mockRequest(normalizeUrl(url), 'DELETE', params)
  }
  return instance.delete(url, { ...config, params })
}

export { get, post, put, del }
