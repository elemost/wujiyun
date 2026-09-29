package com.wuji.service.model.vo;

import com.wuji.service.enums.FormPrivilegeUserPrivilegeEnum;
import com.wuji.service.model.info.FormPrivilegeDataScope;
import com.wuji.service.model.info.FormPrivilegeFieldConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class FormPrivilegeVO {
    private String id;

    private String categoryId;

    @ApiModelProperty("名称")
    private String groupName;

    @ApiModelProperty("描述")
    private String description;

    private List<FormPrivilegeFieldConfig> fieldPrivilegeList;

    private List<FormPrivilegeFieldConfig> operateFieldPrivilegeList;

    @ApiModelProperty("分组类型")
    private String groupType;

    @ApiModelProperty("操作权限")
    private List<String> operatePrivilegeList;

    @ApiModelProperty("查看权限")
    private List<String> viewPrivilegeList;

    @ApiModelProperty("数据范围")
    private List<FormPrivilegeDataScope> dataScopeList;

    /**
     * @see FormPrivilegeUserPrivilegeEnum
     */
    private String userPrivilege;

    private String applicationId;

    private List<FormPrivilegeUserVO> formPrivilegeUserList;

    private List<String> buttonList;

    private Integer sort;

    private String privilegeConfig;
}
