package com.wuji.service.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class FormInfoVO {
    private String id;

    private String applicationId;

    private String formId;

    private String infoConfig;

    @ApiModelProperty("是否为默认详情页")
    private Boolean defaultConfig;

    @ApiModelProperty("详情页名称")
    private String infoName;

    private Date createTime;

    private Boolean enable;

    private Integer sort;

    private String otherConfig;
}
