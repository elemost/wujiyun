package com.wuji.service.model.request;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class FormExtraFunctionTitleSaveRequest {
    private String applicationId;

    private String formId;

    private JSONObject configJson;
}
