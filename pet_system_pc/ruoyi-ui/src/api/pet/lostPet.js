import request from '@/utils/request'

// 查询宠物丢失列表
export function listLostPet(query) {
  return request({
    url: '/pet/lostPet/list',
    method: 'get',
    params: query
  })
}

// 查询宠物丢失详细
export function getLostPet(lostId) {
  return request({
    url: '/pet/lostPet/' + lostId,
    method: 'get'
  })
}

// 新增宠物丢失
export function addLostPet(data) {
  return request({
    url: '/pet/lostPet',
    method: 'post',
    data: data
  })
}

// 修改宠物丢失
export function updateLostPet(data) {
  return request({
    url: '/pet/lostPet',
    method: 'put',
    data: data
  })
}

// 删除宠物丢失
export function delLostPet(lostId) {
  return request({
    url: '/pet/lostPet/' + lostId,
    method: 'delete'
  })
}
