<template>
  <div>
    <el-cascader
      ref="cascader"
      v-model="selectedArea"
      :options="cityOptions"
      :props="cascaderProps"
      placeholder="请选择省/市/区"
      clearable
      @change="handleChange"
      filterable
    />
  </div>
</template>

<script>
import { transformCityData } from "../utils/cityTransform.js"

export default {
  name: "CityCascade",
  props: {
    value: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      cityOptions: [],
      selectedArea: [],
      cascaderProps: {
        value: 'code',
        label: 'name',
        children: 'children',
        expandTrigger: 'hover'
      }
    };
  },
  watch: {
    value: {
      handler(newVal) {
        this.selectedArea = newVal;
      },
      immediate: true
    }
  },
  mounted() {
    // 在组件挂载时转换数据
    this.cityOptions = transformCityData();
  },
  methods: {
    handleChange(value) {
      this.$emit('input', value); // 支持 v-model
      // 获取完整的选中对象信息
      this.$nextTick(() => {
        if (this.$refs.cascader) {
          const selectedData = this.$refs.cascader.getCheckedNodes()[0];
          this.$emit('change', selectedData);
        }
      });
    }
  }
};
</script>
