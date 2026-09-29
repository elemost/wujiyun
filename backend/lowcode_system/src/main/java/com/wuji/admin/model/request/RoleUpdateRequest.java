package com.wuji.admin.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RoleUpdateRequest {
    @ApiModelProperty("角色ID")
    private Long roleId;

    @ApiModelProperty("角色名称")
    private String roleName;

    @ApiModelProperty("角色权限字符串")
    private String roleKey;

    @ApiModelProperty("显示顺序")
    private Integer roleSort;

    @ApiModelProperty("备注")
    private String remark;

    private List<Long> menuIdList;
}
