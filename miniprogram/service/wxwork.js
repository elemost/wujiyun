import request from './request';

// 生成企业签名
export function generateCompanySignature(url) {
  return request({
    'url': '/wecom/getCompanySignature',
    'method': 'post',
    'data': { url },
  })
}

// 生成应用签名
export function generateAppSignature(url) {
  return request({
    'url': '/wecom/geAppSignature',
    'method': 'post',
    'data': { url },
  })
}