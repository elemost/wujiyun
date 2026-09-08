<template>
    <view class="market-container">
        <MarketCom :isRequest="isRequest" />
        <!-- 底部导航栏 -->
        <CustomTabBar />
    </view>
</template>

<script setup>
import CustomTabBar from '@/components/custom-tab-bar/index.vue';
import MarketCom from '@/components/market-com/market-com.vue';
import { ref } from 'vue';
import { onPullDownRefresh } from '@dcloudio/uni-app';
const isRequest = ref(true);

// 下拉刷新
onPullDownRefresh(() => {
	isRequest.value = false
	uni.showLoading({
		title: '刷新中...',
	})
	setTimeout(() => {
		isRequest.value = true
	}, 500);
	setTimeout(() => {
		uni.stopPullDownRefresh();
		uni.hideLoading();
	}, 1000);
})
</script>

<style scoped lang="scss">
.market-container {
    position: relative;
    padding-bottom: calc(68px + env(safe-area-inset-bottom));
}
</style>