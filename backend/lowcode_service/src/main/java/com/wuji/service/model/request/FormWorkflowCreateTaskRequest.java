package com.wuji.service.model.request;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class FormWorkflowCreateTaskRequest {
    private String applicationId;

    private String formId;

    private String createFormId;

    private Integer version;

    private JSONObject instValue;

    private String uuid;

    private String parentTaskId;

    private String parentProcessInstanceId;

    private String createUserId;

    private Boolean needCallBack;

    private String processInstanceId;
}
