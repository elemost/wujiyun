package com.wuji.service.model.info.stream;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataStreamCycleNode extends DataStreamCommon {

    // SPECIFIED_FIELD END_NODE
    private String cycleMode;

    private Integer maxTime;

    // SKIP_NEXT END_CONTINUE END
    private String errorDone;

    private DataStreamQuoteField cycleField;

    private DataStreamCommon cycleNode;

}
