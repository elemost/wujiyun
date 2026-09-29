package com.wuji.service.model.info;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class FormExtraFunctionInfoRelate {
    private String formId;

    private String name;

    private JSONObject relateConfig;
}
