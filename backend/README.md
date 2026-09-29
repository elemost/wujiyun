# lowcode 低代码平台

基于 Spring Boot 的多模块低代码平台后端，提供表单引擎、流程引擎（Flowable）、开放平台、插件体系与定时任务调度能力。

- 构建工具：Maven
- 语言：Java 8
- 启动模块：`lowcode_api`
- 默认端口：`9007`，接口前缀 `/lowcode/api/v1`

## 技术栈

| 类别 | 组件 | 版本 |
| --- | --- | --- |
| 基础框架 | Spring Boot | 2.7.17 |
| 持久层 | MyBatis-Plus | 3.5.3.2 |
| 数据库 | MySQL Connector/J | 8.0.27 |
| 连接池 | Druid / dynamic-datasource | 1.2.16 / 3.5.2 |
| 缓存 | Redis、MongoDB | Spring Boot Starter |
| 工作流 | Flowable | 6.7.2 |
| 安全 | Spring Security + jjwt | 0.9.1 |
| 对象存储 | MinIO SDK / 腾讯云 COS | 8.3.9 / 5.6.52 |
| 服务调用 | OpenFeign + OkHttp | 3.1.8 / 11.10 |
| 接口文档 | Springfox Swagger | 3.0.0 |
| 数据库版本管理 | Flyway | 7.15.0 |
| 工具库 | Hutool / FastJSON / Guava / Lombok / MapStruct | 5.8.10 / 1.2.83 / 31.1-android / 1.18.34 / 1.5.3.Final |
| 报表导出 | EasyExcel / jxls-poi / poi-tl | 3.3.1 / 2.10.0 / 1.10.0 |
| 微信生态 | weixin-java-mp / weixin-java-miniapp | 4.5.0 |

## 模块结构

```
lowcode
├── lowcode_common     公共基础库：工具类、常量、枚举、缓存、表达式引擎、权限
├── lowcode_framework  框架层：安全认证、过滤器、AOP、全局异常、请求上下文
├── lowcode_system     系统管理：组织、用户、企业、短信、Excel、第三方客户端
├── lowcode_systemapi  系统能力对外 API，供其他模块 Feign 调用
├── lowcode_service    核心业务：表单/数据/校验/组件/导出/缓存
├── lowcode_workflow   工作流引擎（Flowable）
├── lowcode_message    消息通知：站内信、邮件、模板
├── lowcode_wechat     微信集成：公众号、小程序
├── lowcode_quartz     定时任务调度（Quartz）
├── lowcode_open       开放接口与对外组件
├── lowcode_openplat   开放平台（可独立部署，openplat.jar）
├── lowcode_plugin     插件系统（可独立部署，plugin.jar）
├── lowcode_factory    模型解析、转换与调度
└── lowcode_api        启动聚合模块，打包可执行 jar
```

依赖方向大致为：

```
common ← system / systemapi / wechat / quartz / plugin / openplat
       ← service ← workflow / message
       ← open / factory
       ← api（聚合全部）
```

## 快速开始

### 环境要求

- JDK 1.8
- Maven 3.6+
- MySQL 8.x、Redis、MongoDB（副本集）、MinIO（或腾讯云 COS）

### 本地构建

```bash
# 全量编译并安装到本地仓库（跳过测试）
mvn clean install -DskipTests
```

### 启动

在 IDE 中运行 `lowcode_api/src/main/java/com/wuji/console/Application.java`，或：

```bash
java -Dspring.profiles.active=pro -jar lowcode_api/target/*.jar
```

启动后接口地址为 `http://localhost:9007/lowcode/api/v1`。

## 配置说明

主配置位于 `lowcode_api/src/main/resources/application.yml`，生产配置为 `application-pro.yml`（Docker 部署时使用 `docker/config/application-pro.yml`）。

需要配置的外部依赖：

- MySQL 数据源（主库与从库）
- Redis
- MongoDB 副本集
- MinIO 或腾讯云 COS 对象存储
- 微信公众平台 / 小程序、企业微信
- SSO OAuth 授权地址

> 安全提示：仓库中的生产配置示例包含明文口令，请改为通过环境变量或密钥管理服务注入；同时务必替换默认的 `token.secret` 并关闭 Druid 监控页的默认弱口令。

## 许可证

本项目采用 [Apache License 2.0](LICENSE) 开源协议。
