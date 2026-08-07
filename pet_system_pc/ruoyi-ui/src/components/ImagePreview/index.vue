<template>
  <el-image
    :src="`${realSrc}`"
    fit="cover"
    :style="`width:${realWidth};height:${realHeight};`"
    :preview-src-list="realSrcList"
  >
    <div slot="error" class="image-slot">
      <i class="el-icon-picture-outline"></i>
    </div>
  </el-image>
</template>

<script>
import { isExternal } from "@/utils/validate"

export default {
  name: "ImagePreview",
  data(){
    return{
      downloadUrl: process.env.VUE_APP_DOWNLOAD_URL,
    }
  },
  props: {
    src: {
      type: String,
      default: ""
    },
    width: {
      type: [Number, String],
      default: ""
    },
    height: {
      type: [Number, String],
      default: ""
    }
  },
  methods:{
    /**
     * 根据文件名获取完整的下载URL
     * @param {string} fileName - 文件名
     * @returns {string} 完整的下载URL
     */
    getFileDownloadUrl(fileName) {
      // 如果是外部链接或已经是完整URL，直接返回
      if (isExternal(fileName) || fileName.startsWith(process.env.VUE_APP_BASE_API)) {
        return fileName;
      }
      // 否则，拼接成下载接口的URL，并对文件名进行编码
      return `${this.downloadUrl}?name=${encodeURIComponent(fileName)}`;
    }
  },
  computed: {
    realSrc() {
      if (!this.src) {
        return
      }
      // 取第一个图片的文件名
      const firstFileName = this.src.split(',')[0].trim();
      return this.getFileDownloadUrl(firstFileName);
    },
    realSrcList() {
      if (!this.src) {
        return
      }
      // 分割所有文件名，并过滤掉空字符串
      const fileNames = this.src.split(',').filter(item => item && item.trim());
      // 将每个文件名转换为下载URL
      return fileNames.map(fileName => this.getFileDownloadUrl(fileName.trim()));
    },
    realWidth() {
      return typeof this.width == "string" ? this.width : `${this.width}px`
    },
    realHeight() {
      return typeof this.height == "string" ? this.height : `${this.height}px`
    }
  }
}
</script>

<style lang="scss" scoped>
.el-image {
  border-radius: 5px;
  background-color: #ebeef5;
  box-shadow: 0 0 5px 1px #ccc;
  ::v-deep .el-image__inner {
    transition: all 0.3s;
    cursor: pointer;
    &:hover {
      transform: scale(1.2);
    }
  }
  ::v-deep .image-slot {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    color: #909399;
    font-size: 30px;
  }
}
</style>
