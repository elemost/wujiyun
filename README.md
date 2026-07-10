# 五极云 · EleCloud（开源版）
## AI+aPaaS 企业级零代码开发平台

> ⚠️ **重要声明：本项目开源仅用于学习研究与企业内部非商用，严禁任何商业用途。**

---

### 📌 开源使用准则（必读）
✅ **允许**
- 阅读源码、学习架构、技术交流、个人研究
- 企业内网/私有化部署，仅限**内部办公、内部管理自用**
- 自由修改、二次开发，仅限学习与内部使用

❌ **严禁**
- 售卖、租赁、SaaS服务、贴牌、项目交付等一切商业行为
- 去除版权信息、二次分发商用、盈利性改造
- 嵌入商业系统、对外提供收费服务

**商用必须联系官方（杭州五极网络技术有限公司）获取书面授权。**

---

### 🌟 平台简介
**五极云（EleCloud）** 是杭州五极网络技术有限公司自研的 **AI+aPaaS 企业级零代码应用搭建平台**，定位**少写代码、快速上线、自主可控**，助力中小企业低成本完成数字化搭建。

支持可视化拖拽搭建各类管理系统，内置仓库WMS、客户CRM、进销存、流程审批、数据统计、入库出库、库存调拨、智能盘点等全套业务模块，开源版本主打技术学习、源码研读、本地私有化部署自用。

---

### 🛠 技术栈
**后端**：Java 8+ / Spring Boot / Spring Security / MyBatis-Plus / Redis / MySQL 8.0
**前端**：React / Ant Design / Umi / 可视化拖拽引擎 / TypeScript
**核心能力**：数据建模、表单设计、流程引擎、权限控制、数据大屏、API集成

---

### ✨ 核心功能
- 🧩 可视化拖拽设计器，零基础快速搭建业务系统
- 📊 数据模型自定义，自动生成表单与数据表
- 🔄 全流程业务管理：入库、出库、调拨、盘点、数据报表统计
- 🔐 多级角色权限管控，数据权限精准隔离
- 📈 可视化数据大屏，业务数据直观展示
- 🔗 支持第三方接口对接，实现多系统数据互通

---

### 📁 项目目录结构
wujiyun-open/
├── README.md
├── LICENSE
├── .gitignore
├── pom.xml
├── docs/ # 部署文档、使用教程
├── sql/ # 数据库初始化脚本
├── backend/ # Java 后端服务
│ ├── wujiyun-core # 零代码核心引擎
│ ├── wujiyun-admin # 后台管理服务
│ └── wujiyun-common # 公共工具依赖
└── frontend/ # React 前端页面
├── wujiyun-design # 可视化设计器
└── wujiyun-admin # 业务管理前端


---

# 🚀 一键快速安装（Windows / Linux / Mac）
## 1. Windows 一键部署
```shell
# 下载一键部署脚本
wget https://install.elemost.com/wujiyun/windows_install.bat
# 右键以管理员身份运行 windows_install.bat
# 自动配置环境、初始化数据库、启动服务

## 2. Linux 一键部署（CentOS / Ubuntu）
# 在线一键安装
curl -fsSL https://install.elemost.com/wujiyun/linux_install.sh | bash
# 启动命令
systemctl start wujiyun
# 访问地址：http://服务器IP:8080

## 3. MacOS 一键部署
# 终端执行一键安装
bash <(curl -s https://install.elemost.com/wujiyun/mac_install.sh)
# 启动项目
./start.sh

## 手动部署环境要求
JDK 1.8+
MySQL 8.0
Redis 5.0+
Node.js 16+
🤝 技术交流与贡献
欢迎 Star、Fork、提交 Issue 交流技术思路

## 🤝 技术交流与贡献
欢迎 Star、Fork、提交 Issue 交流技术思路
禁止提交任何商用改造、盈利化二次开发代码
📄 版权说明
Copyright © 2026 杭州五极网络技术有限公司
本仓库实际生效协议为自定义非商用协议，GitHub 展示 MIT 仅为占位，一切使用规则以仓库内 LICENSE 文件为准。

## 📞 官方对接
官方站点：https://www.elemost.com
商用授权、定制开发、私有化正式版均可联系官方洽谈
觉得好用欢迎点亮 Star ⭐ 支持开源学习！
禁止提交任何商用改造、盈利化二次开发代码
