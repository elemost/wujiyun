package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ApplicationFlowableVO {
    private String id;

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

    private String categoryName;

    private List<ApplicationCategoryVO> applicationCategoryList;
}
