import request from '@/utils/request'

// 查询会议参与人员列表
export function listMeetingparticipant(query) {
  return request({
    url: '/meeting/meetingparticipant/list',
    method: 'get',
    params: query
  })
}

// 查询会议参与人员详细
export function getMeetingparticipant(id) {
  return request({
    url: '/meeting/meetingparticipant/' + id,
    method: 'get'
  })
}

// 新增会议参与人员
export function addMeetingparticipant(data) {
  return request({
    url: '/meeting/meetingparticipant',
    method: 'post',
    data: data
  })
}

// 修改会议参与人员
export function updateMeetingparticipant(data) {
  return request({
    url: '/meeting/meetingparticipant',
    method: 'put',
    data: data
  })
}

// 删除会议参与人员
export function delMeetingparticipant(id) {
  return request({
    url: '/meeting/meetingparticipant/' + id,
    method: 'delete'
  })
}
