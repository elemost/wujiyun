import request from './request';

// 登录方法
export function loginApi(params) {
  return request({
    'url': '/login',
    'method': 'post',
    'data': params,
    'cusMsg': true,
    'shouldReturnDataDirectly': true,
  })
}

// 获取验证码
export function getCodeApi(phone) {
  return request({
    'url': `/login/message/${phone}`,
    'method': 'get',
    'shouldReturnDataDirectly': true,
  })
}
// 登录方法
export function registerUser(params) {
  return request({
    'url': '/user/complete',
    'method': 'post',
    'data': params,
    'cusMsg': true,
  })
}
export function checkCompanyName(params) {
  return request({
    'url': '/company/checkCompanyExist',
    'method':'post',
    'data': params,
    'cusMsg': true,
    'shouldReturnDataDirectly': true,
  })
}
export function switchCompany(params) {
  return request({
    'url': '/login/switchCompany',
    'method': 'post',
    'data': params
  })
}