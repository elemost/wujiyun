package com.wuji.service.service.stream;

import com.wuji.service.enums.DataStreamNodeTypeEnum;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamCommon;
import com.wuji.service.service.FormDataStreamExecuteService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DataStreamEndServiceImpl extends FormDataStreamCommonExecuteImpl implements FormDataStreamExecuteService {

    @Override
    public String nodeType() {
        return DataStreamNodeTypeEnum.END.getNodeType();
    }

    @Override
    public void execute(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                        Map<Long, DataStreamCalculateVO> nodeIdMap) {
        formDataStreamTrigger.setEndNode(Boolean.TRUE);
    }
}
