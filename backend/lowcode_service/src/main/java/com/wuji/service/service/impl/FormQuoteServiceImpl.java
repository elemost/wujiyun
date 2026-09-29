package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.constant.Constants;
import com.wuji.service.converter.AbstractFormQuoteConverter;
import com.wuji.service.enums.ApplicationCategoryCategoryTypeEnum;
import com.wuji.service.enums.FormQuoteTypeEnum;
import com.wuji.service.mapper.FormQuoteMapper;
import com.wuji.service.model.entity.FormQuoteEntity;
import com.wuji.service.model.info.FormConfigCommon;
import com.wuji.service.model.request.FormQuoteSaveInfoRequest;
import com.wuji.service.model.request.FormQuoteSaveRequest;
import com.wuji.service.model.request.RelevanceRelationRequest;
import com.wuji.service.model.vo.FormAggregateVO;
import com.wuji.service.model.vo.FormModuleVO;
import com.wuji.service.model.vo.FormQuoteInfoVO;
import com.wuji.service.model.vo.FormQuoteVO;
import com.wuji.service.model.vo.FormVO;
import com.wuji.service.model.vo.RelevanceRelationVO;
import com.wuji.service.model.vo.TemplateFormQuoteVO;
import com.wuji.service.service.ApplicationCategoryService;
import com.wuji.service.service.FormAggregateService;
import com.wuji.service.service.FormModuleService;
import com.wuji.service.service.FormQuoteService;
import com.wuji.service.service.FormService;
import com.wuji.service.service.TemplateFormQuoteService;
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
 * @since 2024-11-09
 */
@Service
public class FormQuoteServiceImpl extends ServiceImpl<FormQuoteMapper, FormQuoteEntity> implements FormQuoteService {

    @Autowired
    private FormQuoteMapper formQuoteMapper;

    @Autowired
    private ApplicationCategoryService applicationCategoryService;

    @Autowired
    private TemplateFormQuoteService templateFormQuoteService;

    @Autowired
    private FormAggregateService formAggregateService;

    @Autowired
    private FormService formService;

    @Autowired
    private FormModuleService formModuleService;


    @Override
    public void save(FormQuoteSaveRequest formQuoteSaveRequest) {
        LambdaQueryWrapper<FormQuoteEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormQuoteEntity::getFormId, formQuoteSaveRequest.getFormId());
        // queryWrapper.eq(FormQuoteEntity::getBusinessType, formQuoteSaveRequest.getBusinessType());
        if ("DASH".equals(formQuoteSaveRequest.getType())) {
            queryWrapper.eq(StringUtils.isNotEmpty(formQuoteSaveRequest.getBusinessId()),
                    FormQuoteEntity::getBusinessId, formQuoteSaveRequest.getBusinessId());
        }
        queryWrapper.eq(FormQuoteEntity::getApplicationId, formQuoteSaveRequest.getApplicationId());
        formQuoteMapper.delete(queryWrapper);
        if (CollectionUtils.isEmpty(formQuoteSaveRequest.getFormQuoteSaveInfoList())) {
            return;
        }
        UserDomain user = UserUtils.getUser();
        List<FormQuoteEntity> formQuoteEntityList = new ArrayList<>();
        for (FormQuoteSaveInfoRequest formQuoteSaveInfoRequest : formQuoteSaveRequest.getFormQuoteSaveInfoList()) {
            FormQuoteEntity formQuoteEntity = AbstractFormQuoteConverter.INSTANCE.toEntity(formQuoteSaveInfoRequest);
            formQuoteEntity.setFormId(formQuoteSaveRequest.getFormId());
            formQuoteEntity.setBusinessType(formQuoteSaveInfoRequest.getBusinessType());
            formQuoteEntity.setApplicationId(formQuoteSaveRequest.getApplicationId());
            formQuoteEntity.setCreatorName(user.getNickName());
            formQuoteEntity.setModifierName(user.getNickName());
            formQuoteEntityList.add(formQuoteEntity);
        }
        saveBatch(formQuoteEntityList);
    }

    @Override
    public FormQuoteVO getInfo(String formId, String applicationId) {
        LambdaQueryWrapper<FormQuoteEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormQuoteEntity::getFormId, formId);
        queryWrapper.eq(FormQuoteEntity::getApplicationId, applicationId);
        queryWrapper.eq(FormQuoteEntity::getDeleted, false);
        List<FormQuoteEntity> formQuoteList = formQuoteMapper.selectList(queryWrapper);

        LambdaQueryWrapper<FormQuoteEntity> queryQuotedWrapper = new LambdaQueryWrapper<>();
        queryQuotedWrapper.eq(FormQuoteEntity::getQuoteFormId, formId);
        queryQuotedWrapper.eq(FormQuoteEntity::getApplicationId, applicationId);
        queryQuotedWrapper.eq(FormQuoteEntity::getDeleted, false);
        List<FormQuoteEntity> formQuoteQuotedList = formQuoteMapper.selectList(queryQuotedWrapper);
        List<String> formIdList = formQuoteList.stream().map(FormQuoteEntity::getFormId).collect(Collectors.toList());
        formIdList.addAll(formQuoteQuotedList.stream().map(FormQuoteEntity::getFormId).collect(Collectors.toList()));
        formIdList.addAll(
                formQuoteQuotedList.stream().map(FormQuoteEntity::getQuoteFormId).collect(Collectors.toList()));
        formIdList.addAll(formQuoteList.stream().map(FormQuoteEntity::getFormId).collect(Collectors.toList()));

        List<FormVO> formVOS = formService.getByIdList(formIdList, applicationId);
        Map<String, String> formNameMap =
                formVOS.stream().collect(Collectors.toMap(FormVO::getId, FormVO::getFormName));
        List<FormAggregateVO> formAggregateVOS = formAggregateService.getByIdList(applicationId, formIdList);
        Map<String, String> formAggNameMap =
                formAggregateVOS.stream().collect(Collectors.toMap(FormAggregateVO::getId, FormAggregateVO::getName));
        FormQuoteVO formQuoteVO = new FormQuoteVO();
        formQuoteVO.setFormQuoteInfoQuoteList(buildFormQuoteInfoList(formQuoteList, formNameMap, formAggNameMap));
        formQuoteVO.setFormQuoteInfoQuotedList(
                buildFormQuoteInfoList(formQuoteQuotedList, formNameMap, formAggNameMap));
        return formQuoteVO;
    }

    @Override
    public void delete(String formId, String applicationId, List<String> businessIdList) {
        LambdaQueryWrapper<FormQuoteEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormQuoteEntity::getFormId, formId);
        queryWrapper.eq(FormQuoteEntity::getDeleted, false);
        queryWrapper.in(CollectionUtils.isNotEmpty(businessIdList), FormQuoteEntity::getBusinessId, businessIdList);
        FormQuoteEntity formQuoteEntity = new FormQuoteEntity();
        formQuoteEntity.setDeleted(Boolean.TRUE);
        formQuoteEntity.setModifierName(UserUtils.getUser().getNickName());
        formQuoteMapper.update(formQuoteEntity, queryWrapper);
    }

    @Override
    public List<FormQuoteInfoVO> getByFormIdList(String applicationId, List<String> formIdList) {
        if (CollectionUtils.isEmpty(formIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<FormQuoteEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormQuoteEntity::getApplicationId, applicationId);
        queryWrapper.in(FormQuoteEntity::getFormId, formIdList);
        queryWrapper.eq(FormQuoteEntity::getDeleted, false);
        List<FormQuoteEntity> formQuoteEntityList = formQuoteMapper.selectList(queryWrapper);

        List<FormQuoteInfoVO> formQuoteInfoVOList = new ArrayList<>();
        for (FormQuoteEntity formQuoteEntity : formQuoteEntityList) {
            FormQuoteInfoVO formQuoteInfoVO = AbstractFormQuoteConverter.INSTANCE.toVO(formQuoteEntity);
            formQuoteInfoVOList.add(formQuoteInfoVO);
        }
        return formQuoteInfoVOList;
    }

    @Override
    public void useTemplate(String applicationId, String templateApplicationId) {
        List<TemplateFormQuoteVO> templateFormQuoteVOList =
                templateFormQuoteService.getByApplicationId(templateApplicationId);
        if (CollectionUtils.isEmpty(templateFormQuoteVOList)) {
            return;
        }
        UserDomain user = UserUtils.getUser();
        List<FormQuoteEntity> formQuoteEntityList = new ArrayList<>();
        for (TemplateFormQuoteVO templateFormQuoteVO : templateFormQuoteVOList) {
            FormQuoteEntity formQuoteEntity = AbstractFormQuoteConverter.INSTANCE.toEntity(templateFormQuoteVO);
            formQuoteEntity.setApplicationId(applicationId);
            formQuoteEntity.setCreatorName(user.getNickName());
            formQuoteEntity.setModifierName(user.getNickName());
            formQuoteEntityList.add(formQuoteEntity);
        }
        saveBatch(formQuoteEntityList);
    }

    private List<FormQuoteInfoVO> buildFormQuoteInfoList(List<FormQuoteEntity> formQuoteEntityList,
                                                         Map<String, String> formNameMap,
                                                         Map<String, String> formAggNameMap) {
        List<FormQuoteInfoVO> formQuoteInfoVOList = new ArrayList<>();
        for (FormQuoteEntity formQuoteEntity : formQuoteEntityList) {
            FormQuoteInfoVO formQuoteInfoVO = AbstractFormQuoteConverter.INSTANCE.toVO(formQuoteEntity);
            if (FormQuoteTypeEnum.AGG.name().equals(formQuoteEntity.getQuoteType())) {
                formQuoteInfoVO.setQuoteFormName(formAggNameMap.get(formQuoteEntity.getQuoteFormId()));
            } else {
                formQuoteInfoVO.setQuoteFormName(formNameMap.get(formQuoteEntity.getQuoteFormId()));
            }
            if (formQuoteEntity.getFormId().startsWith(Constants.AGGREGATE_TABLE)) {
                formQuoteInfoVO.setFormName(formAggNameMap.get(formQuoteEntity.getFormId()));
            } else {
                formQuoteInfoVO.setFormName(formNameMap.get(formQuoteEntity.getFormId()));
            }
            formQuoteInfoVOList.add(formQuoteInfoVO);
        }
        return formQuoteInfoVOList;
    }

    @Override
    public List<RelevanceRelationVO> relevanceRelation(RelevanceRelationRequest relevanceRelationRequest) {
        List<RelevanceRelationVO> relevanceRelationVOS = new ArrayList<>();
        FormVO info =
                formService.info(relevanceRelationRequest.getFormId(), relevanceRelationRequest.getApplicationId());
        if (ApplicationCategoryCategoryTypeEnum.DASH.name().equals(info.getFormType())) {
            List<FormModuleVO> formModuleVOList = formModuleService.getByFormId(relevanceRelationRequest.getFormId(),
                    relevanceRelationRequest.getApplicationId());
            Map<String, String> nameMap =
                    formModuleVOList.stream().collect(Collectors.toMap(FormModuleVO::getId, FormModuleVO::getName));
            for (String businessId : relevanceRelationRequest.getBusinessIdList()) {
                RelevanceRelationVO relevanceRelationVO = new RelevanceRelationVO();
                relevanceRelationVO.setBusinessId(businessId);
                relevanceRelationVO.setBusinessName(nameMap.get(businessId));
                relevanceRelationVOS.add(relevanceRelationVO);
            }
        } else if (ApplicationCategoryCategoryTypeEnum.getFormType().contains(info.getFormType())) {
            List<FormConfigCommon> configSpreadList =
                    formService.getAllFormConfigCommonList(relevanceRelationRequest.getFormId(), Boolean.TRUE,
                            relevanceRelationRequest.getApplicationId(), Boolean.TRUE).getFields();
            Map<String, FormConfigCommon> nameMap =
                    configSpreadList.stream().collect(Collectors.toMap(FormConfigCommon::getName, c -> c));
            for (String businessId : relevanceRelationRequest.getBusinessIdList()) {
                RelevanceRelationVO relevanceRelationVO = new RelevanceRelationVO();
                relevanceRelationVO.setBusinessId(businessId);
                FormConfigCommon formConfigCommon = nameMap.get(businessId);
                if (formConfigCommon != null) {
                    if (StringUtils.isNotEmpty(formConfigCommon.getSubFromLabel())) {
                        relevanceRelationVO.setBusinessName(
                                formConfigCommon.getSubFromLabel() + "_" + formConfigCommon.getLabel());
                    } else {
                        relevanceRelationVO.setBusinessName(formConfigCommon.getLabel());
                    }
                }
                relevanceRelationVOS.add(relevanceRelationVO);
            }
        }
        return relevanceRelationVOS;
    }

    @Override
    public void copyApplication(String applicationId, String sourceApplicationId) {
        LambdaQueryWrapper<FormQuoteEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FormQuoteEntity::getApplicationId, sourceApplicationId);
        queryWrapper.eq(FormQuoteEntity::getDeleted, false);
        List<FormQuoteEntity> sourceFormQuoteEntityList = formQuoteMapper.selectList(queryWrapper);

        if (CollectionUtils.isEmpty(sourceFormQuoteEntityList)) {
            return;
        }
        UserDomain user = UserUtils.getUser();
        List<FormQuoteEntity> formQuoteEntityList = new ArrayList<>();
        for (FormQuoteEntity sourceFormQuote : sourceFormQuoteEntityList) {
            FormQuoteEntity formQuoteEntity =
                    JSONObject.parseObject(JSONObject.toJSONString(sourceFormQuote), FormQuoteEntity.class);
            formQuoteEntity.setId(null);
            formQuoteEntity.setApplicationId(applicationId);
            formQuoteEntity.setCreatorName(user.getNickName());
            formQuoteEntity.setModifierName(user.getNickName());
            formQuoteEntityList.add(formQuoteEntity);
        }
        saveBatch(formQuoteEntityList);
    }
}
