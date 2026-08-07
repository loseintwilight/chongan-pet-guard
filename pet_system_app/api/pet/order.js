import request from '@/utils/request';

// 查询订单列表
export function getPurchaseRecords(query) {
	return request({
		url: '/pet/order/getPurchaseRecords',
		method: 'get',
		params: query
	});
}

// 查询订单详细
export function getOrder(orderId) {
	return request({
		url: '/pet/order/' + orderId,
		method: 'get'
	});
}
