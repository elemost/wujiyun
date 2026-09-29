package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.utils.PageUtils;
import com.wuji.common.utils.SnowFlakeIdUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.constant.Constants;
import com.wuji.service.converter.AbstractFormAggregateConverter;
import com.wuji.service.mapper.FormAggregateMapper;
import com.wuji.service.model.entity.FormAggregateEntity;
import com.wuji.service.model.info.FormAggregateConfig;
import com.wuji.service.model.info.FormAggregateTable;
import com.wuji.service.model.info.FormAggregateTableField;
import com.wuji.service.model.info.FormAggregateTableValField;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.request.FormAggregateCreateRequest;
import com.wuji.service.model.request.FormAggregateDataRequest;
import com.wuji.service.model.request.FormAggregateListRequest;
import com.wuji.service.model.request.FormAggregateUpdateRequest;
import com.wuji.service.model.vo.FieldExistNameVO;
import com.wuji.service.model.vo.FormAggregateDataVO;
import com.wuji.service.model.vo.FormAggregateMongoVO;
import com.wuji.service.model.vo.FormAggregateStatisticVO;
import com.wuji.service.model.vo.FormAggregateVO;
import com.wuji.service.model.vo.FormFieldVO;
import com.wuji.service.model.vo.TemplateFormAggregateVO;
import com.wuji.service.service.AggregateTableService;
import com.wuji.service.service.FormAggregateService;
import com.wuji.service.service.TemplateFormAggregateService;
import com.wuji.service.utils.TemplateDealConfigUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 聚合表 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-03-10
 */
@Service
public class FormAggregateServiceImpl extends ServiceImpl<FormAggregateMapper, FormAggregateEntity>
        implements FormAggregateService {

    @Autowired
    private FormAggregateMapper formAggregateMapper;

    @Autowired
    private AggregateTableService aggregateTableService;

    @Autowired
    private TemplateFormAggregateService templateFormAggregateService;

    @Override
    public String create(FormAggregateCreateRequest formAggregateCreateRequest) {
        FormAggregateEntity formAggregateEntity =
                AbstractFormAggregateConverter.INSTANCE.toEntity(formAggregateCreateRequest);
        formAggregateEntity.setId(Constants.AGGREGATE_TABLE + SnowFlakeIdUtils.generateId());
        formAggregateEntity.setCreator(UserUtils.getUser().getNickName());
        formAggregateEntity.setModifier(UserUtils.getUser().getNickName());
        formAggregateMapper.insert(formAggregateEntity);
        return formAggregateEntity.getId();
    }

    @Override
    public void update(FormAggregateUpdateRequest formAggregateUpdateRequest) {
        FormAggregateEntity formAggregateEntity =
                AbstractFormAggregateConverter.INSTANCE.toEntity(formAggregateUpdateRequest);
        LambdaQueryWrapper<FormAggregateEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormAggregateEntity::getId, formAggregateUpdateRequest.getId());
        queryWrapper.eq(FormAggregateEntity::getApplicationId, formAggregateUpdateRequest.getApplicationId());
        formAggregateEntity.setModifier(UserUtils.getUser().getNickName());
        formAggregateMapper.update(formAggregateEntity, queryWrapper);
    }

    @Override
    public QueryPageVO<FormAggregateVO> queryPage(FormAggregateListRequest formAggregateListRequest) {
        LambdaQueryWrapper<FormAggregateEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormAggregateEntity::getApplicationId, formAggregateListRequest.getApplicationId());
        queryWrapper.eq(FormAggregateEntity::getDeleted, Boolean.FALSE);
        Page<FormAggregateEntity> formAggregateEntityPage = formAggregateMapper.selectPage(
                new Page<>(formAggregateListRequest.getPageNum(), formAggregateListRequest.getPageSize()),
                queryWrapper);
        List<FormAggregateEntity> records = formAggregateEntityPage.getRecords();
        List<FormAggregateVO> formAggregateVOList = new ArrayList<>();
        for (FormAggregateEntity formAggregateEntity : records) {
            FormAggregateVO formAggregateVO = AbstractFormAggregateConverter.INSTANCE.toVO(formAggregateEntity);
            formAggregateVOList.add(formAggregateVO);
        }
        return PageUtils.toQueryPage(formAggregateEntityPage, formAggregateVOList);
    }

    @Override
    public List<FormAggregateVO> getByIdList(String applicationId, List<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<FormAggregateEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormAggregateEntity::getApplicationId, applicationId);
        queryWrapper.in(FormAggregateEntity::getId, ids);
        List<FormAggregateEntity> records = formAggregateMapper.selectList(queryWrapper);
        List<FormAggregateVO> formAggregateVOList = new ArrayList<>();
        for (FormAggregateEntity formAggregateEntity : records) {
            FormAggregateVO formAggregateVO = AbstractFormAggregateConverter.INSTANCE.toVO(formAggregateEntity);
            formAggregateVOList.add(formAggregateVO);
        }
        return formAggregateVOList;
    }

    @Override
    public List<FormAggregateVO> getByApplicationId(String applicationId) {
        LambdaQueryWrapper<FormAggregateEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormAggregateEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormAggregateEntity::getDeleted, Boolean.FALSE);
        List<FormAggregateEntity> records = formAggregateMapper.selectList(queryWrapper);
        List<FormAggregateVO> formAggregateVOList = new ArrayList<>();
        for (FormAggregateEntity formAggregateEntity : records) {
            FormAggregateVO formAggregateVO = AbstractFormAggregateConverter.INSTANCE.toVO(formAggregateEntity);
            formAggregateVOList.add(formAggregateVO);
        }
        return formAggregateVOList;
    }

    @Override
    public Long getCountByApplication(List<String> applicationIds) {
        if (CollectionUtils.isEmpty(applicationIds)) {
            return 0L;
        }
        LambdaQueryWrapper<FormAggregateEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FormAggregateEntity::getApplicationId, applicationIds);
        queryWrapper.eq(FormAggregateEntity::getDeleted, Boolean.FALSE);
        Long count = formAggregateMapper.selectCount(queryWrapper);
        return count == null ? 0 : count;
    }

    @Override
    public void delete(String id, String applicationId) {
        LambdaQueryWrapper<FormAggregateEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormAggregateEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormAggregateEntity::getId, id);
        FormAggregateEntity formAggregateEntity = new FormAggregateEntity();
        formAggregateEntity.setDeleted(Boolean.TRUE);
        formAggregateEntity.setModifier(UserUtils.getUser().getUserId());
        formAggregateMapper.update(formAggregateEntity, queryWrapper);
    }

    @Override
    public FormAggregateVO info(String id, String applicationId) {
        LambdaQueryWrapper<FormAggregateEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormAggregateEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormAggregateEntity::getId, id);
        FormAggregateEntity formAggregateEntity = formAggregateMapper.selectOne(queryWrapper);
        return AbstractFormAggregateConverter.INSTANCE.toVO(formAggregateEntity);
    }

    @Override
    public FieldExistNameVO getFields(String id, String applicationId) {
        FieldExistNameVO fieldExistNameVO = new FieldExistNameVO();
        FormAggregateVO info = info(id, applicationId);
        List<FormConfigCommon> formConfigCommonList = getAggField(info);
        fieldExistNameVO.setName(info.getName());
        fieldExistNameVO.setFields(formConfigCommonList);
        return fieldExistNameVO;
    }

    @Override
    public List<FieldExistNameVO> getFieldByAggIds(List<String> idList, String applicationId) {
        if (CollectionUtils.isEmpty(idList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<FormAggregateEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormAggregateEntity::getApplicationId, applicationId);
        queryWrapper.in(FormAggregateEntity::getId, idList);
        queryWrapper.eq(FormAggregateEntity::getDeleted, Boolean.FALSE);
        List<FormAggregateEntity> formAggregateEntityList = formAggregateMapper.selectList(queryWrapper);
        List<FieldExistNameVO> fieldExistNameVOS = new ArrayList<>();
        for (FormAggregateEntity formAggregateEntity : formAggregateEntityList) {
            FieldExistNameVO fieldExistNameVO = new FieldExistNameVO();
            FormAggregateVO formAggregateVO = AbstractFormAggregateConverter.INSTANCE.toVO(formAggregateEntity);
            List<FormConfigCommon> formConfigCommonList = getAggField(formAggregateVO);
            fieldExistNameVO.setName(formAggregateVO.getName());
            fieldExistNameVO.setFields(formConfigCommonList);
            fieldExistNameVO.setFormId(formAggregateEntity.getId());
            fieldExistNameVOS.add(fieldExistNameVO);
        }
        return fieldExistNameVOS;
    }

    @Override
    public List<FormFieldVO> getFieldByApplicationId(String applicationId) {
        List<FormAggregateVO> formAggregateVOList = getByApplicationId(applicationId);
        List<FormFieldVO> formFieldVOS = new ArrayList<>();
        for (FormAggregateVO formAggregateVO : formAggregateVOList) {
            FormFieldVO formFieldVO = new FormFieldVO();
            List<FormConfigCommon> formConfigCommons = getAggField(formAggregateVO);
            formFieldVO.setId(formAggregateVO.getId());
            formFieldVO.setFormType("AGG");
            formFieldVO.setCategoryName(formAggregateVO.getName());
            formFieldVO.setApplicationId(applicationId);
            formFieldVO.setFields(formConfigCommons);
            formFieldVOS.add(formFieldVO);
        }
        return formFieldVOS;
    }

    private static List<FormConfigCommon> getAggField(FormAggregateVO formAggregateVO) {
        FormAggregateConfig formAggregateConfig =
                JSONObject.parseObject(formAggregateVO.getConfig(), FormAggregateConfig.class);
        if (formAggregateConfig == null) {
            return new ArrayList<>();
        }
        FormAggregateTable formAggregateTable = formAggregateConfig.getFormAggregateTable();
        List<FormConfigCommon> formConfigCommonList = new ArrayList<>();
        List<FormAggregateTableField> fieldXs = formAggregateTable.getFieldXs();
        for (FormAggregateTableField formAggregateTableField : fieldXs) {
            FormConfigCommon formConfigCommon = new FormConfigCommon();
            formConfigCommon.setName(formAggregateTableField.getTag());
            formConfigCommon.setLabel(formAggregateTableField.getText());
            formConfigCommon.setType(formAggregateTableField.getType());
            if (FormFieldTypeEnum.FORM_INPUT_USER_SINGLE.getFieldType().equals(formAggregateTableField.getType())) {
                formConfigCommon.setType(FormFieldTypeEnum.FORM_INPUT_USER_SINGLE_USED.getFieldType());
            }
            if (FormFieldTypeEnum.FORM_INPUT_DEPT_SINGLE.getFieldType().equals(formAggregateTableField.getType())) {
                formConfigCommon.setType(FormFieldTypeEnum.FORM_INPUT_DEPT_SINGLE_USED.getFieldType());
            }
            if (FormFieldTypeEnum.INPUT_DATE.getFieldType().equals(formAggregateTableField.getType())) {
                formConfigCommon.setType(FormFieldTypeEnum.INPUT_TEXT.getFieldType());
            }
            formConfigCommonList.add(formConfigCommon);
        }
        List<FormAggregateTableValField> valFields = formAggregateTable.getValFields();
        for (FormAggregateTableValField formAggregateTableValField : valFields) {
            FormConfigCommon formConfigCommon = new FormConfigCommon();
            formConfigCommon.setName(formAggregateTableValField.getTag());
            formConfigCommon.setLabel(formAggregateTableValField.getText());
            formConfigCommon.setType(FormFieldTypeEnum.INPUT_NUMBER.getFieldType());
            formConfigCommon.setFormat(formAggregateTableValField.getFormat().getFormat());
            formConfigCommonList.add(formConfigCommon);
        }
        return formConfigCommonList;
    }

    @Override
    public FormAggregateMongoVO buildAggregate(String id, String applicationId) {
        FormAggregateVO info = info(id, applicationId);
        FormAggregateConfig formAggregateConfig = JSONObject.parseObject(info.getConfig(), FormAggregateConfig.class);
        formAggregateConfig.getFormAggregateTable().setApplicationId(info.getApplicationId());
        return aggregateTableService.buildAggregate(formAggregateConfig.getFormAggregateTable());
    }

    @Override
    public Object getDataById(String id, FormAggregateDataRequest formAggregateDataRequest) {
        FormAggregateDataVO formAggregateDataVO = new FormAggregateDataVO();
        FormAggregateVO info = info(id, formAggregateDataRequest.getApplicationId());
        FormAggregateConfig formAggregateConfig = JSONObject.parseObject(info.getConfig(), FormAggregateConfig.class);
        Object object = aggregateTableService.aggregateTable(formAggregateConfig.getFormAggregateTable(),
                formAggregateDataRequest.getFilter());
        formAggregateDataVO.setData(object);
        formAggregateDataVO.setFormAggregateConfig(formAggregateConfig);
        return formAggregateDataVO;
    }

    @Override
    public void useTemplate(String applicationId, String templateId) {
        List<TemplateFormAggregateVO> templateFormAggregateVOList =
                templateFormAggregateService.getByApplicationId(templateId);
        if (CollectionUtils.isEmpty(templateFormAggregateVOList)) {
            return;
        }
        List<FormAggregateEntity> formAggregateEntities = new ArrayList<>();
        for (TemplateFormAggregateVO formAggregateVO : templateFormAggregateVOList) {
            FormAggregateEntity formAggregateEntity = AbstractFormAggregateConverter.INSTANCE.toEntity(formAggregateVO);
            formAggregateEntity.setConfig(
                    TemplateDealConfigUtil.dealAggregateConfig(JSONObject.parseObject(formAggregateVO.getConfig()),
                            UserUtils.getUser()));
            formAggregateEntity.setModifier(UserUtils.getUser().getNickName());
            formAggregateEntity.setCreator(UserUtils.getUser().getNickName());
            formAggregateEntity.setApplicationId(applicationId);
            formAggregateEntities.add(formAggregateEntity);
        }
        saveBatch(formAggregateEntities);
    }

    @Override
    public void copyApplication(String applicationId, String sourceApplicationId, Boolean share) {
        List<FormAggregateVO> formAggregateVOList = getByApplicationId(sourceApplicationId);
        if (CollectionUtils.isEmpty(formAggregateVOList)) {
            return;
        }
        List<FormAggregateEntity> formAggregateEntities = new ArrayList<>();
        for (FormAggregateVO formAggregateVO : formAggregateVOList) {
            FormAggregateEntity formAggregateEntity = AbstractFormAggregateConverter.INSTANCE.toEntity(formAggregateVO);
            formAggregateEntity.setModifier(UserUtils.getUser().getNickName());
            formAggregateEntity.setCreator(UserUtils.getUser().getNickName());
            formAggregateEntity.setApplicationId(applicationId);
            if (share) {
                String config =
                        TemplateDealConfigUtil.dealAggregateConfig(JSONObject.parseObject(formAggregateVO.getConfig()),
                                UserUtils.getUser());
                formAggregateEntity.setConfig(config);
            }
            formAggregateEntities.add(formAggregateEntity);
        }
        saveBatch(formAggregateEntities);
    }

    @Override
    public List<FormAggregateStatisticVO> statisticDetail(List<String> applicationIds) {
        QueryWrapper<FormAggregateEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", Boolean.FALSE);
        queryWrapper.in("application_id", applicationIds);
        queryWrapper.groupBy("application_id");
        return formAggregateMapper.applicationStatistic(queryWrapper);
    }
}
