<template>
	<view class="home-container">
		<!-- 顶部栏 -->
		<view class="top-bar">
			<view class="brand-section">
				<image class="brand-icon" src="/static/pet-logo.jpeg" mode="aspectFit" />
				<text class="brand-name">宠物伴侣</text>
			</view>
			<view class="shop-info">
				<text class="shop-name">幸福之家宠物乐园</text>
				<text class="shop-tag">官方认证</text>
			</view>
			<view class="notification-section" @click="toNotifications">
				<image src="/static/notification-bell.png" mode="aspectFit" style="width: 24px; height: 24px" />
				<view class="notification-badge" v-if="hasNewNotification">{{ unReadNumber }}</view>
			</view>
		</view>

		<!-- 顶部轮播图区域 -->
		<view class="carousel-section">
			<uni-swiper-dot class="uni-swiper-dot-box" :info="carouselList" :current="current" field="content"
				mode="dot" :dots-styles="{
					backgroundColor: 'rgba(255, 255, 255, 0.5)',
					selectedBackgroundColor: '#ffffff',
					border: '1px solid rgba(255, 255, 255, 0.8)'
				}">
				<swiper class="swiper-box" :current="swiperDotIndex" @change="changeSwiper" circular autoplay>
					<swiper-item v-for="(item, index) in carouselList" :key="index">
						<view class="swiper-item" @click="clickBannerItem(item)">
							<image :src="getImageUrl(item.images)" mode="aspectFill" :draggable="false" />
							<view class="swiper-overlay"></view>
							<view class="swiper-text-content">
								<text class="swiper-title">{{ item.title || '精彩活动' }}</text>
								<text class="swiper-subtitle">{{ item.subtitle || '点击了解更多详情' }}</text>
							</view>
						</view>
					</swiper-item>
				</swiper>
			</uni-swiper-dot>
		</view>

		<!-- 每日科普模块 -->
		<view class="daily-knowledge-section" @click="toKnowledgeList">
			<view class="knowledge-icon">
				<i class="fas fa-lightbulb" style="font-size: 60rpx; color: #ff9a9e"></i>
			</view>
			<view class="knowledge-content">
				<text class="knowledge-title">每日科普</text>
				<text class="knowledge-subtitle">宠物科普教程，快来看</text>
			</view>
			<view class="knowledge-action">
				<text class="action-text">查看全部</text>
				<i class="fas fa-chevron-right" style="font-size: 24rpx; color: #ff9a9e; margin-left: 10rpx"></i>
			</view>
		</view>

		<!-- 日常活动模块 -->
		<view class="daily-activity-section">
			<view class="section-header">
				<text class="section-title">日常活动</text>
				<text class="section-more" @click="toActivityList">查看全部</text>
			</view>
			<view v-if="activityList.length > 0">
				<view class="activity-card" @click="toActivityDetail(activityList[0])">
					<image :src="getImageUrl(activityList[0].image || activityList[0].images)" mode="aspectFill"
						:draggable="false" class="activity-image" />
					<view class="activity-info">
						<text class="info-title">{{ activityList[0].title || '暂无标题' }}</text>
						<text
							class="info-item">开始时间：{{ activityList[0].startTime || activityList[0].beginTime || '暂无开始时间' }}</text>
						<text
							class="info-item">结束时间：{{ activityList[0].endTime || activityList[0].finishTime || '暂无结束时间' }}</text>
					</view>
				</view>
			</view>
			<view v-else class="no-data">
				<text class="no-data-text">暂无活动数据</text>
			</view>
		</view>

		<!-- AI推荐宠物模块 -->
		<view class="ai-recommend-section" @click="toAiRecommend">
			<view class="ai-recommend-icon">
				<i class="fas fa-robot" style="font-size: 60rpx; color: #667eea"></i>
			</view>
			<view class="ai-recommend-content">
				<text class="ai-recommend-title">试试小缘AI推荐宠物</text>
				<text class="ai-recommend-subtitle">智能匹配，找到你的命中注定</text>
			</view>
			<view class="ai-recommend-action">
				<text class="action-text">立即体验</text>
				<i class="fas fa-chevron-right" style="font-size: 24rpx; color: #667eea; margin-left: 10rpx"></i>
			</view>
		</view>

		<!-- 精选毛孩子模块 -->
		<view class="featured-pets-section">
			<view class="section-header">
				<text class="section-title">精选毛孩子</text>
				<text class="section-more" @click="toPetList">查看全部</text>
			</view>

			<view class="pets-grid">
				<view v-for="pet in featuredPets" :key="pet.petId" class="pet-card" @click="toPetDetail(pet.petId)">
					<!-- 宠物图片 -->
					<image :src="getImageUrl(pet.images)" mode="aspectFill" class="pet-image" :draggable="false"
						@error="handleImageError" @load="handleImageLoad" />

					<!-- 宠物信息 -->
					<view class="pet-info">
						<!-- 宠物名称和基本信息 -->
						<view class="pet-basic-info">
							<text class="pet-name">{{ pet.name || '未命名' }}</text>
							<view class="pet-meta">
								<!-- 年龄 -->
								<text v-if="pet.age !== undefined && pet.age !== null"
									class="meta-item">{{ pet.age }}{{ getAgeUnit(pet.age) }}</text>

								<!-- 物种 -->
								<text v-if="pet.species !== undefined && pet.species !== null" class="meta-item">
									{{ getSpeciesText(pet.species) }}
								</text>

								<!-- 品种 -->
								<text v-if="pet.breed" class="meta-item">
									{{ pet.breed }}
								</text>
							</view>
						</view>

						<!-- 宠物描述 -->
						<text v-if="pet.description" class="pet-description">{{ pet.description }}</text>

						<!-- 健康状态 -->
						<view class="pet-health-status">
							<!-- 免疫状态 -->
							<view v-if="pet.immunity !== undefined && pet.immunity !== 2" class="health-item">
								<view class="health-icon">
									<image v-if="pet.immunity === 0" src="/static/icons/immune-yes.png"
										class="health-icon-img" />
									<image v-else src="/static/icons/immune-no.png" class="health-icon-img" />
								</view>
								<text class="health-text">免疫</text>
								<text class="health-status" :class="pet.immunity === 0 ? 'status-yes' : 'status-no'">
									{{ pet.immunity === 0 ? '已' : '未' }}
								</text>
							</view>

							<!-- 绝育状态 -->
							<view v-if="pet.neuter !== undefined && pet.neuter !== 2" class="health-item">
								<view class="health-icon">
									<image v-if="pet.neuter === 1" src="/static/icons/neuter-yes.png"
										class="health-icon-img" />
									<image v-else src="/static/icons/neuter-no.png" class="health-icon-img" />
								</view>
								<text class="health-text">绝育</text>
								<text class="health-status" :class="pet.neuter === 1 ? 'status-yes' : 'status-no'">
									{{ pet.neuter === 1 ? '已' : '未' }}
								</text>
							</view>

							<!-- 驱虫状态 -->
							<view v-if="pet.insectRepellent !== undefined && pet.insectRepellent !== 2"
								class="health-item">
								<view class="health-icon">
									<image v-if="pet.insectRepellent === 1" src="/static/icons/deworm-yes.png"
										class="health-icon-img" />
									<image v-else src="/static/icons/deworm-no.png" class="health-icon-img" />
								</view>
								<text class="health-text">驱虫</text>
								<text class="health-status"
									:class="pet.insectRepellent === 1 ? 'status-yes' : 'status-no'">
									{{ pet.insectRepellent === 1 ? '已' : '未' }}
								</text>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 服务承诺区域 -->
		<view class="service-promise-section">
			<view class="divider-with-text">
				<view class="divider-line"></view>
				<text class="divider-text">✨ 我们的服务承诺 ✨</text>
				<view class="divider-line"></view>
			</view>
		</view>

		<!-- 底部核心功能区 -->
		<view class="core-services-wrapper">
			<view class="service-card" @click="toPetCare">
				<view class="card-image-container">
					<image src="/static/托养流程图片.png" mode="aspectFill" :draggable="false" class="service-image" />
				</view>
				<view class="card-content">
					<text class="card-title">专业托养</text>
					<text class="card-subtitle">家庭式照护，安心之选</text>
				</view>
			</view>

			<view class="service-card primary-card">
				<button open-type="contact">
					<view class="card-image-container">
						<image src="https://q9.itc.cn/q_70/images01/20250911/b65644324d11493f8b83916f5b715c19.png"
							mode="aspectFill" :draggable="false" class="service-image" />
					</view>
					<view class="card-content">
						<text class="card-title">在线客服</text>
						<text class="card-subtitle">24小时为您守候</text>
					</view>
				</button>
			</view>

			<view class="service-card" @click="toPetRescue">
				<view class="card-image-container">
					<image src="/static/救助帮助.png" mode="aspectFill" :draggable="false" class="service-image" />
				</view>
				<view class="card-content">
					<text class="card-title">爱心救助</text>
					<text class="card-subtitle">给流浪一个温暖的家</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		listCarousel
	} from '@/api/pet/carousel.js';
	import {
		getUnReadNoticeNumber
	} from '@/api/pet/notice.js';
	import config from '@/config';
	import {
		listPet
	} from '@/api/pet/adoption.js';
	import {
		listActivity
	} from '@/api/pet/activity.js';

	export default {
		data() {
			return {
				current: 0,
				swiperDotIndex: 0,
				carouselList: [],
				activityList: [], // 活动列表数据
				queryParams: {
					status: 1
				},
				activityQuery: {
					pageNum: 1,
					pageSize: 1 // 只获取一个最新活动
				},
				hasNewNotification: false, // 模拟有新通知
				unReadNumber: 0,
				// 新增：精选宠物数据
				featuredPets: []
			};
		},
		onLoad() {
			this.getCarouselList();
			this.getNumberOfUnReadNotice();
			this.loadFeaturedPets(); // 加载精选宠物
			this.getActivityList();
		},
		onPullDownRefresh() {
			// 重置数据
			this.carouselList = [];
			this.unReadNumber = 0;
			this.hasNewNotification = false;

			// 重新加载数据
			this.getCarouselList();
			this.getNumberOfUnReadNotice();

			// 停止下拉刷新动画（通常在数据加载完成后调用）
			setTimeout(() => {
				uni.stopPullDownRefresh();
			}, 1000); // 模拟1秒的加载时间
		},
		methods: {
			// 获取活动列表
			getActivityList() {
				listActivity(this.activityQuery)
					.then((response) => {
						this.activityList = response.rows || [];
					})
					.catch((error) => {
						console.error('获取活动列表失败:', error);
						uni.showToast({
							title: '加载活动列表失败',
							icon: 'none'
						});
					});
			},
			async loadFeaturedPets() {
				try {
					const params = {
						pageNum: 1,
						pageSize: 4,
						status: 0
					};
					const res = await listPet(params);
					console.log('API返回数据:', res);

					if (res.code === 200) {
						// 确保数据格式正确
						this.featuredPets = res.rows.map((pet) => {
							console.log('单个宠物数据:', pet);
							return {
								petId: pet.petId || pet.id,
								name: pet.name || '未命名',
								images: pet.images || '',
								species: pet.species,
								breed: pet.breed || '',
								age: pet.age,
								gender: pet.gender,
								description: pet.description || '',
								immunity: pet.immunity,
								neuter: pet.neuter,
								insectRepellent: pet.insectRepellent
							};
						});
						console.log('处理后的宠物数据:', this.featuredPets);
					} else {
						console.error('加载精选宠物失败: 数据结构不正确', res);
						// 设置默认数据用于测试
						this.featuredPets = [{
							petId: 1,
							name: '测试宠物',
							images: '',
							species: 0,
							breed: '田园猫',
							age: 6,
							description: '这是一只可爱的猫咪',
							immunity: 0,
							neuter: 1,
							insectRepellent: 0
						}];
					}
				} catch (error) {
					console.error('加载精选宠物异常:', error);
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					});
				}
			},

			// 获取宠物描述信息
			getPetDesc(pet) {
				const parts = [];

				// 物种
				if (pet.species !== undefined && pet.species !== null) {
					const speciesText = this.getSpeciesText(pet.species);
					if (speciesText) parts.push(speciesText);
				}

				// 品种
				if (pet.breed) {
					parts.push(pet.breed);
				}

				// 年龄
				if (pet.age !== undefined && pet.age !== null) {
					parts.push(`${pet.age}${this.getAgeUnit(pet.age)}`);
				}

				// 性别
				if (pet.gender !== undefined && pet.gender !== null) {
					const genderText = this.getGenderText(pet.gender);
					if (genderText) parts.push(genderText);
				}

				return parts.length > 0 ? parts.join(' · ') : '待完善信息';
			},

			// 获取物种文本
			getSpeciesText(species) {
				const speciesMap = {
					0: '猫',
					1: '狗',
					2: '其他'
				};
				return speciesMap[species] || '';
			},

			// 获取性别文本
			getGenderText(gender) {
				const genderMap = {
					0: '母',
					1: '公',
					2: '未知'
				};
				return genderMap[gender] || '';
			},

			// 获取年龄单位
			getAgeUnit(age) {
				const ageNum = Number(age);
				return ageNum >= 12 ? '岁' : '个月';
			},
			getCarouselList() {
				listCarousel(this.queryParams)
					.then((response) => {
						this.carouselList = response.rows || [];
					})
					.catch((error) => {
						console.error('获取轮播图失败:', error);
						uni.showToast({
							title: '加载轮播图失败',
							icon: 'none'
						});
					});
			},
			getImageUrl(fileName) {
				return config.getDownloadUrl(fileName);
			},
			clickBannerItem(item) {
				if (item.linkUrl) {
					uni.navigateTo({
						url: item.linkUrl
					});
				} else {
					uni.showToast({
						title: '功能开发中',
						icon: 'none'
					});
				}
			},
			changeSwiper(e) {
				this.current = e.detail.current;
			},
			toPetCare() {
				uni.navigateTo({
					url: '/pages/pet-care/pet-care'
				});
			},
			toPetRescue() {
				uni.navigateTo({
					url: '/pages/pet-rescue/pet-rescue'
				});
			},
			toStoreHome() {
				uni.showToast({
					title: '进入店铺主页',
					icon: 'none'
				});
			},
			toNotifications() {
				uni.navigateTo({
					url: '/pages/notice/index'
				});
			},
			toKnowledgeList() {
				uni.navigateTo({
					url: '/pages/article/index'
				});
			},
			toActivityList() {
				uni.navigateTo({
					url: '/pages/activity/index'
				});
			},
			toPetList() {
				uni.navigateTo({
					url: '/pages/adoption/index'
				});
			},
			toAiRecommend() {
				uni.navigateTo({
					url: '/pages/ai/ai'
				});
			},
			toPetDetail(petId) {
				uni.navigateTo({
					url: '/pages/adoption/detail?petId=' + petId
				});
			},
			//获取未读通知数量
			getNumberOfUnReadNotice() {
				getUnReadNoticeNumber().then((response) => {
					this.unReadNumber = response.data;
					if (this.unReadNumber == 0) {
						this.hasNewNotification = false;
					} else {
						this.hasNewNotification = true;
					}
				});
			},
			// 图片错误处理
			handleImageError(e) {
				console.error('图片加载失败:', e);
				e.currentTarget.src = '/static/images/default-pet.png';
				e.currentTarget.onerror = null;
			},

			// 图片加载成功
			handleImageLoad(e) {
				console.log('图片加载成功');
			},
			// 跳转到活动详情
			toActivityDetail(item) {
				uni.navigateTo({
					url: `/pages/activity/detail?id=${item.activityId}`
				});
			}
		}
	};
</script>

<style lang="scss" scoped>
	/* --- 全局与容器 --- */
	.home-container {
		display: flex;
		flex-direction: column;
		background-color: #f8f9fa;
		min-height: 100vh;
	}

	/* --- 顶部栏样式 --- */
	.top-bar {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 20rpx 30rpx;
		background: linear-gradient(90deg, #ff9a9e 0%, #fad0c4 100%);
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.1);
		position: relative;
		z-index: 100;
	}

	.brand-section {
		display: flex;
		align-items: center;
		gap: 12rpx;
	}

	.brand-icon {
		width: 50rpx;
		height: 50rpx;
		border-radius: 12rpx;
		background-color: rgba(255, 255, 255, 0.9);
	}

	.brand-name {
		font-size: 32rpx;
		font-weight: bold;
		color: #ffffff;
		text-shadow: 0 1rpx 2rpx rgba(0, 0, 0, 0.2);
	}

	.shop-info {
		flex: 1;
		text-align: center;
	}

	.shop-name {
		font-size: 28rpx;
		font-weight: 500;
		color: #ffffff;
		display: block;
	}

	.shop-tag {
		font-size: 22rpx;
		color: rgba(255, 255, 255, 0.9);
		display: block;
		margin-top: 4rpx;
	}

	.notification-section {
		position: relative;
	}

	.notification-badge {
		position: absolute;
		top: -8rpx;
		right: -8rpx;
		min-width: 32rpx;
		height: 32rpx;
		background-color: #ff4757;
		color: #ffffff;
		border-radius: 50%;
		font-size: 20rpx;
		display: flex;
		justify-content: center;
		align-items: center;
	}

	/* --- 轮播图区域 --- */
	.carousel-section {
		position: relative;
		margin-top: 20rpx;

		.swiper-box {
			height: 420rpx;
		}

		.swiper-item {
			position: relative;
			width: 100%;
			height: 100%;
			overflow: hidden;

			image {
				width: 100%;
				height: 100%;
			}

			.swiper-overlay {
				position: absolute;
				bottom: 0;
				left: 0;
				right: 0;
				height: 150rpx;
				background: linear-gradient(to top, rgba(0, 0, 0, 0.6), transparent);
			}

			.swiper-text-content {
				position: absolute;
				bottom: 30rpx;
				left: 30rpx;
				right: 30rpx;
				z-index: 10;
				color: #ffffff;
				text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.5);
				display: flex;
				flex-direction: column;
			}

			.swiper-title {
				font-size: 36rpx;
				font-weight: bold;
				margin-bottom: 10rpx;
				overflow: hidden;
				text-overflow: ellipsis;
				white-space: nowrap;
			}

			.swiper-subtitle {
				font-size: 26rpx;
				opacity: 0.9;
				overflow: hidden;
				text-overflow: ellipsis;
				display: -webkit-box;
				-webkit-line-clamp: 2;
				-webkit-box-orient: vertical;
			}
		}
	}

	/* --- 每日科普模块样式 --- */
	.daily-knowledge-section {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 30rpx;
		background: linear-gradient(90deg, #ffeaa7 0%, #fab1a0 100%);
		border-radius: 20rpx;
		margin: 20rpx 30rpx;
		box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.05);
		cursor: pointer;
		transition: transform 0.3s ease, box-shadow 0.3s ease;
	}

	.daily-knowledge-section:active {
		transform: scale(0.98);
		box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.08);
	}

	.knowledge-icon {
		font-size: 60rpx;
		color: #ff9a9e;
	}

	.knowledge-content {
		flex: 1;
		margin-left: 20rpx;
	}

	.knowledge-title {
		display: block;
		font-size: 34rpx;
		font-weight: bold;
		color: #2d3436;
		margin-bottom: 8rpx;
	}

	.knowledge-subtitle {
		display: block;
		font-size: 26rpx;
		color: #636e72;
	}

	.knowledge-action {
		display: flex;
		align-items: center;
	}

	.action-text {
		font-size: 28rpx;
		color: #ff9a9e;
		font-weight: 500;
	}

	/* AI推荐宠物模块样式 */
	.ai-recommend-section {
		display: flex;
		align-items: center;
		justify-content: space-between;
		padding: 30rpx;
		background: linear-gradient(90deg, #e8e4ff 0%, #d4e4ff 100%);
		border-radius: 20rpx;
		margin: 20rpx 30rpx;
		box-shadow: 0 8rpx 25rpx rgba(102, 126, 234, 0.1);
		cursor: pointer;
		transition: transform 0.3s ease, box-shadow 0.3s ease;
	}

	.ai-recommend-section:active {
		transform: scale(0.98);
		box-shadow: 0 4rpx 15rpx rgba(102, 126, 234, 0.15);
	}

	.ai-recommend-icon {
		font-size: 60rpx;
		color: #667eea;
	}

	.ai-recommend-content {
		flex: 1;
		margin-left: 20rpx;
	}

	.ai-recommend-title {
		display: block;
		font-size: 34rpx;
		font-weight: bold;
		color: #2d3436;
		margin-bottom: 8rpx;
	}

	.ai-recommend-subtitle {
		display: block;
		font-size: 26rpx;
		color: #636e72;
	}

	.ai-recommend-action {
		display: flex;
		align-items: center;
	}

	.ai-recommend-action .action-text {
		color: #667eea;
	}

	/* 精选毛孩子模块 - 修复后 */
	.featured-pets-section {
		margin: 20rpx 30rpx;
		background-color: #ffffff;
		border-radius: 20rpx;
		box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.05);
		overflow: hidden;
	}

	/* 宠物网格 - 修复：只保留一个定义 */
	.pets-grid {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		gap: 24rpx;
		padding: 30rpx;
	}

	.pet-card {
		background-color: #fff;
		border-radius: 16rpx;
		overflow: hidden;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
		transition: transform 0.2s ease;
		display: flex;
		flex-direction: column;
		height: 100%;
	}

	.pet-card:active {
		transform: scale(0.98);
	}

	.pet-image {
		width: 100%;
		height: 200rpx;
		display: block;
		object-fit: cover;
	}

	.pet-info {
		padding: 20rpx;
		flex: 1;
		display: flex;
		flex-direction: column;
	}

	/* 宠物名称 - 修复：确保正确的选择器层级 */
	.pet-info .pet-name {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
		display: block;
		margin-bottom: 8rpx;
		line-height: 1.2;
	}

	.pet-basic-info {
		margin-bottom: 16rpx;
	}

	.pet-meta {
		display: flex;
		flex-wrap: wrap;
		gap: 12rpx;
		margin-bottom: 12rpx;
	}

	.meta-item {
		font-size: 24rpx;
		color: #666;
		padding: 4rpx 12rpx;
		background-color: #f5f5f5;
		border-radius: 20rpx;
		line-height: 1.2;
	}

	/* 宠物描述 */
	.pet-description {
		font-size: 26rpx;
		color: #666;
		line-height: 1.5;
		margin-bottom: 20rpx;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	/* 健康状态样式 - 修改后的布局 */
	.pet-health-status {
		display: flex;
		justify-content: space-between;
		gap: 10rpx;
		padding-top: 16rpx;
		margin-top: auto;
		border-top: 1rpx solid #f0f0f0;
	}

	.health-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		flex: 1;
		min-width: 0;
	}

	.health-icon {
		width: 40rpx;
		height: 40rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 6rpx;
	}

	.health-icon-img {
		width: 32rpx;
		height: 32rpx;
	}

	.health-text {
		font-size: 22rpx;
		color: #666;
		text-align: center;
		margin-bottom: 4rpx;
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		width: 100%;
	}

	.health-status {
		font-size: 20rpx;
		font-weight: 500;
		padding: 4rpx 12rpx;
		border-radius: 12rpx;
		min-width: 40rpx;
		text-align: center;
		width: 100%;
		box-sizing: border-box;
	}

	.status-yes {
		background-color: #e8f5e9;
		color: #4caf50;
	}

	.status-no {
		background-color: #ffebee;
		color: #f44336;
	}

	/* --- 日常活动模块样式 --- */
	.daily-activity-section {
		margin: 20rpx 30rpx;
		background-color: #ffffff;
		border-radius: 20rpx;
		box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.05);
		overflow: hidden;
	}

	.section-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 25rpx 30rpx;
		border-bottom: 1rpx solid #f0f0f0;
	}

	.section-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.section-more {
		font-size: 28rpx;
		color: #ff6b6b;
	}

	.activity-card {
		position: relative;
	}

	.activity-image {
		width: 100%;
		height: 300rpx;
		display: block;
	}

	.activity-info {
		position: absolute;
		bottom: 0;
		left: 0;
		right: 0;
		padding: 25rpx 30rpx;
		background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
		color: #ffffff;
	}

	.info-title {
		display: block;
		font-size: 30rpx;
		font-weight: bold;
		margin-bottom: 10rpx;
	}

	.info-item {
		display: block;
		font-size: 26rpx;
		opacity: 0.9;
	}

	/* --- 精选毛孩子模块样式 --- */
	.featured-pets-section {
		margin: 20rpx 30rpx;
		background-color: #ffffff;
		border-radius: 20rpx;
		box-shadow: 0 8rpx 25rpx rgba(0, 0, 0, 0.05);
		overflow: hidden;
	}

	.pets-grid {
		display: grid;
		grid-template-columns: repeat(2, 1fr);
		gap: 20rpx;
		padding: 25rpx 30rpx;
	}

	.pet-card {
		background-color: #fff;
		border-radius: 16rpx;
		overflow: hidden;
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
		transition: transform 0.2s ease;
	}

	.pet-card:active {
		transform: scale(0.98);
	}

	.pet-image {
		width: 100%;
		height: 180rpx;
		display: block;
	}

	.pet-info {
		padding: 20rpx;
	}

	.pet-name {
		display: block;
		font-size: 30rpx;
		font-weight: bold;
		color: #333;
		margin-bottom: 8rpx;
	}

	.pet-desc {
		display: block;
		font-size: 24rpx;
		color: #666;
		margin-bottom: 15rpx;
	}

	.pet-tags {
		display: flex;
		gap: 10rpx;
	}

	.pet-tag {
		padding: 4rpx 12rpx;
		font-size: 22rpx;
		border-radius: 12rpx;
	}

	/* 标签颜色 */
	.pet-tag:nth-child(1) {
		background-color: #e3f2fd;
		color: #1976d2;
	}

	.pet-tag:nth-child(2) {
		background-color: #e8f5e9;
		color: #388e3c;
	}

	.pet-tag:nth-child(3) {
		background-color: #fff3e0;
		color: #f57c00;
	}

	.pet-tag:nth-child(4) {
		background-color: #fce4ec;
		color: #c2185b;
	}

	/* --- 服务承诺区域 --- */
	.service-promise-section {
		padding: 40rpx 0 20rpx;

		.divider-with-text {
			display: flex;
			align-items: center;
			justify-content: center;
			padding: 0 40rpx;
		}

		.divider-line {
			flex: 1;
			height: 2rpx;
			background: linear-gradient(to right, transparent, #ddd, transparent);
		}

		.divider-text {
			padding: 0 30rpx;
			font-size: 30rpx;
			color: #666;
			font-weight: 500;
			white-space: nowrap;
		}
	}

	/* --- 核心功能区 --- */
	.core-services-wrapper {
		display: flex;
		justify-content: space-around;
		align-items: stretch;
		padding: 20rpx 40rpx 60rpx;
		gap: 20rpx;
	}

	/* --- 服务卡片 --- */
	.service-card {
		flex: 1;
		display: flex;
		flex-direction: column;
		background-color: #ffffff;
		border-radius: 24rpx;
		box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.06);
		overflow: hidden;
		transition: all 0.3s ease;

		button {
			background-color: transparent !important;
			border: none !important;
			padding: 0 !important;
			margin: 0 !important;
			line-height: normal !important;

			&::after {
				border: none;
			}
		}

		&:active {
			transform: scale(0.96);
			box-shadow: 0 4rpx 15rpx rgba(0, 0, 0, 0.08);
		}
	}

	.primary-card {
		border: 2rpx solid #3498db;
		transform: scale(1.05);
		box-shadow: 0 12rpx 40rpx rgba(52, 152, 219, 0.2);
	}

	.card-image-container {
		width: 100%;
		height: 200rpx;
		overflow: hidden;

		.service-image {
			width: 100%;
			height: 100%;
			transition: transform 0.5s ease;
		}
	}

	.service-card:hover .service-image {
		transform: scale(1.1);
	}

	.card-content {
		padding: 24rpx;
		text-align: center;
		flex-grow: 1;
		display: flex;
		flex-direction: column;
		justify-content: center;
	}

	.card-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #2c3e50;
		margin-bottom: 10rpx;
	}

	.card-subtitle {
		font-size: 24rpx;
		color: #7f8c8d;
	}

	.primary-card {
		.card-title {
			color: #3498db;
		}

		.card-subtitle {
			color: #5dade2;
		}
	}
</style>