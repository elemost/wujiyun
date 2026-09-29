package com.wuji.service.model.info.stream;

import com.wuji.service.model.info.MongoSort;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataStreamQueryOneNode extends DataStreamCommon {
    private DataStreamConditionRel matchRule;

    private List<MongoSort> sorts;

    private String formId;
}
