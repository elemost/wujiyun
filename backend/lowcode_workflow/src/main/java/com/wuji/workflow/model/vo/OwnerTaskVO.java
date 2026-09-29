package com.wuji.workflow.model.vo;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class OwnerTaskVO {
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
    private Map<String, Object> processVariables;

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
     * 流程实例ID
     */
    private String procInsId;
    /**
     * 流程实例
     */
    private String processInstanceId;

    private Date createTime;

    private String processStatus;

    private Date finishTime;

}
