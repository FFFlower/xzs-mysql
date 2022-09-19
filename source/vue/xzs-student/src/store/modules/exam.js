import subjectApi from '@/api/subject'

const state = {
  subjects: []
}

const getters = {
  subjectEnumFormat: (state) => (key) => {
    for (let item of state.subjects) {
      if (item.id === key) {
        return item.name + ' ( ' + item.levelName + ' )'
      }
    }
    return null
  },
  subjectEnumSimpleFormat: (state) => (key) => {
    for (let item of state.subjects) {
      if (item.id === key) {
        return item.name
      }
    }
    return null
  }
}

// actions
const actions = {
  initSubject ({ commit }) {
    subjectApi.allList().then(re => {
      commit('setSubjects', re.response)
    }).catch(e => {
      console.log(e)
    })
  },
  initSubjectByUser ({ commit }) {
    subjectApi.listByUser().then(re => {
      commit('setSubjects', re.response)
    }).catch(e => {
      console.log(e)
    })
  }
}

// mutations
const mutations = {
  setSubjects: (state, subjects) => {
    state.subjects = subjects
  }
}

export default {
  namespaced: true,
  state,
  getters,
  actions,
  mutations
}
