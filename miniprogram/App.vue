<script>
import { useThemeStore } from '@/store/theme';
import { useUserStore } from '@/store/user';

export default {
	onLaunch: function () {
		let that = this;
		// 检查设备类型并在手机端跳转到指定网址
		that.checkDeviceAndRedirect();
		// 检查并处理URL中的token参数
		that.handleUrlToken();
		// #ifdef MP-WEIXIN
		that.init();
		// #endif
		// 添加全局异常监听
		that.setupErrorHandler();
		// 监听登录成功事件,更新主题色
		uni.$on('loginSuccess', function () {
			that.init();
		})
	},
	methods: {
		// 检查设备类型并在手机端跳转到指定网址
		checkDeviceAndRedirect() {
			// #ifdef H5
			// 检测是否为移动设备
			const isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);

			if (!isMobile) {
				// 跳转到指定网址
				window.location.replace('https://cloud.elemost.com');
			}
			// #endif
		},
		// 处理URL中的token参数
		handleUrlToken() {
			// #ifdef H5
			// 获取URL参数
			const urlParams = new URLSearchParams(window.location.search);
			const token = urlParams.get('token');
			if (token) {
				// 设置token到本地存储
				import('@/utils/auth').then(({ setToken }) => {
					setToken(token);
					// 触发登录成功事件，更新用户信息
					uni.$emit('loginSuccess');
				});
			} else {
				this.init();
			}
			// #endif
		},
		init() {
			// 获取用户信息
			useUserStore().getUserInfo()
			// 初始化主题色
			useThemeStore().getThemeColor()
			// 获取用户公司列表
			useUserStore().getUserCompanyList()
		},
		setupErrorHandler() {
			// 全局错误捕获
			uni.onError((error) => {
				console.error('全局捕获错误:', error)
			})

			// 页面不存在处理
			uni.onPageNotFound((res) => {
				uni.redirectTo({
					url: '/pages/error/404'
				})
			})

			// 监听页面显示事件，处理页面跳转时携带的token
			uni.onAppShow(() => {
				this.handleUrlToken();
			})
		}
	}
}
</script>

<style lang="scss">
@import "@/static/iconfont/iconfont.css";
/* 引入公共样式 */
@import '@/common/common.scss';

@import '@/uni_modules/uv-ui-tools/index.scss';

/* 全局样式 */
page {
	height: 100%;
	background-color: #F6F6F8 !important;
	color: $text-color;
	font-size: 28rpx;
	font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', sans-serif;
}

.modal-btns {
	display: flex;
	width: 100%;
	border-top: 1px solid #e5e5e5;

	.uv-button-wrapper {
		width: 50%;
		border-radius: 0; // 取消圆角
		box-sizing: border-box;

		&:first-child {
			border-right: 1px solid #e5e5e5; // 左边框
		}
	}
}

// #ifdef H5
.uni-tabbar-bottom {
	display: none;
}

// #endif</style>