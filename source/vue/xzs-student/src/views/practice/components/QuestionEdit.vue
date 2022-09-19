<template>
  <div style="line-height:1.8">
    <div v-if="qType==1" v-loading="qLoading">
      <div class="q-title">{{serial}}、{{question.title}}
        <el-tag type="success" v-if="showResult && doRight">正确</el-tag>
        <el-tag type="danger" v-if="showResult && !doRight">错误</el-tag>
      </div>
      <div class="q-content">
        <el-form-item prop="content">
        <el-radio-group v-model="answer.content" @change="answer.completed = true" >
          <el-radio v-for="item in question.items"  :key="item.prefix"  :label="item.prefix" style="display: block;padding: 5px 0">
            <span class="question-prefix">{{item.prefix}}.</span>
            <span v-html="item.content" class="q-item-span-content"></span>
          </el-radio>
        </el-radio-group>
        </el-form-item>
      </div>
    </div>
    <div v-else-if="qType==2" v-loading="qLoading">
      <div>{{serial}}、</div>
      <div class="q-title" v-html="question.title"/>
      <div class="q-content">
        <el-checkbox-group v-model="answer.contentArray" @change="answer.completed = true" >
          <el-checkbox v-for="item in question.items" :label="item.prefix" :key="item.prefix"  >
            <span class="question-prefix">{{item.prefix}}.</span>
            <span v-html="item.content" class="q-item-span-content"></span>
          </el-checkbox>
        </el-checkbox-group>
      </div>
    </div>
    <div v-else-if="qType==3" v-loading="qLoading">
      <div class="q-title">{{serial}}、{{question.title}}
        <el-tag type="success" v-if="showResult && doRight">正确</el-tag>
        <el-tag type="danger" v-if="showResult && !doRight">错误</el-tag>
      </div>
<!--      <span style="padding-right: 10px;">(</span>-->
      <el-form-item prop="content">
      <el-radio-group v-model="answer.content" @change="answer.completed = true" >
        <el-radio  v-for="item in question.items"  :key="item.prefix"  :label="item.prefix" style="display: block;padding: 5px 0">
          <span v-html="item.content" class="q-item-span-content"></span>
        </el-radio>
      </el-radio-group>
      </el-form-item>
<!--      <span style="padding-left: 10px;">)</span>-->
    </div>
    <div v-else-if="qType==4" v-loading="qLoading">
      <div>{{serial}}、</div>
      <div class="q-title" v-html="question.title"/>
      <div>
        <el-form-item :label="item.prefix" :key="item.prefix"  v-for="item in question.items"  label-width="50px" style="margin-top: 10px;margin-bottom: 10px;">
          <el-input v-model="answer.contentArray[item.prefix-1]"  @change="answer.completed = true" />
        </el-form-item>
      </div>
    </div>
    <div v-else-if="qType==5" v-loading="qLoading">
      <div class="q-title" v-html="question.title"/>
      <div>
        <el-input v-model="answer.content" type="textarea" rows="5"  @change="answer.completed = true"/>
      </div>
    </div>
    <div v-else>
    </div>
  </div>

</template>

<script>
export default {
  name: 'QuestionShow',
  props: {
    question: {
      type: Object,
      default: function () {
        return {}
      }
    },
    answer: {
      type: Object,
      default: function () {
        return { id: null, content: '', contentArray: [] }
      }
    },
    qLoading: {
      type: Boolean,
      default: false
    },
    qType: {
      type: Number,
      default: 0
    },
    serial: {
      type: Number,
      default: 0
    },
    showResult: {
      type: Boolean,
      default: false
    },
    doRight: {
      type: Boolean,
      default: false
    }
  },
  methods: {
  }
}
</script>
