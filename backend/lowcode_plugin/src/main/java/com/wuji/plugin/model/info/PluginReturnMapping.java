package com.wuji.plugin.model.info;

import lombok.Data;

import java.util.List;

@Data
public class PluginReturnMapping {
    private String fieldId;

    private String fieldType;

    private String jsonPath;

    private String label;

    private List<PluginReturnMapping> children;
}
