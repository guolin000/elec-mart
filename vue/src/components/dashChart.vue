<template>
  <div class="dashboard">
    <!-- 顶部标题部分 -->
    <div class="card-header">
      <span>订单统计</span>
    </div>
    <!-- 主体内容部分 -->
    <el-container class="main-content">
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
        <!-- 日期选择器 -->
        <el-header class="date-picker-header">
          <el-date-picker
              v-model="dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="onDateChange"
              class="data_picker"
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
import * as echarts from "echarts";

export default {
  name: "dashChart",
  props: {
    chartData: {
      type: Object,
      required: true,
    },
    stats:{
      type:Array,
      required: true,
      default:()=>[
        { title: '本月订单总数', value: "暂无数据", change: 10, period: '上月' },
        { title: '本周订单总数', value: "暂无数据", change: -10, period: '上周' },
        { title: '本月销售总额', value: "暂无数据", change: 10, period: '上月' },
        { title: '本周销售总额', value: "暂无数据", change: -10, period: '上周' },
      ]
    }
  },
  data() {
    return {
      stats: [],
      dateRange: [], // 绑定日期选择器的日期范围
      chart: null, // ECharts 实例
    };
  },
  watch: {
    // 监听父组件传入的数据变化
    chartData: {
      immediate: true,
      handler(newData) {
        this.renderChart(newData);
      },
    },
  },
  mounted() {
    this.chart = echarts.init(document.getElementById("chart"));
    this.fetchDefaultData();
  },
  methods: {
    // 默认加载当前日期为基准的前后三天数据
    fetchDefaultData() {
      console.log("子组件加载默认日期");
      const today = new Date();
      const startDate = new Date(today);
      startDate.setDate(today.getDate() - 3);
      const endDate = new Date(today);
      endDate.setDate(today.getDate() + 3);
      this.dateRange = [startDate, endDate];
      console.log( this.formatDateRange(startDate, endDate))
      // 通知父组件加载默认数据
      this.$emit("dateChange", this.formatDateRange(startDate, endDate));
    },
    // 用户选择日期后触发
    onDateChange() {
      if (this.dateRange.length === 2) {
        const [start, end] = this.dateRange;
        this.$emit("dateChange", this.formatDateRange(start, end));
      }
    },
    // 格式化日期范围为字符串
    formatDateRange(startDate, endDate) {
      const format = (date) =>
          `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(
              2,
              "0"
          )}-${String(date.getDate()).padStart(2, "0")}`;
      return { startDate: format(startDate), endDate: format(endDate) };
    },
    // 渲染图表
    renderChart(chartData) {
      this.chart.setOption({
        title: { text: "订单统计", left: "center" },
        tooltip: { trigger: "axis" },
        legend: { data: ["订单总数", "销售总额"], bottom: 0 },
        xAxis: { type: "category", data: chartData.dates },
        yAxis: [
          { type: "value", name: "订单总数" },
          { type: "value", name: "销售总额", position: "right" },
        ],
        series: [
          { name: "订单总数", type: "line", data: chartData.orders , areaStyle: {},},
          { name: "销售总额", type: "line", yAxisIndex: 1, data: chartData.sales , areaStyle: {},},
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
  flex-direction: column;
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  width: 100%;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #5d646b; /* 数值的颜色 */;
  background: #e3e4e7; /* 浅灰色背景 */
  padding: 10px 20px; /* 上下内边距和左右内边距 */
  border-radius: 6px 6px 0 0; /* 圆角只应用到上边 */
  margin-bottom: 0;
}

.main-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.stats-aside {
  background-color: #f9f9f9;
  padding: 20px;
  overflow-y: auto;
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
  height: 100%;
}
</style>
