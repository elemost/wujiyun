package com.wuji.service.model.request;

import lombok.Data;

@Data
public class FormAggregateUpdateRequest {
    private String id;

    private String name;

    private String config;

    private String applicationId;
}
