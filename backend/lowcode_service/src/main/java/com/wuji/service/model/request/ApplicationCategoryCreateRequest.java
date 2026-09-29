package com.wuji.service.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApplicationCategoryCreateRequest {

    private String applicationId;

    private String parentId;

    private String sourceId;

    @ApiModelProperty("目录名称")
    private String categoryName;

    @ApiModelProperty("目录类型")
    private String categoryType;

    private String icon;
}
