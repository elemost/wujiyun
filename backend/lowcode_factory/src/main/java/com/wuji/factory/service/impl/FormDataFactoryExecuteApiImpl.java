package com.wuji.factory.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wuji.common.api.FormDataFactoryExecuteApi;
import com.wuji.common.enums.ResultCode;
import com.wuji.common.model.vo.DataFactoryInputFormVO;
import com.wuji.common.model.vo.DataFactoryReturnFieldCommonVO;
import com.wuji.common.model.vo.DataFactoryStageAllFieldVO;
import com.wuji.common.model.vo.DataFactoryStageCommonFieldVO;
import com.wuji.common.model.vo.DataFactoryStageInfoVO;
import com.wuji.common.model.vo.FormDataFactoryParamVO;
import com.wuji.factory.converter.AbstractFormDataFactoryExecuteConverter;
import com.wuji.factory.model.info.DataFactoryConfig;
import com.wuji.factory.model.info.DataFactoryInputStage;
import com.wuji.factory.model.info.DataFactoryStage;
import com.wuji.factory.model.request.DataFactoryRequest;
import com.wuji.factory.model.vo.DataFactoryStageFieldVO;
import com.wuji.factory.model.vo.DataFactoryStageVO;
import com.wuji.factory.model.vo.FormDataFactoryBuildVO;
import com.wuji.factory.service.FormDataFactoryExecuteService;
import com.wuji.service.exception.ServiceException;
import com.wuji.service.model.request.factory.DataFactoryStageDataSourceRequest;
import com.wuji.service.model.vo.FormDataFactoryPublishVO;
import com.wuji.service.service.FormDataFactoryPublishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FormDataFactoryExecuteApiImpl implements FormDataFactoryExecuteApi {

    @Autowired
    private FormDataFactoryExecuteService formDataFactoryExecuteService;

    @Autowired
    private FormDataFactoryPublishService formDataFactoryPublishService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public DataFactoryStageInfoVO getStageField(String applicationId, String id) {
        FormDataFactoryPublishVO lastVersion = formDataFactoryPublishService.getLastVersion(applicationId, id);
        DataFactoryRequest dataFactoryRequest = new DataFactoryRequest();
        dataFactoryRequest.setApplicationId(applicationId);
        dataFactoryRequest.setStageId("end");
        DataFactoryConfig dataFactoryConfig;
        try {
            dataFactoryConfig = objectMapper.readValue(lastVersion.getFactoryConfig(), DataFactoryConfig.class);
        } catch (Exception e) {
            log.error("转化数智助手失败", e);
            return null;
        }
        dataFactoryRequest.setDataFactoryStageList(dataFactoryConfig.getDataFactoryStageList());
        List<DataFactoryStageFieldVO> dataFactoryStageFieldVOS =
                formDataFactoryExecuteService.stageField(dataFactoryRequest);
        DataFactoryStageInfoVO dataFactoryStageInfoVO = new DataFactoryStageInfoVO();
        List<DataFactoryStageCommonFieldVO> fields =
                dataFactoryStageFieldVOS.stream().map(AbstractFormDataFactoryExecuteConverter.INSTANCE::toVO)
                        .collect(Collectors.toList());
        dataFactoryStageInfoVO.setFactoryName(lastVersion.getFactoryName());
        dataFactoryStageInfoVO.setFields(fields);
        return dataFactoryStageInfoVO;
    }

    @Override
    public FormDataFactoryParamVO getParam(String applicationId, String id) {
        FormDataFactoryPublishVO lastVersion = formDataFactoryPublishService.getLastVersion(applicationId, id);
        DataFactoryRequest dataFactoryRequest = new DataFactoryRequest();
        dataFactoryRequest.setApplicationId(applicationId);
        dataFactoryRequest.setStageId("end");
        DataFactoryConfig dataFactoryConfig;
        try {
            dataFactoryConfig = objectMapper.readValue(lastVersion.getFactoryConfig(), DataFactoryConfig.class);
        } catch (Exception e) {
            log.error("转化数智助手失败", e);
            return null;
        }
        dataFactoryRequest.setDataFactoryStageList(dataFactoryConfig.getDataFactoryStageList());
        FormDataFactoryBuildVO build = formDataFactoryExecuteService.getParam(dataFactoryRequest);
        FormDataFactoryParamVO formDataFactoryParamVO = new FormDataFactoryParamVO();
        formDataFactoryParamVO.setAggregationOperations(build.getAggregationOperations());
        formDataFactoryParamVO.setTableName(build.getTableName());
        DataFactoryStage dataFactoryStage = build.getStageIdToMap().get("end");
        if (dataFactoryStage == null) {
            throw new ServiceException(ResultCode.DATA_STREAM_CONFIG_NOT_EXIST);
        }
        List<DataFactoryReturnFieldCommonVO> fields =
                dataFactoryStage.getReturnFields().stream().map(AbstractFormDataFactoryExecuteConverter.INSTANCE::toVO)
                        .collect(Collectors.toList());
        formDataFactoryParamVO.setFields(fields);
        return formDataFactoryParamVO;
    }

    @Override
    public List<DataFactoryStageAllFieldVO> getAllFinalField(String applicationId) {
        List<DataFactoryStageAllFieldVO> dataFactoryStageAllFieldVOS = new ArrayList<>();
        List<DataFactoryStageVO> infoByIds = formDataFactoryExecuteService.getInfoByIds(null, applicationId);
        for (DataFactoryStageVO dataFactoryStageVO : infoByIds) {
            DataFactoryStageAllFieldVO dataFactoryStageInfoVO = new DataFactoryStageAllFieldVO();
            if (CollectionUtils.isEmpty(dataFactoryStageVO.getFields())) {
                continue;
            }
            List<DataFactoryReturnFieldCommonVO> fields =
                    dataFactoryStageVO.getFields().stream().map(AbstractFormDataFactoryExecuteConverter.INSTANCE::toVO)
                            .collect(Collectors.toList());
            dataFactoryStageInfoVO.setName(dataFactoryStageVO.getName());
            dataFactoryStageInfoVO.setId(dataFactoryStageVO.getId());
            dataFactoryStageInfoVO.setFields(fields);
            dataFactoryStageAllFieldVOS.add(dataFactoryStageInfoVO);
        }
        return dataFactoryStageAllFieldVOS;
    }

    @Override
    public List<DataFactoryInputFormVO> getAllInput(String applicationId, List<String> ids) {
        List<FormDataFactoryPublishVO> formDataFactoryPublishVOS =
                formDataFactoryPublishService.queryPublishList(applicationId, ids);
        List<DataFactoryInputFormVO> dataFactoryInputFormVOList = new ArrayList<>();
        for (FormDataFactoryPublishVO formDataFactoryPublish : formDataFactoryPublishVOS) {
            if (StringUtils.isEmpty(formDataFactoryPublish.getFactoryConfig())) {
                continue;
            }
            DataFactoryConfig dataFactoryConfig;
            try {
                dataFactoryConfig =
                        objectMapper.readValue(formDataFactoryPublish.getFactoryConfig(), DataFactoryConfig.class);
            } catch (Exception e) {
                log.error("转化数智助手失败", e);
                continue;
            }
            List<DataFactoryStage> dataFactoryStageList = dataFactoryConfig.getDataFactoryStageList();
            List<DataFactoryInputFormVO.Input> inputs = new ArrayList<>();
            for (DataFactoryStage dataFactoryStage : dataFactoryStageList) {
                if ("input".equals(dataFactoryStage.getType())) {
                    DataFactoryInputFormVO.Input input = new DataFactoryInputFormVO.Input();
                    DataFactoryInputStage dataFactoryInputStage = (DataFactoryInputStage) dataFactoryStage;
                    DataFactoryStageDataSourceRequest dataSource = dataFactoryInputStage.getDataSource();
                    input.setFormId(dataSource.getFormId());
                    if (StringUtils.isNotEmpty(dataSource.getApplicationId())) {
                        input.setApplicationId(dataSource.getApplicationId());
                    } else {
                        input.setApplicationId(applicationId);
                    }
                    inputs.add(input);
                }
            }
            DataFactoryInputFormVO dataFactoryInputFormVO = new DataFactoryInputFormVO();
            dataFactoryInputFormVO.setInputs(inputs);
            dataFactoryInputFormVO.setId(formDataFactoryPublish.getId());
            dataFactoryInputFormVO.setVersion(formDataFactoryPublish.getVersion());
            dataFactoryInputFormVOList.add(dataFactoryInputFormVO);
        }
        return dataFactoryInputFormVOList;
    }
}
