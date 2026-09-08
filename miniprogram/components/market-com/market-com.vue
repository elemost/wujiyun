<template>
    <view class="market-com" :style="themeColor">
        <view class="market-header">
            <!-- 搜索框 -->
            <view style="padding:24rpx 32rpx 0;">
                <uv-input :customStyle="{ 'border-width': '1px !important' }" v-model="searchKeyword"
                    placeholder="请输入搜索内容" @input="handleSearch" prefixIcon="search"
                    prefixIconStyle="font-size: 22px;color: #909399">
                    <template v-slot:suffix>
                        <uv-icon v-if="searchKeyword" @click="handleSearch('')" name="close-circle-fill" color="#ACACAC"
                            size="18" />
                    </template>
                </uv-input>
            </view>
            <!-- 可折叠筛选项 -->
            <view class="filter-section">
                <view class="filter-header" v-if="!isFilterExpanded">
                    <uv-tabs :list="filterOptions" :current="currentTab" lineColor='none'
                        :itemStyle="{ height: '52rpx', padding: '0', margin: '0 16rpx 0 0' }"
                        :inactiveStyle="inactiveStyle" :activeStyle="activeStyle" @click="selectFilter">
                        <template v-slot:right>
                            <view class="icon-box">
                                <uv-icon v-if="!isFilterExpanded" name="zhankai" custom-prefix="custom-icon" size="18"
                                    @click="toggleShowAllOptions"></uv-icon>
                            </view>
                        </template>
                    </uv-tabs>
                </view>
                <!-- 根据 isFilterExpanded 控制筛选项的显示 -->
                <view class="filter-options-wrap" v-if="isFilterExpanded">
                    <!-- 展开按钮 -->
                    <view @click="toggleShowAllOptions" class="close">
                        <text>收起</text>
                    </view>
                    <view class="filter-options">
                        <view v-for="(option, index) in filterOptions" :key="option.name"
                            :class="['option-item', { 'selected': selectedFilter === option.name }]"
                            @click="selectFilter({ ...option, index })">
                            {{ option.name }}
                        </view>
                    </view>
                </view>
            </view>
        </view>

        <view v-if="isFilterExpanded" @click="toggleShowAllOptions" class="mask"></view>

        <!-- 滚动列表 -->
        <scroll-view scroll-y :lower-threshold="100" :scroll-top="scrollTop" @scroll="scroll" @scrolltolower="loadMore"
            class="content">
            <view class="card-list">
                <view class="empty-wrap" v-if="isEmpty">
                    <uv-empty mode="data" text="没有找到相关应用" icon=""></uv-empty>
                </view>
                <view v-for="item in marketList" :key="item.id" class="card-item" @click="changeMarket(item.id)">
                    <view class="card-header">
                        <image :src="item.logo" mode="aspectFill" class="card-logo" />

                    </view>
                    <view class="card-name">
                        <text class="card-name-text">{{ item.applicationName }}</text>
                        <view v-if="item.hotTags && item.hotTags[0].tagValue === 'HOT'">
                            <image src="/static/img/hot.png" mode="aspectFit" style="width: 24px;height: 12px;" />
                        </view>
                        <view v-else-if="item.hotTags">
                            <image src="/static/img/new.png" mode="aspectFit" style="width: 24px;height: 12px;" />
                        </view>
                    </view>
                    <view class="card-description">{{ item.description }}</view>
                    <view class="card-footer">
                        <view class="card-tags">
                            <text v-for="tag in handleTags(item.tags || [])" :key="tag.tagValue" class="tag">
                                {{ tag.tagValue }}
                            </text>
                        </view>
                        <view class="card-download-count">
                            <uv-icon name="icon_386" custom-prefix="custom-icon" size="32rpx"></uv-icon>
                            <text>
                                {{ item.downloadCount > 10000 ? `${(item.downloadCount / 10000).toFixed(1)}w+` :
                                    item.downloadCount }}
                            </text>
                        </view>
                    </view>
                </view>
            </view>
            <uv-load-more
                :customStyle="{ display: marketList.length > 6 ? 'flex' : 'none', marginBottom: 0, marginTop: 0, padding: '20rpx 0' }"
                :status="total > marketList.length ? 'loading' : 'nomore'" />
        </scroll-view>
    </view>
</template>

<script setup>
import { useGlobalTheme } from '@/utils/theme';
import { ref, watch, computed, nextTick } from 'vue';
import { postMarketList } from '@/service/market';
import { filterOptions } from '@/config/loginOptions';

const { themeColor } = useGlobalTheme();
const searchKeyword = ref('');
const isFilterExpanded = ref(false);
const selectedFilter = ref('全部场景');
const marketList = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(8);
const currentTab = ref(0);
const inactiveStyle = {
    backgroundColor: '#DEDEDE',
    color: '#181818',
    height: '52rpx',
    padding: '0 20rpx',
    borderRadius: '8rpx',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontSize: '24rpx'
}
const scrollTop = ref(0);
const oldScrollTop = ref(0);
const isEmpty = ref(false);

// 定义props
const props = defineProps({
    isRequest: {
        type: Boolean,
        default: false
    }
})

// 是否请求
const isRequest = computed(() => props.isRequest);

const activeStyle = {
    ...inactiveStyle,
    backgroundColor: themeColor.value['--primary-color'],
    color: '#fff'
}

const scroll = (e) => {
    oldScrollTop.value = e.detail.scrollTop;
}

// 选择筛选选项
const selectFilter = (option) => {
    selectedFilter.value = option.name;
    currentPage.value = 1;
    currentTab.value = option.index;
    fetchMarketList();
};

// 处理搜索
const handleSearch = (val) => {
    const searchWord = val.trim();
    searchKeyword.value = searchWord;
    currentPage.value = 1;
    uni.$uv.debounce(() => {
        fetchMarketList();
    }, 500)
};

// 处理标签
const handleTags = (tags) => {
    // 只取前两个标签
    return tags.slice(0, 2)
}

// 获取列表数据
const fetchMarketList = async () => {
    try {
        const response = await postMarketList({
            pageNum: currentPage.value,
            pageSize: pageSize.value,
            applicationName: searchKeyword.value || '',
            scene: selectedFilter.value === '全部场景' ? '' : selectedFilter.value,
            sortType: "downloadCount"
        });
        marketList.value = currentPage.value == 1 ? response.list : [...marketList.value, ...response.list];
        total.value = response.total;
        if (currentPage.value == 1) {
            scrollTop.value = oldScrollTop.value;
            nextTick(() => {
                scrollTop.value = 0;
            })
        }
        isEmpty.value = total.value === 0;
    } catch (error) {
        console.error('获取列表失败:', error);
    }
};

// 滚动到底部加载更多
const loadMore = () => {
    if (marketList.value.length < total.value) {
        currentPage.value++;
        fetchMarketList();
    }
};

watch(isRequest, (newVal) => {
    if (newVal) {
        currentPage.value = 1;
        fetchMarketList();
    }
}, { immediate: true })

// 跳转到模板详情页面
const changeMarket = (id) => {
    uni.navigateTo({
        url: `/pages/market/marketDetail?id=${id}` // 假设模板详情页面路径是这个，可根据实际情况修改
    });
};

// 切换显示全部选项
const toggleShowAllOptions = () => {
    isFilterExpanded.value = !isFilterExpanded.value;
};
</script>

<style scoped lang="scss">
.market-com {
    position: relative;

    .market-header {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        display: flex;
        flex-direction: column;
        background-color: #fff;
        box-sizing: border-box;
        z-index: 100;
    }
}

.filter-section {
    padding: 24rpx 32rpx;
    background-color: #fff;
    // border-bottom-right-radius: 24rpx;
    // border-bottom-left-radius: 24rpx;
}

.mask {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.2);
    z-index: 99;
}

.filter-header {
    .icon-box {
        width: 66rpx;
        height: 100%;
        display: flex;
        justify-content: flex-end;
        align-items: center;
    }
}

.filter-options-wrap {
    .filter-options {
        padding: 24rpx 0 12rpx;
        display: flex;
        flex-wrap: wrap;
        gap: 24rpx;
    }

    .close {
        color: var(--primary-color);
        font-size: 24rpx;
        display: flex;
        justify-content: flex-end;
    }
}

.filter-options-inline {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
}

.option-item {
    height: 52rpx;
    padding: 0 20rpx;
    border-radius: 4px;
    background-color: #DEDEDE;
    color: #181818;
    box-sizing: border-box;
    text-align: center;
    // 添加以下代码使内容垂直居中
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 24rpx;
}

.option-item.selected {
    background-color: var(--primary-color);
    color: #fff;
}

.content {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: calc(100vh - 68px);
    background-color: #F6F6F8;
    padding: 208rpx 32rpx 0;
    box-sizing: border-box;
}

.card-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    padding: 20rpx 0;

    .empty-wrap {
        width: 100%;
        display: flex;
        justify-content: center;
        align-items: center;
        padding-top: 100rpx;
    }
}

.card-item {
    width: calc(50% - 5px);
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    padding: 10px;
    box-sizing: border-box;
}

.card-header {
    position: relative;
    margin-bottom: 10px;
}

.card-logo {
    width: 100%;
    height: 100px;
    border-radius: 4px;
}


.card-name {
    font-weight: bold;
    margin-bottom: 5px;
    display: flex;
    gap: 5px;

    .card-name-text {
        width: 114px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }
}

.card-description {
    font-size: 12px;
    color: #666;
    margin-bottom: 10px;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
    text-overflow: ellipsis;
}

.card-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.card-tags {
    flex: 1;
    display: flex;
    gap: 4rpx;
}

.tag {
    flex: 1;
    font-size: 12px;
    color: #14B59C;
    background-color: #F5FFFC;
    border-radius: 4px;
    margin-right: 4px;
    padding: 2px;
    border: 1px solid #AFE1D9; // 边框颜色与背景颜色相同，形成无间隙的效果;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    box-sizing: border-box;
    text-align: center; // 文本居中对齐;
}

.card-download-count {
    font-size: 24rpx;
    color: #999;
    display: flex;
    align-items: center;
    gap: 6rpx;
    margin-left: 10rpx;
}
</style>