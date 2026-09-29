package com.wuji.service.model.request;

import lombok.Data;

import java.util.List;

@Data
public class FormRuleSortRequest {
    private String applicationId;

    private String formId;

    private List<String> idList;

}
