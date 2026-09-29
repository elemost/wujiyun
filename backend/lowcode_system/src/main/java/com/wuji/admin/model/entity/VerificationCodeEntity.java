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
 * 验证码
 * </p>
 *
 * @author hzm
 * @since 2025-02-20
 */
@Getter
@Setter
@TableName("wj_verification_code")
@ApiModel(value = "VerificationCodeEntity对象", description = "验证码")
public class VerificationCodeEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("电话")
    private String mobile;

    @ApiModelProperty("验证码")
    private String code;

    @ApiModelProperty("创建时间")
    private Long createTime;

    @ApiModelProperty("是否已使用")
    private Boolean codeUsed;
}
