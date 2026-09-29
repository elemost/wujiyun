package com.wuji.service.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class FormModuleUpdateRequest {
    private String id;

    private String applicationId;

    @ApiModelProperty("对应的表单id")
    private String formId;

    @ApiModelProperty("配置")
    private String config;

    @ApiModelProperty("名称")
    private String name;

    private String businessId;

    @ApiModelProperty("业务id")
    private List<String> businessIdList;

    @ApiModelProperty("业务类型")
    private String businessType;

    private String moduleType;
}
