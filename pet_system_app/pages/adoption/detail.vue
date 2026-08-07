<template>
	<view class="pet-detail-container">
		<!-- 宠物图片 -->
		<view class="pet-image-container">
			<image :src="getImageUrl(petInfo.images)" mode="aspectFill" class="pet-image" @error="handleImageError"></image>
		</view>

		<!-- 宠物基本信息 -->
		<view class="pet-info-container">
			<view class="pet-name-section">
				<text class="pet-name">{{ petInfo.name }}</text>
				<!-- 修复这里：改为计算属性或直接表达式 -->
				<view class="pet-gender-tag" :class="genderClass">
					<text>{{ genderText }}</text>
				</view>
			</view>

			<view class="pet-basic-info">
				<text class="basic-info-item">物种：{{ speciesText }}</text>
				<text class="basic-info-item">品种：{{ petInfo.breed || '未知' }}</text>
				<text class="basic-info-item">年龄：{{ petInfo.age || '未知' }}{{ ageUnit }}</text>
			</view>

			<view class="pet-meta-info">
				<view class="health-status">
					<view class="status-item">
						<view class="status-icon">💉</view>
						<text class="status-text">{{ immunityText }}</text>
					</view>
					<view class="status-item">
						<view class="status-icon">✂️</view>
						<text class="status-text">{{ neuterText }}</text>
					</view>
					<view class="status-item">
						<view class="status-icon">🐛</view>
						<text class="status-text">{{ insectRepellentText }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 宠物详细描述 -->
		<view class="pet-description-section">
			<text class="section-title">宠物介绍</text>
			<text class="pet-description">{{ petInfo.description || '暂无描述' }}</text>
		</view>

		<!-- 领养条件 -->
		<view class="adoption-conditions-section">
			<text class="section-title">领养条件</text>
			<view class="condition-item">
				<text class="condition-icon">✓</text>
				<text class="condition-text">必须同城领养</text>
			</view>
			<view class="condition-item">
				<text class="condition-icon">✓</text>
				<text class="condition-text">必须签署领养协议</text>
			</view>
			<view class="condition-item">
				<text class="condition-icon">✓</text>
				<text class="condition-text">不可遗弃、不可转送、不可转卖</text>
			</view>
			<view class="condition-item">
				<text class="condition-icon">✓</text>
				<text class="condition-text">定期回访，不可虐待宠物</text>
			</view>
		</view>

		<!-- 底部操作栏 -->
		<view class="action-bar">
			<view class="collect-button" @click="handleCollect">
				<view class="collect-icon" :class="{ collected: isCollected }">
					<i v-if="isCollected" class="fas fa-heart" style="color: #ff4757"></i>
					<i v-else class="far fa-heart" style="color: #666"></i>
				</view>
				<text class="collect-text" :class="{ collected: isCollected }">
					{{ isCollected ? '已收藏' : '收藏' }}
				</text>
			</view>
			<view class="adopt-button" @click="handleAdopt">
				<text class="adopt-text">我要领养</text>
			</view>
		</view>
	</view>
</template>

<script>
import { getPet } from '@/api/pet/adoption.js';
import { collectPet, cancelCollect, checkCollect } from '@/api/pet/collect.js';
import config from '@/config.js';

export default {
	data() {
		return {
			petId: '',
			petInfo: {
				name: '',
				gender: 2,
				species: 0,
				breed: '',
				age: '',
				imageUrl: '',
				images: '',
				description: '',
				immunity: 2,
				neuter: 2,
				insectRepellent: 2,
				isCollect: ''
			},
			isCollected: false,
			loading: false
		};
	},

	computed: {
		// 性别相关计算属性
		genderClass() {
			const gender = this.petInfo.gender;
			const genderClassMap = {
				0: 'gender-female',
				1: 'gender-male',
				2: 'gender-unknown'
			};
			return genderClassMap[gender] || 'gender-unknown';
		},

		genderText() {
			const gender = this.petInfo.gender;
			const genderMap = {
				0: '母',
				1: '公',
				2: '未知'
			};
			return genderMap[gender] || '未知';
		},

		// 物种文本
		speciesText() {
			const species = this.petInfo.species;
			const speciesMap = {
				0: '猫',
				1: '狗',
				2: '其他'
			};
			return speciesMap[species] || '宠物';
		},

		// 年龄单位
		ageUnit() {
			const age = this.petInfo.age;
			if (!age) return '';
			const ageNum = Number(age);
			return ageNum >= 12 ? '岁' : '个月';
		},

		// 健康状态文本
		immunityText() {
			const immunity = this.petInfo.immunity;
			const immunityMap = {
				0: '已免疫',
				1: '未免疫',
				2: '免疫未知'
			};
			return immunityMap[immunity] || '免疫未知';
		},

		neuterText() {
			const neuter = this.petInfo.neuter;
			const neuterMap = {
				0: '未绝育',
				1: '已绝育',
				2: '绝育未知'
			};
			return neuterMap[neuter] || '绝育未知';
		},

		insectRepellentText() {
			const insectRepellent = this.petInfo.insectRepellent;
			const insectRepellentMap = {
				0: '未驱虫',
				1: '已驱虫',
				2: '驱虫未知'
			};
			return insectRepellentMap[insectRepellent] || '驱虫未知';
		}
	},

	onLoad(options) {
		if (options.petId || options.id) {
			this.petId = options.petId || options.id;
			this.loadPetInfo(this.petId);
			this.checkCollectStatus();
		}
	},

	methods: {
		async loadPetInfo(petId) {
			try {
				uni.showLoading({
					title: '加载中...',
					mask: true
				});
				const res = await getPet(petId);
				console.log(res);
				if (res.code === 200 && res.data) {
					this.petInfo = res.data;
					this.isCollected = res.data.isCollect;
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

		// 在宠物详情页的methods中修改：

		// 检查收藏状态
		async checkCollectStatus() {
			try {
				const userInfo = uni.getStorageSync('userInfo');
				if (!userInfo || !userInfo.userId) {
					this.isCollected = false;
					return;
				}

				const params = {
					userId: userInfo.userId,
					petId: this.petId
				};

				const res = await checkCollect(params);
				if (res.code === 200) {
					this.isCollected = res.data.collected || false;
				}
			} catch (error) {
				console.error('检查收藏状态失败:', error);
			}
		},

		// 处理收藏
		async handleCollect() {
			if (this.loading) return;

			try {
				this.loading = true;
				const params = {
					petId: this.petId
				};

				let res;
				if (this.isCollected) {
					res = await cancelCollect(params);
				} else {
					res = await collectPet(params);
				}

				if (res.code === 200) {
					this.isCollected = !this.isCollected;
					uni.showToast({
						title: this.isCollected ? '收藏成功' : '已取消收藏',
						icon: 'success',
						duration: 1500
					});
				} else {
					throw new Error(res.msg || '操作失败');
				}
			} catch (error) {
				console.error('收藏操作失败:', error);
				uni.showToast({
					title: error.message || '操作失败',
					icon: 'none'
				});
			} finally {
				this.loading = false;
			}
		},

		handleAdopt() {
			if (this.petId) {
				uni.navigateTo({
					url: `/pages/adoption/apply?petId=${this.petId}`
				});
			}
		},

		handleImageError(e) {
			console.error('图片加载失败:', e);
			e.currentTarget.src = '/static/images/entrust01.png';
			e.currentTarget.onerror = null;
		},

		getImageUrl(fileName) {
			return config.getDownloadUrl(fileName);
		}
	}
};
</script>

<style lang="scss" scoped>
.pet-detail-container {
	position: relative;
	width: 100%;
	min-height: 100vh;
	background-color: #f8f8f8;
}

.back-button {
	position: absolute;
	top: 40rpx;
	left: 30rpx;
	width: 60rpx;
	height: 60rpx;
	background-color: rgba(0, 0, 0, 0.3);
	border-radius: 50%;
	display: flex;
	justify-content: center;
	align-items: center;
	z-index: 100;
}

.back-icon {
	color: white;
	font-size: 36rpx;
	font-weight: bold;
}

.pet-image-container {
	width: 100%;
	height: 500rpx;
	overflow: hidden;
}

.pet-image {
	width: 100%;
	height: 100%;
}

.pet-info-container {
	padding: 30rpx;
	background-color: white;
	margin-bottom: 20rpx;
}

.pet-name-section {
	display: flex;
	align-items: center;
	margin-bottom: 15rpx;
	gap: 20rpx;
}

.pet-name {
	font-size: 48rpx;
	font-weight: bold;
	color: #2c3e50;
}

.pet-gender-tag {
	padding: 8rpx 16rpx;
	border-radius: 20rpx;
	font-size: 24rpx;
	color: white;
	font-weight: 500;
	backdrop-filter: blur(10px);
}

.pet-gender-tag.gender-male {
	background-color: #409eff;
}

.pet-gender-tag.gender-female {
	background-color: #f7879a;
}

.pet-gender-tag.gender-unknown {
	background-color: #cccccc;
	color: #666;
}

.pet-basic-info {
	display: flex;
	flex-wrap: wrap;
	gap: 20rpx;
	margin-bottom: 15rpx;
}

.basic-info-item {
	font-size: 28rpx;
	color: #666;
	padding: 8rpx 16rpx;
	background-color: #f5f5f5;
	border-radius: 20rpx;
	backdrop-filter: blur(10px);
}

.pet-meta-info {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
	font-size: 28rpx;
	color: #999;
}

.health-status {
	display: flex;
	gap: 30rpx;
	flex-wrap: wrap;
}

.status-item {
	display: flex;
	align-items: center;
	gap: 8rpx;
	padding: 8rpx 16rpx;
	background-color: #f8f9fa;
	border-radius: 20rpx;
	backdrop-filter: blur(10px);
}

.status-icon {
	font-size: 32rpx;
}

.status-text {
	font-size: 26rpx;
	color: #666;
}

.pet-description-section,
.adoption-conditions-section {
	padding: 30rpx;
	background-color: white;
	margin-bottom: 20rpx;
}

.section-title {
	font-size: 36rpx;
	font-weight: bold;
	margin-bottom: 20rpx;
	display: block;
	color: #333;
}

.pet-description {
	font-size: 30rpx;
	color: #333;
	line-height: 1.8;
	white-space: pre-line;
}

.condition-item {
	display: flex;
	align-items: center;
	padding: 15rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
}

.condition-item:last-child {
	border-bottom: none;
}

.condition-icon {
	width: 40rpx;
	height: 40rpx;
	background-color: #4caf50;
	color: white;
	border-radius: 50%;
	display: flex;
	justify-content: center;
	align-items: center;
	font-size: 24rpx;
	margin-right: 20rpx;
}

.condition-text {
	font-size: 28rpx;
	color: #333;
}

.action-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	height: 120rpx;
	background-color: rgba(255, 255, 255, 0.95);
	display: flex;
	align-items: center;
	padding: 0 30rpx;
	border-top: 1rpx solid rgba(0, 0, 0, 0.1);
	backdrop-filter: blur(20rpx);
	z-index: 100;
}

.collect-button {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	width: 120rpx;
	height: 100rpx;
	background-color: rgba(248, 249, 250, 0.8);
	border-radius: 16rpx;
	margin-right: 20rpx;
	transition: all 0.3s ease;
	backdrop-filter: blur(10px);
}

.collect-button:active {
	transform: scale(0.95);
}

.collect-icon {
	font-size: 32rpx;
	margin-bottom: 8rpx;
}

.collect-text {
	font-size: 24rpx;
	color: #666;
	font-weight: 500;
	transition: all 0.3s ease;
}

.collect-text.collected {
	color: #ff4757;
	font-weight: 600;
}

.adopt-button {
	flex: 1;
	height: 100rpx;
	background: linear-gradient(135deg, #ff9a44 0%, #ff6b35 100%);
	border-radius: 16rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 32rpx;
	color: #ffffff;
	font-weight: 600;
	box-shadow: 0 4rpx 20rpx rgba(255, 107, 53, 0.3);
	transition: all 0.3s ease;
}

.adopt-button:active {
	transform: translateY(2rpx);
	box-shadow: 0 2rpx 10rpx rgba(255, 107, 53, 0.3);
}
</style>
