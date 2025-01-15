<template>
  <div class="container">
    <!-- 左侧动态矢量图 -->
    <div id="lottie-background" class="lottie-background"></div>

    <!-- 右侧注册框 -->
    <div class="register-box">
      <div class="register-content">
        <div class="register-title">欢迎注册</div>
        <el-form :model="form" :rules="rules" ref="formRef">
          <el-form-item prop="username">
            <el-input
                prefix-icon="el-icon-user"
                placeholder="请输入账号"
                v-model="form.username"
            ></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
                prefix-icon="el-icon-lock"
                placeholder="请输入密码"
                show-password
                v-model="form.password"
            ></el-input>
          </el-form-item>
          <el-form-item prop="confirmPass">
            <el-input
                prefix-icon="el-icon-lock"
                placeholder="请确认密码"
                show-password
                v-model="form.confirmPass"
            ></el-input>
          </el-form-item>
          <el-form-item prop="email">
            <el-input
                prefix-icon="el-icon-message"
                placeholder="请输入邮箱"
                v-model="form.email"
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-input
                prefix-icon="el-icon-message"
                placeholder="请输入邮箱验证码"
                v-model="form.verificationCode"
            ></el-input>
            <el-button
                @click="sendVerificationCode"
                type="primary"
                :disabled="isCodeSent"
            >
              {{ isCodeSent ? `${countdown}s 后重新发送` : "发送验证码" }}
            </el-button>
          </el-form-item>
          <el-form-item>
            <el-select
                v-model="form.role"
                placeholder="请选择角色"
                style="width: 100%"
            >
              <el-option label="商家" value="BUSINESS"></el-option>
              <el-option label="用户" value="USER"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button class="register-button" @click="register">注 册</el-button>
          </el-form-item>
          <div class="register-footer">
            已有账号？请 <a href="/login">登录</a>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import lottie from "lottie-web";

export default {
  name: "Register",
  data() {
    const validateEmail = (rule, value, callback) => {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!value) {
        callback(new Error("请输入邮箱"));
      } else if (!emailRegex.test(value)) {
        callback(new Error("请输入正确的邮箱格式"));
      } else {
        callback();
      }
    };

    const validatePassword = (rule, confirmPass, callback) => {
      if (confirmPass === "") {
        callback(new Error("请确认密码"));
      } else if (confirmPass !== this.form.password) {
        callback(new Error("两次输入的密码不一致"));
      } else {
        callback();
      }
    };

    return {
      form: {
        username: "",
        password: "",
        confirmPass: "",
        email: "",
        role: "",
        verificationCode: "",
      },
      rules: {
        username: [{ required: true, message: "请输入账号", trigger: "blur" }],
        password: [{ required: true, message: "请输入密码", trigger: "blur" }],
        confirmPass: [{ validator: validatePassword, trigger: "blur" }],
        email: [{ validator: validateEmail, trigger: "blur" }],
        verificationCode: [
          { required: true, message: "请输入验证码", trigger: "blur" },
        ],
      },
      isCodeSent: false,
      countdown: 60,
    };
  },
  mounted() {
    lottie.loadAnimation({
      container: document.getElementById("lottie-background"),
      renderer: "svg",
      loop: true,
      autoplay: true,
      path: "/animations/register.json", // 替换为你的 JSON 动画文件路径
    });
  },
  methods: {
    // 发送验证码请求
    sendVerificationCode() {
      if (!this.form.email) {
        this.$message.error("请先输入邮箱");
        return;
      }
      this.$request
          .post("/api/send-code", { email: this.form.email })
          .then((res) => {
            if (res.code === "200") {
              this.$message.success("验证码已发送，请注意查收");
              this.startCountdown();
            } else {
              this.$message.error(res.msg);
            }
          })
          .catch((error) => {
            console.error("发送验证码失败:", error);
            this.$message.error("发送验证码失败，请稍后重试");
          });
    },
    // 启动倒计时
    startCountdown() {
      this.isCodeSent = true;
      this.countdown = 60;
      const timer = setInterval(() => {
        this.countdown--;
        if (this.countdown <= 0) {
          clearInterval(timer);
          this.isCodeSent = false;
        }
      }, 1000);
    },
    // 注册请求
    register() {
      this.$refs["formRef"].validate((valid) => {
        if (valid) {
          this.$request
              .post("/api/register", this.form)
              .then((res) => {
                if (res.code === "200") {
                  this.$router.push("/login"); // 跳转登录页面
                  this.$message.success("注册成功");
                } else {
                  this.$message.error(res.msg);
                }
              })
              .catch((error) => {
                console.error("注册失败:", error);
                this.$message.error("注册失败，请稍后重试");
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

.register-box {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(to right, #ffffff, #f3f6fa);
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
}

.register-content {
  width: 80%;
  max-width: 400px;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0px 6px 15px rgba(0, 0, 0, 0.15);
}

.register-title {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
}

.register-button {
  width: 100%;
  background-color: #0968fc;
  border-color: #0968fc;
  color: white;
  font-size: 16px;
  border-radius: 8px;
}

.register-footer {
  text-align: right;
  margin-top: 10px;
  font-size: 14px;
  color: #555;
}

.register-footer a {
  color: #0968fc;
}

@media screen and (max-width: 768px) {
  .container {
    flex-direction: column;
  }

  .lottie-background,
  .register-box {
    flex: none;
    width: 100%;
    height: 50%;
  }

  .register-content {
    width: 90%;
  }
}
</style>