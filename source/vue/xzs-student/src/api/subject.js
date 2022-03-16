import { post } from '@/utils/request'

export default {
  list: query => post('/api/student/education/subject/list'),
  allList: query => post('/api/student/education/subject/all-list'),
  select: id => post('/api/student/education/subject/select/' + id)
}
