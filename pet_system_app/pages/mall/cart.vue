<template>
	<view class="cart-page">
		<view class="address-bar">
			<text class="address-icon">[地]</text>
			<text class="address-text">配送至济南市</text>
			<text class="arrow-icon">v</text>
		</view>

		<view v-if="cartList.length === 0" class="empty-cart">
			<view class="empty-illustration">
				<view class="box-img">
					<text>空</text>
				</view>
				<view class="badge-tag">
					<text>空</text>
				</view>
			</view>
			<view class="empty-text-group">
				<text class="empty-title">购物车空空如也，</text>
				<text class="empty-subtitle">快去挑选中意好货吧~</text>
			</view>
			<view class="empty-btns">
				<view class="empty-btn btn-buy" @click="goToMall">
					<text>马上抢购</text>
				</view>
				<view class="empty-btn btn-fav" @click="goToFav">
					<text>查看收藏好货</text>
				</view>
			</view>
		</view>

		<scroll-view v-else scroll-y class="cart-content" :style="{ height: contentHeight + 'px' }">
			<view class="cart-list">
				<view class="cart-item" v-for="(item, index) in cartList" :key="index">
					<view class="item-left">
						<view class="product-thumb">
							<text>{{ item.resourceName.substring(0, 2) }}</text>
						</view>
					</view>
					<view class="item-center">
						<text class="item-name">{{ item.resourceName }}</text>
						<text class="item-spec">{{ item.specification }}</text>
						<text class="shop-name">{{ item.shopName || '店铺' }}</text>
					</view>
					<view class="item-right">
						<view class="price-row">
							<text class="item-price">{{ (item.price / 100).toFixed(2) }}元</text>
						</view>
						<view class="quantity-row">
							<view class="qty-btn minus" @click="changeQty(index, -1)">
								<text>-</text>
							</view>
							<text class="qty-num">{{ item.quantity }}</text>
							<view class="qty-btn plus" @click="changeQty(index, 1)">
								<text>+</text>
							</view>
						</view>
					</view>
					<view class="delete-btn" @click="deleteItem(index)">
						<text>X</text>
					</view>
				</view>
			</view>

			<view class="cart-bottom-bar">
				<view class="bottom-left">
					<text class="total-label">合计：</text>
					<text class="total-price">{{ totalPrice.toFixed(2) }}元</text>
				</view>
				<view class="checkout-btn" @click="checkout">
					<text>结算({{ totalCount }})</text>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			cartList: [],
			contentHeight: 500
		};
	},
	computed: {
		totalCount() {
			var total = 0;
			for (var i = 0; i < this.cartList.length; i++) {
				total += this.cartList[i].quantity || 1;
			}
			return total;
		},
		totalPrice() {
			var sum = 0;
			for (var i = 0; i < this.cartList.length; i++) {
				var item = this.cartList[i];
				sum += (item.price / 100) * (item.quantity || 1);
			}
			return sum;
		}
	},
	onLoad() {
		this.calculateHeight();
	},
	onShow() {
		this.loadCartData();
	},
	methods: {
		calculateHeight() {
			var systemInfo = uni.getSystemInfoSync();
			this.contentHeight = systemInfo.windowHeight - 120;
		},
		loadCartData() {
			try {
				var data = uni.getStorageSync('mall_cart_list');
				if (data && Array.isArray(data)) {
					this.cartList = data;
				} else {
					this.cartList = [];
				}
			} catch (e) {
				this.cartList = [];
			}
		},
		saveCartData() {
			try {
				uni.setStorageSync('mall_cart_list', this.cartList);
			} catch (e) {}
		},
		changeQty(index, delta) {
			var newQty = (this.cartList[index].quantity || 1) + delta;
			if (newQty < 1) {
				this.deleteItem(index);
				return;
			}
			if (newQty > 99) {
				newQty = 99;
			}
			this.cartList[index].quantity = newQty;
			this.saveCartData();
		},
		deleteItem(index) {
			this.cartList.splice(index, 1);
			this.saveCartData();
			if (this.cartList.length === 0) {
				uni.showToast({ title: '购物车已清空', icon: 'none' });
			}
		},
		checkout() {
			uni.showToast({ title: '结算功能开发中', icon: 'none' });
		},
		goToMall() {
			uni.navigateTo({
				url: '/pages/mall/index'
			});
		},
		goToFav() {
			uni.navigateTo({
				url: '/pages/mine/collection/index'
			});
		}
	}
};
</script>

<style lang="scss" scoped>
.cart-page {
	min-height: 100vh;
	background-color: #f5f5f5;
}

.address-bar {
	display: flex;
	align-items: center;
	background-color: #fff;
	padding: 24rpx;

	.address-icon {
		font-size: 28rpx;
		color: #999;
		margin-right: 10rpx;
	}

	.address-text {
		font-size: 28rpx;
		color: #333;
		flex: 1;
	}

	.arrow-icon {
		font-size: 22rpx;
		color: #ccc;
	}
}

.empty-cart {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding-top: 160rpx;

	.empty-illustration {
		position: relative;
		margin-bottom: 40rpx;

		.box-img {
			width: 180rpx;
			height: 140rpx;
			background: linear-gradient(135deg, #e8e8e8 0%, #d4d4d4 100%);
			border-radius: 16rpx;
			display: flex;
			align-items: center;
			justify-content: center;

			text {
				font-size: 48rpx;
				color: #999;
			}
		}

		.badge-tag {
			position: absolute;
			top: -20rpx;
			right: -10rpx;
			width: 64rpx;
			height: 40rpx;
			background: linear-gradient(135deg, #ff6b9d 0%, #ff4444 100%);
			border-radius: 20rpx;
			display: flex;
			align-items: center;
			justify-content: center;

			text {
				font-size: 20rpx;
				color: #fff;
				font-weight: bold;
			}
		}
	}

	.empty-text-group {
		margin-bottom: 50rpx;
		text-align: center;

		.empty-title {
			font-size: 32rpx;
			color: #333;
			display: block;
			margin-bottom: 8rpx;
		}

		.empty-subtitle {
			font-size: 26rpx;
			color: #999;
			display: block;
		}
	}

	.empty-btns {
		display: flex;
		gap: 30rpx;

		.empty-btn {
			padding: 20rpx 56rpx;
			border-radius: 40rpx;
			border: 1rpx solid #ddd;

			text {
				font-size: 28rpx;
				color: #666;
			}

			&.btn-buy {
				background-color: #ff6b35;
				border: none;

				text {
					color: #fff;
					font-weight: bold;
				}
			}

			&.btn-fav {
				background-color: #fff;

				text {
					color: #666;
				}
			}
		}
	}
}

.cart-content {
	padding: 16rpx 24rpx;
}

.cart-list {
	.cart-item {
		display: flex;
		align-items: center;
		background-color: #fff;
		border-radius: 16rpx;
		padding: 24rpx;
		margin-bottom: 16rpx;
		position: relative;

		.item-left {
			width: 130rpx;
			flex-shrink: 0;
			margin-right: 16rpx;

			.product-thumb {
				width: 120rpx;
				height: 120rpx;
				background: linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%);
				border-radius: 12rpx;
				display: flex;
				align-items: center;
				justify-content: center;

				text {
					color: #fff;
					font-size: 36rpx;
					font-weight: bold;
				}
			}
		}

		.item-center {
			flex: 1;
			min-width: 0;

			.item-name {
				font-size: 28rpx;
				color: #333;
				font-weight: bold;
				display: block;
				margin-bottom: 6rpx;
				overflow: hidden;
				text-overflow: ellipsis;
				white-space: nowrap;
			}

			.item-spec {
				font-size: 24rpx;
				color: #999;
				display: block;
				margin-bottom: 6rpx;
			}

			.shop-name {
				font-size: 22rpx;
				color: #bbb;
				display: block;
			}
		}

		.item-right {
			display: flex;
			flex-direction: column;
			align-items: flex-end;
			margin-left: 12rpx;

			.price-row {
				margin-bottom: 12rpx;

				.item-price {
					font-size: 30rpx;
					color: #ff6b35;
					font-weight: bold;
				}
			}

			.quantity-row {
				display: flex;
				align-items: center;

				.qty-btn {
					width: 46rpx;
					height: 46rpx;
					border: 1rpx solid #ddd;
					border-radius: 8rpx;
					display: flex;
					align-items: center;
					justify-content: center;

					text {
						font-size: 26rpx;
						color: #666;
					}

					&.plus {
						background-color: #ff6b35;
						border: none;

						text {
							color: #fff;
						}
					}
				}

				.qty-num {
					width: 60rpx;
					text-align: center;
					font-size: 26rpx;
					color: #333;
				}
			}
		}

		.delete-btn {
			position: absolute;
			top: 12rpx;
			right: 12rpx;
			width: 40rpx;
			height: 40rpx;
			display: flex;
			align-items: center;
			justify-content: center;

			text {
				font-size: 24rpx;
				color: #ccc;
			}
		}
	}
}

.cart-bottom-bar {
	position: fixed;
	left: 0;
	right: 0;
	bottom: 0;
	display: flex;
	align-items: center;
	justify-content: space-between;
	background-color: #fff;
	padding: 16rpx 24rpx;
	padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
	box-shadow: 0 -2rpx 16rpx rgba(0, 0, 0, 0.06);

	.bottom-left {
		display: flex;
		align-items: baseline;

		.total-label {
			font-size: 28rpx;
			color: #333;
		}

		.total-price {
			font-size: 36rpx;
			color: #ff6b35;
			font-weight: bold;
		}
	}

	.checkout-btn {
		background: linear-gradient(135deg, #ff6b35 0%, #ff4444 100%);
		padding: 18rpx 52rpx;
		border-radius: 36rpx;

		text {
			font-size: 28rpx;
			color: #fff;
			font-weight: bold;
		}
	}
}
</style>
