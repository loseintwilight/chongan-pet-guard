<template>
	<view class="shop-products-page">
		<view class="search-bar">
			<view class="search-input-wrapper">
				<input class="search-input" type="text" placeholder="搜索店铺商品" v-model="keyword" @confirm="handleSearch" :adjust-position="false" :hold-keyboard="false" />
			</view>
			<view class="search-btn" @click="handleSearch">
				<text>搜索</text>
			</view>
		</view>

		<scroll-view scroll-x :show-scrollbar="false" class="category-scroll">
			<view class="category-list">
				<view
					class="category-item"
					v-for="(item, index) in categoryList"
					:key="item.categoryId"
					:class="{ active: currentCategoryId === item.categoryId }"
					@click="selectCategory(item)">
					<text>{{ item.categoryName }}</text>
				</view>
			</view>
		</scroll-view>

		<scroll-view scroll-y class="product-scroll" :style="{ height: scrollHeight + 'px' }" @scrolltolower="loadMore" refresher-enabled :refresher-triggered="refreshing" @refresherrefresh="onRefresh">
			<view class="product-list">
				<view class="product-card" v-for="(item, index) in productList" :key="index" @click="goToDetail(item)">
					<view class="card-left">
						<view class="product-image-box">
							<text class="image-text">商品{{ index + 1 }}</text>
							<view class="shop-tag">
								<text>{{ shopName }}</text>
							</view>
						</view>
					</view>

					<view class="card-right">
						<view class="product-name-row">
							<text class="name-text">{{ item.resourceName }} | {{ item.specification || '优质好货' }}</text>
						</view>

						<view class="price-row">
							<text class="current-price">{{ (item.price / 100).toFixed(2) }}元</text>
						</view>

						<view class="bottom-row">
							<view class="add-cart-btn" @click.stop="addToCart(item)">
								<text>加入购物车</text>
							</view>
							<view class="buy-btn" @click.stop="buyNow(item)">
								<text>立即购买</text>
							</view>
						</view>
					</view>
				</view>
			</view>

			<view class="no-more" v-if="!hasMore && productList.length > 0">
				<text>—— 已经到底啦 ——</text>
			</view>

			<view class="empty-state" v-if="!loading && productList.length === 0">
				<text class="empty-text">暂无商品</text>
			</view>
		</scroll-view>

		<view class="floating-cart" @click="goToCart">
			<view class="cart-badge-box">
				<text class="floating-cart-icon">[车]</text>
				<view class="cart-count-badge" v-if="cartCount > 0">
					<text>{{ cartCount > 99 ? '99+' : cartCount }}</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { getProductListByShop, getCategoryList } from '@/api/pet/mall.js';

export default {
	data() {
		return {
			shopId: null,
			shopName: '',
			keyword: '',
			searchKeyword: '',
			currentCategoryId: 0,
			scrollHeight: 600,
			cartCount: 0,
			loading: false,
			hasMore: true,
			refreshing: false,
			categoryList: [
				{ categoryId: 0, categoryName: '全部' }
			],
			productList: [],
			queryParams: {
				pageNum: 1,
				pageSize: 20
			}
		};
	},
	onLoad(options) {
		this.shopId = options.shopId;
		this.shopName = decodeURIComponent(options.shopName || '');
		this.calculateHeight();
		this.loadCategories();
		this.loadProducts(true);
	},
	onShow() {
		this.calculateHeight();
		this.loadCartCount();
	},
	methods: {
		calculateHeight() {
			var systemInfo = uni.getSystemInfoSync();
			this.scrollHeight = systemInfo.windowHeight - 220;
		},
		loadCategories() {
			getCategoryList().then(res => {
				if (res.code === 200 && res.data) {
					var list = [{ categoryId: 0, categoryName: '全部' }];
					for (var i = 0; i < res.data.length; i++) {
						list.push(res.data[i]);
					}
					this.categoryList = list;
				}
			}).catch(() => {});
		},
		loadProducts(reset) {
			if (this.loading) return;
			if (reset) {
				this.queryParams.pageNum = 1;
				this.hasMore = true;
			}
			if (!this.hasMore) return;
			this.loading = true;
			var params = {
				pageNum: this.queryParams.pageNum,
				pageSize: this.queryParams.pageSize
			};
			if (this.searchKeyword) {
				params.resourceName = this.searchKeyword;
			}
			if (this.currentCategoryId && this.currentCategoryId !== 0) {
				params.categoryId = this.currentCategoryId;
			}
			var self = this;
			getProductListByShop(this.shopId, params).then(res => {
				self.loading = false;
				if (res.code === 200) {
					var rows = res.rows || [];
					if (reset) {
						self.productList = rows;
					} else {
						self.productList = self.productList.concat(rows);
					}
					if (rows.length < self.queryParams.pageSize) {
						self.hasMore = false;
					} else {
						self.queryParams.pageNum++;
					}
				}
			}).catch(err => {
				self.loading = false;
				console.error('加载商品失败:', err);
			});
		},
		loadCartCount() {
			try {
				var cartData = uni.getStorageSync('mall_cart_list');
				if (cartData && Array.isArray(cartData)) {
					var total = 0;
					for (var i = 0; i < cartData.length; i++) {
						total += cartData[i].quantity || 1;
					}
					this.cartCount = total;
				} else {
					this.cartCount = 0;
				}
			} catch (e) {
				this.cartCount = 0;
			}
		},
		selectCategory(item) {
			this.currentCategoryId = item.categoryId;
			this.loadProducts(true);
		},
		handleSearch() {
			this.searchKeyword = this.keyword;
			this.loadProducts(true);
		},
		loadMore() {
			if (!this.loading && this.hasMore) {
				this.loadProducts(false);
			}
		},
		onRefresh() {
			this.refreshing = true;
			var self = this;
			setTimeout(function() {
				self.loadProducts(true);
				self.refreshing = false;
			}, 500);
		},
		goToDetail(item) {
			uni.navigateTo({
				url: '/pages/mall/detail?resourceId=' + item.resourceId
			});
		},
		buyNow(item) {
			uni.navigateTo({
				url: '/pages/mall/detail?resourceId=' + item.resourceId
			});
		},
		addToCart(item) {
			try {
				var cartData = uni.getStorageSync('mall_cart_list') || [];
				var found = false;
				for (var i = 0; i < cartData.length; i++) {
					if (cartData[i].resourceId === item.resourceId) {
						cartData[i].quantity = (cartData[i].quantity || 1) + 1;
						found = true;
						break;
					}
				}
				if (!found) {
					cartData.push({
						resourceId: item.resourceId,
						resourceName: item.resourceName,
						specification: item.specification,
						price: item.price,
						categoryName: item.categoryName,
						shopId: item.shopId,
						shopName: item.shopName,
						images: item.images,
						quantity: 1
					});
				}
				uni.setStorageSync('mall_cart_list', cartData);
				this.loadCartCount();
				uni.showToast({ title: '已加入购物车', icon: 'success' });
			} catch (e) {
				uni.showToast({ title: '加入购物车失败', icon: 'none' });
			}
		},
		goToCart() {
			uni.navigateTo({
				url: '/pages/mall/cart'
			});
		}
	}
};
</script>

<style lang="scss" scoped>
.shop-products-page {
	min-height: 100vh;
	background-color: #f5f5f5;
	display: flex;
	flex-direction: column;
}

.search-bar {
	display: flex;
	align-items: center;
	padding: 16rpx 24rpx;
	background-color: #fff;
	position: relative;
	z-index: 10;
	flex-shrink: 0;

	.search-input-wrapper {
		flex: 1;
		display: flex;
		align-items: center;
		background-color: #f5f5f5;
		border-radius: 32rpx;
		padding: 14rpx 24rpx;

		.search-input {
			flex: 1;
			font-size: 26rpx;
		}
	}

	.search-btn {
		margin-left: 16rpx;
		background: linear-gradient(135deg, #ff6b35 0%, #ff4444 100%);
		padding: 14rpx 32rpx;
		border-radius: 32rpx;

		text {
			font-size: 26rpx;
			color: #fff;
			font-weight: bold;
		}
	}
}

.category-scroll {
	white-space: nowrap;
	background-color: #fff;
	padding-bottom: 16rpx;
	position: relative;
	z-index: 10;
	flex-shrink: 0;

	.category-list {
		display: inline-flex;
		padding: 0 24rpx;

		.category-item {
			display: inline-block;
			padding: 12rpx 28rpx;
			margin-right: 16rpx;
			font-size: 28rpx;
			color: #333;
			border-radius: 32rpx;
			background-color: #f5f5f5;

			&.active {
				color: #ff6b35;
				background-color: #fff0e8;
				font-weight: bold;
			}
		}
	}
}

.product-scroll {
	background-color: #f5f5f5;
	flex: 1;
	overflow: hidden;
}

.product-list {
	padding: 16rpx 24rpx 140rpx 24rpx;
}

.product-card {
	display: flex;
	background-color: #fff;
	border-radius: 16rpx;
	margin-bottom: 16rpx;
	padding: 20rpx;
}

.card-left {
	width: 180rpx;
	flex-shrink: 0;
	margin-right: 16rpx;

	.product-image-box {
		width: 180rpx;
		height: 180rpx;
		background: linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%);
		border-radius: 12rpx;
		position: relative;
		display: flex;
		align-items: center;
		justify-content: center;
		overflow: hidden;

		.image-text {
			color: #fff;
			font-size: 36rpx;
			font-weight: bold;
		}

		.shop-tag {
			position: absolute;
			top: 6rpx;
			left: 6rpx;
			background-color: rgba(255, 107, 53, 0.9);
			padding: 4rpx 10rpx;
			border-radius: 6rpx;

			text {
				color: #fff;
				font-size: 18rpx;
			}
		}
	}
}

.card-right {
	flex: 1;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
	min-width: 0;
}

.product-name-row {
	.name-text {
		font-size: 28rpx;
		color: #333;
		line-height: 1.4;
		display: block;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}
}

.price-row {
	display: flex;
	align-items: baseline;
	margin-top: 12rpx;

	.current-price {
		font-size: 38rpx;
		color: #ff6b35;
		font-weight: bold;
	}
}

.bottom-row {
	display: flex;
	align-items: center;
	justify-content: flex-end;
	margin-top: 12rpx;
	gap: 16rpx;

	.add-cart-btn {
		background: linear-gradient(135deg, #ffb347 0%, #ffcc33 100%);
		padding: 12rpx 24rpx;
		border-radius: 30rpx;

		text {
			color: #fff;
			font-size: 24rpx;
			font-weight: bold;
		}
	}

	.buy-btn {
		background: linear-gradient(135deg, #ff6b35 0%, #ff4444 100%);
		padding: 12rpx 36rpx;
		border-radius: 30rpx;

		text {
			color: #fff;
			font-size: 26rpx;
			font-weight: bold;
		}
	}
}

.no-more {
	text-align: center;
	padding: 30rpx 0 140rpx 0;

	text {
		color: #999;
		font-size: 24rpx;
	}
}

.empty-state {
	display: flex;
	justify-content: center;
	align-items: center;
	padding: 100rpx 0;

	.empty-text {
		color: #999;
		font-size: 28rpx;
	}
}

.floating-cart {
	position: fixed;
	right: 30rpx;
	bottom: 120rpx;
	z-index: 999;

	.cart-badge-box {
		width: 96rpx;
		height: 96rpx;
		background: linear-gradient(135deg, #333 0%, #555 100%);
		border-radius: 50%;
		display: flex;
		align-items: center;
		justify-content: center;
		box-shadow: 0 6rpx 20rpx rgba(0, 0, 0, 0.3);
		position: relative;

		.floating-cart-icon {
			font-size: 40rpx;
			color: #fff;
		}

		.cart-count-badge {
			position: absolute;
			top: -4rpx;
			right: -4rpx;
			min-width: 36rpx;
			height: 36rpx;
			background: linear-gradient(135deg, #ff4444 0%, #ff6b35 100%);
			border-radius: 18rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			padding: 0 8rpx;
			box-shadow: 0 2rpx 8rpx rgba(255, 68, 68, 0.4);

			text {
				font-size: 20rpx;
				color: #fff;
				font-weight: bold;
			}
		}
	}
}
</style>
