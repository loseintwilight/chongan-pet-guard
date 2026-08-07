import request from '@/utils/request';

export function getMyRescueList(query) {
	return request({
		url: '/pet/rescue/list',
		method: 'get',
		params: query
	});
}
