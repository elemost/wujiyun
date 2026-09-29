package com.wuji.service.service;

import com.wuji.service.model.domain.DataStreamTriggerLogStageDomain;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamCommon;

import java.util.Map;

public interface DataStreamTriggerService {

    void trigger(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                 Map<Long, DataStreamCalculateVO> nodeIdMap);

    void insertLog(DataStreamTriggerLogStageDomain dataStreamTriggerLogStageDomain);
}
