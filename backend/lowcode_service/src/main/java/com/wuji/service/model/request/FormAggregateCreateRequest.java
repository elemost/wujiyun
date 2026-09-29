package com.wuji.service.model.request;

import com.wuji.common.privilege.annotation.ApplicationId;
import lombok.Data;

@Data
public class FormAggregateCreateRequest {

    private String name;

    private String config;

    @ApplicationId
    private String applicationId;
}
