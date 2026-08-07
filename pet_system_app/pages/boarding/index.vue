<template>
	<view class="pet-care-container">
		<!-- 服务介绍卡片 -->
		<view class="intro-card">
			<image class="intro-image" src="/static/托养流程图片.png" mode="aspectFill" />
			<view class="intro-content">
				<text class="intro-title">家庭式宠物托养服务</text>
				<text class="intro-subtitle">告别冰冷的笼子，给爱宠一个温暖的家</text>
			</view>
		</view>

		<!-- 托养流程 (使用 mp-html 渲染 Markdown) -->
		<view class="process-section">
			<view class="section-header">
				<text class="section-title">透明安心的托养流程</text>
			</view>
			<view class="process-content">
				<mp-html :content="markdownContent" />
			</view>
		</view>

		<!-- 核心优势 -->
		<view class="advantages-section">
			<view class="section-header">
				<text class="section-title">我们的核心优势</text>
			</view>
			<view class="advantages-grid">
				<view class="advantage-item" v-for="(item, index) in advantages" :key="index">
					<view class="advantage-icon">
						<uni-icons :type="item.icon" size="40" :color="item.color"></uni-icons>
					</view>
					<text class="advantage-title">{{ item.title }}</text>
					<text class="advantage-desc">{{ item.desc }}</text>
				</view>
			</view>
		</view>

		<!-- 底部咨询栏 -->
		<view class="bottom-bar">
			<button class="consult-button" @click="toRegistBorading">
				<uni-icons type="headphones" size="20" color="#fff"></uni-icons>
				<text class="button-text">立即登记托养</text>
			</button>
		</view>
	</view>
</template>

<script>
// 引入 marked 库
import { marked } from 'marked';

export default {
	data() {
		return {
			// 1. 美化后的 Markdown 文本，增加了情感化文案和表情符号
			mdText: `
# 🐾 透明安心的托养流程

> 让每一次托养，都成为一次安心的旅行

---

## ✨ 我们的服务承诺

我们提供的不只是托养，而是充满爱与专业的家庭式照护。您的爱宠将在这里享受到：

- **🏡 家庭式环境**：告别笼养，在宽敞温馨的家中自由活动。
- **📹 24小时监控**：随时随地通过手机查看爱宠的实时动态。
- **💊 专业护理**：具备宠物护理知识，应对突发状况，按时喂药。
- **📝 每日报告**：每日发送照片/视频，记录爱宠的开心瞬间。

---

## 🛡️ 标准化四步流程

我们采用标准化的四步流程，确保您的爱宠安全、健康地度过每一天。

### Step 1: 接待与登记
*我们首先会与您和爱宠进行亲切互动，消除它们的紧张感，并详细记录宠物的习性、喜好和特殊需求。*

### Step 2: 体检与评估
*由专业人员进行基础健康检查，评估精神状态，确保它以最佳状态开始托养生活。*

### Step 3: 日常活动与照料
*根据宠物特性定制活动计划，包括定时喂食、互动游戏、户外散步，并每日清洁，保证环境卫生。*

### Step 4: 愉快返程
*在您来接爱宠前，我们会为它进行一次基础美容清洁，让它漂漂亮亮、开开心心地回到您的怀抱。*

---

## 💖 我们的核心服务

1.  **科学喂养**：严格遵循您的喂养习惯，提供高品质宠粮。
2.  **陪伴互动**：充足的玩耍时间，避免爱宠因分离而焦虑。
3.  **健康监护**：密切观察精神、食欲、排泄状况，及时沟通。
4.  **实时反馈**：通过微信群或小程序，每日推送“萌宠日报”。

---

## 📊 明晰的收费标准

| 宠物类型 | 体验价(天) | 包含服务 | 额外服务 |
|---------|---------|---------|---------|
| 🐕 小型犬  | ¥88     | 基础护理、每日报告 | 美容、训练 |
| 🐕 中型犬  | ¥128    | 基础护理、每日报告 | 美容、训练 |
| 🐈 猫咪    | ¥68     | 基础护理、每日报告 | 指甲修剪、梳毛 |

---

## 📞 立即预约

准备好给您的爱宠一个完美的假期了吗？立即联系我们，为它预定一个温暖的家！
			`,
			markdownContent: '', // 用于存放转换后的 HTML
			advantages: [
				{
					icon: 'home',
					color: '#3498db',
					title: '家庭环境',
					desc: '无笼散养，温馨舒适'
				},
				{
					icon: 'videocam',
					color: '#2ecc71',
					title: '实时监控',
					desc: '24小时在线查看'
				},
				{
					icon: 'heart',
					color: '#e74c3c',
					title: '科学喂养',
					desc: '定制化营养方案'
				},
				{
					icon: 'staff',
					color: '#f39c12',
					title: '专业团队',
					desc: '持证上岗经验丰富'
				}
			]
		};
	},
	onLoad() {
		// 2. 在页面加载时，将 Markdown 转换为 HTML
		this.markdownContent = marked(this.mdText);
	},
	methods: {
		navigateBack() {
			uni.navigateBack();
		},
		toRegistBorading() {
			uni.navigateTo({
				url: '/pages/boarding/select_stop'
			});
		}
	}
};
</script>

<style lang="scss" scoped>
.pet-care-container {
	min-height: 100vh;
	background-color: #f8f9fa;
	padding-bottom: 120rpx; /* 为底部固定栏留出空间 */
}

/* 顶部标题栏 */
.page-header {
	display: flex;
	align-items: center;
	padding: 20rpx 30rpx;
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

/* 服务介绍卡片 */
.intro-card {
	position: relative;
	margin: 20rpx 30rpx;
	border-radius: 20rpx;
	overflow: hidden;
	box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.06);
}

.intro-image {
	width: 100%;
	height: 400rpx;
	display: block;
}

.intro-content {
	position: absolute;
	bottom: 0;
	left: 0;
	right: 0;
	padding: 40rpx 30rpx;
	background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
	color: #ffffff;
}

.intro-title {
	display: block;
	font-size: 40rpx;
	font-weight: bold;
	margin-bottom: 10rpx;
}

.intro-subtitle {
	display: block;
	font-size: 28rpx;
	opacity: 0.9;
}

/* 流程和优势区块 */
.process-section,
.advantages-section {
	margin: 20rpx 30rpx;
	background-color: #ffffff;
	border-radius: 20rpx;
	box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.06);
	overflow: hidden;
}

.section-header {
	padding: 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.section-title {
	font-size: 36rpx;
	font-weight: bold;
	color: #2c3e50;
}

.process-content {
	padding: 30rpx;
}

/* mp-html 样式 */
.mp-html {
	line-height: 1.8;
	color: #34495e;

	h1 {
		font-size: 40rpx;
		color: #2c3e50;
		text-align: center;
		margin: 20rpx 0 40rpx;
		font-weight: bold;
	}

	h2 {
		font-size: 34rpx;
		color: #3498db;
		border-left: 6rpx solid #3498db;
		padding-left: 20rpx;
		margin-top: 50rpx;
		margin-bottom: 30rpx;
	}

	h3 {
		font-size: 30rpx;
		color: #e67e22;
		margin-top: 40rpx;
		margin-bottom: 20rpx;
	}

	blockquote {
		border-left: 5rpx solid #2ecc71;
		padding: 20rpx 30rpx;
		margin: 30rpx 0;
		background-color: rgba(46, 204, 113, 0.1);
		color: #555;
		font-style: italic;
		border-radius: 0 16rpx 16rpx 0;
	}

	hr {
		border: none;
		border-top: 2rpx dashed #bdc3c7;
		margin: 50rpx 0;
	}

	ul {
		list-style-type: none;
		padding-left: 10rpx;
	}

	li {
		background-color: #f8f9fa;
		margin-bottom: 15rpx;
		padding: 20rpx 25rpx;
		border-radius: 12rpx;
		box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
	}

	table {
		width: 100%;
		border-collapse: collapse;
		margin: 40rpx 0;
		font-size: 26rpx;
		border-radius: 16rpx;
		overflow: hidden;
		box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.08);
	}

	th,
	td {
		padding: 20rpx;
		text-align: center;
		border: none;
	}

	th {
		background-color: #3498db;
		color: #ffffff;
		font-weight: bold;
	}

	tr:nth-child(even) {
		background-color: #f8f9fa;
	}

	tr:nth-child(odd) {
		background-color: #ffffff;
	}

	p {
		margin-bottom: 25rpx;
	}
}

/* 核心优势 */
.advantages-grid {
	display: grid;
	grid-template-columns: repeat(2, 1fr);
	gap: 20rpx;
	padding: 30rpx;
}

.advantage-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	text-align: center;
	padding: 30rpx 20rpx;
	background-color: #f8f9fa;
	border-radius: 16rpx;
	transition: transform 0.2s ease;
}

.advantage-item:active {
	transform: scale(0.98);
}

.advantage-icon {
	margin-bottom: 15rpx;
}

.advantage-title {
	font-size: 30rpx;
	font-weight: bold;
	color: #2c3e50;
	margin-bottom: 10rpx;
}

.advantage-desc {
	font-size: 24rpx;
	color: #7f8c8d;
}

/* 底部咨询栏 */
.bottom-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	padding: 20rpx 30rpx;
	background-color: #ffffff;
	box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.consult-button {
	width: 100%;
	background: linear-gradient(90deg, #3498db 0%, #2ecc71 100%);
	border-radius: 30rpx;
	padding: 25rpx 0;
	font-size: 32rpx;
	font-weight: bold;
	color: #ffffff;
	display: flex;
	justify-content: center;
	align-items: center;
	gap: 10rpx;
	box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.1);
	transition: all 0.3s ease;

	&::after {
		border: none;
	}
}

.consult-button:active {
	transform: scale(0.98);
	box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.08);
}
</style>
