<template>
	<view class="rescue-list-container">
		<!-- 顶部标题栏 -->
		<view class="header-section">
			<view class="header-content">
				<view class="back-icon" @click="navigateBack">
					<view class="iconfont icon-back"></view>
				</view>
				<view class="title">我的救助</view>
				<view class="header-placeholder"></view>
			</view>
		</view>

		<!-- 下拉刷新提示 -->
		<view class="refresh-tip" :class="{ show: isRefreshing }">
			<view class="refresh-icon">
				<view class="iconfont icon-refresh"></view>
			</view>
			<text class="refresh-text">{{ refreshText }}</text>
		</view>

		<!-- 救助列表 -->
		<scroll-view class="list-scroll" scroll-y="true" refresher-enabled="true" :refresher-triggered="isRefreshing" @refresherrefresh="onRefresh" @refresherrestore="onRestore">
			<view class="list-section">
				<view v-if="loading && !isRefreshing" class="loading-container">
					<view class="loading-spinner"></view>
					<text class="loading-text">加载中...</text>
				</view>

				<view v-else-if="filteredRescues.length === 0" class="empty-container">
					<view class="empty-icon">🐾</view>
					<view class="empty-title">暂无救助记录</view>
					<view class="empty-desc">您还没有提交过救助登记</view>
					<view class="empty-btn" @click="goToApply">去登记救助</view>
				</view>

				<view v-else class="rescue-list">
					<view class="rescue-card" v-for="(item, index) in filteredRescues" :key="item.rescueId" @click="viewDetail(item.rescueId)">
						<!-- 卡片头部 -->
						<view class="card-header">
							<view class="pet-info">
								<image class="pet-avatar" :src="getImageUrl(item.images)" mode="aspectFill"></image>
							</view>

							<!-- 使用条件渲染替代动态class -->
							<view v-if="item.status === 0" class="status-badge status-pending">待处理</view>
							<view v-else-if="item.status === 1" class="status-badge status-processing">已审核(处理中)</view>
							<view v-else-if="item.status === 2" class="status-badge status-completed">已处理</view>
							<view v-else-if="item.status === 3" class="status-badge status-cancelled">已审核(未通过)</view>
							<view v-else class="status-badge status-pending">待处理</view>
						</view>

						<!-- 救助信息 -->
						<view class="rescue-info">
							<view class="info-item">
								<view class="info-icon">📍</view>
								<view class="info-text">{{ item.location }}</view>
							</view>
							<view class="info-item">
								<view class="info-icon">⏰</view>
								<view class="info-text">{{ formatTime(item.createTime) }}</view>
							</view>
							<view class="info-item" v-if="item.petDesc">
								<view class="info-icon">📝</view>
								<view class="info-text description">{{ item.petDesc }}</view>
							</view>
						</view>

						<!-- 卡片底部 -->
						<view class="card-footer">
							<view class="time-info">登记时间：{{ formatDateTime(item.createTime) }}</view>
							<view class="action-btn" @click.stop="viewDetail(item)">
								查看详情
								<view class="iconfont icon-arrow-right"></view>
							</view>
						</view>
					</view>
				</view>

				<!-- 上拉加载更多 -->
				<view v-if="hasMore && !loading" class="load-more" @click="loadMore">
					<text class="load-more-text">点击加载更多</text>
				</view>

				<view v-if="!hasMore && filteredRescues.length > 0" class="no-more">
					<text class="no-more-text">没有更多数据了</text>
				</view>
			</view>
		</scroll-view>

		<!-- 底部操作按钮 -->
		<view class="bottom-actions">
			<view class="apply-btn" @click="goToApply">
				<view class="btn-icon">➕</view>
				<view class="btn-text">登记救助</view>
			</view>
		</view>
	</view>
</template>

<script>
import { getMyRescueList } from '@/api/pet/help.js';
import config from '@/config';

export default {
	data() {
		return {
			loading: false,
			isRefreshing: false,
			refreshText: '下拉刷新',
			currentFilter: 'all',
			rescueList: [],
			pageNum: 1,
			pageSize: 10,
			total: 0,
			hasMore: true,
			stats: {
				total: 0,
				pending: 0,
				processing: 0,
				completed: 0,
				cancelled: 0
			}
		};
	},
	computed: {
		// 筛选后的救助列表
		filteredRescues() {
			if (this.currentFilter === 'all') {
				return this.rescueList;
			}
			return this.rescueList.filter((item) => {
				if (this.currentFilter === 'pending') return item.status === 0;
				if (this.currentFilter === 'processing') return item.status === 1;
				if (this.currentFilter === 'completed') return item.status === 2;
				if (this.currentFilter === 'cancelled') return item.status === 3;
				return true;
			});
		},
		// 统计数据
		totalRescues() {
			return this.stats.total;
		},
		pendingRescues() {
			return this.stats.pending;
		},
		completedRescues() {
			return this.stats.completed;
		}
	},
	onLoad() {
		this.loadRescueList();
	},
	// 启用下拉刷新
	onPullDownRefresh() {
		this.onRefresh();
	},
	// 触底加载更多
	onReachBottom() {
		if (this.hasMore && !this.loading) {
			this.loadMore();
		}
	},
	methods: {
		navigateBack() {
			uni.navigateBack();
		},
		// 下拉刷新
		async onRefresh() {
			this.isRefreshing = true;
			this.refreshText = '正在刷新...';

			// 重置分页
			this.pageNum = 1;
			this.hasMore = true;

			try {
				await this.loadRescueList(false);
				this.refreshText = '刷新成功';

				setTimeout(() => {
					this.isRefreshing = false;
					this.refreshText = '下拉刷新';
				}, 500);
			} catch (error) {
				this.refreshText = '刷新失败';
				setTimeout(() => {
					this.isRefreshing = false;
					this.refreshText = '下拉刷新';
				}, 1000);
			}
		},
		// 刷新结束
		onRestore() {
			this.isRefreshing = false;
			this.refreshText = '下拉刷新';
		},
		// 加载救助列表
		async loadRescueList(showLoading = true) {
			try {
				if (showLoading) {
					this.loading = true;
				}

				const params = {
					pageNum: this.pageNum,
					pageSize: this.pageSize
				};

				const response = await getMyRescueList(params);
				console.log(response);
				if (response.code === 200) {
					const newData = response.rows || [];

					if (this.pageNum === 1) {
						// 第一页，替换数据
						this.rescueList = newData;
					} else {
						// 后续页，追加数据
						this.rescueList = [...this.rescueList, ...newData];
					}

					this.total = response.total || 0;

					// 判断是否还有更多数据
					this.hasMore = this.rescueList.length < this.total;

					this.calculateStats();
				} else {
					uni.showToast({
						title: response.msg || '加载失败',
						icon: 'none'
					});
				}
			} catch (error) {
				console.error('加载救助列表失败:', error);
				uni.showToast({
					title: '加载失败',
					icon: 'none'
				});
			} finally {
				this.loading = false;
				if (showLoading) {
					uni.stopPullDownRefresh();
				}
			}
		},
		// 加载更多
		async loadMore() {
			if (!this.hasMore || this.loading) return;

			this.pageNum++;
			await this.loadRescueList(false);
		},
		// 计算统计数据
		calculateStats() {
			this.stats = {
				total: this.rescueList.length,
				pending: this.rescueList.filter((item) => item.status === 0).length,
				processing: this.rescueList.filter((item) => item.status === 1).length,
				completed: this.rescueList.filter((item) => item.status === 2).length,
				cancelled: this.rescueList.filter((item) => item.status === 3).length
			};
		},
		// 切换筛选条件
		changeFilter(filter) {
			this.currentFilter = filter;
		},
		// 查看详情
		viewDetail(item) {
			uni.navigateTo({
				url: `/pages/help/detail?id=${item}`
			});
		},
		// 去登记救助
		goToApply() {
			uni.navigateTo({
				url: '/pages/rescue/rescue'
			});
		},
		// 获取图片URL
		getImageUrl(image) {
			if (!image) return '/static/images/default-pet.png';
			if (image.startsWith('http')) return image;
			return config.getDownloadUrl(image);
		},
		// 格式化时间
		formatTime(time) {
			if (!time) return '';
			const date = new Date(time);
			const now = new Date();
			const diff = now - date;

			if (diff < 60000) {
				return '刚刚';
			} else if (diff < 3600000) {
				return Math.floor(diff / 60000) + '分钟前';
			} else if (diff < 86400000) {
				return Math.floor(diff / 3600000) + '小时前';
			} else {
				return Math.floor(diff / 86400000) + '天前';
			}
		},
		// 格式化日期时间
		formatDateTime(time) {
			if (!time) return '';
			const date = new Date(time);
			const year = date.getFullYear();
			const month = String(date.getMonth() + 1).padStart(2, '0');
			const day = String(date.getDate()).padStart(2, '0');
			const hours = String(date.getHours()).padStart(2, '0');
			const minutes = String(date.getMinutes()).padStart(2, '0');

			return `${year}-${month}-${day} ${hours}:${minutes}`;
		}
	}
};
</script>

<style lang="scss" scoped>
.rescue-list-container {
	min-height: 100vh;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	padding-bottom: 120rpx;
	position: relative;

	.header-section {
		background: rgba(255, 255, 255, 0.1);
		backdrop-filter: blur(10px);
		padding: 20rpx 30rpx;
		position: sticky;
		top: 0;
		z-index: 100;

		.header-content {
			display: flex;
			align-items: center;
			justify-content: space-between;

			.back-icon {
				width: 60rpx;
				height: 60rpx;
				display: flex;
				justify-content: center;
				align-items: center;
				color: #ffffff;
				font-size: 32rpx;
			}

			.title {
				font-size: 36rpx;
				font-weight: bold;
				color: #ffffff;
			}

			.header-placeholder {
				width: 60rpx;
			}
		}
	}

	// 下拉刷新提示
	.refresh-tip {
		position: absolute;
		top: 100rpx;
		left: 50%;
		transform: translateX(-50%);
		background: rgba(0, 0, 0, 0.8);
		color: #ffffff;
		padding: 20rpx 40rpx;
		border-radius: 40rpx;
		display: flex;
		align-items: center;
		gap: 15rpx;
		z-index: 1000;
		opacity: 0;
		transition: all 0.3s ease;

		&.show {
			opacity: 1;
			top: 120rpx;
		}

		.refresh-icon {
			animation: rotate 1s linear infinite;

			.iconfont {
				font-size: 28rpx;
			}
		}

		.refresh-text {
			font-size: 24rpx;
		}
	}

	.list-scroll {
		height: calc(100vh - 200rpx);
	}

	.list-section {
		margin: 0 30rpx;
		padding-top: 20rpx;

		.loading-container {
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			padding: 100rpx 0;

			.loading-spinner {
				width: 60rpx;
				height: 60rpx;
				border: 4rpx solid #f3f3f3;
				border-top: 4rpx solid #4facfe;
				border-radius: 50%;
				animation: spin 1s linear infinite;
				margin-bottom: 20rpx;
			}

			.loading-text {
				font-size: 26rpx;
				color: #ffffff;
			}
		}

		.empty-container {
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			padding: 100rpx 0;

			.empty-icon {
				font-size: 120rpx;
				margin-bottom: 30rpx;
			}

			.empty-title {
				font-size: 32rpx;
				color: #ffffff;
				font-weight: bold;
				margin-bottom: 15rpx;
			}

			.empty-desc {
				font-size: 26rpx;
				color: rgba(255, 255, 255, 0.8);
				margin-bottom: 40rpx;
			}

			.empty-btn {
				padding: 20rpx 40rpx;
				background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
				color: #ffffff;
				border-radius: 40rpx;
				font-size: 28rpx;
				font-weight: bold;
			}
		}

		.rescue-list {
			.rescue-card {
				background: rgba(255, 255, 255, 0.95);
				border-radius: 20rpx;
				padding: 30rpx;
				margin-bottom: 20rpx;
				backdrop-filter: blur(10px);
				transition: all 0.3s ease;

				&:active {
					transform: scale(0.98);
				}

				.card-header {
					display: flex;
					align-items: center;
					justify-content: space-between;
					margin-bottom: 20rpx;

					.pet-info {
						display: flex;
						align-items: center;
						flex: 1;

						.pet-avatar {
							width: 80rpx;
							height: 80rpx;
							border-radius: 50%;
							margin-right: 20rpx;
							background: #f8f9fa;
						}

						.pet-details {
							.pet-name {
								font-size: 30rpx;
								font-weight: bold;
								color: #2c3e50;
								margin-bottom: 8rpx;
							}

							.pet-type {
								font-size: 24rpx;
								color: #6c757d;
							}
						}
					}

					.status-badge {
						padding: 12rpx 24rpx;
						border-radius: 30rpx;
						font-size: 22rpx;
						font-weight: bold;
					}

					.status-pending {
						background: #fff3cd;
						color: #856404;
					}

					.status-processing {
						background: #cce5ff;
						color: #004085;
					}

					.status-completed {
						background: #d4edda;
						color: #155724;
					}

					.status-cancelled {
						background: #f8d7da;
						color: #721c24;
					}
				}

				.rescue-info {
					margin-bottom: 20rpx;

					.info-item {
						display: flex;
						align-items: flex-start;
						margin-bottom: 12rpx;

						.info-icon {
							font-size: 24rpx;
							margin-right: 12rpx;
							margin-top: 2rpx;
						}

						.info-text {
							flex: 1;
							font-size: 26rpx;
							color: #495057;
							line-height: 1.4;

							&.description {
								color: #6c757d;
								display: -webkit-box;
								-webkit-box-orient: vertical;
								-webkit-line-clamp: 2;
								overflow: hidden;
							}
						}
					}
				}

				.card-footer {
					display: flex;
					align-items: center;
					justify-content: space-between;
					padding-top: 20rpx;
					border-top: 1rpx solid #e9ecef;

					.time-info {
						font-size: 22rpx;
						color: #6c757d;
					}

					.action-btn {
						display: flex;
						align-items: center;
						font-size: 24rpx;
						color: #4facfe;
						font-weight: bold;

						.icon-arrow-right {
							margin-left: 8rpx;
							font-size: 20rpx;
						}
					}
				}
			}
		}

		// 加载更多
		.load-more {
			text-align: center;
			padding: 30rpx 0;

			.load-more-text {
				font-size: 26rpx;
				color: rgba(255, 255, 255, 0.8);
			}
		}

		// 没有更多
		.no-more {
			text-align: center;
			padding: 30rpx 0;

			.no-more-text {
				font-size: 24rpx;
				color: rgba(255, 255, 255, 0.6);
			}
		}
	}

	.bottom-actions {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		background: rgba(255, 255, 255, 0.95);
		backdrop-filter: blur(10px);
		padding: 20rpx 30rpx;
		box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);

		.apply-btn {
			display: flex;
			align-items: center;
			justify-content: center;
			background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
			color: #ffffff;
			border-radius: 40rpx;
			padding: 20rpx;
			font-weight: bold;
			transition: all 0.3s ease;

			&:active {
				transform: scale(0.98);
			}

			.btn-icon {
				font-size: 28rpx;
				margin-right: 10rpx;
			}

			.btn-text {
				font-size: 28rpx;
			}
		}
	}
}

@keyframes spin {
	0% {
		transform: rotate(0deg);
	}
	100% {
		transform: rotate(360deg);
	}
}

@keyframes rotate {
	0% {
		transform: rotate(0deg);
	}
	100% {
		transform: rotate(360deg);
	}
}
</style>
