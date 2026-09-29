package com.wuji.service.model.request;

import lombok.Data;

import java.util.List;

@Data
public class RelevanceRelationRequest {
    private String formId;

    private String applicationId;

    private List<String> businessIdList;
}
