<template>
	<view class="index-container" :style="{ ...themeColor }">
		<!-- 自定义导航栏内容 -->
		<view class="custom-navbar" :style="{ height: navBarHeight + 'px', paddingTop: statusBarHeight + 'px' }">
			<template v-if="companyInfo">
				<!-- #ifdef MP-WEIXIN -->
				<uv-drop-down ref="dropDown" :is-sticky="false" sign="dropDown_1" :custom-style="{ padding: '0 64rpx' }"
					:extra-icon="{ name: 'arrow-down-fill', color: '#181818', size: '24rpx' }"
					:extra-active-icon="{ name: 'arrow-up-fill', color: '#181818', size: '24rpx' }" text-size="32rpx"
					text-active-size="32rpx" text-color="#181818" text-active-color="#181818">
					<uv-drop-down-item name="company" type="2" :label="companyInfo.companyName" maxWidth="400rpx"
						:value="companyInfo.companyId">
					</uv-drop-down-item>
				</uv-drop-down>
				<!-- #endif -->
				 <!-- #ifdef H5 -->
				<view class="company-name">{{companyInfo.companyName}}</view>
				<!-- #endif -->
			</template>
			<template v-else>
				<view class="appName">
					<!-- #ifdef H5 -->
					<view class="title">欢迎来到五极云</view>
					<!-- #endif -->
					<view @click="goLogin" class="btn" :style="{ height: capsuleHeight + 'px' }">登录 / 注册五极云</view>
				</view>
			</template>
		</view>
		<uv-drop-down-popup @popupChange="popupChange" sign="dropDown_1" :click-overlay-on-close="true"
			ref="dropDownPopup">
			<scroll-view :scroll-top="scrollTop" scroll-y="true" class="company-list">
				<view :class="['company-list-item', activeIndex === index ? 'active-item' : '']"
					v-for="(item, index) in companyList" :key="item.companyId" @click="companySelect(item)">
					<image class="company-logo" :src="item.logo || '/static/img/default-head.jpg'" />
					<text class="company-name">
						{{ item.companyName }}
					</text>
					<uv-icon v-if="activeIndex === index" name="checkmark" size="20"
						:color="themeColor['--primary-color']" />
				</view>
			</scroll-view>
		</uv-drop-down-popup>

		<!-- 内容区域 -->
		<scroll-view v-if="token && companyInfo" scroll-y="true"
			:style="{ height: `calc(100vh - ${topNavHeight}px - 68px)` }">
			<view class="content">
				<view class="search-box" @click="goSearch">
					<uv-input placeholder="输入名称进行搜索" prefixIcon="search" border="none" readonly="true"
						placeholderStyle="color: #ACACAC;font-size: 28rpx"
						prefixIconStyle="font-size: 20px;color: #7B7B7B">
					</uv-input>
				</view>
				<!-- 最近使用 -->
				<RecentlyUsed :isRequest="pageState === 'show'" />
				<!-- 流程中心 -->
				<ProcessCenter :isRequest="pageState === 'show'" />
				<!-- 我的应用 -->
				<MyApp :isRequest="pageState === 'show'" />
				<!-- 外部应用 -->
				<ExternalApp v-if="!!externalCompany" />
			</view>
		</scroll-view>
		<template v-if="!token || !companyInfo">
			<MarketCom :isRequest="pageState === 'show'" />
		</template>
		<!-- 添加小程序弹窗 -->
		<view v-if="showAddToMyMiniProgramPopup" class="add-mask" @click="closeAddToMyMiniProgramPopup">
			<view class="box" @click.stop :style="{ top: `${statusBarHeight + navBarHeight + 10}px` }">
				<view class="header">
					<text>将五极云添加到我的小程序</text>
				</view>
				<view class="add-content">
					<view class="li">
						<view class="li-order">1</view>
						<text>点击按钮“</text>
						<image class="img" src="/static/img/bound.png" />
						<text>”选择添加</text>
					</view>
					<view class="img-box">
						<image class="img-add" src="/static/img/add-app.png" />
						<image class="img-handle" src="/static/img/handle.png" />
					</view>
					<view class="li" style="line-height: 36rpx;">
						<view class="li-order" style="top:4rpx">2</view>
						<text style="flex:1;">微信首页下拉，从“我的小程序”进入</text>
					</view>
					<view class="tip-box">
						<view class="title">我的小程序</view>
						<view class="logo-box">
							<image class="logo-img" src="/static/img/logo.png" />
							<image class="img-handle" src="/static/img/handle.png" />
							<text>五极云</text>
						</view>
					</view>
				</view>
			</view>
		</view>
		<!-- 底部导航栏 -->
		<CustomTabBar />
	</view>
</template>

<script setup>
import { computed, ref, watch } from 'vue';
import { useUserStore } from '@/store/user';
import { onLoad, onShow, onHide, onPageScroll, onPullDownRefresh, onShareAppMessage, onShareTimeline } from '@dcloudio/uni-app';
import { useGlobalTheme } from '@/utils/theme';
import { getToken, getAddToMyMiniProgramPopup, setAddToMyMiniProgramPopup } from '@/utils/auth'
import { shareApp } from '@/utils/share';
import { logout } from '@/utils/auth';
import CustomTabBar from '@/components/custom-tab-bar/index.vue';
import RecentlyUsed from '@/components/platform/recently-used/index.vue';
import ProcessCenter from '@/components/platform/process-center/index.vue';
import MyApp from '@/components/platform/my-app/index.vue';
import ExternalApp from '@/components/platform/external-app/index.vue';
import MarketCom from '@/components/market-com/market-com.vue';
const userStore = useUserStore();
const { themeColor } = useGlobalTheme();
// 页面状态
const pageState = ref('show');
// 导航栏高度
const navBarHeight = ref(0);
// 胶囊高度
const capsuleHeight = ref(0);
// 下拉菜单
const dropDown = ref(null);
// 下拉菜单弹出层
const dropDownPopup = ref(null);
// 登录token
const token = ref(getToken());
const showAddToMyMiniProgramPopup = ref(false);
// 公司信息
const companyInfo = computed(() => userStore.userCompany);
// 公司列表
const companyList = computed(() => userStore.companyList || []);
// 外部公司
const externalCompany = computed(() => userStore?.externalCompany || null);
// 获取状态栏高度
const statusBarHeight = computed(() => {
	return uni.$uv.sys().statusBarHeight;
});
// 顶部导航高度
const topNavHeight = computed(() => {
	// 判断是否为微信小程序
	if (uni.getSystemInfoSync().uniPlatform !== 'web') {
		return navBarHeight.value + statusBarHeight.value;
	} else {
		return 0;
	}
})

// 当前选中的公司索引
const activeIndex = computed(() => {
	const findIndex = companyList.value.findIndex((item) => item.companyId === companyInfo.value?.companyId);
	return findIndex;
})
const scrollTop = ref(0);

// 关闭添加到我的小程序弹窗
const closeAddToMyMiniProgramPopup = () => {
	showAddToMyMiniProgramPopup.value = false;
}

// 分享配置
shareApp({
	onShareAppMessage,
	onShareTimeline,
	isTimeline: true,
	path: '/pages/index/index',
	title: 'AI+aPaaS企业级零代码应用搭建平台',
	fail: (err) => {
		console.log(err)
	}
})

// 页面加载完成时
onLoad(() => {
	// 获取导航栏高度
	// #ifdef MP-WEIXIN	
	capsuleHeight.value = uni.getMenuButtonBoundingClientRect().height || 32;
	navBarHeight.value = 32;
	// #endif
	// #ifdef H5
	capsuleHeight.value = 26;
	navBarHeight.value = 40;
	// #endif
	// 监听登录成功事件,更新主题色
	uni.$on('loginSuccess', function () {
		token.value = getToken();
	})
	// #ifdef MP-WEIXIN	
	uni.checkIsAddedToMyMiniProgram({
		success(res) {
			if (!res.added) {
				const isPopup = getAddToMyMiniProgramPopup();
				if (!isPopup) {
					setAddToMyMiniProgramPopup();
					showAddToMyMiniProgramPopup.value = true;
				}
			}
		}
	});
	// #endif
});

const popupChange = (e) => {
	if (e.show) {
		scrollTop.value = activeIndex.value * 68;
	}
}

// 监听页面滚动事件，调用下拉菜单的init方法
onPageScroll(() => {
	dropDown.value.init();
})

// 下拉刷新
onPullDownRefresh(() => {
	pageState.value = 'hide';
	uni.showLoading({
		title: '刷新中...',
	})
	setTimeout(() => {
		pageState.value = 'show';
	}, 500);
	setTimeout(() => {
		uni.stopPullDownRefresh();
		uni.hideLoading();
	}, 1000);
})

// 页面显示时
onShow(() => {
	pageState.value = 'show';
})

// 页面隐藏时
onHide(() => {
	pageState.value = 'hide';
})

// 搜索
const goSearch = () => {
	uni.navigateTo({
		url: '/pages/search/search'
	})
}

// 公司选择
const companySelect = (e) => {
	if (e.companyId !== companyInfo.value?.companyId) {
		userStore.switchCompany(e.companyId);
		dropDownPopup.value.close();
	}
}

// 登录
const goLogin = () => {
	logout('/pages/index/index', 'navigateTo');
}

</script>

<style lang="scss" scoped>
.index-container {
	position: relative;
	height: 100%;
	box-sizing: border-box;
	padding-bottom: calc(68px + env(safe-area-inset-bottom));

	.custom-navbar {
		width: 100%;
		background-color: #fff;
		display: flex;
		align-items: center;

		.company-name {
			font-size: 30rpx;
			color: #181818;
			text-align: center;
			width: 100%;
			font-weight: 600;
			padding: 0 30rpx;
			overflow: hidden;
			text-overflow: ellipsis;
			white-space: nowrap;
		}

		.appName {
			color: #181818;
			height: 100%;
			display: flex;
			width: 100%;
			align-items: center;
			justify-content: space-between;
			// #ifdef MP-WEIXIN
			height: 44px;
			// #endif
			padding: 0 20rpx;

			.title {
				font-size: 14px;
				color: #4c4c4c;
			}

			.btn {
				background-color: var(--primary-color);
				color: #ffffff;
				font-size: 28rpx;
				display: flex;
				align-items: center;
				padding: 0 32rpx;
				border-radius: 32rpx;
			}

			text {
				color: var(--primary-color);
			}
		}
	}

	.content {
		padding: 20rpx 32rpx 16rpx;
		background-color: #f6f6f8;
		display: flex;
		flex-direction: column;
		gap: 16rpx;

		.search-box {
			border-radius: 16rpx;
			background-color: #fff;
			height: 76rpx;
			padding: 0 32rpx;
			display: flex;
			align-items: center;
		}
	}

	.company-list {
		padding: 0 32rpx;
		box-sizing: border-box;
		max-height: 50vh;

		.company-list-item {
			padding: 16rpx 32rpx;
			display: flex;
			align-items: center;
			justify-content: space-between;
			border-radius: 16rpx;
			margin-top: 24rpx;

			.company-logo {
				width: 80rpx;
				height: 80rpx;
				border-radius: 8rpx;
				background-color: #fff;
				padding: 10rpx;
				box-sizing: border-box;
			}

			.company-name {
				flex: 1;
				font-size: 32rpx;
				line-height: 48rpx;
				margin: 0 24rpx;
				overflow: hidden;
				text-overflow: ellipsis;
				white-space: nowrap;
			}

			&:last-child {
				margin-bottom: 24rpx;
			}
		}

		.active-item {
			background-color: #f5f6f8;
		}
	}

	.no-login {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		padding-top: 150rpx;
		width: 100%;

		.logo {
			width: 184rpx;
			margin-bottom: 32rpx;
		}

		.login-tip {
			font-size: 32rpx;
			color: #181818;
			margin-bottom: 50rpx;
		}

	}

	.add-mask {
		position: fixed;
		z-index: 999;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		background-color: rgba(0, 0, 0, 0.5);

		.box {
			position: absolute;
			background-color: #fff;
			padding: 40rpx;
			border-radius: 32rpx;
			right: 32px;
			box-sizing: border-box;

			.header {
				margin-bottom: 36rpx;
				text-align: center;

				text {
					font-size: 32rpx;
					line-height: 44rpx;
					font-weight: 500;
				}
			}

			.add-content {
				width: 412rpx;

				.li {
					display: flex;
					font-size: 26rpx;
					color: #151515;
					margin-bottom: 8rpx;
					line-height: 48rpx;

					.li-order {
						position: relative;
						width: 32rpx;
						height: 32rpx;
						border-radius: 50%;
						background: #000000;
						line-height: 32rpx;
						text-align: center;
						margin-right: 16rpx;
						color: #fff;
						font-size: 24rpx;
						top: 8rpx
					}

					.img {
						width: 48rpx;
						height: 48rpx;
						margin: 0 16rpx;
					}
				}

				.img-box {
					position: relative;
					width: 412rpx;
					height: 204rpx;
					margin-bottom: 28rpx;

					.img-add {
						width: 100%;
						height: 100%;
					}

					.img-handle {
						position: absolute;
						right: -6rpx;
						top: 76rpx;
						width: 56rpx;
						height: 64rpx;
					}
				}

				.tip-box {
					width: 100%;
					background: #4C4C4C;
					border-radius: 16rpx;
					margin-top: 16rpx;
					padding: 22rpx 24rpx;
					box-sizing: border-box;
					display: flex;
					flex-direction: column;
					align-items: center;

					.title {
						color: #959596;
						font-size: 20rpx;
						width: 100%;
						margin-bottom: 10rpx;
					}

					.logo-box {
						position: relative;
						width: 80rpx;
						text-align: center;

						.logo-img {
							width: 80rpx;
							height: 80rpx;
						}

						.img-handle {
							position: absolute;
							right: -44rpx;
							top: 38rpx;
							width: 56rpx;
							height: 64rpx;
						}

						text {
							color: #959596;
							font-size: 20rpx;
							line-height: 24rpx;
							margin-top: 12rpx;
						}
					}
				}
			}


			&::before {
				// 设置向上的三角
				content: '';
				position: absolute;
				top: -20rpx;
				right: 60rpx;
				width: 0;
				height: 0;
				border-left: 20rpx solid transparent;
				border-right: 20rpx solid transparent;
				border-bottom: 20rpx solid #fff;
			}
		}
	}

	:global(.uv-dp__container) {
		border-bottom-left-radius: 16rpx;
		border-bottom-right-radius: 16rpx;
	}

	:global(.uv-drop-down) {
		border-bottom: 0 !important;
	}
}
</style>