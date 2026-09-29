package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.mapper.TemplateFormModelMapper;
import com.wuji.service.model.entity.FormModelEntity;
import com.wuji.service.model.entity.TemplateApplicationCategoryEntity;
import com.wuji.service.model.entity.TemplateFormModelEntity;
import com.wuji.service.model.vo.ApplicationCategoryVO;
import com.wuji.service.model.vo.FormModelVO;
import com.wuji.service.service.FormModelService;
import com.wuji.service.service.TemplateFormModelService;
import com.wuji.workflow.model.request.ModelRequest;
import com.wuji.workflow.model.vo.ModelCopyVO;
import com.wuji.workflow.service.ModelManageService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
 * @since 2024-09-18
 */
@Service
public class TemplateFormModelServiceImpl extends ServiceImpl<TemplateFormModelMapper, TemplateFormModelEntity>
        implements TemplateFormModelService {

    @Autowired
    private TemplateFormModelMapper templateFormModelMapper;

    @Autowired
    private FormModelService formModelService;

    @Autowired
    private ModelManageService modelManageService;

    @Override
    public void generateTemplate(String templateApplicationId, String sourceTemplateId,
                                 List<ApplicationCategoryVO> oldApplicationCategoryVOList, Boolean exist) {
        if (exist) {
            LambdaQueryWrapper<TemplateFormModelEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TemplateFormModelEntity::getApplicationId, templateApplicationId);
            templateFormModelMapper.delete(queryWrapper);
            modelManageService.deleteByApplication(templateApplicationId);
        }
        List<String> formIdList =
                oldApplicationCategoryVOList.stream().map(ApplicationCategoryVO::getId).collect(Collectors.toList());
        Map<String, ApplicationCategoryVO> categoryIdMap =
                oldApplicationCategoryVOList.stream().collect(Collectors.toMap(ApplicationCategoryVO::getId, c -> c));
        List<FormModelVO> formModelVOList = formModelService.getByFormIdList(formIdList, sourceTemplateId);
        if (CollectionUtils.isEmpty(formModelVOList)) {
            return;
        }
        List<TemplateFormModelEntity> templateFormModelEntityList = new ArrayList<>();

        for (FormModelVO formModelVO : formModelVOList) {
            TemplateFormModelEntity templateFormModelEntity = new TemplateFormModelEntity();
            ApplicationCategoryVO applicationCategoryVO = categoryIdMap.get(formModelVO.getFormId());
            String key = "form_process_" + formModelVO.getFormId() + "_" + templateApplicationId;
            templateFormModelEntity.setApplicationId(templateApplicationId);
            templateFormModelEntity.setBusinessType(key);
            templateFormModelEntity.setFormId(formModelVO.getFormId());
            templateFormModelEntity.setStatus(formModelVO.getStatus());
            // 生成流程引擎
            ModelCopyVO modelCopyVO = copyModel(templateApplicationId, key, applicationCategoryVO.getCategoryName(),
                    formModelVO.getModelId(), "template", exist);
            if (modelCopyVO != null) {
                templateFormModelEntity.setModelId(modelCopyVO.getModelId());
                templateFormModelEntityList.add(templateFormModelEntity);
            }
        }
        saveBatch(templateFormModelEntityList);
    }

    private ModelCopyVO copyModel(String templateApplicationId, String key, String categoryName, String modelId,
                                  String tenantId, Boolean exist) {
        ModelRequest modelRequest = new ModelRequest();
        modelRequest.setKey(key);
        modelRequest.setName(categoryName + "流程模型");
        modelRequest.setApplicationId(templateApplicationId);
        modelRequest.setTenantId(tenantId);
        modelRequest.setModelId(modelId);
        return modelManageService.copyModel(modelRequest, true, exist);
    }

    @Override
    public void useTemplate(String applicationId, String templateApplicationId, List<TemplateApplicationCategoryEntity> oldApplicationCategoryVOList) {
        List<String> formIdList = oldApplicationCategoryVOList.stream().map(TemplateApplicationCategoryEntity::getId)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(formIdList)) {
            return;
        }
        Map<String, TemplateApplicationCategoryEntity> categoryIdMap = oldApplicationCategoryVOList.stream()
                .collect(Collectors.toMap(TemplateApplicationCategoryEntity::getId, c -> c));
        LambdaQueryWrapper<TemplateFormModelEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(TemplateFormModelEntity::getFormId, formIdList);
        queryWrapper.eq(TemplateFormModelEntity::getApplicationId, templateApplicationId);
        List<TemplateFormModelEntity> formModelEntityList = templateFormModelMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(formModelEntityList)) {
            return;
        }
        List<FormModelEntity> templateFormModelEntityList = new ArrayList<>();
        for (TemplateFormModelEntity templateFormModelEntity : formModelEntityList) {
            TemplateApplicationCategoryEntity applicationCategoryEntity =
                    categoryIdMap.get(templateFormModelEntity.getFormId());
            String key = "form_process_" + applicationCategoryEntity.getId() + "_" + applicationId;
            ModelCopyVO modelCopyVO = copyModel(applicationId, key, applicationCategoryEntity.getCategoryName(),
                    templateFormModelEntity.getModelId(), UserUtils.getUser().getCompanyId().toString(), Boolean.FALSE);
            if (modelCopyVO == null) {
                continue;
            }
            FormModelEntity formModelEntity = new FormModelEntity();
            formModelEntity.setModelId(modelCopyVO.getModelId());
            formModelEntity.setBusinessType(key);
            formModelEntity.setFormId(applicationCategoryEntity.getId());
            formModelEntity.setApplicationId(applicationId);
            formModelEntity.setStatus(templateFormModelEntity.getStatus());
            templateFormModelEntityList.add(formModelEntity);
        }
        formModelService.saveBatch(templateFormModelEntityList);
    }
}
