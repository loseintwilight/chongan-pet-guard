<template>
	<view class="debug-container">
		<view class="header">
			<text class="title">验证码调试工具</text>
			<text class="subtitle">用于诊断验证码问题</text>
		</view>

		<!-- 配置信息 -->
		<view class="card">
			<text class="card-title">📋 配置信息</text>
			<view class="info-item">
				<text class="label">后端地址:</text>
				<text class="value">{{ baseUrl }}</text>
			</view>
			<view class="info-item">
				<text class="label">验证码接口:</text>
				<text class="value">{{ baseUrl }}/captchaImage</text>
			</view>
		</view>

		<!-- 快速测试 -->
		<view class="card">
			<text class="card-title">🧪 快速测试</text>
			<button @click="testCaptcha" class="btn btn-primary">获取验证码</button>
			<button @click="testNetwork" class="btn btn-secondary">测试网络</button>
			<button @click="clearAll" class="btn btn-danger">清空所有</button>
		</view>

		<!-- 验证码显示 -->
		<view class="card" v-if="captchaImg">
			<text class="card-title">✅ 验证码</text>
			<image :src="captchaImg" class="captcha-image" mode="aspectFit"></image>
			<view class="info-item">
				<text class="label">UUID:</text>
				<text class="value">{{ captchaUuid }}</text>
			</view>
		</view>

		<!-- 响应数据 -->
		<view class="card" v-if="responseData">
			<text class="card-title">📦 响应数据</text>
			<view class="code-block">
				<text class="code-text">{{ responseData }}</text>
			</view>
		</view>

		<!-- 错误信息 -->
		<view class="card error-card" v-if="errorMsg">
			<text class="card-title">❌ 错误信息</text>
			<text class="error-text">{{ errorMsg }}</text>
		</view>

		<!-- 日志 -->
		<view class="card">
			<text class="card-title">📝 调试日志</text>
			<scroll-view class="log-container" scroll-y>
				<view v-for="(log, index) in logs" :key="index" class="log-item">
					<text :class="['log-text', log.type]">{{ log.message }}</text>
				</view>
			</scroll-view>
		</view>

		<!-- 建议 -->
		<view class="card tips-card">
			<text class="card-title">💡 检查清单</text>
			<view class="tip-item">
				<text class="tip-icon">{{ backendRunning ? '✓' : '✗' }}</text>
				<text>后端服务已启动</text>
			</view>
			<view class="tip-item">
				<text class="tip-icon">{{ networkOk ? '✓' : '✗' }}</text>
				<text>网络连接正常</text>
			</view>
			<view class="tip-item">
				<text class="tip-icon">{{ captchaOk ? '✓' : '✗' }}</text>
				<text>验证码获取成功</text>
			</view>
		</view>

		<button @click="goBack" class="btn btn-back">返回登录</button>
	</view>
</template>

<script>
import config from '@/config';

export default {
	data() {
		return {
			baseUrl: config.baseUrl,
			captchaImg: '',
			captchaUuid: '',
			responseData: '',
			errorMsg: '',
			logs: [],
			backendRunning: false,
			networkOk: false,
			captchaOk: false
		};
	},
	onLoad() {
		this.addLog('调试工具已加载', 'info');
		this.addLog('后端地址: ' + this.baseUrl, 'info');
	},
	methods: {
		addLog(message, type = 'info') {
			const time = new Date().toLocaleTimeString();
			this.logs.unshift({
				message: `[${time}] ${message}`,
				type: type
			});
			console.log(`[${type}] ${message}`);
		},
		
		testNetwork() {
			this.addLog('开始测试网络连接...', 'info');
			this.errorMsg = '';
			
			uni.request({
				url: this.baseUrl + '/captchaImage',
				method: 'GET',
				timeout: 10000,
				success: (res) => {
					this.addLog('网络请求成功，状态码: ' + res.statusCode, 'success');
					this.networkOk = true;
					this.backendRunning = res.statusCode === 200;
					
					if (res.statusCode === 200) {
						this.addLog('✓ 后端服务正常运行', 'success');
					} else {
						this.addLog('✗ 后端返回错误状态码', 'error');
					}
				},
				fail: (err) => {
					this.addLog('网络请求失败: ' + JSON.stringify(err), 'error');
					this.errorMsg = '网络连接失败: ' + (err.errMsg || '未知错误');
					this.networkOk = false;
					this.backendRunning = false;
				}
			});
		},
		
		testCaptcha() {
			this.addLog('开始获取验证码...', 'info');
			this.errorMsg = '';
			this.responseData = '';
			this.captchaImg = '';
			this.captchaUuid = '';
			
			const requestUrl = this.baseUrl + '/captchaImage';
			this.addLog('请求URL: ' + requestUrl, 'info');
			
			uni.request({
				url: requestUrl,
				method: 'GET',
				timeout: 20000,
				header: {
					'Content-Type': 'application/json'
				},
				success: (res) => {
					this.addLog('请求成功，状态码: ' + res.statusCode, 'success');
					this.addLog('响应数据: ' + JSON.stringify(res.data).substring(0, 200), 'info');
					
					this.responseData = JSON.stringify(res.data, null, 2);
					
					if (res.statusCode === 200 && res.data) {
						const data = res.data.data || res.data;
						
						if (data.img && data.uuid) {
							this.captchaImg = 'data:image/gif;base64,' + data.img;
							this.captchaUuid = data.uuid;
							this.captchaOk = true;
							this.addLog('✓ 验证码获取成功', 'success');
							this.addLog('UUID: ' + data.uuid, 'success');
							this.addLog('图片数据长度: ' + data.img.length, 'success');
						} else {
							this.errorMsg = '验证码数据不完整';
							this.addLog('✗ 缺少img或uuid字段', 'error');
							this.captchaOk = false;
						}
					} else {
						this.errorMsg = '请求失败，状态码: ' + res.statusCode;
						this.addLog('✗ 请求失败', 'error');
						this.captchaOk = false;
					}
				},
				fail: (err) => {
					this.addLog('请求失败: ' + JSON.stringify(err), 'error');
					this.errorMsg = '请求失败: ' + (err.errMsg || '未知错误');
					this.captchaOk = false;
				}
			});
		},
		
		clearAll() {
			this.captchaImg = '';
			this.captchaUuid = '';
			this.responseData = '';
			this.errorMsg = '';
			this.logs = [];
			this.addLog('已清空所有数据', 'info');
		},
		
		goBack() {
			uni.navigateBack();
		}
	}
};
</script>

<style lang="scss" scoped>
.debug-container {
	padding: 20rpx;
	background-color: #f5f5f5;
	min-height: 100vh;
}

.header {
	text-align: center;
	padding: 30rpx 0;
	margin-bottom: 20rpx;
	
	.title {
		display: block;
		font-size: 40rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 10rpx;
	}
	
	.subtitle {
		display: block;
		font-size: 24rpx;
		color: #999;
	}
}

.card {
	background-color: #fff;
	border-radius: 12rpx;
	padding: 20rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
	
	.card-title {
		display: block;
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 15rpx;
		padding-bottom: 10rpx;
		border-bottom: 2rpx solid #f0f0f0;
	}
}

.info-item {
	display: flex;
	margin-bottom: 12rpx;
	
	.label {
		width: 120rpx;
		color: #666;
		font-size: 24rpx;
		font-weight: bold;
	}
	
	.value {
		flex: 1;
		color: #333;
		font-size: 24rpx;
		word-break: break-all;
		font-family: monospace;
	}
}

.btn {
	width: 100%;
	height: 70rpx;
	border-radius: 10rpx;
	border: none;
	font-size: 26rpx;
	font-weight: bold;
	margin-bottom: 12rpx;
	color: #fff;
	
	&.btn-primary {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	}
	
	&.btn-secondary {
		background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
	}
	
	&.btn-danger {
		background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
	}
	
	&.btn-back {
		background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
		margin-top: 20rpx;
	}
}

.captcha-image {
	width: 100%;
	height: 150rpx;
	border: 2rpx solid #ddd;
	border-radius: 8rpx;
	margin-bottom: 15rpx;
}

.code-block {
	background-color: #f5f5f5;
	border-radius: 8rpx;
	padding: 15rpx;
	max-height: 300rpx;
	overflow-y: auto;
	
	.code-text {
		font-size: 20rpx;
		color: #333;
		font-family: monospace;
		line-height: 30rpx;
		word-break: break-all;
	}
}

.error-card {
	border-left: 4rpx solid #ff4d4f;
	
	.error-text {
		color: #ff4d4f;
		font-size: 24rpx;
		line-height: 36rpx;
		word-break: break-all;
	}
}

.log-container {
	height: 300rpx;
	background-color: #1e1e1e;
	border-radius: 8rpx;
	padding: 12rpx;
	
	.log-item {
		margin-bottom: 8rpx;
		
		.log-text {
			font-size: 20rpx;
			font-family: monospace;
			line-height: 28rpx;
			
			&.info {
				color: #0f0;
			}
			
			&.success {
				color: #0f0;
				font-weight: bold;
			}
			
			&.error {
				color: #f00;
				font-weight: bold;
			}
		}
	}
}

.tips-card {
	border-left: 4rpx solid #1890ff;
	
	.tip-item {
		display: flex;
		align-items: center;
		margin-bottom: 12rpx;
		font-size: 24rpx;
		color: #333;
		
		.tip-icon {
			width: 40rpx;
			text-align: center;
			font-weight: bold;
			margin-right: 10rpx;
			font-size: 28rpx;
		}
	}
}
</style>
