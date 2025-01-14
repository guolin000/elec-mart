<template>
  <div class="main-content">
    <div class="container">
      <!-- 左侧内容区域 -->
      <div class="left-section">
        <!-- 二级分类列表 -->
        <div class="second-type-list">
          <div
              v-for="secondType in secondTypes"
              :key="secondType.id"
              @click="selectSecondType(secondType.id)"
              :class="['second-type-item', { 'active': secondType.id === selectedSecondTypeId }]"
          >
            {{ secondType.name }}
          </div>
        </div>

        <!-- 商品列表 -->
        <div class="goods-list">
          <el-row :gutter="20">
            <el-col
                :span="6"
                class="goods-item"
                v-for="item in goodsData"
                :key="item.id"
            >
              <img
                  @click="navTo('/front/detail?id=' + item.id)"
                  :src="item.img"
                  alt=""
                  class="goods-image"
              >
              <div class="goods-name">{{ item.name }}</div>
              <div class="goods-price">￥ {{ item.price }} / {{ item.unit }}</div>
            </el-col>
          </el-row>
        </div>
      </div>

      <!-- 右侧猜你喜欢部分 -->
      <div class="right-section">
        <div class="recommend-title">猜你喜欢</div>
        <div class="recommend-list">
          <div
              class="recommend-item"
              v-for="item in recommendData"
              :key="item.id"
          >
            <img
                @click="navTo('/front/detail?id=' + item.id)"
                :src="item.img"
                alt=""
                class="recommend-image"
            >
            <div class="recommend-name">{{ item.name }}</div>
            <div class="recommend-price">￥ {{ item.price }} / {{ item.unit }}</div>
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
      typeId: this.$route.query.id || null, // 获取一级分类ID
      goodsData: [],          // 存储商品数据
      recommendData: [],      // 存储推荐商品数据
      secondTypes: [],        // 存储二级分类列表
      selectedSecondTypeId: null // 当前选中的二级分类ID
    }
  },
  mounted() {
    if (this.typeId) {
      this.loadType()
      this.loadRecommend()
    } else {
      this.$message.error('分类ID不存在')
    }
  },
  methods: {
    // 加载推荐商品
    loadRecommend() {
      this.$request.get('/goods/recommend')
          .then(res => {
            if (res.code === '200') {
              this.recommendData = res.data
            } else {
              this.$message.error(res.msg)
            }
          })
          .catch(error => {
            console.error('加载推荐商品失败:', error)
            this.$message.error('加载推荐商品失败')
          })
    },

    // 加载二级分类
    loadType() {
      this.$request.get('/secondType/selectByTypeId/' + this.typeId)
          .then(res => {
            if (res.code === '200') {
              this.secondTypes = res.data
              if (this.secondTypes.length > 0) {
                this.selectedSecondTypeId = this.secondTypes[0].id // 默认选择第一个二级分类
                this.loadGoods(this.selectedSecondTypeId) // 加载选中二级分类的商品
              } else {
                this.goodsData = [] // 如果没有二级分类，则清空商品列表
                this.$message.info('暂无二级分类')
              }
            } else {
              this.$message.error(res.msg)
            }
          })
          .catch(error => {
            console.error('加载二级分类失败:', error)
            this.$message.error('加载二级分类失败')
          })
    },

    // 加载商品数据
    loadGoods(secondTypeId) {
      if (!secondTypeId) {
        this.goodsData = []
        return
      }
      this.$request.get('/goods/selectByTypeId?id=' + secondTypeId)
          .then(res => {
            if (res.code === '200') {
              this.goodsData = res.data
            } else {
              this.$message.error(res.msg)
            }
          })
          .catch(error => {
            console.error('加载商品失败:', error)
            this.$message.error('加载商品失败')
          })
    },

    // 选择二级分类时更新商品列表
    selectSecondType(secondTypeId) {
      if (secondTypeId === this.selectedSecondTypeId) return // 如果点击的是当前选中的分类，则不做处理
      this.selectedSecondTypeId = secondTypeId
      this.loadGoods(secondTypeId) // 加载选中二级分类的商品
    },

    // 导航到指定URL
    navTo(url) {
      this.$router.push(url)
    }
  }
}
</script>

<style scoped>
.container {
  display: flex;
  width: 70%;
  background-color: white;
  margin: 30px auto;
  border-radius: 20px;
  padding: 20px;
}

.left-section {
  flex: 1;
  padding: 0 20px;
}

.second-type-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #cccccc;
}

.second-type-item {
  padding: 8px 16px;
  background-color: #f5f5f5;
  border-radius: 20px;
  cursor: pointer;
  transition: background-color 0.3s, color 0.3s;
  white-space: nowrap;
  font-size: 16px;
  color: #000;
}

.second-type-item:hover {
  background-color: #409EFF;
  color: white;
}

.second-type-item.active {
  background-color: #409EFF; /* Element UI 的主色 */
  color: white;
}

.goods-list {
  margin: 20px 0;
}

.goods-item {
  margin-bottom: 20px;
  text-align: center;
}

.goods-image {
  width: 100%;
  height: 175px;
  border-radius: 10px;
  border: 1px solid #cccccc;
  cursor: pointer;
  object-fit: cover;
}

.goods-name {
  margin-top: 10px;
  font-weight: 500;
  font-size: 16px;
  width: 180px;
  color: #000;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
  margin: 10px auto 0;
}

.goods-price {
  margin-top: 5px;
  font-size: 20px;
  color: #FF5000;
}

.right-section {
  width: 250px;
  padding: 0 20px;
  border-left: 1px solid #cccccc;
}

.recommend-title {
  font-size: 18px;
  color: #000;
  border-bottom: 1px solid #cccccc;
  padding-bottom: 10px;
}

.recommend-list {
  margin: 20px 0;
}

.recommend-item {
  margin-bottom: 20px;
  text-align: center;
}

.recommend-image {
  width: 100%;
  height: 175px;
  border-radius: 10px;
  border: 1px solid #cccccc;
  cursor: pointer;
  object-fit: cover;
}

.recommend-name {
  margin-top: 10px;
  font-weight: 500;
  font-size: 16px;
  width: 180px;
  color: #000;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
  margin: 10px auto 0;
}

.recommend-price {
  margin-top: 5px;
  font-size: 20px;
  color: #FF5000;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .container {
    width: 90%;
  }
}

@media (max-width: 768px) {
  .container {
    flex-direction: column;
    width: 95%;
  }

  .right-section {
    width: 100%;
    border-left: none;
    border-top: 1px solid #cccccc;
    padding-top: 20px;
  }
}
</style>
