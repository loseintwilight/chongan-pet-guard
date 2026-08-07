<template>
	<view class="function-container">
		<!-- 顶部栏 -->
		<view class="top-bar">
			<view class="brand-section">
				<image class="brand-icon" src="/static/pet-logo.jpeg" mode="aspectFit" />
				<text class="brand-name">功能中心</text>
			</view>
			<view class="notification-section" @click="toNotifications">
				<image src="/static/notification-bell.png" mode="aspectFit" style="width: 24px; height: 24px" />
			</view>
		</view>

		<!-- 搜索栏 -->
		<view class="search-section">
			<view class="search-bar">
				<text class="search-icon">🔍</text>
				<input v-model="searchKeyword" class="search-input" placeholder="搜索功能服务..." @confirm="handleSearch" />
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
							<text class="swiper-title">{{ item.title || '宠物服务' }}</text>
							<text class="swiper-subtitle">{{ item.subtitle || '专业服务，贴心呵护' }}</text>
						</view>
					</view>
				</swiper-item>
			</swiper>
		</view>

		<!-- 主要服务分类 -->
		<view class="main-services-section">
			<view class="section-header">
				<text class="section-title">🎯 主要服务</text>
			</view>
			<view class="main-services-grid">
				<view class="main-service-card" @click="navigateTo('/pages/article/index')">
					<view class="service-icon-container">
						<image class="service-icon" src="/static/work/宠物科普.png" mode="aspectFit" />
					</view>
					<view class="service-info">
						<text class="service-title">宠物科普</text>
						<text class="service-subtitle">专业知识，科学养宠</text>
					</view>
					<view class="service-arrow">→</view>
				</view>

				<view class="main-service-card" @click="navigateTo('/pages/activity/index')">
					<view class="service-icon-container">
						<image class="service-icon" src="/static/work/活动推荐.png" mode="aspectFit" />
					</view>
					<view class="service-info">
						<text class="service-title">活动推荐</text>
						<text class="service-subtitle">精彩活动，丰富生活</text>
					</view>
					<view class="service-arrow">→</view>
				</view>

				<view class="main-service-card" @click="navigateTo('/pages/rescue/rescue')">
					<view class="service-icon-container">
						<image class="service-icon" src="/static/work/救助登记.png" mode="aspectFit" />
					</view>
					<view class="service-info">
						<text class="service-title">救助登记</text>
						<text class="service-subtitle">爱心救助，温暖守护</text>
					</view>
					<view class="service-arrow">→</view>
				</view>
			</view>
		</view>

		<!-- 功能分类网格 -->
		<view class="function-categories-section">
			<view class="section-header">
				<text class="section-title">📱 全部功能</text>
			</view>
			<view class="categories-grid">
				<view v-for="category in functionCategories" :key="category.id" class="category-card"
					@click="navigateTo(category.url)">
					<view class="category-icon-wrapper">
						<image :src="category.icon" class="category-icon" mode="aspectFit" />
					</view>
					<text class="category-title">{{ category.title }}</text>
					<text class="category-subtitle">{{ category.subtitle }}</text>
				</view>
			</view>
		</view>

		<!-- 推荐服务 -->
		<view class="recommended-section">
			<view class="section-header">
				<text class="section-title">⭐ 推荐服务</text>
				<text class="section-more" @click="toAllServices">查看全部</text>
			</view>
			<view class="recommended-grid">
				<view v-for="service in recommendedServices" :key="service.id" class="recommended-card"
					@click="navigateTo(service.url)">
					<view class="recommended-image-container">
						<image :src="service.image" class="recommended-image" mode="aspectFill" />
						<view class="recommended-badge" v-if="service.isHot">
							<text class="badge-text">热门</text>
						</view>
					</view>
					<view class="recommended-content">
						<text class="recommended-title">{{ service.title }}</text>
						<text class="recommended-description">{{ service.description }}</text>
						<view class="recommended-meta">
							<text class="meta-item">{{ service.users }}人使用</text>
							<text class="meta-rating">⭐ {{ service.rating }}</text>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 品牌合作 -->
		<view class="brands-section">
			<view class="section-header">
				<text class="section-title">🏆 品牌合作</text>
			</view>
			<view class="brands-carousel">
				<scroll-view scroll-x class="brands-scroll" show-scrollbar="false">
					<view v-for="brand in partnerBrands" :key="brand.id" class="brand-card">
						<image :src="brand.logo" class="brand-logo" mode="aspectFit" />
						<text class="brand-name">{{ brand.name }}</text>
					</view>
				</scroll-view>
			</view>
		</view>

		<!-- 健康推荐 -->
		<view class="health-recommend-section">
			<view class="section-header">
				<text class="section-title">💊 健康推荐</text>
			</view>
			<view class="health-grid">
				<view v-for="item in healthItems" :key="item.id" class="health-card" @click="navigateTo(item.url)">
					<view class="health-icon-container">
						<text class="health-icon">{{ item.icon }}</text>
					</view>
					<text class="health-title">{{ item.title }}</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		listCarousel
	} from '@/api/pet/carousel.js';
	import config from '@/config';

	export default {
		data() {
			return {
				current: 0,
				carouselList: [],
				searchKeyword: '',

				// 主要服务分类
				functionCategories: [{
						id: 1,
						title: '宠物领养',
						subtitle: '给毛孩子一个家',
						icon: '/static/work/宠物领养.png',
						url: '/pages/adoption/index'
					},
					{
						id: 2,
						title: '宠物托养',
						subtitle: '专业照护服务',
						icon: '/static/work/宠物托养.png',
						url: '/pages/boarding/index'
					},
					{
						id: 3,
						title: '爱心捐助',
						subtitle: '帮助流浪动物',
						icon: '/static/work/爱心捐助.png',
						url: '/pages/donation/index'
					},
					{
						id: 4,
						title: '附近店铺',
						subtitle: '便捷周边服务',
						icon: '/static/work/附近店铺.png',
						url: '/pages/store/stores'
					},
					{
						id: 5,
						title: '寻宠启事',
						subtitle: '寻找走失宠物',
						icon: '/static/work/寻宠启事.png',
						url: '/pages/lostPet/index'
					},
					{
						id: 6,
						title: '宠物商城',
						subtitle: '优质宠物用品',
						icon: '/static/work/宠物商城.png',
						url: '/pages/mall/index'
					}
				],

				// 推荐服务
				recommendedServices: [{
						id: 1,
						title: '宠物健康体检',
						description: '专业兽医团队，全面健康检查',
						image: '/static/work/健康体检.png',
						url: '/pages/care/index',
						users: '2.3k',
						rating: '4.9',
						isHot: true
					},
					{
						id: 2,
						title: '宠物美容护理',
						description: '专业美容师，让宠物更美丽',
						image: '/static/work/美容护理.png',
						url: '/pages/care/grooming',
						users: '1.8k',
						rating: '4.8',
						isHot: false
					},
					{
						id: 3,
						title: '宠物训练课程',
						description: '科学训练方法，培养好习惯',
						image: '/static/work/训练课程.png',
						url: '/pages/care/training',
						users: '956',
						rating: '4.7',
						isHot: false
					},
					{
						id: 4,
						title: '宠物医疗保险',
						description: '全面医疗保障，安心养宠',
						image: '/static/work/医疗保险.png',
						url: '/pages/care/insurance',
						users: '3.1k',
						rating: '4.9',
						isHot: true
					}
				],

				// 合作品牌
				partnerBrands: [{
						id: 1,
						name: '皇家',
						logo: '/static/brands/royal.png'
					},
					{
						id: 2,
						name: '冠能',
						logo: '/static/brands/eukanuba.png'
					},
					{
						id: 3,
						name: '希尔斯',
						logo: '/static/brands/hills.png'
					},
					{
						id: 4,
						name: '雀巢',
						logo: '/static/brands/nestle.png'
					},
					{
						id: 5,
						name: '伟嘉',
						logo: '/static/brands/whiskas.png'
					}
				],

				// 健康推荐
				healthItems: [{
						id: 1,
						title: '健康主粮',
						icon: '🍖',
						url: '/pages/shop/food'
					},
					{
						id: 2,
						title: '营养零食',
						icon: '🦴',
						url: '/pages/shop/snacks'
					},
					{
						id: 3,
						title: '护理用品',
						icon: '🧴',
						url: '/pages/shop/care'
					},
					{
						id: 4,
						title: '医疗保健',
						icon: '💊',
						url: '/pages/shop/medical'
					},
					{
						id: 5,
						title: '玩具用品',
						icon: '🎾',
						url: '/pages/shop/toys'
					},
					{
						id: 6,
						title: '清洁用品',
						icon: '🧹',
						url: '/pages/shop/cleaning'
					}
				]
			};
		},

		onLoad() {
			this.getCarouselList();
		},

		methods: {
			getCarouselList() {
				const params = {
					status: 1
				};
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

			handleSearch() {
				// 搜索功能逻辑
				console.log('搜索:', this.searchKeyword);
			},

			navigateTo(url) {
				uni.navigateTo({
					url: url
				});
			},

			toNotifications() {
				uni.navigateTo({
					url: '/pages/notice/index'
				});
			},

			toAllServices() {
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
	.function-container {
		display: flex;
		flex-direction: column;
		background: linear-gradient(135deg, #fff9e6 0%, #ffeaa7 100%);
		min-height: 100vh;
	}

	/* --- 顶部栏样式 --- */
	.top-bar {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 20rpx 30rpx;
		background: linear-gradient(90deg, #fdcb6e 0%, #e17055 100%);
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

	/* --- 搜索区域 --- */
	.search-section {
		margin: 20rpx 30rpx;
		background-color: #ffffff;
		border-radius: 20rpx;
		box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.05);
		padding: 20rpx 30rpx;
	}

	.search-bar {
		display: flex;
		align-items: center;
		background: rgba(248, 249, 250, 0.95);
		border-radius: 50rpx;
		padding: 15rpx 20rpx;
		border: 2rpx solid #e0e0e0;
		transition: all 0.3s ease;
	}

	.search-bar:focus-within {
		border-color: #e17055;
		box-shadow: 0 0 0 4rpx rgba(225, 112, 85, 0.1);
	}

	.search-icon {
		font-size: 24rpx;
		color: #e17055;
		margin-right: 15rpx;
	}

	.search-input {
		flex: 1;
		font-size: 28rpx;
		color: #333;
	}

	/* --- 轮播图区域 --- */
	.carousel-section {
		position: relative;
		margin: 20rpx 30rpx;

		.swiper-box {
			height: 300rpx;
		}

		.swiper-item {
			position: relative;
			width: 100%;
			height: 100%;
			overflow: hidden;
			border-radius: 20rpx;

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
			}

			.swiper-subtitle {
				font-size: 26rpx;
				opacity: 0.9;
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
		color: #e17055;
	}

	/* --- 主要服务模块 --- */
	.main-services-section {
		margin: 20rpx 30rpx;
		background-color: #ffffff;
		border-radius: 20rpx;
		box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.05);
		overflow: hidden;
	}

	.main-services-grid {
		display: flex;
		flex-direction: column;
		gap: 2rpx;
	}

	.main-service-card {
		display: flex;
		align-items: center;
		padding: 30rpx;
		background: rgba(255, 255, 255, 0.95);
		transition: all 0.3s ease;
		border-bottom: 1rpx solid #f8f9fa;
	}

	.main-service-card:last-child {
		border-bottom: none;
	}

	.main-service-card:active {
		background: #f8f9fa;
		transform: scale(0.98);
	}

	.service-icon-container {
		width: 80rpx;
		height: 80rpx;
		border-radius: 20rpx;
		background: linear-gradient(135deg, #fdcb6e, #e17055);
		display: flex;
		align-items: center;
		justify-content: center;
		margin-right: 20rpx;
	}

	.service-icon {
		width: 50rpx;
		height: 50rpx;
	}

	.service-info {
		flex: 1;
	}

	.service-title {
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
		display: block;
		margin-bottom: 8rpx;
	}

	.service-subtitle {
		font-size: 24rpx;
		color: #666;
		display: block;
	}

	.service-arrow {
		font-size: 32rpx;
		color: #e17055;
		font-weight: bold;
	}

	/* --- 功能分类网格 --- */
	.function-categories-section {
		margin: 20rpx 30rpx;
		background-color: #ffffff;
		border-radius: 20rpx;
		box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.05);
		overflow: hidden;
	}

	.categories-grid {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		gap: 20rpx;
		padding: 30rpx;
	}

	.category-card {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 30rpx 20rpx;
		background: rgba(255, 255, 255, 0.95);
		border-radius: 16rpx;
		box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.05);
		transition: all 0.3s ease;
	}

	.category-card:active {
		transform: translateY(-5rpx);
		box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.1);
	}

	.category-icon-wrapper {
		width: 80rpx;
		height: 80rpx;
		border-radius: 50%;
		background: linear-gradient(135deg, #74b9ff, #096dd9);
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 16rpx;
	}

	.category-icon {
		width: 50rpx;
		height: 50rpx;
	}

	.category-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 8rpx;
		text-align: center;
	}

	.category-subtitle {
		font-size: 22rpx;
		color: #666;
		text-align: center;
	}

	/* --- 推荐服务模块 --- */
	.recommended-section {
		margin: 20rpx 30rpx;
		background-color: #ffffff;
		border-radius: 20rpx;
		box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.05);
		overflow: hidden;
	}

	.recommended-grid {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		gap: 20rpx;
		padding: 30rpx;
	}

	.recommended-card {
		background: rgba(255, 255, 255, 0.95);
		border-radius: 16rpx;
		overflow: hidden;
		box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.05);
		transition: all 0.3s ease;
	}

	.recommended-card:active {
		transform: translateY(-5rpx);
		box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.1);
	}

	.recommended-image-container {
		position: relative;
		height: 180rpx;
		overflow: hidden;
	}

	.recommended-image {
		width: 100%;
		height: 100%;
		object-fit: cover;
	}

	.recommended-badge {
		position: absolute;
		top: 12rpx;
		left: 12rpx;
		background: linear-gradient(135deg, #ff6b6b, #ff8e53);
		padding: 6rpx 12rpx;
		border-radius: 12rpx;
	}

	.badge-text {
		font-size: 20rpx;
		color: white;
		font-weight: bold;
	}

	.recommended-content {
		padding: 20rpx;
	}

	.recommended-title {
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 8rpx;
	}

	.recommended-description {
		font-size: 24rpx;
		color: #666;
		line-height: 1.4;
		margin-bottom: 12rpx;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
		overflow: hidden;
	}

	.recommended-meta {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.meta-item {
		font-size: 22rpx;
		color: #999;
	}

	.meta-rating {
		font-size: 22rpx;
		color: #e17055;
	}

	/* --- 品牌合作模块 --- */
	.brands-section {
		margin: 20rpx 30rpx;
		background-color: #ffffff;
		border-radius: 20rpx;
		box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.05);
		overflow: hidden;
	}

	.brands-scroll {
		white-space: nowrap;
		padding: 20rpx 30rpx;
	}

	.brand-card {
		display: inline-flex;
		flex-direction: column;
		align-items: center;
		margin-right: 40rpx;
		width: 120rpx;
	}

	.brand-logo {
		width: 80rpx;
		height: 80rpx;
		border-radius: 12rpx;
		margin-bottom: 12rpx;
	}

	.brand-name {
		font-size: 24rpx;
		color: #666;
		text-align: center;
	}

	/* --- 健康推荐模块 --- */
	.health-recommend-section {
		margin: 20rpx 30rpx;
		background-color: #ffffff;
		border-radius: 20rpx;
		box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.05);
		overflow: hidden;
		padding-bottom: 30rpx;
	}

	.health-grid {
		display: grid;
		grid-template-columns: repeat(3, 1fr);
		gap: 20rpx;
		padding: 0 30rpx;
	}

	.health-card {
		display: flex;
		flex-direction: column;
		align-items: center;
		padding: 30rpx 20rpx;
		background: linear-gradient(135deg, #f8f9fa, #e9ecef);
		border-radius: 16rpx;
		transition: all 0.3s ease;
	}

	.health-card:active {
		transform: scale(0.95);
		background: linear-gradient(135deg, #e9ecef, #dee2e6);
	}

	.health-icon-container {
		width: 60rpx;
		height: 60rpx;
		border-radius: 50%;
		background: linear-gradient(135deg, #74b9ff, #096dd9);
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 16rpx;
	}

	.health-icon {
		font-size: 32rpx;
	}

	.health-title {
		font-size: 24rpx;
		color: #333;
		text-align: center;
	}
</style>