package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class FormFlowableCopyVO {

    private Long id;

    @ApiModelProperty("流程实例id")
    private String processInstanceId;

    @ApiModelProperty("任务id")
    private String taskId;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("发起人")
    private String initiator;

    private String initiatorName;

    @ApiModelProperty("表单id")
    private String formId;

    private String formName;

    @ApiModelProperty("流程业务key")
    private String businessType;

    @ApiModelProperty("模型id")
    private String modelId;

    @ApiModelProperty("任务节点id")
    private String activityId;

    @ApiModelProperty("对应流程")
    private String processDefinitionId;

    @ApiModelProperty("对应流程名称")
    private String processDefinitionName;

    private String dataUuid;

    private String applicationName;

    private Boolean userView;

    private Date viewTime;
    /**
     * 创建时间
     */
    protected Date createTime;

    private String applicationId;
}
