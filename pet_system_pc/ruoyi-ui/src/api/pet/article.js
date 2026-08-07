import request from '@/utils/request'

// 查询科普文章列表
export function listArticle(query) {
  return request({
    url: '/pet/article/list',
    method: 'get',
    params: query
  })
}

// 查询科普文章详细
export function getArticle(articleId) {
  return request({
    url: '/pet/article/' + articleId,
    method: 'get'
  })
}

// 新增科普文章
export function addArticle(data) {
  return request({
    url: '/pet/article',
    method: 'post',
    data: data
  })
}

// 修改科普文章
export function updateArticle(data) {
  return request({
    url: '/pet/article',
    method: 'put',
    data: data
  })
}

// 删除科普文章
export function delArticle(articleId) {
  return request({
    url: '/pet/article/' + articleId,
    method: 'delete'
  })
}
