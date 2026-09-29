package com.wuji.workflow.model.info;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class FlowableAssigneeConfig {
    private String assigneeType;

    private String assigneeId;

    private String assigneeName;

    public static List<FlowableAssigneeConfig> getDefaultConfig() {
        FlowableAssigneeConfig flowableAssigneeConfig = new FlowableAssigneeConfig();
        flowableAssigneeConfig.setAssigneeType("self");
        flowableAssigneeConfig.setAssigneeName("流程发起人");
        flowableAssigneeConfig.setAssigneeId("self");
        return Collections.singletonList(flowableAssigneeConfig);
    }
}
