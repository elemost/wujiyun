package com.wuji.service.service.stream;


import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamCommon;
import com.wuji.service.service.FormDataStreamExecuteService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class DataStreamButtonTriggerExecuteImpl extends FormDataStreamCommonExecuteImpl
        implements FormDataStreamExecuteService {

    @Override
    public String nodeType() {
        return "button_trigger";
    }

    @Override
    public void execute(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                        Map<Long, DataStreamCalculateVO> nodeIdMap) {
        DataStreamCalculateVO dataStreamCalculateVO = new DataStreamCalculateVO();
        dataStreamCalculateVO.setNodeId(dataStreamCommon.getNodeId());
        dataStreamCalculateVO.setNodeType(dataStreamCommon.getType());
        dataStreamCalculateVO.setJsonValue(formDataStreamTrigger.getJsonObject());
        dataStreamCalculateVO.setFormId(formDataStreamTrigger.getDataStreamTrigger().getFormId());
        nodeIdMap.put(dataStreamCalculateVO.getNodeId(), dataStreamCalculateVO);
        insertLog(formDataStreamTrigger, dataStreamCommon,
                Collections.singletonList(formDataStreamTrigger.getDataStreamTrigger().getTitle()), null, null);
        super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
    }
}