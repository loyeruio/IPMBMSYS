import request from '@/utils/request'

// 查询study列表
export function listLearning(query) {
  return request({
    url: '/learning/learning/list',
    method: 'get',
    params: query
  })
}

// 查询study详细
export function getLearning(id) {
  return request({
    url: '/learning/learning/' + id,
    method: 'get'
  })
}

// 新增study
export function addLearning(data) {
  return request({
    url: '/learning/learning',
    method: 'post',
    data: data
  })
}

// 修改study
export function updateLearning(data) {
  return request({
    url: '/learning/learning',
    method: 'put',
    data: data
  })
}

// 删除study
export function delLearning(id) {
  return request({
    url: '/learning/learning/' + id,
    method: 'delete'
  })
}
