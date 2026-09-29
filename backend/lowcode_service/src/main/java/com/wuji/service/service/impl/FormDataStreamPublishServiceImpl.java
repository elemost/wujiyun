package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.Striped;
import com.wuji.common.enums.FormFieldTypeEnum;
import com.wuji.common.enums.RepeatTriggerEnum;
import com.wuji.common.enums.UserDefaultEnum;
import com.wuji.common.exception.BizException;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.TimeIntervalUtils;
import com.wuji.common.utils.TimeUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractFormDataStreamPublishConverter;
import com.wuji.service.enums.FormDataStreamConfigTypeEnum;
import com.wuji.service.enums.FormDataStreamStateEnum;
import com.wuji.service.enums.FormSystemFieldEnum;
import com.wuji.service.enums.SearchMethodEnum;
import com.wuji.service.mapper.FormDataStreamPublishMapper;
import com.wuji.service.model.domain.DataStreamTriggerLogDomain;
import com.wuji.service.model.domain.LowcodeDataDomain;
import com.wuji.service.model.entity.FormDataStreamPublishEntity;
import com.wuji.service.model.info.FormConfigEncryptKey;
import com.wuji.service.model.info.FormDataStreamTrigger;
import com.wuji.service.model.info.FormDataTitle;
import com.wuji.service.model.info.MongodbSearchCondition;
import com.wuji.service.model.info.MongodbSearchFilter;
import com.wuji.service.model.info.stream.DataStreamCalculateVO;
import com.wuji.service.model.info.stream.DataStreamCommon;
import com.wuji.service.model.info.stream.DataStreamTimeTriggerNode;
import com.wuji.service.model.request.FormDataStreamButtonTriggerRequest;
import com.wuji.service.model.request.FormDataStreamPublishRequest;
import com.wuji.service.model.request.FormDataStreamUpdateRequest;
import com.wuji.service.model.request.FormSearchDataRequest;
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.model.vo.FormDataStreamPublishVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.service.ApplicationService;
import com.wuji.service.service.DataStreamTriggerService;
import com.wuji.service.service.FormDataStreamPublishService;
import com.wuji.service.service.FormExtraFunctionTitleService;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.service.service.FormService;
import com.wuji.service.utils.DataStreamExecuteUtils;
import com.wuji.workflow.model.vo.PendingTaskVO;
import com.wuji.workflow.service.WorkFlowService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-03-26
 */
@Service
@Slf4j
public class FormDataStreamPublishServiceImpl
        extends ServiceImpl<FormDataStreamPublishMapper, FormDataStreamPublishEntity>
        implements FormDataStreamPublishService {

    @Autowired
    private FormDataStreamPublishMapper formDataStreamPublishMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FormService formService;

    @Autowired
    private FormExtraFunctionTitleService formExtraFunctionTitleService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ScheduledExecutorService dataStreamScheduledExecutor;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private DataStreamTriggerService dataStreamTriggerService;

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Autowired
    private WorkFlowService workFlowService;

    private final Striped<Lock> stripedLock = Striped.lock(256);


    @Override
    public void publish(FormDataStreamUpdateRequest formDataStreamUpdateRequest) {
        LambdaQueryWrapper<FormDataStreamPublishEntity> updateLastVersion = new LambdaQueryWrapper<>();
        updateLastVersion.eq(FormDataStreamPublishEntity::getApplicationId,
                formDataStreamUpdateRequest.getApplicationId());
        updateLastVersion.eq(FormDataStreamPublishEntity::getId, formDataStreamUpdateRequest.getId());
        updateLastVersion.eq(FormDataStreamPublishEntity::getLastVersion, Boolean.TRUE);
        FormDataStreamPublishEntity formDataStreamPublishEntity = new FormDataStreamPublishEntity();
        formDataStreamPublishEntity.setLastVersion(Boolean.FALSE);
        formDataStreamPublishMapper.update(formDataStreamPublishEntity, updateLastVersion);
        FormDataStreamPublishEntity newVersion =
                AbstractFormDataStreamPublishConverter.INSTANCE.toEntity(formDataStreamUpdateRequest);
        newVersion.setState(FormDataStreamStateEnum.PUBLISH.name());
        newVersion.setLastVersion(Boolean.TRUE);
        newVersion.setEnable(formDataStreamPublishEntity.getEnable());
        newVersion.setModifier(UserUtils.getUser().getNickName());
        newVersion.setCreator(UserUtils.getUser().getNickName());
        formDataStreamPublishMapper.insert(newVersion);
    }

    @Override
    public void trigger(String formId, String action, LowcodeDataDomain lowcodeDataDomain, String applicationId,
                        FormDataStreamTrigger formDataStreamTrigger) {
        LambdaQueryWrapper<FormDataStreamPublishEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataStreamPublishEntity::getFormId, formId);
        queryWrapper.eq(FormDataStreamPublishEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormDataStreamPublishEntity::getConfigType, FormDataStreamConfigTypeEnum.FORM.name());
        queryWrapper.eq(FormDataStreamPublishEntity::getDeleted, Boolean.FALSE);
        queryWrapper.eq(FormDataStreamPublishEntity::getEnable, Boolean.TRUE);
        queryWrapper.eq(FormDataStreamPublishEntity::getLastVersion, Boolean.TRUE);
        List<FormDataStreamPublishEntity> formDataStreamEntityList =
                formDataStreamPublishMapper.selectList(queryWrapper);
        Map<String, Map<String, FormConfigEncryptKey>> encryptKeyMap = new HashMap<>();
        for (FormDataStreamPublishEntity formDataStreamEntity : formDataStreamEntityList) {
            formDataStreamTrigger.setUserDomain(UserUtils.getUser());
            trigger(formId, action, lowcodeDataDomain, applicationId, formDataStreamTrigger, formDataStreamEntity,
                    encryptKeyMap);
        }
    }

    @Override
    public void trigger(String formId, String action, LowcodeDataDomain lowcodeDataDomain, String applicationId,
                        FormDataStreamTrigger formDataStreamTrigger, String dataStreamId) {
        LambdaQueryWrapper<FormDataStreamPublishEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataStreamPublishEntity::getFormId, formId);
        queryWrapper.eq(FormDataStreamPublishEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormDataStreamPublishEntity::getConfigType, FormDataStreamConfigTypeEnum.FORM.name());
        queryWrapper.eq(FormDataStreamPublishEntity::getDeleted, Boolean.FALSE);
        queryWrapper.eq(FormDataStreamPublishEntity::getEnable, Boolean.TRUE);
        queryWrapper.eq(FormDataStreamPublishEntity::getLastVersion, Boolean.TRUE);
        queryWrapper.eq(FormDataStreamPublishEntity::getId, dataStreamId);
        List<FormDataStreamPublishEntity> formDataStreamEntityList =
                formDataStreamPublishMapper.selectList(queryWrapper);
        Map<String, Map<String, FormConfigEncryptKey>> encryptKeyMap = new HashMap<>();
        for (FormDataStreamPublishEntity formDataStreamEntity : formDataStreamEntityList) {
            formDataStreamTrigger.setUserDomain(UserUtils.getUser());
            trigger(formId, action, lowcodeDataDomain, applicationId, formDataStreamTrigger, formDataStreamEntity,
                    encryptKeyMap);
        }
    }

    @Override
    public void buttonTrigger(FormDataStreamButtonTriggerRequest triggerRequest) {
        LambdaQueryWrapper<FormDataStreamPublishEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataStreamPublishEntity::getLastVersion, Boolean.TRUE);
        queryWrapper.eq(FormDataStreamPublishEntity::getApplicationId, triggerRequest.getApplicationId());
        queryWrapper.eq(FormDataStreamPublishEntity::getId, triggerRequest.getDataStreamId());
        FormDataStreamPublishEntity formDataStreamPublishEntity = formDataStreamPublishMapper.selectOne(queryWrapper);
        FormDataStreamTrigger trigger = new FormDataStreamTrigger();
        LowcodeDataDomain lowcodeDataDomain =
                formMongoDbService.info(triggerRequest.getUuid(), triggerRequest.getFormId(),
                        triggerRequest.getApplicationId());
        trigger.setUserDomain(UserUtils.getUser());
        dataStreamExecute(triggerRequest.getFormId(), "button", lowcodeDataDomain, triggerRequest.getApplicationId(),
                trigger, formDataStreamPublishEntity, new HashMap<>());
    }

    @Override
    public void timeTrigger(String dataStreamId, String applicationId) {
        LambdaQueryWrapper<FormDataStreamPublishEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataStreamPublishEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormDataStreamPublishEntity::getId, dataStreamId);
        queryWrapper.eq(FormDataStreamPublishEntity::getConfigType, FormDataStreamConfigTypeEnum.TIME.name());
        queryWrapper.eq(FormDataStreamPublishEntity::getDeleted, Boolean.FALSE);
        queryWrapper.eq(FormDataStreamPublishEntity::getEnable, Boolean.TRUE);
        queryWrapper.eq(FormDataStreamPublishEntity::getLastVersion, Boolean.TRUE);
        FormDataStreamPublishEntity formDataStreamPublish = formDataStreamPublishMapper.selectOne(queryWrapper);
        if (formDataStreamPublish == null) {
            return;
        }
        DataStreamCommon dataStreamCommon = null;
        try {
            dataStreamCommon = objectMapper.readValue(formDataStreamPublish.getConfig(), DataStreamCommon.class);
        } catch (Exception e) {
            log.error("数据流配置解析失败", e);
        }
        if (dataStreamCommon == null) {
            return;
        }
        DataStreamTimeTriggerNode dataStreamTimeTriggerNode = (DataStreamTimeTriggerNode) dataStreamCommon;
        ApplicationVO applicationVO = applicationService.detail(applicationId);
        FormDataStreamTrigger formDataStreamTrigger = new FormDataStreamTrigger();
        if (UserUtils.getUser() == null) {
            UserDomain userDomain = new UserDomain();
            userDomain.setUserId(UserDefaultEnum.SYSTEM_USER.getId().toString());
            userDomain.setNickName(UserDefaultEnum.SYSTEM_USER.getName());
            userDomain.setCompanyId(applicationVO.getCompanyId());
            UserUtils.setUser(userDomain);
        }
        formDataStreamTrigger.setUserDomain(UserUtils.getUser());
        if ("custom".equals((dataStreamTimeTriggerNode).getTriggerTimeType())) {
            dataStreamExecute(formDataStreamPublish.getFormId(), "time", null, applicationId, formDataStreamTrigger,
                    formDataStreamPublish, new HashMap<>());
        } else {
            Date dateByDaytime = TimeUtils.getDateByDaytime(dataStreamTimeTriggerNode.getDaytime());
            if (dataStreamTimeTriggerNode.getOffset() == null) {
                dataStreamTimeTriggerNode.setOffset(0);
            }
            Date dateByOffset = null;
            if ("m".equals(dataStreamTimeTriggerNode.getOffsetUnit())) {
                dateByOffset = TimeUtils.getMinuteZero(dateByDaytime, -dataStreamTimeTriggerNode.getOffset());
            } else if ("h".equals(dataStreamTimeTriggerNode.getOffsetUnit())) {
                dateByOffset = TimeUtils.getHourZero(dateByDaytime, -dataStreamTimeTriggerNode.getOffset());
            } else {
                dateByOffset = TimeUtils.getDataZero(dateByDaytime, -dataStreamTimeTriggerNode.getOffset());
            }
            MongodbSearchFilter mongodbSearchFilter = new MongodbSearchFilter();
            MongodbSearchCondition mongodbSearchCondition = new MongodbSearchCondition();
            mongodbSearchCondition.setFieldId(dataStreamTimeTriggerNode.getDateFieldId());
            mongodbSearchCondition.setType(FormFieldTypeEnum.INPUT_DATE.getFieldType());
            mongodbSearchCondition.setValue(Collections.singletonList(dateByOffset.getTime()));
            mongodbSearchCondition.setMethod(SearchMethodEnum.GE.name());
            mongodbSearchFilter.setConditionList(Collections.singletonList(mongodbSearchCondition));
            mongodbSearchFilter.setRel("AND");
            FormSearchDataRequest formSearchDataRequest = new FormSearchDataRequest();
            // formSearchDataRequest.setFilter(mongodbSearchFilter);
            formSearchDataRequest.setFormId(dataStreamTimeTriggerNode.getTriggerFormId());
            formSearchDataRequest.setApplicationId(applicationId);
            formSearchDataRequest.setPageSize(5000);
            QueryPageVO<LowcodeDataDomain> lowcodeDataVOQueryPageVO =
                    formMongoDbService.queryListDomain(formSearchDataRequest);
            for (LowcodeDataDomain lowcodeDataDomain : lowcodeDataVOQueryPageVO.getList()) {
                Long end = lowcodeDataDomain.getInstValue().getLong(dataStreamTimeTriggerNode.getDateFieldId());
                if (end == null) {
                    continue;
                }
                boolean timeCycleMatch = TimeIntervalUtils.isTimeCycleMatch(new Date(end), dateByOffset,
                        RepeatTriggerEnum.valueOf(dataStreamTimeTriggerNode.getRepeatTrigger()));
                if (timeCycleMatch) {
                    dataStreamExecute(dataStreamTimeTriggerNode.getTriggerFormId(), "time", lowcodeDataDomain,
                            applicationId, formDataStreamTrigger, formDataStreamPublish, new HashMap<>());
                }
            }
        }
    }

    @Override
    public void delete(String id, String applicationId) {
        LambdaQueryWrapper<FormDataStreamPublishEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataStreamPublishEntity::getId, id);
        queryWrapper.eq(FormDataStreamPublishEntity::getApplicationId, applicationId);
        FormDataStreamPublishEntity formDataStreamPublishEntity = new FormDataStreamPublishEntity();
        formDataStreamPublishEntity.setDeleted(Boolean.TRUE);
        formDataStreamPublishEntity.setModifier(UserUtils.getUser().getNickName());
        formDataStreamPublishMapper.update(formDataStreamPublishEntity, queryWrapper);
    }

    @Override
    public void openOrClose(String id, String applicationId, Boolean enable) {
        LambdaQueryWrapper<FormDataStreamPublishEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataStreamPublishEntity::getId, id);
        queryWrapper.eq(FormDataStreamPublishEntity::getApplicationId, applicationId);
        FormDataStreamPublishEntity formDataStreamPublishEntity = new FormDataStreamPublishEntity();
        formDataStreamPublishEntity.setEnable(enable);
        formDataStreamPublishEntity.setModifier(UserUtils.getUser().getNickName());
        formDataStreamPublishMapper.update(formDataStreamPublishEntity, queryWrapper);
    }

    @Override
    public List<FormDataStreamPublishVO> getByApplicationIdList(String applicationId) {
        LambdaQueryWrapper<FormDataStreamPublishEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormDataStreamPublishEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormDataStreamPublishEntity::getDeleted, Boolean.FALSE);
        queryWrapper.eq(FormDataStreamPublishEntity::getLastVersion, Boolean.TRUE);
        List<FormDataStreamPublishEntity> formDataStreamEntityList =
                formDataStreamPublishMapper.selectList(queryWrapper);
        return formDataStreamEntityList.stream().map(AbstractFormDataStreamPublishConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public void batchPublish(List<FormDataStreamPublishRequest> formDataStreamPublishRequests) {
        List<FormDataStreamPublishEntity> formDataStreamEntityList = new ArrayList<>();
        for (FormDataStreamPublishRequest formDataStreamPublishRequest : formDataStreamPublishRequests) {
            FormDataStreamPublishEntity formDataStreamPublishEntity =
                    AbstractFormDataStreamPublishConverter.INSTANCE.toEntity(formDataStreamPublishRequest);
            formDataStreamPublishEntity.setLastVersion(Boolean.TRUE);
            formDataStreamPublishEntity.setModifier(UserUtils.getUser().getNickName());
            formDataStreamPublishEntity.setCreator(UserUtils.getUser().getNickName());
            formDataStreamEntityList.add(formDataStreamPublishEntity);
        }
        saveBatch(formDataStreamEntityList);
    }


    public void trigger(String formId, String action, LowcodeDataDomain lowcodeDataDomain, String applicationId,
                        FormDataStreamTrigger formDataStreamTrigger, FormDataStreamPublishEntity formDataStreamEntity,
                        Map<String, Map<String, FormConfigEncryptKey>> encryptKeyMap) {
        Boolean needNewThread = DataStreamExecuteUtils.needNewThread();
        if (needNewThread) {
            Runnable task = () -> {
                FormDataStreamTrigger trigger = JSONObject.parseObject(JSONObject.toJSONString(formDataStreamTrigger),
                        FormDataStreamTrigger.class);
                dataStreamExecute(formId, action, lowcodeDataDomain, applicationId, trigger, formDataStreamEntity,
                        encryptKeyMap);
            };
            if (formDataStreamTrigger.getParentList().isEmpty()) {
                dataStreamScheduledExecutor.schedule(task, formDataStreamTrigger.getDelay(), TimeUnit.MILLISECONDS);
            } else {
                dataStreamScheduledExecutor.schedule(task, 1000, TimeUnit.MILLISECONDS);
            }
        } else {
            FormDataStreamTrigger trigger =
                    JSONObject.parseObject(JSONObject.toJSONString(formDataStreamTrigger), FormDataStreamTrigger.class);
            dataStreamExecute(formId, action, lowcodeDataDomain, applicationId, trigger, formDataStreamEntity,
                    encryptKeyMap);
        }
    }

    public void dataStreamExecute(String formId, String action, LowcodeDataDomain lowcodeDataDomain,
                                  String applicationId, FormDataStreamTrigger formDataStreamTrigger,
                                  FormDataStreamPublishEntity formDataStreamEntity,
                                  Map<String, Map<String, FormConfigEncryptKey>> encryptKeyMap) {
        UserUtils.setUser(formDataStreamTrigger.getUserDomain());
        FormVO info = new FormVO();
        if (StringUtils.isNotEmpty(formId)) {
            info = formService.info(formId, applicationId);
        } else {
            info.setId(null);
            info.setApplicationId(applicationId);
        }
        DataStreamTriggerLogDomain dataStreamTriggerLogDomain =
                buildDomain(action, lowcodeDataDomain, formDataStreamEntity, info);
        DataStreamCommon dataStreamCommon;
        try {
            dataStreamCommon = objectMapper.readValue(formDataStreamEntity.getConfig(), DataStreamCommon.class);
        } catch (Exception e) {
            log.error("转化数智助手失败", e);
            dataStreamTriggerLogDomain.setResult("fail");
            dataStreamTriggerLogDomain.setExceptionError("配置解析失败：" + e.getMessage());
            dataStreamTriggerLogDomain.setTriggerEndTime(new Date().getTime());
            mongoTemplate.insert(dataStreamTriggerLogDomain);
            UserUtils.clearUser();
            return;
        }
        try {
            Map<Long, DataStreamCalculateVO> nodeIdMap = new HashMap<>();
            formDataStreamTrigger.setEncryptKeyMap(encryptKeyMap);
            formDataStreamTrigger.setTriggerId(dataStreamTriggerLogDomain.getUuid());
            formDataStreamTrigger.setApplicationId(applicationId);
            formDataStreamTrigger.setAction(action);
            if (lowcodeDataDomain != null) {
                JSONObject jsonObject = FormSystemFieldEnum.putSystemValue(lowcodeDataDomain);
                if (StringUtils.isNotEmpty(lowcodeDataDomain.getProcessInstanceId())) {
                    // 任务
                    List<PendingTaskVO> taskVOList = workFlowService.getTaskByProcessInstanceIdList(
                            Collections.singletonList(lowcodeDataDomain.getProcessInstanceId()));
                    if (CollectionUtils.isNotEmpty(taskVOList)) {
                        PendingTaskVO pendingTaskVO = taskVOList.get(0);
                        jsonObject.put("taskId", pendingTaskVO.getTaskId());
                    }
                }
                formDataStreamTrigger.setJsonObject(jsonObject);
            }
            formDataStreamTrigger.setDataStreamTrigger(dataStreamTriggerLogDomain);
            if (CollectionUtils.isNotEmpty(formDataStreamTrigger.getParentList())) {
                dataStreamTriggerLogDomain.setLastParentId(
                        formDataStreamTrigger.getParentList().get(formDataStreamTrigger.getParentList().size() - 1));
            }
            formDataStreamTrigger.getParentList().add(dataStreamTriggerLogDomain.getUuid());
            String bizKey = applicationId + "_" + formDataStreamEntity.getId();
            Lock lock = stripedLock.get(bizKey);

            lock.lock(); // 阻塞等待，直到获取锁
            try {
                dataStreamTriggerService.trigger(formDataStreamTrigger, dataStreamCommon, nodeIdMap);
            } finally {
                lock.unlock();
            }
            if ("trigger".equals(formDataStreamTrigger.getDataStreamTrigger().getResult())) {
                log.info("formId:{} , applicationId:{}, action:{}, triggerId:{}, triggerUuid:{}", formId, applicationId,
                        action, formDataStreamEntity.getId(), formDataStreamTrigger.getTriggerId());
                dataStreamTriggerLogDomain.setResult("success");
                dataStreamTriggerLogDomain.setTriggerEndTime(new Date().getTime());
                if (formDataStreamTrigger.getParentList().size() > 1) {
                    dataStreamTriggerLogDomain.setParentList(formDataStreamTrigger.getParentList()
                            .subList(0, formDataStreamTrigger.getParentList().size() - 1));
                }
                mongoTemplate.insert(dataStreamTriggerLogDomain);
            }
        } catch (Exception e) {
            log.info("formId:{} , applicationId:{}, action:{}, triggerId:{}, triggerUuid:{}", formId, applicationId,
                    action, formDataStreamEntity.getId(), formDataStreamTrigger.getTriggerId());
            dataStreamTriggerLogDomain.setResult("fail");
            dataStreamTriggerLogDomain.setExceptionError(e.getMessage());
            dataStreamTriggerLogDomain.setTriggerEndTime(new Date().getTime());
            dataStreamTriggerLogDomain.setParentList(formDataStreamTrigger.getParentList());
            mongoTemplate.insert(dataStreamTriggerLogDomain);
            log.error("智能助手运行失败", e);
            throw new BizException("智能助手运行失败：" + e.getMessage());
        } finally {
            UserUtils.clearUser();
        }
    }

    private DataStreamTriggerLogDomain buildDomain(String action, LowcodeDataDomain lowcodeDataDomain,
                                                   FormDataStreamPublishEntity formDataStreamEntity, FormVO info) {
        DataStreamTriggerLogDomain dataStreamTriggerLogDomain = new DataStreamTriggerLogDomain();
        dataStreamTriggerLogDomain.setCreateName(UserUtils.getUser().getNickName());
        dataStreamTriggerLogDomain.setCreator(UserUtils.getUser().getUserId());
        dataStreamTriggerLogDomain.setFormId(info.getId());
        dataStreamTriggerLogDomain.setAction(action);
        dataStreamTriggerLogDomain.setApplicationId(info.getApplicationId());
        dataStreamTriggerLogDomain.setTriggerStartTime(new Date().getTime());
        if (lowcodeDataDomain != null) {
            formExtraFunctionTitleService.buildTitle(Collections.singletonList(lowcodeDataDomain), info.getConfig(),
                    info.getId(), info.getApplicationId());
            dataStreamTriggerLogDomain.setTitle(
                    new FormDataTitle(lowcodeDataDomain.getUuid(), lowcodeDataDomain.getTitle()));
        }
        dataStreamTriggerLogDomain.setDataStreamId(formDataStreamEntity.getId());
        dataStreamTriggerLogDomain.setDataStreamVersion(formDataStreamEntity.getVersion());
        String guid = ObjectId.getGuid();
        dataStreamTriggerLogDomain.setUuid(guid);
        return dataStreamTriggerLogDomain;
    }
}
