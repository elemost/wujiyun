package com.wuji.service.service.stream;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormUser;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.model.info.FormConfigEncryptKey;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.MongoFieldRelate;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamCommon;
import com.wuji.service.model.info.stream.DataStreamTriggerNode;
import com.wuji.service.service.FormDataStreamExecuteService;
import com.wuji.service.utils.MongoSearchUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class FormDataStreamTriggerExecuteImpl extends FormDataStreamCommonExecuteImpl
        implements FormDataStreamExecuteService {

    @Override
    public String nodeType() {
        return "trigger";
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
        boolean trigger = false;
        DataStreamTriggerNode dataStreamTriggerNode = (DataStreamTriggerNode) dataStreamCommon;
        DataStreamTriggerNode.DataStreamTriggerAction streamTriggerAction = dataStreamTriggerNode.getActions().stream()
                .filter(c -> formDataStreamTrigger.getAction().equals(c.getAction())).findFirst().orElse(null);
        if (streamTriggerAction != null) {
            if ("update".equals(formDataStreamTrigger.getAction())) {
                if ("AT_WILL".equals(streamTriggerAction.getMode())) {
                    trigger = true;
                } else if ("SPEC_FIELD_VALUE".equals(streamTriggerAction.getMode())) {
                    List<DataStreamTriggerNode.DataStreamTriggerFieldValue> triggerFieldValues =
                            streamTriggerAction.getTriggerFieldValues();
                    for (DataStreamTriggerNode.DataStreamTriggerFieldValue dataStreamTriggerFieldValue : triggerFieldValues) {
                        JSONObject jsonValue = dataStreamCalculateVO.getJsonValue();
                        Object current = jsonValue.get(dataStreamTriggerFieldValue.getFieldId());
                        Object pre = jsonValue.get(dataStreamTriggerFieldValue.getFieldId() + "_pre");
                        if (dataStreamTriggerFieldValue.getCurrentValue().equals(current) &&
                                dataStreamTriggerFieldValue.getPreValue().equals(pre)) {
                            if ("OR".equalsIgnoreCase(streamTriggerAction.getRel())) {
                                trigger = true;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                } else {
                    if (CollectionUtils.isNotEmpty(formDataStreamTrigger.getUpdateKey())) {
                        trigger = formDataStreamTrigger.getUpdateKey().stream()
                                .anyMatch(c -> streamTriggerAction.getTriggerFields().contains(c));
                    }
                }
            } else if ("activity_finish".equals(formDataStreamTrigger.getAction())) {
                if (formDataStreamTrigger.getActivityId().equals(streamTriggerAction.getActivityId()) &&
                        streamTriggerAction.getActivityActions().contains(formDataStreamTrigger.getActivityAction())) {
                    trigger = true;
                }
            } else {
                trigger = true;
            }
        }
        if (trigger) {
            if (dataStreamTriggerNode.getCondition() != null) {
                List<MongoFieldRelate> relates = dataStreamTriggerNode.getCondition().getRelates();
                if (relates != null) {
                    for (MongoFieldRelate mongoFieldRelate : relates) {
                        mongoFieldRelate.setMode("CUSTOM");
                    }
                    Map<String, FormConfigEncryptKey> encryptKeyMap = formDataStreamTrigger.getEncryptKeyMap()
                            .get(formDataStreamTrigger.getDataStreamTrigger().getFormId());
                    MongodbSearchFilter mongodbSearchFilter =
                            MongoSearchUtils.toFilter(dataStreamTriggerNode.getCondition(), nodeIdMap, encryptKeyMap);
                    trigger = MongoSearchUtils.checkData(mongodbSearchFilter, formDataStreamTrigger.getJsonObject());
                }
                if (trigger) {
                    formDataStreamTrigger.getDataStreamTrigger().setResult("trigger");
                    insertLog(formDataStreamTrigger, dataStreamCommon);
                    super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
                }
            } else {
                formDataStreamTrigger.getDataStreamTrigger().setResult("trigger");
                insertLog(formDataStreamTrigger, dataStreamCommon);
                super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
            }
        }
    }

    @Override
    public void useTemplate(DataStreamCommon dataStreamCommon, String applicationId, String sourceApplicationId) {
        DataStreamTriggerNode dataStreamTriggerNode = (DataStreamTriggerNode) dataStreamCommon;
        if (dataStreamTriggerNode.getCondition() != null) {
            List<MongoFieldRelate> conditionList = dataStreamTriggerNode.getCondition().getRelates();
            if (CollectionUtils.isNotEmpty(conditionList)) {
                for (MongoFieldRelate mongodbSearchCondition : conditionList) {
                    if (mongodbSearchCondition.getValue() == null) {
                        continue;
                    }
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
