import request from './request';

export function postMarketList(params) {
    return request({
        url: '/template/application/queryList',
        method: 'post',
        data: params
    })
}
// 模版详情
export async function getTemplateDetail(id) {
    return request({
        'url': `/template/application/info/${id}`,
        'method': 'get',
    })
}
// 使用模版
export async function useTemplate(id, needData) {
    return request({
        'url': `/template/application/useTemplate/${id}?needData=${needData}`,
        'method': 'get',
    })
}
