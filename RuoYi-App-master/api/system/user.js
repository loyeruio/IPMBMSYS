import upload from '@/utils/upload'
import request from '@/utils/request'

// 用户密码重置
export function updateUserPwd(oldPassword, newPassword) {
  const data = {
    oldPassword,
    newPassword
  }
  return request({
    url: '/system/user/profile/updatePwd',
    method: 'put',
    data: data
  })
}

// 查询用户个人信息
export function getUserProfile() {
  return request({
    url: '/system/user/profile',
    method: 'get'
  })
}

// 修改用户个人信息
export function updateUserProfile(data) {
  return request({
    url: '/system/user/profile',
    method: 'put',
    data: data
  })
}

// 用户头像上传
export function uploadAvatar(data) {
  return upload({
    url: '/system/user/profile/avatar',
    name: data.name,
    filePath: data.filePath
  })
}

// 查询用户列表（不分页）
export function listAllUser(query) {
  return request({
    url: '/system/user/listAll',
    method: 'get',
    params: query
  })
}

// 查询用户列表
export function listUser(query) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params: query
  })
}

// 根据用户ID获取角色列表
export function getRoleListByUserId(userId) {
  return request({
    url: '/system/user/roles/' + userId,
    method: 'get'
  })
}

// 根据用户ID获取职务列表
export function getPostListByUserId(userId) {
  return request({
    url: '/system/user/posts/' + userId,
    method: 'get'
  })
}
