package com.wuji.service.service.impl;

import com.wuji.common.trans.MultiTransactional;
import com.wuji.common.utils.ToolSpring;
import com.wuji.service.context.DataStreamContext;
import com.wuji.service.model.domain.DataStreamTriggerLogStageDomain;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamCommon;
import com.wuji.service.service.DataStreamTriggerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class FormDataStreamTriggerServiceImpl implements DataStreamTriggerService {

    private DataStreamContext dataStreamContext;

    @Autowired
    private MongoTemplate mongoTemplate;

    @MultiTransactional(value = {"mybatisTransactionManager", "mongoTransactionManager"})
    @Override
    public void trigger(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                        Map<Long, DataStreamCalculateVO> nodeIdMap) {
        if (dataStreamContext == null) {
            dataStreamContext = ToolSpring.getBean(DataStreamContext.class);
        }
        dataStreamContext.getHandler(dataStreamCommon.getType())
                .execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
    }

    @Override
    @Async
    public void insertLog(DataStreamTriggerLogStageDomain dataStreamTriggerLogStageDomain) {
        mongoTemplate.insert(dataStreamTriggerLogStageDomain);
    }
}
