// src/api/article.js
import request from '@/utils/request'; // 注意：路径是@/utils/request（带/）

// 查询科普文章列表
export function listArticle(query) {
  return request({
    url: '/pet/article/list',
    method: 'GET', // 小程序端建议大写，避免兼容性问题
    data: query // 小程序wx.request用data传参，而非params
  });
}

// 查询科普文章详细（核心报错接口）
export function getArticle(articleId) {
  return request({
    url: `/pet/article/${articleId}`, // 路径拼接保持不变
    method: 'GET'
  });
}

// 新增科普文章
export function addArticle(data) {
  return request({
    url: '/pet/article',
    method: 'POST',
    data: data
  });
}

// 修改科普文章
export function updateArticle(data) {
  return request({
    url: '/pet/article',
    method: 'PUT',
    data: data
  });
}

// 删除科普文章
export function delArticle(articleId) {
  return request({
    url: `/pet/article/${articleId}`,
    method: 'DELETE'
  });
}