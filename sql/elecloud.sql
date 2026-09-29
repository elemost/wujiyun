USE mysql;
create user 'admin'@'%' identified by 'admin@123';
grant ALL on *.* to 'admin'@'%';

CREATE DATABASE IF NOT EXISTS `wuji_home` CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
USE `wuji_home`;

SET NAMES utf8mb4;

-- ----------------------------
-- Table structure for sys_company_pull_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_company_pull_config`;
CREATE TABLE `sys_company_pull_config`
(
    `id`            varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `app_id`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT '应用id',
    `pull_config`   varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '拉取配置',
    `company_id`    bigint                                                         DEFAULT NULL COMMENT '公司id',
    `config_type`   varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT '配置类型',
    `create_time`   datetime                                                       DEFAULT CURRENT_TIMESTAMP,
    `creator`       varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '创建人',
    `modify_time`   datetime                                                       DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`      varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '修改人',
    `deleted`       tinyint                                                        DEFAULT '0' COMMENT '删除',
    `source_app_id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT '来源应用id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`
(
    `config_id`    int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
    `config_name`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '参数名称',
    `config_key`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '参数键名',
    `config_value` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '参数键值',
    `config_type`  char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
    `create_by`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '创建者',
    `create_time`  datetime                                                      DEFAULT NULL COMMENT '创建时间',
    `update_by`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '更新者',
    `update_time`  datetime                                                      DEFAULT NULL COMMENT '更新时间',
    `remark`       varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`config_id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='参数配置表';

DROP TABLE IF EXISTS `wj_client_function`;
CREATE TABLE `wj_client_function`
(
    `client_id`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `permission_key`  varchar(64) COLLATE utf8mb4_general_ci                       DEFAULT NULL,
    `permission_name` varchar(64) COLLATE utf8mb4_general_ci                       DEFAULT NULL,
    `equity_id`       varchar(64) COLLATE utf8mb4_general_ci                       DEFAULT NULL COMMENT '权益id',
    `limit_count`     int                                                          DEFAULT NULL COMMENT '限制数量'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`
(
    `dept_id`           bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
    `parent_id`         bigint                                                        DEFAULT '0' COMMENT '父部门id',
    `company_id`        bigint                                                        DEFAULT NULL COMMENT '公司ID',
    `ancestors`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '祖级列表',
    `dept_name`         varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '部门名称',
    `order_num`         int                                                           DEFAULT '0' COMMENT '显示顺序',
    `leader`            varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '负责人',
    `phone`             varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '联系电话',
    `email`             varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '邮箱',
    `status`            char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
    `del_flag`          char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `create_by`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '创建者',
    `create_time`       datetime                                                      DEFAULT NULL COMMENT '创建时间',
    `update_by`         varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '更新者',
    `update_time`       datetime                                                      DEFAULT NULL COMMENT '更新时间',
    `dept_type`         varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   DEFAULT '00' COMMENT '组织架构类型',
    `third_id`          varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '三方id',
    `source_company_id` bigint                                                        DEFAULT NULL,
    PRIMARY KEY (`dept_id`) USING BTREE,
    KEY                 `idx_companyId` (`company_id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='部门表';

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`
(
    `dict_code`   bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
    `dict_sort`   int                                                           DEFAULT '0' COMMENT '字典排序',
    `dict_label`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典标签',
    `dict_value`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典键值',
    `dict_type`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典类型',
    `css_class`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
    `list_class`  varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '表格回显样式',
    `is_default`  char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
    `status`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `create_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '创建者',
    `create_time` datetime                                                      DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '更新者',
    `update_time` datetime                                                      DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典数据表';

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`
(
    `dict_id`     bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
    `dict_name`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典名称',
    `dict_type`   varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典类型',
    `status`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `create_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '创建者',
    `create_time` datetime                                                      DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '更新者',
    `update_time` datetime                                                      DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`dict_id`) USING BTREE,
    UNIQUE KEY `dict_type` (`dict_type`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典类型表';

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`
(
    `job_id`          bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `job_name`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT '' COMMENT '任务名称',
    `job_group`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
    `invoke_target`   varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标字符串',
    `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT '' COMMENT 'cron执行表达式',
    `misfire_policy`  varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci           DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
    `concurrent`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci               DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
    `status`          char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci               DEFAULT '0' COMMENT '状态（0正常 1暂停）',
    `create_by`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci           DEFAULT '' COMMENT '创建者',
    `create_time`     datetime                                                               DEFAULT NULL COMMENT '创建时间',
    `update_by`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci           DEFAULT '' COMMENT '更新者',
    `update_time`     datetime                                                               DEFAULT NULL COMMENT '更新时间',
    `remark`          varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT '' COMMENT '备注信息',
    PRIMARY KEY (`job_id`, `job_name`, `job_group`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='定时任务调度表';

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log`
(
    `job_log_id`     bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
    `job_name`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '任务名称',
    `job_group`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '任务组名',
    `invoke_target`  varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标字符串',
    `job_message`    varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '日志信息',
    `status`         char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci       DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
    `exception_info` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '异常信息',
    `create_time`    datetime                                                       DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='定时任务调度日志表';

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`
(
    `info_id`        bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
    `user_name`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '用户账号',
    `ipaddr`         varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '登录IP地址',
    `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '登录地点',
    `browser`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '浏览器类型',
    `os`             varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '操作系统',
    `status`         char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
    `msg`            varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '提示消息',
    `login_time`     datetime                                                      DEFAULT NULL COMMENT '访问时间',
    `company_id`     bigint                                                        DEFAULT NULL COMMENT '公司ID',
    `user_id`        bigint                                                        DEFAULT NULL COMMENT '用户ID',
    PRIMARY KEY (`info_id`) USING BTREE,
    KEY              `idx_sys_logininfor_s` (`status`) USING BTREE,
    KEY              `idx_sys_logininfor_lt` (`login_time`) USING BTREE,
    KEY              `idx_companyId` (`company_id`) USING BTREE,
    KEY              `idx_userId` (`user_id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统访问记录';

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`
(
    `menu_id`     bigint                                                       NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `menu_name`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
    `parent_id`   bigint                                                        DEFAULT '0' COMMENT '父菜单ID',
    `order_num`   int                                                           DEFAULT '0' COMMENT '显示顺序',
    `path`        varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '路由地址',
    `component`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组件路径',
    `query`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '路由参数',
    `is_frame`    int                                                           DEFAULT '1' COMMENT '是否为外链（0是 1否）',
    `is_cache`    int                                                           DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
    `menu_type`   char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
    `visible`     char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
    `status`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
    `perms`       varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '权限标识',
    `icon`        varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '#' COMMENT '菜单图标',
    `create_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '创建者',
    `create_time` datetime                                                      DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '更新者',
    `update_time` datetime                                                      DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜单权限表';

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`
(
    `notice_id`      int                                                          NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `notice_title`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告标题',
    `notice_type`    char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NOT NULL COMMENT '公告类型（1通知 2公告）',
    `notice_content` longblob COMMENT '公告内容',
    `status`         char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
    `create_by`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '创建者',
    `create_time`    datetime                                                      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '更新者',
    `update_time`    datetime                                                      DEFAULT NULL COMMENT '更新时间',
    `remark`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='通知公告表';

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`
(
    `oper_id`        bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
    `title`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   DEFAULT '' COMMENT '模块标题',
    `business_type`  int                                                            DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
    `method`         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '方法名称',
    `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   DEFAULT '' COMMENT '请求方式',
    `operator_type`  int                                                            DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
    `oper_name`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   DEFAULT '' COMMENT '操作人员',
    `dept_name`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   DEFAULT '' COMMENT '部门名称',
    `oper_url`       varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '请求URL',
    `oper_ip`        varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '主机地址',
    `oper_location`  varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '操作地点',
    `oper_param`     varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '请求参数',
    `json_result`    varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '返回参数',
    `status`         int                                                            DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
    `error_msg`      varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '错误消息',
    `oper_time`      datetime                                                       DEFAULT NULL COMMENT '操作时间',
    `cost_time`      bigint                                                         DEFAULT '0' COMMENT '消耗时间',
    `user_id`        bigint                                                         DEFAULT NULL COMMENT '用户ID',
    `company_id`     bigint                                                         DEFAULT NULL COMMENT '公司ID',
    PRIMARY KEY (`oper_id`) USING BTREE,
    KEY              `idx_sys_oper_log_bt` (`business_type`) USING BTREE,
    KEY              `idx_sys_oper_log_s` (`status`) USING BTREE,
    KEY              `idx_sys_oper_log_ot` (`oper_time`) USING BTREE,
    KEY              `idx_companyId` (`company_id`) USING BTREE,
    KEY              `idx_userId` (`user_id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志记录';

-- ----------------------------
-- Table structure for sys_oss
-- ----------------------------
DROP TABLE IF EXISTS `sys_oss`;
CREATE TABLE `sys_oss`
(
    `oss_id`        bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '对象存储主键',
    `file_name`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '文件名',
    `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '原名',
    `file_suffix`   varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL DEFAULT '' COMMENT '文件后缀名',
    `url`           varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'URL地址',
    `create_time`   datetime                                                               DEFAULT NULL COMMENT '创建时间',
    `create_by`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci           DEFAULT '' COMMENT '上传人',
    `update_time`   datetime                                                               DEFAULT NULL COMMENT '更新时间',
    `update_by`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci           DEFAULT '' COMMENT '更新人',
    `service`       varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NOT NULL DEFAULT 'minio' COMMENT '服务商',
    PRIMARY KEY (`oss_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='OSS对象存储表';

-- ----------------------------
-- Table structure for sys_oss_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_oss_config`;
CREATE TABLE `sys_oss_config`
(
    `oss_config_id` bigint                                                       NOT NULL AUTO_INCREMENT COMMENT '主建',
    `config_key`    varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '' COMMENT '配置key',
    `access_key`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         DEFAULT '' COMMENT 'accessKey',
    `secret_key`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         DEFAULT '' COMMENT '秘钥',
    `bucket_name`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         DEFAULT '' COMMENT '桶名称',
    `prefix`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         DEFAULT '' COMMENT '前缀',
    `endpoint`      varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         DEFAULT '' COMMENT '访问站点',
    `domain`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         DEFAULT '' COMMENT '自定义域名',
    `is_https`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci              DEFAULT 'N' COMMENT '是否https（Y=是,N=否）',
    `region`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         DEFAULT '' COMMENT '域',
    `access_policy` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci     NOT NULL DEFAULT '1' COMMENT '桶权限类型(0=private 1=public 2=custom)',
    `status`        char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci              DEFAULT '1' COMMENT '状态（0=正常,1=停用）',
    `ext1`          varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         DEFAULT '' COMMENT '扩展字段',
    `create_by`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci          DEFAULT '' COMMENT '创建者',
    `create_time`   datetime                                                              DEFAULT NULL COMMENT '创建时间',
    `update_by`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci          DEFAULT '' COMMENT '更新者',
    `update_time`   datetime                                                              DEFAULT NULL COMMENT '更新时间',
    `remark`        varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci         DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`oss_config_id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='对象存储配置表';

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`
(
    `post_id`     bigint                                                       NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
    `post_code`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位编码',
    `post_name`   varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位名称',
    `post_sort`   int                                                          NOT NULL COMMENT '显示顺序',
    `status`      char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci     NOT NULL COMMENT '状态（0正常 1停用）',
    `create_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '创建者',
    `create_time` datetime                                                      DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '更新者',
    `update_time` datetime                                                      DEFAULT NULL COMMENT '更新时间',
    `remark`      varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
    `del_flag`    char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '删除标记',
    `company_id`  bigint                                                        DEFAULT NULL COMMENT '公司id',
    PRIMARY KEY (`post_id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='岗位信息表';

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `role_id`             bigint                                                        NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name`           varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  NOT NULL COMMENT '角色名称',
    `role_key`            varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色权限字符串',
    `role_sort`           int                                                           NOT NULL COMMENT '显示顺序',
    `data_scope`          char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
    `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
    `dept_check_strictly` tinyint(1) DEFAULT '1' COMMENT '部门树选择项是否关联显示',
    `status`              char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      NOT NULL COMMENT '角色状态（0正常 1停用）',
    `del_flag`            char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `create_by`           varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '创建者',
    `create_time`         datetime                                                      DEFAULT NULL COMMENT '创建时间',
    `update_by`           varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '更新者',
    `update_time`         datetime                                                      DEFAULT NULL COMMENT '更新时间',
    `remark`              varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
    `company_id`          bigint                                                        DEFAULT NULL,
    `source`              varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL,
    PRIMARY KEY (`role_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色信息表';

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`
(
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `dept_id` bigint NOT NULL COMMENT '部门ID',
    PRIMARY KEY (`role_id`, `dept_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色和部门关联表';

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`
(
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `menu_id` bigint NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色和菜单关联表';

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `user_id`          bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `user_code`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '用户编码',
    `uuid`             varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '用户对外唯一值',
    `company_id`       bigint                                                        DEFAULT '0' COMMENT '企业id',
    `dept_id`          bigint                                                        DEFAULT NULL COMMENT '部门ID',
    `user_name`        varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户账号',
    `nick_name`        varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户昵称',
    `password`         varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '密码',
    `real_name`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '真实姓名',
    `user_type`        varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   DEFAULT '00' COMMENT '用户类型（00系统用户）',
    `email`            varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '用户邮箱',
    `phonenumber`      varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '手机号码',
    `sex`              char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
    `avatar`           varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '头像地址',
    `status`           char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
    `del_flag`         char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci      DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `login_ip`         varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '最后登录IP',
    `login_date`       datetime                                                      DEFAULT NULL COMMENT '最后登录时间',
    `create_by`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '创建者',
    `create_time`      datetime                                                      DEFAULT NULL COMMENT '创建时间',
    `update_by`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT '' COMMENT '更新者',
    `update_time`      datetime                                                      DEFAULT NULL COMMENT '更新时间',
    `remark`           varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
    `industry`         varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '行业',
    `source`           varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '来源',
    `position`         varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '职位',
    `screen_count`     int                                                           DEFAULT '10' COMMENT '大屏数量',
    `screen_use_count` int                                                           DEFAULT '0' COMMENT '已使用大屏数量',
    `demand`           varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '需求',
    `interests`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `ding_third_id`    varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '钉钉userid',
    PRIMARY KEY (`user_id`) USING BTREE,
    KEY                `idex_company_id` (`company_id`) USING BTREE,
    KEY                `idx_user_code` (`user_code`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户信息表';

-- ----------------------------
-- Table structure for sys_user_company
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_company`;
CREATE TABLE `sys_user_company`
(
    `id`                bigint NOT NULL AUTO_INCREMENT,
    `company_id`        bigint                                                   DEFAULT NULL COMMENT '公司id',
    `user_id`           bigint                                                   DEFAULT NULL COMMENT '用户id',
    `ding_third_id`     varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin    DEFAULT NULL COMMENT '钉钉三方id',
    `ding_union_id`     varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin    DEFAULT NULL COMMENT '钉钉unionid',
    `status`            char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
    `del_flag`          char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
    `create_time`       datetime                                                 DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `lark_union_id`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin    DEFAULT NULL COMMENT '飞书unionid',
    `lark_user_id`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin    DEFAULT NULL COMMENT '飞书userid',
    `lark_open_id`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin    DEFAULT NULL COMMENT '飞书openid',
    `nick_name`         varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '昵称',
    `email`             varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin    DEFAULT NULL COMMENT '邮箱',
    `user_type`         varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin     DEFAULT '00' COMMENT '00 内部员工 10 外部用户',
    `invite_code`       varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin    DEFAULT NULL COMMENT '邀请码',
    `entry_time`        datetime                                                 DEFAULT NULL COMMENT '入职时间',
    `report_leader`     varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin    DEFAULT NULL COMMENT '汇报对象',
    `third_id`          varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin    DEFAULT NULL COMMENT '三方id',
    `third_type`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin    DEFAULT NULL COMMENT '三方类型',
    `we_com_user_id`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin    DEFAULT NULL COMMENT '企业微信userId',
    `admin_user`        tinyint(1) DEFAULT '0' COMMENT 'admin用户',
    `avatar`            varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '头像',
    `source_company_id` bigint                                                   DEFAULT NULL,
    `user_company_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin    DEFAULT NULL,
    `job_number`        varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin    DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


CREATE TABLE `sys_sso_login`
(
    `id`          bigint NOT NULL AUTO_INCREMENT,
    `company_id`  int                                                          DEFAULT NULL,
    `secret`      varchar(255) COLLATE utf8mb4_bin                             DEFAULT NULL,
    `client_id`   varchar(0) COLLATE utf8mb4_bin                               DEFAULT NULL,
    `log_out_url` varchar(255) COLLATE utf8mb4_bin                             DEFAULT NULL COMMENT '退出登陆地址',
    `create_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
    `create_time` datetime                                                     DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
    `update_time` datetime                                                     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for sys_user_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_dept`;
CREATE TABLE `sys_user_dept`
(
    `user_id`    bigint DEFAULT NULL COMMENT '用户id',
    `dept_id`    bigint DEFAULT NULL COMMENT '部门id',
    `company_id` bigint DEFAULT NULL COMMENT '公司id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ----------------------------
-- Table structure for sys_user_info
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_info`;
CREATE TABLE `sys_user_info`
(
    `id`         bigint NOT NULL AUTO_INCREMENT,
    `user_id`    bigint                                                 DEFAULT NULL,
    `info_key`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  DEFAULT NULL,
    `info_value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
    `company_id` bigint                                                 DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post`
(
    `user_id`    bigint NOT NULL COMMENT '用户ID',
    `post_id`    bigint NOT NULL COMMENT '岗位ID',
    `company_id` bigint DEFAULT NULL COMMENT '公司id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户与岗位关联表';

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`
(
    `user_id`    bigint NOT NULL COMMENT '用户ID',
    `role_id`    bigint NOT NULL COMMENT '角色ID',
    `company_id` bigint NOT NULL COMMENT '公司id',
    PRIMARY KEY (`user_id`, `role_id`, `company_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户和角色关联表';

DROP TABLE IF EXISTS `wj_city`;
CREATE TABLE `wj_city`
(
    `id`        int NOT NULL AUTO_INCREMENT,
    `pid`       int                                                   DEFAULT NULL,
    `code`      int                                                   DEFAULT NULL COMMENT '编号',
    `city_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
    `type`      int                                                   DEFAULT '0' COMMENT '等级',
    PRIMARY KEY (`id`) USING BTREE,
    KEY         `pid_index` (`pid`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `wj_city` VALUES (1, 0, 110000, '北京市', 1);
INSERT INTO `wj_city` VALUES (2, 110000, 110100, '北京市', 2);
INSERT INTO `wj_city` VALUES (3, 110100, 110101, '东城区', 3);
INSERT INTO `wj_city` VALUES (4, 110100, 110102, '西城区', 3);
INSERT INTO `wj_city` VALUES (5, 110100, 110105, '朝阳区', 3);
INSERT INTO `wj_city` VALUES (6, 110100, 110106, '丰台区', 3);
INSERT INTO `wj_city` VALUES (7, 110100, 110107, '石景山区', 3);
INSERT INTO `wj_city` VALUES (8, 110100, 110108, '海淀区', 3);
INSERT INTO `wj_city` VALUES (9, 110100, 110109, '门头沟区', 3);
INSERT INTO `wj_city` VALUES (10, 110100, 110111, '房山区', 3);
INSERT INTO `wj_city` VALUES (11, 110100, 110112, '通州区', 3);
INSERT INTO `wj_city` VALUES (12, 110100, 110113, '顺义区', 3);
INSERT INTO `wj_city` VALUES (13, 110100, 110114, '昌平区', 3);
INSERT INTO `wj_city` VALUES (14, 110100, 110115, '大兴区', 3);
INSERT INTO `wj_city` VALUES (15, 110100, 110116, '怀柔区', 3);
INSERT INTO `wj_city` VALUES (16, 110100, 110117, '平谷区', 3);
INSERT INTO `wj_city` VALUES (17, 110100, 110118, '密云区', 3);
INSERT INTO `wj_city` VALUES (18, 110100, 110119, '延庆区', 3);
INSERT INTO `wj_city` VALUES (19, 0, 120000, '天津市', 1);
INSERT INTO `wj_city` VALUES (20, 120000, 120100, '天津市', 2);
INSERT INTO `wj_city` VALUES (21, 120100, 120101, '和平区', 3);
INSERT INTO `wj_city` VALUES (22, 120100, 120102, '河东区', 3);
INSERT INTO `wj_city` VALUES (23, 120100, 120103, '河西区', 3);
INSERT INTO `wj_city` VALUES (24, 120100, 120104, '南开区', 3);
INSERT INTO `wj_city` VALUES (25, 120100, 120105, '河北区', 3);
INSERT INTO `wj_city` VALUES (26, 120100, 120106, '红桥区', 3);
INSERT INTO `wj_city` VALUES (27, 120100, 120110, '东丽区', 3);
INSERT INTO `wj_city` VALUES (28, 120100, 120111, '西青区', 3);
INSERT INTO `wj_city` VALUES (29, 120100, 120112, '津南区', 3);
INSERT INTO `wj_city` VALUES (30, 120100, 120113, '北辰区', 3);
INSERT INTO `wj_city` VALUES (31, 120100, 120114, '武清区', 3);
INSERT INTO `wj_city` VALUES (32, 120100, 120115, '宝坻区', 3);
INSERT INTO `wj_city` VALUES (33, 120100, 120116, '滨海新区', 3);
INSERT INTO `wj_city` VALUES (34, 120100, 120117, '宁河区', 3);
INSERT INTO `wj_city` VALUES (35, 120100, 120118, '静海区', 3);
INSERT INTO `wj_city` VALUES (36, 120100, 120119, '蓟州区', 3);
INSERT INTO `wj_city` VALUES (37, 0, 130000, '河北省', 1);
INSERT INTO `wj_city` VALUES (38, 130000, 130100, '石家庄市', 2);
INSERT INTO `wj_city` VALUES (39, 130100, 130102, '长安区', 3);
INSERT INTO `wj_city` VALUES (40, 130100, 130104, '桥西区', 3);
INSERT INTO `wj_city` VALUES (41, 130100, 130105, '新华区', 3);
INSERT INTO `wj_city` VALUES (42, 130100, 130107, '井陉矿区', 3);
INSERT INTO `wj_city` VALUES (43, 130100, 130108, '裕华区', 3);
INSERT INTO `wj_city` VALUES (44, 130100, 130109, '藁城区', 3);
INSERT INTO `wj_city` VALUES (45, 130100, 130110, '鹿泉区', 3);
INSERT INTO `wj_city` VALUES (46, 130100, 130111, '栾城区', 3);
INSERT INTO `wj_city` VALUES (47, 130100, 130121, '井陉县', 3);
INSERT INTO `wj_city` VALUES (48, 130100, 130123, '正定县', 3);
INSERT INTO `wj_city` VALUES (49, 130100, 130125, '行唐县', 3);
INSERT INTO `wj_city` VALUES (50, 130100, 130126, '灵寿县', 3);
INSERT INTO `wj_city` VALUES (51, 130100, 130127, '高邑县', 3);
INSERT INTO `wj_city` VALUES (52, 130100, 130128, '深泽县', 3);
INSERT INTO `wj_city` VALUES (53, 130100, 130129, '赞皇县', 3);
INSERT INTO `wj_city` VALUES (54, 130100, 130130, '无极县', 3);
INSERT INTO `wj_city` VALUES (55, 130100, 130131, '平山县', 3);
INSERT INTO `wj_city` VALUES (56, 130100, 130132, '元氏县', 3);
INSERT INTO `wj_city` VALUES (57, 130100, 130133, '赵县', 3);
INSERT INTO `wj_city` VALUES (58, 130100, 130171, '石家庄高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (59, 130100, 130172, '石家庄循环化工园区', 3);
INSERT INTO `wj_city` VALUES (60, 130100, 130181, '辛集市', 3);
INSERT INTO `wj_city` VALUES (61, 130100, 130183, '晋州市', 3);
INSERT INTO `wj_city` VALUES (62, 130100, 130184, '新乐市', 3);
INSERT INTO `wj_city` VALUES (63, 130000, 130200, '唐山市', 2);
INSERT INTO `wj_city` VALUES (64, 130200, 130202, '路南区', 3);
INSERT INTO `wj_city` VALUES (65, 130200, 130203, '路北区', 3);
INSERT INTO `wj_city` VALUES (66, 130200, 130204, '古冶区', 3);
INSERT INTO `wj_city` VALUES (67, 130200, 130205, '开平区', 3);
INSERT INTO `wj_city` VALUES (68, 130200, 130207, '丰南区', 3);
INSERT INTO `wj_city` VALUES (69, 130200, 130208, '丰润区', 3);
INSERT INTO `wj_city` VALUES (70, 130200, 130209, '曹妃甸区', 3);
INSERT INTO `wj_city` VALUES (71, 130200, 130224, '滦南县', 3);
INSERT INTO `wj_city` VALUES (72, 130200, 130225, '乐亭县', 3);
INSERT INTO `wj_city` VALUES (73, 130200, 130227, '迁西县', 3);
INSERT INTO `wj_city` VALUES (74, 130200, 130229, '玉田县', 3);
INSERT INTO `wj_city` VALUES (75, 130200, 130271, '河北唐山芦台经济开发区', 3);
INSERT INTO `wj_city` VALUES (76, 130200, 130272, '唐山市汉沽管理区', 3);
INSERT INTO `wj_city` VALUES (77, 130200, 130273, '唐山高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (78, 130200, 130274, '河北唐山海港经济开发区', 3);
INSERT INTO `wj_city` VALUES (79, 130200, 130281, '遵化市', 3);
INSERT INTO `wj_city` VALUES (80, 130200, 130283, '迁安市', 3);
INSERT INTO `wj_city` VALUES (81, 130200, 130284, '滦州市', 3);
INSERT INTO `wj_city` VALUES (82, 130000, 130300, '秦皇岛市', 2);
INSERT INTO `wj_city` VALUES (83, 130300, 130302, '海港区', 3);
INSERT INTO `wj_city` VALUES (84, 130300, 130303, '山海关区', 3);
INSERT INTO `wj_city` VALUES (85, 130300, 130304, '北戴河区', 3);
INSERT INTO `wj_city` VALUES (86, 130300, 130306, '抚宁区', 3);
INSERT INTO `wj_city` VALUES (87, 130300, 130321, '青龙满族自治县', 3);
INSERT INTO `wj_city` VALUES (88, 130300, 130322, '昌黎县', 3);
INSERT INTO `wj_city` VALUES (89, 130300, 130324, '卢龙县', 3);
INSERT INTO `wj_city` VALUES (90, 130300, 130371, '秦皇岛市经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (91, 130300, 130372, '北戴河新区', 3);
INSERT INTO `wj_city` VALUES (92, 130000, 130400, '邯郸市', 2);
INSERT INTO `wj_city` VALUES (93, 130400, 130402, '邯山区', 3);
INSERT INTO `wj_city` VALUES (94, 130400, 130403, '丛台区', 3);
INSERT INTO `wj_city` VALUES (95, 130400, 130404, '复兴区', 3);
INSERT INTO `wj_city` VALUES (96, 130400, 130406, '峰峰矿区', 3);
INSERT INTO `wj_city` VALUES (97, 130400, 130407, '肥乡区', 3);
INSERT INTO `wj_city` VALUES (98, 130400, 130408, '永年区', 3);
INSERT INTO `wj_city` VALUES (99, 130400, 130423, '临漳县', 3);
INSERT INTO `wj_city` VALUES (100, 130400, 130424, '成安县', 3);
INSERT INTO `wj_city` VALUES (101, 130400, 130425, '大名县', 3);
INSERT INTO `wj_city` VALUES (102, 130400, 130426, '涉县', 3);
INSERT INTO `wj_city` VALUES (103, 130400, 130427, '磁县', 3);
INSERT INTO `wj_city` VALUES (104, 130400, 130430, '邱县', 3);
INSERT INTO `wj_city` VALUES (105, 130400, 130431, '鸡泽县', 3);
INSERT INTO `wj_city` VALUES (106, 130400, 130432, '广平县', 3);
INSERT INTO `wj_city` VALUES (107, 130400, 130433, '馆陶县', 3);
INSERT INTO `wj_city` VALUES (108, 130400, 130434, '魏县', 3);
INSERT INTO `wj_city` VALUES (109, 130400, 130435, '曲周县', 3);
INSERT INTO `wj_city` VALUES (110, 130400, 130471, '邯郸经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (111, 130400, 130473, '邯郸冀南新区', 3);
INSERT INTO `wj_city` VALUES (112, 130400, 130481, '武安市', 3);
INSERT INTO `wj_city` VALUES (113, 130000, 130500, '邢台市', 2);
INSERT INTO `wj_city` VALUES (114, 130500, 130502, '襄都区', 3);
INSERT INTO `wj_city` VALUES (115, 130500, 130503, '信都区', 3);
INSERT INTO `wj_city` VALUES (116, 130500, 130505, '任泽区', 3);
INSERT INTO `wj_city` VALUES (117, 130500, 130506, '南和区', 3);
INSERT INTO `wj_city` VALUES (118, 130500, 130522, '临城县', 3);
INSERT INTO `wj_city` VALUES (119, 130500, 130523, '内丘县', 3);
INSERT INTO `wj_city` VALUES (120, 130500, 130524, '柏乡县', 3);
INSERT INTO `wj_city` VALUES (121, 130500, 130525, '隆尧县', 3);
INSERT INTO `wj_city` VALUES (122, 130500, 130528, '宁晋县', 3);
INSERT INTO `wj_city` VALUES (123, 130500, 130529, '巨鹿县', 3);
INSERT INTO `wj_city` VALUES (124, 130500, 130530, '新河县', 3);
INSERT INTO `wj_city` VALUES (125, 130500, 130531, '广宗县', 3);
INSERT INTO `wj_city` VALUES (126, 130500, 130532, '平乡县', 3);
INSERT INTO `wj_city` VALUES (127, 130500, 130533, '威县', 3);
INSERT INTO `wj_city` VALUES (128, 130500, 130534, '清河县', 3);
INSERT INTO `wj_city` VALUES (129, 130500, 130535, '临西县', 3);
INSERT INTO `wj_city` VALUES (130, 130500, 130571, '河北邢台经济开发区', 3);
INSERT INTO `wj_city` VALUES (131, 130500, 130581, '南宫市', 3);
INSERT INTO `wj_city` VALUES (132, 130500, 130582, '沙河市', 3);
INSERT INTO `wj_city` VALUES (133, 130000, 130600, '保定市', 2);
INSERT INTO `wj_city` VALUES (134, 130600, 130602, '竞秀区', 3);
INSERT INTO `wj_city` VALUES (135, 130600, 130606, '莲池区', 3);
INSERT INTO `wj_city` VALUES (136, 130600, 130607, '满城区', 3);
INSERT INTO `wj_city` VALUES (137, 130600, 130608, '清苑区', 3);
INSERT INTO `wj_city` VALUES (138, 130600, 130609, '徐水区', 3);
INSERT INTO `wj_city` VALUES (139, 130600, 130623, '涞水县', 3);
INSERT INTO `wj_city` VALUES (140, 130600, 130624, '阜平县', 3);
INSERT INTO `wj_city` VALUES (141, 130600, 130626, '定兴县', 3);
INSERT INTO `wj_city` VALUES (142, 130600, 130627, '唐县', 3);
INSERT INTO `wj_city` VALUES (143, 130600, 130628, '高阳县', 3);
INSERT INTO `wj_city` VALUES (144, 130600, 130629, '容城县', 3);
INSERT INTO `wj_city` VALUES (145, 130600, 130630, '涞源县', 3);
INSERT INTO `wj_city` VALUES (146, 130600, 130631, '望都县', 3);
INSERT INTO `wj_city` VALUES (147, 130600, 130632, '安新县', 3);
INSERT INTO `wj_city` VALUES (148, 130600, 130633, '易县', 3);
INSERT INTO `wj_city` VALUES (149, 130600, 130634, '曲阳县', 3);
INSERT INTO `wj_city` VALUES (150, 130600, 130635, '蠡县', 3);
INSERT INTO `wj_city` VALUES (151, 130600, 130636, '顺平县', 3);
INSERT INTO `wj_city` VALUES (152, 130600, 130637, '博野县', 3);
INSERT INTO `wj_city` VALUES (153, 130600, 130638, '雄县', 3);
INSERT INTO `wj_city` VALUES (154, 130600, 130671, '保定高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (155, 130600, 130672, '保定白沟新城', 3);
INSERT INTO `wj_city` VALUES (156, 130600, 130681, '涿州市', 3);
INSERT INTO `wj_city` VALUES (157, 130600, 130682, '定州市', 3);
INSERT INTO `wj_city` VALUES (158, 130600, 130683, '安国市', 3);
INSERT INTO `wj_city` VALUES (159, 130600, 130684, '高碑店市', 3);
INSERT INTO `wj_city` VALUES (160, 130000, 130700, '张家口市', 2);
INSERT INTO `wj_city` VALUES (161, 130700, 130702, '桥东区', 3);
INSERT INTO `wj_city` VALUES (162, 130700, 130703, '桥西区', 3);
INSERT INTO `wj_city` VALUES (163, 130700, 130705, '宣化区', 3);
INSERT INTO `wj_city` VALUES (164, 130700, 130706, '下花园区', 3);
INSERT INTO `wj_city` VALUES (165, 130700, 130708, '万全区', 3);
INSERT INTO `wj_city` VALUES (166, 130700, 130709, '崇礼区', 3);
INSERT INTO `wj_city` VALUES (167, 130700, 130722, '张北县', 3);
INSERT INTO `wj_city` VALUES (168, 130700, 130723, '康保县', 3);
INSERT INTO `wj_city` VALUES (169, 130700, 130724, '沽源县', 3);
INSERT INTO `wj_city` VALUES (170, 130700, 130725, '尚义县', 3);
INSERT INTO `wj_city` VALUES (171, 130700, 130726, '蔚县', 3);
INSERT INTO `wj_city` VALUES (172, 130700, 130727, '阳原县', 3);
INSERT INTO `wj_city` VALUES (173, 130700, 130728, '怀安县', 3);
INSERT INTO `wj_city` VALUES (174, 130700, 130730, '怀来县', 3);
INSERT INTO `wj_city` VALUES (175, 130700, 130731, '涿鹿县', 3);
INSERT INTO `wj_city` VALUES (176, 130700, 130732, '赤城县', 3);
INSERT INTO `wj_city` VALUES (177, 130700, 130771, '张家口经济开发区', 3);
INSERT INTO `wj_city` VALUES (178, 130700, 130772, '张家口市察北管理区', 3);
INSERT INTO `wj_city` VALUES (179, 130700, 130773, '张家口市塞北管理区', 3);
INSERT INTO `wj_city` VALUES (180, 130000, 130800, '承德市', 2);
INSERT INTO `wj_city` VALUES (181, 130800, 130802, '双桥区', 3);
INSERT INTO `wj_city` VALUES (182, 130800, 130803, '双滦区', 3);
INSERT INTO `wj_city` VALUES (183, 130800, 130804, '鹰手营子矿区', 3);
INSERT INTO `wj_city` VALUES (184, 130800, 130821, '承德县', 3);
INSERT INTO `wj_city` VALUES (185, 130800, 130822, '兴隆县', 3);
INSERT INTO `wj_city` VALUES (186, 130800, 130824, '滦平县', 3);
INSERT INTO `wj_city` VALUES (187, 130800, 130825, '隆化县', 3);
INSERT INTO `wj_city` VALUES (188, 130800, 130826, '丰宁满族自治县', 3);
INSERT INTO `wj_city` VALUES (189, 130800, 130827, '宽城满族自治县', 3);
INSERT INTO `wj_city` VALUES (190, 130800, 130828, '围场满族蒙古族自治县', 3);
INSERT INTO `wj_city` VALUES (191, 130800, 130871, '承德高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (192, 130800, 130881, '平泉市', 3);
INSERT INTO `wj_city` VALUES (193, 130000, 130900, '沧州市', 2);
INSERT INTO `wj_city` VALUES (194, 130900, 130902, '新华区', 3);
INSERT INTO `wj_city` VALUES (195, 130900, 130903, '运河区', 3);
INSERT INTO `wj_city` VALUES (196, 130900, 130921, '沧县', 3);
INSERT INTO `wj_city` VALUES (197, 130900, 130922, '青县', 3);
INSERT INTO `wj_city` VALUES (198, 130900, 130923, '东光县', 3);
INSERT INTO `wj_city` VALUES (199, 130900, 130924, '海兴县', 3);
INSERT INTO `wj_city` VALUES (200, 130900, 130925, '盐山县', 3);
INSERT INTO `wj_city` VALUES (201, 130900, 130926, '肃宁县', 3);
INSERT INTO `wj_city` VALUES (202, 130900, 130927, '南皮县', 3);
INSERT INTO `wj_city` VALUES (203, 130900, 130928, '吴桥县', 3);
INSERT INTO `wj_city` VALUES (204, 130900, 130929, '献县', 3);
INSERT INTO `wj_city` VALUES (205, 130900, 130930, '孟村回族自治县', 3);
INSERT INTO `wj_city` VALUES (206, 130900, 130971, '河北沧州经济开发区', 3);
INSERT INTO `wj_city` VALUES (207, 130900, 130972, '沧州高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (208, 130900, 130973, '沧州渤海新区', 3);
INSERT INTO `wj_city` VALUES (209, 130900, 130981, '泊头市', 3);
INSERT INTO `wj_city` VALUES (210, 130900, 130982, '任丘市', 3);
INSERT INTO `wj_city` VALUES (211, 130900, 130983, '黄骅市', 3);
INSERT INTO `wj_city` VALUES (212, 130900, 130984, '河间市', 3);
INSERT INTO `wj_city` VALUES (213, 130000, 131000, '廊坊市', 2);
INSERT INTO `wj_city` VALUES (214, 131000, 131002, '安次区', 3);
INSERT INTO `wj_city` VALUES (215, 131000, 131003, '广阳区', 3);
INSERT INTO `wj_city` VALUES (216, 131000, 131022, '固安县', 3);
INSERT INTO `wj_city` VALUES (217, 131000, 131023, '永清县', 3);
INSERT INTO `wj_city` VALUES (218, 131000, 131024, '香河县', 3);
INSERT INTO `wj_city` VALUES (219, 131000, 131025, '大城县', 3);
INSERT INTO `wj_city` VALUES (220, 131000, 131026, '文安县', 3);
INSERT INTO `wj_city` VALUES (221, 131000, 131028, '大厂回族自治县', 3);
INSERT INTO `wj_city` VALUES (222, 131000, 131071, '廊坊经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (223, 131000, 131081, '霸州市', 3);
INSERT INTO `wj_city` VALUES (224, 131000, 131082, '三河市', 3);
INSERT INTO `wj_city` VALUES (225, 130000, 131100, '衡水市', 2);
INSERT INTO `wj_city` VALUES (226, 131100, 131102, '桃城区', 3);
INSERT INTO `wj_city` VALUES (227, 131100, 131103, '冀州区', 3);
INSERT INTO `wj_city` VALUES (228, 131100, 131121, '枣强县', 3);
INSERT INTO `wj_city` VALUES (229, 131100, 131122, '武邑县', 3);
INSERT INTO `wj_city` VALUES (230, 131100, 131123, '武强县', 3);
INSERT INTO `wj_city` VALUES (231, 131100, 131124, '饶阳县', 3);
INSERT INTO `wj_city` VALUES (232, 131100, 131125, '安平县', 3);
INSERT INTO `wj_city` VALUES (233, 131100, 131126, '故城县', 3);
INSERT INTO `wj_city` VALUES (234, 131100, 131127, '景县', 3);
INSERT INTO `wj_city` VALUES (235, 131100, 131128, '阜城县', 3);
INSERT INTO `wj_city` VALUES (236, 131100, 131171, '河北衡水高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (237, 131100, 131172, '衡水滨湖新区', 3);
INSERT INTO `wj_city` VALUES (238, 131100, 131182, '深州市', 3);
INSERT INTO `wj_city` VALUES (239, 130000, 133100, '雄安新区', 2);
INSERT INTO `wj_city` VALUES (241, 0, 140000, '山西省', 1);
INSERT INTO `wj_city` VALUES (242, 140000, 140100, '太原市', 2);
INSERT INTO `wj_city` VALUES (243, 140100, 140105, '小店区', 3);
INSERT INTO `wj_city` VALUES (244, 140100, 140106, '迎泽区', 3);
INSERT INTO `wj_city` VALUES (245, 140100, 140107, '杏花岭区', 3);
INSERT INTO `wj_city` VALUES (246, 140100, 140108, '尖草坪区', 3);
INSERT INTO `wj_city` VALUES (247, 140100, 140109, '万柏林区', 3);
INSERT INTO `wj_city` VALUES (248, 140100, 140110, '晋源区', 3);
INSERT INTO `wj_city` VALUES (249, 140100, 140121, '清徐县', 3);
INSERT INTO `wj_city` VALUES (250, 140100, 140122, '阳曲县', 3);
INSERT INTO `wj_city` VALUES (251, 140100, 140123, '娄烦县', 3);
INSERT INTO `wj_city` VALUES (252, 140100, 140171, '山西转型综合改革示范区', 3);
INSERT INTO `wj_city` VALUES (253, 140100, 140181, '古交市', 3);
INSERT INTO `wj_city` VALUES (254, 140000, 140200, '大同市', 2);
INSERT INTO `wj_city` VALUES (255, 140200, 140212, '新荣区', 3);
INSERT INTO `wj_city` VALUES (256, 140200, 140213, '平城区', 3);
INSERT INTO `wj_city` VALUES (257, 140200, 140214, '云冈区', 3);
INSERT INTO `wj_city` VALUES (258, 140200, 140215, '云州区', 3);
INSERT INTO `wj_city` VALUES (259, 140200, 140221, '阳高县', 3);
INSERT INTO `wj_city` VALUES (260, 140200, 140222, '天镇县', 3);
INSERT INTO `wj_city` VALUES (261, 140200, 140223, '广灵县', 3);
INSERT INTO `wj_city` VALUES (262, 140200, 140224, '灵丘县', 3);
INSERT INTO `wj_city` VALUES (263, 140200, 140225, '浑源县', 3);
INSERT INTO `wj_city` VALUES (264, 140200, 140226, '左云县', 3);
INSERT INTO `wj_city` VALUES (265, 140200, 140271, '山西大同经济开发区', 3);
INSERT INTO `wj_city` VALUES (266, 140000, 140300, '阳泉市', 2);
INSERT INTO `wj_city` VALUES (267, 140300, 140302, '城区', 3);
INSERT INTO `wj_city` VALUES (268, 140300, 140303, '矿区', 3);
INSERT INTO `wj_city` VALUES (269, 140300, 140311, '郊区', 3);
INSERT INTO `wj_city` VALUES (270, 140300, 140321, '平定县', 3);
INSERT INTO `wj_city` VALUES (271, 140300, 140322, '盂县', 3);
INSERT INTO `wj_city` VALUES (272, 140000, 140400, '长治市', 2);
INSERT INTO `wj_city` VALUES (273, 140400, 140403, '潞州区', 3);
INSERT INTO `wj_city` VALUES (274, 140400, 140404, '上党区', 3);
INSERT INTO `wj_city` VALUES (275, 140400, 140405, '屯留区', 3);
INSERT INTO `wj_city` VALUES (276, 140400, 140406, '潞城区', 3);
INSERT INTO `wj_city` VALUES (277, 140400, 140423, '襄垣县', 3);
INSERT INTO `wj_city` VALUES (278, 140400, 140425, '平顺县', 3);
INSERT INTO `wj_city` VALUES (279, 140400, 140426, '黎城县', 3);
INSERT INTO `wj_city` VALUES (280, 140400, 140427, '壶关县', 3);
INSERT INTO `wj_city` VALUES (281, 140400, 140428, '长子县', 3);
INSERT INTO `wj_city` VALUES (282, 140400, 140429, '武乡县', 3);
INSERT INTO `wj_city` VALUES (283, 140400, 140430, '沁县', 3);
INSERT INTO `wj_city` VALUES (284, 140400, 140431, '沁源县', 3);
INSERT INTO `wj_city` VALUES (285, 140000, 140500, '晋城市', 2);
INSERT INTO `wj_city` VALUES (286, 140500, 140502, '城区', 3);
INSERT INTO `wj_city` VALUES (287, 140500, 140521, '沁水县', 3);
INSERT INTO `wj_city` VALUES (288, 140500, 140522, '阳城县', 3);
INSERT INTO `wj_city` VALUES (289, 140500, 140524, '陵川县', 3);
INSERT INTO `wj_city` VALUES (290, 140500, 140525, '泽州县', 3);
INSERT INTO `wj_city` VALUES (291, 140500, 140581, '高平市', 3);
INSERT INTO `wj_city` VALUES (292, 140000, 140600, '朔州市', 2);
INSERT INTO `wj_city` VALUES (293, 140600, 140602, '朔城区', 3);
INSERT INTO `wj_city` VALUES (294, 140600, 140603, '平鲁区', 3);
INSERT INTO `wj_city` VALUES (295, 140600, 140621, '山阴县', 3);
INSERT INTO `wj_city` VALUES (296, 140600, 140622, '应县', 3);
INSERT INTO `wj_city` VALUES (297, 140600, 140623, '右玉县', 3);
INSERT INTO `wj_city` VALUES (298, 140600, 140671, '山西朔州经济开发区', 3);
INSERT INTO `wj_city` VALUES (299, 140600, 140681, '怀仁市', 3);
INSERT INTO `wj_city` VALUES (300, 140000, 140700, '晋中市', 2);
INSERT INTO `wj_city` VALUES (301, 140700, 140702, '榆次区', 3);
INSERT INTO `wj_city` VALUES (302, 140700, 140703, '太谷区', 3);
INSERT INTO `wj_city` VALUES (303, 140700, 140721, '榆社县', 3);
INSERT INTO `wj_city` VALUES (304, 140700, 140722, '左权县', 3);
INSERT INTO `wj_city` VALUES (305, 140700, 140723, '和顺县', 3);
INSERT INTO `wj_city` VALUES (306, 140700, 140724, '昔阳县', 3);
INSERT INTO `wj_city` VALUES (307, 140700, 140725, '寿阳县', 3);
INSERT INTO `wj_city` VALUES (308, 140700, 140727, '祁县', 3);
INSERT INTO `wj_city` VALUES (309, 140700, 140728, '平遥县', 3);
INSERT INTO `wj_city` VALUES (310, 140700, 140729, '灵石县', 3);
INSERT INTO `wj_city` VALUES (311, 140700, 140781, '介休市', 3);
INSERT INTO `wj_city` VALUES (312, 140000, 140800, '运城市', 2);
INSERT INTO `wj_city` VALUES (313, 140800, 140802, '盐湖区', 3);
INSERT INTO `wj_city` VALUES (314, 140800, 140821, '临猗县', 3);
INSERT INTO `wj_city` VALUES (315, 140800, 140822, '万荣县', 3);
INSERT INTO `wj_city` VALUES (316, 140800, 140823, '闻喜县', 3);
INSERT INTO `wj_city` VALUES (317, 140800, 140824, '稷山县', 3);
INSERT INTO `wj_city` VALUES (318, 140800, 140825, '新绛县', 3);
INSERT INTO `wj_city` VALUES (319, 140800, 140826, '绛县', 3);
INSERT INTO `wj_city` VALUES (320, 140800, 140827, '垣曲县', 3);
INSERT INTO `wj_city` VALUES (321, 140800, 140828, '夏县', 3);
INSERT INTO `wj_city` VALUES (322, 140800, 140829, '平陆县', 3);
INSERT INTO `wj_city` VALUES (323, 140800, 140830, '芮城县', 3);
INSERT INTO `wj_city` VALUES (324, 140800, 140881, '永济市', 3);
INSERT INTO `wj_city` VALUES (325, 140800, 140882, '河津市', 3);
INSERT INTO `wj_city` VALUES (326, 140000, 140900, '忻州市', 2);
INSERT INTO `wj_city` VALUES (327, 140900, 140902, '忻府区', 3);
INSERT INTO `wj_city` VALUES (328, 140900, 140921, '定襄县', 3);
INSERT INTO `wj_city` VALUES (329, 140900, 140922, '五台县', 3);
INSERT INTO `wj_city` VALUES (330, 140900, 140923, '代县', 3);
INSERT INTO `wj_city` VALUES (331, 140900, 140924, '繁峙县', 3);
INSERT INTO `wj_city` VALUES (332, 140900, 140925, '宁武县', 3);
INSERT INTO `wj_city` VALUES (333, 140900, 140926, '静乐县', 3);
INSERT INTO `wj_city` VALUES (334, 140900, 140927, '神池县', 3);
INSERT INTO `wj_city` VALUES (335, 140900, 140928, '五寨县', 3);
INSERT INTO `wj_city` VALUES (336, 140900, 140929, '岢岚县', 3);
INSERT INTO `wj_city` VALUES (337, 140900, 140930, '河曲县', 3);
INSERT INTO `wj_city` VALUES (338, 140900, 140931, '保德县', 3);
INSERT INTO `wj_city` VALUES (339, 140900, 140932, '偏关县', 3);
INSERT INTO `wj_city` VALUES (340, 140900, 140971, '五台山风景名胜区', 3);
INSERT INTO `wj_city` VALUES (341, 140900, 140981, '原平市', 3);
INSERT INTO `wj_city` VALUES (342, 140000, 141000, '临汾市', 2);
INSERT INTO `wj_city` VALUES (343, 141000, 141002, '尧都区', 3);
INSERT INTO `wj_city` VALUES (344, 141000, 141021, '曲沃县', 3);
INSERT INTO `wj_city` VALUES (345, 141000, 141022, '翼城县', 3);
INSERT INTO `wj_city` VALUES (346, 141000, 141023, '襄汾县', 3);
INSERT INTO `wj_city` VALUES (347, 141000, 141024, '洪洞县', 3);
INSERT INTO `wj_city` VALUES (348, 141000, 141025, '古县', 3);
INSERT INTO `wj_city` VALUES (349, 141000, 141026, '安泽县', 3);
INSERT INTO `wj_city` VALUES (350, 141000, 141027, '浮山县', 3);
INSERT INTO `wj_city` VALUES (351, 141000, 141028, '吉县', 3);
INSERT INTO `wj_city` VALUES (352, 141000, 141029, '乡宁县', 3);
INSERT INTO `wj_city` VALUES (353, 141000, 141030, '大宁县', 3);
INSERT INTO `wj_city` VALUES (354, 141000, 141031, '隰县', 3);
INSERT INTO `wj_city` VALUES (355, 141000, 141032, '永和县', 3);
INSERT INTO `wj_city` VALUES (356, 141000, 141033, '蒲县', 3);
INSERT INTO `wj_city` VALUES (357, 141000, 141034, '汾西县', 3);
INSERT INTO `wj_city` VALUES (358, 141000, 141081, '侯马市', 3);
INSERT INTO `wj_city` VALUES (359, 141000, 141082, '霍州市', 3);
INSERT INTO `wj_city` VALUES (360, 140000, 141100, '吕梁市', 2);
INSERT INTO `wj_city` VALUES (361, 141100, 141102, '离石区', 3);
INSERT INTO `wj_city` VALUES (362, 141100, 141121, '文水县', 3);
INSERT INTO `wj_city` VALUES (363, 141100, 141122, '交城县', 3);
INSERT INTO `wj_city` VALUES (364, 141100, 141123, '兴县', 3);
INSERT INTO `wj_city` VALUES (365, 141100, 141124, '临县', 3);
INSERT INTO `wj_city` VALUES (366, 141100, 141125, '柳林县', 3);
INSERT INTO `wj_city` VALUES (367, 141100, 141126, '石楼县', 3);
INSERT INTO `wj_city` VALUES (368, 141100, 141127, '岚县', 3);
INSERT INTO `wj_city` VALUES (369, 141100, 141128, '方山县', 3);
INSERT INTO `wj_city` VALUES (370, 141100, 141129, '中阳县', 3);
INSERT INTO `wj_city` VALUES (371, 141100, 141130, '交口县', 3);
INSERT INTO `wj_city` VALUES (372, 141100, 141181, '孝义市', 3);
INSERT INTO `wj_city` VALUES (373, 141100, 141182, '汾阳市', 3);
INSERT INTO `wj_city` VALUES (374, 0, 150000, '内蒙古自治区', 1);
INSERT INTO `wj_city` VALUES (375, 150000, 150100, '呼和浩特市', 2);
INSERT INTO `wj_city` VALUES (376, 150100, 150102, '新城区', 3);
INSERT INTO `wj_city` VALUES (377, 150100, 150103, '回民区', 3);
INSERT INTO `wj_city` VALUES (378, 150100, 150104, '玉泉区', 3);
INSERT INTO `wj_city` VALUES (379, 150100, 150105, '赛罕区', 3);
INSERT INTO `wj_city` VALUES (380, 150100, 150121, '土默特左旗', 3);
INSERT INTO `wj_city` VALUES (381, 150100, 150122, '托克托县', 3);
INSERT INTO `wj_city` VALUES (382, 150100, 150123, '和林格尔县', 3);
INSERT INTO `wj_city` VALUES (383, 150100, 150124, '清水河县', 3);
INSERT INTO `wj_city` VALUES (384, 150100, 150125, '武川县', 3);
INSERT INTO `wj_city` VALUES (385, 150100, 150172, '呼和浩特经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (386, 150000, 150200, '包头市', 2);
INSERT INTO `wj_city` VALUES (387, 150200, 150202, '东河区', 3);
INSERT INTO `wj_city` VALUES (388, 150200, 150203, '昆都仑区', 3);
INSERT INTO `wj_city` VALUES (389, 150200, 150204, '青山区', 3);
INSERT INTO `wj_city` VALUES (390, 150200, 150205, '石拐区', 3);
INSERT INTO `wj_city` VALUES (391, 150200, 150206, '白云鄂博矿区', 3);
INSERT INTO `wj_city` VALUES (392, 150200, 150207, '九原区', 3);
INSERT INTO `wj_city` VALUES (393, 150200, 150221, '土默特右旗', 3);
INSERT INTO `wj_city` VALUES (394, 150200, 150222, '固阳县', 3);
INSERT INTO `wj_city` VALUES (395, 150200, 150223, '达尔罕茂明安联合旗', 3);
INSERT INTO `wj_city` VALUES (396, 150200, 150271, '包头稀土高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (397, 150000, 150300, '乌海市', 2);
INSERT INTO `wj_city` VALUES (398, 150300, 150302, '海勃湾区', 3);
INSERT INTO `wj_city` VALUES (399, 150300, 150303, '海南区', 3);
INSERT INTO `wj_city` VALUES (400, 150300, 150304, '乌达区', 3);
INSERT INTO `wj_city` VALUES (401, 150000, 150400, '赤峰市', 2);
INSERT INTO `wj_city` VALUES (402, 150400, 150402, '红山区', 3);
INSERT INTO `wj_city` VALUES (403, 150400, 150403, '元宝山区', 3);
INSERT INTO `wj_city` VALUES (404, 150400, 150404, '松山区', 3);
INSERT INTO `wj_city` VALUES (405, 150400, 150421, '阿鲁科尔沁旗', 3);
INSERT INTO `wj_city` VALUES (406, 150400, 150422, '巴林左旗', 3);
INSERT INTO `wj_city` VALUES (407, 150400, 150423, '巴林右旗', 3);
INSERT INTO `wj_city` VALUES (408, 150400, 150424, '林西县', 3);
INSERT INTO `wj_city` VALUES (409, 150400, 150425, '克什克腾旗', 3);
INSERT INTO `wj_city` VALUES (410, 150400, 150426, '翁牛特旗', 3);
INSERT INTO `wj_city` VALUES (411, 150400, 150428, '喀喇沁旗', 3);
INSERT INTO `wj_city` VALUES (412, 150400, 150429, '宁城县', 3);
INSERT INTO `wj_city` VALUES (413, 150400, 150430, '敖汉旗', 3);
INSERT INTO `wj_city` VALUES (414, 150000, 150500, '通辽市', 2);
INSERT INTO `wj_city` VALUES (415, 150500, 150502, '科尔沁区', 3);
INSERT INTO `wj_city` VALUES (416, 150500, 150521, '科尔沁左翼中旗', 3);
INSERT INTO `wj_city` VALUES (417, 150500, 150522, '科尔沁左翼后旗', 3);
INSERT INTO `wj_city` VALUES (418, 150500, 150523, '开鲁县', 3);
INSERT INTO `wj_city` VALUES (419, 150500, 150524, '库伦旗', 3);
INSERT INTO `wj_city` VALUES (420, 150500, 150525, '奈曼旗', 3);
INSERT INTO `wj_city` VALUES (421, 150500, 150526, '扎鲁特旗', 3);
INSERT INTO `wj_city` VALUES (422, 150500, 150571, '通辽经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (423, 150500, 150581, '霍林郭勒市', 3);
INSERT INTO `wj_city` VALUES (424, 150000, 150600, '鄂尔多斯市', 2);
INSERT INTO `wj_city` VALUES (425, 150600, 150602, '东胜区', 3);
INSERT INTO `wj_city` VALUES (426, 150600, 150603, '康巴什区', 3);
INSERT INTO `wj_city` VALUES (427, 150600, 150621, '达拉特旗', 3);
INSERT INTO `wj_city` VALUES (428, 150600, 150622, '准格尔旗', 3);
INSERT INTO `wj_city` VALUES (429, 150600, 150623, '鄂托克前旗', 3);
INSERT INTO `wj_city` VALUES (430, 150600, 150624, '鄂托克旗', 3);
INSERT INTO `wj_city` VALUES (431, 150600, 150625, '杭锦旗', 3);
INSERT INTO `wj_city` VALUES (432, 150600, 150626, '乌审旗', 3);
INSERT INTO `wj_city` VALUES (433, 150600, 150627, '伊金霍洛旗', 3);
INSERT INTO `wj_city` VALUES (434, 150000, 150700, '呼伦贝尔市', 2);
INSERT INTO `wj_city` VALUES (435, 150700, 150702, '海拉尔区', 3);
INSERT INTO `wj_city` VALUES (436, 150700, 150703, '扎赉诺尔区', 3);
INSERT INTO `wj_city` VALUES (437, 150700, 150721, '阿荣旗', 3);
INSERT INTO `wj_city` VALUES (438, 150700, 150722, '莫力达瓦达斡尔族自治旗', 3);
INSERT INTO `wj_city` VALUES (439, 150700, 150723, '鄂伦春自治旗', 3);
INSERT INTO `wj_city` VALUES (440, 150700, 150724, '鄂温克族自治旗', 3);
INSERT INTO `wj_city` VALUES (441, 150700, 150725, '陈巴尔虎旗', 3);
INSERT INTO `wj_city` VALUES (442, 150700, 150726, '新巴尔虎左旗', 3);
INSERT INTO `wj_city` VALUES (443, 150700, 150727, '新巴尔虎右旗', 3);
INSERT INTO `wj_city` VALUES (444, 150700, 150781, '满洲里市', 3);
INSERT INTO `wj_city` VALUES (445, 150700, 150782, '牙克石市', 3);
INSERT INTO `wj_city` VALUES (446, 150700, 150783, '扎兰屯市', 3);
INSERT INTO `wj_city` VALUES (447, 150700, 150784, '额尔古纳市', 3);
INSERT INTO `wj_city` VALUES (448, 150700, 150785, '根河市', 3);
INSERT INTO `wj_city` VALUES (449, 150000, 150800, '巴彦淖尔市', 2);
INSERT INTO `wj_city` VALUES (450, 150800, 150802, '临河区', 3);
INSERT INTO `wj_city` VALUES (451, 150800, 150821, '五原县', 3);
INSERT INTO `wj_city` VALUES (452, 150800, 150822, '磴口县', 3);
INSERT INTO `wj_city` VALUES (453, 150800, 150823, '乌拉特前旗', 3);
INSERT INTO `wj_city` VALUES (454, 150800, 150824, '乌拉特中旗', 3);
INSERT INTO `wj_city` VALUES (455, 150800, 150825, '乌拉特后旗', 3);
INSERT INTO `wj_city` VALUES (456, 150800, 150826, '杭锦后旗', 3);
INSERT INTO `wj_city` VALUES (457, 150000, 150900, '乌兰察布市', 2);
INSERT INTO `wj_city` VALUES (458, 150900, 150902, '集宁区', 3);
INSERT INTO `wj_city` VALUES (459, 150900, 150921, '卓资县', 3);
INSERT INTO `wj_city` VALUES (460, 150900, 150922, '化德县', 3);
INSERT INTO `wj_city` VALUES (461, 150900, 150923, '商都县', 3);
INSERT INTO `wj_city` VALUES (462, 150900, 150924, '兴和县', 3);
INSERT INTO `wj_city` VALUES (463, 150900, 150925, '凉城县', 3);
INSERT INTO `wj_city` VALUES (464, 150900, 150926, '察哈尔右翼前旗', 3);
INSERT INTO `wj_city` VALUES (465, 150900, 150927, '察哈尔右翼中旗', 3);
INSERT INTO `wj_city` VALUES (466, 150900, 150928, '察哈尔右翼后旗', 3);
INSERT INTO `wj_city` VALUES (467, 150900, 150929, '四子王旗', 3);
INSERT INTO `wj_city` VALUES (468, 150900, 150981, '丰镇市', 3);
INSERT INTO `wj_city` VALUES (469, 150000, 152200, '兴安盟', 2);
INSERT INTO `wj_city` VALUES (470, 152200, 152201, '乌兰浩特市', 3);
INSERT INTO `wj_city` VALUES (471, 152200, 152202, '阿尔山市', 3);
INSERT INTO `wj_city` VALUES (472, 152200, 152221, '科尔沁右翼前旗', 3);
INSERT INTO `wj_city` VALUES (473, 152200, 152222, '科尔沁右翼中旗', 3);
INSERT INTO `wj_city` VALUES (474, 152200, 152223, '扎赉特旗', 3);
INSERT INTO `wj_city` VALUES (475, 152200, 152224, '突泉县', 3);
INSERT INTO `wj_city` VALUES (476, 150000, 152500, '锡林郭勒盟', 2);
INSERT INTO `wj_city` VALUES (477, 152500, 152501, '二连浩特市', 3);
INSERT INTO `wj_city` VALUES (478, 152500, 152502, '锡林浩特市', 3);
INSERT INTO `wj_city` VALUES (479, 152500, 152522, '阿巴嘎旗', 3);
INSERT INTO `wj_city` VALUES (480, 152500, 152523, '苏尼特左旗', 3);
INSERT INTO `wj_city` VALUES (481, 152500, 152524, '苏尼特右旗', 3);
INSERT INTO `wj_city` VALUES (482, 152500, 152525, '东乌珠穆沁旗', 3);
INSERT INTO `wj_city` VALUES (483, 152500, 152526, '西乌珠穆沁旗', 3);
INSERT INTO `wj_city` VALUES (484, 152500, 152527, '太仆寺旗', 3);
INSERT INTO `wj_city` VALUES (485, 152500, 152528, '镶黄旗', 3);
INSERT INTO `wj_city` VALUES (486, 152500, 152529, '正镶白旗', 3);
INSERT INTO `wj_city` VALUES (487, 152500, 152530, '正蓝旗', 3);
INSERT INTO `wj_city` VALUES (488, 152500, 152531, '多伦县', 3);
INSERT INTO `wj_city` VALUES (489, 152500, 152571, '乌拉盖管理区管委会', 3);
INSERT INTO `wj_city` VALUES (490, 150000, 152900, '阿拉善盟', 2);
INSERT INTO `wj_city` VALUES (491, 152900, 152921, '阿拉善左旗', 3);
INSERT INTO `wj_city` VALUES (492, 152900, 152922, '阿拉善右旗', 3);
INSERT INTO `wj_city` VALUES (493, 152900, 152923, '额济纳旗', 3);
INSERT INTO `wj_city` VALUES (494, 152900, 152971, '内蒙古阿拉善高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (495, 0, 210000, '辽宁省', 1);
INSERT INTO `wj_city` VALUES (496, 210000, 210100, '沈阳市', 2);
INSERT INTO `wj_city` VALUES (497, 210100, 210102, '和平区', 3);
INSERT INTO `wj_city` VALUES (498, 210100, 210103, '沈河区', 3);
INSERT INTO `wj_city` VALUES (499, 210100, 210104, '大东区', 3);
INSERT INTO `wj_city` VALUES (500, 210100, 210105, '皇姑区', 3);
INSERT INTO `wj_city` VALUES (501, 210100, 210106, '铁西区', 3);
INSERT INTO `wj_city` VALUES (502, 210100, 210111, '苏家屯区', 3);
INSERT INTO `wj_city` VALUES (503, 210100, 210112, '浑南区', 3);
INSERT INTO `wj_city` VALUES (504, 210100, 210113, '沈北新区', 3);
INSERT INTO `wj_city` VALUES (505, 210100, 210114, '于洪区', 3);
INSERT INTO `wj_city` VALUES (506, 210100, 210115, '辽中区', 3);
INSERT INTO `wj_city` VALUES (507, 210100, 210123, '康平县', 3);
INSERT INTO `wj_city` VALUES (508, 210100, 210124, '法库县', 3);
INSERT INTO `wj_city` VALUES (509, 210100, 210181, '新民市', 3);
INSERT INTO `wj_city` VALUES (510, 210000, 210200, '大连市', 2);
INSERT INTO `wj_city` VALUES (511, 210200, 210202, '中山区', 3);
INSERT INTO `wj_city` VALUES (512, 210200, 210203, '西岗区', 3);
INSERT INTO `wj_city` VALUES (513, 210200, 210204, '沙河口区', 3);
INSERT INTO `wj_city` VALUES (514, 210200, 210211, '甘井子区', 3);
INSERT INTO `wj_city` VALUES (515, 210200, 210212, '旅顺口区', 3);
INSERT INTO `wj_city` VALUES (516, 210200, 210213, '金州区', 3);
INSERT INTO `wj_city` VALUES (517, 210200, 210214, '普兰店区', 3);
INSERT INTO `wj_city` VALUES (518, 210200, 210224, '长海县', 3);
INSERT INTO `wj_city` VALUES (519, 210200, 210281, '瓦房店市', 3);
INSERT INTO `wj_city` VALUES (520, 210200, 210283, '庄河市', 3);
INSERT INTO `wj_city` VALUES (521, 210000, 210300, '鞍山市', 2);
INSERT INTO `wj_city` VALUES (522, 210300, 210302, '铁东区', 3);
INSERT INTO `wj_city` VALUES (523, 210300, 210303, '铁西区', 3);
INSERT INTO `wj_city` VALUES (524, 210300, 210304, '立山区', 3);
INSERT INTO `wj_city` VALUES (525, 210300, 210311, '千山区', 3);
INSERT INTO `wj_city` VALUES (526, 210300, 210321, '台安县', 3);
INSERT INTO `wj_city` VALUES (527, 210300, 210323, '岫岩满族自治县', 3);
INSERT INTO `wj_city` VALUES (528, 210300, 210381, '海城市', 3);
INSERT INTO `wj_city` VALUES (529, 210000, 210400, '抚顺市', 2);
INSERT INTO `wj_city` VALUES (530, 210400, 210402, '新抚区', 3);
INSERT INTO `wj_city` VALUES (531, 210400, 210403, '东洲区', 3);
INSERT INTO `wj_city` VALUES (532, 210400, 210404, '望花区', 3);
INSERT INTO `wj_city` VALUES (533, 210400, 210411, '顺城区', 3);
INSERT INTO `wj_city` VALUES (534, 210400, 210421, '抚顺县', 3);
INSERT INTO `wj_city` VALUES (535, 210400, 210422, '新宾满族自治县', 3);
INSERT INTO `wj_city` VALUES (536, 210400, 210423, '清原满族自治县', 3);
INSERT INTO `wj_city` VALUES (537, 210000, 210500, '本溪市', 2);
INSERT INTO `wj_city` VALUES (538, 210500, 210502, '平山区', 3);
INSERT INTO `wj_city` VALUES (539, 210500, 210503, '溪湖区', 3);
INSERT INTO `wj_city` VALUES (540, 210500, 210504, '明山区', 3);
INSERT INTO `wj_city` VALUES (541, 210500, 210505, '南芬区', 3);
INSERT INTO `wj_city` VALUES (542, 210500, 210521, '本溪满族自治县', 3);
INSERT INTO `wj_city` VALUES (543, 210500, 210522, '桓仁满族自治县', 3);
INSERT INTO `wj_city` VALUES (544, 210000, 210600, '丹东市', 2);
INSERT INTO `wj_city` VALUES (545, 210600, 210602, '元宝区', 3);
INSERT INTO `wj_city` VALUES (546, 210600, 210603, '振兴区', 3);
INSERT INTO `wj_city` VALUES (547, 210600, 210604, '振安区', 3);
INSERT INTO `wj_city` VALUES (548, 210600, 210624, '宽甸满族自治县', 3);
INSERT INTO `wj_city` VALUES (549, 210600, 210681, '东港市', 3);
INSERT INTO `wj_city` VALUES (550, 210600, 210682, '凤城市', 3);
INSERT INTO `wj_city` VALUES (551, 210000, 210700, '锦州市', 2);
INSERT INTO `wj_city` VALUES (552, 210700, 210702, '古塔区', 3);
INSERT INTO `wj_city` VALUES (553, 210700, 210703, '凌河区', 3);
INSERT INTO `wj_city` VALUES (554, 210700, 210711, '太和区', 3);
INSERT INTO `wj_city` VALUES (555, 210700, 210726, '黑山县', 3);
INSERT INTO `wj_city` VALUES (556, 210700, 210727, '义县', 3);
INSERT INTO `wj_city` VALUES (557, 210700, 210781, '凌海市', 3);
INSERT INTO `wj_city` VALUES (558, 210700, 210782, '北镇市', 3);
INSERT INTO `wj_city` VALUES (559, 210000, 210800, '营口市', 2);
INSERT INTO `wj_city` VALUES (560, 210800, 210802, '站前区', 3);
INSERT INTO `wj_city` VALUES (561, 210800, 210803, '西市区', 3);
INSERT INTO `wj_city` VALUES (562, 210800, 210804, '鲅鱼圈区', 3);
INSERT INTO `wj_city` VALUES (563, 210800, 210811, '老边区', 3);
INSERT INTO `wj_city` VALUES (564, 210800, 210881, '盖州市', 3);
INSERT INTO `wj_city` VALUES (565, 210800, 210882, '大石桥市', 3);
INSERT INTO `wj_city` VALUES (566, 210000, 210900, '阜新市', 2);
INSERT INTO `wj_city` VALUES (567, 210900, 210902, '海州区', 3);
INSERT INTO `wj_city` VALUES (568, 210900, 210903, '新邱区', 3);
INSERT INTO `wj_city` VALUES (569, 210900, 210904, '太平区', 3);
INSERT INTO `wj_city` VALUES (570, 210900, 210905, '清河门区', 3);
INSERT INTO `wj_city` VALUES (571, 210900, 210911, '细河区', 3);
INSERT INTO `wj_city` VALUES (572, 210900, 210921, '阜新蒙古族自治县', 3);
INSERT INTO `wj_city` VALUES (573, 210900, 210922, '彰武县', 3);
INSERT INTO `wj_city` VALUES (574, 210000, 211000, '辽阳市', 2);
INSERT INTO `wj_city` VALUES (575, 211000, 211002, '白塔区', 3);
INSERT INTO `wj_city` VALUES (576, 211000, 211003, '文圣区', 3);
INSERT INTO `wj_city` VALUES (577, 211000, 211004, '宏伟区', 3);
INSERT INTO `wj_city` VALUES (578, 211000, 211005, '弓长岭区', 3);
INSERT INTO `wj_city` VALUES (579, 211000, 211011, '太子河区', 3);
INSERT INTO `wj_city` VALUES (580, 211000, 211021, '辽阳县', 3);
INSERT INTO `wj_city` VALUES (581, 211000, 211081, '灯塔市', 3);
INSERT INTO `wj_city` VALUES (582, 210000, 211100, '盘锦市', 2);
INSERT INTO `wj_city` VALUES (583, 211100, 211102, '双台子区', 3);
INSERT INTO `wj_city` VALUES (584, 211100, 211103, '兴隆台区', 3);
INSERT INTO `wj_city` VALUES (585, 211100, 211104, '大洼区', 3);
INSERT INTO `wj_city` VALUES (586, 211100, 211122, '盘山县', 3);
INSERT INTO `wj_city` VALUES (587, 210000, 211200, '铁岭市', 2);
INSERT INTO `wj_city` VALUES (588, 211200, 211202, '银州区', 3);
INSERT INTO `wj_city` VALUES (589, 211200, 211204, '清河区', 3);
INSERT INTO `wj_city` VALUES (590, 211200, 211221, '铁岭县', 3);
INSERT INTO `wj_city` VALUES (591, 211200, 211223, '西丰县', 3);
INSERT INTO `wj_city` VALUES (592, 211200, 211224, '昌图县', 3);
INSERT INTO `wj_city` VALUES (593, 211200, 211281, '调兵山市', 3);
INSERT INTO `wj_city` VALUES (594, 211200, 211282, '开原市', 3);
INSERT INTO `wj_city` VALUES (595, 210000, 211300, '朝阳市', 2);
INSERT INTO `wj_city` VALUES (596, 211300, 211302, '双塔区', 3);
INSERT INTO `wj_city` VALUES (597, 211300, 211303, '龙城区', 3);
INSERT INTO `wj_city` VALUES (598, 211300, 211321, '朝阳县', 3);
INSERT INTO `wj_city` VALUES (599, 211300, 211322, '建平县', 3);
INSERT INTO `wj_city` VALUES (600, 211300, 211324, '喀喇沁左翼蒙古族自治县', 3);
INSERT INTO `wj_city` VALUES (601, 211300, 211381, '北票市', 3);
INSERT INTO `wj_city` VALUES (602, 211300, 211382, '凌源市', 3);
INSERT INTO `wj_city` VALUES (603, 210000, 211400, '葫芦岛市', 2);
INSERT INTO `wj_city` VALUES (604, 211400, 211402, '连山区', 3);
INSERT INTO `wj_city` VALUES (605, 211400, 211403, '龙港区', 3);
INSERT INTO `wj_city` VALUES (606, 211400, 211404, '南票区', 3);
INSERT INTO `wj_city` VALUES (607, 211400, 211421, '绥中县', 3);
INSERT INTO `wj_city` VALUES (608, 211400, 211422, '建昌县', 3);
INSERT INTO `wj_city` VALUES (609, 211400, 211481, '兴城市', 3);
INSERT INTO `wj_city` VALUES (610, 0, 220000, '吉林省', 1);
INSERT INTO `wj_city` VALUES (611, 220000, 220100, '长春市', 2);
INSERT INTO `wj_city` VALUES (612, 220100, 220102, '南关区', 3);
INSERT INTO `wj_city` VALUES (613, 220100, 220103, '宽城区', 3);
INSERT INTO `wj_city` VALUES (614, 220100, 220104, '朝阳区', 3);
INSERT INTO `wj_city` VALUES (615, 220100, 220105, '二道区', 3);
INSERT INTO `wj_city` VALUES (616, 220100, 220106, '绿园区', 3);
INSERT INTO `wj_city` VALUES (617, 220100, 220112, '双阳区', 3);
INSERT INTO `wj_city` VALUES (618, 220100, 220113, '九台区', 3);
INSERT INTO `wj_city` VALUES (619, 220100, 220122, '农安县', 3);
INSERT INTO `wj_city` VALUES (620, 220100, 220171, '长春经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (621, 220100, 220172, '长春净月高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (622, 220100, 220173, '长春高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (623, 220100, 220174, '长春汽车经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (624, 220100, 220182, '榆树市', 3);
INSERT INTO `wj_city` VALUES (625, 220100, 220183, '德惠市', 3);
INSERT INTO `wj_city` VALUES (626, 220100, 220184, '公主岭市', 3);
INSERT INTO `wj_city` VALUES (627, 220000, 220200, '吉林市', 2);
INSERT INTO `wj_city` VALUES (628, 220200, 220202, '昌邑区', 3);
INSERT INTO `wj_city` VALUES (629, 220200, 220203, '龙潭区', 3);
INSERT INTO `wj_city` VALUES (630, 220200, 220204, '船营区', 3);
INSERT INTO `wj_city` VALUES (631, 220200, 220211, '丰满区', 3);
INSERT INTO `wj_city` VALUES (632, 220200, 220221, '永吉县', 3);
INSERT INTO `wj_city` VALUES (633, 220200, 220271, '吉林经济开发区', 3);
INSERT INTO `wj_city` VALUES (634, 220200, 220272, '吉林高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (635, 220200, 220273, '吉林中国新加坡食品区', 3);
INSERT INTO `wj_city` VALUES (636, 220200, 220281, '蛟河市', 3);
INSERT INTO `wj_city` VALUES (637, 220200, 220282, '桦甸市', 3);
INSERT INTO `wj_city` VALUES (638, 220200, 220283, '舒兰市', 3);
INSERT INTO `wj_city` VALUES (639, 220200, 220284, '磐石市', 3);
INSERT INTO `wj_city` VALUES (640, 220000, 220300, '四平市', 2);
INSERT INTO `wj_city` VALUES (641, 220300, 220302, '铁西区', 3);
INSERT INTO `wj_city` VALUES (642, 220300, 220303, '铁东区', 3);
INSERT INTO `wj_city` VALUES (643, 220300, 220322, '梨树县', 3);
INSERT INTO `wj_city` VALUES (644, 220300, 220323, '伊通满族自治县', 3);
INSERT INTO `wj_city` VALUES (645, 220300, 220382, '双辽市', 3);
INSERT INTO `wj_city` VALUES (646, 220000, 220400, '辽源市', 2);
INSERT INTO `wj_city` VALUES (647, 220400, 220402, '龙山区', 3);
INSERT INTO `wj_city` VALUES (648, 220400, 220403, '西安区', 3);
INSERT INTO `wj_city` VALUES (649, 220400, 220421, '东丰县', 3);
INSERT INTO `wj_city` VALUES (650, 220400, 220422, '东辽县', 3);
INSERT INTO `wj_city` VALUES (651, 220000, 220500, '通化市', 2);
INSERT INTO `wj_city` VALUES (652, 220500, 220502, '东昌区', 3);
INSERT INTO `wj_city` VALUES (653, 220500, 220503, '二道江区', 3);
INSERT INTO `wj_city` VALUES (654, 220500, 220521, '通化县', 3);
INSERT INTO `wj_city` VALUES (655, 220500, 220523, '辉南县', 3);
INSERT INTO `wj_city` VALUES (656, 220500, 220524, '柳河县', 3);
INSERT INTO `wj_city` VALUES (657, 220500, 220581, '梅河口市', 3);
INSERT INTO `wj_city` VALUES (658, 220500, 220582, '集安市', 3);
INSERT INTO `wj_city` VALUES (659, 220000, 220600, '白山市', 2);
INSERT INTO `wj_city` VALUES (660, 220600, 220602, '浑江区', 3);
INSERT INTO `wj_city` VALUES (661, 220600, 220605, '江源区', 3);
INSERT INTO `wj_city` VALUES (662, 220600, 220621, '抚松县', 3);
INSERT INTO `wj_city` VALUES (663, 220600, 220622, '靖宇县', 3);
INSERT INTO `wj_city` VALUES (664, 220600, 220623, '长白朝鲜族自治县', 3);
INSERT INTO `wj_city` VALUES (665, 220600, 220681, '临江市', 3);
INSERT INTO `wj_city` VALUES (666, 220000, 220700, '松原市', 2);
INSERT INTO `wj_city` VALUES (667, 220700, 220702, '宁江区', 3);
INSERT INTO `wj_city` VALUES (668, 220700, 220721, '前郭尔罗斯蒙古族自治县', 3);
INSERT INTO `wj_city` VALUES (669, 220700, 220722, '长岭县', 3);
INSERT INTO `wj_city` VALUES (670, 220700, 220723, '乾安县', 3);
INSERT INTO `wj_city` VALUES (671, 220700, 220771, '吉林松原经济开发区', 3);
INSERT INTO `wj_city` VALUES (672, 220700, 220781, '扶余市', 3);
INSERT INTO `wj_city` VALUES (673, 220000, 220800, '白城市', 2);
INSERT INTO `wj_city` VALUES (674, 220800, 220802, '洮北区', 3);
INSERT INTO `wj_city` VALUES (675, 220800, 220821, '镇赉县', 3);
INSERT INTO `wj_city` VALUES (676, 220800, 220822, '通榆县', 3);
INSERT INTO `wj_city` VALUES (677, 220800, 220871, '吉林白城经济开发区', 3);
INSERT INTO `wj_city` VALUES (678, 220800, 220881, '洮南市', 3);
INSERT INTO `wj_city` VALUES (679, 220800, 220882, '大安市', 3);
INSERT INTO `wj_city` VALUES (680, 220000, 222400, '延边朝鲜族自治州', 2);
INSERT INTO `wj_city` VALUES (681, 222400, 222401, '延吉市', 3);
INSERT INTO `wj_city` VALUES (682, 222400, 222402, '图们市', 3);
INSERT INTO `wj_city` VALUES (683, 222400, 222403, '敦化市', 3);
INSERT INTO `wj_city` VALUES (684, 222400, 222404, '珲春市', 3);
INSERT INTO `wj_city` VALUES (685, 222400, 222405, '龙井市', 3);
INSERT INTO `wj_city` VALUES (686, 222400, 222406, '和龙市', 3);
INSERT INTO `wj_city` VALUES (687, 222400, 222424, '汪清县', 3);
INSERT INTO `wj_city` VALUES (688, 222400, 222426, '安图县', 3);
INSERT INTO `wj_city` VALUES (689, 0, 230000, '黑龙江省', 1);
INSERT INTO `wj_city` VALUES (690, 230000, 230100, '哈尔滨市', 2);
INSERT INTO `wj_city` VALUES (691, 230100, 230102, '道里区', 3);
INSERT INTO `wj_city` VALUES (692, 230100, 230103, '南岗区', 3);
INSERT INTO `wj_city` VALUES (693, 230100, 230104, '道外区', 3);
INSERT INTO `wj_city` VALUES (694, 230100, 230108, '平房区', 3);
INSERT INTO `wj_city` VALUES (695, 230100, 230109, '松北区', 3);
INSERT INTO `wj_city` VALUES (696, 230100, 230110, '香坊区', 3);
INSERT INTO `wj_city` VALUES (697, 230100, 230111, '呼兰区', 3);
INSERT INTO `wj_city` VALUES (698, 230100, 230112, '阿城区', 3);
INSERT INTO `wj_city` VALUES (699, 230100, 230113, '双城区', 3);
INSERT INTO `wj_city` VALUES (700, 230100, 230123, '依兰县', 3);
INSERT INTO `wj_city` VALUES (701, 230100, 230124, '方正县', 3);
INSERT INTO `wj_city` VALUES (702, 230100, 230125, '宾县', 3);
INSERT INTO `wj_city` VALUES (703, 230100, 230126, '巴彦县', 3);
INSERT INTO `wj_city` VALUES (704, 230100, 230127, '木兰县', 3);
INSERT INTO `wj_city` VALUES (705, 230100, 230128, '通河县', 3);
INSERT INTO `wj_city` VALUES (706, 230100, 230129, '延寿县', 3);
INSERT INTO `wj_city` VALUES (707, 230100, 230183, '尚志市', 3);
INSERT INTO `wj_city` VALUES (708, 230100, 230184, '五常市', 3);
INSERT INTO `wj_city` VALUES (709, 230000, 230200, '齐齐哈尔市', 2);
INSERT INTO `wj_city` VALUES (710, 230200, 230202, '龙沙区', 3);
INSERT INTO `wj_city` VALUES (711, 230200, 230203, '建华区', 3);
INSERT INTO `wj_city` VALUES (712, 230200, 230204, '铁锋区', 3);
INSERT INTO `wj_city` VALUES (713, 230200, 230205, '昂昂溪区', 3);
INSERT INTO `wj_city` VALUES (714, 230200, 230206, '富拉尔基区', 3);
INSERT INTO `wj_city` VALUES (715, 230200, 230207, '碾子山区', 3);
INSERT INTO `wj_city` VALUES (716, 230200, 230208, '梅里斯达斡尔族区', 3);
INSERT INTO `wj_city` VALUES (717, 230200, 230221, '龙江县', 3);
INSERT INTO `wj_city` VALUES (718, 230200, 230223, '依安县', 3);
INSERT INTO `wj_city` VALUES (719, 230200, 230224, '泰来县', 3);
INSERT INTO `wj_city` VALUES (720, 230200, 230225, '甘南县', 3);
INSERT INTO `wj_city` VALUES (721, 230200, 230227, '富裕县', 3);
INSERT INTO `wj_city` VALUES (722, 230200, 230229, '克山县', 3);
INSERT INTO `wj_city` VALUES (723, 230200, 230230, '克东县', 3);
INSERT INTO `wj_city` VALUES (724, 230200, 230231, '拜泉县', 3);
INSERT INTO `wj_city` VALUES (725, 230200, 230281, '讷河市', 3);
INSERT INTO `wj_city` VALUES (726, 230000, 230300, '鸡西市', 2);
INSERT INTO `wj_city` VALUES (727, 230300, 230302, '鸡冠区', 3);
INSERT INTO `wj_city` VALUES (728, 230300, 230303, '恒山区', 3);
INSERT INTO `wj_city` VALUES (729, 230300, 230304, '滴道区', 3);
INSERT INTO `wj_city` VALUES (730, 230300, 230305, '梨树区', 3);
INSERT INTO `wj_city` VALUES (731, 230300, 230306, '城子河区', 3);
INSERT INTO `wj_city` VALUES (732, 230300, 230307, '麻山区', 3);
INSERT INTO `wj_city` VALUES (733, 230300, 230321, '鸡东县', 3);
INSERT INTO `wj_city` VALUES (734, 230300, 230381, '虎林市', 3);
INSERT INTO `wj_city` VALUES (735, 230300, 230382, '密山市', 3);
INSERT INTO `wj_city` VALUES (736, 230000, 230400, '鹤岗市', 2);
INSERT INTO `wj_city` VALUES (737, 230400, 230402, '向阳区', 3);
INSERT INTO `wj_city` VALUES (738, 230400, 230403, '工农区', 3);
INSERT INTO `wj_city` VALUES (739, 230400, 230404, '南山区', 3);
INSERT INTO `wj_city` VALUES (740, 230400, 230405, '兴安区', 3);
INSERT INTO `wj_city` VALUES (741, 230400, 230406, '东山区', 3);
INSERT INTO `wj_city` VALUES (742, 230400, 230407, '兴山区', 3);
INSERT INTO `wj_city` VALUES (743, 230400, 230421, '萝北县', 3);
INSERT INTO `wj_city` VALUES (744, 230400, 230422, '绥滨县', 3);
INSERT INTO `wj_city` VALUES (745, 230000, 230500, '双鸭山市', 2);
INSERT INTO `wj_city` VALUES (746, 230500, 230502, '尖山区', 3);
INSERT INTO `wj_city` VALUES (747, 230500, 230503, '岭东区', 3);
INSERT INTO `wj_city` VALUES (748, 230500, 230505, '四方台区', 3);
INSERT INTO `wj_city` VALUES (749, 230500, 230506, '宝山区', 3);
INSERT INTO `wj_city` VALUES (750, 230500, 230521, '集贤县', 3);
INSERT INTO `wj_city` VALUES (751, 230500, 230522, '友谊县', 3);
INSERT INTO `wj_city` VALUES (752, 230500, 230523, '宝清县', 3);
INSERT INTO `wj_city` VALUES (753, 230500, 230524, '饶河县', 3);
INSERT INTO `wj_city` VALUES (754, 230000, 230600, '大庆市', 2);
INSERT INTO `wj_city` VALUES (755, 230600, 230602, '萨尔图区', 3);
INSERT INTO `wj_city` VALUES (756, 230600, 230603, '龙凤区', 3);
INSERT INTO `wj_city` VALUES (757, 230600, 230604, '让胡路区', 3);
INSERT INTO `wj_city` VALUES (758, 230600, 230605, '红岗区', 3);
INSERT INTO `wj_city` VALUES (759, 230600, 230606, '大同区', 3);
INSERT INTO `wj_city` VALUES (760, 230600, 230621, '肇州县', 3);
INSERT INTO `wj_city` VALUES (761, 230600, 230622, '肇源县', 3);
INSERT INTO `wj_city` VALUES (762, 230600, 230623, '林甸县', 3);
INSERT INTO `wj_city` VALUES (763, 230600, 230624, '杜尔伯特蒙古族自治县', 3);
INSERT INTO `wj_city` VALUES (764, 230600, 230671, '大庆高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (765, 230000, 230700, '伊春市', 2);
INSERT INTO `wj_city` VALUES (766, 230700, 230717, '伊美区', 3);
INSERT INTO `wj_city` VALUES (767, 230700, 230718, '乌翠区', 3);
INSERT INTO `wj_city` VALUES (768, 230700, 230719, '友好区', 3);
INSERT INTO `wj_city` VALUES (769, 230700, 230722, '嘉荫县', 3);
INSERT INTO `wj_city` VALUES (770, 230700, 230723, '汤旺县', 3);
INSERT INTO `wj_city` VALUES (771, 230700, 230724, '丰林县', 3);
INSERT INTO `wj_city` VALUES (772, 230700, 230725, '大箐山县', 3);
INSERT INTO `wj_city` VALUES (773, 230700, 230726, '南岔县', 3);
INSERT INTO `wj_city` VALUES (774, 230700, 230751, '金林区', 3);
INSERT INTO `wj_city` VALUES (775, 230700, 230781, '铁力市', 3);
INSERT INTO `wj_city` VALUES (776, 230000, 230800, '佳木斯市', 2);
INSERT INTO `wj_city` VALUES (777, 230800, 230803, '向阳区', 3);
INSERT INTO `wj_city` VALUES (778, 230800, 230804, '前进区', 3);
INSERT INTO `wj_city` VALUES (779, 230800, 230805, '东风区', 3);
INSERT INTO `wj_city` VALUES (780, 230800, 230811, '郊区', 3);
INSERT INTO `wj_city` VALUES (781, 230800, 230822, '桦南县', 3);
INSERT INTO `wj_city` VALUES (782, 230800, 230826, '桦川县', 3);
INSERT INTO `wj_city` VALUES (783, 230800, 230828, '汤原县', 3);
INSERT INTO `wj_city` VALUES (784, 230800, 230881, '同江市', 3);
INSERT INTO `wj_city` VALUES (785, 230800, 230882, '富锦市', 3);
INSERT INTO `wj_city` VALUES (786, 230800, 230883, '抚远市', 3);
INSERT INTO `wj_city` VALUES (787, 230000, 230900, '七台河市', 2);
INSERT INTO `wj_city` VALUES (788, 230900, 230902, '新兴区', 3);
INSERT INTO `wj_city` VALUES (789, 230900, 230903, '桃山区', 3);
INSERT INTO `wj_city` VALUES (790, 230900, 230904, '茄子河区', 3);
INSERT INTO `wj_city` VALUES (791, 230900, 230921, '勃利县', 3);
INSERT INTO `wj_city` VALUES (792, 230000, 231000, '牡丹江市', 2);
INSERT INTO `wj_city` VALUES (793, 231000, 231002, '东安区', 3);
INSERT INTO `wj_city` VALUES (794, 231000, 231003, '阳明区', 3);
INSERT INTO `wj_city` VALUES (795, 231000, 231004, '爱民区', 3);
INSERT INTO `wj_city` VALUES (796, 231000, 231005, '西安区', 3);
INSERT INTO `wj_city` VALUES (797, 231000, 231025, '林口县', 3);
INSERT INTO `wj_city` VALUES (798, 231000, 231081, '绥芬河市', 3);
INSERT INTO `wj_city` VALUES (799, 231000, 231083, '海林市', 3);
INSERT INTO `wj_city` VALUES (800, 231000, 231084, '宁安市', 3);
INSERT INTO `wj_city` VALUES (801, 231000, 231085, '穆棱市', 3);
INSERT INTO `wj_city` VALUES (802, 231000, 231086, '东宁市', 3);
INSERT INTO `wj_city` VALUES (803, 230000, 231100, '黑河市', 2);
INSERT INTO `wj_city` VALUES (804, 231100, 231102, '爱辉区', 3);
INSERT INTO `wj_city` VALUES (805, 231100, 231123, '逊克县', 3);
INSERT INTO `wj_city` VALUES (806, 231100, 231124, '孙吴县', 3);
INSERT INTO `wj_city` VALUES (807, 231100, 231181, '北安市', 3);
INSERT INTO `wj_city` VALUES (808, 231100, 231182, '五大连池市', 3);
INSERT INTO `wj_city` VALUES (809, 231100, 231183, '嫩江市', 3);
INSERT INTO `wj_city` VALUES (810, 230000, 231200, '绥化市', 2);
INSERT INTO `wj_city` VALUES (811, 231200, 231202, '北林区', 3);
INSERT INTO `wj_city` VALUES (812, 231200, 231221, '望奎县', 3);
INSERT INTO `wj_city` VALUES (813, 231200, 231222, '兰西县', 3);
INSERT INTO `wj_city` VALUES (814, 231200, 231223, '青冈县', 3);
INSERT INTO `wj_city` VALUES (815, 231200, 231224, '庆安县', 3);
INSERT INTO `wj_city` VALUES (816, 231200, 231225, '明水县', 3);
INSERT INTO `wj_city` VALUES (817, 231200, 231226, '绥棱县', 3);
INSERT INTO `wj_city` VALUES (818, 231200, 231281, '安达市', 3);
INSERT INTO `wj_city` VALUES (819, 231200, 231282, '肇东市', 3);
INSERT INTO `wj_city` VALUES (820, 231200, 231283, '海伦市', 3);
INSERT INTO `wj_city` VALUES (821, 230000, 232700, '大兴安岭地区', 2);
INSERT INTO `wj_city` VALUES (822, 232700, 232701, '漠河市', 3);
INSERT INTO `wj_city` VALUES (823, 232700, 232721, '呼玛县', 3);
INSERT INTO `wj_city` VALUES (824, 232700, 232722, '塔河县', 3);
INSERT INTO `wj_city` VALUES (825, 232700, 232761, '加格达奇区', 3);
INSERT INTO `wj_city` VALUES (826, 232700, 232762, '松岭区', 3);
INSERT INTO `wj_city` VALUES (827, 232700, 232763, '新林区', 3);
INSERT INTO `wj_city` VALUES (828, 232700, 232764, '呼中区', 3);
INSERT INTO `wj_city` VALUES (829, 0, 310000, '上海市', 1);
INSERT INTO `wj_city` VALUES (830, 310000, 310100, '上海市', 2);
INSERT INTO `wj_city` VALUES (831, 310100, 310101, '黄浦区', 3);
INSERT INTO `wj_city` VALUES (832, 310100, 310104, '徐汇区', 3);
INSERT INTO `wj_city` VALUES (833, 310100, 310105, '长宁区', 3);
INSERT INTO `wj_city` VALUES (834, 310100, 310106, '静安区', 3);
INSERT INTO `wj_city` VALUES (835, 310100, 310107, '普陀区', 3);
INSERT INTO `wj_city` VALUES (836, 310100, 310109, '虹口区', 3);
INSERT INTO `wj_city` VALUES (837, 310100, 310110, '杨浦区', 3);
INSERT INTO `wj_city` VALUES (838, 310100, 310112, '闵行区', 3);
INSERT INTO `wj_city` VALUES (839, 310100, 310113, '宝山区', 3);
INSERT INTO `wj_city` VALUES (840, 310100, 310114, '嘉定区', 3);
INSERT INTO `wj_city` VALUES (841, 310100, 310115, '浦东新区', 3);
INSERT INTO `wj_city` VALUES (842, 310100, 310116, '金山区', 3);
INSERT INTO `wj_city` VALUES (843, 310100, 310117, '松江区', 3);
INSERT INTO `wj_city` VALUES (844, 310100, 310118, '青浦区', 3);
INSERT INTO `wj_city` VALUES (845, 310100, 310120, '奉贤区', 3);
INSERT INTO `wj_city` VALUES (846, 310100, 310151, '崇明区', 3);
INSERT INTO `wj_city` VALUES (847, 0, 320000, '江苏省', 1);
INSERT INTO `wj_city` VALUES (848, 320000, 320100, '南京市', 2);
INSERT INTO `wj_city` VALUES (849, 320100, 320102, '玄武区', 3);
INSERT INTO `wj_city` VALUES (850, 320100, 320104, '秦淮区', 3);
INSERT INTO `wj_city` VALUES (851, 320100, 320105, '建邺区', 3);
INSERT INTO `wj_city` VALUES (852, 320100, 320106, '鼓楼区', 3);
INSERT INTO `wj_city` VALUES (853, 320100, 320111, '浦口区', 3);
INSERT INTO `wj_city` VALUES (854, 320100, 320113, '栖霞区', 3);
INSERT INTO `wj_city` VALUES (855, 320100, 320114, '雨花台区', 3);
INSERT INTO `wj_city` VALUES (856, 320100, 320115, '江宁区', 3);
INSERT INTO `wj_city` VALUES (857, 320100, 320116, '六合区', 3);
INSERT INTO `wj_city` VALUES (858, 320100, 320117, '溧水区', 3);
INSERT INTO `wj_city` VALUES (859, 320100, 320118, '高淳区', 3);
INSERT INTO `wj_city` VALUES (860, 320000, 320200, '无锡市', 2);
INSERT INTO `wj_city` VALUES (861, 320200, 320205, '锡山区', 3);
INSERT INTO `wj_city` VALUES (862, 320200, 320206, '惠山区', 3);
INSERT INTO `wj_city` VALUES (863, 320200, 320211, '滨湖区', 3);
INSERT INTO `wj_city` VALUES (864, 320200, 320213, '梁溪区', 3);
INSERT INTO `wj_city` VALUES (865, 320200, 320214, '新吴区', 3);
INSERT INTO `wj_city` VALUES (866, 320200, 320281, '江阴市', 3);
INSERT INTO `wj_city` VALUES (867, 320200, 320282, '宜兴市', 3);
INSERT INTO `wj_city` VALUES (868, 320000, 320300, '徐州市', 2);
INSERT INTO `wj_city` VALUES (869, 320300, 320302, '鼓楼区', 3);
INSERT INTO `wj_city` VALUES (870, 320300, 320303, '云龙区', 3);
INSERT INTO `wj_city` VALUES (871, 320300, 320305, '贾汪区', 3);
INSERT INTO `wj_city` VALUES (872, 320300, 320311, '泉山区', 3);
INSERT INTO `wj_city` VALUES (873, 320300, 320312, '铜山区', 3);
INSERT INTO `wj_city` VALUES (874, 320300, 320321, '丰县', 3);
INSERT INTO `wj_city` VALUES (875, 320300, 320322, '沛县', 3);
INSERT INTO `wj_city` VALUES (876, 320300, 320324, '睢宁县', 3);
INSERT INTO `wj_city` VALUES (877, 320300, 320371, '徐州经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (878, 320300, 320381, '新沂市', 3);
INSERT INTO `wj_city` VALUES (879, 320300, 320382, '邳州市', 3);
INSERT INTO `wj_city` VALUES (880, 320000, 320400, '常州市', 2);
INSERT INTO `wj_city` VALUES (881, 320400, 320402, '天宁区', 3);
INSERT INTO `wj_city` VALUES (882, 320400, 320404, '钟楼区', 3);
INSERT INTO `wj_city` VALUES (883, 320400, 320411, '新北区', 3);
INSERT INTO `wj_city` VALUES (884, 320400, 320412, '武进区', 3);
INSERT INTO `wj_city` VALUES (885, 320400, 320413, '金坛区', 3);
INSERT INTO `wj_city` VALUES (886, 320400, 320481, '溧阳市', 3);
INSERT INTO `wj_city` VALUES (887, 320000, 320500, '苏州市', 2);
INSERT INTO `wj_city` VALUES (888, 320500, 320505, '虎丘区', 3);
INSERT INTO `wj_city` VALUES (889, 320500, 320506, '吴中区', 3);
INSERT INTO `wj_city` VALUES (890, 320500, 320507, '相城区', 3);
INSERT INTO `wj_city` VALUES (891, 320500, 320508, '姑苏区', 3);
INSERT INTO `wj_city` VALUES (892, 320500, 320509, '吴江区', 3);
INSERT INTO `wj_city` VALUES (893, 320500, 320576, '苏州工业园区', 3);
INSERT INTO `wj_city` VALUES (894, 320500, 320581, '常熟市', 3);
INSERT INTO `wj_city` VALUES (895, 320500, 320582, '张家港市', 3);
INSERT INTO `wj_city` VALUES (896, 320500, 320583, '昆山市', 3);
INSERT INTO `wj_city` VALUES (897, 320500, 320585, '太仓市', 3);
INSERT INTO `wj_city` VALUES (898, 320000, 320600, '南通市', 2);
INSERT INTO `wj_city` VALUES (899, 320600, 320612, '通州区', 3);
INSERT INTO `wj_city` VALUES (900, 320600, 320613, '崇川区', 3);
INSERT INTO `wj_city` VALUES (901, 320600, 320614, '海门区', 3);
INSERT INTO `wj_city` VALUES (902, 320600, 320623, '如东县', 3);
INSERT INTO `wj_city` VALUES (903, 320600, 320671, '南通经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (904, 320600, 320681, '启东市', 3);
INSERT INTO `wj_city` VALUES (905, 320600, 320682, '如皋市', 3);
INSERT INTO `wj_city` VALUES (906, 320600, 320685, '海安市', 3);
INSERT INTO `wj_city` VALUES (907, 320000, 320700, '连云港市', 2);
INSERT INTO `wj_city` VALUES (908, 320700, 320703, '连云区', 3);
INSERT INTO `wj_city` VALUES (909, 320700, 320706, '海州区', 3);
INSERT INTO `wj_city` VALUES (910, 320700, 320707, '赣榆区', 3);
INSERT INTO `wj_city` VALUES (911, 320700, 320722, '东海县', 3);
INSERT INTO `wj_city` VALUES (912, 320700, 320723, '灌云县', 3);
INSERT INTO `wj_city` VALUES (913, 320700, 320724, '灌南县', 3);
INSERT INTO `wj_city` VALUES (914, 320700, 320771, '连云港经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (915, 320000, 320800, '淮安市', 2);
INSERT INTO `wj_city` VALUES (916, 320800, 320803, '淮安区', 3);
INSERT INTO `wj_city` VALUES (917, 320800, 320804, '淮阴区', 3);
INSERT INTO `wj_city` VALUES (918, 320800, 320812, '清江浦区', 3);
INSERT INTO `wj_city` VALUES (919, 320800, 320813, '洪泽区', 3);
INSERT INTO `wj_city` VALUES (920, 320800, 320826, '涟水县', 3);
INSERT INTO `wj_city` VALUES (921, 320800, 320830, '盱眙县', 3);
INSERT INTO `wj_city` VALUES (922, 320800, 320831, '金湖县', 3);
INSERT INTO `wj_city` VALUES (923, 320800, 320871, '淮安经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (924, 320000, 320900, '盐城市', 2);
INSERT INTO `wj_city` VALUES (925, 320900, 320902, '亭湖区', 3);
INSERT INTO `wj_city` VALUES (926, 320900, 320903, '盐都区', 3);
INSERT INTO `wj_city` VALUES (927, 320900, 320904, '大丰区', 3);
INSERT INTO `wj_city` VALUES (928, 320900, 320921, '响水县', 3);
INSERT INTO `wj_city` VALUES (929, 320900, 320922, '滨海县', 3);
INSERT INTO `wj_city` VALUES (930, 320900, 320923, '阜宁县', 3);
INSERT INTO `wj_city` VALUES (931, 320900, 320924, '射阳县', 3);
INSERT INTO `wj_city` VALUES (932, 320900, 320925, '建湖县', 3);
INSERT INTO `wj_city` VALUES (933, 320900, 320971, '盐城经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (934, 320900, 320981, '东台市', 3);
INSERT INTO `wj_city` VALUES (935, 320000, 321000, '扬州市', 2);
INSERT INTO `wj_city` VALUES (936, 321000, 321002, '广陵区', 3);
INSERT INTO `wj_city` VALUES (937, 321000, 321003, '邗江区', 3);
INSERT INTO `wj_city` VALUES (938, 321000, 321012, '江都区', 3);
INSERT INTO `wj_city` VALUES (939, 321000, 321023, '宝应县', 3);
INSERT INTO `wj_city` VALUES (940, 321000, 321071, '扬州经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (941, 321000, 321081, '仪征市', 3);
INSERT INTO `wj_city` VALUES (942, 321000, 321084, '高邮市', 3);
INSERT INTO `wj_city` VALUES (943, 320000, 321100, '镇江市', 2);
INSERT INTO `wj_city` VALUES (944, 321100, 321102, '京口区', 3);
INSERT INTO `wj_city` VALUES (945, 321100, 321111, '润州区', 3);
INSERT INTO `wj_city` VALUES (946, 321100, 321112, '丹徒区', 3);
INSERT INTO `wj_city` VALUES (947, 321100, 321171, '镇江新区', 3);
INSERT INTO `wj_city` VALUES (948, 321100, 321181, '丹阳市', 3);
INSERT INTO `wj_city` VALUES (949, 321100, 321182, '扬中市', 3);
INSERT INTO `wj_city` VALUES (950, 321100, 321183, '句容市', 3);
INSERT INTO `wj_city` VALUES (951, 320000, 321200, '泰州市', 2);
INSERT INTO `wj_city` VALUES (952, 321200, 321202, '海陵区', 3);
INSERT INTO `wj_city` VALUES (953, 321200, 321203, '高港区', 3);
INSERT INTO `wj_city` VALUES (954, 321200, 321204, '姜堰区', 3);
INSERT INTO `wj_city` VALUES (955, 321200, 321281, '兴化市', 3);
INSERT INTO `wj_city` VALUES (956, 321200, 321282, '靖江市', 3);
INSERT INTO `wj_city` VALUES (957, 321200, 321283, '泰兴市', 3);
INSERT INTO `wj_city` VALUES (958, 320000, 321300, '宿迁市', 2);
INSERT INTO `wj_city` VALUES (959, 321300, 321302, '宿城区', 3);
INSERT INTO `wj_city` VALUES (960, 321300, 321311, '宿豫区', 3);
INSERT INTO `wj_city` VALUES (961, 321300, 321322, '沭阳县', 3);
INSERT INTO `wj_city` VALUES (962, 321300, 321323, '泗阳县', 3);
INSERT INTO `wj_city` VALUES (963, 321300, 321324, '泗洪县', 3);
INSERT INTO `wj_city` VALUES (964, 321300, 321371, '宿迁经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (965, 0, 330000, '浙江省', 1);
INSERT INTO `wj_city` VALUES (966, 330000, 330100, '杭州市', 2);
INSERT INTO `wj_city` VALUES (967, 330100, 330102, '上城区', 3);
INSERT INTO `wj_city` VALUES (968, 330100, 330105, '拱墅区', 3);
INSERT INTO `wj_city` VALUES (969, 330100, 330106, '西湖区', 3);
INSERT INTO `wj_city` VALUES (970, 330100, 330108, '滨江区', 3);
INSERT INTO `wj_city` VALUES (971, 330100, 330109, '萧山区', 3);
INSERT INTO `wj_city` VALUES (972, 330100, 330110, '余杭区', 3);
INSERT INTO `wj_city` VALUES (973, 330100, 330111, '富阳区', 3);
INSERT INTO `wj_city` VALUES (974, 330100, 330112, '临安区', 3);
INSERT INTO `wj_city` VALUES (975, 330100, 330113, '临平区', 3);
INSERT INTO `wj_city` VALUES (976, 330100, 330114, '钱塘区', 3);
INSERT INTO `wj_city` VALUES (977, 330100, 330122, '桐庐县', 3);
INSERT INTO `wj_city` VALUES (978, 330100, 330127, '淳安县', 3);
INSERT INTO `wj_city` VALUES (979, 330100, 330182, '建德市', 3);
INSERT INTO `wj_city` VALUES (980, 330000, 330200, '宁波市', 2);
INSERT INTO `wj_city` VALUES (981, 330200, 330203, '海曙区', 3);
INSERT INTO `wj_city` VALUES (982, 330200, 330205, '江北区', 3);
INSERT INTO `wj_city` VALUES (983, 330200, 330206, '北仑区', 3);
INSERT INTO `wj_city` VALUES (984, 330200, 330211, '镇海区', 3);
INSERT INTO `wj_city` VALUES (985, 330200, 330212, '鄞州区', 3);
INSERT INTO `wj_city` VALUES (986, 330200, 330213, '奉化区', 3);
INSERT INTO `wj_city` VALUES (987, 330200, 330225, '象山县', 3);
INSERT INTO `wj_city` VALUES (988, 330200, 330226, '宁海县', 3);
INSERT INTO `wj_city` VALUES (989, 330200, 330281, '余姚市', 3);
INSERT INTO `wj_city` VALUES (990, 330200, 330282, '慈溪市', 3);
INSERT INTO `wj_city` VALUES (991, 330000, 330300, '温州市', 2);
INSERT INTO `wj_city` VALUES (992, 330300, 330302, '鹿城区', 3);
INSERT INTO `wj_city` VALUES (993, 330300, 330303, '龙湾区', 3);
INSERT INTO `wj_city` VALUES (994, 330300, 330304, '瓯海区', 3);
INSERT INTO `wj_city` VALUES (995, 330300, 330305, '洞头区', 3);
INSERT INTO `wj_city` VALUES (996, 330300, 330324, '永嘉县', 3);
INSERT INTO `wj_city` VALUES (997, 330300, 330326, '平阳县', 3);
INSERT INTO `wj_city` VALUES (998, 330300, 330327, '苍南县', 3);
INSERT INTO `wj_city` VALUES (999, 330300, 330328, '文成县', 3);
INSERT INTO `wj_city` VALUES (1000, 330300, 330329, '泰顺县', 3);
INSERT INTO `wj_city` VALUES (1001, 330300, 330381, '瑞安市', 3);
INSERT INTO `wj_city` VALUES (1002, 330300, 330382, '乐清市', 3);
INSERT INTO `wj_city` VALUES (1003, 330300, 330383, '龙港市', 3);
INSERT INTO `wj_city` VALUES (1004, 330000, 330400, '嘉兴市', 2);
INSERT INTO `wj_city` VALUES (1005, 330400, 330402, '南湖区', 3);
INSERT INTO `wj_city` VALUES (1006, 330400, 330411, '秀洲区', 3);
INSERT INTO `wj_city` VALUES (1007, 330400, 330421, '嘉善县', 3);
INSERT INTO `wj_city` VALUES (1008, 330400, 330424, '海盐县', 3);
INSERT INTO `wj_city` VALUES (1009, 330400, 330481, '海宁市', 3);
INSERT INTO `wj_city` VALUES (1010, 330400, 330482, '平湖市', 3);
INSERT INTO `wj_city` VALUES (1011, 330400, 330483, '桐乡市', 3);
INSERT INTO `wj_city` VALUES (1012, 330000, 330500, '湖州市', 2);
INSERT INTO `wj_city` VALUES (1013, 330500, 330502, '吴兴区', 3);
INSERT INTO `wj_city` VALUES (1014, 330500, 330503, '南浔区', 3);
INSERT INTO `wj_city` VALUES (1015, 330500, 330521, '德清县', 3);
INSERT INTO `wj_city` VALUES (1016, 330500, 330522, '长兴县', 3);
INSERT INTO `wj_city` VALUES (1017, 330500, 330523, '安吉县', 3);
INSERT INTO `wj_city` VALUES (1018, 330000, 330600, '绍兴市', 2);
INSERT INTO `wj_city` VALUES (1019, 330600, 330602, '越城区', 3);
INSERT INTO `wj_city` VALUES (1020, 330600, 330603, '柯桥区', 3);
INSERT INTO `wj_city` VALUES (1021, 330600, 330604, '上虞区', 3);
INSERT INTO `wj_city` VALUES (1022, 330600, 330624, '新昌县', 3);
INSERT INTO `wj_city` VALUES (1023, 330600, 330681, '诸暨市', 3);
INSERT INTO `wj_city` VALUES (1024, 330600, 330683, '嵊州市', 3);
INSERT INTO `wj_city` VALUES (1025, 330000, 330700, '金华市', 2);
INSERT INTO `wj_city` VALUES (1026, 330700, 330702, '婺城区', 3);
INSERT INTO `wj_city` VALUES (1027, 330700, 330703, '金东区', 3);
INSERT INTO `wj_city` VALUES (1028, 330700, 330723, '武义县', 3);
INSERT INTO `wj_city` VALUES (1029, 330700, 330726, '浦江县', 3);
INSERT INTO `wj_city` VALUES (1030, 330700, 330727, '磐安县', 3);
INSERT INTO `wj_city` VALUES (1031, 330700, 330781, '兰溪市', 3);
INSERT INTO `wj_city` VALUES (1032, 330700, 330782, '义乌市', 3);
INSERT INTO `wj_city` VALUES (1033, 330700, 330783, '东阳市', 3);
INSERT INTO `wj_city` VALUES (1034, 330700, 330784, '永康市', 3);
INSERT INTO `wj_city` VALUES (1035, 330000, 330800, '衢州市', 2);
INSERT INTO `wj_city` VALUES (1036, 330800, 330802, '柯城区', 3);
INSERT INTO `wj_city` VALUES (1037, 330800, 330803, '衢江区', 3);
INSERT INTO `wj_city` VALUES (1038, 330800, 330822, '常山县', 3);
INSERT INTO `wj_city` VALUES (1039, 330800, 330824, '开化县', 3);
INSERT INTO `wj_city` VALUES (1040, 330800, 330825, '龙游县', 3);
INSERT INTO `wj_city` VALUES (1041, 330800, 330881, '江山市', 3);
INSERT INTO `wj_city` VALUES (1042, 330000, 330900, '舟山市', 2);
INSERT INTO `wj_city` VALUES (1043, 330900, 330902, '定海区', 3);
INSERT INTO `wj_city` VALUES (1044, 330900, 330903, '普陀区', 3);
INSERT INTO `wj_city` VALUES (1045, 330900, 330921, '岱山县', 3);
INSERT INTO `wj_city` VALUES (1046, 330900, 330922, '嵊泗县', 3);
INSERT INTO `wj_city` VALUES (1047, 330000, 331000, '台州市', 2);
INSERT INTO `wj_city` VALUES (1048, 331000, 331002, '椒江区', 3);
INSERT INTO `wj_city` VALUES (1049, 331000, 331003, '黄岩区', 3);
INSERT INTO `wj_city` VALUES (1050, 331000, 331004, '路桥区', 3);
INSERT INTO `wj_city` VALUES (1051, 331000, 331022, '三门县', 3);
INSERT INTO `wj_city` VALUES (1052, 331000, 331023, '天台县', 3);
INSERT INTO `wj_city` VALUES (1053, 331000, 331024, '仙居县', 3);
INSERT INTO `wj_city` VALUES (1054, 331000, 331081, '温岭市', 3);
INSERT INTO `wj_city` VALUES (1055, 331000, 331082, '临海市', 3);
INSERT INTO `wj_city` VALUES (1056, 331000, 331083, '玉环市', 3);
INSERT INTO `wj_city` VALUES (1057, 330000, 331100, '丽水市', 2);
INSERT INTO `wj_city` VALUES (1058, 331100, 331102, '莲都区', 3);
INSERT INTO `wj_city` VALUES (1059, 331100, 331121, '青田县', 3);
INSERT INTO `wj_city` VALUES (1060, 331100, 331122, '缙云县', 3);
INSERT INTO `wj_city` VALUES (1061, 331100, 331123, '遂昌县', 3);
INSERT INTO `wj_city` VALUES (1062, 331100, 331124, '松阳县', 3);
INSERT INTO `wj_city` VALUES (1063, 331100, 331125, '云和县', 3);
INSERT INTO `wj_city` VALUES (1064, 331100, 331126, '庆元县', 3);
INSERT INTO `wj_city` VALUES (1065, 331100, 331127, '景宁畲族自治县', 3);
INSERT INTO `wj_city` VALUES (1066, 331100, 331181, '龙泉市', 3);
INSERT INTO `wj_city` VALUES (1067, 0, 340000, '安徽省', 1);
INSERT INTO `wj_city` VALUES (1068, 340000, 340100, '合肥市', 2);
INSERT INTO `wj_city` VALUES (1069, 340100, 340102, '瑶海区', 3);
INSERT INTO `wj_city` VALUES (1070, 340100, 340103, '庐阳区', 3);
INSERT INTO `wj_city` VALUES (1071, 340100, 340104, '蜀山区', 3);
INSERT INTO `wj_city` VALUES (1072, 340100, 340111, '包河区', 3);
INSERT INTO `wj_city` VALUES (1073, 340100, 340121, '长丰县', 3);
INSERT INTO `wj_city` VALUES (1074, 340100, 340122, '肥东县', 3);
INSERT INTO `wj_city` VALUES (1075, 340100, 340123, '肥西县', 3);
INSERT INTO `wj_city` VALUES (1076, 340100, 340124, '庐江县', 3);
INSERT INTO `wj_city` VALUES (1077, 340100, 340176, '合肥高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (1078, 340100, 340177, '合肥经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (1079, 340100, 340178, '合肥新站高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (1080, 340100, 340181, '巢湖市', 3);
INSERT INTO `wj_city` VALUES (1081, 340000, 340200, '芜湖市', 2);
INSERT INTO `wj_city` VALUES (1082, 340200, 340202, '镜湖区', 3);
INSERT INTO `wj_city` VALUES (1083, 340200, 340207, '鸠江区', 3);
INSERT INTO `wj_city` VALUES (1084, 340200, 340209, '弋江区', 3);
INSERT INTO `wj_city` VALUES (1085, 340200, 340210, '湾沚区', 3);
INSERT INTO `wj_city` VALUES (1086, 340200, 340212, '繁昌区', 3);
INSERT INTO `wj_city` VALUES (1087, 340200, 340223, '南陵县', 3);
INSERT INTO `wj_city` VALUES (1088, 340200, 340271, '芜湖经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (1089, 340200, 340272, '安徽芜湖三山经济开发区', 3);
INSERT INTO `wj_city` VALUES (1090, 340200, 340281, '无为市', 3);
INSERT INTO `wj_city` VALUES (1091, 340000, 340300, '蚌埠市', 2);
INSERT INTO `wj_city` VALUES (1092, 340300, 340302, '龙子湖区', 3);
INSERT INTO `wj_city` VALUES (1093, 340300, 340303, '蚌山区', 3);
INSERT INTO `wj_city` VALUES (1094, 340300, 340304, '禹会区', 3);
INSERT INTO `wj_city` VALUES (1095, 340300, 340311, '淮上区', 3);
INSERT INTO `wj_city` VALUES (1096, 340300, 340321, '怀远县', 3);
INSERT INTO `wj_city` VALUES (1097, 340300, 340322, '五河县', 3);
INSERT INTO `wj_city` VALUES (1098, 340300, 340323, '固镇县', 3);
INSERT INTO `wj_city` VALUES (1099, 340300, 340371, '蚌埠市高新技术开发区', 3);
INSERT INTO `wj_city` VALUES (1100, 340300, 340372, '蚌埠市经济开发区', 3);
INSERT INTO `wj_city` VALUES (1101, 340000, 340400, '淮南市', 2);
INSERT INTO `wj_city` VALUES (1102, 340400, 340402, '大通区', 3);
INSERT INTO `wj_city` VALUES (1103, 340400, 340403, '田家庵区', 3);
INSERT INTO `wj_city` VALUES (1104, 340400, 340404, '谢家集区', 3);
INSERT INTO `wj_city` VALUES (1105, 340400, 340405, '八公山区', 3);
INSERT INTO `wj_city` VALUES (1106, 340400, 340406, '潘集区', 3);
INSERT INTO `wj_city` VALUES (1107, 340400, 340421, '凤台县', 3);
INSERT INTO `wj_city` VALUES (1108, 340400, 340422, '寿县', 3);
INSERT INTO `wj_city` VALUES (1109, 340000, 340500, '马鞍山市', 2);
INSERT INTO `wj_city` VALUES (1110, 340500, 340503, '花山区', 3);
INSERT INTO `wj_city` VALUES (1111, 340500, 340504, '雨山区', 3);
INSERT INTO `wj_city` VALUES (1112, 340500, 340506, '博望区', 3);
INSERT INTO `wj_city` VALUES (1113, 340500, 340521, '当涂县', 3);
INSERT INTO `wj_city` VALUES (1114, 340500, 340522, '含山县', 3);
INSERT INTO `wj_city` VALUES (1115, 340500, 340523, '和县', 3);
INSERT INTO `wj_city` VALUES (1116, 340000, 340600, '淮北市', 2);
INSERT INTO `wj_city` VALUES (1117, 340600, 340602, '杜集区', 3);
INSERT INTO `wj_city` VALUES (1118, 340600, 340603, '相山区', 3);
INSERT INTO `wj_city` VALUES (1119, 340600, 340604, '烈山区', 3);
INSERT INTO `wj_city` VALUES (1120, 340600, 340621, '濉溪县', 3);
INSERT INTO `wj_city` VALUES (1121, 340000, 340700, '铜陵市', 2);
INSERT INTO `wj_city` VALUES (1122, 340700, 340705, '铜官区', 3);
INSERT INTO `wj_city` VALUES (1123, 340700, 340706, '义安区', 3);
INSERT INTO `wj_city` VALUES (1124, 340700, 340711, '郊区', 3);
INSERT INTO `wj_city` VALUES (1125, 340700, 340722, '枞阳县', 3);
INSERT INTO `wj_city` VALUES (1126, 340000, 340800, '安庆市', 2);
INSERT INTO `wj_city` VALUES (1127, 340800, 340802, '迎江区', 3);
INSERT INTO `wj_city` VALUES (1128, 340800, 340803, '大观区', 3);
INSERT INTO `wj_city` VALUES (1129, 340800, 340811, '宜秀区', 3);
INSERT INTO `wj_city` VALUES (1130, 340800, 340822, '怀宁县', 3);
INSERT INTO `wj_city` VALUES (1131, 340800, 340825, '太湖县', 3);
INSERT INTO `wj_city` VALUES (1132, 340800, 340826, '宿松县', 3);
INSERT INTO `wj_city` VALUES (1133, 340800, 340827, '望江县', 3);
INSERT INTO `wj_city` VALUES (1134, 340800, 340828, '岳西县', 3);
INSERT INTO `wj_city` VALUES (1135, 340800, 340871, '安徽安庆经济开发区', 3);
INSERT INTO `wj_city` VALUES (1136, 340800, 340881, '桐城市', 3);
INSERT INTO `wj_city` VALUES (1137, 340800, 340882, '潜山市', 3);
INSERT INTO `wj_city` VALUES (1138, 340000, 341000, '黄山市', 2);
INSERT INTO `wj_city` VALUES (1139, 341000, 341002, '屯溪区', 3);
INSERT INTO `wj_city` VALUES (1140, 341000, 341003, '黄山区', 3);
INSERT INTO `wj_city` VALUES (1141, 341000, 341004, '徽州区', 3);
INSERT INTO `wj_city` VALUES (1142, 341000, 341021, '歙县', 3);
INSERT INTO `wj_city` VALUES (1143, 341000, 341022, '休宁县', 3);
INSERT INTO `wj_city` VALUES (1144, 341000, 341023, '黟县', 3);
INSERT INTO `wj_city` VALUES (1145, 341000, 341024, '祁门县', 3);
INSERT INTO `wj_city` VALUES (1146, 340000, 341100, '滁州市', 2);
INSERT INTO `wj_city` VALUES (1147, 341100, 341102, '琅琊区', 3);
INSERT INTO `wj_city` VALUES (1148, 341100, 341103, '南谯区', 3);
INSERT INTO `wj_city` VALUES (1149, 341100, 341122, '来安县', 3);
INSERT INTO `wj_city` VALUES (1150, 341100, 341124, '全椒县', 3);
INSERT INTO `wj_city` VALUES (1151, 341100, 341125, '定远县', 3);
INSERT INTO `wj_city` VALUES (1152, 341100, 341126, '凤阳县', 3);
INSERT INTO `wj_city` VALUES (1153, 341100, 341171, '中新苏滁高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (1154, 341100, 341172, '滁州经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (1155, 341100, 341181, '天长市', 3);
INSERT INTO `wj_city` VALUES (1156, 341100, 341182, '明光市', 3);
INSERT INTO `wj_city` VALUES (1157, 340000, 341200, '阜阳市', 2);
INSERT INTO `wj_city` VALUES (1158, 341200, 341202, '颍州区', 3);
INSERT INTO `wj_city` VALUES (1159, 341200, 341203, '颍东区', 3);
INSERT INTO `wj_city` VALUES (1160, 341200, 341204, '颍泉区', 3);
INSERT INTO `wj_city` VALUES (1161, 341200, 341221, '临泉县', 3);
INSERT INTO `wj_city` VALUES (1162, 341200, 341222, '太和县', 3);
INSERT INTO `wj_city` VALUES (1163, 341200, 341225, '阜南县', 3);
INSERT INTO `wj_city` VALUES (1164, 341200, 341226, '颍上县', 3);
INSERT INTO `wj_city` VALUES (1165, 341200, 341271, '阜阳合肥现代产业园区', 3);
INSERT INTO `wj_city` VALUES (1166, 341200, 341272, '阜阳经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (1167, 341200, 341282, '界首市', 3);
INSERT INTO `wj_city` VALUES (1168, 340000, 341300, '宿州市', 2);
INSERT INTO `wj_city` VALUES (1169, 341300, 341302, '埇桥区', 3);
INSERT INTO `wj_city` VALUES (1170, 341300, 341321, '砀山县', 3);
INSERT INTO `wj_city` VALUES (1171, 341300, 341322, '萧县', 3);
INSERT INTO `wj_city` VALUES (1172, 341300, 341323, '灵璧县', 3);
INSERT INTO `wj_city` VALUES (1173, 341300, 341324, '泗县', 3);
INSERT INTO `wj_city` VALUES (1174, 341300, 341371, '宿州马鞍山现代产业园区', 3);
INSERT INTO `wj_city` VALUES (1175, 341300, 341372, '宿州经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (1176, 340000, 341500, '六安市', 2);
INSERT INTO `wj_city` VALUES (1177, 341500, 341502, '金安区', 3);
INSERT INTO `wj_city` VALUES (1178, 341500, 341503, '裕安区', 3);
INSERT INTO `wj_city` VALUES (1179, 341500, 341504, '叶集区', 3);
INSERT INTO `wj_city` VALUES (1180, 341500, 341522, '霍邱县', 3);
INSERT INTO `wj_city` VALUES (1181, 341500, 341523, '舒城县', 3);
INSERT INTO `wj_city` VALUES (1182, 341500, 341524, '金寨县', 3);
INSERT INTO `wj_city` VALUES (1183, 341500, 341525, '霍山县', 3);
INSERT INTO `wj_city` VALUES (1184, 340000, 341600, '亳州市', 2);
INSERT INTO `wj_city` VALUES (1185, 341600, 341602, '谯城区', 3);
INSERT INTO `wj_city` VALUES (1186, 341600, 341621, '涡阳县', 3);
INSERT INTO `wj_city` VALUES (1187, 341600, 341622, '蒙城县', 3);
INSERT INTO `wj_city` VALUES (1188, 341600, 341623, '利辛县', 3);
INSERT INTO `wj_city` VALUES (1189, 340000, 341700, '池州市', 2);
INSERT INTO `wj_city` VALUES (1190, 341700, 341702, '贵池区', 3);
INSERT INTO `wj_city` VALUES (1191, 341700, 341721, '东至县', 3);
INSERT INTO `wj_city` VALUES (1192, 341700, 341722, '石台县', 3);
INSERT INTO `wj_city` VALUES (1193, 341700, 341723, '青阳县', 3);
INSERT INTO `wj_city` VALUES (1194, 340000, 341800, '宣城市', 2);
INSERT INTO `wj_city` VALUES (1195, 341800, 341802, '宣州区', 3);
INSERT INTO `wj_city` VALUES (1196, 341800, 341821, '郎溪县', 3);
INSERT INTO `wj_city` VALUES (1197, 341800, 341823, '泾县', 3);
INSERT INTO `wj_city` VALUES (1198, 341800, 341824, '绩溪县', 3);
INSERT INTO `wj_city` VALUES (1199, 341800, 341825, '旌德县', 3);
INSERT INTO `wj_city` VALUES (1200, 341800, 341871, '宣城市经济开发区', 3);
INSERT INTO `wj_city` VALUES (1201, 341800, 341881, '宁国市', 3);
INSERT INTO `wj_city` VALUES (1202, 341800, 341882, '广德市', 3);
INSERT INTO `wj_city` VALUES (1203, 0, 350000, '福建省', 1);
INSERT INTO `wj_city` VALUES (1204, 350000, 350100, '福州市', 2);
INSERT INTO `wj_city` VALUES (1205, 350100, 350102, '鼓楼区', 3);
INSERT INTO `wj_city` VALUES (1206, 350100, 350103, '台江区', 3);
INSERT INTO `wj_city` VALUES (1207, 350100, 350104, '仓山区', 3);
INSERT INTO `wj_city` VALUES (1208, 350100, 350105, '马尾区', 3);
INSERT INTO `wj_city` VALUES (1209, 350100, 350111, '晋安区', 3);
INSERT INTO `wj_city` VALUES (1210, 350100, 350112, '长乐区', 3);
INSERT INTO `wj_city` VALUES (1211, 350100, 350121, '闽侯县', 3);
INSERT INTO `wj_city` VALUES (1212, 350100, 350122, '连江县', 3);
INSERT INTO `wj_city` VALUES (1213, 350100, 350123, '罗源县', 3);
INSERT INTO `wj_city` VALUES (1214, 350100, 350124, '闽清县', 3);
INSERT INTO `wj_city` VALUES (1215, 350100, 350125, '永泰县', 3);
INSERT INTO `wj_city` VALUES (1216, 350100, 350128, '平潭县', 3);
INSERT INTO `wj_city` VALUES (1217, 350100, 350181, '福清市', 3);
INSERT INTO `wj_city` VALUES (1218, 350000, 350200, '厦门市', 2);
INSERT INTO `wj_city` VALUES (1219, 350200, 350203, '思明区', 3);
INSERT INTO `wj_city` VALUES (1220, 350200, 350205, '海沧区', 3);
INSERT INTO `wj_city` VALUES (1221, 350200, 350206, '湖里区', 3);
INSERT INTO `wj_city` VALUES (1222, 350200, 350211, '集美区', 3);
INSERT INTO `wj_city` VALUES (1223, 350200, 350212, '同安区', 3);
INSERT INTO `wj_city` VALUES (1224, 350200, 350213, '翔安区', 3);
INSERT INTO `wj_city` VALUES (1225, 350000, 350300, '莆田市', 2);
INSERT INTO `wj_city` VALUES (1226, 350300, 350302, '城厢区', 3);
INSERT INTO `wj_city` VALUES (1227, 350300, 350303, '涵江区', 3);
INSERT INTO `wj_city` VALUES (1228, 350300, 350304, '荔城区', 3);
INSERT INTO `wj_city` VALUES (1229, 350300, 350305, '秀屿区', 3);
INSERT INTO `wj_city` VALUES (1230, 350300, 350322, '仙游县', 3);
INSERT INTO `wj_city` VALUES (1231, 350000, 350400, '三明市', 2);
INSERT INTO `wj_city` VALUES (1232, 350400, 350404, '三元区', 3);
INSERT INTO `wj_city` VALUES (1233, 350400, 350405, '沙县区', 3);
INSERT INTO `wj_city` VALUES (1234, 350400, 350421, '明溪县', 3);
INSERT INTO `wj_city` VALUES (1235, 350400, 350423, '清流县', 3);
INSERT INTO `wj_city` VALUES (1236, 350400, 350424, '宁化县', 3);
INSERT INTO `wj_city` VALUES (1237, 350400, 350425, '大田县', 3);
INSERT INTO `wj_city` VALUES (1238, 350400, 350426, '尤溪县', 3);
INSERT INTO `wj_city` VALUES (1239, 350400, 350428, '将乐县', 3);
INSERT INTO `wj_city` VALUES (1240, 350400, 350429, '泰宁县', 3);
INSERT INTO `wj_city` VALUES (1241, 350400, 350430, '建宁县', 3);
INSERT INTO `wj_city` VALUES (1242, 350400, 350481, '永安市', 3);
INSERT INTO `wj_city` VALUES (1243, 350000, 350500, '泉州市', 2);
INSERT INTO `wj_city` VALUES (1244, 350500, 350502, '鲤城区', 3);
INSERT INTO `wj_city` VALUES (1245, 350500, 350503, '丰泽区', 3);
INSERT INTO `wj_city` VALUES (1246, 350500, 350504, '洛江区', 3);
INSERT INTO `wj_city` VALUES (1247, 350500, 350505, '泉港区', 3);
INSERT INTO `wj_city` VALUES (1248, 350500, 350521, '惠安县', 3);
INSERT INTO `wj_city` VALUES (1249, 350500, 350524, '安溪县', 3);
INSERT INTO `wj_city` VALUES (1250, 350500, 350525, '永春县', 3);
INSERT INTO `wj_city` VALUES (1251, 350500, 350526, '德化县', 3);
INSERT INTO `wj_city` VALUES (1252, 350500, 350527, '金门县', 3);
INSERT INTO `wj_city` VALUES (1253, 350500, 350581, '石狮市', 3);
INSERT INTO `wj_city` VALUES (1254, 350500, 350582, '晋江市', 3);
INSERT INTO `wj_city` VALUES (1255, 350500, 350583, '南安市', 3);
INSERT INTO `wj_city` VALUES (1256, 350000, 350600, '漳州市', 2);
INSERT INTO `wj_city` VALUES (1257, 350600, 350602, '芗城区', 3);
INSERT INTO `wj_city` VALUES (1258, 350600, 350603, '龙文区', 3);
INSERT INTO `wj_city` VALUES (1259, 350600, 350604, '龙海区', 3);
INSERT INTO `wj_city` VALUES (1260, 350600, 350605, '长泰区', 3);
INSERT INTO `wj_city` VALUES (1261, 350600, 350622, '云霄县', 3);
INSERT INTO `wj_city` VALUES (1262, 350600, 350623, '漳浦县', 3);
INSERT INTO `wj_city` VALUES (1263, 350600, 350624, '诏安县', 3);
INSERT INTO `wj_city` VALUES (1264, 350600, 350626, '东山县', 3);
INSERT INTO `wj_city` VALUES (1265, 350600, 350627, '南靖县', 3);
INSERT INTO `wj_city` VALUES (1266, 350600, 350628, '平和县', 3);
INSERT INTO `wj_city` VALUES (1267, 350600, 350629, '华安县', 3);
INSERT INTO `wj_city` VALUES (1268, 350000, 350700, '南平市', 2);
INSERT INTO `wj_city` VALUES (1269, 350700, 350702, '延平区', 3);
INSERT INTO `wj_city` VALUES (1270, 350700, 350703, '建阳区', 3);
INSERT INTO `wj_city` VALUES (1271, 350700, 350721, '顺昌县', 3);
INSERT INTO `wj_city` VALUES (1272, 350700, 350722, '浦城县', 3);
INSERT INTO `wj_city` VALUES (1273, 350700, 350723, '光泽县', 3);
INSERT INTO `wj_city` VALUES (1274, 350700, 350724, '松溪县', 3);
INSERT INTO `wj_city` VALUES (1275, 350700, 350725, '政和县', 3);
INSERT INTO `wj_city` VALUES (1276, 350700, 350781, '邵武市', 3);
INSERT INTO `wj_city` VALUES (1277, 350700, 350782, '武夷山市', 3);
INSERT INTO `wj_city` VALUES (1278, 350700, 350783, '建瓯市', 3);
INSERT INTO `wj_city` VALUES (1279, 350000, 350800, '龙岩市', 2);
INSERT INTO `wj_city` VALUES (1280, 350800, 350802, '新罗区', 3);
INSERT INTO `wj_city` VALUES (1281, 350800, 350803, '永定区', 3);
INSERT INTO `wj_city` VALUES (1282, 350800, 350821, '长汀县', 3);
INSERT INTO `wj_city` VALUES (1283, 350800, 350823, '上杭县', 3);
INSERT INTO `wj_city` VALUES (1284, 350800, 350824, '武平县', 3);
INSERT INTO `wj_city` VALUES (1285, 350800, 350825, '连城县', 3);
INSERT INTO `wj_city` VALUES (1286, 350800, 350881, '漳平市', 3);
INSERT INTO `wj_city` VALUES (1287, 350000, 350900, '宁德市', 2);
INSERT INTO `wj_city` VALUES (1288, 350900, 350902, '蕉城区', 3);
INSERT INTO `wj_city` VALUES (1289, 350900, 350921, '霞浦县', 3);
INSERT INTO `wj_city` VALUES (1290, 350900, 350922, '古田县', 3);
INSERT INTO `wj_city` VALUES (1291, 350900, 350923, '屏南县', 3);
INSERT INTO `wj_city` VALUES (1292, 350900, 350924, '寿宁县', 3);
INSERT INTO `wj_city` VALUES (1293, 350900, 350925, '周宁县', 3);
INSERT INTO `wj_city` VALUES (1294, 350900, 350926, '柘荣县', 3);
INSERT INTO `wj_city` VALUES (1295, 350900, 350981, '福安市', 3);
INSERT INTO `wj_city` VALUES (1296, 350900, 350982, '福鼎市', 3);
INSERT INTO `wj_city` VALUES (1297, 0, 360000, '江西省', 1);
INSERT INTO `wj_city` VALUES (1298, 360000, 360100, '南昌市', 2);
INSERT INTO `wj_city` VALUES (1299, 360100, 360102, '东湖区', 3);
INSERT INTO `wj_city` VALUES (1300, 360100, 360103, '西湖区', 3);
INSERT INTO `wj_city` VALUES (1301, 360100, 360104, '青云谱区', 3);
INSERT INTO `wj_city` VALUES (1302, 360100, 360111, '青山湖区', 3);
INSERT INTO `wj_city` VALUES (1303, 360100, 360112, '新建区', 3);
INSERT INTO `wj_city` VALUES (1304, 360100, 360113, '红谷滩区', 3);
INSERT INTO `wj_city` VALUES (1305, 360100, 360121, '南昌县', 3);
INSERT INTO `wj_city` VALUES (1306, 360100, 360123, '安义县', 3);
INSERT INTO `wj_city` VALUES (1307, 360100, 360124, '进贤县', 3);
INSERT INTO `wj_city` VALUES (1308, 360000, 360200, '景德镇市', 2);
INSERT INTO `wj_city` VALUES (1309, 360200, 360202, '昌江区', 3);
INSERT INTO `wj_city` VALUES (1310, 360200, 360203, '珠山区', 3);
INSERT INTO `wj_city` VALUES (1311, 360200, 360222, '浮梁县', 3);
INSERT INTO `wj_city` VALUES (1312, 360200, 360281, '乐平市', 3);
INSERT INTO `wj_city` VALUES (1313, 360000, 360300, '萍乡市', 2);
INSERT INTO `wj_city` VALUES (1314, 360300, 360302, '安源区', 3);
INSERT INTO `wj_city` VALUES (1315, 360300, 360313, '湘东区', 3);
INSERT INTO `wj_city` VALUES (1316, 360300, 360321, '莲花县', 3);
INSERT INTO `wj_city` VALUES (1317, 360300, 360322, '上栗县', 3);
INSERT INTO `wj_city` VALUES (1318, 360300, 360323, '芦溪县', 3);
INSERT INTO `wj_city` VALUES (1319, 360000, 360400, '九江市', 2);
INSERT INTO `wj_city` VALUES (1320, 360400, 360402, '濂溪区', 3);
INSERT INTO `wj_city` VALUES (1321, 360400, 360403, '浔阳区', 3);
INSERT INTO `wj_city` VALUES (1322, 360400, 360404, '柴桑区', 3);
INSERT INTO `wj_city` VALUES (1323, 360400, 360423, '武宁县', 3);
INSERT INTO `wj_city` VALUES (1324, 360400, 360424, '修水县', 3);
INSERT INTO `wj_city` VALUES (1325, 360400, 360425, '永修县', 3);
INSERT INTO `wj_city` VALUES (1326, 360400, 360426, '德安县', 3);
INSERT INTO `wj_city` VALUES (1327, 360400, 360428, '都昌县', 3);
INSERT INTO `wj_city` VALUES (1328, 360400, 360429, '湖口县', 3);
INSERT INTO `wj_city` VALUES (1329, 360400, 360430, '彭泽县', 3);
INSERT INTO `wj_city` VALUES (1330, 360400, 360481, '瑞昌市', 3);
INSERT INTO `wj_city` VALUES (1331, 360400, 360482, '共青城市', 3);
INSERT INTO `wj_city` VALUES (1332, 360400, 360483, '庐山市', 3);
INSERT INTO `wj_city` VALUES (1333, 360000, 360500, '新余市', 2);
INSERT INTO `wj_city` VALUES (1334, 360500, 360502, '渝水区', 3);
INSERT INTO `wj_city` VALUES (1335, 360500, 360521, '分宜县', 3);
INSERT INTO `wj_city` VALUES (1336, 360000, 360600, '鹰潭市', 2);
INSERT INTO `wj_city` VALUES (1337, 360600, 360602, '月湖区', 3);
INSERT INTO `wj_city` VALUES (1338, 360600, 360603, '余江区', 3);
INSERT INTO `wj_city` VALUES (1339, 360600, 360681, '贵溪市', 3);
INSERT INTO `wj_city` VALUES (1340, 360000, 360700, '赣州市', 2);
INSERT INTO `wj_city` VALUES (1341, 360700, 360702, '章贡区', 3);
INSERT INTO `wj_city` VALUES (1342, 360700, 360703, '南康区', 3);
INSERT INTO `wj_city` VALUES (1343, 360700, 360704, '赣县区', 3);
INSERT INTO `wj_city` VALUES (1344, 360700, 360722, '信丰县', 3);
INSERT INTO `wj_city` VALUES (1345, 360700, 360723, '大余县', 3);
INSERT INTO `wj_city` VALUES (1346, 360700, 360724, '上犹县', 3);
INSERT INTO `wj_city` VALUES (1347, 360700, 360725, '崇义县', 3);
INSERT INTO `wj_city` VALUES (1348, 360700, 360726, '安远县', 3);
INSERT INTO `wj_city` VALUES (1349, 360700, 360728, '定南县', 3);
INSERT INTO `wj_city` VALUES (1350, 360700, 360729, '全南县', 3);
INSERT INTO `wj_city` VALUES (1351, 360700, 360730, '宁都县', 3);
INSERT INTO `wj_city` VALUES (1352, 360700, 360731, '于都县', 3);
INSERT INTO `wj_city` VALUES (1353, 360700, 360732, '兴国县', 3);
INSERT INTO `wj_city` VALUES (1354, 360700, 360733, '会昌县', 3);
INSERT INTO `wj_city` VALUES (1355, 360700, 360734, '寻乌县', 3);
INSERT INTO `wj_city` VALUES (1356, 360700, 360735, '石城县', 3);
INSERT INTO `wj_city` VALUES (1357, 360700, 360781, '瑞金市', 3);
INSERT INTO `wj_city` VALUES (1358, 360700, 360783, '龙南市', 3);
INSERT INTO `wj_city` VALUES (1359, 360000, 360800, '吉安市', 2);
INSERT INTO `wj_city` VALUES (1360, 360800, 360802, '吉州区', 3);
INSERT INTO `wj_city` VALUES (1361, 360800, 360803, '青原区', 3);
INSERT INTO `wj_city` VALUES (1362, 360800, 360821, '吉安县', 3);
INSERT INTO `wj_city` VALUES (1363, 360800, 360822, '吉水县', 3);
INSERT INTO `wj_city` VALUES (1364, 360800, 360823, '峡江县', 3);
INSERT INTO `wj_city` VALUES (1365, 360800, 360824, '新干县', 3);
INSERT INTO `wj_city` VALUES (1366, 360800, 360825, '永丰县', 3);
INSERT INTO `wj_city` VALUES (1367, 360800, 360826, '泰和县', 3);
INSERT INTO `wj_city` VALUES (1368, 360800, 360827, '遂川县', 3);
INSERT INTO `wj_city` VALUES (1369, 360800, 360828, '万安县', 3);
INSERT INTO `wj_city` VALUES (1370, 360800, 360829, '安福县', 3);
INSERT INTO `wj_city` VALUES (1371, 360800, 360830, '永新县', 3);
INSERT INTO `wj_city` VALUES (1372, 360800, 360881, '井冈山市', 3);
INSERT INTO `wj_city` VALUES (1373, 360000, 360900, '宜春市', 2);
INSERT INTO `wj_city` VALUES (1374, 360900, 360902, '袁州区', 3);
INSERT INTO `wj_city` VALUES (1375, 360900, 360921, '奉新县', 3);
INSERT INTO `wj_city` VALUES (1376, 360900, 360922, '万载县', 3);
INSERT INTO `wj_city` VALUES (1377, 360900, 360923, '上高县', 3);
INSERT INTO `wj_city` VALUES (1378, 360900, 360924, '宜丰县', 3);
INSERT INTO `wj_city` VALUES (1379, 360900, 360925, '靖安县', 3);
INSERT INTO `wj_city` VALUES (1380, 360900, 360926, '铜鼓县', 3);
INSERT INTO `wj_city` VALUES (1381, 360900, 360981, '丰城市', 3);
INSERT INTO `wj_city` VALUES (1382, 360900, 360982, '樟树市', 3);
INSERT INTO `wj_city` VALUES (1383, 360900, 360983, '高安市', 3);
INSERT INTO `wj_city` VALUES (1384, 360000, 361000, '抚州市', 2);
INSERT INTO `wj_city` VALUES (1385, 361000, 361002, '临川区', 3);
INSERT INTO `wj_city` VALUES (1386, 361000, 361003, '东乡区', 3);
INSERT INTO `wj_city` VALUES (1387, 361000, 361021, '南城县', 3);
INSERT INTO `wj_city` VALUES (1388, 361000, 361022, '黎川县', 3);
INSERT INTO `wj_city` VALUES (1389, 361000, 361023, '南丰县', 3);
INSERT INTO `wj_city` VALUES (1390, 361000, 361024, '崇仁县', 3);
INSERT INTO `wj_city` VALUES (1391, 361000, 361025, '乐安县', 3);
INSERT INTO `wj_city` VALUES (1392, 361000, 361026, '宜黄县', 3);
INSERT INTO `wj_city` VALUES (1393, 361000, 361027, '金溪县', 3);
INSERT INTO `wj_city` VALUES (1394, 361000, 361028, '资溪县', 3);
INSERT INTO `wj_city` VALUES (1395, 361000, 361030, '广昌县', 3);
INSERT INTO `wj_city` VALUES (1396, 360000, 361100, '上饶市', 2);
INSERT INTO `wj_city` VALUES (1397, 361100, 361102, '信州区', 3);
INSERT INTO `wj_city` VALUES (1398, 361100, 361103, '广丰区', 3);
INSERT INTO `wj_city` VALUES (1399, 361100, 361104, '广信区', 3);
INSERT INTO `wj_city` VALUES (1400, 361100, 361123, '玉山县', 3);
INSERT INTO `wj_city` VALUES (1401, 361100, 361124, '铅山县', 3);
INSERT INTO `wj_city` VALUES (1402, 361100, 361125, '横峰县', 3);
INSERT INTO `wj_city` VALUES (1403, 361100, 361126, '弋阳县', 3);
INSERT INTO `wj_city` VALUES (1404, 361100, 361127, '余干县', 3);
INSERT INTO `wj_city` VALUES (1405, 361100, 361128, '鄱阳县', 3);
INSERT INTO `wj_city` VALUES (1406, 361100, 361129, '万年县', 3);
INSERT INTO `wj_city` VALUES (1407, 361100, 361130, '婺源县', 3);
INSERT INTO `wj_city` VALUES (1408, 361100, 361181, '德兴市', 3);
INSERT INTO `wj_city` VALUES (1409, 0, 370000, '山东省', 1);
INSERT INTO `wj_city` VALUES (1410, 370000, 370100, '济南市', 2);
INSERT INTO `wj_city` VALUES (1411, 370100, 370102, '历下区', 3);
INSERT INTO `wj_city` VALUES (1412, 370100, 370103, '市中区', 3);
INSERT INTO `wj_city` VALUES (1413, 370100, 370104, '槐荫区', 3);
INSERT INTO `wj_city` VALUES (1414, 370100, 370105, '天桥区', 3);
INSERT INTO `wj_city` VALUES (1415, 370100, 370112, '历城区', 3);
INSERT INTO `wj_city` VALUES (1416, 370100, 370113, '长清区', 3);
INSERT INTO `wj_city` VALUES (1417, 370100, 370114, '章丘区', 3);
INSERT INTO `wj_city` VALUES (1418, 370100, 370115, '济阳区', 3);
INSERT INTO `wj_city` VALUES (1419, 370100, 370116, '莱芜区', 3);
INSERT INTO `wj_city` VALUES (1420, 370100, 370117, '钢城区', 3);
INSERT INTO `wj_city` VALUES (1421, 370100, 370124, '平阴县', 3);
INSERT INTO `wj_city` VALUES (1422, 370100, 370126, '商河县', 3);
INSERT INTO `wj_city` VALUES (1423, 370100, 370176, '济南高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (1424, 370000, 370200, '青岛市', 2);
INSERT INTO `wj_city` VALUES (1425, 370200, 370202, '市南区', 3);
INSERT INTO `wj_city` VALUES (1426, 370200, 370203, '市北区', 3);
INSERT INTO `wj_city` VALUES (1427, 370200, 370211, '黄岛区', 3);
INSERT INTO `wj_city` VALUES (1428, 370200, 370212, '崂山区', 3);
INSERT INTO `wj_city` VALUES (1429, 370200, 370213, '李沧区', 3);
INSERT INTO `wj_city` VALUES (1430, 370200, 370214, '城阳区', 3);
INSERT INTO `wj_city` VALUES (1431, 370200, 370215, '即墨区', 3);
INSERT INTO `wj_city` VALUES (1432, 370200, 370281, '胶州市', 3);
INSERT INTO `wj_city` VALUES (1433, 370200, 370283, '平度市', 3);
INSERT INTO `wj_city` VALUES (1434, 370200, 370285, '莱西市', 3);
INSERT INTO `wj_city` VALUES (1435, 370000, 370300, '淄博市', 2);
INSERT INTO `wj_city` VALUES (1436, 370300, 370302, '淄川区', 3);
INSERT INTO `wj_city` VALUES (1437, 370300, 370303, '张店区', 3);
INSERT INTO `wj_city` VALUES (1438, 370300, 370304, '博山区', 3);
INSERT INTO `wj_city` VALUES (1439, 370300, 370305, '临淄区', 3);
INSERT INTO `wj_city` VALUES (1440, 370300, 370306, '周村区', 3);
INSERT INTO `wj_city` VALUES (1441, 370300, 370321, '桓台县', 3);
INSERT INTO `wj_city` VALUES (1442, 370300, 370322, '高青县', 3);
INSERT INTO `wj_city` VALUES (1443, 370300, 370323, '沂源县', 3);
INSERT INTO `wj_city` VALUES (1444, 370000, 370400, '枣庄市', 2);
INSERT INTO `wj_city` VALUES (1445, 370400, 370402, '市中区', 3);
INSERT INTO `wj_city` VALUES (1446, 370400, 370403, '薛城区', 3);
INSERT INTO `wj_city` VALUES (1447, 370400, 370404, '峄城区', 3);
INSERT INTO `wj_city` VALUES (1448, 370400, 370405, '台儿庄区', 3);
INSERT INTO `wj_city` VALUES (1449, 370400, 370406, '山亭区', 3);
INSERT INTO `wj_city` VALUES (1450, 370400, 370481, '滕州市', 3);
INSERT INTO `wj_city` VALUES (1451, 370000, 370500, '东营市', 2);
INSERT INTO `wj_city` VALUES (1452, 370500, 370502, '东营区', 3);
INSERT INTO `wj_city` VALUES (1453, 370500, 370503, '河口区', 3);
INSERT INTO `wj_city` VALUES (1454, 370500, 370505, '垦利区', 3);
INSERT INTO `wj_city` VALUES (1455, 370500, 370522, '利津县', 3);
INSERT INTO `wj_city` VALUES (1456, 370500, 370523, '广饶县', 3);
INSERT INTO `wj_city` VALUES (1457, 370500, 370571, '东营经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (1458, 370500, 370572, '东营港经济开发区', 3);
INSERT INTO `wj_city` VALUES (1459, 370000, 370600, '烟台市', 2);
INSERT INTO `wj_city` VALUES (1460, 370600, 370602, '芝罘区', 3);
INSERT INTO `wj_city` VALUES (1461, 370600, 370611, '福山区', 3);
INSERT INTO `wj_city` VALUES (1462, 370600, 370612, '牟平区', 3);
INSERT INTO `wj_city` VALUES (1463, 370600, 370613, '莱山区', 3);
INSERT INTO `wj_city` VALUES (1464, 370600, 370614, '蓬莱区', 3);
INSERT INTO `wj_city` VALUES (1465, 370600, 370671, '烟台高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (1466, 370600, 370676, '烟台经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (1467, 370600, 370681, '龙口市', 3);
INSERT INTO `wj_city` VALUES (1468, 370600, 370682, '莱阳市', 3);
INSERT INTO `wj_city` VALUES (1469, 370600, 370683, '莱州市', 3);
INSERT INTO `wj_city` VALUES (1470, 370600, 370685, '招远市', 3);
INSERT INTO `wj_city` VALUES (1471, 370600, 370686, '栖霞市', 3);
INSERT INTO `wj_city` VALUES (1472, 370600, 370687, '海阳市', 3);
INSERT INTO `wj_city` VALUES (1473, 370000, 370700, '潍坊市', 2);
INSERT INTO `wj_city` VALUES (1474, 370700, 370702, '潍城区', 3);
INSERT INTO `wj_city` VALUES (1475, 370700, 370703, '寒亭区', 3);
INSERT INTO `wj_city` VALUES (1476, 370700, 370704, '坊子区', 3);
INSERT INTO `wj_city` VALUES (1477, 370700, 370705, '奎文区', 3);
INSERT INTO `wj_city` VALUES (1478, 370700, 370724, '临朐县', 3);
INSERT INTO `wj_city` VALUES (1479, 370700, 370725, '昌乐县', 3);
INSERT INTO `wj_city` VALUES (1480, 370700, 370772, '潍坊滨海经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (1481, 370700, 370781, '青州市', 3);
INSERT INTO `wj_city` VALUES (1482, 370700, 370782, '诸城市', 3);
INSERT INTO `wj_city` VALUES (1483, 370700, 370783, '寿光市', 3);
INSERT INTO `wj_city` VALUES (1484, 370700, 370784, '安丘市', 3);
INSERT INTO `wj_city` VALUES (1485, 370700, 370785, '高密市', 3);
INSERT INTO `wj_city` VALUES (1486, 370700, 370786, '昌邑市', 3);
INSERT INTO `wj_city` VALUES (1487, 370000, 370800, '济宁市', 2);
INSERT INTO `wj_city` VALUES (1488, 370800, 370811, '任城区', 3);
INSERT INTO `wj_city` VALUES (1489, 370800, 370812, '兖州区', 3);
INSERT INTO `wj_city` VALUES (1490, 370800, 370826, '微山县', 3);
INSERT INTO `wj_city` VALUES (1491, 370800, 370827, '鱼台县', 3);
INSERT INTO `wj_city` VALUES (1492, 370800, 370828, '金乡县', 3);
INSERT INTO `wj_city` VALUES (1493, 370800, 370829, '嘉祥县', 3);
INSERT INTO `wj_city` VALUES (1494, 370800, 370830, '汶上县', 3);
INSERT INTO `wj_city` VALUES (1495, 370800, 370831, '泗水县', 3);
INSERT INTO `wj_city` VALUES (1496, 370800, 370832, '梁山县', 3);
INSERT INTO `wj_city` VALUES (1497, 370800, 370871, '济宁高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (1498, 370800, 370881, '曲阜市', 3);
INSERT INTO `wj_city` VALUES (1499, 370800, 370883, '邹城市', 3);
INSERT INTO `wj_city` VALUES (1500, 370000, 370900, '泰安市', 2);
INSERT INTO `wj_city` VALUES (1501, 370900, 370902, '泰山区', 3);
INSERT INTO `wj_city` VALUES (1502, 370900, 370911, '岱岳区', 3);
INSERT INTO `wj_city` VALUES (1503, 370900, 370921, '宁阳县', 3);
INSERT INTO `wj_city` VALUES (1504, 370900, 370923, '东平县', 3);
INSERT INTO `wj_city` VALUES (1505, 370900, 370982, '新泰市', 3);
INSERT INTO `wj_city` VALUES (1506, 370900, 370983, '肥城市', 3);
INSERT INTO `wj_city` VALUES (1507, 370000, 371000, '威海市', 2);
INSERT INTO `wj_city` VALUES (1508, 371000, 371002, '环翠区', 3);
INSERT INTO `wj_city` VALUES (1509, 371000, 371003, '文登区', 3);
INSERT INTO `wj_city` VALUES (1510, 371000, 371071, '威海火炬高技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (1511, 371000, 371072, '威海经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (1512, 371000, 371073, '威海临港经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (1513, 371000, 371082, '荣成市', 3);
INSERT INTO `wj_city` VALUES (1514, 371000, 371083, '乳山市', 3);
INSERT INTO `wj_city` VALUES (1515, 370000, 371100, '日照市', 2);
INSERT INTO `wj_city` VALUES (1516, 371100, 371102, '东港区', 3);
INSERT INTO `wj_city` VALUES (1517, 371100, 371103, '岚山区', 3);
INSERT INTO `wj_city` VALUES (1518, 371100, 371121, '五莲县', 3);
INSERT INTO `wj_city` VALUES (1519, 371100, 371122, '莒县', 3);
INSERT INTO `wj_city` VALUES (1520, 371100, 371171, '日照经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (1521, 370000, 371300, '临沂市', 2);
INSERT INTO `wj_city` VALUES (1522, 371300, 371302, '兰山区', 3);
INSERT INTO `wj_city` VALUES (1523, 371300, 371311, '罗庄区', 3);
INSERT INTO `wj_city` VALUES (1524, 371300, 371312, '河东区', 3);
INSERT INTO `wj_city` VALUES (1525, 371300, 371321, '沂南县', 3);
INSERT INTO `wj_city` VALUES (1526, 371300, 371322, '郯城县', 3);
INSERT INTO `wj_city` VALUES (1527, 371300, 371323, '沂水县', 3);
INSERT INTO `wj_city` VALUES (1528, 371300, 371324, '兰陵县', 3);
INSERT INTO `wj_city` VALUES (1529, 371300, 371325, '费县', 3);
INSERT INTO `wj_city` VALUES (1530, 371300, 371326, '平邑县', 3);
INSERT INTO `wj_city` VALUES (1531, 371300, 371327, '莒南县', 3);
INSERT INTO `wj_city` VALUES (1532, 371300, 371328, '蒙阴县', 3);
INSERT INTO `wj_city` VALUES (1533, 371300, 371329, '临沭县', 3);
INSERT INTO `wj_city` VALUES (1534, 371300, 371371, '临沂高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (1535, 370000, 371400, '德州市', 2);
INSERT INTO `wj_city` VALUES (1536, 371400, 371402, '德城区', 3);
INSERT INTO `wj_city` VALUES (1537, 371400, 371403, '陵城区', 3);
INSERT INTO `wj_city` VALUES (1538, 371400, 371422, '宁津县', 3);
INSERT INTO `wj_city` VALUES (1539, 371400, 371423, '庆云县', 3);
INSERT INTO `wj_city` VALUES (1540, 371400, 371424, '临邑县', 3);
INSERT INTO `wj_city` VALUES (1541, 371400, 371425, '齐河县', 3);
INSERT INTO `wj_city` VALUES (1542, 371400, 371426, '平原县', 3);
INSERT INTO `wj_city` VALUES (1543, 371400, 371427, '夏津县', 3);
INSERT INTO `wj_city` VALUES (1544, 371400, 371428, '武城县', 3);
INSERT INTO `wj_city` VALUES (1545, 371400, 371471, '德州天衢新区', 3);
INSERT INTO `wj_city` VALUES (1546, 371400, 371481, '乐陵市', 3);
INSERT INTO `wj_city` VALUES (1547, 371400, 371482, '禹城市', 3);
INSERT INTO `wj_city` VALUES (1548, 370000, 371500, '聊城市', 2);
INSERT INTO `wj_city` VALUES (1549, 371500, 371502, '东昌府区', 3);
INSERT INTO `wj_city` VALUES (1550, 371500, 371503, '茌平区', 3);
INSERT INTO `wj_city` VALUES (1551, 371500, 371521, '阳谷县', 3);
INSERT INTO `wj_city` VALUES (1552, 371500, 371522, '莘县', 3);
INSERT INTO `wj_city` VALUES (1553, 371500, 371524, '东阿县', 3);
INSERT INTO `wj_city` VALUES (1554, 371500, 371525, '冠县', 3);
INSERT INTO `wj_city` VALUES (1555, 371500, 371526, '高唐县', 3);
INSERT INTO `wj_city` VALUES (1556, 371500, 371581, '临清市', 3);
INSERT INTO `wj_city` VALUES (1557, 370000, 371600, '滨州市', 2);
INSERT INTO `wj_city` VALUES (1558, 371600, 371602, '滨城区', 3);
INSERT INTO `wj_city` VALUES (1559, 371600, 371603, '沾化区', 3);
INSERT INTO `wj_city` VALUES (1560, 371600, 371621, '惠民县', 3);
INSERT INTO `wj_city` VALUES (1561, 371600, 371622, '阳信县', 3);
INSERT INTO `wj_city` VALUES (1562, 371600, 371623, '无棣县', 3);
INSERT INTO `wj_city` VALUES (1563, 371600, 371625, '博兴县', 3);
INSERT INTO `wj_city` VALUES (1564, 371600, 371681, '邹平市', 3);
INSERT INTO `wj_city` VALUES (1565, 370000, 371700, '菏泽市', 2);
INSERT INTO `wj_city` VALUES (1566, 371700, 371702, '牡丹区', 3);
INSERT INTO `wj_city` VALUES (1567, 371700, 371703, '定陶区', 3);
INSERT INTO `wj_city` VALUES (1568, 371700, 371721, '曹县', 3);
INSERT INTO `wj_city` VALUES (1569, 371700, 371722, '单县', 3);
INSERT INTO `wj_city` VALUES (1570, 371700, 371723, '成武县', 3);
INSERT INTO `wj_city` VALUES (1571, 371700, 371724, '巨野县', 3);
INSERT INTO `wj_city` VALUES (1572, 371700, 371725, '郓城县', 3);
INSERT INTO `wj_city` VALUES (1573, 371700, 371726, '鄄城县', 3);
INSERT INTO `wj_city` VALUES (1574, 371700, 371728, '东明县', 3);
INSERT INTO `wj_city` VALUES (1575, 371700, 371771, '菏泽经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (1576, 371700, 371772, '菏泽高新技术开发区', 3);
INSERT INTO `wj_city` VALUES (1577, 0, 410000, '河南省', 1);
INSERT INTO `wj_city` VALUES (1578, 410000, 410100, '郑州市', 2);
INSERT INTO `wj_city` VALUES (1579, 410100, 410102, '中原区', 3);
INSERT INTO `wj_city` VALUES (1580, 410100, 410103, '二七区', 3);
INSERT INTO `wj_city` VALUES (1581, 410100, 410104, '管城回族区', 3);
INSERT INTO `wj_city` VALUES (1582, 410100, 410105, '金水区', 3);
INSERT INTO `wj_city` VALUES (1583, 410100, 410106, '上街区', 3);
INSERT INTO `wj_city` VALUES (1584, 410100, 410108, '惠济区', 3);
INSERT INTO `wj_city` VALUES (1585, 410100, 410122, '中牟县', 3);
INSERT INTO `wj_city` VALUES (1586, 410100, 410171, '郑州经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (1587, 410100, 410172, '郑州高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (1588, 410100, 410173, '郑州航空港经济综合实验区', 3);
INSERT INTO `wj_city` VALUES (1589, 410100, 410181, '巩义市', 3);
INSERT INTO `wj_city` VALUES (1590, 410100, 410182, '荥阳市', 3);
INSERT INTO `wj_city` VALUES (1591, 410100, 410183, '新密市', 3);
INSERT INTO `wj_city` VALUES (1592, 410100, 410184, '新郑市', 3);
INSERT INTO `wj_city` VALUES (1593, 410100, 410185, '登封市', 3);
INSERT INTO `wj_city` VALUES (1594, 410000, 410200, '开封市', 2);
INSERT INTO `wj_city` VALUES (1595, 410200, 410202, '龙亭区', 3);
INSERT INTO `wj_city` VALUES (1596, 410200, 410203, '顺河回族区', 3);
INSERT INTO `wj_city` VALUES (1597, 410200, 410204, '鼓楼区', 3);
INSERT INTO `wj_city` VALUES (1598, 410200, 410205, '禹王台区', 3);
INSERT INTO `wj_city` VALUES (1599, 410200, 410212, '祥符区', 3);
INSERT INTO `wj_city` VALUES (1600, 410200, 410221, '杞县', 3);
INSERT INTO `wj_city` VALUES (1601, 410200, 410222, '通许县', 3);
INSERT INTO `wj_city` VALUES (1602, 410200, 410223, '尉氏县', 3);
INSERT INTO `wj_city` VALUES (1603, 410200, 410225, '兰考县', 3);
INSERT INTO `wj_city` VALUES (1604, 410000, 410300, '洛阳市', 2);
INSERT INTO `wj_city` VALUES (1605, 410300, 410302, '老城区', 3);
INSERT INTO `wj_city` VALUES (1606, 410300, 410303, '西工区', 3);
INSERT INTO `wj_city` VALUES (1607, 410300, 410304, '瀍河回族区', 3);
INSERT INTO `wj_city` VALUES (1608, 410300, 410305, '涧西区', 3);
INSERT INTO `wj_city` VALUES (1609, 410300, 410307, '偃师区', 3);
INSERT INTO `wj_city` VALUES (1610, 410300, 410308, '孟津区', 3);
INSERT INTO `wj_city` VALUES (1611, 410300, 410311, '洛龙区', 3);
INSERT INTO `wj_city` VALUES (1612, 410300, 410323, '新安县', 3);
INSERT INTO `wj_city` VALUES (1613, 410300, 410324, '栾川县', 3);
INSERT INTO `wj_city` VALUES (1614, 410300, 410325, '嵩县', 3);
INSERT INTO `wj_city` VALUES (1615, 410300, 410326, '汝阳县', 3);
INSERT INTO `wj_city` VALUES (1616, 410300, 410327, '宜阳县', 3);
INSERT INTO `wj_city` VALUES (1617, 410300, 410328, '洛宁县', 3);
INSERT INTO `wj_city` VALUES (1618, 410300, 410329, '伊川县', 3);
INSERT INTO `wj_city` VALUES (1619, 410300, 410371, '洛阳高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (1620, 410000, 410400, '平顶山市', 2);
INSERT INTO `wj_city` VALUES (1621, 410400, 410402, '新华区', 3);
INSERT INTO `wj_city` VALUES (1622, 410400, 410403, '卫东区', 3);
INSERT INTO `wj_city` VALUES (1623, 410400, 410404, '石龙区', 3);
INSERT INTO `wj_city` VALUES (1624, 410400, 410411, '湛河区', 3);
INSERT INTO `wj_city` VALUES (1625, 410400, 410421, '宝丰县', 3);
INSERT INTO `wj_city` VALUES (1626, 410400, 410422, '叶县', 3);
INSERT INTO `wj_city` VALUES (1627, 410400, 410423, '鲁山县', 3);
INSERT INTO `wj_city` VALUES (1628, 410400, 410425, '郏县', 3);
INSERT INTO `wj_city` VALUES (1629, 410400, 410471, '平顶山高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (1630, 410400, 410472, '平顶山市城乡一体化示范区', 3);
INSERT INTO `wj_city` VALUES (1631, 410400, 410481, '舞钢市', 3);
INSERT INTO `wj_city` VALUES (1632, 410400, 410482, '汝州市', 3);
INSERT INTO `wj_city` VALUES (1633, 410000, 410500, '安阳市', 2);
INSERT INTO `wj_city` VALUES (1634, 410500, 410502, '文峰区', 3);
INSERT INTO `wj_city` VALUES (1635, 410500, 410503, '北关区', 3);
INSERT INTO `wj_city` VALUES (1636, 410500, 410505, '殷都区', 3);
INSERT INTO `wj_city` VALUES (1637, 410500, 410506, '龙安区', 3);
INSERT INTO `wj_city` VALUES (1638, 410500, 410522, '安阳县', 3);
INSERT INTO `wj_city` VALUES (1639, 410500, 410523, '汤阴县', 3);
INSERT INTO `wj_city` VALUES (1640, 410500, 410526, '滑县', 3);
INSERT INTO `wj_city` VALUES (1641, 410500, 410527, '内黄县', 3);
INSERT INTO `wj_city` VALUES (1642, 410500, 410571, '安阳高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (1643, 410500, 410581, '林州市', 3);
INSERT INTO `wj_city` VALUES (1644, 410000, 410600, '鹤壁市', 2);
INSERT INTO `wj_city` VALUES (1645, 410600, 410602, '鹤山区', 3);
INSERT INTO `wj_city` VALUES (1646, 410600, 410603, '山城区', 3);
INSERT INTO `wj_city` VALUES (1647, 410600, 410611, '淇滨区', 3);
INSERT INTO `wj_city` VALUES (1648, 410600, 410621, '浚县', 3);
INSERT INTO `wj_city` VALUES (1649, 410600, 410622, '淇县', 3);
INSERT INTO `wj_city` VALUES (1650, 410600, 410671, '鹤壁经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (1651, 410000, 410700, '新乡市', 2);
INSERT INTO `wj_city` VALUES (1652, 410700, 410702, '红旗区', 3);
INSERT INTO `wj_city` VALUES (1653, 410700, 410703, '卫滨区', 3);
INSERT INTO `wj_city` VALUES (1654, 410700, 410704, '凤泉区', 3);
INSERT INTO `wj_city` VALUES (1655, 410700, 410711, '牧野区', 3);
INSERT INTO `wj_city` VALUES (1656, 410700, 410721, '新乡县', 3);
INSERT INTO `wj_city` VALUES (1657, 410700, 410724, '获嘉县', 3);
INSERT INTO `wj_city` VALUES (1658, 410700, 410725, '原阳县', 3);
INSERT INTO `wj_city` VALUES (1659, 410700, 410726, '延津县', 3);
INSERT INTO `wj_city` VALUES (1660, 410700, 410727, '封丘县', 3);
INSERT INTO `wj_city` VALUES (1661, 410700, 410771, '新乡高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (1662, 410700, 410772, '新乡经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (1663, 410700, 410773, '新乡市平原城乡一体化示范区', 3);
INSERT INTO `wj_city` VALUES (1664, 410700, 410781, '卫辉市', 3);
INSERT INTO `wj_city` VALUES (1665, 410700, 410782, '辉县市', 3);
INSERT INTO `wj_city` VALUES (1666, 410700, 410783, '长垣市', 3);
INSERT INTO `wj_city` VALUES (1667, 410000, 410800, '焦作市', 2);
INSERT INTO `wj_city` VALUES (1668, 410800, 410802, '解放区', 3);
INSERT INTO `wj_city` VALUES (1669, 410800, 410803, '中站区', 3);
INSERT INTO `wj_city` VALUES (1670, 410800, 410804, '马村区', 3);
INSERT INTO `wj_city` VALUES (1671, 410800, 410811, '山阳区', 3);
INSERT INTO `wj_city` VALUES (1672, 410800, 410821, '修武县', 3);
INSERT INTO `wj_city` VALUES (1673, 410800, 410822, '博爱县', 3);
INSERT INTO `wj_city` VALUES (1674, 410800, 410823, '武陟县', 3);
INSERT INTO `wj_city` VALUES (1675, 410800, 410825, '温县', 3);
INSERT INTO `wj_city` VALUES (1676, 410800, 410871, '焦作城乡一体化示范区', 3);
INSERT INTO `wj_city` VALUES (1677, 410800, 410882, '沁阳市', 3);
INSERT INTO `wj_city` VALUES (1678, 410800, 410883, '孟州市', 3);
INSERT INTO `wj_city` VALUES (1679, 410000, 410900, '濮阳市', 2);
INSERT INTO `wj_city` VALUES (1680, 410900, 410902, '华龙区', 3);
INSERT INTO `wj_city` VALUES (1681, 410900, 410922, '清丰县', 3);
INSERT INTO `wj_city` VALUES (1682, 410900, 410923, '南乐县', 3);
INSERT INTO `wj_city` VALUES (1683, 410900, 410926, '范县', 3);
INSERT INTO `wj_city` VALUES (1684, 410900, 410927, '台前县', 3);
INSERT INTO `wj_city` VALUES (1685, 410900, 410928, '濮阳县', 3);
INSERT INTO `wj_city` VALUES (1686, 410900, 410971, '河南濮阳工业园区', 3);
INSERT INTO `wj_city` VALUES (1687, 410900, 410972, '濮阳经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (1688, 410000, 411000, '许昌市', 2);
INSERT INTO `wj_city` VALUES (1689, 411000, 411002, '魏都区', 3);
INSERT INTO `wj_city` VALUES (1690, 411000, 411003, '建安区', 3);
INSERT INTO `wj_city` VALUES (1691, 411000, 411024, '鄢陵县', 3);
INSERT INTO `wj_city` VALUES (1692, 411000, 411025, '襄城县', 3);
INSERT INTO `wj_city` VALUES (1693, 411000, 411071, '许昌经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (1694, 411000, 411081, '禹州市', 3);
INSERT INTO `wj_city` VALUES (1695, 411000, 411082, '长葛市', 3);
INSERT INTO `wj_city` VALUES (1696, 410000, 411100, '漯河市', 2);
INSERT INTO `wj_city` VALUES (1697, 411100, 411102, '源汇区', 3);
INSERT INTO `wj_city` VALUES (1698, 411100, 411103, '郾城区', 3);
INSERT INTO `wj_city` VALUES (1699, 411100, 411104, '召陵区', 3);
INSERT INTO `wj_city` VALUES (1700, 411100, 411121, '舞阳县', 3);
INSERT INTO `wj_city` VALUES (1701, 411100, 411122, '临颍县', 3);
INSERT INTO `wj_city` VALUES (1702, 411100, 411171, '漯河经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (1703, 410000, 411200, '三门峡市', 2);
INSERT INTO `wj_city` VALUES (1704, 411200, 411202, '湖滨区', 3);
INSERT INTO `wj_city` VALUES (1705, 411200, 411203, '陕州区', 3);
INSERT INTO `wj_city` VALUES (1706, 411200, 411221, '渑池县', 3);
INSERT INTO `wj_city` VALUES (1707, 411200, 411224, '卢氏县', 3);
INSERT INTO `wj_city` VALUES (1708, 411200, 411271, '河南三门峡经济开发区', 3);
INSERT INTO `wj_city` VALUES (1709, 411200, 411281, '义马市', 3);
INSERT INTO `wj_city` VALUES (1710, 411200, 411282, '灵宝市', 3);
INSERT INTO `wj_city` VALUES (1711, 410000, 411300, '南阳市', 2);
INSERT INTO `wj_city` VALUES (1712, 411300, 411302, '宛城区', 3);
INSERT INTO `wj_city` VALUES (1713, 411300, 411303, '卧龙区', 3);
INSERT INTO `wj_city` VALUES (1714, 411300, 411321, '南召县', 3);
INSERT INTO `wj_city` VALUES (1715, 411300, 411322, '方城县', 3);
INSERT INTO `wj_city` VALUES (1716, 411300, 411323, '西峡县', 3);
INSERT INTO `wj_city` VALUES (1717, 411300, 411324, '镇平县', 3);
INSERT INTO `wj_city` VALUES (1718, 411300, 411325, '内乡县', 3);
INSERT INTO `wj_city` VALUES (1719, 411300, 411326, '淅川县', 3);
INSERT INTO `wj_city` VALUES (1720, 411300, 411327, '社旗县', 3);
INSERT INTO `wj_city` VALUES (1721, 411300, 411328, '唐河县', 3);
INSERT INTO `wj_city` VALUES (1722, 411300, 411329, '新野县', 3);
INSERT INTO `wj_city` VALUES (1723, 411300, 411330, '桐柏县', 3);
INSERT INTO `wj_city` VALUES (1724, 411300, 411371, '南阳高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (1725, 411300, 411372, '南阳市城乡一体化示范区', 3);
INSERT INTO `wj_city` VALUES (1726, 411300, 411381, '邓州市', 3);
INSERT INTO `wj_city` VALUES (1727, 410000, 411400, '商丘市', 2);
INSERT INTO `wj_city` VALUES (1728, 411400, 411402, '梁园区', 3);
INSERT INTO `wj_city` VALUES (1729, 411400, 411403, '睢阳区', 3);
INSERT INTO `wj_city` VALUES (1730, 411400, 411421, '民权县', 3);
INSERT INTO `wj_city` VALUES (1731, 411400, 411422, '睢县', 3);
INSERT INTO `wj_city` VALUES (1732, 411400, 411423, '宁陵县', 3);
INSERT INTO `wj_city` VALUES (1733, 411400, 411424, '柘城县', 3);
INSERT INTO `wj_city` VALUES (1734, 411400, 411425, '虞城县', 3);
INSERT INTO `wj_city` VALUES (1735, 411400, 411426, '夏邑县', 3);
INSERT INTO `wj_city` VALUES (1736, 411400, 411471, '豫东综合物流产业聚集区', 3);
INSERT INTO `wj_city` VALUES (1737, 411400, 411472, '河南商丘经济开发区', 3);
INSERT INTO `wj_city` VALUES (1738, 411400, 411481, '永城市', 3);
INSERT INTO `wj_city` VALUES (1739, 410000, 411500, '信阳市', 2);
INSERT INTO `wj_city` VALUES (1740, 411500, 411502, '浉河区', 3);
INSERT INTO `wj_city` VALUES (1741, 411500, 411503, '平桥区', 3);
INSERT INTO `wj_city` VALUES (1742, 411500, 411521, '罗山县', 3);
INSERT INTO `wj_city` VALUES (1743, 411500, 411522, '光山县', 3);
INSERT INTO `wj_city` VALUES (1744, 411500, 411523, '新县', 3);
INSERT INTO `wj_city` VALUES (1745, 411500, 411524, '商城县', 3);
INSERT INTO `wj_city` VALUES (1746, 411500, 411525, '固始县', 3);
INSERT INTO `wj_city` VALUES (1747, 411500, 411526, '潢川县', 3);
INSERT INTO `wj_city` VALUES (1748, 411500, 411527, '淮滨县', 3);
INSERT INTO `wj_city` VALUES (1749, 411500, 411528, '息县', 3);
INSERT INTO `wj_city` VALUES (1750, 411500, 411571, '信阳高新技术产业开发区', 3);
INSERT INTO `wj_city` VALUES (1751, 410000, 411600, '周口市', 2);
INSERT INTO `wj_city` VALUES (1752, 411600, 411602, '川汇区', 3);
INSERT INTO `wj_city` VALUES (1753, 411600, 411603, '淮阳区', 3);
INSERT INTO `wj_city` VALUES (1754, 411600, 411621, '扶沟县', 3);
INSERT INTO `wj_city` VALUES (1755, 411600, 411622, '西华县', 3);
INSERT INTO `wj_city` VALUES (1756, 411600, 411623, '商水县', 3);
INSERT INTO `wj_city` VALUES (1757, 411600, 411624, '沈丘县', 3);
INSERT INTO `wj_city` VALUES (1758, 411600, 411625, '郸城县', 3);
INSERT INTO `wj_city` VALUES (1759, 411600, 411627, '太康县', 3);
INSERT INTO `wj_city` VALUES (1760, 411600, 411628, '鹿邑县', 3);
INSERT INTO `wj_city` VALUES (1761, 411600, 411671, '周口临港开发区', 3);
INSERT INTO `wj_city` VALUES (1762, 411600, 411681, '项城市', 3);
INSERT INTO `wj_city` VALUES (1763, 410000, 411700, '驻马店市', 2);
INSERT INTO `wj_city` VALUES (1764, 411700, 411702, '驿城区', 3);
INSERT INTO `wj_city` VALUES (1765, 411700, 411721, '西平县', 3);
INSERT INTO `wj_city` VALUES (1766, 411700, 411722, '上蔡县', 3);
INSERT INTO `wj_city` VALUES (1767, 411700, 411723, '平舆县', 3);
INSERT INTO `wj_city` VALUES (1768, 411700, 411724, '正阳县', 3);
INSERT INTO `wj_city` VALUES (1769, 411700, 411725, '确山县', 3);
INSERT INTO `wj_city` VALUES (1770, 411700, 411726, '泌阳县', 3);
INSERT INTO `wj_city` VALUES (1771, 411700, 411727, '汝南县', 3);
INSERT INTO `wj_city` VALUES (1772, 411700, 411728, '遂平县', 3);
INSERT INTO `wj_city` VALUES (1773, 411700, 411729, '新蔡县', 3);
INSERT INTO `wj_city` VALUES (1774, 411700, 411771, '河南驻马店经济开发区', 3);
INSERT INTO `wj_city` VALUES (1775, 410000, 419000, '省直辖县级行政区划', 2);
INSERT INTO `wj_city` VALUES (1776, 419000, 419001, '济源市', 3);
INSERT INTO `wj_city` VALUES (1777, 0, 420000, '湖北省', 1);
INSERT INTO `wj_city` VALUES (1778, 420000, 420100, '武汉市', 2);
INSERT INTO `wj_city` VALUES (1779, 420100, 420102, '江岸区', 3);
INSERT INTO `wj_city` VALUES (1780, 420100, 420103, '江汉区', 3);
INSERT INTO `wj_city` VALUES (1781, 420100, 420104, '硚口区', 3);
INSERT INTO `wj_city` VALUES (1782, 420100, 420105, '汉阳区', 3);
INSERT INTO `wj_city` VALUES (1783, 420100, 420106, '武昌区', 3);
INSERT INTO `wj_city` VALUES (1784, 420100, 420107, '青山区', 3);
INSERT INTO `wj_city` VALUES (1785, 420100, 420111, '洪山区', 3);
INSERT INTO `wj_city` VALUES (1786, 420100, 420112, '东西湖区', 3);
INSERT INTO `wj_city` VALUES (1787, 420100, 420113, '汉南区', 3);
INSERT INTO `wj_city` VALUES (1788, 420100, 420114, '蔡甸区', 3);
INSERT INTO `wj_city` VALUES (1789, 420100, 420115, '江夏区', 3);
INSERT INTO `wj_city` VALUES (1790, 420100, 420116, '黄陂区', 3);
INSERT INTO `wj_city` VALUES (1791, 420100, 420117, '新洲区', 3);
INSERT INTO `wj_city` VALUES (1792, 420000, 420200, '黄石市', 2);
INSERT INTO `wj_city` VALUES (1793, 420200, 420202, '黄石港区', 3);
INSERT INTO `wj_city` VALUES (1794, 420200, 420203, '西塞山区', 3);
INSERT INTO `wj_city` VALUES (1795, 420200, 420204, '下陆区', 3);
INSERT INTO `wj_city` VALUES (1796, 420200, 420205, '铁山区', 3);
INSERT INTO `wj_city` VALUES (1797, 420200, 420222, '阳新县', 3);
INSERT INTO `wj_city` VALUES (1798, 420200, 420281, '大冶市', 3);
INSERT INTO `wj_city` VALUES (1799, 420000, 420300, '十堰市', 2);
INSERT INTO `wj_city` VALUES (1800, 420300, 420302, '茅箭区', 3);
INSERT INTO `wj_city` VALUES (1801, 420300, 420303, '张湾区', 3);
INSERT INTO `wj_city` VALUES (1802, 420300, 420304, '郧阳区', 3);
INSERT INTO `wj_city` VALUES (1803, 420300, 420322, '郧西县', 3);
INSERT INTO `wj_city` VALUES (1804, 420300, 420323, '竹山县', 3);
INSERT INTO `wj_city` VALUES (1805, 420300, 420324, '竹溪县', 3);
INSERT INTO `wj_city` VALUES (1806, 420300, 420325, '房县', 3);
INSERT INTO `wj_city` VALUES (1807, 420300, 420381, '丹江口市', 3);
INSERT INTO `wj_city` VALUES (1808, 420000, 420500, '宜昌市', 2);
INSERT INTO `wj_city` VALUES (1809, 420500, 420502, '西陵区', 3);
INSERT INTO `wj_city` VALUES (1810, 420500, 420503, '伍家岗区', 3);
INSERT INTO `wj_city` VALUES (1811, 420500, 420504, '点军区', 3);
INSERT INTO `wj_city` VALUES (1812, 420500, 420505, '猇亭区', 3);
INSERT INTO `wj_city` VALUES (1813, 420500, 420506, '夷陵区', 3);
INSERT INTO `wj_city` VALUES (1814, 420500, 420525, '远安县', 3);
INSERT INTO `wj_city` VALUES (1815, 420500, 420526, '兴山县', 3);
INSERT INTO `wj_city` VALUES (1816, 420500, 420527, '秭归县', 3);
INSERT INTO `wj_city` VALUES (1817, 420500, 420528, '长阳土家族自治县', 3);
INSERT INTO `wj_city` VALUES (1818, 420500, 420529, '五峰土家族自治县', 3);
INSERT INTO `wj_city` VALUES (1819, 420500, 420581, '宜都市', 3);
INSERT INTO `wj_city` VALUES (1820, 420500, 420582, '当阳市', 3);
INSERT INTO `wj_city` VALUES (1821, 420500, 420583, '枝江市', 3);
INSERT INTO `wj_city` VALUES (1822, 420000, 420600, '襄阳市', 2);
INSERT INTO `wj_city` VALUES (1823, 420600, 420602, '襄城区', 3);
INSERT INTO `wj_city` VALUES (1824, 420600, 420606, '樊城区', 3);
INSERT INTO `wj_city` VALUES (1825, 420600, 420607, '襄州区', 3);
INSERT INTO `wj_city` VALUES (1826, 420600, 420624, '南漳县', 3);
INSERT INTO `wj_city` VALUES (1827, 420600, 420625, '谷城县', 3);
INSERT INTO `wj_city` VALUES (1828, 420600, 420626, '保康县', 3);
INSERT INTO `wj_city` VALUES (1829, 420600, 420682, '老河口市', 3);
INSERT INTO `wj_city` VALUES (1830, 420600, 420683, '枣阳市', 3);
INSERT INTO `wj_city` VALUES (1831, 420600, 420684, '宜城市', 3);
INSERT INTO `wj_city` VALUES (1832, 420000, 420700, '鄂州市', 2);
INSERT INTO `wj_city` VALUES (1833, 420700, 420702, '梁子湖区', 3);
INSERT INTO `wj_city` VALUES (1834, 420700, 420703, '华容区', 3);
INSERT INTO `wj_city` VALUES (1835, 420700, 420704, '鄂城区', 3);
INSERT INTO `wj_city` VALUES (1836, 420000, 420800, '荆门市', 2);
INSERT INTO `wj_city` VALUES (1837, 420800, 420802, '东宝区', 3);
INSERT INTO `wj_city` VALUES (1838, 420800, 420804, '掇刀区', 3);
INSERT INTO `wj_city` VALUES (1839, 420800, 420822, '沙洋县', 3);
INSERT INTO `wj_city` VALUES (1840, 420800, 420881, '钟祥市', 3);
INSERT INTO `wj_city` VALUES (1841, 420800, 420882, '京山市', 3);
INSERT INTO `wj_city` VALUES (1842, 420000, 420900, '孝感市', 2);
INSERT INTO `wj_city` VALUES (1843, 420900, 420902, '孝南区', 3);
INSERT INTO `wj_city` VALUES (1844, 420900, 420921, '孝昌县', 3);
INSERT INTO `wj_city` VALUES (1845, 420900, 420922, '大悟县', 3);
INSERT INTO `wj_city` VALUES (1846, 420900, 420923, '云梦县', 3);
INSERT INTO `wj_city` VALUES (1847, 420900, 420981, '应城市', 3);
INSERT INTO `wj_city` VALUES (1848, 420900, 420982, '安陆市', 3);
INSERT INTO `wj_city` VALUES (1849, 420900, 420984, '汉川市', 3);
INSERT INTO `wj_city` VALUES (1850, 420000, 421000, '荆州市', 2);
INSERT INTO `wj_city` VALUES (1851, 421000, 421002, '沙市区', 3);
INSERT INTO `wj_city` VALUES (1852, 421000, 421003, '荆州区', 3);
INSERT INTO `wj_city` VALUES (1853, 421000, 421022, '公安县', 3);
INSERT INTO `wj_city` VALUES (1854, 421000, 421024, '江陵县', 3);
INSERT INTO `wj_city` VALUES (1855, 421000, 421071, '荆州经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (1856, 421000, 421081, '石首市', 3);
INSERT INTO `wj_city` VALUES (1857, 421000, 421083, '洪湖市', 3);
INSERT INTO `wj_city` VALUES (1858, 421000, 421087, '松滋市', 3);
INSERT INTO `wj_city` VALUES (1859, 421000, 421088, '监利市', 3);
INSERT INTO `wj_city` VALUES (1860, 420000, 421100, '黄冈市', 2);
INSERT INTO `wj_city` VALUES (1861, 421100, 421102, '黄州区', 3);
INSERT INTO `wj_city` VALUES (1862, 421100, 421121, '团风县', 3);
INSERT INTO `wj_city` VALUES (1863, 421100, 421122, '红安县', 3);
INSERT INTO `wj_city` VALUES (1864, 421100, 421123, '罗田县', 3);
INSERT INTO `wj_city` VALUES (1865, 421100, 421124, '英山县', 3);
INSERT INTO `wj_city` VALUES (1866, 421100, 421125, '浠水县', 3);
INSERT INTO `wj_city` VALUES (1867, 421100, 421126, '蕲春县', 3);
INSERT INTO `wj_city` VALUES (1868, 421100, 421127, '黄梅县', 3);
INSERT INTO `wj_city` VALUES (1869, 421100, 421171, '龙感湖管理区', 3);
INSERT INTO `wj_city` VALUES (1870, 421100, 421181, '麻城市', 3);
INSERT INTO `wj_city` VALUES (1871, 421100, 421182, '武穴市', 3);
INSERT INTO `wj_city` VALUES (1872, 420000, 421200, '咸宁市', 2);
INSERT INTO `wj_city` VALUES (1873, 421200, 421202, '咸安区', 3);
INSERT INTO `wj_city` VALUES (1874, 421200, 421221, '嘉鱼县', 3);
INSERT INTO `wj_city` VALUES (1875, 421200, 421222, '通城县', 3);
INSERT INTO `wj_city` VALUES (1876, 421200, 421223, '崇阳县', 3);
INSERT INTO `wj_city` VALUES (1877, 421200, 421224, '通山县', 3);
INSERT INTO `wj_city` VALUES (1878, 421200, 421281, '赤壁市', 3);
INSERT INTO `wj_city` VALUES (1879, 420000, 421300, '随州市', 2);
INSERT INTO `wj_city` VALUES (1880, 421300, 421303, '曾都区', 3);
INSERT INTO `wj_city` VALUES (1881, 421300, 421321, '随县', 3);
INSERT INTO `wj_city` VALUES (1882, 421300, 421381, '广水市', 3);
INSERT INTO `wj_city` VALUES (1883, 420000, 422800, '恩施土家族苗族自治州', 2);
INSERT INTO `wj_city` VALUES (1884, 422800, 422801, '恩施市', 3);
INSERT INTO `wj_city` VALUES (1885, 422800, 422802, '利川市', 3);
INSERT INTO `wj_city` VALUES (1886, 422800, 422822, '建始县', 3);
INSERT INTO `wj_city` VALUES (1887, 422800, 422823, '巴东县', 3);
INSERT INTO `wj_city` VALUES (1888, 422800, 422825, '宣恩县', 3);
INSERT INTO `wj_city` VALUES (1889, 422800, 422826, '咸丰县', 3);
INSERT INTO `wj_city` VALUES (1890, 422800, 422827, '来凤县', 3);
INSERT INTO `wj_city` VALUES (1891, 422800, 422828, '鹤峰县', 3);
INSERT INTO `wj_city` VALUES (1892, 420000, 429000, '省直辖县级行政区划', 2);
INSERT INTO `wj_city` VALUES (1893, 429000, 429004, '仙桃市', 3);
INSERT INTO `wj_city` VALUES (1894, 429000, 429005, '潜江市', 3);
INSERT INTO `wj_city` VALUES (1895, 429000, 429006, '天门市', 3);
INSERT INTO `wj_city` VALUES (1896, 429000, 429021, '神农架林区', 3);
INSERT INTO `wj_city` VALUES (1897, 0, 430000, '湖南省', 1);
INSERT INTO `wj_city` VALUES (1898, 430000, 430100, '长沙市', 2);
INSERT INTO `wj_city` VALUES (1899, 430100, 430102, '芙蓉区', 3);
INSERT INTO `wj_city` VALUES (1900, 430100, 430103, '天心区', 3);
INSERT INTO `wj_city` VALUES (1901, 430100, 430104, '岳麓区', 3);
INSERT INTO `wj_city` VALUES (1902, 430100, 430105, '开福区', 3);
INSERT INTO `wj_city` VALUES (1903, 430100, 430111, '雨花区', 3);
INSERT INTO `wj_city` VALUES (1904, 430100, 430112, '望城区', 3);
INSERT INTO `wj_city` VALUES (1905, 430100, 430121, '长沙县', 3);
INSERT INTO `wj_city` VALUES (1906, 430100, 430181, '浏阳市', 3);
INSERT INTO `wj_city` VALUES (1907, 430100, 430182, '宁乡市', 3);
INSERT INTO `wj_city` VALUES (1908, 430000, 430200, '株洲市', 2);
INSERT INTO `wj_city` VALUES (1909, 430200, 430202, '荷塘区', 3);
INSERT INTO `wj_city` VALUES (1910, 430200, 430203, '芦淞区', 3);
INSERT INTO `wj_city` VALUES (1911, 430200, 430204, '石峰区', 3);
INSERT INTO `wj_city` VALUES (1912, 430200, 430211, '天元区', 3);
INSERT INTO `wj_city` VALUES (1913, 430200, 430212, '渌口区', 3);
INSERT INTO `wj_city` VALUES (1914, 430200, 430223, '攸县', 3);
INSERT INTO `wj_city` VALUES (1915, 430200, 430224, '茶陵县', 3);
INSERT INTO `wj_city` VALUES (1916, 430200, 430225, '炎陵县', 3);
INSERT INTO `wj_city` VALUES (1917, 430200, 430281, '醴陵市', 3);
INSERT INTO `wj_city` VALUES (1918, 430000, 430300, '湘潭市', 2);
INSERT INTO `wj_city` VALUES (1919, 430300, 430302, '雨湖区', 3);
INSERT INTO `wj_city` VALUES (1920, 430300, 430304, '岳塘区', 3);
INSERT INTO `wj_city` VALUES (1921, 430300, 430321, '湘潭县', 3);
INSERT INTO `wj_city` VALUES (1922, 430300, 430371, '湖南湘潭高新技术产业园区', 3);
INSERT INTO `wj_city` VALUES (1923, 430300, 430372, '湘潭昭山示范区', 3);
INSERT INTO `wj_city` VALUES (1924, 430300, 430373, '湘潭九华示范区', 3);
INSERT INTO `wj_city` VALUES (1925, 430300, 430381, '湘乡市', 3);
INSERT INTO `wj_city` VALUES (1926, 430300, 430382, '韶山市', 3);
INSERT INTO `wj_city` VALUES (1927, 430000, 430400, '衡阳市', 2);
INSERT INTO `wj_city` VALUES (1928, 430400, 430405, '珠晖区', 3);
INSERT INTO `wj_city` VALUES (1929, 430400, 430406, '雁峰区', 3);
INSERT INTO `wj_city` VALUES (1930, 430400, 430407, '石鼓区', 3);
INSERT INTO `wj_city` VALUES (1931, 430400, 430408, '蒸湘区', 3);
INSERT INTO `wj_city` VALUES (1932, 430400, 430412, '南岳区', 3);
INSERT INTO `wj_city` VALUES (1933, 430400, 430421, '衡阳县', 3);
INSERT INTO `wj_city` VALUES (1934, 430400, 430422, '衡南县', 3);
INSERT INTO `wj_city` VALUES (1935, 430400, 430423, '衡山县', 3);
INSERT INTO `wj_city` VALUES (1936, 430400, 430424, '衡东县', 3);
INSERT INTO `wj_city` VALUES (1937, 430400, 430426, '祁东县', 3);
INSERT INTO `wj_city` VALUES (1938, 430400, 430473, '湖南衡阳松木经济开发区', 3);
INSERT INTO `wj_city` VALUES (1939, 430400, 430476, '湖南衡阳高新技术产业园区', 3);
INSERT INTO `wj_city` VALUES (1940, 430400, 430481, '耒阳市', 3);
INSERT INTO `wj_city` VALUES (1941, 430400, 430482, '常宁市', 3);
INSERT INTO `wj_city` VALUES (1942, 430000, 430500, '邵阳市', 2);
INSERT INTO `wj_city` VALUES (1943, 430500, 430502, '双清区', 3);
INSERT INTO `wj_city` VALUES (1944, 430500, 430503, '大祥区', 3);
INSERT INTO `wj_city` VALUES (1945, 430500, 430511, '北塔区', 3);
INSERT INTO `wj_city` VALUES (1946, 430500, 430522, '新邵县', 3);
INSERT INTO `wj_city` VALUES (1947, 430500, 430523, '邵阳县', 3);
INSERT INTO `wj_city` VALUES (1948, 430500, 430524, '隆回县', 3);
INSERT INTO `wj_city` VALUES (1949, 430500, 430525, '洞口县', 3);
INSERT INTO `wj_city` VALUES (1950, 430500, 430527, '绥宁县', 3);
INSERT INTO `wj_city` VALUES (1951, 430500, 430528, '新宁县', 3);
INSERT INTO `wj_city` VALUES (1952, 430500, 430529, '城步苗族自治县', 3);
INSERT INTO `wj_city` VALUES (1953, 430500, 430581, '武冈市', 3);
INSERT INTO `wj_city` VALUES (1954, 430500, 430582, '邵东市', 3);
INSERT INTO `wj_city` VALUES (1955, 430000, 430600, '岳阳市', 2);
INSERT INTO `wj_city` VALUES (1956, 430600, 430602, '岳阳楼区', 3);
INSERT INTO `wj_city` VALUES (1957, 430600, 430603, '云溪区', 3);
INSERT INTO `wj_city` VALUES (1958, 430600, 430611, '君山区', 3);
INSERT INTO `wj_city` VALUES (1959, 430600, 430621, '岳阳县', 3);
INSERT INTO `wj_city` VALUES (1960, 430600, 430623, '华容县', 3);
INSERT INTO `wj_city` VALUES (1961, 430600, 430624, '湘阴县', 3);
INSERT INTO `wj_city` VALUES (1962, 430600, 430626, '平江县', 3);
INSERT INTO `wj_city` VALUES (1963, 430600, 430671, '岳阳市屈原管理区', 3);
INSERT INTO `wj_city` VALUES (1964, 430600, 430681, '汨罗市', 3);
INSERT INTO `wj_city` VALUES (1965, 430600, 430682, '临湘市', 3);
INSERT INTO `wj_city` VALUES (1966, 430000, 430700, '常德市', 2);
INSERT INTO `wj_city` VALUES (1967, 430700, 430702, '武陵区', 3);
INSERT INTO `wj_city` VALUES (1968, 430700, 430703, '鼎城区', 3);
INSERT INTO `wj_city` VALUES (1969, 430700, 430721, '安乡县', 3);
INSERT INTO `wj_city` VALUES (1970, 430700, 430722, '汉寿县', 3);
INSERT INTO `wj_city` VALUES (1971, 430700, 430723, '澧县', 3);
INSERT INTO `wj_city` VALUES (1972, 430700, 430724, '临澧县', 3);
INSERT INTO `wj_city` VALUES (1973, 430700, 430725, '桃源县', 3);
INSERT INTO `wj_city` VALUES (1974, 430700, 430726, '石门县', 3);
INSERT INTO `wj_city` VALUES (1975, 430700, 430771, '常德市西洞庭管理区', 3);
INSERT INTO `wj_city` VALUES (1976, 430700, 430781, '津市市', 3);
INSERT INTO `wj_city` VALUES (1977, 430000, 430800, '张家界市', 2);
INSERT INTO `wj_city` VALUES (1978, 430800, 430802, '永定区', 3);
INSERT INTO `wj_city` VALUES (1979, 430800, 430811, '武陵源区', 3);
INSERT INTO `wj_city` VALUES (1980, 430800, 430821, '慈利县', 3);
INSERT INTO `wj_city` VALUES (1981, 430800, 430822, '桑植县', 3);
INSERT INTO `wj_city` VALUES (1982, 430000, 430900, '益阳市', 2);
INSERT INTO `wj_city` VALUES (1983, 430900, 430902, '资阳区', 3);
INSERT INTO `wj_city` VALUES (1984, 430900, 430903, '赫山区', 3);
INSERT INTO `wj_city` VALUES (1985, 430900, 430921, '南县', 3);
INSERT INTO `wj_city` VALUES (1986, 430900, 430922, '桃江县', 3);
INSERT INTO `wj_city` VALUES (1987, 430900, 430923, '安化县', 3);
INSERT INTO `wj_city` VALUES (1988, 430900, 430971, '益阳市大通湖管理区', 3);
INSERT INTO `wj_city` VALUES (1989, 430900, 430972, '湖南益阳高新技术产业园区', 3);
INSERT INTO `wj_city` VALUES (1990, 430900, 430981, '沅江市', 3);
INSERT INTO `wj_city` VALUES (1991, 430000, 431000, '郴州市', 2);
INSERT INTO `wj_city` VALUES (1992, 431000, 431002, '北湖区', 3);
INSERT INTO `wj_city` VALUES (1993, 431000, 431003, '苏仙区', 3);
INSERT INTO `wj_city` VALUES (1994, 431000, 431021, '桂阳县', 3);
INSERT INTO `wj_city` VALUES (1995, 431000, 431022, '宜章县', 3);
INSERT INTO `wj_city` VALUES (1996, 431000, 431023, '永兴县', 3);
INSERT INTO `wj_city` VALUES (1997, 431000, 431024, '嘉禾县', 3);
INSERT INTO `wj_city` VALUES (1998, 431000, 431025, '临武县', 3);
INSERT INTO `wj_city` VALUES (1999, 431000, 431026, '汝城县', 3);
INSERT INTO `wj_city` VALUES (2000, 431000, 431027, '桂东县', 3);
INSERT INTO `wj_city` VALUES (2001, 431000, 431028, '安仁县', 3);
INSERT INTO `wj_city` VALUES (2002, 431000, 431081, '资兴市', 3);
INSERT INTO `wj_city` VALUES (2003, 430000, 431100, '永州市', 2);
INSERT INTO `wj_city` VALUES (2004, 431100, 431102, '零陵区', 3);
INSERT INTO `wj_city` VALUES (2005, 431100, 431103, '冷水滩区', 3);
INSERT INTO `wj_city` VALUES (2006, 431100, 431122, '东安县', 3);
INSERT INTO `wj_city` VALUES (2007, 431100, 431123, '双牌县', 3);
INSERT INTO `wj_city` VALUES (2008, 431100, 431124, '道县', 3);
INSERT INTO `wj_city` VALUES (2009, 431100, 431125, '江永县', 3);
INSERT INTO `wj_city` VALUES (2010, 431100, 431126, '宁远县', 3);
INSERT INTO `wj_city` VALUES (2011, 431100, 431127, '蓝山县', 3);
INSERT INTO `wj_city` VALUES (2012, 431100, 431128, '新田县', 3);
INSERT INTO `wj_city` VALUES (2013, 431100, 431129, '江华瑶族自治县', 3);
INSERT INTO `wj_city` VALUES (2014, 431100, 431171, '永州经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (2015, 431100, 431173, '永州市回龙圩管理区', 3);
INSERT INTO `wj_city` VALUES (2016, 431100, 431181, '祁阳市', 3);
INSERT INTO `wj_city` VALUES (2017, 430000, 431200, '怀化市', 2);
INSERT INTO `wj_city` VALUES (2018, 431200, 431202, '鹤城区', 3);
INSERT INTO `wj_city` VALUES (2019, 431200, 431221, '中方县', 3);
INSERT INTO `wj_city` VALUES (2020, 431200, 431222, '沅陵县', 3);
INSERT INTO `wj_city` VALUES (2021, 431200, 431223, '辰溪县', 3);
INSERT INTO `wj_city` VALUES (2022, 431200, 431224, '溆浦县', 3);
INSERT INTO `wj_city` VALUES (2023, 431200, 431225, '会同县', 3);
INSERT INTO `wj_city` VALUES (2024, 431200, 431226, '麻阳苗族自治县', 3);
INSERT INTO `wj_city` VALUES (2025, 431200, 431227, '新晃侗族自治县', 3);
INSERT INTO `wj_city` VALUES (2026, 431200, 431228, '芷江侗族自治县', 3);
INSERT INTO `wj_city` VALUES (2027, 431200, 431229, '靖州苗族侗族自治县', 3);
INSERT INTO `wj_city` VALUES (2028, 431200, 431230, '通道侗族自治县', 3);
INSERT INTO `wj_city` VALUES (2029, 431200, 431271, '怀化市洪江管理区', 3);
INSERT INTO `wj_city` VALUES (2030, 431200, 431281, '洪江市', 3);
INSERT INTO `wj_city` VALUES (2031, 430000, 431300, '娄底市', 2);
INSERT INTO `wj_city` VALUES (2032, 431300, 431302, '娄星区', 3);
INSERT INTO `wj_city` VALUES (2033, 431300, 431321, '双峰县', 3);
INSERT INTO `wj_city` VALUES (2034, 431300, 431322, '新化县', 3);
INSERT INTO `wj_city` VALUES (2035, 431300, 431381, '冷水江市', 3);
INSERT INTO `wj_city` VALUES (2036, 431300, 431382, '涟源市', 3);
INSERT INTO `wj_city` VALUES (2037, 430000, 433100, '湘西土家族苗族自治州', 2);
INSERT INTO `wj_city` VALUES (2038, 433100, 433101, '吉首市', 3);
INSERT INTO `wj_city` VALUES (2039, 433100, 433122, '泸溪县', 3);
INSERT INTO `wj_city` VALUES (2040, 433100, 433123, '凤凰县', 3);
INSERT INTO `wj_city` VALUES (2041, 433100, 433124, '花垣县', 3);
INSERT INTO `wj_city` VALUES (2042, 433100, 433125, '保靖县', 3);
INSERT INTO `wj_city` VALUES (2043, 433100, 433126, '古丈县', 3);
INSERT INTO `wj_city` VALUES (2044, 433100, 433127, '永顺县', 3);
INSERT INTO `wj_city` VALUES (2045, 433100, 433130, '龙山县', 3);
INSERT INTO `wj_city` VALUES (2046, 0, 440000, '广东省', 1);
INSERT INTO `wj_city` VALUES (2047, 440000, 440100, '广州市', 2);
INSERT INTO `wj_city` VALUES (2048, 440100, 440103, '荔湾区', 3);
INSERT INTO `wj_city` VALUES (2049, 440100, 440104, '越秀区', 3);
INSERT INTO `wj_city` VALUES (2050, 440100, 440105, '海珠区', 3);
INSERT INTO `wj_city` VALUES (2051, 440100, 440106, '天河区', 3);
INSERT INTO `wj_city` VALUES (2052, 440100, 440111, '白云区', 3);
INSERT INTO `wj_city` VALUES (2053, 440100, 440112, '黄埔区', 3);
INSERT INTO `wj_city` VALUES (2054, 440100, 440113, '番禺区', 3);
INSERT INTO `wj_city` VALUES (2055, 440100, 440114, '花都区', 3);
INSERT INTO `wj_city` VALUES (2056, 440100, 440115, '南沙区', 3);
INSERT INTO `wj_city` VALUES (2057, 440100, 440117, '从化区', 3);
INSERT INTO `wj_city` VALUES (2058, 440100, 440118, '增城区', 3);
INSERT INTO `wj_city` VALUES (2059, 440000, 440200, '韶关市', 2);
INSERT INTO `wj_city` VALUES (2060, 440200, 440203, '武江区', 3);
INSERT INTO `wj_city` VALUES (2061, 440200, 440204, '浈江区', 3);
INSERT INTO `wj_city` VALUES (2062, 440200, 440205, '曲江区', 3);
INSERT INTO `wj_city` VALUES (2063, 440200, 440222, '始兴县', 3);
INSERT INTO `wj_city` VALUES (2064, 440200, 440224, '仁化县', 3);
INSERT INTO `wj_city` VALUES (2065, 440200, 440229, '翁源县', 3);
INSERT INTO `wj_city` VALUES (2066, 440200, 440232, '乳源瑶族自治县', 3);
INSERT INTO `wj_city` VALUES (2067, 440200, 440233, '新丰县', 3);
INSERT INTO `wj_city` VALUES (2068, 440200, 440281, '乐昌市', 3);
INSERT INTO `wj_city` VALUES (2069, 440200, 440282, '南雄市', 3);
INSERT INTO `wj_city` VALUES (2070, 440000, 440300, '深圳市', 2);
INSERT INTO `wj_city` VALUES (2071, 440300, 440303, '罗湖区', 3);
INSERT INTO `wj_city` VALUES (2072, 440300, 440304, '福田区', 3);
INSERT INTO `wj_city` VALUES (2073, 440300, 440305, '南山区', 3);
INSERT INTO `wj_city` VALUES (2074, 440300, 440306, '宝安区', 3);
INSERT INTO `wj_city` VALUES (2075, 440300, 440307, '龙岗区', 3);
INSERT INTO `wj_city` VALUES (2076, 440300, 440308, '盐田区', 3);
INSERT INTO `wj_city` VALUES (2077, 440300, 440309, '龙华区', 3);
INSERT INTO `wj_city` VALUES (2078, 440300, 440310, '坪山区', 3);
INSERT INTO `wj_city` VALUES (2079, 440300, 440311, '光明区', 3);
INSERT INTO `wj_city` VALUES (2080, 440000, 440400, '珠海市', 2);
INSERT INTO `wj_city` VALUES (2081, 440400, 440402, '香洲区', 3);
INSERT INTO `wj_city` VALUES (2082, 440400, 440403, '斗门区', 3);
INSERT INTO `wj_city` VALUES (2083, 440400, 440404, '金湾区', 3);
INSERT INTO `wj_city` VALUES (2084, 440000, 440500, '汕头市', 2);
INSERT INTO `wj_city` VALUES (2085, 440500, 440507, '龙湖区', 3);
INSERT INTO `wj_city` VALUES (2086, 440500, 440511, '金平区', 3);
INSERT INTO `wj_city` VALUES (2087, 440500, 440512, '濠江区', 3);
INSERT INTO `wj_city` VALUES (2088, 440500, 440513, '潮阳区', 3);
INSERT INTO `wj_city` VALUES (2089, 440500, 440514, '潮南区', 3);
INSERT INTO `wj_city` VALUES (2090, 440500, 440515, '澄海区', 3);
INSERT INTO `wj_city` VALUES (2091, 440500, 440523, '南澳县', 3);
INSERT INTO `wj_city` VALUES (2092, 440000, 440600, '佛山市', 2);
INSERT INTO `wj_city` VALUES (2093, 440600, 440604, '禅城区', 3);
INSERT INTO `wj_city` VALUES (2094, 440600, 440605, '南海区', 3);
INSERT INTO `wj_city` VALUES (2095, 440600, 440606, '顺德区', 3);
INSERT INTO `wj_city` VALUES (2096, 440600, 440607, '三水区', 3);
INSERT INTO `wj_city` VALUES (2097, 440600, 440608, '高明区', 3);
INSERT INTO `wj_city` VALUES (2098, 440000, 440700, '江门市', 2);
INSERT INTO `wj_city` VALUES (2099, 440700, 440703, '蓬江区', 3);
INSERT INTO `wj_city` VALUES (2100, 440700, 440704, '江海区', 3);
INSERT INTO `wj_city` VALUES (2101, 440700, 440705, '新会区', 3);
INSERT INTO `wj_city` VALUES (2102, 440700, 440781, '台山市', 3);
INSERT INTO `wj_city` VALUES (2103, 440700, 440783, '开平市', 3);
INSERT INTO `wj_city` VALUES (2104, 440700, 440784, '鹤山市', 3);
INSERT INTO `wj_city` VALUES (2105, 440700, 440785, '恩平市', 3);
INSERT INTO `wj_city` VALUES (2106, 440000, 440800, '湛江市', 2);
INSERT INTO `wj_city` VALUES (2107, 440800, 440802, '赤坎区', 3);
INSERT INTO `wj_city` VALUES (2108, 440800, 440803, '霞山区', 3);
INSERT INTO `wj_city` VALUES (2109, 440800, 440804, '坡头区', 3);
INSERT INTO `wj_city` VALUES (2110, 440800, 440811, '麻章区', 3);
INSERT INTO `wj_city` VALUES (2111, 440800, 440823, '遂溪县', 3);
INSERT INTO `wj_city` VALUES (2112, 440800, 440825, '徐闻县', 3);
INSERT INTO `wj_city` VALUES (2113, 440800, 440881, '廉江市', 3);
INSERT INTO `wj_city` VALUES (2114, 440800, 440882, '雷州市', 3);
INSERT INTO `wj_city` VALUES (2115, 440800, 440883, '吴川市', 3);
INSERT INTO `wj_city` VALUES (2116, 440000, 440900, '茂名市', 2);
INSERT INTO `wj_city` VALUES (2117, 440900, 440902, '茂南区', 3);
INSERT INTO `wj_city` VALUES (2118, 440900, 440904, '电白区', 3);
INSERT INTO `wj_city` VALUES (2119, 440900, 440981, '高州市', 3);
INSERT INTO `wj_city` VALUES (2120, 440900, 440982, '化州市', 3);
INSERT INTO `wj_city` VALUES (2121, 440900, 440983, '信宜市', 3);
INSERT INTO `wj_city` VALUES (2122, 440000, 441200, '肇庆市', 2);
INSERT INTO `wj_city` VALUES (2123, 441200, 441202, '端州区', 3);
INSERT INTO `wj_city` VALUES (2124, 441200, 441203, '鼎湖区', 3);
INSERT INTO `wj_city` VALUES (2125, 441200, 441204, '高要区', 3);
INSERT INTO `wj_city` VALUES (2126, 441200, 441223, '广宁县', 3);
INSERT INTO `wj_city` VALUES (2127, 441200, 441224, '怀集县', 3);
INSERT INTO `wj_city` VALUES (2128, 441200, 441225, '封开县', 3);
INSERT INTO `wj_city` VALUES (2129, 441200, 441226, '德庆县', 3);
INSERT INTO `wj_city` VALUES (2130, 441200, 441284, '四会市', 3);
INSERT INTO `wj_city` VALUES (2131, 440000, 441300, '惠州市', 2);
INSERT INTO `wj_city` VALUES (2132, 441300, 441302, '惠城区', 3);
INSERT INTO `wj_city` VALUES (2133, 441300, 441303, '惠阳区', 3);
INSERT INTO `wj_city` VALUES (2134, 441300, 441322, '博罗县', 3);
INSERT INTO `wj_city` VALUES (2135, 441300, 441323, '惠东县', 3);
INSERT INTO `wj_city` VALUES (2136, 441300, 441324, '龙门县', 3);
INSERT INTO `wj_city` VALUES (2137, 440000, 441400, '梅州市', 2);
INSERT INTO `wj_city` VALUES (2138, 441400, 441402, '梅江区', 3);
INSERT INTO `wj_city` VALUES (2139, 441400, 441403, '梅县区', 3);
INSERT INTO `wj_city` VALUES (2140, 441400, 441422, '大埔县', 3);
INSERT INTO `wj_city` VALUES (2141, 441400, 441423, '丰顺县', 3);
INSERT INTO `wj_city` VALUES (2142, 441400, 441424, '五华县', 3);
INSERT INTO `wj_city` VALUES (2143, 441400, 441426, '平远县', 3);
INSERT INTO `wj_city` VALUES (2144, 441400, 441427, '蕉岭县', 3);
INSERT INTO `wj_city` VALUES (2145, 441400, 441481, '兴宁市', 3);
INSERT INTO `wj_city` VALUES (2146, 440000, 441500, '汕尾市', 2);
INSERT INTO `wj_city` VALUES (2147, 441500, 441502, '城区', 3);
INSERT INTO `wj_city` VALUES (2148, 441500, 441521, '海丰县', 3);
INSERT INTO `wj_city` VALUES (2149, 441500, 441523, '陆河县', 3);
INSERT INTO `wj_city` VALUES (2150, 441500, 441581, '陆丰市', 3);
INSERT INTO `wj_city` VALUES (2151, 440000, 441600, '河源市', 2);
INSERT INTO `wj_city` VALUES (2152, 441600, 441602, '源城区', 3);
INSERT INTO `wj_city` VALUES (2153, 441600, 441621, '紫金县', 3);
INSERT INTO `wj_city` VALUES (2154, 441600, 441622, '龙川县', 3);
INSERT INTO `wj_city` VALUES (2155, 441600, 441623, '连平县', 3);
INSERT INTO `wj_city` VALUES (2156, 441600, 441624, '和平县', 3);
INSERT INTO `wj_city` VALUES (2157, 441600, 441625, '东源县', 3);
INSERT INTO `wj_city` VALUES (2158, 440000, 441700, '阳江市', 2);
INSERT INTO `wj_city` VALUES (2159, 441700, 441702, '江城区', 3);
INSERT INTO `wj_city` VALUES (2160, 441700, 441704, '阳东区', 3);
INSERT INTO `wj_city` VALUES (2161, 441700, 441721, '阳西县', 3);
INSERT INTO `wj_city` VALUES (2162, 441700, 441781, '阳春市', 3);
INSERT INTO `wj_city` VALUES (2163, 440000, 441800, '清远市', 2);
INSERT INTO `wj_city` VALUES (2164, 441800, 441802, '清城区', 3);
INSERT INTO `wj_city` VALUES (2165, 441800, 441803, '清新区', 3);
INSERT INTO `wj_city` VALUES (2166, 441800, 441821, '佛冈县', 3);
INSERT INTO `wj_city` VALUES (2167, 441800, 441823, '阳山县', 3);
INSERT INTO `wj_city` VALUES (2168, 441800, 441825, '连山壮族瑶族自治县', 3);
INSERT INTO `wj_city` VALUES (2169, 441800, 441826, '连南瑶族自治县', 3);
INSERT INTO `wj_city` VALUES (2170, 441800, 441881, '英德市', 3);
INSERT INTO `wj_city` VALUES (2171, 441800, 441882, '连州市', 3);
INSERT INTO `wj_city` VALUES (2172, 440000, 441900, '东莞市', 2);
INSERT INTO `wj_city` VALUES (2209, 440000, 442000, '中山市', 2);
INSERT INTO `wj_city` VALUES (2233, 440000, 445100, '潮州市', 2);
INSERT INTO `wj_city` VALUES (2234, 445100, 445102, '湘桥区', 3);
INSERT INTO `wj_city` VALUES (2235, 445100, 445103, '潮安区', 3);
INSERT INTO `wj_city` VALUES (2236, 445100, 445122, '饶平县', 3);
INSERT INTO `wj_city` VALUES (2237, 440000, 445200, '揭阳市', 2);
INSERT INTO `wj_city` VALUES (2238, 445200, 445202, '榕城区', 3);
INSERT INTO `wj_city` VALUES (2239, 445200, 445203, '揭东区', 3);
INSERT INTO `wj_city` VALUES (2240, 445200, 445222, '揭西县', 3);
INSERT INTO `wj_city` VALUES (2241, 445200, 445224, '惠来县', 3);
INSERT INTO `wj_city` VALUES (2242, 445200, 445281, '普宁市', 3);
INSERT INTO `wj_city` VALUES (2243, 440000, 445300, '云浮市', 2);
INSERT INTO `wj_city` VALUES (2244, 445300, 445302, '云城区', 3);
INSERT INTO `wj_city` VALUES (2245, 445300, 445303, '云安区', 3);
INSERT INTO `wj_city` VALUES (2246, 445300, 445321, '新兴县', 3);
INSERT INTO `wj_city` VALUES (2247, 445300, 445322, '郁南县', 3);
INSERT INTO `wj_city` VALUES (2248, 445300, 445381, '罗定市', 3);
INSERT INTO `wj_city` VALUES (2249, 0, 450000, '广西壮族自治区', 1);
INSERT INTO `wj_city` VALUES (2250, 450000, 450100, '南宁市', 2);
INSERT INTO `wj_city` VALUES (2251, 450100, 450102, '兴宁区', 3);
INSERT INTO `wj_city` VALUES (2252, 450100, 450103, '青秀区', 3);
INSERT INTO `wj_city` VALUES (2253, 450100, 450105, '江南区', 3);
INSERT INTO `wj_city` VALUES (2254, 450100, 450107, '西乡塘区', 3);
INSERT INTO `wj_city` VALUES (2255, 450100, 450108, '良庆区', 3);
INSERT INTO `wj_city` VALUES (2256, 450100, 450109, '邕宁区', 3);
INSERT INTO `wj_city` VALUES (2257, 450100, 450110, '武鸣区', 3);
INSERT INTO `wj_city` VALUES (2258, 450100, 450123, '隆安县', 3);
INSERT INTO `wj_city` VALUES (2259, 450100, 450124, '马山县', 3);
INSERT INTO `wj_city` VALUES (2260, 450100, 450125, '上林县', 3);
INSERT INTO `wj_city` VALUES (2261, 450100, 450126, '宾阳县', 3);
INSERT INTO `wj_city` VALUES (2262, 450100, 450181, '横州市', 3);
INSERT INTO `wj_city` VALUES (2263, 450000, 450200, '柳州市', 2);
INSERT INTO `wj_city` VALUES (2264, 450200, 450202, '城中区', 3);
INSERT INTO `wj_city` VALUES (2265, 450200, 450203, '鱼峰区', 3);
INSERT INTO `wj_city` VALUES (2266, 450200, 450204, '柳南区', 3);
INSERT INTO `wj_city` VALUES (2267, 450200, 450205, '柳北区', 3);
INSERT INTO `wj_city` VALUES (2268, 450200, 450206, '柳江区', 3);
INSERT INTO `wj_city` VALUES (2269, 450200, 450222, '柳城县', 3);
INSERT INTO `wj_city` VALUES (2270, 450200, 450223, '鹿寨县', 3);
INSERT INTO `wj_city` VALUES (2271, 450200, 450224, '融安县', 3);
INSERT INTO `wj_city` VALUES (2272, 450200, 450225, '融水苗族自治县', 3);
INSERT INTO `wj_city` VALUES (2273, 450200, 450226, '三江侗族自治县', 3);
INSERT INTO `wj_city` VALUES (2274, 450000, 450300, '桂林市', 2);
INSERT INTO `wj_city` VALUES (2275, 450300, 450302, '秀峰区', 3);
INSERT INTO `wj_city` VALUES (2276, 450300, 450303, '叠彩区', 3);
INSERT INTO `wj_city` VALUES (2277, 450300, 450304, '象山区', 3);
INSERT INTO `wj_city` VALUES (2278, 450300, 450305, '七星区', 3);
INSERT INTO `wj_city` VALUES (2279, 450300, 450311, '雁山区', 3);
INSERT INTO `wj_city` VALUES (2280, 450300, 450312, '临桂区', 3);
INSERT INTO `wj_city` VALUES (2281, 450300, 450321, '阳朔县', 3);
INSERT INTO `wj_city` VALUES (2282, 450300, 450323, '灵川县', 3);
INSERT INTO `wj_city` VALUES (2283, 450300, 450324, '全州县', 3);
INSERT INTO `wj_city` VALUES (2284, 450300, 450325, '兴安县', 3);
INSERT INTO `wj_city` VALUES (2285, 450300, 450326, '永福县', 3);
INSERT INTO `wj_city` VALUES (2286, 450300, 450327, '灌阳县', 3);
INSERT INTO `wj_city` VALUES (2287, 450300, 450328, '龙胜各族自治县', 3);
INSERT INTO `wj_city` VALUES (2288, 450300, 450329, '资源县', 3);
INSERT INTO `wj_city` VALUES (2289, 450300, 450330, '平乐县', 3);
INSERT INTO `wj_city` VALUES (2290, 450300, 450332, '恭城瑶族自治县', 3);
INSERT INTO `wj_city` VALUES (2291, 450300, 450381, '荔浦市', 3);
INSERT INTO `wj_city` VALUES (2292, 450000, 450400, '梧州市', 2);
INSERT INTO `wj_city` VALUES (2293, 450400, 450403, '万秀区', 3);
INSERT INTO `wj_city` VALUES (2294, 450400, 450405, '长洲区', 3);
INSERT INTO `wj_city` VALUES (2295, 450400, 450406, '龙圩区', 3);
INSERT INTO `wj_city` VALUES (2296, 450400, 450421, '苍梧县', 3);
INSERT INTO `wj_city` VALUES (2297, 450400, 450422, '藤县', 3);
INSERT INTO `wj_city` VALUES (2298, 450400, 450423, '蒙山县', 3);
INSERT INTO `wj_city` VALUES (2299, 450400, 450481, '岑溪市', 3);
INSERT INTO `wj_city` VALUES (2300, 450000, 450500, '北海市', 2);
INSERT INTO `wj_city` VALUES (2301, 450500, 450502, '海城区', 3);
INSERT INTO `wj_city` VALUES (2302, 450500, 450503, '银海区', 3);
INSERT INTO `wj_city` VALUES (2303, 450500, 450512, '铁山港区', 3);
INSERT INTO `wj_city` VALUES (2304, 450500, 450521, '合浦县', 3);
INSERT INTO `wj_city` VALUES (2305, 450000, 450600, '防城港市', 2);
INSERT INTO `wj_city` VALUES (2306, 450600, 450602, '港口区', 3);
INSERT INTO `wj_city` VALUES (2307, 450600, 450603, '防城区', 3);
INSERT INTO `wj_city` VALUES (2308, 450600, 450621, '上思县', 3);
INSERT INTO `wj_city` VALUES (2309, 450600, 450681, '东兴市', 3);
INSERT INTO `wj_city` VALUES (2310, 450000, 450700, '钦州市', 2);
INSERT INTO `wj_city` VALUES (2311, 450700, 450702, '钦南区', 3);
INSERT INTO `wj_city` VALUES (2312, 450700, 450703, '钦北区', 3);
INSERT INTO `wj_city` VALUES (2313, 450700, 450721, '灵山县', 3);
INSERT INTO `wj_city` VALUES (2314, 450700, 450722, '浦北县', 3);
INSERT INTO `wj_city` VALUES (2315, 450000, 450800, '贵港市', 2);
INSERT INTO `wj_city` VALUES (2316, 450800, 450802, '港北区', 3);
INSERT INTO `wj_city` VALUES (2317, 450800, 450803, '港南区', 3);
INSERT INTO `wj_city` VALUES (2318, 450800, 450804, '覃塘区', 3);
INSERT INTO `wj_city` VALUES (2319, 450800, 450821, '平南县', 3);
INSERT INTO `wj_city` VALUES (2320, 450800, 450881, '桂平市', 3);
INSERT INTO `wj_city` VALUES (2321, 450000, 450900, '玉林市', 2);
INSERT INTO `wj_city` VALUES (2322, 450900, 450902, '玉州区', 3);
INSERT INTO `wj_city` VALUES (2323, 450900, 450903, '福绵区', 3);
INSERT INTO `wj_city` VALUES (2324, 450900, 450921, '容县', 3);
INSERT INTO `wj_city` VALUES (2325, 450900, 450922, '陆川县', 3);
INSERT INTO `wj_city` VALUES (2326, 450900, 450923, '博白县', 3);
INSERT INTO `wj_city` VALUES (2327, 450900, 450924, '兴业县', 3);
INSERT INTO `wj_city` VALUES (2328, 450900, 450981, '北流市', 3);
INSERT INTO `wj_city` VALUES (2329, 450000, 451000, '百色市', 2);
INSERT INTO `wj_city` VALUES (2330, 451000, 451002, '右江区', 3);
INSERT INTO `wj_city` VALUES (2331, 451000, 451003, '田阳区', 3);
INSERT INTO `wj_city` VALUES (2332, 451000, 451022, '田东县', 3);
INSERT INTO `wj_city` VALUES (2333, 451000, 451024, '德保县', 3);
INSERT INTO `wj_city` VALUES (2334, 451000, 451026, '那坡县', 3);
INSERT INTO `wj_city` VALUES (2335, 451000, 451027, '凌云县', 3);
INSERT INTO `wj_city` VALUES (2336, 451000, 451028, '乐业县', 3);
INSERT INTO `wj_city` VALUES (2337, 451000, 451029, '田林县', 3);
INSERT INTO `wj_city` VALUES (2338, 451000, 451030, '西林县', 3);
INSERT INTO `wj_city` VALUES (2339, 451000, 451031, '隆林各族自治县', 3);
INSERT INTO `wj_city` VALUES (2340, 451000, 451081, '靖西市', 3);
INSERT INTO `wj_city` VALUES (2341, 451000, 451082, '平果市', 3);
INSERT INTO `wj_city` VALUES (2342, 450000, 451100, '贺州市', 2);
INSERT INTO `wj_city` VALUES (2343, 451100, 451102, '八步区', 3);
INSERT INTO `wj_city` VALUES (2344, 451100, 451103, '平桂区', 3);
INSERT INTO `wj_city` VALUES (2345, 451100, 451121, '昭平县', 3);
INSERT INTO `wj_city` VALUES (2346, 451100, 451122, '钟山县', 3);
INSERT INTO `wj_city` VALUES (2347, 451100, 451123, '富川瑶族自治县', 3);
INSERT INTO `wj_city` VALUES (2348, 450000, 451200, '河池市', 2);
INSERT INTO `wj_city` VALUES (2349, 451200, 451202, '金城江区', 3);
INSERT INTO `wj_city` VALUES (2350, 451200, 451203, '宜州区', 3);
INSERT INTO `wj_city` VALUES (2351, 451200, 451221, '南丹县', 3);
INSERT INTO `wj_city` VALUES (2352, 451200, 451222, '天峨县', 3);
INSERT INTO `wj_city` VALUES (2353, 451200, 451223, '凤山县', 3);
INSERT INTO `wj_city` VALUES (2354, 451200, 451224, '东兰县', 3);
INSERT INTO `wj_city` VALUES (2355, 451200, 451225, '罗城仫佬族自治县', 3);
INSERT INTO `wj_city` VALUES (2356, 451200, 451226, '环江毛南族自治县', 3);
INSERT INTO `wj_city` VALUES (2357, 451200, 451227, '巴马瑶族自治县', 3);
INSERT INTO `wj_city` VALUES (2358, 451200, 451228, '都安瑶族自治县', 3);
INSERT INTO `wj_city` VALUES (2359, 451200, 451229, '大化瑶族自治县', 3);
INSERT INTO `wj_city` VALUES (2360, 450000, 451300, '来宾市', 2);
INSERT INTO `wj_city` VALUES (2361, 451300, 451302, '兴宾区', 3);
INSERT INTO `wj_city` VALUES (2362, 451300, 451321, '忻城县', 3);
INSERT INTO `wj_city` VALUES (2363, 451300, 451322, '象州县', 3);
INSERT INTO `wj_city` VALUES (2364, 451300, 451323, '武宣县', 3);
INSERT INTO `wj_city` VALUES (2365, 451300, 451324, '金秀瑶族自治县', 3);
INSERT INTO `wj_city` VALUES (2366, 451300, 451381, '合山市', 3);
INSERT INTO `wj_city` VALUES (2367, 450000, 451400, '崇左市', 2);
INSERT INTO `wj_city` VALUES (2368, 451400, 451402, '江州区', 3);
INSERT INTO `wj_city` VALUES (2369, 451400, 451421, '扶绥县', 3);
INSERT INTO `wj_city` VALUES (2370, 451400, 451422, '宁明县', 3);
INSERT INTO `wj_city` VALUES (2371, 451400, 451423, '龙州县', 3);
INSERT INTO `wj_city` VALUES (2372, 451400, 451424, '大新县', 3);
INSERT INTO `wj_city` VALUES (2373, 451400, 451425, '天等县', 3);
INSERT INTO `wj_city` VALUES (2374, 451400, 451481, '凭祥市', 3);
INSERT INTO `wj_city` VALUES (2375, 0, 460000, '海南省', 1);
INSERT INTO `wj_city` VALUES (2376, 460000, 460100, '海口市', 2);
INSERT INTO `wj_city` VALUES (2377, 460100, 460105, '秀英区', 3);
INSERT INTO `wj_city` VALUES (2378, 460100, 460106, '龙华区', 3);
INSERT INTO `wj_city` VALUES (2379, 460100, 460107, '琼山区', 3);
INSERT INTO `wj_city` VALUES (2380, 460100, 460108, '美兰区', 3);
INSERT INTO `wj_city` VALUES (2381, 460000, 460200, '三亚市', 2);
INSERT INTO `wj_city` VALUES (2382, 460200, 460202, '海棠区', 3);
INSERT INTO `wj_city` VALUES (2383, 460200, 460203, '吉阳区', 3);
INSERT INTO `wj_city` VALUES (2384, 460200, 460204, '天涯区', 3);
INSERT INTO `wj_city` VALUES (2385, 460200, 460205, '崖州区', 3);
INSERT INTO `wj_city` VALUES (2386, 460000, 460300, '三沙市', 2);
INSERT INTO `wj_city` VALUES (2387, 460300, 460321, '西沙群岛', 3);
INSERT INTO `wj_city` VALUES (2388, 460300, 460322, '南沙群岛', 3);
INSERT INTO `wj_city` VALUES (2389, 460300, 460323, '中沙群岛的岛礁及其海域', 3);
INSERT INTO `wj_city` VALUES (2390, 460000, 460400, '儋州市', 2);
INSERT INTO `wj_city` VALUES (2409, 460000, 469000, '省直辖县级行政区划', 2);
INSERT INTO `wj_city` VALUES (2410, 469000, 469001, '五指山市', 3);
INSERT INTO `wj_city` VALUES (2411, 469000, 469002, '琼海市', 3);
INSERT INTO `wj_city` VALUES (2412, 469000, 469005, '文昌市', 3);
INSERT INTO `wj_city` VALUES (2413, 469000, 469006, '万宁市', 3);
INSERT INTO `wj_city` VALUES (2414, 469000, 469007, '东方市', 3);
INSERT INTO `wj_city` VALUES (2415, 469000, 469021, '定安县', 3);
INSERT INTO `wj_city` VALUES (2416, 469000, 469022, '屯昌县', 3);
INSERT INTO `wj_city` VALUES (2417, 469000, 469023, '澄迈县', 3);
INSERT INTO `wj_city` VALUES (2418, 469000, 469024, '临高县', 3);
INSERT INTO `wj_city` VALUES (2419, 469000, 469025, '白沙黎族自治县', 3);
INSERT INTO `wj_city` VALUES (2420, 469000, 469026, '昌江黎族自治县', 3);
INSERT INTO `wj_city` VALUES (2421, 469000, 469027, '乐东黎族自治县', 3);
INSERT INTO `wj_city` VALUES (2422, 469000, 469028, '陵水黎族自治县', 3);
INSERT INTO `wj_city` VALUES (2423, 469000, 469029, '保亭黎族苗族自治县', 3);
INSERT INTO `wj_city` VALUES (2424, 469000, 469030, '琼中黎族苗族自治县', 3);
INSERT INTO `wj_city` VALUES (2425, 0, 500000, '重庆市', 1);
INSERT INTO `wj_city` VALUES (2426, 500000, 500100, '重庆市', 2);
INSERT INTO `wj_city` VALUES (2427, 500100, 500101, '万州区', 3);
INSERT INTO `wj_city` VALUES (2428, 500100, 500102, '涪陵区', 3);
INSERT INTO `wj_city` VALUES (2429, 500100, 500103, '渝中区', 3);
INSERT INTO `wj_city` VALUES (2430, 500100, 500104, '大渡口区', 3);
INSERT INTO `wj_city` VALUES (2431, 500100, 500105, '江北区', 3);
INSERT INTO `wj_city` VALUES (2432, 500100, 500106, '沙坪坝区', 3);
INSERT INTO `wj_city` VALUES (2433, 500100, 500107, '九龙坡区', 3);
INSERT INTO `wj_city` VALUES (2434, 500100, 500108, '南岸区', 3);
INSERT INTO `wj_city` VALUES (2435, 500100, 500109, '北碚区', 3);
INSERT INTO `wj_city` VALUES (2436, 500100, 500110, '綦江区', 3);
INSERT INTO `wj_city` VALUES (2437, 500100, 500111, '大足区', 3);
INSERT INTO `wj_city` VALUES (2438, 500100, 500112, '渝北区', 3);
INSERT INTO `wj_city` VALUES (2439, 500100, 500113, '巴南区', 3);
INSERT INTO `wj_city` VALUES (2440, 500100, 500114, '黔江区', 3);
INSERT INTO `wj_city` VALUES (2441, 500100, 500115, '长寿区', 3);
INSERT INTO `wj_city` VALUES (2442, 500100, 500116, '江津区', 3);
INSERT INTO `wj_city` VALUES (2443, 500100, 500117, '合川区', 3);
INSERT INTO `wj_city` VALUES (2444, 500100, 500118, '永川区', 3);
INSERT INTO `wj_city` VALUES (2445, 500100, 500119, '南川区', 3);
INSERT INTO `wj_city` VALUES (2446, 500100, 500120, '璧山区', 3);
INSERT INTO `wj_city` VALUES (2447, 500100, 500151, '铜梁区', 3);
INSERT INTO `wj_city` VALUES (2448, 500100, 500152, '潼南区', 3);
INSERT INTO `wj_city` VALUES (2449, 500100, 500153, '荣昌区', 3);
INSERT INTO `wj_city` VALUES (2450, 500100, 500154, '开州区', 3);
INSERT INTO `wj_city` VALUES (2451, 500100, 500155, '梁平区', 3);
INSERT INTO `wj_city` VALUES (2452, 500100, 500156, '武隆区', 3);
INSERT INTO `wj_city` VALUES (2453, 500100, 500229, '城口县', 3);
INSERT INTO `wj_city` VALUES (2454, 500100, 500230, '丰都县', 3);
INSERT INTO `wj_city` VALUES (2455, 500100, 500231, '垫江县', 3);
INSERT INTO `wj_city` VALUES (2456, 500100, 500233, '忠县', 3);
INSERT INTO `wj_city` VALUES (2457, 500100, 500235, '云阳县', 3);
INSERT INTO `wj_city` VALUES (2458, 500100, 500236, '奉节县', 3);
INSERT INTO `wj_city` VALUES (2459, 500100, 500237, '巫山县', 3);
INSERT INTO `wj_city` VALUES (2460, 500100, 500238, '巫溪县', 3);
INSERT INTO `wj_city` VALUES (2461, 500100, 500240, '石柱土家族自治县', 3);
INSERT INTO `wj_city` VALUES (2462, 500100, 500241, '秀山土家族苗族自治县', 3);
INSERT INTO `wj_city` VALUES (2463, 500100, 500242, '酉阳土家族苗族自治县', 3);
INSERT INTO `wj_city` VALUES (2464, 500100, 500243, '彭水苗族土家族自治县', 3);
INSERT INTO `wj_city` VALUES (2465, 0, 510000, '四川省', 1);
INSERT INTO `wj_city` VALUES (2466, 510000, 510100, '成都市', 2);
INSERT INTO `wj_city` VALUES (2467, 510100, 510104, '锦江区', 3);
INSERT INTO `wj_city` VALUES (2468, 510100, 510105, '青羊区', 3);
INSERT INTO `wj_city` VALUES (2469, 510100, 510106, '金牛区', 3);
INSERT INTO `wj_city` VALUES (2470, 510100, 510107, '武侯区', 3);
INSERT INTO `wj_city` VALUES (2471, 510100, 510108, '成华区', 3);
INSERT INTO `wj_city` VALUES (2472, 510100, 510112, '龙泉驿区', 3);
INSERT INTO `wj_city` VALUES (2473, 510100, 510113, '青白江区', 3);
INSERT INTO `wj_city` VALUES (2474, 510100, 510114, '新都区', 3);
INSERT INTO `wj_city` VALUES (2475, 510100, 510115, '温江区', 3);
INSERT INTO `wj_city` VALUES (2476, 510100, 510116, '双流区', 3);
INSERT INTO `wj_city` VALUES (2477, 510100, 510117, '郫都区', 3);
INSERT INTO `wj_city` VALUES (2478, 510100, 510118, '新津区', 3);
INSERT INTO `wj_city` VALUES (2479, 510100, 510121, '金堂县', 3);
INSERT INTO `wj_city` VALUES (2480, 510100, 510129, '大邑县', 3);
INSERT INTO `wj_city` VALUES (2481, 510100, 510131, '蒲江县', 3);
INSERT INTO `wj_city` VALUES (2482, 510100, 510181, '都江堰市', 3);
INSERT INTO `wj_city` VALUES (2483, 510100, 510182, '彭州市', 3);
INSERT INTO `wj_city` VALUES (2484, 510100, 510183, '邛崃市', 3);
INSERT INTO `wj_city` VALUES (2485, 510100, 510184, '崇州市', 3);
INSERT INTO `wj_city` VALUES (2486, 510100, 510185, '简阳市', 3);
INSERT INTO `wj_city` VALUES (2487, 510000, 510300, '自贡市', 2);
INSERT INTO `wj_city` VALUES (2488, 510300, 510302, '自流井区', 3);
INSERT INTO `wj_city` VALUES (2489, 510300, 510303, '贡井区', 3);
INSERT INTO `wj_city` VALUES (2490, 510300, 510304, '大安区', 3);
INSERT INTO `wj_city` VALUES (2491, 510300, 510311, '沿滩区', 3);
INSERT INTO `wj_city` VALUES (2492, 510300, 510321, '荣县', 3);
INSERT INTO `wj_city` VALUES (2493, 510300, 510322, '富顺县', 3);
INSERT INTO `wj_city` VALUES (2494, 510000, 510400, '攀枝花市', 2);
INSERT INTO `wj_city` VALUES (2495, 510400, 510402, '东区', 3);
INSERT INTO `wj_city` VALUES (2496, 510400, 510403, '西区', 3);
INSERT INTO `wj_city` VALUES (2497, 510400, 510411, '仁和区', 3);
INSERT INTO `wj_city` VALUES (2498, 510400, 510421, '米易县', 3);
INSERT INTO `wj_city` VALUES (2499, 510400, 510422, '盐边县', 3);
INSERT INTO `wj_city` VALUES (2500, 510000, 510500, '泸州市', 2);
INSERT INTO `wj_city` VALUES (2501, 510500, 510502, '江阳区', 3);
INSERT INTO `wj_city` VALUES (2502, 510500, 510503, '纳溪区', 3);
INSERT INTO `wj_city` VALUES (2503, 510500, 510504, '龙马潭区', 3);
INSERT INTO `wj_city` VALUES (2504, 510500, 510521, '泸县', 3);
INSERT INTO `wj_city` VALUES (2505, 510500, 510522, '合江县', 3);
INSERT INTO `wj_city` VALUES (2506, 510500, 510524, '叙永县', 3);
INSERT INTO `wj_city` VALUES (2507, 510500, 510525, '古蔺县', 3);
INSERT INTO `wj_city` VALUES (2508, 510000, 510600, '德阳市', 2);
INSERT INTO `wj_city` VALUES (2509, 510600, 510603, '旌阳区', 3);
INSERT INTO `wj_city` VALUES (2510, 510600, 510604, '罗江区', 3);
INSERT INTO `wj_city` VALUES (2511, 510600, 510623, '中江县', 3);
INSERT INTO `wj_city` VALUES (2512, 510600, 510681, '广汉市', 3);
INSERT INTO `wj_city` VALUES (2513, 510600, 510682, '什邡市', 3);
INSERT INTO `wj_city` VALUES (2514, 510600, 510683, '绵竹市', 3);
INSERT INTO `wj_city` VALUES (2515, 510000, 510700, '绵阳市', 2);
INSERT INTO `wj_city` VALUES (2516, 510700, 510703, '涪城区', 3);
INSERT INTO `wj_city` VALUES (2517, 510700, 510704, '游仙区', 3);
INSERT INTO `wj_city` VALUES (2518, 510700, 510705, '安州区', 3);
INSERT INTO `wj_city` VALUES (2519, 510700, 510722, '三台县', 3);
INSERT INTO `wj_city` VALUES (2520, 510700, 510723, '盐亭县', 3);
INSERT INTO `wj_city` VALUES (2521, 510700, 510725, '梓潼县', 3);
INSERT INTO `wj_city` VALUES (2522, 510700, 510726, '北川羌族自治县', 3);
INSERT INTO `wj_city` VALUES (2523, 510700, 510727, '平武县', 3);
INSERT INTO `wj_city` VALUES (2524, 510700, 510781, '江油市', 3);
INSERT INTO `wj_city` VALUES (2525, 510000, 510800, '广元市', 2);
INSERT INTO `wj_city` VALUES (2526, 510800, 510802, '利州区', 3);
INSERT INTO `wj_city` VALUES (2527, 510800, 510811, '昭化区', 3);
INSERT INTO `wj_city` VALUES (2528, 510800, 510812, '朝天区', 3);
INSERT INTO `wj_city` VALUES (2529, 510800, 510821, '旺苍县', 3);
INSERT INTO `wj_city` VALUES (2530, 510800, 510822, '青川县', 3);
INSERT INTO `wj_city` VALUES (2531, 510800, 510823, '剑阁县', 3);
INSERT INTO `wj_city` VALUES (2532, 510800, 510824, '苍溪县', 3);
INSERT INTO `wj_city` VALUES (2533, 510000, 510900, '遂宁市', 2);
INSERT INTO `wj_city` VALUES (2534, 510900, 510903, '船山区', 3);
INSERT INTO `wj_city` VALUES (2535, 510900, 510904, '安居区', 3);
INSERT INTO `wj_city` VALUES (2536, 510900, 510921, '蓬溪县', 3);
INSERT INTO `wj_city` VALUES (2537, 510900, 510923, '大英县', 3);
INSERT INTO `wj_city` VALUES (2538, 510900, 510981, '射洪市', 3);
INSERT INTO `wj_city` VALUES (2539, 510000, 511000, '内江市', 2);
INSERT INTO `wj_city` VALUES (2540, 511000, 511002, '市中区', 3);
INSERT INTO `wj_city` VALUES (2541, 511000, 511011, '东兴区', 3);
INSERT INTO `wj_city` VALUES (2542, 511000, 511024, '威远县', 3);
INSERT INTO `wj_city` VALUES (2543, 511000, 511025, '资中县', 3);
INSERT INTO `wj_city` VALUES (2544, 511000, 511083, '隆昌市', 3);
INSERT INTO `wj_city` VALUES (2545, 510000, 511100, '乐山市', 2);
INSERT INTO `wj_city` VALUES (2546, 511100, 511102, '市中区', 3);
INSERT INTO `wj_city` VALUES (2547, 511100, 511111, '沙湾区', 3);
INSERT INTO `wj_city` VALUES (2548, 511100, 511112, '五通桥区', 3);
INSERT INTO `wj_city` VALUES (2549, 511100, 511113, '金口河区', 3);
INSERT INTO `wj_city` VALUES (2550, 511100, 511123, '犍为县', 3);
INSERT INTO `wj_city` VALUES (2551, 511100, 511124, '井研县', 3);
INSERT INTO `wj_city` VALUES (2552, 511100, 511126, '夹江县', 3);
INSERT INTO `wj_city` VALUES (2553, 511100, 511129, '沐川县', 3);
INSERT INTO `wj_city` VALUES (2554, 511100, 511132, '峨边彝族自治县', 3);
INSERT INTO `wj_city` VALUES (2555, 511100, 511133, '马边彝族自治县', 3);
INSERT INTO `wj_city` VALUES (2556, 511100, 511181, '峨眉山市', 3);
INSERT INTO `wj_city` VALUES (2557, 510000, 511300, '南充市', 2);
INSERT INTO `wj_city` VALUES (2558, 511300, 511302, '顺庆区', 3);
INSERT INTO `wj_city` VALUES (2559, 511300, 511303, '高坪区', 3);
INSERT INTO `wj_city` VALUES (2560, 511300, 511304, '嘉陵区', 3);
INSERT INTO `wj_city` VALUES (2561, 511300, 511321, '南部县', 3);
INSERT INTO `wj_city` VALUES (2562, 511300, 511322, '营山县', 3);
INSERT INTO `wj_city` VALUES (2563, 511300, 511323, '蓬安县', 3);
INSERT INTO `wj_city` VALUES (2564, 511300, 511324, '仪陇县', 3);
INSERT INTO `wj_city` VALUES (2565, 511300, 511325, '西充县', 3);
INSERT INTO `wj_city` VALUES (2566, 511300, 511381, '阆中市', 3);
INSERT INTO `wj_city` VALUES (2567, 510000, 511400, '眉山市', 2);
INSERT INTO `wj_city` VALUES (2568, 511400, 511402, '东坡区', 3);
INSERT INTO `wj_city` VALUES (2569, 511400, 511403, '彭山区', 3);
INSERT INTO `wj_city` VALUES (2570, 511400, 511421, '仁寿县', 3);
INSERT INTO `wj_city` VALUES (2571, 511400, 511423, '洪雅县', 3);
INSERT INTO `wj_city` VALUES (2572, 511400, 511424, '丹棱县', 3);
INSERT INTO `wj_city` VALUES (2573, 511400, 511425, '青神县', 3);
INSERT INTO `wj_city` VALUES (2574, 510000, 511500, '宜宾市', 2);
INSERT INTO `wj_city` VALUES (2575, 511500, 511502, '翠屏区', 3);
INSERT INTO `wj_city` VALUES (2576, 511500, 511503, '南溪区', 3);
INSERT INTO `wj_city` VALUES (2577, 511500, 511504, '叙州区', 3);
INSERT INTO `wj_city` VALUES (2578, 511500, 511523, '江安县', 3);
INSERT INTO `wj_city` VALUES (2579, 511500, 511524, '长宁县', 3);
INSERT INTO `wj_city` VALUES (2580, 511500, 511525, '高县', 3);
INSERT INTO `wj_city` VALUES (2581, 511500, 511526, '珙县', 3);
INSERT INTO `wj_city` VALUES (2582, 511500, 511527, '筠连县', 3);
INSERT INTO `wj_city` VALUES (2583, 511500, 511528, '兴文县', 3);
INSERT INTO `wj_city` VALUES (2584, 511500, 511529, '屏山县', 3);
INSERT INTO `wj_city` VALUES (2585, 510000, 511600, '广安市', 2);
INSERT INTO `wj_city` VALUES (2586, 511600, 511602, '广安区', 3);
INSERT INTO `wj_city` VALUES (2587, 511600, 511603, '前锋区', 3);
INSERT INTO `wj_city` VALUES (2588, 511600, 511621, '岳池县', 3);
INSERT INTO `wj_city` VALUES (2589, 511600, 511622, '武胜县', 3);
INSERT INTO `wj_city` VALUES (2590, 511600, 511623, '邻水县', 3);
INSERT INTO `wj_city` VALUES (2591, 511600, 511681, '华蓥市', 3);
INSERT INTO `wj_city` VALUES (2592, 510000, 511700, '达州市', 2);
INSERT INTO `wj_city` VALUES (2593, 511700, 511702, '通川区', 3);
INSERT INTO `wj_city` VALUES (2594, 511700, 511703, '达川区', 3);
INSERT INTO `wj_city` VALUES (2595, 511700, 511722, '宣汉县', 3);
INSERT INTO `wj_city` VALUES (2596, 511700, 511723, '开江县', 3);
INSERT INTO `wj_city` VALUES (2597, 511700, 511724, '大竹县', 3);
INSERT INTO `wj_city` VALUES (2598, 511700, 511725, '渠县', 3);
INSERT INTO `wj_city` VALUES (2599, 511700, 511781, '万源市', 3);
INSERT INTO `wj_city` VALUES (2600, 510000, 511800, '雅安市', 2);
INSERT INTO `wj_city` VALUES (2601, 511800, 511802, '雨城区', 3);
INSERT INTO `wj_city` VALUES (2602, 511800, 511803, '名山区', 3);
INSERT INTO `wj_city` VALUES (2603, 511800, 511822, '荥经县', 3);
INSERT INTO `wj_city` VALUES (2604, 511800, 511823, '汉源县', 3);
INSERT INTO `wj_city` VALUES (2605, 511800, 511824, '石棉县', 3);
INSERT INTO `wj_city` VALUES (2606, 511800, 511825, '天全县', 3);
INSERT INTO `wj_city` VALUES (2607, 511800, 511826, '芦山县', 3);
INSERT INTO `wj_city` VALUES (2608, 511800, 511827, '宝兴县', 3);
INSERT INTO `wj_city` VALUES (2609, 510000, 511900, '巴中市', 2);
INSERT INTO `wj_city` VALUES (2610, 511900, 511902, '巴州区', 3);
INSERT INTO `wj_city` VALUES (2611, 511900, 511903, '恩阳区', 3);
INSERT INTO `wj_city` VALUES (2612, 511900, 511921, '通江县', 3);
INSERT INTO `wj_city` VALUES (2613, 511900, 511922, '南江县', 3);
INSERT INTO `wj_city` VALUES (2614, 511900, 511923, '平昌县', 3);
INSERT INTO `wj_city` VALUES (2615, 510000, 512000, '资阳市', 2);
INSERT INTO `wj_city` VALUES (2616, 512000, 512002, '雁江区', 3);
INSERT INTO `wj_city` VALUES (2617, 512000, 512021, '安岳县', 3);
INSERT INTO `wj_city` VALUES (2618, 512000, 512022, '乐至县', 3);
INSERT INTO `wj_city` VALUES (2619, 510000, 513200, '阿坝藏族羌族自治州', 2);
INSERT INTO `wj_city` VALUES (2620, 513200, 513201, '马尔康市', 3);
INSERT INTO `wj_city` VALUES (2621, 513200, 513221, '汶川县', 3);
INSERT INTO `wj_city` VALUES (2622, 513200, 513222, '理县', 3);
INSERT INTO `wj_city` VALUES (2623, 513200, 513223, '茂县', 3);
INSERT INTO `wj_city` VALUES (2624, 513200, 513224, '松潘县', 3);
INSERT INTO `wj_city` VALUES (2625, 513200, 513225, '九寨沟县', 3);
INSERT INTO `wj_city` VALUES (2626, 513200, 513226, '金川县', 3);
INSERT INTO `wj_city` VALUES (2627, 513200, 513227, '小金县', 3);
INSERT INTO `wj_city` VALUES (2628, 513200, 513228, '黑水县', 3);
INSERT INTO `wj_city` VALUES (2629, 513200, 513230, '壤塘县', 3);
INSERT INTO `wj_city` VALUES (2630, 513200, 513231, '阿坝县', 3);
INSERT INTO `wj_city` VALUES (2631, 513200, 513232, '若尔盖县', 3);
INSERT INTO `wj_city` VALUES (2632, 513200, 513233, '红原县', 3);
INSERT INTO `wj_city` VALUES (2633, 510000, 513300, '甘孜藏族自治州', 2);
INSERT INTO `wj_city` VALUES (2634, 513300, 513301, '康定市', 3);
INSERT INTO `wj_city` VALUES (2635, 513300, 513322, '泸定县', 3);
INSERT INTO `wj_city` VALUES (2636, 513300, 513323, '丹巴县', 3);
INSERT INTO `wj_city` VALUES (2637, 513300, 513324, '九龙县', 3);
INSERT INTO `wj_city` VALUES (2638, 513300, 513325, '雅江县', 3);
INSERT INTO `wj_city` VALUES (2639, 513300, 513326, '道孚县', 3);
INSERT INTO `wj_city` VALUES (2640, 513300, 513327, '炉霍县', 3);
INSERT INTO `wj_city` VALUES (2641, 513300, 513328, '甘孜县', 3);
INSERT INTO `wj_city` VALUES (2642, 513300, 513329, '新龙县', 3);
INSERT INTO `wj_city` VALUES (2643, 513300, 513330, '德格县', 3);
INSERT INTO `wj_city` VALUES (2644, 513300, 513331, '白玉县', 3);
INSERT INTO `wj_city` VALUES (2645, 513300, 513332, '石渠县', 3);
INSERT INTO `wj_city` VALUES (2646, 513300, 513333, '色达县', 3);
INSERT INTO `wj_city` VALUES (2647, 513300, 513334, '理塘县', 3);
INSERT INTO `wj_city` VALUES (2648, 513300, 513335, '巴塘县', 3);
INSERT INTO `wj_city` VALUES (2649, 513300, 513336, '乡城县', 3);
INSERT INTO `wj_city` VALUES (2650, 513300, 513337, '稻城县', 3);
INSERT INTO `wj_city` VALUES (2651, 513300, 513338, '得荣县', 3);
INSERT INTO `wj_city` VALUES (2652, 510000, 513400, '凉山彝族自治州', 2);
INSERT INTO `wj_city` VALUES (2653, 513400, 513401, '西昌市', 3);
INSERT INTO `wj_city` VALUES (2654, 513400, 513402, '会理市', 3);
INSERT INTO `wj_city` VALUES (2655, 513400, 513422, '木里藏族自治县', 3);
INSERT INTO `wj_city` VALUES (2656, 513400, 513423, '盐源县', 3);
INSERT INTO `wj_city` VALUES (2657, 513400, 513424, '德昌县', 3);
INSERT INTO `wj_city` VALUES (2658, 513400, 513426, '会东县', 3);
INSERT INTO `wj_city` VALUES (2659, 513400, 513427, '宁南县', 3);
INSERT INTO `wj_city` VALUES (2660, 513400, 513428, '普格县', 3);
INSERT INTO `wj_city` VALUES (2661, 513400, 513429, '布拖县', 3);
INSERT INTO `wj_city` VALUES (2662, 513400, 513430, '金阳县', 3);
INSERT INTO `wj_city` VALUES (2663, 513400, 513431, '昭觉县', 3);
INSERT INTO `wj_city` VALUES (2664, 513400, 513432, '喜德县', 3);
INSERT INTO `wj_city` VALUES (2665, 513400, 513433, '冕宁县', 3);
INSERT INTO `wj_city` VALUES (2666, 513400, 513434, '越西县', 3);
INSERT INTO `wj_city` VALUES (2667, 513400, 513435, '甘洛县', 3);
INSERT INTO `wj_city` VALUES (2668, 513400, 513436, '美姑县', 3);
INSERT INTO `wj_city` VALUES (2669, 513400, 513437, '雷波县', 3);
INSERT INTO `wj_city` VALUES (2670, 0, 520000, '贵州省', 1);
INSERT INTO `wj_city` VALUES (2671, 520000, 520100, '贵阳市', 2);
INSERT INTO `wj_city` VALUES (2672, 520100, 520102, '南明区', 3);
INSERT INTO `wj_city` VALUES (2673, 520100, 520103, '云岩区', 3);
INSERT INTO `wj_city` VALUES (2674, 520100, 520111, '花溪区', 3);
INSERT INTO `wj_city` VALUES (2675, 520100, 520112, '乌当区', 3);
INSERT INTO `wj_city` VALUES (2676, 520100, 520113, '白云区', 3);
INSERT INTO `wj_city` VALUES (2677, 520100, 520115, '观山湖区', 3);
INSERT INTO `wj_city` VALUES (2678, 520100, 520121, '开阳县', 3);
INSERT INTO `wj_city` VALUES (2679, 520100, 520122, '息烽县', 3);
INSERT INTO `wj_city` VALUES (2680, 520100, 520123, '修文县', 3);
INSERT INTO `wj_city` VALUES (2681, 520100, 520181, '清镇市', 3);
INSERT INTO `wj_city` VALUES (2682, 520000, 520200, '六盘水市', 2);
INSERT INTO `wj_city` VALUES (2683, 520200, 520201, '钟山区', 3);
INSERT INTO `wj_city` VALUES (2684, 520200, 520203, '六枝特区', 3);
INSERT INTO `wj_city` VALUES (2685, 520200, 520204, '水城区', 3);
INSERT INTO `wj_city` VALUES (2686, 520200, 520281, '盘州市', 3);
INSERT INTO `wj_city` VALUES (2687, 520000, 520300, '遵义市', 2);
INSERT INTO `wj_city` VALUES (2688, 520300, 520302, '红花岗区', 3);
INSERT INTO `wj_city` VALUES (2689, 520300, 520303, '汇川区', 3);
INSERT INTO `wj_city` VALUES (2690, 520300, 520304, '播州区', 3);
INSERT INTO `wj_city` VALUES (2691, 520300, 520322, '桐梓县', 3);
INSERT INTO `wj_city` VALUES (2692, 520300, 520323, '绥阳县', 3);
INSERT INTO `wj_city` VALUES (2693, 520300, 520324, '正安县', 3);
INSERT INTO `wj_city` VALUES (2694, 520300, 520325, '道真仡佬族苗族自治县', 3);
INSERT INTO `wj_city` VALUES (2695, 520300, 520326, '务川仡佬族苗族自治县', 3);
INSERT INTO `wj_city` VALUES (2696, 520300, 520327, '凤冈县', 3);
INSERT INTO `wj_city` VALUES (2697, 520300, 520328, '湄潭县', 3);
INSERT INTO `wj_city` VALUES (2698, 520300, 520329, '余庆县', 3);
INSERT INTO `wj_city` VALUES (2699, 520300, 520330, '习水县', 3);
INSERT INTO `wj_city` VALUES (2700, 520300, 520381, '赤水市', 3);
INSERT INTO `wj_city` VALUES (2701, 520300, 520382, '仁怀市', 3);
INSERT INTO `wj_city` VALUES (2702, 520000, 520400, '安顺市', 2);
INSERT INTO `wj_city` VALUES (2703, 520400, 520402, '西秀区', 3);
INSERT INTO `wj_city` VALUES (2704, 520400, 520403, '平坝区', 3);
INSERT INTO `wj_city` VALUES (2705, 520400, 520422, '普定县', 3);
INSERT INTO `wj_city` VALUES (2706, 520400, 520423, '镇宁布依族苗族自治县', 3);
INSERT INTO `wj_city` VALUES (2707, 520400, 520424, '关岭布依族苗族自治县', 3);
INSERT INTO `wj_city` VALUES (2708, 520400, 520425, '紫云苗族布依族自治县', 3);
INSERT INTO `wj_city` VALUES (2709, 520000, 520500, '毕节市', 2);
INSERT INTO `wj_city` VALUES (2710, 520500, 520502, '七星关区', 3);
INSERT INTO `wj_city` VALUES (2711, 520500, 520521, '大方县', 3);
INSERT INTO `wj_city` VALUES (2712, 520500, 520523, '金沙县', 3);
INSERT INTO `wj_city` VALUES (2713, 520500, 520524, '织金县', 3);
INSERT INTO `wj_city` VALUES (2714, 520500, 520525, '纳雍县', 3);
INSERT INTO `wj_city` VALUES (2715, 520500, 520526, '威宁彝族回族苗族自治县', 3);
INSERT INTO `wj_city` VALUES (2716, 520500, 520527, '赫章县', 3);
INSERT INTO `wj_city` VALUES (2717, 520500, 520581, '黔西市', 3);
INSERT INTO `wj_city` VALUES (2718, 520000, 520600, '铜仁市', 2);
INSERT INTO `wj_city` VALUES (2719, 520600, 520602, '碧江区', 3);
INSERT INTO `wj_city` VALUES (2720, 520600, 520603, '万山区', 3);
INSERT INTO `wj_city` VALUES (2721, 520600, 520621, '江口县', 3);
INSERT INTO `wj_city` VALUES (2722, 520600, 520622, '玉屏侗族自治县', 3);
INSERT INTO `wj_city` VALUES (2723, 520600, 520623, '石阡县', 3);
INSERT INTO `wj_city` VALUES (2724, 520600, 520624, '思南县', 3);
INSERT INTO `wj_city` VALUES (2725, 520600, 520625, '印江土家族苗族自治县', 3);
INSERT INTO `wj_city` VALUES (2726, 520600, 520626, '德江县', 3);
INSERT INTO `wj_city` VALUES (2727, 520600, 520627, '沿河土家族自治县', 3);
INSERT INTO `wj_city` VALUES (2728, 520600, 520628, '松桃苗族自治县', 3);
INSERT INTO `wj_city` VALUES (2729, 520000, 522300, '黔西南布依族苗族自治州', 2);
INSERT INTO `wj_city` VALUES (2730, 522300, 522301, '兴义市', 3);
INSERT INTO `wj_city` VALUES (2731, 522300, 522302, '兴仁市', 3);
INSERT INTO `wj_city` VALUES (2732, 522300, 522323, '普安县', 3);
INSERT INTO `wj_city` VALUES (2733, 522300, 522324, '晴隆县', 3);
INSERT INTO `wj_city` VALUES (2734, 522300, 522325, '贞丰县', 3);
INSERT INTO `wj_city` VALUES (2735, 522300, 522326, '望谟县', 3);
INSERT INTO `wj_city` VALUES (2736, 522300, 522327, '册亨县', 3);
INSERT INTO `wj_city` VALUES (2737, 522300, 522328, '安龙县', 3);
INSERT INTO `wj_city` VALUES (2738, 520000, 522600, '黔东南苗族侗族自治州', 2);
INSERT INTO `wj_city` VALUES (2739, 522600, 522601, '凯里市', 3);
INSERT INTO `wj_city` VALUES (2740, 522600, 522622, '黄平县', 3);
INSERT INTO `wj_city` VALUES (2741, 522600, 522623, '施秉县', 3);
INSERT INTO `wj_city` VALUES (2742, 522600, 522624, '三穗县', 3);
INSERT INTO `wj_city` VALUES (2743, 522600, 522625, '镇远县', 3);
INSERT INTO `wj_city` VALUES (2744, 522600, 522626, '岑巩县', 3);
INSERT INTO `wj_city` VALUES (2745, 522600, 522627, '天柱县', 3);
INSERT INTO `wj_city` VALUES (2746, 522600, 522628, '锦屏县', 3);
INSERT INTO `wj_city` VALUES (2747, 522600, 522629, '剑河县', 3);
INSERT INTO `wj_city` VALUES (2748, 522600, 522630, '台江县', 3);
INSERT INTO `wj_city` VALUES (2749, 522600, 522631, '黎平县', 3);
INSERT INTO `wj_city` VALUES (2750, 522600, 522632, '榕江县', 3);
INSERT INTO `wj_city` VALUES (2751, 522600, 522633, '从江县', 3);
INSERT INTO `wj_city` VALUES (2752, 522600, 522634, '雷山县', 3);
INSERT INTO `wj_city` VALUES (2753, 522600, 522635, '麻江县', 3);
INSERT INTO `wj_city` VALUES (2754, 522600, 522636, '丹寨县', 3);
INSERT INTO `wj_city` VALUES (2755, 520000, 522700, '黔南布依族苗族自治州', 2);
INSERT INTO `wj_city` VALUES (2756, 522700, 522701, '都匀市', 3);
INSERT INTO `wj_city` VALUES (2757, 522700, 522702, '福泉市', 3);
INSERT INTO `wj_city` VALUES (2758, 522700, 522722, '荔波县', 3);
INSERT INTO `wj_city` VALUES (2759, 522700, 522723, '贵定县', 3);
INSERT INTO `wj_city` VALUES (2760, 522700, 522725, '瓮安县', 3);
INSERT INTO `wj_city` VALUES (2761, 522700, 522726, '独山县', 3);
INSERT INTO `wj_city` VALUES (2762, 522700, 522727, '平塘县', 3);
INSERT INTO `wj_city` VALUES (2763, 522700, 522728, '罗甸县', 3);
INSERT INTO `wj_city` VALUES (2764, 522700, 522729, '长顺县', 3);
INSERT INTO `wj_city` VALUES (2765, 522700, 522730, '龙里县', 3);
INSERT INTO `wj_city` VALUES (2766, 522700, 522731, '惠水县', 3);
INSERT INTO `wj_city` VALUES (2767, 522700, 522732, '三都水族自治县', 3);
INSERT INTO `wj_city` VALUES (2768, 0, 530000, '云南省', 1);
INSERT INTO `wj_city` VALUES (2769, 530000, 530100, '昆明市', 2);
INSERT INTO `wj_city` VALUES (2770, 530100, 530102, '五华区', 3);
INSERT INTO `wj_city` VALUES (2771, 530100, 530103, '盘龙区', 3);
INSERT INTO `wj_city` VALUES (2772, 530100, 530111, '官渡区', 3);
INSERT INTO `wj_city` VALUES (2773, 530100, 530112, '西山区', 3);
INSERT INTO `wj_city` VALUES (2774, 530100, 530113, '东川区', 3);
INSERT INTO `wj_city` VALUES (2775, 530100, 530114, '呈贡区', 3);
INSERT INTO `wj_city` VALUES (2776, 530100, 530115, '晋宁区', 3);
INSERT INTO `wj_city` VALUES (2777, 530100, 530124, '富民县', 3);
INSERT INTO `wj_city` VALUES (2778, 530100, 530125, '宜良县', 3);
INSERT INTO `wj_city` VALUES (2779, 530100, 530126, '石林彝族自治县', 3);
INSERT INTO `wj_city` VALUES (2780, 530100, 530127, '嵩明县', 3);
INSERT INTO `wj_city` VALUES (2781, 530100, 530128, '禄劝彝族苗族自治县', 3);
INSERT INTO `wj_city` VALUES (2782, 530100, 530129, '寻甸回族彝族自治县', 3);
INSERT INTO `wj_city` VALUES (2783, 530100, 530181, '安宁市', 3);
INSERT INTO `wj_city` VALUES (2784, 530000, 530300, '曲靖市', 2);
INSERT INTO `wj_city` VALUES (2785, 530300, 530302, '麒麟区', 3);
INSERT INTO `wj_city` VALUES (2786, 530300, 530303, '沾益区', 3);
INSERT INTO `wj_city` VALUES (2787, 530300, 530304, '马龙区', 3);
INSERT INTO `wj_city` VALUES (2788, 530300, 530322, '陆良县', 3);
INSERT INTO `wj_city` VALUES (2789, 530300, 530323, '师宗县', 3);
INSERT INTO `wj_city` VALUES (2790, 530300, 530324, '罗平县', 3);
INSERT INTO `wj_city` VALUES (2791, 530300, 530325, '富源县', 3);
INSERT INTO `wj_city` VALUES (2792, 530300, 530326, '会泽县', 3);
INSERT INTO `wj_city` VALUES (2793, 530300, 530381, '宣威市', 3);
INSERT INTO `wj_city` VALUES (2794, 530000, 530400, '玉溪市', 2);
INSERT INTO `wj_city` VALUES (2795, 530400, 530402, '红塔区', 3);
INSERT INTO `wj_city` VALUES (2796, 530400, 530403, '江川区', 3);
INSERT INTO `wj_city` VALUES (2797, 530400, 530423, '通海县', 3);
INSERT INTO `wj_city` VALUES (2798, 530400, 530424, '华宁县', 3);
INSERT INTO `wj_city` VALUES (2799, 530400, 530425, '易门县', 3);
INSERT INTO `wj_city` VALUES (2800, 530400, 530426, '峨山彝族自治县', 3);
INSERT INTO `wj_city` VALUES (2801, 530400, 530427, '新平彝族傣族自治县', 3);
INSERT INTO `wj_city` VALUES (2802, 530400, 530428, '元江哈尼族彝族傣族自治县', 3);
INSERT INTO `wj_city` VALUES (2803, 530400, 530481, '澄江市', 3);
INSERT INTO `wj_city` VALUES (2804, 530000, 530500, '保山市', 2);
INSERT INTO `wj_city` VALUES (2805, 530500, 530502, '隆阳区', 3);
INSERT INTO `wj_city` VALUES (2806, 530500, 530521, '施甸县', 3);
INSERT INTO `wj_city` VALUES (2807, 530500, 530523, '龙陵县', 3);
INSERT INTO `wj_city` VALUES (2808, 530500, 530524, '昌宁县', 3);
INSERT INTO `wj_city` VALUES (2809, 530500, 530581, '腾冲市', 3);
INSERT INTO `wj_city` VALUES (2810, 530000, 530600, '昭通市', 2);
INSERT INTO `wj_city` VALUES (2811, 530600, 530602, '昭阳区', 3);
INSERT INTO `wj_city` VALUES (2812, 530600, 530621, '鲁甸县', 3);
INSERT INTO `wj_city` VALUES (2813, 530600, 530622, '巧家县', 3);
INSERT INTO `wj_city` VALUES (2814, 530600, 530623, '盐津县', 3);
INSERT INTO `wj_city` VALUES (2815, 530600, 530624, '大关县', 3);
INSERT INTO `wj_city` VALUES (2816, 530600, 530625, '永善县', 3);
INSERT INTO `wj_city` VALUES (2817, 530600, 530626, '绥江县', 3);
INSERT INTO `wj_city` VALUES (2818, 530600, 530627, '镇雄县', 3);
INSERT INTO `wj_city` VALUES (2819, 530600, 530628, '彝良县', 3);
INSERT INTO `wj_city` VALUES (2820, 530600, 530629, '威信县', 3);
INSERT INTO `wj_city` VALUES (2821, 530600, 530681, '水富市', 3);
INSERT INTO `wj_city` VALUES (2822, 530000, 530700, '丽江市', 2);
INSERT INTO `wj_city` VALUES (2823, 530700, 530702, '古城区', 3);
INSERT INTO `wj_city` VALUES (2824, 530700, 530721, '玉龙纳西族自治县', 3);
INSERT INTO `wj_city` VALUES (2825, 530700, 530722, '永胜县', 3);
INSERT INTO `wj_city` VALUES (2826, 530700, 530723, '华坪县', 3);
INSERT INTO `wj_city` VALUES (2827, 530700, 530724, '宁蒗彝族自治县', 3);
INSERT INTO `wj_city` VALUES (2828, 530000, 530800, '普洱市', 2);
INSERT INTO `wj_city` VALUES (2829, 530800, 530802, '思茅区', 3);
INSERT INTO `wj_city` VALUES (2830, 530800, 530821, '宁洱哈尼族彝族自治县', 3);
INSERT INTO `wj_city` VALUES (2831, 530800, 530822, '墨江哈尼族自治县', 3);
INSERT INTO `wj_city` VALUES (2832, 530800, 530823, '景东彝族自治县', 3);
INSERT INTO `wj_city` VALUES (2833, 530800, 530824, '景谷傣族彝族自治县', 3);
INSERT INTO `wj_city` VALUES (2834, 530800, 530825, '镇沅彝族哈尼族拉祜族自治县', 3);
INSERT INTO `wj_city` VALUES (2835, 530800, 530826, '江城哈尼族彝族自治县', 3);
INSERT INTO `wj_city` VALUES (2836, 530800, 530827, '孟连傣族拉祜族佤族自治县', 3);
INSERT INTO `wj_city` VALUES (2837, 530800, 530828, '澜沧拉祜族自治县', 3);
INSERT INTO `wj_city` VALUES (2838, 530800, 530829, '西盟佤族自治县', 3);
INSERT INTO `wj_city` VALUES (2839, 530000, 530900, '临沧市', 2);
INSERT INTO `wj_city` VALUES (2840, 530900, 530902, '临翔区', 3);
INSERT INTO `wj_city` VALUES (2841, 530900, 530921, '凤庆县', 3);
INSERT INTO `wj_city` VALUES (2842, 530900, 530922, '云县', 3);
INSERT INTO `wj_city` VALUES (2843, 530900, 530923, '永德县', 3);
INSERT INTO `wj_city` VALUES (2844, 530900, 530924, '镇康县', 3);
INSERT INTO `wj_city` VALUES (2845, 530900, 530925, '双江拉祜族佤族布朗族傣族自治县', 3);
INSERT INTO `wj_city` VALUES (2846, 530900, 530926, '耿马傣族佤族自治县', 3);
INSERT INTO `wj_city` VALUES (2847, 530900, 530927, '沧源佤族自治县', 3);
INSERT INTO `wj_city` VALUES (2848, 530000, 532300, '楚雄彝族自治州', 2);
INSERT INTO `wj_city` VALUES (2849, 532300, 532301, '楚雄市', 3);
INSERT INTO `wj_city` VALUES (2850, 532300, 532302, '禄丰市', 3);
INSERT INTO `wj_city` VALUES (2851, 532300, 532322, '双柏县', 3);
INSERT INTO `wj_city` VALUES (2852, 532300, 532323, '牟定县', 3);
INSERT INTO `wj_city` VALUES (2853, 532300, 532324, '南华县', 3);
INSERT INTO `wj_city` VALUES (2854, 532300, 532325, '姚安县', 3);
INSERT INTO `wj_city` VALUES (2855, 532300, 532326, '大姚县', 3);
INSERT INTO `wj_city` VALUES (2856, 532300, 532327, '永仁县', 3);
INSERT INTO `wj_city` VALUES (2857, 532300, 532328, '元谋县', 3);
INSERT INTO `wj_city` VALUES (2858, 532300, 532329, '武定县', 3);
INSERT INTO `wj_city` VALUES (2859, 530000, 532500, '红河哈尼族彝族自治州', 2);
INSERT INTO `wj_city` VALUES (2860, 532500, 532501, '个旧市', 3);
INSERT INTO `wj_city` VALUES (2861, 532500, 532502, '开远市', 3);
INSERT INTO `wj_city` VALUES (2862, 532500, 532503, '蒙自市', 3);
INSERT INTO `wj_city` VALUES (2863, 532500, 532504, '弥勒市', 3);
INSERT INTO `wj_city` VALUES (2864, 532500, 532523, '屏边苗族自治县', 3);
INSERT INTO `wj_city` VALUES (2865, 532500, 532524, '建水县', 3);
INSERT INTO `wj_city` VALUES (2866, 532500, 532525, '石屏县', 3);
INSERT INTO `wj_city` VALUES (2867, 532500, 532527, '泸西县', 3);
INSERT INTO `wj_city` VALUES (2868, 532500, 532528, '元阳县', 3);
INSERT INTO `wj_city` VALUES (2869, 532500, 532529, '红河县', 3);
INSERT INTO `wj_city` VALUES (2870, 532500, 532530, '金平苗族瑶族傣族自治县', 3);
INSERT INTO `wj_city` VALUES (2871, 532500, 532531, '绿春县', 3);
INSERT INTO `wj_city` VALUES (2872, 532500, 532532, '河口瑶族自治县', 3);
INSERT INTO `wj_city` VALUES (2873, 530000, 532600, '文山壮族苗族自治州', 2);
INSERT INTO `wj_city` VALUES (2874, 532600, 532601, '文山市', 3);
INSERT INTO `wj_city` VALUES (2875, 532600, 532622, '砚山县', 3);
INSERT INTO `wj_city` VALUES (2876, 532600, 532623, '西畴县', 3);
INSERT INTO `wj_city` VALUES (2877, 532600, 532624, '麻栗坡县', 3);
INSERT INTO `wj_city` VALUES (2878, 532600, 532625, '马关县', 3);
INSERT INTO `wj_city` VALUES (2879, 532600, 532626, '丘北县', 3);
INSERT INTO `wj_city` VALUES (2880, 532600, 532627, '广南县', 3);
INSERT INTO `wj_city` VALUES (2881, 532600, 532628, '富宁县', 3);
INSERT INTO `wj_city` VALUES (2882, 530000, 532800, '西双版纳傣族自治州', 2);
INSERT INTO `wj_city` VALUES (2883, 532800, 532801, '景洪市', 3);
INSERT INTO `wj_city` VALUES (2884, 532800, 532822, '勐海县', 3);
INSERT INTO `wj_city` VALUES (2885, 532800, 532823, '勐腊县', 3);
INSERT INTO `wj_city` VALUES (2886, 530000, 532900, '大理白族自治州', 2);
INSERT INTO `wj_city` VALUES (2887, 532900, 532901, '大理市', 3);
INSERT INTO `wj_city` VALUES (2888, 532900, 532922, '漾濞彝族自治县', 3);
INSERT INTO `wj_city` VALUES (2889, 532900, 532923, '祥云县', 3);
INSERT INTO `wj_city` VALUES (2890, 532900, 532924, '宾川县', 3);
INSERT INTO `wj_city` VALUES (2891, 532900, 532925, '弥渡县', 3);
INSERT INTO `wj_city` VALUES (2892, 532900, 532926, '南涧彝族自治县', 3);
INSERT INTO `wj_city` VALUES (2893, 532900, 532927, '巍山彝族回族自治县', 3);
INSERT INTO `wj_city` VALUES (2894, 532900, 532928, '永平县', 3);
INSERT INTO `wj_city` VALUES (2895, 532900, 532929, '云龙县', 3);
INSERT INTO `wj_city` VALUES (2896, 532900, 532930, '洱源县', 3);
INSERT INTO `wj_city` VALUES (2897, 532900, 532931, '剑川县', 3);
INSERT INTO `wj_city` VALUES (2898, 532900, 532932, '鹤庆县', 3);
INSERT INTO `wj_city` VALUES (2899, 530000, 533100, '德宏傣族景颇族自治州', 2);
INSERT INTO `wj_city` VALUES (2900, 533100, 533102, '瑞丽市', 3);
INSERT INTO `wj_city` VALUES (2901, 533100, 533103, '芒市', 3);
INSERT INTO `wj_city` VALUES (2902, 533100, 533122, '梁河县', 3);
INSERT INTO `wj_city` VALUES (2903, 533100, 533123, '盈江县', 3);
INSERT INTO `wj_city` VALUES (2904, 533100, 533124, '陇川县', 3);
INSERT INTO `wj_city` VALUES (2905, 530000, 533300, '怒江傈僳族自治州', 2);
INSERT INTO `wj_city` VALUES (2906, 533300, 533301, '泸水市', 3);
INSERT INTO `wj_city` VALUES (2907, 533300, 533323, '福贡县', 3);
INSERT INTO `wj_city` VALUES (2908, 533300, 533324, '贡山独龙族怒族自治县', 3);
INSERT INTO `wj_city` VALUES (2909, 533300, 533325, '兰坪白族普米族自治县', 3);
INSERT INTO `wj_city` VALUES (2910, 530000, 533400, '迪庆藏族自治州', 2);
INSERT INTO `wj_city` VALUES (2911, 533400, 533401, '香格里拉市', 3);
INSERT INTO `wj_city` VALUES (2912, 533400, 533422, '德钦县', 3);
INSERT INTO `wj_city` VALUES (2913, 533400, 533423, '维西傈僳族自治县', 3);
INSERT INTO `wj_city` VALUES (2914, 0, 540000, '西藏自治区', 1);
INSERT INTO `wj_city` VALUES (2915, 540000, 540100, '拉萨市', 2);
INSERT INTO `wj_city` VALUES (2916, 540100, 540102, '城关区', 3);
INSERT INTO `wj_city` VALUES (2917, 540100, 540103, '堆龙德庆区', 3);
INSERT INTO `wj_city` VALUES (2918, 540100, 540104, '达孜区', 3);
INSERT INTO `wj_city` VALUES (2919, 540100, 540121, '林周县', 3);
INSERT INTO `wj_city` VALUES (2920, 540100, 540122, '当雄县', 3);
INSERT INTO `wj_city` VALUES (2921, 540100, 540123, '尼木县', 3);
INSERT INTO `wj_city` VALUES (2922, 540100, 540124, '曲水县', 3);
INSERT INTO `wj_city` VALUES (2923, 540100, 540127, '墨竹工卡县', 3);
INSERT INTO `wj_city` VALUES (2924, 540100, 540171, '格尔木藏青工业园区', 3);
INSERT INTO `wj_city` VALUES (2925, 540100, 540172, '拉萨经济技术开发区', 3);
INSERT INTO `wj_city` VALUES (2926, 540100, 540173, '西藏文化旅游创意园区', 3);
INSERT INTO `wj_city` VALUES (2927, 540100, 540174, '达孜工业园区', 3);
INSERT INTO `wj_city` VALUES (2928, 540000, 540200, '日喀则市', 2);
INSERT INTO `wj_city` VALUES (2929, 540200, 540202, '桑珠孜区', 3);
INSERT INTO `wj_city` VALUES (2930, 540200, 540221, '南木林县', 3);
INSERT INTO `wj_city` VALUES (2931, 540200, 540222, '江孜县', 3);
INSERT INTO `wj_city` VALUES (2932, 540200, 540223, '定日县', 3);
INSERT INTO `wj_city` VALUES (2933, 540200, 540224, '萨迦县', 3);
INSERT INTO `wj_city` VALUES (2934, 540200, 540225, '拉孜县', 3);
INSERT INTO `wj_city` VALUES (2935, 540200, 540226, '昂仁县', 3);
INSERT INTO `wj_city` VALUES (2936, 540200, 540227, '谢通门县', 3);
INSERT INTO `wj_city` VALUES (2937, 540200, 540228, '白朗县', 3);
INSERT INTO `wj_city` VALUES (2938, 540200, 540229, '仁布县', 3);
INSERT INTO `wj_city` VALUES (2939, 540200, 540230, '康马县', 3);
INSERT INTO `wj_city` VALUES (2940, 540200, 540231, '定结县', 3);
INSERT INTO `wj_city` VALUES (2941, 540200, 540232, '仲巴县', 3);
INSERT INTO `wj_city` VALUES (2942, 540200, 540233, '亚东县', 3);
INSERT INTO `wj_city` VALUES (2943, 540200, 540234, '吉隆县', 3);
INSERT INTO `wj_city` VALUES (2944, 540200, 540235, '聂拉木县', 3);
INSERT INTO `wj_city` VALUES (2945, 540200, 540236, '萨嘎县', 3);
INSERT INTO `wj_city` VALUES (2946, 540200, 540237, '岗巴县', 3);
INSERT INTO `wj_city` VALUES (2947, 540000, 540300, '昌都市', 2);
INSERT INTO `wj_city` VALUES (2948, 540300, 540302, '卡若区', 3);
INSERT INTO `wj_city` VALUES (2949, 540300, 540321, '江达县', 3);
INSERT INTO `wj_city` VALUES (2950, 540300, 540322, '贡觉县', 3);
INSERT INTO `wj_city` VALUES (2951, 540300, 540323, '类乌齐县', 3);
INSERT INTO `wj_city` VALUES (2952, 540300, 540324, '丁青县', 3);
INSERT INTO `wj_city` VALUES (2953, 540300, 540325, '察雅县', 3);
INSERT INTO `wj_city` VALUES (2954, 540300, 540326, '八宿县', 3);
INSERT INTO `wj_city` VALUES (2955, 540300, 540327, '左贡县', 3);
INSERT INTO `wj_city` VALUES (2956, 540300, 540328, '芒康县', 3);
INSERT INTO `wj_city` VALUES (2957, 540300, 540329, '洛隆县', 3);
INSERT INTO `wj_city` VALUES (2958, 540300, 540330, '边坝县', 3);
INSERT INTO `wj_city` VALUES (2959, 540000, 540400, '林芝市', 2);
INSERT INTO `wj_city` VALUES (2960, 540400, 540402, '巴宜区', 3);
INSERT INTO `wj_city` VALUES (2961, 540400, 540421, '工布江达县', 3);
INSERT INTO `wj_city` VALUES (2962, 540400, 540423, '墨脱县', 3);
INSERT INTO `wj_city` VALUES (2963, 540400, 540424, '波密县', 3);
INSERT INTO `wj_city` VALUES (2964, 540400, 540425, '察隅县', 3);
INSERT INTO `wj_city` VALUES (2965, 540400, 540426, '朗县', 3);
INSERT INTO `wj_city` VALUES (2966, 540400, 540481, '米林市', 3);
INSERT INTO `wj_city` VALUES (2967, 540000, 540500, '山南市', 2);
INSERT INTO `wj_city` VALUES (2968, 540500, 540502, '乃东区', 3);
INSERT INTO `wj_city` VALUES (2969, 540500, 540521, '扎囊县', 3);
INSERT INTO `wj_city` VALUES (2970, 540500, 540522, '贡嘎县', 3);
INSERT INTO `wj_city` VALUES (2971, 540500, 540523, '桑日县', 3);
INSERT INTO `wj_city` VALUES (2972, 540500, 540524, '琼结县', 3);
INSERT INTO `wj_city` VALUES (2973, 540500, 540525, '曲松县', 3);
INSERT INTO `wj_city` VALUES (2974, 540500, 540526, '措美县', 3);
INSERT INTO `wj_city` VALUES (2975, 540500, 540527, '洛扎县', 3);
INSERT INTO `wj_city` VALUES (2976, 540500, 540528, '加查县', 3);
INSERT INTO `wj_city` VALUES (2977, 540500, 540529, '隆子县', 3);
INSERT INTO `wj_city` VALUES (2978, 540500, 540531, '浪卡子县', 3);
INSERT INTO `wj_city` VALUES (2979, 540500, 540581, '错那市', 3);
INSERT INTO `wj_city` VALUES (2980, 540000, 540600, '那曲市', 2);
INSERT INTO `wj_city` VALUES (2981, 540600, 540602, '色尼区', 3);
INSERT INTO `wj_city` VALUES (2982, 540600, 540621, '嘉黎县', 3);
INSERT INTO `wj_city` VALUES (2983, 540600, 540622, '比如县', 3);
INSERT INTO `wj_city` VALUES (2984, 540600, 540623, '聂荣县', 3);
INSERT INTO `wj_city` VALUES (2985, 540600, 540624, '安多县', 3);
INSERT INTO `wj_city` VALUES (2986, 540600, 540625, '申扎县', 3);
INSERT INTO `wj_city` VALUES (2987, 540600, 540626, '索县', 3);
INSERT INTO `wj_city` VALUES (2988, 540600, 540627, '班戈县', 3);
INSERT INTO `wj_city` VALUES (2989, 540600, 540628, '巴青县', 3);
INSERT INTO `wj_city` VALUES (2990, 540600, 540629, '尼玛县', 3);
INSERT INTO `wj_city` VALUES (2991, 540600, 540630, '双湖县', 3);
INSERT INTO `wj_city` VALUES (2992, 540000, 542500, '阿里地区', 2);
INSERT INTO `wj_city` VALUES (2993, 542500, 542521, '普兰县', 3);
INSERT INTO `wj_city` VALUES (2994, 542500, 542522, '札达县', 3);
INSERT INTO `wj_city` VALUES (2995, 542500, 542523, '噶尔县', 3);
INSERT INTO `wj_city` VALUES (2996, 542500, 542524, '日土县', 3);
INSERT INTO `wj_city` VALUES (2997, 542500, 542525, '革吉县', 3);
INSERT INTO `wj_city` VALUES (2998, 542500, 542526, '改则县', 3);
INSERT INTO `wj_city` VALUES (2999, 542500, 542527, '措勤县', 3);
INSERT INTO `wj_city` VALUES (3000, 0, 610000, '陕西省', 1);
INSERT INTO `wj_city` VALUES (3001, 610000, 610100, '西安市', 2);
INSERT INTO `wj_city` VALUES (3002, 610100, 610102, '新城区', 3);
INSERT INTO `wj_city` VALUES (3003, 610100, 610103, '碑林区', 3);
INSERT INTO `wj_city` VALUES (3004, 610100, 610104, '莲湖区', 3);
INSERT INTO `wj_city` VALUES (3005, 610100, 610111, '灞桥区', 3);
INSERT INTO `wj_city` VALUES (3006, 610100, 610112, '未央区', 3);
INSERT INTO `wj_city` VALUES (3007, 610100, 610113, '雁塔区', 3);
INSERT INTO `wj_city` VALUES (3008, 610100, 610114, '阎良区', 3);
INSERT INTO `wj_city` VALUES (3009, 610100, 610115, '临潼区', 3);
INSERT INTO `wj_city` VALUES (3010, 610100, 610116, '长安区', 3);
INSERT INTO `wj_city` VALUES (3011, 610100, 610117, '高陵区', 3);
INSERT INTO `wj_city` VALUES (3012, 610100, 610118, '鄠邑区', 3);
INSERT INTO `wj_city` VALUES (3013, 610100, 610122, '蓝田县', 3);
INSERT INTO `wj_city` VALUES (3014, 610100, 610124, '周至县', 3);
INSERT INTO `wj_city` VALUES (3015, 610000, 610200, '铜川市', 2);
INSERT INTO `wj_city` VALUES (3016, 610200, 610202, '王益区', 3);
INSERT INTO `wj_city` VALUES (3017, 610200, 610203, '印台区', 3);
INSERT INTO `wj_city` VALUES (3018, 610200, 610204, '耀州区', 3);
INSERT INTO `wj_city` VALUES (3019, 610200, 610222, '宜君县', 3);
INSERT INTO `wj_city` VALUES (3020, 610000, 610300, '宝鸡市', 2);
INSERT INTO `wj_city` VALUES (3021, 610300, 610302, '渭滨区', 3);
INSERT INTO `wj_city` VALUES (3022, 610300, 610303, '金台区', 3);
INSERT INTO `wj_city` VALUES (3023, 610300, 610304, '陈仓区', 3);
INSERT INTO `wj_city` VALUES (3024, 610300, 610305, '凤翔区', 3);
INSERT INTO `wj_city` VALUES (3025, 610300, 610323, '岐山县', 3);
INSERT INTO `wj_city` VALUES (3026, 610300, 610324, '扶风县', 3);
INSERT INTO `wj_city` VALUES (3027, 610300, 610326, '眉县', 3);
INSERT INTO `wj_city` VALUES (3028, 610300, 610327, '陇县', 3);
INSERT INTO `wj_city` VALUES (3029, 610300, 610328, '千阳县', 3);
INSERT INTO `wj_city` VALUES (3030, 610300, 610329, '麟游县', 3);
INSERT INTO `wj_city` VALUES (3031, 610300, 610330, '凤县', 3);
INSERT INTO `wj_city` VALUES (3032, 610300, 610331, '太白县', 3);
INSERT INTO `wj_city` VALUES (3033, 610000, 610400, '咸阳市', 2);
INSERT INTO `wj_city` VALUES (3034, 610400, 610402, '秦都区', 3);
INSERT INTO `wj_city` VALUES (3035, 610400, 610403, '杨陵区', 3);
INSERT INTO `wj_city` VALUES (3036, 610400, 610404, '渭城区', 3);
INSERT INTO `wj_city` VALUES (3037, 610400, 610422, '三原县', 3);
INSERT INTO `wj_city` VALUES (3038, 610400, 610423, '泾阳县', 3);
INSERT INTO `wj_city` VALUES (3039, 610400, 610424, '乾县', 3);
INSERT INTO `wj_city` VALUES (3040, 610400, 610425, '礼泉县', 3);
INSERT INTO `wj_city` VALUES (3041, 610400, 610426, '永寿县', 3);
INSERT INTO `wj_city` VALUES (3042, 610400, 610428, '长武县', 3);
INSERT INTO `wj_city` VALUES (3043, 610400, 610429, '旬邑县', 3);
INSERT INTO `wj_city` VALUES (3044, 610400, 610430, '淳化县', 3);
INSERT INTO `wj_city` VALUES (3045, 610400, 610431, '武功县', 3);
INSERT INTO `wj_city` VALUES (3046, 610400, 610481, '兴平市', 3);
INSERT INTO `wj_city` VALUES (3047, 610400, 610482, '彬州市', 3);
INSERT INTO `wj_city` VALUES (3048, 610000, 610500, '渭南市', 2);
INSERT INTO `wj_city` VALUES (3049, 610500, 610502, '临渭区', 3);
INSERT INTO `wj_city` VALUES (3050, 610500, 610503, '华州区', 3);
INSERT INTO `wj_city` VALUES (3051, 610500, 610522, '潼关县', 3);
INSERT INTO `wj_city` VALUES (3052, 610500, 610523, '大荔县', 3);
INSERT INTO `wj_city` VALUES (3053, 610500, 610524, '合阳县', 3);
INSERT INTO `wj_city` VALUES (3054, 610500, 610525, '澄城县', 3);
INSERT INTO `wj_city` VALUES (3055, 610500, 610526, '蒲城县', 3);
INSERT INTO `wj_city` VALUES (3056, 610500, 610527, '白水县', 3);
INSERT INTO `wj_city` VALUES (3057, 610500, 610528, '富平县', 3);
INSERT INTO `wj_city` VALUES (3058, 610500, 610581, '韩城市', 3);
INSERT INTO `wj_city` VALUES (3059, 610500, 610582, '华阴市', 3);
INSERT INTO `wj_city` VALUES (3060, 610000, 610600, '延安市', 2);
INSERT INTO `wj_city` VALUES (3061, 610600, 610602, '宝塔区', 3);
INSERT INTO `wj_city` VALUES (3062, 610600, 610603, '安塞区', 3);
INSERT INTO `wj_city` VALUES (3063, 610600, 610621, '延长县', 3);
INSERT INTO `wj_city` VALUES (3064, 610600, 610622, '延川县', 3);
INSERT INTO `wj_city` VALUES (3065, 610600, 610625, '志丹县', 3);
INSERT INTO `wj_city` VALUES (3066, 610600, 610626, '吴起县', 3);
INSERT INTO `wj_city` VALUES (3067, 610600, 610627, '甘泉县', 3);
INSERT INTO `wj_city` VALUES (3068, 610600, 610628, '富县', 3);
INSERT INTO `wj_city` VALUES (3069, 610600, 610629, '洛川县', 3);
INSERT INTO `wj_city` VALUES (3070, 610600, 610630, '宜川县', 3);
INSERT INTO `wj_city` VALUES (3071, 610600, 610631, '黄龙县', 3);
INSERT INTO `wj_city` VALUES (3072, 610600, 610632, '黄陵县', 3);
INSERT INTO `wj_city` VALUES (3073, 610600, 610681, '子长市', 3);
INSERT INTO `wj_city` VALUES (3074, 610000, 610700, '汉中市', 2);
INSERT INTO `wj_city` VALUES (3075, 610700, 610702, '汉台区', 3);
INSERT INTO `wj_city` VALUES (3076, 610700, 610703, '南郑区', 3);
INSERT INTO `wj_city` VALUES (3077, 610700, 610722, '城固县', 3);
INSERT INTO `wj_city` VALUES (3078, 610700, 610723, '洋县', 3);
INSERT INTO `wj_city` VALUES (3079, 610700, 610724, '西乡县', 3);
INSERT INTO `wj_city` VALUES (3080, 610700, 610725, '勉县', 3);
INSERT INTO `wj_city` VALUES (3081, 610700, 610726, '宁强县', 3);
INSERT INTO `wj_city` VALUES (3082, 610700, 610727, '略阳县', 3);
INSERT INTO `wj_city` VALUES (3083, 610700, 610728, '镇巴县', 3);
INSERT INTO `wj_city` VALUES (3084, 610700, 610729, '留坝县', 3);
INSERT INTO `wj_city` VALUES (3085, 610700, 610730, '佛坪县', 3);
INSERT INTO `wj_city` VALUES (3086, 610000, 610800, '榆林市', 2);
INSERT INTO `wj_city` VALUES (3087, 610800, 610802, '榆阳区', 3);
INSERT INTO `wj_city` VALUES (3088, 610800, 610803, '横山区', 3);
INSERT INTO `wj_city` VALUES (3089, 610800, 610822, '府谷县', 3);
INSERT INTO `wj_city` VALUES (3090, 610800, 610824, '靖边县', 3);
INSERT INTO `wj_city` VALUES (3091, 610800, 610825, '定边县', 3);
INSERT INTO `wj_city` VALUES (3092, 610800, 610826, '绥德县', 3);
INSERT INTO `wj_city` VALUES (3093, 610800, 610827, '米脂县', 3);
INSERT INTO `wj_city` VALUES (3094, 610800, 610828, '佳县', 3);
INSERT INTO `wj_city` VALUES (3095, 610800, 610829, '吴堡县', 3);
INSERT INTO `wj_city` VALUES (3096, 610800, 610830, '清涧县', 3);
INSERT INTO `wj_city` VALUES (3097, 610800, 610831, '子洲县', 3);
INSERT INTO `wj_city` VALUES (3098, 610800, 610881, '神木市', 3);
INSERT INTO `wj_city` VALUES (3099, 610000, 610900, '安康市', 2);
INSERT INTO `wj_city` VALUES (3100, 610900, 610902, '汉滨区', 3);
INSERT INTO `wj_city` VALUES (3101, 610900, 610921, '汉阴县', 3);
INSERT INTO `wj_city` VALUES (3102, 610900, 610922, '石泉县', 3);
INSERT INTO `wj_city` VALUES (3103, 610900, 610923, '宁陕县', 3);
INSERT INTO `wj_city` VALUES (3104, 610900, 610924, '紫阳县', 3);
INSERT INTO `wj_city` VALUES (3105, 610900, 610925, '岚皋县', 3);
INSERT INTO `wj_city` VALUES (3106, 610900, 610926, '平利县', 3);
INSERT INTO `wj_city` VALUES (3107, 610900, 610927, '镇坪县', 3);
INSERT INTO `wj_city` VALUES (3108, 610900, 610929, '白河县', 3);
INSERT INTO `wj_city` VALUES (3109, 610900, 610981, '旬阳市', 3);
INSERT INTO `wj_city` VALUES (3110, 610000, 611000, '商洛市', 2);
INSERT INTO `wj_city` VALUES (3111, 611000, 611002, '商州区', 3);
INSERT INTO `wj_city` VALUES (3112, 611000, 611021, '洛南县', 3);
INSERT INTO `wj_city` VALUES (3113, 611000, 611022, '丹凤县', 3);
INSERT INTO `wj_city` VALUES (3114, 611000, 611023, '商南县', 3);
INSERT INTO `wj_city` VALUES (3115, 611000, 611024, '山阳县', 3);
INSERT INTO `wj_city` VALUES (3116, 611000, 611025, '镇安县', 3);
INSERT INTO `wj_city` VALUES (3117, 611000, 611026, '柞水县', 3);
INSERT INTO `wj_city` VALUES (3118, 0, 620000, '甘肃省', 1);
INSERT INTO `wj_city` VALUES (3119, 620000, 620100, '兰州市', 2);
INSERT INTO `wj_city` VALUES (3120, 620100, 620102, '城关区', 3);
INSERT INTO `wj_city` VALUES (3121, 620100, 620103, '七里河区', 3);
INSERT INTO `wj_city` VALUES (3122, 620100, 620104, '西固区', 3);
INSERT INTO `wj_city` VALUES (3123, 620100, 620105, '安宁区', 3);
INSERT INTO `wj_city` VALUES (3124, 620100, 620111, '红古区', 3);
INSERT INTO `wj_city` VALUES (3125, 620100, 620121, '永登县', 3);
INSERT INTO `wj_city` VALUES (3126, 620100, 620122, '皋兰县', 3);
INSERT INTO `wj_city` VALUES (3127, 620100, 620123, '榆中县', 3);
INSERT INTO `wj_city` VALUES (3128, 620100, 620171, '兰州新区', 3);
INSERT INTO `wj_city` VALUES (3129, 620000, 620200, '嘉峪关市', 2);
INSERT INTO `wj_city` VALUES (3130, 620000, 620300, '金昌市', 2);
INSERT INTO `wj_city` VALUES (3131, 620300, 620302, '金川区', 3);
INSERT INTO `wj_city` VALUES (3132, 620300, 620321, '永昌县', 3);
INSERT INTO `wj_city` VALUES (3133, 620000, 620400, '白银市', 2);
INSERT INTO `wj_city` VALUES (3134, 620400, 620402, '白银区', 3);
INSERT INTO `wj_city` VALUES (3135, 620400, 620403, '平川区', 3);
INSERT INTO `wj_city` VALUES (3136, 620400, 620421, '靖远县', 3);
INSERT INTO `wj_city` VALUES (3137, 620400, 620422, '会宁县', 3);
INSERT INTO `wj_city` VALUES (3138, 620400, 620423, '景泰县', 3);
INSERT INTO `wj_city` VALUES (3139, 620000, 620500, '天水市', 2);
INSERT INTO `wj_city` VALUES (3140, 620500, 620502, '秦州区', 3);
INSERT INTO `wj_city` VALUES (3141, 620500, 620503, '麦积区', 3);
INSERT INTO `wj_city` VALUES (3142, 620500, 620521, '清水县', 3);
INSERT INTO `wj_city` VALUES (3143, 620500, 620522, '秦安县', 3);
INSERT INTO `wj_city` VALUES (3144, 620500, 620523, '甘谷县', 3);
INSERT INTO `wj_city` VALUES (3145, 620500, 620524, '武山县', 3);
INSERT INTO `wj_city` VALUES (3146, 620500, 620525, '张家川回族自治县', 3);
INSERT INTO `wj_city` VALUES (3147, 620000, 620600, '武威市', 2);
INSERT INTO `wj_city` VALUES (3148, 620600, 620602, '凉州区', 3);
INSERT INTO `wj_city` VALUES (3149, 620600, 620621, '民勤县', 3);
INSERT INTO `wj_city` VALUES (3150, 620600, 620622, '古浪县', 3);
INSERT INTO `wj_city` VALUES (3151, 620600, 620623, '天祝藏族自治县', 3);
INSERT INTO `wj_city` VALUES (3152, 620000, 620700, '张掖市', 2);
INSERT INTO `wj_city` VALUES (3153, 620700, 620702, '甘州区', 3);
INSERT INTO `wj_city` VALUES (3154, 620700, 620721, '肃南裕固族自治县', 3);
INSERT INTO `wj_city` VALUES (3155, 620700, 620722, '民乐县', 3);
INSERT INTO `wj_city` VALUES (3156, 620700, 620723, '临泽县', 3);
INSERT INTO `wj_city` VALUES (3157, 620700, 620724, '高台县', 3);
INSERT INTO `wj_city` VALUES (3158, 620700, 620725, '山丹县', 3);
INSERT INTO `wj_city` VALUES (3159, 620000, 620800, '平凉市', 2);
INSERT INTO `wj_city` VALUES (3160, 620800, 620802, '崆峒区', 3);
INSERT INTO `wj_city` VALUES (3161, 620800, 620821, '泾川县', 3);
INSERT INTO `wj_city` VALUES (3162, 620800, 620822, '灵台县', 3);
INSERT INTO `wj_city` VALUES (3163, 620800, 620823, '崇信县', 3);
INSERT INTO `wj_city` VALUES (3164, 620800, 620825, '庄浪县', 3);
INSERT INTO `wj_city` VALUES (3165, 620800, 620826, '静宁县', 3);
INSERT INTO `wj_city` VALUES (3166, 620800, 620881, '华亭市', 3);
INSERT INTO `wj_city` VALUES (3167, 620000, 620900, '酒泉市', 2);
INSERT INTO `wj_city` VALUES (3168, 620900, 620902, '肃州区', 3);
INSERT INTO `wj_city` VALUES (3169, 620900, 620921, '金塔县', 3);
INSERT INTO `wj_city` VALUES (3170, 620900, 620922, '瓜州县', 3);
INSERT INTO `wj_city` VALUES (3171, 620900, 620923, '肃北蒙古族自治县', 3);
INSERT INTO `wj_city` VALUES (3172, 620900, 620924, '阿克塞哈萨克族自治县', 3);
INSERT INTO `wj_city` VALUES (3173, 620900, 620981, '玉门市', 3);
INSERT INTO `wj_city` VALUES (3174, 620900, 620982, '敦煌市', 3);
INSERT INTO `wj_city` VALUES (3175, 620000, 621000, '庆阳市', 2);
INSERT INTO `wj_city` VALUES (3176, 621000, 621002, '西峰区', 3);
INSERT INTO `wj_city` VALUES (3177, 621000, 621021, '庆城县', 3);
INSERT INTO `wj_city` VALUES (3178, 621000, 621022, '环县', 3);
INSERT INTO `wj_city` VALUES (3179, 621000, 621023, '华池县', 3);
INSERT INTO `wj_city` VALUES (3180, 621000, 621024, '合水县', 3);
INSERT INTO `wj_city` VALUES (3181, 621000, 621025, '正宁县', 3);
INSERT INTO `wj_city` VALUES (3182, 621000, 621026, '宁县', 3);
INSERT INTO `wj_city` VALUES (3183, 621000, 621027, '镇原县', 3);
INSERT INTO `wj_city` VALUES (3184, 620000, 621100, '定西市', 2);
INSERT INTO `wj_city` VALUES (3185, 621100, 621102, '安定区', 3);
INSERT INTO `wj_city` VALUES (3186, 621100, 621121, '通渭县', 3);
INSERT INTO `wj_city` VALUES (3187, 621100, 621122, '陇西县', 3);
INSERT INTO `wj_city` VALUES (3188, 621100, 621123, '渭源县', 3);
INSERT INTO `wj_city` VALUES (3189, 621100, 621124, '临洮县', 3);
INSERT INTO `wj_city` VALUES (3190, 621100, 621125, '漳县', 3);
INSERT INTO `wj_city` VALUES (3191, 621100, 621126, '岷县', 3);
INSERT INTO `wj_city` VALUES (3192, 620000, 621200, '陇南市', 2);
INSERT INTO `wj_city` VALUES (3193, 621200, 621202, '武都区', 3);
INSERT INTO `wj_city` VALUES (3194, 621200, 621221, '成县', 3);
INSERT INTO `wj_city` VALUES (3195, 621200, 621222, '文县', 3);
INSERT INTO `wj_city` VALUES (3196, 621200, 621223, '宕昌县', 3);
INSERT INTO `wj_city` VALUES (3197, 621200, 621224, '康县', 3);
INSERT INTO `wj_city` VALUES (3198, 621200, 621225, '西和县', 3);
INSERT INTO `wj_city` VALUES (3199, 621200, 621226, '礼县', 3);
INSERT INTO `wj_city` VALUES (3200, 621200, 621227, '徽县', 3);
INSERT INTO `wj_city` VALUES (3201, 621200, 621228, '两当县', 3);
INSERT INTO `wj_city` VALUES (3202, 620000, 622900, '临夏回族自治州', 2);
INSERT INTO `wj_city` VALUES (3203, 622900, 622901, '临夏市', 3);
INSERT INTO `wj_city` VALUES (3204, 622900, 622921, '临夏县', 3);
INSERT INTO `wj_city` VALUES (3205, 622900, 622922, '康乐县', 3);
INSERT INTO `wj_city` VALUES (3206, 622900, 622923, '永靖县', 3);
INSERT INTO `wj_city` VALUES (3207, 622900, 622924, '广河县', 3);
INSERT INTO `wj_city` VALUES (3208, 622900, 622925, '和政县', 3);
INSERT INTO `wj_city` VALUES (3209, 622900, 622926, '东乡族自治县', 3);
INSERT INTO `wj_city` VALUES (3210, 622900, 622927, '积石山保安族东乡族撒拉族自治县', 3);
INSERT INTO `wj_city` VALUES (3211, 620000, 623000, '甘南藏族自治州', 2);
INSERT INTO `wj_city` VALUES (3212, 623000, 623001, '合作市', 3);
INSERT INTO `wj_city` VALUES (3213, 623000, 623021, '临潭县', 3);
INSERT INTO `wj_city` VALUES (3214, 623000, 623022, '卓尼县', 3);
INSERT INTO `wj_city` VALUES (3215, 623000, 623023, '舟曲县', 3);
INSERT INTO `wj_city` VALUES (3216, 623000, 623024, '迭部县', 3);
INSERT INTO `wj_city` VALUES (3217, 623000, 623025, '玛曲县', 3);
INSERT INTO `wj_city` VALUES (3218, 623000, 623026, '碌曲县', 3);
INSERT INTO `wj_city` VALUES (3219, 623000, 623027, '夏河县', 3);
INSERT INTO `wj_city` VALUES (3220, 0, 630000, '青海省', 1);
INSERT INTO `wj_city` VALUES (3221, 630000, 630100, '西宁市', 2);
INSERT INTO `wj_city` VALUES (3222, 630100, 630102, '城东区', 3);
INSERT INTO `wj_city` VALUES (3223, 630100, 630103, '城中区', 3);
INSERT INTO `wj_city` VALUES (3224, 630100, 630104, '城西区', 3);
INSERT INTO `wj_city` VALUES (3225, 630100, 630105, '城北区', 3);
INSERT INTO `wj_city` VALUES (3226, 630100, 630106, '湟中区', 3);
INSERT INTO `wj_city` VALUES (3227, 630100, 630121, '大通回族土族自治县', 3);
INSERT INTO `wj_city` VALUES (3228, 630100, 630123, '湟源县', 3);
INSERT INTO `wj_city` VALUES (3229, 630000, 630200, '海东市', 2);
INSERT INTO `wj_city` VALUES (3230, 630200, 630202, '乐都区', 3);
INSERT INTO `wj_city` VALUES (3231, 630200, 630203, '平安区', 3);
INSERT INTO `wj_city` VALUES (3232, 630200, 630222, '民和回族土族自治县', 3);
INSERT INTO `wj_city` VALUES (3233, 630200, 630223, '互助土族自治县', 3);
INSERT INTO `wj_city` VALUES (3234, 630200, 630224, '化隆回族自治县', 3);
INSERT INTO `wj_city` VALUES (3235, 630200, 630225, '循化撒拉族自治县', 3);
INSERT INTO `wj_city` VALUES (3236, 630000, 632200, '海北藏族自治州', 2);
INSERT INTO `wj_city` VALUES (3237, 632200, 632221, '门源回族自治县', 3);
INSERT INTO `wj_city` VALUES (3238, 632200, 632222, '祁连县', 3);
INSERT INTO `wj_city` VALUES (3239, 632200, 632223, '海晏县', 3);
INSERT INTO `wj_city` VALUES (3240, 632200, 632224, '刚察县', 3);
INSERT INTO `wj_city` VALUES (3241, 630000, 632300, '黄南藏族自治州', 2);
INSERT INTO `wj_city` VALUES (3242, 632300, 632301, '同仁市', 3);
INSERT INTO `wj_city` VALUES (3243, 632300, 632322, '尖扎县', 3);
INSERT INTO `wj_city` VALUES (3244, 632300, 632323, '泽库县', 3);
INSERT INTO `wj_city` VALUES (3245, 632300, 632324, '河南蒙古族自治县', 3);
INSERT INTO `wj_city` VALUES (3246, 630000, 632500, '海南藏族自治州', 2);
INSERT INTO `wj_city` VALUES (3247, 632500, 632521, '共和县', 3);
INSERT INTO `wj_city` VALUES (3248, 632500, 632522, '同德县', 3);
INSERT INTO `wj_city` VALUES (3249, 632500, 632523, '贵德县', 3);
INSERT INTO `wj_city` VALUES (3250, 632500, 632524, '兴海县', 3);
INSERT INTO `wj_city` VALUES (3251, 632500, 632525, '贵南县', 3);
INSERT INTO `wj_city` VALUES (3252, 630000, 632600, '果洛藏族自治州', 2);
INSERT INTO `wj_city` VALUES (3253, 632600, 632621, '玛沁县', 3);
INSERT INTO `wj_city` VALUES (3254, 632600, 632622, '班玛县', 3);
INSERT INTO `wj_city` VALUES (3255, 632600, 632623, '甘德县', 3);
INSERT INTO `wj_city` VALUES (3256, 632600, 632624, '达日县', 3);
INSERT INTO `wj_city` VALUES (3257, 632600, 632625, '久治县', 3);
INSERT INTO `wj_city` VALUES (3258, 632600, 632626, '玛多县', 3);
INSERT INTO `wj_city` VALUES (3259, 630000, 632700, '玉树藏族自治州', 2);
INSERT INTO `wj_city` VALUES (3260, 632700, 632701, '玉树市', 3);
INSERT INTO `wj_city` VALUES (3261, 632700, 632722, '杂多县', 3);
INSERT INTO `wj_city` VALUES (3262, 632700, 632723, '称多县', 3);
INSERT INTO `wj_city` VALUES (3263, 632700, 632724, '治多县', 3);
INSERT INTO `wj_city` VALUES (3264, 632700, 632725, '囊谦县', 3);
INSERT INTO `wj_city` VALUES (3265, 632700, 632726, '曲麻莱县', 3);
INSERT INTO `wj_city` VALUES (3266, 630000, 632800, '海西蒙古族藏族自治州', 2);
INSERT INTO `wj_city` VALUES (3267, 632800, 632801, '格尔木市', 3);
INSERT INTO `wj_city` VALUES (3268, 632800, 632802, '德令哈市', 3);
INSERT INTO `wj_city` VALUES (3269, 632800, 632803, '茫崖市', 3);
INSERT INTO `wj_city` VALUES (3270, 632800, 632821, '乌兰县', 3);
INSERT INTO `wj_city` VALUES (3271, 632800, 632822, '都兰县', 3);
INSERT INTO `wj_city` VALUES (3272, 632800, 632823, '天峻县', 3);
INSERT INTO `wj_city` VALUES (3273, 632800, 632857, '大柴旦行政委员会', 3);
INSERT INTO `wj_city` VALUES (3274, 0, 640000, '宁夏回族自治区', 1);
INSERT INTO `wj_city` VALUES (3275, 640000, 640100, '银川市', 2);
INSERT INTO `wj_city` VALUES (3276, 640100, 640104, '兴庆区', 3);
INSERT INTO `wj_city` VALUES (3277, 640100, 640105, '西夏区', 3);
INSERT INTO `wj_city` VALUES (3278, 640100, 640106, '金凤区', 3);
INSERT INTO `wj_city` VALUES (3279, 640100, 640121, '永宁县', 3);
INSERT INTO `wj_city` VALUES (3280, 640100, 640122, '贺兰县', 3);
INSERT INTO `wj_city` VALUES (3281, 640100, 640181, '灵武市', 3);
INSERT INTO `wj_city` VALUES (3282, 640000, 640200, '石嘴山市', 2);
INSERT INTO `wj_city` VALUES (3283, 640200, 640202, '大武口区', 3);
INSERT INTO `wj_city` VALUES (3284, 640200, 640205, '惠农区', 3);
INSERT INTO `wj_city` VALUES (3285, 640200, 640221, '平罗县', 3);
INSERT INTO `wj_city` VALUES (3286, 640000, 640300, '吴忠市', 2);
INSERT INTO `wj_city` VALUES (3287, 640300, 640302, '利通区', 3);
INSERT INTO `wj_city` VALUES (3288, 640300, 640303, '红寺堡区', 3);
INSERT INTO `wj_city` VALUES (3289, 640300, 640323, '盐池县', 3);
INSERT INTO `wj_city` VALUES (3290, 640300, 640324, '同心县', 3);
INSERT INTO `wj_city` VALUES (3291, 640300, 640381, '青铜峡市', 3);
INSERT INTO `wj_city` VALUES (3292, 640000, 640400, '固原市', 2);
INSERT INTO `wj_city` VALUES (3293, 640400, 640402, '原州区', 3);
INSERT INTO `wj_city` VALUES (3294, 640400, 640422, '西吉县', 3);
INSERT INTO `wj_city` VALUES (3295, 640400, 640423, '隆德县', 3);
INSERT INTO `wj_city` VALUES (3296, 640400, 640424, '泾源县', 3);
INSERT INTO `wj_city` VALUES (3297, 640400, 640425, '彭阳县', 3);
INSERT INTO `wj_city` VALUES (3298, 640000, 640500, '中卫市', 2);
INSERT INTO `wj_city` VALUES (3299, 640500, 640502, '沙坡头区', 3);
INSERT INTO `wj_city` VALUES (3300, 640500, 640521, '中宁县', 3);
INSERT INTO `wj_city` VALUES (3301, 640500, 640522, '海原县', 3);
INSERT INTO `wj_city` VALUES (3302, 0, 650000, '新疆维吾尔自治区', 1);
INSERT INTO `wj_city` VALUES (3303, 650000, 650100, '乌鲁木齐市', 2);
INSERT INTO `wj_city` VALUES (3304, 650100, 650102, '天山区', 3);
INSERT INTO `wj_city` VALUES (3305, 650100, 650103, '沙依巴克区', 3);
INSERT INTO `wj_city` VALUES (3306, 650100, 650104, '新市区', 3);
INSERT INTO `wj_city` VALUES (3307, 650100, 650105, '水磨沟区', 3);
INSERT INTO `wj_city` VALUES (3308, 650100, 650106, '头屯河区', 3);
INSERT INTO `wj_city` VALUES (3309, 650100, 650107, '达坂城区', 3);
INSERT INTO `wj_city` VALUES (3310, 650100, 650109, '米东区', 3);
INSERT INTO `wj_city` VALUES (3311, 650100, 650121, '乌鲁木齐县', 3);
INSERT INTO `wj_city` VALUES (3312, 650000, 650200, '克拉玛依市', 2);
INSERT INTO `wj_city` VALUES (3313, 650200, 650202, '独山子区', 3);
INSERT INTO `wj_city` VALUES (3314, 650200, 650203, '克拉玛依区', 3);
INSERT INTO `wj_city` VALUES (3315, 650200, 650204, '白碱滩区', 3);
INSERT INTO `wj_city` VALUES (3316, 650200, 650205, '乌尔禾区', 3);
INSERT INTO `wj_city` VALUES (3317, 650000, 650400, '吐鲁番市', 2);
INSERT INTO `wj_city` VALUES (3318, 650400, 650402, '高昌区', 3);
INSERT INTO `wj_city` VALUES (3319, 650400, 650421, '鄯善县', 3);
INSERT INTO `wj_city` VALUES (3320, 650400, 650422, '托克逊县', 3);
INSERT INTO `wj_city` VALUES (3321, 650000, 650500, '哈密市', 2);
INSERT INTO `wj_city` VALUES (3322, 650500, 650502, '伊州区', 3);
INSERT INTO `wj_city` VALUES (3323, 650500, 650521, '巴里坤哈萨克自治县', 3);
INSERT INTO `wj_city` VALUES (3324, 650500, 650522, '伊吾县', 3);
INSERT INTO `wj_city` VALUES (3325, 650000, 652300, '昌吉回族自治州', 2);
INSERT INTO `wj_city` VALUES (3326, 652300, 652301, '昌吉市', 3);
INSERT INTO `wj_city` VALUES (3327, 652300, 652302, '阜康市', 3);
INSERT INTO `wj_city` VALUES (3328, 652300, 652323, '呼图壁县', 3);
INSERT INTO `wj_city` VALUES (3329, 652300, 652324, '玛纳斯县', 3);
INSERT INTO `wj_city` VALUES (3330, 652300, 652325, '奇台县', 3);
INSERT INTO `wj_city` VALUES (3331, 652300, 652327, '吉木萨尔县', 3);
INSERT INTO `wj_city` VALUES (3332, 652300, 652328, '木垒哈萨克自治县', 3);
INSERT INTO `wj_city` VALUES (3333, 650000, 652700, '博尔塔拉蒙古自治州', 2);
INSERT INTO `wj_city` VALUES (3334, 652700, 652701, '博乐市', 3);
INSERT INTO `wj_city` VALUES (3335, 652700, 652702, '阿拉山口市', 3);
INSERT INTO `wj_city` VALUES (3336, 652700, 652722, '精河县', 3);
INSERT INTO `wj_city` VALUES (3337, 652700, 652723, '温泉县', 3);
INSERT INTO `wj_city` VALUES (3338, 650000, 652800, '巴音郭楞蒙古自治州', 2);
INSERT INTO `wj_city` VALUES (3339, 652800, 652801, '库尔勒市', 3);
INSERT INTO `wj_city` VALUES (3340, 652800, 652822, '轮台县', 3);
INSERT INTO `wj_city` VALUES (3341, 652800, 652823, '尉犁县', 3);
INSERT INTO `wj_city` VALUES (3342, 652800, 652824, '若羌县', 3);
INSERT INTO `wj_city` VALUES (3343, 652800, 652825, '且末县', 3);
INSERT INTO `wj_city` VALUES (3344, 652800, 652826, '焉耆回族自治县', 3);
INSERT INTO `wj_city` VALUES (3345, 652800, 652827, '和静县', 3);
INSERT INTO `wj_city` VALUES (3346, 652800, 652828, '和硕县', 3);
INSERT INTO `wj_city` VALUES (3347, 652800, 652829, '博湖县', 3);
INSERT INTO `wj_city` VALUES (3348, 650000, 652900, '阿克苏地区', 2);
INSERT INTO `wj_city` VALUES (3349, 652900, 652901, '阿克苏市', 3);
INSERT INTO `wj_city` VALUES (3350, 652900, 652902, '库车市', 3);
INSERT INTO `wj_city` VALUES (3351, 652900, 652922, '温宿县', 3);
INSERT INTO `wj_city` VALUES (3352, 652900, 652924, '沙雅县', 3);
INSERT INTO `wj_city` VALUES (3353, 652900, 652925, '新和县', 3);
INSERT INTO `wj_city` VALUES (3354, 652900, 652926, '拜城县', 3);
INSERT INTO `wj_city` VALUES (3355, 652900, 652927, '乌什县', 3);
INSERT INTO `wj_city` VALUES (3356, 652900, 652928, '阿瓦提县', 3);
INSERT INTO `wj_city` VALUES (3357, 652900, 652929, '柯坪县', 3);
INSERT INTO `wj_city` VALUES (3358, 650000, 653000, '克孜勒苏柯尔克孜自治州', 2);
INSERT INTO `wj_city` VALUES (3359, 653000, 653001, '阿图什市', 3);
INSERT INTO `wj_city` VALUES (3360, 653000, 653022, '阿克陶县', 3);
INSERT INTO `wj_city` VALUES (3361, 653000, 653023, '阿合奇县', 3);
INSERT INTO `wj_city` VALUES (3362, 653000, 653024, '乌恰县', 3);
INSERT INTO `wj_city` VALUES (3363, 650000, 653100, '喀什地区', 2);
INSERT INTO `wj_city` VALUES (3364, 653100, 653101, '喀什市', 3);
INSERT INTO `wj_city` VALUES (3365, 653100, 653121, '疏附县', 3);
INSERT INTO `wj_city` VALUES (3366, 653100, 653122, '疏勒县', 3);
INSERT INTO `wj_city` VALUES (3367, 653100, 653123, '英吉沙县', 3);
INSERT INTO `wj_city` VALUES (3368, 653100, 653124, '泽普县', 3);
INSERT INTO `wj_city` VALUES (3369, 653100, 653125, '莎车县', 3);
INSERT INTO `wj_city` VALUES (3370, 653100, 653126, '叶城县', 3);
INSERT INTO `wj_city` VALUES (3371, 653100, 653127, '麦盖提县', 3);
INSERT INTO `wj_city` VALUES (3372, 653100, 653128, '岳普湖县', 3);
INSERT INTO `wj_city` VALUES (3373, 653100, 653129, '伽师县', 3);
INSERT INTO `wj_city` VALUES (3374, 653100, 653130, '巴楚县', 3);
INSERT INTO `wj_city` VALUES (3375, 653100, 653131, '塔什库尔干塔吉克自治县', 3);
INSERT INTO `wj_city` VALUES (3376, 650000, 653200, '和田地区', 2);
INSERT INTO `wj_city` VALUES (3377, 653200, 653201, '和田市', 3);
INSERT INTO `wj_city` VALUES (3378, 653200, 653221, '和田县', 3);
INSERT INTO `wj_city` VALUES (3379, 653200, 653222, '墨玉县', 3);
INSERT INTO `wj_city` VALUES (3380, 653200, 653223, '皮山县', 3);
INSERT INTO `wj_city` VALUES (3381, 653200, 653224, '洛浦县', 3);
INSERT INTO `wj_city` VALUES (3382, 653200, 653225, '策勒县', 3);
INSERT INTO `wj_city` VALUES (3383, 653200, 653226, '于田县', 3);
INSERT INTO `wj_city` VALUES (3384, 653200, 653227, '民丰县', 3);
INSERT INTO `wj_city` VALUES (3385, 650000, 654000, '伊犁哈萨克自治州', 2);
INSERT INTO `wj_city` VALUES (3386, 654000, 654002, '伊宁市', 3);
INSERT INTO `wj_city` VALUES (3387, 654000, 654003, '奎屯市', 3);
INSERT INTO `wj_city` VALUES (3388, 654000, 654004, '霍尔果斯市', 3);
INSERT INTO `wj_city` VALUES (3389, 654000, 654021, '伊宁县', 3);
INSERT INTO `wj_city` VALUES (3390, 654000, 654022, '察布查尔锡伯自治县', 3);
INSERT INTO `wj_city` VALUES (3391, 654000, 654023, '霍城县', 3);
INSERT INTO `wj_city` VALUES (3392, 654000, 654024, '巩留县', 3);
INSERT INTO `wj_city` VALUES (3393, 654000, 654025, '新源县', 3);
INSERT INTO `wj_city` VALUES (3394, 654000, 654026, '昭苏县', 3);
INSERT INTO `wj_city` VALUES (3395, 654000, 654027, '特克斯县', 3);
INSERT INTO `wj_city` VALUES (3396, 654000, 654028, '尼勒克县', 3);
INSERT INTO `wj_city` VALUES (3397, 650000, 654200, '塔城地区', 2);
INSERT INTO `wj_city` VALUES (3398, 654200, 654201, '塔城市', 3);
INSERT INTO `wj_city` VALUES (3399, 654200, 654202, '乌苏市', 3);
INSERT INTO `wj_city` VALUES (3400, 654200, 654203, '沙湾市', 3);
INSERT INTO `wj_city` VALUES (3401, 654200, 654221, '额敏县', 3);
INSERT INTO `wj_city` VALUES (3402, 654200, 654224, '托里县', 3);
INSERT INTO `wj_city` VALUES (3403, 654200, 654225, '裕民县', 3);
INSERT INTO `wj_city` VALUES (3404, 654200, 654226, '和布克赛尔蒙古自治县', 3);
INSERT INTO `wj_city` VALUES (3405, 650000, 654300, '阿勒泰地区', 2);
INSERT INTO `wj_city` VALUES (3406, 654300, 654301, '阿勒泰市', 3);
INSERT INTO `wj_city` VALUES (3407, 654300, 654321, '布尔津县', 3);
INSERT INTO `wj_city` VALUES (3408, 654300, 654322, '富蕴县', 3);
INSERT INTO `wj_city` VALUES (3409, 654300, 654323, '福海县', 3);
INSERT INTO `wj_city` VALUES (3410, 654300, 654324, '哈巴河县', 3);
INSERT INTO `wj_city` VALUES (3411, 654300, 654325, '青河县', 3);
INSERT INTO `wj_city` VALUES (3412, 654300, 654326, '吉木乃县', 3);
INSERT INTO `wj_city` VALUES (3413, 650000, 659000, '自治区直辖县级行政区划', 2);
INSERT INTO `wj_city` VALUES (3414, 659000, 659001, '石河子市', 3);
INSERT INTO `wj_city` VALUES (3415, 659000, 659002, '阿拉尔市', 3);
INSERT INTO `wj_city` VALUES (3416, 659000, 659003, '图木舒克市', 3);
INSERT INTO `wj_city` VALUES (3417, 659000, 659004, '五家渠市', 3);
INSERT INTO `wj_city` VALUES (3418, 659000, 659005, '北屯市', 3);
INSERT INTO `wj_city` VALUES (3419, 659000, 659006, '铁门关市', 3);
INSERT INTO `wj_city` VALUES (3420, 659000, 659007, '双河市', 3);
INSERT INTO `wj_city` VALUES (3421, 659000, 659008, '可克达拉市', 3);
INSERT INTO `wj_city` VALUES (3422, 659000, 659009, '昆玉市', 3);
INSERT INTO `wj_city` VALUES (3423, 659000, 659010, '胡杨河市', 3);
INSERT INTO `wj_city` VALUES (3424, 659000, 659011, '新星市', 3);
INSERT INTO `wj_city` VALUES (3425, 659000, 659012, '白杨市', 3);
INSERT INTO `wj_city` VALUES (3426, 0, 710000, '台湾省', 1);
INSERT INTO `wj_city` VALUES (3469, 0, 810000, '香港特别行政区', 1);
INSERT INTO `wj_city` VALUES (3488, 0, 820000, '澳门特别行政区', 1);


DROP TABLE IF EXISTS `wj_company`;
CREATE TABLE `wj_company`
(
    `id`           bigint NOT NULL AUTO_INCREMENT,
    `company_uuid` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '对外唯一值',
    `company_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '企业名称',
    `status`       smallint                                                      DEFAULT NULL COMMENT '状态',
    `create_by`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '创建人',
    `create_time`  datetime                                                      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '更新人',
    `update_time`  datetime                                                      DEFAULT NULL COMMENT '更新时间',
    `industry`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '行业',
    `source`       varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '来源',
    `use_count`    int                                                           DEFAULT '0' COMMENT '使用人数',
    `pull_config`  varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '钉钉配置',
    `secret_id`    varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '钉钉corpId',
    `data_source`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '系统组织架构数据来源',
    `company_type` tinyint(1) DEFAULT '1' COMMENT '1普通公司 2代理商',
    `parent_id`    bigint                                                        DEFAULT '0' COMMENT '父级id',
    `main_id`      bigint                                                        DEFAULT NULL COMMENT '上层公司id',
    `channel_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT 'SYSTEM' COMMENT '渠道类型',
    PRIMARY KEY (`id`) USING BTREE,
    KEY            `company_uuid` (`company_uuid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='企业表';

DROP TABLE IF EXISTS `wj_company_app`;
CREATE TABLE `wj_company_app`
(
    `id`          bigint NOT NULL AUTO_INCREMENT,
    `org_id`      bigint NOT NULL COMMENT '机构id',
    `client_id`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'app代码id',
    `start_time`  datetime                                                     DEFAULT NULL COMMENT '开始时间',
    `end_time`    datetime                                                     DEFAULT NULL COMMENT '结束时间',
    `app_version` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '应用版本标记',
    `equity_id`   varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '权益id',
    `buy_out` tinyint(1) DEFAULT '0',
    PRIMARY KEY (`id`) USING BTREE,
    KEY           `idx_org_id` (`org_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='企业使用产品限制';

DROP TABLE IF EXISTS `wj_company_info`;
CREATE TABLE `wj_company_info`
(
    `id`           bigint NOT NULL AUTO_INCREMENT,
    `company_id`   bigint                                                        DEFAULT NULL,
    `config_key`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '配置key',
    `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '配置的值',
    `create_by`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '创建人',
    `create_time`  datetime                                                      DEFAULT NULL COMMENT '创建时间',
    `update_by`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '更新人',
    `update_time`  datetime                                                      DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS `wj_company_relation`;
CREATE TABLE `wj_company_relation`
(
    `id`              bigint NOT NULL AUTO_INCREMENT,
    `company_id`      bigint                                                       DEFAULT NULL COMMENT '公司id',
    `related_company` bigint                                                       DEFAULT NULL COMMENT '关联公司',
    `related_type`    varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
    `user_id`         bigint                                                       DEFAULT NULL COMMENT '关联人',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS `wj_main_company`;
CREATE TABLE `wj_main_company`
(
    `id`           bigint NOT NULL AUTO_INCREMENT COMMENT '公司id',
    `company_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '公司',
    `create_time`  datetime                                                      DEFAULT NULL COMMENT '创建时间',
    `update_time`  datetime                                                      DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

DROP TABLE IF EXISTS `wj_item`;
CREATE TABLE `wj_item`
(
    `id`               bigint                                                       NOT NULL AUTO_INCREMENT,
    `item_code`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品编号',
    `item_name`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '商品名称',
    `item_config`      varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品配置',
    `price`            decimal(12, 2)                                                 DEFAULT '0.00' COMMENT '价格',
    `threshold_amount` decimal(12, 2)                                                 DEFAULT NULL COMMENT '最低价，购买最低总价',
    `status`           smallint                                                       DEFAULT NULL COMMENT '商品状态',
    `create_time`      datetime                                                       DEFAULT NULL COMMENT '创建时间',
    `update_time`      datetime                                                       DEFAULT NULL COMMENT '修改时间',
    `source`           varchar(255) COLLATE utf8mb4_general_ci                        DEFAULT NULL COMMENT '来源',
    `product_name`     varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '产品',
    `version`          varchar(255) COLLATE utf8mb4_general_ci                        DEFAULT NULL COMMENT '版本',
    `max_factor` decimal(10,2) DEFAULT NULL COMMENT '最大系数',
    `min_factor` decimal(10,2) DEFAULT NULL COMMENT '最小系数',
    `user_count` int DEFAULT NULL COMMENT '最少人数',
    `step_length` decimal(11,2) DEFAULT NULL,
    `description` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '描述',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                `idx_item_code` (`item_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='五极产品';

DROP TABLE IF EXISTS `wj_order_item`;
CREATE TABLE `wj_order_item`
(
    `id`          bigint                                                       NOT NULL AUTO_INCREMENT,
    `order_id`    bigint                                                       NOT NULL COMMENT ' 订单id',
    `company_id`  bigint                                                       NOT NULL COMMENT ' 企业id',
    `user_id`     bigint                                                         DEFAULT NULL COMMENT ' 用户id',
    `item_id`     bigint                                                       NOT NULL COMMENT ' 商品id',
    `item_code`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品编号',
    `item_name`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '商品名称',
    `item_config` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '商品配置',
    `item_num`    int                                                            DEFAULT '1' COMMENT '购买数量',
    `item_price`  decimal(12, 2)                                                 DEFAULT NULL COMMENT '商品单价',
    `amount`      decimal(12, 2)                                                 DEFAULT '0.00' COMMENT '金额',
    `create_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT '创建人',
    `create_time` datetime                                                       DEFAULT NULL COMMENT '创建时间',
    `update_by`   varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci   DEFAULT NULL COMMENT '修改人',
    `update_time` datetime                                                       DEFAULT NULL COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY           `idx_order_id` (`order_id`) USING BTREE,
    KEY           `idx_company_item` (`company_id`,`item_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单商品';

DROP TABLE IF EXISTS `wj_orders`;
CREATE TABLE `wj_orders`
(
    `id`             bigint                                                       NOT NULL AUTO_INCREMENT,
    `order_no`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT ' 订单编号',
    `user_id`        bigint                                                                DEFAULT NULL COMMENT '用户id',
    `company_id`     bigint                                                       NOT NULL DEFAULT '0' COMMENT '企业id',
    `screen_uuid`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT '大屏uuid',
    `screen_name`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         DEFAULT NULL COMMENT '大屏名称',
    `index_image`    varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         DEFAULT NULL COMMENT '大屏封面图',
    `download_url`   varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         DEFAULT NULL COMMENT '下载地址',
    `amount`         decimal(12, 2)                                                        DEFAULT '0.00' COMMENT '金额',
    `original_price` decimal(12, 2)                                                        DEFAULT '0.00' COMMENT '原价',
    `transaction_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci         DEFAULT NULL COMMENT '交易号',
    `status`         smallint                                                              DEFAULT NULL COMMENT '订单状态,0:未支付,1:支付中,2:已支付,-1:取消订单',
    `create_by`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT '创建人',
    `create_time`    datetime                                                              DEFAULT NULL COMMENT '创建时间',
    `update_by`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci          DEFAULT NULL COMMENT '修改人',
    `update_time`    datetime                                                              DEFAULT NULL COMMENT '修改时间',
    `order_period`   int                                                                   DEFAULT '365' COMMENT '天数',
    `sync`           int                                                                   DEFAULT '0',
    `order_type` int DEFAULT '1' COMMENT '1购买 2升级',
    `order_count` int DEFAULT '1',
    `buy_out` tinyint(1) DEFAULT '0',
    PRIMARY KEY (`id`) USING BTREE,
    KEY              `order_no` (`order_no`) USING BTREE,
    KEY              `user_id` (`user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='支付订单表';

CREATE DATABASE IF NOT EXISTS `wuji_cloud` CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;
USE `wuji_cloud`;

/*
 Navicat Premium Data Transfer

 Source Server         : 低代码平台sql数据库
 Source Server Type    : MySQL
 Source Server Version : 50736
 Source Host           : 192.168.1.187:3306
 Source Schema         : lowcode

 Target Server Type    : MySQL
 Target Server Version : 50736
 File Encoding         : 65001

 Date: 02/07/2026 17:39:48
*/

SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for lc_across_app
-- ----------------------------
DROP TABLE IF EXISTS `lc_across_app`;
CREATE TABLE `lc_across_app`
(
    `id`             bigint(24) NOT NULL AUTO_INCREMENT,
    `company_id`     bigint(20) DEFAULT NULL,
    `application_id` varchar(24) COLLATE utf8mb4_bin DEFAULT NULL,
    `form_id`        varchar(24) COLLATE utf8mb4_bin DEFAULT NULL,
    `create_time`    datetime                        DEFAULT CURRENT_TIMESTAMP,
    `creator_name`   varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人名字',
    `modify_time`    datetime                        DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier_name`  varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人名字',
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    `config_app_id`  varchar(24) COLLATE utf8mb4_bin DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_application
-- ----------------------------
DROP TABLE IF EXISTS `lc_application`;
CREATE TABLE `lc_application`
(
    `id`                 varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `company_id`         bigint(20) NOT NULL COMMENT '公司id',
    `application_name`   varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '应用名称',
    `description`        varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '描述',
    `visit_url`          varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '访问地址',
    `state`              varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '状态',
    `create_time`        datetime                         DEFAULT CURRENT_TIMESTAMP,
    `creator`            varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '创建人',
    `modify_time`        datetime                         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`           varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '修改人',
    `deleted`            tinyint(1) DEFAULT '0' COMMENT '删除',
    `application_type`   varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '应用类型',
    `icon`               varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '图标',
    `template_id`        varchar(24) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '模板id',
    `application_nature` varchar(8) COLLATE utf8mb4_bin   DEFAULT 'NORMAL' COMMENT '应用性质',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                  `company_id_idx` (`company_id`),
    KEY                  `creator_idx` (`creator`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='应用';

-- ----------------------------
-- Table structure for lc_application_category
-- ----------------------------
DROP TABLE IF EXISTS `lc_application_category`;
CREATE TABLE `lc_application_category`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `application_id` varchar(24) COLLATE utf8mb4_bin NOT NULL COMMENT '应用id',
    `company_id`     bigint(20) DEFAULT NULL COMMENT '公司id',
    `parent_id`      varchar(24) COLLATE utf8mb4_bin  DEFAULT NULL,
    `source_id`      varchar(24) COLLATE utf8mb4_bin  DEFAULT NULL,
    `category_name`  varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '目录名称',
    `category_type`  varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '类目名称',
    `create_time`    datetime                         DEFAULT CURRENT_TIMESTAMP,
    `show_type`      varchar(16) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '显示类型',
    `creator`        varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '创建人',
    `modify_time`    datetime                         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`       varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '修改人',
    `deleted`        tinyint(4) DEFAULT '0' COMMENT '删除',
    `published`      tinyint(1) DEFAULT NULL COMMENT '是否发布',
    `icon`           varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'icon',
    `sort_num`       int(10) DEFAULT '0' COMMENT '排序值',
    PRIMARY KEY (`id`, `application_id`) USING BTREE,
    KEY              `parent_id_idx` (`parent_id`),
    KEY              `source_id_idx` (`source_id`),
    KEY              `creator_idx` (`creator`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='应用目录';

-- ----------------------------
-- Table structure for lc_application_info
-- ----------------------------
DROP TABLE IF EXISTS `lc_application_info`;
CREATE TABLE `lc_application_info`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `application_id` varchar(24) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '应用id',
    `info_key`       varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'key',
    `info_value`     varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'value',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_application_privilege
-- ----------------------------
DROP TABLE IF EXISTS `lc_application_privilege`;
CREATE TABLE `lc_application_privilege`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `business_type`  varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '权限业务类型：用户，部门',
    `business_id`    varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '权限业务id',
    `create_time`    datetime                         DEFAULT CURRENT_TIMESTAMP,
    `creator_name`   varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '创建人名字',
    `application_id` varchar(24) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '应用id',
    `privilege_type` varchar(16) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '权限类型',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_application_share
-- ----------------------------
DROP TABLE IF EXISTS `lc_application_share`;
CREATE TABLE `lc_application_share`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `application_id` varchar(24) COLLATE utf8mb4_bin DEFAULT NULL,
    `expire_time`    datetime                        DEFAULT NULL,
    `create_time`    datetime                        DEFAULT CURRENT_TIMESTAMP,
    `creator_name`   varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人名字',
    `expire_day`     int(11) DEFAULT NULL,
    `need_data`      tinyint(1) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_application_use_time
-- ----------------------------
DROP TABLE IF EXISTS `lc_application_use_time`;
CREATE TABLE `lc_application_use_time`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `application_id` varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用id',
    `user_id`        varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户id',
    `use_time`       datetime                        DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '使用时间',
    `company_id`     bigint(20) DEFAULT NULL COMMENT '公司id',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_application_user_sort
-- ----------------------------
DROP TABLE IF EXISTS `lc_application_user_sort`;
CREATE TABLE `lc_application_user_sort`
(
    `id`             bigint(20) DEFAULT NULL,
    `user_id`        varchar(32) COLLATE utf8mb4_bin DEFAULT NULL,
    `company_id`     bigint(20) DEFAULT NULL,
    `application_id` varchar(24) COLLATE utf8mb4_bin DEFAULT NULL,
    `sort`           int(2) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_city
-- ----------------------------
DROP TABLE IF EXISTS `lc_city`;
CREATE TABLE `lc_city`
(
    `id`        int(11) NOT NULL AUTO_INCREMENT,
    `pid`       int(11) DEFAULT NULL,
    `code`      int(11) DEFAULT NULL COMMENT '编号',
    `city_name` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL,
    `type`      int(11) DEFAULT '0' COMMENT '等级',
    PRIMARY KEY (`id`) USING BTREE,
    KEY         `pid_index` (`pid`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_company_plugin
-- ----------------------------
DROP TABLE IF EXISTS `lc_company_plugin`;
CREATE TABLE `lc_company_plugin`
(
    `id`                  varchar(24) CHARACTER SET armscii8 NOT NULL,
    `company_id`          bigint(20) NOT NULL,
    `plugin_config`       longtext COLLATE utf8mb4_bin,
    `plugin_type`         varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL,
    `plugin_name`         varchar(256) COLLATE utf8mb4_bin DEFAULT NULL,
    `create_time`         datetime                         DEFAULT CURRENT_TIMESTAMP,
    `creator`             varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '创建人',
    `modify_time`         datetime                         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`            varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '修改人',
    `plugin_param`        longtext COLLATE utf8mb4_bin COMMENT '插件参数',
    `deleted`             tinyint(1) DEFAULT '0' COMMENT '删除',
    `plugin_return`       longtext COLLATE utf8mb4_bin COMMENT '插件返回参数',
    `function_type`       varchar(16) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '插件事件',
    `plugin_extra_config` longtext COLLATE utf8mb4_bin COMMENT '额外配置',
    PRIMARY KEY (`id`, `company_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_flowable_activity_config
-- ----------------------------
DROP TABLE IF EXISTS `lc_flowable_activity_config`;
CREATE TABLE `lc_flowable_activity_config`
(
    `id`               bigint(20) NOT NULL AUTO_INCREMENT,
    `activity_pid`     varchar(64) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '节点父级id',
    `condition_config` text COLLATE utf8mb4_bin COMMENT '节点条件配置',
    `model_id`         varchar(36) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '模型id',
    `activity_name`    varchar(64) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '审批节点名称',
    `activity_id`      varchar(64) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '任务id',
    `activity_type`    varchar(64) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '节点类型',
    `assignee_config`  longtext COLLATE utf8mb4_bin COMMENT '审批人配置',
    `field_config`     longtext COLLATE utf8mb4_bin COMMENT '字段配置',
    `copy_config`      longtext COLLATE utf8mb4_bin COMMENT '抄送配置',
    `create_time`      datetime                          DEFAULT CURRENT_TIMESTAMP,
    `creator_name`     varchar(64) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '创建人名字',
    `modify_time`      datetime                          DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier_name`    varchar(64) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '修改人名字',
    `deleted`          tinyint(1) DEFAULT '0' COMMENT '删除',
    `sub_flow_config`  text COLLATE utf8mb4_bin COMMENT '子流程配置',
    `audit_type`       varchar(32) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '审核类型',
    `remind_config`    text COLLATE utf8mb4_bin,
    `button_config`    text COLLATE utf8mb4_bin COMMENT '按钮配置',
    `other_config`     varchar(1024) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '其他配置',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_flowable_config
-- ----------------------------
DROP TABLE IF EXISTS `lc_flowable_config`;
CREATE TABLE `lc_flowable_config`
(
    `id`       bigint(20) NOT NULL AUTO_INCREMENT,
    `model_id` varchar(36) COLLATE utf8mb4_bin DEFAULT NULL,
    `config`   longtext COLLATE utf8mb4_bin,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_flowable_copy
-- ----------------------------
DROP TABLE IF EXISTS `lc_flowable_copy`;
CREATE TABLE `lc_flowable_copy`
(
    `id`                      bigint(20) NOT NULL AUTO_INCREMENT,
    `process_instance_id`     varchar(36) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '流程实例id',
    `task_id`                 varchar(36) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '任务id',
    `task_name`               varchar(255) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '任务名称',
    `initiator`               varchar(64) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '发起人',
    `form_id`                 varchar(24) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '表单id',
    `business_type`           varchar(64) CHARACTER SET utf8mb4 DEFAULT '' COMMENT '流程业务key',
    `model_id`                varchar(36) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '模型id',
    `create_time`             datetime                          DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `process_definition_id`   varchar(128) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '流程编号',
    `process_definition_name` varchar(64) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '流程名称',
    `activity_id`             varchar(128) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '任务节点id',
    `application_id`          varchar(24) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '应用id',
    `data_uuid`               varchar(36) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '数据id',
    `company_id`              bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='抄送表';

-- ----------------------------
-- Table structure for lc_flowable_copy_user
-- ----------------------------
DROP TABLE IF EXISTS `lc_flowable_copy_user`;
CREATE TABLE `lc_flowable_copy_user`
(
    `id`        bigint(20) NOT NULL AUTO_INCREMENT,
    `copy_id`   bigint(20) DEFAULT NULL COMMENT '抄送id',
    `user_id`   bigint(20) DEFAULT NULL COMMENT '用户id',
    `user_view` tinyint(1) DEFAULT '0' COMMENT '用户查看',
    `view_time` datetime DEFAULT NULL COMMENT '用户查看时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='抄送对象表';

-- ----------------------------
-- Table structure for lc_flowable_operate_log
-- ----------------------------
DROP TABLE IF EXISTS `lc_flowable_operate_log`;
CREATE TABLE `lc_flowable_operate_log`
(
    `id`                  bigint(20) NOT NULL AUTO_INCREMENT,
    `operate`             varchar(255) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '操作类型',
    `task_key`            varchar(255) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '节点key',
    `task_id`             varchar(64) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '任务id',
    `task_name`           varchar(64) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '任务名称',
    `process_instance_id` varchar(64) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '流程实例id',
    `create_time`         datetime                          DEFAULT CURRENT_TIMESTAMP,
    `creator`             varchar(36) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '创建人',
    `comment`             varchar(1024) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '评论',
    `task_create_time`    datetime                          DEFAULT NULL COMMENT '任务创建时间',
    `duration`            varchar(64) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '用时',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_form
-- ----------------------------
DROP TABLE IF EXISTS `lc_form`;
CREATE TABLE `lc_form`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL COMMENT 'id即categoryid',
    `company_id`     bigint(20) DEFAULT NULL COMMENT '公司id',
    `source_id`      varchar(24) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '来源id',
    `config`         longtext COLLATE utf8mb4_bin COMMENT '配置',
    `table_name`     varchar(64) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '对应表名',
    `create_time`    datetime                                 DEFAULT CURRENT_TIMESTAMP,
    `creator`        varchar(36) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '创建人',
    `modify_time`    datetime                                 DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`       varchar(36) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '修改人',
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    `version`        int(11) DEFAULT NULL COMMENT '版本',
    `form_type`      varchar(32) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '表单类型',
    `application_id` varchar(32) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '应用id',
    `form_config`    text COLLATE utf8mb4_bin COMMENT '表单列表配置',
    PRIMARY KEY (`id`, `application_id`) USING BTREE,
    KEY              `source_id_idx` (`source_id`),
    KEY              `creator_idx` (`creator`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='表单';

-- ----------------------------
-- Table structure for lc_form_aggregate
-- ----------------------------
DROP TABLE IF EXISTS `lc_form_aggregate`;
CREATE TABLE `lc_form_aggregate`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `application_id` varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `name`           varchar(64) COLLATE utf8mb4_bin DEFAULT NULL,
    `config`         longtext COLLATE utf8mb4_bin,
    `create_time`    datetime                        DEFAULT CURRENT_TIMESTAMP,
    `creator`        varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
    `modify_time`    datetime                        DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`       varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    PRIMARY KEY (`id`, `application_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='聚合表';

-- ----------------------------
-- Table structure for lc_form_data_factory
-- ----------------------------
DROP TABLE IF EXISTS `lc_form_data_factory`;
CREATE TABLE `lc_form_data_factory`
(
    `id`                varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `application_id`    varchar(32) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '应用id',
    `factory_type`      varchar(32) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '数据工厂类型',
    `factory_config`    longtext COLLATE utf8mb4_bin COMMENT '数据工厂配置',
    `table_name`        varchar(64) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '对应表名',
    `create_time`       datetime                                 DEFAULT CURRENT_TIMESTAMP,
    `creator`           varchar(36) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '创建人',
    `modify_time`       datetime                                 DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`          varchar(36) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '修改人',
    `deleted`           tinyint(1) DEFAULT '0' COMMENT '删除',
    `factory_name`      varchar(64) COLLATE utf8mb4_bin          DEFAULT NULL,
    `sync_config`       longtext COLLATE utf8mb4_bin,
    `status`            varchar(16) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '状态',
    `version`           int(10) DEFAULT '0' COMMENT '版本号',
    `sync_config_error` tinyint(1) DEFAULT '0',
    `sync_form`         tinyint(1) DEFAULT '0',
    PRIMARY KEY (`id`, `application_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_form_data_factory_input
-- ----------------------------
DROP TABLE IF EXISTS `lc_form_data_factory_input`;
CREATE TABLE `lc_form_data_factory_input`
(
    `id`                   bigint(20) NOT NULL AUTO_INCREMENT,
    `data_factory_id`      varchar(24) DEFAULT NULL,
    `application_id`       varchar(24) DEFAULT NULL,
    `input_form_id`        varchar(24) DEFAULT NULL,
    `input_application_id` varchar(24) DEFAULT NULL,
    `version`              int(2) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=armscii8;

-- ----------------------------
-- Table structure for lc_form_data_factory_publish
-- ----------------------------
DROP TABLE IF EXISTS `lc_form_data_factory_publish`;
CREATE TABLE `lc_form_data_factory_publish`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `application_id` varchar(32) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '应用id',
    `factory_type`   varchar(32) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '数据工厂类型',
    `factory_config` longtext COLLATE utf8mb4_bin COMMENT '数据工厂配置',
    `table_name`     varchar(64) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '对应表名',
    `create_time`    datetime                                 DEFAULT CURRENT_TIMESTAMP,
    `creator`        varchar(36) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '创建人',
    `modify_time`    datetime                                 DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`       varchar(36) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '修改人',
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    `factory_name`   varchar(64) COLLATE utf8mb4_bin          DEFAULT NULL,
    `sync_config`    longtext COLLATE utf8mb4_bin,
    `version`        int(10) NOT NULL DEFAULT '1',
    `last_version`   tinyint(1) DEFAULT NULL,
    PRIMARY KEY (`id`, `application_id`, `version`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_form_data_log
-- ----------------------------
DROP TABLE IF EXISTS `lc_form_data_log`;
CREATE TABLE `lc_form_data_log`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL COMMENT 'id',
    `application_id` varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用id',
    `form_id`        varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '表单id',
    `record_id`      varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录行id',
    `log_action`     varchar(8) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '创建,修改',
    `log_content`    text COLLATE utf8mb4_bin COMMENT '记录详情',
    `creator`        varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
    `create_time`    datetime                        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    KEY              `form_id_idx` (`form_id`),
    KEY              `record_id_idx` (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='表单数据日志';

-- ----------------------------
-- Table structure for lc_form_data_stream
-- ----------------------------
DROP TABLE IF EXISTS `lc_form_data_stream`;
CREATE TABLE `lc_form_data_stream`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `name`           varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '名字',
    `config_type`    varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '配置类型',
    `config`         longtext COLLATE utf8mb4_bin COMMENT '配置',
    `application_id` varchar(24) COLLATE utf8mb4_bin NOT NULL COMMENT '应用id',
    `form_id`        varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '表单id',
    `canvas_config`  longtext COLLATE utf8mb4_bin COMMENT '画布配置',
    `create_time`    datetime                        DEFAULT CURRENT_TIMESTAMP,
    `creator`        varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
    `modify_time`    datetime                        DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`       varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    `state`          varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '状态',
    `version`        int(10) DEFAULT '1',
    `enable`         int(1) DEFAULT '1',
    PRIMARY KEY (`id`, `application_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_form_data_stream_publish
-- ----------------------------
DROP TABLE IF EXISTS `lc_form_data_stream_publish`;
CREATE TABLE `lc_form_data_stream_publish`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `name`           varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '名字',
    `config_type`    varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '配置类型',
    `config`         longtext COLLATE utf8mb4_bin COMMENT '配置',
    `application_id` varchar(24) COLLATE utf8mb4_bin NOT NULL COMMENT '应用id',
    `form_id`        varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '表单id',
    `canvas_config`  longtext COLLATE utf8mb4_bin COMMENT '画布配置',
    `create_time`    datetime                        DEFAULT CURRENT_TIMESTAMP,
    `creator`        varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
    `modify_time`    datetime                        DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`       varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    `state`          varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '状态',
    `version`        int(10) NOT NULL DEFAULT '1',
    `last_version`   int(1) DEFAULT NULL COMMENT '是否最后一个版本',
    `enable`         int(1) DEFAULT '1',
    PRIMARY KEY (`id`, `application_id`, `version`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_form_extra_function
-- ----------------------------
DROP TABLE IF EXISTS `lc_form_extra_function`;
CREATE TABLE `lc_form_extra_function`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `application_id` varchar(24) COLLATE utf8mb4_bin DEFAULT NULL,
    `form_id`        varchar(24) COLLATE utf8mb4_bin DEFAULT NULL,
    `config`         longtext COLLATE utf8mb4_bin,
    `create_time`    datetime                        DEFAULT CURRENT_TIMESTAMP,
    `creator`        varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
    `modify_time`    datetime                        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`       varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    `function_Type`  varchar(32) COLLATE utf8mb4_bin DEFAULT NULL,
    `sort`           int(10) DEFAULT '0' COMMENT '排序值',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_form_extra_function_relation
-- ----------------------------
DROP TABLE IF EXISTS `lc_form_extra_function_relation`;
CREATE TABLE `lc_form_extra_function_relation`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT,
    `function_id`   varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '功能id',
    `business_id`   varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '业务id',
    `business_type` varchar(16) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '业务类型',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_form_img
-- ----------------------------
DROP TABLE IF EXISTS `lc_form_img`;
CREATE TABLE `lc_form_img`
(
    `id`          varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `img_url`     longtext COLLATE utf8mb4_bin,
    `img_type`    varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
    `create_time` datetime                         DEFAULT CURRENT_TIMESTAMP,
    `creator`     varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '创建人',
    `modify_time` datetime                         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`    varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '修改人',
    `deleted`     tinyint(1) DEFAULT '0' COMMENT '删除',
    `file_name`   varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '文件名称',
    `bucket`      varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT 'bucket',
    `secret`      tinyint(1) DEFAULT '0' COMMENT '是否加密桶',
    `img_key`     varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'imgKey',
    `company_id`  bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_form_info
-- ----------------------------
DROP TABLE IF EXISTS `lc_form_info`;
CREATE TABLE `lc_form_info`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `application_id` varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `form_id`        varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `info_config`    longtext COLLATE utf8mb4_bin,
    `default_config` tinyint(1) DEFAULT NULL COMMENT '是否为默认详情页',
    `info_name`      varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '详情页名称',
    `create_time`    datetime                        DEFAULT CURRENT_TIMESTAMP,
    `creator_name`   varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人名字',
    `modify_time`    datetime                        DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier_name`  varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人名字',
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    `enable`         tinyint(1) DEFAULT '0',
    `sort`           int(10) DEFAULT '0' COMMENT '排序值',
    `other_config`   longtext COLLATE utf8mb4_bin,
    PRIMARY KEY (`id`, `application_id`, `form_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_form_model
-- ----------------------------
DROP TABLE IF EXISTS `lc_form_model`;
CREATE TABLE `lc_form_model`
(
    `id`                    bigint(20) NOT NULL AUTO_INCREMENT,
    `form_id`               varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '表单id',
    `model_id`              varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '流程模块id',
    `business_type`         varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '业务类型',
    `process_definition_id` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL,
    `application_id`        varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用id',
    `status`                varchar(6) COLLATE utf8mb4_bin  DEFAULT 'UP' COMMENT '状态',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='流程表单绑定表';

-- ----------------------------
-- Table structure for lc_form_module
-- ----------------------------
DROP TABLE IF EXISTS `lc_form_module`;
CREATE TABLE `lc_form_module`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `form_id`        varchar(24) COLLATE utf8mb4_bin NOT NULL COMMENT '对应的表单id',
    `config`         text COLLATE utf8mb4_bin COMMENT '配置',
    `name`           varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '名称',
    `business_id`    varchar(256) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '业务id',
    `business_type`  varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '业务类型',
    `create_time`    datetime                         DEFAULT CURRENT_TIMESTAMP,
    `creator`        varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '创建人',
    `modify_time`    datetime                         DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`       varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '修改人',
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    `module_type`    varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '组件类型',
    `application_id` varchar(24) COLLATE utf8mb4_bin NOT NULL COMMENT '应用id',
    PRIMARY KEY (`id`, `application_id`, `form_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='表单组件表';

-- ----------------------------
-- Table structure for lc_form_privilege
-- ----------------------------
DROP TABLE IF EXISTS `lc_form_privilege`;
CREATE TABLE `lc_form_privilege`
(
    `id`                      varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `application_id`          varchar(24) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '应用id',
    `category_id`             varchar(24) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '页面id',
    `user_privilege`          varchar(32) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '用户权限类型',
    `group_name`              varchar(64) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '名称',
    `description`             varchar(256) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '描述',
    `field_privilege`         longtext COLLATE utf8mb4_bin COMMENT '字段类型',
    `group_type`              varchar(32) COLLATE utf8mb4_bin   DEFAULT 'PRIVILEGE' COMMENT '分组类型',
    `view_privilege`          varchar(256) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '查看权限',
    `operate_privilege`       varchar(512) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '操作权限',
    `data_scope`              varchar(1024) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '数据范围',
    `create_time`             datetime                          DEFAULT CURRENT_TIMESTAMP,
    `creator_name`            varchar(64) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '创建人名字',
    `modify_time`             datetime                          DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier_name`           varchar(64) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '修改人名字',
    `deleted`                 tinyint(1) DEFAULT '0' COMMENT '删除',
    `operate_field_privilege` longtext COLLATE utf8mb4_bin COMMENT '操作字段类型',
    `sort`                    int(10) DEFAULT '0',
    `privilege_config`        longtext COLLATE utf8mb4_bin,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_form_privilege_user
-- ----------------------------
DROP TABLE IF EXISTS `lc_form_privilege_user`;
CREATE TABLE `lc_form_privilege_user`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `application_id` varchar(24) COLLATE utf8mb4_bin    DEFAULT NULL COMMENT '应用id',
    `category_id`    varchar(24) COLLATE utf8mb4_bin    DEFAULT NULL COMMENT '页面id',
    `group_id`       varchar(24) CHARACTER SET armscii8 DEFAULT NULL,
    `business_id`    varchar(24) COLLATE utf8mb4_bin    DEFAULT NULL,
    `business_type`  varchar(24) COLLATE utf8mb4_bin    DEFAULT NULL,
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_form_public_publish
-- ----------------------------
DROP TABLE IF EXISTS `lc_form_public_publish`;
CREATE TABLE `lc_form_public_publish`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `application_id` varchar(24) COLLATE utf8mb4_bin DEFAULT NULL,
    `form_id`        varchar(24) COLLATE utf8mb4_bin DEFAULT NULL,
    `publish_type`   varchar(16) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '发布类型',
    `config`         text COLLATE utf8mb4_bin COMMENT '发布配置',
    `create_time`    datetime                        DEFAULT CURRENT_TIMESTAMP,
    `creator_name`   varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人名字',
    `modify_time`    datetime                        DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier_name`  varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人名字',
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    `state`          tinyint(1) DEFAULT NULL COMMENT '状态',
    `access_token`   varchar(64) COLLATE utf8mb4_bin DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_form_publish
-- ----------------------------
DROP TABLE IF EXISTS `lc_form_publish`;
CREATE TABLE `lc_form_publish`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `company_id`     bigint(20) DEFAULT NULL COMMENT '公司id',
    `source_id`      varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '来源id',
    `config`         longtext COLLATE utf8mb4_bin COMMENT '配置',
    `table_name`     varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '对应表名',
    `version`        int(11) NOT NULL COMMENT '版本',
    `last_version`   tinyint(1) DEFAULT NULL COMMENT '最后一个版本',
    `create_time`    datetime                        DEFAULT CURRENT_TIMESTAMP,
    `creator`        varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
    `form_type`      varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '表单类型',
    `application_id` varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `form_config`    text COLLATE utf8mb4_bin COMMENT '表单列表配置',
    PRIMARY KEY (`id`, `application_id`, `version`) USING BTREE,
    KEY              `creator_idx` (`creator`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='表单发布表';

-- ----------------------------
-- Table structure for lc_form_quote
-- ----------------------------
DROP TABLE IF EXISTS `lc_form_quote`;
CREATE TABLE `lc_form_quote`
(
    `id`                  bigint(20) NOT NULL AUTO_INCREMENT,
    `form_id`             varchar(24) CHARACTER SET armscii8 DEFAULT NULL COMMENT '表单id',
    `business_type`       varchar(255) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '类型',
    `business_id`         varchar(128) CHARACTER SET armscii8 DEFAULT NULL COMMENT '对应类型id',
    `quote_form_id`       varchar(24) CHARACTER SET armscii8 DEFAULT NULL COMMENT '引用表单',
    `quote_field`         varchar(64) CHARACTER SET armscii8 DEFAULT NULL COMMENT '引用字段',
    `create_time`         datetime                           DEFAULT CURRENT_TIMESTAMP,
    `creator_name`        varchar(64) COLLATE utf8mb4_bin    DEFAULT NULL COMMENT '创建人名字',
    `modify_time`         datetime                           DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier_name`       varchar(64) COLLATE utf8mb4_bin    DEFAULT NULL COMMENT '修改人名字',
    `deleted`             tinyint(1) DEFAULT '0' COMMENT '删除',
    `application_id`      varchar(24) COLLATE utf8mb4_bin    DEFAULT NULL COMMENT '应用id',
    `business_field_type` varchar(64) COLLATE utf8mb4_bin    DEFAULT NULL COMMENT '业务字段类型',
    `quote_field_type`    varchar(64) COLLATE utf8mb4_bin    DEFAULT NULL COMMENT '引用字段类型',
    `aggregate`           tinyint(1) DEFAULT NULL COMMENT '是否聚合',
    `quote_type`          varchar(16) COLLATE utf8mb4_bin    DEFAULT 'FILED' COMMENT '引用类型',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_form_rule
-- ----------------------------
DROP TABLE IF EXISTS `lc_form_rule`;
CREATE TABLE `lc_form_rule`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `form_id`        varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `application_id` varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `rule_name`      varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
    `rule_config`    longtext COLLATE utf8mb4_bin,
    `rule_type`      varchar(16) COLLATE utf8mb4_bin  DEFAULT NULL,
    `state`          varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '状态',
    `create_time`    datetime                         DEFAULT CURRENT_TIMESTAMP,
    `creator`        varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '创建人',
    `modify_time`    datetime                         DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`       varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '修改人',
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    `sort`           int(10) DEFAULT '0',
    PRIMARY KEY (`id`, `application_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_form_serial_number
-- ----------------------------
DROP TABLE IF EXISTS `lc_form_serial_number`;
CREATE TABLE `lc_form_serial_number`
(
    `serial_key`    varchar(128) COLLATE utf8mb4_bin NOT NULL,
    `date_time`     varchar(8) COLLATE utf8mb4_bin   NOT NULL,
    `serial_number` int(20) DEFAULT NULL,
    PRIMARY KEY (`serial_key`, `date_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_form_use_time
-- ----------------------------
DROP TABLE IF EXISTS `lc_form_use_time`;
CREATE TABLE `lc_form_use_time`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `form_id`        varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用id',
    `user_id`        varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '用户id',
    `use_time`       datetime                        DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '使用时间',
    `application_id` varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用id',
    `company_id`     bigint(20) DEFAULT NULL COMMENT '公司id',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_form_user_config
-- ----------------------------
DROP TABLE IF EXISTS `lc_form_user_config`;
CREATE TABLE `lc_form_user_config`
(
    `id`             varchar(24) COLLATE utf8mb4_bin  NOT NULL COMMENT 'form_id',
    `application_id` varchar(24) COLLATE utf8mb4_bin  NOT NULL COMMENT '应用id',
    `config_type`    varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '配置类型',
    `user_id`        bigint(20) NOT NULL,
    `config`         longtext COLLATE utf8mb4_bin COMMENT '配置',
    `create_time`    datetime                        DEFAULT CURRENT_TIMESTAMP,
    `creator`        varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
    `modify_time`    datetime                        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`       varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    PRIMARY KEY (`id`, `application_id`, `config_type`, `user_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_job
-- ----------------------------
DROP TABLE IF EXISTS `lc_job`;
CREATE TABLE `lc_job`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `business_id`     varchar(128) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '业务id',
    `job_name`        varchar(64) COLLATE utf8mb4_bin  NOT NULL DEFAULT '' COMMENT '任务名称',
    `job_group`       varchar(64) COLLATE utf8mb4_bin  NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
    `invoke_target`   varchar(500) COLLATE utf8mb4_bin NOT NULL COMMENT '调用目标字符串',
    `cron_expression` varchar(64) COLLATE utf8mb4_bin           DEFAULT '' COMMENT 'cron执行表达式',
    `misfire_policy`  varchar(20) COLLATE utf8mb4_bin           DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
    `concurrent`      char(1) COLLATE utf8mb4_bin      NOT NULL DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
    `status`          char(1) COLLATE utf8mb4_bin               DEFAULT '0' COMMENT '状态（0正常 1暂停）',
    `create_time`     datetime                                  DEFAULT CURRENT_TIMESTAMP,
    `creator`         varchar(36) COLLATE utf8mb4_bin           DEFAULT NULL COMMENT '创建人',
    `modify_time`     datetime                                  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`        varchar(36) COLLATE utf8mb4_bin           DEFAULT NULL COMMENT '修改人',
    `remark`          varchar(500) COLLATE utf8mb4_bin          DEFAULT '' COMMENT '备注信息',
    `deleted`         tinyint(1) DEFAULT '0' COMMENT '删除',
    `start_time`      datetime                                  DEFAULT NULL COMMENT '开始时间',
    `end_time`        datetime                                  DEFAULT NULL COMMENT '结束时间',
    PRIMARY KEY (`id`, `job_name`, `job_group`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='定时任务调度表';

-- ----------------------------
-- Table structure for lc_job_log
-- ----------------------------
DROP TABLE IF EXISTS `lc_job_log`;
CREATE TABLE `lc_job_log`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
    `job_id`         bigint(20) NOT NULL COMMENT '任务id',
    `job_name`       varchar(64) COLLATE utf8mb4_bin  NOT NULL COMMENT '任务名称',
    `job_group`      varchar(64) COLLATE utf8mb4_bin  NOT NULL COMMENT '任务组名',
    `invoke_target`  varchar(500) COLLATE utf8mb4_bin NOT NULL COMMENT '调用目标字符串',
    `job_message`    varchar(500) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '日志信息',
    `status`         char(1) COLLATE utf8mb4_bin       DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
    `exception_info` varchar(2000) COLLATE utf8mb4_bin DEFAULT '' COMMENT '异常信息',
    `start_time`     datetime                          DEFAULT NULL COMMENT '创建时间',
    `end_time`       datetime                          DEFAULT NULL COMMENT '结束时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='定时任务调度日志表';

-- ----------------------------
-- Table structure for lc_manage
-- ----------------------------
DROP TABLE IF EXISTS `lc_manage`;
CREATE TABLE `lc_manage`
(
    `id`               varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `company_id`       bigint(20) DEFAULT NULL COMMENT '公司id',
    `manage_name`      varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '管理组名称',
    `dept_scope`       longtext COLLATE utf8mb4_bin COMMENT '部门管理',
    `post_scope`       longtext COLLATE utf8mb4_bin COMMENT '职位管理范围',
    `role_read`        tinyint(1) DEFAULT '0' COMMENT '内部角色可见',
    `role_write`       tinyint(1) DEFAULT '0' COMMENT '内部角色可管理',
    `dept_manage`      tinyint(1) DEFAULT '0' COMMENT '内部部门',
    `corp_coop_manage` tinyint(1) DEFAULT '0' COMMENT '互联组织',
    `app_update`       tinyint(1) DEFAULT '0' COMMENT '是否可操作应用',
    `create_time`      datetime                        DEFAULT CURRENT_TIMESTAMP,
    `creator`          varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
    `modify_time`      datetime                        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`         varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
    `deleted`          tinyint(1) DEFAULT '0' COMMENT '删除',
    `dept_scope_type`  text COLLATE utf8mb4_bin,
    `post_scope_type`  text COLLATE utf8mb4_bin,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_manage_application
-- ----------------------------
DROP TABLE IF EXISTS `lc_manage_application`;
CREATE TABLE `lc_manage_application`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `group_id`       varchar(24) COLLATE utf8mb4_bin DEFAULT NULL,
    `application_id` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL,
    `company_id`     bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_manage_user
-- ----------------------------
DROP TABLE IF EXISTS `lc_manage_user`;
CREATE TABLE `lc_manage_user`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT,
    `group_id`   varchar(24) COLLATE utf8mb4_bin DEFAULT NULL,
    `user_id`    bigint(20) DEFAULT NULL,
    `company_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE `lc_mcp_token`
(
    `id`          varchar(24) NOT NULL,
    `name`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  DEFAULT NULL,
    `expire_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '过期类型',
    `expire_time` datetime                                               DEFAULT NULL,
    `company_id`  bigint(20) DEFAULT NULL,
    `user_id`     bigint(20) DEFAULT NULL,
    `token`       varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
    `create_time` datetime                                               DEFAULT CURRENT_TIMESTAMP,
    `creator`     varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '创建人',
    `modify_time` datetime                                               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`    varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '修改人',
    `deleted`     tinyint(1) DEFAULT '0' COMMENT '删除',
    `status`      varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '状态',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_message
-- ----------------------------
DROP TABLE IF EXISTS `lc_message`;
CREATE TABLE `lc_message`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT,
    `company_id`   bigint(20) DEFAULT NULL COMMENT '公司id',
    `title`        varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '标题',
    `content`      text COLLATE utf8mb4_bin COMMENT '内容',
    `source`       varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '来源',
    `message_type` varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '消息类型',
    `create_time`  datetime                         DEFAULT CURRENT_TIMESTAMP,
    `creator`      varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '创建人',
    `deleted`      tinyint(1) DEFAULT '0',
    `send_type`    varchar(16) COLLATE utf8mb4_bin  DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_message_user
-- ----------------------------
DROP TABLE IF EXISTS `lc_message_user`;
CREATE TABLE `lc_message_user`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT,
    `message_id` bigint(20) DEFAULT NULL COMMENT '消息id',
    `view`       tinyint(1) DEFAULT '0' COMMENT '是否查看',
    `user_id`    bigint(20) DEFAULT NULL COMMENT '用户id',
    `view_time`  datetime DEFAULT NULL COMMENT '查看时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_plugin
-- ----------------------------
DROP TABLE IF EXISTS `lc_plugin`;
CREATE TABLE `lc_plugin`
(
    `id`                  varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `plugin_config`       longtext COLLATE utf8mb4_bin,
    `function_type`       varchar(16) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '插件事件',
    `plugin_type`         varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL,
    `plugin_name`         varchar(256) COLLATE utf8mb4_bin DEFAULT NULL,
    `plugin_return`       longtext COLLATE utf8mb4_bin COMMENT '插件返回参数',
    `plugin_param`        longtext COLLATE utf8mb4_bin COMMENT '插件参数',
    `plugin_extra_config` longtext COLLATE utf8mb4_bin COMMENT '额外配置',
    `creator`             varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '创建人',
    `modifier`            varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '修改人',
    `create_time`         datetime                         DEFAULT CURRENT_TIMESTAMP,
    `modify_time`         datetime                         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `deleted`             tinyint(1) DEFAULT '0' COMMENT '删除',
    `default_install`     tinyint(1) DEFAULT '0' COMMENT '默认安装',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='插件表';

INSERT INTO `lc_plugin` (`id`, `plugin_config`, `function_type`, `plugin_type`, `plugin_name`, `plugin_return`, `plugin_param`, `creator`, `modifier`, `create_time`, `modify_time`, `deleted`, `plugin_extra_config`, `default_install`) VALUES ('67283754bf936b22e671025f', NULL, 'API', 'WECOM_ROBOT', '企业微信群消息推送', NULL, '[\n	{\n		\"label\": \"群推送地址\",\n		\"required\": true,\n		\"fieldId\": \"url\",\n		\"fieldType\": \"text\",\n        \"tip\":\"请输入群消息推送地址, 如: Webhook 地址\"\n	},\n	{\n		\"label\": \"消息模板\",\n		\"required\": true,\n		\"fieldId\": \"markdowns\",\n		\"fieldType\": \"message_text\"\n	}\n]', NULL, NULL, '2025-09-30 10:01:05', '2026-08-21 11:14:23', 0, NULL, 1);
INSERT INTO `lc_plugin` (`id`, `plugin_config`, `function_type`, `plugin_type`, `plugin_name`, `plugin_return`, `plugin_param`, `creator`, `modifier`, `create_time`, `modify_time`, `deleted`, `plugin_extra_config`, `default_install`) VALUES ('67283754bf936b22e671025d', NULL, 'API', 'SYNC_DATA', '数据推送', NULL, '[\n	{\n		\"fieldId\": \"url\",\n		\"fieldType\": \"text\",\n		\"label\": \"推送地址\",\n		\"required\": true,\n        \"tip\":\"请输入推送地址, 如: https://www.elemost.com/api/order\"\n	},\n	{\n		\"fieldId\": \"appKey\",\n		\"fieldType\": \"text\",\n		\"label\": \"密钥\",\n		\"required\": true\n	}\n]', NULL, NULL, '2025-07-18 10:47:14', '2026-08-21 11:14:57', 0, NULL, 1);
INSERT INTO `lc_plugin` (`id`, `plugin_config`, `function_type`, `plugin_type`, `plugin_name`, `plugin_return`, `plugin_param`, `creator`, `modifier`, `create_time`, `modify_time`, `deleted`, `plugin_extra_config`, `default_install`) VALUES ('67283754bf936b22e671026k', NULL, 'API', 'DING_TALK_SWARM_ROBOT', '钉钉群消息推送', NULL, '[\n	{\n		\"fieldId\": \"title\",\n		\"fieldType\": \"text\",\n		\"label\": \"消息标题\",\n		\"required\": true\n	},\n	{\n		\"fieldId\": \"robotId\",\n		\"fieldType\": \"text\",\n		\"label\": \"群推送地址\",\n		\"required\": true,\n        \"tip\":\"请输入群消息推送地址, 如: Webhook 地址;\"\n	},\n	{\n		\"fieldId\": \"markdowns\",\n		\"fieldType\": \"message_text\",\n		\"label\": \"消息模板\",\n		\"required\": true\n	},\n	{\n		\"fieldId\": \"remindUsers\",\n		\"fieldType\": \"user\",\n		\"label\": \"需提醒对象\",\n		\"required\": false\n	}\n]', NULL, NULL, '2025-11-19 15:35:58', '2026-08-21 11:15:47', 0, NULL, 1);
-- ----------------------------
-- Table structure for lc_plugin_log
-- ----------------------------
DROP TABLE IF EXISTS `lc_plugin_log`;
CREATE TABLE `lc_plugin_log`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `plugin_id`   varchar(32) COLLATE utf8mb4_bin DEFAULT NULL,
    `company_id`  bigint(20) DEFAULT NULL,
    `user_id`     bigint(20) DEFAULT NULL,
    `create_time` datetime                        DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_task
-- ----------------------------
DROP TABLE IF EXISTS `lc_task`;
CREATE TABLE `lc_task`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `task_type`      varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
    `result`         longtext COLLATE utf8mb4_bin,
    `create_time`    datetime                         DEFAULT CURRENT_TIMESTAMP,
    `creator`        varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '创建人',
    `application_id` varchar(24) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '应用id',
    `form_id`        varchar(24) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '页面id',
    `status`         varchar(16) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '状态',
    `input`          longtext COLLATE utf8mb4_bin COMMENT '输入参数',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_template_application
-- ----------------------------
DROP TABLE IF EXISTS `lc_template_application`;
CREATE TABLE `lc_template_application`
(
    `id`                    varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `company_id`            bigint(20) DEFAULT NULL COMMENT '公司id',
    `application_name`      varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '应用名称',
    `description`           varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '描述',
    `visit_url`             varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '访问地址',
    `state`                 varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '状态',
    `create_time`           datetime                         DEFAULT CURRENT_TIMESTAMP,
    `creator`               varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '创建人',
    `modify_time`           datetime                         DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`              varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '修改人',
    `deleted`               tinyint(1) DEFAULT '0' COMMENT '删除',
    `application_type`      varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '应用类型',
    `icon`                  varchar(256) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '图标',
    `source_application_id` varchar(24) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '来源应用id',
    `introduce`             text COLLATE utf8mb4_bin COMMENT '模板介绍',
    `logo`                  varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '模板介绍',
    `download_count`        int(11) DEFAULT '0' COMMENT '下载数量',
    `recommend`             int(11) DEFAULT '0' COMMENT '推荐值',
    `application_nature`    varchar(8) COLLATE utf8mb4_bin   DEFAULT 'NORMAL' COMMENT '应用性质',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                     `company_id_idx` (`company_id`) USING BTREE,
    KEY                     `creator_idx` (`creator`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='应用';

-- ----------------------------
-- Table structure for lc_template_application_category
-- ----------------------------
DROP TABLE IF EXISTS `lc_template_application_category`;
CREATE TABLE `lc_template_application_category`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `application_id` varchar(24) COLLATE utf8mb4_bin NOT NULL COMMENT '应用id',
    `company_id`     bigint(20) DEFAULT NULL COMMENT '公司id',
    `parent_id`      varchar(24) COLLATE utf8mb4_bin  DEFAULT NULL,
    `source_id`      varchar(24) COLLATE utf8mb4_bin  DEFAULT NULL,
    `category_name`  varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '目录名称',
    `category_type`  varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '类目名称',
    `create_time`    datetime                         DEFAULT CURRENT_TIMESTAMP,
    `show_type`      varchar(16) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '显示类型',
    `creator`        varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '创建人',
    `modify_time`    datetime                         DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`       varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '修改人',
    `deleted`        tinyint(4) DEFAULT '0' COMMENT '删除',
    `published`      tinyint(1) DEFAULT NULL COMMENT '是否发布',
    `icon`           varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT 'icon',
    `sort_num`       int(10) DEFAULT '0' COMMENT '排序值',
    PRIMARY KEY (`id`, `application_id`) USING BTREE,
    KEY              `parent_id_idx` (`parent_id`),
    KEY              `source_id_idx` (`source_id`),
    KEY              `creator_idx` (`creator`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='应用目录';

-- ----------------------------
-- Table structure for lc_template_application_copy1
-- ----------------------------
DROP TABLE IF EXISTS `lc_template_application_copy1`;
CREATE TABLE `lc_template_application_copy1`
(
    `id`                    varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `company_id`            bigint(20) DEFAULT NULL COMMENT '公司id',
    `application_name`      varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '应用名称',
    `description`           varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '描述',
    `visit_url`             varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '访问地址',
    `state`                 varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '状态',
    `create_time`           datetime                         DEFAULT CURRENT_TIMESTAMP,
    `creator`               varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '创建人',
    `modify_time`           datetime                         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`              varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '修改人',
    `deleted`               tinyint(1) DEFAULT '0' COMMENT '删除',
    `application_type`      varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '应用类型',
    `icon`                  varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '图标',
    `source_application_id` varchar(24) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '来源应用id',
    `introduce`             text COLLATE utf8mb4_bin COMMENT '模板介绍',
    `logo`                  varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '模板介绍',
    `download_count`        int(11) DEFAULT '0' COMMENT '下载数量',
    `recommend`             int(11) DEFAULT '0' COMMENT '推荐值',
    PRIMARY KEY (`id`) USING BTREE,
    KEY                     `company_id_idx` (`company_id`) USING BTREE,
    KEY                     `creator_idx` (`creator`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='应用';

-- ----------------------------
-- Table structure for lc_template_application_img
-- ----------------------------
DROP TABLE IF EXISTS `lc_template_application_img`;
CREATE TABLE `lc_template_application_img`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `application_id` varchar(24) COLLATE utf8mb4_bin  DEFAULT NULL,
    `img_url`        varchar(128) COLLATE utf8mb4_bin DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_template_application_img_copy1
-- ----------------------------
DROP TABLE IF EXISTS `lc_template_application_img_copy1`;
CREATE TABLE `lc_template_application_img_copy1`
(
    `id`             bigint(20) DEFAULT NULL,
    `application_id` varchar(24) COLLATE utf8mb4_bin  DEFAULT NULL,
    `img_url`        varchar(128) COLLATE utf8mb4_bin DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_template_application_tag
-- ----------------------------
DROP TABLE IF EXISTS `lc_template_application_tag`;
CREATE TABLE `lc_template_application_tag`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `application_id` varchar(24) COLLATE utf8mb4_bin  DEFAULT NULL,
    `tag_type`       varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL,
    `tag_value`      varchar(128) COLLATE utf8mb4_bin DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_template_application_tag_copy1
-- ----------------------------
DROP TABLE IF EXISTS `lc_template_application_tag_copy1`;
CREATE TABLE `lc_template_application_tag_copy1`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `application_id` varchar(24) COLLATE utf8mb4_bin  DEFAULT NULL,
    `tag_type`       varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL,
    `tag_value`      varchar(128) COLLATE utf8mb4_bin DEFAULT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_template_form
-- ----------------------------
DROP TABLE IF EXISTS `lc_template_form`;
CREATE TABLE `lc_template_form`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL COMMENT 'id即categoryid',
    `company_id`     bigint(20) DEFAULT NULL COMMENT '公司id',
    `source_id`      varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '来源id',
    `config`         longtext COLLATE utf8mb4_bin COMMENT '配置',
    `table_name`     varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '对应表名',
    `create_time`    datetime                        DEFAULT CURRENT_TIMESTAMP,
    `creator`        varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
    `modify_time`    datetime                        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`       varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    `version`        int(11) DEFAULT NULL COMMENT '版本',
    `form_type`      varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '表单类型',
    `application_id` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '应用id',
    `form_config`    text COLLATE utf8mb4_bin COMMENT '表单列表配置',
    PRIMARY KEY (`id`, `application_id`) USING BTREE,
    KEY              `source_id_idx` (`source_id`),
    KEY              `creator_idx` (`creator`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='表单';

-- ----------------------------
-- Table structure for lc_template_form_aggregate
-- ----------------------------
DROP TABLE IF EXISTS `lc_template_form_aggregate`;
CREATE TABLE `lc_template_form_aggregate`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `application_id` varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `name`           varchar(64) COLLATE utf8mb4_bin DEFAULT NULL,
    `config`         longtext COLLATE utf8mb4_bin,
    `create_time`    datetime                        DEFAULT CURRENT_TIMESTAMP,
    `creator`        varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
    `modify_time`    datetime                        DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`       varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    PRIMARY KEY (`id`, `application_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='聚合表';

-- ----------------------------
-- Table structure for lc_template_form_data_factory
-- ----------------------------
DROP TABLE IF EXISTS `lc_template_form_data_factory`;
CREATE TABLE `lc_template_form_data_factory`
(
    `id`                varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `application_id`    varchar(32) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '应用id',
    `factory_type`      varchar(32) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '数据工厂类型',
    `factory_config`    longtext COLLATE utf8mb4_bin COMMENT '数据工厂配置',
    `table_name`        varchar(64) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '对应表名',
    `create_time`       datetime                                 DEFAULT CURRENT_TIMESTAMP,
    `creator`           varchar(36) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '创建人',
    `modify_time`       datetime                                 DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`          varchar(36) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '修改人',
    `deleted`           tinyint(1) DEFAULT '0' COMMENT '删除',
    `factory_name`      varchar(64) COLLATE utf8mb4_bin          DEFAULT NULL,
    `sync_config`       longtext COLLATE utf8mb4_bin,
    `status`            varchar(16) COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '状态',
    `version`           int(10) DEFAULT '0' COMMENT '版本号',
    `sync_config_error` tinyint(1) DEFAULT '0',
    `sync_form`         tinyint(1) DEFAULT '0',
    PRIMARY KEY (`id`, `application_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_template_form_data_stream
-- ----------------------------
DROP TABLE IF EXISTS `lc_template_form_data_stream`;
CREATE TABLE `lc_template_form_data_stream`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `name`           varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '名字',
    `config_type`    varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '配置类型',
    `config`         longtext COLLATE utf8mb4_bin COMMENT '配置',
    `application_id` varchar(24) COLLATE utf8mb4_bin NOT NULL COMMENT '应用id',
    `form_id`        varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '表单id',
    `canvas_config`  longtext COLLATE utf8mb4_bin COMMENT '画布配置',
    `create_time`    datetime                        DEFAULT CURRENT_TIMESTAMP,
    `creator`        varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
    `modify_time`    datetime                        DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`       varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    `state`          varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '状态',
    `version`        int(10) DEFAULT '1',
    `enable`         int(1) DEFAULT '1',
    PRIMARY KEY (`id`, `application_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_template_form_extra_function
-- ----------------------------
DROP TABLE IF EXISTS `lc_template_form_extra_function`;
CREATE TABLE `lc_template_form_extra_function`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `application_id` varchar(24) COLLATE utf8mb4_bin DEFAULT NULL,
    `form_id`        varchar(24) COLLATE utf8mb4_bin DEFAULT NULL,
    `config`         longtext COLLATE utf8mb4_bin,
    `create_time`    datetime                        DEFAULT CURRENT_TIMESTAMP,
    `creator`        varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
    `modify_time`    datetime                        DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`       varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    `function_Type`  varchar(32) COLLATE utf8mb4_bin DEFAULT NULL,
    `sort`           int(10) DEFAULT '0' COMMENT '排序值',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_template_form_extra_function_relation
-- ----------------------------
DROP TABLE IF EXISTS `lc_template_form_extra_function_relation`;
CREATE TABLE `lc_template_form_extra_function_relation`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT,
    `function_id`   varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '功能id',
    `business_id`   varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '业务id',
    `business_type` varchar(16) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '业务类型',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_template_form_info
-- ----------------------------
DROP TABLE IF EXISTS `lc_template_form_info`;
CREATE TABLE `lc_template_form_info`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `application_id` varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `form_id`        varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `info_config`    longtext COLLATE utf8mb4_bin,
    `default_config` tinyint(1) DEFAULT NULL COMMENT '是否为默认详情页',
    `info_name`      varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '详情页名称',
    `create_time`    datetime                        DEFAULT CURRENT_TIMESTAMP,
    `creator_name`   varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人名字',
    `modify_time`    datetime                        DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier_name`  varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人名字',
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    `enable`         tinyint(1) DEFAULT '0',
    `sort`           int(10) DEFAULT '0' COMMENT '排序值',
    `other_config`   longtext COLLATE utf8mb4_bin,
    PRIMARY KEY (`id`, `application_id`, `form_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_template_form_model
-- ----------------------------
DROP TABLE IF EXISTS `lc_template_form_model`;
CREATE TABLE `lc_template_form_model`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `form_id`        varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '表单id',
    `model_id`       varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '流程模块id',
    `business_type`  varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '业务类型',
    `application_id` varchar(24) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '应用id',
    `status`         varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='流程表单绑定表';

-- ----------------------------
-- Table structure for lc_template_form_module
-- ----------------------------
DROP TABLE IF EXISTS `lc_template_form_module`;
CREATE TABLE `lc_template_form_module`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `form_id`        varchar(24) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '对应的表单id',
    `config`         text COLLATE utf8mb4_bin COMMENT '配置',
    `name`           varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '名称',
    `business_id`    varchar(256) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '业务id',
    `business_type`  varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '业务类型',
    `create_time`    datetime                         DEFAULT CURRENT_TIMESTAMP,
    `creator`        varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '创建人',
    `modify_time`    datetime                         DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`       varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '修改人',
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    `module_type`    varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '组件类型',
    `application_id` varchar(24) COLLATE utf8mb4_bin NOT NULL COMMENT '应用id',
    PRIMARY KEY (`id`, `application_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='表单组件表';

-- ----------------------------
-- Table structure for lc_template_form_privilege
-- ----------------------------
DROP TABLE IF EXISTS `lc_template_form_privilege`;
CREATE TABLE `lc_template_form_privilege`
(
    `id`                      varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `application_id`          varchar(24) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '应用id',
    `category_id`             varchar(24) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '页面id',
    `user_privilege`          varchar(32) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '用户权限类型',
    `group_name`              varchar(64) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '名称',
    `description`             varchar(256) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '描述',
    `field_privilege`         longtext COLLATE utf8mb4_bin COMMENT '字段类型',
    `group_type`              varchar(32) COLLATE utf8mb4_bin   DEFAULT 'PRIVILEGE' COMMENT '分组类型',
    `view_privilege`          varchar(256) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '查看权限',
    `operate_privilege`       varchar(512) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '操作权限',
    `data_scope`              varchar(1024) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '数据范围',
    `create_time`             datetime                          DEFAULT CURRENT_TIMESTAMP,
    `creator_name`            varchar(64) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '创建人名字',
    `modify_time`             datetime                          DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier_name`           varchar(64) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '修改人名字',
    `deleted`                 tinyint(1) DEFAULT '0' COMMENT '删除',
    `operate_field_privilege` longtext COLLATE utf8mb4_bin COMMENT '操作字段类型',
    `sort`                    int(10) DEFAULT '0',
    `privilege_config`        longtext COLLATE utf8mb4_bin,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_template_form_privilege_user
-- ----------------------------
DROP TABLE IF EXISTS `lc_template_form_privilege_user`;
CREATE TABLE `lc_template_form_privilege_user`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT,
    `application_id` varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '应用id',
    `category_id`    varchar(24) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '页面id',
    `group_id`       varchar(24) COLLATE utf8mb4_bin DEFAULT NULL,
    `business_id`    varchar(24) COLLATE utf8mb4_bin DEFAULT NULL,
    `business_type`  varchar(24) COLLATE utf8mb4_bin DEFAULT NULL,
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_template_form_public_publish
-- ----------------------------
DROP TABLE IF EXISTS `lc_template_form_public_publish`;
CREATE TABLE `lc_template_form_public_publish`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `application_id` varchar(24) COLLATE utf8mb4_bin DEFAULT NULL,
    `form_id`        varchar(24) COLLATE utf8mb4_bin DEFAULT NULL,
    `publish_type`   varchar(16) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '发布类型',
    `config`         text COLLATE utf8mb4_bin COMMENT '发布配置',
    `create_time`    datetime                        DEFAULT CURRENT_TIMESTAMP,
    `creator_name`   varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人名字',
    `modify_time`    datetime                        DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier_name`  varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人名字',
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    `state`          tinyint(1) DEFAULT NULL COMMENT '状态',
    `access_token`   varchar(64) COLLATE utf8mb4_bin DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_template_form_quote
-- ----------------------------
DROP TABLE IF EXISTS `lc_template_form_quote`;
CREATE TABLE `lc_template_form_quote`
(
    `id`                  bigint(20) NOT NULL AUTO_INCREMENT,
    `form_id`             varchar(24) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '表单id',
    `business_type`       varchar(255) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '类型',
    `business_id`         varchar(24) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '对应类型id',
    `quote_form_id`       varchar(24) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '引用表单',
    `quote_field`         varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '引用字段',
    `create_time`         datetime                         DEFAULT CURRENT_TIMESTAMP,
    `creator_name`        varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '创建人名字',
    `modify_time`         datetime                         DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier_name`       varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '修改人名字',
    `deleted`             tinyint(1) DEFAULT '0' COMMENT '删除',
    `application_id`      varchar(24) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '应用id',
    `business_field_type` varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '业务字段类型',
    `quote_field_type`    varchar(64) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '引用字段类型',
    `aggregate`           tinyint(1) DEFAULT NULL COMMENT '是否聚合',
    `quote_type`          varchar(16) COLLATE utf8mb4_bin  DEFAULT 'FILED' COMMENT '引用类型',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for lc_template_form_rule
-- ----------------------------
DROP TABLE IF EXISTS `lc_template_form_rule`;
CREATE TABLE `lc_template_form_rule`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `form_id`        varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `application_id` varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `rule_name`      varchar(255) COLLATE utf8mb4_bin DEFAULT NULL,
    `rule_config`    longtext COLLATE utf8mb4_bin,
    `rule_type`      varchar(16) COLLATE utf8mb4_bin  DEFAULT NULL,
    `state`          varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '状态',
    `create_time`    datetime                         DEFAULT CURRENT_TIMESTAMP,
    `creator`        varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '创建人',
    `modify_time`    datetime                         DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`       varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '修改人',
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    `sort`           int(10) DEFAULT '0',
    PRIMARY KEY (`id`, `application_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

DROP TABLE IF EXISTS `op_data_api_config`;
CREATE TABLE `op_data_api_config`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `config_name`    varchar(64) COLLATE utf8mb4_bin DEFAULT NULL,
    `config`         longtext COLLATE utf8mb4_bin,
    `create_time`    datetime                        DEFAULT CURRENT_TIMESTAMP,
    `creator`        varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
    `modify_time`    datetime                        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`       varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    `company_id`     bigint(20) DEFAULT NULL,
    `application_id` varchar(24) COLLATE utf8mb4_bin DEFAULT NULL,
    `config_type`    varchar(32) COLLATE utf8mb4_bin DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

DROP TABLE IF EXISTS `op_secret`;
CREATE TABLE `op_secret`
(
    `id`          varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `company_id`  bigint(20) DEFAULT NULL,
    `app_key`     varchar(32) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '密钥key',
    `app_secret`  varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '开放平台key',
    `state`       varchar(16) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '状态 OPEN CLOSE',
    `create_time` datetime                         DEFAULT CURRENT_TIMESTAMP,
    `creator`     varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '创建人',
    `modify_time` datetime                         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`    varchar(36) COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '修改人',
    `deleted`     tinyint(1) DEFAULT '0' COMMENT '删除',
    `remark`      varchar(256) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


DROP TABLE IF EXISTS `op_secret_log`;
CREATE TABLE `op_secret_log`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT,
    `secret_id`     varchar(24) COLLATE utf8mb4_bin   DEFAULT NULL,
    `request_param` longtext COLLATE utf8mb4_bin COMMENT '请求参数',
    `create_time`   datetime                          DEFAULT CURRENT_TIMESTAMP,
    `result`        varchar(32) COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '请求结果',
    `api_name`      varchar(32) COLLATE utf8mb4_bin   DEFAULT NULL,
    `error_message` varchar(2100) COLLATE utf8mb4_bin DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;


DROP TABLE IF EXISTS `op_sync_mapping`;
CREATE TABLE `op_sync_mapping`
(
    `id`             varchar(24) COLLATE utf8mb4_bin NOT NULL,
    `application_id` varchar(32) COLLATE utf8mb4_bin NOT NULL,
    `form_id`        varchar(32) COLLATE utf8mb4_bin NOT NULL,
    `mapping_config` longtext COLLATE utf8mb4_bin,
    `create_time`    datetime                        DEFAULT CURRENT_TIMESTAMP,
    `creator`        varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '创建人',
    `modify_time`    datetime                        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `modifier`       varchar(36) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '修改人',
    `deleted`        tinyint(1) DEFAULT '0' COMMENT '删除',
    PRIMARY KEY (`id`, `application_id`, `form_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;