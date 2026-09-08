<template>
	<view class="login-container" :style="themeColor">
		<uv-toast ref="toast"></uv-toast>
		<!-- <image class="logo" mode="widthFix" src="/static/login/logo.png"></image> -->
		<view class="login-header">
			<view class="login-title-container">
				<!-- <text class="main-title">五极云</text> -->
				<image mode="widthFix" src="/static/login/logo.png"></image>
				<text class="sub-title">AI+aPaaS 企业级零代码应用搭建平台</text>
			</view>
			<image class="login-img" mode="widthFix" src="/static/login/login_top.png"></image>
		</view>
		<view class="agreement">
			<uv-checkbox-group size="14" v-model="checkboxValue" :activeColor="themeColor['--primary-color']"
				:customStyle="{ width: '100%' }" shape="circle">
				<uv-checkbox name="agree">
					<view class="checkbox-label-wrap">
						<text> 我已阅读并同意 </text>
						<text @click.stop="openProtocol(1)"
							:style="{ color: themeColor['--primary-color'], cursor: 'pointer' }"
							@mouseenter="hoverText = 1" @mouseleave="hoverText = 0">《使用协议》</text>
						<text> 和 </text>
						<text @click.stop="openProtocol(2)"
							:style="{ color: themeColor['--primary-color'], cursor: 'pointer', }"
							@mouseenter="hoverText = 2" @mouseleave="hoverText = 0">《隐私协议》</text>
					</view>
				</uv-checkbox>
			</uv-checkbox-group>
		</view>
		<view class="login-btn">
			<uv-button :loading="loading" text="手机号快捷登录/注册" @click="initWechatLogin" type="primary">
				<uv-icon name="mobile" custom-prefix="custom-icon" size="24" color="#ffffff"></uv-icon>
				<text style="font-size: 14px; margin-left: 16rpx;">手机号快捷登录/注册</text>
			</uv-button>
		</view>
		<view class="login-btn">
			<uv-button type="primary" text="其他登录/注册方式" :plain="true" :hairline="true" @click="otherLogin">
				<uv-icon name="my" custom-prefix="custom-icon" size="24"
					:color="themeColor['--primary-color']"></uv-icon>
				<text style="font-size: 14px;margin-left: 16rpx;">其他登录/注册方式</text>
			</uv-button>
		</view>

		<uv-modal ref="modal" title="提示" :closeOnClickOverlay="false" content='您还未绑定手机号，请绑定'>
			<template v-slot:confirmButton>
				<view class="modal-btns">
					<uv-button :customStyle="{ border: 0 }" @click="closeModal">取消</uv-button>
					<uv-button plain :customStyle="{ border: 0 }" type="primary" open-type="getPhoneNumber"
						:confirmColor="themeColor['--primary-color']" @getphonenumber="handleGetPhoneNumber">
						绑定手机号
					</uv-button>
				</view>
			</template>
		</uv-modal>
	</view>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useUserStore } from '@/store/user';
import { useGlobalTheme } from '@/utils/theme';
import { loginApi } from '@/service/login';
import { setToken } from '@/utils/auth';
import { openProtocol } from '@/utils/common';
import { onLoad } from '@dcloudio/uni-app';
import { tabbarPages } from '@/utils/constant';

const { themeColor } = useGlobalTheme();
const userStore = useUserStore();
const toast = ref(null);
const checkboxValue = ref([]);
const hoverText = ref(0);
const modal = ref(null);
const loading = ref(false);
const redirectUrl = ref('')

let storedCode = '';

const isAgreed = computed(() => checkboxValue.value.includes('agree'));

const closeModal = () => {
	modal.value.close();
};

const returnBeforeLoginPage = () => {
	if (redirectUrl.value && tabbarPages.includes(redirectUrl.value)) {
		uni.switchTab({
			url: redirectUrl.value
		});
	} else if (redirectUrl.value && !tabbarPages.includes(redirectUrl.value)) {
		uni.reLaunch({
			url: redirectUrl.value
		});
	} else {
		uni.switchTab({
			url: '/pages/index/index'
		});
	}
}

const initWechatLogin = () => {
	if (!isAgreed.value) {
		uni.$uv.toast('请先阅读并同意相关协议');
		return;
	}
	// 调用 wx.login 获取 code
	wx.login({
		success: (loginRes) => {
			if (loginRes.code) {
				storedCode = loginRes.code;
				loading.value = true;
				// 获取到 code 后，调用 loginApi
				loginApi({ code: storedCode, loginType: "miniapp_code" }).then(res => {
					if (res.otherData.newUser) {
						modal.value.open();
					} else {
						setToken(res.data)
						uni.$emit('loginSuccess');
						returnBeforeLoginPage()
					}
				}).catch(error => {
					toast.value.show({
						position: 'top',
						type: 'error',
						message: error ? JSON.stringify(error) : '登录接口调用失败，请重试',
					});
				}).finally(() => {
					loading.value = false;
				});
			} else {
				console.error('获取登录凭证失败:', loginRes.errMsg);
				toast.value.show({
					position: 'top',
					type: 'error',
					message: '获取登录凭证失败，请重试',
				});
			}
		},
		fail: (err) => {
			console.error('wx.login 调用失败:', err);
			toast.value.show({
				position: 'top',
				type: 'error',
				message: '登录接口调用失败，请重试',
			});
		}
	});
};

// 修改 handleGetPhoneNumber 方法以使用 storedCode
const handleGetPhoneNumber = (e) => {
	if (e.errMsg !== 'getPhoneNumber:ok') {
		uni.$uv.toast('获取手机号失败，请重试');
		return;
	}

	if (e.errMsg === 'getPhoneNumber:ok') {
		modal.value.close();
		// 调用 wx.login 获取 code
		wx.login({
			success: (loginRes) => {
				if (loginRes.code) {
					// 调用 loginApi 进行登录，传入 code 和 phoneCode 等参数
					loginApi({ code: loginRes.code, phoneCode: e.code, loginType: "miniapp" }).then(res => {
						setToken(res.data);
						if (res.otherData.newUser) {
							userStore.setShowForm(true);
							uni.redirectTo({
								url: '/pages/login/phoneLogin?redirectUrl=' + encodeURIComponent(redirectUrl.value)
							});
							return;
						}
						uni.$emit('loginSuccess');
						returnBeforeLoginPage()
					}).catch(error => {
						uni.$uv.toast('登录失败，请重试');
					}).finally(() => {
						loading.value = false;
					});
				} else {
					console.error('获取登录凭证失败:', loginRes.errMsg);
					toast.value.show({
						position: 'top',
						type: 'error',
						message: '获取登录凭证失败，请重试',
					});
				}
			},
			fail: (err) => {
				console.error('wx.login 调用失败:', err);
				toast.value.show({
					position: 'top',
					type: 'error',
					message: '登录接口调用失败，请重试',
				});
			}
		});
	}
};

// 其他登录/注册方式方法
const otherLogin = () => {
	// 跳转到手机登录页
	uni.navigateTo({
		url: '/pages/login/phoneLogin?redirectUrl=' + encodeURIComponent(redirectUrl.value)
	});
};

onLoad((option) => {
	const url = option?.redirectUrl ? decodeURIComponent(option?.redirectUrl) : ''
	redirectUrl.value = url
})

</script>

<style lang="scss" scoped>
/* 引入公共样式 */
@import '@/common/common.scss';

.login-container {
	padding: 60rpx 66rpx 60rpx;
	min-height: 100%;
	background-color: #FFFFFF;
	box-sizing: border-box;

	.logo {
		position: absolute;
		top: 30rpx;
		left: 30rpx;
		width: 100rpx;
	}

	.login-header {
		display: flex;
		flex-direction: column;
		align-items: center;
		margin-bottom: 60rpx;

		.login-img {
			width: 100%;
		}

		.login-title-container {
			display: flex;
			flex-direction: column;
			align-items: center;
			margin-top: 40rpx;

			image {
				width: 200rpx;
				margin-bottom: 20rpx;
			}
		}

		.main-title {
			font-size: 40rpx;
			line-height: 64rpx;
			font-weight: 500;
		}

		.sub-title {
			font-size: 28rpx; // 字体更小
			// color: #999999; // 颜色更浅
			margin-top: 10rpx;
		}
	}

	.agreement {
		display: flex;
		align-items: center;
		margin-bottom: 40rpx;
		font-size: 12px; // 统一字体大小
		text-align: center; // 文本居中

		.checkbox-label-wrap {
			font-size: 12px; // 字体大小;
			line-height: 14px;
		}
	}

	.login-btn {
		margin-bottom: 32rpx; // 新增向下外边距
	}
}
</style>
