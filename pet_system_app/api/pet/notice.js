import request from '@/utils/request';

export function getPersonNoticeMsg(query) {
	return request({
		url: '/pet/notice/getPersonNoticeMsg',
		method: 'get',
		params: query
	});
}

export function getUnReadNoticeNumber() {
	return request({
		url: '/pet/notice/getUnReadNoticeNumber',
		method: 'get'
	});
}

// 查询通知详细
export function getNoticeDetail(noticeId) {
	return request({
		url: '/pet/notice/' + noticeId,
		method: 'get'
	});
}
