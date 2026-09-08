import request from "./request";

// 获取我的应用列表
export function getMyApplicationApi(params) {
    return request({
        'url': '/workbench/application/queryList',
        'method': 'post',
        'data': params,
    })
}

// 获取当前用户所有应用和表单
export function getApplicationAndFormApi() {
    return request({
        'url': '/application/myAllPrivilege',
        'method': 'get'
    })
}

// 获取应用详情
export function getApplicationDetailApi(applicationId) {
    return request({
        'url': `/application/detail/${applicationId}`,
        'method': 'get',
    })
}

// 获取应用菜单
export function getApplicationMenuApi(applicationId) {
    return request({
        'url': `/workbench/application/category/selectList/${applicationId}`,
        'method': 'get',
    })
}

// 获取应用表单
export function getApplicationFormApi(applicationId, formId) {
    return request({
        'url': `/form/info/${formId}?applicationId=${applicationId}`,
       'method': 'get',
    })
}