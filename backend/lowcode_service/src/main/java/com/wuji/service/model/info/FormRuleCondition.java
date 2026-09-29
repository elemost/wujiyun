package com.wuji.service.model.info;

import com.wuji.service.model.info.stream.DataStreamConditionRel;
import com.wuji.service.model.info.stream.DataStreamQuoteField;
import lombok.Data;

import java.util.List;

@Data
public class FormRuleCondition {
    private String id;

    private String fieldId;

    private String fieldSubForm;

    private String fieldType;

    //CUSTOM NODE_FIELD FORM_DATA DATA_STREAM
    private String quoteType;

    private String method;

    private List<Object> value;

    private String quoteFormId;

    private DataStreamQuoteField quoteField;

    // 数据搜索条件
    private DataStreamConditionRel filter;

    // 子表单匹配条件
    private DataStreamConditionRel subformFilter;

}
