<template>
  <view class="my-container" :style="themeColor">
    <view class="my-content">
      <!-- 个人信息模块 -->
      <view class="user-info">
        <view class="avatar">
          <image class="avatarImg" v-if="userInfo?.avatar" :src="userInfo.avatar" mode="aspectFill" />
          <image class="avatarImg" v-else src="/static/img/default-head.jpg" mode="aspectFill" />
        </view>
        <view class="user-details">
          <view class="user-name">
            <OpenData type="userName" :openid="userInfo?.nickName || ''" />
          </view>
          <view class="user-company-container">
            <view class="user-company">
              {{ userCompany && userCompany.companyName || '' }}
            </view>
            <!-- 根据公司数量控制按钮显示 -->
            <!-- #ifdef MP-WEIXIN -->
            <view v-if="companyCount > 1" class="switch-btn" @click="openCompany">
              <uv-icon name="Vector" :color="themeColor['--primary-color']" custom-prefix="custom-icon"
                size="30rpx"></uv-icon>
              <text class="switch-text">切换组织</text>
            </view>
            <!-- #endif -->
          </view>
        </view>
      </view>
      <!-- 三个卡片 -->
      <view class="card-section">
        <view class="combined-card">
          <view class="single-card" @click="goToPersonalCenter">
            <view>
              <view class="icon-circle">
                <uv-icon name="geren" custom-prefix="custom-icon" size="14" color="#fff" />
              </view>
              <text>个人中心</text>
            </view>

            <uni-icons type="right" class="card-icon-right"></uni-icons>
          </view>
          <view class="separator"></view>
          <view class="single-card" @click="goToFeedback">
            <view>
              <view class="icon-circle">
                <uv-icon name="yijianfankui" custom-prefix="custom-icon" size="12" color="#fff" />
              </view>
              <text>意见反馈</text>
            </view>
            <uni-icons type="right" class="card-icon-right"></uni-icons>
            <view class="feedback-btn">
              <uv-button open-type="feedback"></uv-button>
            </view>
          </view>
          <!-- <view class="single-card" @click="goToHelpCenter">
            <view>
              <view class="icon-circle">
                <uv-icon name="bangzhuwenti" custom-prefix="custom-icon" size="14" color="#fff" />
              </view>
              <text>帮助中心</text>
            </view>
            <uni-icons type="right" class="card-icon-right"></uni-icons>
          </view> -->
        </view>
        <!-- <view class="single-card feedback-card" @click="goToFeedback">
          <view>
            <view class="icon-circle">
              <uv-icon name="yijianfankui" custom-prefix="custom-icon" size="12" color="#fff" />
            </view>
            <text>意见反馈</text>
          </view>
          <uni-icons type="right" class="card-icon-right"></uni-icons>
          <view class="feedback-btn">
            <uv-button open-type="feedback"></uv-button>
          </view>
        </view> -->
      </view>
    </view>
    <!-- #ifdef MP-WEIXIN -->
    <view class="logout-wrap">
      <!-- 退出登录按钮 -->
      <view class="logout-btn" @click="logout">
        <uv-icon name="tuichu-" custom-prefix="custom-icon" size="18"></uv-icon>
        <text style="margin-left: 16rpx;">退出登录</text>
      </view>
    </view>
    <!-- #endif -->
    <uv-popup ref="popup" mode="bottom" :round="10" closeable>
      <view class="compangText">
        切换企业组织
      </view>
      <scroll-view scroll-y="true" class="company-list">
        <view v-for="(company, index) in companyList" :key="index"
          :class="['company-list-item', activeIndex === index ? 'active-item' : '']" @click=" changeCompany(company)">
          <image class="company-logo" :src="company.logo || '/static/logo.png'" />
          <text class="company-name">
            {{ company.companyName }} <!-- 假设公司名称字段为 companyName -->
          </text>
          <uv-icon v-if="activeIndex === index" name="checkmark" size="20" :color="themeColor['--primary-color']" />
        </view>
      </scroll-view>
    </uv-popup>
    <!-- 底部导航栏 -->
    <CustomTabBar />
  </view>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useUserStore } from '@/store/user';
import { useGlobalTheme } from '@/utils/theme';
import { getUserCompanyListApi } from '@/service/user';
import CustomTabBar from '@/components/custom-tab-bar/index.vue';
import OpenData from '@/components/open-data/index.vue'

const { themeColor } = useGlobalTheme();
const userStore = useUserStore();
const userInfo = computed(() => userStore.userInfo);
const userCompany = computed(() => userStore.userCompany);
const companyCount = ref(0);
const companyList = ref([]);
// 控制 uv-popup 的显示与隐藏
const popup = ref(null);
// 公司信息
const companyInfo = computed(() => userStore.userCompany);
// 当前选中的公司索引
const activeIndex = computed(() => {
  const findIndex = companyList.value.findIndex((item) => item.companyId === companyInfo.value?.companyId);
  return findIndex;
})
const logout = () => {
  uni.showModal({
    title: '确定退出登录吗',
    // content: '您确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.logout();
      } else if (res.cancel) {
      }
    }
  });
};

// 跳转到个人中心
const goToPersonalCenter = () => {
  uni.navigateTo({
    url: '/pages/personalCenter/personalCenter'
  });
};

// 跳转到帮助中心
const goToHelpCenter = () => {
  uni.navigateTo({
    url: '/pages/personalCenter/helpCenter'
  });
};

// 跳转到意见反馈
const goToFeedback = () => {
  console.log('跳转到意见反馈');
};

// 切换公司的方法
const openCompany = () => {
  popup.value.open();
};

const changeCompany = (company) => {
  popup.value.close();
  userStore.switchCompany(company.companyId)
};

onMounted(async () => {
  try {
    const response = await getUserCompanyListApi();
    if (response) {
      companyCount.value = response.length;
      companyList.value = response;
    }
  } catch (error) {
    console.error('获取公司列表失败:', error);
  }
});
</script>

<style scoped lang="scss">
.my-container {
  padding-bottom: calc(68px + env(safe-area-inset-bottom));
}

.user-info {
  height: 162rpx;
  display: flex;
  align-items: center;
  padding: 0 50rpx;
  background-color: #fff;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  margin-right: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;

  .avatarImg {
    width: 120rpx;
    height: 120rpx;
    border-radius: 50%;
    border: 1px solid #EEEFEF;
    box-sizing: border-box;
    padding: 20rpx;
  }
}

.user-details {
  flex: 1;
}

.user-name {
  height: 48rpx;
  line-height: 48rpx;
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 12rpx;
  color: #181818;
}

.user-company {
  max-width: 308rpx;
  width: 308rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 28rpx;
  color: #7B7B7B;
}

.card-section {
  padding: 20rpx;
}

.combined-card {
  background-color: #fff;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  overflow: hidden;
}

.single-card {
  position: relative;
  background-color: #fff;
  font-size: 34rpx;
  height: 56rpx;
  padding: 32rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;

  view {
    display: flex;
    gap: 8px;
  }

  .icon-circle {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    background-color: var(--primary-color);
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .feedback-btn {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 10;

    &::v-deep .uv-button {
      width: 710rpx;
      height: 100%;
      opacity: 0;
    }
  }
}

.card-icon-right {
  margin-left: auto;
  margin-right: 0;
}

.card-icon-left {
  margin-right: 20rpx;
}

.card-icon-right {
  margin-left: 20rpx;
}

.separator {
  height: 1rpx;
  background-color: #eee;
  margin: 0 30rpx;
}

.user-company-container {
  display: flex;
  align-items: center;
}

.switch-btn {
  margin-left: 20rpx;
  padding: 8rpx 16rpx;
  border: 1rpx solid var(--primary-color);
  border-radius: 8rpx;
  background-color: transparent;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8rpx;

  text {
    font-size: 24rpx;
    line-height: 36rpx;
  }
}

.switch-icon {
  margin-right: 8rpx;
  color: var(--primary-color);
}

.switch-text {
  color: var(--primary-color);
}

.logout-btn {
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 16rpx;
  padding: 24rpx 32rpx;
  background-color: #fff;
  margin-top: 100rpx;
  box-sizing: border-box;
}

.feedback-card {
  background-color: #fff;
  border-radius: 16rpx;
  margin-top: 20rpx;
}

.compangText {
  font-size: 17px;
  color: #000000;
  width: 100%;
  text-align: center;
  padding: 10px 0;
  font-weight: bold;
}

.company-list {
  padding: 32rpx;
  box-sizing: border-box;
  max-height: 50vh;

  .company-list-item {
    padding: 16rpx 32rpx;
    display: flex;
    align-items: center;
    justify-content: space-between;
    border-radius: 16rpx;
    margin-bottom: 24rpx;

    .company-logo {
      width: 80rpx;
      height: 80rpx;
      border-radius: 8rpx;
      background-color: #fff;
    }

    .company-name {
      flex: 1;
      font-size: 32rpx;
      line-height: 48rpx;
      margin: 0 24rpx;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    &:last-child {
      margin-bottom: 0;
    }
  }

  .active-item {
    background-color: #f5f6f8;
  }
}

.logout-wrap {
  position: fixed;
  bottom: calc(100px + env(safe-area-inset-bottom));
  ;
  width: 100%;
  left: 0;
  display: flex;
  justify-content: center;
}
</style>