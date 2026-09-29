package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FormPrivilegeUserVO {
    private String groupId;

    @ApiModelProperty("权限业务类型：用户，部门")
    private String businessType;

    @ApiModelProperty("权限业务id")
    private String businessId;

    private String businessName;

    private String applicationId;

    private String categoryId;
}
