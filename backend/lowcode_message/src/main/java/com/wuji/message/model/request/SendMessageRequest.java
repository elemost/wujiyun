package com.wuji.message.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SendMessageRequest {

    private String receiveType;

    @ApiModelProperty("企业微信支持：text")
    private String messageType;

    private String message;

    private String robotId;

    private Long companyId;

    private String source;

    private List<Long> userIdList;

    private String suiteId;
}
