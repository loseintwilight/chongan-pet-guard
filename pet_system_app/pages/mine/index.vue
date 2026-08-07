<template>
	<view class="mine-container" :style="{ height: `${windowHeight}px` }">
		<!-- 顶部个人信息栏 -->
		<view class="header-section">
			<view class="flex padding justify-between">
				<view class="flex align-center">
					<view v-if="!avatar" class="cu-avatar xl round bg-white">
						<view class="iconfont icon-people text-gray icon"></view>
					</view>
					<image v-if="avatar" @click="handleToAvatar" :src="avatar" class="cu-avatar xl round" mode="widthFix"></image>
					<view v-if="!name" @click="handleToLogin" class="login-tip">点击登录</view>
					<view v-if="name" @click="handleToInfo" class="user-info">
						<view class="u_title">用户名：{{ name }}</view>
						<view class="u_subtitle">当前所在城市：济南</view>
					</view>
				</view>
				<view @click="handleToInfo" class="flex align-center">
					<text>修改信息</text>
					<view class="iconfont icon-edit"></view>
				</view>
			</view>
		</view>

		<!-- 宠物功能区域 -->
		<view class="pet-actions-section">
			<view class="pet-actions-container">
				<view class="pet-action-item" @click="handleToPetAdopt">
					<view class="pet-image-container">
						<image src="/static/images/adopt.png" class="pet-image" mode="aspectFit"></image>
					</view>
					<text class="pet-text">我的领养</text>
				</view>
				<view class="pet-action-item" @click="handleToPetRescue">
					<view class="pet-image-container">
						<image :src="require('@/static/images/help.png')" class="pet-image" mode="aspectFit"></image>
					</view>
					<text class="pet-text">我的救助</text>
				</view>
				<view class="pet-action-item" @click="handleToSearch">
					<view class="pet-image-container">
						<image :src="require('@/static/images/search.png')" class="pet-image" mode="aspectFit"></image>
					</view>
					<text class="pet-text">我的丢失</text>
				</view>
				<view class="pet-action-item" @click="handleToCare">
					<view class="pet-image-container">
						<image :src="require('@/static/images/care.png')" class="pet-image" mode="aspectFit"></image>
					</view>
					<text class="pet-text">我的托养</text>
				</view>
			</view>
		</view>

		<!-- 菜单列表 -->
		<view class="content-section">
			<view class="menu-list">
				<view class="list-item" @click="handleToPurchase">
					<view class="item-content">
						<view class="item-image-container">
							<image :src="require('@/static/images/shopping.png')" class="item-image" mode="aspectFit"></image>
						</view>
						<view class="item-text">购买记录</view>
						<view class="item-arrow">
							<view class="iconfont icon-right"></view>
						</view>
					</view>
				</view>
				<view class="list-item" @click="handleToActivity">
					<view class="item-content">
						<view class="item-image-container">
							<image :src="require('@/static/images/calendar.png')" class="item-image" mode="aspectFit"></image>
						</view>
						<view class="item-text">活动记录</view>
						<view class="item-arrow">
							<view class="iconfont icon-right"></view>
						</view>
					</view>
				</view>
				<view class="list-item" @click="handleToCollection">
					<view class="item-content">
						<view class="item-image-container">
							<image :src="require('@/static/images/favorite.png')" class="item-image" mode="aspectFit"></image>
						</view>
						<view class="item-text">我的收藏</view>
						<view class="item-arrow">
							<view class="iconfont icon-right"></view>
						</view>
					</view>
				</view>
				<view class="list-item" @click="handleToStore">
					<view class="item-content">
						<view class="item-image-container">
							<image :src="require('@/static/images/store.png')" class="item-image" mode="aspectFit"></image>
						</view>
						<view class="item-text">注册店铺</view>
						<view class="item-arrow">
							<view class="iconfont icon-right"></view>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 底部装饰 -->
		<view class="page-footer">
			<text class="footer-text">宠物伴侣与您同行</text>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			name: this.$store.state.user.name
		};
	},
	computed: {
		avatar() {
			return this.$store.state.user.avatar;
		},
		windowHeight() {
			return uni.getSystemInfoSync().windowHeight - 50;
		}
	},
	methods: {
		handleToInfo() {
			this.$tab.navigateTo('/pages/mine/info/index');
		},
		handleToAvatar() {
			this.$tab.navigateTo('/pages/mine/avatar/index');
		},
		handleToPurchase() {
			this.$tab.navigateTo('/pages/order/index');
		},
		handleToActivity() {
			this.$tab.navigateTo('/pages/activity/record');
		},
		handleToCollection() {
			this.$tab.navigateTo('/pages/mine/collection/index');
		},
		handleToStore() {
			this.$tab.navigateTo('/pages/shop/index');
		},
		handleToAuth() {
			this.$tab.navigateTo('/pages/auth/index');
		},
		handleToCommunity() {
			this.$modal.showToast('QQ群：①133713780(满)、②146013835(满)、③189091635');
		},
		handleToService() {
			this.$modal.showToast('在线客服功能建设中~');
		},
		handleToFeedback() {
			this.$modal.showToast('反馈社区功能建设中~');
		},
		handleToLike() {
			this.$modal.showToast('感谢您的支持！');
		},
		handleToPetAdopt() {
			this.$tab.navigateTo('/pages/myAdoption/index');
		},
		handleToPetRescue() {
			this.$tab.navigateTo('/pages/help/index');
		},
		handleToSearch() {
			this.$tab.navigateTo('/pages/search/index');
		},
		handleToCare() {
			this.$tab.navigateTo('/pages/care/index');
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

.mine-container {
	width: 100%;
	height: 100%;

	.header-section {
		padding: 30rpx 30rpx 60rpx 30rpx;
		background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
		color: #ffffff;
		border-radius: 0 0 30rpx 30rpx;
		box-shadow: 0 8rpx 25rpx rgba(79, 172, 254, 0.3);
		position: relative;
		overflow: hidden;

		/* 柔和光效 */
		&::after {
			content: '';
			position: absolute;
			top: 0;
			left: -100%;
			width: 50%;
			height: 100%;
			background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
			animation: soft-shine 3s infinite linear;
		}

		.login-tip {
			font-size: 28rpx;
			margin-left: 15rpx;
			letter-spacing: 1rpx;
		}

		.cu-avatar {
			border: 2rpx solid #ffffff;
			box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.1);

			.icon {
				font-size: 60rpx;
				color: #ffffff;
			}
		}

		.user-info {
			margin-left: 20rpx;

			.u_title {
				font-size: 32rpx;
				font-weight: 600;
				letter-spacing: 1rpx;
				margin-bottom: 10rpx;
			}

			.u_subtitle {
				font-size: 24rpx;
				color: rgba(255, 255, 255, 0.9);
				letter-spacing: 1rpx;
			}
		}
	}

	.pet-actions-section {
		padding: 0 30rpx 30rpx 30rpx;

		.pet-actions-container {
			display: flex;
			justify-content: space-between;
			background: rgba(255, 255, 255, 0.95);
			backdrop-filter: blur(10px);
			border-radius: 25rpx;
			padding: 30rpx 0;
			box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.1);
			border: 1rpx solid rgba(255, 255, 255, 0.2);

			.pet-action-item {
				flex: 1;
				display: flex;
				flex-direction: column;
				align-items: center;
				gap: 15rpx;
				padding: 0 20rpx;

				.pet-image-container {
					width: 70rpx;
					height: 70rpx;
					border-radius: 50%;
					background: rgba(79, 172, 254, 0.1);
					display: flex;
					justify-content: center;
					align-items: center;
					overflow: hidden;
					transition: all 0.3s ease;
				}

				.pet-image {
					width: 50rpx;
					height: 50rpx;
					object-fit: contain;
				}

				.pet-text {
					font-size: 24rpx;
					color: #4facfe;
					letter-spacing: 0.5rpx;
				}

				&:active .pet-image-container {
					transform: scale(0.9);
					background: rgba(79, 172, 254, 0.2);
				}
			}
		}
	}

	.content-section {
		position: relative;
		top: -30rpx;
		padding: 0 30rpx;

		.menu-list {
			margin-bottom: 30rpx;

			.list-item {
				background: rgba(255, 255, 255, 0.95);
				backdrop-filter: blur(10px);
				border-radius: 25rpx;
				margin-bottom: 20rpx;
				box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.1);
				border: 1rpx solid rgba(255, 255, 255, 0.2);
				transition: all 0.3s ease;

				&:active {
					transform: translateX(5rpx);
					box-shadow: 0 5rpx 15rpx rgba(0, 0, 0, 0.1);
				}

				.item-content {
					display: flex;
					align-items: center;
					padding: 30rpx;

					.item-image-container {
						width: 50rpx;
						height: 50rpx;
						border-radius: 12rpx;
						background: rgba(79, 172, 254, 0.1);
						display: flex;
						justify-content: center;
						align-items: center;
						margin-right: 20rpx;
						overflow: hidden;
						transition: all 0.3s ease;
					}

					.item-image {
						width: 30rpx;
						height: 30rpx;
						object-fit: contain;
					}

					.item-text {
						font-size: 30rpx;
						color: #495057;
						letter-spacing: 0.5rpx;
						flex: 1;
					}

					.item-arrow {
						width: 30rpx;
						height: 30rpx;
						display: flex;
						justify-content: center;
						align-items: center;

						.iconfont {
							font-size: 20rpx;
							color: #adb5bd;
						}
					}
				}
			}
		}
	}

	.function-section {
		padding: 0 30rpx 30rpx 30rpx;

		.function-container {
			display: flex;
			justify-content: space-between;
			background: rgba(255, 255, 255, 0.95);
			backdrop-filter: blur(10px);
			border-radius: 25rpx;
			padding: 30rpx 0;
			box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.1);
			border: 1rpx solid rgba(255, 255, 255, 0.2);

			.function-item {
				flex: 1;
				display: flex;
				flex-direction: column;
				align-items: center;
				gap: 15rpx;
				padding: 0 10rpx;

				.function-image-container {
					width: 70rpx;
					height: 70rpx;
					border-radius: 50%;
					background: rgba(79, 172, 254, 0.1);
					display: flex;
					justify-content: center;
					align-items: center;
					overflow: hidden;
					transition: all 0.3s ease;
				}

				.function-image {
					width: 50rpx;
					height: 50rpx;
					object-fit: contain;
				}

				.function-text {
					font-size: 24rpx;
					color: #4facfe;
					letter-spacing: 0.5rpx;
				}

				&:active .function-image-container {
					transform: scale(0.9);
					background: rgba(79, 172, 254, 0.2);
				}
			}
		}
	}

	.page-footer {
		margin-top: 40rpx;
		text-align: center;
		padding: 0 30rpx;

		.footer-text {
			font-size: 24rpx;
			color: #4facfe;
			letter-spacing: 1rpx;
			background: rgba(79, 172, 254, 0.1);
			padding: 10rpx 20rpx;
			border-radius: 50rpx;
			box-shadow: 0 2rpx 10rpx rgba(79, 172, 254, 0.2);
		}
	}
}

/* 柔和光效动画 */
@keyframes soft-shine {
	100% {
		left: 100%;
	}
}
</style>
