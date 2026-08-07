<template>
	<view class="collection-container" :style="{ height: `${windowHeight}px` }">
		<!-- 收藏列表 -->
		<view class="collection-list" ref="collectionList" @scrolltolower="handleScrollToLower">
			<view v-if="loading" class="loading-state">
				<view class="loading-icon">
					<view class="iconfont icon-loading"></view>
				</view>
				<view class="loading-text">加载中...</view>
			</view>

			<view v-else-if="collectionList.length === 0" class="empty-state">
				<view class="empty-icon">
					<view class="iconfont icon-heart"></view>
				</view>
				<view class="empty-text">暂无收藏的宠物</view>
				<view class="empty-subtext">去领养页面看看可爱的宠物吧</view>
			</view>

			<view v-else class="pet-card-list">
				<view class="pet-card" v-for="(pet, index) in collectionList" :key="pet.petId" @click="handleToPetDetail(pet)">
					<view class="pet-image-container">
						<image :src="getImageUrl(pet.images)" class="pet-image" mode="aspectFill" :draggable="false" />
						<view :class="['pet-status', getStatusClass(pet.status)]">
							<text>{{ getStatusText(pet.status) }}</text>
						</view>
					</view>
					<view class="pet-info">
						<view class="pet-name">{{ pet.name }}</view>
						<view class="pet-species">{{ pet.breed }}</view>
						<view class="pet-meta">
							<view class="meta-item">
								<view class="meta-icon">📍</view>
								<view class="meta-text">{{ pet.location }}</view>
							</view>
							<view class="meta-item">
								<view class="meta-icon">📅</view>
								<view class="meta-text">{{ formatDate(pet.createTime) }}</view>
							</view>
						</view>
						<view class="pet-actions">
							<view class="action-btn" @click.stop="handleRemoveFavorite(pet)">
								<view class="iconfont icon-heart"></view>
							</view>
						</view>
					</view>
				</view>
			</view>

			<!-- 加载更多 -->
			<view v-if="loadingMore" class="loading-more">
				<view class="loading-icon">
					<view class="iconfont icon-loading"></view>
				</view>
				<view class="loading-text">加载更多...</view>
			</view>
		</view>

		<!-- 底部提示 -->
		<view class="bottom-tip" v-if="collectionList.length > 0">
			<text class="tip-text">共 {{ collectionList.length }} 个收藏</text>
		</view>
	</view>
</template>

<script>
import { getCollectPetsList } from '@/api/pet/collect.js';
import config from '@/config';
export default {
	data() {
		return {
			windowHeight: 0,
			loading: true,
			loadingMore: false,
			collectionList: [],
			pageNum: 1,
			pageSize: 10,
			total: 0,
			hasMore: true
		};
	},
	onLoad() {
		//this.windowHeight = uni.getSystemInfoSync().windowHeight - 50;
		this.loadFavoritePets();
	},
	onPullDownRefresh() {
		this.refreshData();
	},
	methods: {
		refreshData() {
			this.pageNum = 1;
			this.collectionList = [];
			this.hasMore = true;
			this.loadFavoritePets(true);
		},
		async loadFavoritePets(isRefresh = true) {
			if (isRefresh) {
				this.loading = true;
			} else {
				this.loadingMore = true;
			}

			try {
				const response = await getCollectPetsList({
					pageNum: this.pageNum,
					pageSize: this.pageSize
				});
				console.log(response);
				if (response.code === 200) {
					const newData = response.rows || [];
					if (isRefresh) {
						this.collectionList = newData;
					} else {
						this.collectionList = [...this.collectionList, ...newData];
					}
					this.total = response.total || 0;
					this.hasMore = newData.length >= this.pageSize;
				} else {
					uni.showToast({
						title: '加载失败: ' + response.msg,
						icon: 'none'
					});
				}
			} catch (error) {
				uni.showToast({
					title: '加载失败: ' + error.message,
					icon: 'none'
				});
				console.error('加载收藏列表失败:', error);
			} finally {
				if (isRefresh) {
					uni.stopPullDownRefresh();
					this.loading = false;
				} else {
					this.loadingMore = false;
				}
			}
		},
		handleScrollToLower() {
			if (!this.loadingMore && this.hasMore) {
				this.pageNum++;
				this.loadFavoritePets();
			}
		},
		formatDate(dateString) {
			if (!dateString) return '未知时间';
			const date = new Date(dateString);
			const month = date.getMonth() + 1;
			const day = date.getDate();
			return `${month}月${day}日`;
		},
		getStatusText(status) {
			switch (status) {
				case 0:
					return '即将领养';
				case 1:
					return '可领养';
				case 2:
					return '已领养';
				default:
					return '未知状态';
			}
		},
		getStatusClass(status) {
			switch (status) {
				case 0:
					return 'status-upcoming';
				case 1:
					return 'status-available';
				case 2:
					return 'status-adopted';
				default:
					return 'status-unknown';
			}
		},
		getImageUrl(fileName) {
			return config.getDownloadUrl(fileName);
		},
		handleToPetDetail(pet) {
			uni.navigateTo({
				url: `/pages/adoption/detail?id=${pet.petId}`
			});
		},
		async handleRemoveFavorite(pet) {
			uni.showModal({
				title: '确认取消收藏',
				content: `确定要取消收藏"${pet.name}"吗？`,
				success: async (res) => {
					if (res.confirm) {
						try {
							// 这里调用取消收藏的API
							// const response = await removeFavorite(pet.petId);
							// if (response.code === 200) {
							this.collectionList = this.collectionList.filter((item) => item.petId !== pet.petId);
							uni.showToast({
								title: '取消收藏成功',
								icon: 'success'
							});
							// } else {
							//   uni.showToast({
							//     title: '取消收藏失败: ' + response.msg,
							//     icon: 'none'
							//   });
							// }
						} catch (error) {
							uni.showToast({
								title: '取消收藏失败: ' + error.message,
								icon: 'none'
							});
						}
					}
				}
			});
		}
	}
};
</script>

<style lang="scss" scoped>
page {
	background: #f8f9fa;
	background-image: radial-gradient(#e9ecef 1px, transparent 1px), radial-gradient(#e9ecef 1px, transparent 1px);
	background-size: 30rpx 30rpx;
	background-position: 0 0, 15rpx 15rpx;
}

.collection-container {
	width: 100%;
	height: 100%;

	.collection-list {
		padding: 20rpx 30rpx;
		min-height: calc(100% - 200rpx);
		overflow: auto;

		.loading-state {
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			padding: 60rpx 0;
			color: #6c757d;

			.loading-icon {
				font-size: 60rpx;
				margin-bottom: 20rpx;
				color: #adb5bd;
				animation: spin 1s linear infinite;
			}

			.loading-text {
				font-size: 28rpx;
			}
		}

		.empty-state {
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			padding: 60rpx 0;
			color: #6c757d;

			.empty-icon {
				font-size: 80rpx;
				margin-bottom: 20rpx;
				color: #adb5bd;
			}

			.empty-text {
				font-size: 28rpx;
				margin-bottom: 10rpx;
			}

			.empty-subtext {
				font-size: 24rpx;
				color: #adb5bd;
			}
		}

		.pet-card-list {
			display: flex;
			flex-direction: column;
			gap: 20rpx;
		}

		.pet-card {
			background: #ffffff;
			border-radius: 20rpx;
			overflow: hidden;
			box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.05);
			transition: all 0.3s ease;

			&:active {
				transform: translateY(-5rpx);
				box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.1);
			}

			.pet-image-container {
				position: relative;
				height: 200rpx;
				overflow: hidden;

				.pet-image {
					width: 100%;
					height: 100%;
					object-fit: cover;
				}

				.pet-status {
					position: absolute;
					top: 15rpx;
					right: 15rpx;
					padding: 8rpx 15rpx;
					border-radius: 20rpx;
					font-size: 22rpx;
					color: #ffffff;
					text-align: center;
					min-width: 80rpx;

					&.status-upcoming {
						background: #ffc107;
					}

					&.status-available {
						background: #28a745;
					}

					&.status-adopted {
						background: #6c757d;
					}

					&.status-unknown {
						background: #dc3545;
					}
				}
			}

			.pet-info {
				padding: 20rpx;
				position: relative;

				.pet-name {
					font-size: 30rpx;
					font-weight: 600;
					color: #212529;
					margin-bottom: 10rpx;
				}

				.pet-species {
					font-size: 26rpx;
					color: #6c757d;
					margin-bottom: 15rpx;
				}

				.pet-meta {
					display: flex;
					gap: 20rpx;
					margin-bottom: 15rpx;

					.meta-item {
						display: flex;
						align-items: center;
						gap: 8rpx;
						font-size: 24rpx;
						color: #6c757d;

						.meta-icon {
							font-size: 20rpx;
						}
					}
				}

				.pet-actions {
					position: absolute;
					bottom: 20rpx;
					right: 20rpx;

					.action-btn {
						width: 40rpx;
						height: 40rpx;
						display: flex;
						justify-content: center;
						align-items: center;
						color: #dc3545;
						transition: all 0.3s ease;

						&:active {
							transform: scale(1.2);
						}
					}
				}
			}
		}

		.loading-more {
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;
			padding: 30rpx 0;
			color: #6c757d;

			.loading-icon {
				font-size: 40rpx;
				margin-bottom: 10rpx;
				color: #adb5bd;
				animation: spin 1s linear infinite;
			}

			.loading-text {
				font-size: 24rpx;
			}
		}
	}

	.bottom-tip {
		padding: 20rpx 30rpx;
		background: #ffffff;
		border-top: 1rpx solid #e9ecef;
		text-align: center;

		.tip-text {
			font-size: 24rpx;
			color: #6c757d;
		}
	}
}

@keyframes spin {
	0% {
		transform: rotate(0deg);
	}
	100% {
		transform: rotate(360deg);
	}
}
</style>
