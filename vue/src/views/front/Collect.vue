<template>
  <div class="main-content">
    <div style="width: 70%; background-color: white; margin: 30px auto; border-radius: 20px">
      <div style="padding-bottom: 10px">
        <div class="section-container" style="font-size: 18px; color: #000000FF; line-height: 80px; border-bottom: #cccccc 1px solid;">
          <div style=" display: flex; align-items: center; /* 垂直居中 */">
            <lottie-animation
                :animation-data="animationData2"
                :loop="true"
                :autoplay="true"
                style="width:50px; height:50px;"
            ></lottie-animation>
            <div >全部收藏（{{ collectData.length }}件）</div>
          </div>
          <lottie-animation
              :animation-data="animationData"
              :loop="true"
              :autoplay="true"
              style="width:100px; height:100px;"
          ></lottie-animation>
        </div>
        <div style="margin: 20px 0; padding: 0 50px">
          <div class="table">
            <el-table :data="collectData" strip>
              <el-table-column label="商品图片" width="120px">
                <template v-slot="scope">
                  <el-image style="width: 80px; height: 60px; border-radius: 3px" v-if="scope.row.goodsImg"
                            :src="scope.row.goodsImg" :preview-src-list="[scope.row.goodsImg]"></el-image>
                </template>
              </el-table-column>
              <el-table-column prop="goodsName" label="商品名称" width="350px"></el-table-column>
              <el-table-column prop="businessName" label="店铺名称"></el-table-column>
              <el-table-column prop="goodsPrice" label="商品价格"></el-table-column>
              <el-table-column label="操作" align="center" width="180">
                <template v-slot="scope">
                  <el-button size="mini" type="danger" plain @click="del(scope.row.id)">移除收藏</el-button>
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
import * as animationData from '../../assets/办公.json';
import * as animationData2 from '../../assets/星球.json';
import LottieAnimation from "@/components/LottieAnimation";
import Lottie from "lottie-web";

export default {

  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      collectData: [],
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
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
    this.loadCollect(1)
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    loadCollect(pageNum) {
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/collect/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
        }
      }).then(res => {
        if (res.code === '200') {
          this.collectData = res.data?.list
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
      this.$request.delete('/collect/delete/' + id).then(res => {
        if (res.code === '200') {
          this.$message.success('移除成功')
          this.loadCollect(1)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleCurrentChange(pageNum) {
      this.loadCollect(pageNum)
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
