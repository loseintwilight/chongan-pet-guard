import request from '@/utils/request'

// 查询商品列表
export function listResource(query) {
  return request({
    url: '/pet/resource/list',
    method: 'get',
    params: query
  })
}

// 查询商品详细
export function getResource(resourceId) {
  return request({
    url: '/pet/resource/' + resourceId,
    method: 'get'
  })
}

// 新增商品
export function addResource(data) {
  return request({
    url: '/pet/resource',
    method: 'post',
    data: data
  })
}

// 修改商品
export function updateResource(data) {
  return request({
    url: '/pet/resource',
    method: 'put',
    data: data
  })
}

// 删除商品
export function delResource(resourceId) {
  return request({
    url: '/pet/resource/' + resourceId,
    method: 'delete'
  })
}
