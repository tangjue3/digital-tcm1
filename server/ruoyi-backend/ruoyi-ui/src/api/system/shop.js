import request from '@/utils/request'

// 查询中医房列表
export function listShop(query) {
  return request({
    url: '/system/shop/list',
    method: 'get',
    params: query
  })
}

// 查询中医房详细
export function getShop(id) {
  return request({
    url: '/system/shop/' + id,
    method: 'get'
  })
}

// 新增中医房
export function addShop(data) {
  return request({
    url: '/system/shop',
    method: 'post',
    data: data
  })
}

// 修改中医房
export function updateShop(data) {
  return request({
    url: '/system/shop',
    method: 'put',
    data: data
  })
}

// 删除中医房
export function delShop(id) {
  return request({
    url: '/system/shop/' + id,
    method: 'delete'
  })
}
