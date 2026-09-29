package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.model.entity.BaseUuidEntity;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractFormInfoConverter;
import com.wuji.service.mapper.FormInfoMapper;
import com.wuji.service.model.entity.FormInfoEntity;
import com.wuji.service.model.request.FormInfoCopyRequest;
import com.wuji.service.model.request.FormInfoCreateRequest;
import com.wuji.service.model.request.FormInfoRequest;
import com.wuji.service.model.request.FormInfoSortRequest;
import com.wuji.service.model.request.FormInfoUpdateRequest;
import com.wuji.service.model.vo.FormInfoVO;
import com.wuji.service.model.vo.TemplateFormInfoVO;
import com.wuji.service.service.FormInfoService;
import com.wuji.service.service.TemplateFormInfoService;
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
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-09-09
 */
@Service
public class FormInfoServiceImpl extends ServiceImpl<FormInfoMapper, FormInfoEntity> implements FormInfoService {

    @Autowired
    private FormInfoMapper formInfoMapper;

    @Autowired
    private TemplateFormInfoService templateFormInfoService;

    @Override
    public String create(FormInfoCreateRequest formInfoCreateRequest) {
        FormInfoEntity formInfoEntity = AbstractFormInfoConverter.INSTANCE.toEntity(formInfoCreateRequest);
        formInfoEntity.setId(ObjectId.getGuid());
        formInfoEntity.setCreatorName(UserUtils.getUser().getNickName());
        formInfoEntity.setModifierName(UserUtils.getUser().getNickName());
        QueryWrapper<FormInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("form_id", formInfoCreateRequest.getFormId());
        queryWrapper.eq("application_id", formInfoCreateRequest.getApplicationId());
        queryWrapper.eq("deleted", Boolean.FALSE);
        Integer sort = formInfoMapper.maxSort(queryWrapper);
        formInfoEntity.setSort(sort + 1);
        formInfoMapper.insert(formInfoEntity);
        return formInfoEntity.getId();
    }

    @Override
    public void update(FormInfoUpdateRequest formInfoUpdateRequest) {
        LambdaQueryWrapper<FormInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormInfoEntity::getFormId, formInfoUpdateRequest.getFormId());
        queryWrapper.eq(FormInfoEntity::getApplicationId, formInfoUpdateRequest.getApplicationId());
        queryWrapper.eq(FormInfoEntity::getId, formInfoUpdateRequest.getId());
        FormInfoEntity formInfoEntity = AbstractFormInfoConverter.INSTANCE.toEntity(formInfoUpdateRequest);
        formInfoEntity.setModifierName(UserUtils.getUser().getNickName());
        formInfoMapper.update(formInfoEntity, queryWrapper);
    }

    @Override
    public FormInfoVO info(String id, String applicationId, String formId) {
        LambdaQueryWrapper<FormInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormInfoEntity::getFormId, formId);
        queryWrapper.eq(FormInfoEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormInfoEntity::getId, id);
        FormInfoEntity formInfoEntity = formInfoMapper.selectOne(queryWrapper);
        return AbstractFormInfoConverter.INSTANCE.toVO(formInfoEntity);
    }

    @Override
    public void delete(String id, String applicationId, String formId) {
        LambdaQueryWrapper<FormInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormInfoEntity::getFormId, formId);
        queryWrapper.eq(FormInfoEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormInfoEntity::getId, id);
        FormInfoEntity formInfoEntity = new FormInfoEntity();
        formInfoEntity.setModifierName(UserUtils.getUser().getNickName());
        formInfoEntity.setDeleted(Boolean.TRUE);
        formInfoMapper.update(formInfoEntity, queryWrapper);
    }

    @Override
    public void setDefault(String id, String applicationId, String formId, Boolean defaultConfig) {
        LambdaQueryWrapper<FormInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormInfoEntity::getFormId, formId);
        queryWrapper.eq(FormInfoEntity::getApplicationId, applicationId);
        FormInfoEntity setFalse = new FormInfoEntity();
        setFalse.setDefaultConfig(Boolean.FALSE);
        formInfoMapper.update(setFalse, queryWrapper);
        queryWrapper.eq(FormInfoEntity::getId, id);
        FormInfoEntity setTrue = new FormInfoEntity();
        setTrue.setDefaultConfig(defaultConfig);
        formInfoMapper.update(setTrue, queryWrapper);
    }

    @Override
    public void setEnable(String id, String applicationId, String formId, Boolean enable) {
        LambdaQueryWrapper<FormInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormInfoEntity::getFormId, formId);
        queryWrapper.eq(FormInfoEntity::getId, id);
        queryWrapper.eq(FormInfoEntity::getApplicationId, applicationId);
        FormInfoEntity formInfoEntity = new FormInfoEntity();
        formInfoEntity.setEnable(enable);
        formInfoEntity.setModifierName(UserUtils.getUser().getNickName());
        formInfoMapper.update(formInfoEntity, queryWrapper);
    }

    @Override
    public List<FormInfoVO> queryList(FormInfoRequest formInfoRequest) {
        LambdaQueryWrapper<FormInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormInfoEntity::getFormId, formInfoRequest.getFormId());
        queryWrapper.eq(FormInfoEntity::getApplicationId, formInfoRequest.getApplicationId());
        queryWrapper.eq(FormInfoEntity::getDeleted, Boolean.FALSE);
        queryWrapper.orderByDesc(FormInfoEntity::getSort, FormInfoEntity::getCreateTime);
        List<FormInfoEntity> formInfoEntities = formInfoMapper.selectList(queryWrapper);
        return formInfoEntities.stream().map(AbstractFormInfoConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    @Override
    public FormInfoVO getDefault(String applicationId, String formId) {
        LambdaQueryWrapper<FormInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormInfoEntity::getFormId, formId);
        queryWrapper.eq(FormInfoEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormInfoEntity::getDefaultConfig, Boolean.TRUE);
        queryWrapper.eq(FormInfoEntity::getDeleted, Boolean.FALSE);
        FormInfoEntity formInfoEntity = formInfoMapper.selectOne(queryWrapper);
        return AbstractFormInfoConverter.INSTANCE.toVO(formInfoEntity);
    }

    @Override
    public List<FormInfoVO> queryByApplicationId(String applicationId) {
        LambdaQueryWrapper<FormInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormInfoEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormInfoEntity::getDeleted, Boolean.FALSE);
        List<FormInfoEntity> formInfoEntities = formInfoMapper.selectList(queryWrapper);
        return formInfoEntities.stream().map(AbstractFormInfoConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    @Override
    public void userTemplate(String applicationId, String templateApplicationId) {
        List<TemplateFormInfoVO> templateFormInfoVOList =
                templateFormInfoService.getByApplicationId(templateApplicationId);
        if (CollectionUtils.isEmpty(templateFormInfoVOList)) {
            return;
        }
        List<FormInfoEntity> formInfoEntities = new ArrayList<>();
        for (TemplateFormInfoVO templateFormInfoVO : templateFormInfoVOList) {
            FormInfoEntity formInfoEntity = AbstractFormInfoConverter.INSTANCE.toEntity(templateFormInfoVO);
            formInfoEntity.setApplicationId(applicationId);
            formInfoEntity.setCreatorName(UserUtils.getUser().getNickName());
            formInfoEntity.setModifierName(UserUtils.getUser().getNickName());
            formInfoEntities.add(formInfoEntity);
        }
        saveBatch(formInfoEntities);
    }

    @Override
    public void copyApplication(String applicationId, String sourceApplicationId) {
        List<FormInfoVO> formInfoVOS = queryByApplicationId(sourceApplicationId);
        if (CollectionUtils.isEmpty(formInfoVOS)) {
            return;
        }
        List<FormInfoEntity> formInfoEntities = new ArrayList<>();
        for (FormInfoVO formInfoVO : formInfoVOS) {
            FormInfoEntity formInfoEntity = AbstractFormInfoConverter.INSTANCE.toEntity(formInfoVO);
            formInfoEntity.setApplicationId(applicationId);
            formInfoEntity.setCreatorName(UserUtils.getUser().getNickName());
            formInfoEntity.setModifierName(UserUtils.getUser().getNickName());
            formInfoEntities.add(formInfoEntity);
        }
        saveBatch(formInfoEntities);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sort(FormInfoSortRequest formInfoSortRequest) {
        LambdaQueryWrapper<FormInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormInfoEntity::getFormId, formInfoSortRequest.getFormId());
        queryWrapper.eq(FormInfoEntity::getApplicationId, formInfoSortRequest.getApplicationId());
        queryWrapper.in(FormInfoEntity::getId, formInfoSortRequest.getIdList());
        List<FormInfoEntity> formInfoEntities = formInfoMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(formInfoEntities)) {
            return;
        }
        Map<String, FormInfoEntity> formInfoEntityMap =
                formInfoEntities.stream().collect(Collectors.toMap(BaseUuidEntity::getId, c -> c));
        int max = formInfoSortRequest.getIdList().size();
        for (String id : formInfoSortRequest.getIdList()) {
            FormInfoEntity formInfoEntity = formInfoEntityMap.get(id);
            if (formInfoEntity != null) {
                formInfoEntity.setSort(max);
            }
            max--;
        }
        formInfoMapper.batchUpdate(formInfoSortRequest.getApplicationId(), formInfoEntities,
                formInfoSortRequest.getFormId());
    }

    @Override
    public String copy(FormInfoCopyRequest formInfoCopyRequest) {
        LambdaQueryWrapper<FormInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormInfoEntity::getFormId, formInfoCopyRequest.getFormId());
        queryWrapper.eq(FormInfoEntity::getId, formInfoCopyRequest.getId());
        queryWrapper.eq(FormInfoEntity::getApplicationId, formInfoCopyRequest.getApplicationId());
        FormInfoEntity formInfoEntity = formInfoMapper.selectOne(queryWrapper);
        FormInfoCreateRequest formInfoCreateRequest = AbstractFormInfoConverter.INSTANCE.toRequest(formInfoEntity);
        formInfoCreateRequest.setInfoName(formInfoCreateRequest.getInfoName() + "_拷贝");
        formInfoCreateRequest.setDefaultConfig(Boolean.FALSE);
        return create(formInfoCreateRequest);
    }

    @Override
    public void copy(String formId, String newFormId, String applicationId) {
        LambdaQueryWrapper<FormInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormInfoEntity::getFormId, formId);
        queryWrapper.eq(FormInfoEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormInfoEntity::getDeleted, Boolean.FALSE);
        List<FormInfoEntity> formInfoEntities = formInfoMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(formInfoEntities)) {
            return;
        }
        for (FormInfoEntity formInfoEntity : formInfoEntities) {
            formInfoEntity.setId(ObjectId.getGuid());
            formInfoEntity.setCreatorName(UserUtils.getUser().getNickName());
            formInfoEntity.setModifierName(UserUtils.getUser().getNickName());
            formInfoEntity.setFormId(newFormId);
            formInfoEntity.setCreateTime(null);
            formInfoEntity.setModifyTime(null);
        }
        saveBatch(formInfoEntities);
    }
}
