package com.wuji.service.service.stream;

import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.info.FormConfigEncryptKey;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamCommon;
import com.wuji.service.model.info.stream.DataStreamConditionRel;
import com.wuji.service.model.info.stream.DataStreamFlowableNode;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.service.FormDataStreamExecuteService;
import com.wuji.service.service.FormService;
import com.wuji.service.utils.MongoDbDataTransUtils;
import com.wuji.service.utils.MongoSearchUtils;
import com.wuji.workflow.model.request.FlowableAuditRequest;
import com.wuji.workflow.model.vo.PendingTaskVO;
import com.wuji.workflow.service.WorkFlowService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FormDataStreamFlowableExecuteImpl extends FormDataStreamCommonExecuteImpl
        implements FormDataStreamExecuteService {

    @Autowired
    private FormService formService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private WorkFlowService workFlowService;

    @Override
    public String nodeType() {
        return "flowable";
    }

    @Override
    public void execute(FormDataStreamTrigger formDataStreamTrigger, DataStreamCommon dataStreamCommon,
                        Map<Long, DataStreamCalculateVO> nodeIdMap) {
        DataStreamFlowableNode dataStreamFlowableNode = (DataStreamFlowableNode) dataStreamCommon;
        DataStreamConditionRel matchRule = dataStreamFlowableNode.getMatchRule();
        Boolean needQuery = MongoDbDataTransUtils.checkWhileNull(nodeIdMap, matchRule.getRelates());
        if (!needQuery) {
            return;
        }
        Map<String, FormConfigEncryptKey> encryptKeyMap =
                formDataStreamTrigger.getEncryptKeyMap().get(dataStreamFlowableNode.getFormId());
        MongodbSearchFilter mongodbSearchFilter = MongoSearchUtils.toFilter(matchRule, nodeIdMap, encryptKeyMap);
        FormVO info = formService.info(dataStreamFlowableNode.getFormId(), formDataStreamTrigger.getApplicationId());
        // 高级搜索
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();
        Criteria filter = MongoSearchUtils.buildCriteriaByFilter(mongodbSearchFilter, new ArrayList<>());
        criteriaList.add(filter);
        MongoSearchUtils.buildCommonFilter(criteriaList, info.getApplicationId(), info.getId());
        Criteria criteria = new Criteria();
        criteria.andOperator(criteriaList);
        query.addCriteria(criteria);
        // 执行查找到的匹配的全部文档信息
        List<LowcodeDataDomain> lowcodeInsertDataDomains =
                mongoTemplate.find(query, LowcodeDataDomain.class, info.getTableName());
        List<String> processInstanceIdList =
                lowcodeInsertDataDomains.stream().map(LowcodeDataDomain::getProcessInstanceId)
                        .collect(Collectors.toList());
        List<PendingTaskVO> taskByProcessInstanceIdList =
                workFlowService.getTaskByProcessInstanceIdList(processInstanceIdList);
        Map<String, List<PendingTaskVO>> processInstanceIdMap = taskByProcessInstanceIdList.stream()
                .collect(Collectors.groupingBy(PendingTaskVO::getProcessInstanceId));
        for (LowcodeDataDomain lowcodeDataDomain : lowcodeInsertDataDomains) {
            FlowableAuditRequest flowableAuditRequest = new FlowableAuditRequest();
            flowableAuditRequest.setProcessInstanceId(lowcodeDataDomain.getProcessInstanceId());
            List<PendingTaskVO> pendingTaskVOList = processInstanceIdMap.get(lowcodeDataDomain.getProcessInstanceId());
            if (CollectionUtils.isNotEmpty(pendingTaskVOList)) {
                PendingTaskVO pendingTaskVO = pendingTaskVOList.get(0);
                if (!pendingTaskVO.getTaskDefKey().equals(dataStreamFlowableNode.getAuditTaskKey())) {
                    return;
                }
                if ("agree".equals(dataStreamFlowableNode.getAuditResult())) {
                    flowableAuditRequest.setComment("审核通过");
                    flowableAuditRequest.setTaskId(pendingTaskVO.getTaskId());
                    workFlowService.taskComplete(flowableAuditRequest, Boolean.TRUE);
                } else {
                    flowableAuditRequest.setComment("审核驳回");
                    flowableAuditRequest.setTaskId(pendingTaskVO.getTaskId());
                    flowableAuditRequest.setTargetKey(dataStreamFlowableNode.getTargetKey());
                    workFlowService.taskReturn(flowableAuditRequest);
                }
            }
        }
        super.execute(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
    }
}
