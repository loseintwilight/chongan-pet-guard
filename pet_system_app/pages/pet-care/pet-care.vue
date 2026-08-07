<template>
	<view class="pet-care-container">
		<!-- 使用 mp-html 组件来渲染转换后的 HTML -->
		<mp-html :content="markdownContent" />
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
# 🐾 欢迎来到爱宠的第二个家

> 让每一次托养，都成为一次安心的旅行

---

## ✨ 我们的服务承诺

我们提供的不只是托养，而是充满爱与专业的家庭式照护。您的爱宠将在这里享受到：

- **🏡 家庭式环境**：告别笼养，在宽敞温馨的家中自由活动。
- **📹 24小时监控**：随时随地通过手机查看爱宠的实时动态。
- **💊 专业护理**：具备宠物护理知识，应对突发状况，按时喂药。
- **📝 每日报告**：每日发送照片/视频，记录爱宠的开心瞬间。

---

## 🛡️ 透明安心的托养流程

我们采用标准化的四步流程，确保您的爱宠安全、健康地度过每一天。

### Step 1: 接待与登记
<img src="/static/images/entrust01.png" alt="热情接待，建立信任">
*我们首先会与您和爱宠进行亲切互动，消除它们的紧张感，并详细记录宠物的习性、喜好和特殊需求。*

### Step 2: 体检与评估
<img src="/static/images/entrust02.png" alt="专业体检，健康评估">
*由专业人员进行基础健康检查，评估精神状态，确保它以最佳状态开始托养生活。*

### Step 3: 日常活动与照料
<img src="/static/images/entrust03.png" alt="悉心照料，快乐玩耍">
*根据宠物特性定制活动计划，包括定时喂食、互动游戏、户外散步，并每日清洁，保证环境卫生。*

### Step 4: 愉快返程
<img src="/static/images/entrust04.png" alt="干净整洁，愉快回家">
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
			markdownContent: '' // 用于存放转换后的 HTML
		};
	},
	onLoad() {
		// 2. 在页面加载时，将 Markdown 转换为 HTML
		this.markdownContent = marked(this.mdText);
	}
};
</script>

<style>
/* 全局容器：科技感背景 */
.pet-care-container {
	padding: 30rpx;
	line-height: 1.8;
	/* 渐变背景，营造科技感 */
	background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
	min-height: 100vh; /* 确保背景铺满全屏 */
}

/* --- mp-html 内部元素样式美化 --- */

/* 标题样式：温暖且突出 */
.mp-html h1 {
	font-size: 52rpx;
	color: #2c3e50; /* 深灰色，沉稳专业 */
	text-align: center;
	margin: 20rpx 0 40rpx;
	font-weight: bold;
	text-shadow: 2rpx 2rpx 4rpx rgba(0, 0, 0, 0.1);
}

/* 二级标题：作为区块标题 */
.mp-html h2 {
	font-size: 38rpx;
	color: #3498db; /* 科技蓝 */
	border-left: 8rpx solid #3498db;
	padding-left: 20rpx;
	margin-top: 60rpx;
	margin-bottom: 30rpx;
}

/* 三级标题：流程步骤 */
.mp-html h3 {
	font-size: 32rpx;
	color: #e67e22; /* 温暖橙色 */
	margin-top: 40rpx;
	margin-bottom: 20rpx;
}

/* 引用块：突出核心承诺 */
.mp-html blockquote {
	border-left: 5rpx solid #2ecc71; /* 活力绿 */
	padding: 20rpx 30rpx;
	margin: 30rpx 0;
	background-color: rgba(255, 255, 255, 0.8);
	color: #555;
	font-style: italic;
	border-radius: 0 16rpx 16rpx 0;
	box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.05);
}

/* 分割线 */
.mp-html hr {
	border: none;
	border-top: 2rpx dashed #bdc3c7;
	margin: 50rpx 0;
}

/* 列表样式：增加亲和力 */
.mp-html ul {
	list-style-type: none; /* 去掉默认圆点 */
	padding-left: 10rpx;
}

.mp-html li {
	background-color: rgba(255, 255, 255, 0.7);
	margin-bottom: 20rpx;
	padding: 20rpx 25rpx;
	border-radius: 12rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
	transition: transform 0.2s ease-in-out;
}

/* 图片样式：卡片式设计，增强视觉冲击力 */
.mp-html img {
	width: 100%;
	height: auto;
	border-radius: 20rpx; /* 更圆润的边角 */
	margin: 30rpx 0;
	box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.15); /* 更强的阴影，更有层次 */
	/* 增加一个淡入的动画效果 */
	opacity: 0;
	animation: fadeIn 1s ease-in-out forwards;
}

/* 图片下方的说明文字 */
.mp-html p:last-child {
	font-size: 26rpx;
	color: #7f8c8d;
	text-align: center;
	margin-top: -20rpx;
	margin-bottom: 40rpx;
}

/* 表格样式：清晰、现代 */
.mp-html table {
	width: 100%;
	border-collapse: collapse;
	margin: 40rpx 0;
	font-size: 28rpx;
	border-radius: 16rpx;
	overflow: hidden; /* 让圆角生效 */
	box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.08);
}

.mp-html th,
.mp-html td {
	padding: 25rpx 20rpx;
	text-align: center;
	border: none; /* 去掉默认边框 */
}

.mp-html th {
	background-color: #3498db; /* 表头使用科技蓝 */
	color: #ffffff;
	font-weight: bold;
}

.mp-html tr:nth-child(even) {
	background-color: rgba(236, 240, 241, 0.5); /* 斑马纹，提升可读性 */
}

.mp-html tr:nth-child(odd) {
	background-color: #ffffff;
}

/* 段落样式 */
.mp-html p {
	margin-bottom: 25rpx;
	color: #34495e;
}

/* 定义淡入动画 */
@keyframes fadeIn {
	from {
		opacity: 0;
		transform: translateY(20rpx);
	}
	to {
		opacity: 1;
		transform: translateY(0);
	}
}
</style>
