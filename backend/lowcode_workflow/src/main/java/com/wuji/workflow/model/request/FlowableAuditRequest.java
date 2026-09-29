package com.wuji.workflow.model.request;

import com.wuji.admin.aspect.corp.annotation.CorpCoop;
import com.wuji.workflow.model.info.FlowableAssigneeConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class FlowableAuditRequest {
    private String processInstanceId;

    private String taskId;

    private String comment;

    @ApiModelProperty("退回节点")
    private String targetKey;

    @ApiModelProperty("委派用户")
    private Long userId;

    private String taskKey;

    @CorpCoop
    private String companyUuid;

    private List<FlowableAssigneeConfig> flowableAssigneeConfigList;
}
