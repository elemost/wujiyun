package com.wuji.service.service.stream;

import com.alibaba.fastjson.JSONObject;
import com.wuji.service.enums.DataStreamNodeTypeEnum;
import com.wuji.service.model.info.FormConfigEncryptKey;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.MongoFieldRelate;
import com.wuji.service.model.info.MongodbSearchCondition;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamCommon;
import com.wuji.service.model.info.stream.DataStreamConditionNode;
import com.wuji.service.model.info.stream.DataStreamConditionRel;
import com.wuji.service.service.DataStreamConditionService;
import com.wuji.service.utils.MongoDbDataTransUtils;
import com.wuji.service.utils.MongoSearchUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class DataStreamConditionServiceImpl extends FormDataStreamCommonExecuteImpl
        implements DataStreamConditionService {

    @Override
    public String nodeType() {
        return "condition";
    }

    @Override
    public Boolean conditionExecute(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                                    Map<Long, DataStreamCalculateVO> nodeIdMap) {
        DataStreamConditionNode dataStreamConditionNode = (DataStreamConditionNode) dataStreamCommon;
        DataStreamConditionRel condition = dataStreamConditionNode.getCondition();
        boolean executeResult = false;
        Boolean needExecute = MongoDbDataTransUtils.checkWhileNull(nodeIdMap, condition.getRelates());
        if (!needExecute) {
            return Boolean.FALSE;
        }
        for (MongoFieldRelate mongoFieldRelate : condition.getRelates()) {
            DataStreamCalculateVO dataStreamCalculateVO = nodeIdMap.get(mongoFieldRelate.getNodeId());
            Map<String, FormConfigEncryptKey> encryptKeyMap =
                    formDataStreamTrigger.getEncryptKeyMap().get(dataStreamCalculateVO.getFormId());
            List<MongodbSearchCondition> mongodbSearchConditions =
                    MongoSearchUtils.buildConditionList(Collections.singletonList(mongoFieldRelate), nodeIdMap,
                            encryptKeyMap);
            MongodbSearchCondition mongodbSearchCondition = mongodbSearchConditions.get(0);
            JSONObject jsonObject = new JSONObject();
            if (DataStreamNodeTypeEnum.CALCULATE.getNodeType().equalsIgnoreCase(dataStreamCalculateVO.getNodeType())) {
                jsonObject.put("key", dataStreamCalculateVO.getValue());
                mongodbSearchCondition.setFieldId("key");
                mongodbSearchCondition.setType("key");
            } else {
                jsonObject = dataStreamCalculateVO.getJsonValue();
            }
            executeResult = MongoSearchUtils.checkData(mongodbSearchCondition, jsonObject);
            if ("AND".equalsIgnoreCase(condition.getRel())) {
                if (!executeResult) {
                    break;
                }
            } else {
                if (executeResult) {
                    break;
                }
            }
        }
        if (executeResult) {
            this.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
        }
        return executeResult;
    }

    @Override
    public void execute(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                        Map<Long, DataStreamCalculateVO> nodeIdMap) {
        insertLog(formDataStreamTrigger, dataStreamCommon, null, null, null);
        super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
    }

    @Override
    public void useTemplate(DataStreamCommon dataStreamCommon, String applicationId, String sourceApplicationId) {
        DataStreamConditionNode dataStreamConditionNode = (DataStreamConditionNode) dataStreamCommon;
        if (dataStreamConditionNode.getCondition() != null) {
            DataStreamConditionRel condition = dataStreamConditionNode.getCondition();
            buildFieldRelate(condition.getRelates());
        }
        super.useTemplate(dataStreamCommon, applicationId, sourceApplicationId);
    }
}
