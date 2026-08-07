<template>
	<view class="activity-list-container">
		<!-- 顶部标题栏 -->
		<view class="header">
			<view class="header-content">
				<view class="title">🐾 宠物活动中心</view>
				<view class="subtitle">探索精彩活动，创造美好回忆</view>
			</view>
		</view>

		<!-- 活动类型筛选 -->
		<view class="filter-tabs">
			<view class="tab-item" :class="{ active: currentTab === 'all' }" @click="switchTab('all')">
				<text>全部活动</text>
			</view>
			<view class="tab-item" :class="{ active: currentTab === 'ongoing' }" @click="switchTab('ongoing')">
				<text>🔥 热门进行</text>
			</view>
			<view class="tab-item" :class="{ active: currentTab === 'upcoming' }" @click="switchTab('upcoming')">
				<text>⏰ 即将开始</text>
			</view>
			<view class="tab-item" :class="{ active: currentTab === 'ended' }" @click="switchTab('ended')">
				<text>📋 精彩回顾</text>
			</view>
		</view>

		<!-- 搜索栏 -->
		<view class="search-bar" v-if="showSearchBar">
			<view class="search-input-wrapper">
				<text class="search-icon">🔍</text>
				<input v-model="queryParams.title" class="search-input" placeholder="搜索你感兴趣的活动..." @confirm="handleQuery" @blur="hideSearch" />
				<text class="clear-icon" @click="clearSearch">✕</text>
			</view>
		</view>

		<!-- 活动列表 -->
		<view class="activities-list">
			<view class="activity-card" v-for="activity in filteredActivities" :key="activity.activityId" @click="navigateToDetail(activity)">
				<view class="activity-image-container">
					<image :src="getImageUrl(activity.images)" class="activity-image" mode="aspectFill" />
					<!-- 使用条件判断替代方法调用 -->
					<view class="activity-status status-upcoming" v-if="activity.status === 0">
						<text class="status-icon">⏰</text>
						<text class="status-text">即将开始</text>
					</view>
					<view class="activity-status status-ongoing" v-else-if="activity.status === 1">
						<text class="status-icon">🔥</text>
						<text class="status-text">热门进行</text>
					</view>
					<view class="activity-status status-ended" v-else-if="activity.status === 2">
						<text class="status-icon">📋</text>
						<text class="status-text">精彩回顾</text>
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
							<text class="info-icon">📅活动时间</text>
							<text class="info-text">{{ formatDate(activity.startTime) }} - {{ formatDate(activity.endTime) }}</text>
						</view>
						<view class="info-item">
							<text class="info-icon">📍活动地点</text>
							<text class="info-text">{{ activity.address }}</text>
						</view>
						<view class="info-item">
							<text class="info-icon">🎯目标人数</text>
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
import config from '@/config';

export default {
	data() {
		return {
			activityList: [],
			loadingStatus: 'more',
			page: 1,
			pageSize: 10,
			total: 0,
			showSearchBar: false,
			currentTab: 'all',
			queryParams: {
				pageNum: 1,
				pageSize: 10,
				title: '',
				status: ''
			},
			activityTypeDict: {} // 活动类型字典
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
	created() {
		this.loadDictData();
		this.getList();
	},
	methods: {
		/** 加载字典数据 */
		async loadDictData() {
			try {
				// 获取活动类型字典
				const typeRes = await getDicts('pet_activity_category');
				console.log('原始字典数据:', typeRes.data);

				this.activityTypeDict = {};
				typeRes.data.forEach((item) => {
					console.log('字典项:', item);

					// 关键修正：使用 dictValue 作为键
					const dictKey = item.dictValue;

					if (dictKey !== undefined && dictKey !== null) {
						this.activityTypeDict[dictKey] = {
							label: item.dictLabel,
							dictValue: item.dictValue,
							icon: this.getTypeIcon(item.dictValue)
						};
						console.log(`添加字典项: ${dictKey} ->${item.dictLabel}`);
					}
				});

				console.log('最终字典:', this.activityTypeDict);
			} catch (error) {
				console.error('加载活动类型字典失败:', error);
				uni.showToast({
					title: '加载字典数据失败',
					icon: 'none'
				});
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
						console.log('活动列表数据:', data);

						// 检查每个活动的activityCategory字段
						data.forEach((activity, index) => {
							console.log(`活动${index} - activityCategory:${activity.activityCategory}, 标题:${activity.title}`);
						});

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
			// 根据字典的 dictValue 映射图标
			const icons = {
				0: '🐾', // 宗教仪式
				1: '🎉', // 互动游戏
				2: '🎓', // 文化市集
				3: '👑' // 竞技活动
			};
			return icons[type] || '🐾';
		},

		/** 获取活动类型文本（从字典获取） */
		getActivityTypeText(activityCategoryId) {
			console.log('获取类型文本, activityCategoryId:', activityCategoryId, '字典:', this.activityTypeDict);

			// 安全检查
			if (activityCategoryId === undefined || activityCategoryId === null || activityCategoryId === '') {
				console.log('activityCategoryId为空，返回默认值');
				return '其他活动';
			}

			// 使用 dictValue 查找字典
			const typeInfo = this.activityTypeDict[activityCategoryId];
			if (typeInfo && typeInfo.label) {
				console.log('找到对应的类型:', typeInfo.label);
				return typeInfo.label;
			}

			console.log('未找到对应的类型，返回默认值');
			return '其他活动';
		},

		/** 切换标签 */
		switchTab(tab) {
			this.currentTab = tab;
			// 根据标签设置查询参数
			switch (tab) {
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

		/** 显示搜索栏 */
		showSearch() {
			this.showSearchBar = true;
		},

		/** 隐藏搜索栏 */
		hideSearch() {
			this.showSearchBar = false;
			this.queryParams = {
				pageNum: 1,
				pageSize: 10,
				title: '',
				status: this.queryParams.status
			};
			this.getList();
		},

		/** 清除搜索 */
		clearSearch() {
			this.queryParams.title = '';
			this.handleQuery();
		},

		/** 搜索按钮操作 */
		handleQuery() {
			this.page = 1;
			this.queryParams.pageNum = 1;
			this.activityList = [];
			this.getList();
		},

		/** 加载更多 */
		loadMore() {
			if (this.loadingStatus === 'more') {
				this.page++;
				this.queryParams.pageNum = this.page;
				this.getList();
			}
		},

		/** 跳转到活动详情 */
		navigateToDetail(activity) {
			uni.navigateTo({
				url: '/pages/activity/detail?id=' + activity.activityId
			});
		},

		/** 跳转到发布页面 */
		navigateToAdd() {
			uni.navigateTo({
				url: '/pages/activity/activity_add'
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
			this.loadMore();
		}
	}
};
</script>

<style lang="scss" scoped>
.activity-list-container {
	min-height: 100vh;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	padding: 20rpx;
	box-sizing: border-box;
	position: relative;

	&::before {
		content: '';
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: radial-gradient(circle at 20% 50%, rgba(120, 119, 198, 0.3) 0%, transparent 50%),
			radial-gradient(circle at 80% 80%, rgba(255, 119, 198, 0.3) 0%, transparent 50%), radial-gradient(circle at 40% 20%, rgba(255, 219, 118, 0.2) 0%, transparent 50%);
		pointer-events: none;
	}
}

/* 顶部标题栏 */
.header {
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20px);
	border-radius: 24rpx;
	padding: 30rpx;
	margin-bottom: 20rpx;
	display: flex;
	justify-content: space-between;
	align-items: center;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.15);
	border: 1rpx solid rgba(255, 255, 255, 0.2);
	position: relative;
	z-index: 10;
}

.header-content {
	text-align: center;
	flex: 1;
}

.title {
	font-size: 36rpx;
	font-weight: bold;
	background: linear-gradient(135deg, #667eea, #764ba2);
	-webkit-background-clip: text;
	-webkit-text-fill-color: transparent;
	background-clip: text;
	margin-bottom: 8rpx;
}

.subtitle {
	font-size: 24rpx;
	color: #666;
	opacity: 0.8;
}

.header-actions {
	display: flex;
	gap: 15rpx;
}

.action-btn {
	background: linear-gradient(135deg, #667eea, #764ba2);
	border-radius: 50rpx;
	padding: 12rpx 20rpx;
	display: flex;
	align-items: center;
	gap: 8rpx;
	transition: all 0.3s ease;
	box-shadow: 0 4rpx 15rpx rgba(102, 126, 234, 0.3);
}

.action-btn:active {
	transform: scale(0.95);
	box-shadow: 0 2rpx 8rpx rgba(102, 126, 234, 0.3);
}

.icon {
	font-size: 22rpx;
}

.label {
	font-size: 22rpx;
	color: white;
	font-weight: 500;
}

/* 筛选标签 */
.filter-tabs {
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20px);
	border-radius: 24rpx;
	padding: 12rpx;
	margin-bottom: 20rpx;
	display: flex;
	justify-content: space-around;
	box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
	border: 1rpx solid rgba(255, 255, 255, 0.2);
	position: relative;
	z-index: 10;
}

.tab-item {
	padding: 12rpx 24rpx;
	border-radius: 20rpx;
	font-size: 24rpx;
	color: #666;
	transition: all 0.3s ease;
	font-weight: 500;
}

.tab-item.active {
	background: linear-gradient(135deg, #667eea, #764ba2);
	color: white;
	box-shadow: 0 4rpx 15rpx rgba(102, 126, 234, 0.3);
	transform: translateY(-2rpx);
}

/* 搜索栏 */
.search-bar {
	margin-bottom: 20rpx;
	position: relative;
	z-index: 10;
}

.search-input-wrapper {
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20px);
	border-radius: 50rpx;
	padding: 15rpx 20rpx;
	display: flex;
	align-items: center;
	box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
	border: 1rpx solid rgba(255, 255, 255, 0.2);
}

.search-icon {
	font-size: 24rpx;
	color: #667eea;
	margin-right: 15rpx;
}

.search-input {
	flex: 1;
	font-size: 28rpx;
	color: #333;
}

.clear-icon {
	font-size: 24rpx;
	color: #999;
	margin-left: 10rpx;
}

/* 活动列表 */
.activities-list {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
	position: relative;
	z-index: 10;
}

.activity-card {
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20px);
	border-radius: 24rpx;
	overflow: hidden;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.15);
	transition: all 0.3s ease;
	border: 1rpx solid rgba(255, 255, 255, 0.2);
}

.activity-card:active {
	transform: translateY(-5rpx);
	box-shadow: 0 12rpx 40rpx rgba(0, 0, 0, 0.2);
}

.activity-image-container {
	position: relative;
	height: 320rpx;
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

.price-info {
	display: flex;
	flex-direction: column;
	gap: 4rpx;
}

.price-label {
	font-size: 22rpx;
	color: #999;
}

.price-value {
	font-size: 28rpx;
	font-weight: bold;
	color: #ff6b6b;
}

.price-value.free {
	color: #4caf50;
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

/* 加载更多 */
.load-more {
	text-align: center;
	padding: 30rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 10rpx;
	position: relative;
	z-index: 10;
}

.loading-icon,
.end-icon {
	font-size: 32rpx;
}

.loading-text,
.end-text {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.8);
}
</style>
