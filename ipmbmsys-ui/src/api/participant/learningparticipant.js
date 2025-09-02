import request from '@/utils/request'

// 查询participant列表
export function listParticipant(query) {
  return request({
    url: '/participant/participant/list',
    method: 'get',
    params: query
  })
}

// 查询participant详细
export function getParticipant(id) {
  return request({
    url: '/participant/participant/' + id,
    method: 'get'
  })
}

// 新增participant
export function addParticipant(data) {
  return request({
    url: '/participant/participant',
    method: 'post',
    data: data
  })
}

// 修改participant
export function updateParticipant(data) {
  return request({
    url: '/participant/participant',
    method: 'put',
    data: data
  })
}

// 删除participant
export function delParticipant(id) {
  return request({
    url: '/participant/participant/' + id,
    method: 'delete'
  })
}
