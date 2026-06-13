import request from '@/utils/request'

// 查询中药材列表
export function listChinesemedicine(query) {
  return request({
    url: '/system/chinesemedicine/list',
    method: 'get',
    params: query
  })
}

// 查询中药材详细
export function getChinesemedicine(id) {
  return request({
    url: '/system/chinesemedicine/' + id,
    method: 'get'
  })
}

// 新增中药材
export function addChinesemedicine(data) {
  return request({
    url: '/system/chinesemedicine',
    method: 'post',
    data: data
  })
}

// 修改中药材
export function updateChinesemedicine(data) {
  return request({
    url: '/system/chinesemedicine',
    method: 'put',
    data: data
  })
}

// 删除中药材
export function delChinesemedicine(id) {
  return request({
    url: '/system/chinesemedicine/' + id,
    method: 'delete'
  })
}
