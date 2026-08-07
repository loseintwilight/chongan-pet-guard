import request from '@/utils/request';

// 查询商品详细
export function getResource(resourceId) {
	return request({
		url: '/pet/resource/' + resourceId,
		method: 'get'
	});
}
