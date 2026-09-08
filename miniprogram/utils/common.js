// 表单图标映射
import { formIconMap, formIconColorMap } from '@/utils/constant';
import { useGlobalTheme } from '@/utils/theme';
/**
* 显示消息提示框
*/
export function toast(content, type = 'none', duration = 2000) {
	uni.showToast({
		title: content,
		icon: type,
		duration: duration
	})
}

/**
* 显示模态弹窗
* @param content 提示的标题
*/
export function showConfirm(content) {
	return new Promise((resolve, reject) => {
		uni.showModal({
			title: '提示',
			content: content,
			cancelText: '取消',
			confirmText: '确定',
			confirmColor: themeColor.value['--primary-color'],
			success: function (res) {
				resolve(res)
			}
		})
	})
}

/**
 * 打开协议
 * @param {Number} type 1:服务协议 2:隐私协议
 */
export const openProtocol = (type) => {
	let url = '';
	if (type === 1) {
		url = 'https://www.elemost.com/五极网络服务条款.pdf';
	} else {
		url = 'https://www.elemost.com/五极网络用户隐私协议.pdf';
	}

	uni.downloadFile({
		url,
		success: (res) => {
			if (res.statusCode === 200) {
				uni.openDocument({
					filePath: res.tempFilePath,
					success: () => {
						console.log('文件打开成功');
					},
					fail: (err) => {
						console.error('文件打开失败', err);
						toast.value.show({
							position: 'top',
							type: 'error',
							message: '文件打开失败',
						});
					}
				});
			}
		},
		fail: (err) => {
			console.error('文件下载失败', err);
			toast.value.show({
				position: 'top',
				type: 'error',
				message: '文件下载失败',
			});
		}
	});
};

/**
 * 获取表单图标信息
 * @param {String} categoryType 表单类型
 * @param {String} type 图标类型
 */
export const getFormIcon = (categoryType, type) => {
	const { themeColor } = useGlobalTheme();
	if (type === 'icon') {
		return formIconMap[categoryType] || 'normal-form';
	} else if (type === 'color') {
		return formIconColorMap[categoryType] || themeColor.value['--primary-color'];
	}
	return '';
}

// 判断是安卓还是ios
export const getPlatform = () => {
	return uni.getSystemInfoSync().osName;
}

// 判断是否是企业微信
export const isWwEnv = () => {
	if (uni.getSystemInfoSync().uniPlatform === 'web') {
	const ua = navigator.userAgent.toLowerCase();
	return ua.includes('wxwork');
	}
	return false;
}
