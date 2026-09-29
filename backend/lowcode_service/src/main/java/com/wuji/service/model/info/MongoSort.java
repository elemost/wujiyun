package com.wuji.service.model.info;

import lombok.Data;

import java.util.List;

@Data
public class MongoSort {
    private String fieldId;

    private String fieldType;

    private String sortType;

    private List<Object> values;

    private String tag;
}
