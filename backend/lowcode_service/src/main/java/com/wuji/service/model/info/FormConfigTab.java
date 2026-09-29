package com.wuji.service.model.info;

import lombok.Data;

import java.util.List;

@Data
public class FormConfigTab {
    private String id;

    private String title;

    private List<FormConfigCommon> body;
}
