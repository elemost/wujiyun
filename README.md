<p align="center">
  <img src="https://www.elemost.com/logo.png" alt="五极云 WujiYun" width="240"/>
</p>

<h3 align="center">五极云</h3>
<p align="center">AI + aPaaS 企业级零代码快速开发平台｜源码可用社区版</p>

<p align="center">
  <img src="https://img.shields.io/badge/Version-社区版-blue" alt="版本">
  <img src="https://img.shields.io/badge/License-源码可用-lightgrey" alt="许可协议">
  <img src="https://img.shields.io/badge/Support-CentOS%20%7C%20Ubuntu%20%7C%20Debian-green" alt="支持系统">
  <img src="https://img.shields.io/badge/Docker-一键部署-blue" alt="Docker">
  <img src="https://img.shields.io/github/stars/elemost/wujiyun?style=flat&label=Stars&color=yellow" alt="Stars">
  <img src="https://img.shields.io/github/forks/elemost/wujiyun?style=flat&label=Forks&color=green" alt="Forks">
  <img src="https://img.shields.io/github/issues/elemost/wujiyun?style=flat&label=Issues&color=orange" alt="Issues">
</p>

<p align="center">
我们致力于持续迭代产品，欢迎点亮⭐Star，您的鼓励是我们持续开发的动力！
</p>

<p align="center">
<a href="https://www.elemost.com">官方网站</a> ·
<a href="https://github.com/elemost/wujiyun">GitHub仓库</a> ·
<a href="./docs/部署文档.md">帮助文档</a> ·
<a href="./LICENSE">许可协议</a> ·
<a href="https://www.elemost.com">商业授权</a>
</p>

---

> **五极云** 基于 Java + React + UniApp 架构，源码可用的零代码 aPaaS 平台，面向企业、工厂、政企，可视化拖拽快速搭建内部业务系统，支持内网私有化部署，数据本地留存。
> 开箱即用，无需大量编码，快速实现 OA、CRM、进销存、流程审批、数据台账、业务管理系统。

> ⚠️ **社区版说明**：社区版源码可用，仅供个人学习与企业内部非商用；如需对外商业化使用，请获取官方商业授权。详见 [LICENSE](./LICENSE)。

## ✨ 核心亮点
- ✅ **私有化部署**：全部数据存储本地服务器，不上传第三方云端，满足内网安全、等保合规场景
- ✅ **Docker 一键部署**：一条命令完成整套环境安装，自动处理 MySQL / Redis / MongoDB，降低部署成本
- ✅ **零代码可视化开发**：表单、页面、菜单、视图拖拽搭建，业务人员也能开发业务系统
- ✅ **内置工作流引擎**：支持串行、并行、条件分支审批，适配企业各类审批流程
- ✅ **细粒度权限管控**：组织架构、角色、数据权限、按钮权限完整体系
- ✅ **多端支持**：PC 后台 + UniApp 移动端，适配微信小程序、H5
- ✅ **开放 API**：标准 RESTful 接口，支持与第三方系统对接
- ✅ **源码可用**：社区版开放源码，支持二次开发，Java 开发者可深度定制业务逻辑

## 🚀 快速开始｜一键部署
> 支持 CentOS / Ubuntu / Debian Linux 系统，推荐 x86_64 服务器

```bash
# CentOS / 腾讯云 OpenCloud
curl -fsSL https://cloud.elemost.com/docker/centos/install.sh | bash

# Ubuntu / 阿里云 AlibabaCloud
curl -fsSL https://cloud.elemost.com/docker/ubuntu/install.sh | bash

# Debian
curl -fsSL https://cloud.elemost.com/docker/debian/install.sh | bash
```

部署完成后访问：

```Plain Text
http://服务器IP:8080
```

> 💡 提示：一键脚本自动安装 Docker、数据库、中间件；如果服务器已安装 Docker，脚本会自动跳过 Docker 安装步骤。
> 
> 

## 📋 主要功能

1. 可视化表单、页面设计器

2. 企业工作流审批引擎

3. 组织架构与精细化权限管理

4. 数据视图、查询统计、数据大屏

5. 开放 API，支持第三方系统对接

6. UniApp 多端适配（微信小程序、H5）

7. 用户操作审计日志

8. **单租户架构**（社区版）

## 🎯 适用场景

- 企业内部 OA、人事行政、流程审批系统

- CRM 客户管理、销售跟进、客户台账

- 进销存、仓库物料、库存管理

- 工厂车间生产、设备台账、生产流程管理

- 政府、事业单位内部业务管理系统

- 需要私有化、数据不能出内网的业务场景

## 🛠 技术栈

**后端**

- Java 8\+ / SpringBoot

- MySQL 8\.0、Redis、MongoDB

- MyBatis\-Plus、工作流引擎

**前端**

- React \+ TypeScript

- Ant Design

**移动端**

- UniApp，支持编译为微信小程序、H5

**部署方式**

- Docker Compose 容器化一键部署

## 📦 版本说明

- **社区版**：源码可用，免费用于个人学习、企业内部非商用；提供基础零代码能力，**单租户架构**，支持私有化部署。

- **标准版 / 专业版 / 旗舰版**：商业授权版本，提供高级功能、MCP 智能体、信创适配、技术支持、实施服务、7×24 服务。

> 商业版详情，请访问官网：[https://www\.elemost\.com](https://www.elemost.com)
> 
> 

## 📂 项目目录说明

```Plain Text
wujiyun
├── backend          # Java 后端源码
├── frontend         # React 前端源码
├── uniapp           # UniApp 移动端源码（微信小程序、H5）
├── sql              # 数据库初始化脚本
├── docs             # 部署文档、使用文档、API 文档
└── README.md        # 项目说明
```

## 💡 参与贡献

欢迎提交 Issue 反馈 Bug、提交功能建议；

欢迎 Fork 项目，提交 PR 参与代码贡献。

## 📄 许可协议

社区版为**源码可用**版本，允许个人学习、企业内部非商用使用；

禁止未经授权对外商业化、二次分发与售卖。

商用请联系官方获取授权，详见 [LICENSE](./LICENSE)。

## 📞 官方联系方式

官方网站：[https://www\.elemost\.com](https://www.elemost.com)

开源仓库：[https://github\.com/elemost/wujiyun](https://github.com/elemost/wujiyun)


<table style="text-align: center;">
  <tr><td style="text-align:center;"><strong>技术专家（技术咨询、部署问题）</strong></td><td style="text-align:center;"><strong>售前专家（商务、授权、方案咨询）</strong></td></tr>
  <tr>
    <td style="text-align:center;"><img src="https://cos.elemost.com/elemost/Jackie-code.png" alt="技术专家" style="width:200px;height:200px;"/></td>
    <td style="text-align:center;"><img src="https://www.elemost.com/assets/Eric-five-CU89rZGn.png" alt="售前专家" style="width:200px;height:200px;"/></td>
  </tr>
</table>
  
  
    
