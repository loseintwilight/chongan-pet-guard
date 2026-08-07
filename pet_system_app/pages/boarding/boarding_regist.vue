<template>
	<view class="pet-care-registration">
		<!-- 顶部标题栏 -->
		<view class="page-header">
			<view class="back-icon" @click="navigateBack">
				<view class="iconfont icon-back"></view>
			</view>
			<view class="header-title">宠物托养登记</view>
			<view class="header-actions"></view>
		</view>

		<!-- 表单容器 -->
		<view class="form-container">
			<uni-forms ref="form" :modelValue="formData" :rules="rules">
				<!-- 宠物图片上传 -->
				<uni-forms-item name="images" label="宠物照片">
					<uni-file-picker v-model="formData.images" file-mediatype="image" :limit="1" :deleteable="true" @select="onFileSelect" />
				</uni-forms-item>

				<uni-forms-item name="name" label="宠物名称">
					<uni-easyinput v-model="formData.name" placeholder="请输入宠物名称" />
				</uni-forms-item>

				<uni-forms-item name="species" label="物种">
					<uni-data-select v-model="formData.species" :localdata="speciesOptions" placeholder="请选择物种" />
				</uni-forms-item>

				<uni-forms-item name="breed" label="品种">
					<uni-easyinput v-model="formData.breed" placeholder="请输入品种" />
				</uni-forms-item>

				<uni-forms-item name="gender" label="性别">
					<uni-data-select v-model="formData.gender" :localdata="genderOptions" placeholder="请选择性别" />
				</uni-forms-item>

				<uni-forms-item name="age" label="年龄/月">
					<uni-number-box v-model="formData.age" :min="0" :max="30" :step="1" :disabled-input="false" />
				</uni-forms-item>

				<uni-forms-item name="healthInfo" label="健康状态">
					<uni-easyinput v-model="formData.healthInfo" placeholder="请描述宠物的健康状况" />
				</uni-forms-item>

				<uni-forms-item name="neuter" label="是否绝育">
					<uni-data-select v-model="formData.neuter" :localdata="neuteredOptions" placeholder="请选择是否绝育" />
				</uni-forms-item>

				<uni-forms-item name="immunity" label="是否免疫">
					<uni-data-select v-model="formData.immunity" :localdata="immunityOptions" placeholder="请选择是否免疫" />
				</uni-forms-item>

				<uni-forms-item name="insectRepellent" label="是否驱虫">
					<uni-data-select v-model="formData.insectRepellent" :localdata="insectRepellentOptions" placeholder="请选择是否驱虫" />
				</uni-forms-item>

				<uni-forms-item name="dewormingInterval" label="驱虫周期(天)">
					<uni-number-box v-model="formData.dewormingInterval" :min="0" :step="1" :disabled-input="false" />
				</uni-forms-item>

				<uni-forms-item name="insectRepellentLastTime" label="上回驱虫日期">
					<uni-datetime-picker v-model="formData.insectRepellentLastTime" type="date" placeholder="请选择日期" />
				</uni-forms-item>

				<uni-forms-item name="description" label="宠物描述">
					<textarea v-model="formData.description" placeholder="请描述宠物的性格、习惯、饮食偏好等" />
				</uni-forms-item>

				<!-- 时间范围选择器 -->
				<uni-forms-item name="careTimeRange" label="托养时间范围">
					<uni-datetime-picker v-model="formData.careTimeRange" type="range" start="2024-01-01" end="2025-12-31" :clear-icon="false" placeholder="请选择托养时间范围" />
				</uni-forms-item>

				<uni-forms-item name="emergencyContact" label="紧急联系人">
					<uni-easyinput v-model="formData.emergencyContact" placeholder="请输入紧急联系人姓名" />
				</uni-forms-item>

				<uni-forms-item name="emergencyPhone" label="紧急联系人电话">
					<uni-easyinput v-model="formData.emergencyPhone" type="number" placeholder="请输入联系电话" />
				</uni-forms-item>

				<uni-forms-item name="specialRequirements" label="特殊要求">
					<textarea v-model="formData.specialRequirements" placeholder="如有特殊要求请在此说明" />
				</uni-forms-item>
			</uni-forms>

			<!-- 提交按钮 -->
			<view class="submit-section">
				<button class="submit-button" @click="submitForm">提交登记</button>
			</view>
		</view>
	</view>
</template>

<script>
import { getDicts } from '@/api/system/dict/data';
import { addBoarding } from '@/api/pet/boarding.js';
import config from '@/config';

export default {
	data() {
		return {
			formData: {
				// 注意：images 仅为 uni-file-picker 组件使用
				images: [],
				// 新增：用于存放上传成功后的图片URL
				imageUrl: '',
				name: '',
				species: '',
				breed: '',
				gender: '',
				age: 1,
				healthInfo: '',
				neuter: 2,
				immunity: 2,
				insectRepellent: 2,
				dewormingInterval: '',
				insectRepellentLastTime: '',
				description: '',
				careTimeRange: [],
				emergencyContact: '',
				emergencyPhone: '',
				specialRequirements: '',
				shopId: '',
				shopName: ''
			},
			rules: {
				// 规则保持不变
				name: { rules: [{ required: true, errorMessage: '请输入宠物名称' }] },
				species: { rules: [{ required: true, errorMessage: '请选择物种' }] },
				gender: { rules: [{ required: true, errorMessage: '请选择性别' }] },
				age: { rules: [{ required: true, errorMessage: '请选择年龄' }] },
				healthInfo: { rules: [{ required: true, errorMessage: '请输入健康情况' }] },
				careTimeRange: { rules: [{ required: true, errorMessage: '请选择托养时间范围' }] },
				emergencyContact: { rules: [{ required: true, errorMessage: '请输入紧急联系人' }] },
				emergencyPhone: { rules: [{ required: true, errorMessage: '请输入联系电话' }] }
			},
			speciesOptions: [],
			genderOptions: [],
			neuteredOptions: [],
			immunityOptions: [],
			insectRepellentOptions: [],
			isUploading: false // 用于防止重复提交
		};
	},
	onLoad(options) {
		if (options && options.storeId) {
			this.formData.shopId = options.storeId;
			this.formData.shopName = decodeURIComponent(options.storeName || '');
		}
	},
	created() {
		this.loadDictData();
	},
	methods: {
		navigateBack() {
			uni.navigateBack();
		},
		async loadDictData() {
			// 此部分代码保持不变
			try {
				const speciesRes = await getDicts('pet_pet_species');
				this.speciesOptions = speciesRes.data.map((item) => ({ value: item.dictValue, text: item.dictLabel }));
				const genderRes = await getDicts('pet_pet_gender');
				this.genderOptions = genderRes.data.map((item) => ({ value: item.dictValue, text: item.dictLabel }));
				const neuteredRes = await getDicts('pet_pet_neuter');
				this.neuteredOptions = neuteredRes.data.map((item) => ({ value: item.dictValue, text: item.dictLabel }));
				const immunityRes = await getDicts('pet_pet_immunity');
				this.immunityOptions = immunityRes.data.map((item) => ({ value: item.dictValue, text: item.dictLabel }));
				const insectRepellentRes = await getDicts('pet_pet_insect_repellent');
				this.insectRepellentOptions = insectRepellentRes.data.map((item) => ({ value: item.dictValue, text: item.dictLabel }));
			} catch (error) {
				console.error('加载字典数据失败:', error);
				uni.showToast({ title: '加载字典数据失败', icon: 'none' });
			}
		},

		// 【修正】onFileSelect 方法，恢复为只存储文件名
		async onFileSelect(e) {
			console.log('选择的文件:', e);
			if (e.tempFiles && e.tempFiles.length > 0) {
				const file = e.tempFiles[0];
				uni.showLoading({ title: '图片上传中...', mask: true });
				try {
					const uploadResult = await this.uploadImage(file);
					// 【关键】只将文件名存入 formData.imageUrl，用于提交给后端
					console.log('上传成功，文件名:', uploadResult.name);
					this.formData.imageUrl = uploadResult.name; // <--- 改回 .name
					uni.hideLoading();
					uni.showToast({ title: '图片上传成功', icon: 'success' });
				} catch (error) {
					uni.hideLoading();
					uni.showToast({ title: '图片上传失败: ' + error.message, icon: 'none' });
					// 上传失败，清空图片选择器
					this.formData.images = [];
				}
			}
		},

		// 【最终修正版】uploadImage 方法，匹配 /common/upload 接口
		async uploadImage(file) {
			// uni-file-picker 在不同平台返回的对象结构略有不同
			const filePath = file.file ? file.file.path : file.path;

			if (!filePath) {
				throw new Error('无法获取文件路径');
			}

			try {
				const [err, res] = await uni.uploadFile({
					// 【修改1】URL 改回 /common/upload
					url: config.baseUrl + '/common/upload',
					filePath: filePath,
					// 【修改2】name 改回 file，以匹配后端参数名
					name: 'file'
					// 接口不需要token，无需header
				});

				if (err) {
					throw err;
				}

				const data = JSON.parse(res.data);
				console.log(data);
				if (data.code === 200) {
					// 【修改3】根据后端返回的新结构解析数据
					// 后端返回的 data.data.url 是相对路径，如 "2023/12/27/abc.jpg"
					const relativeUrl = data.url;
					const fileName = data.url;

					// 我们需要手动拼接成完整的URL，用于展示
					// 【注意】这个资源路径前缀需要根据你的后端配置来定
					// 若依框架通常是 /profile/uploads/
					const fullUrl = config.baseUrl + '/profile/uploads/' + relativeUrl;

					return {
						url: fullUrl, // 返回完整的URL，虽然当前页面用不到，但备用
						name: fileName // 返回文件名，用于提交
					};
				} else {
					throw new Error(data.msg || '上传失败');
				}
			} catch (error) {
				console.error('图片上传失败:', error);
				throw error;
			}
		},

		// 【修改点3】简化 submitForm 方法，专注于表单验证和数据提交
		async submitForm() {
			// 检查图片是否已上传成功（通过 imageUrl 字段判断）
			if (!this.formData.imageUrl) {
				uni.showToast({ title: '请等待宠物照片上传完成', icon: 'none' });
				return;
			}

			if (this.isUploading) {
				uni.showToast({ title: '正在提交中，请稍候', icon: 'none' });
				return;
			}

			this.$refs.form
				.validate()
				.then(async () => {
					this.isUploading = true; // 设置提交锁
					uni.showLoading({ title: '提交中...', mask: true });

					if (this.formData.careTimeRange && this.formData.careTimeRange.length === 2) {
						const startDate = new Date(this.formData.careTimeRange[0]);
						const endDate = new Date(this.formData.careTimeRange[1]);
						const days = Math.ceil((endDate - startDate) / (1000 * 60 * 60 * 24));
						const dailyRate = this.formData.species === 'dog' ? 88 : this.formData.species === 'cat' ? 68 : 50;
						const totalFee = days * dailyRate;

						// 准备提交给后端的数据
						const submitData = {
							...this.formData,
							// 【关键】使用 imageUrl 而不是 images
							images: this.formData.imageUrl,
							startDate: this.formData.careTimeRange[0],
							endDate: this.formData.careTimeRange[1],
							totalDays: days,
							totalFee: totalFee,
							userId: uni.getStorageSync('userId'),
							status: 0
						};
						console.log(submitData);
						// // 移除不需要提交给后端的字段
						// delete submitData.images;
						// delete submitData.imageUrl;
						// delete submitData.careTimeRange;

						try {
							const response = await addBoarding(submitData);
							if (response.code === 200) {
								uni.hideLoading();
								uni.showToast({ title: '提交成功，等待审核', icon: 'success' });
								setTimeout(() => {
									uni.navigateBack();
								}, 1500);
							} else {
								uni.hideLoading();
								uni.showToast({ title: '提交失败: ' + response.msg, icon: 'none' });
							}
						} catch (error) {
							uni.hideLoading();
							uni.showToast({ title: '提交失败: ' + error.message, icon: 'none' });
						}
					} else {
						uni.hideLoading();
						uni.showToast({ title: '请选择完整的托养时间范围', icon: 'none' });
					}
				})
				.catch((errors) => {
					console.log('表单错误:', errors);
					uni.hideLoading();
					uni.showToast({ title: '请填写完整表单信息', icon: 'none' });
				})
				.finally(() => {
					uni.hideLoading();
					this.isUploading = false;
				});
		}
	}
};
</script>

<style lang="scss" scoped>
.pet-care-registration {
	min-height: 100vh;
	background-color: #f8f9fa;
	padding: 30rpx;
	box-sizing: border-box;

	.page-header {
		display: flex;
		align-items: center;
		padding: 20rpx 30rpx;
		background-color: #ffffff;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
		position: sticky;
		top: 0;
		z-index: 100;

		.back-icon {
			width: 60rpx;
			height: 60rpx;
			display: flex;
			justify-content: center;
			align-items: center;
			color: #6c757d;
		}

		.header-title {
			font-size: 34rpx;
			font-weight: bold;
			color: #2c3e50;
			flex: 1;
			text-align: center;
		}

		.header-actions {
			width: 60rpx;
			height: 60rpx;
			display: flex;
			justify-content: center;
			align-items: center;
		}
	}

	.form-container {
		background-color: #ffffff;
		border-radius: 20rpx;
		box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.06);
		padding: 30rpx;
		margin-top: 20rpx;
	}

	.submit-section {
		margin-top: 40rpx;
		text-align: center;
	}

	.submit-button {
		width: 100%;
		background: linear-gradient(90deg, #3498db 0%, #2ecc71 100%);
		border-radius: 30rpx;
		padding: 25rpx 0;
		font-size: 32rpx;
		font-weight: bold;
		color: #ffffff;
		box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.1);
		transition: all 0.3s ease;
	}

	.submit-button:active {
		transform: scale(0.98);
		box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.08);
	}

	textarea {
		min-height: 120rpx;
		padding: 15rpx;
		font-size: 28rpx;
	}

	.uni-forms-item {
		margin-bottom: 30rpx;
	}

	.uni-easyinput {
		font-size: 28rpx;
	}

	.uni-data-select {
		font-size: 28rpx;
	}

	.uni-datetime-picker {
		font-size: 28rpx;
	}

	.uni-data-checkbox {
		font-size: 28rpx;
	}

	.uni-number-box {
		font-size: 28rpx;
	}
}
</style>
