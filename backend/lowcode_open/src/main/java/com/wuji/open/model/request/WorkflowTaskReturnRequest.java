package com.wuji.open.model.request;

import lombok.Data;

@Data
public class WorkflowTaskReturnRequest {
    private String taskId;
    private String processInstanceId;
    private String comment;
    private String taskKey;
}
