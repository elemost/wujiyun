<template>
    <view class="loading-container">
        <slot></slot>
        <view v-if="loading && isWrapper" class="loading-mask">
            <uv-loading-icon mode="circle" :color="loadingColor" :size="size" :textSize="textSize"
                :text="text"></uv-loading-icon>
        </view>
        <view v-if="!isWrapper && loading" class="loading-wrapper">
            <uv-loading-icon mode="circle" :color="loadingColor" :size="size" :textSize="textSize"
                :text="text"></uv-loading-icon>
        </view>
    </view>
</template>

<script setup>
import { computed } from 'vue';
import { useGlobalTheme } from '@/utils/theme';

const props = defineProps({
    loading: {
        type: Boolean,
        default: false
    },
    size: {
        type: Number,
        default: 24
    },
    textSize: {
        type: Number,
        default: 14
    },
    text: {
        type: String,
        default: ''
    },
    color: {
        type: String,
        default: ''
    },
    isWrapper: {
        type: Boolean,
        default: true
    }
});

const { themeColor } = useGlobalTheme();

const loadingColor = computed(() => {
    if (props.color) {
        return props.color;
    } else {
        return themeColor.value['--primary-color'];
    }
});
</script>

<style lang="scss" scoped>
.loading-container {
    position: relative;
}

.loading-mask {
    position: absolute;
    min-height: 24px;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(255, 255, 255, 0.7);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 999;
}

.loading-wrapper {
    min-height: 24px;
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>