package com.wuji.service.model.info.stream;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataStreamCreateNode extends DataStreamCommon {
    private String formId;

    private String applicationId;

    private List<DataStreamFieldTrans> fieldTrans;

    private List<DataStreamSubFormFilter> subFormFilterList = new ArrayList<>();
}
