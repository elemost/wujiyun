package com.wuji.service.model.info.stream;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class DataStreamCalculateVO {
    private Long nodeId;

    private JSONObject jsonValue;

    private Object value;

    private String nodeType;

    private Boolean resultNull = false;

    private List<JSONObject> jsonValueList;

    private String formId;
}
