package com.wuji.admin.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserCompanyVO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("公司id")
    private Long companyId;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("钉钉三方id")
    private String dingThirdId;

    @ApiModelProperty("钉钉unionid")
    private String dingUnionId;

    private String status;

    private String delFlag;

    private String larkUnionId;

    private String larkUserId;

    private String larkOpenId;

    private String nickName;

    private String email;

    private String thirdId;

    private String thirdType;

    private Boolean adminUser;

    private String avatar;

    private String weComUserId;

    private String userType;

    private Long sourceCompanyId;

    @ApiModelProperty("工号")
    private String jobNumber;
}
