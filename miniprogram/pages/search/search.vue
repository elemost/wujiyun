<template>
    <div class="search-contanier" :style="themeColor">
        <div class="search-box">
            <view class="input-box">
                <uv-input 
                    v-model="searchText"
                    placeholder="输入名称进行搜索" prefixIcon="search" 
                    border="none" 
                    focus 
                    placeholderStyle="color: #ACACAC;font-size: 28rpx" prefixIconStyle="font-size: 20px;color: #7B7B7B"
                    @input="inputChange">
                    <template v-slot:suffix>
                        <uv-icon v-if="searchText" @click="clearInput" name="close-circle-fill" color="#ACACAC" size="18" />
                    </template>
                </uv-input>
            </view>
        </div>
        <view class="search-result">
            <view v-if="!searchReasult.length && searchText && !loading" class="tip">没有搜索到相关结果</view>
            <view v-if="loading" class="tip">搜索中...</view>
            <view class="result-item" v-for="item in searchReasult" :key="item.id" @click="navigatorTo(item)">
                <view class="icon-box">
                    <template v-if="item.type === 'APPLICATION'">
                        <AppIcon :icon="item.icon" />
                    </template>
                    <template v-else>
                        <uv-icon :name="getFormIcon(item.type, 'icon')" :color="getFormIcon(item.type, 'color')"
                            custom-prefix="custom-icon" size="30" />
                    </template>
                </view>
                <view class="name">
                    <rich-text :nodes="handleName(item.name)"></rich-text>
                </view>
            </view>
        </view>
    </div>
</template>
<script setup>
import { onMounted, ref } from 'vue';
import { useGlobalTheme } from '@/utils/theme';
import { getFormIcon } from '@/utils/common';
import AppIcon from '@/components/app-icon/index.vue';
import { getApplicationAndFormApi } from '@/service/application';
const { themeColor } = useGlobalTheme();
const searchReasult = ref([]);
const allApplicationAndForm = ref([]);
const searchText = ref('');
const loading = ref(false);

// 处理数据的名称，关键字高亮
const handleName = (name) => {
    const reg = new RegExp(searchText.value, 'g');
    const str = name.replace(reg, `<span style="color: var(--primary-color)">${searchText.value}</span>`);
    return `<div style="width:100%;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;">${str}</div>`
}

// 获取搜索结果
const getSearchResult = async () => {
    const res = await getApplicationAndFormApi();
    allApplicationAndForm.value = res || [];
}

// 根据输入框内容筛选搜索结果
const filterSearchResult = (value) => {
    const text = value.trim();
    if (text) {
        searchReasult.value = allApplicationAndForm.value.filter((item) => {
            return item.name?.toString().toLowerCase().includes(text.toLowerCase());
        }).map((item) => {
            return {
                ...item,
                id: item.applicationId + '#' + (item.formId || ''),
            }
        })
    } else {
        searchReasult.value = [];
    }
}

// 清除输入框内容
const clearInput = () => {
    searchText.value = '';
    inputChange('');
}

// 输入框内容改变
const inputChange = (value) => {
    searchReasult.value = [];
    if (value.trim()) loading.value = true;
    uni.$uv.debounce(() => {
        loading.value = false;
        filterSearchResult(value);
        searchText.value = value.trim();
    }, 500)
}

// 跳转
const navigatorTo = (item) => {
    if (item.type === 'APPLICATION') {
        uni.navigateTo({
            url: `/pages/application/application?applicationId=${item.applicationId}`
        })
    } else {
        uni.navigateTo({
            url: `/pages/application/application?applicationId=${item.applicationId}&formId=${item.id}`
        })
    }
}

onMounted(() => {
    getSearchResult();
})

</script>
<style lang="scss" scoped>
.search-contanier {
    height: 100%;
    background-color: #fff;
    padding-top: 108rpx;
    box-sizing: border-box;

    .search-box {
        position: fixed;
        top: 0;
        left: 0;
        padding: 16rpx 32rpx;
        display: flex;
        align-items: center;
        justify-content: space-between;
        box-sizing: border-box;
        width: 100%;
        z-index: 100;

        .input-box {
            flex: 1;
            margin-right: 24rpx;
            height: 76rpx;
            border-radius: 16rpx;
            background-color: #F5F6F8;
            display: flex;
            align-items: center;
            padding: 0 32rpx;
        }

        .search-text {
            font-size: 28rpx;
            color: var(--primary-color);
            line-height: 44rpx;
        }
    }

    .search-result {
        padding: 0 32rpx;

        .tip {
            padding-top: 84rpx;
            text-align: center;
            font-size: 28rpx;
            color: #ACACAC;
        }

        .result-item {
            padding: 24rpx 32rpx;
            display: flex;
            align-items: center;
            border-bottom: 1rpx solid #EEEFEF;

            .icon-box {
                width: 60rpx;
                height: 60rpx;
                border-radius: 10rpx;
                margin-right: 24rpx;
                overflow: hidden;
            }

            .name {
                font-size: 34rpx;
                line-height: 44rpx;
                flex: 1;
                color: #181818;
            }
        }
    }
}
</style>