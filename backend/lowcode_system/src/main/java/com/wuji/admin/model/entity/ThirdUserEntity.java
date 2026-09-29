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
 * @since 2025-02-20
 */
@Getter
@Setter
@TableName("wj_third_user")
@ApiModel(value = "ThirdUserEntity对象", description = "")
public class ThirdUserEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    @ApiModelProperty("微信openid")
    private String openId;

    private String unionId;

    @ApiModelProperty("1 公众号 2 小程序")
    private Short thirdType;
}
