// initial state
const state = {
  user: {
    sexEnum: [{ key: 1, value: '男' }, { key: 2, value: '女' }],
    levelEnum: [{ key: 1, value: '一年级' }, { key: 2, value: '二年级' }, { key: 3, value: '三年级' }, { key: 4, value: '四年级' }, { key: 5, value: '五年级' }, { key: 6, value: '六年级' },
      { key: 7, value: '初一' }, { key: 8, value: '初二' }, { key: 9, value: '初三' },
      { key: 10, value: '高一' }, { key: 11, value: '高二' }, { key: 12, value: '高三' }],
    roleEnum: [{ key: 1, value: '学生' }, { key: 2, value: '教师' }, { key: 3, value: '管理员' }],
    message: {
      readTag: [{ key: true, value: 'success' }, { key: false, value: 'warning' }],
      readText: [{ key: true, value: '已读' }, { key: false, value: '未读' }]
    },
    jobCategoryEnum: [{ key: 1, value: '电工作业' }, { key: 2, value: '焊接与热切割作业' }, { key: 3, value: '高处作业' },
      { key: 4, value: '制冷与空调作业' }, { key: 5, value: '金属非金属矿山安全作业' }, { key: 6, value: '冶金（有色）生产安全作业' },
      { key: 7, value: '危险化学品安全作业' }, { key: 8, value: '烟花爆竹安全作业' }]
  },
  exam: {
    examPaper: {
      paperTypeEnum: [{ key: 1, value: '固定试卷' }, { key: 8, value: '训练试卷' }]
    },
    examPaperAnswer: {
      statusEnum: [{ key: 1, value: '待批改' }, { key: 2, value: '完成' }],
      statusTag: [{ key: 1, value: 'warning' }, { key: 2, value: 'success' }]
    },
    question: {
      typeEnum: [{ key: 1, value: '单选题' }, { key: 2, value: '多选题' }, { key: 3, value: '判断题' }, { key: 4, value: '填空题' }, { key: 5, value: '简答题' }],
      answer: {
        doRightTag: [{ key: true, value: 'success' }, { key: false, value: 'danger' }, { key: null, value: 'warning' }],
        doRightEnum: [{ key: true, value: '正确' }, { key: false, value: '错误' }, { key: null, value: '待批改' }],
        doCompletedTag: [{ key: false, value: 'info' }, { key: true, value: 'success' }]
      }
    }
  }
}

// getters
const getters = {
  enumFormat: (state) => (arrary, key) => {
    return format(arrary, key)
  }
}

// actions
const actions = {}

// mutations
const mutations = {}

const format = function (array, key) {
  for (let item of array) {
    if (item.key === key) {
      return item.value
    }
  }
  return null
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}
