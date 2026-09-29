package com.wuji.service.model.info;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class FormPrivilegeFieldConfig {
    @ApiModelProperty("字段id")
    private String type;

    private String name;

    @ApiModelProperty("是否可见")
    private Boolean visibleFlag;

    @ApiModelProperty("是否可编辑")
    private Boolean editableFlag;

    private List<FormPrivilegeFieldConfig> children;
}
