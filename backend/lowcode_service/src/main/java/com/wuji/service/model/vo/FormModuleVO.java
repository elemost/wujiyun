package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class FormModuleVO {
    private String id;

    @ApiModelProperty("对应的表单id")
    private String formId;

    @ApiModelProperty("配置")
    private String config;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("业务id")
    private String businessId;

    @ApiModelProperty("业务类型")
    private String businessType;

    private String moduleType;

    private List<FormModuleBusinessVO> businessList;

    private List<String> businessIdList;
}
