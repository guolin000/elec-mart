<template>
  <el-container>
    <!-- 头部固定 -->
    <el-header class="manager-header" height="70px">
      <div class="manager-header-left">
        <img src="@/assets/imgs/logo.png" style="border-radius: 10px" />
        <div class="title">后台管理系统</div>
      </div>

      <div class="manager-header-center">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: $route.path }">{{ $route.meta.name }}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <div class="manager-header-right">
        <el-dropdown placement="bottom">
          <div class="avatar">
            <img :src="user.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
            <div>{{ user.name || '管理员' }}</div>
          </div>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item @click.native="goToPerson">个人信息</el-dropdown-item>
            <el-dropdown-item @click.native="$router.push('/password')">修改密码</el-dropdown-item>
            <el-dropdown-item @click.native="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </el-header>

    <!-- 主体区域 -->
    <el-container>
      <!-- 侧边栏固定 -->
      <el-aside width="250px" class="manager-main-left">
        <el-menu :default-openeds="['info', 'user']" router style="border: none" :default-active="$route.path">
          <el-menu-item v-if="user.role === 'ADMIN'" index="/home">
            <i class="el-icon-s-home"></i>
            <span>系统首页</span>
          </el-menu-item>
          <el-menu-item v-if="user.role === 'BUSINESS'" index="/businessHome">
            <i class="el-icon-s-home"></i>
            <span>店铺信息</span>
          </el-menu-item>
          <el-submenu index="info">
            <template slot="title">
              <i class="el-icon-menu"></i><span>信息管理</span>
            </template>
            <el-menu-item v-if="user.role === 'ADMIN'" index="/notice">公告信息</el-menu-item>
            <el-menu-item v-if="user.role === 'ADMIN'" index="/type">一级分类</el-menu-item>
            <el-menu-item v-if="user.role === 'ADMIN'" index="/secondType">二级分类</el-menu-item>
            <el-menu-item v-if="user.role === 'ADMIN'" index="/goods">商品信息</el-menu-item>
            <el-menu-item v-if="user.role === 'BUSINESS'" index="/businessGoods">商品信息</el-menu-item>
            <el-menu-item v-if="user.role === 'BUSINESS'" index="/businessOrders">订单管理</el-menu-item>
            <el-menu-item v-else index="/orders">订单管理</el-menu-item>
            <el-menu-item index="/comment">评论管理</el-menu-item>
          </el-submenu>

          <el-submenu index="user" v-if="user.role === 'ADMIN'">
            <template slot="title">
              <i class="el-icon-menu"></i><span>用户管理</span>
            </template>
            <el-menu-item index="/admin">管理员信息</el-menu-item>
            <el-menu-item index="/business">商家信息</el-menu-item>
            <el-menu-item index="/user">用户信息</el-menu-item>
          </el-submenu>
        </el-menu>
      </el-aside>

      <!-- 右侧内容可滚动 -->
      <el-main class="manager-main-right">
        <router-view @update:user="updateUser" />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: "Manager",
  data() {
    return {
      user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
    }
  },
  created() {
    if (!this.user.id) {
      this.$router.push('/front/home')
    }
  },
  methods: {
    updateUser() {
      this.user = JSON.parse(localStorage.getItem('xm-user') || '{}') // 重新获取用户最新信息
    },
    goToPerson() {
      if (this.user.role === 'ADMIN') {
        this.$router.push('/adminPerson')
      } else if (this.user.role === 'BUSINESS') {
        this.$router.push('/businessPerson')
      }
    },
    logout() {
      localStorage.removeItem('xm-user')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
@import "@/assets/css/manager.css";
</style>
