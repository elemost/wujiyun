package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApplicationPrivilegeVO {
    @ApiModelProperty("权限业务类型：用户，部门")
    private String businessType;

    @ApiModelProperty("权限业务id")
    private String businessId;

    private String businessName;

    private String privilegeType;
}
