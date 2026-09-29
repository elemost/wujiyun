package com.wuji.service.service.stream;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wuji.service.constant.Constants;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.info.FormConfigEncryptKey;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamCommon;
import com.wuji.service.model.info.stream.DataStreamConditionRel;
import com.wuji.service.model.info.stream.DataStreamQueryMoreNode;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.service.FormDataStreamExecuteService;
import com.wuji.service.service.FormService;
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
import java.util.List;
import java.util.Map;

@Service
public class FormDataStreamQueryMoreExecuteImpl extends FormDataStreamCommonExecuteImpl
        implements FormDataStreamExecuteService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FormService formService;

    @Override
    public String nodeType() {
        return "more";
    }

    @Override
    public void execute(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                        Map<Long, DataStreamCalculateVO> nodeIdMap) {
        DataStreamQueryMoreNode dataStreamQueryMoreNode = (DataStreamQueryMoreNode) dataStreamCommon;
        DataStreamConditionRel dataStreamUpdateNodeMatchRule = dataStreamQueryMoreNode.getMatchRule();
        Map<String, FormConfigEncryptKey> encryptKeyMap =
                formDataStreamTrigger.getEncryptKeyMap().get(dataStreamQueryMoreNode.getFormId());
        MongodbSearchFilter mongodbSearchFilter =
                MongoSearchUtils.toFilter(dataStreamUpdateNodeMatchRule, nodeIdMap, encryptKeyMap);
        String applicationId = StringUtils.isNotEmpty(dataStreamQueryMoreNode.getApplicationId()) ?
                dataStreamQueryMoreNode.getApplicationId() : formDataStreamTrigger.getApplicationId();
        FormVO info = formService.info(dataStreamQueryMoreNode.getFormId(), applicationId);
        Boolean needQuery = MongoDbDataTransUtils.checkWhileNull(nodeIdMap, dataStreamUpdateNodeMatchRule.getRelates());
        if (!needQuery) {
            setWhileNull(formDataStreamTrigger, nodeIdMap, dataStreamQueryMoreNode, info);
            return;
        }
        // 高级搜索
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();
        MongoSearchUtils.buildCommonFilter(criteriaList, info.getApplicationId(), info.getId());
        Criteria filter = MongoSearchUtils.buildCriteriaByFilter(mongodbSearchFilter, new ArrayList<>());
        if (filter != null) {
            criteriaList.add(filter);
        }
        if (CollectionUtils.isNotEmpty(criteriaList)) {
            Criteria criteria = new Criteria();
            criteria.andOperator(criteriaList);
            query.addCriteria(criteria);
        }
        if (CollectionUtils.isNotEmpty(dataStreamQueryMoreNode.getSorts())) {
            MongoSearchUtils.buildSort(query, dataStreamQueryMoreNode.getSorts());
        }
        query.limit(dataStreamQueryMoreNode.getLimit());
        List<LowcodeDataDomain> lowcodeDataDomainList =
                mongoTemplate.find(query, LowcodeDataDomain.class, info.getTableName());
        if (CollectionUtils.isEmpty(lowcodeDataDomainList)) {
            setWhileNull(formDataStreamTrigger, nodeIdMap, dataStreamQueryMoreNode, info);
            return;
        }
        DataStreamCalculateVO dataStreamCalculateVO = new DataStreamCalculateVO();
        dataStreamCalculateVO.setNodeId(dataStreamQueryMoreNode.getNodeId());
        dataStreamCalculateVO.setNodeType(dataStreamCommon.getType());
        dataStreamCalculateVO.setFormId(dataStreamQueryMoreNode.getFormId());
        JSONObject returnJson = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        for (LowcodeDataDomain lowcodeDataDomain : lowcodeDataDomainList) {
            jsonArray.add(FormSystemFieldEnum.putSystemValue(lowcodeDataDomain));
        }
        returnJson.put("key", jsonArray);
        returnJson.put(Constants.SIZE, jsonArray.size());
        dataStreamCalculateVO.setJsonValue(returnJson);
        nodeIdMap.put(dataStreamCalculateVO.getNodeId(), dataStreamCalculateVO);
        insertLog(formDataStreamTrigger, dataStreamCommon, lowcodeDataDomainList, info, "query");
        super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
    }

    private void setWhileNull(FormDataStreamTrigger formDataStreamTrigger, Map<Long, DataStreamCalculateVO> nodeIdMap,
                              DataStreamQueryMoreNode dataStreamQueryMoreNode, FormVO info) {
        DataStreamCalculateVO dataStreamCalculateVO = new DataStreamCalculateVO();
        dataStreamCalculateVO.setNodeId(dataStreamQueryMoreNode.getNodeId());
        dataStreamCalculateVO.setNodeType(dataStreamQueryMoreNode.getType());
        dataStreamCalculateVO.setResultNull(Boolean.TRUE);
        dataStreamCalculateVO.setFormId(dataStreamQueryMoreNode.getFormId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.SIZE, 0);
        dataStreamCalculateVO.setJsonValue(jsonObject);
        nodeIdMap.put(dataStreamCalculateVO.getNodeId(), dataStreamCalculateVO);
        insertLog(formDataStreamTrigger, dataStreamQueryMoreNode, new ArrayList<>(), info, "query");
        super.execute(formDataStreamTrigger, dataStreamQueryMoreNode, nodeIdMap);
    }

    @Override
    public void useTemplate(DataStreamCommon dataStreamCommon, String applicationId, String sourceApplicationId) {
        DataStreamQueryMoreNode dataStreamQueryMoreNode = (DataStreamQueryMoreNode) dataStreamCommon;
        if (dataStreamQueryMoreNode.getMatchRule() != null) {
            buildFieldRelate(dataStreamQueryMoreNode.getMatchRule().getRelates());
        }
        super.useTemplate(dataStreamCommon, applicationId, sourceApplicationId);
    }
}
