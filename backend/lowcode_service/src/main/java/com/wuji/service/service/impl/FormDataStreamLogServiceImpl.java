package com.wuji.service.service.impl;

import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.service.converter.AbstractDataStreamTriggerLogConverter;
import com.wuji.service.model.domain.DataStreamTriggerLogDomain;
import com.wuji.service.model.domain.DataStreamTriggerLogStageDomain;
import com.wuji.service.model.request.FormDataStreamLogRequest;
import com.wuji.service.model.vo.DataStreamTriggerLogStageVO;
import com.wuji.service.model.vo.DataStreamTriggerLogVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.service.FormDataStreamLogService;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.service.service.FormService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FormDataStreamLogServiceImpl implements FormDataStreamLogService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Autowired
    private FormService formService;

    @Override
    public QueryPageVO<DataStreamTriggerLogVO> queryList(FormDataStreamLogRequest formDataStreamLogRequest) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("applicationId").is(formDataStreamLogRequest.getApplicationId()));
        if (StringUtils.isNotEmpty(formDataStreamLogRequest.getFormId())) {
            criteriaList.add(Criteria.where("formId").is(formDataStreamLogRequest.getFormId()));
        }
        criteriaList.add(Criteria.where("dataStreamId").is(formDataStreamLogRequest.getDataStreamId()));
        if (formDataStreamLogRequest.getStartTime() != null) {
            criteriaList.add(Criteria.where("triggerStartTime").gte(formDataStreamLogRequest.getStartTime()));
        }
        if (formDataStreamLogRequest.getEndTime() != null) {
            criteriaList.add(Criteria.where("triggerStartTime").lte(formDataStreamLogRequest.getEndTime()));
        }
        if (formDataStreamLogRequest.getResult() != null) {
            criteriaList.add(Criteria.where("result").is(formDataStreamLogRequest.getResult()));
        }
        if (CollectionUtils.isNotEmpty(formDataStreamLogRequest.getCreators())) {
            criteriaList.add(Criteria.where("creator").in(formDataStreamLogRequest.getCreators()));
        }
        query.addCriteria(new Criteria().andOperator(criteriaList));
        query.with(Sort.by(Sort.Order.desc("triggerStartTime")));
        long count = mongoTemplate.count(query, FormDataStreamLogRequest.class, "data_stream_trigger_log");
        query.limit(formDataStreamLogRequest.getPageSize());
        query.skip((long) formDataStreamLogRequest.getOffSet());
        QueryPageVO<DataStreamTriggerLogVO> queryPageVO = new QueryPageVO<>();
        queryPageVO.setPageNum(formDataStreamLogRequest.getPageNum());
        queryPageVO.setPageSize(formDataStreamLogRequest.getPageSize());
        queryPageVO.setTotal((int) count);
        List<DataStreamTriggerLogDomain> dataStreamTriggerLogs =
                mongoTemplate.find(query, DataStreamTriggerLogDomain.class, "data_stream_trigger_log");
        List<DataStreamTriggerLogVO> dataStreamTriggerLogVOList = new ArrayList<>();
        String formName = "";
        if (StringUtils.isNotEmpty(formDataStreamLogRequest.getFormId())) {
            FormVO info =
                    formService.info(formDataStreamLogRequest.getFormId(), formDataStreamLogRequest.getApplicationId());
            formName = info.getFormName();
        }
        for (DataStreamTriggerLogDomain dataStreamTriggerLogDomain : dataStreamTriggerLogs) {
            DataStreamTriggerLogVO dataStreamTriggerLogVO =
                    AbstractDataStreamTriggerLogConverter.INSTANCE.toVO(dataStreamTriggerLogDomain);
            dataStreamTriggerLogVO.setFormName(formName);
            dataStreamTriggerLogVOList.add(dataStreamTriggerLogVO);
        }
        queryPageVO.setList(dataStreamTriggerLogVOList);

        return queryPageVO;
    }

    @Override
    public List<DataStreamTriggerLogStageVO> getStageByLogUuid(String uuid) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("triggerUuid").is(uuid));
        query.addCriteria(new Criteria().andOperator(criteriaList));
        query.with(Sort.by(Sort.Order.desc("createTime")));
        List<DataStreamTriggerLogStageDomain> dataStreamTriggerLogDomainList =
                mongoTemplate.find(query, DataStreamTriggerLogStageDomain.class);
        return dataStreamTriggerLogDomainList.stream().map(AbstractDataStreamTriggerLogConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public void tryAgain(String uuid) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("uuid").is(uuid));
        query.addCriteria(new Criteria().andOperator(criteriaList));
        DataStreamTriggerLogDomain dataStreamTriggerLogDomain =
                mongoTemplate.findOne(query, DataStreamTriggerLogDomain.class);
        if (dataStreamTriggerLogDomain == null) {
            return;
        }
        if (dataStreamTriggerLogDomain.getTitle() == null) {
            return;
        }
        String dataUuid = dataStreamTriggerLogDomain.getTitle().getUuid();
        formMongoDbService.dataStreamTriggerAgain(dataStreamTriggerLogDomain, dataUuid);
    }


}
