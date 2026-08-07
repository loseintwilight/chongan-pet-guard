<template>
	<view class="rescue-container">
		<mp-html :content="markdownContent" />
	</view>
</template>

<script>
import { marked } from 'marked';

export default {
	data() {
		return {
			// 1. 充满情感和号召力的 Markdown 文本
			mdText: `
# 🐾 每一个生命都值得被温柔以待

> 在城市的一角，有许多小生命正在等待一双温暖的手，一个安全的家。

---

## 🆘 它们的故事，需要您的倾听

它们曾是主人怀中的宝贝，却因各种原因流落街头。它们是：

- **🐕 遗弃的毛孩**：因搬家、怀孕等原因被无情抛弃。
- **🐈 受伤的流浪者**：在街头遭受意外，急需医疗救助。
- **🐶 新生的孤儿**：还未睁眼，母亲就已不知所踪。

<img src="/static/images/rescue-story.jpg" alt="一只等待救助的小猫，眼神充满渴望">

---

## 🤝 您的援手，是它们唯一的希望

救助之路漫长而艰辛，但我们相信，汇集每一份微小的力量，就能点亮它们的生命之光。您可以通过以下方式加入我们：

### 1. 线上捐赠：爱心直达

您的每一笔捐款都将用于：
- **🏥 医疗救治**：疫苗、驱虫、绝育、伤病治疗。
- **🍖 基础口粮**：保证救助站毛孩们的温饱。
- **🏡 场地维护**：为它们提供一个干净、安全的庇护所。

<img src="/static/images/rescue-donate.jpg" alt="志愿者正在给狗狗喂食">

### 2. 成为志愿者：亲身参与

如果您有时间，我们欢迎您成为志愿者：
- **🧹 环境清洁**：帮助打扫救助站，保持卫生。
- **🚶 遛狗互动**：陪伴它们玩耍，进行社会化训练。
- **📸 拍摄记录**：为它们拍摄可爱的照片/视频，帮助寻找领养家庭。

### 3. 提供领养：给它们一个家

**领养代替购买**，是给流浪动物最好的礼物。您可以访问我们的 [**领养中心页面**](/pages/adoption/index)，了解等待领养的毛孩。

---

## 📋 我们的救助流程

我们承诺每一份善款都用得透明、高效。

1.  **发现与评估**：接到求助信息后，第一时间评估情况。

**如果您发现了需要救助的小动物，请立即联系我们** 

[**点击这里，立即登记**](/pages/rescue/rescue)

2.  **紧急救援**：将受困动物转移至安全地带或合作医院。
3.  **医疗与康复**：进行全面体检和治疗，直至恢复健康。
4.  **寻找领养**：通过线上平台和线下活动，为它们寻找永久的家。

---

## 📊 我们的成果 (截至本月)

| 项目 | 数量 | 说明 |
|---------|---------|---------|
| 🐾 成功救助 | 256只 | 包括猫、狗及其他小动物 |
| 🏡 成功领养 | 189只 | 找到了充满爱的新家庭 |
| 💊 完成绝育 | 230例 | 从源头控制流浪动物数量 |

---

## 📞 联系我们或立即捐赠

如果您发现需要救助的动物，或想为它们贡献一份力量，请随时联系我们！

**🚨 救助热线**：400-123-4567 (24小时)
**💬 微信客服**：RescueHelper2023

[**点击这里，立即捐赠**](/pages/donation/index)
			`,
			markdownContent: ''
		};
	},
	onLoad() {
		this.markdownContent = marked(this.mdText);
	}
};
</script>

<style>
/* 救助页面专属容器：温暖、有希望的色调 */
.rescue-container {
	padding: 30rpx;
	line-height: 1.8;
	/* 温暖的渐变背景，从橙色到粉色，营造爱心氛围 */
	background: linear-gradient(135deg, #ffeaa7 0%, #fdcb6e 100%);
	min-height: 100vh;
}

/* --- mp-html 内部元素样式 --- */

/* 标题：突出主题，充满力量 */
.rescue-container .mp-html h1 {
	font-size: 52rpx;
	color: #2d3436; /* 深炭灰色，庄重 */
	text-align: center;
	margin: 20rpx 0 40rpx;
	font-weight: bold;
	text-shadow: 2rpx 2rpx 4rpx rgba(0, 0, 0, 0.1);
}

/* 引用块：核心情感诉求 */
.rescue-container .mp-html blockquote {
	border-left: 5rpx solid #e17055; /* 热情珊瑚色 */
	padding: 20rpx 30rpx;
	margin: 30rpx 0;
	background-color: rgba(255, 255, 255, 0.8);
	color: #555;
	font-style: italic;
	border-radius: 0 16rpx 16rpx 0;
	box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.05);
}

/* 二级标题：作为模块标题 */
.rescue-container .mp-html h2 {
	font-size: 38rpx;
	color: #e17055; /* 与引用块呼应的暖色调 */
	border-left: 8rpx solid #e17055;
	padding-left: 20rpx;
	margin-top: 60rpx;
	margin-bottom: 30rpx;
}

/* 三级标题：具体行动项 */
.rescue-container .mp-html h3 {
	font-size: 32rpx;
	color: #00b894; /* 代表希望和行动的绿色 */
	margin-top: 40rpx;
	margin-bottom: 20rpx;
}

/* 列表：将“它们的故事”卡片化 */
.rescue-container .mp-html ul {
	list-style-type: none;
	padding-left: 10rpx;
}

.rescue-container .mp-html li {
	background-color: rgba(255, 255, 255, 0.7);
	margin-bottom: 20rpx;
	padding: 20rpx 25rpx;
	border-radius: 12rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

/* 图片：故事性和情感冲击力 */
.rescue-container .mp-html img {
	width: 100%;
	height: auto;
	border-radius: 20rpx;
	margin: 30rpx 0;
	box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.15);
	/* 添加淡入动画 */
	opacity: 0;
	animation: fadeIn 1s ease-in-out forwards;
}

/* 表格：展示成果，建立信任 */
.rescue-container .mp-html table {
	width: 100%;
	border-collapse: collapse;
	margin: 40rpx 0;
	font-size: 28rpx;
	border-radius: 16rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.08);
}

.rescue-container .mp-html th,
.rescue-container .mp-html td {
	padding: 25rpx 20rpx;
	text-align: center;
	border: none;
}

.rescue-container .mp-html th {
	background-color: #00b894; /* 表头用希望绿 */
	color: #ffffff;
	font-weight: bold;
}

.rescue-container .mp-html tr:nth-child(even) {
	background-color: rgba(253, 203, 110, 0.3); /* 斑马纹与背景色呼应 */
}

.rescue-container .mp-html tr:nth-child(odd) {
	background-color: #ffffff;
}

/* 段落 */
.rescue-container .mp-html p {
	margin-bottom: 25rpx;
	color: #2d3436;
}

/* 链接样式：突出行动按钮 */
.rescue-container .mp-html a {
	display: inline-block;
	background-color: #e17055;
	color: #ffffff !important;
	padding: 20rpx 40rpx;
	border-radius: 50rpx;
	text-decoration: none;
	font-weight: bold;
	margin-top: 20rpx;
	box-shadow: 0 4rpx 15rpx rgba(225, 112, 85, 0.4);
	transition: transform 0.2s ease, box-shadow 0.2s ease;
}

/* 链接点击效果 */
.rescue-container .mp-html a:active {
	transform: scale(0.98);
	box-shadow: 0 2rpx 8rpx rgba(225, 112, 85, 0.4);
}

/* 淡入动画 */
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
