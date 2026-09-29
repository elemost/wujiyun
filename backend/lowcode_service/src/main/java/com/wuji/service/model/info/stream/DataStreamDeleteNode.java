package com.wuji.service.model.info.stream;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataStreamDeleteNode extends DataStreamCommon {

    // node form
    private String deleteObject;

    private String deleteObjectId;

    private DataStreamConditionRel matchRule;
}
