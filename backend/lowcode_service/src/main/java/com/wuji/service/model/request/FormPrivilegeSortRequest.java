package com.wuji.service.model.request;

import lombok.Data;

import java.util.List;

@Data
public class FormPrivilegeSortRequest {
    private List<String> idList;

    private String categoryId;

    private String applicationId;
}
