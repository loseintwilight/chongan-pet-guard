<template>
	<view class="shop-list-container">
		<!-- 顶部标题栏 -->
		<view class="header">
			<view class="header-content">
				<view class="title">宠物伴侣中心</view>
				<view class="subtitle">选择您的专属宠物之家</view>
			</view>
			<view class="header-actions">
				<view class="action-btn" @click="showSearch">
					<text class="icon">🔍</text>
					<text class="label">搜索</text>
				</view>
				<view class="action-btn" @click="navigateToAdd">
					<text class="icon">➕</text>
					<text class="label">添加</text>
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
		<view class="shops-grid">
			<view class="shop-card" v-for="shop in filteredShopList" :key="shop.shopId" @click="navigateToDetail(shop)">
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
			</view>
		</view>

		<!-- 加载更多 -->
		<view class="load-more" v-if="loadingStatus === 'more'">
			<text>加载更多...</text>
		</view>
		<view class="load-more" v-if="loadingStatus === 'noMore'">
			<text>没有更多数据了</text>
		</view>
	</view>
</template>

<script>
import { listShop } from '@/api/pet/shop';
import config from '@/config';

export default {
	data() {
		return {
			shopList: [],
			loadingStatus: 'more',
			page: 1,
			pageSize: 10,
			total: 0,
			showSearchBar: false,
			queryParams: {
				pageNum: 1,
				pageSize: 10,
				shopName: ''
			}
		};
	},
	created() {
		this.getList();
	},
	computed: {
		/** 过滤掉未审核状态的店铺 */
		filteredShopList() {
			return this.shopList.filter((shop) => shop.status !== 2);
		}
	},
	methods: {
		/** 查询店铺列表 */
		getList() {
			this.loadingStatus = 'loading';
			this.queryParams.pageNum = this.page;
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

		/** 加载更多 */
		loadMore() {
			if (this.loadingStatus === 'more') {
				this.page++;
				this.queryParams.pageNum = this.page;
				this.getList();
			}
		},

		/** 跳转到店铺详情页面 */
		navigateToDetail(shop) {
			uni.navigateTo({
				url: '/pages/store/shop_detail?shopId=' + shop.shopId
			});
		},

		/** 跳转到添加页面 */
		navigateToAdd() {
			uni.navigateTo({
				url: '/pages/shop/index'
			});
		},

		/** 获取图片URL */
		getImageUrl(fileName) {
			return config.getDownloadUrl(fileName);
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
.shop-list-container {
	min-height: 100vh;
	background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
	padding: 20rpx;
	box-sizing: border-box;
}

/* 顶部标题栏 */
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

/* 搜索栏 */
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

/* 店铺列表 */
.shops-grid {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(300rpx, 1fr));
	gap: 20rpx;
}

.shop-card {
	background: rgba(255, 255, 255, 0.9);
	backdrop-filter: blur(10px);
	border-radius: 20rpx;
	overflow: hidden;
	box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.1);
	transition: all 0.3s ease;
	cursor: pointer;
}

.shop-card:active {
	transform: translateY(5rpx);
	box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.1);
}

.shop-image-container {
	position: relative;
	height: 180rpx;
	overflow: hidden;
}

.shop-image {
	width: 100%;
	height: 100%;
	object-fit: cover;
}

.status-badge {
	position: absolute;
	top: 10rpx;
	right: 10rpx;
	padding: 5rpx 15rpx;
	border-radius: 20rpx;
	font-size: 20rpx;
	font-weight: bold;
	color: white;
	text-shadow: 0 1rpx 2rpx rgba(0, 0, 0, 0.3);
}

.shop-info {
	padding: 20rpx;
}

.shop-name {
	font-size: 28rpx;
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

/* 加载更多 */
.load-more {
	text-align: center;
	padding: 20rpx;
	font-size: 24rpx;
	color: #546e7a;
	opacity: 0.7;
}
</style>
