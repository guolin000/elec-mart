<template>
  <div class="main-content">
    <div style="width: 70%; background-color: white; margin: 30px auto; border-radius: 20px">
      <div style="padding-bottom: 10px">
        <div style="display: flex; font-size: 18px; color: #000000FF; line-height: 80px; border-bottom: #cccccc 1px solid;">
          <div style="margin-left: 20px; flex: 1">订单结算</div>


          <div style="flex: 2; text-align: right; padding-right: 40px">
            <el-select v-model="addressId" placeholder="请选择收货地址" style="width: 50%">
              <el-option v-for="item in addressData" :label="item.username + ' - ' + item.useraddress + ' - ' + item.phone" :value="item.id"></el-option>
            </el-select>
          </div>

        </div>


        <div style="margin: 20px 0; padding: 0 50px">
          <div class="table">
            <el-table :data="goodsData" strip @selection-change="handleSelectionChange" style="font-size: 16px; ">
              <el-table-column width="55" align="center"></el-table-column>
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
              <el-table-column prop="goodsPrice" label="原价"></el-table-column>
              <el-table-column prop="num" label="选择数量">
                <template v-slot="scope">
                  <el-input-number v-model="scope.row.num" style="width: 100px" @change="handleChange(scope.row)" :min="1"></el-input-number>
                </template>
              </el-table-column>
              <el-table-column prop="goodsPrice" label="原价"></el-table-column>
            </el-table>

          </div>
        </div>

        <div style="display: flex; font-size: 18px; color: #000000FF; line-height: 80px; ">
            <div style="flex: 1; font-size: 20px; text-align: left; padding-left: 100px">
            <span style="padding-right: 30px">付款方式 : </span>
                <el-radio-group v-model="selectedValue" style="transform: scale(1.2); font-size: 20px;">
                      <el-radio label="option1" >支付宝</el-radio>
                      <el-radio label="option2">余额</el-radio>
                    </el-radio-group>
            </div>

                  <div style="flex: 2; font-size: 20px; text-align: right; padding-right: 40px">
                    共计 ￥   {{totalPrice}}    <el-button type="danger" style=" font-size: 18px;" round @click="pay">提交订单</el-button>
                  </div>
                </div>
      </div>
    </div>
  </div>
</template>

<script>

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
      selectedValue: 'option1'  // 默认选中的值
    }
  },
  mounted() {
    this.loadGoods()
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
    loadGoods() {
            this.goodsId=Number(this.$route.query.buyGoodsId)
            if(this.goodsId!=null && !Number.isNaN(this.goodsId)){

                console.log(this.goodsId)
                this.$request.get('/cart/selectByGoodsId?goodsId=' + this.goodsId).then(res => {
                      if (res.code === '200') {
                        console.table(res.data)
                        this.goodsData[0] = res.data
                        console.table(this.goodsData)
                      } else {
                         this.$message.error(res.msg)
                      }
                })

                this.$request.get('/cart/selectAll?id=' + this.goodsId).then(res => {
                                      if (res.code === '200') {
                                        console.table(res.data)
                                        this.goodsData[0] = res.data
                                        console.table(this.goodsData)
                                      } else {
                                         this.$message.error(res.msg)
                                      }
                                })
            }

            else{
                const selectedData = this.$route.query.selectedData;

                          if (selectedData) {
                            this.goodsData = JSON.parse(selectedData);
                          }
                      console.table(this.goodsData)


            }

    //计算总价
                      this.totalPrice = 0
                            this.goodsData.forEach(item => {
                            console.log( item.goodsPrice)
                            this.totalPrice += item.goodsPrice * item.num
                      })



        },
    navTo(url) {
      location.href = url
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
      this.goodsData = rows
      // 计计算总价格
      this.goodsData.forEach(item => {
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
      let data = {
        userId: this.user.id,
        addressId: this.addressId,
        status: '待发货',
        cartData: this.goodsData
      }
      this.$request.post('/orders/add', data).then(res => {
        if (res.code === '200') {
          this.$message.success('操作成功')

        } else {
          this.$message.error(res.msg)
        }
      })
     location.href = "/front/orders"

    }
  }
}
</script>
