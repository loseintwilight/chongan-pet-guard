import request from '@/utils/request'

// 查询通知模板列表
export function listTemplate(query) {
  return request({
    url: '/pet/template/list',
    method: 'get',
    params: query
  })
}

// 查询通知模板详细
export function getTemplate(templateId) {
  return request({
    url: '/pet/template/' + templateId,
    method: 'get'
  })
}

// 新增通知模板
export function addTemplate(data) {
  return request({
    url: '/pet/template',
    method: 'post',
    data: data
  })
}

// 修改通知模板
export function updateTemplate(data) {
  return request({
    url: '/pet/template',
    method: 'put',
    data: data
  })
}

// 删除通知模板
export function delTemplate(templateId) {
  return request({
    url: '/pet/template/' + templateId,
    method: 'delete'
  })
}

export function sendNoticeByTemplate(params) {
  return request({
    url: '/pet/template/sendNotice',
    method: 'post',
    data: params
  })
}
