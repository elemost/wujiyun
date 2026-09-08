<template>
    <uv-skeletons :loading="!companyId" :skeleton="skeleton">
        <view class="process-center" :style="themeColor">
            <view class="process-item" @click="goPage('waitting')">
                <view v-if="pendingCount > 0" class="badge-box">
                    <uv-badge type="error" max="99" :value="pendingCount"
                        :customStyle="{ fontSize: '20rpx' }"></uv-badge>
                </view>
                <uv-icon name="daiban" :color="themeColor['--primary-color']" custom-prefix="custom-icon" size="28" />
                <text class="process-item-text">我的待办</text>
            </view>
            <view class="process-item" @click="goPage('initiate')">
                <uv-icon name="faqi" :color="themeColor['--primary-color']" custom-prefix="custom-icon" size="28" />
                <text class="process-item-text">我发起的</text>
            </view>
            <view class="process-item" @click="goPage('handle')">
                <uv-icon name="chuli" :color="themeColor['--primary-color']" custom-prefix="custom-icon" size="28" />
                <text class="process-item-text">我处理的</text>
            </view>
            <view class="process-item" @click="goPage('send')">
                <uv-icon name="chaosong" :color="themeColor['--primary-color']" custom-prefix="custom-icon" size="28" />
                <text class="process-item-text">抄送我的</text>
            </view>
        </view>
    </uv-skeletons>
</template>
<script setup>
import { computed, watchEffect, ref } from 'vue';
import { getflowableStatisticApi } from '@/service/platform';
import { useGlobalTheme } from '@/utils/theme';
import { useUserStore } from '@/store/user';
import { appConfig } from '@/config/app';
import { getToken } from '@/utils/auth';
const userStore = useUserStore();
// 当前外部公司
const externalCompany = computed(() => userStore?.externalCompany || null);
// 骨架屏
const skeleton = ref([{
    type: 'flex',
    children: [{
        type: 'custom',
        style: 'width:96rpx; height: 100rpx; margin-right:80rpx;',
    }, {
        type: 'custom',
        style: 'width:96rpx; height: 100rpx; margin-right:80rpx;',
    }, {
        type: 'custom',
        style: 'width:96rpx; height: 100rpx; margin-right:80rpx;',
    }, {
        type: 'custom',
        style: 'width:96rpx; height: 100rpx',
    }],
    style: 'padding: 16rpx 32rpx; background-color: #fff; border-radius: 16rpx;',
}])

// 定义props
const props = defineProps({
    isRequest: {
        type: Boolean,
        default: false
    }
})

// 公司信息
const companyId = computed(() => userStore.getCompanyId());

// 是否请求
const isRequest = computed(() => props.isRequest);

const { themeColor } = useGlobalTheme();
const pendingCount = ref(0);
// 定时器
let timer = null;

// 获取待办数量
const getProcessCount = async (companyInfo) => {
    let res1 = null;
    let res2 = null;
    if (companyInfo === false) {
        res1 = await getflowableStatisticApi();
    }
    if (companyInfo) {
        res1 = await getflowableStatisticApi();
        res2 = await getflowableStatisticApi({
            companyUuid: companyInfo.companyUuid,
        });
    }
    pendingCount.value = (res1?.pendingCount || 0) + (res2?.pendingCount || 0);
}

// 跳转页面
const goPage = (type) => {
    const token = getToken();
    let url = `${appConfig.webViewUrl}/mobile/processCenter/${type}?token=${token}`;
    if (externalCompany.value) {
        url += '&companyType=inner&companyUuid=' + externalCompany.value.companyUuid;
    }
    uni.navigateTo({
        url: `/pages/webView/webView?url=${encodeURIComponent(url)}`
    })
}

// 替换原有的两个watch监听
watchEffect(() => {
    if (isRequest.value && companyId.value) {
        getProcessCount(externalCompany.value);
        if (!timer) {
            // 每60秒获取一次待办数量
            timer = setInterval(() => {
                getProcessCount(externalCompany.value);
            }, 30000);
        }
    } else {
        if (timer) {
            // 清空定时器
            clearInterval(timer);
            timer = null;
        }
    }
});

</script>
<style lang="scss">
.process-center {
    padding: 16rpx 32rpx;
    background-color: #fff;
    border-radius: 16rpx;
    display: flex;
    justify-content: space-between;

    .process-item {
        position: relative;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 8rpx;

        .badge-box {
            position: absolute;
            top: -10rpx;
            right: 0;
            z-index: 10;
        }

        .process-item-text {
            font-size: 24rpx;
            line-height: 36rpx;
            color: #181818;
        }
    }
}
</style>