import request from '@/utils/request'

// 查询通知与用户之间的列表
export function listUserNotice(query) {
  return request({
    url: '/pet/userNotice/list',
    method: 'get',
    params: query
  })
}

// 查询通知与用户之间的详细
export function getUserNotice(unId) {
  return request({
    url: '/pet/userNotice/' + unId,
    method: 'get'
  })
}

// 新增通知与用户之间的
export function addUserNotice(data) {
  return request({
    url: '/pet/userNotice',
    method: 'post',
    data: data
  })
}

// 修改通知与用户之间的
export function updateUserNotice(data) {
  return request({
    url: '/pet/userNotice',
    method: 'put',
    data: data
  })
}

// 删除通知与用户之间的
export function delUserNotice(unId) {
  return request({
    url: '/pet/userNotice/' + unId,
    method: 'delete'
  })
}
