import request from '@/utils/request'

// 查询会议管理列表
export function listCommon(query) {
  return request({
    url: '/meeting/meeting/list',
    method: 'get',
    params: query
  })
}

// 查询会议管理详细
export function getCommon(id) {
  return request({
    url: '/meeting/meeting/' + id,
    method: 'get'
  })
}

// 新增会议管理
export function addCommon(data) {
  return request({
    url: '/meeting/meeting',
    method: 'post',
    data: data
  })
}

// 修改会议管理
export function updateCommon(data) {
  return request({
    url: '/meeting/meeting',
    method: 'put',
    data: data
  })
}

// 删除会议管理
export function delCommon(id) {
  return request({
    url: '/meeting/meeting/' + id,
    method: 'delete'
  })
}
