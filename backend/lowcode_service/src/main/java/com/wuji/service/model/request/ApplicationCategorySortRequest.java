package com.wuji.service.model.request;

import lombok.Data;

@Data
public class ApplicationCategorySortRequest {
    private String previousId;

    private String nextId;

    private String sortId;

    private String applicationId;

    private String parentId;
}
