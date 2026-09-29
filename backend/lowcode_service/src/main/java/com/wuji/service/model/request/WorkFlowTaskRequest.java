package com.wuji.service.model.request;

import lombok.Data;

@Data
public class WorkFlowTaskRequest {
    private String taskId;

    private String processInstanceId;
}
