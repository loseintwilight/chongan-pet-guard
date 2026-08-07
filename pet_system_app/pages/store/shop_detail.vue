<template>
	<view class="shop-detail-container">
		<!-- 店铺头部信息 -->
		<view class="shop-header">
			<view class="shop-image-container">
				<image :src="getImageUrl(shopInfo.mainImage)" class="shop-image" mode="aspectFill" />
				<view class="status-badge" v-if="shopInfo.status === 0" style="background: linear-gradient(135deg, #4caf50, #8bc34a)">
					<text>正常营业</text>
				</view>
				<view class="status-badge" v-else-if="shopInfo.status === 1" style="background: linear-gradient(135deg, #ff9800, #ffc107)">
					<text>休息中</text>
				</view>
			</view>
			<view class="shop-basic-info">
				<view class="shop-name">{{ shopInfo.shopName }}</view>
				<view class="shop-rating">
					<text class="rating-stars">★★★★★</text>
					<text class="rating-text">5.0分</text>
				</view>
			</view>
		</view>

		<!-- 详细信息卡片 -->
		<view class="info-section">
			<view class="section-title">
				<text class="title-icon">📍</text>
				<text class="title-text">店铺地址</text>
			</view>
			<view class="info-card">
				<text class="address-text">{{ shopInfo.province }}{{ shopInfo.city }}{{ shopInfo.district }}{{ shopInfo.address }}</text>
			</view>
		</view>

		<view class="info-section">
			<view class="section-title">
				<text class="title-icon">👤</text>
				<text class="title-text">负责人信息</text>
			</view>
			<view class="info-card">
				<view class="info-item">
					<text class="info-label">姓名：</text>
					<text class="info-value">{{ shopInfo.legalName }}</text>
				</view>
				<view class="info-item">
					<text class="info-label">电话：</text>
					<text class="info-value">{{ shopInfo.legalPhone }}</text>
				</view>
			</view>
		</view>

		<view class="info-section">
			<view class="section-title">
				<text class="title-icon">⏰</text>
				<text class="title-text">营业时间</text>
			</view>
			<view class="info-card">
				<text class="info-value">{{ shopInfo.bussinessHours }}</text>
			</view>
		</view>

		<view class="info-section">
			<view class="section-title">
				<text class="title-icon">📝</text>
				<text class="title-text">店铺简介</text>
			</view>
			<view class="info-card">
				<!-- 使用 Markdown 渲染的简介 -->
				<rich-text :nodes="markdownHtml" class="description-content"></rich-text>
			</view>
		</view>

		<!-- 服务项目 -->
		<view class="info-section">
			<view class="section-title">
				<text class="title-icon">🐾</text>
				<text class="title-text">服务项目</text>
			</view>
			<view class="services-grid">
				<view class="service-item" v-for="(service, index) in services" :key="index">
					<text class="service-icon">{{ service.icon }}</text>
					<text class="service-name">{{ service.name }}</text>
				</view>
			</view>
		</view>

		<!-- 店铺环境图片 -->
		<view class="info-section" v-if="shopImages.length > 0">
			<view class="section-title">
				<text class="title-icon">📷</text>
				<text class="title-text">店铺环境</text>
			</view>
			<view class="images-grid">
				<image v-for="(image, index) in shopImages" :key="index" :src="getImageUrl(image)" class="environment-image" mode="aspectFill" @click="previewImage(index)" />
			</view>
		</view>

		<!-- 底部操作栏 -->
		<view class="bottom-actions">

			<view class="action-btn shop-products-btn" @click="goToShopProducts">
				<text class="btn-icon">🛒</text>
				<text class="btn-text">该商店商品</text>
			</view>

			<view class="action-btn contact-btn" @click="makePhoneCall">
				<text class="btn-icon">📞</text>
				<text class="btn-text">联系店铺</text>
			</view>
			<!-- 			<view class="action-btn booking-btn" @click="navigateToBooking">
				<text class="btn-icon">📅</text>
				<text class="btn-text">立即预约</text>
			</view> -->

		</view>
	</view>
</template>

<script>
import { getShop } from '@/api/pet/shop';
import { marked } from 'marked';
import config from '@/config';

export default {
	data() {
		return {
			shopId: null,
			shopInfo: {},
			shopImages: [],
			markdownHtml: '',
			services: [
				{ icon: '🏠', name: '宠物寄养' },
				{ icon: '🛁', name: '宠物洗护' },
				{ icon: '✂️', name: '美容造型' },
				{ icon: '🏥', name: '健康检查' },
				{ icon: '🚗', name: '接送服务' },
				{ icon: '🎾', name: '运动训练' }
			]
		};
	},
	watch: {
		'shopInfo.description': {
			handler(newVal) {
				this.parseMarkdown(newVal);
			},
			immediate: true
		}
	},
	onLoad(options) {
		this.shopId = options.shopId;
		if (this.shopId) {
			this.getShopDetail();
		}
	},
	methods: {
		/** 获取店铺详情 */
		getShopDetail() {
			uni.showLoading({
				title: '加载中...'
			});

			getShop(this.shopId)
				.then((response) => {
					if (response.code === 200) {
						this.shopInfo = response.data;
						// 处理店铺图片
						if (this.shopInfo.images) {
							this.shopImages = this.shopInfo.images.split(',');
						}
					} else {
						uni.showToast({
							title: '加载失败: ' + (response.msg || '未知错误'),
							icon: 'none'
						});
					}
				})
				.catch((error) => {
					uni.showToast({
						title: '加载失败: ' + (error.message || '网络错误'),
						icon: 'none'
					});
					console.error('获取店铺详情失败:', error);
				})
				.finally(() => {
					uni.hideLoading();
				});
		},

		/** 解析 Markdown */
		parseMarkdown(content) {
			if (!content) {
				this.markdownHtml = '<p style="color: #666;">暂无简介</p>';
				return;
			}

			try {
				// 配置 marked 选项
				marked.setOptions({
					breaks: true, // 支持换行
					gfm: true, // 支持 GitHub 风格的 Markdown
					sanitize: false // 允许 HTML 标签
				});

				// 解析 Markdown
				const html = marked(content);

				// 添加自定义样式
				this.markdownHtml = this.addCustomStyles(html);
			} catch (error) {
				console.error('Markdown 解析失败:', error);
				this.markdownHtml = '<p style="color: #ff4444;">简介格式错误</p>';
			}
		},

		/** 添加自定义样式 */
		addCustomStyles(html) {
			// 为 Markdown 元素添加样式
			return html
				.replace(/<h1>/g, '<h1 style="font-size: 32rpx; font-weight: bold; color: #1976d2; margin: 20rpx 0 15rpx 0;">')
				.replace(/<h2>/g, '<h2 style="font-size: 28rpx; font-weight: bold; color: #1976d2; margin: 18rpx 0 12rpx 0;">')
				.replace(/<h3>/g, '<h3 style="font-size: 26rpx; font-weight: bold; color: #333; margin: 15rpx 0 10rpx 0;">')
				.replace(/<p>/g, '<p style="font-size: 26rpx; color: #333; line-height: 1.6; margin: 10rpx 0;">')
				.replace(/<ul>/g, '<ul style="margin: 10rpx 0; padding-left: 30rpx;">')
				.replace(/<ol>/g, '<ol style="margin: 10rpx 0; padding-left: 30rpx;">')
				.replace(/<li>/g, '<li style="font-size: 26rpx; color: #333; line-height: 1.6; margin: 8rpx 0;">')
				.replace(/<blockquote>/g, '<blockquote style="border-left: 4rpx solid #1976d2; padding-left: 20rpx; margin: 15rpx 0; color: #666; font-style: italic;">')
				.replace(/<code>/g, '<code style="background: #f5f5f5; padding: 4rpx 8rpx; border-radius: 6rpx; font-size: 24rpx; color: #e91e63;">')
				.replace(/<pre>/g, '<pre style="background: #f5f5f5; padding: 15rpx; border-radius: 10rpx; overflow-x: auto; margin: 15rpx 0;">')
				.replace(/<strong>/g, '<strong style="color: #1976d2; font-weight: bold;">')
				.replace(/<em>/g, '<em style="font-style: italic; color: #666;">');
		},

		/** 获取图片URL */
		getImageUrl(fileName) {
			if (!fileName) return '/static/images/placeholder.png';
			return config.getDownloadUrl(fileName);
		},

		/** 拨打电话 */
		makePhoneCall() {
			if (!this.shopInfo.legalPhone) {
				uni.showToast({
					title: '暂无联系电话',
					icon: 'none'
				});
				return;
			}

			uni.makePhoneCall({
				phoneNumber: this.shopInfo.legalPhone,
				fail: (error) => {
					console.error('拨打电话失败:', error);
					uni.showToast({
						title: '拨打电话失败',
						icon: 'none'
					});
				}
			});
		},

		/** 跳转到预约页面 */
		navigateToBooking() {
			uni.navigateTo({
				url: '/pages/booking/booking_form?shopId=' + this.shopId + '&shopName=' + encodeURIComponent(this.shopInfo.shopName)
			});
		},


		/** 跳转到店铺商品列表 */
		goToShopProducts() {
			uni.navigateTo({
				url: '/pages/mall/shop_products?shopId=' + this.shopId + '&shopName=' + encodeURIComponent(this.shopInfo.shopName)
			});
		},

		/** 预览图片 */
		previewImage(current) {
			const urls = this.shopImages.map((image) => this.getImageUrl(image));
			uni.previewImage({
				current: current,
				urls: urls
			});
		}
	}
};
</script>

<style lang="scss" scoped>
.shop-detail-container {
	min-height: 100vh;
	background: linear-gradient(135deg, #e0f7fa 0%, #b2ebf2 100%);
	padding-bottom: 120rpx;
}

/* 店铺头部 */
.shop-header {
	background: rgba(255, 255, 255, 0.9);
	backdrop-filter: blur(10px);
	margin: 20rpx;
	border-radius: 20rpx;
	overflow: hidden;
	box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.1);
}

.shop-image-container {
	position: relative;
	height: 400rpx;
	overflow: hidden;
}

.shop-image {
	width: 100%;
	height: 100%;
	object-fit: cover;
}

.status-badge {
	position: absolute;
	top: 20rpx;
	right: 20rpx;
	padding: 10rpx 20rpx;
	border-radius: 20rpx;
	font-size: 24rpx;
	font-weight: bold;
	color: white;
	text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
}

.shop-basic-info {
	padding: 30rpx;
}

.shop-name {
	font-size: 36rpx;
	font-weight: bold;
	color: #1976d2;
	margin-bottom: 15rpx;
}

.shop-rating {
	display: flex;
	align-items: center;
	gap: 10rpx;
}

.rating-stars {
	font-size: 28rpx;
	color: #ffa726;
}

.rating-text {
	font-size: 24rpx;
	color: #666;
}

/* 信息区域 */
.info-section {
	margin: 20rpx;
}

.section-title {
	display: flex;
	align-items: center;
	gap: 15rpx;
	margin-bottom: 15rpx;
}

.title-icon {
	font-size: 28rpx;
}

.title-text {
	font-size: 28rpx;
	font-weight: bold;
	color: #1976d2;
}

.info-card {
	background: rgba(255, 255, 255, 0.9);
	backdrop-filter: blur(10px);
	border-radius: 20rpx;
	padding: 25rpx;
	box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.1);
}

.info-item {
	display: flex;
	margin-bottom: 15rpx;
}

.info-item:last-child {
	margin-bottom: 0;
}

.info-label {
	font-size: 26rpx;
	color: #666;
	width: 120rpx;
}

.info-value {
	font-size: 26rpx;
	color: #333;
	flex: 1;
}

.address-text {
	font-size: 26rpx;
	color: #333;
	line-height: 1.6;
}

/* Markdown 内容样式 */
.description-content {
	font-size: 26rpx;
	color: #333;
	line-height: 1.6;
}

/* 服务项目 */
.services-grid {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 15rpx;
}

.service-item {
	background: rgba(255, 255, 255, 0.9);
	backdrop-filter: blur(10px);
	border-radius: 15rpx;
	padding: 20rpx;
	text-align: center;
	box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.1);
	transition: all 0.3s ease;
}

.service-item:active {
	transform: scale(0.95);
}

.service-icon {
	font-size: 32rpx;
	display: block;
	margin-bottom: 10rpx;
}

.service-name {
	font-size: 24rpx;
	color: #333;
}

/* 环境图片 */
.images-grid {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 10rpx;
}

.environment-image {
	width: 100%;
	height: 200rpx;
	border-radius: 15rpx;
	object-fit: cover;
}

/* 底部操作栏 */
.bottom-actions {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(10px);
	padding: 20rpx;
	display: flex;
	gap: 20rpx;
	box-shadow: 0 -4rpx 15rpx rgba(0, 0, 0, 0.1);
}

.action-btn {
	flex: 1;
	border-radius: 50rpx;
	padding: 20rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 10rpx;
	transition: all 0.3s ease;
}

.action-btn:active {
	transform: scale(0.95);
}

.contact-btn {
	background: rgba(25, 118, 210, 0.1);
	border: 2rpx solid #1976d2;
}


	.shop-products-btn {
		background: linear-gradient(135deg, #ff6b35 0%, #ff8c42 100%);

		.btn-text {
			color: white;
		}
	}

	.booking-btn {

	background: linear-gradient(135deg, #1976d2, #42a5f5);
}

.btn-icon {
	font-size: 28rpx;
}

.btn-text {
	font-size: 26rpx;
	font-weight: bold;
}

.contact-btn .btn-text {
	color: #1976d2;
}

.booking-btn .btn-text {
	color: white;
}
</style>