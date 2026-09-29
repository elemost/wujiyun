package com.wuji.service.model.info.stream;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataStreamUpdateNode extends DataStreamCommon {
    private DataStreamConditionRel condition;

    private DataStreamConditionRel matchRule;

    private List<DataStreamSubFormFilter> subFormFilterList = new ArrayList<>();

    private List<DataStreamFieldTrans> updateFieldTrans;

    private List<DataStreamFieldTrans> createFieldTrans;

    // object form
    private String updateObject;

    private String updateObjectId;

    private Boolean createWhileNull = Boolean.FALSE;
}
