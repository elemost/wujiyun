package com.wuji.service.model.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class FormExtraFunctionInfoPrivilegeVO {
    private String name;

    private String formId;

    private JSONObject relateConfig;
}
