package com.wuji.service.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuji.service.converter.AbstractFormModelConverter;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.request.FormModelDesignerRequest;
import com.wuji.service.model.vo.FormModelVO;
import com.wuji.service.service.FormModelDesignerService;
import com.wuji.service.service.FormModelService;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.workflow.model.domain.FlowableActivityConfigDomain;
import com.wuji.workflow.model.domain.FormModelDesignerDomain;
import com.wuji.workflow.model.flowable.ProcessModel;
import com.wuji.workflow.model.flowable.model.Node;
import com.wuji.workflow.model.vo.ModelVO;
import com.wuji.workflow.service.FlowDesignerService;
import com.wuji.workflow.service.FlowableActivityConfigService;
import com.wuji.workflow.service.ModelManageService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.BpmnModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FormModelDesignerServiceImpl implements FormModelDesignerService {

    @Autowired
    private ModelManageService modelManageService;

    @Autowired
    private FlowDesignerService flowDesignerService;

    @Autowired
    private FlowableActivityConfigService flowableActivityConfigService;

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FormModelService formModelService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String designModel(FormModelDesignerRequest formModelDesignerRequest) {
        ModelVO info = modelManageService.info(formModelDesignerRequest.getModelId());
        ProcessModel processModel = new ProcessModel();

        try {
            String config = objectMapper.writer().writeValueAsString(formModelDesignerRequest.getProcess());
            Node node = objectMapper.readValue(config, Node.class);
            processModel.setProcess(node);
        } catch (Exception e) {
            log.error("转化失败", e);
        }
        processModel.setModelKey(info.getModelKey());
        processModel.setModelName(info.getModelName());
        BpmnModel bpmnModel = processModel.toBpmnModel();
        FormModelDesignerDomain formModelDesignerDomain =
                AbstractFormModelConverter.INSTANCE.toDomain(formModelDesignerRequest);
        return flowDesignerService.designModule(formModelDesignerRequest.getModelId(), bpmnModel,
                formModelDesignerDomain, Boolean.FALSE);
    }

    @Override
    public List<FlowableActivityConfigDomain> getByFormId(String formId, String dataUuid, String applicationId) {
        LowcodeDataDomain info = formMongoDbService.info(dataUuid, formId, applicationId);

        if (info == null) {
            return new ArrayList<>();
        }
        return flowableActivityConfigService.detail(info.getModelId(), null);
    }

    @Override
    public List<FlowableActivityConfigDomain> getPublishModelConfig(String formId, String applicationId) {
        FormModelVO formModelVO = formModelService.info(formId, applicationId);
        if (formModelVO == null) {
            return new ArrayList<>();
        }
        return flowableActivityConfigService.detail(formModelVO.getModelId(), null);
    }
}
