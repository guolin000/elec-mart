<template>
  <div class="main-content">
    <div style="display: flex; width: 70%; background-color: white; margin: 30px auto; border-radius: 20px">
      <div style="flex: 1; padding: 0 20px">
        <div style="font-size: 18px; color: #000000FF; line-height: 80px; border-bottom: #cccccc 1px solid">
          <div
              v-for="secondType in secondTypes"
              :key="secondType.id"
              @click="selectSecondType(secondType.id)"
              :class="['second-type-item', { 'active': secondType.id === selectedSecondTypeId }]"
          >
            {{ secondType.name }}
          </div>
        </div>
        <div style="margin: 20px 0">
          <el-row :gutter="20">
            <el-col :span="6" style="margin-bottom: 20px" v-for="item in goodsData">
              <img @click="navTo('/front/detail?id=' + item.id)" :src="item.img" alt="" style="width: 100%; height: 175px; border-radius: 10px; border: #cccccc 1px solid">
              <div style="margin-top: 10px; font-weight: 500; font-size: 16px; width: 180px; color: #000000FF; text-overflow: ellipsis; overflow: hidden; white-space: nowrap;">{{item.name}}</div>
              <div style="margin-top: 5px; font-size: 20px; color: #FF5000FF">￥ {{item.price}} / {{item.unit}}</div>
            </el-col>
          </el-row>
        </div>
      </div>
      <div style="width: 250px; padding: 0 20px; border-left: #cccccc 1px solid">
        <div style="font-size: 18px; color: #000000FF; line-height: 80px; border-bottom: #cccccc 1px solid">猜你喜欢</div>
        <div style="margin: 20px 0; padding: 0 10px">
          <div style="margin-bottom: 20px" v-for="item in recommendData">
            <img @click="navTo('/front/detail?id=' + item.id)" :src="item.img" alt="" style="width: 100%; height: 175px; border-radius: 10px; border: #cccccc 1px solid">
            <div style="margin-top: 10px; font-weight: 500; font-size: 16px; width: 180px; color: #000000FF; text-overflow: ellipsis; overflow: hidden; white-space: nowrap;">{{item.name}}</div>
            <div style="margin-top: 5px; font-size: 20px; color: #FF5000FF">￥ {{ item.price }} / {{item.unit}}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

export default {

  data() {
    let typeId = this.$route.query.id
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      typeId: typeId,
      goodsData: [],
      recommendData: [],
      typeData: {},
      secondTypes:[],// 存储二级分类列表
      selectedSecondTypeId: null // 当前选中的二级分类ID
    }
  },
  mounted() {
    this.loadGoods()
    this.loadType()
    this.loadRecommend()
  },
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    loadRecommend() {
      this.$request.get('/goods/recommend').then(res => {
        if (res.code === '200') {
          this.recommendData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    loadType() {
      this.$request.get('/secondType/selectByTypeId/' + this.typeId).then(res => {
        if (res.code === '200') {
          this.secondTypes = res.data
          if (this.secondTypes.length > 0) {
            this.selectedSecondTypeId = this.secondTypes[0].id // 默认选择第一个二级分类
            this.loadGoods(this.selectedSecondTypeId) // 加载选中二级分类的商品
          }
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    loadGoods(secondTypeId = this.selectedSecondTypeId) {
      this.$request.get('/goods/selectByTypeId?id=' + secondTypeId).then(res => {
        if (res.code === '200') {
          this.goodsData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    // 点击选择某个二级分类时，更新商品列表
    selectSecondType(secondTypeId) {
      this.selectedSecondTypeId = secondTypeId
      this.loadGoods(secondTypeId) // 加载选中二级分类的商品
    },

    navTo(url) {
      location.href = url
    }
  }
}
</script>
