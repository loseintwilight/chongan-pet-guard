import request from '@/utils/request'

// 查询宠物领养列表
export function listAdoption(query) {
  return request({
    url: '/pet/adoption/list',
    method: 'get',
    params: query
  })
}

// 查询宠物领养详细
export function getAdoption(adoptionId) {
  return request({
    url: '/pet/adoption/' + adoptionId,
    method: 'get'
  })
}

// 新增宠物领养
export function addAdoption(data) {
  return request({
    url: '/pet/adoption',
    method: 'post',
    data: data
  })
}

// 修改宠物领养
export function updateAdoption(data) {
  return request({
    url: '/pet/adoption',
    method: 'put',
    data: data
  })
}

// 删除宠物领养
export function delAdoption(adoptionId) {
  return request({
    url: '/pet/adoption/' + adoptionId,
    method: 'delete'
  })
}
