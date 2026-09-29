package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.model.entity.BaseUuidEntity;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractFormRuleConverter;
import com.wuji.service.enums.FormRuleStateEnum;
import com.wuji.service.mapper.FormRuleMapper;
import com.wuji.service.model.entity.FormRuleEntity;
import com.wuji.service.model.request.FormRuleCreateRequest;
import com.wuji.service.model.request.FormRuleSortRequest;
import com.wuji.service.model.request.FormRuleUpdateRequest;
import com.wuji.service.model.vo.FormRuleVO;
import com.wuji.service.model.vo.TemplateFormRuleVO;
import com.wuji.service.service.FormRuleService;
import com.wuji.service.service.TemplateFormRuleService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-12-16
 */
@Service
public class FormRuleServiceImpl extends ServiceImpl<FormRuleMapper, FormRuleEntity> implements FormRuleService {

    @Autowired
    private FormRuleMapper formRuleMapper;

    @Autowired
    private TemplateFormRuleService templateFormRuleService;

    @Override
    public String create(FormRuleCreateRequest formRuleCreateRequest) {
        FormRuleEntity formRuleEntity = AbstractFormRuleConverter.INSTANCE.toEntity(formRuleCreateRequest);
        formRuleEntity.setId(ObjectId.getGuid());
        formRuleEntity.setCreator(UserUtils.getUser().getUserId());
        formRuleEntity.setModifier(UserUtils.getUser().getUserId());
        formRuleEntity.setState(FormRuleStateEnum.UP.name());
        formRuleMapper.insert(formRuleEntity);
        return formRuleEntity.getId();
    }

    @Override
    public void update(FormRuleUpdateRequest formRuleUpdateRequest) {
        FormRuleEntity formRuleEntity = AbstractFormRuleConverter.INSTANCE.toEntity(formRuleUpdateRequest);
        formRuleEntity.setModifier(UserUtils.getUser().getNickName());
        LambdaQueryWrapper<FormRuleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormRuleEntity::getFormId, formRuleUpdateRequest.getFormId());
        queryWrapper.eq(FormRuleEntity::getApplicationId, formRuleUpdateRequest.getApplicationId());
        queryWrapper.eq(FormRuleEntity::getId, formRuleUpdateRequest.getId());
        formRuleMapper.update(formRuleEntity, queryWrapper);
    }

    @Override
    public void updateStatus(FormRuleUpdateRequest formRuleUpdateRequest) {
        FormRuleEntity formRuleEntity = AbstractFormRuleConverter.INSTANCE.toEntity(formRuleUpdateRequest);
        formRuleEntity.setModifier(UserUtils.getUser().getNickName());
        LambdaQueryWrapper<FormRuleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormRuleEntity::getFormId, formRuleUpdateRequest.getFormId());
        queryWrapper.eq(FormRuleEntity::getApplicationId, formRuleUpdateRequest.getApplicationId());
        queryWrapper.eq(FormRuleEntity::getId, formRuleUpdateRequest.getId());
        formRuleMapper.update(formRuleEntity, queryWrapper);
    }

    @Override
    public FormRuleVO info(String id, String applicationId, String formId) {
        LambdaQueryWrapper<FormRuleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormRuleEntity::getFormId, formId);
        queryWrapper.eq(FormRuleEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormRuleEntity::getId, id);
        FormRuleEntity formRuleEntity = formRuleMapper.selectOne(queryWrapper);
        return AbstractFormRuleConverter.INSTANCE.toVO(formRuleEntity);
    }

    @Override
    public List<FormRuleVO> queryList(String formId, String applicationId, String ruleType, String state) {
        LambdaQueryWrapper<FormRuleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormRuleEntity::getFormId, formId);
        queryWrapper.eq(StringUtils.isNotEmpty(state), FormRuleEntity::getState, state);
        queryWrapper.eq(FormRuleEntity::getApplicationId, applicationId);
        queryWrapper.eq(StringUtils.isNotEmpty(ruleType), FormRuleEntity::getRuleType, ruleType);
        queryWrapper.eq(FormRuleEntity::getDeleted, Boolean.FALSE);
        queryWrapper.orderByDesc(FormRuleEntity::getSort);
        queryWrapper.orderByAsc(FormRuleEntity::getId);
        List<FormRuleEntity> formRuleEntities = formRuleMapper.selectList(queryWrapper);
        return formRuleEntities.stream().map(AbstractFormRuleConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    @Override
    public List<FormRuleVO> queryListByApplication(String applicationId) {
        LambdaQueryWrapper<FormRuleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormRuleEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormRuleEntity::getDeleted, Boolean.FALSE);
        queryWrapper.orderByDesc(FormRuleEntity::getSort);
        queryWrapper.orderByAsc(FormRuleEntity::getId);
        List<FormRuleEntity> formRuleEntities = formRuleMapper.selectList(queryWrapper);
        return formRuleEntities.stream().map(AbstractFormRuleConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    @Override
    public void delete(String id, String applicationId, String formId) {
        FormRuleEntity formRuleEntity = new FormRuleEntity();
        formRuleEntity.setDeleted(Boolean.TRUE);
        formRuleEntity.setModifier(UserUtils.getUser().getNickName());
        LambdaQueryWrapper<FormRuleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormRuleEntity::getFormId, formId);
        queryWrapper.eq(FormRuleEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormRuleEntity::getId, id);
        formRuleMapper.update(formRuleEntity, queryWrapper);
    }

    @Override
    public void sort(FormRuleSortRequest formRuleSortRequest) {
        LambdaQueryWrapper<FormRuleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormRuleEntity::getApplicationId, formRuleSortRequest.getApplicationId());
        queryWrapper.eq(FormRuleEntity::getFormId, formRuleSortRequest.getFormId());
        queryWrapper.in(FormRuleEntity::getId, formRuleSortRequest.getIdList());
        List<FormRuleEntity> formRuleEntities = formRuleMapper.selectList(queryWrapper);
        Map<String, FormRuleEntity> formRuleMap =
                formRuleEntities.stream().collect(Collectors.toMap(BaseUuidEntity::getId, c -> c));
        int sort = formRuleEntities.size();
        for (String id : formRuleSortRequest.getIdList()) {
            FormRuleEntity formRuleEntity = formRuleMap.get(id);
            if (formRuleEntity != null) {
                formRuleEntity.setSort(sort);
            }
            sort--;
        }
        formRuleMapper.batchUpdate(formRuleSortRequest.getApplicationId(), formRuleEntities,
                formRuleSortRequest.getFormId());
    }

    @Override
    public Map<String, List<FormRuleVO>> queryAllList(String applicationId, String formId) {
        LambdaQueryWrapper<FormRuleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormRuleEntity::getFormId, formId);
        queryWrapper.eq(FormRuleEntity::getState, "UP");
        queryWrapper.eq(FormRuleEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormRuleEntity::getDeleted, Boolean.FALSE);
        queryWrapper.orderByDesc(FormRuleEntity::getSort);
        queryWrapper.orderByAsc(FormRuleEntity::getId);
        List<FormRuleEntity> formRuleEntities = formRuleMapper.selectList(queryWrapper);
        return formRuleEntities.stream().map(AbstractFormRuleConverter.INSTANCE::toVO).collect(Collectors.toList())
                .stream().collect(Collectors.groupingBy(FormRuleVO::getRuleType));
    }

    @Override
    public void userTemplate(String applicationId, String templateApplicationId) {
        List<TemplateFormRuleVO> templateFormRuleVOList =
                templateFormRuleService.getByApplicationId(templateApplicationId);
        if (CollectionUtils.isEmpty(templateFormRuleVOList)) {
            return;
        }
        List<FormRuleEntity> formRuleEntityList = new ArrayList<>();
        for (TemplateFormRuleVO templateFormRuleVO : templateFormRuleVOList) {
            FormRuleEntity formRuleEntity = AbstractFormRuleConverter.INSTANCE.toEntity(templateFormRuleVO);
            formRuleEntity.setApplicationId(applicationId);
            formRuleEntity.setCreator(UserUtils.getUser().getNickName());
            formRuleEntity.setModifier(UserUtils.getUser().getNickName());
            formRuleEntityList.add(formRuleEntity);
        }
        saveBatch(formRuleEntityList);
    }

    @Override
    public void copyApplication(String applicationId, String sourceApplicationId) {
        List<FormRuleVO> formRuleVOS = queryListByApplication(sourceApplicationId);
        if (CollectionUtils.isEmpty(formRuleVOS)) {
            return;
        }
        List<FormRuleEntity> formRuleEntityList = new ArrayList<>();
        for (FormRuleVO formRuleVO : formRuleVOS) {
            FormRuleEntity formRuleEntity = AbstractFormRuleConverter.INSTANCE.toEntity(formRuleVO);
            formRuleEntity.setApplicationId(applicationId);
            formRuleEntity.setCreator(UserUtils.getUser().getNickName());
            formRuleEntity.setModifier(UserUtils.getUser().getNickName());
            formRuleEntityList.add(formRuleEntity);
        }
        saveBatch(formRuleEntityList);
    }
}
