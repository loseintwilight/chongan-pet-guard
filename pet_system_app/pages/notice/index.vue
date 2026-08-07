<template>
	<view class="notification-page">
		<!-- 通知列表 -->
		<view class="notification-list">
			<view class="notification-item" v-for="(item, index) in notificationList" :key="index" @click="navigateToDetail(item)">
				<view class="item-content">
					<text class="item-title">{{ item.title }}</text>
				</view>
				<view class="item-meta">
					<text class="item-publisher">发布者: {{ item.publishName }}</text>
					<text class="item-time">发布时间: {{ item.publishTime }}</text>
				</view>
				<!-- 未读标记 -->
				<view class="unread-badge" v-if="!item.readStatus"></view>
			</view>
		</view>

		<!-- 加载状态提示 -->
		<view class="loading" v-if="loading">
			<uni-icons type="spinner-cycle" size="24" color="#999"></uni-icons>
			<text>加载中...</text>
		</view>

		<!-- 没有更多数据提示 -->
		<view class="no-more" v-if="finished && !loading">
			<text>没有更多数据了</text>
		</view>
	</view>
</template>

<script>
import { getPersonNoticeMsg, getUnReadNoticeNumber } from '@/api/pet/notice.js';

export default {
	data() {
		return {
			notificationList: [],
			queryParams: {
				pageNum: 1,
				pageSize: 10
			},
			loading: false,
			finished: false
		};
	},
	onLoad() {
		this.getNoticeList();
	},
	onShow() {
		console.log('onShow被调用，准备刷新通知列表');
		// 重置分页参数
		this.queryParams.pageNum = 1;
		this.finished = false;
		this.notificationList = [];

		// 重新加载数据
		this.getNoticeList();
	},
	onPullDownRefresh() {
		// 重置分页参数
		this.queryParams.pageNum = 1;
		this.finished = false;
		this.notificationList = [];

		// 重新加载数据
		this.getNoticeList();
	},
	onReachBottom() {
		// 如果正在加载或已加载完所有数据，则不执行
		if (this.loading || this.finished) return;

		this.queryParams.pageNum++;
		this.getNoticeList();
	},
	methods: {
		navigateBack() {
			uni.navigateBack();
		},
		navigateToDetail(item) {
			uni.navigateTo({
				url: '/pages/notice/detail?noticeId=' + item.noticeId
			});
		},
		async getNoticeList() {
		this.loading = true;

		try {
			const res = await getPersonNoticeMsg(this.queryParams);
			const newData = res.rows; // 假设返回的数据格式为 { data: [...] }
			console.log('获取到的通知列表数据:', newData);
			console.log('每个通知的readStatus:', newData.map(item => ({ noticeId: item.noticeId, readStatus: item.readStatus })));
			if (newData.length < this.queryParams.pageSize) {
				this.finished = true; // 数据不足一页，标记为已加载完
			}

			this.notificationList = [...this.notificationList, ...newData]; // 合并新数据
		} catch (error) {
			console.error('加载通知列表失败:', error);
			uni.showToast({
				title: '加载失败',
				icon: 'none'
			});
		} finally {
			this.loading = false;
			uni.stopPullDownRefresh(); // 停止下拉刷新动画
		}
	}
	}
};
</script>

<style lang="scss" scoped>
.notification-page {
	min-height: 100vh;
	background-color: #f8f9fa;
	padding: 30rpx;
	box-sizing: border-box;
}

/* 通知列表 */
.notification-list {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

/* 通知项 */
.notification-item {
	background-color: #ffffff;
	border-radius: 16rpx;
	box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.05);
	padding: 25rpx 30rpx;
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
	cursor: pointer;
	position: relative; /* 为未读标记提供定位上下文 */

	&:hover {
		transform: translateY(-3rpx);
		box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.1);
	}

	&:active {
		transform: scale(0.98);
	}
}

.item-content {
	margin-bottom: 12rpx;
}

.item-title {
	font-size: 32rpx;
	font-weight: 500;
	color: #2c3e50;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.item-meta {
	display: flex;
	justify-content: space-between;
	font-size: 24rpx;
	color: #7f8c8d;
}

.item-publisher,
.item-time {
	white-space: nowrap;
}

/* 未读标记 - 优化后 */
.unread-badge {
	position: absolute;
	top: 25rpx;
	right: 25rpx;
	width: 12rpx;
	height: 12rpx;
	background-color: #ff6b6b; /* 使用更鲜艳的红色，更加醒目 */
	border-radius: 50%;
	box-shadow: 0 2rpx 6rpx rgba(255, 107, 107, 0.4); /* 添加轻微的内阴影，增加立体感 */
	animation: pulse 2s infinite; /* 添加脉冲动画，吸引注意力 */
}

/* 脉冲动画 */
@keyframes pulse {
	0% {
		transform: scale(1);
		opacity: 1;
	}
	50% {
		transform: scale(1.1);
		opacity: 0.8;
	}
	100% {
		transform: scale(1);
		opacity: 1;
	}
}

/* 加载状态提示 */
.loading {
	display: flex;
	justify-content: center;
	align-items: center;
	padding: 20rpx;
	color: #999;
}

.loading .uni-icons {
	margin-right: 10rpx;
}

/* 没有更多数据提示 */
.no-more {
	text-align: center;
	padding: 20rpx;
	color: #999;
}
</style>
