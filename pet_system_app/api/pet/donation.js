import request from '@/utils/request';

// 新增捐赠
export function addDonation(data) {
	return request({
		url: '/pet/donation',
		method: 'post',
		data: data
	});
}
