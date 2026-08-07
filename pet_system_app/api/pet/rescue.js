import request from '@/utils/request';
// 查询宠物丢失列表
export function listLostPet(query) {
	return request({
		url: '/pet/lostPet/list',
		method: 'get',
		params: query
	});
}

// 新增救助登记
export function addRescue(data) {
	return request({
		url: '/pet/rescue',
		method: 'post',
		data: data
	});
}

// 查询宠物丢失详细
export function getLostPet(lostId) {
	console.log('调用getLostPet API，lostId:', lostId, '类型:', typeof lostId);
	// 确保lostId是有效的数字
	const id = Number(lostId);
	if (isNaN(id)) {
		return Promise.reject(new Error('无效的宠物ID'));
	}
	return request({
		url: `/pet/lostPet/${id}`,
		method: 'get'
	});
}

// 新增宠物丢失
export function addLostPet(data) {
	return request({
		url: '/pet/lostPet',
		method: 'post',
		data: data
	});
}

// 修改宠物丢失
export function updateLostPet(data) {
	return request({
		url: '/pet/lostPet',
		method: 'put',
		data: data
	});
}

// 删除宠物丢失
export function delLostPet(lostId) {
	return request({
		url: '/pet/lostPet/' + lostId,
		method: 'delete'
	});
}

// 获取救助详情
export function getRescueDetail(id) {
	return request({
		url: `/pet/rescue/${id}`,
		method: 'get'
	});
}

// 更新救助信息
export function updateRescue(data) {
	return request({
		url: '/pet/rescue/update',
		method: 'put',
		data: data
	});
}

// 取消救助
export function cancelRescue(id) {
	return request({
		url: `/pet/rescue/cancel/${id}`,
		method: 'post'
	});
}
