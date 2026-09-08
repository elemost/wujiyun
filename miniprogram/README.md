# 五极云移动端应用

## 项目简介
五极云是一个基于Vue和uni-app开发的多端应用项目。

## 技术栈
- 前端框架: Vue 3
- 状态管理: Pinia
- UI组件: uView UI (https://www.uvui.cn/guide/demo.html)
- 数学计算: math.js
- 工具库: lodash, crypto-js

## 项目结构
├── components/       # 公共组件
├── pages/            # 页面目录
├── store/            # 状态管理
├── utils/            # 工具函数
└── static/           # 静态资源

## 配置说明

### uni-app AppID
- **配置文件**: `manifest.json` 顶层 `appid` 字段
- **当前值**: `__UNI__F0A23ED`
- **说明**: uni-app 应用的唯一标识，在 HBuilderX 中创建项目时自动生成，用于云端打包等场景。

### 微信小程序 AppID
- **配置文件**: `manifest.json` → `mp-weixin` → `appid`
- **当前值**: `wx9302365095adc69d`
- **说明**: 微信小程序的应用标识，从[微信公众平台](https://mp.weixin.qq.com/)注册小程序后获取。如需发布到自有小程序账号，需修改为此处的值。

### 接口地址 baseUrl
- **配置文件**: `config/app.js` → `appConfig.baseUrl`
- **当前值**: `https://cloud.elemost.com/api`
- **说明**: 所有后端 API 请求的基础地址，项目中的网络请求会在此地址后拼接具体接口路径。部署到新环境时需同步修改。

### WebView 地址 webViewUrl
- **配置文件**: `config/app.js` → `appConfig.webViewUrl`
- **当前值**: `https://cloud.elemost.com`
- **说明**: 应用中 WebView 组件加载 H5 页面时使用的基础地址，与 baseUrl 使用同一个域名但不带 `/api` 后缀。

