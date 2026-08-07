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
			<!-- 使用rich-text组件渲染Markdown解析后的HTML -->
			<rich-text class="description-content" :nodes="parsedContent"></rich-text>
		</view>

		<!-- 活动规则 -->
		<view class="activity-rules">
			<view class="section-title">活动规则</view>
			<view class="rules-content">
				<view class="rule-item" v-for="(rule, index) in rules" :key="index">
					<text class="rule-number">{{ index + 1 }}.</text>
					<text class="rule-text">{{ rule }}</text>
				</view>
			</view>
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
			<view class="registration-btn" @click="handleRegistration" :class="{ disabled: activity.status !== 0 || activity.count >= activity.personCount }">
				<text class="btn-text">{{ getButtonText() }}</text>
			</view>
		</view>

		<!-- 底部提示 -->
		<view class="bottom-tip" v-if="activity.status === 2">
			<text class="tip-text">📋 此活动已结束，感谢您的关注！</text>
		</view>
	</view>
</template>

<script>
import { getActivity, signUp } from '@/api/pet/activity';
import { getDicts } from '@/api/system/dict/data';
import config from '@/config';
// 引入marked库用于解析Markdown
import marked from 'marked';

export default {
	data() {
		return {
			activity: {}, // 活动详情
			activityTypeDict: {}, // 活动类型字典
			rules: ['请提前15分钟到达活动现场', '请携带宠物相关证件', '活动期间请照顾好您的宠物', '请遵守现场工作人员的安排'],
			isUserSignedUp: false,
			parsedContent: '' // 解析后的HTML内容
		};
	},
	computed: {
		// 监听活动内容变化，自动解析Markdown
		watchContent() {
			if (this.activity.content) {
				this.parseMarkdown(this.activity.content);
			}
		}
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

					// 解析Markdown内容
					if (data.content) {
						this.parseMarkdown(data.content);
					}
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

		/** 解析Markdown内容 */
		parseMarkdown(markdown) {
			try {
				// 配置marked选项
				marked.setOptions({
					highlight: function (code, lang) {
						// 如果有代码高亮需求，可以在这里处理
						return code;
					},
					breaks: true, // 支持换行
					sanitize: false // 允许HTML标签（根据需求调整）
				});

				// 解析Markdown为HTML
				let html = marked(markdown);

				// 对图片路径进行处理，确保能正确显示
				html = html.replace(/<img([^>]*)src="([^"]*)"([^>]*)>/g, (match, before, src, after) => {
					// 如果是相对路径，转换为完整URL
					if (src && !src.startsWith('http') && !src.startsWith('//')) {
						const fullUrl = config.getDownloadUrl(src);
						return `<img${before}src="${fullUrl}"${after}>`;
					}
					return match;
				});

				// 添加样式类，便于后续样式定制
				html = `<div class="markdown-content">${html}</div>`;

				this.parsedContent = html;
			} catch (error) {
				console.error('Markdown解析失败:', error);
				// 解析失败时显示原始内容
				this.parsedContent = `<div class="markdown-content">${markdown}</div>`;
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

		/** 获取按钮文本 */
		getButtonText() {
			// 优先判断用户是否已报名
			if (this.isUserSignedUp) {
				return '已报名';
			}
			// 再根据活动状态判断
			if (this.activity.status === 0) {
				return '立即报名';
			} else if (this.activity.status === 1) {
				return '活动进行中';
			} else {
				return '活动已结束';
			}
		},

		/** 处理报名 */
		handleRegistration() {
			// 如果用户已报名，点击按钮不做任何事（或者可以提示"您已报名"）
			if (this.isUserSignedUp) {
				uni.showToast({ title: '您已经报名过了', icon: 'none' });
				return;
			}

			// 如果活动不是报名中，提示用户
			if (this.activity.status !== 0) {
				uni.showToast({ title: '当前不在报名时间内', icon: 'none' });
				return;
			}

			// 如果名额已满，提示用户
			if (this.activity.count >= this.activity.personCount) {
				uni.showToast({ title: '名额已满', icon: 'none' });
				return;
			}

			// 通过所有检查，弹出确认框
			uni.showModal({
				title: '确认报名',
				content: '确定要报名参加这个活动吗？',
				success: (res) => {
					if (res.confirm) {
						this.submitRegistration();
					}
				}
			});
		},

		/** 提交报名 */
		async submitRegistration() {
			try {
				uni.showLoading({ title: '报名中...', mask: true });
				// 调用报名接口
				const response = await signUp(this.activityId);

				if (response.code === 200) {
					// 报名成功
					uni.hideLoading();
					uni.showToast({ title: '报名成功！', icon: 'success' });

					// 【关键】报名成功后，重新加载活动详情
					// 这会更新 activity.count (参与人数)，并且如果后端接口设计得好，也会更新用户的报名状态
					this.loadActivityDetail();
				} else {
					uni.hideLoading();
					uni.showToast({ title: response.msg || '报名失败', icon: 'none' });
				}
			} catch (error) {
				uni.hideLoading();
				uni.showToast({ title: '网络错误，报名失败', icon: 'none' });
				console.error('报名失败:', error);
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
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	padding: 20rpx;
	box-sizing: border-box;
	position: relative;

	&::before {
		content: '';
		position: absolute;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		background: radial-gradient(circle at 20% 50%, rgba(120, 119, 198, 0.3) 0%, transparent 50%),
			radial-gradient(circle at 80% 80%, rgba(255, 119, 198, 0.3) 0%, transparent 50%), radial-gradient(circle at 40% 20%, rgba(255, 219, 118, 0.2) 0%, transparent 50%);
		pointer-events: none;
	}
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

/* Markdown内容样式 */
::v-deep .markdown-content {
	font-size: 26rpx;
	color: #666;
	line-height: 1.6;

	h1,
	h2,
	h3,
	h4,
	h5,
	h6 {
		color: #333;
		margin: 20rpx 0 10rpx;
		font-weight: bold;
	}

	h1 {
		font-size: 32rpx;
	}
	h2 {
		font-size: 30rpx;
	}
	h3 {
		font-size: 28rpx;
	}
	h4 {
		font-size: 26rpx;
	}

	p {
		margin-bottom: 15rpx;
	}

	ul,
	ol {
		padding-left: 30rpx;
		margin-bottom: 15rpx;
	}

	li {
		margin-bottom: 8rpx;
	}

	blockquote {
		border-left: 4rpx solid #667eea;
		padding-left: 20rpx;
		margin: 15rpx 0;
		color: #888;
		font-style: italic;
	}

	code {
		background-color: #f5f5f5;
		padding: 2rpx 6rpx;
		border-radius: 4rpx;
		font-family: monospace;
		font-size: 24rpx;
	}

	pre {
		background-color: #f5f5f5;
		padding: 15rpx;
		border-radius: 8rpx;
		overflow-x: auto;
		margin: 15rpx 0;

		code {
			background: none;
			padding: 0;
		}
	}

	img {
		max-width: 100%;
		height: auto;
		border-radius: 8rpx;
		margin: 10rpx 0;
	}

	table {
		width: 100%;
		border-collapse: collapse;
		margin: 15rpx 0;

		th,
		td {
			border: 1rpx solid #e0e0e0;
			padding: 10rpx;
			text-align: left;
		}

		th {
			background-color: #f5f5f5;
			font-weight: bold;
		}
	}

	a {
		color: #667eea;
		text-decoration: underline;
	}

	hr {
		border: none;
		border-top: 1rpx solid #e0e0e0;
		margin: 20rpx 0;
	}
}

/* 活动规则 */
.activity-rules {
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20px);
	border-radius: 24rpx;
	padding: 25rpx;
	margin-bottom: 20rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.15);
	border: 1rpx solid rgba(255, 255, 255, 0.2);
}

.rules-content {
	font-size: 26rpx;
	color: #666;
	line-height: 1.6;
}

.rule-item {
	display: flex;
	align-items: flex-start;
	gap: 10rpx;
	margin-bottom: 12rpx;
}

.rule-number {
	font-size: 24rpx;
	font-weight: bold;
	color: #667eea;
	min-width: 24rpx;
}

.rule-text {
	flex: 1;
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
	background: linear-gradient(135deg, #667eea, #764ba2);
	border-radius: 50rpx;
	padding: 15rpx 30rpx;
	display: flex;
	justify-content: center;
	align-items: center;
	font-size: 28rpx;
	color: white;
	font-weight: bold;
	box-shadow: 0 4rpx 15rpx rgba(102, 126, 234, 0.3);
	transition: all 0.3s ease;
}

.registration-btn:active {
	transform: scale(0.95);
	box-shadow: 0 2rpx 8rpx rgba(102, 126, 234, 0.3);
}

.registration-btn.disabled {
	background: #ccc;
	cursor: not-allowed;
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
