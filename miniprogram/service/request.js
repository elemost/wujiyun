import {
	appConfig
} from '@/config/app'
import {
	getToken,
	logout
} from '@/utils/auth'
import {
	toast
} from '@/utils/common'
import { loginPages } from '@/utils/constant'

let timeout = 60000;
let baseUrl = appConfig.baseUrl;

const request = config => {
	config.header = config.header || {}
	// 是否自定义消息提示
	const cusMsg = config.cusMsg || false
	// 是否直接返回 data
	const shouldReturnDataDirectly = config.shouldReturnDataDirectly || false;
	if (getToken()) {
		config.header['Authorization'] = getToken()
	}
	// get请求映射params参数
	if (config.params) {
		let url = config.url + uni.$uv.queryParams(config.params)
		config.url = url
	}
	if (config.baseUrl) {
		baseUrl = config.baseUrl
	}
	console.log(baseUrl + config.url)
	return new Promise((resolve, reject) => {
		uni.request({
			method: config.method || 'get',
			timeout: config.timeout || timeout,
			url: baseUrl + config.url,
			data: config.data,
			header: config.header,
			dataType: 'json'
		}).then(response => {
			let { statusCode, data } = response
			if (statusCode !== 200) {
				toast('请求出错：' + statusCode)
				reject('后端接口连接异常')
				return
			}

			if (config.url.includes('/version.json')) {
				resolve(data)
				return
			}

			const code = data.code || '0000';

			if (code === '0000') {
				if (shouldReturnDataDirectly) {
					resolve(data);
				} else {
					resolve(data.data)
				}
			} else if (code === '9004') {
				// 获取当前页面路径
				const pages = getCurrentPages()
				const currentRoute = uni.$uv.page()
				let currentPage = pages[pages.length - 1].$page.fullPath
				if (currentPage.includes('scene')) {
					currentPage = decodeURIComponent(currentPage)
				}
				if (!loginPages.includes(currentRoute)) {
					logout(currentPage)
				}
				reject('无效的会话，或者会话已过期，请重新登录。')
			} else {
				!cusMsg && toast(data.message)
				reject(data)
			}
		}).catch(error => {
			toast('请求出错：' + JSON.stringify(error))
			reject(error)
		})
	})
}

export default request