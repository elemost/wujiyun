package com.wuji.service.model.request;

import lombok.Data;

@Data
public class ApplicationShareCreateRequest {
    private String applicationId;

    private Integer expireDay;

    private Boolean needData;
}
