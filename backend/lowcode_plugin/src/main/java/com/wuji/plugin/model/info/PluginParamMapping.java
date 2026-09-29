package com.wuji.plugin.model.info;

import lombok.Data;

import java.util.List;

@Data
public class PluginParamMapping {
    private String fieldId;

    private String fieldType;

    //
    private String valueType;

    /**
     * 自定义value
     */
    private Object value;

    private List<PluginParamMapping> children;

    private List<List<PluginParamMapping>> subFormList;
}
