package com.wuji.admin.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RoleCreateRequest {


    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("角色权限字符串")
    private String roleKey;

    @ApiModelProperty("显示顺序")
    private Integer roleSort;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("角色状态（0正常 1停用）")
    private String status;

    private List<Long> menuIdList;
}
