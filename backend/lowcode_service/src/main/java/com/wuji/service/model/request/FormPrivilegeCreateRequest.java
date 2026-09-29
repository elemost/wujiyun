package com.wuji.service.model.request;

import com.wuji.service.enums.FormPrivilegeUserPrivilegeEnum;
import com.wuji.service.model.info.FormPrivilegeDataScope;
import com.wuji.service.model.info.FormPrivilegeFieldConfig;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class FormPrivilegeCreateRequest {
    private String applicationId;

    @ApiModelProperty("页面id")
    private String categoryId;

    @ApiModelProperty("名称")
    private String groupName;

    @ApiModelProperty("描述")
    private String description;

    private List<FormPrivilegeFieldConfig> fieldPrivilegeList;

    private List<FormPrivilegeFieldConfig>operateFieldPrivilegeList;

    @ApiModelProperty("分组类型")
    private String groupType = "PRIVILEGE";

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

    private List<FormPrivilegeUserRequest> formPrivilegeUserList;

    private List<String> buttonList;

    private String privilegeConfig;
}
