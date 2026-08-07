<template>
	<view class="my-lost-page">
		<!-- 页面头部 -->
		<view class="header">
			<text class="title">我的丢失</text>
		</view>

		<!-- 内容区域 -->
		<scroll-view class="content" scroll-y refresher-enabled @refresherrefresh="onRefresh" @scrolltolower="onLoadMore" lower-threshold="200">
			<!-- 加载提示 -->
			<view v-if="loading" class="loading-container">
				<uni-spinner type="scale"></uni-spinner>
				<text class="loading-text">加载中...</text>
			</view>

			<!-- 列表内容 -->
			<view v-if="lostPetList.length > 0" class="lost-pet-list">
				<view v-for="item in lostPetList" :key="item.lostId" class="lost-pet-item" :class="{ 'fade-in': true }">
					<!-- 宠物图片 -->
					<view class="pet-image">
						<image :src="getImageUrl(item.images)" mode="aspectFill" @error="handleImageError($event)"></image>
					</view>

					<!-- 宠物信息 -->
					<view class="pet-info">
						<!-- 宠物名称和状态 -->
						<view class="pet-header">
							<text class="pet-name">{{ item.petName }}</text>
							<view :class="['status-tag', item.status === 0 ? 'searching' : 'found']">
								{{ item.status === 0 ? '寻找中' : '已找到' }}
							</view>
						</view>

						<!-- 丢失时间 -->
						<view class="pet-detail">
							<text class="label">丢失时间：</text>
							<text class="value">{{ formatDate(item.lostTime) }}</text>
						</view>

						<!-- 丢失地点 -->
						<view class="pet-detail">
							<text class="label">丢失地点：</text>
							<text class="value">{{ item.lostLocation }}</text>
						</view>

						<!-- 酬谢金额 -->
						<view v-if="item.reward > 0" class="pet-detail">
							<text class="label">酬谢金额：</text>
							<text class="reward-value">¥{{ item.reward }}</text>
						</view>

						<!-- 操作按钮 -->
						<view class="action-buttons">
							<button size="mini" type="primary" @click="navigateToDetail(item.lostId)">详情</button>
							<button size="mini" type="default" @click="navigateToEdit(item.lostId)">编辑</button>
							<button size="mini" type="warn" @click="deleteLostPet(item.lostId)">删除</button>
						</view>
					</view>
				</view>
			</view>

			<!-- 空数据提示 -->
			<view v-else-if="!loading" class="empty-container">
				<image src="/static/images/entrust01.png" class="empty-image"></image>
				<text class="empty-text">暂无丢失宠物信息</text>
				<text class="empty-subtext">添加您的丢失宠物信息，让更多人帮助寻找</text>
				<button type="primary" class="empty-button" @click="navigateToPublish">发布丢失信息</button>
			</view>

			<!-- 加载更多提示 -->
			<view v-if="!loading && !noMore && lostPetList.length > 0" class="load-more-container">
				<uni-spinner type="circle"></uni-spinner>
				<text class="load-more-text">加载更多...</text>
			</view>

			<!-- 没有更多数据提示 -->
			<view v-if="noMore" class="no-more-container">
				<text class="no-more-text">没有更多数据了</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
import { listLostPet, delLostPet } from '@/api/pet/lostPet.js';
import { getToken } from '@/utils/auth.js';
import storage from '@/utils/storage.js';
import constant from '@/utils/constant.js';
import store from '@/store/index.js';
import config from '@/config.js';

export default {
	data() {
		return {
			lostPetList: [], // 丢失宠物列表
			queryParams: {
				// 查询参数
				pageNum: 1, // 当前页码
				pageSize: 4, // 每页数量 - 初始加载4条
				userId: '' // 用户ID
			},
			loading: false, // 加载状态
			refreshing: false, // 刷新状态
			noMore: false, // 是否没有更多数据
			total: 0, // 总记录数
			scrollViewHeight: 0, // 滚动区域高度
			userInfo: null // 用户信息
		};
	},

	onLoad() {
		// 检查登录状态
		this.checkLogin();

		// 计算滚动区域高度
		this.calcScrollViewHeight();
	},

	methods: {
		/**
		 * 检查登录状态
		 */
		checkLogin() {
			const token = getToken();
			if (!token) {
				// 未登录，跳转到登录页面
				uni.showToast({
					title: '请先登录',
					icon: 'none',
					duration: 2000
				});

				setTimeout(() => {
					uni.navigateTo({
						url: '/pages/login'
					});
				}, 2000);
				return;
			}

			// 获取用户ID
			const userId = storage.get(constant.id);
			if (userId) {
				this.queryParams.userId = userId;
				// 加载数据
				this.loadLostPetList();
			} else {
				// 尝试从 store 获取用户信息
				this.getUserInfoFromStore();
			}
		},

		/**
		 * 从 store 获取用户信息
		 */
		getUserInfoFromStore() {
			store
				.dispatch('GetInfo')
				.then(() => {
					const userId = storage.get(constant.id);
					if (userId) {
						this.queryParams.userId = userId;
						this.loadLostPetList();
					} else {
						this.showLoginError();
					}
				})
				.catch(() => {
					this.showLoginError();
				});
		},

		/**
		 * 显示登录错误提示
		 */
		showLoginError() {
			uni.showToast({
				title: '获取用户信息失败，请重新登录',
				icon: 'none',
				duration: 2000
			});

			setTimeout(() => {
				uni.navigateTo({
					url: '/pages/login'
				});
			}, 2000);
		},

		/**
		 * 计算滚动区域高度
		 */
		calcScrollViewHeight() {
			// 获取系统信息
			uni.getSystemInfo({
				success: (res) => {
					// 导航栏高度 + 状态栏高度
					const navBarHeight = res.statusBarHeight + 44;
					// 滚动区域高度 = 屏幕高度 - 导航栏高度 - 底部安全区域高度
					this.scrollViewHeight = res.windowHeight - navBarHeight;
				}
			});
		},

		/**
		 * 加载丢失宠物列表
		 */
		async loadLostPetList() {
			if (this.loading) return;

			try {
				this.loading = true;

				// 调用API获取数据
				const res = await listLostPet(this.queryParams);

				if (res.code === 200) {
					// 处理返回数据
					const data = res.rows || [];
					this.total = res.total;

					if (this.queryParams.pageNum === 1) {
						// 第一页，直接替换数据
						this.lostPetList = data;
					} else {
						// 非第一页，追加数据
						this.lostPetList = this.lostPetList.concat(data);
					}

					// 判断是否还有更多数据
					this.noMore = this.lostPetList.length >= this.total;
				} else {
					uni.showToast({
						title: '获取数据失败',
						icon: 'none',
						duration: 2000
					});
				}
			} catch (error) {
				console.error('加载丢失宠物列表失败:', error);
				uni.showToast({
					title: '网络异常，请稍后重试',
					icon: 'none',
					duration: 2000
				});
			} finally {
				this.loading = false;
				this.refreshing = false;
			}
		},

		/**
		 * 下拉刷新
		 */
		onRefresh() {
			this.refreshing = true;
			this.queryParams.pageNum = 1;
			this.noMore = false;

			// 确保有用户ID
			const userId = storage.get(constant.id);
			if (userId) {
				this.queryParams.userId = userId;
				this.loadLostPetList();
			} else {
				// 尝试重新获取用户信息
				store
					.dispatch('GetInfo')
					.then(() => {
						this.queryParams.userId = storage.get(constant.id);
						this.loadLostPetList();
					})
					.catch(() => {
						this.refreshing = false;
						uni.showToast({
							title: '获取用户信息失败',
							icon: 'none',
							duration: 2000
						});
					});
			}
		},

		/**
		 * 上拉加载更多
		 */
		onLoadMore() {
			// 防止重复加载、刷新时加载或没有更多数据时加载
			if (this.loading || this.refreshing || this.noMore) return;

			this.queryParams.pageNum++;
			this.loadLostPetList();
		},

		/**
		 * 跳转到详情页
		 */
		navigateToDetail(lostId) {
			uni.navigateTo({
				url: `/pages/search/detail?lostId=${lostId}`
			});
		},

		/**
		 * 跳转到编辑页
		 */
		navigateToEdit(lostId) {
			uni.navigateTo({
				url: `/pages/search/update?lostId=${lostId}`
			});
		},

		/**
		 * 删除丢失宠物信息
		 */
		async deleteLostPet(lostId) {
			// 确认删除
			uni.showModal({
				title: '确认删除',
				content: '确定要删除这条丢失宠物信息吗？',
				success: async (res) => {
					if (res.confirm) {
						try {
							const result = await delLostPet(lostId);

							if (result.code === 200) {
								uni.showToast({
									title: '删除成功',
									icon: 'success',
									duration: 2000
								});

								// 重新加载数据
								this.queryParams.pageNum = 1;
								this.loadLostPetList();
							} else {
								uni.showToast({
									title: '删除失败',
									icon: 'none',
									duration: 2000
								});
							}
						} catch (error) {
							console.error('删除丢失宠物信息失败:', error);
							uni.showToast({
								title: '网络异常，请稍后重试',
								icon: 'none',
								duration: 2000
							});
						}
					}
				}
			});
		},

		/**
		 * 格式化日期
		 */
		formatDate(date) {
			if (!date) return '';

			const d = new Date(date);
			const year = d.getFullYear();
			const month = String(d.getMonth() + 1).padStart(2, '0');
			const day = String(d.getDate()).padStart(2, '0');

			return `${year}-${month}-${day}`;
		},

		/**
		 * 获取完整的图片URL
		 */
		getImageUrl(fileName) {
			// 如果获取失败，返回默认图片
			return config.getDownloadUrl(fileName) || '/static/images/entrust01.png';
		},

		/**
		 * 处理图片加载错误
		 */
		handleImageError(e) {
			console.error('图片加载失败:', e);
			e.currentTarget.src = '/static/images/entrust01.png';
			e.currentTarget.onerror = null;
		},

		/**
		 * 跳转到发布页面
		 */
		navigateToPublish() {
			uni.navigateTo({
				url: '/pages/lostPet/registration'
			});
		}
	}
};
</script>

<style scoped>
/* 页面容器 */
.my-lost-page {
	width: 100%;
	min-height: 100vh;
	background-color: #f5f5f5;
}

/* 页面头部 */
.header {
	display: flex;
	align-items: center;
	justify-content: center;
	height: 88rpx;
	background-color: #fff;
	border-bottom: 1px solid #eee;
}

.title {
	font-size: 36rpx;
	font-weight: bold;
	color: #333;
}

/* 内容区域 */
.content {
	width: 100%;
	height: calc(100vh - 88rpx);
	padding: 24rpx 20rpx;
	box-sizing: border-box;
}

/* 加载提示 */
.loading-container {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 60rpx 0;
}

.loading-text {
	margin-top: 20rpx;
	font-size: 28rpx;
	color: #666;
}

/* 列表内容 */
.lost-pet-list {
	display: flex;
	flex-direction: column;
	gap: 28rpx;
}

/* 列表项 */
.lost-pet-item {
	display: flex;
	align-items: center;
	background-color: #fff;
	border-radius: 24rpx;
	padding: 16rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
	transition: all 0.3s ease;
}

/* 淡入动画 */
.fade-in {
	animation: fadeIn 0.6s ease-out forwards;
	opacity: 0;
	transform: translateY(20rpx);
}

@keyframes fadeIn {
	from {
		opacity: 0;
		transform: translateY(20rpx);
	}
	to {
		opacity: 1;
		transform: translateY(0);
	}
}

.lost-pet-item:hover {
	box-shadow: 0 10rpx 40rpx rgba(0, 0, 0, 0.15);
	transform: translateY(-3rpx);
}

/* 宠物图片 */
.pet-image {
	width: 240rpx;
	height: 240rpx;
	overflow: hidden;
	border-radius: 20rpx;
	margin-right: 20rpx;
}

.pet-image image {
	width: 100%;
	height: 100%;
	transition: transform 0.3s ease;
}

.lost-pet-item:hover .pet-image image {
	transform: scale(1.05);
}

/* 宠物信息 */
.pet-info {
	flex: 1;
	padding: 16rpx 0;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

/* 宠物头部 */
.pet-header {
	display: flex;
	align-items: center;
	justify-content: space-between;
	margin-bottom: 16rpx;
}

.pet-name {
	font-size: 34rpx;
	font-weight: 700;
	color: #262626;
	line-height: 1.3;
}

/* 状态标签 */
.status-tag {
	padding: 8rpx 20rpx;
	border-radius: 20rpx;
	font-size: 24rpx;
	font-weight: 600;
	letter-spacing: 0.5rpx;
	transition: all 0.3s ease;
	position: relative;
	overflow: hidden;
}

.status-tag::before {
	content: '';
	position: absolute;
	top: 0;
	left: -100%;
	width: 100%;
	height: 100%;
	background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
	transition: left 0.5s;
}

.status-tag:hover::before {
	left: 100%;
}

.status-tag.searching {
	background: linear-gradient(135deg, #e6f7ff 0%, #b3d8ff 100%);
	color: #096dd9;
	box-shadow: 0 2rpx 8rpx rgba(24, 144, 255, 0.2);
}

.status-tag.found {
	background: linear-gradient(135deg, #f6ffed 0%, #d9f7be 100%);
	color: #389e0d;
	box-shadow: 0 2rpx 8rpx rgba(82, 196, 26, 0.2);
}

/* 宠物详情 */
.pet-detail {
	display: flex;
	align-items: center;
	margin-bottom: 12rpx;
	line-height: 1.4;
}

.pet-detail .label {
	font-size: 26rpx;
	color: #8c8c8c;
	margin-right: 10rpx;
	font-weight: 500;
}

.pet-detail .value {
	font-size: 26rpx;
	color: #595959;
	font-weight: 500;
}

/* 酬谢金额 */
.reward-value {
	font-size: 30rpx;
	color: #ff4d4f;
	font-weight: 700;
	letter-spacing: 0.5rpx;
}

/* 操作按钮 */
.action-buttons {
	display: flex;
	gap: 12rpx;
	margin-top: 16rpx;
}

.action-buttons button {
	flex: 1;
	border-radius: 20rpx !important;
	font-weight: 600 !important;
	font-size: 22rpx !important;
	padding: 8rpx 0 !important;
	transition: all 0.3s ease !important;
	letter-spacing: 0.5rpx;
}

.action-buttons button:hover {
	transform: translateY(-2rpx);
	box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.2) !important;
}

.action-buttons button:active {
	transform: translateY(0);
}

.action-buttons button:first-child {
	background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%) !important;
	border: none !important;
	color: #fff !important;
}

.action-buttons button:nth-child(2) {
	background: linear-gradient(135deg, #faad14 0%, #d48806 100%) !important;
	border: none !important;
	color: #fff !important;
}

.action-buttons button:last-child {
	background: linear-gradient(135deg, #ff7875 0%, #ff4d4f 100%) !important;
	border: none !important;
	color: #fff !important;
}

/* 空数据提示 */
.empty-container {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 100rpx 40rpx;
	text-align: center;
}

.empty-image {
	width: 240rpx;
	height: 240rpx;
	margin-bottom: 30rpx;
	opacity: 0.6;
	border-radius: 50%;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
}

.empty-text {
	font-size: 32rpx;
	color: #262626;
	font-weight: 600;
	margin-bottom: 16rpx;
}

.empty-subtext {
	font-size: 26rpx;
	color: #8c8c8c;
	margin-bottom: 50rpx;
	line-height: 1.5;
	max-width: 500rpx;
}

.empty-button {
	width: 300rpx;
	height: 80rpx;
	line-height: 80rpx;
	font-size: 28rpx;
	font-weight: 600;
	border-radius: 40rpx !important;
	background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%) !important;
	box-shadow: 0 6rpx 24rpx rgba(24, 144, 255, 0.3);
	transition: all 0.3s ease !important;
}

.empty-button:hover {
	transform: translateY(-2rpx);
	box-shadow: 0 8rpx 32rpx rgba(24, 144, 255, 0.4) !important;
}

/* 加载更多提示 */
.load-more-container {
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 40rpx 0;
}

.load-more-text {
	margin-left: 12rpx;
	font-size: 28rpx;
	color: #666;
}

/* 没有更多数据 */
.no-more-container {
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 40rpx 0;
}

.no-more-text {
	font-size: 28rpx;
	color: #999;
}
</style>
