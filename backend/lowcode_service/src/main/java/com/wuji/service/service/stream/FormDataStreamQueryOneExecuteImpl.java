package com.wuji.service.service.stream;

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
import com.wuji.service.model.info.stream.DataStreamQueryOneNode;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.service.FormDataStreamExecuteService;
import com.wuji.service.service.FormService;
import com.wuji.service.utils.FormConfigUtils;
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
import java.util.List;
import java.util.Map;

@Service
public class FormDataStreamQueryOneExecuteImpl extends FormDataStreamCommonExecuteImpl
        implements FormDataStreamExecuteService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FormService formService;

    @Override
    public String nodeType() {
        return "one";
    }

    @Override
    public void execute(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                        Map<Long, DataStreamCalculateVO> nodeIdMap) {
        DataStreamQueryOneNode dataStreamQueryOneNode = (DataStreamQueryOneNode) dataStreamCommon;
        DataStreamConditionRel dataStreamUpdateNodeMatchRule = dataStreamQueryOneNode.getMatchRule();
        String applicationId = StringUtils.isNotEmpty(dataStreamQueryOneNode.getApplicationId()) ?
                dataStreamQueryOneNode.getApplicationId() : formDataStreamTrigger.getApplicationId();
        FormVO info = formService.info(dataStreamQueryOneNode.getFormId(), applicationId);
        Boolean needQuery = MongoDbDataTransUtils.checkWhileNull(nodeIdMap, dataStreamUpdateNodeMatchRule.getRelates());
        if (!needQuery) {
            setWhileNull(formDataStreamTrigger, dataStreamCommon, nodeIdMap, dataStreamQueryOneNode, info);
            return;
        }
        LowcodeDataDomain lowcodeDataDomain =
                queryData(formDataStreamTrigger, dataStreamCommon, nodeIdMap, info, dataStreamQueryOneNode);
        if (lowcodeDataDomain == null) {
            return;
        }
        List<LowcodeDataDomain> lowcodeDataDomains = Collections.singletonList(lowcodeDataDomain);
        FormConfigUtils.buildDataTitle(info.getConfig(), lowcodeDataDomains);
        DataStreamCalculateVO dataStreamCalculateVO = new DataStreamCalculateVO();
        dataStreamCalculateVO.setNodeId(dataStreamQueryOneNode.getNodeId());
        dataStreamCalculateVO.setNodeType(dataStreamCommon.getType());
        dataStreamCalculateVO.setFormId(dataStreamQueryOneNode.getFormId());
        JSONObject jsonObject = FormSystemFieldEnum.putSystemValue(lowcodeDataDomain);
        jsonObject.put(Constants.SIZE, 1);
        dataStreamCalculateVO.setJsonValue(jsonObject);
        nodeIdMap.put(dataStreamCalculateVO.getNodeId(), dataStreamCalculateVO);
        insertLog(formDataStreamTrigger, dataStreamCommon, lowcodeDataDomains, info, "query");
        super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
    }

    private LowcodeDataDomain queryData(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                                        Map<Long, DataStreamCalculateVO> nodeIdMap, FormVO info,
                                        DataStreamQueryOneNode dataStreamQueryOneNode) {
        Map<String, FormConfigEncryptKey> encryptKeyMap =
                formDataStreamTrigger.getEncryptKeyMap().get(dataStreamQueryOneNode.getFormId());
        MongodbSearchFilter mongodbSearchFilter =
                MongoSearchUtils.toFilter(dataStreamQueryOneNode.getMatchRule(), nodeIdMap, encryptKeyMap);
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
        query.limit(1);
        if (CollectionUtils.isNotEmpty(dataStreamQueryOneNode.getSorts())) {
            MongoSearchUtils.buildSort(query, dataStreamQueryOneNode.getSorts());
        }
        LowcodeDataDomain lowcodeDataDomain =
                mongoTemplate.findOne(query, LowcodeDataDomain.class, info.getTableName());
        if (lowcodeDataDomain == null) {
            setWhileNull(formDataStreamTrigger, dataStreamCommon, nodeIdMap, dataStreamQueryOneNode, info);
            return null;
        }
        return lowcodeDataDomain;
    }

    private void setWhileNull(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                              Map<Long, DataStreamCalculateVO> nodeIdMap, DataStreamQueryOneNode dataStreamQueryOneNode,
                              FormVO info) {
        DataStreamCalculateVO dataStreamCalculateVO = new DataStreamCalculateVO();
        dataStreamCalculateVO.setNodeId(dataStreamQueryOneNode.getNodeId());
        dataStreamCalculateVO.setNodeType(dataStreamCommon.getType());
        dataStreamCalculateVO.setResultNull(Boolean.TRUE);
        dataStreamCalculateVO.setFormId(dataStreamQueryOneNode.getFormId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.SIZE, 0);
        dataStreamCalculateVO.setJsonValue(jsonObject);
        nodeIdMap.put(dataStreamCalculateVO.getNodeId(), dataStreamCalculateVO);
        insertLog(formDataStreamTrigger, dataStreamCommon, new ArrayList<>(), info, "query");
        super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
    }

    @Override
    public void useTemplate(DataStreamCommon dataStreamCommon, String applicationId, String sourceApplicationId) {
        DataStreamQueryOneNode dataStreamQueryOneNode = (DataStreamQueryOneNode) dataStreamCommon;
        if (dataStreamQueryOneNode.getMatchRule() != null) {
            buildFieldRelate(dataStreamQueryOneNode.getMatchRule().getRelates());
        }
        super.useTemplate(dataStreamCommon, applicationId, sourceApplicationId);
    }
}
