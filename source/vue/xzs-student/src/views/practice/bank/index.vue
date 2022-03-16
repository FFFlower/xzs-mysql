<template>
  <div v-cloak>
    <el-container  class="app-item-contain">
      <el-main>
        <el-row>
          <el-col :span="16">
            <el-card class="box-card" style="padding: 50px" v-loading="formLoading">
              <el-form :model="answerItem" ref="form" :rules="rules">
                  <QuestionEdit :qType="form.questionType" :question="form"
                               :answer="answerItem" :showResult="showResultFlag" :doRight="doRight"/>
              </el-form>
              <el-row>
                <el-tag v-if="showAnswerFlag" type="success">正确答案：{{ form.correct }}</el-tag>
              </el-row>
              <el-row>
                <el-col :span="16" style="margin-top: 20px">
                  <el-button type="warning" @click="showAnswer">显示答案</el-button>
                  <el-button type="primary" @click="submitAnswer">提交答案</el-button>
                  <el-button type="primary" v-if="canNext" @click="nextQuestion">下一题</el-button>
                </el-col>
                <el-col :span="2" :offset="4" style="margin-top: 20px">
                  <el-button type="info" @click="exitPractice">退出练习</el-button>
                </el-col>
              </el-row>
            </el-card>
          </el-col>
          <el-col :span="7" :offset="1">
            <el-card class="box-card">
              <el-row style="padding: 10px;text-align: center">
                <el-progress type="circle" :percentage="answerProcess"></el-progress>
              </el-row>
              <el-row style="padding: 10px;">
                <el-col :span="8"><span>作业类别：</span></el-col>
                <el-col :span="12"><span>{{ jobCategoryFormatter() }}</span></el-col>
              </el-row>
              <el-row style="padding: 10px;">
                <el-col :span="8"><span>准操项目：</span></el-col>
                <el-col :span="12"><span>{{ subjectFormatter() }}</span></el-col>
              </el-row>
              <el-row style="padding: 10px;">
                <el-col :span="8"><span>题目总数：</span></el-col>
                <el-col :span="12"><span>{{ total }}</span></el-col>
              </el-row>
              <el-row style="padding: 10px;">
                <el-col :span="8"><span>单选题数：</span></el-col>
                <el-col :span="12"><span>{{ singleCount }}</span></el-col>
              </el-row>
              <el-row style="padding: 10px;">
                <el-col :span="8"><span>判断题数：</span></el-col>
                <el-col :span="12"><span>{{ judgeCount }}</span></el-col>
              </el-row>
              <el-row style="padding: 10px;">
                <el-col :span="8"><span>当前进度：</span></el-col>
                <el-col :span="12"><span>{{ currentQuestionNum }}</span></el-col>
              </el-row>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { mapState, mapGetters, mapActions } from 'vuex'
import questionApi from '@/api/question'
import questionAnswerApi from '@/api/questionAnswer'
import QuestionEdit from '../components/QuestionEdit'

export default {
  components: { QuestionEdit },
  data () {
    return {
      form: {},
      levelId: 0,
      subjectId: 0,
      questionId: 0,
      recordForm: {
        level: 0,
        subjectId: 0,
        questionId: 0,
        answer: ''
      },
      questionForm: {
        level: 0,
        subjectId: 0,
        currentIdStart: 0,
        pageIndex: 1,
        pageSize: 10,
        orderBy: 'id',
        sort: 'asc'
      },
      rules: {
        content: [
          { required: true, message: '请选择一个选项', trigger: 'blur' }
        ]
      },
      total: 0,
      singleCount: 0,
      judgeCount: 0,
      remainTotal: 0,
      curQuestionIdx: 0,
      questionList: [],
      showAnswerFlag: false,
      showResultFlag: false,
      canNext: false,
      formLoading: false,
      timer: null,
      remainTime: 0,
      answerItem: {}
    }
  },
  created () {
    this.levelId = parseInt(this.$route.query.levelId)
    this.subjectId = parseInt(this.$route.query.subjectId)
    this.questionForm.level = this.levelId
    this.questionForm.subjectId = this.subjectId
    this.recordForm.level = this.levelId
    this.recordForm.subjectId = this.subjectId
    // 初始化总题数
    this.initCount()
  },
  mounted () {

  },
  beforeDestroy () {
    window.clearInterval(this.timer)
  },
  methods: {
    initQuestion () {
      let _this = this
      questionAnswerApi.viewRecord(this.recordForm).then((re) => {
        if (re.code === 1 && re.response) {
          this.$confirm('检测到保存的答题进度，是否继续上次答题？', '确认信息', {
            distinguishCancelAndClose: true,
            confirmButtonText: '继续答题',
            cancelButtonText: '重置' })
            .then(() => {
              this.questionForm.currentIdStart = re.response
              _this.nextPage()
            })
            .catch(action => {
              _this.nextPage()
            })
        } else {
          _this.nextPage()
        }
      })
    },
    nextQuestion () {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.curQuestionIdx = this.curQuestionIdx + 1
          this.showAnswerFlag = false
          this.showResultFlag = false
          this.canNext = false
          // 判断当前问题列表是否超出，超出查下一页
          if (this.curQuestionIdx >= this.questionList.length) {
            this.nextPage()
          } else {
            this.getQuestion()
          }
        }
      })
    },
    nextPage () {
      let _this = this
      _this.formLoading = true
      questionApi.page(this.questionForm).then(re => {
        _this.questionList = re.response.list
        _this.remainTotal = re.response.total
        if (_this.remainTotal === 0) {
          _this.$alert('已经最后一题了', '提示', {
            confirmButtonText: '确定',
            callback: action => {
              _this.$router.push({ path: '/index' })
            }
          })
        }
        _this.curQuestionIdx = 0
        _this.formLoading = false
        _this.getQuestion()
      })
    },
    getQuestion () {
      this.form = this.questionList[this.curQuestionIdx]
      this.questionId = this.form.id
      this.questionForm.currentIdStart = this.form.id
      this.recordForm.questionId = this.form.id
      this.initAnswer()
    },
    initAnswer () {
      this.answerItem = { questionId: this.form.id, content: null, contentArray: [], completed: false, itemOrder: this.form.itemOrder }
    },
    initCount () {
      let _this = this
      questionApi.count(this.questionForm).then(re => {
        _this.total = re.response.count
        _this.singleCount = re.response.singleCount
        _this.judgeCount = re.response.judgeCount
        if (_this.total === 0) {
          this.$alert('暂时无可练习试题，请联系管理员添加', '提示', {
            confirmButtonText: '确定',
            callback: action => {
              _this.$router.push({ path: '/index' })
            }
          })
        } else {
          // 初始化问题
          _this.initQuestion()
          // 初始化准操项目
          _this.initSubject()
        }
      })
    },
    exitPractice () {
      this.$router.push({ path: '/index' })
    },
    submitAnswer () {
      let _this = this
      this.$refs.form.validate((valid) => {
        if (valid) {
          _this.recordForm.answer = _this.answerItem.content
          questionAnswerApi.record(this.recordForm)
          _this.showResult()
          // _this.nextQuestion()
        }
      })
    },
    subjectFormatter () {
      return this.subjectEnumSimpleFormat(this.subjectId)
    },
    jobCategoryFormatter () {
      return this.enumFormat(this.jobCategoryEnum, this.levelId)
    },
    showResult () {
      this.showResultFlag = true
      this.canNext = true
      this.showAnswer()
    },
    showAnswer () {
      this.showAnswerFlag = true
    },
    ...mapActions('exam', { initSubject: 'initSubject' })
  },
  computed: {
    ...mapGetters('enumItem', ['enumFormat']),
    ...mapGetters('exam', ['subjectEnumSimpleFormat']),
    ...mapState('enumItem', {
      doCompletedTag: state => state.exam.question.answer.doCompletedTag,
      jobCategoryEnum: state => state.user.jobCategoryEnum
    }),
    currentQuestionNum () {
      return this.total - this.remainTotal + this.curQuestionIdx
    },
    doRight () {
      return this.answerItem.content === this.form.correct
    },
    answerProcess () {
      if (this.total <= 0) {
        return 0
      }
      return parseInt((this.total - this.remainTotal + this.curQuestionIdx) * 100 / this.total)
    }
  }
}
</script>

<style lang="scss" scoped>
  .align-center {
    text-align: center
  }

  .exam-question-item {
    padding: 10px;

    .el-form-item__label {
      font-size: 15px !important;
    }
  }

  .question-title-padding {
    padding-left: 25px;
    padding-right: 25px;
  }
  .box-card{
    font-size: 18px;
  }
  [v-cloak] {
    display: none;
  }
</style>
