<template>
    <view class="content" :style="themeColor">
        <view class="search-box">
            <view class="input-box">
                <uv-input 
                    v-model="searchText"
                    placeholder="输入名称进行搜索" prefixIcon="search" border="none"
                    placeholderStyle="color: #ACACAC;font-size: 28rpx" prefixIconStyle="font-size: 20px;color: #7B7B7B"
                    @input="inputChange">
                    <template v-slot:suffix>
                        <uv-icon v-if="searchText" @click="() => {
                            inputChange('');
                        }" name="close-circle-fill" color="#ACACAC" size="18" />
                    </template>
                </uv-input>
            </view>
        </view>
        <scroll-view scroll-y="true" class="menu-container">
            <menu-item v-for="item in menuList" :key="item.id" :item="item" @item-click="handleItemClick"
                @item-expand="handleItemExpand" :expanded-keys="expandedKeys" :selected-keys="selectedKeys"
                :search-word="searchText" />
        </scroll-view>
    </view>
</template>

<script setup>
import { watchEffect, ref } from 'vue';
import { useGlobalTheme } from '@/utils/theme';
import MenuItem from './menu-item.vue';
const props = defineProps({
    // 菜单数据
    menuData: {
        type: Array,
        default: () => []
    },
    // 值
    value: {
        type: String,
        default: ''
    },
    checkStrictly: {
        type: Boolean,
        default: false
    }
});
const { themeColor } = useGlobalTheme();
const menuList = ref([]);
// 默认展开的菜单ID
const expandedKeys = ref([]);
// 默认选中的菜单ID
const selectedKeys = ref([]);
// 搜索框内容
const searchText = ref('');

const emit = defineEmits(['on-select']);

// 输入框内容改变
const inputChange = (value) => {
    searchText.value = value.trim();
    uni.$uv.debounce(() => {
        if (searchText.value) {
            // 递归搜索函数
            const searchMenu = (data, keyword) => {
                const result = [];

                data.forEach(item => {
                    const isMatch = item.name.includes(keyword);
                    const hasChildren = item.children && item.children.length > 0;

                    // 递归处理子节点
                    let filteredChildren = [];
                    if (hasChildren) {
                        const childResult = searchMenu(item.children, keyword);
                        filteredChildren = childResult.result;
                    }

                    // 如果当前节点匹配 或 子节点有匹配项，则保留当前节点
                    if (isMatch || filteredChildren.length > 0) {
                        const newItem = { ...item };
                        if (filteredChildren.length > 0) {
                            newItem.children = filteredChildren;
                            newItem.expanded = true; // 可选：用于前端展开菜单
                        }
                        result.push(newItem);
                    }
                });

                // 收集所有匹配的ID，递归处理
                const matchedIds = result.flatMap(item => {
                    const childIds = item.children ? item.children.flatMap(child => child.id) : [];
                    return [item.id, ...childIds];
                })

                return { result, matchedIds };
            };

            const { result, matchedIds } = searchMenu(props.menuData, searchText.value);
            menuList.value = result;
            expandedKeys.value = matchedIds; // 合并去重
        } else {
            menuList.value = props.menuData;
            expandedKeys.value = [props.value];
        }
    }, 500)
};

// 修改handleItemClick方法
const handleItemClick = (item) => {
    emit('on-select', item);
    if (props.checkStrictly) {
        if (expandedKeys.value.includes(item.id)) {
            expandedKeys.value = expandedKeys.value.filter(key => key !== item.id);
        } else {
            expandedKeys.value = [...expandedKeys.value, item.id];
        }
    }
};

// 修改handleItemExpand方法
const handleItemExpand = (item) => {
    if (expandedKeys.value.includes(item.id)) {
        expandedKeys.value = expandedKeys.value.filter(key => key !== item.id);
    } else {
        expandedKeys.value = [...expandedKeys.value, item.id];
    }
    if (props.checkStrictly) {
        // 选中当前节点
        emit('on-select', item);
    }
};

watchEffect(() => {
    menuList.value = props.menuData;
    if (props.value) {
        selectedKeys.value = [props.value];
    } else {
        // 如果props.value为空，递归找到menuData里type不为FILE的第一个id作为默认选中的菜单ID
        const findFirstNonFileId = (data) => {
            for (const item of data) {
                if (item.type !== 'FILE') {
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
        const firstNodFieldId = findFirstNonFileId(props.menuData);
        expandedKeys.value = [firstNodFieldId];
        selectedKeys.value = [firstNodFieldId];
    }
});

</script>

<style lang="scss" scoped>
.content {
    height: 100%;

    .search-box {
        padding: 16rpx 32rpx;
        box-sizing: border-box;
        width: 100%;

        .input-box {
            height: 76rpx;
            border-radius: 38rpx;
            background-color: #F5F6F8;
            display: flex;
            align-items: center;
            padding: 0 32rpx;
        }
    }

    .menu-container {
        padding: 0 36rpx;
        height: calc(100% - 108rpx);
        box-sizing: border-box;
    }
}
</style>