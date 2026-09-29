package com.wuji.service.model.info;

import lombok.Data;

import java.util.List;

@Data
public class FormAggregateConfig {
    private FormAggregateTable formAggregateTable;

    private List<FormAggregateValidator> validators;
}
