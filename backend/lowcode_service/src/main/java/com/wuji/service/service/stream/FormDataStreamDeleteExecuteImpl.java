package com.wuji.service.service.stream;

import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.info.FormConfigEncryptKey;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamCommon;
import com.wuji.service.model.info.stream.DataStreamConditionRel;
import com.wuji.service.model.info.stream.DataStreamDeleteNode;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.service.FormDataStreamExecuteService;
import com.wuji.service.service.FormMongoDbService;
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
public class FormDataStreamDeleteExecuteImpl extends FormDataStreamCommonExecuteImpl
        implements FormDataStreamExecuteService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FormService formService;

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Override
    public String nodeType() {
        return "delete";
    }

    @Override
    public void execute(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                        Map<Long, DataStreamCalculateVO> nodeIdMap) {
        DataStreamDeleteNode dataStreamDeleteNode = (DataStreamDeleteNode) dataStreamCommon;
        DataStreamConditionRel matchRule = dataStreamDeleteNode.getMatchRule();
        String applicationId = StringUtils.isNotEmpty(dataStreamDeleteNode.getApplicationId()) ?
                dataStreamDeleteNode.getApplicationId() : formDataStreamTrigger.getApplicationId();
        FormVO info = formService.info(dataStreamDeleteNode.getDeleteObjectId(), applicationId);
        Boolean needDelete = MongoDbDataTransUtils.checkWhileNull(nodeIdMap, matchRule.getRelates());
        if (!needDelete) {
            insertLog(formDataStreamTrigger, dataStreamCommon, new ArrayList<>(), info, "delete");
            super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
            return;
        }
        Map<String, FormConfigEncryptKey> encryptKeyMap = formDataStreamTrigger.getEncryptKeyMap().get(info.getId());
        MongodbSearchFilter mongodbSearchFilter = MongoSearchUtils.toFilter(matchRule, nodeIdMap, encryptKeyMap);

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
        List<LowcodeDataDomain> lowcodeDataDomainList =
                mongoTemplate.find(query, LowcodeDataDomain.class, info.getTableName());
        for (LowcodeDataDomain lowcodeDataDomain : lowcodeDataDomainList) {
            formMongoDbService.deleteData(lowcodeDataDomain.getUuid(), info.getId(),
                    formDataStreamTrigger.getApplicationId(), formDataStreamTrigger.getParentList());
        }
        insertLog(formDataStreamTrigger, dataStreamCommon, lowcodeDataDomainList, info, "delete");
        super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
    }

    @Override
    public void useTemplate(DataStreamCommon dataStreamCommon, String applicationId, String sourceApplicationId) {
        DataStreamDeleteNode dataStreamDeleteNode = (DataStreamDeleteNode) dataStreamCommon;
        if (dataStreamDeleteNode.getMatchRule() != null) {
            buildFieldRelate(dataStreamDeleteNode.getMatchRule().getRelates());
        }
        super.useTemplate(dataStreamCommon, applicationId, sourceApplicationId);
    }
}
