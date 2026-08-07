<template>
	<view class="tech-pet-login-container">
		<!-- 动态星空背景 -->
		<view class="starfield-bg">
			<view class="stars"></view>
			<view class="grid-lines"></view>
		</view>

		<!-- 主要内容区 -->
		<view class="main-content">
			<!-- Logo和标题 -->
			<view class="header-section">
				<view class="logo-wrapper">
					<image class="logo-image" src="/static/pet-logo.jpeg" mode="aspectFit"></image>
					<view class="pulse-ring"></view>
				</view>
				<text class="title">宠物伴侣</text>
				<text class="subtitle">连接未来，守护挚爱</text>
			</view>

			<!-- 登录表单 -->
			<view class="login-form-card">
				<view class="input-group">
					<view class="input-item">
						<view class="icon-wrapper">
							<text class="iconfont icon-user"></text>
						</view>
						<input v-model="loginForm.username" class="input-field" type="text" placeholder="请输入账号" maxlength="30" confirm-type="next" />
					</view>
					<view class="input-item">
						<view class="icon-wrapper">
							<text class="iconfont icon-password"></text>
						</view>
						<input v-model="loginForm.password" type="password" class="input-field" placeholder="请输入密码" maxlength="20" confirm-type="next" @confirm="handleLogin" />
					</view>
					<view class="input-item captcha-box" v-if="captchaEnabled">
						<view class="icon-wrapper">
							<text class="iconfont icon-code"></text>
						</view>
						<input v-model="loginForm.code" type="number" class="input-field captcha-input" placeholder="请输入验证码" maxlength="4" confirm-type="done" @confirm="handleLogin" />
						<image :src="codeUrl" @click="getCode" class="captcha-img"></image>
					</view>
				</view>

				<view class="button-group">
					<button @click="handleLogin" class="login-btn primary">
						<text class="btn-text">登 录</text>
						<view class="btn-shine"></view>
					</button>
					<button @click="wxHandleLogin" class="login-btn wechat">
						<text class="iconfont icon-weixin"></text>
						<text class="btn-text">微信授权登录</text>
					</button>
				</view>
				<view class="register-entry" v-if="register">
					<text>没有账号？</text>
					<text @click="handleUserRegister" class="link">立即注册</text>
				</view>
			</view>

			<!-- 底部协议 -->
			<view class="footer-agreement">
				<view class="xieyi agree-line" @click="toggleAgree">
					<view class="agree-box" :class="agreeChecked ? 'checked' : ''">
						<text v-if="agreeChecked" class="agree-tick">✓</text>
					</view>
					<text>我已阅读并同意</text>
					<text @click="handleUserAgrement" class="link">《用户协议》</text>
					<text>与</text>
					<text @click="handlePrivacy" class="link">《隐私协议》</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { getCodeImg } from '@/api/login';
import { getToken } from '@/utils/auth';

export default {
	data() {
		return {
			codeUrl: '',
			captchaEnabled: true,
			register: true,
			agreeChecked: false,
			globalConfig: getApp().globalData.config,
			loginForm: {
				username: '',
				password: '',
				code: '',
				uuid: ''
			},
			wxLoginForm: {
				code: '',
				encryptedIv: '',
				encryptedData: ''
			}
		};
	},
	created() {
		this.getCode();
	},
	onLoad() {
		//#ifdef H5
		if (getToken()) {
			this.$tab.reLaunch('/pages/index');
		}
		//#endif
	},
	methods: {
		toggleAgree() {
			this.agreeChecked = !this.agreeChecked;
		},
		ensureAgreementAccepted() {
			if (this.agreeChecked) return true;
			this.$modal.msgError('请先同意用户协议和隐私协议');
			return false;
		},
		handleUserRegister() {
			this.$tab.redirectTo(`/pages/register`);
		},
		handlePrivacy() {
			let site = this.globalConfig.appInfo.agreements[0];
			this.$tab.navigateTo(`/pages/common/webview/index?title=${site.title}&url=${site.url}`);
		},
		handleUserAgrement() {
			let site = this.globalConfig.appInfo.agreements[1];
			this.$tab.navigateTo(`/pages/common/webview/index?title=${site.title}&url=${site.url}`);
		},
		getCode() {
			console.log('开始获取验证码...');
			
			getCodeImg().then((res) => {
				console.log('验证码接口返回数据:', res);
				
				// 处理响应数据
				const captchaData = res.data || res;
				console.log('处理后的验证码数据:', captchaData);
				
				this.captchaEnabled = captchaData.captchaEnabled === undefined ? true : captchaData.captchaEnabled;
				
				if (this.captchaEnabled) {
					console.log('验证码已启用');
					console.log('验证码UUID:', captchaData.uuid);
					console.log('验证码图片数据长度:', captchaData.img ? captchaData.img.length : 0);
					
					// 确保base64数据正确
					if (captchaData.img && captchaData.img.length > 0 && captchaData.uuid) {
						this.codeUrl = 'data:image/gif;base64,' + captchaData.img;
						this.loginForm.uuid = captchaData.uuid;
						console.log('✓ 验证码设置成功');
					} else {
						console.error('✗ 验证码图片数据或UUID为空:', { 
							img: captchaData.img ? '有数据' : '无数据',
							uuid: captchaData.uuid 
						});
						this.$modal.msgError('验证码加载失败，请检查后端服务');
					}
				} else {
					console.log('验证码功能已禁用');
				}
			}).catch((error) => {
				console.error('获取验证码失败:', error);
				this.$modal.msgError('验证码加载失败: ' + (error.message || '未知错误'));
			});
		},
		async wxHandleLogin() {
			if (!this.ensureAgreementAccepted()) return;

			console.log('微信小程序发起授权登录');
			this.$modal.loading('登录中，请耐心等待...');
			uni.getProvider({
				service: 'oauth',
				success: (res) => {
					console.log(res);
					if (~res.provider.indexOf('weixin')) {
						uni.login({
							provider: 'weixin',
							success: (loginRes) => {
								console.log('登录', loginRes);
								this.wxLoginForm.code = loginRes.code;
								uni.getUserInfo({
									success: (resInfo) => {
										console.log('用户信息', resInfo);
										this.wxLoginForm.encryptedIv = resInfo.iv;
										this.wxLoginForm.encryptedData = resInfo.encryptedData;
										this.sendWxLoginFormToLocalService();
									}
								});
							}
						});
					}
				}
			});
		},
		sendWxLoginFormToLocalService() {
			console.log('向后端发送登录请求....');
			this.$store
				.dispatch('WxLogin', this.wxLoginForm)
				.then(() => {
					this.$modal.closeLoading();
					this.loginSuccess();
				})
				.catch(() => {
					this.$modal.msgError('微信登录失败');
				});
		},
		async handleLogin() {
			console.log('登录表单数据:', this.loginForm);
			if (!this.ensureAgreementAccepted()) return;

			if (this.loginForm.username === '') {
				this.$modal.msgError('请输入账号');
			} else if (this.loginForm.password === '') {
				this.$modal.msgError('请输入密码');
			} else if (this.loginForm.code === '' && this.captchaEnabled) {
				this.$modal.msgError('请输入验证码');
			} else if (this.loginForm.uuid === '' && this.captchaEnabled) {
				this.$modal.msgError('验证码已过期，请重新获取');
			} else {
				this.$modal.loading('登录中，请耐心等待...');
				this.pwdLogin();
			}
		},
		async pwdLogin() {
			this.$store
				.dispatch('Login', this.loginForm)
				.then(() => {
					this.$modal.closeLoading();
					this.$modal.msgSuccess('登录成功');
					// 延迟跳转到首页，让用户看到登录成功的提示
					setTimeout(() => {
						this.loginSuccess();
					}, 1000);
				})
				.catch((error) => {
					this.$modal.closeLoading();
					let errorMessage = error.message || '登录失败，请检查账号密码和验证码';
					// 根据错误信息显示不同的弹窗
					if (errorMessage.includes('用户名') || errorMessage.includes('密码')) {
						this.$modal.msgError('用户名或密码错误');
					} else if (errorMessage.includes('验证码')) {
						this.$modal.msgError('验证码错误');
						if (this.captchaEnabled) {
							this.getCode();
						}
					} else {
						this.$modal.msgError(errorMessage);
						if (this.captchaEnabled) {
							this.getCode();
						}
					}
				});
		},
		loginSuccess(result) {
			console.log('登录成功，开始获取用户信息');
			this.$store.dispatch('GetInfo').then((res) => {
				console.log('获取用户信息成功，跳转首页');
				this.$tab.reLaunch('/pages/index');
			}).catch((error) => {
				console.error('获取用户信息失败:', error);
				this.$modal.msgError('获取用户信息失败');
			});
		}
	}
};
</script>

<style lang="scss" scoped>
page {
	height: 100%;
	overflow: hidden;
}

.tech-pet-login-container {
	position: relative;
	width: 100%;
	height: 100vh;
	display: flex;
	justify-content: center;
	align-items: center;
	overflow: hidden;
}

/* 动态星空背景 */
.starfield-bg {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: linear-gradient(to bottom, #0f0c29, #302b63, #24243e);
	z-index: 0;
	.stars {
		position: absolute;
		width: 100%;
		height: 100%;
		background-image: radial-gradient(2px 2px at 20px 30px, #eee, transparent), radial-gradient(2px 2px at 40px 70px, rgba(255, 255, 255, 0.8), transparent),
			radial-gradient(1px 1px at 50px 50px, #fff, transparent), radial-gradient(1px 1px at 80px 10px, #fff, transparent),
			radial-gradient(2px 2px at 130px 80px, rgba(255, 255, 255, 0.6), transparent);
		background-repeat: repeat;
		background-size: 200px 200px;
		animation: zoom 10s infinite;
		opacity: 0.5;
	}
	.grid-lines {
		position: absolute;
		width: 100%;
		height: 100%;
		background-image: linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px), linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
		background-size: 50px 50px;
	}
}

@keyframes zoom {
	0% {
		transform: scale(1);
	}
	50% {
		transform: scale(1.05);
	}
	100% {
		transform: scale(1);
	}
}

/* 主要内容 */
.main-content {
	width: 85%;
	z-index: 10;
	display: flex;
	flex-direction: column;
	align-items: center;
}

/* Logo和标题 */
.header-section {
	text-align: center;
	margin-bottom: 50rpx;
	animation: fadeInDown 1s ease-out;
	.logo-wrapper {
		position: relative;
		display: inline-block;
		.logo-image {
			width: 140rpx;
			height: 140rpx;
			border-radius: 50%;
			border: 3px solid rgba(255, 255, 255, 0.5);
			box-shadow: 0 0 20px rgba(102, 126, 234, 0.5);
			z-index: 2;
			position: relative;
		}
		.pulse-ring {
			position: absolute;
			top: 50%;
			left: 50%;
			transform: translate(-50%, -50%);
			width: 160rpx;
			height: 160rpx;
			border: 2px solid rgba(102, 126, 234, 0.6);
			border-radius: 50%;
			animation: pulse-ring 2s cubic-bezier(0.455, 0.03, 0.515, 0.955) infinite;
		}
	}
	.title {
		display: block;
		font-size: 52rpx;
		font-weight: bold;
		color: #ffffff;
		margin-top: 20rpx;
		letter-spacing: 3rpx;
		text-shadow: 0 0 10px rgba(102, 126, 234, 0.7);
	}
	.subtitle {
		display: block;
		font-size: 26rpx;
		color: rgba(255, 255, 255, 0.7);
		margin-top: 10rpx;
		letter-spacing: 1rpx;
	}
}

@keyframes pulse-ring {
	0% {
		transform: translate(-50%, -50%) scale(0.9);
		opacity: 1;
	}
	100% {
		transform: translate(-50%, -50%) scale(1.3);
		opacity: 0;
	}
}

/* 登录表单卡片 */
.login-form-card {
	width: 100%;
	background: rgba(255, 255, 255, 0.08);
	backdrop-filter: blur(20px);
	border-radius: 30rpx;
	padding: 50rpx 40rpx;
	border: 1px solid rgba(255, 255, 255, 0.1);
	box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.37);
	animation: fadeInUp 1s ease-out;
	.input-group {
		margin-bottom: 40rpx;
		.input-item {
			display: flex;
			align-items: center;
			background: rgba(255, 255, 255, 0.05);
			border-radius: 16rpx;
			padding: 10rpx 20rpx;
			margin-bottom: 25rpx;
			border: 1px solid rgba(255, 255, 255, 0.1);
			transition: all 0.3s ease;
			&:focus-within {
				background: rgba(255, 255, 255, 0.1);
				border-color: #667eea;
				box-shadow: 0 0 15px rgba(102, 126, 234, 0.3);
			}
			.icon-wrapper {
				margin-right: 20rpx;
				color: rgba(255, 255, 255, 0.6);
				font-size: 36rpx;
			}
			.input-field {
				flex: 1;
				font-size: 30rpx;
				color: #ffffff;
				height: 80rpx;
				line-height: 80rpx;
			}
			.input-field::placeholder {
				color: rgba(255, 255, 255, 0.4);
			}
		}
		.captcha-box {
			.captcha-input {
				width: 60%;
			}
			.captcha-img {
				height: 70rpx;
				width: 180rpx;
				border-radius: 10rpx;
				margin-left: 20rpx;
			}
		}
	}
}

/* 按钮组 */
.button-group {
	.login-btn {
		width: 100%;
		height: 88rpx;
		border-radius: 16rpx;
		font-size: 32rpx;
		font-weight: bold;
		display: flex;
		justify-content: center;
		align-items: center;
		margin-bottom: 20rpx;
		border: none;
		position: relative;
		overflow: hidden;
		transition: all 0.3s ease;
		&:active {
			transform: scale(0.98);
		}
		.btn-text {
			position: relative;
			z-index: 2;
		}
	}
	.primary {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		color: #ffffff;
		box-shadow: 0 4px 15px 0 rgba(102, 126, 234, 0.4);
		.btn-shine {
			position: absolute;
			top: -50%;
			left: -50%;
			width: 200%;
			height: 200%;
			background: linear-gradient(to top right, rgba(255, 255, 255, 0) 0%, rgba(255, 255, 255, 0.3) 50%, rgba(255, 255, 255, 0) 100%);
			transform: rotate(30deg);
			animation: shine 3s infinite;
		}
	}
	.wechat {
		background: rgba(7, 193, 96, 0.8);
		color: #ffffff;
		box-shadow: 0 4px 15px 0 rgba(7, 193, 96, 0.3);
		.iconfont {
			font-size: 34rpx;
			margin-right: 10rpx;
		}
	}
}

.register-entry {
	width: 100%;
	text-align: center;
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.8);
	margin-top: 10rpx;
	.link {
		color: #667eea;
		font-weight: bold;
		margin-left: 4rpx;
		text-decoration: underline;
	}
}

@keyframes shine {
	0% {
		transform: translateX(-100%) translateY(-100%) rotate(30deg);
	}
	80% {
		transform: translateX(100%) translateY(100%) rotate(30deg);
	}
	100% {
		transform: translateX(100%) translateY(100%) rotate(30deg);
	}
}

/* 底部协议 */
.footer-agreement {
	text-align: center;
	margin-top: 20rpx;
	animation: fadeIn 1.5s ease-out;
	.xieyi {
		font-size: 24rpx;
		color: rgba(255, 255, 255, 0.6);
		margin-bottom: 10rpx;
		.link {
			color: #667eea;
			font-weight: bold;
			margin: 0 4rpx;
			text-decoration: underline;
		}
	}
}

.agree-line {
	display: flex;
	align-items: center;
	justify-content: center;
	flex-wrap: nowrap;
}

.agree-box {
	width: 28rpx;
	height: 28rpx;
	border-radius: 8rpx;
	border: 2rpx solid rgba(255, 255, 255, 0.35);
	background: rgba(255, 255, 255, 0.06);
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 8rpx;
}

.agree-box.checked {
	border-color: rgba(102, 126, 234, 0.9);
	background: rgba(102, 126, 234, 0.25);
}

.agree-tick {
	color: #ffffff;
	font-size: 20rpx;
	font-weight: bold;
	line-height: 1;
}

/* 动画 */
@keyframes fadeInDown {
	from {
		opacity: 0;
		transform: translateY(-40rpx);
	}
	to {
		opacity: 1;
		transform: translateY(0);
	}
}

@keyframes fadeInUp {
	from {
		opacity: 0;
		transform: translateY(40rpx);
	}
	to {
		opacity: 1;
		transform: translateY(0);
	}
}

@keyframes fadeIn {
	from {
		opacity: 0;
	}
	to {
		opacity: 1;
	}
}
</style>