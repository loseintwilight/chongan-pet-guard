import request from '@/utils/request';

import { getApiBaseUrl } from '@/utils/api-base-url';

import config from '@/config';


// 微信登录方法
export function wxLogin(code, encryptedIv, encryptedData) {
	const data = {
		code,
		encryptedIv,
		encryptedData
	};
	return request({
		url: '/wxLogin',
		headers: {
			isToken: false
		},
		method: 'post',
		data: data
	});
}

// 登录方法
export function login(username, password, code, uuid) {
	const data = {
		username,
		password,
		code,
		uuid
	};
	console.log('登录API请求参数:', data);
	return request({
		url: '/login',
		headers: {
			isToken: false
		},
		method: 'post',
		data: data
	});
}

// 注册方法
export function register(data) {
	return request({
		url: '/register',
		headers: {
			isToken: false
		},
		method: 'post',
		data: data
	});
}

// 获取用户详细信息
export function getInfo() {
	return request({
		url: '/getInfo',
		method: 'get'
	});
}

// 退出方法
export function logout() {
	return request({
		url: '/logout',
		method: 'post'
	});
}

// 获取验证码（勿用 require('@/config').default，小程序里 .default 常为 undefined）
export function getCodeImg() {

	return new Promise((resolve, reject) => {
		const requestUrl = config.baseUrl + '/captchaImage';
		console.log('获取验证码，请求URL:', requestUrl);
		uni.request({
			url: requestUrl,
			method: 'GET',
			timeout: 20000,
			header: {
				'Content-Type': 'application/json'
			},
			success: (res) => {
				if (res.statusCode !== 200 || res.data == null) {
					reject(new Error('验证码请求失败 HTTP ' + res.statusCode));
					return;
				}
				const body = res.data;
				if (typeof body !== 'object') {
					reject(new Error('接口返回非 JSON'));
					return;
				}
				const codeNum = Number(body.code);
				const ok = codeNum === 200 || codeNum === 0;
				const hasImg = body.img != null && String(body.img).length > 0;
				const hasUuid = body.uuid != null && String(body.uuid).length > 0;
				if (ok) {
					resolve(body);
					return;
				}
				if (hasImg && hasUuid) {
					resolve(body);
					return;
				}
				reject(new Error(body.msg || '获取验证码失败 code=' + body.code));
			},
			fail: (err) => {
				const errMsg = (err && (err.errMsg || err.message)) || '网络请求失败';
				reject(new Error(errMsg));
			}
		});
	});
}
