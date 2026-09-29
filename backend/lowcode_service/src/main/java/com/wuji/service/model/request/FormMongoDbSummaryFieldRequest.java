package com.wuji.service.model.request;

import com.wuji.service.model.info.stream.DataStreamQuoteField;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class FormMongoDbSummaryFieldRequest {
    private String fieldId;

    private String fieldType;

    private String subForm;

    private String op;

    private String formula;

    private Map<String, DataStreamQuoteField> quoteFieldMap;

    private List<String> quoteFields;
}
