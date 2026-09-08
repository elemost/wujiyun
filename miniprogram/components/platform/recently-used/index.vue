<template>
    <view v-if="showView" class="recently-used" :style="themeColor">
        <view class="title">最近使用</view>
        <uv-skeletons :loading="loading" :skeleton="skeleton">
            <view class="recently-used-list">
                <view class="recently-used-item" v-for="(item, index) in list" :key="index" @click="navigatorTo(item)">
                    <uv-icon :name="getFormIcon(item.categoryType, 'icon')"
                        :color="getFormIcon(item.categoryType, 'color')" custom-prefix="custom-icon" size="20" />
                    <text class="recently-used-text">{{ item.categoryName }}</text>
                </view>
            </view>
        </uv-skeletons>
    </view>
</template>
<script setup>
import { computed, watch, ref } from 'vue';
import { useGlobalTheme } from '@/utils/theme';
import { getFormIcon } from '@/utils/common';
import { useUserStore } from '@/store/user';
import { getRecentlyUsedApi } from '@/service/platform';
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

const list = ref([]);
const loading = ref(true);
const showView = ref(true);
const skeleton = ref([{
    type: 'flex',
    children: [{
        type: 'custom',
        style: 'width:303rpx; height:80rpx; margin-right:16rpx;',
    }, {
        type: 'custom',
        style: 'width:303rpx; height:80rpx;',
    }],
    gap: '16rpx',
    num: 3,
}])
// 跳转
const navigatorTo = (item) => {
    uni.navigateTo({
        url: `/pages/application/application?applicationId=${item.applicationId}&formId=${item.id}`
    })
}
// 获取最近使用列表
const getRecentlyUsedList = async () => {
    const res = await getRecentlyUsedApi({ limitCount: 6 })
    list.value = res || [];
    loading.value = false;
    showView.value = list.value.length > 0;
}
// 监听公司切换
watch([companyId, isRequest], ([newCompanyVal, newIsRequest]) => { 
    if (newCompanyVal&&newIsRequest) {
        getRecentlyUsedList()
    }
},{ immediate: true})

</script>
<style lang="scss">
.recently-used {
    padding: 16rpx 32rpx;
    background-color: #fff;
    border-radius: 16rpx;

    .title {
        font-size: 32rpx;
        font-weight: 500;
        line-height: 44rpx;
        margin-bottom: 32rpx;
    }

    .recently-used-list {
        display: flex;
        justify-content: space-between;
        flex-wrap: wrap;
        gap: 16rpx;

        .recently-used-item {
            display: flex;
            align-items: center;
            width: calc(50% - 10rpx);
            height: 80rpx;
            border-radius: 8rpx;
            background-color: #f5f6f8;
            padding: 16rpx 24rpx;
            box-sizing: border-box;

            .recently-used-text {
                font-size: 24rpx;
                line-height: 36rpx;
                margin-left: 8rpx;
                flex: 1;
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
            }
        }
    }
}
</style>
