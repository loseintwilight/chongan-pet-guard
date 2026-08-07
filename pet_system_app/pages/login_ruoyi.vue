<template>
	<view class="normal-login-container">
		<view class="logo-content align-center justify-center flex">
			<image style="width: 100rpx; height: 100rpx" :src="globalConfig.appInfo.logo" mode="widthFix"></image>
			<text class="title">若依移动端登录</text>
		</view>
		<view class="login-form-content">
			<view class="input-item flex align-center">
				<view class="iconfont icon-user icon"></view>
				<input v-model="loginForm.username" class="input" type="text" placeholder="请输入账号" maxlength="30" />
			</view>
			<view class="input-item flex align-center">
				<view class="iconfont icon-password icon"></view>
				<input v-model="loginForm.password" type="password" class="input" placeholder="请输入密码" maxlength="20" />
			</view>
			<view class="input-item flex align-center" style="width: 60%; margin: 0px" v-if="captchaEnabled">
				<view class="iconfont icon-code icon"></view>
				<input v-model="loginForm.code" type="number" class="input" placeholder="请输入验证码" maxlength="4" />
				<view class="login-code">
					<image :src="codeUrl" @click="getCode" class="login-code-img"></image>
				</view>
			</view>
			<view class="action-btn">
				<button @click="handleLogin" class="login-btn cu-btn block bg-blue lg round">登录</button>
				<button @click="wxHandleLogin" class="login-btn cu-btn block bg-green lg round">微信授权登录</button>
			</view>

			<view class="reg text-center" v-if="register">
				<text class="text-grey1">没有账号？</text>
				<text @click="handleUserRegister" class="text-blue">立即注册</text>
			</view>
			<view class="xieyi text-center">
				<text class="text-grey1">登录即代表同意</text>
				<text @click="handleUserAgrement" class="text-blue">《用户协议》</text>
				<text @click="handlePrivacy" class="text-blue">《隐私协议》</text>
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
			// 用户注册开关
			register: false,
			globalConfig: getApp().globalData.config,
			loginForm: {
				username: 'admin',
				password: 'admin123',
				code: '',
				uuid: ''
			},
			//微信登录
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
		// 用户注册
		handleUserRegister() {
			this.$tab.redirectTo(`/pages/register`);
		},
		// 隐私协议
		handlePrivacy() {
			let site = this.globalConfig.appInfo.agreements[0];
			this.$tab.navigateTo(`/pages/common/webview/index?title=${site.title}&url=${site.url}`);
		},
		// 用户协议
		handleUserAgrement() {
			let site = this.globalConfig.appInfo.agreements[1];
			this.$tab.navigateTo(`/pages/common/webview/index?title=${site.title}&url=${site.url}`);
		},
		// 获取图形验证码
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
					if (captchaData.img && captchaData.uuid) {
						this.codeUrl = 'data:image/gif;base64,' + captchaData.img;
						this.loginForm.uuid = captchaData.uuid;
						console.log('✓ 验证码设置成功');
					} else {
						console.error('✗ 验证码数据不完整');
					}
				} else {
					console.log('验证码功能已禁用');
				}
			}).catch((error) => {
				console.error('获取验证码失败:', error);
			});
		},
		//微信授权登录
		async wxHandleLogin() {
			console.log('微信小程序发起授权登录');
			this.$modal.loading('登录中，请耐心等待...');
			//获取服务商信息
			uni.getProvider({
				service: 'oauth',
				success: (res) => {
					console.log(res);
					if (~res.provider.indexOf('weixin')) {
						//登录
						uni.login({
							provider: 'weixin',
							success: (loginRes) => {
								console.log('登录', loginRes);
								this.wxLoginForm.code = loginRes.code;
								//获取用户信息
								uni.getUserInfo({
									success: (resInfo) => {
										console.log('用户信息', resInfo);
										//设置偏移量和加密数据
										this.wxLoginForm.encryptedIv = resInfo.iv;
										this.wxLoginForm.encryptedData = resInfo.encryptedData;
										//向后端发送登录请求 TODO
										this.sendWxLoginFormToLocalService();
									}
								});
							}
						});
					}
				}
			});
		},
		//向后端发送登录请求
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
		// 登录方法
		async handleLogin() {
			if (this.loginForm.username === '') {
				this.$modal.msgError('请输入账号');
			} else if (this.loginForm.password === '') {
				this.$modal.msgError('请输入密码');
			} else if (this.loginForm.code === '' && this.captchaEnabled) {
				this.$modal.msgError('请输入验证码');
			} else {
				this.$modal.loading('登录中，请耐心等待...');
				this.pwdLogin();
			}
		},
		// 密码登录
		async pwdLogin() {
			this.$store
				.dispatch('Login', this.loginForm)
				.then(() => {
					this.$modal.closeLoading();
					this.loginSuccess();
				})
				.catch(() => {
					if (this.captchaEnabled) {
						this.getCode();
					}
				});
		},
		// 登录成功后，处理函数
		loginSuccess(result) {
			// 设置用户信息
			this.$store.dispatch('GetInfo').then((res) => {
				this.$tab.reLaunch('/pages/index');
			});
		}
	}
};
</script>

<style lang="scss" scoped>
page {
	background-color: #ffffff;
}

.normal-login-container {
	width: 100%;

	.logo-content {
		width: 100%;
		font-size: 21px;
		text-align: center;
		padding-top: 15%;

		image {
			border-radius: 4px;
		}

		.title {
			margin-left: 10px;
		}
	}

	.login-form-content {
		text-align: center;
		margin: 20px auto;
		margin-top: 15%;
		width: 80%;

		.input-item {
			margin: 20px auto;
			background-color: #f5f6f7;
			height: 45px;
			border-radius: 20px;

			.icon {
				font-size: 38rpx;
				margin-left: 10px;
				color: #999;
			}

			.input {
				width: 100%;
				font-size: 14px;
				line-height: 20px;
				text-align: left;
				padding-left: 15px;
			}
		}

		.login-btn {
			margin-top: 40px;
			height: 45px;
		}

		.reg {
			margin-top: 15px;
		}

		.xieyi {
			color: #333;
			margin-top: 20px;
		}

		.login-code {
			height: 38px;
			float: right;

			.login-code-img {
				height: 38px;
				position: absolute;
				margin-left: 10px;
				width: 200rpx;
			}
		}
	}
}
</style>
