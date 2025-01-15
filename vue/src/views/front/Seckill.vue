<template>
  <div class="main-content">
    <div style="display: flex; width: 70%; background-color: white; margin: 30px auto; border-radius: 20px">
      <div style="flex: 1; padding: 0 20px">
        <div class="section-container">
          <lottie-animation
              :animation-data="animationData"
              :loop="true"
              :autoplay="true"
              style="width:80px; height:80px;"
          ></lottie-animation>
          <div style="font-size: 22px; color: #000000FF; line-height: 80px; border-bottom: #cccccc 1px solid;font-weight: bolder">秒杀专区</div>

        </div>
        <div style="margin: 20px 0">
          <el-row :gutter="20">
            <el-col :span="6" style="margin-bottom: 20px" v-for="item in seckillData" :key="item.id">
              <img :src="item.img" alt="" style="width: 100%; height: 175px; border-radius: 10px; border: #cccccc 1px solid">

              <!-- 商品名称 -->
              <div style="margin-top: 10px; font-weight: 500; font-size: 16px; width: 180px; color: #000000FF; text-overflow: ellipsis; overflow: hidden; white-space: nowrap;">
                {{item.name}}
              </div>

              <!-- 原价和秒杀价 -->
              <div style="margin-top: 5px; font-size: 20px; color: #FF5000FF">
                <span style="text-decoration: line-through; color: #999; font-size: 16px;">￥ {{item.price}}</span>
                <span> ￥ {{item.seckillPrice}} / {{item.unit}}</span>
              </div>

              <!-- 立即抢购按钮 -->
              <el-button
                  type="primary"
                  style="margin-top: 10px; width: 100%; background-color: #FF5000; border-color: #FF5000;"
                  @click="navTo('/front/product?id=' + item.seckillId)">
                立即抢购
              </el-button>
            </el-col>
          </el-row>

        </div>
      </div>
    </div>
  </div>
</template>

<script>

import * as animationData from "@/assets/清仓.json";
import LottieAnimation from "@/components/LottieAnimation";
import Lottie from "lottie-web";

export default {

  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      seckillData: [],
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
    this.loadseckill()
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    loadseckill() {
      this.$request.get('/seckill/products_d').then(res => {
        // 打印整个返回结果
        console.log('Response:', res);

        if (res.code === '200') {
          this.seckillData = res.data;
          // 打印seckillData内容
          console.log('Seckill Data:', res.data);
        } else {
          this.$message.error(res.msg);
          // 打印错误信息
          /*console.log('Error:', res.msg);*/
        }
      }).catch(error => {
        // 捕获请求错误并打印
        console.error('Request failed:', error);
      });
    },
    navTo(url) {
      location.href = url
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
