// @/api/pet/collect.js
import request from '@/utils/request';

// 收藏宠物
export function collectPet(data) {
	return request({
		url: '/pet/collect',
		method: 'post',
		data: data
	});
}

// 取消收藏
export function cancelCollect(data) {
	return request({
		url: '/pet/collect/cancelCollect',
		method: 'post',
		data: data
	});
}

export function getCollectPetsList() {
	return request({
		url: '/pet/pet/collectList',
		method: 'get'
	});
}
