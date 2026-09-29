package com.wuji.service.model.vo;

import com.wuji.service.model.info.FormConfigCommon;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class FormFieldVO {
    private String id;

    private String applicationId;

    @ApiModelProperty("公司id")
    private Long companyId;

    @ApiModelProperty("来源id")
    private String sourceId;

    @ApiModelProperty("配置")
    private String config;

    private String formType;

    private String categoryName;

    private List<FormConfigCommon> fields;

    private String tableName;
}
