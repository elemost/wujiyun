package com.wuji.service.model.info;

import lombok.Data;

import java.util.List;

@Data
public class FormExtraFunctionTemplate {
    private String fileId;

    private String fileName;

    private String templateName;

    private String fileType;

    private String fileUrl;

    private List<FormInfoRelation> relations;
}
