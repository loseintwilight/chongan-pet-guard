import { getApiBaseUrl } from '@/utils/api-base-url'
import storage from '@/utils/storage'
import constant from '@/utils/constant'
import { isHttp, isEmpty } from "@/utils/validate"
import { login,wxLogin, logout, getInfo } from '@/api/login'
import { getToken, setToken, removeToken } from '@/utils/auth'
import defAva from '@/static/images/profile.jpg'

const user = {
  state: {
    token: getToken(),
    id: storage.get(constant.id),
    name: storage.get(constant.name),
    avatar: storage.get(constant.avatar),
    roles: storage.get(constant.roles),
    permissions: storage.get(constant.permissions)
  },

  mutations: {
    SET_TOKEN: (state, token) => {
      state.token = token
    },
    SET_ID: (state, id) => {
      state.id = id
      storage.set(constant.id, id)
    },
    SET_NAME: (state, name) => {
      state.name = name
      storage.set(constant.name, name)
    },
    SET_AVATAR: (state, avatar) => {
      state.avatar = avatar
      storage.set(constant.avatar, avatar)
    },
    SET_ROLES: (state, roles) => {
      state.roles = roles
      storage.set(constant.roles, roles)
    },
    SET_PERMISSIONS: (state, permissions) => {
      state.permissions = permissions
      storage.set(constant.permissions, permissions)
    }
  },

  actions: {
	  // 微信登录
	  WxLogin({ commit }, userInfo) {
	    const code = userInfo.code
	    const encryptedIv = userInfo.encryptedIv
	    const encryptedData = userInfo.encryptedData
	    return new Promise((resolve, reject) => {
	      wxLogin(code, encryptedIv, encryptedData).then(res => {
	        setToken(res.token)
	        commit('SET_TOKEN', res.token)
	        resolve()
	      }).catch(error => {
	        reject(error)
	      })
	    })
	  },
    // 登录
    Login({ commit }, userInfo) {
      const username = userInfo.username.trim()
      const password = userInfo.password
      const code = userInfo.code
      const uuid = userInfo.uuid
      console.log('登录请求参数:', { username, password, code, uuid })
      return new Promise((resolve, reject) => {
        login(username, password, code, uuid).then(res => {
          console.log('登录请求成功:', res)
          if (!res || !res.token) {
            console.error('登录返回缺少 token', res)
            reject(new Error('登录失败，请重试'))
            return
          }
          setToken(res.token)
          commit('SET_TOKEN', res.token)
          resolve()
        }).catch(error => {
          console.error('登录请求失败:', error)
          reject(error)
        })
      })
    },

    // 获取用户信息
    GetInfo({ commit, state }) {
      console.log('获取用户信息，当前token:', state.token)
      return new Promise((resolve, reject) => {
        getInfo().then(res => {
          console.log('获取用户信息成功:', res)
          const user = res.user
          console.log('用户信息:', user)
		  let avatar = user.avatar || ""
		  if (!isHttp(avatar)) {
            avatar = (isEmpty(avatar)) ? defAva : getApiBaseUrl() + avatar
          }
          const userid = (isEmpty(user) || isEmpty(user.userId)) ? "" : user.userId
		  const username = (isEmpty(user) || isEmpty(user.userName)) ? "" : user.userName
		  if (res.roles && res.roles.length > 0) {
            commit('SET_ROLES', res.roles)
            commit('SET_PERMISSIONS', res.permissions)
          } else {
            commit('SET_ROLES', ['ROLE_DEFAULT'])
          }
          commit('SET_ID', userid)
          commit('SET_NAME', username)
          commit('SET_AVATAR', avatar)
          resolve(res)
        }).catch(error => {
          console.error('获取用户信息失败:', error)
          reject(error)
        })
      })
    },

    // 退出系统
    LogOut({ commit, state }) {
      return new Promise((resolve, reject) => {
        logout(state.token).then(() => {
          commit('SET_TOKEN', '')
          commit('SET_ROLES', [])
          commit('SET_PERMISSIONS', [])
          removeToken()
          storage.clean()
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    }
  }
}

export default user
