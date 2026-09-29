package com.wuji.workflow.model.request;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class FlowableCopyCreateRequest {
    @ApiModelProperty("流程实例id")
    private String processInstanceId;

    @ApiModelProperty("任务id")
    private String taskId;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("发起人")
    private String initiator;

    @ApiModelProperty("表单id")
    private String formId;

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

    private String applicationId;

    private String dataUuid;

    private Long companyId;

    private Map<String, Object> processVariables;

    private JSONObject instValue;
}
