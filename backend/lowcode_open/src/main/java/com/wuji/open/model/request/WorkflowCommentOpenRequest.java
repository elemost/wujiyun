package com.wuji.open.model.request;

import lombok.Data;

@Data
public class WorkflowCommentOpenRequest {
    private String applicationId;

    private String formId;

    private String uuid;
}
