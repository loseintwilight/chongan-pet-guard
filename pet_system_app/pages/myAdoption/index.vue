<template>
	<view class="pet-container">
		<!-- 筛选标签 -->
		<view class="filter-tabs">
			<view v-for="tab in filterTabs" :key="tab.value" class="filter-tab"
				:class="{ active: activeTab === tab.value }" @click="handleTabChange(tab.value)">
				<text class="tab-text">{{ tab.label }}</text>
			</view>
		</view>

		<!-- 宠物列表 -->
		<scroll-view scroll-y class="pet-list" :refresher-enabled="true" :refresher-triggered="refreshing"
			@refresherrefresh="onRefresh" @scrolltolower="onScrollToLower" :style="{ height: scrollViewHeight + 'px' }">
			<!-- 下拉刷新提示 -->
			<view v-if="refreshing" class="refresh-tips">
				<text>正在刷新...</text>
			</view>

			<view v-if="!loading && adoptionList.length === 0" class="empty-state">
				<view class="empty-img">🐾</view>
				<text class="empty-text">暂无领养宠物</text>
				<text class="empty-tip">下拉刷新试试</text>
			</view>

			<view v-for="item in adoptionList" :key="item.pet.petId" class="pet-card" @click="navigateToDetail(item)">
				<view class="pet-card-content">
					<!-- 左侧宠物图片区域 -->
					<view class="pet-image-wrapper">
						<image :src="getImageUrl(item.pet.images)" mode="aspectFill" class="pet-image"
							:draggable="false" @error="handleImageError" @load="handleImageLoad" />
						<!-- 宠物物种标签 -->
						<view class="species-tag">
							{{ getSpeciesText(item.pet.species) }}
						</view>
					</view>

					<!-- 右侧宠物信息区域 -->
					<view class="pet-info">
						<!-- 第一行：宠物名称和年龄 -->
						<view class="pet-header">
							<text class="pet-name">{{ item.pet.name || '未命名' }}</text>
							<text class="pet-age">{{ item.pet.age || '?' }}个月</text>
						</view>

						<!-- 第二行：宠物品种 -->
						<view v-if="item.pet.breed" class="pet-breed">
							<text>{{ item.pet.breed }}</text>
						</view>

						<!-- 第三行：宠物描述 -->
						<view class="pet-description">
							<text>{{ item.pet.description || '这个可爱的小家伙正在寻找一个温暖的家～' }}</text>
						</view>

						<!-- 第四行：健康状态 - 固定位置 -->
						<view class="health-status">
							<view class="status-item">
								<view class="status-icon">💉</view>
								<text class="status-text">{{ getImmunityText(item.pet.immunity) }}</text>
							</view>

							<view class="status-item">
								<view class="status-icon">✂️</view>
								<text class="status-text">{{ getNeuterText(item.pet.neuter) }}</text>
							</view>

							<view class="status-item">
								<view class="status-icon">🐛</view>
								<text class="status-text">{{ getInsectRepellentText(item.pet.insectRepellent) }}</text>
							</view>
						</view>


					</view>
				</view>
			</view>

			<!-- 加载更多 -->
			<view v-if="loading && adoptionList.length > 0" class="loading-more">
				<text>加载中...</text>
			</view>

			<view v-if="noMore" class="no-more">
				<text>已显示全部</text>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	import {
		listMyAppliedPets
	} from '@/api/pet/adoption.js'
	import config from '@/config';

	export default {
		data() {
			return {
				// 筛选标签
				filterTabs: [{
						label: '全部',
						value: null
					},
					{
						label: '待审核',
						value: 0
					},
					{
						label: '通过',
						value: 1
					},
					{
						label: '不通过',
						value: 2
					},
					{
						label: '已领养',
						value: 3
					}
				],
				activeTab: null,

				// 领养列表
				adoptionList: [],


				// 分页参数
				queryParams: {
					pageNum: 1,
					pageSize: 10
				},

				// 加载状态
				loading: false,
				refreshing: false,
				noMore: false,
				total: 0,

				// 滚动区域高度
				scrollViewHeight: 0
			}
		},

		onLoad() {
			this.loadadoptionList()
			this.calcScrollViewHeight()
		},

		onShow() {
			this.calcScrollViewHeight()
		},

		methods: {
			// 计算滚动区域高度
			calcScrollViewHeight() {
				uni.getSystemInfo({
					success: (res) => {
						// 减去筛选标签高度
						this.scrollViewHeight = res.windowHeight - 80
					}
				})
			},

			getImageUrl(fileName) {
				return config.getDownloadUrl(fileName);
			},

			// 加载宠物列表
			async loadadoptionList() {
				if (this.loading) return

				this.loading = true
				try {
					const params = {
						pageNum: this.queryParams.pageNum,
						pageSize: this.queryParams.pageSize,
						status: this.activeTab
					}

					const res = await listMyAppliedPets(params)
					if (res.code === 200) {
						const data = res.rows || res.data || []

						if (this.queryParams.pageNum === 1) {
							this.adoptionList = data
						} else {
							this.adoptionList = [...this.adoptionList, ...data]
						}

						this.total = res.total || 0
						this.noMore = this.adoptionList.length >= this.total
					} else {
						uni.showToast({
							title: res.msg || '加载失败',
							icon: 'none'
						})
					}
				} catch (error) {
					console.error('加载宠物列表失败:', error)
					uni.showToast({
						title: '加载失败',
						icon: 'none'
					})
				} finally {
					this.loading = false
					this.refreshing = false
				}
			},

			// 处理标签切换
			handleTabChange(tabValue) {
				this.activeTab = tabValue
				this.queryParams.pageNum = 1
				this.loadadoptionList()
			},

			// 下拉刷新
			onRefresh() {
				if (this.refreshing || this.loading) return

				this.refreshing = true
				this.queryParams.pageNum = 1
				this.loadadoptionList()
			},

			// 滚动到底部加载更多
			onScrollToLower() {
				if (this.loading || this.noMore) return

				this.queryParams.pageNum++
				this.loadadoptionList()
			},

			// 跳转到详情页
			navigateToDetail(it) { // 直接接收 item 对象，不用单独传 petId
				// 1. 序列化对象 + 编码（防止中文/特殊字符截断）
				const AdoptionItem = encodeURIComponent(JSON.stringify(it))
				console.log(it);
				// 2. 拼接 URL 传参
				uni.navigateTo({
					url: `/pages/myAdoption/detail?AdoptionItem=${AdoptionItem}`
				})
			},


			// 获取物种文本
			getSpeciesText(species) {
				const speciesMap = {
					0: '猫',
					1: '狗',
					2: '其他'
				}
				return speciesMap[species] || '宠物'
			},

			// 获取免疫状态文本
			getImmunityText(immunity) {
				const immunityMap = {
					0: '已免疫',
					1: '未免疫',
					2: '免疫未知'
				}
				return immunityMap[immunity] || '免疫未知'
			},

			// 获取绝育状态文本
			getNeuterText(neuter) {
				const neuterMap = {
					0: '未绝育',
					1: '已绝育',
					2: '绝育未知'
				}
				return neuterMap[neuter] || '绝育未知'
			},

			// 获取驱虫状态文本
			getInsectRepellentText(insectRepellent) {
				const insectRepellentMap = {
					0: '未驱虫',
					1: '已驱虫',
					2: '驱虫未知'
				}
				return insectRepellentMap[insectRepellent] || '驱虫未知'
			},

			// 图片错误处理
			handleImageError(e) {
				console.error('图片加载失败:', e)
				e.currentTarget.src = '/static/images/default-pet.png'
				e.currentTarget.onerror = null
			},

			// 图片加载成功
			handleImageLoad(e) {
				console.log('图片加载成功')
			}
		}
	}
</script>

<style scoped>
	.pet-container {
		background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
		min-height: 100vh;
	}

	/* 筛选标签 */
	.filter-tabs {
		display: flex;
		justify-content: center;
		gap: 16rpx;
		padding: 20rpx 30rpx;
		background: rgba(255, 255, 255, 0.95);
		backdrop-filter: blur(10px);
		border-bottom: 1rpx solid rgba(0, 0, 0, 0.1);
		position: sticky;
		top: 0;
		z-index: 10;
	}

	.filter-tab {
		padding: 12rpx 24rpx;
		border-radius: 25rpx;
		background: #f8f9fa;
		border: 2rpx solid transparent;
		transition: all 0.3s ease;
		cursor: pointer;
	}

	.filter-tab.active {
		background: linear-gradient(135deg, #ff6b35, #ff8e53);
		border-color: #ff6b35;
		transform: translateY(-2rpx);
		box-shadow: 0 4rpx 12rpx rgba(255, 107, 53, 0.3);
	}

	.tab-text {
		font-size: 28rpx;
		color: #666;
		font-weight: 500;
		transition: all 0.3s ease;
	}

	.filter-tab.active .tab-text {
		color: #ffffff;
		font-weight: 600;
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
		color: #ff6b35;
		font-weight: 600;
		backdrop-filter: blur(10px);
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

	.pet-age {
		font-size: 26rpx;
		color: #ff6b35;
		font-weight: 600;
		background: rgba(255, 107, 53, 0.1);
		padding: 8rpx 16rpx;
		border-radius: 12rpx;
		white-space: nowrap;
	}

	/* 宠物品种 */
	.pet-breed {
		font-size: 24rpx;
		color: #666;
		background: rgba(102, 102, 102, 0.1);
		padding: 6rpx 12rpx;
		border-radius: 8rpx;
		align-self: flex-start;
	}

	/* 宠物描述 */
	.pet-description {
		font-size: 26rpx;
		color: #666;
		line-height: 1.5;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
		overflow: hidden;
	}

	/* 健康状态 - 固定位置 */
	.health-status {
		display: flex;
		gap: 20rpx;
		margin-top: 8rpx;
	}

	.status-item {
		display: flex;
		align-items: center;
		gap: 8rpx;
		padding: 8rpx 16rpx;
		background: rgba(245, 245, 245, 0.8);
		border-radius: 12rpx;
		backdrop-filter: blur(10px);
	}

	.status-icon {
		font-size: 24rpx;
	}

	.status-text {
		font-size: 22rpx;
		color: #666;
		font-weight: 500;
		white-space: nowrap;
	}

	/* 位置信息 */
	.pet-location {
		display: flex;
		align-items: center;
		gap: 8rpx;
		margin-top: 4rpx;
	}

	.location-icon {
		font-size: 20rpx;
		color: #999;
	}

	.location-text {
		font-size: 22rpx;
		color: #999;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
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