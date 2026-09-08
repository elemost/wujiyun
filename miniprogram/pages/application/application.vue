<template>
    <!-- #ifdef MP-WEIXIN -->
    <web-view @error="onError" v-if="token && formId" :src="webUrl" @message="onMessage" />
    <!-- #endif -->
    <view style="padding-top: 100rpx;" v-if="isEmpty">
        <uv-empty mode="list" text="该应用还没有内容"></uv-empty>
    </view>
</template>
<script setup>
import { computed, ref } from 'vue';
import { clearLoginInfo, getToken } from '@/utils/auth';
import { appConfig } from '@/config/app';
import { getApplicationMenuApi } from '@/service/application';
import { getVersionInfoApi } from '@/service/platform';
import { onLoad } from '@dcloudio/uni-app';
const token = getToken();
const applicationId = ref('');
const formId = ref('');
const isEmpty = ref(false);
const version = ref('');

const webViewUrl = appConfig.webViewUrl;

const webUrl = computed(() => {
    if (formId.value) {
        return `${webViewUrl}/${applicationId.value}/mobile/app/${formId.value}?token=${token}${version.value ? '&version=' + version.value : ''}`
    }
})

// 获取应用菜单列表
const getFirstFormIdInApp = async () => {
    const res = await getApplicationMenuApi(applicationId.value);
    const list = res || [];
    const findFirstNonFileId = (data) => {
        for (const item of data) {
            if (item.categoryType !== 'FILE') {
                return item.id;
            }
            if (item.children && item.children.length > 0) {
                const result = findFirstNonFileId(item.children);
                if (result) {
                    return result;
                }
            }
        }
    }
    const firstNodFieldId = findFirstNonFileId(list);
    return firstNodFieldId;
}

// 获取表单信息
const getFormInfo = async () => {
    let id = '';
    if (!formId.value) {
        id = await getFirstFormIdInApp();
    } else {
        id = formId.value;
    }
    formId.value = id;
    isEmpty.value = !id;
	if (uni.getSystemInfoSync().uniPlatform === 'web' && id) {
		window.location.replace(`${webViewUrl}/${applicationId.value}/mobile/app/${formId.value}?token=${token}${version.value ? '&version=' + version.value : ''}`)
	}
}

// 处理消息
const onMessage = (e) => {
    const data = e?.detail?.data?.[0];
    if (data?.type === 'loginOut') {
        clearLoginInfo();
    }
}

// 加载失败
const onError = () => {
    uni.showToast({ title: '加载失败' });
}

const parseSceneParams = (sceneStr) => {
    const params = {};
    const pairs = sceneStr.split('&');

    pairs.forEach(pair => {
        const [key, value] = pair.split('=');
        if (key) {
            params[key] = value !== undefined ? decodeURIComponent(value.replace(/\+/g, ' ')) : '';
        }
    });

    return params;
}

onLoad(async (option) => {
    const scene = option?.scene ? decodeURIComponent(option.scene) : ''
    const params = parseSceneParams(scene);
    applicationId.value = option?.applicationId || params?.applicationId
    const versionInfo = await getVersionInfoApi();
    version.value = versionInfo?.version || '';
    formId.value = option?.formId || params?.formId
    if (applicationId.value) {
        getFormInfo()
    }
})
</script>
<style lang="scss" scoped></style>
