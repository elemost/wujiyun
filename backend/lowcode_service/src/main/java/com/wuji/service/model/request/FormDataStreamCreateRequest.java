package com.wuji.service.model.request;

import com.wuji.common.privilege.annotation.ApplicationId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FormDataStreamCreateRequest {
    @ApiModelProperty("名字")
    private String name;

    @ApiModelProperty("配置类型")
    private String configType;

    @ApiModelProperty("配置")
    private String config;

    @ApiModelProperty("应用id")
    @ApplicationId
    private String applicationId;

    @ApiModelProperty("表单id")
    private String formId;

    private String canvasConfig;

    private String state;

    private Integer version;
}
