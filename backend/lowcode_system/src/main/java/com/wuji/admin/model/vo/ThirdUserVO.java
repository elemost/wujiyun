package com.wuji.admin.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@ApiModel(value = "ThirdUserEntity对象", description = "")
public class ThirdUserVO {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    @ApiModelProperty("微信openid")
    private String openId;

    private String unionId;
}
