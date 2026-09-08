<template>
    <div class="message-container" :style="{ ...themeColor, paddingTop: `calc(44px + ${statusBarHeight}px)` }">
        <uv-toast ref="toast"></uv-toast>
        <!-- 一级 tab：通知和待办任务 -->
        <uv-navbar ref="navbar" fixed>
            <template v-slot:left>
                <view class="first-level-tabs">
                    <view v-for="(item, index) in firstLevelTabs" :key="index"
                        :class="{ active: activeFirstLevelTab === index }" @click="changeFirstLevelTab(index)"
                        style="display: flex; gap:4px">
                        <text> {{ item.label }} </text>
                        <uv-badge
                            v-if="(index === 0 && unreadMessageCount > 0) || (index === 1 && (todoQuantity.inner || todoQuantity.out))"
                            :isDot="true" type="error"></uv-badge>
                    </view>
                </view>
            </template>
            <template v-if="externalCompany && activeFirstLevelTab === 1" v-slot:right>
                <view @click="showPicker" class="external-company" :style="{ paddingRight: menuButtonWidth + 'px' }">
                    <text class="type-name"> {{typeOptions.find(item => item.value === type).label}} </text>
                    <uv-icon name="arrow-down-fill" color="#181818" size="24rpx" />
                </view>
            </template>
        </uv-navbar>
        <view class="tabs-wrap"
            :style="{ top: `calc(44px + ${statusBarHeight}px)`, height: `${activeFirstLevelTab === 0 ? '84rpx' : '168rpx'}` }">
            <!-- 二级 tab，根据 activeFirstLevelTab 的值渲染不同内容 -->
            <view v-if="activeFirstLevelTab === 0" class="second-level-one-tabs">
                <view v-for="(item, index) in secondLevelTabsMap[0]" :key="index" :class="{
                    active: activeSecondLevelTab === item.value,
                }" @click="changeSecondLevelTab(item.value)" style="display: flex; align-items: center;gap:4px">
                    <text> {{ item.label }} </text>
                    <!-- 在未读 tab 上添加 uv-badge -->
                    <uv-badge v-if="item.label === '未读' && unreadMessageCount > 0"
                        :value="unreadMessageCount"></uv-badge>
                </view>
            </view>
            <view v-else class="second-level-tabs">
                <view v-for="(item, index) in secondLevelTabsMap[1]" :key="index" :class="{
                    active: activeSecondLevelTab === item.value
                }" @click="changeSecondLevelTab(item.value)">
                    {{ item.label }}
                </view>
            </view>

            <!-- 三级 tab -->
            <view class="second-level-one-tabs" v-if="activeFirstLevelTab === 1">
                <!-- 我的待办 -->
                <template v-if="activeSecondLevelTab === 'waitting'">
                    <view v-for="(item, index) in [
                        { label: '全部', value: 'all' },
                        { label: '驳回', value: 'rejected' },
                    ]" :key="value" :class="{ active: activeThirdLevelTab === index }"
                        @click="changeThirdLevelTab(index, item.value)"
                        style="display: flex;align-items: center;gap:4px">
                        {{ item.label }}
                        <uv-badge
                            v-if="item.label === '全部' && ((type === 'inner' && todoQuantity.inner > 0) || (type === 'out' && todoQuantity.out > 0))"
                            max="99" :value="type === 'inner' ? todoQuantity.inner : todoQuantity.out">
                        </uv-badge>
                    </view>
                </template>
                <!-- 我发起的 -->
                <template v-if="activeSecondLevelTab === 'initiate'">
                    <view v-for="(item, index) in [
                        { label: '全部', value: 'all' },
                        { label: '进行中', value: 'running' },
                        { label: '已完成', value: 'completed' }
                    ]" :key="index" :class="{ active: activeThirdLevelTab === index }"
                        @click="changeThirdLevelTab(index, item.value)">
                        {{ item.label }}
                    </view>
                </template>
                <!-- 我处理的 -->
                <template v-if="activeSecondLevelTab === 'handle'">
                    <view v-for="(item, index) in [
                        { label: '全部', value: 'all' }
                    ]" :key="index" :class="{ active: activeThirdLevelTab === index }"
                        @click="changeThirdLevelTab(index)">
                        {{ item.label }}
                    </view>
                </template>
                <!-- 抄送我的 -->
                <template v-if="activeSecondLevelTab === 'send'">
                    <view v-for="(item, index) in [
                        { label: '全部', value: 'all' },
                        { label: '已读', value: 'true' },
                        { label: '未读', value: 'false' }
                    ]" :key="index" :class="{ active: activeThirdLevelTab === index }"
                        @click="changeThirdLevelTab(index, item.value)">
                        {{ item.label }}
                    </view>
                </template>
            </view>
        </view>

        <!-- 内容区域 -->
        <scroll-view scroll-y :style="scrollStyle" class="content-area" @scrolltolower="loadMore"
            :scroll-top="scrollTop">
            <view v-if="activeFirstLevelTab === 0" style="padding-top: 24rpx;">
                <view style="padding-top: 100rpx;" v-if="messages.length === 0 && !isLoading">
                    <uv-empty mode="message" icon=""></uv-empty>
                </view>
                <view class="reminder-card" @click="openPopup(message)" v-for="(message, index) in messages"
                    :key="index">
                    <view>
                        <view class="content">
                            <view class="task">
                                <view class="task-info" style="align-items: center;gap: 4px;">
                                    <!-- 添加 uv-badge 红点 -->
                                    <uv-badge v-if="!message.view" isDot></uv-badge>
                                    <text>{{ messageTypeMap[message.messageType] || '未知类型消息' }}</text>
                                </view>
                                <text>{{ formatTime(message.createTime) }}</text>
                            </view>
                            <rich-text :nodes="message.content"></rich-text>
                        </view>
                        <view class="divider"></view>
                        <view class="view-details">
                            <text class="question">查看详情</text>
                            <uni-icons type="right" class="card-icon-right"></uni-icons>
                        </view>
                    </view>
                </view>
            </view>
            <view style="padding-top: 24rpx;" v-else>
                <view style="padding-top: 100rpx;" v-if="pendingTasks.length === 0 && !isLoading">
                    <uv-empty mode="list" icon=""></uv-empty>
                </view>
                <view v-for="(item, index) in pendingTasks" :key="index">
                    <view class="card" @click="handTask(item)">
                        <view>
                            <view>
                                <text class="form-name-bold">{{ item.formName }}</text>
                            </view>
                            <view class="task-info-flex">
                                <view>
                                    <uv-icon bold name="process" custom-prefix="custom-icon" size="16" />
                                    <text>{{ item.taskName }}</text>
                                </view>
                                <text>{{ formatTime(item.createTime) }}</text>
                            </view>
                        </view>
                        <view>
                            <view style="display: flex;">
                                <text class="fixed-width-text">发起人</text>
                                <view class="icon-circle">
                                    <uv-icon name="geren" custom-prefix="custom-icon" size="14" color="#fff" />
                                </view>
                                <text>{{ item.startUserName }}</text>
                            </view>
                            <view style="display: flex;">
                                <text class="fixed-width-text">来源应用</text>
                                <view class="ellipsis-text">
                                    <text>{{ item.applicationName }}</text>
                                </view>
                            </view>
                            <view>
                                <text class="fixed-width-text">审批流程图</text>
                                <text>{{ item.procDefName }}</text>
                            </view>
                        </view>
                    </view>
                </view>
            </view>
            <view style="height: 24rpx;"></view>
            <uv-load-more v-if="isLoading" status="loading" />
        </scroll-view>
        <uv-popup ref="popup" mode="bottom" :round="10" @change="onPopupClose">
            <!-- 这里可以添加弹窗的具体内容 -->
            <view v-if="selectedMessage" class="selectedMessage">
                <view v-html="selectedMessage.content"></view>
            </view>
        </uv-popup>
        <uv-picker ref="picker" :confirmColor="themeColor['--primary-color']"
            :activeColor="themeColor['--primary-color']" :columns="[typeOptions]" keyName="label"
            @confirm="confirmPicker">
        </uv-picker>
        <!-- 底部导航栏 -->
        <CustomTabBar />
    </div>
</template>

<script setup>
import { ref, computed, watch, queuePostFlushCb } from 'vue';
import { useGlobalTheme } from '@/utils/theme';
import { getMessage, getNotView, markMessageAsViewed, getPendingList, getOwnerList, getDoneList, getCopyList, flowableStatistic } from '@/service/message';
import CustomTabBar from '@/components/custom-tab-bar/index.vue';
import { useUserStore } from '@/store/user';
import { appConfig } from '@/config/app';
import { getToken } from '@/utils/auth';
import { onLoad, onShow } from '@dcloudio/uni-app';

const userStore = useUserStore();
const externalCompany = computed(() => userStore.externalCompany);
const { themeColor } = useGlobalTheme();
const toast = ref(null);
// 选择器
const picker = ref(null);
// 消息类型
const typeOptions = ref([{
    label: '内部组织',
    value: 'inner'
}, {
    label: '外部组织',
    value: 'out'
}]);
// 消息类型
const type = ref('inner');
// 存储通知信息
const messages = ref([]);
// 存储未读消息数量
const unreadMessageCount = ref(0);
const todoQuantity = ref({
    inner: 0,
    out: 0,
});
console.log('todoQuantity', todoQuantity);
// 存储待办任务列表
const pendingTasks = ref([]);
const total = ref(0);
// 当前页码
const currentPage = ref(1);
// 每页数量
const pageSize = ref(10);
// 控制 uv-popup 的显示与隐藏
const popup = ref(null);
// 存储当前选中的消息
const selectedMessage = ref(null);
// 当前激活的三级 tab 的 value
const activeThirdLevelTabValue = ref(null);
// 定义 messageType 到提示文字的映射
const messageTypeMap = {
    APPLICATION_EXPIRE: '提醒消息',
    VIP_EXPIRE: '提醒消息',
    DOSAGE_REMINDER: '用量提醒',
    NOTICE: '公司通知',
};
// 一级 tab 配置
const firstLevelTabs = [
    { label: '通知' },
    { label: '待办任务' }
];

// 二级 tab 配置
const secondLevelTabsMap = {
    0: [
        { label: '全部', value: 'all' },
        { label: '未读', value: 'unread' },
        { label: '已读', value: 'read' }
    ],
    1: [
        { label: '我的待办', value: 'waitting' },
        { label: '我发起的', value: 'initiate' },
        { label: '我处理的', value: 'handle' },
        { label: '抄送我的', value: 'send' }
    ]
};

// 获取状态栏高度
const statusBarHeight = uni.$uv.sys().statusBarHeight
// 获取胶囊按钮
let menuButtonWidth = 0
// #ifdef MP-WEIXIN
menuButtonWidth = uni.getMenuButtonBoundingClientRect().width
// #endif

// 当前激活的一级 tab
const activeFirstLevelTab = ref(0);
// 当前激活的二级 tab
const activeSecondLevelTab = ref('all');
// 当前激活的三级 tab
const activeThirdLevelTab = ref(0);

const scrollStyle = computed(() => {
    if (activeFirstLevelTab.value === 0) {
        return {
            height: `calc(100vh - 68px - 44px - ${statusBarHeight}px)`,
            paddingTop: '84rpx',
        };
    } else {
        return {
            height: `calc(100vh - 68px - 44px - ${statusBarHeight}px)`,
            paddingTop: '168rpx'
        };
    }
});

// 显示选择器
const showPicker = () => {
    picker.value.open();
}

// 组织类型切换
const confirmPicker = (e) => {
    type.value = e.value[0].value;
    fetchPendingTasks(activeSecondLevelTab.value, activeThirdLevelTabValue.value, e.value[0].value);
}

// 切换一级 tab
const changeFirstLevelTab = (index) => {
    if (index === activeFirstLevelTab.value) return;
    currentPage.value = 1;
    activeFirstLevelTab.value = index;
    activeSecondLevelTab.value = index === 0 ? 'all' : 'waitting';
    activeThirdLevelTab.value = 0;
    activeThirdLevelTabValue.value = null;
    if (index === 0) {
        fetchMessages();
    } else {
        const companyType = !todoQuantity.value.inner && todoQuantity.value.out ? 'out' : 'inner';
        type.value = companyType
        picker.value.setIndexs(companyType === 'inner' ? [0] : [1], true);
        fetchPendingTasks(activeSecondLevelTab.value, activeThirdLevelTabValue.value, companyType);
    }
};

// 添加加载状态
const isLoading = ref(false);

// 独立方法，根据二级 tab 索引和三级 tab value 调用不同 API
const fetchPendingTasks = async (secondLevelValue, thirdLevelValue, companyType) => {
    isLoading.value = true; // 开始加载
    const params = {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        companyUuid: companyType === 'out' ? externalCompany?.value?.companyUuid : undefined
    }
    try {
        let response;
        switch (secondLevelValue) {
            case 'waitting': // 我的待办
                response = await getPendingList({
                    ...params, status: (!thirdLevelValue || thirdLevelValue === 'all') ? '' : thirdLevelValue
                });
                break;
            case 'initiate': // 我发起的
                response = await getOwnerList({
                    ...params, status: (!thirdLevelValue || thirdLevelValue === 'all') ? '' : thirdLevelValue
                });
                break;
            case 'handle': // 我处理的
                response = await getDoneList(params);
                break;
            case 'send': // 抄送我的
                response = await getCopyList({
                    ...params, userView: (!thirdLevelValue || thirdLevelValue === 'all') ? '' : thirdLevelValue
                });
                break;
            default:
                return;
        }
        total.value = response.total;
        pendingTasks.value = currentPage.value === 1 ? response.list : [...pendingTasks.value, ...response.list];
    } finally {
        isLoading.value = false; // 加载结束
    }
};

// 切换二级 tab
const changeSecondLevelTab = async (value) => {
    currentPage.value = 1;
    activeSecondLevelTab.value = value;
    activeThirdLevelTab.value = 0;
    activeThirdLevelTabValue.value = null;
    if (activeFirstLevelTab.value === 0) {
        await fetchMessages();
    } else {
        total.value = 0;
        await fetchPendingTasks(value, activeThirdLevelTabValue.value, type.value);
    }
};

// 切换三级 tab
const changeThirdLevelTab = async (index, value) => {
    currentPage.value = 1;
    activeThirdLevelTab.value = index;
    activeThirdLevelTabValue.value = value;
    if (activeFirstLevelTab.value === 1) {
        total.value = 0;
        await fetchPendingTasks(activeSecondLevelTab.value, value, type.value);
    }
};

// 获取消息
const fetchMessages = async () => {
    isLoading.value = true; // 开始加载
    let view;
    if (activeSecondLevelTab.value === 'all') {
        view = null;
    } else if (activeSecondLevelTab.value === 'unread') {
        view = false;
    } else if (activeSecondLevelTab.value === 'read') {
        view = true;
    }

    try {
        const response = await getMessage({ view: view });
        if (response.code && response.code != '0000') {
            toast.value.show({
                position: 'top',
                icon: false,
                type: 'error',
                message: response.message,
            });
            return;
        }
        messages.value = response.data.list;
    } finally {
        isLoading.value = false; // 加载结束
    }
};

const onPopupClose = (e) => {
    if (!e.show && selectedMessage.value && !selectedMessage.value.view) {
        markMessageAsViewed(selectedMessage.value.id).then(() => {
            fetchMessages();
            fetchUnreadMessageCount()
        }).catch((error) => {
            console.error('标记消息为已读失败:', error);
        });
    }
};
const openPopup = (message) => {
    uni.navigateTo({
        url: '/pages/message/messageDetail?id=' + message.id
    })
    // selectedMessage.value = message;
    // popup.value.open();
};
// 处理滚动事件
const loadMore = () => {
    if (activeFirstLevelTab.value === 1 && pendingTasks.value.length < total.value) {
        currentPage.value++;
        fetchPendingTasks(activeSecondLevelTab.value, activeThirdLevelTabValue.value, type.value);
    }
}

const fetchUnreadMessageCount = async () => {
    try {
        const response = await getNotView();
        if (response) {
            let totalCount = 0;
            // 遍历响应数据的所有值并累加
            for (const key in response) {
                if (response.hasOwnProperty(key)) {
                    totalCount += response[key];
                }
            }
            unreadMessageCount.value = totalCount; // 假设返回数据结构中有 data 字段存储未读消息数量
        }
    } catch (error) {
        console.error('获取未读消息数量失败:', error);
    }
};

const handTask = (item) => {
    const webViewUrl = appConfig.webViewUrl;
    const token = getToken();
    let url = `${webViewUrl}/mobile/processCenter/detail?token=${token}&taskId=${item.taskId}&copyId=${item.id}&processInstanceId=${item.processInstanceId}&type=${activeSecondLevelTab.value}`;
    // 确保跳转路径与pages.json完全一致（注意大小写）
    if (type.value === 'out') {
        url += '&companyType=out&companyUuid=' + externalCompany.value.companyUuid;
    }
    uni.navigateTo({
        url: `/pages/webView/webView?url=${encodeURIComponent(url)}`,
    });
};

// 时间戳转换函数
const formatTime = (timestamp) => {
    if (!timestamp) return '';
    const date = new Date(timestamp);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}`;
};

// 获取待办数量
const getProcessCount = async (companyInfo) => {
    let res1 = null;
    let res2 = null;
    if (companyInfo === false) {
        res1 = await flowableStatistic();
    }
    if (companyInfo) {
        res1 = await flowableStatistic();
        res2 = await flowableStatistic({
            companyUuid: companyInfo.companyUuid,
        });
    }
    todoQuantity.value = {
        inner: res1?.pendingCount || 0,
        out: res2?.pendingCount || 0,
    }
}

onShow(() => {
    getProcessCount(externalCompany.value)
    fetchUnreadMessageCount()
    if (activeFirstLevelTab.value === 0) {
        fetchMessages();
    } else {
        fetchPendingTasks(activeSecondLevelTab.value, activeThirdLevelTabValue.value, type.value);
    }
})

onLoad(() => {
    uni.$on('messageUpdate', () => {
        if (activeFirstLevelTab.value === 0) {
            fetchMessages();
        } else {
            fetchPendingTasks(activeSecondLevelTab.value, activeThirdLevelTabValue.value, type.value);
        }
    })
})

// 监听外部公司变化
watch(externalCompany, (newExternalCompany) => {
    if (newExternalCompany) {
        type.value = 'inner';
        picture.value.setIndexs([0], true)
    }
})

</script>

<style lang="scss" scoped>
.message-container {
    padding-bottom: calc(68px + env(safe-area-inset-bottom));
    background-color: #F7F6F8;

    .external-company {
        display: flex;
        align-items: center;
        gap: 10rpx;
        color: #181818;
    }

    .tabs-wrap {
        position: fixed;
        width: 100%;
        left: 0;
        z-index: 10;
    }
}


.first-level-tabs {
    display: flex;
    gap: 64rpx;
    padding: 0 6rpx;
}

.first-level-tabs>view {
    font-size: 32rpx;
    font-weight: 600;
}

.first-level-tabs>view.active {
    color: #14B59C;
    /* 一级 tab 选中时文字变成绿色 */
    border-bottom: none;
    /* 移除一级 tab 的底部边框 */
}

.second-level-tabs {
    display: flex;
    width: 100%;
    box-sizing: border-box;
    gap: 40rpx;
    padding: 16rpx 32rpx 0;
    height: 84rpx;
    background-color: #fff;
}

.second-level-tabs>view {
    height: 44rpx;
    transition: all 0.3s ease; // 添加过渡效果
    color: initial;
    border-bottom: 2px solid transparent;
    box-sizing: border-box;
    font-size: 28rpx;
    line-height: 36rpx;
    color: #ACACAC;
}

.second-level-tabs>view.active {
    color: #181818;
    transition: all 0.3s ease; // 添加过渡效果
    border-bottom: 2px solid var(--primary-color);
}

.second-level-one-tabs {
    padding: 16rpx 32rpx;
    height: 84rpx;
    display: flex;
    width: 100%;
    box-sizing: border-box;
    align-items: center;
    gap: 24rpx;
    background-color: #fff;
}

.second-level-one-tabs>view {
    padding: 0 24rpx;
    background-color: #e3e5e8;
    height: 52rpx;
    border-radius: 8rpx;
    line-height: 52rpx;
}

.second-level-one-tabs>view.active {
    color: white;
    background-color: #14B59C;
    border-bottom: none;
}

.content-area {
    padding: 0 40rpx;
    box-sizing: border-box;
}

.card {
    background-color: #fff;
    padding: 32rpx;
    font-size: 28rpx;
    border-radius: 16rpx;
    color: #181818;
    margin-bottom: 24rpx;
}

.card>view>view {
    height: 42rpx;
    line-height: 42rpx;
    margin-bottom: 16rpx;
}

.card>view:nth-child(1) {
    margin-bottom: 24rpx;
}

.card>view:nth-child(2) {
    margin-top: 48rpx;
    position: relative;
}

.card>view:nth-child(2)::before {
    content: '';
    position: absolute;
    top: -24rpx;
    left: 0;
    right: 0;
    height: 1px;
    background-color: #eee; // 可以根据需要调整线条颜色
}

.form-name-bold {
    font-weight: bold;
}

.task-info-flex {
    display: flex;
    justify-content: space-between;

    view {
        display: flex;
        align-items: center;
        gap: 4px;
    }
}

.fixed-width-text {
    width: 140rpx;
    display: inline-block; // 确保宽度生效
    margin-right: 30rpx;
}

.icon-circle {
    width: 40rpx;
    height: 40rpx;
    border-radius: 50%;
    background-color: var(--primary-color);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 10rpx;
}

.ellipsis-text {
    max-width: 400rpx;
    /* 可根据实际情况调整最大宽度 */
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.reminder-card {
    background-color: #FFF;
    border-radius: 16rpx;
    padding: 32rpx;
    margin-bottom: 24rpx;
}

.reminder-card {
    .content {
        .task {
            display: flex;
            justify-content: space-between;
            margin-bottom: 24rpx;

            .task-info {
                display: flex;
                justify-content: space-between;
            }
        }
    }

}

.reminder-card .content {
    margin-bottom: 32rpx;
}

.reminder-card .divider {
    height: 1px;
    background-color: #eee;
    margin-bottom: 32rpx;
}

.reminder-card .view-details {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .question {
        font-weight: bold;
    }
}

.selectedMessage {
    margin-top: 40rpx;
    min-height: 200rpx;
    padding: 32rpx;
}
</style>
