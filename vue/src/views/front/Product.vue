<template>
  <div class="main-content">
    <div style="width: 60%; background-color: white; min-height: 1000px; margin: 20px auto; border-radius: 20px">
      <div style="padding: 15px 20px">
        <el-row :gutter="20">
          <el-col :span="12">
            <img :src="goodsData.img" alt="" style="width: 100%; height: 400px; border-radius: 20px">
          </el-col>
          <el-col :span="12">
            <div style="font-size: 20px; font-weight: 900; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical;">{{goodsData.name}}</div>
            <div style="color: red; margin-top: 15px">秒杀价：<span style="font-size: 20px">{{goodsData.seckillPrice}} / {{goodsData.unit}}</span></div>
            <div style="margin-top: 20px">
              <img src="@/assets/imgs/right.png" alt="" style="width: 70%; height: 130px; border-radius: 15px">
            </div>
            <el-button
                type="primary"
                style="margin-top: 10px; width: 100%; background-color: #FF5000; border-color: #FF5000;"
                @click="secKill">
              立即秒杀
            </el-button>
          </el-col>
        </el-row>
      </div>
      <div style="padding: 15px 20px">
        <el-tabs v-model="activeName" @tab-click="handleClick">
          <el-tab-pane label="宝贝详情" name="first">
            <div style="padding: 10px 175px" v-html="goodsData.description"></div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script>

export default {

  data() {
    let seckillId = this.$route.query.id
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      seckillId: seckillId,
      goodsData: {},
      activeName: 'first',
    }
  },
  mounted() {
    this.loadGoods()
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    loadGoods() {
      this.$request.get('/seckill/product?id=' + this.seckillId)
          .then(res => {
            if (res.code === '200') {
              this.goodsData = res.data;
              console.log('Response:', res);
            } else {
              this.$message.error(res.msg);
              console.log('Error:', res.msg);
            }
          })
          .catch(err => {
            this.$message.error('请求失败，请稍后重试');
            console.log('Request Error:', err);
          })
    },
    handleClick(tab, event) {
      this.activeName = tab.name
    },
    secKill() {
      let data = {seckillId: this.seckillId, userId: this.user.id}
      console.log(data)
      this.$request.post('/seckill/seckill_p?seckillId=' + data.seckillId + '&userId=' + data.userId).then(res => {
        if (res.code === '200') {
          this.$message.success(res.data)
          this.buy()
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    addCart() {
      let data = {num: 1, userId: this.user.id, goodsId: this.goodsData.goodsId, businessId: this.goodsData.businessId}
      this.$request.post('/cart/add', data).then(res => {
        if (res.code === '200') {
          this.$message.success('生成订单')
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    buy(){
      this.addCart()
      console.log(this.goodsData.goodsId)
      this.$router.push({
        name: 'Check',
        query: { buyGoodsId: this.goodsData.goodsId }
      });

    },
    navTo(url) {
      location.href = url
    }
  }
}
</script>
