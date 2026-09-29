package com.wuji.service.model.vo.form;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FormSubmitCheck {
    private List<MongoSameNameVO> sameNameList = new ArrayList<>();

    private String uuid;
}
