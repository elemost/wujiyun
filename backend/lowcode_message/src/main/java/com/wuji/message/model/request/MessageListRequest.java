package com.wuji.message.model.request;

import com.wuji.common.model.request.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MessageListRequest extends BasePageRequest {
    private String messageType;

    private String content;

    private String source;

    private Boolean view;
}
