<template>
	<view class="care-detail-container">
		<!-- 页面头部 -->
		<view class="page-header">
			<view class="back-icon" @click="navigateBack">
				<view class="iconfont icon-back"></view>
			</view>
			<view class="header-title">订单详情</view>
			<view class="header-placeholder"></view>
		</view>
		
		<!-- 内容区域 -->
		<scroll-view class="content-scroll" scroll-y>
			<!-- 加载状态 -->
			<view v-if="loading" class="loading-container">
				<uni-spinner type="scale"></uni-spinner>
				<text class="loading-text">加载中...</text>
			</view>
			
			<!-- 错误状态 -->
			<view v-else-if="error" class="error-container">
				<image src="/static/images/error.png" class="error-image"></image>
				<text class="error-text">{{ errorMessage || '加载失败，请重试' }}</text>
				<button type="primary" class="retry-button" @click="loadBoardingDetail">重新加载</button>
			</view>
			
			<!-- 空数据状态 -->
			<view v-else-if="!boardingDetail" class="empty-container">
				<image src="/static/images/entrust01.png" class="empty-image"></image>
				<text class="empty-text">暂无订单详情</text>
			</view>
			
			<!-- 订单详情内容 -->
			<view v-else class="detail-content">
				<!-- 宠物图片 -->
				<view class="pet-image-container">
					<image 
						:src="getImageUrl(boardingDetail.images)" 
						mode="aspectFill" 
						@error="handleImageError($event)"
						@click="previewImage"
					></image>
				</view>
				
				<!-- 基本信息卡片 -->
				<view class="info-card">
					<view class="pet-header">
						<text class="pet-name">{{ boardingDetail.petName }}</text>
						<view class="status-tag" :class="{
							'status-pending': boardingDetail.status === 0,
							'status-confirm': boardingDetail.status === 1,
							'status-processing': boardingDetail.status === 2,
							'status-completed': boardingDetail.status === 3,
							'status-canceled': boardingDetail.status === 4,
							'status-user-canceled': boardingDetail.status === 5,
							'status-approved': boardingDetail.status === 6,
							'status-rejected': boardingDetail.status === 7
						}">
							{{ getStatusText(boardingDetail.status) }}
						</view>
					</view>
					<view class="pet-type">{{ boardingDetail.petType }} · {{ boardingDetail.petBreed }}</view>
				</view>
				
				<!-- 托养信息卡片 -->
				<view class="info-card">
					<view class="card-title">托养信息</view>
					<view class="info-item">
						<text class="info-label">托养时间：</text>
						<text class="info-value">{{ formatDate(boardingDetail.startTime) }} 至 {{ formatDate(boardingDetail.endTime) }}</text>
					</view>
					<view class="info-item">
						<text class="info-label">托养费用：</text>
						<text class="info-value price">{{ boardingDetail.totalPrice }}元</text>
					</view>
				</view>
				
				<!-- 宠物详情卡片 -->
				<view class="info-card">
					<view class="card-title">宠物详情</view>
					<view class="info-item">
						<text class="info-label">性别：</text>
						<text class="info-value">{{ boardingDetail.gender === 1 ? '公' : boardingDetail.gender === 2 ? '母' : '未知' }}</text>
					</view>
					<view class="info-item">
						<text class="info-label">年龄：</text>
						<text class="info-value">{{ boardingDetail.age || '未知' }}</text>
					</view>
					<view class="info-item">
						<text class="info-label">健康状况：</text>
						<text class="info-value">{{ boardingDetail.healthInfo || '良好' }}</text>
					</view>
					<view class="info-item">
						<text class="info-label">绝育情况：</text>
						<text class="info-value">{{ boardingDetail.neuter ? '已绝育' : '未绝育' }}</text>
					</view>
					<view class="info-item">
						<text class="info-label">驱虫情况：</text>
						<text class="info-value">{{ boardingDetail.insectRepellent ? '已驱虫' : '未驱虫' }}</text>
					</view>
					<view class="info-item">
						<text class="info-label">驱虫周期：</text>
						<text class="info-value">{{ boardingDetail.dewormingInterval }}</text>
					</view>
                    <view class="info-item">
							<text class="info-label">上次驱虫时间：</text>
							<text class="info-value">{{ formatDate(boardingDetail.insectRepellentLastTime) || '未记录' }}</text>
						</view>
				</view>
				
				<!-- 备注信息 -->
				<view v-if="boardingDetail.notes" class="info-card">
					<view class="card-title">备注</view>
					<text class="notes-text">{{ boardingDetail.notes }}</text>
				</view>

				<!-- 店铺信息 -->
				<view v-if="boardingDetail.shopId" class="info-card">
					<view class="card-title">店铺信息</view>
					
					<!-- 店铺信息加载中 -->
					<view v-if="shopLoading" class="loading-container">
						<uni-spinner type="scale"></uni-spinner>
						<text class="loading-text">加载店铺信息中...</text>
					</view>
					
					<!-- 店铺信息加载失败 -->
					<view v-else-if="shopError" class="shop-error">
						<text class="error-text">店铺信息加载失败</text>
						<button type="primary" size="mini" @click="loadShopDetail(boardingDetail.shopId)">重试</button>
					</view>
					
					<!-- 店铺信息内容 -->
					<view v-else-if="shopDetail">
						<view class="info-item">
							<text class="info-label">店铺名称：</text>
							<text class="info-value">{{ shopDetail.shopName || '暂无' }}</text>
						</view>
						<view class="info-item">
							<text class="info-label">店铺地址：</text>
							<text class="info-value" @click="viewShopLocation">{{ shopDetail.shopAddress || '暂无' }}</text>
						</view>
						<view class="info-item">
							<text class="info-label">联系电话：</text>
							<text class="info-value phone-number" @click="callShop">{{ shopDetail.legalPhone || '暂无' }}</text>
						</view>
						<view class="info-item">
							<text class="info-label">营业时间：</text>
							<text class="info-value">{{ shopDetail.bussinessHours || '暂无信息' }}</text>
						</view>
					</view>
					
					<!-- 店铺信息不存在 -->
					<view v-else class="shop-empty">
						<text class="empty-text">暂无店铺信息</text>
					</view>
				</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
import { getBoarding } from '@/api/pet/boarding.js';
import { getShop } from '@/api/pet/shop.js';
import { getToken } from '@/utils/auth.js';
import config from '@/config.js';

export default {
	data() {
		return {
			boardingId: '',
			boardingDetail: null,
			shopDetail: null,
			loading: true,
			error: false,
			errorMessage: '',
			shopLoading: false,
			shopError: false
		};
	},

	onLoad(options) {
		// 获取订单ID
		if (options.boardingId) {
			this.boardingId = options.boardingId;
			this.loadBoardingDetail();
		} else {
			this.error = true;
			this.errorMessage = '缺少订单ID参数';
			this.loading = false;
		}
	},

	methods: {
		// 返回上一页
		navigateBack() {
			uni.navigateBack();
		},

		// 加载订单详情
		async loadBoardingDetail() {
			try {
				this.loading = true;
				this.error = false;
				
				// 调用API获取订单详情
				const res = await getBoarding(this.boardingId);
				
				if (res.code === 200) {
					// 转换数据格式
					this.boardingDetail = this.transformData(res.data);
					
					// 如果有店铺ID，加载店铺信息
					if (this.boardingDetail.shopId) {
						this.loadShopDetail(this.boardingDetail.shopId);
					}
				} else {
					this.error = true;
					this.errorMessage = res.msg || '加载订单详情失败';
				}
			} catch (e) {
				this.error = true;
				this.errorMessage = e.message || '网络异常，请稍后重试';
				console.error('加载订单详情失败:', e);
			} finally {
				this.loading = false;
			}
		},

		// 加载店铺详情
		async loadShopDetail(shopId) {
			try {
				this.shopLoading = true;
				this.shopError = false;
				
				// 调用API获取店铺详情
				const res = await getShop(shopId);
				
				if (res.code === 200) {
					// 转换店铺数据格式
					this.shopDetail = this.transformShopData(res.data);
				} else {
					this.shopError = true;
					console.error('加载店铺详情失败:', res.msg);
				}
			} catch (e) {
				this.shopError = true;
				console.error('加载店铺详情失败:', e);
			} finally {
				this.shopLoading = false;
			}
		},

		// 转换数据格式
		transformData(data) {
			const transformed = {};
			
			// 字段映射：API字段 -> 模板字段（优先处理，避免被基础转换覆盖）
			transformed.petName = data.name || data.petName || '';
			transformed.petType = this.getPetType(data.species);
			transformed.petBreed = data.breed || data.petBreed || '';
			transformed.startTime = data.startDate || data.start_time || '';
			transformed.endTime = data.endDate || data.end_time || '';
			transformed.totalPrice = data.price || data.totalAmount || data.total_price || 0;
			transformed.boardingId = data.boardingId || data.boarding_id || data.id || '';
			transformed.shopId = data.shopId || data.shop_id || '';
			transformed.gender = data.gender || '';
			transformed.age = data.age || '';
			transformed.healthInfo = data.healthInfo || data.health_info || '';
			transformed.neuter = data.neuter || false;
			transformed.insectRepellent = data.insectRepellent || data.insect_repellent || false;
			transformed.dewormingInterval = data.dewormingInterval || data.deworming_interval || '';
			transformed.insectRepellentLastTime = data.insectRepellentLastTime || data.insect_repellent_last_time || '';
			transformed.feedAmount = data.feedAmount || data.feed_amount || '';
			transformed.feedTime = data.feedTime || data.feed_time || '';
			transformed.walkTime = data.walkTime || data.walk_time || '';
			transformed.images = this.getFirstImage(data.images || data.petImages || data.image || '');
			transformed.specialRequirements = data.specialRequirements || data.special_requirements || '';
			transformed.notes = data.notes || '';
			
			// 基础字段转换（snake_case 到 camelCase）
			for (const key in data) {
				if (data.hasOwnProperty(key) && !transformed[key.replace(/_([a-z])/g, (g) => g[1].toUpperCase())]) {
					const camelKey = key.replace(/_([a-z])/g, (g) => g[1].toUpperCase());
					transformed[camelKey] = data[key];
				}
			}
			
			return transformed;
		},

		// 获取第一张图片
		getFirstImage(images) {
			if (!images) return '';
			
			if (Array.isArray(images)) {
				return images.length > 0 ? images[0] : '';
			}
			
			if (typeof images === 'string') {
				try {
					const parsed = JSON.parse(images);
					if (Array.isArray(parsed) && parsed.length > 0) {
						return parsed[0];
					}
				} catch (e) {
					return images;
				}
			}
			
			return images;
		},

		// 格式化日期
		formatDate(dateString) {
			if (!dateString) return '';
			const date = new Date(dateString);
			const year = date.getFullYear();
			const month = String(date.getMonth() + 1).padStart(2, '0');
			const day = String(date.getDate()).padStart(2, '0');
			return `${year}-${month}-${day}`;
		},

		// 获取宠物类型名称
		getPetType(species) {
			const typeMap = {
				1: '狗狗',
				2: '猫咪',
				3: '其他'
			};
			return typeMap[species] || '未知类型';
		},

		// 获取图片URL
		getImageUrl(fileName) {
			if (!fileName) {
				return '/static/images/default-pet.png';
			}
			const url = config.getDownloadUrl(fileName);
			console.log('图片URL:', url);
			return url || '/static/images/default-pet.png';
		},

		// 处理图片加载错误
		handleImageError(e) {
			console.error('图片加载失败:', e);
			e.currentTarget.src = '/static/images/entrust01.png';
			e.currentTarget.onerror = null;
		},

		// 预览图片
		previewImage() {
			const imageUrl = this.getImageUrl(this.boardingDetail.images);
			uni.previewImage({
				current: imageUrl,
				urls: [imageUrl],
				fail: (err) => {
					console.error('预览图片失败:', err);
					uni.showToast({ title: '预览图片失败', icon: 'none' });
				}
			});
		},

		// 获取订单状态文本
		getStatusText(status) {
			const statusMap = {
				0: '待审核',
				1: '已确认，待送宠',
				2: '托养中',
				3: '已完成',
				4: '已取消',
				5: '用户已取消',
				6: '已审核(通过)',
				7: '已审核(不通过)'
			};
			return statusMap[status] || '未知状态';
		},

		// 拨打电话
		callShop() {
			if (!this.shopDetail.legalPhone) {
				uni.showToast({ title: '暂无联系电话', icon: 'none' });
				return;
			}
			
			uni.makePhoneCall({
				phoneNumber: this.shopDetail.legalPhone,
				fail: (err) => {
					console.error('拨打电话失败:', err);
					uni.showToast({ title: '拨打电话失败', icon: 'none' });
				}
			});
		},

		// 查看店铺位置
		viewShopLocation() {
			if (!this.shopDetail.shopAddress) {
				uni.showToast({ title: '暂无店铺地址', icon: 'none' });
				return;
			}
			
			// 调用地图API查看位置
			uni.openLocation({
				address: this.shopDetail.shopAddress,
				name: this.shopDetail.shopName,
				scale: 18,
				fail: (err) => {
					console.error('打开地图失败:', err);
					uni.showToast({ title: '打开地图失败', icon: 'none' });
				}
			});
		},

		// 转换店铺数据格式
		transformShopData(data) {
			const transformed = {};
			
			// 字段映射：API字段 -> 模板字段
			transformed.shopId = data.shopId || data.shop_id || data.id || '';
			transformed.shopName = data.shopName || data.shop_name || '';
			
			// 连接省份、城市、区县和详细地址
			const province = data.province || '';
			const city = data.city || '';
			const district = data.district || '';
			const address = data.address || '';
			
			// 构建完整地址
			const fullAddress = [province, city, district, address]
				.filter(Boolean) // 过滤空字符串
				.join(''); // 连接所有非空部分
			
			transformed.shopAddress = fullAddress || data.shopAddress || data.shop_address || '';
			transformed.legalPhone = data.legalPhone || data.legal_phone || data.shopPhone || data.shop_phone || '';
			transformed.shopImages = data.shopImages || data.shop_images || [];
			transformed.bussinessHours = data.bussinessHours || data.bussiness_hours || data.openingHours || data.opening_hours || '';
			transformed.description = data.description || '';
			
			return transformed;
		}
	}
};
</script>

<style lang="scss" scoped>
.care-detail-container {
	min-height: 100vh;
	background-color: #f8f9fa;

	.page-header {
		display: flex;
		align-items: center;
		padding: 20rpx 30rpx;
		background-color: #ffffff;
		box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);
		position: sticky;
		top: 0;
		z-index: 100;

		.back-icon {
			width: 60rpx;
			height: 60rpx;
			display: flex;
			justify-content: center;
			align-items: center;
			color: #6c757d;

			.iconfont {
				font-size: 36rpx;
			}
		}

		.header-title {
			font-size: 34rpx;
			font-weight: bold;
			color: #2c3e50;
			flex: 1;
			text-align: center;
		}
		
		.header-placeholder {
			width: 60rpx;
		}
	}

	.content-scroll {
		width: 100%;
		height: calc(100vh - 120rpx);
		padding: 24rpx 20rpx;
		box-sizing: border-box;
	}

	.loading-container {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 100rpx 0;

		.loading-text {
			margin-top: 20rpx;
			font-size: 28rpx;
			color: #666;
		}
	}

	.error-container {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 100rpx 40rpx;
		text-align: center;

		.error-image {
			width: 200rpx;
			height: 200rpx;
			margin-bottom: 30rpx;
			opacity: 0.6;
		}

		.error-text {
			font-size: 32rpx;
			color: #e74c3c;
			margin-bottom: 30rpx;
		}

		.retry-button {
			width: 200rpx;
			height: 80rpx;
			font-size: 28rpx;
		}
	}

	.empty-container {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding: 100rpx 40rpx;
		text-align: center;

		.empty-image {
			width: 240rpx;
			height: 240rpx;
			margin-bottom: 30rpx;
			opacity: 0.6;
		}

		.empty-text {
			font-size: 32rpx;
			color: #262626;
			font-weight: 600;
		}
	}

	.detail-content {
		width: 100%;

		.pet-image-container {
			width: 100%;
			height: 60vw;
			border-radius: 16rpx;
			overflow: hidden;
			margin-bottom: 24rpx;

			image {
				width: 100%;
				height: 100%;
				object-fit: cover;
			}
		}

		.info-card {
			background-color: #ffffff;
			border-radius: 16rpx;
			padding: 30rpx;
			margin-bottom: 24rpx;
			box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.05);

			.card-title {
				font-size: 32rpx;
				font-weight: bold;
				color: #2c3e50;
				margin-bottom: 24rpx;
				padding-bottom: 16rpx;
				border-bottom: 2rpx solid #f0f0f0;
			}

			.pet-header {
				display: flex;
				align-items: center;
				justify-content: space-between;
				margin-bottom: 16rpx;

				.pet-name {
					font-size: 36rpx;
					font-weight: bold;
					color: #2c3e50;
				}

				.status-tag {
					padding: 6rpx 20rpx;
					border-radius: 20rpx;
					font-size: 24rpx;
					font-weight: 500;
					color: #ffffff;
					
					&.status-pending {
						background: linear-gradient(135deg, #ffebee 0%, #ffcdd2 100%);
						color: #c62828;
					}
					
					&.status-confirm {
						background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
						color: #1565c0;
					}
					
					&.status-processing {
						background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
						color: #2e7d32;
					}
					
					&.status-completed {
						background: linear-gradient(135deg, #fffde7 0%, #fff9c4 100%);
						color: #f57f17;
					}
					
					&.status-canceled {
						background: linear-gradient(135deg, #f5f5f5 0%, #e0e0e0 100%);
						color: #616161;
					}
					
					&.status-user-canceled {
						background: linear-gradient(135deg, #f5f5f5 0%, #e0e0e0 100%);
						color: #616161;
					}
					
					&.status-approved {
						background: linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%);
						color: #2e7d32;
					}
					
					&.status-rejected {
						background: linear-gradient(135deg, #ffebee 0%, #ffcdd2 100%);
						color: #c62828;
					}
				}
			}

			.pet-type {
				font-size: 28rpx;
				color: #6c757d;
				margin-bottom: 8rpx;
			}

			.info-item {
				display: flex;
				margin-bottom: 20rpx;

				.info-label {
					font-size: 28rpx;
					color: #6c757d;
					width: 200rpx;
				}

				.info-value {
					font-size: 28rpx;
					color: #2c3e50;
					flex: 1;
					
					&.phone-number {
						color: #3498db;
						text-decoration: underline;
						cursor: pointer;
					}
					
					&:active {
						opacity: 0.8;
					}
				}

				.price {
					color: #e74c3c;
					font-weight: bold;
				}
			}

			.sub-section {
				margin-bottom: 24rpx;

				.sub-title {
					font-size: 30rpx;
					font-weight: 600;
					color: #495057;
					margin-bottom: 16rpx;
					display: block;
				}
			}

			.special-text {
				font-size: 28rpx;
				color: #2c3e50;
				line-height: 1.6;
				background-color: #f8f9fa;
				padding: 20rpx;
				border-radius: 12rpx;
			}

			.notes-text {
				font-size: 28rpx;
				color: #2c3e50;
				line-height: 1.6;
				background-color: #f8f9fa;
				padding: 20rpx;
				border-radius: 12rpx;
			}
		}
	}
}
</style>