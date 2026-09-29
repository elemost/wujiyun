package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TemplateFormPrivilegeUserVO {
    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("页面id")
    private String categoryId;

    private String groupId;

    private String businessId;

    private String businessType;
}
