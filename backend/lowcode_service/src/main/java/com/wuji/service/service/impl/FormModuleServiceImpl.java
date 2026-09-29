package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractFormModuleConverter;
import com.wuji.service.converter.AbstractInstrumentPanelConverter;
import com.wuji.service.mapper.FormModuleMapper;
import com.wuji.service.model.entity.FormModuleEntity;
import com.wuji.service.model.request.FormModuleInsertRequest;
import com.wuji.service.model.request.FormModuleUpdateRequest;
import com.wuji.service.model.request.FormSearchDataRequest;
import com.wuji.service.model.request.InstrumentPanelViewListRequest;
import com.wuji.service.model.request.MongodbAggregateRequest;
import com.wuji.service.model.vo.ApplicationCategoryVO;
import com.wuji.service.model.vo.FormAggregateVO;
import com.wuji.service.model.vo.FormDataFactoryVO;
import com.wuji.service.model.vo.FormModuleBusinessVO;
import com.wuji.service.model.vo.FormModuleVO;
import com.wuji.service.model.vo.LowcodeDataVO;
import com.wuji.service.model.vo.TemplateFormModuleVO;
import com.wuji.service.service.ApplicationCategoryService;
import com.wuji.service.service.FormAggregateService;
import com.wuji.service.service.FormDataFactoryService;
import com.wuji.service.service.FormModuleService;
import com.wuji.service.service.FormMongoDbService;
import com.wuji.service.service.FormQuoteService;
import com.wuji.service.service.TemplateFormModuleService;
import com.wuji.service.utils.TemplateDealConfigUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 表单组件表 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-11-20
 */
@Service
public class FormModuleServiceImpl extends ServiceImpl<FormModuleMapper, FormModuleEntity>
        implements FormModuleService {

    @Autowired
    private FormModuleMapper formModuleMapper;

    @Autowired
    private ApplicationCategoryService applicationCategoryService;

    @Autowired
    private TemplateFormModuleService templateFormModuleService;

    @Autowired
    private FormAggregateService formAggregateService;

    @Autowired
    private FormQuoteService formQuoteService;

    @Autowired
    private FormDataFactoryService formDataFactoryService;

    @Autowired
    private FormMongoDbService formMongoDbService;

    @Override
    public String insert(FormModuleInsertRequest formModuleInsertRequest) {
        FormModuleEntity formModuleEntity = AbstractFormModuleConverter.INSTANCE.toEntity(formModuleInsertRequest);
        if (CollectionUtils.isNotEmpty(formModuleInsertRequest.getBusinessIdList())) {
            formModuleEntity.setBusinessId(JSONArray.toJSONString(formModuleInsertRequest.getBusinessIdList()));
        }
        formModuleEntity.setCreator(UserUtils.getUser().getNickName());
        formModuleEntity.setModifier(UserUtils.getUser().getNickName());
        formModuleEntity.setId(ObjectId.getGuid());
        formModuleMapper.insert(formModuleEntity);
        return formModuleEntity.getId();
    }

    @Override
    public void update(FormModuleUpdateRequest formModuleUpdateRequest) {
        FormModuleEntity formModuleEntity = AbstractFormModuleConverter.INSTANCE.toEntity(formModuleUpdateRequest);
        formModuleEntity.setModifier(UserUtils.getUser().getNickName());
        if (CollectionUtils.isNotEmpty(formModuleUpdateRequest.getBusinessIdList())) {
            formModuleEntity.setBusinessId(JSONArray.toJSONString(formModuleUpdateRequest.getBusinessIdList()));
        }
        LambdaQueryWrapper<FormModuleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormModuleEntity::getId, formModuleUpdateRequest.getId());
        queryWrapper.eq(FormModuleEntity::getApplicationId, formModuleUpdateRequest.getApplicationId());
        queryWrapper.eq(FormModuleEntity::getFormId, formModuleUpdateRequest.getFormId());
        queryWrapper.eq(FormModuleEntity::getDeleted, Boolean.FALSE);
        formModuleMapper.update(formModuleEntity, queryWrapper);
    }

    @Override
    public List<FormModuleVO> getByFormId(String formId, String applicationId) {
        LambdaQueryWrapper<FormModuleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormModuleEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormModuleEntity::getFormId, formId);
        queryWrapper.eq(FormModuleEntity::getDeleted, Boolean.FALSE);
        List<FormModuleEntity> formModuleEntities = formModuleMapper.selectList(queryWrapper);
        List<FormModuleVO> formModuleVOList = new ArrayList<>();
        for (FormModuleEntity formModuleEntity : formModuleEntities) {
            FormModuleVO formModuleVO = AbstractFormModuleConverter.INSTANCE.toVO(formModuleEntity);
            if (StringUtils.isNotEmpty(formModuleEntity.getBusinessId())) {
                formModuleVO.setBusinessIdList(JSONArray.parseArray(formModuleEntity.getBusinessId(), String.class));
            }
            formModuleVOList.add(formModuleVO);
        }
        return formModuleVOList;
    }

    @Override
    public List<FormModuleVO> getByApplicationId(String applicationId) {
        LambdaQueryWrapper<FormModuleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormModuleEntity::getDeleted, Boolean.FALSE);
        queryWrapper.eq(FormModuleEntity::getApplicationId, applicationId);
        List<FormModuleEntity> formModuleEntities = formModuleMapper.selectList(queryWrapper);
        List<FormModuleVO> formModuleVOList = new ArrayList<>();
        for (FormModuleEntity formModuleEntity : formModuleEntities) {
            FormModuleVO formModuleVO = AbstractFormModuleConverter.INSTANCE.toVO(formModuleEntity);
            formModuleVOList.add(formModuleVO);
        }
        return formModuleVOList;
    }

    @Override
    public FormModuleVO info(String id, String applicationId, String formId) {
        LambdaQueryWrapper<FormModuleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormModuleEntity::getId, id);
        queryWrapper.eq(FormModuleEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormModuleEntity::getFormId, formId);
        queryWrapper.eq(FormModuleEntity::getDeleted, Boolean.FALSE);
        FormModuleEntity formModule = formModuleMapper.selectOne(queryWrapper);
        if (formModule == null) {
            return null;
        }
        FormModuleVO formModuleVO = AbstractFormModuleConverter.INSTANCE.toVO(formModule);
        if (StringUtils.isNotEmpty(formModuleVO.getBusinessId())) {
            List<String> businessList = JSONArray.parseArray(formModuleVO.getBusinessId(), String.class);
            Map<String, String> nameMap = new HashMap<>();
            if ("FORM".equals(formModuleVO.getBusinessType()) ||
                    "FLOWABLE_FORM".equals(formModuleVO.getBusinessType())) {
                List<ApplicationCategoryVO> applicationCategoryVOList =
                        applicationCategoryService.getByIdList(businessList, applicationId);
                nameMap = applicationCategoryVOList.stream().collect(
                        Collectors.toMap(ApplicationCategoryVO::getId, ApplicationCategoryVO::getCategoryName));
            } else if ("AGG".equals(formModuleVO.getBusinessType())) {
                List<FormAggregateVO> formAggregateVOList =
                        formAggregateService.getByIdList(applicationId, businessList);
                nameMap = formAggregateVOList.stream()
                        .collect(Collectors.toMap(FormAggregateVO::getId, FormAggregateVO::getName));
            } else if ("FACTORY".equals(formModuleVO.getBusinessType())) {
                List<FormDataFactoryVO> formDataFactoryVOS =
                        formDataFactoryService.queryByIds(applicationId, businessList);
                nameMap = formDataFactoryVOS.stream()
                        .collect(Collectors.toMap(FormDataFactoryVO::getId, FormDataFactoryVO::getFactoryName));
            }
            List<FormModuleBusinessVO> businessVOList = new ArrayList<>();
            for (String businessId : businessList) {
                FormModuleBusinessVO formModuleBusinessVO = new FormModuleBusinessVO();
                formModuleBusinessVO.setBusinessId(businessId);
                formModuleBusinessVO.setBusinessName(nameMap.get(businessId));
                businessVOList.add(formModuleBusinessVO);
            }
            formModuleVO.setBusinessList(businessVOList);
        }
        return formModuleVO;

    }

    @Override
    public void deleteExtra(List<String> idList, String applicationId, String formId) {
        LambdaQueryWrapper<FormModuleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormModuleEntity::getFormId, formId);
        queryWrapper.eq(FormModuleEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormModuleEntity::getDeleted, Boolean.FALSE);
        queryWrapper.notIn(CollectionUtils.isNotEmpty(idList), FormModuleEntity::getId, idList);
        List<FormModuleEntity> formModuleEntities = formModuleMapper.selectList(queryWrapper);
        FormModuleEntity formModuleEntity = new FormModuleEntity();
        formModuleEntity.setDeleted(Boolean.TRUE);
        formModuleEntity.setModifier(UserUtils.getUser().getNickName());
        formModuleMapper.update(formModuleEntity, queryWrapper);
        formQuoteService.delete(formId, applicationId,
                formModuleEntities.stream().map(FormModuleEntity::getId).collect(Collectors.toList()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void useTemplate(String applicationId, String templateApplicationId, String sourceApplicationId) {
        List<TemplateFormModuleVO> templateFormModuleVOList =
                templateFormModuleService.getByApplicationId(templateApplicationId);
        if (CollectionUtils.isEmpty(templateFormModuleVOList)) {
            return;
        }
        List<FormModuleEntity> formModuleEntityList = new ArrayList<>();
        for (TemplateFormModuleVO templateFormModuleVO : templateFormModuleVOList) {
            FormModuleEntity formModuleEntity = AbstractFormModuleConverter.INSTANCE.toEntity(templateFormModuleVO);
            formModuleEntity.setApplicationId(applicationId);
            formModuleEntity.setCreator(UserUtils.getUser().getNickName());
            formModuleEntity.setModifier(UserUtils.getUser().getNickName());
            String config = TemplateDealConfigUtil.deal(sourceApplicationId, applicationId,
                    templateFormModuleVO.getModuleType(), templateFormModuleVO.getConfig(), applicationId);
            formModuleEntity.setConfig(config);
            formModuleEntityList.add(formModuleEntity);
        }
        saveBatch(formModuleEntityList);
    }

    @Override
    public void copyApplication(String applicationId, String sourceApplicationId, Boolean share) {
        List<FormModuleVO> sourceFormModuleVOList = getByApplicationId(sourceApplicationId);
        if (CollectionUtils.isEmpty(sourceFormModuleVOList)) {
            return;
        }
        List<FormModuleEntity> formModuleEntityList = new ArrayList<>();
        for (FormModuleVO formModuleVO : sourceFormModuleVOList) {
            FormModuleEntity formModuleEntity = AbstractFormModuleConverter.INSTANCE.toEntity(formModuleVO);
            formModuleEntity.setApplicationId(applicationId);
            formModuleEntity.setCreator(UserUtils.getUser().getNickName());
            formModuleEntity.setModifier(UserUtils.getUser().getNickName());
            if (share) {
                String config = TemplateDealConfigUtil.dealWhileCopy(sourceApplicationId, formModuleVO.getModuleType(),
                        formModuleVO.getConfig(), applicationId);
                formModuleEntity.setConfig(config);
            }
            formModuleEntityList.add(formModuleEntity);
        }
        saveBatch(formModuleEntityList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void copyForm(String applicationId, String formId, String sourceFormId) {
        List<FormModuleVO> sourceFormModuleVOList = getByFormId(sourceFormId, applicationId);
        if (CollectionUtils.isEmpty(sourceFormModuleVOList)) {
            return;
        }
        List<FormModuleEntity> formModuleEntityList = new ArrayList<>();
        for (FormModuleVO formModuleVO : sourceFormModuleVOList) {
            FormModuleEntity formModuleEntity = AbstractFormModuleConverter.INSTANCE.toEntity(formModuleVO);
            formModuleEntity.setFormId(formId);
            // formModuleEntity.setId(ObjectId.getGuid());
            formModuleEntity.setApplicationId(applicationId);
            formModuleEntity.setCreator(UserUtils.getUser().getNickName());
            formModuleEntity.setModifier(UserUtils.getUser().getNickName());
            formModuleEntityList.add(formModuleEntity);
        }
        saveBatch(formModuleEntityList);
    }

    @Override
    public QueryPageVO<LowcodeDataVO> queryList(InstrumentPanelViewListRequest instrumentPanelViewListRequest) {
        LambdaQueryWrapper<FormModuleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormModuleEntity::getId, instrumentPanelViewListRequest.getModuleId());
        queryWrapper.eq(FormModuleEntity::getApplicationId, instrumentPanelViewListRequest.getApplicationId());
        queryWrapper.eq(FormModuleEntity::getFormId, instrumentPanelViewListRequest.getModuleFormId());
        queryWrapper.eq(FormModuleEntity::getDeleted, Boolean.FALSE);
        FormModuleEntity formModule = formModuleMapper.selectOne(queryWrapper);
        FormSearchDataRequest formSearchDataRequest =
                AbstractInstrumentPanelConverter.INSTANCE.toRequest(instrumentPanelViewListRequest);
        MongodbAggregateRequest mongodbAggregateRequest =
                JSONObject.parseObject(formModule.getConfig(), MongodbAggregateRequest.class);
        formSearchDataRequest.setViewFilter(mongodbAggregateRequest.getWidget().getFilter());
        return formMongoDbService.queryList(formSearchDataRequest);
    }
}
