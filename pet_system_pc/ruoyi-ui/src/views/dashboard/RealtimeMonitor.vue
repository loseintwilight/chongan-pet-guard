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
      this.startRealTimeUpdate()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons')

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: this.generateTimeData()
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '实时订单',
            type: 'line',
            smooth: true,
            symbol: 'none',
            areaStyle: {
              opacity: 0.3
            },
            data: this.generateRandomData(),
            itemStyle: {
              color: '#409EFF'
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: 'rgba(64, 158, 255, 0.3)'
                },
                {
                  offset: 1,
                  color: 'rgba(64, 158, 255, 0.1)'
                }
              ])
            }
          }
        ]
      }

      this.chart.setOption(option)
    },
    generateTimeData() {
      const data = []
      const now = new Date()
      for (let i = 9; i >= 0; i--) {
        const time = new Date(now - i * 60000)
        data.push(time.getHours() + ':' + (time.getMinutes() < 10 ? '0' : '') + time.getMinutes())
      }
      return data
    },
    generateRandomData() {
      const data = []
      for (let i = 0; i < 10; i++) {
        data.push(Math.floor(Math.random() * 100) + 50)
      }
      return data
    },
    startRealTimeUpdate() {
      this.timer = setInterval(() => {
        if (this.chart) {
          const option = this.chart.getOption()
          option.xAxis[0].data.shift()
          option.xAxis[0].data.push(this.generateTimeData()[9])

          option.series[0].data.shift()
          option.series[0].data.push(Math.floor(Math.random() * 100) + 50)

          this.chart.setOption(option)
        }
      }, 3000)
    }
  }
}
</script>
