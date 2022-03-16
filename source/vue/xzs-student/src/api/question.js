import { post } from '@/utils/request'

export default {
  first: (levelId, subjectId, preId) => post('/api/student/question/first/' + levelId + '/' + subjectId + '/' + preId),
  page: data => post('/api/student/question/page', data),
  count: data => post('/api/student/question/count', data)
}
