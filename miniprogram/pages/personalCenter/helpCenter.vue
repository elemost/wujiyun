<template>
    <view :style="themeColor">
        <!-- Tab 导航栏 -->
        <view class="tab-bar">
            <uv-tabs :list="tabs" @click="switchTab"></uv-tabs>
        </view>

        <!-- Tab 内容 -->
        <view class="tab-content">
            <view v-if="activeTab === 0">
                <!-- 常见问题 -->
                <view v-for="(question, index) in commonQuestions" :key="index" class="question-item">
                    <text class="question">{{ question.title }}</text>
                    <uni-icons type="right" class="card-icon-right"></uni-icons>
                </view>
            </view>
            <view v-if="activeTab === 1">
                <!-- 账户问题 -->
                <text>账户问题内容待补充</text>
            </view>
            <view v-if="activeTab === 2">
                <!-- 信息问题 -->
                <text>信息问题内容待补充</text>
            </view>
        </view>
    </view>
</template>

<script setup>
import { ref } from 'vue';

// 定义 Tab 列表
const tabs = ref([
    { name: '常见问题' },
    { name: '账户问题' },
    { name: '信息问题' }
]);
// 当前激活的 Tab
const activeTab = ref(0);

// 定义常见问题列表
const commonQuestions = [
    {
        title: '五极云低代码平台是什么？',
    },
    {
        title: '相较于传统开发方式，它有哪些优势？',
    },
    {
        title: '哪些行业适用于五极云低代码平台',
    },
    {
        title: '使用该平台需要具备怎么的技术背景？',
    }
];

// 切换 Tab 的方法
const switchTab = (e) => {
    activeTab.value = e.index;
};
</script>

<style scoped>
.tab-bar {
    display: flex;
    justify-content: center; /* 水平居中 */
    align-items: center; /* 垂直居中 */
    border-bottom: 1rpx solid #eee;
}

.tab-item {
    flex: 1;
    text-align: center;
    padding: 20rpx 0;
    font-size: 32rpx;
    color: #666;
}

.tab-item.active {
    color: #14B59C;
    border-bottom: 4rpx solid #14B59C;
}

.tab-content {
    padding: 20rpx;
}

.question-item {
    display: flex;
    align-items: center;
    padding: 8px 0;
    height: 40px;
    justify-content: space-between;
}

.question {
    font-size: 32rpx;
    font-weight: bold;
    color: #333;
    display: block;
    margin-bottom: 10rpx;
}
</style>