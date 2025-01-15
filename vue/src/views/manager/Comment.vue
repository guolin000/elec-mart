<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入评论内容" style="width: 200px" v-model="content"></el-input>
      <el-input placeholder="请输入评论用户名称" style="width: 200px;margin-left: 6px" v-model="userName"></el-input>
      <el-input placeholder="请输入商户名称" style="width: 200px;margin-left: 6px" v-model="businessName"></el-input>
      <el-input placeholder="请输入商品类别" style="width: 200px;margin-left: 6px" v-model="typeName"></el-input>

<!--      <el-select v-model="selectedCategory" placeholder="请选择分类" style="width: 150px; margin-left: 10px">-->
<!--        <el-option-->
<!--            v-for="item in categories"-->
<!--            :key="item.id"-->
<!--            :label="item.name"-->
<!--            :value="item.id">-->
<!--        </el-option>-->
<!--      </el-select>-->
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
        <el-table-column prop="goodsName" label="商品名称" show-overflow-tooltip></el-table-column>

        <!-- 新增商品分类列 -->
<!--        <el-table-column label="商品分类">-->
<!--          <template v-slot="scope">-->
<!--            {{ goodsCategoryMap[scope.row.goodsId] || '未知分类' }}-->
<!--          </template>-->
<!--        </el-table-column>-->
        <el-table-column prop="typeName" label="商品类别" show-overflow-tooltip></el-table-column>
        <el-table-column prop="content" label="评论内容" show-overflow-tooltip></el-table-column>
        <el-table-column prop="userName" label="评论用户" show-overflow-tooltip></el-table-column>
        <el-table-column prop="time" label="评论时间" show-overflow-tooltip></el-table-column>
        <el-table-column prop="businessName" label="商户" show-overflow-tooltip></el-table-column>
        <el-table-column label="操作" width="180" align="center">
          <template v-slot="scope">
            <el-button plain type="primary" size="mini" @click="updateStatus(scope.row, '待收货')">发货</el-button>
            <el-button plain type="danger" size="mini" @click="del(scope.row.id)">删除</el-button>
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
  name: "Comment",
  data() {
    return {
      tableData: [],        // 表格数据
      pageNum: 1,           // 当前页码
      pageSize: 10,         // 每页显示的个数
      total: 0,             // 总记录数
      userName:null,
      content: null,
      businessName: null,
      typeName: null,
      selectedCategory: null, // 当前选中的分类ID
      // categories: [],       // 分类列表
      goodsCategoryMap: {}, // 商品分类映射表（goodsId => typeName）
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      ids: []               // 选中行的 ID 数组
    }
  },
  created() {
    // this.loadCategories();       // 加载分类列表
    this.loadGoodsCategories();  // 加载商品分类映射
    this.load(1);                // 加载评论列表
  },
  methods: {
    // 加载商品分类信息
    loadGoodsCategories() {
      this.$request.get('/goods/selectPage', {
        params: {
          pageNum: 1,
          pageSize: 1000 // 假设商品总数不会超过 1000 个
        }
      }).then(res => {
        if (res.code === '200') {
          // 创建商品分类映射表：goodsId => typeName
          this.goodsCategoryMap = res.data?.list.reduce((map, item) => {
            map[item.id] = item.typeName; // 将商品ID与分类名称关联
            return map;
          }, {});
        } else {
          this.$message.error('加载商品分类失败');
        }
      });
    },

    // 加载分类列表
    // loadCategories() {
    //   this.$request.get('/type/selectPage', {
    //     params: {
    //       pageNum: 1,
    //       pageSize: 1000
    //     }
    //   }).then(res => {
    //     if (res.code === '200') {
    //       this.categories = res.data?.list.map(item => ({
    //         id: item.id,
    //         name: item.name
    //       }));
    //     } else {
    //       this.$message.error('加载分类失败');
    //     }
    //   });
    // },

    // 加载评论列表（带分页和分类筛选）
    load(pageNum) {
      if (pageNum) this.pageNum = pageNum;
      this.$request.get('/comment/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          content: this.content,
          userName: this.userName,
          typeName: this.typeName,
          businessName: this.businessName,
        }
      }).then(res => {
        if (res.code === '200') {
          let comments = res.data?.list;

          // 如果选择了分类，则根据 selectedCategory 进行筛选
          if (this.selectedCategory) {
            comments = comments.filter(comment => {
              const categoryName = this.goodsCategoryMap[comment.goodsId];
              return categoryName && categoryName === this.getCategoryNameById(this.selectedCategory);
            });
          }

          this.tableData = comments;
          this.total = comments.length;
        } else {
          this.$message.error(res.msg);
        }
      });
    },

    // 根据分类 ID 获取分类名称
    getCategoryNameById(categoryId) {
      const category = this.categories.find(item => item.id === categoryId);
      return category ? category.name : null;
    },

    // 重置查询条件
    reset() {
      this.content = null;
      this.selectedCategory = null;
      this.typeName = null;
      this.userName = null;
      this.businessName = null;
      this.load(1);
    },

    handleCurrentChange(pageNum) {
      this.load(pageNum);
    },

    handleSelectionChange(rows) {
      this.ids = rows.map(v => v.id); // 选中行的 ID 数组
    },

    del(id) {
      this.$confirm('您确定删除吗？', '确认删除', { type: "warning" }).then(() => {
        this.$request.delete('/comment/delete/' + id).then(res => {
          if (res.code === '200') {
            this.$message.success('操作成功');
            this.load(1);
          } else {
            this.$message.error(res.msg);
          }
        });
      }).catch(() => { });
    },

    delBatch() {
      if (!this.ids.length) {
        this.$message.warning('请选择数据');
        return;
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', { type: "warning" }).then(() => {
        this.$request.delete('/comment/delete/batch', { data: this.ids }).then(res => {
          if (res.code === '200') {
            this.$message.success('操作成功');
            this.load(1);
          } else {
            this.$message.error(res.msg);
          }
        });
      }).catch(() => { });
    },

    updateStatus(row, status) {
      this.form = row;
      this.form.status = status;
      this.save();
    }
  }
}
</script>

<style scoped>
.search {
  margin-bottom: 20px;
}
.operation {
  margin-bottom: 20px;
}
.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>