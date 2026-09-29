package com.wuji.service.service;

import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamCommon;

import java.util.Map;

public interface DataStreamConditionService extends FormDataStreamExecuteService {
    Boolean conditionExecute(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                             Map<Long, DataStreamCalculateVO> nodeIdMap);
}
