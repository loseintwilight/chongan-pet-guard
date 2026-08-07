<template>
	<view class="detail-page">
		<view class="search-header" id="search-header">
			<view class="search-input-wrapper">
				<input type="text" placeholder="搜索全站商品" v-model="keyword" @confirm="handleSearch" />
			</view>
			<view class="search-btn" @click="handleSearch">
				<text>搜索</text>
			</view>
		</view>

		<view class="tab-bar" id="tab-bar">
			<view
				class="tab-item"
				v-for="(tab, index) in tabs"
				:key="index"
				:class="{ active: currentTab === index }"
				@click="switchTab(index)">
				<text>{{ tab.name }}</text>
			</view>
		</view>

		<scroll-view scroll-y class="content-scroll" :style="{ height: contentHeight + 'px' }" @scroll="handleScroll">
			<view v-show="currentTab === 0" class="tab-content">
				<view class="product-showcase">
					<view class="showcase-banner">
						<view class="banner-left">
							<text class="banner-title">{{ productInfo.resourceName }}</text>
							<text class="banner-subtitle">{{ productInfo.categoryName }}</text>
						</view>
						<view class="banner-right">
						<view class="product-image-placeholder">
							<text>商品</text>
						</view>
						<view class="spec-tag">
								<text>{{ productInfo.specification }}</text>
							</view>
						</view>
					</view>
				</view>

				<view class="price-section">
					<view class="price-row">
						<text class="current-price">{{ (productInfo.price / 100).toFixed(2) }}元</text>
						<!-- <text class="original-price" v-if="productInfo.originalPrice && productInfo.originalPrice > productInfo.price">{{ (productInfo.originalPrice / 100).toFixed(2) }}元</text> -->
					</view>

					<view class="title-section">
						<text class="product-title">{{ productInfo.resourceName }} {{ productInfo.specification }} {{ productInfo.categoryName }}</text>
					</view>
				</view>

				<view class="service-section">
					<view class="service-row">
						<text class="service-tag">[保]</text>
						<text class="service-label">安心购</text>
						<text class="service-desc">效期无忧 · 10天价保 · 极速退款 · 7天无理由退货</text>
						<text class="service-arrow">></text>
					</view>

					<view class="location-row">
						<text class="loc-icon">[车]</text>
						<text class="loc-city">济南市</text>
						<text class="loc-free">免运费</text>
						<view class="share-area" @click="shareProduct">
							<!-- <text class="share-icon-text">[分]</text> -->
							<text class="share-label">分享</text>
						</view>
					</view>
				</view>

				<view class="params-section">
					<text class="section-title">商品参数</text>
					<view class="param-grid">
						<view class="param-item" v-if="productInfo.specification">
							<text class="param-label">规格</text>
							<text class="param-value">{{ productInfo.specification }}</text>
						</view>
						<view class="param-item" v-if="productInfo.weight">
							<text class="param-label">重量</text>
							<text class="param-value">{{ productInfo.weight }}g</text>
						</view>
						<view class="param-item" v-if="productInfo.unit">
							<text class="param-label">单位</text>
							<text class="param-value">{{ productInfo.unit }}</text>
						</view>
						<view class="param-item" v-if="productInfo.categoryName">
							<text class="param-label">分类</text>
							<text class="param-value">{{ productInfo.categoryName }}</text>
						</view>
						<view class="param-item" v-if="productInfo.shopName">
							<text class="param-label">店铺</text>
							<text class="param-value">{{ productInfo.shopName }}</text>
						</view>
						<view class="param-item" v-if="productInfo.sales">
							<text class="param-label">销量</text>
							<text class="param-value">{{ productInfo.sales }}</text>
						</view>
					</view>
					<view class="ai-summary" v-if="productInfo.description">
						<text class="ai-tag">AI 总结</text>
						<text class="summary-text">{{ productInfo.description }}</text>
					</view>
				</view>
			</view>

			<view v-show="currentTab === 1" class="tab-content">
				<view class="review-section">
					<view class="review-stats" id="review-stats">
						<view class="stats-left">
							<text class="avg-rating">{{ reviewStats.avgRating }}</text>
							<text class="rating-label">综合评分</text>
						</view>
						<view class="stats-right">
							<view class="rating-bar">
								<text class="bar-label">5星</text>
								<view class="bar-track">
									<view class="bar-fill" :style="{ width: reviewStats.totalCount > 0 ? (reviewStats.fiveCount / reviewStats.totalCount * 100) + '%' : '0%' }"></view>
								</view>
								<text class="bar-count">{{ reviewStats.fiveCount }}</text>
							</view>
							<view class="rating-bar">
								<text class="bar-label">4星</text>
								<view class="bar-track">
									<view class="bar-fill" :style="{ width: reviewStats.totalCount > 0 ? (reviewStats.fourCount / reviewStats.totalCount * 100) + '%' : '0%' }"></view>
								</view>
								<text class="bar-count">{{ reviewStats.fourCount }}</text>
							</view>
							<view class="rating-bar">
								<text class="bar-label">3星</text>
								<view class="bar-track">
									<view class="bar-fill" :style="{ width: reviewStats.totalCount > 0 ? (reviewStats.threeCount / reviewStats.totalCount * 100) + '%' : '0%' }"></view>
								</view>
								<text class="bar-count">{{ reviewStats.threeCount }}</text>
							</view>
							<view class="rating-bar">
								<text class="bar-label">2星</text>
								<view class="bar-track">
									<view class="bar-fill" :style="{ width: reviewStats.totalCount > 0 ? (reviewStats.twoCount / reviewStats.totalCount * 100) + '%' : '0%' }"></view>
								</view>
								<text class="bar-count">{{ reviewStats.twoCount }}</text>
							</view>
							<view class="rating-bar">
								<text class="bar-label">1星</text>
								<view class="bar-track">
									<view class="bar-fill" :style="{ width: reviewStats.totalCount > 0 ? (reviewStats.oneCount / reviewStats.totalCount * 100) + '%' : '0%' }"></view>
								</view>
								<text class="bar-count">{{ reviewStats.oneCount }}</text>
							</view>
						</view>
					</view>

					<view class="review-sort-bar" id="review-sort-bar">
						<view class="sort-item" :class="{ active: reviewOrderBy === 'create_time' }" @click="changeReviewSort('create_time')">
							<text>按时间↓</text>
						</view>
						<view class="sort-item" :class="{ active: reviewOrderBy === 'like_count' }" @click="changeReviewSort('like_count')">
							<text>按点赞</text>
						</view>
						<view class="sort-item" :class="{ active: reviewOrderBy === 'rating' }" @click="changeReviewSort('rating')">
							<text>按评分</text>
						</view>
						<text class="total-count">共{{ reviewStats.totalCount }}条评价</text>
					</view>

					<scroll-view scroll-y class="review-scroll" :style="{ height: reviewScrollHeight + 'px' }">
					<view class="review-list">
						<view class="comment-card" v-for="(review, rIdx) in reviews" :key="review.reviewId">
							<view class="card-top-row">
								<view class="user-left">
									<view class="avatar-circle" :style="{ background: getAvatarColor(review.userName) }">
										<text>{{ getMaskedUserName(review.userName, review.isAnonymous).substring(0, 1) }}</text>
									</view>
									<text class="username">{{ getMaskedUserName(review.userName, review.isAnonymous) }}</text>
									<view class="level-badge">
										<text>LV{{ getLevel(review.userId) }}</text>
									</view>
								</view>
								<view class="floor-badge">
									<view class="floor-avatar-sm" :style="{ background: getAvatarColor(review.userName) }">
										<text>{{ getMaskedUserName(review.userName, review.isAnonymous).substring(0, 1) }}</text>
									</view>
									<text class="floor-num-text">CO.{{ String(review.reviewId).padStart(6, '0') }}</text>
								</view>
							</view>

							<text class="card-content">{{ review.content }}</text>

							<view class="image-area" v-if="review.images">
								<view v-for="(img, imgIdx) in review.images.split(',').slice(0, 3)" :key="imgIdx" class="img-box-lg">
									<view class="img-placeholder-lg"></view>
									<view class="exclusive-tag" v-if="imgIdx === 0">
										<text>⚫ 专属</text>
									</view>
								</view>
							</view>

							<view class="spec-line" v-if="review.specName">
								<text>规格：{{ review.specName }}</text>
							</view>

							<view class="card-bottom-row">
								<view class="bottom-left">
									<text class="time-text">{{ timeAgoText(review.createTime) }}</text>
									<text class="loc-text">{{ getRandomLoc() }}</text>
									<view class="reply-btn-sm" @click="openReplyModal(review, null)">
										<text>回复</text>
									</view>
								</view>
								<view class="bottom-right">
									<view class="vote-btn" @click="handleReviewLike(review)" :class="{ voted: isReviewLiked(review.reviewId) }">
										<text class="vote-icon">👍</text>
										<text class="vote-count">{{ review.likeCount || 0 }}</text>
									</view>
									<view class="vote-btn down">
										<text class="vote-icon">👎</text>
									</view>
									<view class="more-dot-btn" @click="goToAllReviews">
										<text>⋮</text>
									</view>
								</view>
							</view>

							<view class="reply-section" v-if="review.commentList && review.commentList.length > 0">
								<view class="reply-header-info">
									<text class="reply-count-text">相关回复共{{ review.commentList.length }}条</text>
									<view class="reply-sort-hint">
										<text>☰ 按时间</text>
									</view>
								</view>
								<view class="reply-item" v-for="(reply, rIdx) in review.commentList" :key="rIdx">
									<view class="rp-header-row">
										<view class="rp-user-left">
											<view class="rp-avatar-circle" :class="{ merchant: reply.userType == 2 }" :style="{ background: reply.userType == 2 ? '#ff6b35' : getAvatarColor(reply.userName) }">
												<text v-if="reply.userType == 1">{{ getMaskedUserName(reply.userName).substring(0, 1) }}</text>
												<text v-else>商</text>
											</view>
											<text class="rp-name-text" :class="{ merchant: reply.userType == 2 }">{{ reply.userType == 2 ? (reply.shopName || '商家') : getMaskedUserName(reply.userName) }}</text>
											<view class="rp-level-badge" :class="{ merchant: reply.userType == 2 }">
												<text>LV{{ getLevel(reply.userId || reply.shopId) }}</text>
											</view>
											<view class="rp-floor-small" v-if="reply.userType != 2">
												<text class="rp-floor-sep">#</text>
												<text class="rp-floor-id">{{ String(reply.commentId).padStart(4, '0') }}</text>
											</view>
										</view>
										<text class="rp-time-text">{{ timeAgoText(reply.createTime) }}</text>
									</view>
									<text class="rp-body-content">{{ reply.content }}</text>
									<view class="rp-meta-row">
										<text class="rp-loc-text">{{ getRandomLoc() }}</text>
										<view class="rp-reply-btn-sm" @click="openReplyModal(review, reply)" v-if="reply.userType != 2">
											<text>回复</text>
										</view>
										<view class="rp-vote-group">
											<view class="rp-vote-inner" @click="handleReviewLike(reply)" :class="{ voted: isReviewLiked(reply.commentId) }">
												<text class="rp-vote-icon">👍</text>
												<text class="rp-vote-num">{{ reply.likeCount || 0 }}</text>
											</view>
											<view class="rp-vote-inner down">
												<text class="rp-vote-icon">👎</text>
											</view>
										</view>
									</view>
								</view>
							</view>
						</view>
						<view class="empty-review" v-if="reviews.length === 0">
							<text>暂无评价</text>
						</view>
					</view>

					<view class="more-reviews-btn" v-if="reviewStats.totalCount > 3" @click="goToAllReviews">
						<text>查看全部 {{ reviewStats.totalCount }} 条评价 ></text>
					</view>
					</scroll-view>
				</view>
			</view>

			<view v-show="currentTab === 2" class="tab-content">
				<view class="shop-card">
					<view class="shop-header">
						<view class="shop-logo">
							<text>{{ shopNameFirst }}</text>
						</view>
						<view class="shop-info">
							<text class="shop-name">{{ productInfo.shopName || '你好宠物店' }}</text>
							<text class="shop-desc">官方认证店铺 品质保证</text>
						</view>
						<view class="enter-shop-btn" @click="goToShop">
							<text>进店</text>
						</view>
					</view>
				</view>

				<view class="shop-products-section">
					<text class="section-title">店铺热销</text>
					<view class="shop-product-list">
						<view class="shop-product-card" v-for="(item, idx) in shopProducts" :key="idx">
							<view class="spc-left" @click="goToProduct(item)">
								<view class="product-image-box">
									<text class="image-text">商品{{ idx + 1 }}</text>
									<view class="shop-tag" v-if="item.shopName">
										<text>{{ item.shopName }}</text>
									</view>
								</view>
							</view>
							<view class="spc-right">
								<text class="spc-name" @click="goToProduct(item)">{{ item.resourceName }} | {{ item.specification || '优质好货' }}</text>
								<text class="spc-price">{{ (item.price / 100).toFixed(2) }}元</text>
								<view class="spc-actions">
									<view class="add-cart-btn" @click.stop="addToCartFromShop(item)">
										<text>加入购物车</text>
									</view>
									<view class="buy-btn" @click.stop="buyNowFromShop(item)">
										<text>立即购买</text>
									</view>
								</view>
							</view>
						</view>
						<view class="empty-shop-products" v-if="shopProducts.length === 0">
							<text>暂无其他商品</text>
						</view>
					</view>
				</view>
			</view>

			<view v-show="currentTab === 3" class="tab-content">
				<view class="detail-content">
					<text class="section-title">详情</text>
					<view class="description-block">
						<text class="desc-text">{{ productInfo.description || '暂无详情' }}</text>
					</view>
				</view>
			</view>
		</scroll-view>

		<view class="comment-input-bar" id="comment-input-bar" v-show="currentTab === 1">
			<view class="cibar-inner">
				<input class="cibar-input" type="text" placeholder="哎呦，不错哦，发条评论吧" v-model="globalInput" :adjust-position="false" :hold-keyboard="true" @confirm="submitGlobalComment" />
				<view class="cibar-send" @click="submitGlobalComment" :class="{ active: globalInput.trim().length > 0 }">
					<text>发布</text>
				</view>
			</view>
		</view>

		<view class="bottom-bar" id="bottom-bar">
			<view class="bar-left">
				<view class="bar-btn" @click="goToShop">
					<text class="btn-icon">[店]</text>
					<text class="btn-text">店铺</text>
				</view>
				<view class="bar-btn" @click="contactService">
					<text class="btn-icon">[客]</text>
					<text class="btn-text">客服</text>
				</view>
				<view class="bar-btn" @click="goToCart">
					<text class="btn-icon">[车]</text>
					<text class="btn-text">购物车</text>
					<view class="cart-badge" v-if="cartCount > 0">
						<text>{{ cartCount > 99 ? '99+' : cartCount }}</text>
					</view>
				</view>
			</view>
			<view class="bar-right">
				<view class="add-cart-btn" @click="addToCart">
					<text>加入购物车</text>
				</view>
				<view class="buy-now-btn" @click="buyNow">
					<text>立即购买</text>
				</view>
			</view>
		</view>

		<view class="reply-modal-overlay" v-if="replyModalVisible" @click="closeReplyModal"></view>
		<view class="reply-modal" v-if="replyModalVisible" :class="{ show: replyModalVisible }">
			<view class="rm-header">
				<text class="rm-title">{{ replyModalTitle }}</text>
				<view class="rm-close" @click="closeReplyModal">
					<text>✕</text>
				</view>
			</view>
			<view class="rm-body">
				<view class="rm-avatar" :style="{ background: getAvatarColor('我') }">
					<text>{{ '我'.substring(0, 1) }}</text>
				</view>
				<view class="rm-input-wrap">
					<input class="rm-input" type="text" :placeholder="replyModalPlaceholder" v-model="replyModalContent" :adjust-position="false" :hold-keyboard="true" :focus="replyModalFocus" @confirm="submitReplyModal" />
				</view>
			</view>
			<view class="rm-footer">
				<text class="rm-hint">按 Enter 发送</text>
				<view class="rm-send-btn" @click="submitReplyModal" :class="{ active: replyModalContent.trim().length > 0 }">
					<text>发送</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { getProductDetail, getProductListByShop, getReviewList, getReviewStats, likeReview, getShopHotProducts, addComment } from '@/api/pet/mall.js';

export default {
	data() {
		return {
			resourceId: null,
			keyword: '',
			currentTab: 0,
			contentHeight: 500,
			reviewScrollHeight: 440,
			productInfo: {
				resourceId: 0,
				resourceName: '',
				specification: '',
				price: 0,
				originalPrice: 0,
				sales: 0,
				categoryName: '',
				shopId: null,
				shopName: '',
				description: '',
				images: '',
				weight: null,
				unit: ''
			},
			tabs: [
				{ name: '商品' },
				{ name: '评价' },
				{ name: '逛店铺' },
				{ name: '详情' }
			],
			reviews: [],
			reviewStats: {
				totalCount: 0,
				avgRating: 5,
				fiveCount: 0,
				fourCount: 0,
				threeCount: 0,
				twoCount: 0,
				oneCount: 0
			},
			reviewOrderBy: 'create_time',
			reviewIsAsc: 'desc',
			reviewTotal: 0,
			reviewPageNum: 1,
			reviewPageSize: 10,
			likedReviews: [],
			shopProducts: [],
			cartCount: 0,
			globalInput: '',
			replyModalVisible: false,
			replyModalFocus: false,
			replyModalContent: '',
			replyModalTitle: '',
			replyModalPlaceholder: '',
			replyModalReview: null,
			replyModalTarget: null,
			isNavigating: false
		};
	},
	computed: {
		shopNameFirst: function() {
			var name = this.productInfo.shopName;
			return name && name.length > 0 ? name.substring(0, 1) : '店';
		}
	},
	onLoad: function(options) {
		this.resourceId = options.resourceId;
	},
	onReady: function() {
		this.calculateHeight();
	},
	onShow: function() {
		this.loadProductData();
		this.loadCartCount();
	},
	onUnload: function() {
		this.isNavigating = false;
		this.replyModalVisible = false;
	},
	methods: {
		safeNavigate: function(url, type) {
			var self = this;
			if (this.isNavigating) {
				return;
			}
			this.isNavigating = true;

			try {
				if (type === 'switchTab') {
					uni.switchTab({
						url: url,
						success: function() {
							setTimeout(function() {
								self.isNavigating = false;
							}, 500);
						},
						fail: function(err) {
							console.error('跳转失败:', err);
							self.isNavigating = false;
							uni.showToast({ title: '页面跳转失败', icon: 'none' });
						}
					});
				} else {
					var pages = getCurrentPages();
					if (pages.length >= 10) {
						uni.reLaunch({
							url: url,
							success: function() {
								setTimeout(function() {
									self.isNavigating = false;
								}, 500);
							},
							fail: function(err) {
								console.error('跳转失败:', err);
								self.isNavigating = false;
								uni.showToast({ title: '页面跳转失败', icon: 'none' });
							}
						});
					} else {
						uni.navigateTo({
							url: url,
							success: function() {
								setTimeout(function() {
									self.isNavigating = false;
								}, 500);
							},
							fail: function(err) {
								console.error('跳转失败:', err);
								self.isNavigating = false;
								if (err.errMsg && err.errMsg.indexOf('tab') > -1) {
									uni.switchTab({
										url: url
									});
								} else {
									uni.showToast({ title: '页面跳转失败', icon: 'none' });
								}
							}
						});
					}
				}
			} catch (e) {
				console.error('跳转异常:', e);
				this.isNavigating = false;
			}
		},
		calculateHeight: function() {
			var self = this;
			var systemInfo = uni.getSystemInfoSync();
			var windowHeight = systemInfo.windowHeight;
			var screenHeight = systemInfo.screenHeight;
			var safeAreaBottom = systemInfo.safeArea ? systemInfo.safeArea.bottom : screenHeight;
			var safeAreaInsetBottom = screenHeight - safeAreaBottom;

			this.contentHeight = windowHeight - 280;

			this.$nextTick(function() {
				self.calculateReviewScrollHeight(windowHeight, safeAreaInsetBottom);
			});
		},
		calculateReviewScrollHeight: function(windowHeight, safeAreaInsetBottom) {
			var self = this;
			var query = uni.createSelectorQuery().in(self);
			var defaultHeights = {
				searchHeader: 60,
				tabBar: 50,
				reviewStats: 180,
				reviewSortBar: 60,
				commentInputBar: 60,
				bottomBar: 60
			};
			var fixedHeights = {
				searchHeader: 0,
				tabBar: 0,
				reviewStats: 0,
				reviewSortBar: 0,
				commentInputBar: 0,
				bottomBar: 0
			};

			query.select('#search-header').boundingClientRect();
			query.select('#tab-bar').boundingClientRect();
			query.select('#review-stats').boundingClientRect();
			query.select('#review-sort-bar').boundingClientRect();
			query.select('#comment-input-bar').boundingClientRect();
			query.select('#bottom-bar').boundingClientRect();

			query.exec(function(res) {
				try {
					if (res && Array.isArray(res)) {
						if (res[0] && res[0].height) {
							fixedHeights.searchHeader = res[0].height;
						} else {
							fixedHeights.searchHeader = defaultHeights.searchHeader;
						}
						if (res[1] && res[1].height) {
							fixedHeights.tabBar = res[1].height;
						} else {
							fixedHeights.tabBar = defaultHeights.tabBar;
						}
						if (res[2] && res[2].height) {
							fixedHeights.reviewStats = res[2].height;
						} else {
							fixedHeights.reviewStats = defaultHeights.reviewStats;
						}
						if (res[3] && res[3].height) {
							fixedHeights.reviewSortBar = res[3].height;
						} else {
							fixedHeights.reviewSortBar = defaultHeights.reviewSortBar;
						}
						if (res[4] && res[4].height) {
							fixedHeights.commentInputBar = res[4].height;
						} else {
							fixedHeights.commentInputBar = defaultHeights.commentInputBar;
						}
						if (res[5] && res[5].height) {
							fixedHeights.bottomBar = res[5].height;
						} else {
							fixedHeights.bottomBar = defaultHeights.bottomBar;
						}
					} else {
						fixedHeights = Object.assign({}, defaultHeights);
					}

					var totalFixedHeight = fixedHeights.searchHeader +
						fixedHeights.tabBar +
						fixedHeights.reviewStats +
						fixedHeights.reviewSortBar +
						fixedHeights.commentInputBar +
						fixedHeights.bottomBar;

					var reviewScrollHeight = windowHeight - totalFixedHeight - 20;

					if (reviewScrollHeight < 200) {
						reviewScrollHeight = 200;
					}

					if (reviewScrollHeight > windowHeight - 100) {
						reviewScrollHeight = windowHeight - 100;
					}

					self.reviewScrollHeight = Math.floor(reviewScrollHeight);

					console.log('动态高度计算结果:', {
						windowHeight: windowHeight,
						totalFixedHeight: totalFixedHeight,
						reviewScrollHeight: self.reviewScrollHeight,
						fixedHeights: fixedHeights,
						safeAreaInsetBottom: safeAreaInsetBottom
					});

				} catch (e) {
					console.error('计算高度失败:', e);
					var fallbackHeight = windowHeight - 480;
					if (fallbackHeight < 200) {
						fallbackHeight = 200;
					}
					self.reviewScrollHeight = Math.floor(fallbackHeight);
				}
			});
		},
		loadProductData: function() {
			getProductDetail(this.resourceId).then(res => {
				if (res.code === 200 && res.data) {
					this.productInfo = res.data;
					this.loadShopProducts();
					this.loadReviewData();
				}
			}).catch(err => {
				console.error('加载商品详情失败:', err);
				uni.showToast({ title: '加载失败', icon: 'none' });
			});
		},
		loadReviewData: function() {
			this.loadReviewStats();
			this.loadReviewList();
		},
		loadReviewStats: function() {
			getReviewStats(this.resourceId).then(res => {
				if (res.code === 200 && res.data) {
					this.reviewStats = res.data;
				}
			}).catch(err => {
				console.error('加载评价统计失败:', err);
			});
		},
		loadReviewList: function() {
			var query = {
				orderBy: this.reviewOrderBy,
				isAsc: this.reviewIsAsc,
				pageNum: this.reviewPageNum,
				pageSize: this.reviewPageSize
			};
			getReviewList(this.resourceId, query).then(res => {
				if (res.code === 200) {
					this.reviews = res.data.rows || [];
					this.reviewTotal = res.data.total || 0;
				}
			}).catch(err => {
				console.error('加载评价列表失败:', err);
			});
		},
		changeReviewSort: function(orderBy) {
			if (this.reviewOrderBy === orderBy) {
				this.reviewIsAsc = this.reviewIsAsc === 'asc' ? 'desc' : 'asc';
			} else {
				this.reviewOrderBy = orderBy;
				this.reviewIsAsc = 'desc';
			}
			this.reviewPageNum = 1;
			this.loadReviewList();
		},
		handleReviewLike: function(item) {
			var itemId = item.reviewId || item.commentId;
			var idx = this.likedReviews.indexOf(itemId);
			if (idx > -1) {
				this.likedReviews.splice(idx, 1);
				item.likeCount = Math.max(0, (item.likeCount || 0) - 1);
			} else {
				this.likedReviews.push(itemId);
				item.likeCount = (item.likeCount || 0) + 1;
				likeReview(itemId).catch(err => {
					console.error('点赞失败:', err);
				});
			}
		},
		isReviewLiked: function(itemId) {
			return this.likedReviews.indexOf(itemId) > -1;
		},
		goToAllReviews: function() {
			this.safeNavigate('/pages/mall/review_list?resourceId=' + this.resourceId);
		},
		openReplyModal: function(review, targetReply) {
			this.replyModalReview = review;
			this.replyModalTarget = targetReply;
			this.replyModalFocus = false;
			if (targetReply) {
				var name = targetReply.userType == 2 ? (targetReply.shopName || '商家') : this.getMaskedUserName(targetReply.userName);
				this.replyModalTitle = '回复 ' + name;
				this.replyModalPlaceholder = '回复 ' + name + '...';
			} else {
				var name = this.getMaskedUserName(review.userName, review.isAnonymous);
				this.replyModalTitle = '回复 ' + name;
				this.replyModalPlaceholder = '回复 ' + name + '...';
			}
			this.replyModalContent = '';
			this.replyModalVisible = true;
			var self = this;
			setTimeout(function() {
				self.replyModalFocus = true;
			}, 300);
		},
		closeReplyModal: function() {
			this.replyModalVisible = false;
			this.replyModalReview = null;
			this.replyModalTarget = null;
			this.replyModalContent = '';
			this.replyModalFocus = false;
		},
		submitReplyModal: function() {
			var content = this.replyModalContent.trim();
			if (!content) {
				uni.showToast({ title: '请输入回复内容', icon: 'none' });
				return;
			}
			if (!this.replyModalReview) {
				uni.showToast({ title: '请选择要回复的评论', icon: 'none' });
				return;
			}
			var targetId = this.replyModalTarget ? this.replyModalTarget.commentId : 0;
			var self = this;
			addComment({
				reviewId: self.replyModalReview.reviewId,
				parentId: targetId,
				replyToId: targetId,
				userType: 1,
				commentType: 3,
				content: content
			}).then(function(res) {
				if (res.code === 200) {
					uni.showToast({ title: '回复成功', icon: 'success' });
					var newComment = res.data;
					if (!self.replyModalReview.commentList) {
						self.replyModalReview.commentList = [];
					}
					if (newComment) {
						self.replyModalReview.commentList.push(newComment);
					} else {
						self.loadReviewList();
					}
					self.closeReplyModal();
				} else {
					uni.showToast({ title: res.msg || '回复失败', icon: 'none' });
				}
			}).catch(function(err) {
				console.error('回复失败:', err);
				uni.showToast({ title: '回复失败', icon: 'none' });
			});
		},
		submitGlobalComment: function() {
			var content = this.globalInput.trim();
			if (!content) {
				uni.showToast({ title: '请输入评论内容', icon: 'none' });
				return;
			}
			var self = this;
			addComment({
				reviewId: 0,
				parentId: 0,
				replyToId: 0,
				userType: 1,
				commentType: 3,
				content: content
			}).then(function(res) {
				if (res.code === 200) {
					uni.showToast({ title: '评论成功', icon: 'success' });
					self.globalInput = '';
					self.reviewStats.totalCount++;
					self.loadReviewList();
				} else {
					uni.showToast({ title: res.msg || '评论失败', icon: 'none' });
				}
			}).catch(function(err) {
				console.error('评论失败:', err);
				uni.showToast({ title: '评论失败', icon: 'none' });
			});
		},
		formatReviewTime: function(time) {
			if (!time) return '';
			var date = new Date(time);
			var now = new Date();
			var diff = now - date;
			var days = Math.floor(diff / (1000 * 60 * 60 * 24));
			if (days === 0) {
				var hours = Math.floor(diff / (1000 * 60 * 60));
				if (hours === 0) {
					var minutes = Math.floor(diff / (1000 * 60));
					return minutes <= 0 ? '刚刚' : minutes + '分钟前';
				}
				return hours + '小时前';
			} else if (days < 7) {
				return days + '天前';
			} else {
				return date.getFullYear() + '-' + 
					String(date.getMonth() + 1).padStart(2, '0') + '-' + 
					String(date.getDate()).padStart(2, '0');
			}
		},
		getMaskedUserName: function(name, isAnonymous) {
			if (isAnonymous === 1) {
				return '匿名用户';
			}
			if (!name) return '用户';
			if (name.length <= 2) {
				return name.charAt(0) + '*';
			}
			return name.charAt(0) + '***' + name.charAt(name.length - 1);
		},
		timeAgoText: function(time) {
			if (!time) return '';
			var d = new Date(time);
			var now = new Date();
			var diff = now - d;
			var mins = Math.floor(diff / 60000);
			if (mins < 60) return mins + '分钟前';
			var hours = Math.floor(mins / 60);
			if (hours < 24) return hours + '小时前';
			var days = Math.floor(hours / 24);
			return days + '天前';
		},
		getLevel: function(id) {
			if (!id) return 1;
			var levels = [1, 2, 3, 4, 5, 6];
			return levels[id % 6];
		},
		getAvatarColor: function(name) {
			var colors = [
				'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
				'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
				'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
				'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
				'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
				'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)'
			];
			if (!name) return colors[0];
			var code = 0;
			for (var i = 0; i < name.length; i++) {
				code += name.charCodeAt(i);
			}
			return colors[code % colors.length];
		},
		getRandomLoc: function() {
			var locs = ['北京', '上海', '广州', '深圳', '成都', '杭州', '西藏', '四川', '广东', '江西'];
			return locs[Math.floor(Math.random() * locs.length)];
		},
		loadShopProducts: function() {
			var shopId = this.productInfo.shopId;
			if (!shopId) return;
			getShopHotProducts(shopId, 4).then(res => {
				if (res.code === 200) {
					var list = res.data || [];
					this.shopProducts = list.filter(function(item) {
						return item.resourceId != this.resourceId;
					}.bind(this)).slice(0, 4);
				}
			}).catch(err => {
				console.error('加载店铺商品失败:', err);
			});
		},
		handleSearch: function() {
			if (this.keyword && this.keyword.trim()) {
				this.safeNavigate('/pages/mall/index?keyword=' + encodeURIComponent(this.keyword));
			}
		},
		switchTab: function(index) {
			var self = this;
			this.currentTab = index;
			if (index === 1) {
				this.$nextTick(function() {
					self.calculateHeight();
				});
			}
		},
		handleScroll: function(e) {},
		goToShop: function() {
			var shopId = this.productInfo.shopId || 1;
			this.safeNavigate('/pages/store/shop_detail?shopId=' + shopId);
		},
		goToProduct: function(item) {
			this.safeNavigate('/pages/mall/detail?resourceId=' + item.resourceId);
		},
		addToCartFromShop: function(item) {
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
		buyNowFromShop: function(item) {
			this.safeNavigate('/pages/mall/detail?resourceId=' + item.resourceId);
		},
		contactService: function() {
			uni.showToast({ title: '联系客服', icon: 'none' });
		},
		addToCart: function() {
			try {
				var cartData = uni.getStorageSync('mall_cart_list') || [];
				var found = false;
				for (var i = 0; i < cartData.length; i++) {
					if (cartData[i].resourceId === this.productInfo.resourceId) {
						cartData[i].quantity = (cartData[i].quantity || 1) + 1;
						found = true;
						break;
					}
				}
				if (!found) {
					cartData.push({
						resourceId: this.productInfo.resourceId,
						resourceName: this.productInfo.resourceName,
						specification: this.productInfo.specification,
						price: this.productInfo.price,
						categoryName: this.productInfo.categoryName,
						shopId: this.productInfo.shopId,
						shopName: this.productInfo.shopName,
						images: this.productInfo.images,
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
		loadCartCount: function() {
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
		goToCart: function() {
			this.safeNavigate('/pages/mall/cart');
		},
		shareProduct: function() {
			uni.showToast({ title: '分享功能开发中', icon: 'none' });
		},
		buyNow: function() {
			uni.showToast({ title: '立即购买', icon: 'success' });
		}
	}
};
</script>

<style lang="scss" scoped>
.detail-page {
	min-height: 100vh;
	background-color: #f5f5f5;
	display: flex;
	flex-direction: column;
}

.search-header {
	display: flex;
	align-items: center;
	padding: 16rpx 24rpx;
	background-color: #fff;

	.search-input-wrapper {
		flex: 1;
		display: flex;
		align-items: center;
		background-color: #f5f5f5;
		border-radius: 32rpx;
		padding: 14rpx 24rpx;
		margin-right: 16rpx;

		input {
			flex: 1;
			font-size: 26rpx;
		}
	}

	.search-btn {
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

.tab-bar {
	display: flex;
	align-items: center;
	background-color: #fff;
	border-bottom: 1rpx solid #eee;

	.tab-item {
		flex: 1;
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 24rpx 0;
		font-size: 30rpx;
		color: #666;
		position: relative;

		&.active {
			color: #333;
			font-weight: bold;

			&::after {
				content: '';
				position: absolute;
				bottom: 12rpx;
				left: 50%;
				transform: translateX(-50%);
				width: 40rpx;
				height: 6rpx;
				background-color: #ff6b35;
				border-radius: 3rpx;
			}
		}
	}
}

.content-scroll {
	flex: 1;
}

.tab-content {
	background-color: #f5f5f5;
}

.product-showcase {
	.showcase-banner {
		display: flex;
		background: linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%);
		padding: 30rpx;
		color: #fff;

		.banner-left {
			flex: 1;

			.banner-title {
				font-size: 40rpx;
				font-weight: bold;
				display: block;
				margin-bottom: 10rpx;
			}

			.banner-subtitle {
				font-size: 26rpx;
				opacity: 0.9;
				display: block;
			}
		}

		.banner-right {
			width: 200rpx;
			margin-left: 20rpx;

			.product-image-real {
				width: 180rpx;
				height: 180rpx;
				border-radius: 16rpx;
				margin-bottom: 10rpx;
			}

			.product-image-placeholder {
				width: 180rpx;
				height: 180rpx;
				background-color: rgba(255, 255, 255, 0.2);
				border-radius: 16rpx;
				display: flex;
				align-items: center;
				justify-content: center;
				margin-bottom: 10rpx;

				text {
					font-size: 48rpx;
					font-weight: bold;
				}
			}

			.spec-tag {
				background-color: rgba(255, 255, 255, 0.3);
				padding: 6rpx 16rpx;
				border-radius: 8rpx;
				text-align: center;

				text {
					font-size: 22rpx;
				}
			}
		}
	}
}

.price-section {
	background-color: #fff;
	padding: 24rpx;
	margin-top: 2rpx;

	.price-row {
		margin-bottom: 16rpx;
		display: flex;
		align-items: baseline;
		gap: 16rpx;

		.current-price {
			font-size: 48rpx;
			color: #ff6b35;
			font-weight: bold;
		}

		.original-price {
			font-size: 28rpx;
			color: #999;
			text-decoration: line-through;
		}
	}

	.title-section {
		.product-title {
			font-size: 30rpx;
			color: #333;
			line-height: 1.5;
		}
	}
}

.service-section {
	background-color: #fff;
	padding: 0 24rpx;
	margin-top: 16rpx;

	.service-row {
		display: flex;
		align-items: center;
		padding: 24rpx 0;
		border-bottom: 1rpx solid #f5f5f5;

		.service-tag {
			font-size: 28rpx;
			color: #999;
			margin-right: 10rpx;
		}

		.service-label {
			font-size: 28rpx;
			color: #ff6b35;
			font-weight: bold;
			margin-right: 12rpx;
		}

		.service-desc {
			flex: 1;
			font-size: 24rpx;
			color: #666;
			overflow: hidden;
			text-overflow: ellipsis;
			white-space: nowrap;
		}

		.service-arrow {
			font-size: 26rpx;
			color: #ccc;
			margin-left: 8rpx;
		}
	}

	.location-row {
		display: flex;
		align-items: center;
		padding: 20rpx 0;

		.loc-icon {
			font-size: 28rpx;
			color: #999;
			margin-right: 10rpx;
		}

		.loc-city {
			font-size: 26rpx;
			color: #333;
			margin-right: 16rpx;
		}

		.loc-free {
			font-size: 24rpx;
			color: #52c41a;
			margin-right: auto;
		}

		.share-area {
			display: flex;
			align-items: center;

			.share-icon-text {
				font-size: 32rpx;
				color: #52c41a;
				margin-right: 6rpx;
			}

			.share-label {
				font-size: 24rpx;
				color: #666;
			}
		}
	}
}

.params-section {
	background-color: #fff;
	padding: 24rpx;
	margin-top: 16rpx;

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		display: block;
		margin-bottom: 20rpx;
	}

	.param-grid {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		gap: 16rpx;

		.param-item {
			display: flex;
			align-items: center;

			.param-label {
				font-size: 26rpx;
				color: #999;
				width: 140rpx;
			}

			.param-value {
				font-size: 26rpx;
				color: #333;
				flex: 1;
			}
		}
	}
}

.ai-summary {
	margin-top: 24rpx;
	padding: 20rpx;
	background-color: #f9f9f9;
	border-radius: 12rpx;

	.ai-tag {
		font-size: 24rpx;
		color: #ff6b35;
		font-weight: bold;
		margin-right: 12rpx;
	}

	.summary-text {
		font-size: 26rpx;
		color: #666;
		line-height: 1.6;
	}
}

.review-section {
		background-color: #fff;
	}

	.review-stats {
		display: flex;
		padding: 24rpx;
		border-bottom: 1rpx solid #f0f0f0;

		.stats-left {
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			padding-right: 40rpx;
			border-right: 1rpx solid #f0f0f0;
			min-width: 140rpx;

			.avg-rating {
				font-size: 56rpx;
				font-weight: bold;
				color: #ff6b35;
			}

			.rating-label {
				font-size: 24rpx;
				color: #999;
				margin-top: 8rpx;
			}
		}

		.stats-right {
			flex: 1;
			padding-left: 30rpx;

			.rating-bar {
				display: flex;
				align-items: center;
				margin-bottom: 10rpx;

				.bar-label {
					font-size: 22rpx;
					color: #999;
					width: 60rpx;
				}

				.bar-track {
					flex: 1;
					height: 12rpx;
					background-color: #f0f0f0;
					border-radius: 6rpx;
					margin: 0 12rpx;
					overflow: hidden;

					.bar-fill {
						height: 100%;
						background: linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%);
						border-radius: 6rpx;
					}
				}

				.bar-count {
					font-size: 22rpx;
					color: #999;
					width: 60rpx;
					text-align: right;
				}
			}
		}
	}

	.review-sort-bar {
		display: flex;
		align-items: center;
		padding: 20rpx 24rpx;
		background-color: #fafafa;
		border-bottom: 1rpx solid #f0f0f0;

		.sort-item {
			display: flex;
			align-items: center;
			padding: 10rpx 20rpx;
			margin-right: 16rpx;
			border-radius: 24rpx;
			background-color: #fff;
			border: 1rpx solid #e0e0e0;

			text {
				font-size: 24rpx;
				color: #666;
			}

			.sort-arrow {
				margin-left: 6rpx;
				color: #999;
			}

			&.active {
				background-color: #fff5f0;
				border-color: #ff6b35;

				text {
					color: #ff6b35;
				}
			}
		}

		.total-count {
			margin-left: auto;
			font-size: 24rpx;
			color: #999;
		}
	}

	.review-scroll {
		flex: 1;
	}

	.review-list {
		padding: 0 28rpx 20rpx 28rpx;
		background-color: #f8f8f8;

		.comment-card {
			padding: 32rpx 24rpx;
			margin-bottom: 16rpx;
			background-color: #fff;
			border-radius: 16rpx;

			.card-top-row {
				display: flex;
				align-items: center;
				justify-content: space-between;
				margin-bottom: 20rpx;

				.user-left {
					display: flex;
					align-items: center;

					.avatar-circle {
						width: 68rpx;
						height: 68rpx;
						border-radius: 50%;
						display: flex;
						align-items: center;
						justify-content: center;
						margin-right: 14rpx;

						text {
							color: #fff;
							font-size: 28rpx;
							font-weight: bold;
						}
					}

					.username {
						font-size: 30rpx;
						color: #ff69b4;
						font-weight: 600;
						margin-right: 10rpx;
					}

					.level-badge {
						background-color: #ff9500;
						border-radius: 8rpx;
						padding: 3rpx 12rpx;

						text {
							font-size: 20rpx;
							color: #fff;
							font-weight: bold;
						}
					}
				}

				.floor-badge {
					display: flex;
					align-items: center;
					gap: 8rpx;

					.floor-avatar-sm {
						width: 40rpx;
						height: 40rpx;
						border-radius: 50%;
						display: flex;
						align-items: center;
						justify-content: center;

						text {
							color: #fff;
							font-size: 18rpx;
							font-weight: bold;
						}
					}

					.floor-num-text {
						font-size: 20rpx;
						color: #ff9500;
						font-weight: bold;
					}
				}
			}

			.card-content {
				font-size: 30rpx;
				color: #222;
				line-height: 1.75;
				display: block;
				margin-bottom: 20rpx;
				word-break: break-all;
			}

			.image-area {
				display: flex;
				gap: 14rpx;
				margin-bottom: 16rpx;

				.img-box-lg {
					width: 420rpx;
					height: 320rpx;
					border-radius: 12rpx;
					overflow: hidden;
					position: relative;

					.img-placeholder-lg {
						width: 100%;
						height: 100%;
						background: linear-gradient(135deg, #d0d0d0 0%, #eee 100%);
					}

					.exclusive-tag {
						position: absolute;
						top: 14rpx;
						right: 14rpx;
						background-color: rgba(255, 255, 255, 0.95);
						border-radius: 20rpx;
						padding: 4rpx 16rpx;
						box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.15);

						text {
							font-size: 22rpx;
							color: #333;
							font-weight: 500;
						}
					}
				}
			}

			.spec-line {
				margin-bottom: 14rpx;

				text {
					font-size: 24rpx;
					color: #999;
				}
			}

			.card-bottom-row {
				display: flex;
				align-items: center;
				justify-content: space-between;
				margin-top: 12rpx;

				.bottom-left {
					display: flex;
					align-items: center;

					.time-text {
						font-size: 24rpx;
						color: #bbb;
					}

					.loc-text {
						font-size: 24rpx;
						color: #bbb;
						margin-left: 12rpx;
					}

					.reply-btn-sm {
						margin-left: 20rpx;
						padding: 6rpx 20rpx;
						border: 1rpx solid #ddd;
						border-radius: 20rpx;

						text {
							font-size: 24rpx;
							color: #888;
						}
					}
				}

				.bottom-right {
					display: flex;
					align-items: center;

					.vote-btn {
						display: flex;
						align-items: center;
						padding: 8rpx 14rpx;
						border-radius: 8rpx;
						margin-left: 12rpx;

						.vote-icon {
							font-size: 28rpx;
							margin-right: 6rpx;
						}

						.vote-count {
							font-size: 24rpx;
							color: #999;
						}

						&.voted .vote-count {
							color: #333;
						}

						&.down .vote-icon {
							opacity: 0.45;
						}
					}

					.more-dot-btn {
						margin-left: 12rpx;
						padding: 8rpx 10rpx;

						text {
							font-size: 32rpx;
							color: #ccc;
						}
					}
				}
			}

			.reply-section {
				margin-top: 24rpx;
				padding: 20rpx;
				background-color: #f9f9f9;
				border-radius: 14rpx;

				.reply-header-info {
					display: flex;
					align-items: center;
					justify-content: space-between;
					margin-bottom: 16rpx;
					padding-bottom: 12rpx;
					border-bottom: 1rpx solid #eee;

					.reply-count-text {
						font-size: 25rpx;
						color: #666;
					}

					.reply-sort-hint {
						text {
							font-size: 23rpx;
							color: #999;
						}
					}
				}

				.reply-item {
					padding: 16rpx 0;
					border-bottom: 1rpx solid #f0f0f0;

					&:last-child {
						border-bottom: none;
					}

					.rp-header-row {
						display: flex;
						align-items: center;
						justify-content: space-between;
						margin-bottom: 8rpx;

						.rp-user-left {
							display: flex;
							align-items: center;
							flex: 1;

							.rp-avatar-circle {
								width: 48rpx;
								height: 48rpx;
								border-radius: 50%;
								display: flex;
								align-items: center;
								justify-content: center;
								margin-right: 12rpx;
								flex-shrink: 0;

								&.merchant {
									background-color: #ff6b35 !important;
								}

								text {
									color: #fff;
									font-size: 22rpx;
									font-weight: bold;
								}
							}

							.rp-name-text {
								font-size: 27rpx;
								color: #333;
								font-weight: 500;
								margin-right: 10rpx;

								&.merchant {
									color: #ff6b35;
									font-weight: bold;
								}
							}

							.rp-level-badge {
								background-color: #ff9500;
								border-radius: 6rpx;
								padding: 2rpx 10rpx;
								margin-right: 10rpx;

								&.merchant {
									background-color: #ff6b35;
								}

								text {
									font-size: 18rpx;
									color: #fff;
									font-weight: bold;
								}
							}

							.rp-floor-small {
								display: flex;
								align-items: center;
								gap: 2rpx;

								.rp-floor-sep {
									font-size: 18rpx;
									color: #ccc;
								}

								.rp-floor-id {
									font-size: 18rpx;
									color: #bbb;
								}
							}
						}

						.rp-time-text {
							font-size: 22rpx;
							color: #bbb;
							flex-shrink: 0;
						}
					}

					.rp-body-content {
						font-size: 27rpx;
						color: #444;
						line-height: 1.65;
						display: block;
						padding-left: 60rpx;
						margin-bottom: 10rpx;
					}

					.rp-meta-row {
						display: flex;
						align-items: center;
						padding-left: 60rpx;

						.rp-loc-text {
							font-size: 22rpx;
							color: #bbb;
							margin-right: 20rpx;
						}

						.rp-reply-btn-sm {
							margin-right: auto;
							padding: 4rpx 16rpx;
							border: 1rpx solid #ddd;
							border-radius: 16rpx;

							text {
								font-size: 22rpx;
								color: #888;
							}
						}

						.rp-vote-group {
							display: flex;
							align-items: center;
							gap: 4rpx;

							.rp-vote-inner {
								display: flex;
								align-items: center;
								padding: 6rpx 10rpx;
								border-radius: 8rpx;

								.rp-vote-icon {
									font-size: 24rpx;
									margin-right: 4rpx;
								}

								.rp-vote-num {
									font-size: 21rpx;
									color: #999;
								}

								&.voted .rp-vote-num {
									color: #333;
								}

								&.down .rp-vote-icon {
									opacity: 0.4;
								}
							}
						}
					}
				}
			}
		}

		.empty-review {
			padding: 80rpx 0;
			text-align: center;

			text {
				font-size: 28rpx;
				color: #999;
			}
		}

		.more-reviews-btn {
			padding: 24rpx;
			text-align: center;

			text {
				font-size: 28rpx;
				color: #ff6b35;
				font-weight: bold;
			}
		}
	}

.shop-card {
	background-color: #fff;
	margin: 16rpx 24rpx;
	border-radius: 16rpx;
	padding: 24rpx;

	.shop-header {
		display: flex;
		align-items: center;

		.shop-logo {
			width: 80rpx;
			height: 80rpx;
			background: linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%);
			border-radius: 50%;
			display: flex;
			align-items: center;
			justify-content: center;
			margin-right: 20rpx;

			text {
				color: #fff;
				font-size: 32rpx;
				font-weight: bold;
			}
		}

		.shop-info {
			flex: 1;

			.shop-name {
				font-size: 30rpx;
				color: #333;
				font-weight: bold;
				display: block;
			}

			.shop-desc {
				font-size: 24rpx;
				color: #999;
				margin-top: 6rpx;
				display: block;
			}
		}

		.enter-shop-btn {
			background: linear-gradient(135deg, #ff6b35 0%, #ff4444 100%);
			padding: 12rpx 32rpx;
			border-radius: 30rpx;

			text {
				color: #fff;
				font-size: 26rpx;
				font-weight: bold;
			}
		}
	}
}

.shop-products-section {
	background-color: #fff;
	margin: 16rpx 24rpx;
	border-radius: 16rpx;
	padding: 24rpx;

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		display: block;
		margin-bottom: 20rpx;
	}

	.shop-product-list {
		.shop-product-card {
			display: flex;
			background-color: #fff;
			border-radius: 16rpx;
			margin-bottom: 20rpx;
			padding: 16rpx;

			&:last-child {
				margin-bottom: 0;
			}

			.spc-left {
				width: 160rpx;
				flex-shrink: 0;
				margin-right: 16rpx;

				.product-image-box {
					width: 160rpx;
					height: 160rpx;
					background: linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%);
					border-radius: 12rpx;
					position: relative;
					display: flex;
					align-items: center;
					justify-content: center;
					overflow: hidden;

					.image-text {
						color: #fff;
						font-size: 32rpx;
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

			.spc-right {
				flex: 1;
				display: flex;
				flex-direction: column;
				justify-content: space-between;
				min-width: 0;

				.spc-name {
					font-size: 26rpx;
					color: #333;
					line-height: 1.4;
					display: block;
					overflow: hidden;
					text-overflow: ellipsis;
					white-space: nowrap;
				}

				.spc-price {
					font-size: 34rpx;
					color: #ff6b35;
					font-weight: bold;
					margin-top: 8rpx;
				}

				.spc-actions {
					display: flex;
					align-items: center;
					justify-content: flex-end;
					margin-top: 12rpx;
					gap: 12rpx;

					.add-cart-btn {
						background: linear-gradient(135deg, #ffb347 0%, #ffcc33 100%);
						padding: 10rpx 20rpx;
						border-radius: 28rpx;

						text {
							color: #fff;
							font-size: 22rpx;
							font-weight: bold;
						}
					}

					.buy-btn {
						background: linear-gradient(135deg, #ff6b35 0%, #ff4444 100%);
						padding: 10rpx 28rpx;
						border-radius: 28rpx;

						text {
							color: #fff;
							font-size: 22rpx;
							font-weight: bold;
						}
					}
				}
			}
		}

		.empty-shop-products {
			padding: 40rpx 0;
			text-align: center;

			text {
				color: #999;
				font-size: 26rpx;
			}
		}
	}
}

.detail-content {
	background-color: #fff;
	margin: 16rpx 24rpx;
	border-radius: 16rpx;
	padding: 24rpx;

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		display: block;
		margin-bottom: 20rpx;
	}

	.description-block {
		.desc-text {
			font-size: 28rpx;
			color: #666;
			line-height: 1.8;
		}
	}
}

.bottom-bar {
	display: flex;
	align-items: center;
	padding: 16rpx 24rpx;
	background-color: #fff;
	border-top: 1rpx solid #eee;

	.bar-left {
		display: flex;
		gap: 24rpx;

		.bar-btn {
			display: flex;
			flex-direction: column;
			align-items: center;
			position: relative;

			.btn-icon {
				font-size: 36rpx;
				color: #666;
			}

			.btn-text {
				font-size: 20rpx;
				color: #666;
				margin-top: 4rpx;
			}

			.cart-badge {
				position: absolute;
				top: -6rpx;
				right: -10rpx;
				min-width: 32rpx;
				height: 32rpx;
				background: linear-gradient(135deg, #ff4444 0%, #ff6b35 100%);
				border-radius: 16rpx;
				display: flex;
				align-items: center;
				justify-content: center;
				padding: 0 8rpx;

				text {
					color: #fff;
					font-size: 18rpx;
					font-weight: bold;
				}
			}
		}
	}

	.bar-right {
		flex: 1;
		display: flex;
		justify-content: flex-end;
		gap: 16rpx;

		.add-cart-btn {
			background: linear-gradient(135deg, #ffb347 0%, #ffcc33 100%);
			padding: 16rpx 32rpx;
			border-radius: 32rpx;

			text {
				color: #fff;
				font-size: 26rpx;
				font-weight: bold;
			}
		}

		.buy-now-btn {
			background: linear-gradient(135deg, #ff6b35 0%, #ff4444 100%);
			padding: 16rpx 32rpx;
			border-radius: 32rpx;

			text {
				color: #fff;
				font-size: 26rpx;
				font-weight: bold;
			}
		}
	}
}

.comment-input-bar {
	position: fixed;
	left: 0;
	right: 0;
	bottom: 112rpx;
	z-index: 200;
	padding: 8rpx 24rpx;
	background-color: #fff;
	border-top: 1rpx solid #e8e8e8;
	box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);

	.cibar-inner {
		display: flex;
		align-items: center;
		background-color: #f5f5f5;
		border-radius: 40rpx;
		padding: 4rpx 4rpx 4rpx 28rpx;

		.cibar-input {
			flex: 1;
			height: 64rpx;
			font-size: 26rpx;
			color: #333;
		}

		.cibar-send {
			padding: 14rpx 28rpx;
			border-radius: 36rpx;
			background-color: #e0e0e0;

			text {
				font-size: 26rpx;
				color: #fff;
				font-weight: 500;
			}

			&.active {
				background: linear-gradient(135deg, #ff6b35 0%, #ff4444 100%);
			}
		}
	}
}

.reply-modal-overlay {
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: rgba(0, 0, 0, 0.4);
	z-index: 300;
}

.reply-modal {
	position: fixed;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 301;
	background-color: #fff;
	border-radius: 24rpx 24rpx 0 0;
	padding: 24rpx 28rpx;
	padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
	transform: translateY(100%);
	transition: transform 0.3s ease;

	&.show {
		transform: translateY(0);
	}

	.rm-header {
		display: flex;
		align-items: center;
		justify-content: space-between;
		margin-bottom: 20rpx;

		.rm-title {
			font-size: 30rpx;
			color: #333;
			font-weight: 600;
		}

		.rm-close {
			width: 52rpx;
			height: 52rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			background-color: #f5f5f5;
			border-radius: 50%;

			text {
				font-size: 28rpx;
				color: #999;
			}
		}
	}

	.rm-body {
		display: flex;
		align-items: flex-start;
		margin-bottom: 20rpx;

		.rm-avatar {
			width: 56rpx;
			height: 56rpx;
			border-radius: 50%;
			display: flex;
			align-items: center;
			justify-content: center;
			margin-right: 16rpx;
			flex-shrink: 0;

			text {
				color: #fff;
				font-size: 24rpx;
				font-weight: bold;
			}
		}

		.rm-input-wrap {
			flex: 1;
			background-color: #f7f7f7;
			border-radius: 12rpx;
			padding: 16rpx 20rpx;

			.rm-input {
				width: 100%;
				min-height: 72rpx;
				font-size: 28rpx;
				color: #333;
			}
		}
	}

	.rm-footer {
		display: flex;
		align-items: center;
		justify-content: space-between;

		.rm-hint {
			font-size: 24rpx;
			color: #bbb;
		}

		.rm-send-btn {
			padding: 14rpx 40rpx;
			border-radius: 32rpx;
			background-color: #e0e0e0;

			text {
				font-size: 28rpx;
				color: #fff;
				font-weight: 600;
			}

			&.active {
				background: linear-gradient(135deg, #ff6b35 0%, #ff4444 100%);
			}
		}
	}
}
</style>
