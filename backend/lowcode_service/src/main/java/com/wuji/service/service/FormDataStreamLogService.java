package com.wuji.service.service;

import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.service.model.request.FormDataStreamLogRequest;
import com.wuji.service.model.vo.DataStreamTriggerLogStageVO;
import com.wuji.service.model.vo.DataStreamTriggerLogVO;

import java.util.List;

public interface FormDataStreamLogService {
    QueryPageVO<DataStreamTriggerLogVO> queryList(FormDataStreamLogRequest formDataStreamLogRequest);

    List<DataStreamTriggerLogStageVO> getStageByLogUuid(String uuid);

    void tryAgain(String uuid);
}
