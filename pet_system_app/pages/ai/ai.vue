<template>
	<view class="wrap">
		<!-- 内容区域 -->
		<scroll-view class="containerBox" scroll-y="true" :scroll-top="scrollTop" scroll-with-animation
			id="message-scroll">
			<!-- AI消息卡片 -->
			<view class="message-card">
				<!-- 机器人消息 -->
				<view class="message-wrapper robot">
					<view class="ai-header">
						<image class="avatar" src="/static/jimeng-2026-04-..png"></image>
						<text class="ai-name">小缘</text>
					</view>
					<view class="message-content">
						<!-- 加载状态 -->
						<view v-if="isLoading" class="loading-wrapper">
							<view class="loading-dots">
								<view class="dot"></view>
								<view class="dot"></view>
								<view class="dot"></view>
							</view>
							<text class="loading-text">正在思考...</text>
						</view>
						<!-- 打字效果 -->
						<template v-else>
							<rich-text :nodes="formatMessage(displayMessage)"></rich-text>
							<text v-if="isTyping" class="cursor">|</text>
						</template>
					</view>
				</view>

				<!-- 宠物推荐卡片 - 打字完成后才显示 -->
				<view v-if="showPetCard && currentPet" class="pet-recommend-card" @click="navigateToDetail">
					<!-- 左侧宠物图片 -->
					<view class="pet-card-image-wrapper">
						<image :src="getImageUrl(currentPet.images)" mode="aspectFill" class="pet-card-image"
							@error="handleImageError"></image>
						<!-- 物种标签 -->
						<view class="species-tag">
							{{ getSpeciesText(currentPet.species) }}
						</view>
					</view>

					<!-- 右侧宠物信息 -->
					<view class="pet-card-info">
						<!-- 第一行：名称和年龄 -->
						<view class="pet-card-header">
							<text class="pet-card-name">{{ currentPet.name || '未命名' }}</text>
							<text class="pet-card-age">{{ currentPet.age || '?' }}个月</text>
						</view>

						<!-- 品种 -->
						<view v-if="currentPet.breed" class="pet-card-breed">
							<text>{{ currentPet.breed }}</text>
						</view>

						<!-- 健康状态 -->
						<view class="pet-card-health">
							<view class="health-item">
								<text class="health-icon">💉</text>
								<text class="health-text">{{ getImmunityText(currentPet.immunity) }}</text>
							</view>
							<view class="health-item">
								<text class="health-icon">✂️</text>
								<text class="health-text">{{ getNeuterText(currentPet.neuter) }}</text>
							</view>
							<view class="health-item">
								<text class="health-icon">🐛</text>
								<text
									class="health-text">{{ getInsectRepellentText(currentPet.insectRepellent) }}</text>
							</view>
						</view>

						<!-- 查看详情提示 -->
						<view class="view-detail-hint">
							<text>点击查看详情</text>
							<text class="arrow">›</text>
						</view>
					</view>
				</view>

				<!-- 刷新按钮 -->
				<view class="refresh-wrapper">
					<button type="primary" @tap="nextPet" class="refresh-btn" :disabled="isLoading || isTyping"
						:class="{'spinning': isLoading}">
						<text class="iconfont icon-refresh"></text>
					</button>
				</view>
			</view>
			<!-- 用于触发滚动的锚点 -->
			<view id="anchor"></view>
		</scroll-view>
	</view>
</template>

<script>
	import {
		listPet
	} from '@/api/pet/adoption.js';
	import config from '@/config.js';

	export default {
		data() {
			return {
				currentMessage: '',
				displayMessage: '',
				isTyping: false,
				isLoading: false,
				typingTimer: null,
				scrollTop: 0,
				systemPrompt: "你是一个热情的宠物推荐助手，名字叫小缘。你接收宠物信息后，直接用简短温暖的语言向用户推荐，突出宠物亮点，像朋友聊天一样自然。不要自我介绍，开头直接推荐。回复字数控制在100字以内。",
				apiKey: 'sk-9867cf8f9ade4ff88740e7e635bada40',
				petList: [],
				currentPet: null,
				currentPetIndex: 0,
				loading: false,
				showPetCard: false // 控制宠物卡片显示
			};
		},
		onUnmounted() {
			if (this.typingTimer) {
				clearInterval(this.typingTimer)
			}
		},
		onLoad() {
			console.log('AI推荐页面加载');
			this.loadPetList();
		},
		methods: {
			async loadPetList() {
				if (this.loading) return

				this.loading = true
				try {
					const params = {
						pageNum: 1,
						pageSize: 100,
						status: 0
					}

					const res = await listPet(params)
					if (res.code === 200) {
						const data = res.rows || res.data || []
						this.petList = data

						if (this.petList.length > 0) {
							this.$nextTick(() => {
								this.recommendRandomPet()
							})
						} else {
							this.displayMessage = '暂时没有可推荐的宠物信息'
						}
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
				}
			},

			nextPet() {
				if (this.isLoading || this.isTyping) return
				this.displayMessage = ''
				this.showPetCard = false // 隐藏宠物卡片
				this.currentPet = null
				this.recommendRandomPet()
			},

			async recommendRandomPet() {
				if (this.petList.length === 0) {
					this.displayMessage = '暂无宠物可推荐'
					return
				}

				this.isLoading = true
				this.isTyping = false
				this.showPetCard = false
				this.currentPet = null

				const randomIndex = Math.floor(Math.random() * this.petList.length)
				const randomPet = this.petList[randomIndex]
				this.currentPet = randomPet
				const petInfo = this.formatPetInfo(randomPet)

				try {
					const messagesForAPI = [{
						role: "system",
						content: this.systemPrompt
					}, {
						role: "user",
						content: `请为用户推荐这只宠物：${petInfo}`
					}]

					const response = await this.makeRequest(messagesForAPI)

					if (response && response.choices && response.choices.length > 0) {
						this.currentMessage = response.choices[0].message.content
						this.isLoading = false
						this.startTyping()
					} else {
						throw new Error('API返回格式异常')
					}
				} catch (error) {
					console.error('API调用失败:', error)

					let errorMsg = '请求失败，请稍后重试'
					if (error.message.includes('401')) {
						errorMsg = 'API密钥错误，请检查配置'
					} else if (error.message.includes('network')) {
						errorMsg = '网络连接失败，请检查网络'
					}

					uni.showToast({
						title: errorMsg,
						icon: 'none',
						duration: 3000
					})

					this.isLoading = false
					this.displayMessage = `抱歉，我遇到了一些问题：${errorMsg}`
				}
			},

			startTyping() {
				this.displayMessage = ''
				this.isTyping = true
				this.showPetCard = false // 开始打字时隐藏卡片
				let index = 0
				const text = this.currentMessage

				if (this.typingTimer) {
					clearInterval(this.typingTimer)
				}

				this.typingTimer = setInterval(() => {
					if (index < text.length) {
						this.displayMessage += text[index]
						index++
						this.$nextTick(() => {
							this.scrollToBottom()
						})
					} else {
						clearInterval(this.typingTimer)
						this.isTyping = false
						this.showPetCard = true // 打字完成后显示宠物卡片
						this.$nextTick(() => {
							this.scrollToBottom()
						})
					}
				}, 50)
			},

			formatPetInfo(pet) {
				const genderMap = {
					0: '母',
					1: '公',
					2: '未知'
				}
				const speciesMap = {
					0: '猫',
					1: '狗',
					2: '其他'
				}
				const immunityMap = {
					0: '已免疫',
					1: '未免疫',
					2: '免疫未知'
				}
				const neuterMap = {
					0: '未绝育',
					1: '已绝育',
					2: '绝育未知'
				}
				const insectMap = {
					0: '已驱虫',
					1: '未驱虫',
					2: '驱虫未知'
				}

				let info = `名字：${pet.name || '未知'}\n`
				info += `物种：${speciesMap[pet.species] || '宠物'}\n`
				info += `品种：${pet.breed || '未知'}\n`
				info += `性别：${genderMap[pet.gender] || '未知'}\n`
				info += `年龄：${pet.age || '未知'}\n`
				info += `免疫状态：${immunityMap[pet.immunity] || '未知'}\n`
				info += `绝育状态：${neuterMap[pet.neuter] || '未知'}\n`
				info += `驱虫状态：${insectMap[pet.insectRepellent] || '未知'}\n`
				info += `介绍：${pet.description || '暂无介绍'}`

				return info
			},

			makeRequest(messages) {
				return new Promise((resolve, reject) => {
					uni.request({
						url: 'https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions',
						method: 'POST',
						header: {
							'Authorization': `Bearer ${this.apiKey}`,
							'Content-Type': 'application/json'
						},
						data: {
							model: "qwen-plus",
							messages: messages,
							temperature: 0.8,
							max_tokens: 200
						},
						success: (res) => {
							console.log('API响应:', res)

							if (res.statusCode === 200) {
								resolve(res.data)
							} else {
								reject(new Error(
									`HTTP ${res.statusCode}: ${JSON.stringify(res.data)}`))
							}
						},
						fail: (err) => {
							console.error('请求失败:', err)
							reject(new Error(`网络请求失败: ${err.errMsg || '未知错误'}`))
						}
					})
				})
			},

			formatMessage(text) {
				if (!text) return ''

				let cleanedText = text
				cleanedText = cleanedText.replace(/\*\*/g, '')
				cleanedText = cleanedText.replace(/---+/g, '')
				cleanedText = cleanedText.replace(/___+/g, '')
				cleanedText = cleanedText.replace(/===+/g, '')
				cleanedText = cleanedText.replace(/#{1,6}\s*/g, '')
				cleanedText = cleanedText.replace(/`{1,3}/g, '')
				cleanedText = cleanedText.replace(/\n{3,}/g, '\n\n')

				let formatted = cleanedText.replace(/\n/g, '<br>')

				formatted = formatted.replace(/(\d+\.)\s+(.+?)(?=<br>|$)/g, (match, p1, p2) => {
					return `<div style="margin: 5px 0 5px 10px;">${p1} ${p2}</div>`
				})

				formatted = formatted.replace(/([-•*])\s+(.+?)(?=<br>|$)/g, (match, p1, p2) => {
					return `<div style="margin: 5px 0 5px 10px;">${p1} ${p2}</div>`
				})

				formatted = `<div style="font-size: 14px; line-height: 1.6;">${formatted}</div>`

				return formatted
			},

			stripHTML(html) {
				if (!html) return ''
				return html.replace(/<[^>]+>/g, '')
			},

			scrollToBottom() {
				this.$nextTick(() => {
					setTimeout(() => {
						this.scrollTop = 999999
					}, 100)
				})
			},

			copyMessage(message) {
				const plainText = this.stripHTML(message)
				uni.setClipboardData({
					data: plainText,
					success: () => {
						uni.showToast({
							title: '复制成功',
							icon: 'success'
						})
					}
				})
			},

			navigateToDetail() {
				if (this.currentPet && this.currentPet.petId) {
					uni.navigateTo({
						url: `/pages/adoption/detail?id=${this.currentPet.petId}`
					})
				}
			},

			getImageUrl(fileName) {
				if (!fileName) return '/static/images/default-pet.png'
				return config.getDownloadUrl(fileName)
			},

			handleImageError(e) {
				e.currentTarget.src = '/static/images/default-pet.png'
				e.currentTarget.onerror = null
			},

			getSpeciesText(species) {
				const speciesMap = {
					0: '猫',
					1: '狗',
					2: '其他'
				}
				return speciesMap[species] || '宠物'
			},

			getImmunityText(immunity) {
				const immunityMap = {
					0: '已免疫',
					1: '未免疫',
					2: '免疫'
				}
				return immunityMap[immunity] || '免疫'
			},

			getNeuterText(neuter) {
				const neuterMap = {
					0: '未绝育',
					1: '已绝育',
					2: '绝育'
				}
				return neuterMap[neuter] || '绝育'
			},

			getInsectRepellentText(insectRepellent) {
				const insectRepellentMap = {
					0: '未驱虫',
					1: '已驱虫',
					2: '驱虫'
				}
				return insectRepellentMap[insectRepellent] || '驱虫'
			}
		}
	};
</script>

<style scoped>
	.wrap {
		display: flex;
		flex-direction: column;
		height: 100vh;
		background-color: #f8f8f8;
	}

	.containerBox {
		flex: 1;
		overflow-y: auto;
		padding: 20rpx;
		padding-bottom: 20rpx;
		box-sizing: border-box;
	}

	.message-card {
		position: relative;
		background-color: #ffffff;
		border-radius: 20rpx;
		padding: 30rpx;
		padding-bottom: 120rpx;
		box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);
		overflow: hidden;
	}

	.message-wrapper {
		display: flex;
		flex-direction: column;
		align-items: flex-start;
	}

	.robot {
		justify-content: flex-start;
	}

	.ai-header {
		display: flex;
		align-items: center;
		margin-bottom: 16rpx;
	}

	.avatar {
		width: 80rpx;
		height: 80rpx;
		border-radius: 50%;
		flex-shrink: 0;
		margin-right: 20rpx;
	}

	.ai-name {
		font-size: 28rpx;
		font-weight: bold;
		color: #667eea;
		margin-right: 16rpx;
		align-self: center;
	}

	.message-content {
		flex: 1;
		font-size: 28rpx;
		line-height: 1.6;
		color: #333333;
		word-break: break-word;
		padding-top: 10rpx;
	}

	/* 宠物推荐卡片 */
	.pet-recommend-card {
		display: flex;
		margin-top: 30rpx;
		padding: 24rpx;
		background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
		border-radius: 20rpx;
		border: 2rpx solid rgba(255, 107, 53, 0.2);
		transition: all 0.3s ease;
		animation: fadeInUp 0.3s ease-out;
	}

	@keyframes fadeInUp {
		from {
			opacity: 0;
			transform: translateY(20rpx);
		}

		to {
			opacity: 1;
			transform: translateY(0);
		}
	}

	.pet-recommend-card:active {
		transform: scale(0.98);
		background: linear-gradient(135deg, #fff 0%, #f8f9fa 100%);
	}

	/* 左侧图片 */
	.pet-card-image-wrapper {
		position: relative;
		width: 180rpx;
		height: 180rpx;
		border-radius: 16rpx;
		overflow: hidden;
		flex-shrink: 0;
		background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
		box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);
	}

	.pet-card-image {
		width: 100%;
		height: 100%;
		object-fit: cover;
	}

	.species-tag {
		position: absolute;
		top: 10rpx;
		left: 10rpx;
		padding: 4rpx 12rpx;
		background: rgba(255, 255, 255, 0.95);
		border-radius: 16rpx;
		font-size: 20rpx;
		color: #ff6b35;
		font-weight: 600;
		backdrop-filter: blur(10px);
	}

	/* 右侧信息 */
	.pet-card-info {
		flex: 1;
		margin-left: 20rpx;
		display: flex;
		flex-direction: column;
		justify-content: space-between;
	}

	.pet-card-header {
		display: flex;
		justify-content: space-between;
		align-items: center;
		gap: 16rpx;
	}

	.pet-card-name {
		font-size: 32rpx;
		font-weight: bold;
		color: #2c3e50;
		flex: 1;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.pet-card-age {
		font-size: 24rpx;
		color: #ff6b35;
		font-weight: 600;
		background: rgba(255, 107, 53, 0.1);
		padding: 6rpx 14rpx;
		border-radius: 12rpx;
		white-space: nowrap;
	}

	.pet-card-breed {
		font-size: 22rpx;
		color: #666;
		background: rgba(102, 102, 102, 0.1);
		padding: 4rpx 12rpx;
		border-radius: 8rpx;
		align-self: flex-start;
		margin-top: 8rpx;
	}

	/* 健康状态 */
	.pet-card-health {
		display: flex;
		gap: 12rpx;
		margin-top: 12rpx;
		flex-wrap: wrap;
	}

	.health-item {
		display: flex;
		align-items: center;
		gap: 4rpx;
		padding: 6rpx 12rpx;
		background: rgba(255, 255, 255, 0.8);
		border-radius: 10rpx;
		backdrop-filter: blur(10px);
	}

	.health-icon {
		font-size: 20rpx;
	}

	.health-text {
		font-size: 20rpx;
		color: #666;
		font-weight: 500;
	}

	/* 查看详情提示 */
	.view-detail-hint {
		display: flex;
		align-items: center;
		gap: 4rpx;
		margin-top: 12rpx;
		font-size: 22rpx;
		color: #ff6b35;
		font-weight: 500;
	}

	.arrow {
		font-size: 28rpx;
		font-weight: bold;
	}

	.refresh-wrapper {
		position: absolute;
		bottom: 20rpx;
		right: 20rpx;
	}

	.refresh-btn {
		width: 80rpx;
		height: 80rpx;
		border-radius: 50%;
		background-color: #07c160;
		color: white;
		display: flex;
		align-items: center;
		justify-content: center;
		margin: 0;
		padding: 0;
		border: none;
	}

	.refresh-btn[disabled] {
		background-color: #cccccc;
	}

	.refresh-btn .iconfont {
		font-size: 36rpx;
		color: white;
	}

	.refresh-btn.spinning .iconfont {
		animation: spin 1s linear infinite;
	}

	@keyframes spin {
		from {
			transform: rotate(0deg);
		}

		to {
			transform: rotate(360deg);
		}
	}

	.loading-wrapper {
		display: flex;
		align-items: center;
		gap: 10rpx;
	}

	.loading-dots {
		display: flex;
		align-items: center;
		gap: 8rpx;
	}

	.dot {
		width: 12rpx;
		height: 12rpx;
		border-radius: 50%;
		background-color: #07c160;
		animation: dotBounce 1.4s infinite ease-in-out;
	}

	.dot:nth-child(1) {
		animation-delay: -0.32s;
	}

	.dot:nth-child(2) {
		animation-delay: -0.16s;
	}

	.dot:nth-child(3) {
		animation-delay: 0s;
	}

	@keyframes dotBounce {

		0%,
		80%,
		100% {
			transform: scale(0.6);
			opacity: 0.5;
		}

		40% {
			transform: scale(1);
			opacity: 1;
		}
	}

	.loading-text {
		font-size: 26rpx;
		color: #999999;
	}

	.cursor {
		display: inline-block;
		animation: blink 1s infinite;
		color: #999999;
	}

	@keyframes blink {

		0%,
		50% {
			opacity: 1;
		}

		51%,
		100% {
			opacity: 0;
		}
	}

	#anchor {
		height: 1rpx;
		width: 100%;
	}
</style>