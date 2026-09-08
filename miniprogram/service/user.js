import request from './request';

// 获取用户所在当前公司信息
export function getCompanyInfoApi() {
  return request({
    'url': '/company/currentCompany',
    'method': 'get',
  })
}

// 获取用户信息
export function getUserInfoApi() {
  return request({
    'url': '/user/currentUser',
    'method': 'post',
  })
}

// 获取用户所有公司
export function getUserCompanyListApi() {
  return request({
    'url': '/company/user/company/list',
    'method': 'get',
  })
}

// 切换公司
export async function switchCompanyApi(params) {
  return request({
    'url': '/login/switchCompany',
    'method': 'post',
    'data': params,
  })
}
export async function userList(params) {
  return request({
    'url': '/user/list',
    'method': 'post',
    'data': params,
  })
}
// 获取某人信息
export function userInfo(id) {
  return request({
    'url': `/user/info/${id}`,
    'method': 'get',
  })
}
// 角色
export async function postList(params) {
  return request({
    'url': '/post/list',
    'method': 'post',
    'data': params,
  })
}
// 组织
export async function deptList() {
  return request({
    'url': '/department/tree?deptType=00',
    'method': 'post',
  })
}
// 修改信息
export async function rosterApi(params) {
  return request({
    'url': '/user/update/roster',
    'method': 'post',
    'data': params,
  })
}

// 文件上传
export async function uploadFile(params) {
  return request({
    'url': '/common/uploadFile',
    'method': 'post',
    'data': params,
  })
}
