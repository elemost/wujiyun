import { useUserStore } from '@/store/user';
import { useThemeStore } from '@/store/theme';
import { isWwEnv } from '@/utils/common';
const TokenKey = 'App-Token'

// 获取token
export function getToken() {
  return uni.getStorageSync(TokenKey)
}

// 设置token
export function setToken(token) {
  return uni.setStorageSync(TokenKey, token)
}

// 移除token
export function removeToken() {
  return uni.removeStorageSync(TokenKey)
}

// 获取添加到我的小程序弹窗是否已经出现过
export function getAddToMyMiniProgramPopup() {
  return uni.getStorageSync('addToMyMiniProgramPopup')
}

// 设置添加到我的小程序弹窗已经出现过
export function setAddToMyMiniProgramPopup() {
  return uni.setStorageSync('addToMyMiniProgramPopup', true)
}

// 清除登录用户信息
export function clearLoginInfo() {
  removeToken();
  const userStore = useUserStore();
  const themeStore = useThemeStore();
  userStore.clearUserInfo();
  themeStore.resetThemeColor();
}

// 退出登录
export function logout(redirectUrl, type='reLaunch') {
  removeToken();
  const userStore = useUserStore();
  const themeStore = useThemeStore();
  userStore.clearUserInfo();
  themeStore.resetThemeColor();
  if(isWwEnv()){
    window.location.replace(window.location.origin + '/smallProgram/companyWxLoginThree');
    return;
  }
  const loginUrl = '/pages/login/login'
  if(type === 'reLaunch'){
    uni.reLaunch({
      url: redirectUrl ? `${loginUrl}?redirectUrl=${encodeURIComponent(redirectUrl)}` : loginUrl
    })
  }else if(type === 'redirectTo'){
    uni.redirectTo({
      url: redirectUrl ? `${loginUrl}?redirectUrl=${encodeURIComponent(redirectUrl)}` : loginUrl
    })
  }else if(type === 'navigateTo'){
    uni.navigateTo({
      url: redirectUrl ? `${loginUrl}?redirectUrl=${encodeURIComponent(redirectUrl)}` : loginUrl
    })
  }
}

