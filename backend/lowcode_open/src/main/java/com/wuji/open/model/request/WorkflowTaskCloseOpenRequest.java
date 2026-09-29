package com.wuji.open.model.request;

import lombok.Data;

@Data
public class WorkflowTaskCloseOpenRequest {
    private String taskId;

    private String processInstanceId;
}
