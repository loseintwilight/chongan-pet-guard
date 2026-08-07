<template>
	<view class="rescue-container">
		<!-- 头部导航栏 -->
		<view class="header">
			<text class="header-title">宠物救助登记</text>
		</view>

		<!-- 表单区域 -->
		<scroll-view class="form-scroll" scroll-y>
			<view class="form-container">
				<!-- 救助人信息 -->
				<view class="section-title">
					<view class="title-line"></view>
					<text class="title-text">救助人信息</text>
					<view class="title-line"></view>
				</view>

				<view class="form-card">
					<!-- 上报人姓名 -->
					<view class="form-item">
						<view class="form-label">
							<text class="label-text">上报人姓名</text>
							<text class="required">*</text>
						</view>
						<input v-model="formData.reporterName" class="form-input" placeholder="请输入您的姓名" placeholder-class="placeholder" :maxlength="20" />
						<view class="form-hint">请填写真实姓名</view>
					</view>

					<!-- 联系电话 -->
					<view class="form-item">
						<view class="form-label">
							<text class="label-text">联系电话</text>
							<text class="required">*</text>
						</view>
						<input
							v-model="formData.reporterPhone"
							class="form-input"
							placeholder="请输入11位手机号码"
							placeholder-class="placeholder"
							type="number"
							maxlength="11" />
						<view class="form-hint">用于联系您确认救助信息</view>
					</view>
				</view>

				<!-- 宠物基本信息 -->
				<view class="section-title">
					<view class="title-line"></view>
					<text class="title-text">宠物基本信息</text>
					<view class="title-line"></view>
				</view>

				<view class="form-card">
					<!-- 宠物描述 -->
					<view class="form-item">
						<view class="form-label">
							<text class="label-text">宠物描述</text>
							<text class="required">*</text>
						</view>
						<textarea
							v-model="formData.petDesc"
							class="form-textarea"
							placeholder="请描述宠物的品种、年龄、毛色、体型、健康状况等信息"
							placeholder-class="placeholder"
							:maxlength="255"
							auto-height />
						<view class="form-hint">剩余 {{ 255 - (formData.petDesc.length || 0) }} 字</view>
					</view>

					<!-- 发现地点 -->
					<view class="form-item">
						<view class="form-label">
							<text class="label-text">发现地点</text>
							<text class="required">*</text>
						</view>
						<input
							v-model="formData.location"
							class="form-input"
							placeholder="请输入发现地点"
							placeholder-class="placeholder"
							:maxlength="100" />
						<view class="form-hint">请尽可能详细描述发现地点，便于工作人员快速定位</view>
					</view>
				</view>

				<!-- 现场照片 -->
				<view class="section-title">
					<view class="title-line"></view>
					<text class="title-text">现场照片</text>
					<view class="title-line"></view>
				</view>

				<view class="form-card">
					<view class="form-item">
						<view class="form-label">
							<text class="label-text">上传照片</text>
							<text class="required">*</text>
						</view>
						<view class="photo-container">
							<!-- 已上传照片 -->
							<view class="photo-item" v-for="(item, index) in imagesList" :key="index">
								<image class="photo" :src="item.url" mode="aspectFill" @click="previewImage(index)"></image>
								<view class="photo-delete" @click="deleteImage(index)">
									<uni-icons type="close" size="22" color="#fff"></uni-icons>
								</view>
							</view>
							<!-- 上传按钮 -->
							<view class="photo-upload" @click="chooseImage" v-if="imagesList.length < 9">
								<uni-icons type="plus" size="40" color="#999"></uni-icons>
								<text class="upload-text">上传照片</text>
								<text class="upload-hint">{{ imagesList.length }}/9</text>
							</view>
						</view>
						<view class="form-hint">最多上传9张照片，请拍摄清晰的宠物和周围环境照片</view>
					</view>
				</view>

				<!-- 救助情况描述 -->
				<view class="section-title">
					<view class="title-line"></view>
					<text class="title-text">救助情况描述</text>
					<view class="title-line"></view>
				</view>

				<view class="form-card">
					<view class="form-item">
						<textarea
							v-model="formData.situation"
							class="form-textarea"
							placeholder="请描述救助情况，如发现时的状态、是否受伤、是否需要紧急救助等"
							placeholder-class="placeholder"
							:maxlength="500"
							auto-height />
						<view class="form-hint">剩余 {{ 500 - (formData.situation.length || 0) }} 字</view>
					</view>
				</view>
			</view>

			<!-- 提交按钮 -->
			<view class="submit-section">
				<button class="submit-btn" @click="handleSubmit">
					<text class="submit-text">提交救助登记</text>
					<text class="submit-icon">✈</text>
				</button>
				<view class="submit-tips">
					<text>提交后工作人员会尽快处理您的救助请求</text>
				</view>
			</view>
		</scroll-view>
		<!-- 加载提示 -->
		<uni-popup ref="popup" type="center">
			<view class="loading-popup">
				<view class="loading-content">
					<uni-load-more status="loading" :content-text="{ contentdown: '加载中' }" />
					<text class="loading-text">正在提交救助信息，请稍候...</text>
				</view>
			</view>
		</uni-popup>
	</view>
</template>

<script>
import { addRescue } from '@/api/pet/rescue.js';
import upload from '@/utils/upload';
import config from '@/config';
import { getToken } from '@/utils/auth';

export default {
	data() {
		return {
			// 表单数据
			formData: {
				userId: '', // 登记用户id
				reporterName: '', // 上报人姓名
				reporterPhone: '', // 上报人电话
				petDesc: '', // 宠物描述
				location: '', // 发现地点
				images: '', // 现场图片
				situation: '' // 救助情况（额外字段，可放入petDesc）
			},

			// 图片列表
			imagesList: [],
			imageUrls: []
		};
	},

	computed: {
		// 验证表单是否可以提交
		canSubmit() {
			const requiredFields = [this.formData.reporterName, this.formData.reporterPhone, this.formData.petDesc, this.formData.location];

			// 检查必填字段
			const isRequiredFilled = requiredFields.every((field) => field && field.trim().length > 0);

			// 手机号格式验证
			const phoneRegex = /^1[3-9]\d{9}$/;
			const isPhoneValid = phoneRegex.test(this.formData.reporterPhone);

			return isRequiredFilled && isPhoneValid;
		}
	},

	onLoad() {
		// 获取用户信息
		this.getUserInfo();
	},

	methods: {
		// 获取用户信息
		getUserInfo() {
			try {
				const userInfo = uni.getStorageSync('userInfo');
				if (userInfo) {
					this.formData.userId = userInfo.userId || '';
					this.formData.reporterName = userInfo.nickName || '';
					this.formData.reporterPhone = userInfo.phone || '';
				}
			} catch (error) {
				console.error('获取用户信息失败:', error);
			}
		},

		// 选择图片
		chooseImage() {
			uni.chooseImage({
				count: 9 - this.imagesList.length,
				sizeType: ['original', 'compressed'],
				sourceType: ['album', 'camera'],
				success: (res) => {
					this.uploadImages(res.tempFilePaths);
				},
				fail: (err) => {
					console.error('选择图片失败:', err);
				}
			});
		},

		// 上传图片
		async uploadImages(filePaths) {
			try {
				uni.showLoading({
					title: '上传图片中...',
					mask: true
				});

				const uploadPromises = filePaths.map(filePath => {
					return upload({
						url: '/common/upload',
						filePath: filePath,
						name: 'file',
						header: {
							Authorization: 'Bearer ' + getToken()
						}
					});
				});

				const results = await Promise.all(uploadPromises);
				
				// 处理上传结果
				results.forEach(result => {
					if (result.code === 200 && result.fileName) {
						this.imagesList.push({
							url: config.getDownloadUrl(result.fileName),
							fileName: result.fileName
						});
						this.imageUrls.push(result.fileName);
					}
				});

				uni.hideLoading();
			} catch (error) {
				console.error('上传图片失败:', error);
				uni.hideLoading();
				uni.showToast({
					title: '上传图片失败',
					icon: 'none'
				});
			}
		},

		// 预览图片
		previewImage(index) {
			const urls = this.imagesList.map(item => item.url);
			uni.previewImage({
				current: urls[index],
				urls: urls
			});
		},

		// 删除图片
		deleteImage(index) {
			uni.showModal({
				title: '确认删除',
				content: '确定要删除这张照片吗？',
				success: (res) => {
					if (res.confirm) {
						this.imagesList.splice(index, 1);
						this.imageUrls.splice(index, 1);
					}
				}
			});
		},

		// 验证表单
		validateForm() {
			const errors = [];

			if (!this.formData.reporterName?.trim()) {
				errors.push('请填写上报人姓名');
			}

			if (!this.formData.reporterPhone?.trim()) {
				errors.push('请填写联系电话');
			} else if (!/^1[3-9]\d{9}$/.test(this.formData.reporterPhone)) {
				errors.push('请输入正确的手机号码');
			}

			if (!this.formData.petDesc?.trim()) {
				errors.push('请填写宠物描述');
			} else if (this.formData.petDesc.trim().length < 10) {
				errors.push('宠物描述至少需要10个字符');
			}

			if (!this.formData.location?.trim()) {
				errors.push('请填写发现地点');
			}

			return errors;
		},

		// 提交救助信息
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
				content: '确认提交救助登记信息吗？提交后工作人员会尽快处理。',
				success: async (res) => {
					if (res.confirm) {
						await this.submitRescueInfo();
					}
				}
			});
		},

		// 提交救助信息到服务器
		async submitRescueInfo() {
			try {
				// 显示加载提示
				this.$refs.popup.open();

				// 准备提交数据
				const submitData = {
					...this.formData,
					images: this.imageUrls.join(','), // 将图片URLs转为逗号分隔字符串
					status: 0 // 0-待处理
				};

				// 调用API提交救助信息
				const res = await addRescue(submitData);

				if (res.code === 200) {
					// 提交成功
					this.$refs.popup.close();

					uni.showModal({
						title: '提交成功',
						content: '您的救助登记已提交成功！工作人员会尽快处理您的请求。',
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
				console.error('提交救助信息失败:', error);
				this.$refs.popup.close();

				uni.showToast({
					title: error.message || '提交失败，请稍后重试',
					icon: 'none',
					duration: 3000
				});
			}
		}
	}
};
</script>

<style lang="scss" scoped>
.rescue-container {
	min-height: 100vh;
	background: linear-gradient(180deg, #f8f9fa 0%, #ffffff 100%);
}

/* 头部导航栏 */
.header {
	display: flex;
	align-items: center;
	padding: 30rpx;
	background: linear-gradient(135deg, #4a6fd8 0%, #6b8de7 100%);
	position: relative;
	z-index: 10;
}

.header-title {
	font-size: 36rpx;
	font-weight: bold;
	color: #ffffff;
	text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
	flex: 1;
	text-align: center;
}

/* 表单容器 */
.form-scroll {
	height: calc(100vh - 160rpx);
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
	background: linear-gradient(90deg, transparent, #4a6fd8, transparent);
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
	box-shadow: 0 8rpx 30rpx rgba(74, 111, 216, 0.1);
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
	border-color: #4a6fd8;
	background: #fff;
	box-shadow: 0 0 0 4rpx rgba(74, 111, 216, 0.1);
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
	border-color: #4a6fd8;
	background: #fff;
	box-shadow: 0 0 0 4rpx rgba(74, 111, 216, 0.1);
}

/* 表单提示 */
.form-hint {
	font-size: 24rpx;
	color: #6c757d;
	margin-top: 10rpx;
	text-align: right;
}

/* 位置选择 */
.location-container {
	display: flex;
	align-items: center;
	gap: 15rpx;
}

.location-input {
	flex: 1;
}

.location-btn {
	height: 80rpx;
	background: #e3eafd;
	border: none;
	border-radius: 12rpx;
	padding: 0 20rpx;
	display: flex;
	align-items: center;
	gap: 8rpx;
	transition: all 0.3s ease;
}

.location-btn:active {
	background: #d4e0fd;
}

.location-btn-text {
	font-size: 26rpx;
	color: #4a6fd8;
}

/* 照片上传 */
.photo-container {
	display: flex;
	flex-wrap: wrap;
	gap: 20rpx;
	margin-top: 20rpx;
}

.photo-item {
	position: relative;
	width: 200rpx;
	height: 200rpx;
	border-radius: 12rpx;
	overflow: hidden;
	border: 2rpx solid #e9ecef;
}

.photo {
	width: 100%;
	height: 100%;
}

.photo-delete {
	position: absolute;
	top: 10rpx;
	right: 10rpx;
	width: 40rpx;
	height: 40rpx;
	background: rgba(0, 0, 0, 0.6);
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
}

.photo-upload {
	width: 200rpx;
	height: 200rpx;
	background: #f8f9fa;
	border-radius: 12rpx;
	border: 2rpx dashed #adb5bd;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	gap: 10rpx;
	transition: all 0.3s ease;
}

.photo-upload:active {
	background: #e9ecef;
}

.upload-text {
	font-size: 26rpx;
	color: #6c757d;
}

.upload-hint {
	font-size: 22rpx;
	color: #adb5bd;
}

/* 提交按钮 */
.submit-section {
	margin: 40rpx 0 60rpx;
}

.submit-btn {
	background: linear-gradient(135deg, #4a6fd8 0%, #6b8de7 100%);
	height: 90rpx;
	border-radius: 45rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	color: #fff;
	font-size: 32rpx;
	font-weight: 600;
	border: none;
	box-shadow: 0 8rpx 20rpx rgba(74, 111, 216, 0.3);
	transition: all 0.3s ease;
}

.submit-btn:active {
	transform: translateY(2rpx);
	box-shadow: 0 4rpx 10rpx rgba(74, 111, 216, 0.3);
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
</style>