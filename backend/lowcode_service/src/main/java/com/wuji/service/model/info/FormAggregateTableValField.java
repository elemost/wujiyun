package com.wuji.service.model.info;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FormAggregateTableValField {
    private List<String> forms;

    private Map<String, String> formulaMap;

    private String formula;

    private String name;

    private String subForm;

    private String tag;

    private String text;

    private String type;

    private JSONObject formulaConfig;

    private String calculate;

    private FormAggregateFormat format;
}
