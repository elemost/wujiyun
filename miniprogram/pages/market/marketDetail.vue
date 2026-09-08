<template>
    <!-- 添加 padding-bottom 样式 -->
    <view :style="{ ...themeColor, 'padding-bottom': '156rpx' }">
        <!-- 显示加载提示 -->
        <view v-if="isLoading" class="loading">
            <text>加载中...</text>
        </view>
        <view v-else-if="templateDetail">
            <!-- 最上方的上 view -->
            <view class="top-upper-view">
                <view class="head-content">
                    <!-- 假设这里是 icon -->
                    <view class="info-wrap">
                        <view class="icon-box">
                            <AppIcon :icon="templateDetail.icon" />
                        </view>
                        <view class="info-box">
                            <view class="text">{{ templateDetail.applicationName }}</view>
                            <view class="tags">
                                <text v-for="tag in templateDetail.tags" :key="tag.tagValue" class="tag-text">
                                    {{ tag.tagValue }}
                                </text>
                            </view>
                            <view class="time-box">
                                <text class="time">创建时间：{{ formatTime(templateDetail.createTime) }}</text>
                                <view class="download-count">
                                    <uv-icon name="icon_386" custom-prefix="custom-icon" size="16"></uv-icon>
                                    <text>
                                        {{ templateDetail.downloadCount }}
                                    </text>
                                </view>
                            </view>
                        </view>
                    </view>
                    <!-- 右边分享 icon -->
                    <view class="share-icon" v-if="templateDetail">
                        <uv-button customStyle="border:0;background:none" open-type="share">
                            <uv-icon name="fenxiang" custom-prefix="custom-icon" size="24"
                                :color="themeColor['--primary-color']"></uv-icon>
                        </uv-button>
                    </view>
                </view>
            </view>
            <view class="top-lower-view">
                <text>{{ templateDetail.description }}</text>
            </view>
            <!-- 轮播图 -->
            <swiper class="swiper-container" indicator-dots autoplay interval="5000" duration="500">
                <swiper-item v-for="(item, index) in templateDetail.imgList" :key="index">
                    <image :src="item.imgUrl" mode="aspectFill" class="swiper-image" />
                </swiper-item>
            </swiper>
            <!-- 具体介绍 -->
            <view class="description">
                <view class="title">模板介绍</view>
                <!-- #ifdef MP-WEIXIN -->
                <rich-text :nodes="templateDetail.introduce"></rich-text>
                <!-- #endif -->
                <!-- #ifdef H5 -->
                <view v-html="templateDetail.introduce"></view>
                <!-- #endif -->
            </view>
            <view class="bottom">
                <view>
                    热门模板
                </view>
                <view v-if="token" class="bottomRight" @click="goPage">
                    查看全部模板 >
                </view>
            </view>
            <view class="card-list">
                <view class=" empty-wrap" v-if="marketList.length === 0 && !isHotLoading">
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
                            <uv-icon name="icon_386" custom-prefix="custom-icon" size="16"></uv-icon>
                            <text>
                                {{ item.downloadCount > 10000 ? `${(item.downloadCount / 10000).toFixed(1)}w+` :
                                    item.downloadCount }}
                            </text>
                        </view>
                    </view>
                </view>
            </view>
        </view>
        <!-- 底部按钮 -->
        <view class="bottom-buttons">
            <view class="button-item">
                <uv-button :customStyle="{ 'border-width': '1px !important', }" :loading="loadingBtnOne"
                    :iconColor="themeColor['--primary-color']" text="仅安装模板" @tap="useTemplateWithoutData(false)"
                    type="primary" :plain="true" :hairline="true">仅安装模板</uv-button>
            </view>
            <view class="button-item">
                <uv-button :loading="loadingBtnTwo" @tap="useTemplateWithoutData(true)" type="primary" text="安装模板（带数据）"
                    class="button-item-two">安装模板 (带数据)</uv-button>
            </view>
        </view>

    </view>

</template>

<script setup>

import { onMounted, ref } from 'vue';
import { getTemplateDetail, useTemplate, postMarketList } from '@/service/market';
import AppIcon from '@/components/app-icon/index.vue';
import { useGlobalTheme } from '@/utils/theme';
import { onShareAppMessage } from "@dcloudio/uni-app";
import { getToken, logout } from '@/utils/auth';

const { themeColor } = useGlobalTheme();
const templateId = ref('');
const templateDetail = ref(null);
// 添加 isLoading 变量
const isLoading = ref(true);
const loadingBtnOne = ref(false);
const loadingBtnTwo = ref(false);
const isHotLoading = ref(false);
const marketList = ref([]);
const token = getToken();
// 跳转页面
const goPage = () => {
    uni.switchTab({
        url: 'market'
    })
}
// 分享配置
onShareAppMessage(() => {
    return {
        title: templateDetail.value.applicationName,
        path: `/pages/market/marketDetail?id=${templateId.value}`,
        // imageUrl: templateDetail.value.imgList[0].imgUrl,
        success: () => {
            uni.showToast({ title: '分享成功' });
        },
        fail: (err) => {
            uni.showToast({ title: '分享失败', icon: 'error' });
        }
    }
})

// 处理标签
const handleTags = (tags) => {
    // 只取前两个标签
    return tags.slice(0, 2)
}
// 获取列表数据
const fetchMarketList = async () => {
    if (isHotLoading.value) return;
    isHotLoading.value = true;
    try {
        const response = await postMarketList({
            page: 1,
            pageSize: 4,
            scene: '',
            sortType: "downloadCount",
            tag: 'HOT'
        });
        marketList.value = response.list;
    } catch (error) {
        console.error('获取列表失败:', error);
    } finally {
        isHotLoading.value = false;
    }
};
const changeMarket = (id) => {
    uni.navigateTo({
        url: `/pages/market/marketDetail?id=${id}` // 假设模板详情页面路径是这个，可根据实际情况修改
    });
};
// 封装调用 useTemplate 接口的方法，不带数据
const useTemplateWithoutData = async (value) => {
    if (!token) {
        const redirectUrl = encodeURIComponent(`/pages/market/marketDetail?id=${templateId.value}`);
        logout(redirectUrl, 'navigateTo');
        return;
    }
    if (templateId.value) {
        try {
            if (value) {
                loadingBtnTwo.value = true;
            } else {
                loadingBtnOne.value = true;
            }
            const result = await useTemplate(templateId.value, value);
            if (value) {
                loadingBtnTwo.value = false;
            } else {
                loadingBtnOne.value = false;
            }
            uni.$emit('templateUpdate');
            // 弹出弹窗
            uni.showModal({
                title: '提示',
                content: '安装成功',
                confirmText: '立即查看',
                success: (res) => {
                    if (res.confirm) {
                        // 点击立即查看后跳转页面，需替换为实际跳转路径
                        uni.navigateTo({
                            url: `/pages/application/application?applicationId=${result}`
                        });
                    }
                }
            });
        } catch (error) {
            if (value) {
                loadingBtnTwo.value = false;
            } else {
                loadingBtnOne.value = false;
            }
            console.error('仅安装模板失败:', error);
        }
    }
};

onMounted(async () => {
    fetchMarketList();
    const pages = getCurrentPages();
    const currentPage = pages[pages.length - 1];
    const options = currentPage.options || currentPage.$route?.query;

    templateId.value = options.id;
    if (templateId.value) {
        try {
            const response = await getTemplateDetail(templateId.value);
            templateDetail.value = response;
        } catch (error) {
            console.error('获取模板详情失败:', error);
        } finally {
            // 请求完成后，将 isLoading 设置为 false
            isLoading.value = false;
        }
    } else {
        console.error('未获取到模板 ID');
        isLoading.value = false;
    }
});

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
    return `${year}/${month}/${day} `;
};

</script>

<style scoped lang="scss">
.top-upper-view {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 32rpx 32rpx;

    .head-content {
        display: flex;
        align-items: center;
        width: 100%;
        gap: 24rpx;


        .info-wrap {
            display: flex;
            flex: 1;
            gap: 24rpx;

            .icon-box {
                width: 120rpx;
                height: 120rpx;
            }

            .info-box {
                display: flex;
                width: 100%;
                flex-direction: column;

                .text {
                    font-weight: bold;
                    height: 44rpx;
                    line-height: 44rpx;
                    font-size: 32rpx;
                    color: #000000;
                }

                .tags {
                    display: flex;
                    flex-wrap: wrap;
                    margin: 16rpx 0;
                    gap: 8rpx;

                    .tag-text {
                        font-size: 20rpx;
                        line-height: 32rpx;
                        color: #14B59C;
                        background-color: #F5FFFC;
                        padding: 0 12rpx;
                        border-radius: 8rpx;
                        border: 1px solid #AFE1D9;
                    }
                }

                .time-box {
                    display: flex;
                    width: 100%;
                    align-items: center;
                    justify-content: space-between;

                    .time {
                        color: #7B7B7B;
                        font-size: 24rpx;
                        line-height: 32rpx;
                    }

                    .download-count {
                        display: flex;
                        align-items: center;
                        gap: 4px;
                        font-size: 24rpx;
                        color: #7B7B7B;
                    }
                }
            }
        }

        .share-icon {
            width: 48rpx;

            .share-button {
                background: none;
                border: none;
                padding: 0;
                margin: 0;
                outline: none;
                -webkit-appearance: none;
                -moz-appearance: none;
                appearance: none;
            }
        }
    }
}

.top-lower-view {
    height: 36px;
    font-size: 12px;
    line-height: 18px;
    padding: 0 16px 16px 16px;
    color: #000;
}

.swiper-container {
    width: 560rpx;
    height: 316rpx;
    display: flex;
    text-align: center;
    margin: 0 auto; // 可根据实际需求调整高度
}

.swiper-image {
    width: 100%;
    height: 100%;
}

.description {
    // 可以根据需要添加样式
    padding: 32rpx 32rpx 0;
    font-size: 28rpx;
    color: #333;

    .title {
        font-size: 32rpx;
        font-weight: 500;
        margin-bottom: 20rpx;
    }
}

.bottom-buttons {
    height: 156rpx;
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    display: flex;
    gap: 40rpx;
    background-color: white;
    justify-content: center;
    align-items: center;
    padding: 0 32rpx;
}

.button-item {
    flex: 1;
}

.loading {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
}

.bottom {
    display: flex;
    justify-content: space-between;
    height: 44rpx;
    padding: 24rpx 32rpx;
    font-size: 32rpx;
    font-weight: 500;
    color: #1b1b1b;

    .bottomRight {
        color: var(--primary-color);
        font-size: 24rpx;
        font-weight: normal;
    }
}

.card-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    padding: 0 32rpx 32rpx;

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
    margin-left: 10rpx;
    gap: 6rpx
}
</style>
