import request from '@/utils/request'

// 查询class列表
export function listClass(query) {
  return request({
    url: '/class/class/list',
    method: 'get',
    params: query
  })
}

// 查询class详细
export function getClass(id) {
  return request({
    url: '/class/class/' + id,
    method: 'get'
  })
}

// 新增class
export function addClass(data) {
  return request({
    url: '/class/class',
    method: 'post',
    data: data
  })
}

// 修改class
export function updateClass(data) {
  return request({
    url: '/class/class',
    method: 'put',
    data: data
  })
}

// 删除class
export function delClass(id) {
  return request({
    url: '/class/class/' + id,
    method: 'delete'
  })
}
