<template>
	<view class="container">
		<!-- 顶部导航栏 -->
		<view class="nav-bar">
			<view class="nav-title">寻宠启事</view>
			<view class="nav-right">
				<view class="register-btn" @click="goToRegister">丢失登记</view>
			</view>
		</view>

		<!-- 分类标签 -->
		<view class="category-tabs">
			<view v-for="tab in tabs" :key="tab.value" class="tab-item" :class="{ active: activeTab === tab.value }" @click="switchTab(tab.value)">
				{{ tab.label }}
			</view>
		</view>

		<!-- 宠物列表 -->
		<scroll-view
			class="pet-list"
			scroll-y
			:refresher-enabled="true"
			:refresher-triggered="refreshing"
			@refresherrefresh="onRefresh"
			@scrolltolower="loadMore"
			:style="{ height: scrollViewHeight + 'px' }">
			<!-- 下拉刷新提示 -->
			<view v-if="refreshing" class="refresh-tips">
				<text>正在刷新...</text>
			</view>

			<!-- 空状态 -->
			<view v-if="!loading && filteredPetList.length === 0" class="empty-state">
				<view class="empty-img">🔍</view>
				<text class="empty-text">暂无寻宠启事</text>
				<text class="empty-tip">下拉刷新试试</text>
			</view>

			<!-- 宠物卡片列表 -->
			<view v-for="pet in filteredPetList" :key="pet.lostId" class="pet-card" @click="goToDetail(pet.lostId)">
				<view class="pet-card-content">
					<!-- 左侧图片区域 -->
					<view class="pet-image-wrapper">
						<image :src="getImageUrl(pet.images)" mode="aspectFill" class="pet-image" :draggable="false" @error="handleImageError" @load="handleImageLoad" />
						<!-- 宠物物种标签 -->
						<view class="species-tag">
							{{ getSpeciesText(pet.species) }}
						</view>
						<!-- 悬赏金额 -->
						<view v-if="pet.reward && pet.reward > 0" class="reward-tag">
							<text class="reward-icon">💰</text>
							<text class="reward-text">{{ pet.reward }}￥</text>
						</view>
					</view>

					<!-- 右侧信息区域 -->
					<view class="pet-info">
						<!-- 第一行：宠物名称和年龄 -->
						<view class="pet-header">
							<text class="pet-name">{{ pet.petName || '未命名' }}</text>
							<view class="right-info">
								<view class="status-tag" :class="statusClassMap[pet.status] || 'status-searching'">
									{{ getStatusText(pet.status) }}
								</view>
							</view>
						</view>

						<!-- 第二行：特征描述 -->
						<view class="pet-features">
							<text>宠物特征:{{ pet.features || pet.message || '特征描述...' }}</text>
						</view>

						<!-- 第三行：丢失信息 -->
						<view class="lost-info">
							<view class="info-item">
								<view class="info-icon">📍</view>
								<text class="info-text">{{ pet.lostLocation || '未知地点' }}</text>
							</view>
							<view class="info-item">
								<view class="info-icon">🕒</view>
								<text class="info-text">{{ formatTime(pet.lostTime) }}</text>
							</view>
						</view>

						<!-- 第四行：联系人信息 -->
						<view class="contact-info">
							<text class="contact-text">主人:{{ pet.ownerName }}</text>
							<text class="contact-text">📞 {{ pet.ownerPhone }}</text>
						</view>
					</view>
				</view>
			</view>

			<!-- 加载更多 -->
			<view v-if="loading && filteredPetList.length > 0" class="loading-more">
				<text>加载中...</text>
			</view>

			<view v-if="noMore && filteredPetList.length > 0" class="no-more">
				<text>已显示全部</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
import { listLostPet } from '@/api/pet/lostPet.js';
import config from '@/config';

export default {
	data() {
		return {
			// 分类标签
			tabs: [
				{
					label: '全部',
					value: 0
				},
				{
					label: '狗狗',
					value: 1
				},
				{
					label: '猫猫',
					value: 2
				},
				{
					label: '其他',
					value: 3
				}
			],
			activeTab: 0,

			// 宠物列表
			petList: [],

			// 分页参数 - 按照PC端结构定义
			queryParams: {
				lostId: null,
				pageNum: 1,
				pageSize: 10,
				ownerName: null,
				ownerPhone: null,
				petName: null,
				species: null,
				breed: null,
				features: null,
				images: null,
				lostLocation: null,
				lostTime: null,
				status: null,
				params: {} // 添加params对象用于传递时间范围参数
			},

			// 加载状态
			loading: false,
			refreshing: false,
			noMore: false,
			total: 0,

			// 滚动区域高度
			scrollViewHeight: 0,

			// 状态类名映射
			statusClassMap: {
				0: 'status-searching',
				1: 'status-found',
				2: 'status-ended'
			}
		};
	},

	onLoad() {
		this.loadPetList();
		this.calcScrollViewHeight();
	},

	onShow() {
		this.calcScrollViewHeight();
		// 从登记页面返回时刷新数据
		this.loadPetList(true);
	},

	computed: {
		// 根据标签筛选宠物列表
		filteredPetList() {
			if (this.activeTab === 0) {
				return this.petList;
			}
			// 数据库species映射: 0-猫, 1-狗, 2-其他
			// UI标签映射: 0-全部, 1-狗狗, 2-猫猫, 3-其他
			let speciesValue;
			if (this.activeTab === 1) speciesValue = 1; // 狗狗
			else if (this.activeTab === 2) speciesValue = 0; // 猫猫
			else if (this.activeTab === 3) speciesValue = 2; // 其他

			return this.petList.filter((pet) => pet.species === speciesValue);
		}
	},

	methods: {
		// 计算滚动区域高度
		calcScrollViewHeight() {
			// 获取窗口信息（替代已弃用的getSystemInfo）
			const windowInfo = uni.getWindowInfo();
			// 减去导航栏和标签栏高度
			this.scrollViewHeight = windowInfo.windowHeight - 160;
		},

		// 获取图片URL
		getImageUrl(fileName) {
			return config.getDownloadUrl(fileName) || '/static/images/default-pet.png';
		},

		// 加载宠物列表 - 修改查询参数结构
		async loadPetList(forceRefresh = false) {
			if (this.loading && !forceRefresh) return;

			this.loading = true;
			try {
				// 根据PC端代码构建查询参数
				const params = {
					pageNum: this.queryParams.pageNum,
					pageSize: this.queryParams.pageSize,
					ownerName: this.queryParams.ownerName,
					ownerPhone: this.queryParams.ownerPhone,
					petName: this.queryParams.petName,
					species: this.activeTab === 0 ? null : this.getSpeciesValue(),
					breed: this.queryParams.breed,
					lostLocation: this.queryParams.lostLocation,
					status: null, // uniapp端默认显示所有状态的宠物
					params: this.queryParams.params // 传递params对象
				};

				console.log('查询参数:', params); // 调试用

				const res = await listLostPet(params);
				console.log('接口响应:', res); // 调试用

				if (res.code === 200) {
					const data = res.rows || res.data || [];

					if (forceRefresh || this.queryParams.pageNum === 1) {
						this.petList = data;
					} else {
						this.petList = [...this.petList, ...data];
					}

					this.total = res.total || 0;
					this.noMore = this.petList.length >= this.total;

					console.log('加载成功，数据条数:', data.length); // 调试用
				} else {
					console.error('接口返回错误:', res.msg);
					uni.showToast({
						title: res.msg || '加载失败',
						icon: 'none'
					});
				}
			} catch (error) {
				console.error('加载寻宠列表失败:', error);
				uni.showToast({
					title: '加载失败: ' + (error.message || '网络错误'),
					icon: 'none'
				});
			} finally {
				this.loading = false;
				this.refreshing = false;
			}
		},

		// 根据activeTab获取物种值
		getSpeciesValue() {
			if (this.activeTab === 0) return null;
			if (this.activeTab === 1) return 1; // 狗狗
			if (this.activeTab === 2) return 0; // 猫猫
			if (this.activeTab === 3) return 2; // 其他
			return null;
		},

		// 跳转到登记页面
		goToRegister() {
			uni.navigateTo({
				url: '/pages/lostPet/registration'
			});
		},

		// 跳转到详情页面
		goToDetail(lostId) {
			uni.navigateTo({
				url: `/pages/lostPet/detail?id=${lostId}`
			});
		},

		// 切换标签
		switchTab(tabValue) {
			this.activeTab = tabValue;
			this.queryParams.pageNum = 1;
			this.petList = [];
			this.loadPetList();
		},

		// 下拉刷新
		onRefresh() {
			if (this.refreshing || this.loading) return;

			this.refreshing = true;
			this.queryParams.pageNum = 1;
			this.loadPetList(true);
		},

		// 上拉加载更多
		loadMore() {
			if (this.loading || this.noMore) return;

			this.queryParams.pageNum++;
			this.loadPetList();
		},

		// 格式化时间
		formatTime(timeStr) {
			if (!timeStr) return '未知时间';

			const time = new Date(timeStr);
			const now = new Date();
			const diffMs = now - time;
			const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));

			if (diffDays === 0) {
				const hours = time.getHours().toString().padStart(2, '0');
				const minutes = time.getMinutes().toString().padStart(2, '0');
				return `今天 ${hours}:${minutes}`;
			} else if (diffDays === 1) {
				return '昨天';
			} else if (diffDays < 7) {
				return `${diffDays}天前`;
			} else {
				const month = time.getMonth() + 1;
				const day = time.getDate();
				return `${month}月${day}日`;
			}
		},

		// 获取物种文本
		getSpeciesText(species) {
			const speciesMap = {
				0: '猫',
				1: '狗',
				2: '其他'
			};
			return speciesMap[species] || '宠物';
		},

		// 获取状态文本
		getStatusText(status) {
			const statusMap = {
				0: '寻找中',
				1: '已找到',
				2: '已结束'
			};
			return statusMap[status] || '寻找中';
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
		}
	}
};
</script>
<style scoped>
.container {
	background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
	min-height: 100vh;
}

/* 导航栏 */
.nav-bar {
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 20rpx 30rpx;
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(10px);
	border-bottom: 1rpx solid rgba(0, 0, 0, 0.1);
	position: sticky;
	top: 0;
	z-index: 10;
}

.nav-title {
	font-size: 36rpx;
	font-weight: 600;
	color: #2c3e50;
}

.register-btn {
	background: linear-gradient(135deg, #ff6b35, #ff8e53);
	color: white;
	padding: 12rpx 32rpx;
	border-radius: 25rpx;
	font-size: 28rpx;
	font-weight: 500;
	box-shadow: 0 4rpx 12rpx rgba(255, 107, 53, 0.3);
	transition: all 0.3s ease;
}

.register-btn:active {
	transform: translateY(2rpx);
	box-shadow: 0 2rpx 6rpx rgba(255, 107, 53, 0.3);
}

/* 分类标签 */
.category-tabs {
	display: flex;
	justify-content: space-around;
	padding: 20rpx 30rpx;
	background: rgba(255, 255, 255, 0.95);
	backdrop-filter: blur(10px);
	border-bottom: 1rpx solid rgba(0, 0, 0, 0.1);
}

.tab-item {
	padding: 12rpx 24rpx;
	border-radius: 25rpx;
	background: #f8f9fa;
	border: 2rpx solid transparent;
	font-size: 28rpx;
	color: #666;
	font-weight: 500;
	transition: all 0.3s ease;
}

.tab-item.active {
	background: linear-gradient(135deg, #667eea, #764ba2);
	border-color: #667eea;
	color: #ffffff;
	font-weight: 600;
	transform: translateY(-2rpx);
	box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.3);
}

/* 宠物列表 */
.pet-list {
	padding: 20rpx 30rpx;
}

.refresh-tips {
	text-align: center;
	padding: 20rpx 0;
	color: #999;
	font-size: 24rpx;
}

.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 100rpx 0;
}

.empty-img {
	font-size: 120rpx;
	margin-bottom: 30rpx;
	opacity: 0.5;
}

.empty-text {
	font-size: 28rpx;
	color: #666;
	margin-bottom: 16rpx;
}

.empty-tip {
	font-size: 24rpx;
	color: #999;
}

/* 宠物卡片 */
.pet-card {
	background: rgba(255, 255, 255, 0.95);
	border-radius: 24rpx;
	margin-bottom: 30rpx;
	overflow: hidden;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.1);
	backdrop-filter: blur(10px);
	border: 1rpx solid rgba(255, 255, 255, 0.2);
	transition: all 0.3s ease;
}

.pet-card:active {
	transform: translateY(4rpx);
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.15);
}

.pet-card-content {
	display: flex;
	padding: 30rpx;
	align-items: flex-start;
	gap: 24rpx;
}

/* 左侧图片区域 */
.pet-image-wrapper {
	position: relative;
	width: 200rpx;
	height: 200rpx;
	border-radius: 16rpx;
	overflow: hidden;
	flex-shrink: 0;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.15);
}

.pet-image {
	width: 100%;
	height: 100%;
	object-fit: cover;
}

.species-tag {
	position: absolute;
	top: 12rpx;
	left: 12rpx;
	padding: 6rpx 16rpx;
	background: rgba(255, 255, 255, 0.9);
	border-radius: 20rpx;
	font-size: 20rpx;
	color: #667eea;
	font-weight: 600;
	backdrop-filter: blur(10px);
}

.reward-tag {
	position: absolute;
	bottom: 12rpx;
	right: 12rpx;
	display: flex;
	align-items: center;
	padding: 6rpx 12rpx;
	background: rgba(255, 107, 53, 0.9);
	border-radius: 20rpx;
	font-size: 20rpx;
	color: white;
	font-weight: 600;
	backdrop-filter: blur(10px);
}

.reward-icon {
	margin-right: 4rpx;
}

/* 右侧信息区域 */
.pet-info {
	flex: 1;
	min-width: 0;
	display: flex;
	flex-direction: column;
	gap: 16rpx;
}

/* 宠物头部信息 */
.pet-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	gap: 20rpx;
}

.pet-name {
	font-size: 32rpx;
	font-weight: bold;
	color: #2c3e50;
	flex: 1;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.right-info {
	display: flex;
	flex-direction: column;
	align-items: flex-end;
	gap: 8rpx;
}

.pet-age {
	font-size: 26rpx;
	color: #667eea;
	font-weight: 600;
	background: rgba(102, 126, 234, 0.1);
	padding: 8rpx 16rpx;
	border-radius: 12rpx;
	white-space: nowrap;
}

.status-tag {
	padding: 6rpx 12rpx;
	border-radius: 12rpx;
	font-size: 22rpx;
	font-weight: 500;
}

.status-searching {
	background: rgba(255, 107, 53, 0.1);
	color: #ff6b35;
}

.status-found {
	background: rgba(76, 175, 80, 0.1);
	color: #4caf50;
}

.status-ended {
	background: rgba(158, 158, 158, 0.1);
	color: #9e9e9e;
}

/* 特征描述 */
.pet-features {
	font-size: 26rpx;
	color: #666;
	line-height: 1.5;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
}

/* 丢失信息 */
.lost-info {
	display: flex;
	flex-direction: column;
	gap: 12rpx;
}

.info-item {
	display: flex;
	align-items: center;
	gap: 8rpx;
}

.info-icon {
	font-size: 24rpx;
	color: #667eea;
}

.info-text {
	font-size: 24rpx;
	color: #666;
	font-weight: 500;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

/* 联系人信息 */
.contact-info {
	padding-top: 8rpx;
	border-top: 1rpx solid rgba(0, 0, 0, 0.1);
}

.contact-text {
	font-size: 22rpx;
	color: #999;
}

/* 加载更多 */
.loading-more,
.no-more {
	text-align: center;
	padding: 40rpx 0;
	color: #999;
	font-size: 24rpx;
}
</style>
