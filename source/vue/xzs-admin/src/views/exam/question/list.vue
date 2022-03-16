<template>
  <div class="app-container">
    <el-form :model="queryParam" ref="queryForm" :inline="true">
<!--      <el-form-item label="题目ID：">-->
<!--        <el-input v-model="queryParam.id" clearable></el-input>-->
<!--      </el-form-item>-->
      <el-form-item label="题干：">
        <el-input v-model="queryParam.content" clearable></el-input>
      </el-form-item>
      <el-form-item label="作业类别：">
        <el-select v-model="queryParam.level" placeholder="作业类别"  @change="levelChange" clearable>
          <el-option v-for="item in jobCategoryEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="准操项目：">
        <el-select v-model="queryParam.subjectId" clearable>
          <el-option v-for="item in subjectFilter" :key="item.id" :value="item.id"
                     :label="item.name+' ( '+item.levelName+' )'"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="题型：">
        <el-select v-model="queryParam.questionType" clearable>
          <el-option v-for="item in questionType" :key="item.key" :value="item.key" :label="item.value"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">查询</el-button>
        <el-popover placement="bottom" trigger="click">
          <el-button type="warning" size="mini" v-for="item in editUrlEnum" :key="item.key"
                     @click="$router.push({path:item.value})">{{item.name}}
          </el-button>
          <el-button slot="reference" type="primary" class="link-left">添加</el-button>
        </el-popover>
        <el-button type="danger" @click="batchDelete" class="link-left">批量删除</el-button>
        <el-button type="danger" @click="questionDelete.dialog = true" class="link-left">条件删除</el-button>
        <el-button type="primary" @click="questionExcelImport.dialog = true">Excel导入</el-button>
      </el-form-item>
    </el-form>
    <el-table v-loading="listLoading" :data="tableData" border fit highlight-current-row style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="40"></el-table-column>
      <el-table-column prop="id" label="Id" width="90px"/>
      <el-table-column prop="subjectId" label="作业类别" :formatter="subjectFormatter" width="120px"/>
      <el-table-column prop="questionType" label="题型" :formatter="questionTypeFormatter" width="70px"/>
      <el-table-column prop="shortTitle" label="题干" show-overflow-tooltip/>
      <el-table-column prop="score" label="分数" width="60px"/>
      <el-table-column prop="difficult" label="难度" width="60px"/>
      <el-table-column prop="createTime" label="创建时间" width="160px"/>
      <el-table-column label="操作" align="center" width="220px">
        <template slot-scope="{row}">
          <el-button size="mini"   @click="showQuestion(row)">预览</el-button>
          <el-button size="mini"  @click="editQuestion(row)">编辑</el-button>
          <el-button size="mini"  type="danger" @click="deleteQuestion(row)" class="link-left">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="queryParam.pageIndex" :limit.sync="queryParam.pageSize"
                @pagination="search"/>
    <el-dialog :visible.sync="questionShow.dialog" style="width: 100%;height: 100%">
      <QuestionShow :qType="questionShow.qType" :question="questionShow.question" :qLoading="questionShow.loading"/>
    </el-dialog>
    <el-dialog :visible.sync="questionExcelImport.dialog" @closed="importDialogClosed" style="width: 100%;height: 100%">
      <el-form :model="questionExcelImport.form" ref="form" :rules="questionExcelImport.rules" label-width="100px" v-loading="questionExcelImport.formLoading">
        <el-form-item label="作业类别：" prop="level">
          <el-select v-model="questionExcelImport.form.level" placeholder="作业类别" @change="excelImportLevelChange">
            <el-option v-for="item in jobCategoryEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="准操项目：" prop="subjectId">
          <el-select v-model="questionExcelImport.form.subjectId" placeholder="准操项目">
            <el-option v-for="item in subjectFilter" :key="item.id" :value="item.id"
                       :label="item.name+' ( '+item.levelName+' )'"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="附件：" prop="file">
          <el-upload
            class="upload-demo"
            ref="upload"
            :data="questionExcelImport.form"
            action="/api/admin/question/importExcel"
            :file-list="questionExcelImport.fileList"
            :auto-upload="false"
            accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :limit="1">
            <el-button slot="trigger" size="small" type="primary">选取Excel文件</el-button>
          </el-upload>
          <el-link href="/file/question_model.xlsx" target="_blank" type="primary">点击下载Excel导入模版</el-link>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="questionExcelImport.dialog = false">取 消</el-button>
        <el-button type="primary" :loading="questionExcelImport.formLoading" @click="confirmImportExcel">确定</el-button>
      </span>
    </el-dialog>
    <el-dialog :visible.sync="questionDelete.dialog" title="条件删除" @closed="deleteDialogClosed" width="30%" style="width: 100%;height: 100%">
      <el-form :model="questionDelete.form" ref="delForm" :rules="questionDelete.rules" label-width="100px" v-loading="questionDelete.formLoading">
        <el-form-item label="作业类别：" prop="level">
          <el-select v-model="questionDelete.form.level" placeholder="作业类别" @change="conditionDelLevelChange">
            <el-option v-for="item in jobCategoryEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="准操项目：" prop="subjectId">
          <el-select v-model="questionDelete.form.subjectId" placeholder="准操项目">
            <el-option v-for="item in subjectFilter" :key="item.id" :value="item.id"
                       :label="item.name+' ( '+item.levelName+' )'"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="questionDelete.dialog = false">取 消</el-button>
        <el-button type="primary" :loading="questionDelete.formLoading" @click="confirmConditionDelete">删除</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters, mapState, mapActions } from 'vuex'
import Pagination from '@/components/Pagination'
import QuestionShow from './components/Show'
import questionApi from '@/api/question'

export default {
  components: { Pagination, QuestionShow },
  data () {
    return {
      queryParam: {
        id: null,
        questionType: null,
        content: null,
        level: null,
        subjectId: null,
        pageIndex: 1,
        pageSize: 10
      },
      subjectFilter: null,
      listLoading: true,
      tableData: [],
      tableSelectData: [],
      total: 0,
      questionShow: {
        qType: 0,
        dialog: false,
        question: null,
        loading: false
      },
      questionExcelImport: {
        dialog: false,
        formLoading: false,
        fileList: [],
        form: {
          level: null,
          subjectId: null
        },
        rules: {
          level: [
            { required: true, message: '请选择作业类别', trigger: 'change' }
          ],
          subjectId: [
            { required: true, message: '请选择准操项目', trigger: 'change' }
          ]
        }
      },
      questionDelete: {
        dialog: false,
        formLoading: false,
        form: {
          level: null,
          subjectId: null
        },
        closed: true,
        rules: {
          level: [
            { required: true, message: '请选择作业类别', trigger: 'change' }
          ],
          subjectId: [
            { required: true, message: '请选择准操项目', trigger: 'change' }
          ]
        }
      }
    }
  },
  created () {
    this.initSubject()
    this.search()
  },
  methods: {
    submitForm () {
      this.queryParam.pageIndex = 1
      this.search()
    },
    search () {
      this.listLoading = true
      questionApi.pageList(this.queryParam).then(data => {
        const re = data.response
        this.tableData = re.list
        this.total = re.total
        this.queryParam.pageIndex = re.pageNum
        this.listLoading = false
      })
    },
    levelChange () {
      this.queryParam.subjectId = null
      this.subjectFilter = this.subjects.filter(data => data.level === this.queryParam.level)
    },
    confirmImportExcel () {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.questionExcelImport.formLoading = true
          this.$refs.upload.submit()
        }
      })
    },
    confirmConditionDelete () {
      this.$refs.delForm.validate((valid) => {
        if (valid) {
          this.questionDelete.formLoading = true
          let _this = this
          questionApi.delByCondition(_this.questionDelete.form).then(re => {
            if (re.code === 1) {
              _this.search()
              _this.$message.success(re.message)
              _this.deleteDialogClosed()
              _this.questionDelete.dialog = false
            } else {
              _this.$message.error(re.message)
              this.questionDelete.formLoading = false
            }
          })
        }
      })
    },
    handleUploadSuccess (response, file, fileList) {
      if (response.code === 1) {
        this.$message.success('导入成功！')
        this.questionExcelImport.form.level = null
        this.questionExcelImport.form.subjectId = null
        this.questionExcelImport.fileList = []
        this.questionExcelImport.dialog = false
      } else {
        this.$message.error(response.message)
        file.status = 'ready'
      }
      this.questionExcelImport.formLoading = false
    },
    handleUploadError (err, file, fileList) {
      this.questionExcelImport.formLoading = false
      this.$message.error(err)
    },
    importDialogClosed () {
      this.questionExcelImport.form.level = null
      this.questionExcelImport.form.subjectId = null
      this.questionExcelImport.fileList = []
      this.questionExcelImport.formLoading = false
    },
    deleteDialogClosed () {
      this.questionDelete.form.level = null
      this.questionDelete.form.subjectId = null
      this.questionDelete.formLoading = false
    },
    addQuestion () {
      this.$router.push('/exam/question/edit/singleChoice')
    },
    showQuestion (row) {
      let _this = this
      this.questionShow.dialog = true
      this.questionShow.loading = true
      questionApi.select(row.id).then(re => {
        _this.questionShow.qType = re.response.questionType
        _this.questionShow.question = re.response
        _this.questionShow.loading = false
      })
    },
    editQuestion (row) {
      let url = this.enumFormat(this.editUrlEnum, row.questionType)
      this.$router.push({ path: url, query: { id: row.id } })
    },
    deleteQuestion (row) {
      let _this = this
      questionApi.deleteQuestion(row.id).then(re => {
        if (re.code === 1) {
          _this.search()
          _this.$message.success(re.message)
        } else {
          _this.$message.error(re.message)
        }
      })
    },
    batchDelete () {
      let _this = this
      let deleteIds = this.tableSelectData.map(x => x.id)
      questionApi.batchDelete(deleteIds).then(re => {
        if (re.code === 1) {
          _this.search()
          _this.$message.success(re.message)
        } else {
          _this.$message.error(re.message)
        }
      })
    },
    conditionDelete () {
      let _this = this
      let deleteIds = this.tableSelectData.map(x => x.id)
      questionApi.batchDelete(deleteIds).then(re => {
        if (re.code === 1) {
          _this.search()
          _this.$message.success(re.message)
        } else {
          _this.$message.error(re.message)
        }
      })
    },
    excelImportLevelChange () {
      this.questionExcelImport.form.subjectId = null
      this.subjectFilter = this.subjects.filter(data => data.level === this.questionExcelImport.form.level)
    },
    conditionDelLevelChange () {
      this.questionDelete.form.subjectId = null
      this.subjectFilter = this.subjects.filter(data => data.level === this.questionDelete.form.level)
    },
    handleSelectionChange (val) {
      this.tableSelectData = val
    },
    questionTypeFormatter (row, column, cellValue, index) {
      return this.enumFormat(this.questionType, cellValue)
    },
    subjectFormatter (row, column, cellValue, index) {
      return this.subjectEnumFormat(cellValue)
    },
    ...mapActions('exam', { initSubject: 'initSubject' })
  },
  computed: {
    ...mapGetters('enumItem', ['enumFormat']),
    ...mapState('enumItem', {
      questionType: state => state.exam.question.typeEnum,
      editUrlEnum: state => state.exam.question.editUrlEnum,
      jobCategoryEnum: state => state.user.jobCategoryEnum
    }),
    ...mapGetters('exam', ['subjectEnumFormat']),
    ...mapState('exam', { subjects: state => state.subjects })
  }
}
</script>
