<template>
    <view class="app-icon">
        <template v-if="appIcon.url">
            <image :src="appIcon.url" mode="aspectFill" class="app-icon" />
        </template>
        <template v-else>
            <view :style="{ backgroundColor: `#${appIcon.color}`, padding: '15rpx' }">
                <image :src="`/static/appIconSvg/${appIcon.type}.svg`" mode="widthFix" />
            </view>
        </template>
    </view>
</template>
<script setup>
import { computed } from 'vue';
import { defaultAppIconConfig } from '@/utils/constant';

const props = defineProps({
    icon: {
        type: String,
        default: ''
    }
})
const appIcon = computed(() => {
    let newIcon = JSON.parse(props.icon || '{}');
    if (!newIcon.url && !newIcon.type) {
        newIcon = defaultAppIconConfig;
    }
    return newIcon
})
</script>
<style lang="scss">
.app-icon {
    width: 100%;
    height: 100%;
    view{
        width: 100%;
        height: 100%;
        box-sizing: border-box;

        image{
            width: 100%;
        }
    }
}
</style>