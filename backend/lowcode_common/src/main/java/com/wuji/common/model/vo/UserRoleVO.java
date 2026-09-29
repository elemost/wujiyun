package com.wuji.common.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserRoleVO implements Serializable {
    private Long roleId;

    private Long userId;

    private Long companyId;

    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("角色权限字符串")
    private String roleKey;

    @ApiModelProperty("角色排序")
    private Integer roleSort;

    @ApiModelProperty("角色状态（0正常 1停用）")
    private Boolean state;

    @ApiModelProperty("备注")
    private String remark;
}
