package com.wuji.common.model.info;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class FormSelectData {
    private JSONObject fieldValues;

    private String label;

    private String sourceFormId;

    private String uuid;
}
