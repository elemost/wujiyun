package com.wuji.service.service.stream;

import com.wuji.service.context.DataStreamContext;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamCommon;
import com.wuji.service.model.info.stream.DataStreamConditionNode;
import com.wuji.service.model.info.stream.DataStreamGatewayNode;
import com.wuji.service.service.DataStreamConditionService;
import com.wuji.service.service.FormDataStreamExecuteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DataStreamGatewayServiceImpl extends FormDataStreamCommonExecuteImpl
        implements FormDataStreamExecuteService {

    @Autowired
    private DataStreamConditionService dataStreamConditionService;

    @Autowired
    private DataStreamContext dataStreamContext;

    @Override
    public String nodeType() {
        return "gateway";
    }

    @Override
    public void execute(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                        Map<Long, DataStreamCalculateVO> nodeIdMap) {
        DataStreamGatewayNode dataStreamGatewayNode = (DataStreamGatewayNode) dataStreamCommon;
        insertLog(formDataStreamTrigger, dataStreamCommon, null, null, null);
        List<DataStreamConditionNode> children = dataStreamGatewayNode.getChildren();
        Boolean existExecute = Boolean.FALSE;
        for (DataStreamConditionNode dataStreamConditionNode : children) {
            if (dataStreamConditionNode.getDef() != null && dataStreamConditionNode.getDef()) {
                continue;
            }
            Boolean execute =
                    dataStreamConditionService.conditionExecute(formDataStreamTrigger, dataStreamConditionNode,
                            nodeIdMap);
            if (!existExecute) {
                existExecute = execute;
            }
            if (execute) {
                if ("match_one".equals(dataStreamGatewayNode.getTriggerType())) {
                    break;
                }
            }
        }
        if (!existExecute) {
            DataStreamConditionNode dataStreamConditionNode =
                    children.stream().filter(DataStreamConditionNode::getDef).findFirst().orElse(null);
            dataStreamConditionService.execute(formDataStreamTrigger, dataStreamConditionNode, nodeIdMap);
        }
        super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
    }

    @Override
    public void useTemplate(DataStreamCommon dataStreamCommon, String applicationId, String sourceApplicationId) {
        DataStreamGatewayNode dataStreamGatewayNode = (DataStreamGatewayNode) dataStreamCommon;
        List<DataStreamConditionNode> children = dataStreamGatewayNode.getChildren();
        for (DataStreamConditionNode dataStreamConditionNode : children) {
            dataStreamContext.getHandler(dataStreamConditionNode.getType())
                    .useTemplate(dataStreamConditionNode, applicationId, sourceApplicationId);
        }
        super.useTemplate(dataStreamCommon, applicationId, sourceApplicationId);
    }
}
