package com.wuji.service.model.request;

import lombok.Data;

@Data
public class ApplicationOwnerRequest {
    private String applicationName;

    private Integer limitCount;
}
