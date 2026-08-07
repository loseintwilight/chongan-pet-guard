<template>
	<view class="detail-container">
		<!-- 顶部导航栏 -->
		<view class="nav-bar">
			<view class="nav-title">寻宠详情</view>
		</view>

		<!-- 内容区域 -->
		<scroll-view class="content-scroll" scroll-y>
			<!-- 宠物图片 -->
			<view class="pet-image-section">
				<image :src="getImageUrl(petDetail.images)" mode="aspectFill" class="pet-image"></image>
			</view>

			<!-- 宠物信息卡片 -->
			<view class="info-card">
				<!-- 宠物名称和状态 -->
				<view class="pet-header">
					<text class="pet-name">{{ petDetail.petName || '未命名宠物' }}</text>
					<view class="status-badge" :class="statusClass">
						{{ statusText }}
					</view>
				</view>

				<!-- 基本信息 -->
				<view class="basic-info">
					<view class="info-item">
						<text class="info-label">品种</text>
						<text class="info-value">{{ speciesText }}</text>
					</view>
					<view class="info-item">
						<text class="info-label">丢失时间</text>
						<text class="info-value">{{ formatDate(petDetail.lostTime) }}</text>
					</view>
				</view>

				<!-- 丢失地点 -->
				<view class="location-section">
					<text class="location-label">丢失地点</text>
					<text class="location-value">{{ petDetail.lostLocation || '未知地点' }}</text>
				</view>

				<!-- 特征描述 -->
				<view class="description-section" v-if="petDetail.features">
					<text class="section-title">特征描述</text>
					<text class="description-text">{{ petDetail.features }}</text>
				</view>

				<!-- 想说的话 -->
				<view class="message-section" v-if="petDetail.message">
					<text class="section-title">想说的话</text>
					<text class="message-text">{{ petDetail.message }}</text>
				</view>

				<!-- 主人信息 -->
				<view class="owner-section">
					<text class="section-title">主人信息</text>
					<view class="owner-info">
						<text class="owner-name">{{ petDetail.ownerName || '未提供' }}</text>
						<text class="owner-label">主人</text>
					</view>
					<view class="owner-phone" v-if="petDetail.ownerPhone" @click="makePhoneCall">
					<text class="phone-icon">📞</text>
					<text class="phone-number">{{ petDetail.ownerPhone }}</text>
						<text class="call-text">拨打</text>
					</view>
				</view>

				<!-- 悬赏金额 -->
				<view class="reward-section" v-if="petDetail.reward && petDetail.reward > 0">
					<text class="reward-label">💰 悬赏</text>
					<text class="reward-amount">{{ petDetail.reward }}元</text>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
import { getLostPet } from '@/api/pet/lostPet.js';
import config from '@/config';

export default {
	data() {
			return {
				lostId: '',
				loading: false,
				error: false,
				// 宠物详情数据 - 匹配search/index.vue中的字段格式
				petDetail: {
					lostId: '',
					petName: '',
					species: 0,
					breed: '',
					features: '',
					lostLocation: '',
					lostTime: '',
					reward: 0,
					images: '',
					status: 0,
					ownerName: '',
					ownerPhone: '',
					createTime: '',
					updateTime: ''
				}
			};
		},

	computed: {
		// 物种文本
		speciesText() {
			const map = {
				0: '猫',
				1: '狗',
				2: '其他'
			};
			return map[this.petDetail.species] || '宠物';
		},

		// 状态文本
		statusText() {
			const map = {
				0: '寻找中',
				1: '已找到',
				2: '已结束'
			};
			return map[this.petDetail.status] || '寻找中';
		},

		// 状态样式类
		statusClass() {
			const map = {
				0: 'status-searching',
				1: 'status-found',
				2: 'status-ended'
			};
			return map[this.petDetail.status] || 'status-searching';
		}
	},

	onLoad(options) {
		console.log('页面参数:', options);
		this.lostId = options.lostId;
		console.log('获取到的lostId:', this.lostId);
		if (this.lostId) {
			this.loadPetDetail();
		} else {
			console.error('没有获取到宠物ID');
		}
	},

	methods: {
		// 加载宠物详情
		loadPetDetail() {
			if (!this.lostId) return;

			console.log('开始加载宠物详情，ID:', this.lostId);
			this.loading = true;
			this.error = false;

			getLostPet(this.lostId)
				.then(res => {
					console.log('接口完整响应:', res);
					if (res.code === 200) {
						// 使用对象赋值，确保所有字段都被覆盖
						const data = res.data;
						console.log('获取到的宠物数据:', data);
						
						// 转换字段名（蛇形命名转小驼峰）
						const convertToCamelCase = (obj) => {
							if (!obj || typeof obj !== 'object') return obj;
							const result = {};
							for (const key in obj) {
								if (Object.prototype.hasOwnProperty.call(obj, key)) {
									const camelCaseKey = key.replace(/_([a-z])/g, (match, letter) => letter.toUpperCase());
									result[camelCaseKey] = obj[key];
								}
							}
							return result;
						};
						
						const camelCaseData = convertToCamelCase(data);
						console.log('转换后的宠物数据:', camelCaseData);
						
						// 直接赋值，避免逐个设置
						this.petDetail = {
							...this.petDetail,
							...camelCaseData
						};

						console.log('更新后的petDetail:', this.petDetail);
					} else {
						console.error('接口返回错误:', res.msg);
						this.error = true;
						uni.showToast({
							title: '获取数据失败',
							icon: 'none',
							duration: 2000
						});
					}
				})
				.catch(error => {
					console.error('加载宠物详情失败:', error);
					console.error('错误详情:', error.message);
					this.error = true;
					uni.showToast({
						title: '网络异常，请稍后重试',
						icon: 'none',
						duration: 2000
					});
				})
				.finally(() => {
					this.loading = false;
					console.log('数据加载完成');
				});
		},

		// 格式化日期
		formatDate(dateStr) {
			if (!dateStr) return '未知时间';
			try {
				const date = new Date(dateStr);
				if (isNaN(date.getTime())) return dateStr;

				const year = date.getFullYear();
				const month = (date.getMonth() + 1).toString().padStart(2, '0');
				const day = date.getDate().toString().padStart(2, '0');
				return `${year}-${month}-${day}`;
			} catch (error) {
				console.error('格式化日期错误:', error);
				return dateStr;
			}
		},

		// 获取图片URL
		getImageUrl(fileName) {
			if (!fileName) {
				return '/static/images/default-pet.png';
			}
			const url = config.getDownloadUrl(fileName);
			console.log('图片URL:', url);
			return url || '/static/images/default-pet.png';
		},

		// 拨打电话
		makePhoneCall() {
			if (!this.petDetail.ownerPhone) {
				console.log('没有联系电话');
				return;
			}

			console.log('拨打电话:', this.petDetail.ownerPhone);
			uni.makePhoneCall({
				phoneNumber: this.petDetail.ownerPhone
			});
		}
	}
};
</script>

<style scoped>
.detail-container {
	background-color: #f5f5f5;
	min-height: 100vh;
}

/* 导航栏 */
.nav-bar {
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 20rpx 30rpx;
	background-color: #ffffff;
	border-bottom: 1rpx solid #e0e0e0;
}

.nav-title {
	font-size: 36rpx;
	font-weight: 600;
	color: #333333;
}

/* 内容区域 */
.content-scroll {
	height: calc(100vh - 120rpx);
	padding-top: 120rpx;
	margin-top: -120rpx;
}

/* 宠物图片 */
.pet-image-section {
	width: 100%;
	height: 60vw;
	background-color: #ffffff;
	display: flex;
	align-items: center;
	justify-content: center;
	overflow: hidden;
}

.pet-image {
	width: 100%;
	height: 100%;
	object-fit: cover;
}

/* 信息卡片 */
.info-card {
	background-color: #ffffff;
	border-radius: 20rpx 20rpx 0 0;
	padding: 30rpx;
	margin-top: -20rpx;
}

/* 宠物头部 */
.pet-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 30rpx;
}

.pet-name {
	font-size: 40rpx;
	font-weight: bold;
	color: #333333;
	flex: 1;
}

.status-badge {
	padding: 8rpx 20rpx;
	border-radius: 20rpx;
	font-size: 24rpx;
	font-weight: 500;
	color: white;
}

.status-searching {
	background-color: #ff6b35;
}

.status-found {
	background-color: #4caf50;
}

.status-ended {
	background-color: #999999;
}

/* 基本信息 */
.basic-info {
	display: flex;
	margin-bottom: 30rpx;
	padding-bottom: 20rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.info-item {
	flex: 1;
}

.info-label {
	display: block;
	font-size: 24rpx;
	color: #999999;
	margin-bottom: 8rpx;
}

.info-value {
	display: block;
	font-size: 28rpx;
	color: #333333;
	font-weight: 500;
}

/* 地点信息 */
.location-section {
	margin-bottom: 30rpx;
	padding-bottom: 20rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.location-label {
	display: block;
	font-size: 24rpx;
	color: #999999;
	margin-bottom: 8rpx;
}

.location-value {
	display: block;
	font-size: 28rpx;
	color: #333333;
	line-height: 1.4;
}

/* 描述区域 */
.description-section,
.message-section {
	margin-bottom: 30rpx;
}

.section-title {
	display: block;
	font-size: 28rpx;
	color: #333333;
	font-weight: bold;
	margin-bottom: 15rpx;
}

.description-text,
.message-text {
	font-size: 28rpx;
	color: #666666;
	line-height: 1.6;
}

/* 主人信息 */
.owner-section {
	margin-bottom: 30rpx;
	padding: 20rpx;
	background-color: #f9f9f9;
	border-radius: 10rpx;
}

.owner-info {
	display: flex;
	align-items: center;
	margin-bottom: 15rpx;
}

.owner-name {
	font-size: 32rpx;
	color: #333333;
	font-weight: bold;
	margin-right: 15rpx;
}

.owner-label {
	background-color: #4caf50;
	color: white;
	padding: 4rpx 12rpx;
	border-radius: 12rpx;
	font-size: 20rpx;
}

.owner-phone {
	display: flex;
	align-items: center;
	padding: 15rpx 0;
}

.phone-icon {
	font-size: 28rpx;
	margin-right: 10rpx;
	color: #4caf50;
}

.phone-number {
	flex: 1;
	font-size: 28rpx;
	color: #333333;
	font-weight: 500;
}

.call-text {
	font-size: 24rpx;
	color: #4caf50;
	padding: 4rpx 12rpx;
	border: 1rpx solid #4caf50;
	border-radius: 12rpx;
}

/* 悬赏信息 */
.reward-section {
	display: flex;
	align-items: center;
	justify-content: center;
	background: linear-gradient(135deg, #fff3e0, #ffecb3);
	padding: 25rpx;
	border-radius: 15rpx;
}

.reward-label {
	font-size: 28rpx;
	color: #ff6b35;
	font-weight: 500;
	margin-right: 15rpx;
}

.reward-amount {
	font-size: 40rpx;
	color: #ff6b35;
	font-weight: bold;
}
</style>