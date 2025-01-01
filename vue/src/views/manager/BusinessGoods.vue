<template>
  <div>
    <div class="search">
      <el-input placeholder="请输入商品名称查询" style="width: 200px" v-model="name"></el-input>
      <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
      <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
    </div>

    <div class="operation">
      <el-button type="primary" plain @click="handleAdd">发布商品</el-button>
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
      <el-button type="success" plain @click="toggleBatchGoodsUp">批量上下架</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>
        <el-table-column label="商品主图">
          <template v-slot="scope">
            <div style="display: flex; align-items: center">
              <el-image style="width: 40px; height: 40px; " v-if="scope.row.img"
                        :src="scope.row.img" :preview-src-list="[scope.row.img]"></el-image>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" show-overflow-tooltip></el-table-column>
        <el-table-column prop="description" label="商品描述">
          <template v-slot="scope">
            <el-button type="success" size="small" @click="viewEditor(scope.row.description)">点击查看</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="商品价格" show-overflow-tooltip></el-table-column>
<!--        <el-table-column prop="unit" label="计件单位" show-overflow-tooltip></el-table-column>-->
        <el-table-column prop="typeName" label="商品分类" show-overflow-tooltip></el-table-column>
<!--        <el-table-column prop="businessName" label="所属商家" show-overflow-tooltip></el-table-column>-->

        <!-- 优化商品销量列宽度 -->
        <el-table-column prop="count" label="商品销量" show-overflow-tooltip width="100px" align="center"></el-table-column>

        <!-- 状态列 -->
        <el-table-column prop="status" label="状态"  show-overflow-tooltip align="center"></el-table-column>


        <!-- 显示数据库中的上下架状态 -->
        <el-table-column prop="goods_up" label="上下架" align="center" width="120">
          <template v-slot="scope">
            <el-tag :type="scope.row.goods_up ? 'success' : 'info'">
              {{ scope.row.goods_up ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 操作列，增加上架按钮 -->
        <el-table-column label="操作" width="200" align="center">
          <template v-slot="scope">
            <el-button-group>
              <!-- 编辑按钮 -->
              <el-button
                  plain
                  type="primary"
                  @click="handleEdit(scope.row)"
                  size="mini"
                  style="margin-right: 5px;">编辑</el-button>
              <!-- 删除按钮 -->
              <el-button
                  plain
                  type="danger"
                  size="mini"
                  style="margin-right: 5px;"
                  @click="del(scope.row.id)">删除</el-button>
              <!-- 上架按钮 -->
              <el-button
                  v-if="!scope.row.goods_up"
                  plain
                  type="success"
                  size="mini"
                  @click="handleGoodsUp(scope.row)">上架</el-button>
            </el-button-group>
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

    <el-dialog title="信息" :visible.sync="fromVisible" width="40%" :close-on-click-modal="false" destroy-on-close @close="cancel">
      <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules" ref="formRef">
        <el-form-item label="商品主图">
          <el-upload
              class="avatar-uploader"
              :action="$baseUrl + '/files/upload'"
              :headers="{ token: user.token }"
              list-type="picture"
              :on-success="handleAvatarSuccess">
            <el-button type="primary">上传图片</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item prop="name" label="商品名称">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="price" label="商品价格">
          <el-input v-model="form.price" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="typeId" label="商品分类">
          <el-select v-model="form.typeId" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="item in typeData" :label="item.name" :value="item.id" :key="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="unit" label="计件单位">
          <el-input v-model="form.unit" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="description" label="商品介绍">
          <div id="editor" style="width: 100%"></div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>

    <el-dialog title="商品介绍" :visible.sync="editorVisible" width="50%">
      <div v-html="this.viewData" class="w-e-text"></div>
    </el-dialog>
  </div>
</template>


<script>
import E from 'wangeditor'

let editor
function initWangEditor(content) {	setTimeout(() => {
  if (!editor) {
    editor = new E('#editor')
    editor.config.placeholder = '请输入内容'
    editor.config.uploadFileName = 'file'
    editor.config.uploadImgServer = 'http://localhost:9090/files/wang/upload'
    editor.create()
  }
  editor.txt.html(content)
}, 0)
}

export default {
  name: "BusinesssGoods",
  data() {
    return {
      tableData: [],  // 所有的数据
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      name: null,
      fromVisible: false,
      editorVisible: false,
      form: {},
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        title: [
          {required: true, name: '请输入商品名称', trigger: 'blur'},
        ],
        content: [
          {required: true, img: '请输上传商品图主图', trigger: 'blur'},
        ]
      },
      ids: [],
      typeData: [],
      viewData: null
    }
  },
  created() {
    this.load(1)
    this.loadType()
  },
  methods: {
    loadType() {
      this.$request.get('/type/selectAll').then(res => {
        if (res.code === '200') {
          this.typeData = res.data
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleAdd() {   // 新增数据
      if ('审核通过' !== this.user.status) {
        this.$message.warning('您的店铺信息还未审核通过，暂时不允许发布商品')
        return
      }
      this.form = {}  // 新增数据的时候清空数据
      initWangEditor('')
      this.fromVisible = true   // 打开弹窗
    },
    handleEdit(row) {   // 编辑数据
      this.form = JSON.parse(JSON.stringify(row))  // 给form对象赋值  注意要深拷贝数据
      initWangEditor(this.form.description || '')
      this.fromVisible = true   // 打开弹窗
    },
    viewEditor(content) {
      this.viewData = content
      this.editorVisible = true
    },
    cancel() {
      this.fromVisible = false
      location.href = '/goods'
    },
    save() {   // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.form.description = editor.txt.html()
          this.$request({
            url: this.form.id ? '/goods/update' : '/goods/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {  // 表示成功保存
              this.$message.success('保存成功')
              // this.load(1)
              this.fromVisible = false
              location.href = '/goods'
            } else {
              this.$message.error(res.msg)  // 弹出错误的信息
            }
          })
        }
      })
    },
    del(id) {   // 单个删除
      this.$confirm('您确定删除吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/goods/delete/' + id).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
    handleSelectionChange(rows) {   // 当前选中的所有的行数据
      this.ids = rows.map(v => v.id)   //  [1,2]
    },
    delBatch() {   // 批量删除
      if (!this.ids.length) {
        this.$message.warning('请选择数据')
        return
      }
      this.$confirm('您确定批量删除这些数据吗？', '确认删除', {type: "warning"}).then(response => {
        this.$request.delete('/goods/delete/batch', {data: this.ids}).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success('操作成功')
            this.load(1)
          } else {
            this.$message.error(res.msg)  // 弹出错误的信息
          }
        })
      }).catch(() => {
      })
    },
    toggleBatchGoodsUp() {
      if (!this.ids.length) {
        this.$message.warning('请选择数据');
        return;
      }
      this.$confirm('您确定批量切换这些商品的上下架状态吗？', '确认操作', { type: 'warning' }).then(() => {
        this.$request.put('/goods/toggleUp/batch', { data: this.ids }).then((res) => {
          if (res.code === '200') {
            this.$message.success('操作成功');
            this.load(1);
          } else {
            this.$message.error(res.msg);
          }
        });
      });
    },
    // 上架操作
    handleGoodsUp(row) {
      // 更新商品的上下架状态，发送请求到服务器或更新本地数据
      row.goods_up = true;  // 修改为上架状态
      this.updateGoodsStatus(row); // 调用接口或更新逻辑
    },

    // 更新商品的上下架状态（示例）
    updateGoodsStatus(row) {
      // 发送请求更新商品的状态
      axios.post('/updateGoodsStatus', { id: row.id, goods_up: row.goods_up })
          .then(response => {
            // 更新成功后的处理
            this.$message.success('商品已上架');
          })
          .catch(error => {
            // 错误处理
            this.$message.error('上架失败');
          });
    },
    toggleGoodsUp(row) {
      this.$request.put('/goods/toggleUp', { id: row.id, goods_up: row.goods_up }).then((res) => {
        if (res.code === '200') {
          this.$message.success('操作成功');
          this.load(1);
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    load(pageNum) {  // 分页查询
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/goods/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        console.log(res.data)
        this.tableData = res.data?.list
        this.total = res.data?.total
      })
    },
    reset() {
      this.name = null
      this.load(1)
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    handleAvatarSuccess(response, file, fileList) {
      this.form.img = response.data
    },
  }
}
</script>

<style scoped>
.search {
  display: flex;
  margin-bottom: 15px;
}

.operation {
  margin-bottom: 15px;
}

.table {
  margin-bottom: 15px;
}

.pagination {
  text-align: right;
  margin-top: 10px;
}
/*.el-switch__core{*/
/*  width: 35px !important;*/
/*}*/

.el-switch{
  font-size: 10px !important; /* 设置文本字号 */
}
.el-switch__label{
  font-size: 10px !important; /* 设置文本字号 */
}
</style>