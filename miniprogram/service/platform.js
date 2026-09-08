import request from "./request";
import {
	appConfig
} from '@/config/app'

// 最近使用
export function getRecentlyUsedApi(params) {
    return request({
        'url': '/application/category/latestUseForm',
        'method': 'get',
        'params': params,
    })
}

// 获取流程中心待办数量
export function getflowableStatisticApi(params) {
    return request({
        'url': '/workbench/flowableStatistic',
        'method': 'get',
        'params':params,
        'cusMsg': true
    })
}

// 获取版本信息
export function getVersionInfoApi() {
    return request({
        'baseUrl': appConfig.webViewUrl,
        'url': '/version.json',
        'method': 'get',
    })
}
