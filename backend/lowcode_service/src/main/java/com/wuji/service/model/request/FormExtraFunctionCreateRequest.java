package com.wuji.service.model.request;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.privilege.annotation.ApplicationId;
import lombok.Data;

import java.util.List;

@Data
public class FormExtraFunctionCreateRequest {
    private String formId;

    @ApplicationId
    private String applicationId;

    private JSONObject configJson;

    private List<String> privilegeIdList;

    private String functionType;
}
