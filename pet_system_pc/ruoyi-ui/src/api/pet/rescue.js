import request from '@/utils/request'

// 查询救助登记列表
export function listRescue(query) {
  return request({
    url: '/pet/rescue/list',
    method: 'get',
    params: query
  })
}

// 查询救助登记详细
export function getRescue(rescueId) {
  return request({
    url: '/pet/rescue/' + rescueId,
    method: 'get'
  })
}

// 新增救助登记
export function addRescue(data) {
  return request({
    url: '/pet/rescue',
    method: 'post',
    data: data
  })
}

// 修改救助登记
export function updateRescue(data) {
  return request({
    url: '/pet/rescue',
    method: 'put',
    data: data
  })
}

// 删除救助登记
export function delRescue(rescueId) {
  return request({
    url: '/pet/rescue/' + rescueId,
    method: 'delete'
  })
}
