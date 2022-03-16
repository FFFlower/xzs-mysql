import { post } from '@/utils/request'

export default {
  pageList: query => post('/api/admin/question/page', query),
  edit: query => post('/api/admin/question/edit', query),
  select: id => post('/api/admin/question/select/' + id),
  batchDelete: data => post('/api/admin/question/batchDelete', data),
  deleteQuestion: id => post('/api/admin/question/delete/' + id),
  delByCondition: data => post('/api/admin/question/delByCondition', data)
}
