import request from '@/utils/request'

// 查询用户报名列表
export function listActivity_registration(query) {
  return request({
    url: '/pet/activity_registration/list',
    method: 'get',
    params: query
  })
}

// 查询用户报名详细
export function getActivity_registration(uaId) {
  return request({
    url: '/pet/activity_registration/' + uaId,
    method: 'get'
  })
}

// 新增用户报名
export function addActivity_registration(data) {
  return request({
    url: '/pet/activity_registration',
    method: 'post',
    data: data
  })
}

// 修改用户报名
export function updateActivity_registration(data) {
  return request({
    url: '/pet/activity_registration',
    method: 'put',
    data: data
  })
}

// 删除用户报名
export function delActivity_registration(uaId) {
  return request({
    url: '/pet/activity_registration/' + uaId,
    method: 'delete'
  })
}
