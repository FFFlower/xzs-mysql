import { post } from '@/utils/request'

export default {
  select: id => post('/api/student/exam/paper/select/' + id),
  autoGenerate: query => post('/api/student/exam/paper/autoGenerate', query),
  pageList: query => post('/api/student/exam/paper/pageList', query)
}
