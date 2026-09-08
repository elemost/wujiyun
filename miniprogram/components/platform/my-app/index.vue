<template>
    <view class="my-app" :style="themeColor">
        <view class="title">
            <text>我的应用</text>
            <view @click="goMore">
                <text>更多</text>
                <uv-icon name="arrow-right" size="12" color="#7b7b7b" />
            </view>
        </view>
        <uv-skeletons :loading="loading" :skeleton="skeleton">
            <view class="app-list">
                <view class="app-item" v-for="item in appList" :key="item.id" @click="goApp(item)">
                    <view class="icon-box">
                        <AppIcon :icon="item.icon" />
                    </view>
                    <text class="app-item-text">{{ item.applicationName }}</text>
                </view>
            </view>
            <uv-empty iconSize="60" text="还没有创建应用" mode="data" v-if="!appList.length && !loading" />
        </uv-skeletons>
    </view>
</template>
<script setup>
import { onMounted, computed, watch, ref } from 'vue';
import { useGlobalTheme } from '@/utils/theme';
import { useUserStore } from '@/store/user';
import { getMyApplicationApi } from '@/service/application';
import AppIcon from '@/components/app-icon/index.vue';
const { themeColor } = useGlobalTheme();
const userStore = useUserStore();
// 定义props
const props = defineProps({
    isRequest: {
        type: Boolean,
        default: false
    }
})
// 是否请求
const isRequest = computed(() => props.isRequest);
// 公司信息
const companyId = computed(() => userStore.getCompanyId());
// 应用列表
const appList = ref([]);
const total = ref(0);
// 加载中
const loading = ref(true);
// 骨架屏
const skeleton = ref([{
    type: 'flex',
    children: [{
        type: 'custom',
        style: 'width:125.5rpx; height:116rpx; margin-right:40rpx;',
    }, {
        type: 'custom',
        style: 'width:125.5rpx; height:116rpx; margin-right:40rpx;',
    }, {
        type: 'custom',
        style: 'width:125.5rpx; height:116rpx; margin-right:40rpx;',
    }, {
        type: 'custom',
        style: 'width:125.5rpx; height:116rpx;',
    }],
    gap: '16rpx',
    num: 2
}])
// 获取应用列表
const getAppList = async () => {
    const res = await getMyApplicationApi({
        pageNum: 1,
        pageSize: 8
    });
    total.value = res?.total || 0;
    appList.value = res?.list || [];
    loading.value = false;
}
// 跳转
const goMore = () => {
    uni.navigateTo({
        url: '/pages/app-list/index'
    })
}
const goApp = (item) => {
    uni.navigateTo({
        url: `/pages/application/application?applicationId=${item.id}`
    })
}

onMounted(() => {
    uni.$on('templateUpdate', function () {
        getAppList()
    })
})

// 监听公司切换
watch([companyId, isRequest], ([newCompanyVal, newIsRequest]) => { 
    if (newCompanyVal&&newIsRequest) {
        getAppList()
    }
},{ immediate: true})

</script>

<style lang="scss" scoped>
.my-app {
    padding: 16rpx 32rpx;
    background-color: #fff;
    border-radius: 16rpx;

    .title {
        display: flex;
        align-items: center;
        justify-content: space-between;
        font-size: 32rpx;
        font-weight: 500;
        line-height: 44rpx;
        margin-bottom: 32rpx;

        view {
            display: flex;
            align-items: center;
            font-weight: 400;
            gap: 8rpx;

            text:first-child {
                font-size: 24rpx;
                line-height: 36rpx;
                color: #7b7b7b;
            }
        }
    }

    .app-list {
        overflow: hidden;
        display: flex;
        flex-wrap: wrap;
        gap: 16rpx 40rpx;

        .app-item {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            gap: 8rpx;
            // 一行四个
            width: calc(25% - 32rpx);

            .icon-box {
                width: 72rpx;
                height: 72rpx;
                border-radius: 12rpx;
                overflow: hidden;
            }

            .app-item-text {
                font-size: 24rpx;
                line-height: 36rpx;
                color: #181818;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                width: 100%;
                text-align: center;
            }
        }
    }
}
</style>