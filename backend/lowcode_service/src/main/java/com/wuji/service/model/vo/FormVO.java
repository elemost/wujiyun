package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FormVO {
    private String id;

    private String config;

    private String tableName;

    private String formType;

    private String formName;

    @ApiModelProperty("版本")
    private Integer version;

    @ApiModelProperty("显示类型")
    private String showType;

    @ApiModelProperty("是否发布过表单")
    private Boolean published;

    @ApiModelProperty("公司id")
    private Long companyId;

    @ApiModelProperty("来源id")
    private String sourceId;

    private String applicationId;

    private String formConfig;

    private String applicationName;

}
