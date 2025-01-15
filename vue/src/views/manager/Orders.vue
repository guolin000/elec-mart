<template>
  <div>
    <div class="top">
      <div class="search"style="margin-left: -155px">
        <el-input placeholder="请输入订单编号" style="width: 200px" v-model="orderId"></el-input>
        <el-input placeholder="请输入收货人名称" style="width: 200px;margin-left: 6px" v-model="username"></el-input>
        <el-input placeholder="请输入店铺名称" style="width: 200px;margin-left: 6px" v-model="businessName"></el-input>
        <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
        <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
      </div>

      <div class="filters">
        <el-select v-model="orderStatus" placeholder="请选择订单状态" style="width: 150px;">
          <el-option label="已评价" value="已评价"></el-option>
          <el-option label="已完成" value="已完成"></el-option>
          <el-option label="待发货" value="待发货"></el-option>
          <el-option label="待收货" value="待收货"></el-option>
        </el-select>
        <el-button type="primary" plain style="margin-left: 8px" @click="choose">筛选</el-button>
      </div>
    </div>

    <div class="operation">
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe  @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
        <el-table-column label="商品图片">
          <template v-slot="scope">
            <div style="display: flex; align-items: center">
              <el-image style="width: 40px; height: 40px; " v-if="scope.row.goodsImg"
                        :src="scope.row.goodsImg" :preview-src-list="[scope.row.goodsImg]"></el-image>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="orderId" label="订单编号"></el-table-column>
        <el-table-column prop="goodsName" label="商品名称" show-overflow-tooltip></el-table-column>
        <el-table-column prop="goodsPrice" label="商品单价" show-overflow-tooltip>
          <template v-slot="scope">
            {{scope.row.goodsPrice}} / {{scope.row.goodsUnit}}
          </template>
        </el-table-column>
        <el-table-column prop="num" label="商品数量" show-overflow-tooltip></el-table-column>
        <el-table-column prop="price" label="订单总价" show-overflow-tooltip></el-table-column>
        <el-table-column prop="businessName" label="所属店铺" show-overflow-tooltip></el-table-column>
        <el-table-column prop="username" label="收货人" show-overflow-tooltip></el-table-column>
        <el-table-column prop="useraddress" label="收货地址" show-overflow-tooltip></el-table-column>
        <el-table-column prop="phone" label="联系电话" show-overflow-tooltip></el-table-column>
        <el-table-column prop="status" label="订单状态" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template v-slot="scope">
            <el-button plain type="primary" size="mini" v-if="user.role === 'USER' && scope.row.status === '待发货'" @click="updateStatus(scope.row, '待收货')">发货</el-button>
            <el-button plain type="danger" size="mini" @click=del(scope.row.id)>删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
            background
            @current-change="handleCurrentChange"
            :current-page="pageNum"
            :page-sizes="[5, 10, 20]"
            :page-size="pageSize"
            layout="total, prev, pager, next"
            :total="total">
        </el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Orders",
  data() {
    return {
      tableData: [],  // 当前显示的数据
      totalData: [],  // 所有数据
      pageNum: 1,     // 当前的页码
      pageSize: 10,   // 每页显示的个数
      total: 0,
      orderId: null,  // 订单编号搜索条件
      username: null,
      businessName: null,
      orderStatus: null,  // 订单状态筛选条件
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      ids: []
    };
  },
  created() {
    this.load(1)
  },
  methods: {
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$request({
        url: this.form.id ? '/orders/update' : '/orders/add',
        method: this.form.id ? 'PUT' : 'POST',
        data: this.form
      }).then(res => {
        if (res.code === '200') {  // 表示成功保存
          this.$message.success('保存成功')
          this.load(1)
          this.fromVisible = false
        } else {
          this.$message.error(res.msg)  // 弹出错误的信息
        }
      })
    },
    del(id) {   // 单个删除
      this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/orders/delete/' + id).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
    handleSelectionChange(rows) {   // 当前选中的所有的行数据
      this.ids = rows.map(v => v.id)   //  [1,2]
    },
    delBatch() {   // 批量删除
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/orders/delete/batch', {data: this.ids}).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/orders/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          orderId: this.orderId,
          username: this.username,
          businessName: this.businessName,
        }
      }).then(res => {
        if (res.code === '200') {
          this.tableData = res.data?.list
          this.totalData = this.tableData
          this.total = res.data?.total
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    reset() {
      this.orderId = null;
      this.orderStatus = null;
      this.businessName = null;
      this.username = null;
      this.load(1);
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    handleAvatarSuccess(response, file, fileList) {
      this.form.img = response.data
    },
    updateStatus(row, status) {
      this.form = row;
      this.form.status = status
      this.save()
    },
    applyFilters() {
      let filteredData = this.totalData;

      // 根据订单编号筛选
      if (this.orderId) {
        filteredData = filteredData.filter(item => item.orderId.includes(this.orderId));
      }

      // 根据订单状态筛选
      if (this.orderStatus) {
        filteredData = filteredData.filter(item => item.status === this.orderStatus);
      }

      // 更新 tableData
      this.tableData = filteredData;

      // 更新总条数
      this.total = filteredData.length;

      // 重置页码
      this.pageNum = 1;
    },
    choose() {
      this.applyFilters();  // 应用筛选条件
    },

  }
}
</script>

<style scoped>
.top{
  display: flex;
  flex-direction: row;
  justify-content: space-around;
  background:#fff ;
  margin-bottom: 15px;
}
.search {
  display: flex;
}
.filters{
  margin-top: 10px;
}
</style>
