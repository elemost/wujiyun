package com.wuji.workflow.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.utils.UserUtils;
import com.wuji.workflow.enums.WorkflowResultCode;
import com.wuji.workflow.exception.FlowableBizException;
import com.wuji.workflow.model.domain.FormModelDesignerDomain;
import com.wuji.workflow.model.domain.ModelMetaInfoDomain;
import com.wuji.workflow.model.flowable.ProcessModel;
import com.wuji.workflow.model.flowable.model.Node;
import com.wuji.workflow.model.info.FlowableAssigneeConfig;
import com.wuji.workflow.model.request.ModelListRequest;
import com.wuji.workflow.model.request.ModelRequest;
import com.wuji.workflow.model.vo.ModelCopyVO;
import com.wuji.workflow.model.vo.ModelVO;
import com.wuji.workflow.service.FlowDesignerService;
import com.wuji.workflow.service.FlowableConfigService;
import com.wuji.workflow.service.ModelManageService;
import com.wuji.workflow.utils.ModelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ModelQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ModelManageServiceImpl implements ModelManageService {

    @Resource
    private RepositoryService repositoryService;

    @Autowired
    private FlowDesignerService flowDesignerService;

    @Autowired
    private FlowableConfigService flowableConfigService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String createModel(ModelRequest modelRequest) {
        List<Model> sameList = repositoryService.createModelQuery().modelKey(modelRequest.getKey()).list();
        if (CollectionUtils.isNotEmpty(sameList)) {
            return sameList.get(0).getId();
        }
        Model model = repositoryService.newModel();
        model.setCategory(modelRequest.getApplicationId());
        model.setKey(modelRequest.getKey());
        ModelMetaInfoDomain modelMetaInfoDomain = new ModelMetaInfoDomain();
        buildMetaInfo(modelRequest, modelMetaInfoDomain);
        model.setMetaInfo(JSONObject.toJSONString(modelMetaInfoDomain));
        model.setName(modelRequest.getName());
        model.setTenantId(StringUtils.isNotEmpty(modelRequest.getTenantId()) ? modelRequest.getTenantId() :
                UserUtils.getUser().getCompanyId().toString());
        ModelQuery modelQuery = repositoryService.createModelQuery();
        modelMetaInfoDomain.setCreateUser(UserUtils.getUser().getUserId());
        List<Model> list = modelQuery.modelKey(modelRequest.getKey()).list();
        if (!list.isEmpty()) {
            throw new FlowableBizException(WorkflowResultCode.MODULE_KET_SAME);
        } else {
            // 保存模型到act_re_model表
            repositoryService.saveModel(model);
        }
        list = modelQuery.modelKey(modelRequest.getKey()).list();
        return list.get(0).getId();
    }

    private static void buildMetaInfo(ModelRequest modelRequest, ModelMetaInfoDomain modelMetaInfoDomain) {
        modelMetaInfoDomain.setName(modelRequest.getName());
        modelMetaInfoDomain.setDescription(modelRequest.getDescription());
    }

    @Override
    public void updateModel(ModelRequest modelRequest) {
        Model model = repositoryService.getModel(modelRequest.getModelId());
        if (ObjectUtil.isNull(model)) {
            throw new RuntimeException("流程模型不存在！");
        }
        model.setCategory(modelRequest.getApplicationId());
        ModelMetaInfoDomain modelMetaInfoDomain =
                JSONObject.parseObject(model.getMetaInfo(), ModelMetaInfoDomain.class);
        buildMetaInfo(modelRequest, modelMetaInfoDomain);
        model.setMetaInfo(JSONObject.toJSONString(modelMetaInfoDomain));
        model.setTenantId(UserUtils.getUser().getCompanyId().toString());
        // 保存流程模型
        repositoryService.saveModel(model);
    }

    @Override
    public void removeModel(String modelId) {
        repositoryService.deleteModel(modelId);
    }

    @Override
    public String publish(String modelId) {
        try {
            Model model = repositoryService.getModel(modelId);
            // 已发布直接返回
            if (StringUtils.isNotEmpty(model.getDeploymentId())) {
                return modelId;
            }
            byte[] bpmnBytes = repositoryService.getModelEditorSource(modelId);
            String bpmnXml = StringUtils.toEncodedString(bpmnBytes, StandardCharsets.UTF_8);
            BpmnModel bpmnModel = ModelUtils.getBpmnModel(bpmnXml);
            Deployment deploy = repositoryService.createDeployment().category(model.getCategory()).name(model.getName())
                    .key(model.getKey()).addBpmnModel(model.getKey() + ".bpmn20.xml", bpmnModel)
                    .tenantId(model.getTenantId()).deploy();

            ProcessDefinition procDef =
                    repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();
            // 修改流程定义的分类，便于搜索流程
            repositoryService.setProcessDefinitionCategory(procDef.getId(), model.getCategory());
            // 作为是否已发布标识
            model.setDeploymentId(deploy.getId());
            repositoryService.saveModel(model);
            return procDef.getId();
        } catch (Exception e) {
            log.error(WorkflowResultCode.PUBLISH_FLOWABLE_ERROR.getMessage(), e);
            throw new FlowableBizException(WorkflowResultCode.PUBLISH_FLOWABLE_ERROR);
        }
    }

    @Override
    public QueryPageVO<ModelVO> getModelList(ModelListRequest modelListRequest) {
        ModelQuery query =
                repositoryService.createModelQuery().modelCategory(modelListRequest.getApplicationId()).latestVersion();
        if (StringUtils.isNotEmpty(modelListRequest.getKey())) {
            query.modelKey(modelListRequest.getKey());
        }
        if (StringUtils.isNotEmpty(modelListRequest.getName())) {
            query.modelName("%" + modelListRequest.getName() + "%");
        }
        int start = (modelListRequest.getPageNum() - 1) * modelListRequest.getPageSize();
        List<Model> modelList = query.orderByCreateTime().desc().listPage(start, modelListRequest.getPageSize());
        int total = repositoryService.createModelQuery().list().size();
        List<ModelVO> modelVoList = new ArrayList<>(modelList.size());
        for (Model model : modelList) {
            ModelVO modelVo = new ModelVO();
            modelVo.setModelId(model.getId());
            modelVo.setModelName(model.getName());
            modelVo.setModelKey(model.getKey());
            modelVo.setCategory(model.getCategory());
            modelVo.setCreateTime(model.getCreateTime());
            modelVo.setVersion(model.getVersion());
            modelVo.setPublish(model.getDeploymentId() != null);
            ModelMetaInfoDomain metaInfo = JSONObject.parseObject(model.getMetaInfo(), ModelMetaInfoDomain.class);
            if (metaInfo != null) {
                modelVo.setDescription(metaInfo.getDescription());
                modelVo.setFormType(metaInfo.getFormType());
                modelVo.setFormId(metaInfo.getFormId());
            }
            modelVoList.add(modelVo);
        }
        return new QueryPageVO<>(total, modelVoList);
    }

    @Override
    public List<ModelVO> getModelByKey(String key) {
        ModelQuery query = repositoryService.createModelQuery().modelKey(key);
        List<Model> modelList = query.orderByModelVersion().desc().list();
        List<ModelVO> modelVoList = new ArrayList<>(modelList.size());
        for (Model model : modelList) {
            ModelVO modelVo = getModelVO(model);
            modelVoList.add(modelVo);
        }
        return modelVoList;
    }

    private static ModelVO getModelVO(Model model) {
        ModelVO modelVo = new ModelVO();
        modelVo.setModelId(model.getId());
        modelVo.setDeploymentId(model.getDeploymentId());
        modelVo.setModelName(model.getName());
        modelVo.setModelKey(model.getKey());
        modelVo.setCategory(model.getCategory());
        modelVo.setCreateTime(model.getCreateTime());
        modelVo.setVersion(model.getVersion());
        modelVo.setPublish(model.getDeploymentId() != null);
        ModelMetaInfoDomain metaInfo = JSONObject.parseObject(model.getMetaInfo(), ModelMetaInfoDomain.class);
        if (metaInfo != null) {
            modelVo.setDescription(metaInfo.getDescription());
            modelVo.setFormType(metaInfo.getFormType());
            modelVo.setFormId(metaInfo.getFormId());
        }
        return modelVo;
    }

    @Override
    public ModelVO info(String modelId) {
        Model model = repositoryService.createModelQuery().modelId(modelId).singleResult();
        return getModelVO(model);
    }

    @Override
    public ModelVO infoByProcessDefinitionId(String processDefinitionId) {
        ProcessDefinition procDef =
                repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId)
                        .singleResult();
        Model model = repositoryService.createModelQuery().deploymentId(procDef.getDeploymentId()).singleResult();
        return getModelVO(model);
    }

    @Override
    public ModelCopyVO copyModel(ModelRequest modelCopyRequest, Boolean needDefault, Boolean exist) {
        ModelCopyVO modelCopyVO = new ModelCopyVO();
        ModelVO modelVO = flowDesignerService.getModelJSON(modelCopyRequest.getModelId());
        String modelId = createModel(modelCopyRequest);
        FormModelDesignerDomain formModelDesigner = modelVO.getConfig();
        if (formModelDesigner == null) {
            return null;
        }
        if (needDefault) {
            formModelDesigner.getFlowableTaskConfigList().forEach(flowableActivityConfigDomain -> {
                flowableActivityConfigDomain.setAssigneeConfigList(FlowableAssigneeConfig.getDefaultConfig());
                flowableActivityConfigDomain.setCopyConfigList(null);
            });
        }
        ProcessModel processModel = new ProcessModel();
        try {
            String config = objectMapper.writer().writeValueAsString(formModelDesigner.getProcess());
            Node node = objectMapper.readValue(config, Node.class);
            processModel.setProcess(node);
        } catch (Exception e) {
            log.error("转化失败", e);
        }
        processModel.setModelKey(modelCopyRequest.getKey());
        processModel.setModelName(modelCopyRequest.getName());
        flowDesignerService.designModule(modelId, processModel.toBpmnModel(), formModelDesigner, Boolean.FALSE);

        if (StringUtils.isNotEmpty(modelVO.getDeploymentId())) {
            publish(modelId);
        }
        modelCopyVO.setModelId(modelId);
        return modelCopyVO;
    }

    @Override
    public ModelCopyVO createAndPublishModel(ModelRequest modelCopyRequest, Boolean needDefault, Boolean exist) {
        ModelCopyVO modelCopyVO = new ModelCopyVO();
        String modelId = createModel(modelCopyRequest);
        FormModelDesignerDomain formModelDesigner = modelCopyRequest.getConfig();
        if (formModelDesigner == null) {
            return null;
        }
        if (needDefault) {
            formModelDesigner.getFlowableTaskConfigList().forEach(flowableActivityConfigDomain -> {
                flowableActivityConfigDomain.setAssigneeConfigList(FlowableAssigneeConfig.getDefaultConfig());
                flowableActivityConfigDomain.setCopyConfigList(null);
            });
        }
        ProcessModel processModel = new ProcessModel();
        try {
            String config = objectMapper.writer().writeValueAsString(formModelDesigner.getProcess());
            Node node = objectMapper.readValue(config, Node.class);
            processModel.setProcess(node);
        } catch (Exception e) {
            log.error("转化失败", e);
        }
        processModel.setModelKey(modelCopyRequest.getKey());
        processModel.setModelName(modelCopyRequest.getName());
        flowDesignerService.designModule(modelId, processModel.toBpmnModel(), formModelDesigner, Boolean.FALSE);
        publish(modelId);
        modelCopyVO.setModelId(modelId);
        return modelCopyVO;
    }

    @Override
    public void deleteByApplication(String applicationId) {
        List<Model> list = repositoryService.createModelQuery().modelCategory(applicationId).list();
        for (Model model : list) {
            if (StringUtils.isNotEmpty(model.getDeploymentId())) {
                repositoryService.deleteDeployment(model.getDeploymentId());
            }
            repositoryService.deleteModel(model.getId());
        }
        List<String> modelIdList = list.stream().map(Model::getId).collect(Collectors.toList());
        flowableConfigService.delete(modelIdList);
    }

}
