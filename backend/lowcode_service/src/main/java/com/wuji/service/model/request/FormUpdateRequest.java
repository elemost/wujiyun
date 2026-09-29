package com.wuji.service.model.request;

import lombok.Data;

import java.util.List;

@Data
public class FormUpdateRequest {
    private String id;

    private String config;

    private List<String> moduleIdList;

    private String applicationId;

    private String formConfig;

    private String sourceId;

    private String formType;
}
