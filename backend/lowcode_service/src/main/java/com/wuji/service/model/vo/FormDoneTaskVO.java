package com.wuji.service.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class FormDoneTaskVO {
    /**
     * 任务编号
     */
    private String taskId;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 任务Key
     */
    private String taskDefKey;

    /**
     * 流程发起人Id
     */
    private String startUserId;
    /**
     * 流程发起人名称
     */
    private String startUserName;
    /**
     * 流程变量信息
     */
    private Map<String, Object> ProcessVariables;

    /**
     * 流程ID
     */
    private String procDefId;
    /**
     * 流程部署编号
     */
    private String deployId;
    /**
     * 流程key
     */
    private String procDefKey;
    /**
     * 流程定义名称
     */
    private String procDefName;
    /**
     * 流程定义内置使用版本
     */
    private int procDefVersion;
    /**
     * 流程实例
     */
    private String processInstanceId;

    private Date createTime;

    /**
     * 完成时间
     */
    private Date finishTime;

    /**
     * 审批间断时间
     */
    private String duration;

    private String formName;

    private String applicationName;

    private String applicationId;
}
