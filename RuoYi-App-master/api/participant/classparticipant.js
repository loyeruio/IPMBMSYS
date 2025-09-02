import request from '@/utils/request'

// 查询党课参与人员列表
export function listClassparticipant(query) {
  return request({
    url: '/meeting/classparticipant/list',
    method: 'get',
    params: query
  })
}

// 查询党课参与人员详细
export function getClassparticipant(id) {
  return request({
    url: '/meeting/classparticipant/' + id,
    method: 'get'
  })
}

// 新增党课参与人员
export function addClassparticipant(data) {
  return request({
    url: '/meeting/classparticipant',
    method: 'post',
    data: data
  })
}

// 修改党课参与人员
export function updateClassparticipant(data) {
  return request({
    url: '/meeting/classparticipant',
    method: 'put',
    data: data
  })
}

// 删除党课参与人员
export function delClassparticipant(id) {
  return request({
    url: '/meeting/classparticipant/' + id,
    method: 'delete'
  })
}
