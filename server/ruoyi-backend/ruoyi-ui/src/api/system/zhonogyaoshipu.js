import request from '@/utils/request'

// 查询中药食谱列表
export function listZhonogyaoshipu(query) {
  return request({
    url: '/system/zhonogyaoshipu/list',
    method: 'get',
    params: query
  })
}

// 查询中药食谱详细
export function getZhonogyaoshipu(id) {
  return request({
    url: '/system/zhonogyaoshipu/' + id,
    method: 'get'
  })
}

// 新增中药食谱
export function addZhonogyaoshipu(data) {
  return request({
    url: '/system/zhonogyaoshipu',
    method: 'post',
    data: data
  })
}

// 修改中药食谱
export function updateZhonogyaoshipu(data) {
  return request({
    url: '/system/zhonogyaoshipu',
    method: 'put',
    data: data
  })
}

// 删除中药食谱
export function delZhonogyaoshipu(id) {
  return request({
    url: '/system/zhonogyaoshipu/' + id,
    method: 'delete'
  })
}
