package com.wuji.admin.model.request;


import com.wuji.admin.aspect.corp.annotation.CorpCoop;
import com.wuji.common.model.request.BasePageRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserRequest extends BasePageRequest {
    private String realName;

    private String userType = "00";

    private String departmentId;

    private List<Long> roleList;

    private String companyId;

    private String phonenumber;

    private String status;

    private Date startTime;

    private Date endTime;

    @ApiModelProperty("用户账号")
    private String userName;

    private String nickName;

    private String postId;

    @CorpCoop
    private String companyUuid;

    private List<Long> sourceCompanyId;
}
