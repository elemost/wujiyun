<template>
  <ul>
    <li v-for="item in treeData" :key="item.deptId">
      <view 
        @click="handleNodeClick(item)" 
        :style="{ paddingLeft: depth * 20 + 'px' }" 
        :class="['item', { 'selected': selectedDeptId === item.deptId }]"
      >
        {{ item.deptName }}
      </view>
      <Tree 
        @node-click="handleNodeClick" 
        v-if="item.children" 
        :treeData="item.children" 
        :depth="depth + 1"
        :selectedDeptId="selectedDeptId"
      />
    </li>
  </ul>
</template>

<script setup>
import Tree from './index.vue'; // 导入自身组件

defineProps({
  treeData: {
    type: Array,
    required: true
  },
  depth: {
    type: Number,
    default: 0
  },
  // 新增入参，用于表示当前选中的部门 ID
  selectedDeptId: {
    type: Number,
    default: null
  }
});

const emit = defineEmits(['node-click']);

const handleNodeClick = (node) => {
  emit('node-click', node);
}; 
</script>

<style scoped>
ul {
  list-style-type: none;
  padding-left: 0;
}

.item {
  height: 36px;
  line-height: 36px;
  font-size: 14px;
}

/* 选中状态的样式 */
.item.selected {
  background-color: var(--primary-color);
  color: white;
}
</style>