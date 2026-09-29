package com.wuji.service.model.request;

import lombok.Data;

import java.util.List;

@Data
public class FormMongoDbDeleteRequest {
    private List<String> uuidList;

    private String formId;

    private String applicationId;
}
