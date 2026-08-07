<template>
	<view class="care-shop-selection-container" :style="{ height: `${windowHeight}px` }">
		<!-- 顶部标题栏 -->
		<view class="header">
			<view class="header-content">
				<view class="title">宠物托养中心</view>
				<view class="subtitle">选择您的专属宠物之家</view>
			</view>
			<view class="header-actions">
				<view class="action-btn" @click="showSearch">
					<text class="icon">🔍</text>
					<text class="label">搜索</text>
				</view>
			</view>
		</view>

		<!-- 搜索栏 -->
		<view class="search-bar" v-if="showSearchBar">
			<view class="search-input-wrapper">
				<text class="search-icon">🔍</text>
				<input v-model="queryParams.shopName" class="search-input" placeholder="搜索店铺名称..." @confirm="handleQuery" @blur="hideSearch" />
				<text class="clear-icon" @click="clearSearch">✕</text>
			</view>
		</view>

		<!-- 店铺列表 -->
		<view class="shop-list">
			<view v-if="loading" class="loading-state">
				<view class="loading-icon">
					<view class="iconfont icon-loading"></view>
				</view>
				<view class="loading-text">加载中...</view>
			</view>

			<view v-else-if="shopList.length === 0" class="empty-state">
				<view class="empty-icon">
					<view class="iconfont icon-store"></view>
				</view>
				<view class="empty-text">暂无符合条件的店铺</view>
				<view class="empty-subtext">请尝试调整筛选条件或搜索关键词</view>
			</view>

			<view v-else class="shop-card-list">
				<view class="shop-card" v-for="(shop, index) in filteredShopList" :key="shop.shopId" @click="selectShop(shop)">
					<view class="shop-image-container">
						<image :src="getImageUrl(shop.mainImage)" class="shop-image" mode="aspectFill" :draggable="false" />
						<!-- 使用多个条件判断来替代方法调用 -->
						<view class="status-badge" v-if="shop.status === 0" style="background: linear-gradient(135deg, #4caf50, #8bc34a)">
							<text>正常营业</text>
						</view>
						<view class="status-badge" v-else-if="shop.status === 1" style="background: linear-gradient(135deg, #ff9800, #ffc107)">
							<text>休息中</text>
						</view>
						<view class="status-badge" v-else-if="shop.status === 2" style="background: linear-gradient(135deg, #f44336, #e91e63)">
							<text>未审核</text>
						</view>
						<view class="status-badge" v-else style="background: linear-gradient(135deg, #9e9e9e, #607d8b)">
							<text>未知状态</text>
						</view>
					</view>
					<view class="shop-info">
						<view class="shop-name">{{ shop.shopName }}</view>
						<view class="shop-meta">
							<text class="meta-item">👤 {{ shop.legalName }}</text>
							<text class="meta-item">📞 {{ shop.legalPhone }}</text>
						</view>
						<view class="shop-address">
							<text>📍 {{ shop.province }}{{ shop.city }}{{ shop.district }}{{ shop.address }}</text>
						</view>
						<view class="shop-hours">
							<text>⏰ {{ shop.bussinessHours }}</text>
						</view>
					</view>
					<view class="shop-select-indicator" v-if="selectedShop && selectedShop.shopId === shop.shopId">
						<view class="select-icon">
							<view class="iconfont icon-check"></view>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 加载更多 -->
		<view class="load-more" v-if="loadingStatus === 'more'">
			<text>加载更多...</text>
		</view>
		<view class="load-more" v-if="loadingStatus === 'noMore'">
			<text>没有更多数据了</text>
		</view>

		<!-- 底部确认按钮 -->
		<view class="bottom-section">
			<view class="confirm-button" :class="{ disabled: !selectedShop }" @click="confirmSelection">
				<text>确认选择</text>
			</view>
		</view>
	</view>
</template>

<script>
import { listShop } from '@/api/pet/shop';
import config from '@/config';

export default {
	data() {
		return {
			windowHeight: 0,
			loading: true,
			loadingStatus: 'more',
			page: 1,
			pageSize: 10,
			total: 0,
			showSearchBar: false,
			queryParams: {
				pageNum: 1,
				pageSize: 10,
				shopName: ''
			},
			shopList: [],
			selectedShop: null
		};
	},
	computed: {
		/** 过滤掉未审核状态的店铺 */
		filteredShopList() {
			return this.shopList.filter((shop) => shop.status !== 2);
		}
	},
	onLoad() {
		this.windowHeight = uni.getSystemInfoSync().windowHeight - 50;
		this.getList();
	},
	onPullDownRefresh() {
		this.page = 1;
		this.queryParams.pageNum = 1;
		this.shopList = [];
		this.getList().finally(() => {
			uni.stopPullDownRefresh();
		});
	},
	onReachBottom() {
		if (this.loadingStatus === 'more') {
			this.page++;
			this.queryParams.pageNum = this.page;
			this.getList();
		}
	},
	methods: {
		/** 查询店铺列表 */
		getList() {
			this.loading = true;
			this.loadingStatus = 'loading';
			listShop(this.queryParams)
				.then((response) => {
					if (response.code === 200) {
						const data = response.rows || [];
						if (this.page === 1) {
							this.shopList = data;
						} else {
							this.shopList = [...this.shopList, ...data];
						}
						this.total = response.total;
						this.loadingStatus = this.shopList.length >= response.total ? 'noMore' : 'more';
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
					console.error('加载店铺列表失败:', error);
					this.loadingStatus = 'noMore';
				})
				.finally(() => {
					this.loading = false;
				});
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
				shopName: ''
			};
			this.page = 1;
			this.shopList = [];
			this.getList();
		},

		/** 清除搜索 */
		clearSearch() {
			this.queryParams.shopName = '';
			this.handleQuery();
		},

		/** 搜索按钮操作 */
		handleQuery() {
			this.page = 1;
			this.queryParams.pageNum = 1;
			this.shopList = [];
			this.getList();
		},

		/** 选择店铺 */
		selectShop(shop) {
			this.selectedShop = shop;
			uni.showToast({
				title: `已选择: ${shop.shopName}`,
				icon: 'success'
			});
		},

		/** 确认选择店铺 */
		confirmSelection() {
			if (!this.selectedShop) {
				uni.showToast({
					title: '请先选择一个店铺',
					icon: 'none'
				});
				return;
			}

			// 跳转到托养登记页面
			uni.navigateTo({
				url: `/pages/boarding/boarding_regist?storeId=${this.selectedShop.shopId}&storeName=${encodeURIComponent(this.selectedShop.shopName)}`
			});
		},

		/** 跳转到添加页面 */
		navigateToAdd() {
			uni.navigateTo({
				url: '/pages/shop/shop_add'
			});
		},

		/** 获取图片URL */
		getImageUrl(fileName) {
			return config.getDownloadUrl(fileName);
		}
	}
};
</script>

<style lang="scss" scoped>
.care-shop-selection-container {
	width: 100%;
	height: 100%;
	background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
	padding: 20rpx;
	box-sizing: border-box;

	.header {
		background: rgba(255, 255, 255, 0.9);
		backdrop-filter: blur(10px);
		border-radius: 20rpx;
		padding: 30rpx;
		margin-bottom: 20rpx;
		display: flex;
		justify-content: space-between;
		align-items: center;
		box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.1);
	}

	.header-content {
		text-align: center;
		flex: 1;
	}

	.title {
		font-size: 36rpx;
		font-weight: bold;
		color: #1976d2;
		margin-bottom: 10rpx;
		text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
	}

	.subtitle {
		font-size: 24rpx;
		color: #5c6bc0;
		opacity: 0.8;
	}

	.header-actions {
		display: flex;
		gap: 20rpx;
	}

	.action-btn {
		background: rgba(25, 118, 210, 0.1);
		border-radius: 50rpx;
		padding: 15rpx 25rpx;
		display: flex;
		align-items: center;
		gap: 10rpx;
		transition: all 0.3s ease;
	}

	.action-btn:active {
		transform: scale(0.95);
		background: rgba(25, 118, 210, 0.2);
	}

	.icon {
		font-size: 24rpx;
	}

	.label {
		font-size: 24rpx;
		color: #1976d2;
	}

	.search-bar {
		margin-bottom: 20rpx;
	}

	.search-input-wrapper {
		background: rgba(255, 255, 255, 0.9);
		backdrop-filter: blur(10px);
		border-radius: 50rpx;
		padding: 15rpx 20rpx;
		display: flex;
		align-items: center;
		box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.1);
	}

	.search-icon {
		font-size: 24rpx;
		color: #1976d2;
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

	.shop-list {
		padding: 20rpx 0;
		min-height: calc(100% - 200rpx);

		.loading-state {
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			padding: 60rpx 0;
			color: #6c757d;

			.loading-icon {
				font-size: 60rpx;
				margin-bottom: 20rpx;
				color: #adb5bd;
				animation: spin 1s linear infinite;
			}

			.loading-text {
				font-size: 28rpx;
			}
		}

		.empty-state {
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			padding: 60rpx 0;
			color: #6c757d;

			.empty-icon {
				font-size: 80rpx;
				margin-bottom: 20rpx;
				color: #adb5bd;
			}

			.empty-text {
				font-size: 28rpx;
				margin-bottom: 10rpx;
			}

			.empty-subtext {
				font-size: 24rpx;
				color: #adb5bd;
			}
		}

		.shop-card-list {
			display: flex;
			flex-direction: column;
			gap: 20rpx;
		}

		.shop-card {
			background: rgba(255, 255, 255, 0.9);
			backdrop-filter: blur(10px);
			border-radius: 20rpx;
			overflow: hidden;
			box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.1);
			transition: all 0.3s ease;
			position: relative;

			&:active {
				transform: translateY(5rpx);
				box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.1);
			}

			.shop-image-container {
				position: relative;
				height: 200rpx;
				overflow: hidden;
			}

			.shop-image {
				width: 100%;
				height: 100%;
				object-fit: cover;
			}

			.status-badge {
				position: absolute;
				top: 15rpx;
				right: 15rpx;
				padding: 8rpx 15rpx;
				border-radius: 20rpx;
				font-size: 22rpx;
				font-weight: bold;
				color: white;
				text-shadow: 0 1rpx 2rpx rgba(0, 0, 0, 0.3);
			}

			.shop-info {
				padding: 20rpx;
			}

			.shop-name {
				font-size: 30rpx;
				font-weight: bold;
				color: #1976d2;
				margin-bottom: 10rpx;
			}

			.shop-meta {
				display: flex;
				gap: 15rpx;
				margin-bottom: 10rpx;
				font-size: 24rpx;
				color: #546e7a;
			}

			.meta-item {
				opacity: 0.8;
			}

			.shop-address {
				font-size: 24rpx;
				color: #546e7a;
				margin-bottom: 10rpx;
				opacity: 0.8;
			}

			.shop-hours {
				font-size: 22rpx;
				color: #546e7a;
				opacity: 0.7;
			}

			.shop-select-indicator {
				position: absolute;
				bottom: 20rpx;
				right: 20rpx;
				width: 50rpx;
				height: 50rpx;
				background: rgba(25, 118, 210, 0.1);
				border-radius: 50%;
				display: flex;
				justify-content: center;
				align-items: center;
				border: 2rpx solid #1976d2;

				.select-icon {
					color: #1976d2;
					font-size: 30rpx;
				}
			}
		}
	}

	.load-more {
		text-align: center;
		padding: 20rpx;
		font-size: 24rpx;
		color: #546e7a;
		opacity: 0.7;
	}

	.bottom-section {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		padding: 20rpx 30rpx;
		background: rgba(255, 255, 255, 0.9);
		backdrop-filter: blur(10px);
		border-top: 1rpx solid #e9ecef;
		box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);

		.confirm-button {
			padding: 20rpx;
			background: linear-gradient(135deg, #4caf50, #8bc34a);
			border-radius: 30rpx;
			text-align: center;
			font-size: 30rpx;
			font-weight: 600;
			color: #ffffff;
			transition: all 0.3s ease;

			&.disabled {
				background: #cccccc;
				cursor: not-allowed;
			}

			&:active:not(.disabled) {
				transform: scale(0.95);
				background: linear-gradient(135deg, #388e3c, #689f38);
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
</style>
