<template>
	<view class="article-list-page">
		<!-- 顶部科技感导航栏 -->
		<view class="top-nav">
			<view class="nav-decoration left">🐾</view>
			<text class="nav-title">🤖 宠物科普中心</text>
			<view class="nav-decoration right">🌟</view>
		</view>

		<!-- 下拉刷新容器 -->
		<scroll-view
			scroll-y
			:style="{ height: scrollHeight }"
			:scroll-top="scrollTop"
			:scroll-with-animation="true"
			@scroll="scroll"
			@refresherrefresh="onRefresherRefresh"
			:refresher-enabled="true"
			:refresher-triggered="isRefreshing"
			:refresher-default-style="'none'"
			:refresher-background="'#0a0e17'">
			<!-- 加载状态（科技感动效） -->
			<view v-if="loading" class="loading-container">
				<view class="loading-circle"></view>
				<text class="loading-text">正在加载科普宠物知识🚀</text>
			</view>

			<!-- 错误状态（科技感版） -->
			<view v-if="error" class="error-container">
				<text class="error-icon">🤖</text>
				<text class="error-title">加载失败</text>
				<text class="error-desc">{{ errorMsg || '网络连接异常' }}</text>
				<button class="retry-btn" @click="loadArticleList">
					<text>🔄 重试</text>
				</button>
			</view>

			<!-- 文章列表（科技感卡片） -->
			<view class="article-card-list" v-else>
				<!-- 空列表状态（科技感版） -->
				<view v-if="articleList.length === 0" class="empty-container">
					<text class="empty-icon">📡</text>
					<text class="empty-title">暂无科普宠物知识</text>
					<text class="empty-desc">正在同步云端数据...</text>
					<view class="empty-decoration">⚡</view>
				</view>

				<!-- 文章卡片 -->
				<view class="article-card" v-for="(article, index) in articleList" :key="article.id" @click="goToDetail(article.id)" :style="{ animationDelay: index * 0.1 + 's' }">
					<!-- 卡片角标装饰 -->
					<view class="card-corner"></view>

					<!-- 封面图（科技感渐变） -->
					<view class="card-image-wrapper">
						<image :src="getImageUrl(article.coverImage)" class="card-image" mode="aspectFill" :draggable="false" />
						<view class="image-overlay"></view>
						<!-- 置顶标签（科技感） -->
						<view class="top-tag" v-if="article.isTop">
							<text>🔝 置顶</text>
						</view>
						<!-- 新文章标签 -->
						<view class="new-tag" v-if="isNewArticle(article.publishTime)">
							<text>🆕 新</text>
						</view>
					</view>

					<!-- 文章内容区 -->
					<view class="card-content">
						<text class="card-title">{{ article.title }}</text>

						<!-- 辅助信息（科技感排版） -->
						<view class="card-meta">
							<view class="meta-item">
								<text class="meta-icon">👁️</text>
								<text class="meta-text">{{ article.viewCount || 0 }} 浏览</text>
							</view>
							<view class="meta-item">
								<text class="meta-icon">📅</text>
								<text class="meta-text">{{ formatTime(article.publishTime) }}</text>
							</view>
						</view>

						<!-- 底部科技装饰线 -->
						<view class="card-divider">
							<text class="divider-icon">⚡</text>
						</view>
					</view>
				</view>
			</view>

			<!-- 底部科技装饰 -->
			<view class="page-footer">
				<text class="footer-text">🤖 科普与爱宠同行 🤖</text>
			</view>

			<!-- 自定义刷新头部 -->
			<view class="custom-refresher" v-if="showRefresher">
				<view class="refresher-content" :style="{ transform: `translateY(${refreshDistance}px)` }">
					<view class="refresher-icon" v-if="!isRefreshing">
						<text class="icon-text">↓</text>
						<text class="icon-text">↓</text>
						<text class="icon-text">↓</text>
					</view>
					<view class="refresher-icon" v-else>
						<view class="loading-circle-small"></view>
					</view>
					<text class="refresher-text">{{ refreshText }}</text>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
import { listArticle } from '@/api/pet/article';
import config from '@/config';

export default {
	data() {
		return {
			articleList: [],
			loading: true,
			error: false,
			errorMsg: '',
			isRefreshing: false,
			showRefresher: false,
			refreshDistance: 0,
			refreshText: '下拉刷新',
			startY: 0,
			currentY: 0,
			scrollHeight: '100vh',
			scrollTop: 0,
			page: 1,
			limit: 10
		};
	},
	onLoad() {
		this.loadArticleList();
	},
	onReady() {
		// 获取屏幕高度（替代已弃用的getSystemInfo）
		const windowInfo = uni.getWindowInfo();
		this.scrollHeight = windowInfo.windowHeight + 'px';
	},
	methods: {
		// 时间格式化（科技感显示）
		formatTime(time) {
			if (!time) return '未知时间';
			const date = new Date(time);
			const now = new Date();
			const diffDays = Math.floor((now - date) / (1000 * 60 * 60 * 24));
			if (diffDays === 0) return '今日';
			if (diffDays === 1) return '昨日';
			if (diffDays < 7) return `${diffDays}天前`;
			return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
		},

		// 判断是否为新文章（7天内）
		isNewArticle(publishTime) {
			if (!publishTime) return false;
			const date = new Date(publishTime);
			const now = new Date();
			const diffDays = Math.floor((now - date) / (1000 * 60 * 60 * 24));
			return diffDays <= 7;
		},

		// 加载文章列表
		async loadArticleList() {
			try {
				this.loading = true;
				this.error = false;
				this.errorMsg = '';

				const res = await listArticle({ page: this.page, limit: this.limit });

				if (res.code === 200 && res.rows) {
					if (this.page === 1) {
						this.articleList = res.rows.map((item) => ({
							id: item.articleId,
							title: item.title,
							coverImage: item.coverImage,
							viewCount: item.viewCount,
							publishTime: item.publishTime,
							isTop: item.isTop
						}));
					} else {
						this.articleList = [
							...this.articleList,
							...res.rows.map((item) => ({
								id: item.articleId,
								title: item.title,
								coverImage: item.coverImage,
								viewCount: item.viewCount,
								publishTime: item.publishTime,
								isTop: item.isTop
							}))
						];
					}
				} else {
					this.articleList = [];
				}
			} catch (err) {
				this.error = true;
				this.errorMsg = '网络连接异常，请重试';
			} finally {
				this.loading = false;
				this.isRefreshing = false;
			}
		},

		// 跳转详情页（科技感提示）
		goToDetail(articleId) {
			if (articleId) {
				uni.showToast({
					title: '正在加载科技知识🚀',
					icon: 'none',
					duration: 1000
				});
				setTimeout(() => {
					uni.navigateTo({
						url: `/pages/article/detail?id=${articleId}`
					});
				}, 500);
			} else {
				uni.showToast({
					title: '文章加载失败',
					icon: 'none',
					duration: 1500
				});
			}
		},

		// 获取图片URL
		getImageUrl(fileName) {
			return config.getDownloadUrl(fileName);
		},

		// 下拉刷新相关方法
		onRefresherRefresh() {
			this.isRefreshing = true;
			this.refreshText = '刷新中...';
			this.page = 1;
			this.loadArticleList();
		},

		// 滚动事件
		scroll(e) {
			this.scrollTop = e.detail.scrollTop;

			// 滚动到底部加载更多
			if (e.detail.scrollTop + e.detail.height >= e.detail.scrollHeight - 50) {
				if (!this.loading && !this.isRefreshing) {
					this.page++;
					this.loadArticleList();
				}
			}
		}
	}
};
</script>

<style lang="scss" scoped>
/* 全局样式（科技感基调） */
.article-list-page {
	background: #0a0e17; /* 深太空蓝背景 */
	min-height: 100vh;
	padding-bottom: 60rpx;
	background-image: radial-gradient(#1a2332 1px, transparent 1px), radial-gradient(#1a2332 1px, transparent 1px);
	background-size: 30rpx 30rpx;
	background-position: 0 0, 15rpx 15rpx;
}

/* 顶部科技感导航栏 */
.top-nav {
	background: linear-gradient(120deg, #1e3a8a 0%, #3b82f6 100%); /* 深蓝渐变 */
	padding: 30rpx 20rpx;
	text-align: center;
	border-radius: 0 0 30rpx 30rpx;
	box-shadow: 0 6rpx 20rpx rgba(59, 130, 246, 0.3);
	margin-bottom: 30rpx;
	position: relative;
	overflow: hidden;

	/* 导航栏科技光效 */
	&::after {
		content: '';
		position: absolute;
		top: 0;
		left: -100%;
		width: 50%;
		height: 100%;
		background: linear-gradient(90deg, transparent, rgba(59, 130, 246, 0.2), transparent);
		animation: tech-shine 3s infinite linear;
	}

	.nav-decoration {
		position: absolute;
		top: 50%;
		transform: translateY(-50%);
		font-size: 28rpx;

		&.left {
			left: 20rpx;
		}
		&.right {
			right: 20rpx;
		}
	}

	.nav-title {
		font-size: 38rpx;
		font-weight: 600;
		color: #ffffff;
		text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
		letter-spacing: 2rpx;
	}
}

/* 科技光效动画 */
@keyframes tech-shine {
	100% {
		left: 100%;
	}
}

/* 加载状态（科技感圆环动画） */
.loading-container {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 100rpx 0;

	.loading-circle {
		width: 80rpx;
		height: 80rpx;
		border: 8rpx solid #3b82f6;
		border-top: 8rpx solid #60a5fa;
		border-radius: 50%;
		animation: spin 1.5s linear infinite;
		margin-bottom: 25rpx;
		position: relative;

		&::after {
			content: '';
			position: absolute;
			top: 10rpx;
			right: 10rpx;
			width: 15rpx;
			height: 15rpx;
			background: #ffffff;
			border-radius: 50%;
		}
	}

	.loading-text {
		font-size: 28rpx;
		color: #60a5fa;
		letter-spacing: 1rpx;
		animation: pulse 2s infinite ease-in-out;
	}
}

/* 旋转动画（科技感） */
@keyframes spin {
	0% {
		transform: rotate(0deg);
	}
	100% {
		transform: rotate(360deg);
	}
}

/* 呼吸动画 */
@keyframes pulse {
	0%,
	100% {
		opacity: 1;
	}
	50% {
		opacity: 0.7;
	}
}

/* 错误状态（科技感版） */
.error-container {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 80rpx 20rpx;
	text-align: center;

	.error-icon {
		font-size: 100rpx;
		margin-bottom: 20rpx;
		color: #ef4444;
	}

	.error-title {
		font-size: 32rpx;
		color: #60a5fa;
		margin-bottom: 10rpx;
		letter-spacing: 1rpx;
	}

	.error-desc {
		font-size: 26rpx;
		color: #93c5fd;
		margin-bottom: 30rpx;
		line-height: 1.4;
	}

	.retry-btn {
		background: linear-gradient(120deg, #1e3a8a 0%, #3b82f6 100%);
		color: #ffffff;
		border: none;
		border-radius: 50rpx;
		padding: 18rpx 45rpx;
		font-size: 28rpx;
		box-shadow: 0 6rpx 15rpx rgba(59, 130, 246, 0.4);
		transition: all 0.3s ease;

		&:active {
			transform: scale(0.95);
			box-shadow: 0 3rpx 10rpx rgba(59, 130, 246, 0.3);
		}
	}
}

/* 空列表状态（科技感版） */
.empty-container {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 100rpx 20rpx;
	text-align: center;

	.empty-icon {
		font-size: 120rpx;
		color: #1e40af;
		margin-bottom: 20rpx;
		animation: float 3s infinite ease-in-out;
	}

	.empty-title {
		font-size: 32rpx;
		color: #60a5fa;
		margin-bottom: 10rpx;
		letter-spacing: 1rpx;
	}

	.empty-desc {
		font-size: 26rpx;
		color: #3b82f6;
		margin-bottom: 15rpx;
	}

	.empty-decoration {
		font-size: 24rpx;
		color: #1e3a8a;
	}
}

/* 漂浮动画 */
@keyframes float {
	0%,
	100% {
		transform: translateY(0);
	}
	50% {
		transform: translateY(-10rpx);
	}
}

/* 文章卡片列表 */
.article-card-list {
	display: flex;
	flex-direction: column;
	gap: 30rpx;
	padding: 0 20rpx;
}

/* 文章卡片核心样式（科技感） */
.article-card {
	background-color: #1a2332; /* 深蓝卡片背景 */
	border-radius: 25rpx;
	overflow: hidden;
	box-shadow: 0 8rpx 25rpx rgba(59, 130, 246, 0.15);
	transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
	animation: fadeInUp 0.6s ease forwards;
	opacity: 0;
	position: relative;

	/* 卡片科技纹理 */
	&::before {
		content: '';
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background-image: url("data:image/svg+xml,%3Csvg width='100' height='100' viewBox='0 0 100 100' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M11 18c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm48 25c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm-43-7c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm63 31c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM34 90c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm56-76c1.657 0 3-1.343 3-3s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zM12 86c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm28-65c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm23-11c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-6 60c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm29 22c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zM32 63c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm57-13c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-9-21c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM60 91c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM35 41c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM12 60c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2z' fill='%233b82f6' fill-opacity='0.05' fill-rule='evenodd'/%3E%3C/svg%3E");
		pointer-events: none;
	}

	&:hover {
		transform: translateY(-8rpx) rotate(0.5deg);
		box-shadow: 0 15rpx 30rpx rgba(59, 130, 246, 0.25);
	}

	&:active {
		transform: translateY(-3rpx) rotate(0.2deg);
		box-shadow: 0 10rpx 20rpx rgba(59, 130, 246, 0.2);
	}

	/* 卡片角落装饰 */
	.card-corner {
		position: absolute;
		top: 0;
		right: 0;
		width: 40rpx;
		height: 40rpx;
		background: linear-gradient(135deg, transparent 50%, #3b82f6 50%);
		border-radius: 0 25rpx 0 0;
		z-index: 1;

		&::after {
			content: '🔥';
			position: absolute;
			top: 5rpx;
			right: 5rpx;
			font-size: 18rpx;
		}
	}
}

/* 卡片入场动画（科技感） */
@keyframes fadeInUp {
	from {
		opacity: 0;
		transform: translateY(30rpx) scale(0.98);
	}
	to {
		opacity: 1;
		transform: translateY(0) scale(1);
	}
}

/* 封面图容器（科技感效果） */
.card-image-wrapper {
	position: relative;
	overflow: hidden;

	.card-image {
		width: 100%;
		height: 300rpx;
		object-fit: cover;
		transition: all 0.6s ease;
		filter: brightness(1.05) contrast(1.02);
	}

	&:hover .card-image {
		transform: scale(1.08) rotate(1deg);
		filter: brightness(1.1) contrast(1.05);
	}

	/* 科技渐变遮罩 */
	.image-overlay {
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: linear-gradient(180deg, rgba(59, 130, 246, 0.1) 0%, rgba(59, 130, 246, 0.2) 100%);
		pointer-events: none;
	}

	/* 置顶标签（科技感） */
	.top-tag {
		position: absolute;
		top: 20rpx;
		left: 20rpx;
		background: #3b82f6;
		color: #ffffff;
		font-size: 22rpx;
		padding: 8rpx 18rpx;
		border-radius: 50rpx;
		box-shadow: 0 4rpx 12rpx rgba(59, 130, 246, 0.3);
		animation: pulse 2s infinite ease-in-out;
	}

	/* 新文章标签 */
	.new-tag {
		position: absolute;
		top: 20rpx;
		right: 20rpx;
		background: #60a5fa;
		color: #ffffff;
		font-size: 22rpx;
		padding: 8rpx 18rpx;
		border-radius: 50rpx;
		box-shadow: 0 4rpx 12rpx rgba(96, 165, 250, 0.3);
	}
}

/* 卡片内容区（科技感排版） */
.card-content {
	padding: 30rpx 25rpx;

	.card-title {
		display: block;
		font-size: 34rpx;
		font-weight: 500;
		color: #e2e8f0;
		line-height: 1.6;
		margin-bottom: 25rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
		letter-spacing: 0.5rpx;
	}

	/* 元信息（科技感排版） */
	.card-meta {
		display: flex;
		justify-content: space-between;
		align-items: center;

		.meta-item {
			display: flex;
			align-items: center;
			gap: 10rpx;

			.meta-icon {
				font-size: 26rpx;
				color: #60a5fa;
			}

			.meta-text {
				font-size: 26rpx;
				color: #93c5fd;
				letter-spacing: 0.5rpx;
			}
		}
	}

	/* 底部科技装饰线 */
	.card-divider {
		margin-top: 25rpx;
		display: flex;
		align-items: center;

		&::before,
		&::after {
			content: '';
			flex: 1;
			height: 1rpx;
			background: linear-gradient(90deg, transparent, #60a5fa, transparent);
		}

		.divider-icon {
			padding: 0 15rpx;
			font-size: 20rpx;
			color: #60a5fa;
		}
	}
}

/* 底部科技装饰 */
.page-footer {
	margin-top: 40rpx;
	text-align: center;
	padding: 0 20rpx;

	.footer-text {
		font-size: 24rpx;
		color: #60a5fa;
		letter-spacing: 1rpx;
		background: rgba(30, 58, 138, 0.8);
		padding: 10rpx 20rpx;
		border-radius: 50rpx;
		box-shadow: 0 2rpx 10rpx rgba(96, 165, 250, 0.2);
	}
}

/* 自定义刷新头部 */
.custom-refresher {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	height: 80rpx;
	display: flex;
	justify-content: center;
	align-items: center;
	z-index: 10;
}

.refresher-content {
	display: flex;
	flex-direction: column;
	align-items: center;
	transition: transform 0.3s ease;
}

.refresher-icon {
	display: flex;
	flex-direction: column;
	gap: 8rpx;
	margin-bottom: 10rpx;
}

.icon-text {
	font-size: 24rpx;
	color: #60a5fa;
	transition: transform 0.3s ease;
}

.loading-circle-small {
	width: 24rpx;
	height: 24rpx;
	border: 3rpx solid #3b82f6;
	border-top: 3rpx solid #60a5fa;
	border-radius: 50%;
	animation: spin 1s linear infinite;
}

.refresher-text {
	font-size: 24rpx;
	color: #93c5fd;
}
</style>
