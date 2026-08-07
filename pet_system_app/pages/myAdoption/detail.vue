<template>
	<view class="pet-detail-container">
		<!-- 宠物图片 -->
		<view class="pet-image-container">
			<image :src="getImageUrl(adoptionInfo.pet.images)" mode="aspectFill" class="pet-image"
				@error="handleImageError">
			</image>
		</view>

		<!-- 宠物基本信息 -->
		<view class="pet-info-container">
			<view class="pet-name-section">
				<text class="pet-name">{{ adoptionInfo.pet.name }}</text>
				<view class="pet-gender-tag" :class="genderClass">
					<text>{{ genderText }}</text>
				</view>
			</view>

			<view class="pet-basic-info">
				<text class="basic-info-item">物种：{{ speciesText }}</text>
				<text class="basic-info-item">品种：{{ adoptionInfo.pet.breed || '未知' }}</text>
				<text class="basic-info-item">年龄：{{ adoptionInfo.pet.age || '未知' }}{{ ageUnit }}</text>
			</view>

			<view class="pet-meta-info">
				<view class="health-status">
					<view class="status-item">
						<view class="status-icon">💉</view>
						<text class="status-text">{{ immunityText }}</text>
					</view>
					<view class="status-item">
						<view class="status-icon">✂️</view>
						<text class="status-text">{{ neuterText }}</text>
					</view>
					<view class="status-item">
						<view class="status-icon">🐛</view>
						<text class="status-text">{{ insectRepellentText }}</text>
					</view>
				</view>
			</view>
		</view>

		<!-- 申请信息展示区域 -->
		<view class="application-info-section">
			<view class="section-header">
				<view class="section-title-line"></view>
				<text class="section-title-text">申请信息</text>
				<view class="section-title-line"></view>
			</view>

			<view class="info-grid">
				<!-- 申请人信息 -->
				<view class="info-card">
					<view class="info-card-header">
						<view class="info-card-icon">👤</view>
						<text class="info-card-title">申请人信息</text>
					</view>
					<view class="info-content">
						<view class="info-item">
							<text class="info-label">申请人姓名：</text>
							<text class="info-value">{{ adoptionInfo.applicationName || '--' }}</text>
						</view>
						<view class="info-item">
							<text class="info-label">联系电话：</text>
							<text class="info-value phone">{{ adoptionInfo.applicationPhone || '--' }}</text>
						</view>
					</view>
				</view>

				<!-- 申请内容 -->
				<view class="info-card full-width">
					<view class="info-card-header">
						<view class="info-card-icon">📝</view>
						<text class="info-card-title">申请内容</text>
					</view>
					<view class="info-content">
						<view class="info-item">
							<text class="info-label">养宠经验：</text>
							<text class="info-value multi-line">{{ adoptionInfo.experience || '--' }}</text>
						</view>
						<view class="info-item">
							<text class="info-label">领养理由：</text>
							<text class="info-value multi-line">{{ adoptionInfo.reason || '--' }}</text>
						</view>
					</view>
				</view>

				<!-- 审核信息 -->
				<view class="info-card full-width">
					<view class="info-card-header">
						<view class="info-card-icon">✅</view>
						<text class="info-card-title">审核信息</text>
					</view>
					<view class="info-content">
						<view class="info-row">
							<view class="info-item">
								<text class="info-label">申请状态：</text>
								<text class="info-value status-badge" :class="statusClass">
									{{ statusText }}
								</text>
							</view>
							<view class="info-item">
								<text class="info-label">审核人：</text>
								<text
									class="info-value">{{ adoptionInfo.reviewerId ? '审核人ID: ' + adoptionInfo.reviewerId : '--' }}</text>
							</view>
						</view>
						<view class="info-row">
							<view class="info-item">
								<text class="info-label">审核时间：</text>
								<text class="info-value">{{ formatTime(adoptionInfo.reviewTime) || '--' }}</text>
							</view>
							<view class="info-item">
								<text class="info-label">领养时间：</text>
								<text class="info-value">{{ formatTime(adoptionInfo.adoptTime) || '--' }}</text>
							</view>
						</view>
						<view class="info-item full-width">
							<text class="info-label">审核备注：</text>
							<text class="info-value multi-line">{{ adoptionInfo.reviewRemark || '无备注' }}</text>
						</view>
					</view>
				</view>

				<!-- 系统信息 -->
				<view class="info-card full-width">
					<view class="info-card-header">
						<view class="info-card-icon">⚙️</view>
						<text class="info-card-title">系统信息</text>
					</view>
					<view class="info-content">
						<view class="info-row">
							<view class="info-item">
								<text class="info-label">申请编号：</text>
								<text class="info-value code">{{ adoptionInfo.adoptionId || '--' }}</text>
							</view>
							<view class="info-item">
								<text class="info-label">创建时间：</text>
								<text class="info-value">{{ formatTime(adoptionInfo.createTime) || '--' }}</text>
							</view>
						</view>
						<view class="info-row">
							<view class="info-item">
								<text class="info-label">创建人：</text>
								<text class="info-value">{{ adoptionInfo.createBy || '--' }}</text>
							</view>
							<view class="info-item">
								<text class="info-label">更新时间：</text>
								<text class="info-value">{{ formatTime(adoptionInfo.updateTime) || '--' }}</text>
							</view>
						</view>
						<view class="info-item">
							<text class="info-label">更新人：</text>
							<text class="info-value">{{ adoptionInfo.updateBy || '--' }}</text>
						</view>
					</view>
				</view>
			</view>
		</view>

		<!-- 宠物详细描述 -->
		<view class="pet-description-section">
			<text class="section-title">宠物介绍</text>
			<text class="pet-description">{{ adoptionInfo.pet.description || '暂无描述' }}</text>
		</view>

		<!-- 返回按钮 -->
		<view class="back-button-container">
			<button class="back-button" @click="handleBack">
				<text class="back-text">返回</text>
			</button>
		</view>
	</view>
</template>

<script>
	import {
		getPet
	} from '@/api/pet/adoption.js';
	import {
		collectPet,
		cancelCollect,
		checkCollect
	} from '@/api/pet/collect.js';
	import config from '@/config.js';

	export default {
		data() {
			return {
				petId: '',
				adoptionInfo: {},
				loading: false
			};
		},

		computed: {
			// 性别相关计算属性
			genderClass() {
				const gender = this.adoptionInfo.pet?.gender;
				const genderClassMap = {
					0: 'gender-female',
					1: 'gender-male',
					2: 'gender-unknown'
				};
				return genderClassMap[gender] || 'gender-unknown';
			},

			genderText() {
				const gender = this.adoptionInfo.pet?.gender;
				const genderMap = {
					0: '母',
					1: '公',
					2: '未知'
				};
				return genderMap[gender] || '未知';
			},

			// 物种文本
			speciesText() {
				const species = this.adoptionInfo.pet?.species;
				const speciesMap = {
					0: '猫',
					1: '狗',
					2: '其他'
				};
				return speciesMap[species] || '宠物';
			},

			// 年龄单位
			ageUnit() {
				const age = this.adoptionInfo.pet?.age;
				if (!age) return '';
				const ageNum = Number(age);
				return ageNum >= 12 ? '岁' : '个月';
			},

			// 健康状态文本
			immunityText() {
				const immunity = this.adoptionInfo.pet?.immunity;
				const immunityMap = {
					0: '已免疫',
					1: '未免疫',
					2: '免疫未知'
				};
				return immunityMap[immunity] || '免疫未知';
			},

			neuterText() {
				const neuter = this.adoptionInfo.pet?.neuter;
				const neuterMap = {
					0: '未绝育',
					1: '已绝育',
					2: '绝育未知'
				};
				return neuterMap[neuter] || '绝育未知';
			},

			insectRepellentText() {
				const insectRepellent = this.adoptionInfo.pet?.insectRepellent;
				const insectRepellentMap = {
					0: '未驱虫',
					1: '已驱虫',
					2: '驱虫未知'
				};
				return insectRepellentMap[insectRepellent] || '驱虫未知';
			},

			// 申请状态文本
			statusText() {
				const status = this.adoptionInfo.status;
				const statusMap = {
					0: '待审核',
					1: '审核通过',
					2: '审核拒绝',
					3: '已领养'
				};
				return statusMap[status] || '未知状态';
			},

			// 状态类名
			statusClass() {
				const status = this.adoptionInfo.status;
				const classMap = {
					0: 'status-pending',
					1: 'status-approved',
					2: 'status-rejected',
					3: 'status-adopted'
				};
				return classMap[status] || 'status-unknown';
			}
		},

		onLoad(options) {
			if (options.AdoptionItem) {
				try {
					// 解码 + 反序列化，还原 item 对象
					const adoptionItem = JSON.parse(decodeURIComponent(options.AdoptionItem))
					this.adoptionInfo = adoptionItem
					this.petId = this.adoptionInfo.pet?.petId || ''

					// 设置默认值，防止显示undefined
					this.setDefaultValues()
				} catch (error) {
					console.error('解析 item 失败:', error)
					uni.showToast({
						title: '数据解析失败',
						icon: 'none'
					})
					// 解析失败时返回上一页
					setTimeout(() => {
						uni.navigateBack()
					}, 1500)
				}
			} else {
				uni.showToast({
					title: '参数缺失',
					icon: 'none'
				})
				setTimeout(() => {
					uni.navigateBack()
				}, 1500)
			}
		},

		methods: {
			// 设置默认值
			setDefaultValues() {
				if (!this.adoptionInfo.pet) {
					this.adoptionInfo.pet = {}
				}

				// 确保所有需要的字段都有值
				const defaultValues = {
					name: '',
					species: 2,
					breed: '',
					age: '',
					gender: 2,
					immunity: 2,
					neuter: 2,
					insectRepellent: 2,
					description: ''
				}

				// 填充默认值
				Object.keys(defaultValues).forEach(key => {
					if (this.adoptionInfo.pet[key] === undefined) {
						this.adoptionInfo.pet[key] = defaultValues[key]
					}
				})

				// 填充申请信息的默认值
				const adoptionDefaults = {
					applicationName: '',
					applicationPhone: '',
					experience: '',
					reason: '',
					status: 0,
					reviewerId: '',
					reviewTime: '',
					reviewRemark: '',
					adoptTime: '',
					adoptionId: '',
					createTime: '',
					createBy: '',
					updateTime: '',
					updateBy: ''
				}

				Object.keys(adoptionDefaults).forEach(key => {
					if (this.adoptionInfo[key] === undefined) {
						this.adoptionInfo[key] = adoptionDefaults[key]
					}
				})
			},

			// 格式化时间
			formatTime(time) {
				if (!time) return '--'
				try {
					const date = new Date(time)
					if (isNaN(date.getTime())) return time

					return date.toLocaleString('zh-CN', {
						year: 'numeric',
						month: '2-digit',
						day: '2-digit',
						hour: '2-digit',
						minute: '2-digit'
					}).replace(/\//g, '-')
				} catch (error) {
					console.error('格式化时间失败:', error)
					return time
				}
			},

			// 返回
			handleBack() {
				uni.navigateBack()
			},

			handleImageError(e) {
				console.error('图片加载失败:', e)
				e.currentTarget.src = '/static/images/default-pet.png'
				e.currentTarget.onerror = null
			},

			getImageUrl(fileName) {
				if (!fileName) return '/static/images/default-pet.png'
				return config.getDownloadUrl ? config.getDownloadUrl(fileName) : fileName
			}
		}
	}
</script>

<style lang="scss" scoped>
	.pet-detail-container {
		position: relative;
		width: 100%;
		min-height: 100vh;
		background-color: #f5f7fa;
		padding-bottom: 120rpx;
	}

	.pet-image-container {
		width: 100%;
		height: 500rpx;
		overflow: hidden;
		background: linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%);
	}

	.pet-image {
		width: 100%;
		height: 100%;
	}

	.pet-info-container {
		padding: 30rpx;
		background-color: white;
		margin-bottom: 20rpx;
		border-radius: 20rpx;
		margin: -40rpx 20rpx 20rpx;
		box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.08);
		position: relative;
		z-index: 10;
	}

	.pet-name-section {
		display: flex;
		align-items: center;
		margin-bottom: 20rpx;
		gap: 20rpx;
	}

	.pet-name {
		font-size: 48rpx;
		font-weight: bold;
		color: #2c3e50;
	}

	.pet-gender-tag {
		padding: 8rpx 20rpx;
		border-radius: 20rpx;
		font-size: 24rpx;
		color: white;
		font-weight: 500;
	}

	.pet-gender-tag.gender-male {
		background: linear-gradient(135deg, #409eff 0%, #36a2ff 100%);
	}

	.pet-gender-tag.gender-female {
		background: linear-gradient(135deg, #f7879a 0%, #ff9a9e 100%);
	}

	.pet-gender-tag.gender-unknown {
		background: linear-gradient(135deg, #cccccc 0%, #999999 100%);
		color: #fff;
	}

	.pet-basic-info {
		display: flex;
		flex-wrap: wrap;
		gap: 15rpx;
		margin-bottom: 20rpx;
	}

	.basic-info-item {
		font-size: 28rpx;
		color: #666;
		padding: 10rpx 20rpx;
		background-color: #f8f9fa;
		border-radius: 20rpx;
		border: 1rpx solid #e9ecef;
	}

	.pet-meta-info {
		display: flex;
		flex-direction: column;
		gap: 15rpx;
	}

	.health-status {
		display: flex;
		gap: 20rpx;
		flex-wrap: wrap;
	}

	.status-item {
		display: flex;
		align-items: center;
		gap: 10rpx;
		padding: 10rpx 20rpx;
		background-color: #f8f9fa;
		border-radius: 20rpx;
		border: 1rpx solid #e9ecef;
	}

	.status-icon {
		font-size: 32rpx;
	}

	.status-text {
		font-size: 26rpx;
		color: #666;
		font-weight: 500;
	}

	/* 申请信息区域 */
	.application-info-section {
		background-color: white;
		margin: 0 20rpx 20rpx;
		border-radius: 20rpx;
		padding: 30rpx;
		box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.08);
	}

	.section-header {
		display: flex;
		align-items: center;
		justify-content: center;
		margin-bottom: 40rpx;
	}

	.section-title-line {
		flex: 1;
		height: 2rpx;
		background: linear-gradient(90deg, transparent, #667eea, transparent);
	}

	.section-title-text {
		font-size: 36rpx;
		font-weight: bold;
		color: #333;
		margin: 0 30rpx;
		white-space: nowrap;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		-webkit-background-clip: text;
		-webkit-text-fill-color: transparent;
		background-clip: text;
	}

	.info-grid {
		display: flex;
		flex-direction: column;
		gap: 30rpx;
	}

	.info-card {
		background: #f8f9fa;
		border-radius: 16rpx;
		padding: 30rpx;
		border: 1rpx solid #e9ecef;
	}

	.info-card.full-width {
		width: 100%;
	}

	.info-card-header {
		display: flex;
		align-items: center;
		margin-bottom: 25rpx;
		border-bottom: 2rpx solid #e9ecef;
		padding-bottom: 15rpx;
	}

	.info-card-icon {
		font-size: 32rpx;
		margin-right: 15rpx;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		-webkit-background-clip: text;
		-webkit-text-fill-color: transparent;
		background-clip: text;
	}

	.info-card-title {
		font-size: 32rpx;
		font-weight: bold;
		color: #333;
	}

	.info-content {
		display: flex;
		flex-direction: column;
		gap: 20rpx;
	}

	.info-row {
		display: flex;
		flex-wrap: wrap;
		gap: 30rpx;
	}

	.info-item {
		flex: 1;
		min-width: 300rpx;
	}

	.info-item.full-width {
		width: 100%;
		flex: none;
	}

	.info-label {
		font-size: 28rpx;
		color: #666;
		font-weight: 500;
		margin-bottom: 8rpx;
		display: block;
	}

	.info-value {
		font-size: 28rpx;
		color: #333;
		font-weight: 600;
		display: block;
	}

	.info-value.phone {
		color: #1890ff;
		font-family: 'Courier New', monospace;
	}

	.info-value.code {
		color: #722ed1;
		font-family: 'Courier New', monospace;
		background: rgba(114, 46, 209, 0.1);
		padding: 6rpx 12rpx;
		border-radius: 8rpx;
		display: inline-block;
	}

	.info-value.multi-line {
		white-space: pre-wrap;
		word-break: break-word;
		line-height: 1.6;
		background: rgba(0, 0, 0, 0.02);
		padding: 15rpx;
		border-radius: 12rpx;
		border-left: 4rpx solid #667eea;
		margin-top: 8rpx;
	}

	/* 状态徽章 */
	.status-badge {
		display: inline-block;
		padding: 8rpx 20rpx;
		border-radius: 20rpx;
		font-size: 26rpx;
		font-weight: 600;
		text-align: center;
	}

	.status-badge.status-pending {
		color: #ff9800;
		background-color: rgba(255, 152, 0, 0.1);
		border: 1rpx solid rgba(255, 152, 0, 0.3);
	}

	.status-badge.status-approved {
		color: #4caf50;
		background-color: rgba(76, 175, 80, 0.1);
		border: 1rpx solid rgba(76, 175, 80, 0.3);
	}

	.status-badge.status-rejected {
		color: #f44336;
		background-color: rgba(244, 67, 54, 0.1);
		border: 1rpx solid rgba(244, 67, 54, 0.3);
	}

	.status-badge.status-adopted {
		color: #2196f3;
		background-color: rgba(33, 150, 243, 0.1);
		border: 1rpx solid rgba(33, 150, 243, 0.3);
	}

	.status-badge.status-unknown {
		color: #999;
		background-color: rgba(153, 153, 153, 0.1);
		border: 1rpx solid rgba(153, 153, 153, 0.3);
	}

	/* 宠物详细描述 */
	.pet-description-section {
		background-color: white;
		margin: 0 20rpx 20rpx;
		border-radius: 20rpx;
		padding: 30rpx;
		box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.08);
	}

	.section-title {
		font-size: 36rpx;
		font-weight: bold;
		margin-bottom: 25rpx;
		display: block;
		color: #333;
		border-left: 6rpx solid #667eea;
		padding-left: 20rpx;
	}

	.pet-description {
		font-size: 30rpx;
		color: #333;
		line-height: 1.8;
		white-space: pre-line;
	}

	/* 返回按钮 */
	.back-button-container {
		margin: 40rpx 20rpx;
	}

	.back-button {
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		height: 90rpx;
		border-radius: 45rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		color: #fff;
		font-size: 32rpx;
		font-weight: 600;
		border: none;
		box-shadow: 0 8rpx 20rpx rgba(102, 126, 234, 0.3);
		transition: all 0.3s ease;
	}

	.back-button:active {
		transform: translateY(2rpx);
		box-shadow: 0 4rpx 10rpx rgba(102, 126, 234, 0.3);
	}

	.back-text {
		margin-right: 10rpx;
	}
</style>