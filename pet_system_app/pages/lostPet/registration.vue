<template>
	<view class="container">
		<!-- 顶部导航栏 -->
		<view class="header">
			<view class="header-title">丢失登记</view>
		</view>

		<!-- 表单内容 -->
		<view class="form-container">
			<!-- 宠物照片上传 -->
			<view class="form-item">
				<!-- 宠物图片上传 -->
				<uni-forms-item name="images" label="宠物照片">
					<view class="custom-image-picker">
						<!-- 上传按钮 -->
						<view class="upload-btn" @click="chooseImage" v-if="imageList.length < 1">
							<text class="upload-icon">+</text>
							<text class="upload-text">上传宠物照片</text>
						</view>

						<!-- 图片预览区域 -->
						<view class="image-preview" v-if="imageList.length > 0">
							<view class="image-item" v-for="(image, index) in imageList" :key="index">
								<image :src="image" mode="aspectFill" class="preview-image"></image>
								<view class="delete-btn" @click.stop="deleteImage(index)">×</view>
							</view>
						</view>
					</view>
				</uni-forms-item>
			</view>

			<!-- 宠物种类 -->
			<view class="form-item">
				<view class="form-label">
					2.宠物种类
					<text class="required">*</text>
				</view>
				<picker class="picker" :range="petTypes" @change="onSpeciesChange">
					<view class="picker-text">{{ selectedSpecies || '请选择品种' }}</view>
					<view class="picker-icon">▼</view>
				</picker>
			</view>

			<!-- 品种 -->
			<view class="form-item">
				<view class="form-label">
					品种
					<text class="required">*</text>
				</view>
				<input type="text" class="input" placeholder="请输入品种" v-model="formData.breed" />
			</view>

			<!-- 宠物名字 -->
			<view class="form-item">
				<view class="form-label">
					3.宠物名字
					<text class="required">*</text>
				</view>
				<input type="text" class="input" placeholder="请输入宠物名字" v-model="formData.petName" />
			</view>

			<view class="form-item">
				<view class="form-label">
					4.宠物性别
					<text class="required">*</text>
				</view>
				<view class="radio-group">
					<label class="radio-item" @click="setGender('公')">
						<view class="radio-container">
							<view class="radio-icon" :class="{ 'radio-selected': formData.gender === '公' }">
								<text v-if="formData.gender === '公'" class="radio-inner">●</text>
							</view>
							<text class="radio-text">公</text>
						</view>
					</label>
					<label class="radio-item" @click="setGender('母')">
						<view class="radio-container">
							<view class="radio-icon" :class="{ 'radio-selected': formData.gender === '母' }">
								<text v-if="formData.gender === '母'" class="radio-inner">●</text>
							</view>
							<text class="radio-text">母</text>
						</view>
					</label>
				</view>
			</view>

			<!-- 宠物年龄 -->
			<view class="form-item">
				<view class="form-label">
					5.宠物年龄
					<text class="required">*</text>
				</view>
				<view class="age-select">
					<picker class="age-picker" @change="onAgeChange" mode="selector" :range="ageRange">
						<view class="picker-text">{{ formData.age || '请选择年龄' }}</view>
						<view class="picker-icon">▼</view>
					</picker>
					<text class="age-unit">月</text>
				</view>
			</view>

			<!-- 特征描述 -->
			<view class="form-item">
				<view class="form-label">
					6.特征描述
					<text class="required">*</text>
				</view>
				<textarea class="textarea" placeholder="请输入宠物的特征描述（颜色、体型、特殊标记等）" v-model="formData.features" maxlength="200" />
				<view class="word-count">{{ formData.features.length }}/200</view>
			</view>

			<!-- 想说的话 -->
			<view class="form-item">
				<view class="form-label">
					7.想说的话
					<text class="required">*</text>
				</view>
				<textarea class="textarea" placeholder="请输入想说的话" v-model="formData.message" maxlength="500" />
				<view class="word-count">{{ formData.message.length }}/500</view>
			</view>

			<!-- 悬赏金额 -->
			<view class="form-item">
				<view class="form-label">8.悬赏金额</view>
				<view class="reward-input">
					<text class="currency">¥</text>
					<input type="number" class="input" placeholder="0" v-model="formData.reward" @input="validateReward" />
				</view>
				<text class="hint-text">可不填，0元表示无悬赏</text>
			</view>

			<!-- 主人姓名 -->
			<view class="form-item">
				<view class="form-label">
					9.主人姓名
					<text class="required">*</text>
				</view>
				<input type="text" class="input" placeholder="请输入主人姓名" v-model="formData.ownerName" />
			</view>

			<!-- 主人电话 -->
			<view class="form-item">
				<view class="form-label">
					10.主人电话
					<text class="required">*</text>
				</view>
				<input type="number" class="input" placeholder="请输入11位手机号码" v-model="formData.ownerPhone" maxlength="11" />
			</view>

			<!-- 丢失地点 -->
			<view class="form-item">
				<view class="form-label">
					11.丢失地点
					<text class="required">*</text>
				</view>
				<input type="text" class="input" placeholder="请输入详细丢失地点" v-model="formData.lostLocation" />
			</view>

			<!-- 丢失时间 -->
			<view class="form-item">
				<view class="form-label">
					12.丢失时间
					<text class="required">*</text>
				</view>
				<picker class="picker" mode="date" :value="formData.lostTime" @change="onLostTimeChange">
					<view class="picker-text">{{ formData.lostTime || '请选择日期' }}</view>
					<view class="picker-icon">▼</view>
				</picker>
			</view>

			<!-- 提交按钮 -->
			<view class="submit-btn" @click="submitForm">提交登记</view>
		</view>
	</view>
</template>

<script>
import { addLostPet } from '@/api/pet/lostPet.js';
import config from '@/config';

export default {
	data() {
		return {
			// 表单数据
			formData: {
				petName: '',
				species: 0, // 0-猫 1-狗 2-其他
				gender: '公',
				age: 0,
				features: '',
				message: '',
				reward: 0,
				ownerName: '',
				ownerPhone: '',
				breed: '',
				lostLocation: '',
				lostTime: this.getCurrentDate(),
				status: 0
			},
			// 【保留】用于预览的图片URL列表
			imageList: [],
			// 【保留】用于提交的图片文件名列表
			uploadedImageNames: [],
			petTypes: ['猫', '狗', '其他'],
			selectedSpecies: '',
			ageRange: Array.from({ length: 240 }, (_, i) => `${i + 1}`)
			// 【移除】tempFilePaths 不再需要
			// tempFilePaths: [],
		};
	},

	onLoad() {
		// 设置默认丢失时间为今天
		this.formData.lostTime = this.getCurrentDate();
	},
	methods: {
		// 获取当前日期
		getCurrentDate() {
			const now = new Date();
			const year = now.getFullYear();
			const month = (now.getMonth() + 1).toString().padStart(2, '0');
			const day = now.getDate().toString().padStart(2, '0');
			return `${year}-${month}-${day}`;
		},

		// 【正确】选择图片 -> 立即上传
		chooseImage() {
			const maxCount = 5; // 丢失登记最多5张
			if (this.imageList.length >= maxCount) {
				uni.showToast({ title: `最多上传${maxCount}张照片`, icon: 'none' });
				return;
			}
			const count = maxCount - this.imageList.length;

			uni.chooseImage({
				count: count,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: async (res) => {
					const tempFilePaths = res.tempFilePaths;
					uni.showLoading({ title: '上传中...', mask: true });

					try {
						const uploadPromises = tempFilePaths.map((path) => this.uploadSingleImage(path));
						const results = await Promise.all(uploadPromises);

						results.forEach((result) => {
							this.imageList.push(result.url);
							this.uploadedImageNames.push(result.name);
						});

						uni.hideLoading();
						uni.showToast({ title: '上传成功', icon: 'success' });
					} catch (error) {
						uni.hideLoading();
						uni.showToast({ title: '图片上传失败', icon: 'none' });
						console.error('图片上传失败:', error);
					}
				}
			});
		},

		// 【正确】上传单张图片
		async uploadSingleImage(filePath) {
			return new Promise((resolve, reject) => {
				uni.uploadFile({
					url: config.baseUrl + '/common/upload',
					filePath: filePath,
					name: 'file',
					success: (res) => {
						try {
							const data = JSON.parse(res.data);
							if (data.code === 200) {
								// 【关键】根据您之前的后端接口，文件名在 data.url
								const fileName = data.url;
								const fullUrl = config.getDownloadUrl(fileName);
								resolve({ url: fullUrl, name: fileName });
							} else {
								reject(new Error(data.msg || '上传失败'));
							}
						} catch (e) {
							reject(e);
						}
					},
					fail: (err) => {
						reject(err);
					}
				});
			});
		},

		// 【正确】删除图片
		deleteImage(index) {
			this.imageList.splice(index, 1);
			this.uploadedImageNames.splice(index, 1);
		},

		// 【正确】提交表单
		async submitForm() {
			// 1. 验证图片是否已上传
			if (this.uploadedImageNames.length === 0) {
				uni.showToast({ title: '请上传宠物照片', icon: 'none' });
				return;
			}

			// 2. 表单验证
			if (!this.validateForm()) {
				return;
			}

			try {
				uni.showLoading({ title: '提交中...', mask: true });

				// 3. 准备提交数据
				const submitData = {
					...this.formData,
					// 【关键】使用已上传的图片文件名
					images: this.uploadedImageNames.join(',')
				};

				console.log('提交数据:', submitData);

				// 4. 调用API
				const res = await addLostPet(submitData);

				if (res.code === 200) {
					uni.hideLoading();
					uni.showToast({ title: '登记成功！', icon: 'success' });
					setTimeout(() => {
						uni.navigateBack();
					}, 1500);
				} else {
					uni.hideLoading();
					uni.showToast({ title: res.msg || '提交失败', icon: 'none' });
				}
			} catch (error) {
				uni.hideLoading();
				console.error('提交失败:', error);
				uni.showToast({ title: '提交失败，请检查网络', icon: 'none' });
			}
		},

		// 删除图片
		deleteImage(index) {
			this.imageList.splice(index, 1);
			this.tempFilePaths.splice(index, 1);
		},

		// 选择宠物种类
		onSpeciesChange(e) {
			const index = e.detail.value;
			this.selectedSpecies = this.petTypes[index];
			this.formData.species = parseInt(index); // 转换为数字类型
		},

		setGender(gender) {
			this.formData.gender = gender;
		},

		// 选择年龄
		onAgeChange(e) {
			this.formData.age = parseInt(this.ageRange[e.detail.value]);
		},

		// 选择丢失时间
		onLostTimeChange(e) {
			this.formData.lostTime = e.detail.value;
		},

		// 验证悬赏金额
		validateReward(e) {
			let value = parseInt(e.detail.value);
			if (isNaN(value) || value < 0) {
				this.formData.reward = 0;
			} else {
				this.formData.reward = value;
			}
		},

		// 表单验证
		validateForm() {
			if (this.imageList.length === 0) {
				uni.showToast({
					title: '请上传宠物照片',
					icon: 'none'
				});
				return false;
			}

			if (!this.selectedSpecies) {
				uni.showToast({
					title: '请选择宠物种类',
					icon: 'none'
				});
				return false;
			}

			if (!this.formData.breed.trim()) {
				uni.showToast({
					title: '请输入品种',
					icon: 'none'
				});
				return false;
			}

			if (!this.formData.petName.trim()) {
				uni.showToast({
					title: '请输入宠物名字',
					icon: 'none'
				});
				return false;
			}

			if (!this.formData.gender) {
				uni.showToast({
					title: '请选择宠物性别',
					icon: 'none'
				});
				return false;
			}

			if (!this.formData.age) {
				uni.showToast({
					title: '请选择宠物年龄',
					icon: 'none'
				});
				return false;
			}

			if (!this.formData.features.trim()) {
				uni.showToast({
					title: '请输入特征描述',
					icon: 'none'
				});
				return false;
			}

			if (!this.formData.message.trim()) {
				uni.showToast({
					title: '请输入想说的话',
					icon: 'none'
				});
				return false;
			}

			if (!this.formData.ownerName.trim()) {
				uni.showToast({
					title: '请输入主人姓名',
					icon: 'none'
				});
				return false;
			}

			if (!this.formData.ownerPhone.trim()) {
				uni.showToast({
					title: '请输入主人电话',
					icon: 'none'
				});
				return false;
			}

			// 验证手机号格式
			const phoneReg = /^1[3-9]\d{9}$/;
			if (!phoneReg.test(this.formData.ownerPhone)) {
				uni.showToast({
					title: '请输入有效的手机号码',
					icon: 'none'
				});
				return false;
			}

			if (!this.formData.lostLocation.trim()) {
				uni.showToast({
					title: '请输入丢失地点',
					icon: 'none'
				});
				return false;
			}

			if (!this.formData.lostTime) {
				uni.showToast({
					title: '请选择丢失时间',
					icon: 'none'
				});
				return false;
			}

			return true;
		},
		// 【新增】选择图片
		chooseImage() {
			const maxCount = 1; // 托养登记只允许上传1张
			uni.chooseImage({
				count: maxCount,
				sizeType: ['compressed'],
				sourceType: ['album', 'camera'],
				success: async (res) => {
					const tempFilePaths = res.tempFilePaths;
					uni.showLoading({ title: '上传中...', mask: true });

					try {
						// 使用 Promise.all 来并行上传所有选择的图片（虽然这里只有一张）
						const uploadPromises = tempFilePaths.map((path) => this.uploadSingleImage(path));
						const results = await Promise.all(uploadPromises);

						// 将上传成功后的图片URL和文件名添加到列表中
						results.forEach((result) => {
							this.imageList.push(result.url);
							this.uploadedImageNames.push(result.name);
						});

						uni.hideLoading();
						uni.showToast({ title: '上传成功', icon: 'success' });
					} catch (error) {
						uni.hideLoading();
						uni.showToast({ title: '图片上传失败', icon: 'none' });
						console.error('图片上传失败:', error);
					}
				}
			});
		},

		// 上传图片方法（需要根据实际API实现）
		// async uploadImages(filePaths) {
		// 	// TODO: 实现图片上传逻辑
		// 	// 例如：
		// 	// const uploadPromises = filePaths.map(file => {
		// 	//     return new Promise((resolve) => {
		// 	//         // 上传图片到服务器
		// 	//         resolve('uploaded_url')
		// 	//     })
		// 	// })
		// 	// return Promise.all(uploadPromises)
		// 	return filePaths;
		// },

		/**
		 * 批量上传图片
		 * @param {Array<string>} filePaths - 本地临时文件路径数组
		 * @returns {Promise<Array<string>>} - 返回一个Promise，解析为上传成功后的完整URL数组
		 */
		async uploadImages(filePaths) {
			// 如果没有图片需要上传，直接返回空数组
			if (!filePaths || filePaths.length === 0) {
				return [];
			}

			// 显示加载提示
			uni.showLoading({ title: '图片上传中...', mask: true });

			// 将每个文件路径转换为一个上传Promise
			const uploadPromises = filePaths.map((filePath) => {
				return new Promise((resolve, reject) => {
					uni.uploadFile({
						// 您的后端上传接口地址
						url: config.baseUrl + '/common/upload',
						// 要上传的文件路径
						filePath: filePath,
						// 后端接收的文件字段名
						name: 'file',
						success: (res) => {
							try {
								// 解析后端返回的JSON数据
								const data = JSON.parse(res.data);
								console.log(data);
								if (data.code === 200) {
									// 上传成功
									// 从返回数据中获取文件名
									const fileName = data.url;
									// 使用您已有的方法拼接出完整的可访问URL
									const fullUrl = config.getDownloadUrl(fileName);
									// Promise成功，返回完整URL
									resolve(fullUrl);
								} else {
									// 后端返回错误，Promise失败
									reject(new Error(data.msg || '上传失败'));
								}
							} catch (e) {
								// JSON解析失败，Promise失败
								reject(new Error('解析服务器响应失败'));
							}
						},
						fail: (err) => {
							// uni.uploadFile 自身调用失败（如网络问题），Promise失败
							console.error('uni.uploadFile 失败:', err);
							reject(err);
						}
					});
				});
			});

			try {
				// 等待所有图片上传完成
				// Promise.all 会等待所有Promise都resolve（成功）或其中有一个reject（失败）
				const uploadedUrls = await Promise.all(uploadPromises);

				// 所有图片都上传成功，隐藏加载提示并返回URL数组
				uni.hideLoading();
				return uploadedUrls;
			} catch (error) {
				// 如果有任何一张图片上传失败，都会进入这里
				uni.hideLoading();
				console.error('图片上传失败:', error);
				// 抛出错误，让调用方（如submitForm）能够捕获并处理
				throw error;
			}
		}
	}
};
</script>

<style scoped lang="scss">
.custom-image-picker {
	.upload-btn {
		width: 200rpx;
		height: 200rpx;
		border: 2rpx dashed #ddd;
		border-radius: 10rpx;
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		background-color: #fafafa;
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
	.image-preview {
		display: flex;
		flex-wrap: wrap;
		gap: 20rpx;
		.image-item {
			position: relative;
			width: 200rpx;
			height: 200rpx;
			.preview-image {
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
				background: rgba(0, 0, 0, 0.6);
				color: #fff;
				border-radius: 50%;
				display: flex;
				align-items: center;
				justify-content: center;
				font-size: 24rpx;
				font-weight: bold;
				z-index: 10;
			}
		}
	}
}

.container {
	min-height: 100vh;
	background-color: #f5f7fa;
}

.header {
	height: 88rpx;
	background: #ffffff;
	display: flex;
	align-items: center;
	justify-content: center;
	position: sticky;
	top: 0;
	z-index: 100;
	border-bottom: 1px solid #eeeeee;
}

.header-title {
	font-size: 36rpx;
	font-weight: 600;
	color: #333333;
}

.form-container {
	padding: 30rpx;
}

.form-item {
	margin-bottom: 40rpx;
	background: #ffffff;
	border-radius: 16rpx;
	padding: 30rpx;
	box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.form-label {
	font-size: 32rpx;
	font-weight: 500;
	color: #333333;
	margin-bottom: 20rpx;
	display: flex;
	align-items: center;
}

.required {
	color: #fa5151;
	margin-left: 8rpx;
}

/* 上传区域样式 */
.upload-area {
	border: 2rpx dashed #1890ff;
	border-radius: 16rpx;
	padding: 60rpx 30rpx;
	text-align: center;
	background: rgba(24, 144, 255, 0.05);
	margin-top: 20rpx;
}

.upload-placeholder {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
}

.upload-icon {
	font-size: 80rpx;
	color: #1890ff;
	margin-bottom: 20rpx;
}

.upload-text {
	font-size: 32rpx;
	color: #1890ff;
	font-weight: 500;
}

.upload-hint {
	font-size: 24rpx;
	color: #999999;
	margin-top: 20rpx;
}

.image-preview {
	display: flex;
	flex-wrap: wrap;
	gap: 20rpx;
	margin-top: 30rpx;
}

.image-item {
	position: relative;
	width: 160rpx;
	height: 160rpx;
}

.preview-image {
	width: 100%;
	height: 100%;
	border-radius: 12rpx;
}

.delete-btn {
	position: absolute;
	top: -16rpx;
	right: -16rpx;
	width: 40rpx;
	height: 40rpx;
	background: #fa5151;
	color: #ffffff;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 28rpx;
	font-weight: bold;
	z-index: 10;
}

/* 选择器样式 */
.picker {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 24rpx;
	background: #f7f7f7;
	border-radius: 12rpx;
	margin-top: 20rpx;
}

.picker-text {
	font-size: 32rpx;
	color: #333333;
}

.picker-icon {
	font-size: 24rpx;
	color: #999999;
}

/* 输入框样式 */
.input {
	width: 100%;
	height: 88rpx;
	background: #f7f7f7;
	border-radius: 12rpx;
	padding: 0 24rpx;
	font-size: 32rpx;
	color: #333333;
	margin-top: 20rpx;
	box-sizing: border-box;
}

.radio-group {
	display: flex;
	gap: 60rpx;
	margin-top: 20rpx;
}

.radio-item {
	display: flex;
	align-items: center;
}

.radio-container {
	display: flex;
	align-items: center;
}

.radio-icon {
	width: 40rpx;
	height: 40rpx;
	border: 2rpx solid #cccccc;
	border-radius: 50%;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 16rpx;
}

.radio-selected {
	border-color: #1890ff;
}

.radio-inner {
	color: #1890ff;
	font-size: 24rpx;
}

.radio-text {
	font-size: 32rpx;
	color: #333333;
}

/* 年龄选择器样式 */
.age-select {
	display: flex;
	align-items: center;
	background: #f7f7f7;
	border-radius: 12rpx;
	margin-top: 20rpx;
}

.age-picker {
	flex: 1;
	padding: 24rpx;
}

.age-unit {
	padding: 0 24rpx;
	font-size: 32rpx;
	color: #666666;
}

/* 文本域样式 */
.textarea {
	width: 100%;
	min-height: 200rpx;
	background: #f7f7f7;
	border-radius: 12rpx;
	padding: 24rpx;
	font-size: 32rpx;
	color: #333333;
	margin-top: 20rpx;
	box-sizing: border-box;
}

.word-count {
	text-align: right;
	font-size: 24rpx;
	color: #999999;
	margin-top: 10rpx;
}

/* 悬赏金额输入样式 */
.reward-input {
	display: flex;
	align-items: center;
	background: #f7f7f7;
	border-radius: 12rpx;
	margin-top: 20rpx;
	padding: 0 24rpx;
}

.currency {
	font-size: 36rpx;
	color: #333333;
	font-weight: 500;
}

.reward-input .input {
	flex: 1;
	margin: 0;
	background: transparent;
	padding: 24rpx 0;
	font-size: 36rpx;
}

.hint-text {
	display: block;
	font-size: 24rpx;
	color: #999999;
	margin-top: 10rpx;
}

/* 提交按钮样式 */
.submit-btn {
	height: 96rpx;
	background: #1890ff;
	border-radius: 48rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 36rpx;
	color: #ffffff;
	font-weight: 600;
	margin-top: 60rpx;
}

.submit-btn:active {
	opacity: 0.8;
}
</style>
