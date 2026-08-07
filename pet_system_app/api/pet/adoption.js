// src/api/pet/adoption.js

import request from '@/utils/request';

//查询宠物领养列表
export function listAdoptionPet(query) {
	return request({
		url: '/pet/adoption/listAdoptablePet',
		method: 'get',
		params: query
	});
}

// 申请领养接口
export function applyAdoption(data) {
	return request({
		url: '/pet/adoption',
		method: 'post',
		data: data
	});
}

// 收藏宠物接口
export function collectPet(data) {
	return request({
		url: '/pet/adoption/collect',
		method: 'post',
		data: data
	});
}
// 查询宠物信息列表
export function listPet(query) {
	return request({
		url: '/pet/pet/list',
		method: 'get',
		params: query
	});
}

// 查询宠物信息详细
export function getPet(petId) {
	return request({
		url: '/pet/pet/' + petId,
		method: 'get'
	});
}

// 查询我的领养列表
export function listMyAppliedPets(query) {
	return request({
		url: '/pet/adoption/pet/myAdoption',
		method: 'get',
		params: query
	});
}
