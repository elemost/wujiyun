<template>
    <web-view @message="onMessage" :src="url"></web-view>
</template>

<script setup>
import { computed, ref, watchEffect } from 'vue';
import { onLoad } from '@dcloudio/uni-app';
import { useUserStore } from '@/store/user';
import { clearLoginInfo } from '@/utils/auth';
import { getVersionInfoApi } from '@/service/platform';
const userStore = useUserStore();
// 公司信息
const companyInfo = computed(() => userStore.userCompany);
// 外部公司信息
const externalCompanyList = computed(() => userStore?.userInfo?.corpCompanyList || []);

const url = ref('');

watchEffect(() => {
    if (companyInfo.value) {
        uni.setNavigationBarTitle({
            title: companyInfo.value?.companyName,
        })
    }
})

// 处理消息
const onMessage = (e) => {
    const data = e?.detail?.data?.[0];
    if (data?.type === 'loginOut') {
        clearLoginInfo();
    } else if (data?.type === 'messageUpdate') {
        uni.$emit('messageUpdate');
    } else if (data?.type === 'switchExternalCompany') {
        const companyInfo = externalCompanyList.value.find(item => item.companyUuid === data.companyUuid);
        if (companyInfo) {
            userStore.switchExternalCompany(companyInfo);
        }
    }
}

// 正确获取参数的方式
onLoad(async (options) => {
    if (options.url) {
        const versionInfo = await getVersionInfoApi();
        const version = versionInfo?.version || '';
        url.value = decodeURIComponent(options.url + (version ? '&version=' + version : ''));
    } else {
        uni.showToast({ title: '加载失败', icon: 'none' });
        setTimeout(() => uni.navigateBack(), 2000);
    }
});
</script>