<template>
	<view class="function-page">
		<!-- 顶部标题栏 -->
		<view class="page-header">
			<text class="header-title">功能中心</text>
		</view>

		<!-- 功能卡片网格 -->
		<view class="function-grid">
			<!-- 宠物科普 -->
			<view class="function-card" @click="navigateTo('/pages/article/index')">
				<view class="card-icon">
					<image class="icon-image" src="/static/work/宠物科普.png" mode="aspectFit" />
				</view>
				<text class="card-title">宠物科普</text>
			</view>

			<!-- 活动推荐 -->
			<view class="function-card" @click="navigateTo('/pages/activity/index')">
				<view class="card-icon">
					<image class="icon-image" src="/static/work/活动推荐.png" mode="aspectFit" />
				</view>
				<text class="card-title">活动推荐</text>
			</view>

			<!-- 救助登记 -->
			<view class="function-card" @click="navigateTo('/pages/rescue/rescue')">
				<view class="card-icon">
					<image class="icon-image" src="/static/work/救助登记.png" mode="aspectFit" />
				</view>
				<text class="card-title">救助登记</text>
			</view>

			<!-- 附近店铺/站点 -->
			<view class="function-card" @click="navigateTo('/pages/store/stores')">
				<view class="card-icon">
					<image class="icon-image" src="/static/work/附近店铺.png" mode="aspectFit" />
				</view>
				<text class="card-title">附近店铺</text>
			</view>

			<!-- 寻宠启事 -->
			<view class="function-card" @click="navigateTo('/pages/lostPet/index')">
				<view class="card-icon">
					<image class="icon-image" src="/static/work/寻宠启事.png" mode="aspectFit" />
				</view>
				<text class="card-title">寻宠启事</text>
			</view>

			<!-- 宠物领养 -->
			<view class="function-card" @click="navigateTo('/pages/adoption/index')">
				<view class="card-icon">
					<image class="icon-image" src="/static/work/宠物领养.png" mode="aspectFit" />
				</view>
				<text class="card-title">宠物领养</text>
			</view>

			<!-- 宠物托养 -->
			<view class="function-card" @click="navigateTo('/pages/boarding/index')">
				<view class="card-icon">
					<image class="icon-image" src="/static/work/宠物托养.png" mode="aspectFit" />
				</view>
				<text class="card-title">宠物托养</text>
			</view>

			<!-- 爱心捐助 -->
			<view class="function-card" @click="navigateTo('/pages/donation/index')">
				<view class="card-icon">
					<image class="icon-image" src="/static/work/爱心捐助.png" mode="aspectFit" />
				</view>
				<text class="card-title">爱心捐助</text>
			</view>


			<!-- 商品购买 -->
			<view class="function-card" @click="navigateTo('/pages/mall/index')">
				<view class="card-icon">
					<image class="icon-image" src="/static/work/shopping.png" mode="aspectFit" />
				</view>
				<text class="card-title">商品购买</text>
			</view>

		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {};
	},
	methods: {
		navigateTo(url) {
			uni.navigateTo({
				url: url
			});
		}
	}
};
</script>

<style lang="scss" scoped>
.function-page {
	min-height: 100vh;
	background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
	padding: 30rpx;
	box-sizing: border-box;
}

/* 顶部标题栏 */
.page-header {
	text-align: center;
	padding: 40rpx 0 30rpx;
	font-size: 40rpx;
	font-weight: bold;
	color: #2c3e50;
	letter-spacing: 2rpx;
}

/* 功能卡片网格 */
.function-grid {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 35rpx;
}

/* 功能卡片 */
.function-card {
	background: #ffffff;
	border-radius: 20rpx;
	box-shadow: 0 10rpx 30rpx rgba(0, 0, 0, 0.08);
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
	cursor: pointer;
	overflow: hidden;
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 25rpx 0;

	&:hover {
		transform: translateY(-5rpx);
		box-shadow: 0 15rpx 40rpx rgba(0, 0, 0, 0.12);
	}

	&:active {
		transform: scale(0.98);
	}
}

/* 卡片图标容器 */
.card-icon {
	width: 120rpx;
	height: 120rpx;
	margin-bottom: 15rpx; /* 调整图标与文字的间距 */
	border-radius: 50%;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	display: flex;
	justify-content: center;
	align-items: center;
	overflow: hidden;
	transition: all 0.3s ease;
}

/* 卡片图标图片 */
.icon-image {
	width: 60rpx;
	height: 60rpx;
	transition: all 0.3s ease;
	filter: brightness(0) invert(1); /* 将图标变为白色 */
}

/* 卡片标题 - 确保居中 */
.card-title {
	font-size: 30rpx;
	color: #2c3e50;
	font-weight: 500;
	text-align: center;
	transition: all 0.3s ease;
}

/* 卡片悬停效果 - 图标放大 */
.function-card:hover .card-icon {
	transform: scale(1.1);
}

/* 卡片悬停效果 - 标题颜色变化 */
.function-card:hover .card-title {
	color: #667eea;
}

/* 底部导航栏 */
.bottom-nav {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background: #ffffff;
	display: flex;
	justify-content: space-around;
	align-items: center;
	padding: 15rpx 0;
	box-shadow: 0 -4rpx 15rpx rgba(0, 0, 0, 0.05);
}

/* 导航项 */
.nav-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	width: 80rpx;
}

/* 导航图标 */
.nav-icon {
	width: 40rpx;
	height: 40rpx;
	margin-bottom: 5rpx;
	transition: all 0.3s ease;
}

/* 导航文字 */
.nav-text {
	font-size: 22rpx;
	color: #999;
	transition: all 0.3s ease;
}

/* 激活的导航项 */
.nav-item.nav-active .nav-icon {
	filter: brightness(1.2);
}

.nav-item.nav-active .nav-text {
	color: #667eea;
	font-weight: bold;
}
</style>
