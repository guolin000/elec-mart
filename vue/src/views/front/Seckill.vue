<template>
  <div class="main-content">
    <div style="height: 60px; background-color: #C566F6FF"></div>
    <div style="display: flex">
      <div class="left"></div>
      <div style="width: 66%; background-color: white; margin-bottom: 50px">

        <!-- 秒杀倒计时 -->
        <div style="color: #FE0137FF; margin: 15px 0 15px 18px; font-weight: bold; font-size: 16px">秒杀倒计时</div>
        <div style="font-size: 24px; font-weight: bold; color: #FF5000FF; margin: 10px 18px">
          <span>{{ countdown }}</span>
        </div>

        <!-- 秒杀商品列表 -->
        <div style="margin-top: 30px">
          <div style="margin: 40px 0 0 15px; height: 40px; background-color: #04BF04FF; font-size: 20px; color: white; width: 130px; font-weight: bold; line-height: 40px; text-align: center; border-radius: 20px">
            秒杀商品
          </div>
          <div style="margin: 10px 5px 0 5px">
            <el-row>
              <el-col :span="5" v-for="item in flashSaleData" :key="item.id">
                <img @click="navTo('/front/detail?id=' + item.id)" :src="item.img" alt="" style="width: 100%; height: 175px; border-radius: 10px; border: #cccccc 1px solid">
                <div style="margin-top: 10px; font-weight: 500; font-size: 16px; width: 180px; color: #000000FF; text-overflow: ellipsis; overflow: hidden; white-space: nowrap;">
                  {{ item.name }}
                </div>
                <div style="margin-top: 5px; font-size: 20px; color: #FF5000FF">￥ {{ item.price }} / {{ item.unit }}</div>
                <el-button v-if="item.remaining > 0" type="primary" @click="buyNow(item.id)">抢购</el-button>
                <div v-else style="color: red; font-weight: bold">已售罄</div>
              </el-col>
            </el-row>
          </div>
        </div>
      </div>
      <div class="right"></div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      flashSaleData: [],  // 秒杀商品数据
      countdown: '',  // 倒计时字符串
      countdownTime: 0,  // 秒杀结束时间戳
    };
  },
  mounted() {
    this.loadFlashSaleData();
    this.startCountdown();
  },
  methods: {
    // 获取秒杀商品数据
    loadFlashSaleData() {
      this.$request.get('/flashsale/items').then(res => {
        if (res.code === '200') {
          this.flashSaleData = res.data;
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    // 倒计时逻辑
    startCountdown() {
      // 假设秒杀结束时间是服务器返回的一个时间戳
      this.countdownTime = new Date('2025-01-02T00:00:00').getTime(); // 设置秒杀结束时间
      this.updateCountdown();
      setInterval(this.updateCountdown, 1000);
    },
    // 更新倒计时
    updateCountdown() {
      const now = Date.now();
      const remainingTime = this.countdownTime - now;
      if (remainingTime <= 0) {
        this.countdown = '秒杀已结束';
      } else {
        const hours = Math.floor(remainingTime / 1000 / 60 / 60);
        const minutes = Math.floor((remainingTime % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((remainingTime % (1000 * 60)) / 1000);
        this.countdown = `${hours}:${minutes}:${seconds}`;
      }
    },
    // 跳转到商品详情页
    navTo(url) {
      this.$router.push(url);
    },
    // 处理秒杀抢购
    buyNow(itemId) {
      if (!this.user || !this.user.id) {
        this.$message.error('请先登录');
        return;
      }
      this.$request.post(`/flashsale/buy`, { userId: this.user.id, itemId })
          .then(res => {
            if (res.code === '200') {
              this.$message.success('抢购成功！');
              this.loadFlashSaleData();  // 重新加载商品数据
            } else {
              this.$message.error(res.msg);
            }
          });
    },
  }
};
</script>

<style scoped>
.main-content {
  min-height: 100vh;
  background-size: 100%;
  /*background-image: url('@/assets/imgs/img.png');*/
}
.left {
  width: 17%;
  background-repeat: no-repeat;
  /*background-image: url('@/assets/imgs/left-img.png');*/
}
.right {
  width: 17%;
  background-repeat: no-repeat;
  /*background-image: url('@/assets/imgs/right-img.png');*/
}
.el-col-5 {
  width: 20%;
  max-width: 20%;
  padding: 10px 10px;
}
</style>
