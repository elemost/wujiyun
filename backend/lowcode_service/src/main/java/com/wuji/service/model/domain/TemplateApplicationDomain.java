package com.wuji.service.model.domain;

import lombok.Data;

@Data
public class TemplateApplicationDomain {
    private String logo;

    private String id;

    private String applicationName;

    private String introduce;

    private String description;

    private Integer downloadCount;

    private String icon;
}
