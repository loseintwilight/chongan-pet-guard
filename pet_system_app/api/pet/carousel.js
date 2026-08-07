import request from '@/utils/request'

// 查询轮播图列表
export function listCarousel(query) {
  return request({
    url: '/pet/carousel/list',
    method: 'get',
    params: query
  })
}