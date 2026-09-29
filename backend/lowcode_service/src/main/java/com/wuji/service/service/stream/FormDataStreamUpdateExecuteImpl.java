package com.wuji.service.service.stream;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.common.utils.ObjectId;
import com.wuji.service.enums.FormDataStatusEnum;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.enums.SearchMethodEnum;
import com.wuji.service.model.domain.DataStreamCreateTransDomain;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.info.FormConfigEncryptKey;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.MongoFieldRelate;
import com.wuji.service.model.info.MongodbSearchCondition;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamCommon;
import com.wuji.service.model.info.stream.DataStreamConditionRel;
import com.wuji.service.model.info.stream.DataStreamFieldTrans;
import com.wuji.service.model.info.stream.DataStreamQuoteField;
import com.wuji.service.model.info.stream.DataStreamSubFormFilter;
import com.wuji.service.model.info.stream.DataStreamUpdateNode;
import com.wuji.service.model.request.FormInsertDataRequest;
import com.wuji.service.model.request.FormUpdateDataRequest;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.service.FormDataStreamExecuteService;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.service.service.FormService;
import com.wuji.service.service.MongoDbService;
import com.wuji.service.utils.MongoDbDataTransUtils;
import com.wuji.service.utils.MongoSearchUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FormDataStreamUpdateExecuteImpl extends FormDataStreamCommonExecuteImpl
        implements FormDataStreamExecuteService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FormService formService;

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Autowired
    private MongoDbService mongoDbService;

    @Override
    public String nodeType() {
        return "update";
    }

    @Override
    public void execute(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                        Map<Long, DataStreamCalculateVO> nodeIdMap) {
        DataStreamUpdateNode dataStreamUpdateNode = (DataStreamUpdateNode) dataStreamCommon;
        DataStreamConditionRel matchRule = dataStreamUpdateNode.getMatchRule();
        Boolean needQuery = MongoDbDataTransUtils.checkWhileNull(nodeIdMap, matchRule.getRelates());
        if (!needQuery) {
            return;
        }
        Map<String, FormConfigEncryptKey> encryptKeyMap =
                formDataStreamTrigger.getEncryptKeyMap().get(dataStreamUpdateNode.getUpdateObjectId());
        MongodbSearchFilter mongodbSearchFilter = MongoSearchUtils.toFilter(matchRule, nodeIdMap, encryptKeyMap);
        String applicationId = StringUtils.isNotEmpty(dataStreamUpdateNode.getApplicationId()) ?
                dataStreamUpdateNode.getApplicationId() : formDataStreamTrigger.getApplicationId();
        FormVO formVO = formService.info(dataStreamUpdateNode.getUpdateObjectId(), applicationId);
        // 高级搜索
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();
        Criteria filter = MongoSearchUtils.buildCriteriaByFilter(mongodbSearchFilter, new ArrayList<>());
        criteriaList.add(filter);
        MongoSearchUtils.buildCommonFilter(criteriaList, formVO.getApplicationId(), formVO.getId());
        Criteria criteria = new Criteria();
        criteria.andOperator(criteriaList);
        query.addCriteria(criteria);
        // 执行查找到的匹配的全部文档信息
        List<LowcodeDataDomain> lowcodeInsertDataDomains =
                mongoTemplate.find(query, LowcodeDataDomain.class, formVO.getTableName());
        String action = "";
        Object logTitle = lowcodeInsertDataDomains;
        if (CollectionUtils.isNotEmpty(lowcodeInsertDataDomains)) {
            Boolean needUpdate =
                    MongoDbDataTransUtils.checkWhileCreate(nodeIdMap, dataStreamUpdateNode.getUpdateFieldTrans());
            if (!needUpdate) {
                insertLog(formDataStreamTrigger, dataStreamUpdateNode, new ArrayList<>(), formVO, "update");
                super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
                return;
            }
            List<String> update =
                    update(nodeIdMap, lowcodeInsertDataDomains, dataStreamUpdateNode, formVO, formDataStreamTrigger);
            if (CollectionUtils.isNotEmpty(update)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("insert", mongoDbService.getByUuidList(update, formVO.getTableName()));
                jsonObject.put("update", lowcodeInsertDataDomains);
                action = "insert_update";
                logTitle = jsonObject;
            } else {
                action = "update";
            }
        } else {
            if (dataStreamUpdateNode.getCreateWhileNull()) {
                Boolean needCreate =
                        MongoDbDataTransUtils.checkWhileCreate(nodeIdMap, dataStreamUpdateNode.getCreateFieldTrans());
                if (!needCreate) {
                    insertLog(formDataStreamTrigger, dataStreamUpdateNode, new ArrayList<>(), formVO, "insert");
                    super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
                    return;
                }
                List<String> insert = insert(formDataStreamTrigger, nodeIdMap, dataStreamUpdateNode, formVO);
                logTitle = mongoDbService.getByUuidList(insert, formVO.getTableName());
                action = "insert";
            }
        }
        insertLog(formDataStreamTrigger, dataStreamCommon, logTitle, formVO, action);
        super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
    }

    private List<String> insert(FormDataStreamTrigger formDataStreamTrigger, Map<Long, DataStreamCalculateVO> nodeIdMap,
                                DataStreamUpdateNode dataStreamUpdateNode, FormVO formVO) {
        List<DataStreamSubFormFilter> subFormFilterList = dataStreamUpdateNode.getSubFormFilterList();
        if (CollectionUtils.isEmpty(subFormFilterList)) {
            subFormFilterList = new ArrayList<>();
        }
        Map<String, FormConfigEncryptKey> encryptKeyMap = formDataStreamTrigger.getEncryptKeyMap().get(formVO.getId());
        DataStreamCreateTransDomain dataStreamCreateTransDomain =
                MongoDbDataTransUtils.streamTransWhileCreate(nodeIdMap, dataStreamUpdateNode.getCreateFieldTrans(),
                        subFormFilterList, encryptKeyMap);
        List<String> uuidList = new ArrayList<>();
        for (JSONObject jsonObject : dataStreamCreateTransDomain.getReturnList()) {
            FormInsertDataRequest formInsertDataRequest = new FormInsertDataRequest();
            formInsertDataRequest.setFormId(dataStreamUpdateNode.getUpdateObjectId());
            formInsertDataRequest.setStatus(FormDataStatusEnum.PASS.name());
            formInsertDataRequest.setInstValue(jsonObject);
            formInsertDataRequest.setUuid(ObjectId.getGuid());
            formInsertDataRequest.setApplicationId(formVO.getApplicationId());
            formInsertDataRequest.setTriggerParentList(formDataStreamTrigger.getParentList());
            formMongoDbService.insertDataTrigger(formInsertDataRequest);
            uuidList.add(formInsertDataRequest.getUuid());
        }
        List<LowcodeDataDomain> lowcodeDataDomains = mongoDbService.getByUuidList(uuidList, formVO.getTableName());
        DataStreamCalculateVO dataStreamCalculateVO = new DataStreamCalculateVO();
        dataStreamCalculateVO.setNodeId(dataStreamUpdateNode.getNodeId());
        dataStreamCalculateVO.setNodeType(dataStreamUpdateNode.getType());
        dataStreamCalculateVO.setFormId(formVO.getId());
        List<JSONObject> jsonObjects =
                lowcodeDataDomains.stream().map(FormSystemFieldEnum::putSystemValue).collect(Collectors.toList());
        dataStreamCalculateVO.setJsonValueList(jsonObjects);
        nodeIdMap.put(dataStreamCalculateVO.getNodeId(), dataStreamCalculateVO);
        return uuidList;
    }

    private List<String> update(Map<Long, DataStreamCalculateVO> nodeIdMap, List<LowcodeDataDomain> lowcodeDataList,
                                DataStreamUpdateNode dataStreamUpdateNode, FormVO info,
                                FormDataStreamTrigger formDataStreamTrigger) {
        Map<String, FormConfigEncryptKey> encryptKeyMap = formDataStreamTrigger.getEncryptKeyMap().get(info.getId());
        DataStreamConditionRel matchRule = dataStreamUpdateNode.getMatchRule();
        boolean needSplit = false;
        Set<String> subForm = new HashSet<>();
        List<MongoFieldRelate> quoteFieldList = new ArrayList<>();
        if (dataStreamUpdateNode.getCreateWhileNull()) {
            List<MongoFieldRelate> relates = matchRule.getRelates();
            for (MongoFieldRelate mongoFieldRelate : relates) {
                if (StringUtils.isEmpty(mongoFieldRelate.getSubForm()) && mongoFieldRelate.getQuoteField() != null &&
                        StringUtils.isNotEmpty(mongoFieldRelate.getQuoteField().getQuoteSubForm())) {
                    subForm.add(mongoFieldRelate.getQuoteField().getQuoteSubForm());
                    quoteFieldList.add(mongoFieldRelate);
                }
            }
        }
        if (subForm.size() == 1) {
            needSplit = true;
        }
        for (LowcodeDataDomain lowcodeDataDomain : lowcodeDataList) {
            List<DataStreamFieldTrans> updateFieldTrans = dataStreamUpdateNode.getUpdateFieldTrans();
            JSONObject jsonObject =
                    MongoDbDataTransUtils.streamTransWhileUpdate(nodeIdMap, updateFieldTrans, lowcodeDataDomain,
                            dataStreamUpdateNode.getCondition(), encryptKeyMap);
            FormUpdateDataRequest formUpdateDataRequest = new FormUpdateDataRequest();
            formUpdateDataRequest.setFormId(info.getId());
            formUpdateDataRequest.setOnlyUpdate(true);
            formUpdateDataRequest.setVersion(info.getVersion());
            formUpdateDataRequest.setApplicationId(info.getApplicationId());
            formUpdateDataRequest.setInstValue(jsonObject);
            formUpdateDataRequest.setUuid(lowcodeDataDomain.getUuid());
            formUpdateDataRequest.setTriggerParentList(formDataStreamTrigger.getParentList());
            formMongoDbService.updateDataTrigger(formUpdateDataRequest, true);
            lowcodeDataDomain.setInstValue(jsonObject);
        }
        if (needSplit && "AND".equalsIgnoreCase(matchRule.getRel())) {
            MongoFieldRelate demo = quoteFieldList.get(0);
            DataStreamQuoteField quoteField = demo.getQuoteField();
            String quoteSubForm = quoteField.getQuoteSubForm();
            DataStreamCalculateVO dataStreamCalculateVO = nodeIdMap.get(quoteField.getNodeId());
            JSONArray jsonArray = dataStreamCalculateVO.getJsonValue().getJSONArray(quoteSubForm);
            if (jsonArray == null || jsonArray.isEmpty()) {
                return null;
            }
            JSONArray notMatch = new JSONArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                List<MongodbSearchCondition> mongodbSearchConditions = new ArrayList<>();
                for (MongoFieldRelate mongoFieldRelate : quoteFieldList) {
                    MongodbSearchCondition mongodbSearchCondition =
                            MongoSearchUtils.buildConditionList(Collections.singletonList(mongoFieldRelate), nodeIdMap,
                                    encryptKeyMap).get(0);
                    mongodbSearchCondition.setValue(Collections.singletonList(
                            jsonObject.get(mongoFieldRelate.getQuoteField().getQuoteFieldId())));
                    mongodbSearchCondition.setMethod(SearchMethodEnum.EQ.name());
                    mongodbSearchConditions.add(mongodbSearchCondition);
                }
                MongodbSearchFilter mongodbSearchFilter = new MongodbSearchFilter();
                mongodbSearchFilter.setConditionList(mongodbSearchConditions);
                mongodbSearchFilter.setRel("AND");
                boolean match = false;
                for (LowcodeDataDomain lowcodeDataDomain : lowcodeDataList) {
                    Boolean conform = MongoSearchUtils.checkData(mongodbSearchFilter, lowcodeDataDomain.getInstValue());
                    if (conform) {
                        match = true;
                        break;
                    }
                }
                if (!match) {
                    notMatch.add(jsonObject);
                }
            }
            if (notMatch.isEmpty()) {
                return null;
            }
            dataStreamCalculateVO.getJsonValue().put(quoteSubForm, notMatch);
            List<String> insert = insert(formDataStreamTrigger, nodeIdMap, dataStreamUpdateNode, info);
            dataStreamCalculateVO.getJsonValue().put(quoteSubForm, jsonArray);
            return insert;
        }
        return null;
    }

    @Override
    public void useTemplate(DataStreamCommon dataStreamCommon, String applicationId, String sourceApplicationId) {
        DataStreamUpdateNode dataStreamUpdateNode = (DataStreamUpdateNode) dataStreamCommon;
        if (CollectionUtils.isNotEmpty(dataStreamUpdateNode.getCreateFieldTrans())) {
            transUserAndDept(dataStreamUpdateNode.getCreateFieldTrans());
        }
        if (CollectionUtils.isNotEmpty(dataStreamUpdateNode.getUpdateFieldTrans())) {
            transUserAndDept(dataStreamUpdateNode.getUpdateFieldTrans());
        }
        super.useTemplate(dataStreamCommon, applicationId, sourceApplicationId);
    }
}
