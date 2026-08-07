import request from '@/utils/request'

// 查询订单详情（订单项）列表
export function listResourceItem(query) {
  return request({
    url: '/pet/resourceItem/list',
    method: 'get',
    params: query
  })
}

// 查询订单详情（订单项）详细
export function getResourceItem(itemId) {
  return request({
    url: '/pet/resourceItem/' + itemId,
    method: 'get'
  })
}

// 新增订单详情（订单项）
export function addResourceItem(data) {
  return request({
    url: '/pet/resourceItem',
    method: 'post',
    data: data
  })
}

// 修改订单详情（订单项）
export function updateResourceItem(data) {
  return request({
    url: '/pet/resourceItem',
    method: 'put',
    data: data
  })
}

// 删除订单详情（订单项）
export function delResourceItem(itemId) {
  return request({
    url: '/pet/resourceItem/' + itemId,
    method: 'delete'
  })
}
