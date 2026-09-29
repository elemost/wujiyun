package com.wuji.common.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class QrcodeVO {
    private String id;

    @ApiModelProperty("二维码")
    private String qrcode;

    @ApiModelProperty("二维码类型")
    private String type;

    @ApiModelProperty("是否成功")
    private Boolean success;

    @ApiModelProperty("创建时间")
    private Date createTime;

    private String creatorName;

    private String content;
}
