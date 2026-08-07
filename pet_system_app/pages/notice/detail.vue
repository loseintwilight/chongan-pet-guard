<template>
	<view class="notification-detail-page">
		<!-- 通知内容 -->
		<view class="notification-content">
			<view class="notification-meta">
				<text class="publisher">发布者: {{ notification.publishName }}</text>
				<text class="publish-time">发布时间: {{ notification.publishTime }}</text>
			</view>
			<view class="notification-title">
				{{ notification.title }}
			</view>
			<mp-html :content="notification.noticeMain" />
		</view>
	</view>
</template>

<script>
// 引入 marked 库
import { marked } from 'marked';
import { getNoticeDetail } from '@/api/pet/notice.js';

export default {
	data() {
		return {
			notification: {
				title: '',
				noticeMain: '',
				publishName: '',
				publishTime: ''
			},
			noticeId: null
		};
	},
	onLoad(options) {
		this.noticeId = options.noticeId;
		this.getNotice();
	},
	methods: {
		navigateBack() {
			uni.navigateBack({
				success: () => {
					// 返回成功后，尝试刷新上一个页面的数据
					const pages = getCurrentPages();
					const prevPage = pages[pages.length - 2];
					if (prevPage) {
						console.log('返回列表页，尝试刷新数据');
						// 重置分页参数并刷新数据
						prevPage.queryParams.pageNum = 1;
						prevPage.finished = false;
						prevPage.notificationList = [];
						prevPage.getNoticeList();
					}
				}
			});
		},
		getNotice() {
			console.log('获取通知详情，noticeId:', this.noticeId);
			// 假设 getNoticeDetail 是一个异步方法，返回 Promise
			getNoticeDetail(this.noticeId)
				.then((response) => {
					console.log('获取到的通知详情:', response.data);
					// 将 Markdown 内容转换为 HTML
					this.notification = {
						...response.data,
						noticeMain: marked(response.data.noticeMain)
					};
					// 手动标记当前通知为已读
					const pages = getCurrentPages();
					const prevPage = pages[pages.length - 2];
					if (prevPage) {
						console.log('手动标记通知为已读，noticeId:', this.noticeId);
						// 在列表页中查找并更新当前通知的readStatus
						prevPage.notificationList.forEach(notice => {
							if (notice.noticeId === this.noticeId) {
								notice.readStatus = true;
							}
						});
					}
				})
				.catch((error) => {
					console.error('获取通知详情失败:', error);
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					});
				});
		}
	}
};
</script>

<style lang="scss" scoped>
.notification-detail-page {
	min-height: 100vh;
	background-color: #f8f9fa;
	padding: 30rpx;
	box-sizing: border-box;
}

/* 顶部标题栏 */
.page-header {
	display: flex;
	align-items: center;
	padding: 20rpx 0;
	background-color: #ffffff;
	box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
	position: sticky;
	top: 0;
	z-index: 100;
}

.page-header .uni-icons {
	margin-right: 20rpx;
}

.header-title {
	font-size: 34rpx;
	font-weight: bold;
	color: #2c3e50;
}

/* 通知内容 */
.notification-content {
	background-color: #ffffff;
	border-radius: 20rpx;
	box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.06);
	padding: 30rpx;
	margin-top: 20rpx;
}

/* 通知元信息 */
.notification-meta {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
	font-size: 24rpx;
	color: #7f8c8d;
}

.publisher,
.publish-time {
	white-space: nowrap;
}

/* 通知标题 */
.notification-title {
	font-size: 36rpx;
	font-weight: bold;
	color: #2c3e50;
	margin-bottom: 20rpx;
}

/* mp-html 内部元素样式美化 */
.mp-html {
	/* 全局容器：科技感背景 */
	padding: 30rpx;
	line-height: 1.8;
	/* 渐变背景，营造科技感 */
	background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
	min-height: 100vh; /* 确保背景铺满全屏 */

	/* 标题样式：温暖且突出 */
	h1 {
		font-size: 52rpx;
		color: #2c3e50; /* 深灰色，沉稳专业 */
		text-align: center;
		margin: 20rpx 0 40rpx;
		font-weight: bold;
		text-shadow: 2rpx 2rpx 4rpx rgba(0, 0, 0, 0.1);
	}

	/* 二级标题：作为区块标题 */
	h2 {
		font-size: 38rpx;
		color: #3498db; /* 科技蓝 */
		border-left: 8rpx solid #3498db;
		padding-left: 20rpx;
		margin-top: 60rpx;
		margin-bottom: 30rpx;
	}

	/* 三级标题：流程步骤 */
	h3 {
		font-size: 32rpx;
		color: #e67e22; /* 温暖橙色 */
		margin-top: 40rpx;
		margin-bottom: 20rpx;
	}

	/* 引用块：突出核心承诺 */
	blockquote {
		border-left: 5rpx solid #2ecc71; /* 活力绿 */
		padding: 20rpx 30rpx;
		margin: 30rpx 0;
		background-color: rgba(255, 255, 255, 0.8);
		color: #555;
		font-style: italic;
		border-radius: 0 16rpx 16rpx 0;
		box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.05);
	}

	/* 分割线 */
	hr {
		border: none;
		border-top: 2rpx dashed #bdc3c7;
		margin: 50rpx 0;
	}

	/* 列表样式：增加亲和力 */
	ul {
		list-style-type: none; /* 去掉默认圆点 */
		padding-left: 10rpx;
	}

	li {
		background-color: rgba(255, 255, 255, 0.7);
		margin-bottom: 20rpx;
		padding: 20rpx 25rpx;
		border-radius: 12rpx;
		box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
		transition: transform 0.2s ease-in-out;
	}

	/* 图片样式：卡片式设计，增强视觉冲击力 */
	img {
		width: 100%;
		height: auto;
		border-radius: 20rpx; /* 更圆润的边角 */
		margin: 30rpx 0;
		box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.15); /* 更强的阴影，更有层次 */
		/* 增加一个淡入的动画效果 */
		opacity: 0;
		animation: fadeIn 1s ease-in-out forwards;
	}

	/* 图片下方的说明文字 */
	p:last-child {
		font-size: 26rpx;
		color: #7f8c8d;
		text-align: center;
		margin-top: -20rpx;
		margin-bottom: 40rpx;
	}

	/* 表格样式：清晰、现代 */
	table {
		width: 100%;
		border-collapse: collapse;
		margin: 40rpx 0;
		font-size: 28rpx;
		border-radius: 16rpx;
		overflow: hidden; /* 让圆角生效 */
		box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.08);
	}

	th,
	td {
		padding: 25rpx 20rpx;
		text-align: center;
		border: none; /* 去掉默认边框 */
	}

	th {
		background-color: #3498db; /* 表头使用科技蓝 */
		color: #ffffff;
		font-weight: bold;
	}

	tr:nth-child(even) {
		background-color: rgba(236, 240, 241, 0.5); /* 斑马纹，提升可读性 */
	}

	tr:nth-child(odd) {
		background-color: #ffffff;
	}

	/* 段落样式 */
	p {
		margin-bottom: 25rpx;
		color: #34495e;
	}

	/* 定义淡入动画 */
	@keyframes fadeIn {
		from {
			opacity: 0;
			transform: translateY(20rpx);
		}
		to {
			opacity: 1;
			transform: translateY(0);
		}
	}
}
</style>
