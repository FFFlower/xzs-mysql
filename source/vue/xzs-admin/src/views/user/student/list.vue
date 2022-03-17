<template>
  <div class="app-container">
    <el-form :model="queryParam" ref="queryForm" :inline="true">
      <el-form-item label="用户名：">
        <el-input v-model="queryParam.userName"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm">查询</el-button>
        <router-link :to="{path:'/user/student/edit'}" class="link-left">
          <el-button type="primary">添加</el-button>
        </router-link>
        <el-button type="danger" @click="batchDelete" class="link-left">批量删除</el-button>
        <el-button type="primary" @click="excelImport.dialog = true">Excel导入</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="listLoading" :data="tableData" border fit highlight-current-row style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="40"></el-table-column>
      <el-table-column prop="id" label="Id" />
      <el-table-column prop="userName" label="用户名"/>
      <el-table-column prop="realName" label="真实姓名" />
      <el-table-column prop="userLevel" label="作业类别"  :formatter="levelFormatter"/>
      <el-table-column prop="sex" label="性别" width="60px;" :formatter="sexFormatter"/>
      <el-table-column prop="phone" label="手机号"/>
      <el-table-column prop="createTime" label="创建时间" width="160px"/>
      <el-table-column label="状态" prop="status" width="70px">
        <template slot-scope="{row}">
          <el-tag :type="statusTagFormatter(row.status)">
            {{ statusFormatter(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column width="270px" label="操作" align="center">
        <template slot-scope="{row}">
          <el-button size="mini"  @click="changeStatus(row)" class="link-left">
            {{ statusBtnFormatter(row.status) }}
          </el-button>
          <router-link :to="{path:'/user/student/edit', query:{id:row.id}}" class="link-left">
            <el-button size="mini" >编辑</el-button>
          </router-link>
          <router-link :to="{path:'/log/user/list', query:{userId:row.id}}" class="link-left">
            <el-button size="mini" >日志</el-button>
          </router-link>
          <el-button  size="mini" type="danger" @click="deleteUser(row)" class="link-left">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" :page.sync="queryParam.pageIndex" :limit.sync="queryParam.pageSize"
                @pagination="search"/>
    <el-dialog :visible.sync="excelImport.dialog" @closed="importDialogClosed" style="width: 100%;height: 100%">
      <el-form :model="excelImport.form" ref="form" :rules="excelImport.rules" label-width="100px" v-loading="excelImport.formLoading">
        <el-form-item label="作业类别：" prop="level">
          <el-select v-model="excelImport.form.level" placeholder="作业类别" @change="excelImportLevelChange">
            <el-option v-for="item in jobCategoryEnum" :key="item.key" :value="item.key" :label="item.value"></el-option>
          </el-select>
        </el-form-item>
<!--        <el-form-item label="准操项目：" prop="subjectId">-->
<!--          <el-select v-model="excelImport.form.subjectId" placeholder="准操项目">-->
<!--            <el-option v-for="item in subjectFilter" :key="item.id" :value="item.id"-->
<!--                       :label="item.name+' ( '+item.levelName+' )'"></el-option>-->
<!--          </el-select>-->
<!--        </el-form-item>-->
        <el-form-item label="附件：" prop="file">
          <el-upload
            class="upload-demo"
            ref="upload"
            :data="excelImport.form"
            action="/api/admin/user/importExcel"
            :file-list="excelImport.fileList"
            :auto-upload="false"
            accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet,application/vnd.ms-excel"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :limit="1">
            <el-button slot="trigger" size="small" type="primary">选取Excel文件</el-button>
          </el-upload>
          <el-link href="/file/user_model.xlsx" target="_blank" type="primary">点击下载Excel导入模版</el-link>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="excelImport.dialog = false">取 消</el-button>
        <el-button type="primary" :loading="excelImport.formLoading" @click="confirmImportExcel">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters, mapState } from 'vuex'
import Pagination from '@/components/Pagination'
import userApi from '@/api/user'

export default {
  components: { Pagination },
  data () {
    return {
      queryParam: {
        userName: '',
        role: 1,
        pageIndex: 1,
        pageSize: 10
      },
      listLoading: true,
      tableData: [],
      tableSelectData: [],
      total: 0,
      excelImport: {
        dialog: false,
        formLoading: false,
        fileList: [],
        form: {
          level: null
        },
        rules: {
          level: [
            { required: true, message: '请选择作业类别', trigger: 'change' }
          ]
        }
      }
    }
  },
  created () {
    this.search()
  },
  methods: {
    search () {
      this.listLoading = true
      userApi.getUserPageList(this.queryParam).then(data => {
        const re = data.response
        this.tableData = re.list
        this.total = re.total
        this.queryParam.pageIndex = re.pageNum
        this.listLoading = false
      })
    },
    changeStatus (row) {
      let _this = this
      userApi.changeStatus(row.id).then(re => {
        if (re.code === 1) {
          row.status = re.response
          _this.$message.success(re.message)
        } else {
          _this.$message.error(re.message)
        }
      })
    },
    deleteUser (row) {
      let _this = this
      userApi.deleteUser(row.id).then(re => {
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
      userApi.batchDelete(deleteIds).then(re => {
        if (re.code === 1) {
          _this.search()
          _this.$message.success(re.message)
        } else {
          _this.$message.error(re.message)
        }
      })
    },
    handleSelectionChange (val) {
      this.tableSelectData = val
    },
    submitForm () {
      this.queryParam.pageIndex = 1
      this.search()
    },
    levelFormatter  (row, column, cellValue, index) {
      return this.enumFormat(this.jobCategoryEnum, cellValue)
    },
    sexFormatter  (row, column, cellValue, index) {
      return this.enumFormat(this.sexEnum, cellValue)
    },
    statusFormatter (status) {
      return this.enumFormat(this.statusEnum, status)
    },
    statusTagFormatter (status) {
      return this.enumFormat(this.statusTag, status)
    },
    statusBtnFormatter (status) {
      return this.enumFormat(this.statusBtn, status)
    },
    handleUploadSuccess (response, file, fileList) {
      if (response.code === 1) {
        this.$message.success('导入成功！')
        this.excelImport.form.level = null
        this.excelImport.form.subjectId = null
        this.excelImport.fileList = []
        this.excelImport.dialog = false
        this.search()
      } else {
        this.$message.error(response.message)
        file.status = 'ready'
      }
      this.excelImport.formLoading = false
    },
    handleUploadError (err, file, fileList) {
      this.excelImport.formLoading = false
      this.$message.error(err)
    },
    importDialogClosed () {
      this.excelImport.form.level = null
      this.excelImport.form.subjectId = null
      this.excelImport.fileList = []
      this.excelImport.formLoading = false
    },
    excelImportLevelChange () {
      // this.excelImport.form.subjectId = null
      // this.subjectFilter = this.subjects.filter(data => data.level === this.excelImport.form.level)
    },
    confirmImportExcel () {
      this.$refs.form.validate((valid) => {
        if (valid) {
          this.excelImport.formLoading = true
          this.$refs.upload.submit()
        }
      })
    }
  },
  computed: {
    ...mapGetters('enumItem', [
      'enumFormat'
    ]),
    ...mapState('enumItem', {
      sexEnum: state => state.user.sexEnum,
      statusEnum: state => state.user.statusEnum,
      statusTag: state => state.user.statusTag,
      statusBtn: state => state.user.statusBtn,
      jobCategoryEnum: state => state.user.jobCategoryEnum
    })
  }
}
</script>
