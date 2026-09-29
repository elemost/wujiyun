package com.wuji.platform.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SecretLogSaveRequest {
    private String secretId;

    @ApiModelProperty("请求参数")
    private String requestParam;

    @ApiModelProperty("请求结果")
    private String result;

    private String apiName;

    private String errorMessage;
}
