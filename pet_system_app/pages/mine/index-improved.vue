<template>
	<view class="mine-container">
		<!-- 顶部渐变背景 -->
		<view class="background-gradient"></view>
		
		<!-- 用户资料卡片 -->
		<view class="profile-section">
			<view class="profile-card">
				<view class="profile-header">
					<view class="avatar-section">
						<view v-if="!avatar" class="avatar-placeholder" @click="handleToLogin">
							<text class="avatar-icon">👤</text>
						</view>
						<image v-if="avatar" @click="handleToAvatar" :src="avatar" class="user-avatar" mode="aspectFill"></image>
						<view class="level-badge" v-if="name">
							<text class="level-text">Lv.{{ userLevel }}</text>
						</view>
					</view>
					<view class="profile-info">
						<view v-if="!name" class="login-section" @click="handleToLogin">
							<text class="login-title">点击登录</text>
							<text class="login-subtitle">开启您的宠物之旅</text>
						</view>
						<view v-if="name" class="user-section" @click="handleToInfo">
							<text class="user-name">{{ name }}</text>
							<text class="user-id">ID: {{ userId }}</text>
							<view class="user-tags">
								<text class="tag premium-tag" v-if="isPremium">VIP</text>
								<text class="tag active-tag">活跃用户</text>
							</view>
						</view>
					</view>
					<view class="profile-actions">
						<view class="action-btn" @click="handleToSettings">
							<text class="action-icon">⚙️</text>
						</view>
						<view class="action-btn" @click="handleToMessages">
							<text class="action-icon">💬</text>
							<view class="message-badge" v-if="unreadCount > 0">
								<text class="badge-text">{{ unreadCount }}</text>
							</view>
						</view>
					</view>
				</view>
				
				<!-- 用户统计 -->
				<view class="stats-section" v-if="name">
					<view class="stat-item">
						<text class="stat-number">{{ totalAdoptions }}</text>
						<text class="stat-label">领养</text>
					</view>
					<view class="stat-item">
						<text class="stat-number">{{ totalRescues }}</text>
						<text class="stat-label">救助</text>
					</view>
					<view class="stat-item">
						<text class="stat-number">{{ totalDonations }}</text>
						<text class="stat-label">捐助</text>
					</view>
					<view class="stat-item">
						<text class="stat-number">{{ totalActivities }}</text>
						<text class="stat-label">活动</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 成就徽章 -->
		<view class="achievements-section" v-if="name">
			<view class="section-header">
				<text class="section-title">🏆 我的成就</text>
				<text class="section-more" @click="toAllAchievements">查看全部</text>
			</view>
			<scroll-view scroll-x class="achievements-scroll" show-scrollbar="false">
				<view class="achievement-item" v-for="achievement in achievements" :key="achievement.id">
					<view class="achievement-icon" :class="{ 'achieved': achievement.achieved }">
						<text class="icon-text">{{ achievement.icon }}</text>
					</view>
					<text class="achievement-name">{{ achievement.name }}</text>
				</view>
			</scroll-view>
		</view>

		<!-- 快捷服务 -->
		<view class="quick-services-section">
			<view class="section-header">
				<text class="section-title">⚡ 快捷服务</text>
			</view>
			<view class="services-grid">
				<view class="service-card" @click="handleToPetAdopt">
					<view class="service-icon-wrapper">
						<text class="service-icon">🏠</text>
					</view>
					<text class="service-title">我的领养</text>
					<text class="service-count">{{ adoptionCount }}只</text>
				</view>
				<view class="service-card" @click="handleToPetRescue">
					<view class="service-icon-wrapper">
						<text class="service-icon">🆘</text>
					</view>
					<text class="service-title">我的救助</text>
					<text class="service-count">{{ rescueCount }}次</text>
				</view>
				<view class="service-card" @click="handleToSearch">
					<view class="service-icon-wrapper">
						<text class="service-icon">🔍</text>
					</view>
					<text class="service-title">寻宠启事</text>
					<text class="service-count">{{ searchCount }}条</text>
				</view>
				<view class="service-card" @click="handleToCare">
					<view class="service-icon-wrapper">
						<text class="service-icon">🛏️</text>
					</view>
					<text class="service-title">宠物托养</text>
					<text class="service-count">{{ careCount }}只</text>
				</view>
			</view>
		</view>

		<!-- 功能菜单 -->
		<view class="menu-section">
			<view class="section-header">
				<text class="section-title">📋 功能菜单</text>
			</view>
			<view class="menu-list">
				<view class="menu-group">
					<text class="group-title">🛒 交易相关</text>
					<view class="menu-item" @click="handleToPurchase">
						<view class="menu-icon-wrapper">
							<text class="menu-icon">🧾</text>
						</view>
						<text class="menu-title">购买记录</text>
						<text class="menu-subtitle">查看您的购买历史</text>
						<text class="menu-arrow">→</text>
					</view>
					<view class="menu-item" @click="handleToCollection">
						<view class="menu-icon-wrapper">
							<text class="menu-icon">❤️</text>
						</view>
						<text class="menu-title">我的收藏</text>
						<text class="menu-subtitle">收藏的宠物和内容</text>
						<text class="menu-arrow">→</text>
					</view>
				</view>

				<view class="menu-group">
					<text class="group-title">📅 活动记录</text>
					<view class="menu-item" @click="handleToActivity">
						<view class="menu-icon-wrapper">
							<text class="menu-icon">📆</text>
						</view>
						<text class="menu-title">活动记录</text>
						<text class="menu-subtitle">参与的活动详情</text>
						<text class="menu-arrow">→</text>
					</view>
					<view class="menu-item" @click="handleToStore">
						<view class="menu-icon-wrapper">
							<text class="menu-icon">🏪</text>
						</view>
						<text class="menu-title">注册店铺</text>
						<text class="menu-subtitle">申请成为合作商家</text>
						<text class="menu-arrow">→</text>
					</view>
				</view>

				<view class="menu-group">
					<text class="group-title">⚙️ 设置中心</text>
					<view class="menu-item" @click="handleToInfo">
						<view class="menu-icon-wrapper">
							<text class="menu-icon">👤</text>
						</view>
						<text class="menu-title">个人资料</text>
						<text class="menu-subtitle">编辑您的个人信息</text>
						<text class="menu-arrow">→</text>
					</view>
					<view class="menu-item" @click="handleToSettings">
						<view class="menu-icon-wrapper">
							<text class="menu-icon">🔧</text>
						</view>
						<text class="menu-title">应用设置</text>
						<text class="menu-subtitle">个性化您的体验</text>
						<text class="menu-arrow">→</text>
					</view>
				</view>
			</view>
		</view>

		<!-- VIP推广卡片 -->
		<view class="vip-promo-section" v-if="!isPremium">
			<view class="vip-card">
				<view class="vip-content">
					<view class="vip-title">
						<text class="vip-icon">👑</text>
						<text class="vip-text">升级VIP会员</text>
					</view>
					<text class="vip-description">享受专属特权，解锁更多功能</text>
					<view class="vip-benefits">
						<text class="benefit-item">✨ 专属标识</text>
						<text class="benefit-item">🎁 优先服务</text>
						<text class="benefit-item">📞 专属客服</text>
					</view>
				</view>
				<view class="vip-action">
					<button class="vip-btn" @click="handleToVip">立即升级</button>
				</view>
			</view>
		</view>

		<!-- 底部信息 -->
		<view class="footer-section">
			<text class="footer-text">🐾 宠物伴侣与您同行</text>
			<text class="version-text">版本 v2.0.1</text>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			name: this.$store.state.user.name,
			avatar: this.$store.state.user.avatar,
			
			// 用户数据
			userLevel: 5,
			userId: '168888',
			isPremium: false,
			unreadCount: 3,
			
			// 统计数据
			totalAdoptions: 2,
			totalRescues: 5,
			totalDonations: 8,
			totalActivities: 12,
			
			// 服务计数
			adoptionCount: 2,
			rescueCount: 5,
			searchCount: 1,
			careCount: 3,
			
			// 成就系统
			achievements: [
				{ id: 1, name: '新手领养', icon: '🏠', achieved: true },
				{ id: 2, name: '爱心救助', icon: '❤️', achieved: true },
				{ id: 3, name: '活跃参与', icon: '⭐', achieved: true },
				{ id: 4, name: '慈善捐助', icon: '💝', achieved: true },
				{ id: 5, name: '社交达人', icon: '👥', achieved: false },
				{ id: 6, name: '忠实用户', icon: '🏆', achieved: false }
			]
		};
	},
	
	onLoad() {
		this.loadUserData();
	},
	
	methods: {
		loadUserData() {
			// 这里可以加载用户数据
			console.log('加载用户数据');
		},
		
		// 导航方法
		handleToLogin() {
			uni.navigateTo({
				url: '/pages/login'
			});
		},
		
		handleToInfo() {
			uni.navigateTo({
				url: '/pages/mine/info/index'
			});
		},
		
		handleToAvatar() {
			uni.navigateTo({
				url: '/pages/mine/avatar/index'
			});
		},
		
		handleToSettings() {
			uni.navigateTo({
				url: '/pages/mine/setting/index'
			});
		},
		
		handleToMessages() {
			uni.navigateTo({
				url: '/pages/notice/index'
			});
		},
		
		handleToPetAdopt() {
			uni.navigateTo({
				url: '/pages/myAdoption/index'
			});
		},
		
		handleToPetRescue() {
			uni.navigateTo({
				url: '/pages/help/index'
			});
		},
		
		handleToSearch() {
			uni.navigateTo({
				url: '/pages/search/index'
			});
		},
		
		handleToCare() {
			uni.navigateTo({
				url: '/pages/care/index'
			});
		},
		
		handleToPurchase() {
			uni.navigateTo({
				url: '/pages/order/index'
			});
		},
		
		handleToActivity() {
			uni.navigateTo({
				url: '/pages/activity/record'
			});
		},
		
		handleToCollection() {
			uni.navigateTo({
				url: '/pages/mine/collection/index'
			});
		},
		
		handleToStore() {
			uni.navigateTo({
				url: '/pages/shop/index'
			});
		},
		
		handleToVip() {
			uni.showToast({
				title: 'VIP功能开发中',
				icon: 'none'
			});
		},
		
		toAllAchievements() {
			uni.showToast({
				title: '成就系统开发中',
				icon: 'none'
			});
		}
	}
};
</script>

<style lang="scss" scoped>
/* --- 全局与容器 --- */
.mine-container {
	position: relative;
	min-height: 100vh;
	background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
}

/* --- 背景渐变 --- */
.background-gradient {
	position: absolute;
	top: 0;
	left: 0;
	right: 0;
	height: 500rpx;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	z-index: 0;
}

/* --- 用户资料卡片 --- */
.profile-section {
	position: relative;
	z-index: 10;
	padding: 40rpx 30rpx 0;
}

.profile-card {
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(20px);
	border-radius: 30rpx;
	box-shadow: 0 20rpx 40rpx rgba(0, 0, 0, 0.1);
	border: 1rpx solid rgba(255, 255, 255, 0.2);
	overflow: hidden;
}

.profile-header {
	display: flex;
	align-items: flex-start;
	padding: 40rpx 30rpx 30rpx;
	gap: 20rpx;
}

.avatar-section {
	position: relative;
}

.avatar-placeholder {
	width: 120rpx;
	height: 120rpx;
	border-radius: 50%;
	background: linear-gradient(135deg, #667eea, #764ba2);
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0 8rpx 25rpx rgba(102, 126, 234, 0.3);
}

.avatar-icon {
	font-size: 60rpx;
	color: white;
}

.user-avatar {
	width: 120rpx;
	height: 120rpx;
	border-radius: 50%;
	border: 4rpx solid rgba(255, 255, 255, 0.8);
	box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.15);
}

.level-badge {
	position: absolute;
	bottom: -5rpx;
	right: -5rpx;
	background: linear-gradient(135deg, #ffd700, #ffed4e);
	border-radius: 20rpx;
	padding: 6rpx 12rpx;
	box-shadow: 0 4rpx 15rpx rgba(255, 215, 0, 0.4);
}

.level-text {
	font-size: 20rpx;
	color: white;
	font-weight: bold;
}

.profile-info {
	flex: 1;
}

.login-section {
	.login-title {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		display: block;
		margin-bottom: 8rpx;
	}
	
	.login-subtitle {
		font-size: 26rpx;
		color: #666;
		display: block;
	}
}

.user-section {
	.user-name {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		display: block;
		margin-bottom: 8rpx;
	}
	
	.user-id {
		font-size: 24rpx;
		color: #999;
		display: block;
		margin-bottom: 12rpx;
	}
	
	.user-tags {
		display: flex;
		gap: 12rpx;
	}
	
	.tag {
		padding: 6rpx 12rpx;
		border-radius: 12rpx;
		font-size: 20rpx;
		font-weight: 500;
	}
	
	.premium-tag {
		background: linear-gradient(135deg, #ffd700, #ffed4e);
		color: white;
	}
	
	.active-tag {
		background: linear-gradient(135deg, #74b9ff, #096dd9);
		color: white;
	}
}

.profile-actions {
	display: flex;
	flex-direction: column;
	gap: 15rpx;
}

.action-btn {
	width: 60rpx;
	height: 60rpx;
	border-radius: 50%;
	background: rgba(255, 255, 255, 0.9);
	display: flex;
	align-items: center;
	justify-content: center;
	box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.1);
	position: relative;
	transition: all 0.3s ease;
}

.action-btn:active {
	transform: scale(0.95);
}

.action-icon {
	font-size: 28rpx;
}

.message-badge {
	position: absolute;
	top: -5rpx;
	right: -5rpx;
	background: #ff6b6b;
	border-radius: 50%;
	width: 24rpx;
	height: 24rpx;
	display: flex;
	align-items: center;
	justify-content: center;
}

.badge-text {
	font-size: 16rpx;
	color: white;
	font-weight: bold;
}

/* --- 统计数据 --- */
.stats-section {
	display: flex;
	justify-content: space-around;
	padding: 30rpx 0;
	border-top: 1rpx solid rgba(0, 0, 0, 0.05);
}

.stat-item {
	display: flex;
	flex-direction: column;
	align-items: center;
}

.stat-number {
	font-size: 36rpx;
	font-weight: bold;
	color: #667eea;
	margin-bottom: 8rpx;
}

.stat-label {
	font-size: 24rpx;
	color: #666;
}

/* --- 通用模块样式 --- */
.section-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx 30rpx 20rpx;
}

.section-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.section-more {
	font-size: 28rpx;
	color: #667eea;
}

/* --- 成就系统 --- */
.achievements-section {
	padding: 0 30rpx;
}

.achievements-scroll {
	white-space: nowrap;
	padding-bottom: 20rpx;
}

.achievement-item {
	display: inline-flex;
	flex-direction: column;
	align-items: center;
	margin-right: 30rpx;
	width: 120rpx;
}

.achievement-icon {
	width: 80rpx;
	height: 80rpx;
	border-radius: 50%;
	background: #f8f9fa;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 12rpx;
	transition: all 0.3s ease;
}

.achievement-icon.achieved {
	background: linear-gradient(135deg, #ffd700, #ffed4e);
	box-shadow: 0 4rpx 15rpx rgba(255, 215, 0, 0.4);
}

.icon-text {
	font-size: 40rpx;
}

.achievement-name {
	font-size: 24rpx;
	color: #666;
	text-align: center;
}

/* --- 快捷服务 --- */
.quick-services-section {
	padding: 0 30rpx;
}

.services-grid {
	display: grid;
	grid-template-columns: repeat(4, 1fr);
	gap: 20rpx;
	padding-bottom: 20rpx;
}

.service-card {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 30rpx 20rpx;
	background: rgba(255, 255, 255, 0.95);
	border-radius: 20rpx;
	box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.05);
	transition: all 0.3s ease;
}

.service-card:active {
	transform: translateY(-5rpx);
	box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.1);
}

.service-icon-wrapper {
	width: 60rpx;
	height: 60rpx;
	border-radius: 50%;
	background: linear-gradient(135deg, #74b9ff, #096dd9);
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 16rpx;
}

.service-icon {
	font-size: 32rpx;
	color: white;
}

.service-title {
	font-size: 24rpx;
	color: #333;
	margin-bottom: 8rpx;
	text-align: center;
}

.service-count {
	font-size: 20rpx;
	color: #667eea;
	font-weight: bold;
}

/* --- 功能菜单 --- */
.menu-section {
	padding: 0 30rpx;
}

.menu-list {
	display: flex;
	flex-direction: column;
	gap: 30rpx;
}

.menu-group {
	background: rgba(255, 255, 255, 0.95);
	border-radius: 20rpx;
	box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.05);
	overflow: hidden;
}

.group-title {
	font-size: 28rpx;
	font-weight: bold;
	color: #333;
	padding: 30rpx 30rpx 20rpx;
	display: block;
}

.menu-item {
	display: flex;
	align-items: center;
	padding: 25rpx 30rpx;
	transition: all 0.3s ease;
	border-bottom: 1rpx solid rgba(0, 0, 0, 0.05);
}

.menu-item:last-child {
	border-bottom: none;
}

.menu-item:active {
	background: #f8f9fa;
}

.menu-icon-wrapper {
	width: 50rpx;
	height: 50rpx;
	border-radius: 12rpx;
	background: linear-gradient(135deg, #74b9ff, #096dd9);
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 20rpx;
}

.menu-icon {
	font-size: 28rpx;
	color: white;
}

.menu-title {
	font-size: 30rpx;
	color: #333;
	flex: 1;
}

.menu-subtitle {
	font-size: 24rpx;
	color: #666;
	flex: 1;
	margin-left: 20rpx;
}

.menu-arrow {
	font-size: 32rpx;
	color: #adb5bd;
}

/* --- VIP推广 --- */
.vip-promo-section {
	padding: 0 30rpx 30rpx;
}

.vip-card {
	background: linear-gradient(135deg, #ffd700, #ffed4e);
	border-radius: 20rpx;
	padding: 30rpx;
	display: flex;
	align-items: center;
	box-shadow: 0 8rpx 25rpx rgba(255, 215, 0, 0.3);
}

.vip-content {
	flex: 1;
}

.vip-title {
	display: flex;
	align-items: center;
	gap: 12rpx;
	margin-bottom: 12rpx;
}

.vip-icon {
	font-size: 32rpx;
}

.vip-text {
	font-size: 32rpx;
	font-weight: bold;
	color: #333;
}

.vip-description {
	font-size: 26rpx;
	color: #666;
	margin-bottom: 16rpx;
}

.vip-benefits {
	display: flex;
	gap: 20rpx;
}

.benefit-item {
	font-size: 24rpx;
	color: #333;
}

.vip-action {
	.vip-btn {
		background: rgba(255, 255, 255, 0.9);
		border: none;
		border-radius: 30rpx;
		padding: 15rpx 30rpx;
		font-size: 28rpx;
		font-weight: bold;
		color: #333;
		box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.1);
	}
}

/* --- 底部信息 --- */
.footer-section {
	text-align: center;
	padding: 40rpx 30rpx;
}

.footer-text {
	font-size: 28rpx;
	color: #667eea;
	display: block;
	margin-bottom: 8rpx;
}

.version-text {
	font-size: 24rpx;
	color: #999;
}
</style>
