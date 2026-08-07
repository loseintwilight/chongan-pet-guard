<template>
	<view class="purchase-detail-container" :style="{ height: `${windowHeight}px` }">
		<!-- 顶部标题栏 -->
		<view class="header-section">
			<view class="flex align-center">
				<view @click="navigateBack" class="back-icon">
					<view class="iconfont icon-back"></view>
				</view>
				<view class="title">购买记录详情</view>
				<view class="share-icon" @click="shareRecord">
					<view class="iconfont icon-share"></view>
				</view>
			</view>
		</view>

		<!-- 订单信息卡片 -->
		<view class="order-card">
			<view class="order-header">
				<view :class="['order-status', getOrderStatusClass(orderInfo.status)]">
					<text>{{ getOrderStatusText(orderInfo.status) }}</text>
				</view>
				<view class="order-number">
					<text>订单号: {{ orderInfo.orderSn }}</text>
				</view>
			</view>

			<!-- 商品列表 -->
			<view class="order-content">
				<view class="product-list">
					<view class="product-item" v-for="(item, index) in orderInfo.orderItems" :key="item.itemId">
						<view class="product-info">
							<view class="product-image-container">
								<!-- <image :src="getImageUrl(item.resourceId)" class="product-image" mode="aspectFill" /> -->
								<image :src="item.imageUrl" class="product-image" mode="aspectFill" />
							</view>
							<view class="product-details">
								<view class="product-name">{{ item.resourceName }}</view>
								<view class="product-price">
									<text class="price-label">单价:</text>
									<text class="price-value">¥{{ item.price / 100 }}</text>
								</view>
								<view class="product-quantity">
									<text class="quantity-label">数量:</text>
									<text class="quantity-value">{{ item.quantity }}</text>
								</view>
							</view>
						</view>
						<view class="item-total">
							<text class="total-label">小计:</text>
							<text class="total-value">¥{{ item.totalPrice / 100 }}</text>
						</view>
					</view>
				</view>

				<view class="order-summary">
					<view class="summary-item">
						<view class="summary-label">订单总额:</view>
						<view class="summary-value price-total">¥{{ orderInfo.totalAmount / 100 }}</view>
					</view>
				</view>

				<view class="order-meta">
					<view class="meta-item">
						<view class="meta-label">创建时间:</view>
						<view class="meta-value">{{ formatDateTime(orderInfo.createTime) }}</view>
					</view>
					<view class="meta-item" v-if="orderInfo.updateTime">
						<view class="meta-label">更新时间:</view>
						<view class="meta-value">{{ formatDateTime(orderInfo.updateTime) }}</view>
					</view>
					<view class="meta-item" v-if="orderInfo.remark">
						<view class="meta-label">订单备注:</view>
						<view class="meta-value">{{ orderInfo.remark }}</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 订单时间线 -->
		<view class="timeline-section">
			<view class="section-title">订单时间线</view>
			<view class="timeline">
				<view class="timeline-item">
					<view class="timeline-dot active"></view>
					<view class="timeline-content">
						<view class="timeline-date">{{ formatDateTime(orderInfo.createTime) }}</view>
						<view class="timeline-description">订单创建</view>
					</view>
				</view>
				<view class="timeline-item" v-if="orderInfo.updateTime && orderInfo.status > 0">
					<view class="timeline-dot"></view>
					<view class="timeline-content">
						<view class="timeline-date">{{ formatDateTime(orderInfo.updateTime) }}</view>
						<view class="timeline-description">{{ getOrderStatusText(orderInfo.status) }}</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 店铺信息 -->
		<view class="shop-section" v-if="orderInfo.shopId">
			<view class="section-title">店铺信息</view>
			<view class="shop-content">
				<view class="shop-id">店铺ID: {{ orderInfo.shopId }}</view>
			</view>
		</view>
	</view>
</template>

<script>
import { getOrder } from '@/api/pet/order.js';
import config from '@/config';
import { getResource } from '@/api/pet/resource';

export default {
	data() {
		return {
			windowHeight: 0,
			orderId: '',
			orderInfo: {
				orderId: '',
				orderSn: '',
				status: 0,
				totalAmount: 0,
				remark: '',
				shopId: '',
				userId: '',
				createTime: '',
				updateTime: '',
				orderItems: []
			},
			imageCache: {}
		};
	},
	onLoad(options) {
		this.orderId = options.id;
		this.loadOrderDetail();
	},
	onReady() {
		this.windowHeight = uni.getSystemInfoSync().windowHeight - 50;
	},
	methods: {
		navigateBack() {
			uni.navigateBack();
		},
		async loadOrderDetail() {
			try {
				uni.showLoading({ title: '加载中...' });

				const response = await getOrder(this.orderId);
				if (response.code === 200) {
					const orderData = response.data;

					// 并行加载所有商品的图片URL
					const imagePromises = orderData.orderItems.map(async (item) => {
						try {
							const resourceResponse = await getResource(item.resourceId);
							item.imageUrl = config.getDownloadUrl(resourceResponse.data.images);
						} catch (error) {
							console.error(`加载商品 ${item.resourceId} 图片失败:`, error);
							// 使用默认图片或占位图
							item.imageUrl = '/static/images/placeholder.png';
						}
						return item;
					});

					// 等待所有图片加载完成
					await Promise.all(imagePromises);

					this.orderInfo = orderData;
					//this.generateTimeline();
				} else {
					uni.showToast({
						title: '加载失败: ' + response.msg,
						icon: 'none'
					});
				}
			} catch (error) {
				uni.showToast({
					title: '加载失败: ' + error.message,
					icon: 'none'
				});
				console.error('加载购买记录详情失败:', error);
			} finally {
				uni.hideLoading();
			}
		},
		getTotalQuantity() {
			if (!this.orderInfo.orderItems || this.orderInfo.orderItems.length === 0) {
				return 0;
			}
			return this.orderInfo.orderItems.reduce((total, item) => total + item.quantity, 0);
		},
		formatDateTime(dateString) {
			if (!dateString) return '未知时间';
			const date = new Date(dateString);
			const year = date.getFullYear();
			const month = (date.getMonth() + 1).toString().padStart(2, '0');
			const day = date.getDate().toString().padStart(2, '0');
			const hour = date.getHours().toString().padStart(2, '0');
			const minute = date.getMinutes().toString().padStart(2, '0');
			return `${year}-${month}-${day} ${hour}:${minute}`;
		},
		getImageUrl(fileName) {
			return config.getDownloadUrl(fileName);
		},
		getOrderStatusText(status) {
			switch (status) {
				case 0:
					return '待支付';
				case 1:
					return '已支付';
				case 2:
					return '已完成';
				case 3:
					return '已取消';
				default:
					return '未知状态';
			}
		},
		getOrderStatusClass(status) {
			switch (status) {
				case 0:
					return 'status-pending';
				case 1:
					return 'status-paid';
				case 2:
					return 'status-completed';
				case 3:
					return 'status-cancelled';
				default:
					return 'status-unknown';
			}
		},
		handlePayment() {
			uni.showToast({
				title: '跳转到支付页面',
				icon: 'none'
			});
		},
		handleReview() {
			uni.showToast({
				title: '跳转到评价页面',
				icon: 'none'
			});
		},
		handleRebuy() {
			uni.showToast({
				title: '再次购买',
				icon: 'none'
			});
		},
		shareRecord() {
			uni.showToast({
				title: '分享功能',
				icon: 'none'
			});
		}
	}
};
</script>

<style lang="scss" scoped>
page {
	background: #f8f9fa;
	background-image: radial-gradient(#e9ecef 1px, transparent 1px), radial-gradient(#e9ecef 1px, transparent 1px);
	background-size: 30rpx 30rpx;
	background-position: 0 0, 15rpx 15rpx;
}

.purchase-detail-container {
	width: 100%;
	height: 100%;

	.header-section {
		padding: 20rpx 30rpx;
		background: #ffffff;
		border-bottom: 1rpx solid #e9ecef;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);

		.back-icon {
			width: 60rpx;
			height: 60rpx;
			display: flex;
			justify-content: center;
			align-items: center;
			color: #6c757d;
		}

		.title {
			font-size: 32rpx;
			font-weight: 600;
			color: #212529;
			flex: 1;
			text-align: center;
		}

		.share-icon {
			width: 60rpx;
			height: 60rpx;
			display: flex;
			justify-content: center;
			align-items: center;
			color: #6c757d;
		}
	}

	.order-card {
		background: #ffffff;
		border-radius: 20rpx;
		margin: 20rpx 30rpx;
		padding: 30rpx;
		box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.05);

		.order-header {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 20rpx;

			.order-status {
				padding: 8rpx 20rpx;
				border-radius: 20rpx;
				font-size: 24rpx;
				color: #ffffff;
				text-align: center;

				&.status-pending {
					background: #ffc107;
				}

				&.status-paid {
					background: #28a745;
				}

				&.status-completed {
					background: #17a2b8;
				}

				&.status-cancelled {
					background: #dc3545;
				}

				&.status-unknown {
					background: #6c757d;
				}
			}

			.order-number {
				font-size: 24rpx;
				color: #6c757d;
			}
		}

		.order-content {
			.product-list {
				margin-bottom: 20rpx;

				.product-item {
					border-bottom: 1rpx solid #f8f9fa;
					padding-bottom: 20rpx;
					margin-bottom: 20rpx;

					&:last-child {
						border-bottom: none;
						margin-bottom: 0;
					}

					.product-info {
						display: flex;
						gap: 20rpx;
						margin-bottom: 15rpx;

						.product-image-container {
							width: 120rpx;
							height: 120rpx;
							border-radius: 10rpx;
							overflow: hidden;
							flex-shrink: 0;

							.product-image {
								width: 100%;
								height: 100%;
								object-fit: cover;
							}
						}

						.product-details {
							flex: 1;

							.product-name {
								font-size: 28rpx;
								font-weight: 600;
								color: #212529;
								margin-bottom: 10rpx;
							}

							.product-price {
								display: flex;
								align-items: center;
								gap: 10rpx;
								margin-bottom: 8rpx;

								.price-label {
									font-size: 22rpx;
									color: #6c757d;
								}

								.price-value {
									font-size: 24rpx;
									font-weight: 600;
									color: #dc3545;
								}
							}

							.product-quantity {
								display: flex;
								align-items: center;
								gap: 10rpx;

								.quantity-label {
									font-size: 22rpx;
									color: #6c757d;
								}

								.quantity-value {
									font-size: 24rpx;
									color: #212529;
								}
							}
						}
					}

					.item-total {
						display: flex;
						justify-content: flex-end;
						align-items: center;
						gap: 10rpx;

						.total-label {
							font-size: 22rpx;
							color: #6c757d;
						}

						.total-value {
							font-size: 26rpx;
							font-weight: 600;
							color: #dc3545;
						}
					}
				}
			}

			.order-summary {
				background: #f8f9fa;
				border-radius: 10rpx;
				padding: 20rpx;
				margin-bottom: 20rpx;

				.summary-item {
					display: flex;
					justify-content: space-between;
					align-items: center;
					margin-bottom: 10rpx;

					&:last-child {
						margin-bottom: 0;
					}

					.summary-label {
						font-size: 24rpx;
						color: #6c757d;
					}

					.summary-value {
						font-size: 24rpx;
						color: #212529;

						&.price-total {
							font-weight: 600;
							color: #dc3545;
						}
					}
				}
			}

			.order-meta {
				display: flex;
				flex-direction: column;
				gap: 10rpx;
				margin-bottom: 20rpx;

				.meta-item {
					display: flex;
					justify-content: space-between;
					font-size: 22rpx;

					.meta-label {
						color: #6c757d;
					}

					.meta-value {
						color: #212529;
					}
				}
			}

			.order-actions {
				display: flex;
				gap: 15rpx;

				.action-btn {
					flex: 1;
					padding: 15rpx;
					background: #4facfe;
					border-radius: 10rpx;
					font-size: 24rpx;
					color: #ffffff;
					text-align: center;
					transition: all 0.3s ease;

					&:active {
						background: #3b82f6;
					}
				}
			}
		}
	}

	.timeline-section {
		margin: 20rpx 30rpx;

		.section-title {
			font-size: 28rpx;
			font-weight: 600;
			color: #212529;
			margin-bottom: 20rpx;
		}

		.timeline {
			background: #ffffff;
			border-radius: 20rpx;
			padding: 20rpx;
			box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.05);

			.timeline-item {
				display: flex;
				align-items: flex-start;
				gap: 15rpx;
				margin-bottom: 20rpx;
				position: relative;

				&:last-child {
					margin-bottom: 0;
				}

				.timeline-dot {
					width: 20rpx;
					height: 20rpx;
					border-radius: 50%;
					background: #e9ecef;
					flex-shrink: 0;
					position: relative;
					z-index: 1;

					&.active {
						background: #4facfe;
					}
				}

				.timeline-content {
					flex: 1;

					.timeline-date {
						font-size: 22rpx;
						color: #6c757d;
						margin-bottom: 5rpx;
					}

					.timeline-description {
						font-size: 24rpx;
						color: #212529;
					}
				}
			}
		}
	}

	.shop-section {
		margin: 20rpx 30rpx;

		.section-title {
			font-size: 28rpx;
			font-weight: 600;
			color: #212529;
			margin-bottom: 20rpx;
		}

		.shop-content {
			background: #ffffff;
			border-radius: 20rpx;
			padding: 20rpx;
			box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.05);

			.shop-id {
				font-size: 24rpx;
				color: #212529;
			}
		}
	}
}
</style>
