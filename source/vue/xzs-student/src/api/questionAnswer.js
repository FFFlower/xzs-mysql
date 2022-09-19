import { post } from '@/utils/request'

export default {
  pageList: query => post('/api/student/question/answer/page', query),
  errorPageList: query => post('/api/student/exam/error/page', query),
  record: data => post('/api/student/question/answer/practice/record', data),
  viewRecord: data => post('/api/student/question/answer/practice/viewRecord', data),
  select: id => post('/api/student/question/answer/select/' + id),
  delError: id => post('/api/student/exam/error/del/' + id)
}
