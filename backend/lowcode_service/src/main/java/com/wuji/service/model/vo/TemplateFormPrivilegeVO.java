package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TemplateFormPrivilegeVO {
    @ApiModelProperty("应用id")
    private String applicationId;

    @ApiModelProperty("页面id")
    private String categoryId;

    @ApiModelProperty("用户权限类型")
    private String userPrivilege;

    @ApiModelProperty("名称")
    private String groupName;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("字段类型")
    private String fieldPrivilege;

    @ApiModelProperty("分组类型")
    private String groupType;

    @ApiModelProperty("查看权限")
    private String viewPrivilege;

    @ApiModelProperty("操作权限")
    private String operatePrivilege;

    @ApiModelProperty("数据范围")
    private String dataScope;

    @ApiModelProperty("创建人名字")
    private String creatorName;

    @ApiModelProperty("修改人名字")
    private String modifierName;

    @ApiModelProperty("操作字段类型")
    private String operateFieldPrivilege;

    private String id;

    private Integer sort;

    private String privilegeConfig;

    private List<TemplateFormPrivilegeUserVO> templateFormPrivilegeUserList;
}
