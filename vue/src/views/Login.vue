<template>
  <div class="container">
    <!-- 左侧矢量图 -->
    <div id="lottie-background" class="lottie-background"></div>

    <!-- 右侧登录框 -->
    <div class="login-box">
      <div class="login-content">
        <div class="login-title">欢迎登录电子集市</div>
        <el-form :model="form" :rules="rules" ref="formRef">
          <el-form-item prop="username">
            <el-input prefix-icon="el-icon-user" placeholder="请输入账号" v-model="form.username"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input prefix-icon="el-icon-lock" placeholder="请输入密码" show-password v-model="form.password"></el-input>
          </el-form-item>
          <el-form-item>
            <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
              <el-option label="管理员" value="ADMIN"></el-option>
              <el-option label="商家" value="BUSINESS"></el-option>
              <el-option label="用户" value="USER"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button class="login-button" @click="login">登 录</el-button>
          </el-form-item>
          <div class="login-footer">
            还没有账号？请 <a href="/register">注册</a>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import lottie from "lottie-web";

export default {
  name: "Login",
  data() {
    return {
      form: {},
      rules: {
        username: [{ required: true, message: "请输入账号", trigger: "blur" }],
        password: [{ required: true, message: "请输入密码", trigger: "blur" }],
        role: [{ required: true, message: "请选择角色", trigger: "blur" }],
      },
    };
  },
  mounted() {
    // 初始化 Lottie 动画
    lottie.loadAnimation({
      container: document.getElementById("lottie-background"),
      renderer: "svg",
      loop: true,
      autoplay: true,
      path: "/animations/login.json", // 替换为你的 JSON 动画文件路径
    });
  },
  methods: {
    login() {
      this.$refs["formRef"].validate((valid) => {
        if (valid) {
          // 验证通过
          this.$request.post("/login", this.form).then((res) => {
            if (res.code === "200") {
              let user = res.data;
              localStorage.setItem("xm-user", JSON.stringify(user)); // 存储用户数据
              if (user.role === "USER") {
                location.href = "/front/home";
              } else if (user.role === "BUSINESS") {
                location.href = "/businessHome";
              } else {
                location.href = "/home";
              }
              this.$message.success("登录成功");
            } else {
              this.$message.error(res.msg);
            }
          });
        }
      });
    },
  },
};
</script>

<style scoped>
.container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.lottie-background {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
}

.login-box {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(to right, #ffffff, #f3f6fa);
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
}

.login-content {
  width: 80%;
  max-width: 400px;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0px 6px 15px rgba(0, 0, 0, 0.15);
}

.login-title {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
}

.login-button {
  width: 100%;
  background-color: #1386fb;
  border-color: #1386fb;
  color: white;
  font-size: 16px;
  border-radius: 8px;
}

.login-footer {
  text-align: right;
  margin-top: 10px;
  font-size: 14px;
  color: #555;
}

.login-footer a {
  color: #1386fb;
}

@media screen and (max-width: 768px) {
  .container {
    flex-direction: column;
  }

  .lottie-background,
  .login-box {
    flex: none;
    width: 100%;
    height: 50%;
  }

  .login-content {
    width: 90%;
  }
}
</style>