package com.wuji.workflow.model.domain;

import lombok.Data;

@Data
public class ModelMetaInfoDomain {

    /**
     * 创建者（username）
     */
    private String createUser;

    /**
     * 流程描述
     */
    private String description;
    /**
     * 表单类型
     */
    private Integer formType;
    /**
     * 表单编号
     */
    private Long formId;

    private String name;



}
