package com.wuji.admin.model.request;

import com.wuji.common.model.request.BasePageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;


@EqualsAndHashCode(callSuper = true)
@Data
public class RoleListRequest extends BasePageRequest {
    @ApiModelProperty("角色名称")
    private String roleName;


    @ApiModelProperty("角色权限字符串")
    private String roleKey;

    @ApiModelProperty("角色状态（0正常 1停用）")
    private String status;

    private Date startTime;

    private Date endTime;
}
