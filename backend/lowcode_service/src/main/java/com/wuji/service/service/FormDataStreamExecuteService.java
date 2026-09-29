package com.wuji.service.service;

import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamCommon;

import java.util.Map;

public interface FormDataStreamExecuteService {
    String nodeType();

    void execute(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                 Map<Long, DataStreamCalculateVO> nodeIdMap);

    void useTemplate(DataStreamCommon dataStreamCommon, String applicationId, String sourceApplicationId);

}
