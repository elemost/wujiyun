package com.wuji.workflow.model.domain;

import com.wuji.workflow.model.info.FlowableAssigneeConfig;
import com.wuji.workflow.model.info.FlowableCopyConfig;
import com.wuji.workflow.model.info.FlowableFormFieldConfig;
import com.wuji.workflow.model.info.FlowableMongodbSearchFilter;
import com.wuji.workflow.model.info.FlowableSubFlowConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class FlowableActivityConfigDomain {
    private String modelId;

    private String activityPid;

    private String activityId;

    private String activityName;

    private FlowableMongodbSearchFilter conditionConfigJson;

    @ApiModelProperty("审批人配置")
    private List<FlowableAssigneeConfig> assigneeConfigList;

    @ApiModelProperty("字段配置")
    private List<FlowableFormFieldConfig> fieldConfigList;

    @ApiModelProperty("抄送配置")
    private List<FlowableCopyConfig> copyConfigList;

    private FlowableSubFlowConfig subFlowConfigJson;

    private String activityType;

    private String auditType;

    private String buttonConfig;

    private String remindConfig;

    private String otherConfig;
}
