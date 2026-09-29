package com.wuji.service.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskCreateRequest {


    private String applicationId;

    private String formId;

    private String taskType;

    private String input;
}
