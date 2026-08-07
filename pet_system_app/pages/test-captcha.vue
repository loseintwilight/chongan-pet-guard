<template>
	<view class="test-container">
		<view class="header">
			<text class="title">验证码测试页面</text>
		</view>

		<view class="section">
			<text class="section-title">1. 后端配置信息</text>
			<view class="info-box">
				<text class="info-item">后端地址: {{ baseUrl }}</text>
				<text class="info-item">验证码接口: {{ baseUrl }}/captchaImage</text>
			</view>
		</view>

		<view class="section">
			<text class="section-title">2. 网络测试</text>
			<button @click="testNetwork" class="test-btn">测试网络连接</button>
			<view class="result-box" v-if="networkResult">
				<text :class="networkSuccess ? 'success' : 'error'">{{ networkResult }}</text>
			</view>
		</view>

		<view class="section">
			<text class="section-title">3. 验证码测试</text>
			<button @click="testCaptcha" class="test-btn">获取验证码</button>
			<view class="captcha-display" v-if="codeUrl">
				<image :src="codeUrl" class="captcha-img" mode="aspectFit"></image>
				<text class="uuid-text">UUID: {{ uuid }}</text>
			</view>
			<view class="result-box" v-if="captchaResult">
				<text :class="captchaSuccess ? 'success' : 'error'">{{ captchaResult }}</text>
			</view>
		</view>

		<view class="section">
			<text class="section-title">4. 详细日志</text>
			<scroll-view class="log-box" scroll-y>
				<text class="log-item" v-for="(log, index) in logs" :key="index">{{ log }}</text>
			</scroll-view>
			<button @click="clearLogs" class="clear-btn">清空日志</button>
		</view>

		<view class="section">
			<text class="section-title">5. 配置建议</text>
			<view class="tips-box">
				<text class="tip-item">✓ 确保后端服务已启动</text>
				<text class="tip-item">✓ 手机和电脑在同一WiFi</text>
				<text class="tip-item">✓ 使用局域网IP而非localhost</text>
				<text class="tip-item">✓ 开发工具勾选"不校验合法域名"</text>
			</view>
		</view>

		<button @click="goBack" class="back-btn">返回登录页</button>
	</view>
</template>

<script>
import config from '@/config';
import { getCodeImg } from '@/api/login';

export default {
	data() {
		return {
			baseUrl: config.baseUrl,
			codeUrl: '',
			uuid: '',
			networkResult: '',
			networkSuccess: false,
			captchaResult: '',
			captchaSuccess: false,
			logs: []
		};
	},
	onLoad() {
		this.addLog('页面加载完成');
		this.addLog('后端地址: ' + this.baseUrl);
	},
	methods: {
		addLog(message) {
			const time = new Date().toLocaleTimeString();
			this.logs.unshift(`[${time}] ${message}`);
			console.log(message);
		},
		clearLogs() {
			this.logs = [];
			this.addLog('日志已清空');
		},
		testNetwork() {
			this.addLog('开始测试网络连接...');
			this.networkResult = '测试中...';
			this.networkSuccess = false;

			uni.request({
				url: this.baseUrl + '/captchaImage',
				method: 'GET',
				timeout: 10000,
				success: (res) => {
					this.addLog('网络请求成功');
					this.addLog('状态码: ' + res.statusCode);
					this.addLog('响应数据: ' + JSON.stringify(res.data).substring(0, 100));
					
					if (res.statusCode === 200) {
						this.networkResult = '✓ 网络连接正常';
						this.networkSuccess = true;
					} else {
						this.networkResult = '✗ 服务器返回错误: ' + res.statusCode;
						this.networkSuccess = false;
					}
				},
				fail: (err) => {
					this.addLog('网络请求失败: ' + JSON.stringify(err));
					this.networkResult = '✗ 网络连接失败: ' + (err.errMsg || '未知错误');
					this.networkSuccess = false;
				}
			});
		},
		testCaptcha() {
			this.addLog('开始获取验证码...');
			this.captchaResult = '获取中...';
			this.captchaSuccess = false;
			this.codeUrl = '';
			this.uuid = '';

			getCodeImg()
				.then((res) => {
					this.addLog('验证码接口返回成功');
					this.addLog('返回数据: ' + JSON.stringify(res).substring(0, 200));

					if (res.img && res.uuid) {
						this.codeUrl = 'data:image/gif;base64,' + res.img;
						this.uuid = res.uuid;
						this.captchaResult = '✓ 验证码获取成功';
						this.captchaSuccess = true;
						this.addLog('验证码UUID: ' + res.uuid);
						this.addLog('图片数据长度: ' + res.img.length);
					} else {
						this.captchaResult = '✗ 验证码数据不完整';
						this.captchaSuccess = false;
						this.addLog('错误: 缺少img或uuid字段');
					}
				})
				.catch((err) => {
					this.addLog('验证码获取失败: ' + JSON.stringify(err));
					this.captchaResult = '✗ 验证码获取失败: ' + (err.message || '未知错误');
					this.captchaSuccess = false;
				});
		},
		goBack() {
			uni.navigateBack();
		}
	}
};
</script>

<style lang="scss" scoped>
.test-container {
	padding: 30rpx;
	background-color: #f5f5f5;
	min-height: 100vh;
}

.header {
	text-align: center;
	padding: 40rpx 0;
	.title {
		font-size: 40rpx;
		font-weight: bold;
		color: #333;
	}
}

.section {
	background-color: #fff;
	border-radius: 20rpx;
	padding: 30rpx;
	margin-bottom: 30rpx;
	.section-title {
		display: block;
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 20rpx;
	}
}

.info-box {
	.info-item {
		display: block;
		font-size: 26rpx;
		color: #666;
		line-height: 40rpx;
		margin-bottom: 10rpx;
		word-break: break-all;
	}
}

.test-btn {
	width: 100%;
	height: 80rpx;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: #fff;
	border-radius: 16rpx;
	font-size: 30rpx;
	border: none;
}

.result-box {
	margin-top: 20rpx;
	padding: 20rpx;
	border-radius: 10rpx;
	background-color: #f9f9f9;
	.success {
		color: #52c41a;
		font-size: 28rpx;
	}
	.error {
		color: #ff4d4f;
		font-size: 28rpx;
	}
}

.captcha-display {
	margin-top: 20rpx;
	text-align: center;
	.captcha-img {
		width: 300rpx;
		height: 120rpx;
		border: 2rpx solid #ddd;
		border-radius: 10rpx;
	}
	.uuid-text {
		display: block;
		font-size: 24rpx;
		color: #999;
		margin-top: 10rpx;
		word-break: break-all;
	}
}

.log-box {
	height: 400rpx;
	background-color: #000;
	border-radius: 10rpx;
	padding: 20rpx;
	.log-item {
		display: block;
		font-size: 24rpx;
		color: #0f0;
		line-height: 36rpx;
		font-family: monospace;
		margin-bottom: 5rpx;
	}
}

.clear-btn {
	width: 100%;
	height: 60rpx;
	background-color: #ff4d4f;
	color: #fff;
	border-radius: 10rpx;
	font-size: 26rpx;
	margin-top: 20rpx;
	border: none;
}

.tips-box {
	.tip-item {
		display: block;
		font-size: 26rpx;
		color: #52c41a;
		line-height: 40rpx;
		margin-bottom: 10rpx;
	}
}

.back-btn {
	width: 100%;
	height: 80rpx;
	background-color: #1890ff;
	color: #fff;
	border-radius: 16rpx;
	font-size: 30rpx;
	margin-top: 30rpx;
	border: none;
}
</style>
