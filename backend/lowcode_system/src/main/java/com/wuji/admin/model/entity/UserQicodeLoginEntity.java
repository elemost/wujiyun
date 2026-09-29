package com.wuji.admin.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户扫码登录表
 * </p>
 *
 * @author hzm
 * @since 2025-02-20
 */
@Getter
@Setter
@TableName("wj_user_qicode_login")
@ApiModel(value = "UserQicodeLoginEntity对象", description = "用户扫码登录表")
public class UserQicodeLoginEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("获取二维码的凭证")
    private String ticket;

    @ApiModelProperty("生成二维码的screen")
    private String qrScene;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    private String openId;

    private String unionId;

    @ApiModelProperty("是否使用")
    private Byte used;

    @ApiModelProperty("扫码后动作")
    private String event;
}
