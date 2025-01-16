<template>
  <div class="main-content">
    <div style="width: 80%; background-color: white; margin: 30px auto; border-radius: 20px">
      <div style="padding-bottom: 10px">
        <div class="section-container" style="font-size: 18px; color: #000000FF; line-height: 80px; border-bottom: #cccccc 1px solid;">
          <div style=" display: flex; align-items: center; /* 垂直居中 */">
            <lottie-animation
                :animation-data="animationData2"
                :loop="true"
                :autoplay="true"
                style="width:50px; height:50px;"
            ></lottie-animation>
            <div>我的订单（{{ ordersData.length }} 个）</div>
          </div>
          <lottie-animation
              :animation-data="animationData"
              :loop="true"
              :autoplay="true"
              style="width:100px; height:100px;"
          ></lottie-animation>
        </div>
        <div style="margin: 20px 0; padding: 0 50px">
          <div class="filters">
            <el-select v-model="orderStatus" placeholder="请选择订单状态" style="width: 150px;">
              <el-option label="已评价" value="已评价"></el-option>
              <el-option label="已完成" value="已完成"></el-option>
              <el-option label="待发货" value="待发货"></el-option>
              <el-option label="待收货" value="待收货"></el-option>
            </el-select>
            <el-button type="primary" style="margin-left: 10px" plain @click="choose">筛选</el-button>
            <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
          </div>
          <div class="table">
            <el-table :data="ordersData" strip>
              <el-table-column label="商品图片" width="120px">
                <template v-slot="scope">
                  <el-image style="width: 80px; height: 60px; border-radius: 3px" v-if="scope.row.goodsImg"
                            :src="scope.row.goodsImg" :preview-src-list="[scope.row.goodsImg]"></el-image>
                </template>
              </el-table-column>
              <el-table-column prop="orderId" label="订单编号"></el-table-column>
              <el-table-column prop="goodsName" label="商品名称" :show-overflow-tooltip="true">
                <template v-slot="scope">
                  <a :href="'/front/detail?id=' + scope.row.goodsId">{{scope.row.goodsName}}</a>
                </template>
              </el-table-column>
              <el-table-column prop="businessName" label="店铺名称">
                <template v-slot="scope">
                  <a :href="'/front/business?id=' + scope.row.businessId">{{scope.row.businessName}}</a>
                </template>
              </el-table-column>
              <el-table-column prop="goodsPrice" label="商品价格">
                <template v-slot="scope">
                  {{scope.row.goodsPrice}} / {{scope.row.goodsUnit}}
                </template>
              </el-table-column>
              <el-table-column prop="num" label="商品数量"></el-table-column>
              <el-table-column prop="price" label="订单总价"></el-table-column>
              <el-table-column prop="username" label="收货人"></el-table-column>
              <el-table-column prop="useraddress" label="收货地址"></el-table-column>
              <el-table-column prop="phone" label="联系电话"></el-table-column>
              <el-table-column prop="status" label="订单状态"></el-table-column>
              <el-table-column label="操作" align="center" width="180">
                <template v-slot="scope">
                  <el-button :disabled="true" size="mini" type="primary" v-if="scope.row.status === '待发货'" plain @click="updateStatus(scope.row, '已完成') ">确认收货</el-button>
                  <el-button size="mini" type="primary" v-if="scope.row.status === '待收货'" plain @click="updateStatus(scope.row, '已完成')">确认收货</el-button>
                  <el-button size="mini" type="primary" v-if="scope.row.status === '已完成'" plain @click="addComment(scope.row)">评价</el-button>
<!--                  <el-button size="mini" type="danger" plain @click="del(scope.row.id)">删除</el-button>-->
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination" style="margin-top: 20px">
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
      </div>
    </div>
    <el-dialog title="请输入评价内容" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form :model="form" label-width="100px" style="padding-right: 50px" :rules="rules" ref="formRef">
        <el-form-item label="评价内容" prop="username">
          <el-input type="textarea" v-model="form.content" placeholder="请输入评价内容"></el-input>
        </el-form-item>
      </el-form>

      <div slot="footer" class="dialog-footer">
        <el-button @click="fromVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>

import * as animationData from "@/assets/回信.json";
import * as animationData2 from "@/assets/标签.json";
import LottieAnimation from "@/components/LottieAnimation";
import Lottie from "lottie-web";

export default {

  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      ordersData: [],  // 当前显示的数据
      totalData: [],  // 所有数据
      pageNum: 1,     // 当前的页码
      pageSize: 10,   // 每页显示的个数
      total: 0,
      form: {},
      fromVisible: false,
      orderStatus: null,  // 订单状态筛选条件
      animationData,
      animationData2,
    }
  },
  components: {
    LottieAnimation,
  },
  mounted() {
    this.$nextTick(() => {
      // 初始化 Lottie 动画
      const lottieInstance = Lottie.loadAnimation({
        container: this.$refs.lottieContainer,
        renderer: 'svg',
        loop: this.lottieOptions.loop,
        autoplay: this.lottieOptions.autoplay,
        animationData: this.lottieOptions.animationData,
      });
    });
    this.loadOrders(1)
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    loadOrders(pageNum) {
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/orders/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
        }
      }).then(res => {
        if (res.code === '200') {
          this.ordersData = res.data?.list
          console.log(this.ordersData)
          this.totalData = this.ordersData
          this.total = res.data?.total
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    navTo(url) {
      location.href = url
    },
    del(id) {
      this.$request.delete('/orders/delete/' + id).then(res => {
        if (res.code === '200') {
          this.$message.success('删除成功')
          this.loadOrders(1)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    choose() {
      this.applyFilters();  // 应用筛选条件
    },
    applyFilters() {
      let filteredData = this.totalData;

      // 根据订单状态筛选
      if (this.orderStatus) {
        filteredData = filteredData.filter(item => item.status === this.orderStatus);
      }

      // 更新 ordersData
      this.ordersData = filteredData;

      // 更新总条数
      this.total = filteredData.length;

      // 重置页码
      this.pageNum = 1;
    },
    handleCurrentChange(pageNum) {
      this.loadOrders(pageNum)
    },
    updateStatus(row, status) {
      this.form = row
      this.form.status = status
      this.$request.put('/orders/update', this.form).then(res => {
        if (res.code === '200') {
          this.$message.success('操作成功')
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    addComment(row) {
      this.fromVisible = true
      this.form = row
    },
    reset() {
      this.orderStatus = null;
      this.loadOrders(1);
    },
    save() {
      let data = {
        userId: this.user.id,
        businessId: this.form.businessId,
        goodsId: this.form.goodsId,
        content: this.form.content,
      }
      this.$request.post('/comment/add', data).then(res => {
        if (res.code === '200') {
          this.$message.success('评价成功')
          this.fromVisible = false
          this.updateStatus(this.form, '已评价')
          this.form = {}
        } else {
          this.$message.error(res.msg)
        }
      })
    }
  }
}
</script>
<style scoped>
.section-container {
  display: flex;
  align-items: center; /* 垂直居中 */
  justify-content:space-between ; /* 水平间距 */
  margin: 20px 0 0 15px; /* 上外边距 */
}

.section-title {
  height: 40px;
  background-color: #04BF04FF;
  font-size: 20px;
  color: white;
  width: 130px;
  font-weight: bold;
  line-height: 40px;
  text-align: center;
  border-radius: 20px;
}
</style>
