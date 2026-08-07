<template>
	<view class="tech-pet-register-container">
		<view class="starfield-bg">
			<view class="stars"></view>
			<view class="grid-lines"></view>
		</view>

		<view class="main-content">
			<view class="header-section">
				<view class="logo-wrapper">
					<image class="logo-image" src="/static/pet-logo.jpeg" mode="aspectFit"></image>
					<view class="pulse-ring"></view>
				</view>
				<text class="title">账号注册</text>
				<text class="subtitle">创建账号，开启宠物服务</text>
			</view>

			<view class="register-form-card">
				<view class="input-group">
					<view class="input-item">
						<view class="iconfont icon-user icon"></view>
						<input v-model="registerForm.username" class="input-field" type="text" placeholder="请输入账号" maxlength="30" confirm-type="next" />
					</view>
					<view class="input-item">
						<view class="iconfont icon-phone icon"></view>
						<input v-model="registerForm.phonenumber" class="input-field" type="number" placeholder="请输入手机号" maxlength="11" confirm-type="next" />
					</view>
					<view class="input-item">
						<view class="iconfont icon-user icon"></view>
						<input v-model="registerForm.idCard" class="input-field" type="text" placeholder="请输入身份证号" maxlength="18" confirm-type="next" />
					</view>
					<view class="input-item">
						<view class="iconfont icon-password icon"></view>
						<input v-model="registerForm.password" type="password" class="input-field" placeholder="请输入密码" maxlength="20" confirm-type="next" />
					</view>
					<view class="input-item">
						<view class="iconfont icon-password icon"></view>
						<input v-model="registerForm.confirmPassword" type="password" class="input-field" placeholder="请输入确认密码" maxlength="20" confirm-type="next" />
					</view>
					<view class="input-item captcha-box" v-if="captchaEnabled">
						<view class="iconfont icon-code icon"></view>
						<input v-model="registerForm.code" type="number" class="input-field captcha-input" placeholder="请输入验证码" maxlength="4" confirm-type="done" @confirm="handleRegister" />
						<image :src="codeUrl" @click="getCode" class="captcha-img"></image>
					</view>
				</view>
				<button @click="handleRegister" class="register-btn">注 册</button>
			</view>

			<view class="footer-link">
				<text @click="handleUserLogin" class="link">使用已有账号登录</text>
			</view>
		</view>
	</view>
</template>

<script>
  import { getCodeImg, register } from '@/api/login'

  export default {
    data() {
      return {
        codeUrl: "",
        captchaEnabled: true,
        globalConfig: getApp().globalData.config,
        registerForm: {
          username: "",
          phonenumber: "",
          idCard: "",
          password: "",
          confirmPassword: "",
          code: "",
          uuid: ""
        }
      }
    },
    created() {
      this.getCode()
    },
    methods: {
      // 用户登录
      handleUserLogin() {
        this.$tab.navigateTo(`/pages/login`)
      },
      // 获取图形验证码
      getCode() {
        getCodeImg().then(res => {
          this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
          if (this.captchaEnabled) {
            this.codeUrl = 'data:image/gif;base64,' + res.img
            this.registerForm.uuid = res.uuid
          }
        })
      },
      // 注册方法
      async handleRegister() {
        if (this.registerForm.username === "") {
          this.$modal.msgError("请输入您的账号")
        } else if (this.registerForm.phonenumber === "") {
          this.$modal.msgError("请输入手机号")
        } else if (!/^1\d{10}$/.test(this.registerForm.phonenumber)) {
          this.$modal.msgError("请输入正确的手机号")
        } else if (this.registerForm.idCard === "") {
          this.$modal.msgError("请输入身份证号")
        } else if (!/^\d{17}[\dXx]$/.test(this.registerForm.idCard)) {
          this.$modal.msgError("请输入正确的身份证号")
        } else if (this.registerForm.password === "") {
          this.$modal.msgError("请输入您的密码")
        } else if (this.registerForm.confirmPassword === "") {
          this.$modal.msgError("请再次输入您的密码")
        } else if (this.registerForm.password !== this.registerForm.confirmPassword) {
          this.$modal.msgError("两次输入的密码不一致")
        } else if (this.registerForm.code === "" && this.captchaEnabled) {
          this.$modal.msgError("请输入验证码")
        } else {
          this.$modal.loading("注册中，请耐心等待...")
          this.register()
        }
      },
      // 用户注册
      async register() {
        register(this.registerForm).then(res => {
          this.$modal.closeLoading()
          uni.showModal({
          	title: "系统提示",
          	content: "恭喜你，您的账号 " + this.registerForm.username + " 注册成功！",
          	success: function (res) {
          		if (res.confirm) {
                uni.redirectTo({ url: `/pages/login` });
          		}
          	}
          })
        }).catch(() => {
          if (this.captchaEnabled) {
            this.getCode()
          }
        })
      }
    }
  }
</script>

<style lang="scss" scoped>
page {
	height: 100%;
	overflow: hidden;
}

.tech-pet-register-container {
	position: relative;
	width: 100%;
	height: 100vh;
	display: flex;
	justify-content: center;
	align-items: center;
	overflow: hidden;
}

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

.main-content {
	width: 85%;
	z-index: 10;
	display: flex;
	flex-direction: column;
	align-items: center;
}

.header-section {
	text-align: center;
	margin-bottom: 40rpx;
	.logo-wrapper {
		position: relative;
		display: inline-block;
		.logo-image {
			width: 140rpx;
			height: 140rpx;
			border-radius: 50%;
			border: 3px solid rgba(255, 255, 255, 0.5);
			box-shadow: 0 0 20px rgba(102, 126, 234, 0.5);
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
			animation: pulse-ring 2s ease infinite;
		}
	}
	.title {
		display: block;
		font-size: 48rpx;
		font-weight: bold;
		color: #fff;
		margin-top: 20rpx;
	}
	.subtitle {
		display: block;
		font-size: 24rpx;
		color: rgba(255, 255, 255, 0.7);
		margin-top: 8rpx;
	}
}

.register-form-card {
	width: 100%;
	background: rgba(255, 255, 255, 0.08);
	backdrop-filter: blur(20px);
	border-radius: 30rpx;
	padding: 40rpx 32rpx;
	border: 1px solid rgba(255, 255, 255, 0.1);
}

.input-group {
	.input-item {
		display: flex;
		align-items: center;
		background: rgba(255, 255, 255, 0.06);
		border-radius: 16rpx;
		padding: 10rpx 20rpx;
		margin-bottom: 18rpx;
		border: 1px solid rgba(255, 255, 255, 0.1);
	}
	.icon {
		font-size: 34rpx;
		color: rgba(255, 255, 255, 0.6);
		margin-right: 14rpx;
	}
	.input-field {
		flex: 1;
		height: 74rpx;
		line-height: 74rpx;
		color: #fff;
		font-size: 28rpx;
	}
	.input-field::placeholder {
		color: rgba(255, 255, 255, 0.4);
	}
}

.captcha-box {
	.captcha-input {
		width: 55%;
	}
	.captcha-img {
		height: 62rpx;
		width: 180rpx;
		border-radius: 10rpx;
		margin-left: 16rpx;
	}
}

.register-btn {
	width: 100%;
	height: 86rpx;
	border-radius: 16rpx;
	border: none;
	color: #fff;
	font-size: 32rpx;
	font-weight: bold;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.footer-link {
	margin-top: 24rpx;
	.link {
		color: #86a2ff;
		font-size: 24rpx;
		text-decoration: underline;
	}
}

@keyframes zoom {
	0% { transform: scale(1); }
	50% { transform: scale(1.05); }
	100% { transform: scale(1); }
}

@keyframes pulse-ring {
	0% { transform: translate(-50%, -50%) scale(0.9); opacity: 1; }
	100% { transform: translate(-50%, -50%) scale(1.3); opacity: 0; }
}
</style>
