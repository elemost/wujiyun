package com.wuji.service.model.request;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class FormExtraFunctionUpdateRequest {

    private String id;

    private JSONObject configJson;

    private List<String> privilegeIdList;

    private String functionType;
}
