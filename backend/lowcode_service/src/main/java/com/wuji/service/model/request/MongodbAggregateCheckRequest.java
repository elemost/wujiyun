package com.wuji.service.model.request;

import com.wuji.service.model.info.MongodbSearchField;
import com.wuji.service.model.info.stream.DataStreamQuoteField;
import lombok.Data;

import java.util.List;

@Data
public class MongodbAggregateCheckRequest {
    private List<MongodbSearchField> fields;

    private String formula;

    private String applicationId;

    private String formId;

    private List<DataStreamQuoteField> quotes;
}
