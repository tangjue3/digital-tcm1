import request from '@/utils/request'

// 查询实验案例库列表
export function listCase(query) {
  return request({
    url: '/system/case/list',
    method: 'get',
    params: query
  })
}

// 查询实验案例库详细
export function getCase(id) {
  return request({
    url: '/system/case/' + id,
    method: 'get'
  })
}

// 新增实验案例库
export function addCase(data) {
  return request({
    url: '/system/case',
    method: 'post',
    data: data
  })
}

// 修改实验案例库
export function updateCase(data) {
  return request({
    url: '/system/case',
    method: 'put',
    data: data
  })
}

// 删除实验案例库
export function delCase(id) {
  return request({
    url: '/system/case/' + id,
    method: 'delete'
  })
}
