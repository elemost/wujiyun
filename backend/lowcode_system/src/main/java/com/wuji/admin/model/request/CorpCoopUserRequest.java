package com.wuji.admin.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CorpCoopUserRequest {
    private String companyName;

    @ApiModelProperty("用户账号")
    private String userName;

    @ApiModelProperty("用户昵称")
    private String nickName;

    @ApiModelProperty("用户邮箱")
    private String email;

    @ApiModelProperty("手机号码")
    private String phonenumber;

    @ApiModelProperty("帐号状态（0正常 1停用）")
    private String status;

    @ApiModelProperty("用户性别（0男 1女 2未知）")
    private String sex;

    @ApiModelProperty("备注")
    private String remark;

    private String password;
}
