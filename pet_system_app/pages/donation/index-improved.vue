<template>
	<view class="donation-container">
		<!-- 顶部栏 -->
		<view class="top-bar">
			<view class="brand-section">
				<image class="brand-icon" src="/static/pet-logo.jpeg" mode="aspectFit" />
				<text class="brand-name">爱心捐助</text>
			</view>
			<view class="notification-section" @click="toNotifications">
				<image src="/static/notification-bell.png" mode="aspectFit" style="width: 24px; height: 24px" />
			</view>
		</view>

		<!-- 轮播图区域 -->
		<view class="carousel-section">
			<swiper class="swiper-box" :current="current" @change="changeSwiper" circular autoplay>
				<swiper-item v-for="(item, index) in carouselList" :key="index">
					<view class="swiper-item">
						<image :src="getImageUrl(item.images)" mode="aspectFill" :draggable="false" />
						<view class="swiper-overlay"></view>
						<view class="swiper-text-content">
							<text class="swiper-title">{{ item.title || '爱心捐助' }}</text>
							<text class="swiper-subtitle">{{ item.subtitle || '帮助流浪动物，给它们一个温暖的家' }}</text>
						</view>
					</view>
				</swiper-item>
			</swiper>
		</view>

		<!-- 捐助统计模块 -->
		<view class="stats-section">
			<view class="section-header">
				<text class="section-title">📊 爱心统计</text>
				<text class="section-more" @click="toDonationHistory">查看详情</text>
			</view>
			<view class="stats-grid">
				<view class="stat-card">
					<view class="stat-icon">💝</view>
					<text class="stat-number">{{ totalDonations }}</text>
					<text class="stat-label">总捐助金额</text>
				</view>
				<view class="stat-card">
					<view class="stat-icon">👥</view>
					<text class="stat-number">{{ donorCount }}</text>
					<text class="stat-label">爱心人士</text>
				</view>
				<view class="stat-card">
					<view class="stat-icon">🐾</view>
					<text class="stat-number">{{ helpedAnimals }}</text>
					<text class="stat-label">受助动物</text>
				</view>
			</view>
		</view>

		<!-- 捐助项目展示 -->
		<view class="projects-section">
			<view class="section-header">
				<text class="section-title">🎯 捐助项目</text>
				<text class="section-more" @click="toAllProjects">全部项目</text>
			</view>
			<view class="projects-grid">
				<view v-for="project in donationProjects" :key="project.id" class="project-card" @click="selectProject(project)">
					<view class="project-image-container">
						<image :src="getImageUrl(project.image)" mode="aspectFill" class="project-image" />
						<view class="project-progress">
							<text class="progress-text">{{ project.progress }}%</text>
						</view>
					</view>
					<view class="project-info">
						<text class="project-title">{{ project.title }}</text>
						<text class="project-description">{{ project.description }}</text>
						<view class="project-stats">
							<text class="raised">已筹 ¥{{ project.raised }}</text>
							<text class="goal">目标 ¥{{ project.goal }}</text>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 快速捐助区域 -->
		<view class="quick-donation-section">
			<view class="section-header">
				<text class="section-title">⚡ 快速捐助</text>
			</view>
			<view class="donation-card">
				<view class="card-header">
					<text class="card-title">为流浪动物献爱心</text>
					<text class="card-subtitle">您的每一份捐助，都是它们的一份希望</text>
				</view>
				
				<!-- 金额选择 -->
				<view class="amount-section">
					<text class="amount-label">选择捐助金额</text>
					<view class="quick-amount-buttons">
						<view class="quick-amount-button" 
							v-for="amount in quickAmounts" 
							:key="amount" 
							:class="{ active: donationAmount === amount }" 
							@click="setQuickAmount(amount)">
							<text class="button-text">¥{{ amount }}</text>
						</view>
					</view>
					<view class="custom-amount">
						<text class="currency">¥</text>
						<input v-model="donationAmount" type="number" class="amount-input" placeholder="自定义金额" />
					</view>
				</view>

				<!-- 支付按钮 -->
				<view class="payment-section">
					<button class="payment-button" @click="handlePayment">
						<text class="button-text">立即捐助</text>
					</button>
				</view>
			</view>
		</view>

		<!-- 爱心榜 -->
		<view class="love-rank-section">
			<view class="section-header">
				<text class="section-title">💖 爱心榜</text>
				<text class="section-more" @click="toLoveRank">查看完整榜单</text>
			</view>
			<view class="rank-list">
				<view v-for="(donor, index) in topDonors" :key="donor.id" class="rank-item">
					<view class="rank-number" :class="{ 'top-three': index < 3 }">{{ index + 1 }}</view>
					<image :src="donor.avatar || '/static/images/default-avatar.png'" class="donor-avatar" />
					<view class="donor-info">
						<text class="donor-name">{{ donor.name }}</text>
						<text class="donor-amount">¥{{ donor.amount }}</text>
					</view>
					<view class="rank-badge" v-if="index < 3">
						<text class="badge-text">{{ ['🥇', '🥈', '🥉'][index] }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 模拟支付成功提示 -->
		<uni-popup ref="paymentSuccessPopup" type="dialog">
			<uni-popup-dialog
				type="success"
				title="捐助成功"
				content="感谢您的爱心捐助，您的帮助让流浪动物有了更多的希望！"
				:duration="2000"
				:before-close="false"
				@confirm="navigateToHome" />
		</uni-popup>
	</view>
</template>

<script>
import { addDonation } from '@/api/pet/donation.js';
import { listCarousel } from '@/api/pet/carousel.js';
import config from '@/config';

export default {
	data() {
		return {
			current: 0,
			carouselList: [],
			donationAmount: 10,
			quickAmounts: [10, 50, 100, 200, 500],
			
			// 统计数据
			totalDonations: '12,580',
			donorCount: '1,234',
			helpedAnimals: '89',
			
			// 捐助项目
			donationProjects: [
				{
					id: 1,
					title: '冬季救助计划',
					description: '为流浪动物提供温暖的过冬物资',
					image: '/static/work/宠物捐助.png',
					raised: '8,500',
					goal: '10,000',
					progress: 85
				},
				{
					id: 2,
					title: '医疗救助基金',
					description: '帮助受伤的流浪动物获得及时治疗',
					image: '/static/work/宠物捐助.png',
					raised: '5,200',
					goal: '8,000',
					progress: 65
				}
			],
			
			// 爱心榜
			topDonors: [
				{ id: 1, name: '爱心人士', amount: '1,000', avatar: '' },
				{ id: 2, name: '小明', amount: '800', avatar: '' },
				{ id: 3, name: '匿名用户', amount: '500', avatar: '' },
				{ id: 4, name: '宠物爱好者', amount: '300', avatar: '' }
			]
		};
	},
	onLoad() {
		this.getCarouselList();
	},
	methods: {
		getCarouselList() {
			const params = { status: 1 };
			listCarousel(params)
				.then((response) => {
					this.carouselList = response.rows || [];
				})
				.catch((error) => {
					console.error('获取轮播图失败:', error);
				});
		},
		
		getImageUrl(fileName) {
			return config.getDownloadUrl(fileName);
		},
		
		changeSwiper(e) {
			this.current = e.detail.current;
		},
		
		setQuickAmount(amount) {
			this.donationAmount = amount;
		},
		
		selectProject(project) {
			// 可以根据选中的项目调整默认金额
			this.selectedProject = project;
		},
		
		handlePayment() {
			uni.showLoading({
				title: '正在处理...',
				mask: true
			});
			
			const form = {
				amount: this.donationAmount
			};
			
			addDonation(form).then((response) => {
				if (response.code == 200) {
					setTimeout(() => {
						uni.hideLoading();
						this.$refs.paymentSuccessPopup.open();
					}, 2000);
				}
			}).catch(() => {
				uni.hideLoading();
				uni.showToast({
					title: '支付失败，请重试',
					icon: 'none'
				});
			});
		},
		
		navigateToHome() {
			uni.navigateBack();
		},
		
		// 导航方法
		toNotifications() {
			uni.navigateTo({
				url: '/pages/notice/index'
			});
		},
		
		toDonationHistory() {
			uni.showToast({
				title: '功能开发中',
				icon: 'none'
			});
		},
		
		toAllProjects() {
			uni.showToast({
				title: '功能开发中',
				icon: 'none'
			});
		},
		
		toLoveRank() {
			uni.showToast({
				title: '功能开发中',
				icon: 'none'
			});
		}
	}
};
</script>

<style lang="scss" scoped>
/* --- 全局与容器 --- */
.donation-container {
	display: flex;
	flex-direction: column;
	background-color: #f8f9fa;
	min-height: 100vh;
}

/* --- 顶部栏样式 --- */
.top-bar {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx 30rpx;
	background: linear-gradient(90deg, #ff9a9e 0%, #fad0c4 100%);
	box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
	position: relative;
	z-index: 100;
}

.brand-section {
	display: flex;
	align-items: center;
	gap: 12rpx;
}

.brand-icon {
	width: 50rpx;
	height: 50rpx;
	border-radius: 12rpx;
	background-color: rgba(255, 255, 255, 0.9);
}

.brand-name {
	font-size: 32rpx;
	font-weight: bold;
	color: #ffffff;
	text-shadow: 0 1rpx 2rpx rgba(0, 0, 0, 0.2);
}

.notification-section {
	position: relative;
}

/* --- 轮播图区域 --- */
.carousel-section {
	position: relative;
	margin-top: 20rpx;
	.swiper-box {
		height: 350rpx;
	}
	.swiper-item {
		position: relative;
		width: 100%;
		height: 100%;
		overflow: hidden;
		image {
			width: 100%;
			height: 100%;
		}
		.swiper-overlay {
			position: absolute;
			bottom: 0;
			left: 0;
			right: 0;
			height: 150rpx;
			background: linear-gradient(to top, rgba(0, 0, 0, 0.6), transparent);
		}
		.swiper-text-content {
			position: absolute;
			bottom: 30rpx;
			left: 30rpx;
			right: 30rpx;
			z-index: 10;
			color: #ffffff;
			text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.5);
			display: flex;
			flex-direction: column;
		}
		.swiper-title {
			font-size: 36rpx;
			font-weight: bold;
			margin-bottom: 10rpx;
			overflow: hidden;
			text-overflow: ellipsis;
			white-space: nowrap;
		}
		.swiper-subtitle {
			font-size: 26rpx;
			opacity: 0.9;
			overflow: hidden;
			text-overflow: ellipsis;
			display: -webkit-box;
			-webkit-line-clamp: 2;
			-webkit-box-orient: vertical;
		}
	}
}

/* --- 通用模块样式 --- */
.section-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 25rpx 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.section-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.section-more {
	font-size: 28rpx;
	color: #ff6b6b;
}

/* --- 统计模块 --- */
.stats-section {
	margin: 20rpx 30rpx;
	background-color: #ffffff;
	border-radius: 20rpx;
	box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.05);
	overflow: hidden;
}

.stats-grid {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 20rpx;
	padding: 30rpx;
}

.stat-card {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 30rpx 20rpx;
	background: linear-gradient(135deg, #ffeaa7 0%, #fab1a0 100%);
	border-radius: 16rpx;
	box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.05);
}

.stat-icon {
	font-size: 48rpx;
	margin-bottom: 16rpx;
}

.stat-number {
	font-size: 36rpx;
	font-weight: bold;
	color: #2d3436;
	margin-bottom: 8rpx;
}

.stat-label {
	font-size: 24rpx;
	color: #636e72;
}

/* --- 捐助项目模块 --- */
.projects-section {
	margin: 20rpx 30rpx;
	background-color: #ffffff;
	border-radius: 20rpx;
	box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.05);
	overflow: hidden;
}

.projects-grid {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
	padding: 30rpx;
}

.project-card {
	display: flex;
	background: rgba(255, 255, 255, 0.95);
	border-radius: 16rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.05);
	transition: all 0.3s ease;
}

.project-card:active {
	transform: scale(0.98);
}

.project-image-container {
	position: relative;
	width: 200rpx;
	height: 150rpx;
	flex-shrink: 0;
}

.project-image {
	width: 100%;
	height: 100%;
	object-fit: cover;
}

.project-progress {
	position: absolute;
	top: 12rpx;
	right: 12rpx;
	background: rgba(255, 255, 255, 0.9);
	border-radius: 20rpx;
	padding: 6rpx 12rpx;
	backdrop-filter: blur(10px);
}

.progress-text {
	font-size: 20rpx;
	color: #ff6b35;
	font-weight: bold;
}

.project-info {
	flex: 1;
	padding: 20rpx;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

.project-title {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 8rpx;
}

.project-description {
	font-size: 24rpx;
	color: #666;
	line-height: 1.4;
	margin-bottom: 12rpx;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
}

.project-stats {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.raised {
	font-size: 24rpx;
	color: #ff6b35;
	font-weight: bold;
}

.goal {
	font-size: 24rpx;
	color: #999;
}

/* --- 快速捐助区域 --- */
.quick-donation-section {
	margin: 20rpx 30rpx;
	background-color: #ffffff;
	border-radius: 20rpx;
	box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.05);
	overflow: hidden;
}

.donation-card {
	padding: 30rpx;
}

.card-header {
	text-align: center;
	margin-bottom: 30rpx;
}

.card-title {
	font-size: 36rpx;
	font-weight: bold;
	color: #2c3e50;
	margin-bottom: 10rpx;
	display: block;
}

.card-subtitle {
	font-size: 28rpx;
	color: #7f8c8d;
	display: block;
}

.amount-section {
	margin-bottom: 30rpx;
}

.amount-label {
	font-size: 32rpx;
	font-weight: bold;
	color: #2c3e50;
	margin-bottom: 20rpx;
	display: block;
}

.quick-amount-buttons {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 15rpx;
	margin-bottom: 20rpx;
}

.quick-amount-button {
	background-color: #f8f9fa;
	border: 2rpx solid transparent;
	border-radius: 16rpx;
	padding: 20rpx;
	text-align: center;
	transition: all 0.3s ease;
}

.quick-amount-button.active {
	background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
	border-color: #ff6b35;
	transform: translateY(-2rpx);
	box-shadow: 0 4rpx 12rpx rgba(255, 107, 53, 0.3);
}

.button-text {
	font-size: 28rpx;
	color: #2c3e50;
	font-weight: 500;
}

.quick-amount-button.active .button-text {
	color: #ffffff;
	font-weight: 600;
}

.custom-amount {
	display: flex;
	align-items: center;
	background: rgba(255, 255, 255, 0.95);
	border: 2rpx solid #e0e0e0;
	border-radius: 16rpx;
	padding: 20rpx;
	transition: all 0.3s ease;
}

.custom-amount:focus-within {
	border-color: #ff6b35;
	box-shadow: 0 0 0 4rpx rgba(255, 107, 53, 0.1);
}

.currency {
	font-size: 36rpx;
	font-weight: bold;
	color: #2c3e50;
	margin-right: 15rpx;
}

.amount-input {
	flex: 1;
	font-size: 32rpx;
	color: #2c3e50;
}

.payment-section {
	margin-top: 40rpx;
}

.payment-button {
	width: 100%;
	background: linear-gradient(90deg, #ff9a9e 0%, #fad0c4 100%);
	border-radius: 30rpx;
	padding: 25rpx 0;
	font-size: 32rpx;
	font-weight: bold;
	color: #ffffff;
	box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.1);
	transition: all 0.3s ease;
}

.payment-button:active {
	transform: scale(0.98);
	box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.08);
}

/* --- 爱心榜模块 --- */
.love-rank-section {
	margin: 20rpx 30rpx;
	background-color: #ffffff;
	border-radius: 20rpx;
	box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.05);
	overflow: hidden;
}

.rank-list {
	padding: 20rpx 30rpx;
}

.rank-item {
	display: flex;
	align-items: center;
	padding: 20rpx 0;
	border-bottom: 1rpx solid #f0f0f0;
}

.rank-item:last-child {
	border-bottom: none;
}

.rank-number {
	width: 60rpx;
	height: 60rpx;
	border-radius: 50%;
	background-color: #f8f9fa;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 28rpx;
	font-weight: bold;
	color: #666;
	margin-right: 20rpx;
}

.rank-number.top-three {
	background: linear-gradient(135deg, #ffd700, #ffed4e);
	color: #ffffff;
}

.donor-avatar {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
	margin-right: 20rpx;
}

.donor-info {
	flex: 1;
}

.donor-name {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
	display: block;
	margin-bottom: 8rpx;
}

.donor-amount {
	font-size: 24rpx;
	color: #ff6b35;
	font-weight: bold;
}

.rank-badge {
	font-size: 32rpx;
}
</style>
