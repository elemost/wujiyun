package com.wuji.service.service.stream;

import com.alibaba.fastjson.JSONArray;
import com.wuji.service.context.DataStreamContext;
import com.wuji.service.enums.DataStreamNodeTypeEnum;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamCommon;
import com.wuji.service.model.info.stream.DataStreamCycleNode;
import com.wuji.service.model.info.stream.DataStreamQuoteField;
import com.wuji.service.service.FormDataStreamExecuteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class FormDataStreamCycleExecuteImpl extends FormDataStreamCommonExecuteImpl
        implements FormDataStreamExecuteService {

    @Autowired
    private DataStreamContext dataStreamContext;

    @Override
    public String nodeType() {
        return DataStreamNodeTypeEnum.CYCLE.getNodeType();
    }

    @Override
    public void execute(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                        Map<Long, DataStreamCalculateVO> nodeIdMap) {
        DataStreamCycleNode dataStreamCycleNode = (DataStreamCycleNode) dataStreamCommon;
        if ("SPECIFIED_FIELD".equals(dataStreamCycleNode.getCycleMode())) {
            DataStreamQuoteField cycleField = dataStreamCycleNode.getCycleField();
            DataStreamCalculateVO cycleDataStreamCalculate = nodeIdMap.get(cycleField.getNodeId());
            JSONArray jsonArray = cycleDataStreamCalculate.getJsonValue().getJSONArray(cycleField.getQuoteSubForm());
            if (jsonArray == null) {
                insertLog(formDataStreamTrigger, dataStreamCommon, null, null, null);
                super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
                return;
            }
            for (int i = 0; i < jsonArray.size(); i++) {
                DataStreamCalculateVO dataStreamCalculateVO = new DataStreamCalculateVO();
                dataStreamCalculateVO.setNodeId(dataStreamCycleNode.getNodeId());
                dataStreamCalculateVO.setNodeType("one");
                dataStreamCalculateVO.setFormId(cycleDataStreamCalculate.getFormId());
                dataStreamCalculateVO.setJsonValue(jsonArray.getJSONObject(i));
                nodeIdMap.put(dataStreamCalculateVO.getNodeId(), dataStreamCalculateVO);
                formDataStreamTrigger.setCycleIndex(i);
                formDataStreamTrigger.setCycleId(dataStreamCycleNode.getNodeId());
                formDataStreamTrigger.setCycleKey(dataStreamCycleNode.getCycleField().getQuoteFieldId());
                try {
                    super.execute(formDataStreamTrigger, dataStreamCycleNode.getCycleNode(), nodeIdMap);
                } catch (Exception e) {
                    log.error("循环容器执行失败", e);
                    if ("END_CONTINUE".equals(dataStreamCycleNode.getErrorDone())) {
                        insertLog(formDataStreamTrigger, dataStreamCommon, e.getMessage(), null, null);
                        super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
                        return;
                    } else if ("END".equals(dataStreamCycleNode.getErrorDone())) {
                        insertLog(formDataStreamTrigger, dataStreamCommon, e.getMessage(), null, null);
                        throw e;
                    }
                }
            }
        } else if ("END_NODE".equals(dataStreamCycleNode.getCycleMode())) {
            formDataStreamTrigger.setEndNode(Boolean.FALSE);
            Integer maxTime = dataStreamCycleNode.getMaxTime();
            for (int i = 0; i < maxTime; i++) {
                formDataStreamTrigger.setCycleIndex(i);
                try {
                    super.execute(formDataStreamTrigger, ((DataStreamCycleNode) dataStreamCommon).getCycleNode(),
                            nodeIdMap);
                } catch (Exception e) {
                    log.error("循环容器执行失败", e);
                    if ("END_CONTINUE".equals(dataStreamCycleNode.getErrorDone())) {
                        insertLog(formDataStreamTrigger, dataStreamCommon, e.getMessage(), null, null);
                        super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
                        return;
                    } else if ("END".equals(dataStreamCycleNode.getErrorDone())) {
                        insertLog(formDataStreamTrigger, dataStreamCommon, e.getMessage(), null, null);
                        throw e;
                    }
                }
                if (formDataStreamTrigger.getEndNode()) {
                    break;
                }
            }
        }
        // 循环结束将这两个参数设置为null，防止污染后面智能助手
        formDataStreamTrigger.setCycleIndex(null);
        formDataStreamTrigger.setEndNode(null);
        formDataStreamTrigger.setCycleId(null);
        formDataStreamTrigger.setCycleKey(null);
        insertLog(formDataStreamTrigger, dataStreamCommon, null, null, null);
        super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
    }

    @Override
    public void useTemplate(DataStreamCommon dataStreamCommon, String applicationId, String sourceApplicationId) {
        DataStreamCycleNode dataStreamCycleNode = (DataStreamCycleNode) dataStreamCommon;
        dataStreamContext.getHandler(dataStreamCycleNode.getCycleNode().getType())
                .useTemplate(dataStreamCycleNode.getCycleNode(), applicationId, sourceApplicationId);
        super.useTemplate(dataStreamCommon, applicationId, sourceApplicationId);
    }
}
