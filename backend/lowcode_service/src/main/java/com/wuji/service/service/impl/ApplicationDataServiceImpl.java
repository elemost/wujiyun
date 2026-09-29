package com.wuji.service.service.impl;

import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.vo.ApplicationCategoryVO;
import com.wuji.service.model.vo.FormModelVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.service.ApplicationCategoryService;
import com.wuji.service.service.ApplicationDataService;
import com.wuji.service.service.FormModelService;
import com.wuji.service.service.FormService;
import com.wuji.service.utils.MongoSearchUtils;
import com.wuji.workflow.service.FlowableCopyService;
import com.wuji.workflow.service.WorkFlowService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationDataServiceImpl implements ApplicationDataService {

    @Autowired
    private FormService formService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private FormModelService formModelService;

    @Autowired
    private WorkFlowService workFlowService;

    @Autowired
    private ApplicationCategoryService applicationCategoryService;

    @Autowired
    private FlowableCopyService flowableCopyService;

    @Override
    public void clearAllData(String applicationId) {
        List<FormVO> formVOList = formService.getByApplicationId(applicationId);
        for (FormVO formVO : formVOList) {
            mongoTemplate.dropCollection(formVO.getTableName());
        }
        List<FormModelVO> formModelVOList = formModelService.getByApplicationId(applicationId);
        for (FormModelVO formModelVO : formModelVOList) {
            workFlowService.deleteDataByProcessDefinitionKey(formModelVO.getBusinessType());
        }
        flowableCopyService.clear(applicationId);
    }

    @Override
    public void clearAllData(String applicationId, String formId) {
        FormVO formVO = formService.info(formId, applicationId);
        Query query = new Query();
        MongoSearchUtils.buildCommonFilter(query, applicationId, formId);
        mongoTemplate.remove(query, formVO.getTableName());
        List<FormModelVO> formModelVOList =
                formModelService.getByFormIdList(Collections.singletonList(formId), applicationId);
        for (FormModelVO formModelVO : formModelVOList) {
            workFlowService.deleteDataByProcessDefinitionKey(formModelVO.getBusinessType());
        }
        flowableCopyService.clear(applicationId, formId);
    }

    @Override
    public void migrateData(String applicationId) {
        List<ApplicationCategoryVO> applicationCategoryVOList = applicationCategoryService.selectList(applicationId);
        List<FormVO> formVOList = formService.getByIdList(
                applicationCategoryVOList.stream().map(ApplicationCategoryVO::getId).collect(Collectors.toList()),
                applicationId);
        List<LowcodeDataDomain> insertList = new ArrayList<>();
        for (FormVO formVO : formVOList) {
            if (StringUtils.isEmpty(formVO.getTableName())) {
                continue;
            }
            if (applicationId.equals(formVO.getTableName())) {
                continue;
            }
            Query query = new Query();
            MongoSearchUtils.buildCommonFilterWithout(query, applicationId, formVO.getId());
            List<LowcodeDataDomain> lowcodeDataDomains =
                    mongoTemplate.find(query, LowcodeDataDomain.class, formVO.getTableName());
            for (LowcodeDataDomain lowcodeDataDomain : lowcodeDataDomains) {
                lowcodeDataDomain.setApplicationId(applicationId);
            }
            insertList.addAll(lowcodeDataDomains);
        }
        mongoTemplate.insert(insertList, applicationId);
        formService.updateTableName(applicationId);
    }
}
