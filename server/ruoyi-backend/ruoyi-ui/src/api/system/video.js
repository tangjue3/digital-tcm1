import request from '@/utils/request'

// 查询中医药视频列表
export function listVideo(query) {
  return request({
    url: '/system/video/list',
    method: 'get',
    params: query
  })
}

// 查询中医药视频详细
export function getVideo(id) {
  return request({
    url: '/system/video/' + id,
    method: 'get'
  })
}

// 新增中医药视频
export function addVideo(data) {
  return request({
    url: '/system/video',
    method: 'post',
    data: data
  })
}

// 修改中医药视频
export function updateVideo(data) {
  return request({
    url: '/system/video',
    method: 'put',
    data: data
  })
}

// 删除中医药视频
export function delVideo(id) {
  return request({
    url: '/system/video/' + id,
    method: 'delete'
  })
}
