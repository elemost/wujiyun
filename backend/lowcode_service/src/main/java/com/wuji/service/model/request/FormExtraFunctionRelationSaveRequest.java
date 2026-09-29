package com.wuji.service.model.request;

import lombok.Data;

import java.util.List;

@Data
public class FormExtraFunctionRelationSaveRequest {
    private String functionId;

    private String businessType;

    private List<String> businessIdList;
}
