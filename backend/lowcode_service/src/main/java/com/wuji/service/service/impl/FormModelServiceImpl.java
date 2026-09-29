package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractFormModelConverter;
import com.wuji.service.mapper.FormModelMapper;
import com.wuji.service.model.entity.ApplicationCategoryEntity;
import com.wuji.service.model.entity.FormModelEntity;
import com.wuji.service.model.request.FormModelCreateRequest;
import com.wuji.service.model.request.FormModelUpdateRequest;
import com.wuji.service.model.request.FormModelUpdateStatusRequest;
import com.wuji.service.model.vo.ApplicationCategoryVO;
import com.wuji.service.model.vo.FormModelVO;
import com.wuji.service.service.ApplicationCategoryService;
import com.wuji.service.service.FormModelService;
import com.wuji.workflow.model.request.ModelRequest;
import com.wuji.workflow.model.vo.ModelCopyVO;
import com.wuji.workflow.model.vo.ModelVO;
import com.wuji.workflow.service.FlowDesignerService;
import com.wuji.workflow.service.ModelManageService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 流程表单绑定表 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-09-03
 */
@Service
public class FormModelServiceImpl extends ServiceImpl<FormModelMapper, FormModelEntity> implements FormModelService {

    @Autowired
    private FlowDesignerService flowDesignerService;

    @Autowired
    private ModelManageService modelManageService;

    @Autowired
    private FormModelMapper formModelMapper;


    @Autowired
    private ApplicationCategoryService applicationCategoryService;


    @Override
    public FormModelVO createModel(FormModelCreateRequest formModelCreateRequest) {
        LambdaQueryWrapper<FormModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormModelEntity::getFormId, formModelCreateRequest.getFormId());
        queryWrapper.eq(FormModelEntity::getApplicationId, formModelCreateRequest.getApplicationId());
        FormModelEntity exist = formModelMapper.selectOne(queryWrapper);
        if (exist != null) {
            return AbstractFormModelConverter.INSTANCE.toVO(exist);
        }
        ApplicationCategoryVO applicationCategoryVO =
                applicationCategoryService.info(formModelCreateRequest.getFormId(),
                        formModelCreateRequest.getApplicationId());
        ModelRequest modelRequest = new ModelRequest();
        modelRequest.setKey(
                "form_process_" + formModelCreateRequest.getFormId() + "_" + formModelCreateRequest.getApplicationId());
        modelRequest.setName(applicationCategoryVO.getCategoryName() + "流程模型");
        modelRequest.setApplicationId(applicationCategoryVO.getApplicationId());
        String modelId = modelManageService.createModel(modelRequest);
        FormModelEntity formModelEntity = new FormModelEntity();
        formModelEntity.setBusinessType(modelRequest.getKey());
        formModelEntity.setFormId(formModelCreateRequest.getFormId());
        formModelEntity.setModelId(modelId);
        formModelEntity.setApplicationId(formModelCreateRequest.getApplicationId());
        formModelMapper.insert(formModelEntity);
        return AbstractFormModelConverter.INSTANCE.toVO(formModelEntity);
    }

    @Override
    public void updateModel(FormModelUpdateRequest formModelUpdateRequest) {
        LambdaQueryWrapper<FormModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormModelEntity::getFormId, formModelUpdateRequest.getFormId());
        queryWrapper.eq(FormModelEntity::getApplicationId, formModelUpdateRequest.getApplicationId());
        FormModelEntity exist = formModelMapper.selectOne(queryWrapper);
        if (exist == null) {
            return;
        }
        ModelRequest modelRequest = new ModelRequest();
        modelRequest.setModelId(exist.getModelId());
        modelRequest.setName(formModelUpdateRequest.getModelName() + "流程模型");
        modelRequest.setApplicationId(formModelUpdateRequest.getApplicationId());
        modelManageService.updateModel(modelRequest);
    }

    @Override
    public FormModelVO info(String formId, String applicationId) {
        LambdaQueryWrapper<FormModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormModelEntity::getFormId, formId);
        queryWrapper.eq(FormModelEntity::getApplicationId, applicationId);
        FormModelEntity exist = formModelMapper.selectOne(queryWrapper);
        if (exist == null) {
            return null;
        }
        return AbstractFormModelConverter.INSTANCE.toVO(exist);
    }


    @Override
    public void publishOrBind(String modelId, String formId, String applicationId) {
        LambdaQueryWrapper<FormModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormModelEntity::getFormId, formId);
        queryWrapper.eq(FormModelEntity::getApplicationId, applicationId);
        FormModelEntity exist = formModelMapper.selectOne(queryWrapper);
        exist.setModelId(modelId);
        formModelMapper.updateById(exist);
        modelManageService.publish(modelId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String newVersion(String modelId) {
        ModelVO modelJSON = flowDesignerService.getModelJSON(modelId);
        return flowDesignerService.designModule(modelId, modelJSON.getBpmnXml(), modelJSON.getConfig(), true);
    }

    @Override
    public List<FormModelVO> getByFormIdList(List<String> formIdList, String applicationId) {
        if (CollectionUtils.isEmpty(formIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<FormModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FormModelEntity::getFormId, formIdList);
        queryWrapper.eq(FormModelEntity::getApplicationId, applicationId);
        return formModelMapper.selectList(queryWrapper).stream().map(AbstractFormModelConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<FormModelVO> getByApplicationId(String applicationId) {
        LambdaQueryWrapper<FormModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormModelEntity::getApplicationId, applicationId);
        return formModelMapper.selectList(queryWrapper).stream().map(AbstractFormModelConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public void updateStatus(FormModelUpdateStatusRequest formModelUpdateStatusRequest) {
        LambdaQueryWrapper<FormModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormModelEntity::getApplicationId, formModelUpdateStatusRequest.getApplicationId());
        queryWrapper.eq(FormModelEntity::getFormId, formModelUpdateStatusRequest.getFormId());
        FormModelEntity formModelEntity = formModelMapper.selectOne(queryWrapper);
        if (formModelEntity == null) {
            return;
        }
        formModelEntity.setStatus(formModelUpdateStatusRequest.getStatus());
        formModelMapper.updateById(formModelEntity);
    }

    @Override
    public void copyApplication(String applicationId, String sourceApplicationId,
                                List<ApplicationCategoryEntity> flowableList) {
        List<String> formIdList =
                flowableList.stream().map(ApplicationCategoryEntity::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(formIdList)) {
            return;
        }
        Map<String, ApplicationCategoryEntity> categoryIdMap =
                flowableList.stream().collect(Collectors.toMap(ApplicationCategoryEntity::getId, c -> c));
        LambdaQueryWrapper<FormModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FormModelEntity::getFormId, formIdList);
        queryWrapper.eq(FormModelEntity::getApplicationId, sourceApplicationId);
        List<FormModelEntity> formModelEntityList = formModelMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(formModelEntityList)) {
            return;
        }
        List<FormModelEntity> templateFormModelEntityList = new ArrayList<>();
        for (FormModelEntity sourceFormModel : formModelEntityList) {
            ApplicationCategoryEntity applicationCategoryEntity = categoryIdMap.get(sourceFormModel.getFormId());
            String key = "form_process_" + applicationCategoryEntity.getId() + "_" + applicationId;
            ModelCopyVO modelCopyVO = copyModel(applicationId, key, applicationCategoryEntity.getCategoryName(),
                    sourceFormModel.getModelId(), UserUtils.getUser().getCompanyId().toString());
            if (modelCopyVO == null) {
                continue;
            }
            FormModelEntity formModelEntity = new FormModelEntity();
            formModelEntity.setModelId(modelCopyVO.getModelId());
            formModelEntity.setBusinessType(key);
            formModelEntity.setFormId(applicationCategoryEntity.getId());
            formModelEntity.setApplicationId(applicationId);
            formModelEntity.setStatus(sourceFormModel.getStatus());
            templateFormModelEntityList.add(formModelEntity);
        }
        saveBatch(templateFormModelEntityList);
    }

    @Override
    public FormModelVO getByFormId(String formId, String applicationId) {
        LambdaQueryWrapper<FormModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormModelEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormModelEntity::getFormId, formId);
        FormModelEntity exist = formModelMapper.selectOne(queryWrapper);
        return AbstractFormModelConverter.INSTANCE.toVO(exist);
    }

    @Override
    public List<FormModelVO> getByBusinessTypes(List<String> businessTypes) {
        if (CollectionUtils.isEmpty(businessTypes)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<FormModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FormModelEntity::getBusinessType, businessTypes);
        return formModelMapper.selectList(queryWrapper).stream().map(AbstractFormModelConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    private ModelCopyVO copyModel(String templateApplicationId, String key, String categoryName, String modelId,
                                  String tenantId) {
        ModelRequest modelRequest = new ModelRequest();
        modelRequest.setKey(key);
        modelRequest.setName(categoryName + "流程模型");
        modelRequest.setApplicationId(templateApplicationId);
        modelRequest.setTenantId(tenantId);
        modelRequest.setModelId(modelId);
        return modelManageService.copyModel(modelRequest, true, Boolean.FALSE);
    }
}
