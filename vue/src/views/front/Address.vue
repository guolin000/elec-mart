<template>
  <div class="main-content">
    <div style="width: 70%; background-color: white; margin: 30px auto; border-radius: 20px">
      <div style="padding-bottom: 10px">
        <div style="display: flex; font-size: 18px; color: #000000FF; line-height: 80px; border-bottom: #cccccc 1px solid;">
          <div class="section-container" style="flex: 3; margin-left: 20px" >
            <lottie-animation
                :animation-data="animationData"
                :loop="true"
                :autoplay="true"
                style="width:50px; height:50px;"
            ></lottie-animation>
            <div >我的地址</div>
          </div>
          <div style="flex: 1; text-align: right; padding-right: 20px">
            <el-button type="warning" round @click="addAddress">添加收货地址</el-button>
          </div>
        </div>
        <div style="margin: 20px 0; padding: 0 50px">
          <div class="table">
            <el-table :data="addressData" strip>
              <el-table-column prop="username" label="收货人" width="350px"></el-table-column>
              <el-table-column prop="useraddress" label="收货地址"></el-table-column>
              <el-table-column prop="phone" label="联系电话"></el-table-column>
              <el-table-column label="操作" align="center" width="180">
                <template v-slot="scope">
                  <el-button size="mini" type="primary" plain @click="editAddress(scope.row)">编辑</el-button>
                  <el-button size="mini" type="danger" plain @click="del(scope.row.id)">删除</el-button>
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
    <el-dialog title="地址信息" :visible.sync="formVisible" width="40%" :close-on-click-modal="false" destroy-on-close>
      <el-form label-width="100px" style="padding-right: 50px" :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username" label="收货人">
          <el-input v-model="form.username" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="useraddress1" label="选择地址">
          <el-cascader
              size="large"
              :options="pcaTextArr"
              v-model="form.useraddress1"
              autocomplete="off">
          </el-cascader>
        </el-form-item>
        <el-form-item prop="useraddress2" label="详细地址">
          <el-input v-model="form.useraddress2" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item prop="phone" label="联系电话">
          <el-input v-model="form.phone" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="formVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>

import * as animationData from "@/assets/定位.json";
import LottieAnimation from "@/components/LottieAnimation";
import Lottie from "lottie-web";
import { pcaTextArr } from 'element-china-area-data'

export default {


  data() {
    return {
      pcaTextArr,
      selectedOptions: [],

      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
      addressData: [],
      pageNum: 1,   // 当前的页码
      pageSize: 10,  // 每页显示的个数
      total: 0,
      formVisible: false,
      form: {},
      rules: {
        username: [
          {required: true, message: '请输入收货人', trigger: 'blur'},
        ],
        useraddress1:[
          {required: true, message: '请选择地址', trigger: 'blur'},
        ],
        useraddress2: [
          {required: true, message: '请输入详细地址', trigger: 'blur'},
        ],
        phone: [
          {required: true, message: '请输入联系电话', trigger: 'blur'},
        ],
      },
      animationData,
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
  this.loadAddress(1)
},
  // methods：本页面所有的点击事件或者其他函数定义区
  methods: {
    addAddress() {
      this.form = {}
      this.formVisible = true
    },
    editAddress(row) {
      this.form = JSON.parse(JSON.stringify(row))
      this.formVisible = true
    },
    save() {
      this.form.useraddress = this.form.useraddress1 + this.form.useraddress2
      // 保存按钮触发的逻辑  它会触发新增或者更新
      this.$refs.formRef.validate((valid) => {
        if (valid) {
          this.form.userId = this.user.id
          this.$request({
            url: this.form.id ? '/address/update' : '/address/add',
            method: this.form.id ? 'PUT' : 'POST',
            data: this.form
          }).then(res => {
            if (res.code === '200') {  // 表示成功保存
              this.$message.success('保存成功')
              this.loadAddress(1)
              this.formVisible = false
            } else {
              this.$message.error(res.msg)  // 弹出错误的信息
            }
          })
        }
      })
    },
    loadAddress(pageNum) {
      if (pageNum) this.pageNum = pageNum
      this.$request.get('/address/selectPage', {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
        }
      }).then(res => {
        if (res.code === '200') {
          this.addressData = res.data?.list
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
      this.$request.delete('/address/delete/' + id).then(res => {
        if (res.code === '200') {
          this.$message.success('删除成功')
          this.loadAddress(1)
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    handleCurrentChange(pageNum) {
      this.loadAddress(pageNum)
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
