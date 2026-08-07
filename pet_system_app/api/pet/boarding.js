import request from '@/utils/request';

// 新增宠物托养
export function addBoarding(data) {
	return request({
		url: '/pet/boarding',
		method: 'post',
		data: data
	});
}

// 查询用户的托养订单列表
export function listUserBoarding(query) {
	return request({
		url: '/pet/boarding/list',
		method: 'get',
		params: query
	});
}

// 根据订单ID查询托养订单详情
export function getBoarding(boardingId) {
	return request({
		url: `/pet/boarding/${boardingId}`,
		method: 'get'
	});
}

// 删除宠物托养
export function delBoarding(boardingId) {
  return request({
    url: '/pet/boarding/' + boardingId,
    method: 'delete'
  })
}
