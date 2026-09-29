package com.wuji.platform.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class SecretVO {
    private Long companyId;

    @ApiModelProperty("开放平台key")
    private String appSecret;

    private String appKey;

    private String remark;

    @ApiModelProperty("状态 OPEN CLOSE")
    private String state;

    private String id;

    private Date createTime;
}
