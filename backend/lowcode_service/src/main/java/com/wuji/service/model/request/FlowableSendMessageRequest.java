package com.wuji.service.model.request;

import com.wuji.service.model.info.FormMessageMarkdown;
import lombok.Data;

import java.util.List;

@Data
public class FlowableSendMessageRequest {
    private String name;

    private List<Long> assigneeList;

    private String auditUrl;

    private String robotCode;

    private List<FormMessageMarkdown> markdownList;
}
