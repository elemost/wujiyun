package com.wuji.service.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ApplicationPrivilegeSaveRequest {
    @ApiModelProperty("权限业务类型：用户，部门")
    private String businessType;

    @ApiModelProperty("权限业务id")
    private String businessId;
}
