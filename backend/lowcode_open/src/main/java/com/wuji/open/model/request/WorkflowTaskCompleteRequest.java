package com.wuji.open.model.request;

import lombok.Data;

@Data
public class WorkflowTaskCompleteRequest {
    private String taskId;

    private String processInstanceId;

    private String comment;
}
