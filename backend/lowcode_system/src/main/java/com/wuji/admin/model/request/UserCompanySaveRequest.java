package com.wuji.admin.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserCompanySaveRequest {

    @ApiModelProperty("公司id")
    private Long companyId;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("钉钉三方id")
    private String dingThirdId;

    @ApiModelProperty("钉钉unionid")
    private String dingUnionId;

    private String larkUnionId;

    private String larkUserId;

    private String larkOpenId;


    private String nickName;

    private String email;

    private String userType;

    private String reportLeader;

    // private Date entryTime;
    private String inviteCode;

    private String status;

    private String thirdId;

    private String thirdType;

    private String weComUserId;

    private Boolean adminUser;

    private String avatar;

    private Long sourceCompanyId;
}
