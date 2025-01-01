<template>
  <div class="dashboard-card">
    <div class="card-header">
      <span>{{ cardName }}</span>
      <el-button @click="$emit('refresh-data')" size="mini" type="text" class="refresh-button">刷新</el-button>
    </div>
    <div class="card-content">
      <div class="item" v-for="(item, index) in stats" :key="index">
        <div class="item-value">{{ item.value }}</div>
        <div class="item-label">{{ item.label }}</div>
      </div>
    </div>
  </div>
</template>

<script>
// import { ElButton } from 'element-plus'; // 如果使用 Element Plus

export default {
  name: "DataDashboard",
  props: {
    cardName: {
      type: String,
      required: true,
      default: "总览",
    },
    stats: {
      type: Array,
      required: true,
      default: () => [],
    },
  },
};
</script>

<style scoped>
.dashboard-card {
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
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.refresh-button {
  padding: 4px 8px;
  font-size: 12px;
  color: #606266;
  cursor: pointer;
}

.card-content {
  margin: 10px 20px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item {
  text-align: center;
  flex: 1;
}

.item-value {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}

.item-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}
</style>
