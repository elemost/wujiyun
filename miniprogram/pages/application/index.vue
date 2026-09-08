<template>
    <view class="application-container" :style="themeColor">
        <uv-navbar ref="navbar" fixed :back-arrow="pages.length > 1" @click-left="goUrl" leftText="返回"
            :title="applicationInfo.applicationName" titleWidth="360rpx"
            :titleStyle="{ fontSize: platform === 'ios' ? '14px' : '16px' }">
            <template v-slot:left>
                <view class="uv-nav-slot">
                    <uv-icon @click="goUrl" :name="pages.length > 1 ? 'arrow-left' : 'home'" size="18"></uv-icon>
                    <uv-line direction="column" :hairline="false" length="16" margin="0 8px"></uv-line>
                    <uv-icon @click="open" name="grid" size="18"></uv-icon>
                </view>
            </template>
        </uv-navbar>
        <uv-popup ref="popup" mode="left" @change="popupChange">
            <view class="content" :style="{ paddingTop: `calc(44px + ${statusBarHeight}px)` }">
                <Menu :menu-data="menuData" :value="formId"></Menu>
            </view>
        </uv-popup>
    </view>
</template>
<script setup>
import { watchEffect, ref } from 'vue';
import { useGlobalTheme } from '@/utils/theme';
import { getApplicationDetailApi, getApplicationMenuApi } from '@/service/application';
import { getPlatform, getFormIcon } from "@/utils/common";
import Menu from '@/components/menu/index.vue';
const { themeColor } = useGlobalTheme();
const navbar = ref(null);
const popup = ref(null);
// 显示菜单
const showPopup = ref(false);
// 平台
const platform = getPlatform()
// 页面堆栈
const pages = uni.$uv.pages();
// 获取状态栏高度
const statusBarHeight = uni.$uv.sys().statusBarHeight
// 获取页面参数
const props = defineProps({
    applicationId: String,
    formId: String,
});

const applicationInfo = ref({});

const menuData = ref([]);

// 打开菜单
const open = () => {
    if (showPopup.value) {
        popup.value.close()
    } else {
        popup.value.open()
    }
}

// 菜单状态改变
const popupChange = (e) => {
    showPopup.value = e.show;
}

// 获取应用菜单列表
const getMenuList = async () => {
    const res = await getApplicationMenuApi(props.applicationId);
    const list = res || [];
    // 递归处理菜单数据
    const processMenu = (menu, level = 0) => {
        const children = menu.children || [];
        return {
            id: menu.id,
            name: menu.categoryName,
            level: level,
            icon: {
                name: getFormIcon(menu.categoryType, 'icon'),
                color: getFormIcon(menu.categoryType, 'color'),
            },
            type: menu.categoryType,
            children: children.map((el)=>processMenu(el, level + 1))
        };
    };
    menuData.value = list.map((item)=>processMenu(item));
    // console.log('menuData', menuData.value)
}

// 获取应用详情
const getAppDetail = async () => {
    const res = await getApplicationDetailApi(props.applicationId);
    applicationInfo.value = res || {};
}

// 跳转到上一级
const goUrl = () => {
    if (pages.length > 1) {
        uni.navigateBack({
            delta: 1
        })
    } else {
        uni.switchTab({
            url: '/pages/index/index'
        })
    }
}

// 监听applicationId变化
watchEffect(() => {
    if (props.applicationId) {
        getAppDetail()
        getMenuList()
    }
})


</script>
<style lang="scss" scoped>
.application-container {
    height: 100%;
    background-color: #fff;

    .uv-nav-slot {
        display: flex;
        align-items: center;
        justify-content: space-between;
        border-radius: 100px;
        padding: 3px 7px;
        border: 0.5px solid #dadbde;
    }

    .content {
        width: 600rpx;
        height: 100%;
        box-sizing: border-box;
    }

    :global(.uv-navbar--fixed) {
        z-index: 20000 !important;
    }
}
</style>
