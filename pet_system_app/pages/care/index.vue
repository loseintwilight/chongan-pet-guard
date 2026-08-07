<template>
	<view class="care-container">
		<!-- 页面标题栏 -->
		<view class="page-header">
			<view class="back-icon" @click="navigateBack">
				<view class="iconfont icon-back"></view>
			</view>
			<view class="header-title">我的托养</view>
			<view class="header-actions"></view>
		</view>

		<!-- 内容区域 -->
		<scroll-view class="content" scroll-y refresher-enabled @refresherrefresh="onRefresh" @scrolltolower="onLoadMore" lower-threshold="200">
			<!-- 加载提示 -->
			<view v-if="loading" class="loading-container">
				<uni-spinner type="scale"></uni-spinner>
				<text class="loading-text">加载中...</text>
			</view>

			<!-- 空数据提示 -->
			<view v-else-if="!loading && boardingList.length === 0" class="empty-container">
				<image src="/static/images/entrust01.png" class="empty-image"></image>
				<text class="empty-text">暂无托养记录</text>
				<text class="empty-subtext">点击下方按钮开始托养</text>
				<button type="primary" class="empty-button" @click="navigateToBoardingRegist">立即托养</button>
			</view>

			<!-- 托养订单列表 -->
			<view v-else class="boarding-list">
				<view v-for="item in boardingList" :key="item.boardingId" class="boarding-item" :class="{ 'fade-in': true }" @click="navigateToDetail(item.boardingId)">
					<!-- 宠物图片 -->
					<view class="pet-image">
						<image :src="getImageUrl(item.images)" mode="aspectFill" @error="handleImageError($event)"></image>
					</view>

					<!-- 宠物信息 -->
					<view class="pet-details">
						<!-- 宠物名称和状态 -->
						<view class="pet-header">
							<text class="pet-name">{{ item.petName }}</text>
							<view
								class="status-tag"
								:class="{
									'status-pending': item.status === 0,
									'status-confirm': item.status === 1,
									'status-processing': item.status === 2,
									'status-completed': item.status === 3,
									'status-canceled': item.status === 4,
									'status-user-canceled': item.status === 5,
									'status-approved': item.status === 6,
									'status-rejected': item.status === 7
								}">
								{{ getStatusText(item.status) }}
							</view>
						</view>

						<!-- 宠物类型 -->
						<view class="pet-type">{{ item.petType }} · {{ item.petBreed }}</view>

						<!-- 托养信息 -->
						<view class="care-info">
							<view class="info-item">
								<text class="info-label">托养时间：</text>
								<text class="info-value">{{ formatDate(item.startTime) }} 至 {{ formatDate(item.endTime) }}</text>
							</view>

							<view class="info-item">
								<text class="info-label">托养费用：</text>
								<text class="info-value price">{{ item.totalPrice }}元</text>
							</view>
						</view>

						<!-- 操作按钮 - 统一为两个按钮：查看详情和删除 -->
						<view class="action-buttons">
							<button class="action-button detail" size="mini" @click.stop="navigateToDetail(item.boardingId)">查看详情</button>
							<button class="action-button delete" size="mini" @click.stop="handleDeleteBoarding(item.boardingId)">删除</button>
						</view>
					</view>
				</view>
			</view>

			<!-- 加载更多提示 -->
			<view v-if="!loading && !noMore && boardingList.length > 0" class="load-more-container">
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
import { listUserBoarding, delBoarding } from '@/api/pet/boarding.js';
import { getToken } from '@/utils/auth.js';
import storage from '@/utils/storage.js';
import constant from '@/utils/constant.js';
import store from '@/store/index.js';
import config from '@/config.js';

export default {
	data() {
		return {
			boardingList: [], // 托养订单列表
			queryParams: {
				// 查询参数
				pageNum: 1, // 当前页码
				pageSize: 4, // 每页数量 - 初始加载4条
				userId: '', // 用户ID
				status: null // 状态筛选
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
		// 返回上一页
		navigateBack() {
			uni.navigateBack();
		},

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
				this.loadBoardingList();
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
						this.loadBoardingList();
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
			// 获取窗口信息
			const windowInfo = uni.getWindowInfo();
			// 获取状态栏高度
			const statusBarHeight = uni.getSystemInfoSync().statusBarHeight;
			// 导航栏高度 + 状态栏高度
			const navBarHeight = statusBarHeight + 44;
			// 滚动区域高度 = 屏幕高度 - 导航栏高度
			this.scrollViewHeight = windowInfo.windowHeight - navBarHeight;
		},

		/**
		 * 加载托养订单列表
		 */
		async loadBoardingList() {
			if (this.loading) return;

			try {
				this.loading = true;

				// 调用API获取数据
				const res = await listUserBoarding(this.queryParams);

				if (res.code === 200) {
					// 处理返回数据
					let data = res.rows || [];
					this.total = res.total;

					// 映射并转换数据字段
					data = data.map((item) => {
						const transformed = {};

						// 基础字段转换（snake_case 到 camelCase）
						for (const key in item) {
							if (item.hasOwnProperty(key)) {
								const camelKey = key.replace(/_([a-z])/g, (g) => g[1].toUpperCase());
								transformed[camelKey] = item[key];
							}
						}

						// 字段映射：API字段 -> 模板字段
						transformed.petName = item.name || item.petName || ''; // 宠物名称
						transformed.petType = this.getPetType(item.species); // 宠物类型（从species字段映射）
						transformed.petBreed = item.breed || item.petBreed || ''; // 宠物品种
						transformed.startTime = item.startDate || item.start_time || ''; // 开始时间
						transformed.endTime = item.endDate || item.end_time || ''; // 结束时间
						transformed.totalPrice = item.price || item.totalAmount || item.total_price || 0; // 托养费用
						transformed.boardingId = item.boardingId || item.boarding_id || item.id || ''; // 订单ID
						transformed.gender = item.gender || ''; // 宠物性别
						transformed.age = item.age || ''; // 宠物年龄
						transformed.healthInfo = item.healthInfo || item.health_info || ''; // 健康状况
						transformed.description = item.description || ''; // 宠物描述
						transformed.neuter = item.neuter || false; // 是否绝育
						transformed.insectRepellent = item.insectRepellent || item.insect_repellent || false; // 是否驱虫
						transformed.dewormingInterval = item.dewormingInterval || item.deworming_interval || ''; // 驱虫周期
						transformed.insectRepellentLastTime = item.insectRepellentLastTime || item.insect_repellent_last_time || ''; // 上次驱虫时间
						transformed.feedAmount = item.feedAmount || item.feed_amount || ''; // 喂食量
						transformed.feedTime = item.feedTime || item.feed_time || ''; // 喂食时间
						transformed.walkTime = item.walkTime || item.walk_time || ''; // 遛弯时间
						// 修复图片字段映射 - 处理多种可能的图片字段
						transformed.images = this.getFirstImage(item.images || item.petImages || item.image || '');
						transformed.specialRequirements = item.specialRequirements || item.special_requirements || ''; // 特殊要求

						return transformed;
					});

					if (this.queryParams.pageNum === 1) {
						// 第一页，直接替换数据
						this.boardingList = data;
					} else {
						// 非第一页，追加数据
						this.boardingList = this.boardingList.concat(data);
					}

					// 判断是否还有更多数据
					this.noMore = this.boardingList.length >= this.total;
				} else {
					uni.showToast({
						title: '获取数据失败',
						icon: 'none',
						duration: 2000
					});
				}
			} catch (error) {
				console.error('加载托养订单失败:', error);
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
		 * 获取第一张图片（处理图片数组或字符串）
		 */
		getFirstImage(images) {
			if (!images) return '';

			// 如果是数组，取第一张图片
			if (Array.isArray(images)) {
				return images.length > 0 ? images[0] : '';
			}

			// 如果是字符串，尝试解析为JSON数组
			if (typeof images === 'string') {
				try {
					const parsed = JSON.parse(images);
					if (Array.isArray(parsed) && parsed.length > 0) {
						return parsed[0];
					}
				} catch (e) {
					// 如果不是JSON，直接返回字符串
					return images;
				}
			}

			return images;
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
				this.loadBoardingList();
			} else {
				// 尝试重新获取用户信息
				store
					.dispatch('GetInfo')
					.then(() => {
						this.queryParams.userId = storage.get(constant.id);
						this.loadBoardingList();
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
			this.loadBoardingList();
		},

		// 格式化日期
		formatDate(dateString) {
			if (!dateString) return '';
			const date = new Date(dateString);
			const year = date.getFullYear();
			const month = String(date.getMonth() + 1).padStart(2, '0');
			const day = String(date.getDate()).padStart(2, '0');
			return `${year}-${month}-${day}`;
		},

		// 获取宠物类型名称
		getPetType(species) {
			const typeMap = {
				1: '狗狗',
				2: '猫咪',
				3: '其他'
			};
			return typeMap[species] || '未知类型';
		},

		/**
		 * 获取完整的图片URL
		 */
		getImageUrl(fileName) {
			if (!fileName) {
				return '/static/images/entrust01.png';
			}
			const url = config.getDownloadUrl(fileName);
			console.log('图片URL:', url);
			return url || '/static/images/entrust01.png';
		},

		/**
		 * 处理图片加载错误
		 */
		handleImageError(e) {
			console.error('图片加载失败:', e);
			// 设置默认图片
			e.currentTarget.src = '/static/images/entrust01.png';
			// 防止循环错误
			e.currentTarget.onerror = null;
		},

		// 获取订单状态文本
		getStatusText(status) {
			const statusMap = {
				0: '待审核',
				1: '已确认，待送宠',
				2: '托养中',
				3: '已完成',
				4: '已取消',
				5: '用户已取消',
				6: '已审核(通过)',
				7: '已审核(不通过)'
			};
			return statusMap[status] || '未知状态';
		},

		// 导航到托养订单详情页
		navigateToDetail(boardingId) {
			uni.navigateTo({
				url: `/pages/care/detail?boardingId=${boardingId}`
			});
		},

		// 导航到托养登记页
		navigateToBoardingRegist() {
			uni.navigateTo({
				url: '/pages/boarding/boarding_regist'
			});
		},

		/**
		 * 删除托养订单
		 */
		async handleDeleteBoarding(boardingId) {
			// 确认删除
			uni.showModal({
				title: '确认删除',
				content: '确定要删除这个托养订单吗？删除后无法恢复',
				cancelText: '取消',
				confirmText: '确定删除',
				confirmColor: '#FF3B30',
				success: async (res) => {
					if (res.confirm) {
						try {
							uni.showLoading({ title: '删除中...', mask: true });
							// 这里需要调用删除API，如果后端没有提供删除接口，可以使用cancelBoarding
							const result = await delBoarding(boardingId);

							uni.hideLoading();

							if (result.code === 200) {
								uni.showToast({ title: '删除成功', icon: 'success' });
								// 重新加载订单列表
								this.queryParams.pageNum = 1;
								this.loadBoardingList();
							} else {
								uni.showToast({
									title: result.msg || '删除失败',
									icon: 'none'
								});
							}
						} catch (error) {
							uni.hideLoading();
							console.error('删除托养订单失败:', error);
							uni.showToast({
								title: '网络异常，请稍后重试',
								icon: 'none'
							});
						}
					}
				}
			});
		},

		/**
		 * 取消托养订单（保留此方法，但不在界面上显示）
		 */
		async handleCancelBoarding(boardingId) {
			// 确认取消
			uni.showModal({
				title: '确认取消',
				content: '确定要取消这个托养订单吗？',
				cancelText: '再想想',
				confirmText: '确定取消',
				confirmColor: '#FF3B30',
				success: async (res) => {
					if (res.confirm) {
						try {
							uni.showLoading({ title: '取消中...', mask: true });
							const result = await delBoarding(boardingId);

							uni.hideLoading();

							if (result.code === 200) {
								uni.showToast({ title: '取消成功', icon: 'success' });
								// 重新加载订单列表
								this.queryParams.pageNum = 1;
								this.loadBoardingList();
							} else {
								uni.showToast({ title: '取消失败', icon: 'none' });
							}
						} catch (error) {
							uni.hideLoading();
							console.error('取消托养订单失败:', error);
							uni.showToast({
								title: '网络异常，请稍后重试',
								icon: 'none'
							});
						}
					}
				}
			});
		}
	}
};
</script>

<style lang="scss" scoped>
.care-container {
	min-height: 100vh;
	background-color: #f8f9fa;
	padding-bottom: 30rpx;

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

			.iconfont {
				font-size: 36rpx;
			}
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
		}
	}

	.content {
		width: 100%;
		height: calc(100vh - 88rpx);
		padding: 24rpx 20rpx;
		box-sizing: border-box;
	}

	.loading-container {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 60rpx 0;

		.loading-text {
			margin-top: 20rpx;
			font-size: 28rpx;
			color: #666;
		}
	}

	.empty-container {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 100rpx 40rpx;
		text-align: center;

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
	}

	.boarding-list {
		display: flex;
		flex-direction: column;
		gap: 28rpx;
	}

	/* 列表项 */
	.boarding-item {
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

	.boarding-item:hover {
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
		object-fit: cover;
	}

	.boarding-item:hover .pet-image image {
		transform: scale(1.05);
	}

	/* 宠物信息 */
	.pet-details {
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

	/* 宠物类型 */
	.pet-type {
		font-size: 26rpx;
		color: #595959;
		font-weight: 500;
		margin-bottom: 16rpx;
	}

	/* 托养信息 */
	.care-info {
		margin-bottom: 16rpx;
	}

	.info-item {
		display: flex;
		align-items: center;
		margin-bottom: 12rpx;
		line-height: 1.4;
	}

	.info-label {
		font-size: 26rpx;
		color: #8c8c8c;
		margin-right: 10rpx;
		font-weight: 500;
	}

	.info-value {
		font-size: 26rpx;
		color: #595959;
		font-weight: 500;
	}

	.price {
		font-size: 30rpx;
		color: #ff4d4f;
		font-weight: 700;
		letter-spacing: 0.5rpx;
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

	.status-tag.status-pending {
		background: linear-gradient(135deg, #ffebee 0%, #ffcdd2 100%);
		color: #c62828;
		box-shadow: 0 2rpx 8rpx rgba(198, 40, 40, 0.2);
	}

	.status-tag.status-approved {
		background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
		color: #1565c0;
		box-shadow: 0 2rpx 8rpx rgba(21, 101, 192, 0.2);
	}

	.status-tag.status-rejected {
		background: linear-gradient(135deg, #ffebee 0%, #ffcdd2 100%);
		color: #c62828;
		box-shadow: 0 2rpx 8rpx rgba(198, 40, 40, 0.2);
	}

	.status-tag.status-confirm {
		background: linear-gradient(135deg, #fff8e1 0%, #ffecb3 100%);
		color: #f57f17;
		box-shadow: 0 2rpx 8rpx rgba(245, 127, 23, 0.2);
	}

	.status-tag.status-processing {
		background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
		color: #00838f;
		box-shadow: 0 2rpx 8rpx rgba(0, 131, 143, 0.2);
	}

	.status-tag.status-completed {
		background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
		color: #2e7d32;
		box-shadow: 0 2rpx 8rpx rgba(46, 125, 50, 0.2);
	}

	.status-tag.status-canceled {
		background: linear-gradient(135deg, #e0e0e0 0%, #bdbdbd 100%);
		color: #424242;
		box-shadow: 0 2rpx 8rpx rgba(66, 66, 66, 0.2);
	}

	.status-tag.status-user-canceled {
		background: linear-gradient(135deg, #e0e0e0 0%, #bdbdbd 100%);
		color: #424242;
		box-shadow: 0 2rpx 8rpx rgba(66, 66, 66, 0.2);
	}

	/* 操作按钮 */
	.action-buttons {
		display: flex;
		gap: 12rpx;
		margin-top: 16rpx;
	}

	.action-button {
		flex: 1;
		border-radius: 20rpx !important;
		font-weight: 600 !important;
		font-size: 22rpx !important;
		padding: 8rpx 0 !important;
		transition: all 0.3s ease !important;
		letter-spacing: 0.5rpx;
		border: none !important;
	}

	.action-button:hover {
		transform: translateY(-2rpx);
		box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.2) !important;
	}

	.action-button:active {
		transform: translateY(0);
	}

	.action-button.detail {
		background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%) !important;
		color: #fff !important;
	}

	.action-button.delete {
		background: linear-gradient(135deg, #ff7875 0%, #ff4d4f 100%) !important;
		color: #fff !important;
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
}
</style>
