<template>
	<view class="activity-detail-container">
		<!-- 活动图片 -->
		<view class="activity-image-container">
			<image :src="getImageUrl(activity.images)" class="activity-image" mode="aspectFill" />
			<view class="activity-status" v-if="activity.status === 0">
				<text class="status-icon">⏰</text>
				<text class="status-text">即将开始</text>
			</view>
			<view class="activity-status" v-else-if="activity.status === 1">
				<text class="status-icon">🔥</text>
				<text class="status-text">热门进行</text>
			</view>
			<view class="activity-status" v-else-if="activity.status === 2">
				<text class="status-icon">📋</text>
				<text class="status-text">精彩回顾</text>
			</view>
		</view>

		<!-- 活动基本信息 -->
		<view class="activity-info">
			<view class="activity-title">{{ activity.title }}</view>
			<view class="activity-category">
				<text class="category-icon">{{ getTypeIcon(activity.activityCategory) }}</text>
				<text class="category-text">{{ getActivityTypeText(activity.activityCategory) }}</text>
			</view>
			<view class="activity-meta">
				<view class="meta-item">
					<text class="meta-icon">📅</text>
					<text class="meta-text">{{ formatDate(activity.startTime) }} - {{ formatDate(activity.endTime) }}</text>
				</view>
				<view class="meta-item">
					<text class="meta-icon">📍</text>
					<text class="meta-text">{{ activity.address }}</text>
				</view>
				<view class="meta-item">
					<text class="meta-icon">👥</text>
					<text class="meta-text">{{ activity.count }}人参与</text>
				</view>
				<view class="meta-item">
					<text class="meta-icon">🎯</text>
					<text class="meta-text">限额{{ activity.personCount }}人</text>
				</view>
			</view>
		</view>

		<!-- 活动描述 -->
		<view class="activity-description">
			<view class="section-title">活动介绍</view>
			<view class="description-content">{{ activity.content }}</view>
		</view>

		<!-- 主办方信息 -->
		<view class="organizer-info">
			<view class="section-title">主办方信息</view>
			<view class="organizer-content">
				<text class="organizer-name">{{ activity.createOrganize }}</text>
				<text class="organizer-time">创建时间: {{ formatDate(activity.createTime) }}</text>
			</view>
		</view>

		<!-- 报名按钮 -->
		<view class="registration-section">
			<view class="registration-info">
				<view class="info-item">
					<text class="info-label">报名状态</text>
					<text class="info-value" :class="{ 'status-ongoing': activity.status === 1, 'status-ended': activity.status === 2 }">
						{{ getStatusText(activity.status) }}
					</text>
				</view>
				<view class="info-item">
					<text class="info-label">剩余名额</text>
					<text class="info-value">{{ activity.personCount - activity.count }}/{{ activity.personCount }}人</text>
				</view>
			</view>
			<!-- <view class="registration-btn" @click="handleCancelRegistration">
				<text class="btn-text">取消报名</text>
			</view> -->
		</view>

		<!-- 底部提示 -->
		<view class="bottom-tip" v-if="activity.status === 2">
			<text class="tip-text">📋 此活动已结束，感谢您的关注！</text>
		</view>
	</view>
</template>

<script>
import { getActivity } from '@/api/pet/activity';
import { getDicts } from '@/api/system/dict/data';
import config from '@/config';

export default {
	data() {
		return {
			activity: {}, // 活动详情
			activityTypeDict: {}, // 活动类型字典
			isUserSignedUp: true
		};
	},
	onLoad(options) {
		// 使用 onLoad(options) 获取参数
		this.activityId = options.id;
		this.loadDictData();
		this.loadActivityDetail();
	},
	methods: {
		/** 加载字典数据 */
		async loadDictData() {
			try {
				// 获取活动类型字典
				const typeRes = await getDicts('pet_activity_category');
				this.activityTypeDict = {};
				typeRes.data.forEach((item) => {
					this.activityTypeDict[item.dictValue] = {
						label: item.dictLabel,
						icon: this.getTypeIcon(item.dictValue)
					};
				});
				console.log('活动类型字典:', this.activityTypeDict);
			} catch (error) {
				console.error('加载活动类型字典失败:', error);
				uni.showToast({
					title: '加载字典数据失败',
					icon: 'none'
				});
			}
		},

		/** 加载活动详情 */
		async loadActivityDetail() {
			try {
				// 使用 this.activityId 获取活动详情
				if (!this.activityId) {
					uni.showToast({
						title: '活动ID不存在',
						icon: 'none'
					});
					return;
				}

				const response = await getActivity(this.activityId);
				if (response.code === 200) {
					const data = response.data;
					this.activity = data;
					// 【关键】从后端返回的数据中获取用户报名状态
					// 如果后端接口不返回这个字段，您需要额外调用一个查询接口
					this.isUserSignedUp = data.isSignedUp || false; // 假设字段名为 isSignedUp
					console.log('活动详情:', this.activity);
					console.log('用户报名状态:', this.isUserSignedUp);
				} else {
					uni.showToast({
						title: '加载活动详情失败: ' + (response.msg || '未知错误'),
						icon: 'none'
					});
				}
			} catch (error) {
				uni.showToast({
					title: '加载活动详情失败: ' + (error.message || '网络错误'),
					icon: 'none'
				});
				console.error('加载活动详情失败:', error);
			}
		},

		/** 获取类型图标 */
		getTypeIcon(type) {
			const icons = {
				0: '🐾',
				1: '🎉',
				2: '🎓',
				3: '👑'
			};
			return icons[type] || '🐾';
		},

		/** 获取活动类型文本 */
		getActivityTypeText(type) {
			return this.activityTypeDict[type]?.label || '其他活动';
		},

		/** 格式化日期 */
		formatDate(dateString) {
			if (!dateString) return '时间待定';
			const date = new Date(dateString);
			const month = date.getMonth() + 1;
			const day = date.getDate();
			const hours = date.getHours();
			const minutes = date.getMinutes().toString().padStart(2, '0');
			return `${month}月${day}日 ${hours}:${minutes}`;
		},

		/** 获取状态文本 */
		getStatusText(status) {
			switch (status) {
				case 0:
					return '报名中';
				case 1:
					return '进行中';
				case 2:
					return '已结束';
				default:
					return '未知状态';
			}
		},

		/** 处理取消报名 */
		handleCancelRegistration() {
			// 弹出确认框
			uni.showModal({
				title: '确认取消报名',
				content: '确定要取消参加这个活动吗？',
				success: (res) => {
					if (res.confirm) {
						this.submitCancelRegistration();
					}
				}
			});
		},

		/** 提交取消报名 */
		async submitCancelRegistration() {
			try {
				uni.showLoading({ title: '取消报名中...', mask: true });
				// 调用取消报名接口
				// 注意：这里需要替换为实际的取消报名接口
				// 假设接口名为 cancelSignUp
				// const response = await cancelSignUp(this.activityId);

				// 模拟接口请求成功
				const response = { code: 200 };

				if (response.code === 200) {
					// 取消报名成功
					uni.hideLoading();
					uni.showToast({ title: '取消报名成功！', icon: 'success' });

					// 返回上一页
					uni.navigateBack();
				} else {
					uni.hideLoading();
					uni.showToast({ title: response.msg || '取消报名失败', icon: 'none' });
				}
			} catch (error) {
				uni.hideLoading();
				uni.showToast({ title: '网络错误，取消报名失败', icon: 'none' });
				console.error('取消报名失败:', error);
			}
		},

		/** 返回上一页 */
		navigateBack() {
			uni.navigateBack();
		},

		/** 获取图片URL */
		getImageUrl(fileName) {
			if (!fileName) return '/static/images/activity-placeholder.png';
			return config.getDownloadUrl(fileName);
		}
	}
};
</script>

<style lang="scss" scoped>
/* 样式保持不变 */
.activity-detail-container {
	min-height: 100vh;
	background: white;
	padding: 20rpx;
	box-sizing: border-box;
	position: relative;
}

/* 顶部导航栏 */
.header {
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20px);
	border-radius: 24rpx;
	padding: 20rpx;
	display: flex;
	justify-content: space-between;
	align-items: center;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.15);
	border: 1rpx solid rgba(255, 255, 255, 0.2);
	position: relative;
	z-index: 10;
}

.header-left {
	display: flex;
	align-items: center;
	gap: 15rpx;
}

.back-icon {
	font-size: 32rpx;
	color: #667eea;
	font-weight: bold;
}

.title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.header-right {
	display: flex;
	align-items: center;
	gap: 15rpx;
}

.share-icon {
	font-size: 28rpx;
	color: #667eea;
	font-weight: bold;
}

/* 活动图片 */
.activity-image-container {
	position: relative;
	height: 400rpx;
	overflow: hidden;
	border-radius: 24rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.2);
}

.activity-image {
	width: 100%;
	height: 100%;
	object-fit: cover;
}

.activity-status {
	position: absolute;
	top: 20rpx;
	right: 20rpx;
	padding: 10rpx 16rpx;
	border-radius: 20rpx;
	display: flex;
	align-items: center;
	gap: 8rpx;
	backdrop-filter: blur(10px);
	box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.2);
}

.status-icon {
	font-size: 20rpx;
}

.status-text {
	font-size: 22rpx;
	font-weight: bold;
	color: white;
}

.status-upcoming {
	background: linear-gradient(135deg, #2196f3, #64b5f6);
}

.status-ongoing {
	background: linear-gradient(135deg, #ff6b6b, #ff8e53);
}

.status-ended {
	background: linear-gradient(135deg, #9e9e9e, #bdbdbd);
}

/* 活动信息 */
.activity-info {
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20px);
	border-radius: 24rpx;
	padding: 25rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.15);
	border: 1rpx solid rgba(255, 255, 255, 0.2);
}

.activity-title {
	font-size: 36rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 15rpx;
	line-height: 1.3;
}

.activity-category {
	display: flex;
	align-items: center;
	gap: 10rpx;
	margin-bottom: 20rpx;
}

.category-icon {
	font-size: 24rpx;
}

.category-text {
	font-size: 24rpx;
	color: #667eea;
	font-weight: 500;
}

.activity-meta {
	display: flex;
	flex-wrap: wrap;
	gap: 20rpx;
}

.meta-item {
	display: flex;
	align-items: center;
	gap: 8rpx;
}

.meta-icon {
	font-size: 24rpx;
	color: #667eea;
}

.meta-text {
	font-size: 24rpx;
	color: #666;
}

/* 活动描述 */
.activity-description {
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20px);
	border-radius: 24rpx;
	padding: 25rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.15);
	border: 1rpx solid rgba(255, 255, 255, 0.2);
}

.section-title {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
	margin-bottom: 15rpx;
}

.description-content {
	font-size: 26rpx;
	color: #666;
	line-height: 1.6;
}

/* 主办方信息 */
.organizer-info {
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20px);
	border-radius: 24rpx;
	padding: 25rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.15);
	border: 1rpx solid rgba(255, 255, 255, 0.2);
}

.organizer-content {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.organizer-name {
	font-size: 26rpx;
	color: #333;
	font-weight: 500;
}

.organizer-time {
	font-size: 24rpx;
	color: #666;
}

/* 报名区域 */
.registration-section {
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20px);
	border-radius: 24rpx;
	padding: 25rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.15);
	border: 1rpx solid rgba(255, 255, 255, 0.2);
}

.registration-info {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
}

.info-item {
	display: flex;
	flex-direction: column;
	gap: 5rpx;
}

.info-label {
	font-size: 24rpx;
	color: #666;
}

.info-value {
	font-size: 26rpx;
	font-weight: 500;
}

.status-ongoing {
	color: #ff6b6b;
}

.status-ended {
	color: #9e9e9e;
}

.registration-btn {
	background: linear-gradient(135deg, #ff6b6b, #ff8e53);
	border-radius: 50rpx;
	padding: 15rpx 30rpx;
	display: flex;
	justify-content: center;
	align-items: center;
	font-size: 28rpx;
	color: white;
	font-weight: bold;
	box-shadow: 0 4rpx 15rpx rgba(255, 107, 107, 0.3);
	transition: all 0.3s ease;
}

.registration-btn:active {
	transform: scale(0.95);
	box-shadow: 0 2rpx 8rpx rgba(255, 107, 107, 0.3);
}

/* 底部提示 */
.bottom-tip {
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20px);
	border-radius: 24rpx;
	padding: 20rpx;
	text-align: center;
	font-size: 24rpx;
	color: #666;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.15);
	border: 1rpx solid rgba(255, 255, 255, 0.2);
}
</style>