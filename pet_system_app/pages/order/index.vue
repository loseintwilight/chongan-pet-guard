<template>
	<view class="container">
		<!-- 顶部标题栏 -->
		<view class="header">
			<view class="header-title">我的购买</view>
		</view>

		<!-- 订单列表 -->
		<view class="order-list" v-if="orderList.length > 0">
			<view class="order-item" v-for="(order, index) in orderList" :key="index" @click="navigateToDetail(order.orderId)">
				<!-- 订单信息 -->
				<view class="item-info">
					<view class="item-title">订单号: {{ order.orderSn }}</view>
					<view class="item-meta">
						<text class="item-price">¥{{ order.totalAmount / 100 }}</text>
					</view>
					<view class="item-footer">
						<text class="item-status" :class="order.statusClass">{{ getStatusText(order.status) }}</text>
						<text class="item-time">{{ formatDate(order.createTime) }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 空状态提示 -->
		<view class="empty-state" v-else-if="!isLoading">
			<view class="empty-icon">📦</view>
			<view class="empty-text">暂无购买记录</view>
			<view class="empty-subtext">快去逛逛，发现心仪的商品吧！</view>
		</view>

		<!-- 加载中或加载完成提示 -->
		<view class="load-more" v-if="isLoading">
			<text>加载中...</text>
		</view>
		<view class="load-more" v-if="!hasMore && orderList.length > 0">
			<text>没有更多了</text>
		</view>
	</view>
</template>

<script>
import { getPurchaseRecords } from '@/api/pet/order.js';
import config from '@/config';

export default {
	data() {
		return {
			orderList: [],
			page: 1,
			pageSize: 10,
			isLoading: false,
			hasMore: true
		};
	},
	onLoad() {
		this.loadOrderData();
	},
	onPullDownRefresh() {
		this.page = 1;
		this.hasMore = true;
		this.loadOrderData(true);
	},
	onReachBottom() {
		if (this.hasMore && !this.isLoading) {
			this.page++;
			this.loadOrderData();
		}
	},
	methods: {
		async loadOrderData(isRefresh = false) {
			if (this.isLoading) return;
			this.isLoading = true;
			try {
				const res = await getPurchaseRecords({
					pageNum: this.page,
					pageSize: this.pageSize
				});
				if (res.code === 200) {
					let newOrders = res.rows || [];
					newOrders = newOrders.map((order) => {
						let statusClass = '';
						switch (order.status) {
							case 0:
								statusClass = 'status-pending';
								break;
							case 1:
								statusClass = 'status-completed';
								break;
							default:
								statusClass = '';
								break;
						}
						return {
							...order,
							statusClass
						};
					});
					if (isRefresh) {
						this.orderList = newOrders;
					} else {
						this.orderList = [...this.orderList, ...newOrders];
					}
					this.hasMore = newOrders.length === this.pageSize;
				} else {
					uni.showToast({ title: res.msg || '加载失败', icon: 'none' });
				}
			} catch (error) {
				console.error('加载订单失败:', error);
				uni.showToast({ title: '网络错误', icon: 'none' });
			} finally {
				this.isLoading = false;
				if (isRefresh) {
					uni.stopPullDownRefresh();
				}
			}
		},
		navigateToDetail(orderId) {
			uni.navigateTo({
				url: `/pages/order/detail?id=${orderId}`
			});
		},
		getStatusText(status) {
			const statusMap = {
				0: '待付款',
				1: '已完成'
			};
			return statusMap[status] || '未知状态';
		},
		formatDate(dateString) {
			if (!dateString) return '';
			const date = new Date(dateString);
			const year = date.getFullYear();
			const month = (date.getMonth() + 1).toString().padStart(2, '0');
			const day = date.getDate().toString().padStart(2, '0');
			return `${year}-${month}-${day}`;
		}
	}
};
</script>

<style lang="scss" scoped>
.container {
	min-height: 100vh;
	background-color: #f8f9fa;
	padding: 0 20rpx;
	box-sizing: border-box;
}

.header {
	height: 88rpx;
	background: #ffffff;
	display: flex;
	align-items: center;
	justify-content: center;
	position: sticky;
	top: 0;
	z-index: 100;
	border-bottom: 1px solid #f0f0f0;
	box-shadow: 0 1rpx 4rpx rgba(0, 0, 0, 0.05);
}

.header-title {
	font-size: 34rpx;
	font-weight: 600;
	color: #333333;
}

.order-list {
	padding: 20rpx 0;
}

.order-item {
	background-color: #ffffff;
	border-radius: 16rpx;
	padding: 32rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.06);
	transition: transform 0.2s ease, box-shadow 0.2s ease;

	&:active {
		transform: scale(0.98);
		box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
	}
}

.item-info {
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	height: 100%;
}

.item-title {
	font-size: 30rpx;
	font-weight: 500;
	color: #333333;
	margin-bottom: 16rpx;
}

.item-meta {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 16rpx;
}

.item-price {
	font-size: 36rpx;
	font-weight: 600;
	color: #fa5151;
}

.item-footer {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.item-status {
	font-size: 28rpx;
	font-weight: 500;
	padding: 4rpx 12rpx;
	border-radius: 20rpx;
}

.status-pending {
	color: #ff9500;
	background-color: #fff7ed;
}

.status-completed {
	color: #34c759;
	background-color: #f0f9eb;
}

.item-time {
	font-size: 24rpx;
	color: #999999;
}

.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding-top: 200rpx;
	text-align: center;
	color: #999999;

	.empty-icon {
		font-size: 120rpx;
		margin-bottom: 30rpx;
	}

	.empty-text {
		font-size: 32rpx;
		font-weight: 500;
		margin-bottom: 16rpx;
		color: #666666;
	}

	.empty-subtext {
		font-size: 26rpx;
		color: #999999;
	}
}

.load-more {
	text-align: center;
	padding: 30rpx 0;
	font-size: 28rpx;
	color: #999999;
}
</style>
