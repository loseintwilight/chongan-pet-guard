import request from '@/utils/request'

// 查询店铺，站点列表
export function listShop(query) {
  return request({
    url: '/pet/shop/list',
    method: 'get',
    params: query
  })
}

// 查询店铺，站点详细
export function getShop(shopId) {
  return request({
    url: '/pet/shop/' + shopId,
    method: 'get'
  })
}

// 新增店铺，站点
export function addShop(data) {
  return request({
    url: '/pet/shop',
    method: 'post',
    data: data
  })
}

// 修改店铺，站点
export function updateShop(data) {
  return request({
    url: '/pet/shop',
    method: 'put',
    data: data
  })
}

// 删除店铺，站点
export function delShop(shopId) {
  return request({
    url: '/pet/shop/' + shopId,
    method: 'delete'
  })
}
