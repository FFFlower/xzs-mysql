<template>
  <div>
    <el-container  class="app-item-contain">
      <el-main>
        <el-row>
          <el-col :span="16">
            <el-card class="box-card" style="padding: 50px">
              <el-table v-loading="listLoading" :data="tableData" fit highlight-current-row style="width: 100%">
                <el-table-column prop="id" label="序号" width="90px"/>
                <el-table-column prop="name" label="名称"  />
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
            </el-card>
          </el-col>
          <el-col :span="7" :offset="1">
            <el-card class="box-card">

            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination'
import examPaperApi from '@/api/examPaper'

import { mapState, mapGetters, mapActions } from 'vuex'

export default {
  components: { Pagination },
  data () {
    return {
      queryParam: {
        paperType: 1,
        subjectId: 0,
        pageIndex: 1,
        pageSize: 10
      },
      form: {},
      levelId: 0,
      subjectId: 0,
      listLoading: true,
      tableData: [],
      total: 0
    }
  },
  created () {
  },
  mounted () {

  },
  beforeDestroy () {
    window.clearInterval(this.timer)
  },
  methods: {
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
    subjectFormatter () {
      return this.subjectEnumSimpleFormat(this.subjectId)
    },
    jobCategoryFormatter () {
      return this.enumFormat(this.jobCategoryEnum, this.levelId)
    },
    ...mapActions('exam', { initSubject: 'initSubject' })
  },
  computed: {
    ...mapGetters('enumItem', ['enumFormat']),
    ...mapGetters('exam', ['subjectEnumSimpleFormat']),
    ...mapState('enumItem', {
      doCompletedTag: state => state.exam.question.answer.doCompletedTag,
      jobCategoryEnum: state => state.user.jobCategoryEnum
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
      font-size: 15px !important;
    }
  }

  .question-title-padding {
    padding-left: 25px;
    padding-right: 25px;
  }
</style>
