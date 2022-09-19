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
    <el-footer height="340" class="student-footer">
      <div class="foot-container">
        <div class="footer-main">
          <h4>关于我们</h4>
          <p class="el-descriptions">&nbsp;&nbsp;&nbsp;&nbsp;安庆市宜科安全技术服务有限公司成立于2018年，注册资金600W元。是经安徽省应急管理厅和安庆市应急管理局验收批准的，特种作业（电工）理论及实操考点。
            公司地处安庆市宜秀区英德利产业园内，交通便利，环境优美。公司划分为理论和实操培训考试两个区域，占地1300余平方米。目前，公司专职工作人员6人，专职教师4人，兼职教师9人。其中，电专业副教授级4人、高讲1人、讲师1人、电气工程师6人。开设的专业有：低压电工作业、高压电工作业、电力电缆作业、电气试验作业、继电保护作业、焊接熔化与热切割、高处作业，考试合格后，由安庆市应急管理局统一发放相关操作许可证书。
            为了公司发展，为了保障我考点长足稳定服务于安全生产、服务于社会，欢迎相关有识之士加入。
          </p>
        </div>
        <div class="footer-main">
          <h4>联系我们</h4>
          <div class="contact">
            <div><i class="el-icon-phone"></i> 0556-5927666</div>
            <div style="margin-top: 10px"><i class="el-icon-phone"></i> 0556-5927669</div>
            <div style="margin-top: 10px"><i class="el-icon-message"></i> lsls@qq.com</div>
            <div style="margin-top: 10px"><i class="el-icon-map-location"></i> 安徽省安庆市宜秀区中山大道英德利产业园A区1栋5楼</div>
          </div>
        </div>
        <div class="footer-main">
          <h4>小程序</h4>
          <img src="@/assets/wechatqr.jpg" style="width: 100px;height: 100px;"/>
        </div>
      </div>
    </el-footer>
    <div class="foot-copyright">
      <a target="_blank" href="https://beian.miit.gov.cn">
        <img class="footerPubSecIcp_icon" src="//g-0.ss.faisys.com/image/footer/public_security_icon.png" width="20" height="20" alt="备案图标" style="width: 20px; height: 20px;vertical-align: middle;margin-bottom: 2px;">
        皖ICP备2020014763号
      </a>
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
    }).catch(e => {
      console.log(e)
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
      }).catch(e => {
        console.log(e)
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
.foot-copyright a{
  //margin-top: 30px;
  font-size: 14px;
  color: #606266;
}
.footer-main{
  font-size: 14px;
  color: #303133;
}
.footer-main h4{
  font-size: 18px;
}
.footer-main .el-descriptions{
  width: 400px;
  line-height: 25px;
  font-size: 14px;
  color: #606266;
}
.footer-main .contact{
  font-size: 14px;
  color: #606266;
  font-family: 'Rubik', sans-serif;
}
.el-submenu .el-submenu__title{
  font-size: 15px !important;
  line-height: 62px !important;
  color: #252b3a;
}
</style>
