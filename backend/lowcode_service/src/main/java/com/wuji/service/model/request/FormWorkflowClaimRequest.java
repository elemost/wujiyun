package com.wuji.service.model.request;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class FormWorkflowClaimRequest {
    private String uuid;

    private String applicationId;

    private String formId;

    private Integer version;

    private JSONObject instValue;
}
