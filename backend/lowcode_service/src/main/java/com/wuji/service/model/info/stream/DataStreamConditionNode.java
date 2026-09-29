package com.wuji.service.model.info.stream;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataStreamConditionNode extends DataStreamCommon {
    private DataStreamConditionRel condition;

    private Boolean def;
}
