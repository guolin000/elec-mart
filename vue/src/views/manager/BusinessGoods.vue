<template>
  <div>
    <div class="top">
      <div class="search" style="margin-left: 25px">
        <el-input placeholder="请输入商品名称查询" style="width: 200px" v-model="name"></el-input>
        <el-button type="info" plain style="margin-left: 10px" @click="load(1)">查询</el-button>
        <el-button type="warning" plain style="margin-left: 10px" @click="reset">重置</el-button>
      </div>
      <div class="filters">
        <el-select v-model="status" placeholder="请选择状态" style="width: 150px; margin-right: 10px;">
          <el-option label="已审核" value="已审核"></el-option>
          <el-option label="待审核" value="待审核"></el-option>
        </el-select>
        <el-select v-model="goodsUp" placeholder="请选择上下架状态" style="width: 150px;">
          <el-option label="已上架" value="true"></el-option>
          <el-option label="已下架" value="false"></el-option>
        </el-select>
        <el-button type="primary" style="margin-left: 10px" plain @click="choose">筛选</el-button>
      </div>
    </div>
    <div class="operation">
      <el-button type="primary" plain @click="handleAdd">发布商品</el-button>
      <el-button type="danger" plain @click="delBatch">批量删除</el-button>
      <el-button type="success" plain @click="toggleBatchGoodsUp">批量上下架</el-button>
      <el-button id="seckill-button" plain @click="openSeckillDialog" style="background-color: #ff5e5e; color: white;">上架秒杀</el-button>
      <el-button id="cancel-button" plain @click="seckillcancel" style="background-color: dodgerblue; color: white;">下架秒杀</el-button>
    </div>

    <div class="table">
      <el-table :data="tableData" stripe @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center"></el-table-column>
<!--        <el-table-column prop="id" label="序号" width="80" align="center" sortable></el-table-column>-->
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
        <el-table-column prop="count" label="商品库存" show-overflow-tooltip></el-table-column>

        <el-table-column prop="typeName" label="商品分类" show-overflow-tooltip></el-table-column>
<!--        <el-table-column prop="businessName" label="所属商家" show-overflow-tooltip></el-table-column>-->

        <!-- 优化商品销量列宽度 -->
        <el-table-column prop="count" label="商品销量" show-overflow-tooltip width="100px" align="center"></el-table-column>

        <!-- 状态列 -->
        <el-table-column prop="status" label="状态"  show-overflow-tooltip align="center"></el-table-column>


        <!-- 显示数据库中的上下架状态 -->
        <el-table-column prop="goods_up" label="上下架" align="center" width="120">
          <template v-slot="scope">
            <el-tag :type="scope.row.goodsUp === 'true' ? 'success' : 'info'">
              {{ scope.row.goodsUp === 'true' ? '已上架' : '已下架' }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 操作列，增加上架按钮 -->
        <!-- 操作列，增加上下架按钮 -->
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
              <!-- 上下架按钮 -->
              <el-button
                  :plain="true"
                  :type="scope.row.goodsUp === 'true' ? 'danger' : 'success'"
                  size="mini"
                  @click="handleGoodsUp(scope.row)">
                {{ scope.row.goodsUp === 'true' ? '下架' : '上架' }}
              </el-button>
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

      <el-dialog title="上架秒杀" :visible.sync="seckillDialogVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
        <el-form :model="seckillForm" :rules="seckillRules" ref="seckillFormRef">
          <el-form-item label="秒杀开始时间" prop="startTime">
            <el-date-picker
                v-model="seckillForm.startTime"
                type="datetime"
                placeholder="选择开始时间"
                style="width: 100%;"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="秒杀结束时间" prop="endTime">
            <el-date-picker
                v-model="seckillForm.endTime"
                type="datetime"
                placeholder="选择结束时间"
                style="width: 100%;"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="秒杀价格" prop="seckillPrice">
            <el-input v-model="seckillForm.seckillPrice" placeholder="输入秒杀价格"></el-input>
          </el-form-item>
          <el-form-item label="放入数量" prop="num">
            <el-input v-model="seckillForm.num" placeholder="输入数量"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="cancelSeckill">取消</el-button>
          <el-button type="primary" @click="saveSeckill">确定</el-button>
        </div>
      </el-dialog>

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
        <el-form-item prop="typeId" label="一级分类">
          <el-select   v-model="form.secondTypeId" placeholder="请选择分类" style="width: 100%" @change="loadSecondTypes">
            <el-option v-for="item in typeData" :label="item.name" :value="item.id" :key="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item prop="secondTypeData" label="二级分类">
          <el-select v-model="form.typeId" placeholder="请选择分类" style="width: 100%"
                     :disabled="!secondTypeData.length"
          >
            <el-option v-for="item in secondTypeData" :label="item.name" :value="item.id" :key="item.id"></el-option>
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
      totalData:[],
      tableData: [],  // 所有的数据
      pageNum: 1,     // 当前的页码
      pageSize: 10,   // 每页显示的个数
      total: 0,
      name: null,     // 商品名称搜索条件
      status: null,   // 状态筛选条件
      goodsUp: null,  // 上下架状态筛选条件
      fromVisible: false,
      editorVisible: false,
      // 表单数据
      seckillForm: {
        startTime: null,
        endTime: null,
        seckillPrice: null,
        num: null,
      },
      // 秒杀表单的校验规则
      seckillRules: {
        startTime: [{ required: true, message: '请选择秒杀开始时间', trigger: 'blur' }],
        endTime: [{ required: true, message: '请选择秒杀结束时间', trigger: 'blur' }],
        seckillPrice: [{ required: true, message: '请输入秒杀价格', trigger: 'blur' }],
      },
      // 控制秒杀弹窗的显示
      seckillDialogVisible: false,
      ids: [],  // 选中的商品ID
      form: {
        typeId: null,  // 一级分类ID
        secondTypeId: null, // 二级分类ID
      },
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      rules: {
        name: [
          { required: true, message: '请输入商品名称', trigger: 'blur' },
        ],
        price: [
          { required: true, message: '请输入商品价格', trigger: 'blur' },
        ],
        typeId: [
          { required: true, message: '请选择二级分类', trigger: 'blur' },
        ],
        unit: [
          { required: true, message: '请输入计件单位', trigger: 'blur' },
        ],
        // description: [
        //   { required: true, message: '请输入商品介绍', trigger: 'change' },
        // ],
        secondTypeId: [
          { required: true, message: '请选择一级分类', trigger: 'blur' },
        ],
      },
      typeData: [],
      secondTypeData: [],
      viewData: null,
    };
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
    loadSecondTypes(typeId) {
      if (!typeId) {
        this.secondTypeData = []
        this.form.secondTypeId = null
        return
      }
      this.$request.get('/secondType/selectByTypeId/' + typeId).then(res => {
        if (res.code === '200') {
          this.secondTypeData = res.data
        } else {
          this.$message.error(res.msg)
          this.secondTypeData = []
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
      location.href = '/businessGoods'
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
              location.href = '/businessGoods'
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
    // 批量上下架操作
    toggleBatchGoodsUp() {
      if (!this.ids.length) {
        this.$message.warning('请选择数据');
        return;
      }

      // 检查选中商品的状态是否一致
      const selectedRows = this.tableData.filter(item => this.ids.includes(item.id));
      const allUp = selectedRows.every(item => item.goodsUp === 'true');
      const allDown = selectedRows.every(item => item.goodsUp === 'false');

      if (!allUp && !allDown) {
        this.$message.warning('请选择状态一致的商品进行批量操作');
        return;
      }

      // 确定操作类型
      const action = allUp ? '下架' : '上架';
      const confirmMessage = `您确定要批量${action}这些商品吗？`;

      this.$confirm(confirmMessage, `确认批量${action}商品`, { type: "warning" }).then(() => {
        // 发送请求更新商品的状态
        this.$request.put('/goods/toggleUp/batch', { ids: this.ids, goodsUp: action === '下架' ? 'false' : 'true' }).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success(`批量${action}成功`);
            // 更新本地数据状态
            selectedRows.forEach(item => {
              item.goodsUp = action === '下架' ? 'false' : 'true';
            });
            this.load(this.pageNum); // 重新加载当前页数据
          } else {
            this.$message.error(res.msg);  // 弹出错误的信息
          }
        })
      }).catch(() => {
        // 用户取消操作
      });
    },
    // 上下架操作
    handleGoodsUp(row) {
      // 根据当前状态确定操作类型
      const action = row.goodsUp === 'true' ? '下架' : '上架';
      const confirmMessage = `您确定要${action}该商品吗？`;

      this.$confirm(confirmMessage, `确认${action}商品`, { type: "warning" }).then(() => {
        // 发送请求更新商品的状态
        this.$request.put('/goods/toggle', { id: row.id, goodsUp: action === '下架' ? 'false' : 'true' }).then(res => {
          if (res.code === '200') {   // 表示操作成功
            this.$message.success(`${action}成功`);
            // 更新本地数据状态
            row.goodsUp = action === '下架' ? 'false' : 'true';
          } else {
            this.$message.error(res.msg);  // 弹出错误的信息
          }
        })
      }).catch(() => {
        // 用户取消操作
      });
    },

    // 更新商品的上下架状态（示例）
    updateGoodsStatus(id) {
      // 发送请求更新商品的状态
      this.$confirm('您确定上架该商品吗？', '确认上架商品', {type: "warning"}).then(response => {
        this.$request.put('/goods/toggle/' + id).then(res => {
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
        this.totalData = this.tableData
        this.total = res.data?.total
      })
    },
    choose() {
      this.applyFilters();  // 应用筛选条件
    },
    applyFilters() {
      let filteredData = this.totalData;

      // 根据状态筛选
      if (this.status) {
        filteredData = filteredData.filter(item => item.status === this.status);
      }
      // console.log("jjjj" );
      // console.log(filteredData);
      // 根据上下架状态筛选
      if (this.goodsUp !== null) {
        filteredData = filteredData.filter(item => item.goodsUp === this.goodsUp);
      }
      console.log(filteredData);
      // 更新 tableData
      this.tableData = filteredData;

      // 重置页码
      this.pageNum = 1;
    },
    reset() {
      this.name = null;
      this.status = null;
      this.goodsUp = null;
      this.load(1);
    },
    handleCurrentChange(pageNum) {
      this.load(pageNum)
    },
    handleAvatarSuccess(response, file, fileList) {
      this.form.img = response.data
    },
    // 打开秒杀表单弹窗
    openSeckillDialog() {
      if (!this.ids.length) {
        this.$message.warning('请选择商品');
        return;
      }
      if (this.ids.length>1) {
        this.$message.warning('不允许批量操作！');
        return;
      }
      this.seckillForm = { startTime: null, endTime: null, seckillPrice: null };  // 重置表单数据
      this.seckillDialogVisible = true;  // 显示弹窗
    },
    // 取消秒杀操作
    cancelSeckill() {
      this.seckillDialogVisible = false;
    },
    // 保存秒杀数据
    saveSeckill() {
      // 获取年月日时分秒
      const year = this.seckillForm.startTime.getFullYear();
      const month = (this.seckillForm.startTime.getMonth() + 1).toString().padStart(2, '0');  // 月份是从0开始的，所以需要加1
      const day = this.seckillForm.startTime.getDate().toString().padStart(2, '0');
      const hours = this.seckillForm.startTime.getHours().toString().padStart(2, '0');
      const minutes = this.seckillForm.startTime.getMinutes().toString().padStart(2, '0');
      const seconds = this.seckillForm.startTime.getSeconds().toString().padStart(2, '0');
      // 拼接成所需格式
      const startTime = `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
      // 获取年月日时分秒
      const year1 = this.seckillForm.endTime.getFullYear();
      const month1 = (this.seckillForm.endTime.getMonth() + 1).toString().padStart(2, '0');  // 月份是从0开始的，所以需要加1
      const day1 = this.seckillForm.endTime.getDate().toString().padStart(2, '0');
      const hours1 = this.seckillForm.endTime.getHours().toString().padStart(2, '0');
      const minutes1 = this.seckillForm.endTime.getMinutes().toString().padStart(2, '0');
      const seconds1 = this.seckillForm.endTime.getSeconds().toString().padStart(2, '0');
      // 拼接成所需格式
      const endTime = `${year1}-${month1}-${day1} ${hours1}:${minutes1}:${seconds1}`;
      let data = {
        startDate: startTime,
        endDate: endTime,
        price: this.seckillForm.seckillPrice,
        num: this.seckillForm.num,
      }
      this.$request.post('/seckill/add?goodsId=' + this.ids.pop() + '&startDate=' + data.startDate + '&endDate=' + data.endDate
          + '&price=' + data.price + '&num=' + data.num).then(res => {
        if (res.code === '200') {
          this.$message.success(res.data);
          this.seckillDialogVisible = false;  // 关闭弹窗
          this.load(1)
          // 可以根据需要重新加载商品数据
        } else {
          this.$message.error(res.msg);
        }
      }).catch(err => {
        this.$message.error('保存失败');
      });
    },
    seckillcancel(){
      if (!this.ids.length) {
        this.$message.warning('请选择商品');
        return;
      }
      if (this.ids.length>1) {
        this.$message.warning('不允许批量操作！');
        return;
      }
      this.$request.get('/seckill/delete?ids=' + this.ids.pop()).then(res => {
        if (res.code === '200') {
          this.$message.success(res.data)
          this.load(1)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
  }
}
</script>

<style scoped>
.top{
  display: flex;
  flex-direction: row;
  justify-content: space-around;
  background:#fff ;
  margin-bottom: 15px;
}
.search {
  display: flex;
}
.filters{
  margin-top: 10px;
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