package com.wuji.admin.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserCompleteRequest {
    private String companyName;

    private String parentUuid;

    private String industry;

    private String nickName;

    @ApiModelProperty("需求")
    private String demand;

    @ApiModelProperty("管理需求")
    private String manageDemand;

    @ApiModelProperty("了解途径")
    private String understandWay;

    @ApiModelProperty("是否使用过低代码")
    private String useLowcode;

    @ApiModelProperty("公司内角色")
    private String companyRole;

    private String mobile;
}
