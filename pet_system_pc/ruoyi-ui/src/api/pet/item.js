import request from '@/utils/request'

// 查询订单详情（订单项）列表
export function listItem(query) {
  return request({
    url: '/pet/item/list',
    method: 'get',
    params: query
  })
}

// 查询订单详情（订单项）详细
export function getItem(itemId) {
  return request({
    url: '/pet/item/' + itemId,
    method: 'get'
  })
}

// 新增订单详情（订单项）
export function addItem(data) {
  return request({
    url: '/pet/item',
    method: 'post',
    data: data
  })
}

// 修改订单详情（订单项）
export function updateItem(data) {
  return request({
    url: '/pet/item',
    method: 'put',
    data: data
  })
}

// 删除订单详情（订单项）
export function delItem(itemId) {
  return request({
    url: '/pet/item/' + itemId,
    method: 'delete'
  })
}
