<template>
	<view class="apply-container">
		<!-- 头部导航栏 -->
		<view class="header">
			<text class="header-title">领养申请</text>
		</view>

		<!-- 宠物基本信息 -->
		<view class="pet-info-section" v-if="petInfo.petId">
			<image class="pet-avatar" :src="getImageUrl(petInfo.images)" mode="aspectFill" @error="handleImageError"></image>
			<view class="pet-details">
				<text class="pet-name">{{ petInfo.name || '待领养宠物' }}</text>
				<view class="pet-tags">
					<text v-if="petInfo.species !== undefined" class="pet-tag">
						{{ getSpeciesText(petInfo.species) }}
					</text>
					<text v-if="petInfo.breed" class="pet-tag">{{ petInfo.breed }}</text>
					<text v-if="petInfo.age !== undefined" class="pet-tag">{{ petInfo.age }}{{ getAgeUnit(petInfo.age) }}</text>
				</view>
			</view>
		</view>
		<!-- 表单区域 -->
		<scroll-view class="form-scroll" scroll-y>
			<view class="form-container">
				<!-- 申请信息标题 -->
				<view class="section-title">
					<view class="title-line"></view>
					<text class="title-text">申请信息</text>
					<view class="title-line"></view>
				</view>

				<!-- 申请表单 -->
				<view class="form-card">
					<!-- 申请人姓名 -->
					<view class="form-item">
						<view class="form-label">
							<text class="label-text">申请人姓名</text>
							<text class="required">*</text>
						</view>
						<input v-model="formData.application_name" class="form-input" placeholder="请输入您的姓名" placeholder-class="placeholder" :maxlength="20" />
						<view class="form-hint">请填写真实姓名</view>
					</view>

					<!-- 联系电话 -->
					<view class="form-item">
						<view class="form-label">
							<text class="label-text">联系电话</text>
							<text class="required">*</text>
						</view>
						<input
							v-model="formData.application_phone"
							class="form-input"
							placeholder="请输入11位手机号码"
							placeholder-class="placeholder"
							type="number"
							maxlength="11" />
						<view class="form-hint">用于联系您确认申请</view>
					</view>

					<!-- 养宠经验 -->
					<view class="form-item">
						<view class="form-label">
							<text class="label-text">养宠经验</text>
							<text class="required">*</text>
						</view>
						<textarea
							v-model="formData.experience"
							class="form-textarea"
							placeholder="请描述您的养宠经验，如：是否有养宠物经验、照顾宠物的经历等"
							placeholder-class="placeholder"
							:maxlength="500"
							auto-height />
						<view class="form-hint">剩余 {{ 500 - (formData.experience.length || 0) }} 字</view>
					</view>

					<!-- 领养理由 -->
					<view class="form-item">
						<view class="form-label">
							<text class="label-text">领养理由</text>
							<text class="required">*</text>
						</view>
						<textarea
							v-model="formData.reason"
							class="form-textarea"
							placeholder="请说明您为什么想要领养这只宠物，以及您能提供的照顾条件"
							placeholder-class="placeholder"
							:maxlength="500"
							auto-height />
						<view class="form-hint">剩余 {{ 500 - (formData.reason.length || 0) }} 字</view>
					</view>
				</view>
			</view>

			<!-- 协议条款 -->
			<view class="agreement-section">
				<label class="agreement-item" @click="agreementChecked = !agreementChecked">
					<checkbox :checked="agreementChecked" color="#ff9a9e" />
					<text class="agreement-text">我已阅读并同意</text>
					<text class="agreement-link" @click.stop="toAgreement">《领养协议》</text>
				</label>
			</view>

			<!-- 提交按钮 -->
			<view class="submit-section">
				<button class="submit-btn" @click="handleSubmit">
					<text class="submit-text">提交申请</text>
					<text class="submit-icon">✈</text>
				</button>
				<view class="submit-tips">
					<text>提交后工作人员会在1-3个工作日内与您联系</text>
				</view>
			</view>
		</scroll-view>
		<!-- 加载提示 -->
		<uni-popup ref="popup" type="center">
			<view class="loading-popup">
				<view class="loading-content">
					<uni-load-more status="loading" :content-text="{ contentdown: '加载中' }" />
					<text class="loading-text">正在提交申请，请稍候...</text>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script>
import { getPet, applyAdoption } from '@/api/pet/adoption.js';
import config from '@/config';

export default {
	data() {
		return {
			// 宠物信息
			petInfo: {},

			// 表单数据（包含你提供的字段）
			formData: {
				adoption_id: '', // 自动生成
				pet_id: '', // 从URL参数获取
				user_id: '', // 从用户信息获取
				application_name: '',
				application_phone: '',
				experience: '',
				reason: ''
			},

			// 协议同意状态
			agreementChecked: false
		};
	},

	computed: {
		// 验证表单是否可以提交
		canSubmit() {
			const requiredFields = [this.formData.application_name, this.formData.application_phone, this.formData.experience, this.formData.reason];

			// 检查必填字段
			const isRequiredFilled = requiredFields.every((field) => field && field.trim().length > 0);

			// 手机号格式验证
			const phoneRegex = /^1[3-9]\d{9}$/;
			const isPhoneValid = phoneRegex.test(this.formData.application_phone);

			return isRequiredFilled && isPhoneValid && this.agreementChecked;
		},

		// 表单验证状态
		validationStatus() {
			return {
				name: this.formData.application_name?.length > 0,
				phone: /^1[3-9]\d{9}$/.test(this.formData.application_phone),
				experience: this.formData.experience?.length >= 20,
				reason: this.formData.reason?.length >= 20
			};
		},

		// 获取宠物主图片路径（简化模板逻辑）
		getPetMainImage() {
			if (this.petInfo && this.petInfo.images) {
				if (Array.isArray(this.petInfo.images)) {
					return this.petInfo.images[0] || '';
				} else {
					return this.petInfo.images;
				}
			} else if (this.petInfo && this.petInfo.imageUrl) {
				return this.petInfo.imageUrl;
			}
			return '';
		}
	},

	onLoad(options) {
		// 从URL获取宠物ID，支持petId和id两种参数名
		const petId = options.petId || options.id;
		if (petId) {
			this.formData.pet_id = petId;
			this.loadPetInfo(petId);
		}

		// 设置用户ID（从缓存或全局状态获取）
		this.getUserInfo();
	},

	methods: {
		// 加载宠物信息
		async loadPetInfo(petId) {
			try {
				uni.showLoading({
					title: '加载中...',
					mask: true
				});

				const res = await getPet(petId);
				if (res.code === 200 && res.data) {
					this.petInfo = res.data;
				} else {
					throw new Error('获取宠物信息失败');
				}
			} catch (error) {
				console.error('加载宠物信息失败:', error);
				uni.showToast({
					title: '加载失败',
					icon: 'none'
				});
			} finally {
				uni.hideLoading();
			}
		},

		// 获取用户信息
		getUserInfo() {
			// 这里应该从全局状态或缓存获取用户信息
			try {
				const userInfo = uni.getStorageSync('userInfo');
				if (userInfo) {
					this.formData.user_id = userInfo.userId || '';
					this.formData.application_name = userInfo.nickName || '';
					this.formData.application_phone = userInfo.phone || '';
				}
			} catch (error) {
				console.error('获取用户信息失败:', error);
			}
		},

		// 获取图片URL
		getImageUrl(fileName) {
			return config.getDownloadUrl(fileName);
		},

		// 获取物种文本
		getSpeciesText(species) {
			const speciesMap = {
				0: '猫',
				1: '狗',
				2: '其他'
			};
			return speciesMap[species] || '未知';
		},

		// 获取年龄单位
		getAgeUnit(age) {
			const ageNum = Number(age);
			return ageNum >= 12 ? '岁' : '个月';
		},

		// 环境选择变化
		onEnvironmentChange(e) {
			const index = e.detail.value;
			this.environmentIndex = index;
			this.formData.environment = this.environmentOptions[index];
		},

		// 家庭成员同意变化
		onFamilyAgreeChange(e) {
			this.formData.family_agree = e.detail.value;
		},

		// 查看协议
		toAgreement() {
			uni.navigateTo({
				url: '/pages/agreement/index'
			});
		},

		// 验证表单
		validateForm() {
			const errors = [];

			if (!this.formData.application_name?.trim()) {
				errors.push('请填写申请人姓名');
			}

			if (!this.formData.application_phone?.trim()) {
				errors.push('请填写联系电话');
			} else if (!/^1[3-9]\d{9}$/.test(this.formData.application_phone)) {
				errors.push('请输入正确的手机号码');
			}

			if (!this.formData.experience?.trim()) {
				errors.push('请填写养宠经验');
			} else if (this.formData.experience.trim().length < 20) {
				errors.push('养宠经验至少20字');
			}

			if (!this.formData.reason?.trim()) {
				errors.push('请填写领养理由');
			} else if (this.formData.reason.trim().length < 20) {
				errors.push('领养理由至少20字');
			}

			if (!this.agreementChecked) {
				errors.push('请同意领养协议');
			}

			return errors;
		},

		// 提交申请
		async handleSubmit() {
			// 表单验证
			const errors = this.validateForm();
			if (errors.length > 0) {
				uni.showToast({
					title: errors[0],
					icon: 'none',
					duration: 3000
				});
				return;
			}

			// 确认提交
			uni.showModal({
				title: '确认提交',
				content: '确认提交领养申请吗？提交后工作人员会尽快联系您。',
				success: async (res) => {
					if (res.confirm) {
						await this.submitApplication();
					}
				}
			});
		},

		// 提交申请到服务器
		async submitApplication() {
			try {
				// 显示加载提示
				this.$refs.popup.open();

				// 准备提交数据
				const submitData = {
					...this.formData,
					status: 0, // 0-待审核
					petId: this.petInfo.petId
				};

				// 调用API提交申请
				const res = await applyAdoption(submitData);

				if (res.code === 200) {
					// 提交成功
					this.$refs.popup.close();

					uni.showModal({
						title: '提交成功',
						content: '您的领养申请已提交成功！工作人员会在1-3个工作日内联系您。',
						showCancel: false,
						confirmText: '确定',
						success: (res) => {
							if (res.confirm) {
								// 返回上一页
								uni.navigateBack();
							}
						}
					});
				} else {
					throw new Error(res.msg || '提交失败');
				}
			} catch (error) {
				console.error('提交申请失败:', error);
				this.$refs.popup.close();

				uni.showToast({
					title: error.message || '提交失败，请稍后重试',
					icon: 'none',
					duration: 3000
				});
			}
		},

		// 表单输入校验
		validatePhone(phone) {
			return /^1[3-9]\d{9}$/.test(phone);
		},

		// 字数统计
		getWordCount(text) {
			return text ? text.length : 0;
		},

		// 图片加载失败处理
		handleImageError(e) {
			console.error('图片加载失败:', e);
			e.currentTarget.src = '/static/images/default-pet.png';
			e.currentTarget.onerror = null;
		}
	}
};
</script>

<style lang="scss" scoped>
.apply-container {
	min-height: 100vh;
	background: linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%);
}

/* 头部导航栏 */
.header {
	display: flex;
	align-items: center;
	padding: 30rpx;
	background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
	position: relative;
	z-index: 10;
}

.nav-back {
	width: 60rpx;
	height: 60rpx;
	border-radius: 50%;
	background: rgba(255, 255, 255, 0.9);
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 20rpx;
}

.nav-back i {
	font-size: 32rpx;
	color: #ff9a9e;
}

.header-title {
	font-size: 36rpx;
	font-weight: bold;
	color: #ffffff;
	text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
	flex: 1;
	text-align: center;
	margin-right: 60rpx;
	/* 为返回按钮留出空间 */
}

/* 宠物信息 */
.pet-info-section {
	background: linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%);
	margin: 20rpx 30rpx;
	padding: 30rpx;
	border-radius: 20rpx;
	display: flex;
	align-items: center;
	box-shadow: 0 8rpx 25rpx rgba(161, 196, 253, 0.3);
}

.pet-avatar {
	width: 120rpx;
	height: 120rpx;
	border-radius: 20rpx;
	margin-right: 25rpx;
	border: 4rpx solid rgba(255, 255, 255, 0.8);
	background-color: #fff;
}

.pet-details {
	flex: 1;
}

.pet-name {
	font-size: 36rpx;
	font-weight: bold;
	color: #ffffff;
	margin-bottom: 15rpx;
	display: block;
	text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.2);
}

.pet-tags {
	display: flex;
	flex-wrap: wrap;
	gap: 12rpx;
}

.pet-tag {
	font-size: 24rpx;
	color: #4a6fd8;
	background: rgba(255, 255, 255, 0.9);
	padding: 6rpx 18rpx;
	border-radius: 20rpx;
	backdrop-filter: blur(10rpx);
}

/* 表单容器 */
.form-scroll {
	height: calc(100vh - 300rpx);
}

.form-container {
	padding: 30rpx;
}

/* 区域标题 */
.section-title {
	display: flex;
	align-items: center;
	justify-content: center;
	margin: 40rpx 0 30rpx;
}

.title-line {
	flex: 1;
	height: 2rpx;
	background: linear-gradient(90deg, transparent, #ff9a9e, transparent);
}

.title-text {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin: 0 20rpx;
	white-space: nowrap;
}

/* 表单卡片 */
.form-card {
	background: #ffffff;
	border-radius: 20rpx;
	padding: 40rpx 30rpx;
	box-shadow: 0 8rpx 30rpx rgba(255, 154, 158, 0.1);
	margin-bottom: 20rpx;
}

/* 表单项目 */
.form-item {
	margin-bottom: 40rpx;
	position: relative;
}

.form-item:last-child {
	margin-bottom: 0;
}

.form-label {
	display: flex;
	align-items: center;
	margin-bottom: 15rpx;
}

.label-text {
	font-size: 30rpx;
	font-weight: 600;
	color: #333;
	margin-right: 10rpx;
}

.required {
	color: #ff4757;
	font-size: 28rpx;
}

/* 输入框 */
.form-input {
	height: 80rpx;
	background: #f8f9fa;
	border-radius: 12rpx;
	padding: 0 25rpx;
	font-size: 28rpx;
	color: #333;
	border: 2rpx solid #e9ecef;
	transition: all 0.3s ease;
}

.form-input:focus {
	border-color: #ff9a9e;
	background: #fff;
	box-shadow: 0 0 0 4rpx rgba(255, 154, 158, 0.1);
}

.placeholder {
	color: #adb5bd;
	font-size: 28rpx;
}

/* 文本域 */
.form-textarea {
	width: 100%;
	min-height: 150rpx;
	background: #f8f9fa;
	border-radius: 12rpx;
	padding: 25rpx;
	font-size: 28rpx;
	color: #333;
	border: 2rpx solid #e9ecef;
	line-height: 1.5;
	transition: all 0.3s ease;
}

.form-textarea:focus {
	border-color: #ff9a9e;
	background: #fff;
	box-shadow: 0 0 0 4rpx rgba(255, 154, 158, 0.1);
}

/* 表单提示 */
.form-hint {
	font-size: 24rpx;
	color: #6c757d;
	margin-top: 10rpx;
	text-align: right;
}

/* 选择器 */
.form-picker {
	margin-top: 10rpx;
}

.picker-view {
	height: 80rpx;
	background: #f8f9fa;
	border-radius: 12rpx;
	border: 2rpx solid #e9ecef;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 0 25rpx;
	transition: all 0.3s ease;
}

.picker-view:active {
	background: #fff;
	border-color: #ff9a9e;
}

.picker-text {
	font-size: 28rpx;
	color: #333;
	flex: 1;
}

.picker-text.placeholder {
	color: #adb5bd;
}

.picker-arrow {
	font-size: 24rpx;
	color: #6c757d;
	margin-left: 10rpx;
}

/* 单选按钮组 */
.radio-group {
	display: flex;
	flex-wrap: wrap;
	gap: 30rpx;
	margin-top: 15rpx;
}

.radio-item {
	display: flex;
	align-items: center;
}

.radio-text {
	font-size: 28rpx;
	color: #333;
	margin-left: 10rpx;
}

/* 补充信息区域 */
.additional-section {
	margin-top: 50rpx;
	padding-top: 30rpx;
	border-top: 2rpx dashed #e9ecef;
}

/* 协议条款 */
.agreement-section {
	margin: 30rpx 0;
	padding: 20rpx 0;
	display: flex;
	justify-content: center;
}

.agreement-item {
	display: flex;
	align-items: center;
}

.agreement-text {
	font-size: 26rpx;
	color: #666;
	margin: 0 10rpx;
}

.agreement-link {
	font-size: 26rpx;
	color: #4a6fd8;
	text-decoration: underline;
}

/* 提交按钮 */
.submit-section {
	margin: 40rpx 0 60rpx;
}

.submit-btn {
	background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
	height: 90rpx;
	border-radius: 45rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	color: #fff;
	font-size: 32rpx;
	font-weight: 600;
	border: none;
	box-shadow: 0 8rpx 20rpx rgba(255, 154, 158, 0.3);
	transition: all 0.3s ease;
}

.submit-btn:active {
	transform: translateY(2rpx);
	box-shadow: 0 4rpx 10rpx rgba(255, 154, 158, 0.3);
}

.submit-btn.disabled {
	background: linear-gradient(135deg, #ccc 0%, #ddd 100%);
	box-shadow: none;
	opacity: 0.6;
}

.submit-text {
	margin-right: 15rpx;
}

.submit-icon {
	font-size: 28rpx;
}

.submit-tips {
	text-align: center;
	margin-top: 20rpx;
	font-size: 24rpx;
	color: #6c757d;
}

/* 加载弹窗 */
.loading-popup {
	background: rgba(255, 255, 255, 0.95);
	border-radius: 20rpx;
	padding: 50rpx 40rpx;
	backdrop-filter: blur(20rpx);
	box-shadow: 0 20rpx 60rpx rgba(0, 0, 0, 0.1);
}

.loading-content {
	display: flex;
	flex-direction: column;
	align-items: center;
}

.loading-text {
	margin-top: 20rpx;
	font-size: 28rpx;
	color: #333;
}

/* 验证状态样式 */
.form-input.invalid,
.form-textarea.invalid {
	border-color: #ff4757;
	background: rgba(255, 71, 87, 0.05);
}

.form-input.valid,
.form-textarea.valid {
	border-color: #4caf50;
	background: rgba(76, 175, 80, 0.05);
}

.validation-hint {
	font-size: 24rpx;
	margin-top: 8rpx;
	display: flex;
	align-items: center;
}

.validation-hint.invalid {
	color: #ff4757;
}

.validation-hint.valid {
	color: #4caf50;
}

.validation-hint i {
	font-size: 20rpx;
	margin-right: 5rpx;
}
</style>
