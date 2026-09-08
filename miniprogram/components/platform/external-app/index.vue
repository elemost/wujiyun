<template>
    <view class="external-app" :style="themeColor">
        <view class="title">
            <view class="left">
                <text>外部应用</text>
                <view class="drop-down" @click="showPicker">
                    <text>{{ externalCompany.companyName }}</text>
                    <uv-icon name="arrow-down-fill" color="#7b7b7b" size="24rpx" />
                </view>
            </view>
            <view class="right" @click="goMore">
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
            <uv-empty iconSize="60" text="暂无应用" mode="data" v-if="!appList.length && !loading" />
        </uv-skeletons>
        <uv-picker ref="picker" :confirmColor="themeColor['--primary-color']"
            :activeColor="themeColor['--primary-color']" :columns="[externalCompanyList]" keyName="companyName"
            @confirm="confirm">
        </uv-picker>
    </view>
</template>
<script setup>
import { computed, watch, ref } from 'vue';
import { useGlobalTheme } from '@/utils/theme';
import { useUserStore } from '@/store/user';
import { getMyApplicationApi } from '@/service/application';
import AppIcon from '@/components/app-icon/index.vue';
const { themeColor } = useGlobalTheme();
const userStore = useUserStore();
// 外部公司列表
const externalCompanyList = computed(() => userStore?.userInfo?.corpCompanyList || []);
// 当前外部公司
const externalCompany = computed(() => userStore?.externalCompany || null);
// 应用列表
const appList = ref([]);
// 总数
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
// 选择器
const picker = ref(null);
// 获取应用列表
const getAppList = async (companyUuid) => {
    const res = await getMyApplicationApi({
        pageNum: 1,
        pageSize: 8,
        companyUuid
    });
    appList.value = res?.list || [];
    total.value = res?.total || 0;
    loading.value = false;
}
// 跳转
const goMore = () => {
    uni.navigateTo({
        url: '/pages/app-list/index?type=external'
    })
}
const goApp = (item) => {
    uni.navigateTo({
        url: `/pages/application/application?applicationId=${item.id}`
    })
}

// 显示选择器
const showPicker = () => {
    picker.value.open();
    const defaultIndex = externalCompanyList.value.findIndex(item => item.companyUuid === externalCompany.value.companyUuid);
    if (defaultIndex !== -1) {
        picker.value.setIndexs([defaultIndex], true)
    }
}
// 选择器确认
const confirm = (e) => {
    const { value } = e;
    userStore.switchExternalCompany(value[0]);
    getAppList(value[0]?.companyUuid)
}

// 监听外部公司变化
watch(externalCompany, (newExternalCompany) => {
    if (newExternalCompany) {
        getAppList(newExternalCompany?.companyUuid)
    }
}, { immediate: true })

</script>

<style lang="scss" scoped>
.external-app {
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

        .left {
            display: flex;
            align-items: center;
            gap: 16rpx;

            .drop-down {
                display: flex;
                align-items: center;
                font-weight: 400;
                gap: 8rpx;

                text:first-child {
                    font-size: 24rpx;
                    line-height: 36rpx;
                    color: #7b7b7b;
                    max-width: 300rpx;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                }
            }
        }

        .right {
            display: flex;
            align-items: center;
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