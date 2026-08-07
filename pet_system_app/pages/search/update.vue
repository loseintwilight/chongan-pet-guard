<template>
  <view class="update-container">
    <!-- 顶部导航栏 -->
    <view class="nav-bar">
      <view class="nav-title">编辑丢失信息</view>
      <view class="nav-placeholder"></view>
    </view>

    <!-- 内容区域 -->
    <scroll-view class="content-scroll" scroll-y>
      <!-- 加载中状态 -->
      <view v-if="loading" class="loading-container">
        <uni-spinner type="scale"></uni-spinner>
        <text class="loading-text">加载中...</text>
      </view>

      <!-- 错误提示 -->
      <view v-else-if="error" class="error-container">
        <text class="error-text">加载失败，请稍后重试</text>
        <button class="retry-btn" @click="loadPetDetail">重试</button>
      </view>

      <!-- 编辑表单 -->
      <view v-else class="form-container">
        <!-- 宠物图片 -->
        <view class="image-section">
          <text class="section-label">宠物图片</text>
          <view class="image-upload">
            <image v-if="petDetail.images" :src="getImageUrl(petDetail.images)" mode="aspectFill" class="preview-image" @tap="previewImage"></image>
            <view v-else class="upload-placeholder">
              <text class="upload-icon">+</text>
              <text class="upload-text">上传图片</text>
            </view>
            <button class="upload-btn" @click="chooseImage">选择图片</button>
          </view>
        </view>

        <!-- 宠物基本信息 -->
        <view class="form-section">
          <!-- 宠物名称 -->
          <view class="form-item">
            <text class="form-label">宠物名称 *</text>
            <input 
              v-model="petDetail.petName" 
              type="text" 
              placeholder="请输入宠物名称" 
              class="form-input"
            />
          </view>

          <!-- 宠物种类 -->
          <view class="form-item">
            <text class="form-label">宠物种类 *</text>
            <picker v-model="petDetail.species" :range="speciesOptions" class="form-picker">
              <view class="picker-display">
                {{ speciesOptions[petDetail.species] || '请选择种类' }}
              </view>
            </picker>
          </view>

          <!-- 宠物品种 -->
          <view class="form-item">
            <text class="form-label">宠物品种</text>
            <input 
              v-model="petDetail.breed" 
              type="text" 
              placeholder="请输入宠物品种" 
              class="form-input"
            />
          </view>

          <!-- 丢失时间 -->
          <view class="form-item">
            <text class="form-label">丢失时间 *</text>
            <picker 
              mode="date" 
              v-model="petDetail.lostTime" 
              start="2000-01-01" 
              end="2100-12-31" 
              class="form-picker"
            >
              <view class="picker-display">
                {{ formatDate(petDetail.lostTime) || '请选择丢失时间' }}
              </view>
            </picker>
          </view>

          <!-- 丢失地点 -->
          <view class="form-item">
            <text class="form-label">丢失地点 *</text>
            <input 
              v-model="petDetail.lostLocation" 
              type="text" 
              placeholder="请输入丢失地点" 
              class="form-input"
            />
          </view>

          <!-- 特征描述 -->
          <view class="form-item">
            <text class="form-label">特征描述</text>
            <textarea 
              v-model="petDetail.features" 
              placeholder="请描述宠物的特征" 
              class="form-textarea"
              auto-height
            ></textarea>
          </view>

          <!-- 想说的话 -->
          <view class="form-item">
            <text class="form-label">想说的话</text>
            <textarea 
              v-model="petDetail.message" 
              placeholder="请输入您想说的话" 
              class="form-textarea"
              auto-height
            ></textarea>
          </view>

          <!-- 酬谢金额 -->
          <view class="form-item">
            <text class="form-label">酬谢金额</text>
            <input 
              v-model.number="petDetail.reward" 
              type="number" 
              placeholder="请输入酬谢金额（选填）" 
              class="form-input"
            />
          </view>

          <!-- 宠物状态 -->
          <view class="form-item">
            <text class="form-label">宠物状态 *</text>
            <picker v-model="petDetail.status" :range="statusOptions" class="form-picker">
              <view class="picker-display">
                {{ statusOptions[petDetail.status] || '请选择状态' }}
              </view>
            </picker>
          </view>
        </view>

        <!-- 提交按钮 -->
        <view class="submit-container">
          <button 
            class="submit-btn" 
            @click="submitForm"
            :disabled="submitting"
          >
            保存修改
          </button>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { getLostPet, updateLostPet } from '@/api/pet/lostPet.js';
import config from '@/config.js';
import { getToken } from '@/utils/auth';

export default {
  data() {
    return {
      lostId: '',
      loading: true,
      error: false,
      submitting: false,
      petDetail: {
        lostId: '',
        petName: '',
        species: 0,
        breed: '',
        features: '',
        lostLocation: '',
        lostTime: '',
        reward: 0,
        images: '',
        status: 0,
        message: ''
      },
      speciesOptions: ['猫', '狗', '其他'],
      statusOptions: ['寻找中', '已找到', '已结束']
    };
  },

  onLoad(options) {
    this.lostId = options.lostId;
    if (this.lostId) {
      this.loadPetDetail();
    } else {
      this.loading = false;
      this.error = true;
    }
  },

  methods: {
    /** 加载宠物详情 */
    loadPetDetail() {
      if (!this.lostId) return;

      this.loading = true;
      this.error = false;

      uni.showLoading({ title: '加载中...', mask: true });

      getLostPet(this.lostId).then(response => {
        if (response.code === 200) {
          const data = response.data;
          console.log('获取到的详细数据:', data);
          this.petDetail = {
            lostId: data.lost_id || data.lostId || '',
            petName: data.pet_name || data.petName || '',
            species: data.species || 0,
            breed: data.breed || '',
            features: data.features || '',
            lostLocation: data.lost_location || data.lostLocation || '',
            lostTime: this.formatDateForPicker(data.lost_time || data.lostTime || ''),
            reward: data.reward || 0,
            images: data.images || '',
            status: data.status || 0,
            message: data.message || '',
            // 添加后端可能需要的必填字段
            userId: data.user_id || data.userId || '',
            ownerName: data.owner_name || data.ownerName || '',
            ownerPhone: data.owner_phone || data.ownerPhone || ''
          };
        } else {
          this.error = true;
          uni.showToast({
            title: response.msg || '获取数据失败',
            icon: 'none',
            duration: 2000
          });
        }
      }).catch(error => {
        this.error = true;
        uni.showToast({
          title: error.message || '获取数据失败',
          icon: 'none',
          duration: 2000
        });
      }).finally(() => {
        this.loading = false;
        uni.hideLoading();
      });
    },

    /** 格式化日期为picker使用的格式 */
    formatDateForPicker(dateStr) {
      if (!dateStr) return '';
      try {
        if (typeof dateStr === 'number') {
          dateStr = new Date(dateStr);
        }
        
        const date = new Date(dateStr);
        if (isNaN(date.getTime())) return dateStr;

        const year = date.getFullYear();
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        return `${year}-${month}-${day}`;
      } catch (error) {
        return dateStr;
      }
    },

    /** 格式化日期显示 */
    formatDate(dateStr) {
      if (!dateStr) return '';
      return this.formatDateForPicker(dateStr);
    },

    /** 获取图片URL */
    getImageUrl(fileName) {
      if (!fileName) {
        return '/static/images/default-pet.png';
      }
      return config.getDownloadUrl(fileName) || '/static/images/default-pet.png';
    },

    /** 选择图片 */
    chooseImage() {
      uni.chooseImage({
        count: 1,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          const tempFilePath = res.tempFilePaths[0];
          this.uploadImage(tempFilePath);
        }
      });
    },

    /** 上传图片 */
    uploadImage(tempFilePath) {
      uni.showLoading({ title: '上传中...', mask: true });
      
      uni.uploadFile({
        url: config.baseUrl + '/common/upload',
        filePath: tempFilePath,
        name: 'file',
        header: {
          'Authorization': 'Bearer ' + getToken()
        },
        success: (res) => {
          console.log('上传响应:', res);
          try {
            // 检查响应状态码
            if (res.statusCode !== 200) {
              throw new Error(`服务器错误，状态码: ${res.statusCode}`);
            }
            
            // 尝试解析响应数据
            const data = JSON.parse(res.data);
            console.log('解析后的数据:', data);
            
            if (data.code === 200) {
              // 上传成功
              this.petDetail.images = data.data?.fileName || data.data?.url || data.fileName || data.url || '';
              if (this.petDetail.images) {
                uni.showToast({
                  title: '图片上传成功',
                  icon: 'success'
                });
              } else {
                throw new Error('上传成功但未返回图片地址');
              }
            } else {
              // 后端返回错误
              throw new Error(data.msg || '上传失败');
            }
          } catch (error) {
            console.error('图片上传失败:', error);
            console.error('原始响应数据:', res.data);
            uni.showToast({
              title: error.message || '图片上传失败，请重试',
              icon: 'none'
            });
          }
        },
        fail: (error) => {
          console.error('图片上传失败（网络/客户端错误）:', error);
          uni.showToast({
            title: error.errMsg || '图片上传失败，请重试',
            icon: 'none'
          });
        },
        complete: () => {
          uni.hideLoading();
        }
      });
    },
    
    /** 预览图片 */
    previewImage() {
      if (this.petDetail.images) {
        const imageUrl = this.getImageUrl(this.petDetail.images);
        uni.previewImage({
          urls: [imageUrl],
          current: 0
        });
      }
    },

    /** 表单验证 */
    validateForm() {
      if (!this.petDetail.petName || !this.petDetail.petName.trim()) {
        uni.showToast({
          title: '请输入宠物名称',
          icon: 'none',
          duration: 2000
        });
        return false;
      }

      if (!this.petDetail.lostLocation || !this.petDetail.lostLocation.trim()) {
        uni.showToast({
          title: '请输入丢失地点',
          icon: 'none',
          duration: 2000
        });
        return false;
      }

      if (!this.petDetail.lostTime) {
        uni.showToast({
          title: '请选择丢失时间',
          icon: 'none',
          duration: 2000
        });
        return false;
      }

      return true;
    },

    /** 准备提交数据 */
    prepareSubmitData() {
      const submitData = {
        lostId: Number(this.lostId),
        petName: (this.petDetail.petName || '').trim(),
        species: Number(this.petDetail.species) || 0,
        breed: (this.petDetail.breed || '').trim(),
        features: (this.petDetail.features || '').trim(),
        lostLocation: (this.petDetail.lostLocation || '').trim(),
        lostTime: this.formatDateForBackend(this.petDetail.lostTime),
        reward: Number(this.petDetail.reward) || 0,
        images: this.petDetail.images || '',
        status: Number(this.petDetail.status) || 0,
        message: (this.petDetail.message || '').trim(),
        // 添加后端可能需要的必填字段
        userId: Number(this.petDetail.userId) || 0,
        ownerName: (this.petDetail.ownerName || '').trim(),
        ownerPhone: (this.petDetail.ownerPhone || '').trim()
      };

      Object.keys(submitData).forEach(key => {
        if (submitData[key] === null || submitData[key] === undefined) {
          submitData[key] = '';
        }
      });

      console.log('准备提交的数据:', submitData);
      return submitData;
    },

    /** 格式化日期为后端使用的格式 */
    formatDateForBackend(dateStr) {
      if (!dateStr) return '';
      try {
        const date = new Date(dateStr);
        if (isNaN(date.getTime())) return dateStr;
        
        // 格式化日期为 YYYY-MM-DD 格式，与后端实体类中@JsonFormat(pattern = "yyyy-MM-dd")匹配
        const year = date.getFullYear();
        const month = (date.getMonth() + 1).toString().padStart(2, '0');
        const day = date.getDate().toString().padStart(2, '0');
        
        return `${year}-${month}-${day}`;
      } catch (error) {
        return dateStr;
      }
    },

    /** 提交表单 */
    submitForm() {
      if (!this.validateForm() || this.submitting) return;

      const submitData = this.prepareSubmitData();

      if (!submitData.lostId || isNaN(submitData.lostId)) {
        uni.showToast({
          title: '丢失ID无效',
          icon: 'none',
          duration: 3000
        });
        return;
      }

      console.log('开始提交数据，使用导入的API函数');
      this.submitting = true;
      uni.showLoading({ title: '提交中...', mask: true });

      // 使用导入的API函数调用
      updateLostPet(submitData).then(response => {
        console.log('API响应结果:', response);
        if (response.code === 200) {
          uni.showToast({
            title: '保存成功',
            icon: 'success',
            duration: 1500
          });

          setTimeout(() => {
            uni.navigateBack();
          }, 1500);
        } else {
          console.log('API返回错误:', response.msg);
          uni.showToast({
            title: response.msg || '保存失败，请检查数据',
            icon: 'none',
            duration: 3000
          });
        }
      }).catch(error => {
        console.log('API调用失败:', error);
        uni.showToast({
          title: error.msg || error.errMsg || '网络错误，请重试',
          icon: 'none',
          duration: 3000
        });
      }).finally(() => {
        this.submitting = false;
        uni.hideLoading();
      });
    }
  }
};
</script>

<style scoped>
.update-container {
  background-color: #f5f5f5;
  min-height: 100vh;
}

/* 导航栏 */
.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 30rpx;
  background-color: #ffffff;
  border-bottom: 1rpx solid #e0e0e0;
  position: relative;
}

.nav-back {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.nav-back .icon {
  font-size: 36rpx;
  color: #333333;
}

.nav-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333333;
  flex: 1;
  text-align: center;
}

.nav-placeholder {
  width: 60rpx;
}

/* 内容区域 */
.content-scroll {
  height: calc(100vh - 100rpx);
}

/* 加载中状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.loading-text {
  margin-top: 20rpx;
  font-size: 28rpx;
  color: #666666;
}

/* 错误提示 */
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100rpx 0;
}

.error-text {
  font-size: 28rpx;
  color: #ff6b35;
  margin-bottom: 20rpx;
}

.retry-btn {
  background-color: #1890ff;
  color: white;
  border: none;
  padding: 10rpx 40rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
}

/* 表单容器 */
.form-container {
  padding: 20rpx;
  background-color: #f5f5f5;
}

/* 图片上传区域 */
.image-section {
  margin-bottom: 30rpx;
  background-color: #ffffff;
  border-radius: 20rpx;
  padding: 20rpx;
}

.section-label {
  display: block;
  font-size: 28rpx;
  color: #333333;
  font-weight: bold;
  margin-bottom: 20rpx;
}

.image-upload {
  position: relative;
  width: 100%;
  height: 400rpx;
  border-radius: 15rpx;
  overflow: hidden;
  background-color: #f5f5f5;
}

.preview-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #f5f5f5;
}

.upload-icon {
  font-size: 60rpx;
  color: #cccccc;
  margin-bottom: 10rpx;
}

.upload-text {
  font-size: 24rpx;
  color: #999999;
}

.upload-btn {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 80rpx;
  line-height: 80rpx;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  font-size: 28rpx;
  padding: 0;
  margin: 0;
}

/* 表单部分 */
.form-section {
  background-color: #ffffff;
  border-radius: 20rpx;
  padding: 20rpx;
}

.form-item {
  margin-bottom: 30rpx;
}

.form-label {
  display: block;
  font-size: 28rpx;
  color: #333333;
  margin-bottom: 10rpx;
}

.form-input {
  width: 100%;
  height: 80rpx;
  line-height: 80rpx;
  background-color: #f5f5f5;
  border-radius: 10rpx;
  padding: 0 20rpx;
  box-sizing: border-box;
  font-size: 28rpx;
}

.form-textarea {
  width: 100%;
  background-color: #f5f5f5;
  border-radius: 10rpx;
  padding: 20rpx;
  box-sizing: border-box;
  font-size: 28rpx;
  min-height: 150rpx;
}

.form-picker {
  width: 100%;
  height: 80rpx;
  line-height: 80rpx;
  background-color: #f5f5f5;
  border-radius: 10rpx;
  padding: 0 20rpx;
  box-sizing: border-box;
  font-size: 28rpx;
}

.picker-display {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  color: #333333;
}

/* 提交按钮 */
.submit-container {
  padding: 40rpx 20rpx;
  background-color: #f5f5f5;
}

.submit-btn {
  width: 100%;
  height: 100rpx;
  line-height: 100rpx;
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
  color: white;
  border: none;
  border-radius: 50rpx;
  font-size: 32rpx;
  font-weight: bold;
}

.submit-btn:disabled {
  background-color: #cccccc;
}
</style>