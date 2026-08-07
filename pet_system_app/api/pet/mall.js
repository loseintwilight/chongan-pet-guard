import request from '@/utils/request';

export function getProductList(query) {
	return request({
		url: '/mall/product/list',
		method: 'get',
		params: query
	});
}

export function getProductDetail(resourceId) {
	return request({
		url: '/mall/product/' + resourceId,
		method: 'get'
	});
}

export function getProductListByShop(shopId, query) {
	return request({
		url: '/mall/product/listByShop',
		method: 'get',
		params: {
			shopId: shopId,
			...query
		}
	});
}

export function getShopHotProducts(shopId, limit) {
	return request({
		url: '/mall/product/listByShopHot',
		method: 'get',
		params: {
			shopId: shopId,
			limit: limit || 4
		}
	});
}

export function getCategoryList() {
	return request({
		url: '/mall/category/list',
		method: 'get'
	});
}

export function getReviewList(resourceId, query) {
	return request({
		url: '/mall/review/listWithReplies',
		method: 'get',
		params: {
			resourceId: resourceId,
			...query
		}
	});
}

export function getReviewStats(resourceId) {
	return request({
		url: '/mall/review/stats/' + resourceId,
		method: 'get'
	});
}

export function likeReview(commentId) {
	return request({
		url: '/mall/review/like/' + commentId,
		method: 'post'
	});
}

export function unlikeReview(commentId) {
	return request({
		url: '/mall/review/unlike/' + commentId,
		method: 'post'
	});
}

export function addComment(data) {
	return request({
		url: '/mall/review/add',
		method: 'post',
		data: data
	});
}