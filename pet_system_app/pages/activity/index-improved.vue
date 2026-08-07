<template>
	<view class="activity-container">
		<!-- 顶部栏 -->
		<view class="top-bar">
			<view class="brand-section">
				<image class="brand-icon" src="/static/pet-logo.jpeg" mode="aspectFit" />
				<text class="brand-name">活动中心</text>
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
							<text class="swiper-title">{{ item.title || '精彩活动' }}</text>
							<text class="swiper-subtitle">{{ item.subtitle || '探索精彩活动，创造美好回忆' }}</text>
						</view>
					</view>
				</swiper-item>
			</swiper>
		</view>

		<!-- 活动统计模块 -->
		<view class="stats-section">
			<view class="section-header">
				<text class="section-title">📊 活动统计</text>
			</view>
			<view class="stats-grid">
				<view class="stat-card">
					<view class="stat-icon">🎉</view>
					<text class="stat-number">{{ totalActivities }}</text>
					<text class="stat-label">总活动数</text>
				</view>
				<view class="stat-card">
					<view class="stat-icon">👥</view>
					<text class="stat-number">{{ totalParticipants }}</text>
					<text class="stat-label">参与人数</text>
				</view>
				<view class="stat-card">
					<view class="stat-icon">🔥</view>
					<text class="stat-number">{{ ongoingActivities }}</text>
					<text class="stat-label">进行中</text>
				</view>
			</view>
		</view>

		<!-- 活动类型筛选 -->
		<view class="filter-section">
			<view class="section-header">
				<text class="section-title">🎯 活动类型</text>
			</view>
			<view class="filter-tabs">
				<view class="filter-tab" 
					v-for="tab in filterTabs" 
					:key="tab.value" 
					:class="{ active: currentTab === tab.value }" 
					@click="handleTabChange(tab.value)">
					<text class="tab-text">{{ tab.label }}</text>
				</view>
			</view>
		</view>

		<!-- 搜索栏 -->
		<view class="search-section">
			<view class="search-bar">
				<text class="search-icon">🔍</text>
				<input v-model="queryParams.title" class="search-input" placeholder="搜索你感兴趣的活动..." @confirm="handleQuery" />
			</view>
		</view>

		<!-- 活动列表 -->
		<view class="activities-section">
			<view class="section-header">
				<text class="section-title">📅 活动列表</text>
				<text class="section-more" @click="toAllActivities">查看全部</text>
			</view>
			<view class="activities-list">
				<view class="activity-card" v-for="activity in filteredActivities" :key="activity.activityId" @click="navigateToDetail(activity)">
					<view class="activity-image-container">
						<image :src="getImageUrl(activity.images)" class="activity-image" mode="aspectFill" />
						<view class="activity-status" :class="'status-' + getStatusClass(activity.status)">
							<text class="status-icon">{{ getStatusIcon(activity.status) }}</text>
							<text class="status-text">{{ getStatusText(activity.status) }}</text>
						</view>
						<view class="activity-type">
							<text class="type-icon">{{ getTypeIcon(activity.activityCategory) }}</text>
							<text class="type-text">{{ getActivityTypeText(activity.activityCategory) }}</text>
						</view>
						<!-- 参与人数指示器 -->
						<view class="participants-indicator" v-if="activity.count > 0">
							<text class="participants-icon">👥</text>
							<text class="participants-text">{{ activity.count }}人参与</text>
						</view>
					</view>
					<view class="activity-content">
						<view class="activity-title">{{ activity.title }}</view>
						<view class="activity-summary">{{ activity.summary }}</view>
						<view class="activity-info">
							<view class="info-item">
								<text class="info-icon">📅</text>
								<text class="info-text">{{ formatDate(activity.startTime) }} - {{ formatDate(activity.endTime) }}</text>
							</view>
							<view class="info-item">
								<text class="info-icon">📍</text>
								<text class="info-text">{{ activity.address }}</text>
							</view>
							<view class="info-item">
								<text class="info-icon">🎯</text>
								<text class="info-text">限额{{ activity.personCount }}人</text>
							</view>
						</view>
						<view class="activity-footer">
							<view class="join-btn">
								<text class="join-text">立即报名</text>
								<text class="join-arrow">→</text>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 加载更多 -->
		<view class="load-more" v-if="loadingStatus === 'more'">
			<text class="loading-icon">⏳</text>
			<text class="loading-text">加载更多精彩活动...</text>
		</view>
		<view class="load-more" v-if="loadingStatus === 'noMore'">
			<text class="end-icon">🎉</text>
			<text class="end-text">已经到底啦，去看看其他活动吧！</text>
		</view>
	</view>
</template>

<script>
import { listActivity } from '@/api/pet/activity';
import { getDicts } from '@/api/system/dict/data';
import { listCarousel } from '@/api/pet/carousel.js';
import config from '@/config';

export default {
	data() {
		return {
			current: 0,
			carouselList: [],
			activityList: [],
			loadingStatus: 'more',
			page: 1,
			pageSize: 10,
			total: 0,
			currentTab: 'all',
			queryParams: {
				pageNum: 1,
				pageSize: 10,
				title: '',
				status: ''
			},
			activityTypeDict: {},
			
			// 筛选标签
			filterTabs: [
				{ label: '全部活动', value: 'all' },
				{ label: '🔥 热门进行', value: 'ongoing' },
				{ label: '⏰ 即将开始', value: 'upcoming' },
				{ label: '📋 精彩回顾', value: 'ended' }
			],
			
			// 统计数据
			totalActivities: 45,
			totalParticipants: '1,234',
			ongoingActivities: 8
		};
	},
	computed: {
		/** 根据当前标签过滤活动 */
		filteredActivities() {
			if (this.currentTab === 'all') {
				return this.activityList;
			}
			return this.activityList.filter((activity) => {
				switch (this.currentTab) {
					case 'ongoing':
						return activity.status === 1;
					case 'upcoming':
						return activity.status === 0;
					case 'ended':
						return activity.status === 2;
					default:
						return true;
				}
			});
		}
	},
	onLoad() {
		this.loadDictData();
		this.getList();
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
		
		/** 加载字典数据 */
		async loadDictData() {
			try {
				const typeRes = await getDicts('pet_activity_category');
				this.activityTypeDict = {};
				typeRes.data.forEach((item) => {
					const dictKey = item.dictValue;
					if (dictKey !== undefined && dictKey !== null) {
						this.activityTypeDict[dictKey] = {
							label: item.dictLabel,
							dictValue: item.dictValue,
							icon: this.getTypeIcon(item.dictValue)
						};
					}
				});
			} catch (error) {
				console.error('加载活动类型字典失败:', error);
			}
		},

		/** 查询活动列表 */
		getList() {
			this.loadingStatus = 'loading';
			this.queryParams.pageNum = this.page;
			listActivity(this.queryParams)
				.then((response) => {
					if (response.code === 200) {
						const data = response.rows || [];
						if (this.page === 1) {
							this.activityList = data;
						} else {
							this.activityList = [...this.activityList, ...data];
						}
						this.total = response.total;
						this.loadingStatus = this.activityList.length >= response.total ? 'noMore' : 'more';
					} else {
						uni.showToast({
							title: '加载失败: ' + (response.msg || '未知错误'),
							icon: 'none'
						});
						this.loadingStatus = 'noMore';
					}
				})
				.catch((error) => {
					uni.showToast({
						title: '加载失败: ' + (error.message || '网络错误'),
						icon: 'none'
					});
					console.error('加载活动列表失败:', error);
					this.loadingStatus = 'noMore';
				});
		},

		/** 获取类型图标 */
		getTypeIcon(type) {
			const icons = {
				0: '🐾',
				1: '🎉',
				2: '🎓',
				3: '👑'
			};
			return icons[type] || '🐾';
		},

		/** 获取活动类型文本 */
		getActivityTypeText(activityCategoryId) {
			if (activityCategoryId === undefined || activityCategoryId === null || activityCategoryId === '') {
				return '其他活动';
			}
			const typeInfo = this.activityTypeDict[activityCategoryId];
			if (typeInfo && typeInfo.label) {
				return typeInfo.label;
			}
			return '其他活动';
		},

		/** 获取状态样式类 */
		getStatusClass(status) {
			const statusMap = {
				0: 'upcoming',
				1: 'ongoing',
				2: 'ended'
			};
			return statusMap[status] || 'upcoming';
		},

		/** 获取状态图标 */
		getStatusIcon(status) {
			const iconMap = {
				0: '⏰',
				1: '🔥',
				2: '📋'
			};
			return iconMap[status] || '⏰';
		},

		/** 获取状态文本 */
		getStatusText(status) {
			const textMap = {
				0: '即将开始',
				1: '热门进行',
				2: '精彩回顾'
			};
			return textMap[status] || '即将开始';
		},

		/** 切换标签 */
		handleTabChange(tabValue) {
			this.currentTab = tabValue;
			switch (tabValue) {
				case 'ongoing':
					this.queryParams.status = 1;
					break;
				case 'upcoming':
					this.queryParams.status = 0;
					break;
				case 'ended':
					this.queryParams.status = 2;
					break;
				default:
					this.queryParams.status = '';
			}
			this.page = 1;
			this.activityList = [];
			this.getList();
		},

		/** 搜索按钮操作 */
		handleQuery() {
			this.page = 1;
			this.queryParams.pageNum = 1;
			this.activityList = [];
			this.getList();
		},

		/** 跳转到活动详情 */
		navigateToDetail(activity) {
			uni.navigateTo({
				url: '/pages/activity/detail?id=' + activity.activityId
			});
		},

		/** 获取图片URL */
		getImageUrl(fileName) {
			if (!fileName) return '/static/images/activity-placeholder.png';
			return config.getDownloadUrl(fileName);
		},

		/** 格式化日期 */
		formatDate(dateString) {
			if (!dateString) return '时间待定';
			const date = new Date(dateString);
			const month = date.getMonth() + 1;
			const day = date.getDate();
			const hours = date.getHours();
			const minutes = date.getMinutes().toString().padStart(2, '0');
			return `${month}月${day}日 ${hours}:${minutes}`;
		},

		/** 轮播图切换 */
		changeSwiper(e) {
			this.current = e.detail.current;
		},

		/** 下拉刷新 */
		onPullDownRefresh() {
			this.page = 1;
			this.queryParams.pageNum = 1;
			this.getList().finally(() => {
				uni.stopPullDownRefresh();
			});
		},

		/** 滚动到底部 */
		onReachBottom() {
			if (this.loadingStatus === 'more') {
				this.page++;
				this.queryParams.pageNum = this.page;
				this.getList();
			}
		},

		// 导航方法
		toNotifications() {
			uni.navigateTo({
				url: '/pages/notice/index'
			});
		},

		toAllActivities() {
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
.activity-container {
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
	background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
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

/* --- 活动列表模块 --- */
.activities-section {
	margin: 20rpx 30rpx;
	background-color: #ffffff;
	border-radius: 20rpx;
	box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.05);
	overflow: hidden;
}

.activities-list {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
	padding: 20rpx 30rpx;
}

.activity-card {
	background: rgba(255, 255, 255, 0.95);
	border-radius: 24rpx;
	overflow: hidden;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
	backdrop-filter: blur(10px);
	border: 1rpx solid rgba(255, 255, 255, 0.2);
	transition: all 0.3s ease;
}

.activity-card:active {
	transform: translateY(-5rpx);
	box-shadow: 0 12rpx 40rpx rgba(0, 0, 0, 0.15);
}

.activity-image-container {
	position: relative;
	height: 300rpx;
	overflow: hidden;
}

.activity-image {
	width: 100%;
	height: 100%;
	object-fit: cover;
	transition: transform 0.3s ease;
}

.activity-card:active .activity-image {
	transform: scale(1.05);
}

.activity-status {
	position: absolute;
	top: 20rpx;
	right: 20rpx;
	padding: 10rpx 16rpx;
	border-radius: 20rpx;
	display: flex;
	align-items: center;
	gap: 8rpx;
	backdrop-filter: blur(10px);
	box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.2);
}

.status-icon {
	font-size: 20rpx;
}

.status-text {
	font-size: 22rpx;
	font-weight: bold;
	color: white;
}

.status-upcoming {
	background: linear-gradient(135deg, #2196f3, #64b5f6);
}

.status-ongoing {
	background: linear-gradient(135deg, #ff6b6b, #ff8e53);
}

.status-ended {
	background: linear-gradient(135deg, #9e9e9e, #bdbdbd);
}

.activity-type {
	position: absolute;
	top: 20rpx;
	left: 20rpx;
	background: linear-gradient(135deg, #667eea, #764ba2);
	padding: 10rpx 16rpx;
	border-radius: 20rpx;
	display: flex;
	align-items: center;
	gap: 8rpx;
	backdrop-filter: blur(10px);
	box-shadow: 0 4rpx 15rpx rgba(102, 126, 234, 0.3);
}

.type-icon {
	font-size: 20rpx;
}

.type-text {
	font-size: 22rpx;
	color: white;
	font-weight: bold;
}

.participants-indicator {
	position: absolute;
	bottom: 20rpx;
	left: 20rpx;
	background: rgba(0, 0, 0, 0.6);
	backdrop-filter: blur(10px);
	padding: 8rpx 12rpx;
	border-radius: 15rpx;
	display: flex;
	align-items: center;
	gap: 6rpx;
}

.participants-icon {
	font-size: 18rpx;
}

.participants-text {
	font-size: 20rpx;
	color: white;
	font-weight: 500;
}

.activity-content {
	padding: 25rpx;
}

.activity-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 12rpx;
	line-height: 1.3;
}

.activity-summary {
	font-size: 26rpx;
	color: #666;
	line-height: 1.5;
	margin-bottom: 20rpx;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
}

.activity-info {
	display: flex;
	flex-direction: column;
	gap: 12rpx;
	margin-bottom: 20rpx;
}

.info-item {
	display: flex;
	align-items: center;
	gap: 12rpx;
}

.info-icon {
	font-size: 24rpx;
	color: #667eea;
}

.info-text {
	font-size: 24rpx;
	color: #666;
}

.activity-footer {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding-top: 15rpx;
	border-top: 1rpx solid rgba(0, 0, 0, 0.05);
}

.join-btn {
	background: linear-gradient(135deg, #667eea, #764ba2);
	border-radius: 50rpx;
	padding: 12rpx 24rpx;
	display: flex;
	align-items: center;
	gap: 8rpx;
	box-shadow: 0 4rpx 15rpx rgba(102, 126, 234, 0.3);
	transition: all 0.3s ease;
}

.join-btn:active {
	transform: scale(0.95);
	box-shadow: 0 2rpx 8rpx rgba(102, 126, 234, 0.3);
}

.join-text {
	font-size: 24rpx;
	color: white;
	font-weight: bold;
}

.join-arrow {
	font-size: 24rpx;
	color: white;
	font-weight: bold;
}

/* --- 加载更多 --- */
.load-more {
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
