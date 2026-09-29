package com.wuji.admin.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 登录日志
 * </p>
 *
 * @author hzm
 * @since 2024-05-28
 */
@Data
public class LoginLogCreateRequest {

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("IP地址")
    private String ipAddr;

    @ApiModelProperty("状态")
    private Boolean state;

    @ApiModelProperty("描述")
    private String msg;
}
