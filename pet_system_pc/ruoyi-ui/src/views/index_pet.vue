<template>
  <div class="pet-dashboard-container">

    <!-- 数据分析图表网格 -->
    <div class="charts-grid">
      <el-row :gutter="24">
        <!-- 宠物类型分布 -->
        <el-col :xs="24" :sm="12" :lg="8">
          <div class="chart-card">
            <div class="chart-card-header">
              <h4>宠物类型分布</h4>
              <el-tooltip content="查看详细数据" placement="top">
                <i class="el-icon-info"></i>
              </el-tooltip>
            </div>
            <div class="chart-card-content">
              <pet-type-chart height="280px"/>
            </div>
          </div>
        </el-col>


        <!-- 实时数据监控 -->
        <el-col :xs="24" :sm="12" :lg="8">
          <div class="chart-card">
            <div class="chart-card-header">
              <h4>实时数据监控</h4>
              <div class="live-indicator">
                <span class="live-dot"></span>
                <span>实时</span>
              </div>
            </div>
            <div class="chart-card-content">
              <realtime-monitor height="280px"/>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 最新订单动态 -->
    <div class="orders-section">
      <div class="section-header">
        <div class="header-content">
          <h3>最新订单动态</h3>
          <p>实时更新的订单信息</p>
        </div>
        <div class="header-actions">
          <el-button type="text" @click="refreshOrders">
            <i class="el-icon-refresh"></i> 刷新
          </el-button>
        </div>
      </div>

      <div class="orders-table-container">
        <el-table
          :data="recentOrders"
          style="width: 100%"
          :stripe="true"
          :row-class-name="tableRowClassName"
          v-loading="loading">
          <el-table-column prop="orderSn" label="订单号">
            <template slot-scope="scope">
              <span class="order-sn">{{ scope.row.orderSn }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="shopId" label="店铺">
            <template slot-scope="scope">
              <span v-for="item in shopList" v-if="item.shopId === scope.row.shopId">{{ item.shopName }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额">
            <template slot-scope="scope">
              <span class="amount">¥{{ scope.row.totalAmount / 100 }}</span>
            </template>
          </el-table-column>
          <el-table-column label="订单状态" align="center" prop="status">
            <template slot-scope="scope">
              <dict-tag :options="dict.type.pet_donation_status" :value="scope.row.status"/>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间">
            <template slot-scope="scope">
              <span class="time">{{ formatTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
import PanelGroup from './dashboard/PanelGroup'
import LineChart from './dashboard/LineChart'
import PetTypeChart from './dashboard/PetTypeChart'
import ServiceTypeChart from './dashboard/ServiceTypeChart'
import MonthlyIncomeChart from './dashboard/MonthlyIncomeChart'
import UserActivityChart from './dashboard/UserActivityChart'
import RealtimeMonitor from './dashboard/RealtimeMonitor'
import CountTo from 'vue-count-to'

/** 请求api接口 */
import {listOrder} from "@/api/pet/order";
import {listShop} from "@/api/pet/shop";

const lineChartData = {
  petRegistrations: {
    expectedData: [120, 132, 101, 134, 90, 230, 210],
    actualData: [120, 82, 91, 154, 162, 140, 145]
  },
  serviceOrders: {
    expectedData: [200, 182, 191, 234, 290, 330, 310],
    actualData: [180, 160, 151, 206, 245, 290, 270]
  },
  shopRegistrations: {
    expectedData: [80, 100, 121, 104, 105, 90, 100],
    actualData: [120, 90, 100, 138, 142, 130, 130]
  },
  userGrowth: {
    expectedData: [130, 140, 141, 142, 145, 150, 160],
    actualData: [120, 122, 131, 144, 152, 160, 170]
  }
}

export default {
  name: 'PetDashboard',
  dicts: ['pet_donation_status'],
  components: {
    PanelGroup,
    LineChart,
    PetTypeChart,
    ServiceTypeChart,
    MonthlyIncomeChart,
    UserActivityChart,
    RealtimeMonitor,
    CountTo
  },
  data() {
    return {
      lineChartData: lineChartData.petRegistrations,
      chartTimeRange: '7d',
      loading: false,
      totalIncome: 2847650,
      activeUsers: 3847,
      newUsersToday: 127,
      statsCards: [
        {
          label: '宠物注册',
          value: 102400,
          unit: '只',
          icon: 'el-icon-pet',
          theme: 'pet-theme',
          type: 'petRegistrations',
          duration: 2600,
          trend: 'up',
          trendIcon: 'el-icon-top',
          trendValue: 12.5,
          trendText: '较上周'
        },
        {
          label: '服务订单',
          value: 81212,
          unit: '单',
          icon: 'el-icon-s-order',
          theme: 'order-theme',
          type: 'serviceOrders',
          duration: 3000,
          trend: 'up',
          trendIcon: 'el-icon-top',
          trendValue: 8.3,
          trendText: '较上周'
        },
        {
          label: '合作店铺',
          value: 9280,
          unit: '家',
          icon: 'el-icon-s-shop',
          theme: 'shop-theme',
          type: 'shopRegistrations',
          duration: 3200,
          trend: 'down',
          trendIcon: 'el-icon-bottom',
          trendValue: 2.1,
          trendText: '较上周'
        },
        {
          label: '活跃用户',
          value: 136000,
          unit: '人',
          icon: 'el-icon-user-solid',
          theme: 'user-theme',
          type: 'userGrowth',
          duration: 3600,
          trend: 'up',
          trendIcon: 'el-icon-top',
          trendValue: 15.7,
          trendText: '较上周'
        }
      ],
      recentOrders: [],
      orderQueryParams: {
        pageNum: 1,
        pageSize: 5
      },
      shopList: []
    }
  },
  created() {
    this.loadDashboardData()
  },
  methods: {
    handleSetLineChartData(type) {
      this.lineChartData = lineChartData[type]
    },
    handleTimeRangeChange(range) {
      // 根据时间范围更新图表数据
      console.log('时间范围变更:', range)
      this.loadChartData(range)
    },
    async loadDashboardData() {
      try {
        this.loading = true
        // 调用API获取真实数据
        // const response = await getDashboardData()
        // this.recentOrders = response.data.recentOrders
        listOrder(this.orderQueryParams).then(response => {
          this.recentOrders = response.rows
        })
        //获取店铺列表
        const shopQueryParams = {
          pageNum: 1,
          pageSize: 1000000
        }
        listShop(shopQueryParams).then(response => {
          this.shopList = response.rows
        })
      } catch (error) {
        console.error('加载仪表板数据失败:', error)
      } finally {
        this.loading = false
      }
    },
    async loadChartData(timeRange) {
      // 根据时间范围加载图表数据
      console.log('加载图表数据:', timeRange)
    },
    refreshOrders() {
      this.loadDashboardData()
    },
    viewAllOrders() {
      this.$router.push('pet/order/index')
    },
    viewOrderDetail(order) {
      console.log('查看订单详情:', order)
    },
    tableRowClassName({row, rowIndex}) {
      if (row.status === 0) {
        return 'warning-row'
      } else if (row.status === 2) {
        return 'success-row'
      }
      return ''
    },
    getStatusType(status) {
      const statusMap = {
        0: 'warning',
        1: 'success',
        2: 'info',
        3: 'danger'
      }
      return statusMap[status] || 'info'
    },
    getStatusText(status) {
      const statusMap = {
        0: '待支付',
        1: '已支付'
      }
      return statusMap[status] || '未知'
    },
    getServiceTagType(serviceName) {
      const typeMap = {
        '托养服务': 'primary',
        '宠物美容': 'success',
        '宠物医疗': 'warning',
        '宠物训练': 'info'
      }
      return typeMap[serviceName] || ''
    },
    formatTime(time) {
      const date = new Date(time)
      const now = new Date()
      const diff = now - date
      const minutes = Math.floor(diff / 60000)
      const hours = Math.floor(diff / 3600000)
      const days = Math.floor(diff / 86400000)

      if (minutes < 1) return '刚刚'
      if (minutes < 60) return `${minutes}分钟前`
      if (hours < 24) return `${hours}小时前`
      if (days < 7) return `${days}天前`
      return time
    }
  }
}
</script>

<style lang="scss" scoped>
.pet-dashboard-container {
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: calc(100vh - 84px);
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320"><path fill="%23ffffff" fill-opacity="0.05" d="M0,96L48,112C96,128,192,160,288,160C384,160,480,128,576,122.7C672,117,768,139,864,133.3C960,128,1056,96,1152,90.7C1248,85,1344,107,1392,117.3L1440,128L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path></svg>') no-repeat bottom;
    background-size: cover;
    pointer-events: none;
  }

  // 统计卡片样式
  .stats-cards-container {
    margin-bottom: 32px;
    position: relative;
    z-index: 1;

    .stat-card {
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(10px);
      border-radius: 16px;
      padding: 24px;
      margin-bottom: 20px;
      cursor: pointer;
      transition: all 0.3s ease;
      border: 1px solid rgba(255, 255, 255, 0.2);
      position: relative;
      overflow: hidden;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
      }

      .stat-card-content {
        position: relative;
        z-index: 2;
        display: flex;
        align-items: center;
        gap: 16px;

        .stat-icon {
          width: 60px;
          height: 60px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 24px;
          color: white;
        }

        .stat-info {
          flex: 1;

          .stat-value {
            font-size: 28px;
            font-weight: bold;
            color: #2c3e50;
            display: flex;
            align-items: baseline;
            gap: 4px;

            .stat-unit {
              font-size: 14px;
              color: #7f8c8d;
              font-weight: normal;
            }
          }

          .stat-label {
            font-size: 14px;
            color: #7f8c8d;
            margin: 4px 0;
          }

          .stat-trend {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 12px;

            &.up {
              color: #67c23a;
            }

            &.down {
              color: #f56c6c;
            }

            .trend-text {
              color: #909399;
            }
          }
        }
      }

      .stat-card-bg {
        position: absolute;
        top: 0;
        right: 0;
        width: 100px;
        height: 100px;
        opacity: 0.1;

        .bg-pattern {
          width: 100%;
          height: 100%;
          background: radial-gradient(circle, currentColor 0%, transparent 70%);
        }
      }

      &.pet-theme {
        .stat-icon {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }

        .stat-card-bg {
          color: #667eea;
        }
      }

      &.order-theme {
        .stat-icon {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
        }

        .stat-card-bg {
          color: #f093fb;
        }
      }

      &.shop-theme {
        .stat-icon {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        .stat-card-bg {
          color: #4facfe;
        }
      }

      &.user-theme {
        .stat-icon {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
        }

        .stat-card-bg {
          color: #43e97b;
        }
      }
    }
  }

  // 主图表容器
  .main-chart-container {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 16px;
    padding: 24px;
    margin-bottom: 32px;
    position: relative;
    z-index: 1;

    .chart-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;

      .chart-title {
        h3 {
          margin: 0 0 8px 0;
          font-size: 18px;
          color: #2c3e50;
          font-weight: 600;
        }

        p {
          margin: 0;
          font-size: 14px;
          color: #7f8c8d;
        }
      }

      .chart-controls {
        .el-radio-group {
          .el-radio-button__inner {
            border-radius: 20px;
            border: none;
            background: #f5f7fa;
            color: #606266;

            &.is-active {
              background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
              color: white;
            }
          }
        }
      }
    }
  }

  // 图表网格
  .charts-grid {
    margin-bottom: 32px;
    position: relative;
    z-index: 1;

    .chart-card {
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(10px);
      border-radius: 16px;
      padding: 24px;
      height: 100%;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
      }

      .chart-card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;

        h4 {
          margin: 0;
          font-size: 16px;
          color: #2c3e50;
          font-weight: 600;
        }

        .live-indicator {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 12px;
          color: #67c23a;

          .live-dot {
            width: 8px;
            height: 8px;
            background: #67c23a;
            border-radius: 50%;
            animation: pulse 2s infinite;
          }
        }
      }
    }
  }

  // 分析区域
  .analysis-section {
    margin-bottom: 32px;
    position: relative;
    z-index: 1;

    .chart-card {
      &.large {
        .chart-card-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 20px;

          h4 {
            margin: 0;
            font-size: 16px;
            color: #2c3e50;
            font-weight: 600;
          }

          .total-amount {
            text-align: right;

            .label {
              display: block;
              font-size: 12px;
              color: #7f8c8d;
              margin-bottom: 4px;
            }

            .value {
              font-size: 20px;
              font-weight: bold;
              color: #2c3e50;
            }
          }

          .user-stats {
            display: flex;
            gap: 24px;

            .stat-item {
              text-align: center;

              .value {
                display: block;
                font-size: 20px;
                font-weight: bold;
                color: #2c3e50;
                margin-bottom: 4px;
              }

              .label {
                font-size: 12px;
                color: #7f8c8d;
              }
            }
          }
        }
      }
    }
  }

  // 订单区域
  .orders-section {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 16px;
    padding: 24px;
    position: relative;
    z-index: 1;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 24px;

      .header-content {
        h3 {
          margin: 0 0 8px 0;
          font-size: 18px;
          color: #2c3e50;
          font-weight: 600;
        }

        p {
          margin: 0;
          font-size: 14px;
          color: #7f8c8d;
        }
      }

      .header-actions {
        display: flex;
        gap: 12px;
      }
    }

    .orders-table-container {
      .user-info {
        display: flex;
        align-items: center;
        gap: 8px;

        .el-avatar {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
      }

      .order-sn {
        font-family: 'Courier New', monospace;
        font-weight: 600;
        color: #2c3e50;
      }

      .amount {
        font-weight: bold;
        color: #f56c6c;
      }

      .time {
        color: #7f8c8d;
        font-size: 13px;
      }
    }
  }
}

// 表格行样式
::v-deep .el-table {
  .warning-row {
    background-color: #fdf6ec;
  }

  .success-row {
    background-color: #f0f9ff;
  }

  .el-table__row {
    &:hover {
      background-color: #f5f7fa;
    }
  }
}

// 动画
@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(103, 194, 58, 0.7);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(103, 194, 58, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(103, 194, 58, 0);
  }
}

// 响应式设计
@media (max-width: 1024px) {
  .pet-dashboard-container {
    padding: 16px;

    .stats-cards-container {
      .stat-card {
        padding: 16px;

        .stat-card-content {
          .stat-info {
            .stat-value {
              font-size: 24px;
            }
          }
        }
      }
    }

    .main-chart-container,
    .charts-grid .chart-card,
    .analysis-section .chart-card,
    .orders-section {
      padding: 16px;
    }
  }
}
</style>
