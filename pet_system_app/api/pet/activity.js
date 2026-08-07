import request from '@/utils/request';

// 查询活动列表
export function listActivity(query) {
	return request({
		url: '/pet/activity/list',
		method: 'get',
		params: query
	});
}

//查看用户的活动信息
export function getUserActivityList(query) {
	return request({
		url: '/pet/activity/wxList',
		mathod: 'get',
		params: query
	});
}

// 查询活动详细
export function getActivity(activityId) {
	return request({
		url: '/pet/activity/' + activityId,
		method: 'get'
	});
}

export function signUp(activityId) {
	return request({
		url: '/pet/activity_registration/signup/' + activityId,
		method: 'get'
	});
}
