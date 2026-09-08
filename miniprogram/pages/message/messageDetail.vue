<template>
    <view class="message-detail">
        <uv-loading-page :loading="loading" loading-text="加载中..." font-size="24rpx"></uv-loading-page>
        <view class="message-head" v-if="infoData.messageType">
            <text>{{ messageTypeMap[infoData.messageType] }}</text>
            <text>{{ formatTime(infoData.createTime) }}</text>
        </view>
        <view class="content-wrap">
            <rich-text :nodes="infoData.content"></rich-text>
        </view>
    </view>
</template>
<script setup>
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app';
import { getMessageDetail, markMessageAsViewed } from '@/service/message.js'
const loading = ref(false);
const infoData = ref({});

// 定义 messageType 到提示文字的映射
const messageTypeMap = {
    APPLICATION_EXPIRE: '提醒消息',
    VIP_EXPIRE: '提醒消息',
    DOSAGE_REMINDER: '用量提醒',
    NOTICE: '公司通知',
};

// 获取消息详情
const getMessageInfo = (id) => {
    loading.value = true;
    getMessageDetail(id).then(res => {
        loading.value = false;
        infoData.value = res || {};
    }).finally(() => {
        loading.value = false;
    })
}

// 时间戳转换
const formatTime = (timestamp) => {
    return uni.$uv.timeFormat(timestamp, 'yyyy-mm-dd hh:MM:ss')
}

onLoad((options) => {
    if (options.id) {
        getMessageInfo(options.id)
        markMessageAsViewed(options.id)
    }
})
</script>
<style lang="scss">
.message-detail {
    padding: 30rpx;
    background-color: #fff;
    height: 100%;

    .message-head {
        display: flex;
        align-items: center;
        justify-content: space-between;
        height: 100rpx;
        border-bottom: 1px solid #eee;

        text:first-child {
            font-weight: 500;
            font-size: 32rpx;
        }

        text:last-child {
            color: #838892;
        }
    }

    .content-wrap {
        padding: 30rpx 0;
    }
}
</style>
