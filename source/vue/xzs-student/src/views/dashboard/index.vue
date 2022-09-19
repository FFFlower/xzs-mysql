<template>
  <div style="margin-top: 10px">
    <el-row>
      <el-carousel height="400px">
        <el-carousel-item>
          <img src="@/assets/carousel/1.jpg" class="carousel-img">
        </el-carousel-item>
        <el-carousel-item>
          <img src="@/assets/carousel/2.jpg" class="carousel-img">
        </el-carousel-item>
      </el-carousel>
    </el-row>
    <el-row class="app-item-contain app-card-contain">
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <h3 class="index-title-h3" style="border-left: 10px solid rgb(54, 81, 212);">练习中心</h3>
        </div>
        <div style="padding-left: 15px">
          <el-row>
            <el-col :span="4" style="padding: 0 20px;margin: 20px 0">
              <div class="col-icon-item" @click="practiceDialogVisible = true">
                <img src="@/assets/exam-paper/edit.png"/>
                <div class="col-icon-item--text">题库练习</div>
              </div>
            </el-col>
            <el-col :span="4" style="padding: 0 20px;margin-top: 20px">
              <div class="col-icon-item" @click="intelligenceTrain">
                <img src="@/assets/exam-paper/com.png"/>
                <div class="col-icon-item--text">智能训练</div>
              </div>
            </el-col>
          </el-row>
        </div>
      </el-card>
    </el-row>
    <el-row class="app-item-contain app-card-contain">
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <h3 class="index-title-h3">固定试卷</h3>
        </div>
        <div style="padding-left: 15px">
          <el-row>
            <el-col :span="6" v-for="(item, index) in fixedPaper" :key="index" style="padding: 0 20px;margin: 20px 0" >
              <el-card :body-style="{ padding: '10px' }" v-loading="loading" shadow="hover">
                <div style="padding: 14px;">
                  <div class="card-item-first">{{item.name}}</div>
                  <div class="card-item">作业类别：{{jobCategoryFormatter(item.gradeLevel)}}</div>
                  <div class="card-item">准操项目：{{subjectFormatter(item.subjectId)}}</div>
                  <div class="card-item">题目数：{{item.questionCount}}</div>
                  <div class="card-item">试卷总分：{{item.score}}</div>
                  <div class="card-item">考试时长：{{item.suggestTime}}分钟</div>
                  <div class="bottom clearfix">
                    <router-link target="_blank" :to="{path:'/do',query:{id:item.id}}">
                      <el-button type="primary" plain style="float: right">开始做题</el-button>
                    </router-link>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <el-empty v-if="fixedPaper.length === 0" description="暂无试卷"></el-empty>
        </div>
      </el-card>
    </el-row>
    <el-dialog
      title="提示"
      :visible.sync="practiceDialogVisible"
      width="30%">
      <el-form ref="form" :rules="rules" :inline="true" :model="practiceDialogForm">
        <el-form-item label="作业类别" prop="levelId">
          <el-select v-model="practiceDialogForm.levelId" placeholder="作业类别" @change="autoGenerateLevelChange">
            <el-option v-for="item in jobCategoryEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="准操项目" prop="subjectId">
          <el-select v-model="practiceDialogForm.subjectId" placeholder="准操项目">
            <el-option v-for="item in subjectFilter" :key="item.id" :value="item.id" :label="item.name"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
      <el-button @click="practiceDialogVisible = false">取 消</el-button>
      <el-button type="primary" @click="questionBankPractice">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapState, mapGetters, mapActions } from 'vuex'
import indexApi from '@/api/dashboard'
export default {
  data () {
    return {
      fixedPaper: [],
      timeLimitPaper: [],
      pushPaper: [],
      loading: false,
      taskLoading: false,
      taskList: [],
      practiceDialogVisible: false,
      intelligenceTrainDialogVisible: false,
      subjectFilter: [],
      practiceDialogForm: {
        levelId: '',
        subjectId: ''
      },
      rules: {
        levelId: [
          { required: true, message: '请选择作业类别' }
        ],
        subjectId: [
          { required: true, message: '请选择准操项目' }
        ]
      }
    }
  },
  created () {
    let _this = this
    this.loading = true
    indexApi.index().then(re => {
      _this.fixedPaper = re.response.fixedPaper
      _this.timeLimitPaper = re.response.timeLimitPaper
      _this.pushPaper = re.response.pushPaper
      _this.loading = false
    }).catch(e => {
      console.log(e)
    })
    this.initSubjectByUser()
    this.taskLoading = true
    indexApi.task().then(re => {
      _this.taskList = re.response
      _this.taskLoading = false
    }).catch(e => {
      console.log(e)
    })
  },
  methods: {
    statusTagFormatter (status) {
      return this.enumFormat(this.statusTag, status)
    },
    statusTextFormatter (status) {
      return this.enumFormat(this.statusEnum, status)
    },
    subjectFormatter (status) {
      return this.subjectEnumSimpleFormat(status)
    },
    jobCategoryFormatter (status) {
      return this.enumFormat(this.jobCategoryEnum, status)
    },
    autoGenerateLevelChange () {
      this.practiceDialogForm.subjectId = null
      this.subjectFilter = this.subjects.filter(data => data.level === this.practiceDialogForm.levelId)
    },
    questionBankPractice () {
      let _this = this
      this.$refs.form.validate((valid) => {
        if (valid) {
          _this.practiceDialogVisible = false
          _this.$router.push({ path: '/practice/bank/index', query: { levelId: _this.practiceDialogForm.levelId, subjectId: _this.practiceDialogForm.subjectId } })
        } else {
          return false
        }
      })
    },
    intelligenceTrain () {
      this.$router.push({ path: '/practice/index', query: { } })
    },
    ...mapActions('exam', { initSubjectByUser: 'initSubjectByUser' })
  },
  computed: {
    ...mapGetters('enumItem', [
      'enumFormat'
    ]),
    ...mapGetters('exam', ['subjectEnumSimpleFormat']),
    ...mapState('enumItem', {
      statusEnum: state => state.exam.examPaperAnswer.statusEnum,
      statusTag: state => state.exam.examPaperAnswer.statusTag,
      jobCategoryEnum: state => state.user.jobCategoryEnum
    }),
    ...mapState('exam', { subjects: state => state.subjects })
  }
}
</script>

<style lang="scss" scoped>
  .index-title-h3 {
    font-size: 22px;
    font-weight: 400;
    color: #1f2f3d;
    border-left: solid 10px #2ce8b4;
    padding-left: 10px;
    margin: 0 auto;
  }
  .card-item{
    color: #7d7d7f;
    font-size: 14px;
    margin: 20px 0;
  }
  .card-item-first{
    margin-bottom: 30px;
  }

  .el-carousel__item h3 {
    color: #475669;
    font-size: 18px;
    opacity: 0.75;
    line-height: 300px;
    margin: 0;
  }

  .el-carousel__item:nth-child(2n) {
    background-color: #99a9bf;
  }

  .el-carousel__item:nth-child(2n+1) {
    background-color: #d3dce6;
  }

  .bottom {
    margin-top: 13px;
    line-height: 12px;
  }

  .button {
    padding: 0;
    float: right;
  }

  .image {
    width: 50%;
    height: 80%;
    display: block;
    margin: 20px auto 10px auto;
  }
  .card-fixed-item{
    text-align: center;
    font-size: 20px;
    height: 100px;
    line-height: 100px;
  }
  .col-icon-item{
    text-align: center;
    cursor: pointer;
  }
  .col-icon-item .col-icon-item--text{
    margin-top: 10px;
  }
  .clearfix:before,
  .clearfix:after {
    display: table;
    content: "";
  }

  .clearfix:after {
    clear: both
  }
</style>
