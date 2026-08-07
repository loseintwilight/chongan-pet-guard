<template>
	<view class="rescue-detail-container">
		<!-- 顶部标题栏 -->
		<view class="header-section">
			<view class="header-content">
				<view class="back-icon" @click="navigateBack">
					<view class="iconfont icon-back"></view>
				</view>
				<view class="title">救助详情</view>
				<view class="header-actions">
					<view class="action-btn" @click="shareDetail">
						<view class="iconfont icon-share"></view>
					</view>
				</view>
			</view>
		</view>

		<!-- 状态卡片 -->
		<view class="status-section">
			<view class="status-card">
				<!-- 使用条件渲染替代动态class -->
				<view v-if="rescueInfo.status === 0" class="status-icon status-pending">⏳</view>
				<view v-else-if="rescueInfo.status === 1" class="status-icon status-processing">🔄</view>
				<view v-else-if="rescueInfo.status === 2" class="status-icon status-completed">✅</view>
				<view v-else-if="rescueInfo.status === 3" class="status-icon status-cancelled">❌</view>
				<view v-else class="status-icon status-pending">⏳</view>

				<view class="status-info">
					<view class="status-title">{{ getStatusTitle(rescueInfo.status) }}</view>
					<view class="status-desc">{{ getStatusDesc(rescueInfo.status) }}</view>
				</view>
			</view>

			<!-- 审核进度 -->
			<view class="progress-timeline">
				<view class="timeline-item" v-for="(step, index) in progressSteps" :key="index">
					<view class="timeline-dot" :class="step.completed ? 'completed' : step.active ? 'active' : ''"></view>
					<view class="timeline-content">
						<view class="timeline-title">{{ step.title }}</view>
						<view class="timeline-time" v-if="step.time">{{ step.time }}</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 基本信息 -->
		<view class="info-section">
			<view class="section-title">
				<view class="title-icon">🐾</view>
				<view class="title-text">宠物信息</view>
			</view>
			<view class="info-card">
				<view class="pet-header">
					<image class="pet-image" :src="getImageUrl(rescueInfo.images)" mode="aspectFill"></image>
					<view class="pet-basic">
						<view class="pet-name">{{ rescueInfo.petName || '未知宠物' }}</view>
						<view class="pet-tags">
							<view class="pet-tag">{{ rescueInfo.petType }}</view>
							<view class="pet-tag">{{ rescueInfo.petBreed }}</view>
							<!-- 使用条件渲染替代动态class -->
							<view v-if="rescueInfo.petGender === 'male'" class="pet-tag gender-male">公</view>
							<view v-else-if="rescueInfo.petGender === 'female'" class="pet-tag gender-female">母</view>
							<view v-else class="pet-tag">未知</view>
						</view>
					</view>
				</view>
				<view class="pet-details">
					<view class="detail-item">
						<view class="detail-label">年龄</view>
						<view class="detail-value">{{ rescueInfo.petAge || '未知' }}</view>
					</view>
					<view class="detail-item">
						<view class="detail-label">毛色</view>
						<view class="detail-value">{{ rescueInfo.petColor || '未知' }}</view>
					</view>
					<view class="detail-item">
						<view class="detail-label">体型</view>
						<view class="detail-value">{{ rescueInfo.petSize || '未知' }}</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 救助信息 -->
		<view class="info-section">
			<view class="section-title">
				<view class="title-icon">📍</view>
				<view class="title-text">救助信息</view>
			</view>
			<view class="info-card">
				<view class="info-item">
					<view class="info-icon">📍</view>
					<view class="info-content">
						<view class="info-label">救助地点</view>
						<view class="info-value">{{ rescueInfo.location }}</view>
					</view>
				</view>
				<view class="info-item">
					<view class="info-icon">⏰</view>
					<view class="info-content">
						<view class="info-label">救助时间</view>
						<view class="info-value">{{ formatDateTime(rescueInfo.handleTime) }}</view>
					</view>
				</view>
				<view class="info-item">
					<view class="info-icon">📝</view>
					<view class="info-content">
						<view class="info-label">详细描述</view>
						<view class="info-value description">{{ rescueInfo.petDesc }}</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 联系信息 -->
		<view class="info-section">
			<view class="section-title">
				<view class="title-icon">👤</view>
				<view class="title-text">联系信息</view>
			</view>
			<view class="info-card">
				<view class="info-item">
					<view class="info-icon">👤</view>
					<view class="info-content">
						<view class="info-label">联系人</view>
						<view class="info-value">{{ rescueInfo.reporterName }}</view>
					</view>
				</view>
				<view class="info-item">
					<view class="info-icon">📱</view>
					<view class="info-content">
						<view class="info-label">联系电话</view>
						<view class="info-value phone" @click="makeCall(rescueInfo.reporterPhone)">{{ rescueInfo.reporterPhone }}</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 审核信息 -->
		<view class="info-section" v-if="rescueInfo.status !== 0">
			<view class="section-title">
				<view class="title-icon">📋</view>
				<view class="title-text">审核信息</view>
			</view>
			<view class="info-card">
				<view class="info-item">
					<view class="info-icon">👨‍💼</view>
					<view class="info-content">
						<view class="info-label">审核人员</view>
						<view class="info-value">{{ rescueInfo.handleId || '系统' }}</view>
					</view>
				</view>
				<view class="info-item">
					<view class="info-icon">📅</view>
					<view class="info-content">
						<view class="info-label">审核时间</view>
						<view class="info-value">{{ formatDateTime(rescueInfo.handleTime) }}</view>
					</view>
				</view>
				<view class="info-item" v-if="rescueInfo.handleRemark">
					<view class="info-icon">💬</view>
					<view class="info-content">
						<view class="info-label">审核意见</view>
						<view class="info-value remark">{{ rescueInfo.handleRemark }}</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 操作按钮 -->
		<view class="action-section">
			<view class="action-buttons">
				<!-- <view class="action-btn secondary" @click="editRescue" v-if="rescueInfo.status === 0">
					<view class="btn-icon">✏️</view>
					<view class="btn-text">修改信息</view>
				</view>
				<view class="action-btn secondary" @click="cancelRescue" v-if="rescueInfo.status === 0">
					<view class="btn-icon">❌</view>
					<view class="btn-text">取消救助</view>
				</view>
				<view class="action-btn primary" @click="contactRescue" v-if="rescueInfo.status === 1">
					<view class="btn-icon">📞</view>
					<view class="btn-text">联系救助</view>
				</view> -->
				<view class="action-btn primary" @click="shareDetail">
					<view class="btn-icon">📤</view>
					<view class="btn-text">分享详情</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { getRescueDetail, updateRescue, cancelRescue } from '@/api/pet/rescue.js';
import config from '@/config';

export default {
	data() {
		return {
			rescueId: '',
			rescueInfo: {},
			progressSteps: []
		};
	},
	onLoad(options) {
		if (options.id) {
			this.rescueId = options.id;
			this.loadRescueDetail();
		}
	},
	methods: {
		navigateBack() {
			uni.navigateBack();
		},
		// 加载救助详情
		async loadRescueDetail() {
			try {
				uni.showLoading({ title: '加载中...' });

				const response = await getRescueDetail(this.rescueId);

				if (response.code === 200) {
					this.rescueInfo = response.data;
					this.initProgressSteps();
				} else {
					uni.showToast({
						title: response.msg || '加载失败',
						icon: 'none'
					});
				}
			} catch (error) {
				console.error('加载救助详情失败:', error);
				uni.showToast({
					title: '加载失败',
					icon: 'none'
				});
			} finally {
				uni.hideLoading();
			}
		},
		// 初始化进度步骤
		initProgressSteps() {
			const steps = [
				{ title: '提交救助', time: this.formatDateTime(this.rescueInfo.createTime), completed: true, active: false },
				{ title: '审核中', time: '', completed: false, active: false },
				{ title: '救助中', time: '', completed: false, active: false },
				{ title: '已完成', time: '', completed: false, active: false }
			];

			const status = this.rescueInfo.status;

			if (status === 0) {
				steps[1].active = true;
			} else if (status === 1) {
				steps[1].completed = true;
				steps[1].time = this.formatDateTime(this.rescueInfo.auditTime);
				steps[2].active = true;
			} else if (status === 2) {
				steps[1].completed = true;
				steps[1].time = this.formatDateTime(this.rescueInfo.auditTime);
				steps[2].completed = true;
				steps[2].time = this.formatDateTime(this.rescueInfo.processTime);
				steps[3].completed = true;
				steps[3].time = this.formatDateTime(this.rescueInfo.completeTime);
			} else if (status === 3) {
				steps[1].completed = true;
				steps[1].time = this.formatDateTime(this.rescueInfo.auditTime);
			}

			this.progressSteps = steps;
		},
		// 获取状态标题
		getStatusTitle(status) {
			const titleMap = {
				0: '待处理',
				1: '已审核(处理中)',
				2: '已处理',
				3: '已审核(未通过)'
			};
			return titleMap[status] || '待处理';
		},
		// 获取状态描述
		getStatusDesc(status) {
			const descMap = {
				0: '您的救助申请正在审核中，请耐心等待',
				1: '救助人员正在处理，请保持电话畅通',
				2: '救助已完成，感谢您的爱心',
				3: '救助申请未通过审核'
			};
			return descMap[status] || '待处理';
		},
		// 获取图片URL
		getImageUrl(image) {
			if (!image) return '/static/images/default-pet.png';
			if (image.startsWith('http')) return image;
			return config.getDownloadUrl(image);
		},
		// 预览图片
		previewImage(index = 0) {
			const urls = this.rescueInfo.images.map((img) => this.getImageUrl(img));
			uni.previewImage({
				current: urls[index],
				urls: urls
			});
		},
		// 拨打电话
		makeCall(phone) {
			uni.makePhoneCall({
				phoneNumber: phone
			});
		},
		// 修改救助
		editRescue() {
			uni.navigateTo({
				url: `/pages/rescue/edit?id=${this.rescueId}`
			});
		},
		// 取消救助
		cancelRescue() {
			uni.showModal({
				title: '确认取消',
				content: '确定要取消这个救助申请吗？',
				success: async (res) => {
					if (res.confirm) {
						try {
							uni.showLoading({ title: '处理中...' });

							const response = await cancelRescue(this.rescueId);

							if (response.code === 200) {
								uni.showToast({
									title: '取消成功',
									icon: 'success'
								});
								setTimeout(() => {
									this.loadRescueDetail();
								}, 1500);
							} else {
								uni.showToast({
									title: response.msg || '取消失败',
									icon: 'none'
								});
							}
						} catch (error) {
							console.error('取消救助失败:', error);
							uni.showToast({
								title: '取消失败',
								icon: 'none'
							});
						} finally {
							uni.hideLoading();
						}
					}
				}
			});
		},
		// 联系救助
		contactRescue() {
			this.makeCall(this.rescueInfo.contactPhone);
		},
		// 分享详情
		shareDetail() {
			const content = `【救助记录】`;
			uni.setClipboardData({
				data: content,
				success: () => {
					uni.showToast({
						title: '已复制分享内容',
						icon: 'success'
					});
				}
			});
		},
		// 格式化日期时间
		formatDateTime(time) {
			if (!time) return '';
			const date = new Date(time);
			const year = date.getFullYear();
			const month = String(date.getMonth() + 1).padStart(2, '0');
			const day = String(date.getDate()).padStart(2, '0');
			const hours = String(date.getHours()).padStart(2, '0');
			const minutes = String(date.getMinutes()).padStart(2, '0');

			return `${year}-${month}-${day} ${hours}:${minutes}`;
		}
	}
};
</script>

<style lang="scss" scoped>
.rescue-detail-container {
	min-height: 100vh;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	padding-bottom: 120rpx;

	.header-section {
		background: rgba(255, 255, 255, 0.1);
		backdrop-filter: blur(10px);
		padding: 20rpx 30rpx;

		.header-content {
			display: flex;
			align-items: center;
			justify-content: space-between;

			.back-icon {
				width: 60rpx;
				height: 60rpx;
				display: flex;
				justify-content: center;
				align-items: center;
				color: #ffffff;
				font-size: 32rpx;
			}

			.title {
				font-size: 36rpx;
				font-weight: bold;
				color: #ffffff;
			}

			.header-actions {
				.action-btn {
					width: 60rpx;
					height: 60rpx;
					display: flex;
					justify-content: center;
					align-items: center;
					color: #ffffff;
					font-size: 28rpx;
				}
			}
		}
	}

	.status-section {
		margin: 20rpx 30rpx;

		.status-card {
			background: rgba(255, 255, 255, 0.95);
			border-radius: 20rpx;
			padding: 40rpx 30rpx;
			display: flex;
			align-items: center;
			backdrop-filter: blur(10px);
			margin-bottom: 20rpx;

			.status-icon {
				width: 100rpx;
				height: 100rpx;
				border-radius: 50%;
				display: flex;
				justify-content: center;
				align-items: center;
				font-size: 48rpx;
				margin-right: 30rpx;
			}

			.status-pending {
				background: linear-gradient(135deg, #ffc107 0%, #ff9800 100%);
			}

			.status-processing {
				background: linear-gradient(135deg, #17a2b8 0%, #138496 100%);
			}

			.status-completed {
				background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
			}

			.status-cancelled {
				background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
			}

			.status-info {
				flex: 1;

				.status-title {
					font-size: 32rpx;
					font-weight: bold;
					color: #2c3e50;
					margin-bottom: 10rpx;
				}

				.status-desc {
					font-size: 26rpx;
					color: #6c757d;
					line-height: 1.4;
				}
			}
		}

		.progress-timeline {
			background: rgba(255, 255, 255, 0.95);
			border-radius: 20rpx;
			padding: 30rpx;
			backdrop-filter: blur(10px);

			.timeline-item {
				display: flex;
				align-items: flex-start;
				margin-bottom: 30rpx;
				position: relative;

				&:not(:last-child)::after {
					content: '';
					position: absolute;
					left: 18rpx;
					top: 36rpx;
					width: 2rpx;
					height: 50rpx;
					background: #e9ecef;
				}

				.timeline-dot {
					width: 36rpx;
					height: 36rpx;
					border-radius: 50%;
					background: #e9ecef;
					border: 4rpx solid #ffffff;
					margin-right: 20rpx;
					position: relative;
					z-index: 1;
				}

				.timeline-dot.completed {
					background: #28a745;
					border-color: #28a745;
				}

				.timeline-dot.active {
					background: #4facfe;
					border-color: #4facfe;
					animation: pulse 2s infinite;
				}

				.timeline-content {
					flex: 1;

					.timeline-title {
						font-size: 28rpx;
						color: #2c3e50;
						margin-bottom: 8rpx;
					}

					.timeline-time {
						font-size: 24rpx;
						color: #6c757d;
					}
				}
			}
		}
	}

	.info-section {
		margin: 0 30rpx 20rpx;

		.section-title {
			display: flex;
			align-items: center;
			margin-bottom: 15rpx;

			.title-icon {
				font-size: 32rpx;
				margin-right: 10rpx;
			}

			.title-text {
				font-size: 30rpx;
				font-weight: bold;
				color: #ffffff;
			}
		}

		.info-card {
			background: rgba(255, 255, 255, 0.95);
			border-radius: 20rpx;
			padding: 30rpx;
			backdrop-filter: blur(10px);

			.pet-header {
				display: flex;
				align-items: center;
				margin-bottom: 20rpx;

				.pet-image {
					width: 120rpx;
					height: 120rpx;
					border-radius: 20rpx;
					margin-right: 20rpx;
				}

				.pet-basic {
					flex: 1;

					.pet-name {
						font-size: 32rpx;
						font-weight: bold;
						color: #2c3e50;
						margin-bottom: 15rpx;
					}

					.pet-tags {
						display: flex;
						flex-wrap: wrap;
						gap: 10rpx;

						.pet-tag {
							padding: 8rpx 16rpx;
							background: #f8f9fa;
							border-radius: 20rpx;
							font-size: 22rpx;
							color: #495057;
						}

						.gender-male {
							background: #e3f2fd;
							color: #1976d2;
						}

						.gender-female {
							background: #fce4ec;
							color: #c2185b;
						}
					}
				}
			}

			.pet-details {
				display: flex;
				flex-wrap: wrap;
				gap: 20rpx;

				.detail-item {
					flex: 1;
					min-width: 150rpx;

					.detail-label {
						font-size: 24rpx;
						color: #6c757d;
						margin-bottom: 8rpx;
					}

					.detail-value {
						font-size: 26rpx;
						color: #2c3e50;
						font-weight: 500;
					}
				}
			}

			.info-item {
				display: flex;
				align-items: flex-start;
				margin-bottom: 20rpx;

				&:last-child {
					margin-bottom: 0;
				}

				.info-icon {
					font-size: 28rpx;
					margin-right: 15rpx;
					margin-top: 2rpx;
				}

				.info-content {
					flex: 1;

					.info-label {
						font-size: 24rpx;
						color: #6c757d;
						margin-bottom: 8rpx;
					}

					.info-value {
						font-size: 28rpx;
						color: #2c3e50;
						line-height: 1.4;

						&.description {
							color: #495057;
						}

						&.phone {
							color: #4facfe;
							text-decoration: underline;
						}

						&.remark {
							color: #dc3545;
							background: #f8d7da;
							padding: 15rpx;
							border-radius: 10rpx;
						}
					}

					.image-gallery {
						display: flex;
						flex-wrap: wrap;
						gap: 10rpx;

						.gallery-image {
							width: 120rpx;
							height: 120rpx;
							border-radius: 10rpx;
						}
					}
				}
			}
		}
	}

	.action-section {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		background: rgba(255, 255, 255, 0.95);
		backdrop-filter: blur(10px);
		padding: 20rpx 30rpx;
		box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);

		.action-buttons {
			display: flex;
			gap: 20rpx;

			.action-btn {
				flex: 1;
				height: 80rpx;
				border-radius: 40rpx;
				display: flex;
				justify-content: center;
				align-items: center;
				font-weight: bold;
				transition: all 0.3s ease;

				&.secondary {
					background: #f8f9fa;
					color: #6c757d;
					border: 2rpx solid #e9ecef;

					&:active {
						background: #e9ecef;
					}
				}

				&.primary {
					background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
					color: #ffffff;

					&:active {
						background: linear-gradient(135deg, #3b82f6 0%, #0ea5e9 100%);
					}
				}

				.btn-icon {
					font-size: 28rpx;
					margin-right: 8rpx;
				}

				.btn-text {
					font-size: 28rpx;
				}
			}
		}
	}
}

@keyframes pulse {
	0% {
		box-shadow: 0 0 0 0 rgba(79, 172, 254, 0.7);
	}
	70% {
		box-shadow: 0 0 0 10rpx rgba(79, 172, 254, 0);
	}
	100% {
		box-shadow: 0 0 0 0 rgba(79, 172, 254, 0);
	}
}
</style>
