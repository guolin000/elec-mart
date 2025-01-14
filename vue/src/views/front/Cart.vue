<template>
  <div class="main-content">
    <div style="width: 70%; background-color: white; margin: 30px auto; border-radius: 20px">
      <div style="padding-bottom: 10px">
        <div style="display: flex; font-size: 18px; color: #000000FF; line-height: 80px; border-bottom: #cccccc 1px solid;">
          <div class="section-container" style="flex:1;">
            <lottie-animation
                :animation-data="animationData"
                :loop="true"
                :autoplay="true"
                style="width:50px; height:50px;"
            ></lottie-animation>
            <div>全部商品（{{ goodsData.length }}件）</div>
          </div>

          <div style="flex: 1; font-size: 16px; text-align: right; padding-right: 40px">
            已选商品 ￥ {{totalPrice}} <el-button type="danger" style=" font-size: 18px;" round @click="navTo('/front/check')">结算</el-button>
          </div>
        </div>
        <div style="margin: 20px 0; padding: 0 50px">
          <div class="table">
            <el-table :data="goodsData" strip @selection-change="handleSelectionChange" style="font-size: 16px; ">
              <el-table-column type="selection" width="55" align="center"></el-table-column>
              <el-table-column label="商品图片" width="120px">
                <template v-slot="scope">
                  <el-image style="width: 80px; height: 60px; border-radius: 3px" v-if="scope.row.goodsImg"
                            :src="scope.row.goodsImg" :preview-src-list="[scope.row.goodsImg]"></el-image>
                </template>
              </el-table-column>
              <el-table-column prop="goodsName" label="商品名称" width="240px">
                <template v-slot="scope">
                  <a :href="'/front/detail?id=' + scope.row.goodsId">{{scope.row.goodsName}}</a>
                </template>
              </el-table-column>
              <el-table-column prop="businessName" label="店铺名称">
                <template v-slot="scope">
                  <a :href="'/front/business?id=' + scope.row.businessId">{{scope.row.businessName}}</a>
                </template>
              </el-table-column>
              <el-table-column prop="goodsPrice" label="商品价格"></el-table-column>
              <el-table-column prop="num" label="选择数量">
                <template v-slot="scope">
                  <el-input-number v-model="scope.row.num" style="width: 100px" @change="handleChange(scope.row)" :min="1"></el-input-number>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="180">
                <template v-slot="scope">
                  <el-button size="mini" type="danger" plain @click="del(scope.row.id)">移除购物车</el-button>
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
  </div>
</template>

<script>

import LottieAnimation from "@/components/LottieAnimation";
import Lottie from "lottie-web";
import * as animationData from "@/assets/数据 (1).json";
export default {

  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      goodsData: [],
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      totalPrice: 0,
      total: 0,
      addressId: null,
      addressData: [],
      selectedData: [],
      animationData
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
    this.loadGoods(1)
    this.loadAddress()
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    loadAddress() {
      this.$request.get('/address/selectAll').then(res => {
        if (res.code === '200') {
          this.addressData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    loadGoods(pageNum) {
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/cart/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
        }
      }).then(res => {
        if (res.code === '200') {
          this.goodsData = res.data?.list
          this.total = res.data?.total
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    dayin(){
        console.table(this.selectedData)
    },
    navTo(url) {
      if (!this.selectedData || this.selectedData.length === 0) {
            this.$message.warning('请选择商品')
            return
      }

      this.$router.push({
         name: 'Check',
         query: { selectedData: JSON.stringify(this.selectedData) }
      });
    },
    del(id) {
      this.$request.delete('/cart/delete/' + id).then(res => {
        if (res.code === '200') {
          this.$message.success('移除成功')
          this.loadGoods(1)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleCurrentChange(pageNum) {
      this.loadGoods(pageNum)
    },
    handleSelectionChange(rows) {
      this.totalPrice = 0
      this.selectedData = rows
      // 计计算总价格
      this.selectedData.forEach(item => {
        this.totalPrice += (item.goodsPrice * item.num)
      })
    },
    handleChange(row) {
      this.totalPrice = 0
      this.selectedData.forEach(item => {
        this.totalPrice += item.goodsPrice * item.num
      })
    },
    pay() {
      if (!this.addressId) {
        this.$message.warning('请选择收货地址')
        return
      }
      if (!this.selectedData || this.selectedData.length === 0) {
        this.$message.warning('请选择商品')
        return
      }
      let data = {
        userId: this.user.id,
        addressId: this.addressId,
        status: '待发货',
        cartData: this.selectedData
      }
      this.$request.post('/orders/add', data).then(res => {
        if (res.code === '200') {
          this.$message.success('操作成功')
          this.loadGoods(1)
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
  /*justify-content:space-between ; !* 水平间距 *!*/
  /*margin: 40px 0 0 15px; !* 上外边距 *!*/
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
