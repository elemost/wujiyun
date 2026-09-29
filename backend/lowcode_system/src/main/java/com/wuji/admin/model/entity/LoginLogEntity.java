package com.wuji.admin.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * <p>
 * 登录日志
 * </p>
 *
 * @author hzm
 * @since 2024-05-28
 */
@Getter
@Setter
@TableName("sys_login_log")
@ApiModel(value = "LoginLogEntity对象", description = "登录日志")
public class LoginLogEntity {

    /**
     * 主键自增id
     */
    @TableId(type = IdType.AUTO)
    protected String id;

    /**
     * 创建时间
     */
    protected Date createTime;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("IP地址")
    private String ipAddr;

    @ApiModelProperty("状态")
    private Boolean state;

    @ApiModelProperty("描述")
    private String msg;
}
