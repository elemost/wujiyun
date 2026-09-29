package com.wuji.service.model.info;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TemplateApplicationCategoryVO {
    private String id;

    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("公司id")
    private Long companyId;

    private String parentId;

    private String sourceId;

    @ApiModelProperty("目录名称")
    private String categoryName;

    @ApiModelProperty("类目名称")
    private String categoryType;

    @ApiModelProperty("显示类型")
    private String showType;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;

    @ApiModelProperty("是否发布")
    private Boolean published;

    @ApiModelProperty("icon")
    private String icon;

    private Integer sortNum;
}
