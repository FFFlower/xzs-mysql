<template>
  <div style="margin-top: 10px" class="app-contain">
    <el-tabs tab-position="left"  v-model="tabId"  @tab-click="subjectChange" >
      <el-tab-pane :label="item.name"  :key="item.id" :name="item.id" v-for="item in subjectList" style="margin-left: 20px;" >
        <el-row>
          <el-col :span="16">
            <el-table v-loading="listLoading" :data="tableData" fit highlight-current-row style="width: 100%">
              <el-table-column prop="id" label="序号" width="90px"/>
              <el-table-column prop="name" label="名称"  />
              <el-table-column prop="createTime" label="创建时间"  />
              <el-table-column align="right">
                <template slot-scope="{row}">
                  <router-link target="_blank" :to="{path:'/do',query:{id:row.id}}">
                    <el-button  type="text" size="small">开始答题</el-button>
                  </router-link>
                </template>
              </el-table-column>
            </el-table>
            <pagination v-show="total>0" :total="total" :background="false" :page.sync="queryParam.pageIndex" :limit.sync="queryParam.pageSize"
                        @pagination="search" style="margin-top: 20px"/>
          </el-col>
          <el-col :span="7" :offset="1">
            <el-card class="box-card" v-if="queryParam.paperType === 8" :v-loading="formLoading">
              <el-form ref="form" :rules="rules" :model="form" label-width="80px" style="margin-top: 20px;">
                <el-form-item label="作业类别" prop="level">
                  <el-select v-model="form.level" placeholder="作业类别" @change="autoGenerateLevelChange">
                    <el-option v-for="item in jobCategoryEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="准操项目" prop="subjectId">
                  <el-select v-model="form.subjectId" placeholder="准操项目">
                    <el-option v-for="item in subjectFilter" :key="item.id" :value="item.id" :label="item.name"></el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="单选题数">
                  <div class="block">
                    <el-slider
                      v-model="form.singleChoiceNum"
                      :step="10">
                    </el-slider>
                  </div>
                </el-form-item>
                <el-form-item label="判断题数">
                  <div class="block">
                    <el-slider
                      v-model="form.judgeNum"
                      :step="10">
                    </el-slider>
                  </div>
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="generateIntelligenceTrain">生成试卷</el-button>
                  <el-button type="primary" @click="questionBankPractice">题库练习</el-button>
                </el-form-item>
              </el-form>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>

  </div>
</template>

<script>
import { mapActions, mapGetters, mapState } from 'vuex'
import Pagination from '@/components/Pagination'
import examPaperApi from '@/api/examPaper'
import subjectApi from '@/api/subject'

export default {
  components: { Pagination },
  data () {
    return {
      queryParam: {
        paperType: 8,
        subjectId: 0,
        pageIndex: 1,
        pageSize: 10
      },
      form: {
        level: null,
        subjectId: null,
        setMethod: 2,
        questionNum: 100,
        singleChoiceNum: 30,
        judgeNum: 70
      },
      rules: {
        level: [
          { required: true, message: '请选择作业类别' }
        ],
        subjectId: [
          { required: true, message: '请选择准操项目' }
        ]
      },
      levelId: 0,
      subjectId: 0,
      tabId: '',
      listLoading: true,
      subjectList: [],
      subjects: [],
      formLoading: true,
      subjectFilter: [],
      tableData: [],
      total: 0
    }
  },
  created () {
    this.initSubject()
  },
  methods: {
    initSubject () {
      let _this = this
      subjectApi.listByUser().then(re => {
        _this.subjects = re.response
        _this.subjectList = re.response.map(i => { return { id: i.id.toString(), name: i.name } })
        let subjectId = _this.subjectList[0].id
        _this.queryParam.subjectId = subjectId
        _this.tabId = subjectId.toString()
        _this.search()
      })
    },
    search () {
      this.listLoading = true
      examPaperApi.pageList(this.queryParam).then(data => {
        const re = data.response
        this.tableData = re.list
        this.total = re.total
        this.queryParam.pageIndex = re.pageNum
        this.listLoading = false
      })
    },
    generateIntelligenceTrain () {
      let _this = this
      this.$refs.form[0].validate((valid) => {
        if (valid) {
          this.formLoading = true
          examPaperApi.autoGenerate(this.form).then(re => {
            this.formLoading = false
            if (re.code === 1) {
              _this.$message.success(re.message)
              _this.search()
            } else {
              _this.$message.error(re.message)
            }
          }).catch(e => {
            this.formLoading = false
          })
        } else {
          return false
        }
      })
    },
    questionBankPractice () {
      let _this = this
      this.$refs.form[0].validate((valid) => {
        if (valid) {
          _this.practiceDialogVisible = false
          _this.$router.push({ path: '/practice/bank/index', query: { levelId: _this.form.level, subjectId: _this.form.subjectId } })
        } else {
          return false
        }
      })
    },
    paperTypeChange (val) {
      this.search()
    },
    subjectFormatter () {
      return this.subjectEnumSimpleFormat(this.subjectId)
    },
    jobCategoryFormatter () {
      return this.enumFormat(this.jobCategoryEnum, this.levelId)
    },
    subjectChange (tab, event) {
      this.queryParam.subjectId = Number(this.tabId)
      this.search()
    },
    autoGenerateLevelChange () {
      this.form.subjectId = null
      this.subjectFilter = this.subjects.filter(data => data.level === this.form.level)
    }
  },
  computed: {
    ...mapGetters('enumItem', ['enumFormat']),
    ...mapGetters('exam', ['subjectEnumSimpleFormat']),
    ...mapState('enumItem', {
      doCompletedTag: state => state.exam.question.answer.doCompletedTag,
      jobCategoryEnum: state => state.user.jobCategoryEnum,
      paperTypeEnum: state => state.exam.examPaper.paperTypeEnum
    })
  },
  watch: {
    'form.singleChoiceNum' (newVal, oldVal) {
      this.form.judgeNum = this.form.questionNum - newVal
    },
    'form.judgeNum' (newVal, oldVal) {
      this.form.singleChoiceNum = this.form.questionNum - newVal
    }
  }
}
</script>
