package com.wuji.service.model.info.stream;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataStreamEndNode extends DataStreamCommon {
    private Boolean end;
}
