package com.wuji.factory.model.domain;

import com.wuji.service.model.info.MongoSort;
import com.wuji.service.model.info.stream.DataStreamQuoteField;
import com.wuji.service.model.request.factory.DataFactoryStageGroupFieldRequest;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataFactoryField {
    private String joinFieldType;

    private String aliasName;

    private String name;

    private String fieldType;

    private String originLabel;

    private String label;

    private String subFormLabel;

    private String groupType;

    private String relFieldType;

    private String function;

    private String remark;

    private List<DataStreamQuoteField> quoteFields = new ArrayList<>();

    private List<MongoSort> sorts = new ArrayList<>();

    private List<DataFactoryStageGroupFieldRequest> groupFields = new ArrayList<>();

    private DataFactoryStageGroupFieldRequest metric;

    private Integer sort;
}
