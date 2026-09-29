package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class ApplicationShareVO {

    private String id;

    private String applicationId;

    private String applicationName;

    @ApiModelProperty("描述")
    private String description;

    private String icon;

    private Integer expireDay;

    private Long companyId;

    private String companyUuid;

    private Date expireTime;

    @ApiModelProperty("创建人名字")
    private String creatorName;

    /**
     * 创建时间
     */
    private Date createTime;

    private Boolean needData;
}
