// initial state
const state = {
  user: {
    sexEnum: [{ key: 1, value: '男' }, { key: 2, value: '女' }],
    statusEnum: [{ key: 1, value: '启用' }, { key: 2, value: '禁用' }],
    levelEnum: [{ key: 1, value: '一年级' }, { key: 2, value: '二年级' }, { key: 3, value: '三年级' }, { key: 4, value: '四年级' }, { key: 5, value: '五年级' }, { key: 6, value: '六年级' },
      { key: 7, value: '初一' }, { key: 8, value: '初二' }, { key: 9, value: '初三' },
      { key: 10, value: '高一' }, { key: 11, value: '高二' }, { key: 12, value: '高三' }],
    roleEnum: [{ key: 1, value: '学生' }, { key: 2, value: '教师' }, { key: 3, value: '管理员' }],
    statusTag: [{ key: 1, value: 'success' }, { key: 2, value: 'danger' }],
    statusBtn: [{ key: 1, value: '禁用' }, { key: 2, value: '启用' }],
    jobCategoryEnum: [{ key: 1, value: '电工作业' }, { key: 2, value: '焊接与热切割作业' }, { key: 3, value: '高处作业' },
      { key: 4, value: '制冷与空调作业' }, { key: 5, value: '金属非金属矿山安全作业' }, { key: 6, value: '冶金（有色）生产安全作业' },
      { key: 7, value: '危险化学品安全作业' }, { key: 8, value: '烟花爆竹安全作业' }]
  },
  exam: {
    examPaper: {
      paperTypeEnum: [{ key: 1, value: '固定试卷' },
        { key: 4, value: '时段试卷' },
        { key: 6, value: '任务试卷' }
      ],
      setMethodEnum: [
        { key: 1, value: '/exam/paper/edit', name: '顺序出题' },
        { key: 2, value: '/exam/paper/edit', name: '随机抽题' },
        { key: 3, value: '/exam/paper/edit', name: '自选' }
      ]
    },
    question: {
      typeEnum: [{ key: 1, value: '单选题' },
        // { key: 2, value: '多选题' },
        { key: 3, value: '判断题' }
        // { key: 4, value: '填空题' },
        // { key: 5, value: '简答题' }
      ],
      editUrlEnum: [{ key: 1, value: '/exam/question/edit/singleChoice', name: '单选题' },
        // { key: 2, value: '/exam/question/edit/multipleChoice', name: '多选题' },
        { key: 3, value: '/exam/question/edit/trueFalse', name: '判断题' }
        // { key: 4, value: '/exam/question/edit/gapFilling', name: '填空题' },
        // { key: 5, value: '/exam/question/edit/shortAnswer', name: '简答题' }
      ]
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
