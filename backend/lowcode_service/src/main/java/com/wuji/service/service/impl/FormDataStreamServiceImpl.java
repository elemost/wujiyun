package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuji.common.enums.RepeatTriggerEnum;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.TimeUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.quartz.constant.ScheduleConstants;
import com.wuji.quartz.enums.JobGroupEnum;
import com.wuji.quartz.model.request.JobRequest;
import com.wuji.quartz.service.JobService;
import com.wuji.quartz.util.CronUtils;
import com.wuji.service.context.DataStreamContext;
import com.wuji.service.converter.AbstractFormDataStreamConverter;
import com.wuji.service.converter.AbstractFormDataStreamPublishConverter;
import com.wuji.service.enums.FormDataStreamConfigTypeEnum;
import com.wuji.service.enums.FormDataStreamStateEnum;
import com.wuji.service.enums.ServiceResultCode;
import com.wuji.service.exception.ServiceException;
import com.wuji.service.mapper.FormDataStreamMapper;
import com.wuji.service.model.entity.FormDataStreamEntity;
import com.wuji.service.model.info.stream.DataStreamCommon;
import com.wuji.service.model.info.stream.DataStreamTimeTriggerNode;
import com.wuji.service.model.request.FormDataStreamCreateRequest;
import com.wuji.service.model.request.FormDataStreamListRequest;
import com.wuji.service.model.request.FormDataStreamPublishRequest;
import com.wuji.service.model.request.FormDataStreamUpdateRequest;
import com.wuji.service.model.vo.ApplicationCategoryVO;
import com.wuji.service.model.vo.FormDataStreamFromVO;
import com.wuji.service.model.vo.FormDataStreamPublishVO;
import com.wuji.service.model.vo.FormDataStreamStatisticVO;
import com.wuji.service.model.vo.FormDataStreamVO;
import com.wuji.service.model.vo.TemplateFormDataStreamVO;
import com.wuji.service.service.ApplicationCategoryService;
import com.wuji.service.service.FormDataStreamPublishService;
import com.wuji.service.service.FormDataStreamService;
import com.wuji.service.service.TemplateFormDataStreamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-12-30
 */
@Service
@Slf4j
public class FormDataStreamServiceImpl extends ServiceImpl<FormDataStreamMapper, FormDataStreamEntity>
        implements FormDataStreamService {

    @Autowired
    private FormDataStreamMapper formDataStreamMapper;

    @Autowired
    private ApplicationCategoryService applicationCategoryService;

    @Autowired
    private FormDataStreamPublishService formDataStreamPublishService;

    @Autowired
    private TemplateFormDataStreamService templateFormDataStreamService;

    @Autowired
    private DataStreamContext dataStreamContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JobService jobService;

    @Override
    public String create(FormDataStreamCreateRequest formDataStreamCreateRequest) {
        FormDataStreamEntity formDataStreamEntity =
                AbstractFormDataStreamConverter.INSTANCE.toEntity(formDataStreamCreateRequest);
        formDataStreamEntity.setCreator(UserUtils.getUser().getNickName());
        formDataStreamEntity.setModifier(UserUtils.getUser().getNickName());
        formDataStreamEntity.setId(ObjectId.getGuid());
        formDataStreamEntity.setState(FormDataStreamStateEnum.DRAFT.name());
        formDataStreamMapper.insert(formDataStreamEntity);
        return formDataStreamEntity.getId();
    }

    @Override
    public void update(FormDataStreamUpdateRequest formDataStreamUpdateRequest) {
        LambdaQueryWrapper<FormDataStreamEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataStreamEntity::getId, formDataStreamUpdateRequest.getId());
        queryWrapper.eq(FormDataStreamEntity::getApplicationId, formDataStreamUpdateRequest.getApplicationId());
        FormDataStreamEntity exist = formDataStreamMapper.selectOne(queryWrapper);
        FormDataStreamEntity formDataStreamEntity =
                AbstractFormDataStreamConverter.INSTANCE.toEntity(formDataStreamUpdateRequest);
        if (FormDataStreamStateEnum.PUBLISH.name().equals(exist.getState())) {
            formDataStreamEntity.setVersion(exist.getVersion() + 1);
        }
        formDataStreamEntity.setState(FormDataStreamStateEnum.DRAFT.name());
        formDataStreamEntity.setModifier(UserUtils.getUser().getNickName());
        formDataStreamMapper.update(formDataStreamEntity, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(FormDataStreamUpdateRequest formDataStreamUpdateRequest) {
        LambdaQueryWrapper<FormDataStreamEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataStreamEntity::getId, formDataStreamUpdateRequest.getId());
        queryWrapper.eq(FormDataStreamEntity::getApplicationId, formDataStreamUpdateRequest.getApplicationId());
        FormDataStreamEntity exist = formDataStreamMapper.selectOne(queryWrapper);
        if (exist.getDeleted()) {
            throw new ServiceException(ServiceResultCode.FORM_DATA_STREAM_DELETED);
        }
        FormDataStreamEntity formDataStreamEntity =
                AbstractFormDataStreamConverter.INSTANCE.toEntity(formDataStreamUpdateRequest);
        formDataStreamEntity.setState(FormDataStreamStateEnum.PUBLISH.name());
        formDataStreamEntity.setModifier(UserUtils.getUser().getNickName());
        if (FormDataStreamStateEnum.PUBLISH.name().equals(exist.getState())) {
            formDataStreamEntity.setVersion(exist.getVersion() + 1);
        } else {
            formDataStreamEntity.setVersion(exist.getVersion());
        }
        formDataStreamMapper.update(formDataStreamEntity, queryWrapper);
        formDataStreamUpdateRequest.setVersion(formDataStreamEntity.getVersion());
        formDataStreamPublishService.publish(formDataStreamUpdateRequest);
        timeTrigger(exist, formDataStreamEntity);
    }

    private void timeTrigger(FormDataStreamEntity exist, FormDataStreamEntity formDataStreamEntity) {
        if (FormDataStreamConfigTypeEnum.TIME.name().equals(exist.getConfigType())) {
            insertJob(exist, formDataStreamEntity);
        }
    }

    private void insertJob(FormDataStreamEntity exist, FormDataStreamEntity formDataStreamEntity) {
        String businessId = exist.getId() + "_" + formDataStreamEntity.getApplicationId();
        JobRequest jobRequest = new JobRequest();
        jobRequest.setJobName(exist.getName() + "_" + formDataStreamEntity.getId());
        jobRequest.setBusinessId(businessId);
        jobRequest.setJobGroup(JobGroupEnum.DATA_STREAM.name());
        jobRequest.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        String param = "'%s','%s'";
        jobRequest.setParam(String.format(param, exist.getId(), exist.getApplicationId()));
        try {
            DataStreamCommon dataStreamCommon =
                    objectMapper.readValue(formDataStreamEntity.getConfig(), DataStreamCommon.class);
            DataStreamTimeTriggerNode dataStreamTimeTriggerNode = (DataStreamTimeTriggerNode) dataStreamCommon;
            if ("custom".equals(dataStreamTimeTriggerNode.getTriggerTimeType())) {
                jobRequest.setCronExpression(
                        CronUtils.trans(RepeatTriggerEnum.valueOf(dataStreamTimeTriggerNode.getRepeatTrigger()),
                                dataStreamTimeTriggerNode.getCustomTime(), dataStreamTimeTriggerNode.getCustomCron()));
                jobRequest.setStartTime(dataStreamTimeTriggerNode.getCustomTime());
            } else {
                Date dateByDaytime = null;
                if (StringUtils.isEmpty(dataStreamTimeTriggerNode.getDaytime())) {
                    dateByDaytime = TimeUtils.getDateByDaytime("00:00");
                } else {
                    dateByDaytime = TimeUtils.getDateByDaytime(dataStreamTimeTriggerNode.getDaytime());
                }

                jobRequest.setCronExpression(CronUtils.trans(RepeatTriggerEnum.DAILY, dateByDaytime,
                        dataStreamTimeTriggerNode.getCustomCron()));
            }
            jobRequest.setEndTime(dataStreamTimeTriggerNode.getEndTime());
        } catch (Exception e) {
            log.error("转化智能助手失败", e);
        }
        jobRequest.setConcurrent("0");
        jobRequest.setMisfirePolicy("3");
        String invokeTarget = "dataStreamSchedule.trigger(" + jobRequest.getParam() + ")";
        jobRequest.setInvokeTarget(invokeTarget);
        jobService.deleteJob(businessId, JobGroupEnum.DATA_STREAM.name(), Boolean.TRUE);
        jobService.insertJob(jobRequest);
    }

    @Override
    public List<FormDataStreamVO> queryList(FormDataStreamListRequest formDataStreamListRequest) {
        LambdaQueryWrapper<FormDataStreamEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataStreamEntity::getApplicationId, formDataStreamListRequest.getApplicationId());
        queryWrapper.eq(StringUtils.isNotEmpty(formDataStreamListRequest.getFormId()), FormDataStreamEntity::getFormId,
                formDataStreamListRequest.getFormId());
        queryWrapper.eq(FormDataStreamEntity::getDeleted, Boolean.FALSE);
        queryWrapper.like(StringUtils.isNotEmpty(formDataStreamListRequest.getName()), FormDataStreamEntity::getName,
                formDataStreamListRequest.getName());
        queryWrapper.orderByDesc(FormDataStreamEntity::getConfigType, FormDataStreamEntity::getCreateTime);
        List<FormDataStreamEntity> formDataStreamEntityList = formDataStreamMapper.selectList(queryWrapper);
        List<String> formIdList =
                formDataStreamEntityList.stream().map(FormDataStreamEntity::getFormId).collect(Collectors.toList());
        List<ApplicationCategoryVO> applicationCategoryVOList =
                applicationCategoryService.getByIdList(formIdList, formDataStreamListRequest.getApplicationId());
        Map<String, String> categoryNameMap = applicationCategoryVOList.stream()
                .collect(Collectors.toMap(ApplicationCategoryVO::getId, ApplicationCategoryVO::getCategoryName));
        List<FormDataStreamVO> formDataStreamVOS = new ArrayList<>();
        for (FormDataStreamEntity formDataStreamEntity : formDataStreamEntityList) {
            FormDataStreamVO formDataStreamVO = AbstractFormDataStreamConverter.INSTANCE.toVO(formDataStreamEntity);
            if (categoryNameMap.get(formDataStreamEntity.getFormId()) != null) {
                formDataStreamVO.setFormName(categoryNameMap.get(formDataStreamEntity.getFormId()));
            } else {
                formDataStreamVO.setFormName("定时触发");
            }
            formDataStreamVOS.add(formDataStreamVO);
        }
        return formDataStreamVOS;
    }

    @Override
    public FormDataStreamVO info(String id, String applicationId) {
        LambdaQueryWrapper<FormDataStreamEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataStreamEntity::getId, id);
        queryWrapper.eq(FormDataStreamEntity::getApplicationId, applicationId);
        FormDataStreamEntity formDataStreamEntity = formDataStreamMapper.selectOne(queryWrapper);
        FormDataStreamVO formDataStreamVO = AbstractFormDataStreamConverter.INSTANCE.toVO(formDataStreamEntity);
        if (formDataStreamVO == null) {
            return null;
        }
        ApplicationCategoryVO applicationCategoryVO =
                applicationCategoryService.info(formDataStreamVO.getFormId(), formDataStreamVO.getApplicationId());
        if (applicationCategoryVO != null) {
            formDataStreamVO.setFormName(applicationCategoryVO.getCategoryName());
        }
        return formDataStreamVO;
    }

    @Override
    public void delete(String id, String applicationId) {
        LambdaQueryWrapper<FormDataStreamEntity> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(FormDataStreamEntity::getId, id);
        deleteWrapper.eq(FormDataStreamEntity::getApplicationId, applicationId);
        FormDataStreamEntity exist = formDataStreamMapper.selectOne(deleteWrapper);
        FormDataStreamEntity formDataStreamEntity = new FormDataStreamEntity();
        formDataStreamEntity.setDeleted(Boolean.TRUE);
        formDataStreamEntity.setModifier(UserUtils.getUser().getNickName());
        formDataStreamMapper.update(formDataStreamEntity, deleteWrapper);
        formDataStreamPublishService.delete(id, applicationId);
        if (FormDataStreamConfigTypeEnum.TIME.name().equals(exist.getConfigType())) {
            String businessId = id + "_" + applicationId;
            jobService.deleteJob(businessId, JobGroupEnum.DATA_STREAM.name(), Boolean.FALSE);
        }
    }

    @Override
    public void openOrClose(String id, String applicationId, Boolean enable) {
        LambdaQueryWrapper<FormDataStreamEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataStreamEntity::getId, id);
        queryWrapper.eq(FormDataStreamEntity::getApplicationId, applicationId);
        FormDataStreamEntity exist = formDataStreamMapper.selectOne(queryWrapper);
        FormDataStreamEntity formDataStreamEntity = new FormDataStreamEntity();
        formDataStreamEntity.setEnable(enable);
        formDataStreamEntity.setModifier(UserUtils.getUser().getNickName());
        formDataStreamMapper.update(formDataStreamEntity, queryWrapper);
        formDataStreamPublishService.openOrClose(id, applicationId, enable);
        if (FormDataStreamConfigTypeEnum.TIME.name().equals(exist.getConfigType()) && enable) {
            String businessId = exist.getId() + "_" + applicationId;
            jobService.deleteJob(businessId, JobGroupEnum.DATA_STREAM.name(), Boolean.TRUE);
        }
    }

    @Override
    public void useTemplate(String applicationId, String templateId, String sourceApplicationId) {
        List<TemplateFormDataStreamVO> templateFormDataStreamVOS =
                templateFormDataStreamService.getByApplicationId(templateId);
        if (CollectionUtils.isEmpty(templateFormDataStreamVOS)) {
            return;
        }
        List<FormDataStreamEntity> formDataStreamEntityList = new ArrayList<>();
        List<FormDataStreamPublishRequest> formDataStreamPublishRequestList = new ArrayList<>();
        for (TemplateFormDataStreamVO templateFormDataStreamVO : templateFormDataStreamVOS) {
            FormDataStreamEntity formDataStreamEntity =
                    AbstractFormDataStreamConverter.INSTANCE.toEntity(templateFormDataStreamVO);
            formDataStreamEntity.setState(FormDataStreamStateEnum.PUBLISH.name());
            overWriteData(formDataStreamEntity, applicationId, sourceApplicationId);
            formDataStreamEntity.setCreator(UserUtils.getUser().getNickName());
            formDataStreamEntity.setModifier(UserUtils.getUser().getNickName());
            formDataStreamEntity.setApplicationId(applicationId);
            formDataStreamEntity.setVersion(1);
            formDataStreamEntityList.add(formDataStreamEntity);
            FormDataStreamPublishRequest formDataStreamPublishRequest =
                    AbstractFormDataStreamPublishConverter.INSTANCE.toRequest(formDataStreamEntity);
            formDataStreamPublishRequestList.add(formDataStreamPublishRequest);
        }
        saveBatch(formDataStreamEntityList);
        formDataStreamPublishService.batchPublish(formDataStreamPublishRequestList);
        for (FormDataStreamEntity formDataStreamEntity : formDataStreamEntityList) {
            timeTrigger(formDataStreamEntity, formDataStreamEntity);
        }
    }

    private void overWriteData(FormDataStreamEntity formDataStreamEntity, String applicationId,
                               String sourceApplicationId) {
        try {
            DataStreamCommon dataStreamCommon =
                    objectMapper.readValue(formDataStreamEntity.getConfig(), DataStreamCommon.class);
            dataStreamContext.getHandler(dataStreamCommon.getType())
                    .useTemplate(dataStreamCommon, applicationId, sourceApplicationId);
            String config = objectMapper.writer().writeValueAsString(dataStreamCommon);
            formDataStreamEntity.setConfig(config);
        } catch (Exception e) {
            log.error("转化智能助手失败", e);
        }
    }

    @Override
    public Map<String, String> copy(String applicationId, String formId, String newFormId) {
        LambdaQueryWrapper<FormDataStreamEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataStreamEntity::getFormId, formId);
        queryWrapper.eq(FormDataStreamEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormDataStreamEntity::getDeleted, Boolean.FALSE);
        List<FormDataStreamEntity> formDataStreamEntityList = formDataStreamMapper.selectList(queryWrapper);
        List<FormDataStreamPublishRequest> formDataStreamPublishRequestList = new ArrayList<>();
        Map<String, String> dataStreamIdMap = new HashMap<>();
        for (FormDataStreamEntity formDataStreamEntity : formDataStreamEntityList) {
            String guid = ObjectId.getGuid();
            dataStreamIdMap.put(formDataStreamEntity.getId(), guid);
            formDataStreamEntity.setState(FormDataStreamStateEnum.PUBLISH.name());
            formDataStreamEntity.setCreator(UserUtils.getUser().getNickName());
            formDataStreamEntity.setModifier(UserUtils.getUser().getNickName());
            formDataStreamEntity.setFormId(newFormId);
            formDataStreamEntity.setVersion(1);
            formDataStreamEntity.setId(guid);
            FormDataStreamPublishRequest formDataStreamPublishRequest =
                    AbstractFormDataStreamPublishConverter.INSTANCE.toRequest(formDataStreamEntity);
            formDataStreamPublishRequestList.add(formDataStreamPublishRequest);
        }
        saveBatch(formDataStreamEntityList);
        formDataStreamPublishService.batchPublish(formDataStreamPublishRequestList);
        return dataStreamIdMap;
    }

    @Override
    public List<FormDataStreamFromVO> formList(FormDataStreamListRequest formDataStreamListRequest) {
        List<FormDataStreamVO> formDataStreamVOS = queryList(formDataStreamListRequest);
        Map<String, List<FormDataStreamVO>> formMap =
                formDataStreamVOS.stream().collect(Collectors.groupingBy(c -> c.getFormId() + "_" + c.getFormName()));
        List<FormDataStreamFromVO> formDataStreamFromVOList = new ArrayList<>();
        formMap.forEach((key, list) -> {
            FormDataStreamFromVO formDataStreamFromVO = new FormDataStreamFromVO();
            formDataStreamFromVO.setFormDataStreamList(list);
            String[] split = key.split("_");
            formDataStreamFromVO.setFormName(split[1]);
            formDataStreamFromVO.setFormId(split[0]);
            formDataStreamFromVOList.add(formDataStreamFromVO);
        });
        return formDataStreamFromVOList;
    }

    @Override
    public void copyApplication(String applicationId, String sourceApplicationId, List<String> formIdList,
                                Boolean share) {
        List<FormDataStreamPublishVO> formDataStreamPublishVOS =
                formDataStreamPublishService.getByApplicationIdList(sourceApplicationId);
        if (CollectionUtils.isEmpty(formDataStreamPublishVOS)) {
            return;
        }
        List<FormDataStreamEntity> formDataStreamEntityList = new ArrayList<>();
        List<FormDataStreamPublishRequest> formDataStreamPublishRequestList = new ArrayList<>();
        for (FormDataStreamPublishVO formDataStreamPublishVO : formDataStreamPublishVOS) {
            FormDataStreamEntity formDataStreamEntity =
                    AbstractFormDataStreamConverter.INSTANCE.toEntity(formDataStreamPublishVO);
            formDataStreamEntity.setState(FormDataStreamStateEnum.PUBLISH.name());
            formDataStreamEntity.setCreator(UserUtils.getUser().getNickName());
            formDataStreamEntity.setModifier(UserUtils.getUser().getNickName());
            formDataStreamEntity.setApplicationId(applicationId);
            formDataStreamEntity.setVersion(1);
            if (share) {
                overWriteData(formDataStreamEntity, applicationId, sourceApplicationId);
            }
            formDataStreamEntityList.add(formDataStreamEntity);
            FormDataStreamPublishRequest formDataStreamPublishRequest =
                    AbstractFormDataStreamPublishConverter.INSTANCE.toRequest(formDataStreamEntity);
            formDataStreamPublishRequestList.add(formDataStreamPublishRequest);
        }
        saveBatch(formDataStreamEntityList);
        formDataStreamPublishService.batchPublish(formDataStreamPublishRequestList);
    }

    @Override
    public Long getCountByApplication(List<String> applicationIdList) {
        if (CollectionUtils.isEmpty(applicationIdList)) {
            return 0L;
        }
        LambdaQueryWrapper<FormDataStreamEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FormDataStreamEntity::getApplicationId, applicationIdList);
        queryWrapper.eq(FormDataStreamEntity::getDeleted, Boolean.FALSE);
        Long count = formDataStreamMapper.selectCount(queryWrapper);
        return count == null ? 0L : count;
    }

    @Override
    public List<FormDataStreamStatisticVO> statisticDetail(List<String> applicationIdList) {
        QueryWrapper<FormDataStreamEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", Boolean.FALSE);
        queryWrapper.in("application_id", applicationIdList);
        queryWrapper.groupBy("application_id");
        return formDataStreamMapper.applicationStatistic(queryWrapper);
    }

}
