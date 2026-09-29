package com.wuji.service.model.info.stream;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataStreamDemoNode extends DataStreamCommon {

    private List<DataStreamFieldTrans> fieldTrans;

}
