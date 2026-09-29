package com.wuji.service.model.info;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wuji.service.model.info.form.FormConfigQuickEdit;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormConfigCommon {
    private String id;
    
    private String label;

    private String type;

    private String name;

    private Boolean system = Boolean.FALSE;

    private List<FormConfigCommon> columns;

    private Boolean used;

    private List<SerialNumberConfig> rules;

    private String displayFormat;

    /**
     * @see FormConfigQuickEdit
     */
    private String quickEdit;

    private String options;

    private String dividerTitle;

    private Boolean colorful;

    private List<FormConfigTab> tabs;

    private String format;

    private JSONObject dataRegion;

    private Boolean duplicateValue;

    private Boolean visible;

    private Boolean encrypt = Boolean.FALSE;

    private String subFromLabel;

    private String subFormName;

    private JSONObject formLinkageConfig;

    private JSONObject dataLinkageConfig;

    private Boolean systemUrl = Boolean.FALSE;

    private Boolean hidden;

    private Boolean filedEditable;

    private Boolean required;

    private String suffix;
}
