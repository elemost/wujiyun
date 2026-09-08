import App from './App'
import uvUI from '@/uni_modules/uv-ui-tools'
import { createSSRApp } from 'vue'
import { createPinia } from 'pinia'
import pluginMethod from './plugin'
import GlobalLoading from '@/components/global-loading.vue'
import { generateCompanySignature, generateAppSignature } from '@/service/wxwork'
import { isWwEnv } from '@/utils/common'
import * as ww from '@wecom/jssdk'
import { useUserStore } from '@/store/user';

export function createApp() {
  const app = createSSRApp(App)
  const pinia = createPinia()

  const getConfigSignature = async () => {
    const res = await generateCompanySignature(window.location.href);
    return {
      timestamp: res?.timestamp,
      nonceStr: res?.noncestr,
      signature: res?.signature,
    };
  }

  const getAgentConfigSignature = async () => {
    const res = await generateAppSignature(window.location.href);
    return {
      timestamp: res?.timestamp,
      nonceStr: res?.noncestr,
      signature: res?.signature,
    };
  }

  app.use(pinia)
  app.use(uvUI); // 初始化uvUI
  app.use(pluginMethod); // 初始化全局方法 
  app.component('uv-icon', uvUI.uvIcon)
  app.component('GlobalLoading', GlobalLoading)
  app.mixin({
    async onLoad() {
      // #ifdef H5
      const userStore = useUserStore();
      const pullConfig = JSON.parse(userStore?.userCompany?.pullConfig || '{}');
      if (isWwEnv() && pullConfig?.corpId && pullConfig?.clientId) {
        const corpId = pullConfig?.corpId;
        const agentId = pullConfig?.clientId;
        try {
          ww.register({
            corpId: corpId,       // 必填，当前用户企业所属企业ID
            agentId: agentId,                  // 必填，当前应用的AgentID
            getConfigSignature,                // 必填，根据url生成企业签名的回调函数
            getAgentConfigSignature,            // 必填，根据url生成应用签名的回调函
            onConfigFail: (err) => {
              console.error('onConfigFail', err);
            },
            onAgentConfigFail: (err) => {
              console.error('onAgentConfigFail', err);
            },
            onAgentConfigSuccess: () => {
              console.log('初始化企业微信sdk成功');
            }
          })
          ww.initOpenData({
            success: (res) => {
              console.log('初始化成功');
              userStore.setInitOpenData(true);
            },
            fail: (err) => {
              console.error('初始化失败', err);
            }
          });
        } catch (error) {
          console.error('初始化企业微信sdk失败', error);
        }
      }
      // #endif
    }
  })

  return {
    app
  }
}