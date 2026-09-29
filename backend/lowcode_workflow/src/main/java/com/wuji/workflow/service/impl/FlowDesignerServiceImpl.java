package com.wuji.workflow.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.wuji.common.utils.UserUtils;
import com.wuji.workflow.enums.WorkflowResultCode;
import com.wuji.workflow.exception.FlowableBizException;
import com.wuji.workflow.model.domain.FormModelDesignerDomain;
import com.wuji.workflow.model.domain.ModelMetaInfoDomain;
import com.wuji.workflow.model.vo.FlowableConfigVO;
import com.wuji.workflow.model.vo.ModelVO;
import com.wuji.workflow.service.FlowDesignerService;
import com.wuji.workflow.service.FlowableConfigService;
import com.wuji.workflow.utils.ModelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class FlowDesignerServiceImpl implements FlowDesignerService {

    @Resource
    private RepositoryService repositoryService;

    @Autowired
    private FlowableConfigService flowableConfigService;

    @Override
    public String designModule(String modelId, String bpmnXml, FormModelDesignerDomain formModelDesignerDomain,
                               Boolean newVersion) {
        // 保存 BPMN XML
        if (StringUtils.isEmpty(bpmnXml)) {
            return design(modelId, null, formModelDesignerDomain, newVersion, null);
        } else {
            // 保存 BPMN XML
            byte[] bpmnXmlBytes = StringUtils.getBytes(bpmnXml, StandardCharsets.UTF_8);
            BpmnModel bpmnModel = ModelUtils.getBpmnModel(bpmnXml);
            return design(modelId, bpmnModel, formModelDesignerDomain, newVersion, bpmnXmlBytes);
        }
    }

    @Override
    public String designModule(String modelId, BpmnModel bpmnModel, FormModelDesignerDomain formModelDesignerDomain,
                               Boolean newVersion) {
        // 保存 BPMN XML
        byte[] xmlBytes = new BpmnXMLConverter().convertToXML(bpmnModel);
        return design(modelId, bpmnModel, formModelDesignerDomain, newVersion, xmlBytes);
    }

    private String design(String modelId, BpmnModel bpmnModel, FormModelDesignerDomain formModelDesignerDomain,
                          Boolean newVersion, byte[] xmlBytes) {
        // 查询模型信息
        Model model = repositoryService.getModel(modelId);
        if (ObjectUtil.isNull(model)) {
            throw new FlowableBizException(WorkflowResultCode.MODEL_NOT_EXIST);
        }
        List<Model> list = repositoryService.createModelQuery().modelKey(model.getKey()).latestVersion().list();
        Model newModel;
        if (newVersion) {
            Model model1 = list.get(0);
            newModel = repositoryService.newModel();
            newModel.setName(model1.getName());
            newModel.setKey(model.getKey());
            newModel.setCategory(model.getCategory());
            newModel.setMetaInfo(model.getMetaInfo());
            newModel.setTenantId(UserUtils.getUser().getCompanyId().toString());
            newModel.setVersion(model1.getVersion() + 1);
            newModel.setDeploymentId(null);
        } else {
            if (ObjectUtil.isEmpty(bpmnModel)) {
                throw new FlowableBizException(WorkflowResultCode.MODEL_NOT_EXIST);
            }
            String processName = bpmnModel.getMainProcess().getName();
            newModel = model;
            // 设置流程名称
            newModel.setName(processName);
        }
        // 保存流程模型
        repositoryService.saveModel(newModel);
        repositoryService.addModelEditorSource(newModel.getId(), xmlBytes);
        if (formModelDesignerDomain != null) {
            flowableConfigService.save(newModel.getId(), formModelDesignerDomain);
        }
        return newModel.getId();
    }

    @Override
    public ModelVO getModelJSON(String modelId) {
        // 获取流程模型
        Model model = repositoryService.getModel(modelId);
        return getModelVO(model);
    }

    @Override
    public ModelVO getModelJSONByDeployId(String deployId) {
        Model model = repositoryService.createModelQuery().deploymentId(deployId).singleResult();
        if (model == null) {
            throw new FlowableBizException(WorkflowResultCode.MODEL_NOT_EXIST);
        }
        return getModelVO(model);
    }

    @Override
    public void delete(String applicationId) {
        // List<Model> list = repositoryService.createModelQuery().modelCategory(applicationId).list();
        // for (Model model : list) {
        //
        // }
        // repositoryService.de();
        //
        //
        // if (processDefinition == null) {
        //     System.out.println("流程定义不存在：" + applicationId);
        //     return;
        // }
    }

    private ModelVO getModelVO(Model model) {
        if (ObjectUtil.isNull(model)) {
            throw new FlowableBizException(WorkflowResultCode.MODEL_NOT_EXIST);
        }
        // 获取流程图
        String bpmnXml = queryBpmnXmlById(model.getId());
        ModelVO modelVo = new ModelVO();
        modelVo.setModelId(model.getId());
        modelVo.setModelName(model.getName());
        modelVo.setModelKey(model.getKey());
        modelVo.setCategory(model.getCategory());
        modelVo.setCreateTime(model.getCreateTime());
        modelVo.setVersion(model.getVersion());
        modelVo.setBpmnXml(bpmnXml);
        modelVo.setPublish(model.getDeploymentId() != null);
        modelVo.setDeploymentId(model.getDeploymentId());
        ModelMetaInfoDomain metaInfo = JSONObject.parseObject(model.getMetaInfo(), ModelMetaInfoDomain.class);
        if (metaInfo != null) {
            modelVo.setDescription(metaInfo.getDescription());
            modelVo.setFormType(metaInfo.getFormType());
            modelVo.setFormId(metaInfo.getFormId());
        }
        FlowableConfigVO flowableConfigVO = flowableConfigService.getByModelId(model.getId());
        modelVo.setConfig(flowableConfigVO.getConfig());
        return modelVo;
    }

    public String queryBpmnXmlById(String modelId) {
        byte[] bpmnBytes = repositoryService.getModelEditorSource(modelId);
        return StrUtil.utf8Str(bpmnBytes);
    }

}
