<template>
  <div class="app-container">
    <el-form :model="queryParam" ref="queryForm" :inline="true">
<!--      <el-form-item label="题目ID：">-->
<!--        <el-input v-model="queryParam.id" clearable></el-input>-->
<!--      </el-form-item>-->
      <el-form-item label="试卷名称：">
        <el-input v-model="queryParam.name" clearable></el-input>
      </el-form-item>
      <el-form-item label="作业类别：">
        <el-select v-model="queryParam.level" placeholder="作业类别" @change="levelChange" clearable>
          <el-option v-for="item in jobCategoryEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="准操项目：" >
        <el-select v-model="queryParam.subjectId"  clearable>
          <el-option v-for="item in subjectFilter" :key="item.id" :value="item.id" :label="item.name+' ( '+item.levelName+' )'"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">查询</el-button>
<!--        <router-link :to="{path:'/exam/paper/edit'}" class="link-left">-->
<!--          <el-button type="primary">添加</el-button>-->
<!--        </router-link>-->
        <el-popover placement="bottom" trigger="click">
          <el-button type="warning" size="mini" v-for="item in setMethodEnum" :key="item.key"
                     @click="selectSetMethod(item.key)">{{item.name}}
          </el-button>
          <el-button slot="reference" type="primary" class="link-left">添加</el-button>
        </el-popover>
      </el-form-item>
    </el-form>
    <el-table v-loading="listLoading" :data="tableData" border fit highlight-current-row style="width: 100%">
      <el-table-column type="index" width="50" label="序号" align="center"></el-table-column>
      <el-table-column prop="subjectId" label="作业类别" :formatter="subjectFormatter" width="240px" />
      <el-table-column prop="name" label="名称" width="160px"  />
      <el-table-column prop="students" label="答题学员" :formatter="studentFormatter" />
      <el-table-column prop="createTime" label="创建时间" width="160px"/>
      <el-table-column label="操作" align="center"  width="160px">
        <template slot-scope="{row}">
          <el-button size="mini" @click="$router.push({path:'/exam/paper/edit',query:{id:row.id}})" >编辑</el-button>
          <el-button size="mini" type="danger"  @click="deletePaper(row)" class="link-left">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="queryParam.pageIndex" :limit.sync="queryParam.pageSize"
                @pagination="search"/>
    <el-dialog :visible.sync="autoGeneratePaper.showDialog">
      <el-form :model="autoGeneratePaper.form" ref="form" :rules="autoGeneratePaper.rules" label-width="100px" v-loading="autoGeneratePaper.formLoading">
        <el-form-item label="作业类别：" prop="level">
          <el-select v-model="autoGeneratePaper.form.level" placeholder="作业类别" @change="autoGenerateLevelChange">
            <el-option v-for="item in jobCategoryEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="准操项目：" prop="subjectId">
          <el-select v-model="autoGeneratePaper.form.subjectId" placeholder="准操项目">
            <el-option v-for="item in subjectFilter" :key="item.id" :value="item.id"
                       :label="item.name+' ( '+item.levelName+' )'"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="试卷名称："  prop="name">
          <el-input v-model="autoGeneratePaper.form.name"/>
        </el-form-item>
        <el-form-item label="单选题数：">
          <div class="block">
            <el-slider
              v-model="autoGeneratePaper.form.singleChoiceNum"
              :step="10"
              show-input
              show-stops>
            </el-slider>
          </div>
        </el-form-item>
        <el-form-item label="判断题数：">
          <div class="block">
            <el-slider
              v-model="autoGeneratePaper.form.judgeNum"
              :step="10"
              show-input
              show-stops>
            </el-slider>
          </div>
        </el-form-item>
        <el-form-item label="答题学员：">
          <el-select v-model="autoGeneratePaper.form.studentIds" multiple filterable remote reserve-keyword class="max"
                     placeholder="请输入学员用户名或姓名添加"
                     :remote-method="getUserByUserName"
                     :loading="selectLoading">
            <el-option v-for="item in studentList" :key="item.id" :label="item.realName" :value="item.id"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button @click="autoGeneratePaper.showDialog = false">取 消</el-button>
          <el-button type="primary" @click="confirmAutoGenerate">确定</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters, mapState, mapActions } from 'vuex'
import Pagination from '@/components/Pagination'
import examPaperApi from '@/api/examPaper'
import userApi from '@/api/user'

export default {
  components: { Pagination },
  data () {
    return {
      queryParam: {
        id: null,
        name: null,
        level: null,
        subjectId: null,
        pageIndex: 1,
        pageSize: 10
      },
      subjectFilter: null,
      listLoading: true,
      tableData: [],
      total: 0,
      autoGeneratePaper: {
        form: {
          level: null,
          subjectId: null,
          setMethod: 1,
          questionNum: 100,
          singleChoiceNum: 80,
          judgeNum: 20,
          name: '',
          studentIds: []
        },
        formLoading: false,
        showDialog: false,
        rules: {
          level: [
            { required: true, message: '请选择作业类别', trigger: 'change' }
          ],
          subjectId: [
            { required: true, message: '请选择准操项目', trigger: 'change' }
          ],
          name: [
            { required: true, message: '请输入试卷名称', trigger: 'blur' }
          ]
        }
      },
      studentList: [],
      selectLoading: false
    }
  },
  created () {
    this.initSubject()
    this.search()
  },
  watch: {
    'autoGeneratePaper.form.singleChoiceNum' (newVal, oldVal) {
      this.autoGeneratePaper.form.judgeNum = this.autoGeneratePaper.form.questionNum - newVal
    },
    'autoGeneratePaper.form.judgeNum' (newVal, oldVal) {
      this.autoGeneratePaper.form.singleChoiceNum = this.autoGeneratePaper.form.questionNum - newVal
    }
  },
  methods: {
    submitForm () {
      this.queryParam.pageIndex = 1
      this.search()
    },
    getUserByUserName (query) {
      let _this = this
      if (query !== '') {
        if (!this.autoGeneratePaper.form.subjectId) {
          _this.$message.warning('尚未选择准操项目～')
          return
        }
        let queryParam = {
          keyWord: query,
          subjectId: this.autoGeneratePaper.form.subjectId
        }
        _this.selectLoading = true
        userApi.list(queryParam).then(re => {
          _this.selectLoading = false
          _this.studentList = re.response
        })
      } else {
        _this.studentList = []
      }
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
    selectSetMethod (setMethod) {
      this.autoGeneratePaper.form.setMethod = setMethod
      if (this.autoGeneratePaper.form.setMethod === 3) {
        this.$router.push({ path: '/exam/paper/edit' })
      } else {
        this.autoGeneratePaper.showDialog = true
      }
    },
    autoGenerateLevelChange () {
      this.autoGeneratePaper.form.subjectId = null
      this.subjectFilter = this.subjects.filter(data => data.level === this.autoGeneratePaper.form.level)
    },
    confirmAutoGenerate () {
      let _this = this
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.autoGeneratePaper.formLoading = true
          examPaperApi.autoGenerate(this.autoGeneratePaper.form).then(re => {
            if (re.code === 1) {
              _this.$message.success(re.message)
              _this.autoGeneratePaper.showDialog = false
              _this.submitForm()
            } else {
              _this.$message.error(re.message)
              _this.autoGeneratePaper.formLoading = false
            }
          }).catch(e => {
            this.autoGeneratePaper.formLoading = false
          })
        } else {
          return false
        }
      })
    },
    deletePaper (row) {
      let _this = this
      examPaperApi.deletePaper(row.id).then(re => {
        if (re.code === 1) {
          _this.search()
          _this.$message.success(re.message)
        } else {
          _this.$message.error(re.message)
        }
      })
    },
    levelChange () {
      this.queryParam.subjectId = null
      this.subjectFilter = this.subjects.filter(data => data.level === this.queryParam.level)
    },
    subjectFormatter  (row, column, cellValue, index) {
      return this.subjectEnumFormat(cellValue)
    },
    studentFormatter (row, column, cellValue, index) {
      return cellValue.map(x => x.realName).join(',')
    },
    ...mapActions('exam', { initSubject: 'initSubject' }),
    ...mapActions('tagsView', { delCurrentView: 'delCurrentView' })
  },
  computed: {
    ...mapGetters('enumItem', ['enumFormat']),
    ...mapState('enumItem', {
      setMethodEnum: state => state.exam.examPaper.setMethodEnum,
      jobCategoryEnum: state => state.user.jobCategoryEnum
    }),
    ...mapGetters('exam', ['subjectEnumFormat']),
    ...mapState('exam', { subjects: state => state.subjects })
  }
}
</script>
