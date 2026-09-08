/**
 * 分享功能封装
 * @param {Object} options - 分享配置
 * @param {boolean} options.isTimeline - 是否开启朋友圈分享
 * @param {string} options.query - 分享参数,仅朋友圈分享生效
 * @param {string} options.title - 分享标题
 * @param {string} options.imageUrl - 分享图片
 * @param {string} [options.path] - 分享路径，仅分享好友生效
 * @param {function} [options.success] - 分享成功回调
 * @param {function} [options.fail] - 分享失败回调
 * @param {void} [options.onShareAppMessage] - 分享场景，仅分享好友生效
 * @param {void} [options.onShareTimeline] - 分享场景，仅朋友圈分享生效
 */
export function shareApp(options) {
    // 开启朋友圈分享时两个分享都需要配置
    if (options.onShareTimeline) {
        options.onShareTimeline(() => ({
            title: options.title,
            imageUrl: options.imageUrl,
            success: () => {
                options.success && options.success()
            },
            fail: (err) => {
                options.fail && options.fail(err)
            }
        }));
    }
    // 分享好友时配置
    if (options.onShareAppMessage) {
        options.onShareAppMessage(() => ({
            title: options.title,
            path: options.path || '/pages/index/index',
            imageUrl: options.imageUrl,
            success: () => {
                options.success && options.success()
            },
            fail: (err) => {
                options.fail && options.fail(err)
            }
        }));
    }
}