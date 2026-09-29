package com.wuji.service.model.info.stream;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataStreamStartNode extends DataStreamCommon {
    private Boolean start = Boolean.TRUE;
}
