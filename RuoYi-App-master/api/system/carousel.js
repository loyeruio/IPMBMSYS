import request from '@/utils/request'

// 查询轮播图管理列表
export function listCarousel(query) {
  return request({
    url: '/system/carousel/list',
    method: 'get',
    params: query
  })
}

// 公开接口：查询轮播图列表（无需权限）
export function listCarouselPublic(query) {
  return request({
    url: '/system/carousel/public/list',
    method: 'get',
    params: query
  })
}

// 查询轮播图管理详细
export function getCarousel(id) {
  return request({
    url: '/system/carousel/' + id,
    method: 'get'
  })
}

// 新增轮播图管理
export function addCarousel(data) {
  return request({
    url: '/system/carousel',
    method: 'post',
    data: data
  })
}

// 修改轮播图管理
export function updateCarousel(data) {
  return request({
    url: '/system/carousel',
    method: 'put',
    data: data
  })
}

// 删除轮播图管理
export function delCarousel(id) {
  return request({
    url: '/system/carousel/' + id,
    method: 'delete'
  })
}
