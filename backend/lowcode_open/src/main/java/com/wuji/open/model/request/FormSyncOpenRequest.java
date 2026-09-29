package com.wuji.open.model.request;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class FormSyncOpenRequest {
    private String uuid;

    private String applicationId;

    private String formId;

    private String transactionId;

    private String param;

    private JSONObject instValue;

    private Boolean startWorkflow = Boolean.TRUE;

    private Boolean startTrigger = Boolean.TRUE;
}
