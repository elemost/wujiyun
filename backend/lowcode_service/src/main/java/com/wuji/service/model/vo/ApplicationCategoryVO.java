package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ApplicationCategoryVO {
    private String id;

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

    @ApiModelProperty("是否发布过表单")
    private Boolean published;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("修改人")
    private String modifier;

    private String icon;

    private Integer sortNum;

    private List<ApplicationCategoryVO> children;
}
