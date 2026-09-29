package com.wuji.service.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class FormDataStreamFromVO {
    private String formId;

    private String formName;

    private List<FormDataStreamVO> formDataStreamList;
}
