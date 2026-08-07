import request from '@/utils/request'

// 查询宠物托养列表
export function listBoarding(query) {
  return request({
    url: '/pet/boarding/list',
    method: 'get',
    params: query
  })
}

// 查询宠物托养详细
export function getBoarding(boardingId) {
  return request({
    url: '/pet/boarding/' + boardingId,
    method: 'get'
  })
}

// 新增宠物托养
export function addBoarding(data) {
  return request({
    url: '/pet/boarding',
    method: 'post',
    data: data
  })
}

// 修改宠物托养
export function updateBoarding(data) {
  return request({
    url: '/pet/boarding',
    method: 'put',
    data: data
  })
}

// 删除宠物托养
export function delBoarding(boardingId) {
  return request({
    url: '/pet/boarding/' + boardingId,
    method: 'delete'
  })
}
