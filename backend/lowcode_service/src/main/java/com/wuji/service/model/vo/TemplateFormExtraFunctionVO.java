package com.wuji.service.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class TemplateFormExtraFunctionVO {
    private List<TemplateFormExtraFunctionRelationVO> formExtraFunctionRelationList;

    private String applicationId;

    private String formId;

    private String config;

    private String id;

    private String functionType;

    private Integer sort;
}
