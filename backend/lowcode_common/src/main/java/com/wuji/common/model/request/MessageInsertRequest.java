package com.wuji.common.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class MessageInsertRequest {
    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("来源")
    private String source;

    @ApiModelProperty("消息类型")
    private String messageType;

    private List<Long> userIdList;

    private Long companyId;
}
