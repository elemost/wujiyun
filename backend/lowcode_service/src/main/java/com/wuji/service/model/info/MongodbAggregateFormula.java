package com.wuji.service.model.info;

import com.wuji.service.model.info.stream.DataStreamQuoteField;
import lombok.Data;

import java.util.List;

@Data
public class MongodbAggregateFormula {
    private String formId;

    private String formula;

    private String group;

    private String name;

    private String title;

    private String type;

    private String remark;

    private String subForm;

    private Boolean advancedFormula = Boolean.FALSE;

    private List<DataStreamQuoteField> quotes;
}
