<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons')
import resize from './mixins/resize'

export default {
  mixins: [resize],
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '300px'
    }
  },
  data() {
    return {
      chart: null
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')

      this.chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['本月', '上月']
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: ['托养服务', '美容护理', '医疗保健', '训练培训', '用品销售', '其他服务']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '本月',
            type: 'bar',
            data: [120, 200, 150, 80, 70, 110],
            itemStyle: {
              color: '#409EFF'
            }
          },
          {
            name: '上月',
            type: 'bar',
            data: [100, 180, 120, 90, 60, 95],
            itemStyle: {
              color: '#67C23A'
            }
          }
        ]
      })
    }
  }
}
</script>
