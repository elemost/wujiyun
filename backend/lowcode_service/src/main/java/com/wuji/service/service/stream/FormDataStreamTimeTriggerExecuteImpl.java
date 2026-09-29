package com.wuji.service.service.stream;

import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormUser;
import com.wuji.quartz.service.JobService;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.model.info.FormConfigEncryptKey;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.MongoFieldRelate;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamCommon;
import com.wuji.service.model.info.stream.DataStreamTimeTriggerNode;
import com.wuji.service.service.FormDataStreamExecuteService;
import com.wuji.service.utils.MongoSearchUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FormDataStreamTimeTriggerExecuteImpl extends FormDataStreamCommonExecuteImpl
        implements FormDataStreamExecuteService {

    @Autowired
    private JobService jobService;

    @Override
    public String nodeType() {
        return "time_trigger";
    }

    @Override
    public void execute(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                        Map<Long, DataStreamCalculateVO> nodeIdMap) {
        DataStreamTimeTriggerNode dataStreamTriggerNode = (DataStreamTimeTriggerNode) dataStreamCommon;
        DataStreamCalculateVO dataStreamCalculateVO = new DataStreamCalculateVO();
        dataStreamCalculateVO.setNodeId(dataStreamCommon.getNodeId());
        dataStreamCalculateVO.setNodeType(dataStreamCommon.getType());
        dataStreamCalculateVO.setJsonValue(formDataStreamTrigger.getJsonObject());
        dataStreamCalculateVO.setFormId(formDataStreamTrigger.getDataStreamTrigger().getFormId());
        nodeIdMap.put(dataStreamCalculateVO.getNodeId(), dataStreamCalculateVO);
        Map<String, FormConfigEncryptKey> encryptKeyMap = formDataStreamTrigger.getEncryptKeyMap()
                .getOrDefault(formDataStreamTrigger.getDataStreamTrigger().getFormId(), new HashMap<>());
        if (dataStreamTriggerNode.getCondition() == null) {
            formDataStreamTrigger.getDataStreamTrigger().setResult("trigger");
            insertLog(formDataStreamTrigger, dataStreamCommon);
            super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
            return;
        }
        MongodbSearchFilter mongodbSearchFilter =
                MongoSearchUtils.toFilter(dataStreamTriggerNode.getCondition(), nodeIdMap, encryptKeyMap);
        Boolean trigger = MongoSearchUtils.checkData(mongodbSearchFilter, formDataStreamTrigger.getJsonObject());
        if (trigger) {
            formDataStreamTrigger.getDataStreamTrigger().setResult("trigger");
            insertLog(formDataStreamTrigger, dataStreamCommon);
            super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
        }
    }

    @Override
    public void useTemplate(DataStreamCommon dataStreamCommon, String applicationId, String sourceApplicationId) {
        DataStreamTimeTriggerNode dataStreamTriggerNode = (DataStreamTimeTriggerNode) dataStreamCommon;
        if (dataStreamTriggerNode.getCondition() != null) {
            List<MongoFieldRelate> conditionList = dataStreamTriggerNode.getCondition().getRelates();
            if (CollectionUtils.isNotEmpty(conditionList)) {
                for (MongoFieldRelate mongodbSearchCondition : conditionList) {
                    if (FormFieldTypeEnum.getUserFieldType().contains(mongodbSearchCondition.getFieldType()) ||
                            mongodbSearchCondition.getFieldId().equals(FormSystemFieldEnum.CREATE_NAME.getName())) {
                        List<Object> value = new ArrayList<>(FormUser.getDefaultUserObject());
                        mongodbSearchCondition.setValue(value);
                    } else if (FormFieldTypeEnum.getDeptFieldType().contains(mongodbSearchCondition.getFieldType())) {
                        List<Object> value = new ArrayList<>(FormDept.getDefaultDeptObject());
                        mongodbSearchCondition.setValue(value);
                    }
                }
            }
        }
        super.useTemplate(dataStreamCommon, applicationId, sourceApplicationId);
    }

    public void insertLog(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon) {
        insertLog(formDataStreamTrigger, dataStreamCommon,
                Collections.singletonList(formDataStreamTrigger.getDataStreamTrigger().getTitle()), null, null);
    }
}
