package com.wuji.service.model.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;

@Data
public class FormDataStreamPluginVO {
    private Object result;

    private JSONObject returnJson;

    private List<JSONObject> returnJsonList;

    private Boolean more = false;
}
