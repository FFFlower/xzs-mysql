<template>
  <div>
    <el-row style="margin-top: 40px">
      <el-col :span="14" :offset="1">
        <el-form :model="form" ref="form" v-loading="formLoading" label-width="100px">
          <el-row :key="index"  v-for="(titleItem,index) in form.titleItems">
<!--            <h3>{{titleItem.name}}</h3>-->
            <el-card class="exampaper-item-box" v-if="titleItem.questionItems.length!==0">
              <div slot="header">
                <span>{{titleItem.name}}</span>
              </div>
              <el-form-item :key="questionItem.itemOrder"
                            v-for="questionItem in titleItem.questionItems"
                            class="exam-question-item" label-width="50px" :id="'question-'+ questionItem.itemOrder">
                <span slot="label" style="width: 50px;line-height: 1.8;font-size: 20px !important;margin-top: 4px">
                  {{questionItem.itemOrder+'.'}}
                </span>
                <QuestionEdit :qType="questionItem.questionType" :question="questionItem"
                              :answer="answer.answerItems[questionItem.itemOrder-1]"/>
              </el-form-item>
            </el-card>
          </el-row>
<!--          <el-row class="do-align-center">-->
<!--            <el-button type="primary" @click="submitForm">提交</el-button>-->
<!--            <el-button>取消</el-button>-->
<!--          </el-row>-->
        </el-form>
      </el-col>
      <el-col :span="7" :offset="1">
        <el-card class="question-card">
          <el-row>
            <div style="text-align: center;font-size: 16px;font-weight: bold">{{form.name}}</div>
          </el-row>
          <el-divider></el-divider>
          <el-row>
            <span class="question-title-padding">总分：{{form.score}}</span>
            <span class="question-title-padding">时间：{{form.suggestTime}}分钟</span>
          </el-row>
          <el-divider content-position="center">答题卡</el-divider>
          <el-row>
            <el-col>
            <span :key="item.itemOrder"  v-for="item in answer.answerItems">
             <el-tag :type="questionCompleted(item.completed)" class="do-exam-title-tag" @click="goAnchor('#question-'+item.itemOrder)">{{item.itemOrder}}</el-tag>
            </span>
            </el-col>
          </el-row>
          <el-divider content-position="center">剩余时间</el-divider>
          <el-row>
            <div class="remain-time">
              {{formatSeconds(remainTime)}}
            </div>
          </el-row>
          <el-row class="do-align-center">
            <el-button type="primary" @click="submitForm">提交试卷</el-button>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapState, mapGetters } from 'vuex'
import { formatSeconds } from '@/utils'
import QuestionEdit from '../components/QuestionEdit'
import examPaperApi from '@/api/examPaper'
import examPaperAnswerApi from '@/api/examPaperAnswer'

export default {
  components: { QuestionEdit },
  data () {
    return {
      form: {},
      formLoading: false,
      answer: {
        questionId: null,
        doTime: 0,
        answerItems: []
      },
      timer: null,
      remainTime: 0
    }
  },
  created () {
    let id = this.$route.query.id
    let _this = this
    if (id && parseInt(id) !== 0) {
      _this.formLoading = true
      examPaperApi.select(id).then(re => {
        _this.form = re.response
        _this.remainTime = re.response.suggestTime * 60
        _this.initAnswer()
        _this.timeReduce()
        _this.formLoading = false
      })
    }
  },
  mounted () {

  },
  beforeDestroy () {
    window.clearInterval(this.timer)
  },
  methods: {
    formatSeconds (theTime) {
      return formatSeconds(theTime)
    },
    timeReduce () {
      let _this = this
      this.timer = setInterval(function () {
        if (_this.remainTime <= 0) {
          _this.submitForm()
        } else {
          ++_this.answer.doTime
          --_this.remainTime
        }
      }, 1000)
    },
    questionCompleted (completed) {
      return this.enumFormat(this.doCompletedTag, completed)
    },
    goAnchor (selector) {
      this.$el.querySelector(selector).scrollIntoView({ behavior: 'instant', block: 'center', inline: 'nearest' })
    },
    initAnswer () {
      this.answer.id = this.form.id
      let titleItemArray = this.form.titleItems
      for (let tIndex in titleItemArray) {
        let questionArray = titleItemArray[tIndex].questionItems
        for (let qIndex in questionArray) {
          let question = questionArray[qIndex]
          this.answer.answerItems.push({ questionId: question.id, content: null, contentArray: [], completed: false, itemOrder: question.itemOrder })
        }
      }
    },
    submitForm () {
      let _this = this
      window.clearInterval(_this.timer)
      _this.formLoading = true
      examPaperAnswerApi.answerSubmit(this.answer).then(re => {
        if (re.code === 1) {
          _this.$alert('试卷得分：' + re.response + '分', '考试结果', {
            confirmButtonText: '返回考试记录',
            callback: action => {
              _this.$router.push('/record/index')
            }
          })
        } else {
          _this.$message.error(re.message)
        }
        _this.formLoading = false
      }).catch(e => {
        _this.formLoading = false
      })
    }
  },
  computed: {
    ...mapGetters('enumItem', ['enumFormat']),
    ...mapState('enumItem', {
      doCompletedTag: state => state.exam.question.answer.doCompletedTag
    })
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
      line-height: 1.8;
      font-size: 20px !important;
    }
  }

  .question-title-padding {
    padding-left: 25px;
    padding-right: 25px;
  }
  .el-tag{
    width: 30px;
    margin: 2px;
    padding: 0px;
    text-align: center;
  }
  .remain-time{
    text-align: center;
    color: #F56C6C;
  }
  .question-card{
    position: fixed;
    width: 450px;
  }
  .el-card__header{
    background-color: #EBEEF5;font-size: 20px;font-weight: bold;
  }
</style>
