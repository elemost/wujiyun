# 五极云部署文档

**文档版本**：V1\.0\.0

**适用项目**：五极云开源社区版（AI\+aPaaS零代码企业级快速开发平台）

**部署方式**：Docker一键自动化部署、源码手动部署

**适配系统**：Ubuntu、CentOS、Debian 主流 Linux 服务器

---

## 一、项目简介

五极云是一款企业级 AI\+aPaaS 零代码快速开发平台，基于前后端分离架构，可通过可视化拖拽快速搭建 CRM、进销存、OA办公、流程审批、数据大屏、仓储管理等全场景企业业务系统。

平台支持**私有化内网部署、数据本地存储、多端协同**，适配中小企业、工厂、政企内部数字化场景，开源版仅供学习、源码研究、企业内部非商用使用。

### 技术栈

- **后端**：Java8\+、Spring Boot、Spring Security、MyBatis\-Plus、Redis、MySQL8\.0

- **前端**：React、Umi、TypeScript、Ant Design、可视化拖拽引擎

- **移动端**：微信原生小程序

- **部署环境**：Docker / Docker Compose、Linux

---

## 二、环境最低配置要求

### 2\.1 服务器配置

- CPU：4核及以上

- 内存：8G及以上

- 系统盘：40G及以上

- 操作系统：Ubuntu / CentOS / Debian（主流Linux发行版）

### 2\.2 软件环境（源码部署必备）

- JDK 1\.8\+

- MySQL 8\.0\+

- Redis 5\.0\+

- Maven 3\.8\+

- Node\.js 16\+

> **提示**：Docker一键部署无需手动配置以上环境，脚本自动安装所有依赖。
> 
> 

---

## 三、Docker 一键自动化部署（推荐）

支持全平台自动安装 Docker、Docker Compose、初始化数据库、启动全套服务，内置文件 SHA256 哈希校验，防止文件篡改，部署安全可靠。

### 3\.1 部署前置要求

- 必须使用 **root 超级管理员** 执行命令

- 服务器开放 8080 端口（可自行修改端口配置）

- 服务器可正常访问外网（用于拉取镜像、部署脚本）

### 3\.2 各系统部署命令

#### Ubuntu / 阿里云ECS

```Plain Text
curl -fsSL https://cloud.elemost.com/docker/ubuntu/install.sh | bash
```

#### CentOS / 华为云/腾讯云OpenCloud

```Plain Text
curl -fsSL https://cloud.elemost.com/docker/centos/install.sh | bash
```

#### Debian

```Plain Text
curl -fsSL https://cloud.elemost.com/docker/debian/install.sh | bash
```

### 3\.3 部署流程说明

1. 自动创建部署目录：`/wujiyun/server/panel`

2. 自动安装 curl/wget 下载工具

3. 下载 docker\-compose\.yml、初始化脚本、启停/卸载脚本

4. SHA256 哈希校验文件完整性，杜绝篡改风险

5. 自动安装 Docker、Docker Compose

6. 自动初始化数据库、启动全套前后端服务

### 3\.4 访问地址

部署完成后，浏览器访问：

```Plain Text
http://服务器IP:8080
```

---

## 四、Docker 服务管理命令

所有运维脚本统一存放目录：`/wujiyun/server/panel`

### 4\.1 重启服务

```Plain Text
cd /wujiyun/server/panel && ./restart.sh
```

### 4\.2 停止服务

```Plain Text
cd /wujiyun/server/panel && docker-compose down
```

### 4\.3 启动服务

```Plain Text
cd /wujiyun/server/panel && docker-compose up -d
```

### 4\.4 完全卸载

```Plain Text
cd /wujiyun/server/panel && ./uninstall.sh
```

> **注意**：卸载脚本会清空容器、镜像及部署目录数据，请提前备份重要业务数据！
> 
> 

---

## 五、源码手动部署（开发/二次开发使用）

### 5\.1 克隆项目源码

```Plain Text
git clone https://github.com/elemost/wujiyun.git
cd wujiyun
```

### 5\.2 项目目录说明

```Plain Text
wujiyun
├── backend          # Java SpringBoot 后端核心代码
├── frontend         # React PC前端管理页面
├── miniprogram      # 微信小程序移动端源码
├── sql              # 数据库初始化脚本
├── docs             # 项目部署、使用、接口文档
└── README.md        # 项目说明
```

### 5\.3 数据库初始化

1. 新建 MySQL 数据库（推荐编码：utf8mb4，排序规则：utf8mb4\_unicode\_ci）

2. 执行 `sql/` 目录下所有 \.sql 初始化脚本

3. 完成数据表、基础配置数据初始化

### 5\.4 后端部署

1. 进入后端目录：`cd backend`

2. 修改配置文件，配置数据库、Redis 连接信息

3. 编译打包：`mvn clean package -DskipTests`

4. 启动项目：`java -jar target/*.jar`

### 5\.5 前端部署

1. 进入前端目录：`cd frontend`

2. 安装依赖：`npm install`

3. 编译打包：`npm run build`

4. 将打包产物部署至 Nginx 静态目录

### 5\.6 微信小程序部署

1. 打开微信开发者工具

2. 导入 `miniprogram` 目录源码

3. 修改小程序后端接口请求地址为服务器公网IP

4. 编译预览/上传发布

---

## 六、接口文档访问

接口文档：

```Plain Text
https://help.elemost.com
```

---

## 七、常见问题 FAQ

### 7\.1 执行脚本提示 Permission denied

问题原因：脚本无执行权限

解决方案：

```Plain Text
chmod +x *.sh
```

### 7\.2 文件哈希校验失败

问题原因：部署文件下载不完整、被篡改、网络异常

解决方案：重新执行部署命令，切换稳定网络重试

### 7\.3 端口8080被占用

8080端口被占用, 则使用8081端口

### 7\.4 外网无法访问系统

解决方案：服务器安全组、防火墙放行 8080 端口

---

## 八、开源使用规范

本项目开源代码 **仅用于技术学习、源码研究、企业内部非商用私有化部署**。

严禁：源码倒卖、二次贴牌售卖、搭建SaaS商用服务、商业项目外包交付、盈利性二次开发。

如需商用授权、定制开发、专属运维、信创适配服务，请联系官方获取商业版本授权。

---

## 九、官方联系方式

官方网站：https://www.elemost.com

开源仓库：https://github.com/elemost/wujiyun

