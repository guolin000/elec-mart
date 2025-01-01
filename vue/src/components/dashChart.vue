<template>
  <div class="dashboard">
    <el-container>
      <!-- 左侧数据统计部分 -->
      <el-aside width="300px" class="stats-aside">
        <div class="stat-item" v-for="(stat, index) in stats" :key="index">
          <div class="stat-title">{{ stat.title }}</div>
          <div class="stat-value">{{ stat.value }}</div>
          <div :class="{'stat-change': true, 'positive': stat.change >= 0, 'negative': stat.change < 0}">
            {{ stat.change >= 0 ? '+' : '' }}{{ stat.change }}% 同比{{ stat.period }}
          </div>
        </div>
      </el-aside>

      <!-- 右侧图表和日期选择部分 -->
      <el-container>
        <el-header class="date-picker-header">
          <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="fetchChartData"
          />
        </el-header>
        <el-main>
          <div id="chart" class="chart-container"></div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import * as echarts from 'echarts';

export default {
  name: "dashChart",
  data() {
    return {
      // 数据统计部分
      stats: [
        { title: '本月订单总数', value: 10000, change: 10, period: '上月' },
        { title: '本周订单总数', value: 1000, change: -10, period: '上周' },
        { title: '本月销售总额', value: 100000, change: 10, period: '上月' },
        { title: '本周销售总额', value: 50000, change: -10, period: '上周' },
      ],
      // 日期范围
      dateRange: [],
      // 图表实例
      chart: null,
    };
  },
  mounted() {
    // 初始化图表
    this.initChart();
    // 加载初始数据
    this.fetchChartData();
  },
  methods: {
    // 初始化 ECharts 图表
    initChart() {
      this.chart = echarts.init(document.getElementById('chart'));
    },
    // 获取图表数据并渲染
    fetchChartData() {
      // 模拟异步获取数据
      const chartData = {
        dates: ['2023-11-01', '2023-11-02', '2023-11-03', '2023-11-04', '2023-11-05', '2023-11-06', '2023-11-07'],
        orders: [20, 40, 60, 90, 150, 60, 40],
        sales: [2000, 4000, 6000, 8000, 10000, 6000, 4000],
      };

      // 渲染图表
      this.chart.setOption({
        title: { text: '订单统计', left: 'center' },
        tooltip: { trigger: 'axis' },
        legend: { data: ['订单总数', '销售总额'], bottom: 0 },
        xAxis: { type: 'category', data: chartData.dates },
        yAxis: [
          { type: 'value', name: '订单总数' },
          { type: 'value', name: '销售总额', position: 'right' },
        ],
        series: [
          {
            name: '订单总数',
            type: 'line',
            data: chartData.orders,
            areaStyle: {},
          },
          {
            name: '销售总额',
            type: 'line',
            yAxisIndex: 1,
            data: chartData.sales,
            areaStyle: {},
          },
        ],
      });
    },
  },
};
</script>

<style scoped>
.dashboard {
  height: 75vh;
  display: flex;
}

.stats-aside {
  /*height: 100%;*/
  background-color: #f9f9f9;
  padding: 20px;
}

.stat-item {
  margin-bottom: 20px;
}

.stat-title {
  font-size: 16px;
  color: #666;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  margin: 5px 0;
}

.stat-change {
  font-size: 14px;
}

.stat-change.positive {
  color: green;
}

.stat-change.negative {
  color: red;
}

.date-picker-header {
  padding: 10px 20px;
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
}

.chart-container {
  width: 100%;
  height: 95%;
}
</style>
