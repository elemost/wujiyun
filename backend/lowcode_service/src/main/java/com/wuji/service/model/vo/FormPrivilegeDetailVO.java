package com.wuji.service.model.vo;

import com.wuji.service.enums.FormPrivilegeDataScopeTypeEnum;
import com.wuji.service.enums.FormPrivilegeUserPrivilegeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class FormPrivilegeDetailVO {
    private String id;

    private String applicationId;

    @ApiModelProperty("页面id")
    private String categoryId;

    @ApiModelProperty("名称")
    private String groupName;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("字段权限")
    private String fieldPrivilege;

    @ApiModelProperty("操作字段权限")
    private String operateFieldPrivilege;

    @ApiModelProperty("分组类型")
    private String groupType;

    @ApiModelProperty("操作权限")
    private String operatePrivilege;

    @ApiModelProperty("查看权限")
    private String viewPrivilege;

    /**
     * @see FormPrivilegeDataScopeTypeEnum
     */
    @ApiModelProperty("数据范围")
    private String dataScope;

    /**
     * @see FormPrivilegeUserPrivilegeEnum
     */
    private String userPrivilege;

    private Integer sort;

    private List<FormPrivilegeUserVO> formPrivilegeUserList;
}
