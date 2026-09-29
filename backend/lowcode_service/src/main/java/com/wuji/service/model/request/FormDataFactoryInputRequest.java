package com.wuji.service.model.request;

import lombok.Data;

@Data
public class FormDataFactoryInputRequest {
    private String dataFactoryId;

    private String applicationId;

    private String inputFormId;

    private String inputApplicationId;

    private Integer version;
}
