package com.wuji.service.model.info.stream;

import com.wuji.service.enums.DataStreamCalculateFnEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class DataStreamCalculateNode extends DataStreamCommon {
    private String formula;

    private String outputType;

    // formula summary
    private String calculateType;

    /**
     * @see DataStreamCalculateFnEnum
     */
    private String fn;

    private DataStreamQuoteField quoteField;

    private Map<String, DataStreamQuoteField> valMap;

}
