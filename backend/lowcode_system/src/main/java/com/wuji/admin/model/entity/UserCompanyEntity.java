package com.wuji.admin.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author hzm
 * @since 2024-10-28
 */
@Getter
@Setter
@TableName("sys_user_company")
@ApiModel(value = "UserCompanyEntity对象", description = "")
public class UserCompanyEntity {

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

    private String userType;

    private String thirdId;

    private String thirdType;

    private String weComUserId;

    private Boolean adminUser;

    private String avatar;

    private Long sourceCompanyId;

    private String userCompanyCode;

    @ApiModelProperty("工号")
    private String jobNumber;
}
