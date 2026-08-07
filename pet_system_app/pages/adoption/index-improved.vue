<template>
	<view class="adoption-container">
		<!-- 顶部栏 -->
		<view class="top-bar">
			<view class="brand-section">
				<image class="brand-icon" src="/static/pet-logo.jpeg" mode="aspectFit" />
				<text class="brand-name">宠物领养</text>
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
							<text class="swiper-title">{{ item.title || '宠物领养' }}</text>
							<text class="swiper-subtitle">{{ item.subtitle || '给流浪动物一个温暖的家' }}</text>
						</view>
					</view>
				</swiper-item>
			</swiper>
		</view>

		<!-- 领养统计模块 -->
		<view class="stats-section">
			<view class="section-header">
				<text class="section-title">📊 领养统计</text>
			</view>
			<view class="stats-grid">
				<view class="stat-card">
					<view class="stat-icon">🐾</view>
					<text class="stat-number">{{ totalPets }}</text>
					<text class="stat-label">待领养</text>
				</view>
				<view class="stat-card">
					<view class="stat-icon">🏠</view>
					<text class="stat-number">{{ adoptedPets }}</text>
					<text class="stat-label">已领养</text>
				</view>
				<view class="stat-card">
					<view class="stat-icon">❤️</view>
					<text class="stat-number">{{ happyFamilies }}</text>
					<text class="stat-label">幸福家庭</text>
				</view>
			</view>
		</view>

		<!-- 物种筛选 -->
		<view class="filter-section">
			<view class="section-header">
				<text class="section-title">🎯 选择宠物</text>
			</view>
			<view class="filter-tabs">
				<view class="filter-tab" 
					v-for="tab in filterTabs" 
					:key="tab.value" 
					:class="{ active: activeTab === tab.value }" 
					@click="handleTabChange(tab.value)">
					<text class="tab-text">{{ tab.label }}</text>
				</view>
			</view>
		</view>

		<!-- 搜索栏 -->
		<view class="search-section">
			<view class="search-bar">
				<text class="search-icon">🔍</text>
				<input v-model="searchKeyword" class="search-input" placeholder="搜索宠物名称或品种..." @confirm="handleSearch" />
			</view>
		</view>

		<!-- 宠物列表 -->
		<view class="pets-section">
			<view class="section-header">
				<text class="section-title">🐕 精选毛孩子</text>
				<text class="section-more" @click="toAllPets">查看全部</text>
			</view>
			<scroll-view scroll-y class="pet-list" :refresher-enabled="true" :refresher-triggered="refreshing"
				@refresherrefresh="onRefresh" @scrolltolower="onScrollToLower" :style="{ height: scrollViewHeight + 'px' }">
				
				<!-- 下拉刷新提示 -->
				<view v-if="refreshing" class="refresh-tips">
					<text>正在刷新...</text>
				</view>

				<view v-if="!loading && petList.length === 0" class="empty-state">
					<view class="empty-img">🐾</view>
					<text class="empty-text">暂无待领养宠物</text>
					<text class="empty-tip">下拉刷新试试</text>
				</view>

				<view class="pets-grid">
					<view v-for="pet in petList" :key="pet.petId" class="pet-card" @click="navigateToDetail(pet.petId)">
						<view class="pet-image-container">
							<image :src="getImageUrl(pet.images)" mode="aspectFill" class="pet-image" :draggable="false"
									@error="handleImageError" @load="handleImageLoad" />
							<view class="species-tag">
								<text class="species-text">{{ getSpeciesText(pet.species) }}</text>
							</view>
							<view class="age-tag">
								<text class="age-text">{{ pet.age || '?' }}个月</text>
							</view>
						</view>

						<view class="pet-info">
							<view class="pet-header">
								<text class="pet-name">{{ pet.name || '未命名' }}</text>
								<view class="gender-tag" :class="'gender-' + pet.gender">
									<text class="gender-text">{{ getGenderText(pet.gender) }}</text>
								</view>
							</view>

							<view v-if="pet.breed" class="pet-breed">
								<text class="breed-text">{{ pet.breed }}</text>
							</view>

							<view class="pet-description">
								<text class="description-text">{{ pet.description || '这个可爱的小家伙正在寻找一个温暖的家～' }}</text>
							</view>

							<view class="health-status">
								<view class="status-item" v-if="pet.immunity !== undefined && pet.immunity !== 2">
									<view class="status-icon immunity-icon">
										<text class="icon-text">💉</text>
									</view>
									<text class="status-text">{{ getImmunityText(pet.immunity) }}</text>
								</view>

								<view class="status-item" v-if="pet.neuter !== undefined && pet.neuter !== 2">
									<view class="status-icon neuter-icon">
										<text class="icon-text">✂️</text>
									</view>
									<text class="status-text">{{ getNeuterText(pet.neuter) }}</text>
								</view>

								<view class="status-item" v-if="pet.insectRepellent !== undefined && pet.insectRepellent !== 2">
									<view class="status-icon deworm-icon">
										<text class="icon-text">🐛</text>
									</view>
									<text class="status-text">{{ getInsectRepellentText(pet.insectRepellent) }}</text>
								</view>
							</view>

							<view class="adopt-btn">
								<text class="adopt-text">申请领养</text>
								<text class="adopt-arrow">→</text>
							</view>
						</view>
					</view>
				</view>

				<!-- 加载更多 -->
				<view v-if="loading && petList.length > 0" class="loading-more">
					<text class="loading-icon">⏳</text>
					<text class="loading-text">加载更多可爱宠物...</text>
				</view>

				<view v-if="noMore" class="no-more">
					<text class="end-icon">🎉</text>
					<text class="end-text">已经到底啦，去看看其他宠物吧！</text>
				</view>
			</scroll-view>
		</view>
	</view>
</template>

<script>
import { listPet } from '@/api/pet/adoption.js';
import { listCarousel } from '@/api/pet/carousel.js';
import config from '@/config';

export default {
	data() {
		return {
			current: 0,
			carouselList: [],
			
			// 筛选标签
			filterTabs: [
				{ label: '全部', value: null },
				{ label: '🐱 猫猫', value: 0 },
				{ label: '🐶 狗狗', value: 1 },
				{ label: '🐰 其他', value: 2 }
			],
			activeTab: null,
			searchKeyword: '',

			// 宠物列表
			petList: [],

			// 分页参数
			queryParams: {
				pageNum: 1,
				pageSize: 10
			},

			// 加载状态
			loading: false,
			refreshing: false,
			noMore: false,
			total: 0,

			// 滚动区域高度
			scrollViewHeight: 0,
			
			// 统计数据
			totalPets: 28,
			adoptedPets: 156,
			happyFamilies: 142
		};
	},

	onLoad() {
		this.loadPetList();
		this.calcScrollViewHeight();
		this.getCarouselList();
	},

	onShow() {
		this.calcScrollViewHeight();
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

		// 计算滚动区域高度
		calcScrollViewHeight() {
			uni.getSystemInfo({
				success: (res) => {
					// 减去顶部栏、轮播图、统计、筛选等区域的高度
					this.scrollViewHeight = res.windowHeight - 600;
				}
			});
		},

		// 加载宠物列表
		async loadPetList() {
			if (this.loading) return;

			this.loading = true;
			try {
				const params = {
					pageNum: this.queryParams.pageNum,
					pageSize: this.queryParams.pageSize,
					status: 0,
					species: this.activeTab,
					name: this.searchKeyword
				};

				const res = await listPet(params);
				if (res.code === 200) {
					const data = res.rows || res.data || [];

					if (this.queryParams.pageNum === 1) {
						this.petList = data;
					} else {
						this.petList = [...this.petList, ...data];
					}

					this.total = res.total || 0;
					this.noMore = this.petList.length >= this.total;
				} else {
					uni.showToast({
						title: res.msg || '加载失败',
						icon: 'none'
					});
				}
			} catch (error) {
				console.error('加载宠物列表失败:', error);
				uni.showToast({
					title: '加载失败',
					icon: 'none'
				});
			} finally {
				this.loading = false;
				this.refreshing = false;
			}
		},

		// 处理标签切换
		handleTabChange(tabValue) {
			this.activeTab = tabValue;
			this.queryParams.pageNum = 1;
			this.loadPetList();
		},

		// 搜索处理
		handleSearch() {
			this.queryParams.pageNum = 1;
			this.loadPetList();
		},

		// 下拉刷新
		onRefresh() {
			if (this.refreshing || this.loading) return;

			this.refreshing = true;
			this.queryParams.pageNum = 1;
			this.loadPetList();
		},

		// 滚动到底部加载更多
		onScrollToLower() {
			if (this.loading || this.noMore) return;

			this.queryParams.pageNum++;
			this.loadPetList();
		},

		// 跳转到详情页
		navigateToDetail(petId) {
			uni.navigateTo({
				url: `/pages/adoption/detail?id=${petId}`
			});
		},

		// 获取物种文本
		getSpeciesText(species) {
			const speciesMap = {
				0: '猫',
				1: '狗',
				2: '其他'
			};
			return speciesMap[species] || '宠物';
		},

		// 获取性别文本
		getGenderText(gender) {
			const genderMap = {
				0: '母',
				1: '公',
				2: '未知'
			};
			return genderMap[gender] || '未知';
		},

		// 获取免疫状态文本
		getImmunityText(immunity) {
			const immunityMap = {
				0: '已免疫',
				1: '未免疫',
				2: '免疫未知'
			};
			return immunityMap[immunity] || '免疫未知';
		},

		// 获取绝育状态文本
		getNeuterText(neuter) {
			const neuterMap = {
				0: '未绝育',
				1: '已绝育',
				2: '绝育未知'
			};
			return neuterMap[neuter] || '绝育未知';
		},

		// 获取驱虫状态文本
		getInsectRepellentText(insectRepellent) {
			const insectRepellentMap = {
				0: '未驱虫',
				1: '已驱虫',
				2: '驱虫未知'
			};
			return insectRepellentMap[insectRepellent] || '驱虫未知';
		},

		// 图片错误处理
		handleImageError(e) {
			console.error('图片加载失败:', e);
			e.currentTarget.src = '/static/images/default-pet.png';
			e.currentTarget.onerror = null;
		},

		// 图片加载成功
		handleImageLoad(e) {
			console.log('图片加载成功');
		},

		// 导航方法
		toNotifications() {
			uni.navigateTo({
				url: '/pages/notice/index'
			});
		},

		toAllPets() {
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
.adoption-container {
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
	background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
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

/* --- 筛选模块 --- */
.filter-section {
	margin: 20rpx 30rpx;
	background-color: #ffffff;
	border-radius: 20rpx;
	box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.05);
	overflow: hidden;
}

.filter-tabs {
	display: flex;
	justify-content: space-around;
	padding: 20rpx 30rpx;
	gap: 10rpx;
}

.filter-tab {
	padding: 12rpx 20rpx;
	border-radius: 20rpx;
	background: #f8f9fa;
	border: 2rpx solid transparent;
	transition: all 0.3s ease;
	flex: 1;
	text-align: center;
}

.filter-tab.active {
	background: linear-gradient(135deg, #ff6b35, #ff8e53);
	border-color: #ff6b35;
	transform: translateY(-2rpx);
	box-shadow: 0 4rpx 12rpx rgba(255, 107, 53, 0.3);
}

.tab-text {
	font-size: 24rpx;
	color: #666;
	font-weight: 500;
	transition: all 0.3s ease;
}

.filter-tab.active .tab-text {
	color: #ffffff;
	font-weight: 600;
}

/* --- 搜索模块 --- */
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
	border-color: #ff6b35;
	box-shadow: 0 0 0 4rpx rgba(255, 107, 53, 0.1);
}

.search-icon {
	font-size: 24rpx;
	color: #ff6b35;
	margin-right: 15rpx;
}

.search-input {
	flex: 1;
	font-size: 28rpx;
	color: #333;
}

/* --- 宠物列表模块 --- */
.pets-section {
	margin: 20rpx 30rpx;
	background-color: #ffffff;
	border-radius: 20rpx;
	box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.05);
	overflow: hidden;
	flex: 1;
}

.pet-list {
	padding: 20rpx 30rpx;
}

.refresh-tips {
	text-align: center;
	padding: 20rpx 0;
	color: #999;
	font-size: 24rpx;
}

.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 100rpx 0;
}

.empty-img {
	font-size: 120rpx;
	margin-bottom: 30rpx;
	opacity: 0.5;
}

.empty-text {
	font-size: 28rpx;
	color: #666;
	margin-bottom: 16rpx;
}

.empty-tip {
	font-size: 24rpx;
	color: #999;
}

.pets-grid {
	display: grid;
	grid-template-columns: repeat(2, 1fr);
	gap: 20rpx;
}

.pet-card {
	background: rgba(255, 255, 255, 0.95);
	border-radius: 24rpx;
	overflow: hidden;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
	backdrop-filter: blur(10px);
	border: 1rpx solid rgba(255, 255, 255, 0.2);
	transition: all 0.3s ease;
	display: flex;
	flex-direction: column;
}

.pet-card:active {
	transform: translateY(-5rpx);
	box-shadow: 0 12rpx 40rpx rgba(0, 0, 0, 0.15);
}

.pet-image-container {
	position: relative;
	width: 100%;
	height: 200rpx;
	overflow: hidden;
}

.pet-image {
	width: 100%;
	height: 100%;
	object-fit: cover;
	transition: transform 0.3s ease;
}

.pet-card:active .pet-image {
	transform: scale(1.05);
}

.species-tag {
	position: absolute;
	top: 12rpx;
	left: 12rpx;
	background: linear-gradient(135deg, #667eea, #764ba2);
	padding: 6rpx 16rpx;
	border-radius: 20rpx;
	backdrop-filter: blur(10px);
	box-shadow: 0 4rpx 15rpx rgba(102, 126, 234, 0.3);
}

.species-text {
	font-size: 20rpx;
	color: white;
	font-weight: bold;
}

.age-tag {
	position: absolute;
	top: 12rpx;
	right: 12rpx;
	background: rgba(255, 255, 255, 0.9);
	padding: 6rpx 16rpx;
	border-radius: 20rpx;
	backdrop-filter: blur(10px);
}

.age-text {
	font-size: 20rpx;
	color: #ff6b35;
	font-weight: bold;
}

.pet-info {
	padding: 20rpx;
	flex: 1;
	display: flex;
	flex-direction: column;
	gap: 12rpx;
}

.pet-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	gap: 12rpx;
}

.pet-name {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
	flex: 1;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.gender-tag {
	padding: 4rpx 12rpx;
	border-radius: 12rpx;
	backdrop-filter: blur(10px);
}

.gender-0 {
	background: linear-gradient(135deg, #ff9a9e, #fad0c4);
}

.gender-1 {
	background: linear-gradient(135deg, #a8edea, #fed6e3);
}

.gender-2 {
	background: linear-gradient(135deg, #d299c2, #fef9d7);
}

.gender-text {
	font-size: 20rpx;
	color: white;
	font-weight: bold;
}

.pet-breed {
	align-self: flex-start;
}

.breed-text {
	font-size: 22rpx;
	color: #666;
	background: rgba(102, 102, 102, 0.1);
	padding: 6rpx 12rpx;
	border-radius: 8rpx;
}

.pet-description {
	flex: 1;
}

.description-text {
	font-size: 24rpx;
	color: #666;
	line-height: 1.4;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
}

.health-status {
	display: flex;
	gap: 8rpx;
	margin-top: 8rpx;
}

.status-item {
	display: flex;
	align-items: center;
	gap: 6rpx;
	padding: 6rpx 10rpx;
	background: rgba(245, 245, 245, 0.8);
	border-radius: 12rpx;
	backdrop-filter: blur(10px);
	flex: 1;
	justify-content: center;
}

.status-icon {
	width: 24rpx;
	height: 24rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.icon-text {
	font-size: 16rpx;
}

.status-text {
	font-size: 18rpx;
	color: #666;
	font-weight: 500;
	white-space: nowrap;
}

.adopt-btn {
	background: linear-gradient(135deg, #ff6b35, #ff8e53);
	border-radius: 50rpx;
	padding: 12rpx 20rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 8rpx;
	box-shadow: 0 4rpx 15rpx rgba(255, 107, 53, 0.3);
	transition: all 0.3s ease;
	margin-top: 8rpx;
}

.adopt-btn:active {
	transform: scale(0.95);
	box-shadow: 0 2rpx 8rpx rgba(255, 107, 53, 0.3);
}

.adopt-text {
	font-size: 22rpx;
	color: white;
	font-weight: bold;
}

.adopt-arrow {
	font-size: 22rpx;
	color: white;
	font-weight: bold;
}

/* --- 加载状态 --- */
.loading-more,
.no-more {
	text-align: center;
	padding: 30rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 10rpx;
}

.loading-icon,
.end-icon {
	font-size: 32rpx;
}

.loading-text,
.end-text {
	font-size: 24rpx;
	color: #999;
}
</style>
