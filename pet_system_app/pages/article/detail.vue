<template>
	<view class="article-detail-page">
		<!-- 加载中（科技感动效） -->
		<view v-if="loading" class="loading-wrap">
			<view class="loading-circle"></view>
			<text class="loading-text">正在加载科普宠物知识🚀</text>
		</view>

		<!-- 错误提示（科技感版） -->
		<view v-if="error" class="error-wrap">
			<text class="error-icon">🤖</text>
			<text class="error-text">{{ errorMsg || '加载失败' }}</text>
			<button class="retry-btn" @click="loadArticle">🔄 重试</button>
		</view>

		<!-- 文章内容 -->
		<scroll-view scroll-y class="article-content" v-else>
			<!-- 顶部导航栏（科技感渐变） -->
			<view class="page-header">
				<text class="header-title">🤖 宠物科普中心</text>
				<view class="header-decoration">⚡</view>
			</view>

			<!-- 文章标题（科技感排版） -->
			<view class="article-title">
				<view class="title-decoration">
					<text>🔥</text>
				</view>
				<text class="title-text">{{ articleData.title }}</text>
				<view class="title-divider"></view>
			</view>

			<!-- 文章内容（Markdown 直接使用） -->
			<view class="content-wrap" v-if="articleData.content">
				<view class="markdown-content" v-html="articleData.content"></view>
			</view>
			<view class="empty-content" v-else>
				<text class="empty-icon">📡</text>
				<text class="empty-text">暂无科普宠物知识</text>
			</view>

			<!-- 文章信息（科技感排版） -->
			<view class="article-meta">
				<view class="meta-card">
					<view class="meta-item">
						<text class="meta-icon">📅</text>
						<text class="meta-label">发布时间</text>
						<text class="meta-value">{{ formatPublishTime }}</text>
					</view>
					<view class="meta-item">
						<text class="meta-icon">👁️</text>
						<text class="meta-label">浏览量</text>
						<text class="meta-value">{{ articleData.viewCount || 0 }}</text>
					</view>
					<view class="meta-item">
						<text class="meta-icon">👨‍💻</text>
						<text class="meta-label">创作者</text>
						<text class="meta-value">{{ articleData.author || '宠物科普专家' }}</text>
					</view>
				</view>

				<!-- 底部科技装饰 -->
				<view class="meta-footer">
					<text>🤖 科普与爱宠同行 🤖</text>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
import { getArticle } from '@/api/pet/article';

export default {
	data() {
		return {
			loading: true,
			error: false,
			errorMsg: '',
			articleId: '',
			articleData: {
				title: '',
				content: '',
				author: '',
				viewCount: 0,
				publishTime: ''
			}
		};
	},
	computed: {
		formatPublishTime() {
			if (!this.articleData.publishTime) return '未知时间';
			return this.parseTime(this.articleData.publishTime, '{y}-{m}-{d}');
		}
	},
	onLoad(options) {
		if (!options?.id) {
			this.loading = false;
			this.error = true;
			this.errorMsg = '文章ID不存在';
			return;
		}
		if (/^\d+$/.test(options.id)) {
			this.articleId = options.id;
			this.loadArticle();
		} else {
			this.loading = false;
			this.error = true;
			this.errorMsg = '文章ID格式错误';
		}
	},
	methods: {
		navigateBack() {
			uni.navigateBack();
		},

		async loadArticle() {
			try {
				this.loading = true;
				this.error = false;
				this.errorMsg = '';

				console.log('请求文章ID:', this.articleId);
				const res = await getArticle(this.articleId);

				if (!res || res.code !== 200) {
					throw new Error(res?.msg || '获取文章失败');
				}
				if (!res.data || typeof res.data !== 'object') {
					throw new Error('文章数据格式错误');
				}

				this.articleData = {
					title: res.data.title || '未知标题',
					content: res.data.content || '',
					author: res.data.author || '科技专家',
					viewCount: Number(res.data.viewCount) || 0,
					publishTime: res.data.publishTime || ''
				};
			} catch (err) {
				console.error('加载文章失败详情:', {
					articleId: this.articleId,
					errorMsg: err.message,
					stack: err.stack
				});
				this.error = true;
				if (err?.message?.includes('Network Error')) {
					this.errorMsg = '网络连接异常';
				} else {
					this.errorMsg = err?.message || '加载失败';
				}
			} finally {
				this.loading = false;
			}
		},

		parseTime(time, format = '{y}-{m}-{d}') {
			if (!time) return '未知时间';
			let date;
			try {
				if (typeof time === 'string') {
					time = time.replace(/-/g, '/').replace('T', ' ').replace('Z', '');
				}
				date = new Date(time);
				if (isNaN(date.getTime())) return '未知时间';
			} catch (e) {
				return '未知时间';
			}

			const formatObj = {
				y: date.getFullYear(),
				m: date.getMonth() + 1,
				d: date.getDate()
			};

			return format.replace(/{([ymd])+}/g, (match, key) => {
				const value = formatObj[key] || 0;
				return value.toString().padStart(2, '0');
			});
		}
	}
};
</script>

<style lang="scss" scoped>
/* 全局基调（科技感深太空蓝） */
.article-detail-page {
	min-height: 100vh;
	background: #0a0e17;
	background-image: radial-gradient(#1a2332 1px, transparent 1px), radial-gradient(#1a2332 1px, transparent 1px);
	background-size: 30rpx 30rpx;
	background-position: 0 0, 15rpx 15rpx;
}

/* 加载状态（科技感圆环） */
.loading-wrap {
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	height: 80vh;
	gap: 25rpx;
}

.loading-circle {
	width: 70rpx;
	height: 70rpx;
	border: 6rpx solid #3b82f6;
	border-top: 6rpx solid #60a5fa;
	border-radius: 50%;
	animation: spin 1.5s linear infinite;
	position: relative;

	&::after {
		content: '';
		position: absolute;
		top: 8rpx;
		right: 8rpx;
		width: 12rpx;
		height: 12rpx;
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

@keyframes spin {
	0% {
		transform: rotate(0deg);
	}
	100% {
		transform: rotate(360deg);
	}
}

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
.error-wrap {
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	height: 80vh;
	gap: 20rpx;
	text-align: center;
	padding: 0 30rpx;
}

.error-icon {
	font-size: 80rpx;
	margin-bottom: 10rpx;
	color: #ef4444;
}

.error-text {
	font-size: 28rpx;
	color: #60a5fa;
	letter-spacing: 0.5rpx;
	line-height: 1.4;
}

.retry-btn {
	padding: 15rpx 40rpx;
	font-size: 28rpx;
	background: linear-gradient(120deg, #1e3a8a 0%, #3b82f6 100%);
	color: #ffffff;
	border: none;
	border-radius: 50rpx;
	box-shadow: 0 4rpx 12rpx rgba(59, 130, 246, 0.3);
	transition: all 0.3s ease;
	margin-top: 10rpx;

	&:active {
		transform: scale(0.95);
		box-shadow: 0 2rpx 8rpx rgba(59, 130, 246, 0.2);
	}
}

/* 滚动容器 */
.article-content {
	height: 100vh;
}

/* 顶部导航栏（科技感渐变） */
.page-header {
	display: flex;
	align-items: center;
	padding: 25rpx 30rpx;
	background: linear-gradient(120deg, #1e3a8a 0%, #3b82f6 100%);
	border-radius: 0 0 20rpx 20rpx;
	box-shadow: 0 4rpx 15rpx rgba(59, 130, 246, 0.2);
	position: sticky;
	top: 0;
	z-index: 10;

	/* 科技光效 */
	&::after {
		content: '';
		position: absolute;
		top: 0;
		left: -100%;
		width: 50%;
		height: 100%;
		background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
		animation: tech-shine 3s infinite linear;
	}

	.back-btn {
		width: 50rpx;
		height: 50rpx;
		border-radius: 50%;
		background: rgba(255, 255, 255, 0.2);
		display: flex;
		justify-content: center;
		align-items: center;
		transition: all 0.3s ease;

		&:active {
			background: rgba(255, 255, 255, 0.3);
			transform: scale(0.9);
		}
	}

	.header-title {
		font-size: 32rpx;
		font-weight: 600;
		color: #ffffff;
		flex: 1;
		text-align: center;
		letter-spacing: 1rpx;
		text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
	}

	.header-decoration {
		font-size: 24rpx;
	}
}

@keyframes tech-shine {
	100% {
		left: 100%;
	}
}

/* 文章标题（科技感排版） */
.article-title {
	padding: 40rpx 30rpx 20rpx;
	position: relative;

	.title-decoration {
		position: absolute;
		top: 30rpx;
		left: 30rpx;
		font-size: 24rpx;
	}

	.title-text {
		font-size: 36rpx;
		font-weight: 600;
		color: #e2e8f0;
		line-height: 1.6;
		display: block;
		letter-spacing: 1rpx;
		padding-left: 40rpx;
		margin-bottom: 20rpx;
	}

	.title-divider {
		height: 2rpx;
		width: 100rpx;
		background: linear-gradient(90deg, #60a5fa, transparent);
		border-radius: 1rpx;
		margin-left: 40rpx;
	}
}

/* 文章内容（科技感排版） */
.content-wrap {
	padding: 10rpx 30rpx 30rpx;
}

.markdown-content {
	font-size: 28rpx;
	color: #e2e8f0;
	line-height: 2.2;
	letter-spacing: 0.5rpx;
}

/* 空内容提示（科技感） */
.empty-content {
	padding: 80rpx 30rpx;
	text-align: center;
	display: flex;
	flex-direction: column;
	align-items: center;
	gap: 15rpx;

	.empty-icon {
		font-size: 80rpx;
		color: #1e3a8a;
		animation: float 3s infinite ease-in-out;
	}

	.empty-text {
		font-size: 28rpx;
		color: #60a5fa;
		letter-spacing: 0.5rpx;
	}
}

.empty-content-inner {
	text-align: center;
	color: #60a5fa;
	font-size: 28rpx;
	padding: 80rpx 0;
}

@keyframes float {
	0%,
	100% {
		transform: translateY(0);
	}
	50% {
		transform: translateY(-10rpx);
	}
}

/* 文章信息（科技感卡片） */
.article-meta {
	padding: 0 30rpx 60rpx;
}

.meta-card {
	background: #1a2332;
	border-radius: 20rpx;
	padding: 30rpx;
	box-shadow: 0 6rpx 20rpx rgba(59, 130, 246, 0.1);
	margin-bottom: 20rpx;

	/* 科技纹理 */
	&::before {
		content: '';
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background-image: url("data:image/svg+xml,%3Csvg width='100' height='100' viewBox='0 0 100 100' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M11 18c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm48 25c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm-43-7c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm63 31c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM34 90c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm56-76c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM12 86c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm28-65c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm23-11c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-6 60c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm29 22c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zM32 63c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm57-13c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-9-21c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM60 91c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM35 41c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM12 60c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2z' fill='%233b82f6' fill-opacity='0.05' fill-rule='evenodd'/%3E%3C/svg%3E");
		pointer-events: none;
		border-radius: 20rpx;
	}

	.meta-item {
		display: flex;
		align-items: center;
		gap: 15rpx;
		margin-bottom: 20rpx;

		&:last-child {
			margin-bottom: 0;
		}

		.meta-icon {
			font-size: 28rpx;
			color: #60a5fa;
			width: 40rpx;
			text-align: center;
		}

		.meta-label {
			font-size: 26rpx;
			color: #93c5fd;
			width: 120rpx;
		}

		.meta-value {
			font-size: 26rpx;
			color: #e2e8f0;
			letter-spacing: 0.5rpx;
			flex: 1;
		}
	}
}

.meta-footer {
	text-align: center;
	font-size: 24rpx;
	color: #60a5fa;
	letter-spacing: 1rpx;
	background: rgba(30, 58, 138, 0.8);
	padding: 10rpx 20rpx;
	border-radius: 50rpx;
	box-shadow: 0 2rpx 10rpx rgba(96, 165, 250, 0.2);
}
</style>
