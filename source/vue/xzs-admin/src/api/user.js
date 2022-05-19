import { post } from '@/utils/request'

export default {
  getUserPageList: query => post('/api/admin/user/page/list', query),
  getUserEventPageList: query => post('/api/admin/user/event/page/list', query),
  createUser: query => post('/api/admin/user/edit', query),
  selectUser: id => post('/api/admin/user/select/' + id),
  getCurrentUser: () => post('/api/admin/user/current'),
  updateUser: query => post('/api/admin/user/update', query),
  changeStatus: id => post('/api/admin/user/changeStatus/' + id),
  deleteUser: id => post('/api/admin/user/delete/' + id),
  batchDelete: data => post('/api/admin/user/batchDelete', data),
  updateSubject: data => post('/api/admin/user/updateSubject', data),
  selectByUserName: query => post('/api/admin/user/selectByUserName', query),
  list: query => post('/api/admin/user/list', query)
}
