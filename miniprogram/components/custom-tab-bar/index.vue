<template>
  <view v-if="token && companyInfo" class="tab-bar" :style="themeColor">
    <view v-for="(item, index) in list" :key="index" class="tab-bar-item"
      :class="{ active: current === index || (index === 0 && current < index) }"
      @click="switchTab(index, item.pagePath)">
      <view class="icon-box">
        <uv-icon custom-prefix="custom-icon"
          :name="(current === index || (index === 0 && current < index)) ? item.selectedIconPath : item.iconPath"
          :size="28"
          :color="(current === index || (index === 0 && current < index)) ? themeColor['--primary-color'] : '#7b7b7b'" />
      </view>
      <text class="tabbar-text">{{ item.text }}</text>
    </view>
  </view>
  <view v-else></view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { tabbarList } from '@/config/app';
import { useGlobalTheme } from '@/utils/theme';
import { getToken } from '@/utils/auth';
import { useUserStore } from '@/store/user';
const userStore = useUserStore();

const { themeColor } = useGlobalTheme();

// 登录token
const token = ref(getToken());
// 公司信息
const companyInfo = computed(() => userStore.userCompany);

const list = ref(tabbarList)

const current = computed(() => {
  const currentPath = uni.$uv.page()
  return list.value.findIndex(item => item.pagePath === currentPath)
})

// 切换tab项
const switchTab = (index, path) => {
  if (current.value === index) return
  uni.switchTab({
    url: path
  })
}

onMounted(() => {
  uni.$on('loginSuccess', function () {
    token.value = getToken();
  })
})

</script>

<style lang="scss">
@import '@/common/common.scss';

.tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 68px;
  display: flex;
  background: #fff;
  box-shadow: 0px -4px 20px 0px #8082821A;
	padding-bottom: env(safe-area-inset-bottom);


  .tab-bar-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    .icon-box {
      border-radius: 50%;
      display: flex;
      justify-content: center;
    }

    .tabbar-text {
      display: inline-block;
      font-size: 12px;
      color: #7b7b7b;
      margin-top: 2px;
    }

    &.active {
      .tabbar-text {
        color: var(--primary-color);
      }
    }
  }
}
</style>