<template>
  <el-container>
    <!-- 上部内容区域 -->
    <div class="top">
      <el-row :gutter="20" class="stats-section">
        <el-col :span="8">
          <el-card class="small-card">
            <div class="card-content">
              <i class="el-icon-document icon-large"></i> <!-- 增大图标 -->
              <div class="content-right">
                <div class="label">今日订单总数</div> <!-- 标签信息 -->
                <div class="value">{{ orderCount }}</div> <!-- 数值 -->
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card class="small-card">
            <div class="card-content">
              <i class="el-icon-money icon-large"></i> <!-- 增大图标 -->
              <div class="content-right">
                <div class="label">今日销售总额</div> <!-- 标签信息 -->
                <div class="value"> ¥ {{ salesAmount }}</div> <!-- 数值 -->
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card class="small-card">
            <div class="card-content">
              <i class="el-icon-money icon-large"></i> <!-- 增大图标 -->
              <div class="content-right">
                <div class="label">昨日销售总额</div> <!-- 标签信息 -->
                <div class="value"> ¥ {{ salesAmountOld }}</div> <!-- 数值 -->
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 下部待处理事务表格 -->
    <div class="table-container">
      <el-table
          ref="mutipleTable"
          border
          stripe
          :data="tableData"
          :span-method="objectSpanMethod"
      >
        <!-- 动态渲染表格列 -->
        <el-table-column class="table_span" v-for="item in columns" :key="item.prop" :prop="item.prop" :label="item.label" :min-width="item.minWidth">
          <template slot-scope="scope">
            <span class="cell_span" :class="{'number-cell': isNumber(scope.row[item.prop])}">{{ scope.row[item.prop] }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="dashboard-container">
      <div class="dashboard-row">
        <data-dashboard :card-name="cardName" :stats="productStats" @refresh-data="fetchProductStats" />
        <data-dashboard :card-name="cardName2" :stats="userStats" @refresh-data="fetchUserStats" />
      </div>
    </div>

    <dash-chart class="dashChart" :chartData="chartData" :stats="stats" @dateChange="fetchChartData" />
  </el-container>
</template>

<script>
import DataDashboard from "@/components/DataDashboard.vue"
import dashChart from "@/components/dashChart";

export default {
  name: "BusinessHome",
  components: {
    DataDashboard,
    dashChart,
  },
  data() {
    return {
      orderCount: 200,
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      salesAmount: 5000.00,
      salesAmountOld: 50003.00,
      columns: [
        { prop: 'a1', minWidth: '250px', label: '待完成事务' },
        { prop: 'a2', minWidth: '50px', label: '' },
        { prop: 'a3', minWidth: '250px', label: '' },
        { prop: 'a4', minWidth: '50px', label: '' },
      ],
      tableData: [],
      needMergeArr: ['a1'],
      rowMergeArrs: {},
      productStats:[],
      userStats:[],
      cardName: "",
      cardName2: "",
      chartData: { dates: [], orders: [], sales: [] }, // 图表数据
      stats:[],
      monthOrders:0,
      monthSum:0,
      weekOrders:0,
      weekSum:0,
      lastMonthSum:0,
      lastMonthOrders:0,
      lastWeekSum:0,
      lastWeekOrders:0,

    };
  },
  mounted() {
    this.fetchTableData();
    this.fetchUserStats();
    this.fetchProductStats();
    this.fetchCardData();
    this.getDateRanges();
  },
  methods: {
    async fetchProductStats() {
      // 模拟一个异步接口请求
      try {
        // const response = await fetch("/api/product-stats");
        // const data = await response.json();
        const data =  [
          { label: "已下架", value: 100 },
          { label: "已上架", value: 400 },
          { label: "库存紧张", value: 50 },
          { label: "全部商品", value: 500 },
        ];
        this.cardName = "商品总览";
        this.productStats = data;
      } catch (error) {
        console.error("Failed to fetch product stats:", error);
      }
    },

    async fetchUserStats() {
      // 模拟一个异步接口请求
      try {
        // const response = await fetch("/api/product-stats");
        // const data = await response.json();
        const data =  [
          { label: "今日新增", value: 100 },
          { label: "昨日新增", value: 400 },
          { label: "本月新增", value: 50 },
          { label: "粉丝总数", value: 500 },
        ];
        this.cardName2 = "用户总览";
        this.userStats = data;
      } catch (error) {
        console.error("Failed to fetch product stats:", error);
      }

    },
    getDateRanges() {
      const today = new Date();

      // 工具函数：将日期转换为 "YYYY-MM-DD" 格式
      function formatDate(date) {
        return date.toISOString().split('T')[0];
      }

      // 获取日期范围
      const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
      const lastDayOfMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0);

      const firstDayOfWeek = new Date(today);
      firstDayOfWeek.setDate(today.getDate() - today.getDay()); // 设置为周日
      const lastDayOfWeek = new Date(firstDayOfWeek);
      lastDayOfWeek.setDate(firstDayOfWeek.getDate() + 6); // 设置为周六

      const firstDayOfLastMonth = new Date(today.getFullYear(), today.getMonth() - 1, 1);
      const lastDayOfLastMonth = new Date(today.getFullYear(), today.getMonth(), 0);

      const firstDayOfLastWeek = new Date(firstDayOfWeek);
      firstDayOfLastWeek.setDate(firstDayOfWeek.getDate() - 7); // 上周的开始
      const lastDayOfLastWeek = new Date(lastDayOfWeek);
      lastDayOfLastWeek.setDate(lastDayOfWeek.getDate() - 7); // 上周的结束

      // 转换日期为字符串
      const firstDayOfMonthStr = formatDate(firstDayOfMonth);
      const lastDayOfMonthStr = formatDate(lastDayOfMonth);
      const firstDayOfWeekStr = formatDate(firstDayOfWeek);
      const lastDayOfWeekStr = formatDate(lastDayOfWeek);
      const firstDayOfLastMonthStr = formatDate(firstDayOfLastMonth);
      const lastDayOfLastMonthStr = formatDate(lastDayOfLastMonth);
      const firstDayOfLastWeekStr = formatDate(firstDayOfLastWeek);
      const lastDayOfLastWeekStr = formatDate(lastDayOfLastWeek);

      // 发起并行请求
      const requests = [
        this.$request.get('/orders/getOrderTotalData', {
          params: {
            merchantId: this.user.id,
            startDate: firstDayOfMonthStr,
            endDate: lastDayOfMonthStr,
          }
        }),
        this.$request.get('/orders/getOrderTotalData', {
          params: {
            merchantId: this.user.id,
            startDate: firstDayOfWeekStr,
            endDate: lastDayOfWeekStr,
          }
        }),
        this.$request.get('/orders/getOrderTotalData', {
          params: {
            merchantId: this.user.id,
            startDate: firstDayOfLastWeekStr,
            endDate: lastDayOfLastWeekStr,
          }
        }),
        this.$request.get('/orders/getOrderTotalData', {
          params: {
            merchantId: this.user.id,
            startDate: firstDayOfLastMonthStr,
            endDate: lastDayOfLastMonthStr,
          }
        }),
      ];

      Promise.all(requests)
          .then(([monthRes, weekRes, lastWeekRes, lastMonthRes]) => {
            // 更新数据
            this.monthOrders = monthRes.totalOrderCount || 0;
            this.monthSum = monthRes.totalAmount || 0;

            this.weekOrders = weekRes.totalOrderCount || 0;
            this.weekSum = weekRes.totalAmount || 0;

            this.lastWeekOrders = lastWeekRes.totalOrderCount || 0;
            this.lastWeekSum = lastWeekRes.totalAmount || 0;

            this.lastMonthOrders = lastMonthRes.totalOrderCount || 0;
            this.lastMonthSum = lastMonthRes.totalAmount || 0;

            // 工具函数：计算浮动比例
            function calculateChange(currentValue, previousValue) {
              if (previousValue === 0) {
                return currentValue === 0 ? 0 : 100; // 如果都为 0，无变化；否则变化为 100%
              }
              return ((currentValue - previousValue) / previousValue) * 100;
            }

            // 计算浮动比例
            const weekOrders_change = calculateChange(this.weekOrders, this.lastWeekOrders);
            const monthOrders_change = calculateChange(this.monthOrders, this.lastMonthOrders);
            const weekSum_change = calculateChange(this.weekSum, this.lastWeekSum);
            const monthSum_change = calculateChange(this.monthSum, this.lastMonthSum);

            // 更新 stats
            this.stats = [
              { title: '本月订单总数', value: this.monthOrders, change: monthOrders_change, period: '上月' },
              { title: '本周订单总数', value: this.weekOrders, change: weekOrders_change, period: '上周' },
              { title: '本月销售总额', value: this.monthSum, change: monthSum_change, period: '上月' },
              { title: '本周销售总额', value: this.weekSum, change: weekSum_change, period: '上周' },
            ];
          })
          .catch(err => {
            console.error('获取数据失败:', err);
            this.$message.error("数据获取失败，请重试！");
          });
    },
    fetchCardData() {
      const today = new Date();
      const yestDay = new Date(today);
      yestDay.setDate(today.getDate() - 1);
      this.$request.get(`/orders/getOrderData`, {
        params: {
          merchantId:this.user.id,
          startDate: yestDay,
          endDate: today,
        }
      }).then(res =>{
        console.log("Chart data received222:",res);
        this.orderCount = res.orders[1];
        this.salesAmount = res.sales[1];
        this.salesAmountOld = res.sales[0];
      }).catch(error=>{
        console.error("Failed to fetch chart data:", error);
        this.$message.error("数据获取失败，请重试！");
      })
    },
    fetchTableData() {
      const dataFromAPI = [
        { a1: '待付款订单', a2: 10, a3: '已完成订单', a4: 10 },
        { a1: '待发货订单', a2: 20, a3: '新缺货登记', a4: 10 },
        { a1: '已发货订单', a2: 10, a3: '待处理退货订单', a4: 20 }
      ];
      this.tableData = dataFromAPI;
    },
    isNumber(value) {
      return typeof value === 'number';
    },
    fetchChartData(dateRange) {
        // 构造请求 URL 和参数
      console.log(dateRange)
      const { startDate, endDate } = dateRange;
      console.log("id  " + this.user.id);
      this.$request.get(`/orders/getOrderData`, {
          params: {
            merchantId:this.user.id,
            startDate: startDate,
            endDate: endDate,
          }
        }).then(res =>{
          this.chartData = res;
          console.log("Chart data received:", this.chartData);
        }).catch(error=>{
          console.error("Failed to fetch chart data:", error);
          this.$message.error("数据获取失败，请重试！");
        })
    },
    objectSpanMethod({ row, column, rowIndex, columnIndex }) {
      if (this.needMergeArr.includes(column.property)) {
        if (rowIndex > 0 && row[column.property] === this.tableData[rowIndex - 1][column.property]) {
          return { rowspan: 0, colspan: 1 };
        } else {
          return { rowspan: 1, colspan: 1 };
        }
      }
      return { rowspan: 1, colspan: 1 };
    }
  }
};
</script>

<style scoped>
/* 确保容器宽度不超过屏幕宽度，增加左右边距 */
.el-container {
  display: flex;
  flex-direction: column;
  height: auto;
  padding: 30px 50px; /* 增加左右边距 */
}
.el-table th {
  background-color: #857e7e !important; /* 设置灰色背景 */
  color: #333; /* 表头文字颜色 */
}
/* 使用Flexbox布局，优化卡片排列 */
.stats-section {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between; /* 卡片之间的间距 */
  margin-top: 20px;
  width: 100%;
}

/* 限制卡片的宽度，使其在一行上显示 */
.small-card {
  flex: 1 1 30%; /* 卡片占据30%的宽度，根据实际情况调整 */
  margin-bottom: 20px; /* 卡片之间的垂直间距 */
  border-radius: 10px; /* 卡片边角圆润 */
  background: #f5f7fa;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2); /* 轻微阴影效果 */
  transition: all 0.3s ease; /* 鼠标悬停时的平滑过渡 */
}

.small-card:hover {
  transform: translateY(-5px); /* 鼠标悬停时，卡片上升 */
  box-shadow: 0 8px 12px rgba(0, 0, 0, 0.15); /* 鼠标悬停时，阴影增强 */
}

.icon-large {
  font-size: 55px !important; /* 设置图标更大 */
  color: #62a7cc; /* 图标颜色 */
  margin-right: 10px; /* 图标和文字的间距 */
  vertical-align: middle; /* 保证图标垂直居中 */
}

.card-content {
  display: flex;
  align-items: center;
}

.content-right {
  display: flex;
  flex-direction: column;
}

.label {
  font-size:16px;
  color: #82878d;
  margin-bottom: 5px; /* 标签和数值之间的间距 */
}

.value {
  font-size: 20px;
  font-weight: bold;
  color: #5d646b; /* 数值的颜色 */
}

.el-card strong {
  font-size: 20px; /* 增加数值的字体大小 */
  font-weight: bold; /* 数值加粗 */
}

.number-cell {
  color: red;
  font-weight:normal;
  font-size: 14px;
}
.el-table--small{
  font-size: 14px !important;
}
.el-table__header-wrapper{
  background: #e3e4e7 !important; /* 浅灰色背景 */
}
.cell_span{
  font-size: 15px;
  /*font-weight: lighter;*/
}
/* 确保表格和卡片区之间的间距 */
.table-container {
  margin-top: 20px;
  margin-bottom: 20px;
  /*padding: 0 20px;*/
}
.dashboard-container {
  margin-top: 20px; /* 上方的 margin */
}

.dashboard-row {
  display: flex; /* 使用 Flexbox 布局 */
  gap: 20px; /* 卡片之间的间距 */
  justify-content: space-between; /* 使卡片分布均匀 */
}

data-dashboard {
  flex: 1; /* 让每个卡片平分可用空间 */
}

.dashChart{
  margin-top: 30px;
}
</style>
