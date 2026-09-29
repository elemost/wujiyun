package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.api.FormDataFactoryExecuteApi;
import com.wuji.common.enums.RepeatTriggerEnum;
import com.wuji.common.model.vo.DataFactoryInputFormVO;
import com.wuji.common.model.vo.DataFactoryReturnFieldCommonVO;
import com.wuji.common.model.vo.DataFactoryStageCommonFieldVO;
import com.wuji.common.utils.SnowFlakeIdUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.quartz.constant.ScheduleConstants;
import com.wuji.quartz.enums.JobGroupEnum;
import com.wuji.quartz.model.request.JobRequest;
import com.wuji.quartz.service.JobService;
import com.wuji.quartz.util.CronUtils;
import com.wuji.service.constant.Constants;
import com.wuji.service.converter.AbstractFormDataFactoryConverter;
import com.wuji.service.converter.AbstractFormDataFactoryPublishConverter;
import com.wuji.service.enums.FormDataFactoryStatusEnum;
import com.wuji.service.mapper.FormDataFactoryMapper;
import com.wuji.service.model.entity.FormDataFactoryEntity;
import com.wuji.service.model.info.FormFataFactorySyncConfig;
import com.wuji.service.model.request.FormDataFactoryCopyRequest;
import com.wuji.service.model.request.FormDataFactoryCreateRequest;
import com.wuji.service.model.request.FormDataFactoryInputRequest;
import com.wuji.service.model.request.FormDataFactoryPublishRequest;
import com.wuji.service.model.request.FormDataFactoryRequest;
import com.wuji.service.model.request.FormDataFactorySyncConfigRequest;
import com.wuji.service.model.request.FormDataFactoryUpdateRequest;
import com.wuji.service.model.vo.FormDataFactoryStatisticVO;
import com.wuji.service.model.vo.FormDataFactoryUpdateVO;
import com.wuji.service.model.vo.FormDataFactoryVO;
import com.wuji.service.model.vo.TemplateFormDataFactoryVO;
import com.wuji.service.service.FormDataFactoryInputService;
import com.wuji.service.service.FormDataFactoryPublishService;
import com.wuji.service.service.FormDataFactoryService;
import com.wuji.service.service.TemplateFormDataFactoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2026-01-05
 */
@Service
public class FormDataFactoryServiceImpl extends ServiceImpl<FormDataFactoryMapper, FormDataFactoryEntity>
        implements FormDataFactoryService {

    @Autowired
    private FormDataFactoryMapper formDataFactoryMapper;

    @Autowired
    private JobService jobService;

    @Autowired
    private FormDataFactoryPublishService formDataFactoryPublishService;

    @Autowired
    private FormDataFactoryExecuteApi formDataFactoryExecuteApi;

    @Autowired
    private TemplateFormDataFactoryService templateFormDataFactoryService;

    @Autowired
    private FormDataFactoryInputService formDataFactoryInputService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(FormDataFactoryCreateRequest formDataFactoryCreateRequest) {
        FormDataFactoryEntity formDataFactoryEntity =
                AbstractFormDataFactoryConverter.INSTANCE.toEntity(formDataFactoryCreateRequest);
        formDataFactoryEntity.setId(Constants.FAC_PREFIX + SnowFlakeIdUtils.generateId());
        formDataFactoryEntity.setCreator(UserUtils.getUser().getNickName());
        formDataFactoryEntity.setFactoryName("未命名数据流");
        formDataFactoryEntity.setModifier(UserUtils.getUser().getNickName());
        formDataFactoryEntity.setVersion(0);
        formDataFactoryCreateRequest.setStatus(FormDataFactoryStatusEnum.DRAFT.name());
        formDataFactoryMapper.insert(formDataFactoryEntity);
        return formDataFactoryEntity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FormDataFactoryUpdateVO update(FormDataFactoryUpdateRequest formDataFactoryUpdateRequest) {
        LambdaQueryWrapper<FormDataFactoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataFactoryEntity::getId, formDataFactoryUpdateRequest.getId());
        queryWrapper.eq(FormDataFactoryEntity::getApplicationId, formDataFactoryUpdateRequest.getApplicationId());
        FormDataFactoryEntity exist = formDataFactoryMapper.selectOne(queryWrapper);
        FormDataFactoryEntity formDataFactoryEntity =
                AbstractFormDataFactoryConverter.INSTANCE.toEntity(formDataFactoryUpdateRequest);
        formDataFactoryEntity.setModifier(UserUtils.getUser().getNickName());
        FormDataFactoryUpdateVO formDataFactoryUpdateVO = new FormDataFactoryUpdateVO();
        if (FormDataFactoryStatusEnum.PUBLISH.name().equals(formDataFactoryUpdateRequest.getStatus())) {
            formDataFactoryEntity.setVersion(exist.getVersion() + 1);
            FormDataFactoryPublishRequest formDataFactoryPublishRequest =
                    AbstractFormDataFactoryPublishConverter.INSTANCE.toRequest(formDataFactoryEntity);
            formDataFactoryPublishRequest.setFactoryName(exist.getFactoryName());
            formDataFactoryPublishRequest.setFactoryType(exist.getFactoryType());
            formDataFactoryPublishRequest.setSyncConfig(exist.getSyncConfig());
            formDataFactoryPublishService.publish(formDataFactoryPublishRequest);
            if (checkSyncFieldChange(formDataFactoryEntity.getId(), formDataFactoryEntity.getApplicationId(), exist)) {
                formDataFactoryUpdateVO.setSyncConfigError(Boolean.TRUE);
                formDataFactoryEntity.setSyncConfigError(Boolean.TRUE);
            } else {
                formDataFactoryEntity.setSyncConfigError(Boolean.FALSE);
            }
            saveInput(formDataFactoryUpdateRequest.getApplicationId(),
                    Collections.singletonList(formDataFactoryUpdateRequest.getId()));
        }
        formDataFactoryMapper.update(formDataFactoryEntity, queryWrapper);
        if (StringUtils.isNotEmpty(formDataFactoryUpdateRequest.getFactoryName())) {
            formDataFactoryPublishService.updateName(formDataFactoryUpdateRequest.getId(),
                    formDataFactoryUpdateRequest.getApplicationId(), formDataFactoryUpdateRequest.getFactoryName());
        }
        return formDataFactoryUpdateVO;
    }

    private void saveInput(String applicationId, List<String> ids) {
        List<DataFactoryInputFormVO> inputList = formDataFactoryExecuteApi.getAllInput(applicationId, ids);
        List<FormDataFactoryInputRequest> factoryInputs = new ArrayList<>();
        for (DataFactoryInputFormVO dataFactoryInputFormVO : inputList) {
            List<DataFactoryInputFormVO.Input> inputs = dataFactoryInputFormVO.getInputs();
            for (DataFactoryInputFormVO.Input input : inputs) {
                FormDataFactoryInputRequest formDataFactoryInputRequest = new FormDataFactoryInputRequest();
                formDataFactoryInputRequest.setDataFactoryId(dataFactoryInputFormVO.getId());
                formDataFactoryInputRequest.setApplicationId(applicationId);
                formDataFactoryInputRequest.setVersion(dataFactoryInputFormVO.getVersion());
                formDataFactoryInputRequest.setInputApplicationId(input.getApplicationId());
                formDataFactoryInputRequest.setInputFormId(input.getFormId());
                factoryInputs.add(formDataFactoryInputRequest);
            }
        }
        formDataFactoryInputService.saveBatch(factoryInputs);
    }

    private Boolean checkSyncFieldChange(String id, String applicationId, FormDataFactoryEntity exist) {
        if (StringUtils.isNotEmpty(exist.getSyncConfig())) {
            List<DataFactoryStageCommonFieldVO> dataFactoryExecuteApiStageField =
                    formDataFactoryExecuteApi.getStageField(applicationId, id).getFields();
            FormFataFactorySyncConfig formFataFactorySyncConfig =
                    JSONObject.parseObject(exist.getSyncConfig(), FormFataFactorySyncConfig.class);
            DataFactoryStageCommonFieldVO dataFactoryStageCommonFieldVO = dataFactoryExecuteApiStageField.get(0);
            List<DataFactoryReturnFieldCommonVO> fields = dataFactoryStageCommonFieldVO.getFields();
            List<FormFataFactorySyncConfig.MappingField> mappingFieldList =
                    formFataFactorySyncConfig.getMappingFields();
            List<String> mappingFields =
                    fields.stream().map(DataFactoryReturnFieldCommonVO::getAliasName).collect(Collectors.toList());
            for (FormFataFactorySyncConfig.MappingField mappingField : mappingFieldList) {
                if (!mappingFields.contains(mappingField.getFieldId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String copy(FormDataFactoryCopyRequest formDataFactoryCopyRequest) {
        LambdaQueryWrapper<FormDataFactoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataFactoryEntity::getId, formDataFactoryCopyRequest.getId());
        queryWrapper.eq(FormDataFactoryEntity::getApplicationId, formDataFactoryCopyRequest.getApplicationId());
        FormDataFactoryEntity formDataFactoryEntity = formDataFactoryMapper.selectOne(queryWrapper);
        formDataFactoryEntity.setId(Constants.FAC_PREFIX + SnowFlakeIdUtils.generateId());
        formDataFactoryEntity.setCreator(UserUtils.getUser().getNickName());
        formDataFactoryEntity.setModifier(UserUtils.getUser().getNickName());
        formDataFactoryEntity.setModifyTime(null);
        formDataFactoryEntity.setCreateTime(null);
        formDataFactoryMapper.insert(formDataFactoryEntity);
        return formDataFactoryEntity.getId();
    }

    @Override
    public List<FormDataFactoryVO> queryList(FormDataFactoryRequest formDataFactoryRequest) {
        LambdaQueryWrapper<FormDataFactoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataFactoryEntity::getApplicationId, formDataFactoryRequest.getApplicationId());
        queryWrapper.eq(FormDataFactoryEntity::getDeleted, Boolean.FALSE);
        queryWrapper.eq(StringUtils.isNotEmpty(formDataFactoryRequest.getFactoryType()),
                FormDataFactoryEntity::getFactoryType, formDataFactoryRequest.getFactoryType());
        List<FormDataFactoryEntity> formDataFactoryEntities = formDataFactoryMapper.selectList(queryWrapper);
        List<FormDataFactoryVO> formDataFactoryVOS = new ArrayList<>();
        for (FormDataFactoryEntity formDataFactoryEntity : formDataFactoryEntities) {
            FormDataFactoryVO formDataFactory = AbstractFormDataFactoryConverter.INSTANCE.toVO(formDataFactoryEntity);
            formDataFactoryVOS.add(formDataFactory);
        }
        return formDataFactoryVOS;
    }

    @Override
    public List<FormDataFactoryVO> queryByIds(String applicationId, List<String> ids) {
        LambdaQueryWrapper<FormDataFactoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataFactoryEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormDataFactoryEntity::getDeleted, Boolean.FALSE);
        queryWrapper.in(CollectionUtils.isNotEmpty(ids), FormDataFactoryEntity::getId, ids);
        List<FormDataFactoryEntity> formDataFactoryEntities = formDataFactoryMapper.selectList(queryWrapper);
        List<FormDataFactoryVO> formDataFactoryVOS = new ArrayList<>();
        for (FormDataFactoryEntity formDataFactoryEntity : formDataFactoryEntities) {
            FormDataFactoryVO formDataFactory = AbstractFormDataFactoryConverter.INSTANCE.toVO(formDataFactoryEntity);
            formDataFactoryVOS.add(formDataFactory);
        }
        return formDataFactoryVOS;
    }

    @Override
    public FormDataFactoryVO info(String id, String applicationId) {
        LambdaQueryWrapper<FormDataFactoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataFactoryEntity::getId, id);
        queryWrapper.eq(FormDataFactoryEntity::getApplicationId, applicationId);
        FormDataFactoryEntity formDataFactoryEntity = formDataFactoryMapper.selectOne(queryWrapper);
        return AbstractFormDataFactoryConverter.INSTANCE.toVO(formDataFactoryEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id, String applicationId) {
        LambdaQueryWrapper<FormDataFactoryEntity> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(FormDataFactoryEntity::getId, id);
        deleteWrapper.eq(FormDataFactoryEntity::getApplicationId, applicationId);
        FormDataFactoryEntity exist = formDataFactoryMapper.selectOne(deleteWrapper);
        FormDataFactoryEntity formDataFactoryEntity = new FormDataFactoryEntity();
        formDataFactoryEntity.setModifier(UserUtils.getUser().getNickName());
        formDataFactoryEntity.setDeleted(Boolean.TRUE);
        formDataFactoryMapper.update(formDataFactoryEntity, deleteWrapper);
        formDataFactoryPublishService.delete(applicationId, id);
        String businessId = exist.getId() + "_" + exist.getApplicationId();
        jobService.deleteJob(businessId, JobGroupEnum.DATA_FACTORY.name(), Boolean.TRUE);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncFormConfig(FormDataFactorySyncConfigRequest formDataFactorySyncConfigRequest) {
        LambdaQueryWrapper<FormDataFactoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataFactoryEntity::getId, formDataFactorySyncConfigRequest.getId());
        queryWrapper.eq(FormDataFactoryEntity::getApplicationId, formDataFactorySyncConfigRequest.getApplicationId());
        FormDataFactoryEntity formDataFactoryEntity = formDataFactoryMapper.selectOne(queryWrapper);
        FormFataFactorySyncConfig syncConfig = formDataFactorySyncConfigRequest.getSyncConfig();
        formDataFactoryEntity.setSyncForm(syncConfig.getEnable());
        String id = formDataFactoryEntity.getId() + "_" + formDataFactoryEntity.getApplicationId();
        formDataFactoryEntity.setSyncConfig(JSONObject.toJSONString(syncConfig));
        if (formDataFactoryEntity.getSyncForm()) {
            jobService.deleteJob(id, JobGroupEnum.DATA_FACTORY.name(), Boolean.TRUE);
            insertJob(formDataFactoryEntity, syncConfig, id);
            if (checkSyncFieldChange(formDataFactorySyncConfigRequest.getId(),
                    formDataFactorySyncConfigRequest.getApplicationId(), formDataFactoryEntity)) {
                formDataFactoryEntity.setSyncConfigError(Boolean.TRUE);
            } else {
                formDataFactoryEntity.setSyncConfigError(Boolean.FALSE);
            }
        } else {
            jobService.deleteJob(id, JobGroupEnum.DATA_FACTORY.name(), Boolean.TRUE);
        }
        formDataFactoryMapper.update(formDataFactoryEntity, queryWrapper);
    }

    @Override
    public void useTemplate(String applicationId, String templateId, String sourceApplicationId) {
        List<TemplateFormDataFactoryVO> templateFormDataFactoryVOList =
                templateFormDataFactoryService.getByApplicationId(templateId);
        if (CollectionUtils.isEmpty(templateFormDataFactoryVOList)) {
            return;
        }
        List<FormDataFactoryEntity> formDataFactoryEntityList = new ArrayList<>();
        List<FormDataFactoryPublishRequest> formDataFactoryPublishRequests = new ArrayList<>();
        for (TemplateFormDataFactoryVO templateFormDataFactoryVO : templateFormDataFactoryVOList) {
            FormDataFactoryEntity formDataFactoryEntity =
                    AbstractFormDataFactoryConverter.INSTANCE.toEntity(templateFormDataFactoryVO);
            formDataFactoryEntity.setApplicationId(applicationId);
            formDataFactoryEntity.setModifier(UserUtils.getUser().getNickName());
            formDataFactoryEntity.setCreator(UserUtils.getUser().getNickName());
            formDataFactoryEntity.setFactoryConfig(
                    templateFormDataFactoryVO.getFactoryConfig().replaceAll(sourceApplicationId, applicationId));
            formDataFactoryEntityList.add(formDataFactoryEntity);
            FormDataFactoryPublishRequest formDataFactoryPublishRequest =
                    AbstractFormDataFactoryPublishConverter.INSTANCE.toRequest(formDataFactoryEntity);
            formDataFactoryPublishRequests.add(formDataFactoryPublishRequest);
        }
        saveBatch(formDataFactoryEntityList);
        formDataFactoryPublishService.batchPublish(formDataFactoryPublishRequests);
        saveInput(applicationId, null);
        for (FormDataFactoryEntity formDataFactoryEntity : formDataFactoryEntityList) {
            if (formDataFactoryEntity.getSyncConfig() == null) {
                continue;
            }
            FormDataFactorySyncConfigRequest formDataFactorySyncConfigRequest = new FormDataFactorySyncConfigRequest();
            formDataFactorySyncConfigRequest.setId(formDataFactoryEntity.getId());
            formDataFactorySyncConfigRequest.setApplicationId(formDataFactoryEntity.getApplicationId());
            FormFataFactorySyncConfig formFataFactorySyncConfig =
                    JSONObject.parseObject(formDataFactoryEntity.getSyncConfig(), FormFataFactorySyncConfig.class);
            formDataFactorySyncConfigRequest.setSyncConfig(formFataFactorySyncConfig);
            syncFormConfig(formDataFactorySyncConfigRequest);
        }
    }

    @Override
    public void copyApplication(String applicationId, String sourceApplicationId) {
        LambdaQueryWrapper<FormDataFactoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataFactoryEntity::getApplicationId, sourceApplicationId);
        queryWrapper.eq(FormDataFactoryEntity::getDeleted, Boolean.FALSE);
        List<FormDataFactoryEntity> formDataFactoryEntities = formDataFactoryMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(formDataFactoryEntities)) {
            return;
        }
        List<FormDataFactoryEntity> formDataFactoryEntityList = new ArrayList<>();
        List<FormDataFactoryPublishRequest> formDataFactoryPublishRequests = new ArrayList<>();
        for (FormDataFactoryEntity formDataFactoryEntity : formDataFactoryEntities) {
            formDataFactoryEntity.setApplicationId(applicationId);
            formDataFactoryEntity.setModifier(UserUtils.getUser().getNickName());
            formDataFactoryEntity.setCreator(UserUtils.getUser().getNickName());
            formDataFactoryEntity.setCreateTime(null);
            formDataFactoryEntity.setModifyTime(null);
            formDataFactoryEntityList.add(formDataFactoryEntity);
            FormDataFactoryPublishRequest formDataFactoryPublishRequest =
                    AbstractFormDataFactoryPublishConverter.INSTANCE.toRequest(formDataFactoryEntity);
            formDataFactoryPublishRequests.add(formDataFactoryPublishRequest);
        }
        saveBatch(formDataFactoryEntityList);
        formDataFactoryPublishService.batchPublish(formDataFactoryPublishRequests);
        saveInput(applicationId, null);
        for (FormDataFactoryEntity formDataFactoryEntity : formDataFactoryEntityList) {
            if (formDataFactoryEntity.getSyncConfig() == null) {
                continue;
            }
            FormDataFactorySyncConfigRequest formDataFactorySyncConfigRequest = new FormDataFactorySyncConfigRequest();
            formDataFactorySyncConfigRequest.setId(formDataFactoryEntity.getId());
            formDataFactorySyncConfigRequest.setApplicationId(formDataFactoryEntity.getApplicationId());
            FormFataFactorySyncConfig formFataFactorySyncConfig =
                    JSONObject.parseObject(formDataFactoryEntity.getSyncConfig(), FormFataFactorySyncConfig.class);
            formDataFactorySyncConfigRequest.setSyncConfig(formFataFactorySyncConfig);
            syncFormConfig(formDataFactorySyncConfigRequest);
        }
    }

    @Override
    public Long getCountByApplication(List<String> applicationIds) {
        LambdaQueryWrapper<FormDataFactoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FormDataFactoryEntity::getApplicationId, applicationIds);
        queryWrapper.eq(FormDataFactoryEntity::getDeleted, Boolean.FALSE);
        Long count = formDataFactoryMapper.selectCount(queryWrapper);
        return count == null ? 0L : count;
    }


    @Override
    public List<FormDataFactoryStatisticVO> statisticDetail(List<String> applicationIds) {
        QueryWrapper<FormDataFactoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", Boolean.FALSE);
        queryWrapper.in("application_id", applicationIds);
        queryWrapper.groupBy("application_id");
        return formDataFactoryMapper.applicationStatistic(queryWrapper);
    }


    private void insertJob(FormDataFactoryEntity formDataFactoryEntity, FormFataFactorySyncConfig syncConfig,
                           String id) {
        JobRequest jobRequest = new JobRequest();
        jobRequest.setJobName(formDataFactoryEntity.getFactoryName() + "_" + id);
        jobRequest.setBusinessId(id);
        jobRequest.setJobGroup(JobGroupEnum.DATA_FACTORY.name());
        jobRequest.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        jobRequest.setStartTime(syncConfig.getStartTime());
        String param = "'%s','%s'";
        jobRequest.setParam(
                String.format(param, formDataFactoryEntity.getId(), formDataFactoryEntity.getApplicationId()));
        jobRequest.setCronExpression(
                CronUtils.trans(RepeatTriggerEnum.valueOf(syncConfig.getRepeatTrigger()), syncConfig.getStartTime(),
                        syncConfig.getCustomCron()));
        jobRequest.setConcurrent("0");
        jobRequest.setMisfirePolicy("3");
        String invokeTarget = "formDataFactorySchedule.trigger(" + jobRequest.getParam() + ")";
        jobRequest.setInvokeTarget(invokeTarget);
        jobService.insertJob(jobRequest);
    }

}
