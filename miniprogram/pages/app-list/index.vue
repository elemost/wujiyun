<template>
    <view class="app-list-wrap" :style="themeColor">
        <mescroll-body :up="upOption" :down="downOption" @init="mescrollInit" @down="downCallback" @up="upCallback">

            <view class="header">
                <view class="search-box">
                    <uv-input 
                        placeholder="输入名称进行搜索" 
                        v-model.trim="keyword"
                        @input="search"  
                        prefixIcon="search"
                         border="none"
                        placeholderStyle="color: #ACACAC;font-size: 28rpx"
                        prefixIconStyle="font-size: 20px;color: #7B7B7B">
                        <template v-slot:suffix>
                        <uv-icon v-if="keyword" @click="search('')" name="close-circle-fill" color="#ACACAC" size="18" />
                    </template>
                    </uv-input>
                </view>
                <view class="blank"></view>
            </view>
            <view class="wrap">
                <view class="app-list">
                    <view class="app-item" v-for="item in appList" :key="item.id" @click="goApp(item)">
                        <view class="icon-box">
                            <AppIcon :icon="item.icon" />
                        </view>
                        <view class="app-item-text">{{ item.applicationName }}</view>
                    </view>
                </view>
            </view>
        </mescroll-body>
    </view>
</template>
<script setup>
import { computed, ref } from 'vue';
import {onLoad, onPageScroll, onReachBottom } from '@dcloudio/uni-app';
import useMescroll from "@/uni_modules/mescroll-uni/hooks/useMescroll.js";
import { useGlobalTheme } from '@/utils/theme';
import { getMyApplicationApi } from '@/service/application';
import AppIcon from '@/components/app-icon/index.vue';
const { themeColor } = useGlobalTheme();
import { useUserStore } from '@/store/user';
// 调用mescroll的hook (注: mescroll-uni不用传onPageScroll,onReachBottom, 而mescroll-body必传)
const { mescrollInit, getMescroll } = useMescroll(onPageScroll, onReachBottom)
const userStore = useUserStore();
// 类型
const type = ref('');
// 当前外部公司
const externalCompany = computed(() => userStore?.externalCompany || null);
// 上拉加载配置
const upOption = ref({
    noMoreSize: 40,
    textNoMore: '-- 没有更多了 --',
    page: {
        num: 0,
        size: 40
    }
})
// 下拉刷新配置
const downOption = ref({
    use: false
})
const keyword = ref('');
// 应用列表
const appList = ref([]);
// 获取应用列表
const getAppList = async (mescroll) => {
    const res = await getMyApplicationApi({
        pageNum: mescroll.num || 1,
        pageSize: mescroll.size || 40,
        applicationName: keyword.value,
        companyUuid: type.value === 'external'?externalCompany.value?.companyUuid:undefined
    });
    return res;
}
// 搜索
const search = (value) => {
    keyword.value = value;
    uni.$uv.debounce(() => {
        getMescroll().resetUpScroll()
    }, 500)
}
// 上拉加载
const upCallback = async (mescroll) => {
    const res = await getAppList(mescroll);
    const curPageData = res.list || [] // 当前页数据
    if (mescroll.num == 1) appList.value = []; // 第一页需手动制空列表
    appList.value = appList.value.concat(curPageData);
    mescroll.endBySize(curPageData.length, res.total);
}
// 跳转应用
const goApp = (item) => {
    uni.navigateTo({
        url: `/pages/application/application?applicationId=${item.id}`
    })
}

onLoad((option) => {
    if (option.type === 'external') {
        uni.setNavigationBarTitle({
            title: '外部应用',
        })
    }else{
        uni.setNavigationBarTitle({
            title: '我的应用',
        })
    }
    type.value = option.type;
})

</script>

<style lang="scss" scoped>
.app-list-wrap {
    height: 100%;
    background-color: #ffffff;

    .header {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        z-index: 999;
        height: 92rpx;
        padding-top: 20rpx;
        width: 100%;
        background-color: #ffffff;

        .search-box {
            padding: 0 24rpx;
            background-color: #F6F6F8;
            margin: 0 32rpx;
            height: 72rpx;
            border-radius: 36rpx;
            display: flex;
            align-items: center;
            margin-bottom: 20rpx;
        }

        .blank {
            height: 20rpx;
            background-color: #F6F6F8;
            width: 100%;
        }
    }

    .wrap {
        padding-top: 132rpx;
        background-color: #ffffff;

        .app-list {
            display: flex;
            flex-wrap: wrap;
            gap: 24rpx 40rpx;
            padding: 24rpx 32rpx;

            .app-item {
                width: calc(25% - 32rpx);
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                gap: 8rpx;

                .icon-box {
                    width: 72rpx;
                    height: 72rpx;
                    border-radius: 12rpx;
                    overflow: hidden;
                }

                .app-item-text {
                    margin-top: 16rpx;
                    font-size: 24rpx;
                    max-height: 64rpx;
                    line-height: 32rpx;
                    color: #181818;
                    // 超出两行省略
                    overflow: hidden;
                    text-overflow: ellipsis;
                    display: -webkit-box;
                    // 定义 -webkit 前缀属性以兼容旧版浏览器
                    -webkit-line-clamp: 2;
                    // 定义标准属性以提高兼容性
                    line-clamp: 2;
                    -webkit-box-orient: vertical;
                }
            }
        }
    }
}
</style>
