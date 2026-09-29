package com.wuji.service.service.stream;

import com.alibaba.fastjson.JSONObject;
import com.wuji.common.utils.ObjectId;
import com.wuji.service.enums.FormDataStatusEnum;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.model.domain.DataStreamCreateTransDomain;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.info.FormConfigEncryptKey;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamCommon;
import com.wuji.service.model.info.stream.DataStreamCreateNode;
import com.wuji.service.model.info.stream.DataStreamSubFormFilter;
import com.wuji.service.model.request.FormInsertDataRequest;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.service.FormDataStreamExecuteService;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.service.service.FormService;
import com.wuji.service.service.MongoDbService;
import com.wuji.service.utils.MongoDbDataTransUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FormDataStreamCreateExecuteImpl extends FormDataStreamCommonExecuteImpl
        implements FormDataStreamExecuteService {

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Autowired
    private FormService formService;

    @Autowired
    private MongoDbService mongoDbService;

    @Override
    public String nodeType() {
        return "create";
    }

    @Override
    public void execute(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                        Map<Long, DataStreamCalculateVO> nodeIdMap) {
        DataStreamCreateNode dataStreamCreateNode = (DataStreamCreateNode) dataStreamCommon;
        String applicationId = StringUtils.isNotEmpty(dataStreamCreateNode.getApplicationId()) ?
                dataStreamCreateNode.getApplicationId() : formDataStreamTrigger.getApplicationId();
        Boolean needCreate = MongoDbDataTransUtils.checkWhileCreate(nodeIdMap, dataStreamCreateNode.getFieldTrans());
        FormVO formVO = formService.info(dataStreamCreateNode.getFormId(), applicationId);
        if (!needCreate) {
            DataStreamCalculateVO dataStreamCalculateVO = new DataStreamCalculateVO();
            dataStreamCalculateVO.setNodeId(dataStreamCreateNode.getNodeId());
            dataStreamCalculateVO.setNodeType(dataStreamCommon.getType());
            dataStreamCalculateVO.setResultNull(Boolean.TRUE);
            dataStreamCalculateVO.setFormId(dataStreamCreateNode.getFormId());
            nodeIdMap.put(dataStreamCalculateVO.getNodeId(), dataStreamCalculateVO);
            insertLog(formDataStreamTrigger, dataStreamCreateNode, new ArrayList<>(), formVO, "insert");
            super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
            return;
        }
        List<DataStreamSubFormFilter> subFormFilterList = dataStreamCreateNode.getSubFormFilterList();
        if (CollectionUtils.isEmpty(subFormFilterList)) {
            subFormFilterList = new ArrayList<>();
        }
        Map<String, FormConfigEncryptKey> encryptKeyMap =
                formDataStreamTrigger.getEncryptKeyMap().get(dataStreamCreateNode.getFormId());
        DataStreamCreateTransDomain dataStreamCreateTransDomain =
                MongoDbDataTransUtils.streamTransWhileCreate(nodeIdMap, dataStreamCreateNode.getFieldTrans(),
                        subFormFilterList, encryptKeyMap);
        List<String> uuidList = new ArrayList<>();
        for (JSONObject jsonObject : dataStreamCreateTransDomain.getReturnList()) {
            FormInsertDataRequest formInsertDataRequest = new FormInsertDataRequest();
            formInsertDataRequest.setFormId(dataStreamCreateNode.getFormId());
            formInsertDataRequest.setStatus(FormDataStatusEnum.PASS.name());
            formInsertDataRequest.setInstValue(jsonObject);
            formInsertDataRequest.setUuid(ObjectId.getGuid());
            formInsertDataRequest.setApplicationId(applicationId);
            formInsertDataRequest.setTriggerParentList(formDataStreamTrigger.getParentList());
            formMongoDbService.insertDataTrigger(formInsertDataRequest);
            uuidList.add(formInsertDataRequest.getUuid());
        }
        List<LowcodeDataDomain> lowcodeDataDomains = mongoDbService.getByUuidList(uuidList, formVO.getTableName());
        DataStreamCalculateVO dataStreamCalculateVO = new DataStreamCalculateVO();
        dataStreamCalculateVO.setNodeId(dataStreamCreateNode.getNodeId());
        dataStreamCalculateVO.setNodeType(dataStreamCommon.getType());
        dataStreamCalculateVO.setFormId(dataStreamCreateNode.getFormId());
        List<JSONObject> jsonObjects =
                lowcodeDataDomains.stream().map(FormSystemFieldEnum::putSystemValue).collect(Collectors.toList());
        if (dataStreamCreateTransDomain.getMore()) {
            dataStreamCalculateVO.setNodeType("more");
            JSONObject returnJson = new JSONObject();
            returnJson.put("key", jsonObjects);
            returnJson.put("size", jsonObjects.size());
            dataStreamCalculateVO.setJsonValue(returnJson);
            dataStreamCalculateVO.setJsonValueList(jsonObjects);
        } else {
            dataStreamCalculateVO.setJsonValue(jsonObjects.get(0));
        }
        nodeIdMap.put(dataStreamCalculateVO.getNodeId(), dataStreamCalculateVO);
        insertLog(formDataStreamTrigger, dataStreamCreateNode, lowcodeDataDomains, formVO, "insert");
        super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
    }

    @Override
    public void useTemplate(DataStreamCommon dataStreamCommon, String applicationId, String sourceApplicationId) {
        DataStreamCreateNode dataStreamCreateNode = (DataStreamCreateNode) dataStreamCommon;
        if (CollectionUtils.isNotEmpty(dataStreamCreateNode.getFieldTrans())) {
            transUserAndDept(dataStreamCreateNode.getFieldTrans());
        }
        super.useTemplate(dataStreamCommon, applicationId, sourceApplicationId);
    }
}
