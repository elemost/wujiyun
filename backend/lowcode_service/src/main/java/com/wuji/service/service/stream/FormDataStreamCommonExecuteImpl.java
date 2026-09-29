package com.wuji.service.service.stream;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.info.FormDept;
import com.wuji.common.model.info.FormUser;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.context.DataStreamContext;
import com.wuji.service.enums.DataStreamFieldQuoteTypeEnum;
import com.wuji.service.enums.DataStreamNodeTypeEnum;
import com.wuji.service.model.domain.DataStreamTriggerLogStageDomain;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.FormDataTitle;
import com.wuji.service.model.info.MongoFieldRelate;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamCommon;
import com.wuji.service.model.info.stream.DataStreamCreateNode;
import com.wuji.service.model.info.stream.DataStreamDeleteNode;
import com.wuji.service.model.info.stream.DataStreamFieldTrans;
import com.wuji.service.model.info.stream.DataStreamQueryMoreNode;
import com.wuji.service.model.info.stream.DataStreamQueryOneNode;
import com.wuji.service.model.info.stream.DataStreamUpdateNode;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.service.DataStreamTriggerService;
import com.wuji.service.service.FormDataStreamExecuteService;
import com.wuji.service.service.FormExtraFunctionTitleService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FormDataStreamCommonExecuteImpl implements FormDataStreamExecuteService {

    @Autowired
    private DataStreamContext dataStreamContext;

    @Autowired
    private DataStreamTriggerService dataStreamTriggerService;

    @Autowired
    private FormExtraFunctionTitleService formExtraFunctionTitleService;

    @Override
    public String nodeType() {
        return "common";
    }

    @Override
    public void execute(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                        Map<Long, DataStreamCalculateVO> nodeIdMap) {
        // 执行下一个节点
        DataStreamCommon child = dataStreamCommon.getChild();
        if (child == null) {
            return;
        }
        dataStreamContext.getHandler(child.getType()).execute(formDataStreamTrigger, child, nodeIdMap);
    }

    @Override
    public void useTemplate(DataStreamCommon dataStreamCommon, String applicationId, String sourceApplicationId) {
        if (StringUtils.isNotEmpty(dataStreamCommon.getApplicationId()) &&
                dataStreamCommon.getApplicationId().equals(sourceApplicationId)) {
            dataStreamCommon.setApplicationId(applicationId);
        }
        DataStreamCommon child = dataStreamCommon.getChild();
        if (child == null) {
            return;
        }
        dataStreamContext.getHandler(child.getType()).useTemplate(child, applicationId, sourceApplicationId);
    }

    public void transUserAndDept(List<DataStreamFieldTrans> fieldTransList) {
        for (DataStreamFieldTrans dataStreamFieldTrans : fieldTransList) {
            if (dataStreamFieldTrans.getQuoteType().equalsIgnoreCase(DataStreamFieldQuoteTypeEnum.CUSTOM.name())) {
                if (FormFieldTypeEnum.checkUserField(dataStreamFieldTrans.getFieldType(),
                        dataStreamFieldTrans.getFieldId())) {
                    dataStreamFieldTrans.setCustomValue(FormUser.getDefaultUser());
                } else if (FormFieldTypeEnum.checkDeptField(dataStreamFieldTrans.getFieldType(),
                        dataStreamFieldTrans.getFieldId())) {
                    dataStreamFieldTrans.setCustomValue(FormDept.getDefaultDept());
                }
            }
        }
    }

    public void buildFieldRelate(List<MongoFieldRelate> mongoFieldRelateList) {
        if (CollectionUtils.isNotEmpty(mongoFieldRelateList)) {
            for (MongoFieldRelate mongoFieldRelate : mongoFieldRelateList) {
                if (DataStreamFieldQuoteTypeEnum.CUSTOM.name().equalsIgnoreCase(mongoFieldRelate.getMode())) {
                    if (FormFieldTypeEnum.checkUserField(mongoFieldRelate.getFieldType(),
                            mongoFieldRelate.getFieldId())) {
                        mongoFieldRelate.setValue(FormUser.getDefaultUserObject());
                    } else if (FormFieldTypeEnum.checkDeptField(mongoFieldRelate.getFieldType(),
                            mongoFieldRelate.getFieldId())) {
                        mongoFieldRelate.setValue(FormDept.getDefaultDeptObject());
                    }
                }
            }
        }
    }

    public void insertLog(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                          Object nodeResult, FormVO formVO, String dealType) {
        DataStreamTriggerLogStageDomain dataStreamTriggerLogStageDomain =
                buildAndInsertLog(formDataStreamTrigger, dataStreamCommon);
        if (DataStreamNodeTypeEnum.formNodeList().contains(dataStreamCommon.getType())) {
            if ("insert_update".equals(dealType)) {
                JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(nodeResult));
                List<FormDataTitle> insert =
                        getFormDataTitles(jsonObject.getString("insert"), formVO, formDataStreamTrigger);
                dataStreamTriggerLogStageDomain.setInsertList(insert);
                List<FormDataTitle> update =
                        getFormDataTitles(jsonObject.getString("update"), formVO, formDataStreamTrigger);
                dataStreamTriggerLogStageDomain.setUpdateList(update);
            } else {
                List<FormDataTitle> formDataTitleList =
                        getFormDataTitles(JSONObject.toJSONString(nodeResult), formVO, formDataStreamTrigger);
                setFormId(formDataStreamTrigger, dataStreamCommon, dataStreamTriggerLogStageDomain);
                if ("insert".equals(dealType)) {
                    dataStreamTriggerLogStageDomain.setInsertList(formDataTitleList);
                } else if ("update".equals(dealType)) {
                    dataStreamTriggerLogStageDomain.setUpdateList(formDataTitleList);
                } else if ("query".equals(dealType)) {
                    dataStreamTriggerLogStageDomain.setResultList(formDataTitleList);
                } else if ("delete".equals(dealType)) {
                    dataStreamTriggerLogStageDomain.setDeleteList(formDataTitleList);
                }
            }
        } else {
            if (DataStreamNodeTypeEnum.CALCULATE.getNodeType().equals(dataStreamCommon.getType())) {
                if ("success".equals(dealType)) {
                    dataStreamTriggerLogStageDomain.setCalculateResult(nodeResult);
                } else {
                    dataStreamTriggerLogStageDomain.setErrorMessage(nodeResult.toString());
                }
            } else if (DataStreamNodeTypeEnum.TIME_TRIGGER.getNodeType().equals(dataStreamCommon.getType()) ||
                    DataStreamNodeTypeEnum.TRIGGER.getNodeType().equals(dataStreamCommon.getType())) {
                dataStreamTriggerLogStageDomain.setResultList(
                        JSONObject.parseArray(JSONObject.toJSONString(nodeResult), FormDataTitle.class));
            } else if (DataStreamNodeTypeEnum.PLUGIN.getNodeType().equals(dataStreamCommon.getType())) {
                dataStreamTriggerLogStageDomain.setResult(nodeResult);
            } else if (DataStreamNodeTypeEnum.CYCLE.getNodeType().equals(dataStreamCommon.getType())) {
                if (nodeResult != null) {
                    dataStreamTriggerLogStageDomain.setErrorMessage(nodeResult.toString());
                }
            }
        }
        dataStreamTriggerService.insertLog(dataStreamTriggerLogStageDomain);
    }

    private List<FormDataTitle> getFormDataTitles(String jsonObject, FormVO formVO,
                                                  FormDataStreamTrigger formDataStreamTrigger) {
        List<LowcodeDataDomain> insertList = JSONArray.parseArray(jsonObject, LowcodeDataDomain.class);
        formExtraFunctionTitleService.buildTitle(insertList, formVO.getConfig(), formVO.getId(),
                formDataStreamTrigger.getApplicationId());
        return insertList.stream().map(info -> new FormDataTitle(info.getUuid(), info.getDataTitle()))
                .collect(Collectors.toList());
    }

    public DataStreamTriggerLogStageDomain buildAndInsertLog(FormDataStreamTrigger formDataStreamTrigger,
                                                             DataStreamCommon dataStreamCommon) {
        DataStreamTriggerLogStageDomain dataStreamTriggerLogStageDomain = new DataStreamTriggerLogStageDomain();
        dataStreamTriggerLogStageDomain.setTriggerUuid(formDataStreamTrigger.getTriggerId());
        dataStreamTriggerLogStageDomain.setNodeId(dataStreamCommon.getNodeId());
        dataStreamTriggerLogStageDomain.setCreateTime(new Date().getTime());
        dataStreamTriggerLogStageDomain.setCreateName(UserUtils.getUser().getNickName());
        dataStreamTriggerLogStageDomain.setCreator(UserUtils.getUser().getUserId());
        dataStreamTriggerLogStageDomain.setNodeType(dataStreamCommon.getType());
        dataStreamTriggerLogStageDomain.setCycleIndex(formDataStreamTrigger.getCycleIndex());
        return dataStreamTriggerLogStageDomain;
    }

    private static void setFormId(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                                  DataStreamTriggerLogStageDomain dataStreamTriggerLogStageDomain) {
        dataStreamTriggerLogStageDomain.setBusinessType("FORM");
        String applicationId =
                StringUtils.isNotEmpty(dataStreamCommon.getApplicationId()) ? dataStreamCommon.getApplicationId() :
                        formDataStreamTrigger.getApplicationId();
        if (dataStreamCommon instanceof DataStreamCreateNode) {
            DataStreamCreateNode dataStreamCreateNode = (DataStreamCreateNode) dataStreamCommon;
            dataStreamTriggerLogStageDomain.setBusinessId(dataStreamCreateNode.getFormId());
            dataStreamTriggerLogStageDomain.setApplicationId(applicationId);
        } else if (dataStreamCommon instanceof DataStreamUpdateNode) {
            DataStreamUpdateNode dataStreamUpdateNode = (DataStreamUpdateNode) dataStreamCommon;
            dataStreamTriggerLogStageDomain.setBusinessId(dataStreamUpdateNode.getUpdateObjectId());
            if (StringUtils.isNotEmpty(dataStreamUpdateNode.getUpdateObject())) {
                dataStreamTriggerLogStageDomain.setBusinessType(dataStreamUpdateNode.getUpdateObject().toLowerCase());
            }
            dataStreamTriggerLogStageDomain.setApplicationId(applicationId);
        } else if (dataStreamCommon instanceof DataStreamDeleteNode) {
            DataStreamDeleteNode dataStreamDeleteNode = (DataStreamDeleteNode) dataStreamCommon;
            dataStreamTriggerLogStageDomain.setBusinessId(dataStreamDeleteNode.getDeleteObjectId());
            if (StringUtils.isNotEmpty(dataStreamDeleteNode.getDeleteObject())) {
                dataStreamTriggerLogStageDomain.setBusinessType(dataStreamDeleteNode.getDeleteObject());
            }
            dataStreamTriggerLogStageDomain.setApplicationId(applicationId);
        } else if (dataStreamCommon instanceof DataStreamQueryMoreNode) {
            DataStreamQueryMoreNode queryMoreNode = (DataStreamQueryMoreNode) dataStreamCommon;
            dataStreamTriggerLogStageDomain.setBusinessId(queryMoreNode.getFormId());
            dataStreamTriggerLogStageDomain.setApplicationId(applicationId);
        } else if (dataStreamCommon instanceof DataStreamQueryOneNode) {
            DataStreamQueryOneNode dataStreamQueryOneNode = (DataStreamQueryOneNode) dataStreamCommon;
            dataStreamTriggerLogStageDomain.setBusinessId(dataStreamQueryOneNode.getFormId());
            dataStreamTriggerLogStageDomain.setApplicationId(applicationId);
        }
    }

}
