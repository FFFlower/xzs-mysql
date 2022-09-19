<template>
  <div v-cloak>
    <el-container  class="app-item-contain">
      <el-main>
        <el-row>
            <el-form label-position="right" label-width="80px">
              <el-form-item label="筛选：">
                <el-radio-group v-model="questionRange">
                  <el-radio-button label="all">全部</el-radio-button>
                  <el-radio-button v-bind:key="idx+Math.random()" v-for="(item, idx) in allRangeArr" :label="`all-${item}`">{{ item }}</el-radio-button>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="单选题：">
                <el-radio-group v-model="questionRange">
                  <el-radio-button label="1">全部</el-radio-button>
                  <el-radio-button v-bind:key="idx+Math.random()" v-for="(item, idx) in singleRangeArr" :label="`1-${item}`">{{ item }}</el-radio-button>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="判断题：">
                <el-radio-group v-model="questionRange">
                  <el-radio-button label="3">全部</el-radio-button>
                  <el-radio-button v-bind:key="idx+Math.random()" v-for="(item, idx) in judgeRangeArr" :label="`3-${item}`">{{ item }}</el-radio-button>
                </el-radio-group>
              </el-form-item>
            </el-form>
        </el-row>
        <el-row>
          <el-col :span="16">
            <el-card class="box-card" style="padding: 50px" v-loading="formLoading">
              <el-form :model="answerItem" ref="form" :rules="rules">
                  <QuestionEdit :qType="form.questionType" :question="form" :serial="form.sort"
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
                <el-progress type="circle" :percentage="answerProcess" :format="formatProcess">
                </el-progress>
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
              <el-row style="padding: 10px;"><el-button type="primary" round @click="showCardDialog = true">跳转</el-button></el-row>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
    <el-dialog
      title="提示"
      :visible.sync="showCardDialog"
      width="50%">
      <div v-if="rangeType == '1'">
        <el-divider content-position="center">单选题</el-divider>
        <el-row>
          <el-col>
            <span :key="item.itemOrder"  v-for="item in singleCount">
             <el-tag class="do-exam-title-tag" @click="goAnchor(item)">{{item}}</el-tag>
            </span>
          </el-col>
        </el-row>
      </div>
      <div v-else-if="rangeType == '3'">
        <el-divider content-position="center">判断题</el-divider>
        <el-row>
          <el-col>
            <span :key="item.itemOrder"  v-for="item in judgeCount">
             <el-tag class="do-exam-title-tag" @click="goAnchor(item)">{{item}}</el-tag>
            </span>
          </el-col>
        </el-row>
      </div>
      <div v-else>
        <el-divider content-position="center">全部题目</el-divider>
        <el-row>
          <el-col>
            <span :key="item.itemOrder"  v-for="item in total">
             <el-tag class="do-exam-title-tag" @click="goAnchor(item)">{{item}}</el-tag>
            </span>
          </el-col>
        </el-row>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showCardDialog = false">取 消</el-button>
<!--        <el-button type="primary" @click="showCardDialog = false">确 定</el-button>-->
      </span>
    </el-dialog>
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
        // currentQuestionNoStart: null,
        questionType: null,
        pageIndex: 1,
        pageSize: 100,
        orderBy: 'id',
        sort: 'asc'
      },
      rules: {
        content: [
          { required: true, message: '请选择一个选项', trigger: 'blur' }
        ]
      },
      total: 100,
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
      questionNumsScreenBase: 100,
      answerItem: {},
      judgeInt: 0,
      judgeRemainder: 0,
      singleInt: 0,
      singleRemainder: 0,
      allInt: 0,
      allRemainder: 0,
      questionRange: 'all',
      singleRangeArr: [],
      judgeRangeArr: [],
      allRangeArr: [],
      currentQuestionIdx: 0,
      showCardDialog: false
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
      // questionAnswerApi.viewRecord(this.recordForm).then((re) => {
      //   if (re.code === 1 && re.response) {
      //     this.$confirm('检测到保存的答题进度，是否继续上次答题？', '确认信息', {
      //       distinguishCancelAndClose: true,
      //       confirmButtonText: '继续答题',
      //       cancelButtonText: '重置' })
      //       .then(() => {
      //         this.questionForm.currentIdStart = re.response
      //         _this.nextPage()
      //       })
      //       .catch(action => {
      //         _this.nextPage()
      //       })
      //   } else {
      //     _this.nextPage()
      //   }
      // })
      _this.nextPage()
    },
    nextQuestion () {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.curQuestionIdx = this.curQuestionIdx + 1
          this.showAnswerFlag = false
          this.showResultFlag = false
          this.canNext = false
          console.log(this.curQuestionIdx)
          // 判断当前问题列表是否超出，超出查下一页
          if (this.curQuestionIdx >= this.questionList.length) {
            // this.nextPage()
            this.$alert('当前范围已经最后一题了', '提示', {
              confirmButtonText: '确定',
              callback: action => {}
            })
          } else {
            this.getQuestion()
          }
        }
      })
    },
    nextPage (index) {
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
        _this.currentQuestionIdx = 0
        _this.formLoading = false
        if (index) {
          for (let i = 0; i < _this.questionList.length; i++) {
            if (index === _this.questionList[i].sort) {
              _this.curQuestionIdx = i
            }
          }
        }
        _this.getQuestion()
      })
    },
    getQuestion () {
      this.form = this.questionList[this.curQuestionIdx]
      this.questionId = this.form.id
      // this.questionForm.currentIdStart = this.form.id
      this.currentQuestionIdx = this.currentQuestionIdx + 1
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
          // 初始化题数过滤栏
          _this.initNumsScreen()
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
    numsRange (total, remainder, idx) {
      if (total < idx) {
        return ((idx - 1) * this.questionNumsScreenBase + 1) + '-' + ((idx - 1) * this.questionNumsScreenBase + remainder)
      }
      return ((idx - 1) * this.questionNumsScreenBase + 1) + '-' + (idx * this.questionNumsScreenBase)
    },
    subjectFormatter () {
      return this.subjectEnumSimpleFormat(this.subjectId)
    },
    jobCategoryFormatter () {
      return this.enumFormat(this.jobCategoryEnum, this.levelId)
    },
    initNumsScreen () {
      this.judgeInt = Math.trunc(this.judgeCount / this.questionNumsScreenBase)
      this.judgeRemainder = this.judgeCount % this.questionNumsScreenBase
      this.singleInt = Math.trunc(this.singleCount / this.questionNumsScreenBase)
      this.singleRemainder = this.singleCount % this.questionNumsScreenBase
      this.allInt = Math.trunc(this.total / this.questionNumsScreenBase)
      this.allRemainder = this.total % this.questionNumsScreenBase
      for (let i = 0; i < (this.singleInt + Math.ceil(this.singleRemainder / this.questionNumsScreenBase)); i++) {
        this.singleRangeArr.push(this.numsRange(this.singleInt, this.singleRemainder, i + 1))
      }
      for (let i = 0; i < (this.judgeInt + Math.ceil(this.judgeRemainder / this.questionNumsScreenBase)); i++) {
        this.judgeRangeArr.push(this.numsRange(this.judgeInt, this.judgeRemainder, i + 1))
      }
      for (let i = 0; i < (this.allInt + Math.ceil(this.allRemainder / this.questionNumsScreenBase)); i++) {
        this.allRangeArr.push(this.numsRange(this.allInt, this.allRemainder, i + 1))
      }
    },
    showResult () {
      this.showResultFlag = true
      this.canNext = true
      this.showAnswer()
    },
    formatProcess (percentage) {
      let rangeParam = this.questionRange.split('-')
      if (rangeParam[0] === 'all' && rangeParam.length === 1) {
        return this.currentQuestionIdx + '/' + this.total
      }
      if (rangeParam[0] === '1' && rangeParam.length === 1) {
        return this.currentQuestionIdx + '/' + this.singleCount
      }
      if (rangeParam[0] === '3' && rangeParam.length === 1) {
        return this.currentQuestionIdx + '/' + this.judgeCount
      }
      return this.currentQuestionIdx + '/' + (parseInt(rangeParam[2]) - parseInt(rangeParam[1]) + 1)
    },
    showAnswer () {
      this.showAnswerFlag = true
    },
    questionCompleted (completed) {
      return this.enumFormat(this.doCompletedTag, completed)
    },
    goAnchor (index) {
      this.questionForm.pageIndex = Math.floor((index - 1) / this.questionForm.pageSize) + 1
      this.currentQuestionIdx = 1
      this.nextPage(index)
      this.showCardDialog = false
    },
    ...mapActions('exam', { initSubject: 'initSubject' })
  },
  watch: {
    'questionRange' (newVal, oldVal) {
      let rangeParam = newVal.split('-')
      if (rangeParam[0] === 'all') {
        this.questionForm.questionType = null
      } else {
        this.questionForm.questionType = parseInt(rangeParam[0])
      }
      this.questionForm.pageIndex = 1
      if (rangeParam.length > 1) {
        this.questionForm.pageIndex = Math.ceil(parseInt(rangeParam[1]) / this.questionForm.pageSize)
        this.currentQuestionIdx = parseInt(rangeParam[1])
      }
      this.currentQuestionIdx = 1
      this.curQuestionIdx = 0
      // this.questionForm.currentIdStart = 0
      this.nextPage()
    }
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
    rangeType () {
      let rangeParam = this.questionRange.split('-')
      return rangeParam[0]
    },
    answerProcess () {
      // if (this.total <= 0) {
      //   return 0
      // }
      // return parseInt((this.total - this.remainTotal + this.curQuestionIdx) * 100 / this.total)
      if (this.questionRange === null) {
        return 0
      }
      let rangeParam = this.questionRange.split('-')
      if (rangeParam[0] === 'all' && rangeParam.length === 1) {
        return this.currentQuestionIdx * 100 / this.total
      }
      if (rangeParam[0] === '1' && rangeParam.length === 1) {
        return this.currentQuestionIdx * 100 / this.singleCount
      }
      if (rangeParam[0] === '3' && rangeParam.length === 1) {
        return this.currentQuestionIdx * 100 / this.judgeCount
      }
      return this.currentQuestionIdx * 100 / (parseInt(rangeParam[2]) - parseInt(rangeParam[1]) + 1)
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

  .el-tag{
    width: 40px;
    margin: 2px;
    padding: 0px;
    text-align: center;
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
