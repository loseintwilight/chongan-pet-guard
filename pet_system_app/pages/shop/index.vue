<template>
	<view class="shop-application-container">
		<!-- 顶部标题栏 -->
		<view class="header-section">
			<view class="header-content">
				<view class="back-icon" @click="navigateBack">
					<view class="iconfont icon-back"></view>
				</view>
				<view class="title">店铺申请</view>
				<view class="header-placeholder"></view>
			</view>
		</view>

		<!-- 申请进度条 -->
		<view class="progress-section">
			<view class="progress-steps">
				<view class="step-item" :class="{ active: currentStep >= 1, completed: currentStep > 1 }">
					<view class="step-number">1</view>
					<view class="step-text">基本信息</view>
				</view>
				<view class="step-line" :class="{ active: currentStep > 1 }"></view>
				<view class="step-item" :class="{ active: currentStep >= 2, completed: currentStep > 2 }">
					<view class="step-number">2</view>
					<view class="step-text">资质证明</view>
				</view>
				<view class="step-line" :class="{ active: currentStep > 2 }"></view>
				<view class="step-item" :class="{ active: currentStep >= 3, completed: currentStep > 3 }">
					<view class="step-number">3</view>
					<view class="step-text">确认提交</view>
				</view>
			</view>
		</view>

		<!-- 表单内容区域 -->
		<view class="form-section">
			<!-- 第一步：基本信息 -->
			<view v-if="currentStep === 1" class="step-content">
				<view class="step-title">
					<view class="title-icon">📋</view>
					<view class="title-text">基本信息</view>
				</view>

				<uni-forms ref="basicForm" :modelValue="basicForm" :rules="basicRules">
					<uni-forms-item name="shopName" label="店铺名称" required>
						<uni-easyinput v-model="basicForm.shopName" placeholder="请输入店铺名称" maxlength="50" />
					</uni-forms-item>

					<uni-forms-item name="legalName" label="法人姓名" required>
						<uni-easyinput v-model="basicForm.legalName" placeholder="请输入法人姓名" />
					</uni-forms-item>

					<uni-forms-item name="legalPhone" label="联系电话" required>
						<uni-easyinput v-model="basicForm.legalPhone" type="number" placeholder="请输入联系电话" />
					</uni-forms-item>

					<uni-forms-item name="licenseNumber" label="营业执照号" required>
						<uni-easyinput v-model="basicForm.licenseNumber" placeholder="请输入营业执照号" />
					</uni-forms-item>

					<uni-forms-item name="legalNumber" label="身份证号">
						<uni-easyinput v-model="basicForm.legalNumber" placeholder="请输入法人身份证号" />
					</uni-forms-item>

					<uni-forms-item name="region" label="所在地区" required>
						<uni-data-picker v-model="basicForm.region" :localdata="regionData" placeholder="请选择省市区" popup-title="选择地区" @change="onRegionChange" />
					</uni-forms-item>

					<uni-forms-item name="address" label="详细地址" required>
						<uni-easyinput v-model="basicForm.address" type="textarea" placeholder="请输入详细地址" maxlength="200" />
					</uni-forms-item>

					<uni-forms-item name="businessHours" label="营业时间" required>
						<uni-easyinput v-model="basicForm.bussinessHours" placeholder="如：09:00-21:00" />
					</uni-forms-item>

					<uni-forms-item name="description" label="店铺简介">
						<uni-easyinput v-model="basicForm.description" type="textarea" placeholder="请简要描述您的店铺特色和服务" maxlength="500" />
					</uni-forms-item>
				</uni-forms>
			</view>

			<!-- 第二步：资质证明 -->
			<view v-if="currentStep === 2" class="step-content">
				<view class="step-title">
					<view class="title-icon">📄</view>
					<view class="title-text">资质证明</view>
				</view>

				<uni-forms ref="qualificationForm" :modelValue="qualificationForm">
					<!-- 营业执照 -->
					<uni-forms-item name="businessLicense" label="营业执照" required>
						<view class="upload-section">
							<view class="upload-grid">
								<view class="upload-item" v-for="(item, index) in businessLicenseList" :key="index">
									<image :src="getImageDisplayUrl(item)" mode="aspectFill" @click="previewImage(index, 'businessLicense')"></image>
									<view class="delete-btn" @click="deleteImage(index, 'businessLicense')">
										<view class="iconfont icon-close"></view>
									</view>
								</view>
								<view class="upload-btn" @click="chooseImage('businessLicense', 1)" v-if="businessLicenseList.length < 1">
									<view class="upload-icon">+</view>
									<view class="upload-text">上传营业执照</view>
								</view>
							</view>
							<view class="upload-tip">请上传清晰的营业执照照片</view>
						</view>
					</uni-forms-item>

					<uni-forms-item name="shopImages" label="店铺照片">
						<view class="upload-section">
							<view class="upload-grid">
								<view class="upload-item" v-for="(item, index) in shopImagesList" :key="index">
									<image :src="getImageDisplayUrl(item)" mode="aspectFill" @click="previewImage(index, 'shopImages')"></image>
									<view class="delete-btn" @click="deleteImage(index, 'shopImages')">
										<view class="iconfont icon-close"></view>
									</view>
								</view>
								<view class="upload-btn" @click="chooseImage('shopImages', 6)" v-if="shopImagesList.length < 6">
									<view class="upload-icon">+</view>
									<view class="upload-text">上传店铺照片</view>
								</view>
							</view>
							<view class="upload-tip">可上传店铺环境照片（选填）</view>
						</view>
					</uni-forms-item>
				</uni-forms>
			</view>

			<!-- 第三步：确认提交 -->
			<view v-if="currentStep === 3" class="step-content">
				<view class="step-title">
					<view class="title-icon">✅</view>
					<view class="title-text">确认信息</view>
				</view>

				<view class="confirm-section">
					<view class="confirm-card">
						<view class="card-title">基本信息</view>
						<view class="info-item">
							<view class="info-label">店铺名称：</view>
							<view class="info-value">{{ basicForm.shopName }}</view>
						</view>
						<view class="info-item">
							<view class="info-label">法人姓名：</view>
							<view class="info-value">{{ basicForm.legalName }}</view>
						</view>
						<view class="info-item">
							<view class="info-label">联系电话：</view>
							<view class="info-value">{{ basicForm.legalPhone }}</view>
						</view>
						<view class="info-item">
							<view class="info-label">营业执照号：</view>
							<view class="info-value">{{ basicForm.licenseNumber }}</view>
						</view>
						<view class="info-item">
							<view class="info-label">所在地区：</view>
							<view class="info-value">{{ formatRegion(basicForm.region) }}</view>
						</view>
						<view class="info-item">
							<view class="info-label">详细地址：</view>
							<view class="info-value">{{ basicForm.address }}</view>
						</view>
					</view>

					<view class="confirm-card">
						<view class="card-title">资质证明</view>
						<view class="info-item">
							<view class="info-label">营业执照：</view>
							<view class="info-value">已上传</view>
						</view>
						<view class="info-item">
							<view class="info-label">店铺照片：</view>
							<view class="info-value">{{ shopImagesList.length }} 张</view>
						</view>
					</view>

					<view class="agreement-section">
						<checkbox-group @change="onAgreementChange">
							<label class="checkbox-label">
								<checkbox :value="'agree'" :checked="agreed" color="#4facfe" />
								<view class="agreement-text">
									我已阅读并同意
									<text class="link" @click.stop="viewAgreement">《店铺入驻协议》</text>
									和
									<text class="link" @click.stop="viewPrivacy">《隐私政策》</text>
								</view>
							</label>
						</checkbox-group>
					</view>
				</view>
			</view>
		</view>

		<!-- 底部操作按钮 -->
		<view class="bottom-actions">
			<view class="action-buttons">
				<view v-if="currentStep > 1" class="btn-prev" @click="prevStep">上一步</view>
				<view v-if="currentStep < 3" class="btn-next" @click="nextStep">下一步</view>
				<view v-if="currentStep === 3" class="btn-submit" :class="{ disabled: !agreed }" @click="submitApplication">提交申请</view>
			</view>
		</view>
	</view>
</template>

<script>
import { getDicts } from '@/api/system/dict/data';
import { submitShopApplication } from '@/api/pet/shop.js';
import { getToken } from '@/utils/auth';
import config from '@/config';
import { regionData } from '@/api/pet/region.js';

export default {
	data() {
		return {
			currentStep: 1,
			agreed: false,
			// 导入地区数据
			regionData: regionData,
			basicForm: {
				shopName: '',
				legalName: '',
				legalPhone: '',
				licenseNumber: '',
				legalNumber: '',
				region: [],
				province: '',
				city: '',
				district: '',
				address: '',
				bussinessHours: '',
				description: ''
			},
			qualificationForm: {
				businessLicense: [],
				shopImages: []
			},
			// 图片列表 - 存储文件名或完整URL
			businessLicenseList: [],
			shopImagesList: [],
			basicRules: {
				shopName: {
					rules: [{ required: true, errorMessage: '请输入店铺名称' }]
				},
				legalName: {
					rules: [{ required: true, errorMessage: '请输入法人姓名' }]
				},
				legalPhone: {
					rules: [{ required: true, errorMessage: '请输入联系电话' }]
				},
				licenseNumber: {
					rules: [{ required: true, errorMessage: '请输入营业执照号' }]
				},
				region: {
					rules: [{ required: true, errorMessage: '请选择所在地区' }]
				},
				address: {
					rules: [{ required: true, errorMessage: '请输入详细地址' }]
				},
				businessHours: {
					rules: [{ required: true, errorMessage: '请输入营业时间' }]
				}
			},
			qualificationRules: {
				businessLicense: {
					rules: [{ required: true, errorMessage: '请上传营业执照' }]
				}
			},
			shopTypeOptions: []
		};
	},
	onLoad() {
		this.loadDictData();
	},
	methods: {
		navigateBack() {
			uni.navigateBack();
		},
		async loadDictData() {
			try {
				const response = await getDicts('shop_type');
				this.shopTypeOptions = response.data.map((item) => ({
					value: item.dictValue,
					text: item.dictLabel
				}));
			} catch (error) {
				console.error('加载字典数据失败:', error);
			}
		},
		// 地区选择变化事件
		onRegionChange(e) {
			console.log('地区选择原始数据:', e.detail.value);

			// 处理不同格式的返回数据
			let regionArray = [];

			if (Array.isArray(e.detail.value)) {
				// 如果是数组格式
				regionArray = e.detail.value.map((item) => {
					// 如果是对象，取 text 或 value
					if (typeof item === 'object' && item !== null) {
						return item.text || item.value || item.toString();
					}
					// 如果是字符串，直接返回
					return item.toString();
				});
			} else if (typeof e.detail.value === 'string') {
				// 如果是字符串，尝试解析
				regionArray = e.detail.value.split(',');
			} else {
				// 其他情况，转为字符串数组
				regionArray = [e.detail.value.toString()];
			}

			console.log('处理后的地区数据:', regionArray);

			// 更新表单数据
			this.basicForm.region = regionArray;

			// 自动填充省市区字段
			if (regionArray.length >= 3) {
				this.basicForm.province = regionArray[0];
				this.basicForm.city = regionArray[1];
				this.basicForm.district = regionArray[2];
			}
		},

		// 格式化地区显示
		formatRegion(regionArray) {
			if (!regionArray || regionArray.length === 0) {
				return '';
			}

			// 确保是字符串数组
			const regionStrings = regionArray.map((item) => {
				if (typeof item === 'object' && item !== null) {
					return item.text || item.value || item.toString();
				}
				return item.toString();
			});

			return regionStrings.join(' ');
		},

		// 获取图片显示URL
		getImageDisplayUrl(imageItem) {
			// 如果是临时路径（新上传的图片），直接返回
			if (typeof imageItem === 'string' && imageItem.startsWith('wxfile://')) {
				return imageItem;
			}

			// 如果是对象格式，取url字段
			if (typeof imageItem === 'object' && imageItem.url) {
				return this.getImageUrl(imageItem.url);
			}

			// 如果是字符串（文件名），构建下载URL
			if (typeof imageItem === 'string') {
				return this.getImageUrl(imageItem);
			}

			// 默认返回空字符串
			return '';
		},
		getImageUrl(fileName) {
			return config.getDownloadUrl(fileName);
		},
		async nextStep() {
			if (this.currentStep === 1) {
				try {
					await this.$refs.basicForm.validate();
					this.currentStep = 2;
				} catch (error) {
					console.log('基本信息验证失败:', error);
					uni.showToast({
						title: '请完善必填信息',
						icon: 'none'
					});
				}
			} else if (this.currentStep === 2) {
				try {
					await this.$refs.qualificationForm.validate();
					this.currentStep = 3;
				} catch (error) {
					console.log('资质证明验证失败:', error);
					uni.showToast({
						title: '请上传营业执照',
						icon: 'none'
					});
				}
			}
		},
		prevStep() {
			if (this.currentStep > 1) {
				this.currentStep--;
			}
		},
		async submitApplication() {
			console.log('提交前的agreed状态:', this.agreed);

			if (!this.agreed) {
				uni.showToast({
					title: '请先同意相关协议',
					icon: 'none'
				});
				return;
			}

			try {
				uni.showLoading({ title: '提交中...' });

				// 正确处理地区数据
				let province = '',
					city = '',
					district = '';

				if (this.basicForm.region && this.basicForm.region.length >= 3) {
					// 确保获取的是字符串值
					province = this.getRegionText(this.basicForm.region[0]);
					city = this.getRegionText(this.basicForm.region[1]);
					district = this.getRegionText(this.basicForm.region[2]);
				}

				// 准备提交数据
				const submitData = {
					...this.basicForm,
					province: province,
					city: city,
					district: district,
					// 获取营业执照文件名
					licenseImage: this.extractFileName(this.businessLicenseList[0]),
					// 获取店铺主图文件名
					mainImage: this.extractFileName(this.shopImagesList[0]),
					// 店铺照片文件名数组
					shopImages: this.shopImagesList.map((item) => this.extractFileName(item))
				};

				console.log('提交的数据:', submitData);

				const response = await submitShopApplication(submitData);

				if (response.code === 200) {
					uni.showToast({
						title: '提交成功',
						icon: 'success'
					});

					setTimeout(() => {
						uni.navigateBack();
					}, 1500);
				} else {
					uni.showToast({
						title: response.msg || '提交失败',
						icon: 'none'
					});
				}
			} catch (error) {
				console.error('提交失败:', error);
				uni.showToast({
					title: '提交失败: ' + error.message,
					icon: 'none'
				});
			} finally {
				uni.hideLoading();
			}
		},

		// 获取地区文本值
		getRegionText(item) {
			if (typeof item === 'object' && item !== null) {
				return item.text || item.value || item.toString();
			}
			return item.toString();
		},

		// 提取文件名
		extractFileName(imageItem) {
			if (typeof imageItem === 'string') {
				// 如果是完整URL，提取文件名
				if (imageItem.includes('fileName=')) {
					const urlParams = new URLSearchParams(imageItem.split('?')[1]);
					return urlParams.get('fileName') || imageItem;
				}
				// 如果是临时路径，返回完整路径（后端处理）
				if (imageItem.startsWith('wxfile://')) {
					return imageItem;
				}
				// 否则直接返回（认为是文件名）
				return imageItem;
			}

			if (typeof imageItem === 'object' && imageItem.url) {
				return this.extractFileName(imageItem.url);
			}

			return '';
		},
		// 选择图片
		chooseImage(type, count) {
			uni.chooseImage({
				count: count,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: (res) => {
					const tempFilePaths = res.tempFilePaths;
					this.uploadImages(tempFilePaths, type);
				}
			});
		},

		// 上传图片
		async uploadImages(filePaths, type) {
			try {
				uni.showLoading({
					title: '上传图片中...',
					mask: true
				});

				const uploadPromises = filePaths.map((filePath) => {
					return new Promise((resolve, reject) => {
						uni.uploadFile({
							url: config.baseUrl + '/common/upload',
							filePath: filePath,
							name: 'file',
							header: {
								Authorization: 'Bearer ' + getToken()
							},
							success: (res) => {
								try {
									console.log('上传响应:', res);
									const data = JSON.parse(res.data);
									console.log('解析数据:', data);

									if (data.code === 200) {
										// 获取文件名
										let fileName = data.url || data.fileName || data.data?.url || data.data?.fileName;

										if (fileName) {
											console.log('上传成功，文件名:', fileName);
											resolve(fileName);
										} else {
											console.error('无法获取文件名:', data);
											// 如果获取不到文件名，使用临时路径
											resolve(filePath);
										}
									} else {
										reject(new Error(data.msg || '上传失败'));
									}
								} catch (e) {
									console.error('解析响应失败:', e);
									reject(new Error('解析响应数据失败'));
								}
							},
							fail: (err) => {
								console.error('上传失败:', err);
								reject(err);
							}
						});
					});
				});

				const results = await Promise.all(uploadPromises);

				// 根据类型处理上传结果 - 存储文件名
				results.forEach((fileName) => {
					switch (type) {
						case 'businessLicense':
							this.businessLicenseList.push(fileName);
							break;
						case 'shopImages':
							this.shopImagesList.push(fileName);
							break;
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
		previewImage(index, type) {
			let imageList = [];
			switch (type) {
				case 'businessLicense':
					imageList = this.businessLicenseList;
					break;
				case 'shopImages':
					imageList = this.shopImagesList;
					break;
			}

			// 构建预览URL数组
			const urls = imageList.map((item) => this.getImageDisplayUrl(item));

			uni.previewImage({
				current: urls[index],
				urls: urls
			});
		},

		// 删除图片
		deleteImage(index, type) {
			uni.showModal({
				title: '确认删除',
				content: '确定要删除这张照片吗？',
				success: (res) => {
					if (res.confirm) {
						switch (type) {
							case 'businessLicense':
								this.businessLicenseList.splice(index, 1);
								break;
							case 'shopImages':
								this.shopImagesList.splice(index, 1);
								break;
						}
					}
				}
			});
		},
		onAgreementChange(e) {
			console.log('复选框变化:', e.detail.value);
			this.agreed = e.detail.value.includes('agree');
			console.log('agreed状态:', this.agreed);
		},
		viewAgreement() {
			uni.navigateTo({
				url: '/pages/agreement/shop'
			});
		},
		viewPrivacy() {
			uni.navigateTo({
				url: '/pages/agreement/privacy'
			});
		},
		getShopTypeText(type) {
			const option = this.shopTypeOptions.find((item) => item.value === type);
			return option ? option.text : '';
		}
	}
};
</script>

<style lang="scss" scoped>
/* 样式保持不变 */
.shop-application-container {
	min-height: 100vh;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	padding-bottom: 120rpx;

	.header-section {
		background: rgba(255, 255, 255, 0.1);
		backdrop-filter: blur(10px);
		padding: 20rpx 30rpx;

		.header-content {
			display: flex;
			align-items: center;
			justify-content: space-between;

			.back-icon {
				width: 60rpx;
				height: 60rpx;
				display: flex;
				justify-content: center;
				align-items: center;
				color: #ffffff;
			}

			.title {
				font-size: 36rpx;
				font-weight: bold;
				color: #ffffff;
			}

			.header-placeholder {
				width: 60rpx;
			}
		}
	}

	.progress-section {
		background: rgba(255, 255, 255, 0.95);
		margin: 20rpx 30rpx;
		border-radius: 20rpx;
		padding: 40rpx 30rpx;

		.progress-steps {
			display: flex;
			align-items: center;
			justify-content: space-between;

			.step-item {
				display: flex;
				flex-direction: column;
				align-items: center;
				flex: 1;

				.step-number {
					width: 60rpx;
					height: 60rpx;
					border-radius: 50%;
					background: #e9ecef;
					color: #6c757d;
					display: flex;
					justify-content: center;
					align-items: center;
					font-size: 24rpx;
					font-weight: bold;
					margin-bottom: 10rpx;
					transition: all 0.3s ease;
				}

				.step-text {
					font-size: 24rpx;
					color: #6c757d;
					transition: all 0.3s ease;
				}

				&.active {
					.step-number {
						background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
						color: #ffffff;
					}

					.step-text {
						color: #4facfe;
						font-weight: bold;
					}
				}

				&.completed {
					.step-number {
						background: #28a745;
						color: #ffffff;
					}

					.step-text {
						color: #28a745;
					}
				}
			}

			.step-line {
				flex: 1;
				height: 4rpx;
				background: #e9ecef;
				margin: 0 20rpx;
				transition: all 0.3s ease;

				&.active {
					background: #4facfe;
				}
			}
		}
	}

	.form-section {
		margin: 20rpx 30rpx;

		.step-content {
			background: rgba(255, 255, 255, 0.95);
			border-radius: 20rpx;
			padding: 30rpx;

			.step-title {
				display: flex;
				align-items: center;
				margin-bottom: 30rpx;

				.title-icon {
					font-size: 40rpx;
					margin-right: 15rpx;
				}

				.title-text {
					font-size: 32rpx;
					font-weight: bold;
					color: #2c3e50;
				}
			}

			.upload-section {
				.upload-grid {
					display: flex;
					flex-wrap: wrap;
					gap: 20rpx;
					margin-bottom: 10rpx;

					.upload-item {
						width: 200rpx;
						height: 200rpx;
						position: relative;
						border-radius: 10rpx;
						overflow: hidden;

						image {
							width: 100%;
							height: 100%;
							border-radius: 10rpx;
						}

						.delete-btn {
							position: absolute;
							top: -10rpx;
							right: -10rpx;
							width: 40rpx;
							height: 40rpx;
							background: #f56c6c;
							border-radius: 50%;
							display: flex;
							justify-content: center;
							align-items: center;
							color: #ffffff;
							font-size: 20rpx;
						}
					}

					.upload-btn {
						width: 200rpx;
						height: 200rpx;
						border: 2rpx dashed #ddd;
						border-radius: 10rpx;
						display: flex;
						flex-direction: column;
						justify-content: center;
						align-items: center;
						background: #f8f9fa;

						.upload-icon {
							font-size: 60rpx;
							color: #999;
							margin-bottom: 10rpx;
						}

						.upload-text {
							font-size: 24rpx;
							color: #666;
						}
					}
				}

				.upload-tip {
					font-size: 24rpx;
					color: #6c757d;
					margin-top: 10rpx;
				}
			}
		}

		.confirm-section {
			.confirm-card {
				background: #f8f9fa;
				border-radius: 15rpx;
				padding: 25rpx;
				margin-bottom: 20rpx;

				.card-title {
					font-size: 28rpx;
					font-weight: bold;
					color: #2c3e50;
					margin-bottom: 20rpx;
					padding-bottom: 10rpx;
					border-bottom: 2rpx solid #e9ecef;
				}

				.info-item {
					display: flex;
					margin-bottom: 15rpx;

					.info-label {
						width: 200rpx;
						font-size: 26rpx;
						color: #6c757d;
					}

					.info-value {
						flex: 1;
						font-size: 26rpx;
						color: #2c3e50;
					}
				}
			}

			.agreement-section {
				margin-top: 30rpx;

				.checkbox-label {
					display: flex;
					align-items: flex-start;
					gap: 15rpx;

					.agreement-text {
						flex: 1;
						font-size: 26rpx;
						color: #6c757d;
						line-height: 1.5;

						.link {
							color: #4facfe;
							text-decoration: underline;
						}
					}
				}
			}
		}
	}

	.bottom-actions {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		background: rgba(255, 255, 255, 0.95);
		backdrop-filter: blur(10px);
		padding: 20rpx 30rpx;
		box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);

		.action-buttons {
			display: flex;
			gap: 20rpx;

			.btn-prev,
			.btn-next,
			.btn-submit {
				flex: 1;
				height: 80rpx;
				border-radius: 40rpx;
				display: flex;
				justify-content: center;
				align-items: center;
				font-size: 28rpx;
				font-weight: bold;
				transition: all 0.3s ease;
			}

			.btn-prev {
				background: #f8f9fa;
				color: #6c757d;
				border: 2rpx solid #e9ecef;

				&:active {
					background: #e9ecef;
				}
			}

			.btn-next {
				background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
				color: #ffffff;

				&:active {
					background: linear-gradient(135deg, #3b82f6 0%, #0ea5e9 100%);
				}
			}

			.btn-submit {
				background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
				color: #ffffff;

				&.disabled {
					background: #e9ecef;
					color: #6c757d;
				}

				&:active:not(.disabled) {
					background: linear-gradient(135deg, #218838 0%, #1ea085 100%);
				}
			}
		}
	}
}
</style>
