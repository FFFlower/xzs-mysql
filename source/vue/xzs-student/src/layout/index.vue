<template>
  <el-container>
    <el-header height="61" class="student-header">
      <div class="home-logo">
        <a href="/student" style="text-decoration:none;" class="logo-a">
          <img src="@/assets/logo2.png"/>
        </a>
        <div></div>
      </div>
      <el-menu class="el-menu-title" mode="horizontal" :default-active="defaultUrl" :router="true">
        <el-menu-item index="/index">首页</el-menu-item>
        <el-menu-item index="/practice/index">练习中心</el-menu-item>
        <el-menu-item index="/paper/index">试卷中心</el-menu-item>
        <el-menu-item index="/record/index">考试记录</el-menu-item>
        <el-menu-item index="/question/index">错题本</el-menu-item>
      </el-menu>
      <div class="head-user">
        <el-dropdown trigger="click" placement="bottom">
          <el-badge :is-dot="messageCount!==0" >
            <el-avatar  class="el-dropdown-avatar" size="medium"  :src="userInfo.imagePath === null ? require('@/assets/avatar.png') : userInfo.imagePath"></el-avatar>
          </el-badge>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item @click.native="$router.push({path:'/user/index'})">个人中心</el-dropdown-item>
            <el-dropdown-item @click.native="$router.push({path:'/user/message'})">
              <el-badge :value="messageCount" v-if="messageCount!==0">
                <span>消息中心</span>
              </el-badge>
              <span  v-if="messageCount===0">消息中心</span>
            </el-dropdown-item>
            <el-dropdown-item @click.native="logout" divided>退出</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </el-header>
    <el-main class="student-main">
      <router-view/>
    </el-main>
<!--    <el-footer height="340" class="student-footer">-->
<!--      <div class="foot-container">-->
<!--        <div class="footer-main">-->
<!--          <h4>产品介绍</h4>-->
<!--          <a href="https://www.exampractice.top/index" target="_blank" class="footer-main-link">在线搜题系统</a>-->
<!--        </div>-->
<!--        <div class="footer-main">-->
<!--          <h4>微信小程序</h4>-->
<!--          <img src="@/assets/wechatqr.jpg" style="width: 100px;height: 100px;"/>-->
<!--        </div>-->
<!--      </div>-->
<!--    </el-footer>-->
    <div class="foot-copyright">
<!--      <span>Copyright © 2021 武汉思维跳跃科技有限公司 版权所有</span>-->
    </div>
  </el-container>
</template>

<script>
import { mapActions, mapMutations, mapState } from 'vuex'
import loginApi from '@/api/login'
import userApi from '@/api/user'
export default {
  name: 'Layout',
  data () {
    return {
      defaultUrl: '/index',
      userInfo: {
        imagePath: null
      }
    }
  },
  created () {
    let _this = this
    this.defaultUrl = this.routeSelect(this.$route.path)
    this.getUserMessageInfo()
    userApi.getCurrentUser().then(re => {
      _this.userInfo = re.response
    })
  },
  watch: {
    $route (to, from) {
      this.defaultUrl = this.routeSelect(to.path)
    }
  },
  methods: {
    routeSelect (path) {
      let topPath = ['/', '/index', '/paper/index', '/record/index', '/question/index']
      if (topPath.indexOf(path)) {
        return path
      }
      return null
    },
    logout () {
      let _this = this
      loginApi.logout().then(function (result) {
        if (result && result.code === 1) {
          _this.clearLogin()
          _this.$router.push({ path: '/login' })
        }
      })
    },
    ...mapActions('user', ['getUserMessageInfo']),
    ...mapMutations('user', ['clearLogin'])
  },
  computed: {
    ...mapState('user', {
      messageCount: state => state.messageCount
    })
  }
}
</script>

<style lang="scss" scoped>
.home-logo{
  display: flex;
  align-items: center;
}
.logo-a{
  color: #000000;
  height: 56px;
  display: flex;
  align-items: center;
  padding-left: 15px;
}
.logo-a img{
  width: 108px;
  height: 38px;
}
.logo-a:visited{
  text-decoration:none;
  color: #000000;
}
.logo-a h3 {
  display: inline-block;
}
.logo-a span{
  display: inline-block;
  font-size: 25px;
  margin-left: 10px;
}
.foot-copyright{
  margin-top: 30px;
}
</style>
