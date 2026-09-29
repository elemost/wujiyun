package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ApplicationVO {
    private String id;

    @ApiModelProperty("公司id")
    private Long companyId;

    @ApiModelProperty("应用名称")
    private String applicationName;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("访问地址")
    private String visitUrl;

    private String icon;

    private String applicationType;

    @ApiModelProperty("状态")
    private String state;

    private Long expireTime;

    private String creator;

    private String templateId;

    private String applicationNature;

    private List<String> buttonList = new ArrayList<>();
}
