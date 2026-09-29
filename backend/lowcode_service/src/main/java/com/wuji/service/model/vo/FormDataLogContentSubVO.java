package com.wuji.service.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class FormDataLogContentSubVO {
    private String operate;

    private String id;

    private List<FormDataLogContentVO> formDataLogContentList;
}
