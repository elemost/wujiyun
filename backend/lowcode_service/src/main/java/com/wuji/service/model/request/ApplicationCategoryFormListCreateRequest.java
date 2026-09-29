package com.wuji.service.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApplicationCategoryFormListCreateRequest {
    private String parentId;

    private String sourceId;

    @ApiModelProperty("目录名称")
    private String categoryName;

    @ApiModelProperty("类目类型")
    private String categoryType;
}
