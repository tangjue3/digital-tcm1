import request from '@/utils/request'

// 查询常见疾病列表
export function listCommondiseases(query) {
  return request({
    url: '/system/commondiseases/list',
    method: 'get',
    params: query
  })
}

// 查询常见疾病详细
export function getCommondiseases(id) {
  return request({
    url: '/system/commondiseases/' + id,
    method: 'get'
  })
}

// 新增常见疾病
export function addCommondiseases(data) {
  return request({
    url: '/system/commondiseases',
    method: 'post',
    data: data
  })
}

// 修改常见疾病
export function updateCommondiseases(data) {
  return request({
    url: '/system/commondiseases',
    method: 'put',
    data: data
  })
}

// 删除常见疾病
export function delCommondiseases(id) {
  return request({
    url: '/system/commondiseases/' + id,
    method: 'delete'
  })
}
