<template>
	<view class="donation-page">
		<!-- 捐助信息卡片 -->
		<view class="donation-card">
			<view class="card-header">
				<text class="card-title">为流浪动物献爱心</text>
				<text class="card-subtitle">您的每一份捐助，都是它们的一份希望</text>
			</view>
			<view class="card-content">
				<image class="card-image" src="/static/work/宠物捐助.png" mode="aspectFill" />
				<view class="card-description">
					<text class="description-text">帮助流浪动物找到温暖的家，让它们不再流浪。</text>
				</view>
			</view>
		</view>

		<!-- 金额输入区域 -->
		<view class="amount-section">
			<text class="amount-label">捐助金额</text>
			<view class="amount-input">
				<text class="currency">¥</text>
				<uni-number-box v-model="donationAmount" :min="1" :max="10000" :step="1" :disabled-input="false" />
			</view>
			<view class="amount-tips">
				<text class="tips-text">建议捐助金额：10元 - 1000元</text>
			</view>
		</view>

		<!-- 快速选择金额 -->
		<view class="quick-amount-section">
			<text class="quick-amount-label">快速选择</text>
			<view class="quick-amount-buttons">
				<view class="quick-amount-button" v-for="amount in quickAmounts" :key="amount" @click="setQuickAmount(amount)">
					<text class="button-text">¥{{ amount }}</text>
				</view>
			</view>
		</view>

		<!-- 支付按钮 -->
		<view class="payment-section">
			<button class="payment-button" @click="handlePayment">
				<text class="button-text">立即捐助</text>
			</button>
		</view>

		<!-- 模拟支付成功提示 -->
		<uni-popup ref="paymentSuccessPopup" type="dialog">
			<uni-popup-dialog
				type="success"
				title="捐助成功"
				content="感谢您的爱心捐助，您的帮助让流浪动物有了更多的希望！"
				:duration="2000"
				:before-close="false"
				@confirm="navigateToHome" />
		</uni-popup>
	</view>
</template>

<script>
import { addDonation } from '@/api/pet/donation.js';
export default {
	data() {
		return {
			donationAmount: 10, // 默认捐助金额
			quickAmounts: [10, 50, 100, 500] // 快速选择金额
		};
	},
	methods: {
		navigateBack() {
			uni.navigateBack();
		},
		setQuickAmount(amount) {
			this.donationAmount = amount;
		},
		handlePayment() {
			// 模拟支付过程
			uni.showLoading({
				title: '正在处理...',
				mask: true
			});
			//后台支付
			const form = {
				amount: this.donationAmount
			};
			addDonation(form).then((response) => {
				if (response.code == 200) {
					// 模拟2秒的支付时间
					setTimeout(() => {
						uni.hideLoading();
						this.$refs.paymentSuccessPopup.open();
					}, 2000);
				}
			});
		},
		navigateToHome() {
			uni.navigateBack();
		}
	}
};
</script>

<style lang="scss" scoped>
.donation-page {
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

/* 捐助信息卡片 */
.donation-card {
	background-color: #ffffff;
	border-radius: 20rpx;
	box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.06);
	padding: 30rpx;
	margin-top: 20rpx;
}

.card-header {
	margin-bottom: 20rpx;
}

.card-title {
	font-size: 36rpx;
	font-weight: bold;
	color: #2c3e50;
	margin-bottom: 10rpx;
}

.card-subtitle {
	font-size: 28rpx;
	color: #7f8c8d;
}

.card-content {
	display: flex;
	flex-direction: column;
	align-items: center;
}

.card-image {
	width: 100%;
	height: 300rpx;
	border-radius: 16rpx;
	margin-bottom: 20rpx;
}

.card-description {
	font-size: 28rpx;
	color: #666;
	text-align: center;
	line-height: 1.6;
}

/* 金额输入区域 */
.amount-section {
	margin-top: 30rpx;
}

.amount-label {
	font-size: 32rpx;
	font-weight: bold;
	color: #2c3e50;
	margin-bottom: 20rpx;
	display: block;
}

.amount-input {
	display: flex;
	align-items: center;
	justify-content: center;
	margin-bottom: 20rpx;
}

.currency {
	font-size: 36rpx;
	font-weight: bold;
	color: #2c3e50;
	margin-right: 10rpx;
}

/* 快速选择金额 */
.quick-amount-section {
	margin-top: 30rpx;
}

.quick-amount-label {
	font-size: 28rpx;
	color: #7f8c8d;
	margin-bottom: 20rpx;
	display: block;
}

.quick-amount-buttons {
	display: flex;
	justify-content: center;
	gap: 20rpx;
}

.quick-amount-button {
	background-color: #f0f0f0;
	border-radius: 16rpx;
	padding: 15rpx 30rpx;
	cursor: pointer;
	transition: all 0.3s ease;
}

.quick-amount-button:active {
	transform: scale(0.95);
}

.button-text {
	font-size: 28rpx;
	color: #2c3e50;
}

/* 支付按钮 */
.payment-section {
	margin-top: 40rpx;
	display: flex;
	justify-content: center;
}

.payment-button {
	width: 100%;
	background: linear-gradient(90deg, #ff9a9e 0%, #fad0c4 100%);
	border-radius: 30rpx;
	padding: 25rpx 0;
	font-size: 32rpx;
	font-weight: bold;
	color: #ffffff;
	box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.1);
	transition: all 0.3s ease;
}

.payment-button:active {
	transform: scale(0.98);
	box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.08);
}
</style>
