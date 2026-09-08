import request from './request';

// 获取用户信息
export function getMessage(params) {
    return request({
        'url': '/message',
        'method': 'post',
        'data': params,
        'shouldReturnDataDirectly': true,
    })
}
// {"pageNum":1,"pageSize":10,"status":""}  rejected（驳回）
// 待办列表
export function getPendingList(params) {
    return request({
        'url': '/form/workflow/getPendingList',
        'method': 'post',
        'data': params,
    })
}
// 我发起的  status: "running"  运行中  status: "completed"  已完成  
export function getOwnerList(params) {
    return request({
        'url': '/form/workflow/getOwnerList',
        'method': 'post',
        'data': params,
    })
}
// 我处理的
export function getDoneList(params) {
    return request({
        'url': '/form/workflow/getDoneList',
        'method': 'post',
        'data': params,
    })
}
// 抄送我的  userView：true/false
export function getCopyList(params) {
    return request({
        'url': '/form/workflow/getCopyList',
        'method': 'post',
        'data': params,
    })
}

// 新增接口：标记消息为已读
export function markMessageAsViewed(messageId) {
    return request({
        'url': `/message/user/view/${messageId}`,
        'method': 'put',
        'data': {}
    });
}

// 新增接口：获取未读消息
export function getNotView() {
    return request({
        'url': '/message/notView',
        'method': 'get',
        'data': {}
    });
}
// 待办数量
export function flowableStatistic(params) {
    return request({
        'url': '/workbench/flowableStatistic',
        'method': 'get',
        'data': params
    });
}
// 消息详情
export function getMessageDetail(messageId) {
    return request({
        'url': `/message/info/${messageId}`,
        'method': 'get',
        'data': {}
    });
}
