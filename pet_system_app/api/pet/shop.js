import request from '@/utils/request';

// 查询店铺，站点详细
export function getShop(shopId) {
	return request({
		url: '/pet/shop/' + shopId,
		method: 'get'
	});
}

// 查询店铺，站点列表
export function listShop(query) {
	return request({
		url: '/pet/shop/list',
		method: 'get',
		params: query
	});
}

// 提交店铺申请
export function submitShopApplication(data) {
	return request({
		url: '/pet/shop',
		method: 'post',
		data: data
	});
}
