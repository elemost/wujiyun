package com.wuji.service.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApplicationCategoryUpdateRequest {
    private String id;

    private String parentId;

    @ApiModelProperty("目录名称")
    private String categoryName;

    private String icon;

    private String applicationId;

    private String sourceId;

    private String config;
}
