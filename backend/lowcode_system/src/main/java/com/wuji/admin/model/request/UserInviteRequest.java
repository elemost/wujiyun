package com.wuji.admin.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserInviteRequest {
    private String nickName;

    @ApiModelProperty("手机号码")
    private String phonenumber;

    private List<Long> deptIdList;

    private String userType;
}
