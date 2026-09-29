package com.wuji.service.model.info;

import com.wuji.service.model.info.stream.DataStreamConditionRel;
import lombok.Data;

@Data
public class FormInfoRelation {

    private String formId;

    private DataStreamConditionRel condition;

}
