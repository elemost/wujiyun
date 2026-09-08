<template>
    <view :style="themeColor">
        <view class="menu-item" :style="{ paddingLeft: (item.level * 20) + 'px' }" @click.stop="handleClick">
            <view class="menu-item-content"
                :style="{ background: selectedKeys.includes(item.id) ? activeBgColor : '#fff' }">
                <view class="menu-box">
                    <uv-icon custom-prefix="custom-icon" :name="handleIcon(item, 'name')"
                        :color="handleIcon(item, 'color')" size="20" />
                    <view class="menu-text">
                        <rich-text :nodes="handleName(item.name)"></rich-text>
                    </view>
                </view>
                <view class="expand" @click.stop="handleExpandClick">
                    <uv-icon v-if="hasChildren" :name="expandedKeys.includes(item.id) ? 'arrow-down' : 'arrow-right'"
                        size="14" color="#666" :style="{ marginLeft: '20rpx' }" />
                </view>
            </view>
            <view v-if="expandedKeys.includes(item.id) && hasChildren" class="submenu">
                <menu-item v-for="child in item.children" :key="child.id" :item="child" :expanded-keys="expandedKeys"
                    :search-word="searchWord" :selected-keys="selectedKeys" @item-click="$emit('item-click', $event)" @item-expand="$emit('item-expand', $event)" />
            </view>
        </view>
    </view>
</template>

<script setup>
import { useGlobalTheme } from '@/utils/theme';
import { computed } from 'vue';
import { getFormIcon } from '@/utils/common';
import MenuItem from './menu-item.vue';

const { themeColor } = useGlobalTheme();

const activeBgColor = uni.$uv.colorToRgba(themeColor.value['--primary-color'], 0.30)

const props = defineProps({
    item: {
        type: Object,
        required: true
    },
    expandedKeys: {
        type: Array,
        default: () => []
    },
    selectedKeys: {
        type: Array,
        default: () => []
    },
    searchWord: {
        type: String,
        default: ''
    }
});
const emit = defineEmits(['item-click', 'item-expand']);

const hasChildren = computed(() => props.item.children && props.item.children.length);

// 处理图标
const handleIcon = (item, returnField) => {
    if (item.type === 'FILE') {
        const typeIcon = props.expandedKeys.includes(item.id) ? 'EXPANDFOLDER' : 'FOLDER';
        const folderIcon = {
            name: getFormIcon(typeIcon, 'icon'),
            color: '#00b899'
        }
        return folderIcon[returnField];
    } else {
        return item.icon[returnField];
    }
}

// 处理数据的名称，关键字高亮
const handleName = (name) => {
    const reg = new RegExp(props.searchWord, 'g');
    const str = name.replace(reg, `<span style="color: var(--primary-color)">${props.searchWord}</span>`);
    return `<div>${str}</div>`
}

const handleClick = () => {
    emit('item-click', props.item);
};
const handleExpandClick = () => {
    emit('item-expand', props.item);
}
</script>
<style lang="scss" scoped>
.menu-item {

    .menu-item-content {
        display: flex;
        align-items: center;
        border-bottom: 1px solid #f0f0f0;
        width: 100%;
        box-sizing: border-box;
        border-radius: 8rpx;

        .expand{
            width: 60rpx;
            padding: 20rpx 0;
            display: flex;
            align-items: center;
            justify-content: center;
        }
    }

    .menu-box {
        display: flex;
        align-items: center;
        padding: 20rpx 10rpx;
        flex: 1;
        box-sizing: border-box;

        .menu-text {
            font-size: 28rpx;
            color: #333;
            flex: 1;
            margin-left: 10rpx;
        }
    }
}
</style>
