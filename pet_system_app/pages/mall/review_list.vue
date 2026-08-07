<template>
	<view class="review-page" :style="{ paddingTop: statusBarHeight + 'px' }">
		<view class="top-nav">
			<view class="nav-tabs">
				<view class="nav-tab" @click="goBack">
					<text>简介</text>
				</view>
				<view class="nav-tab active">
					<text>评论（{{ stats.totalCount }}）</text>
				</view>
			</view>
			<view class="nav-close" @click="goBack">
				<text>✕</text>
			</view>
		</view>

		<view class="review-sort-bar">
			<view class="sort-item" :class="{ active: orderBy === 'create_time' }" @click="changeSort('create_time')">
				<text>按时间↓</text>
			</view>
			<view class="sort-item" :class="{ active: orderBy === 'like_count' }" @click="changeSort('like_count')">
				<text>按点赞</text>
			</view>
			<view class="sort-item" :class="{ active: orderBy === 'rating' }" @click="changeSort('rating')">
				<text>按评分</text>
			</view>
		</view>

		<scroll-view scroll-y class="comment-scroll" :style="{ height: scrollHeight + 'px' }" @scrolltolower="loadMore" refresher-enabled :refresher-triggered="refreshing" @refresherrefresh="onRefresh">
			<view class="comment-list">
				<view class="comment-card" v-for="(item, idx) in commentList" :key="item.reviewId">
					<view class="card-top-row">
						<view class="user-left">
							<view class="avatar-circle" :style="{ background: getAvatarColor(item.userName) }">
								<text>{{ getMaskedName(item.userName, item.isAnonymous).substring(0, 1) }}</text>
							</view>
							<text class="username">{{ getMaskedName(item.userName, item.isAnonymous) }}</text>
							<view class="level-badge">
								<text>LV{{ getLevel(item.userId) }}</text>
							</view>
						</view>
						<view class="floor-badge">
							<view class="floor-avatar-sm" :style="{ background: getAvatarColor(item.userName) }">
								<text>{{ getMaskedName(item.userName, item.isAnonymous).substring(0, 1) }}</text>
							</view>
							<text class="floor-num-text">CO.{{ String(item.reviewId).padStart(6, '0') }}</text>
						</view>
					</view>

					<text class="card-content">{{ item.content }}</text>

					<view class="image-area" v-if="item.images">
						<view v-for="(img, iIdx) in item.images.split(',').slice(0, 3)" :key="iIdx" class="img-box-lg">
							<view class="img-placeholder-lg"></view>
							<view class="exclusive-tag" v-if="iIdx === 0">
								<text>⚫ 专属</text>
							</view>
						</view>
					</view>

					<view class="spec-line" v-if="item.specName">
						<text>规格：{{ item.specName }}</text>
					</view>

					<view class="card-bottom-row">
						<view class="bottom-left">
							<text class="time-text">{{ timeAgo(item.createTime) }}</text>
							<text class="loc-text">{{ getLocText() }}</text>
							<view class="reply-btn-sm" @click="openReplyModal(item, null)">
								<text>回复</text>
							</view>
						</view>
						<view class="bottom-right">
							<view class="vote-btn" @click="handleLike(item)" :class="{ voted: isLiked(item.reviewId) }">
								<text class="vote-icon">👍</text>
								<text class="vote-count">{{ item.likeCount || 0 }}</text>
							</view>
							<view class="vote-btn down">
								<text class="vote-icon">👎</text>
							</view>
							<view class="more-dot-btn">
								<text>⋮</text>
							</view>
						</view>
					</view>

					<view class="reply-section" v-if="item.commentList && item.commentList.length > 0">
						<view class="reply-header">
							<text class="reply-hint-text">相关回复共{{ item.commentList.length }}条</text>
							<view class="reply-sort-btn">
								<text>☰ 按时间</text>
							</view>
						</view>
						<view class="reply-item" v-for="(rp, rIdx) in item.commentList" :key="rIdx">
							<view class="rp-header-row">
								<view class="rp-user-info">
									<view class="rp-avatar-circle" :class="{ merchant: rp.userType == 2 }" :style="{ background: rp.userType == 2 ? '#ff6b35' : getAvatarColor(rp.userName) }">
										<text v-if="rp.userType == 1">{{ getMaskedName(rp.userName).substring(0, 1) }}</text>
										<text v-else>商</text>
									</view>
									<text class="rp-name" :class="{ merchant: rp.userType == 2 }">{{ rp.userType == 2 ? (rp.shopName || '商家') : getMaskedName(rp.userName) }}</text>
									<view class="rp-level-badge" :class="{ merchant: rp.userType == 2 }">
										<text>LV{{ getLevel(rp.userId || rp.shopId) }}</text>
									</view>
									<view class="rp-floor-badge" v-if="rp.userType != 2">
										<text class="rp-floor-sep">#</text>
										<text class="rp-floor-id">{{ String(rp.commentId).padStart(4, '0') }}</text>
									</view>
								</view>
								<text class="rp-time">{{ timeAgo(rp.createTime) }}</text>
							</view>
							<text class="rp-body-text">{{ rp.content }}</text>
							<view class="rp-meta-row">
								<text class="rp-loc">{{ getLocText() }}</text>
								<view class="rp-reply-btn" @click="openReplyModal(item, rp)" v-if="rp.userType != 2">
									<text>回复</text>
								</view>
								<view class="rp-vote-group">
									<view class="rp-vote-inner" @click="handleLike(rp)" :class="{ voted: isLiked(rp.commentId) }">
										<text class="rp-vote-icon">👍</text>
										<text class="rp-vote-count">{{ rp.likeCount || 0 }}</text>
									</view>
									<view class="rp-vote-inner down">
										<text class="rp-vote-icon">👎</text>
									</view>
								</view>
							</view>
						</view>
					</view>
				</view>

				<view class="empty-tip" v-if="!loading && commentList.length === 0">
					<text>暂无评价，快来发表第一条评价吧~</text>
				</view>
				<view class="no-more-tip" v-if="!hasMore && commentList.length > 0">
					<text>—— 已经到底啦 ——</text>
				</view>
			</view>
		</scroll-view>

		<view class="bottom-input-bar">
			<view class="bibar-inner">
				<input class="bibar-input" type="text" placeholder="哎呦，不错哦，发条评论吧" v-model="globalInput" :adjust-position="false" :hold-keyboard="true" @confirm="submitGlobalComment" @focus="onGlobalInputFocus" />
				<view class="bibar-send" @click="submitGlobalComment" :class="{ active: globalInput.trim().length > 0 }">
					<text>发布</text>
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
import { getReviewList, getReviewStats, likeReview, addComment } from '@/api/pet/mall.js';

export default {
	data() {
		return {
			resourceId: null,
			scrollHeight: 500,
			statusBarHeight: 0,
			commentList: [],
			stats: {
				totalCount: 0,
				avgRating: 5.0,
				fiveCount: 0,
				fourCount: 0,
				threeCount: 0,
				twoCount: 0,
				oneCount: 0
			},
			orderBy: 'create_time',
			pageNum: 1,
			pageSize: 10,
			hasMore: true,
			loading: false,
			refreshing: false,
			likedIds: [],
			globalInput: '',
			replyModalVisible: false,
			replyModalFocus: false,
			replyModalContent: '',
			replyModalTitle: '',
			replyModalPlaceholder: '',
			replyModalReview: null,
			replyModalTarget: null
		};
	},
	onLoad(options) {
		this.resourceId = options.resourceId;
		var sysInfo = uni.getSystemInfoSync();
		this.statusBarHeight = sysInfo.statusBarHeight || 44;
		this.calculateHeight();
		this.loadStats();
		this.loadData(true);
	},
	methods: {
		calculateHeight() {
			var sysInfo = uni.getSystemInfoSync();
			var barH = this.statusBarHeight || 44;
			this.scrollHeight = sysInfo.windowHeight - barH - 100 - 110;
		},
		changeSort(type) {
			if (this.orderBy === type) return;
			this.orderBy = type;
			this.loadData(true);
		},
		loadStats() {
			var self = this;
			getReviewStats(self.resourceId).then(function(res) {
				if (res.code === 200 && res.data) {
					self.stats = res.data;
				}
			}).catch(function() {});
		},
		loadData(reset) {
			if (this.loading) return;
			if (reset) {
				this.pageNum = 1;
				this.hasMore = true;
			}
			if (!this.hasMore) return;
			this.loading = true;
			var self = this;
			getReviewList(self.resourceId, {
				orderBy: self.orderBy,
				isAsc: 'desc',
				pageNum: self.pageNum,
				pageSize: self.pageSize
			}).then(function(res) {
				self.loading = false;
				self.refreshing = false;
				if (res.code === 200) {
					var rows = res.data.rows || [];
					if (reset) {
						self.commentList = rows;
					} else {
						self.commentList = self.commentList.concat(rows);
					}
					if (rows.length < self.pageSize) {
						self.hasMore = false;
					} else {
						self.pageNum++;
					}
				}
			}).catch(function(err) {
				self.loading = false;
				self.refreshing = false;
			});
		},
		loadMore() {
			if (!this.loading && this.hasMore) {
				this.loadData(false);
			}
		},
		onRefresh() {
			this.refreshing = true;
			this.loadStats();
			this.loadData(true);
		},
		handleLike(item) {
			var itemId = item.reviewId || item.commentId;
			var idx = this.likedIds.indexOf(itemId);
			if (idx > -1) {
				this.likedIds.splice(idx, 1);
				item.likeCount = Math.max(0, (item.likeCount || 0) - 1);
			} else {
				this.likedIds.push(itemId);
				item.likeCount = (item.likeCount || 0) + 1;
				likeReview(itemId).catch(function(err) {});
			}
		},
		isLiked(itemId) {
			return this.likedIds.indexOf(itemId) > -1;
		},
		onGlobalInputFocus() {
			if (this.replyModalVisible) return;
		},
		submitGlobalComment() {
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
					self.stats.totalCount++;
					self.loadData(true);
				} else {
					uni.showToast({ title: res.msg || '评论失败', icon: 'none' });
				}
			}).catch(function() {
				uni.showToast({ title: '评论失败', icon: 'none' });
			});
		},
		openReplyModal(review, targetReply) {
			this.replyModalReview = review;
			this.replyModalTarget = targetReply;
			this.replyModalFocus = false;
			if (targetReply) {
				var name = targetReply.userType == 2 ? (targetReply.shopName || '商家') : this.getMaskedName(targetReply.userName);
				this.replyModalTitle = '回复 ' + name;
				this.replyModalPlaceholder = '回复 ' + name + '...';
			} else {
				var name = this.getMaskedName(review.userName, review.isAnonymous);
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
		closeReplyModal() {
			this.replyModalVisible = false;
			this.replyModalReview = null;
			this.replyModalTarget = null;
			this.replyModalContent = '';
			this.replyModalFocus = false;
		},
		submitReplyModal() {
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
						self.loadData(true);
					}
					self.closeReplyModal();
				} else {
					uni.showToast({ title: res.msg || '回复失败', icon: 'none' });
				}
			}).catch(function() {
				uni.showToast({ title: '回复失败', icon: 'none' });
			});
		},
		goBack() {
			uni.navigateBack();
		},
		getMaskedName(name, isAnonymous) {
			if (isAnonymous === 1) return '匿名用户';
			if (!name) return '用户';
			if (name.length <= 2) return name.charAt(0) + '*';
			return name.charAt(0) + '***' + name.charAt(name.length - 1);
		},
		timeAgo(time) {
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
		getLevel(id) {
			if (!id) return 1;
			var levels = [1, 2, 3, 4, 5, 6];
			return levels[id % 6];
		},
		getAvatarColor(name) {
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
		getLocText() {
			var locs = ['北京', '上海', '广州', '深圳', '成都', '杭州', '西藏', '四川', '广东'];
			return locs[Math.floor(Math.random() * locs.length)];
		}
	}
};
</script>

<style lang="scss" scoped>
.review-page {
	min-height: 100vh;
	background-color: #f8f8f8;
	display: flex;
	flex-direction: column;
	box-sizing: border-box;
}

.top-nav {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 16rpx 24rpx;
	background-color: #fff;
	border-bottom: 1rpx solid #f0f0f0;

	.nav-tabs {
		display: flex;
		align-items: center;
		gap: 48rpx;

		.nav-tab {
			padding: 8rpx 0;

			text {
				font-size: 30rpx;
				color: #999;
			}

			&.active {
				position: relative;

				text {
					color: #333;
					font-weight: bold;
					font-size: 32rpx;
				}

				&::after {
					content: '';
					position: absolute;
					bottom: -8rpx;
					left: 50%;
					transform: translateX(-50%);
					width: 40rpx;
					height: 6rpx;
					background-color: #ff4757;
					border-radius: 3rpx;
				}
			}
		}
	}

	.nav-close {
		width: 56rpx;
		height: 56rpx;
		display: flex;
		align-items: center;
		justify-content: center;

		text {
			font-size: 36rpx;
			color: #999;
		}
	}
}

.review-sort-bar {
	display: flex;
	align-items: center;
	padding: 16rpx 28rpx;
	background-color: #fff;
	border-bottom: 1rpx solid #f0f0f0;
	gap: 0;

	.sort-item {
		padding: 8rpx 20rpx;
		margin-right: 16rpx;
		background-color: #f5f5f5;
		border-radius: 20rpx;

		text {
			font-size: 24rpx;
			color: #888;
		}

		&.active {
			background-color: #fff0e6;

			text {
				color: #ff6b35;
				font-weight: 600;
			}
		}
	}
}

.comment-scroll {
	flex: 1;
	overflow: hidden;
}

.comment-list {
	padding: 0 28rpx 160rpx 28rpx;
}

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

		.reply-header {
			display: flex;
			align-items: center;
			justify-content: space-between;
			margin-bottom: 16rpx;
			padding-bottom: 12rpx;
			border-bottom: 1rpx solid #eee;

			.reply-hint-text {
				font-size: 25rpx;
				color: #666;
			}

			.reply-sort-btn {
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

				.rp-user-info {
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

					.rp-name {
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

					.rp-floor-badge {
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

				.rp-time {
					font-size: 22rpx;
					color: #bbb;
					flex-shrink: 0;
				}
			}

			.rp-body-text {
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

				.rp-loc {
					font-size: 22rpx;
					color: #bbb;
					margin-right: 20rpx;
				}

				.rp-reply-btn {
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

						.rp-vote-count {
							font-size: 21rpx;
							color: #999;
						}

						&.voted .rp-vote-count {
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

.empty-tip {
	padding: 120rpx 0;
	text-align: center;

	text {
		font-size: 28rpx;
		color: #999;
	}
}

.no-more-tip {
	text-align: center;
	padding: 30rpx 0 160rpx 0;

	text {
		font-size: 24rpx;
		color: #ccc;
	}
}

.bottom-input-bar {
	position: fixed;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 200;
	padding: 12rpx 24rpx;
	padding-bottom: calc(12rpx + env(safe-area-inset-bottom));
	background-color: #fff;
	border-top: 1rpx solid #e8e8e8;

	.bibar-inner {
		display: flex;
		align-items: center;
		background-color: #f5f5f5;
		border-radius: 40rpx;
		padding: 4rpx 4rpx 4rpx 28rpx;

		.bibar-input {
			flex: 1;
			height: 64rpx;
			font-size: 26rpx;
			color: #333;
		}

		.bibar-send {
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
