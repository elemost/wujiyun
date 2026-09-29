package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.utils.SnowFlakeIdUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractApplicationCategoryConverter;
import com.wuji.service.enums.ApplicationCategoryCategoryShowTypeEnum;
import com.wuji.service.enums.ApplicationCategoryCategoryTypeEnum;
import com.wuji.service.enums.ServiceResultCode;
import com.wuji.service.exception.ServiceException;
import com.wuji.service.mapper.ApplicationCategoryMapper;
import com.wuji.service.model.entity.ApplicationCategoryEntity;
import com.wuji.service.model.entity.FormModelEntity;
import com.wuji.service.model.entity.FormUseTimeEntity;
import com.wuji.service.model.request.ApplicationCategoryCreateRequest;
import com.wuji.service.model.request.ApplicationCategorySortRequest;
import com.wuji.service.model.request.ApplicationCategoryTypeTransRequest;
import com.wuji.service.model.request.ApplicationCategoryUpdateRequest;
import com.wuji.service.model.request.FormCreateRequest;
import com.wuji.service.model.request.FormModelUpdateRequest;
import com.wuji.service.model.request.FormUpdateRequest;
import com.wuji.service.model.vo.ApplicationCategoryStatisticVO;
import com.wuji.service.model.vo.ApplicationCategoryVO;
import com.wuji.service.model.vo.FormModelVO;
import com.wuji.service.model.vo.FormPrivilegeVO;
import com.wuji.service.model.vo.FormQuoteInfoVO;
import com.wuji.service.model.vo.FormQuoteVO;
import com.wuji.service.service.ApplicationCategoryService;
import com.wuji.service.service.FormDataStreamService;
import com.wuji.service.service.FormExtraFunctionService;
import com.wuji.service.service.FormInfoService;
import com.wuji.service.service.FormModelService;
import com.wuji.service.service.FormModuleService;
import com.wuji.service.service.FormPrivilegeService;
import com.wuji.service.service.FormQuoteService;
import com.wuji.service.service.FormService;
import com.wuji.service.service.FormUseTimeService;
import com.wuji.workflow.model.request.ModelRequest;
import com.wuji.workflow.service.ModelManageService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 应用目录 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-08-19
 */
@Service
public class ApplicationCategoryServiceImpl extends ServiceImpl<ApplicationCategoryMapper, ApplicationCategoryEntity>
        implements ApplicationCategoryService {

    @Autowired
    private ApplicationCategoryMapper applicationCategoryMapper;

    @Autowired
    private FormService formService;

    @Autowired
    private FormModelService formModelService;

    @Autowired
    private FormPrivilegeService formPrivilegeService;

    @Autowired
    private FormQuoteService formQuoteService;

    @Autowired
    private FormUseTimeService formUseTimeService;

    @Autowired
    private ModelManageService modelManageService;

    @Autowired
    private FormInfoService formInfoService;

    @Autowired
    private FormDataStreamService formDataStreamService;

    @Autowired
    private FormExtraFunctionService formExtraFunctionServiceImpl;

    @Autowired
    private FormModuleService formModuleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String create(ApplicationCategoryCreateRequest applicationCategoryCreateRequest, Boolean defaultPrivilege) {
        UserDomain user = UserUtils.getUser();
        ApplicationCategoryEntity applicationCategoryEntity =
                AbstractApplicationCategoryConverter.INSTANCE.toEntity(applicationCategoryCreateRequest);
        applicationCategoryEntity.setShowType(ApplicationCategoryCategoryShowTypeEnum.SHOW.name());
        applicationCategoryEntity.setModifier(user.getUserId());
        applicationCategoryEntity.setCreator(user.getUserId());
        applicationCategoryEntity.setCompanyId(user.getCompanyId());
        applicationCategoryEntity.setPublished(Boolean.TRUE);
        applicationCategoryEntity.setId(SnowFlakeIdUtils.generateStr());

        LambdaQueryWrapper<ApplicationCategoryEntity> sortWrapper = new LambdaQueryWrapper<>();
        sortWrapper.eq(ApplicationCategoryEntity::getApplicationId,
                applicationCategoryCreateRequest.getApplicationId());
        sortWrapper.eq(ApplicationCategoryEntity::getParentId, applicationCategoryCreateRequest.getParentId());
        sortWrapper.orderByAsc(ApplicationCategoryEntity::getSortNum);
        sortWrapper.last(" limit 1");
        ApplicationCategoryEntity minSort = applicationCategoryMapper.selectOne(sortWrapper);
        if (minSort != null) {
            applicationCategoryEntity.setSortNum(minSort.getSortNum() - 1);
        }
        if (ApplicationCategoryCategoryTypeEnum.getFormType()
                .contains(applicationCategoryCreateRequest.getCategoryType())) {
            FormCreateRequest formCreateRequest = new FormCreateRequest();
            formCreateRequest.setId(applicationCategoryEntity.getId());
            // formCreateRequest.setTableName(
            //         applicationCategoryEntity.getId() + "_" + applicationCategoryCreateRequest.getApplicationId());
            formCreateRequest.setTableName(applicationCategoryEntity.getApplicationId());
            formCreateRequest.setFormType(applicationCategoryEntity.getCategoryType());
            formCreateRequest.setApplicationId(applicationCategoryCreateRequest.getApplicationId());
            // 创建表单
            formService.createForm(formCreateRequest);
        } else if (ApplicationCategoryCategoryTypeEnum.FILE.name()
                .equals(applicationCategoryCreateRequest.getCategoryType())) {
            applicationCategoryEntity.setPublished(Boolean.TRUE);
        } else if (ApplicationCategoryCategoryTypeEnum.DASH.name()
                .equals(applicationCategoryCreateRequest.getCategoryType()) ||
                ApplicationCategoryCategoryTypeEnum.WEB.name()
                        .equals(applicationCategoryCreateRequest.getCategoryType())) {
            FormCreateRequest formCreateRequest = new FormCreateRequest();
            formCreateRequest.setId(applicationCategoryEntity.getId());
            formCreateRequest.setFormType(applicationCategoryCreateRequest.getCategoryType());
            formCreateRequest.setApplicationId(applicationCategoryCreateRequest.getApplicationId());
            formService.createForm(formCreateRequest);
        } else if (ApplicationCategoryCategoryTypeEnum.VIEW.name()
                .equals(applicationCategoryCreateRequest.getCategoryType())) {
            FormCreateRequest formCreateRequest = new FormCreateRequest();
            formCreateRequest.setId(applicationCategoryEntity.getId());
            formCreateRequest.setSourceId(applicationCategoryCreateRequest.getSourceId());
            formCreateRequest.setFormType(applicationCategoryCreateRequest.getCategoryType());
            formCreateRequest.setApplicationId(applicationCategoryCreateRequest.getApplicationId());
            formService.createForm(formCreateRequest);
        }
        applicationCategoryMapper.insert(applicationCategoryEntity);
        if (defaultPrivilege &&
                !ApplicationCategoryCategoryTypeEnum.FILE.name().equals(applicationCategoryEntity.getCategoryType())) {
            formPrivilegeService.saveDefault(applicationCategoryEntity);
        }
        return applicationCategoryEntity.getId();
    }

    @Override
    public void updateCategory(ApplicationCategoryUpdateRequest applicationCategoryUpdateRequest) {
        LambdaQueryWrapper<ApplicationCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationCategoryEntity::getId, applicationCategoryUpdateRequest.getId());
        queryWrapper.eq(ApplicationCategoryEntity::getApplicationId,
                applicationCategoryUpdateRequest.getApplicationId());
        ApplicationCategoryEntity exist = applicationCategoryMapper.selectOne(queryWrapper);
        ApplicationCategoryEntity applicationCategoryEntity =
                AbstractApplicationCategoryConverter.INSTANCE.toEntity(applicationCategoryUpdateRequest);
        applicationCategoryEntity.setModifier(UserUtils.getUser().getUserId());
        applicationCategoryMapper.update(applicationCategoryEntity, queryWrapper);
        // 流程表单 流程模型名称与表单名称相同
        if (ApplicationCategoryCategoryTypeEnum.FLOWABLE_FORM.name().equals(exist.getCategoryType())) {
            FormModelUpdateRequest formModelUpdateRequest = new FormModelUpdateRequest();
            formModelUpdateRequest.setApplicationId(exist.getApplicationId());
            formModelUpdateRequest.setModelName(applicationCategoryEntity.getCategoryName());
            formModelUpdateRequest.setFormId(exist.getId());
            formModelService.updateModel(formModelUpdateRequest);
        }
        if (StringUtils.isNotEmpty(applicationCategoryUpdateRequest.getConfig())) {
            FormUpdateRequest formUpdateRequest = new FormUpdateRequest();
            formUpdateRequest.setId(applicationCategoryUpdateRequest.getId());
            formUpdateRequest.setConfig(applicationCategoryUpdateRequest.getConfig());
            formUpdateRequest.setSourceId(applicationCategoryUpdateRequest.getSourceId());
            formUpdateRequest.setApplicationId(applicationCategoryUpdateRequest.getApplicationId());
            formService.updateForm(formUpdateRequest);
        }
    }

    @Override
    public void updateShowType(String id, String showType, String applicationId) {
        LambdaQueryWrapper<ApplicationCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationCategoryEntity::getId, id);
        queryWrapper.eq(ApplicationCategoryEntity::getApplicationId, applicationId);
        ApplicationCategoryEntity applicationCategoryEntity = new ApplicationCategoryEntity();
        applicationCategoryEntity.setShowType(showType);
        applicationCategoryEntity.setModifier(UserUtils.getUser().getNickName());
        applicationCategoryMapper.update(applicationCategoryEntity, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(String id, String applicationId) {
        FormQuoteVO info = formQuoteService.getInfo(id, applicationId);
        if (CollectionUtils.isNotEmpty(info.getFormQuoteInfoQuotedList())) {
            throw new ServiceException(ServiceResultCode.QUOTED, ":被" +
                    info.getFormQuoteInfoQuotedList().stream().map(FormQuoteInfoVO::getFormName).distinct()
                            .collect(Collectors.joining("，")) + "引用");
        }
        LambdaQueryWrapper<ApplicationCategoryEntity> parentWrapper = new LambdaQueryWrapper<>();
        parentWrapper.eq(ApplicationCategoryEntity::getParentId, id);
        parentWrapper.eq(ApplicationCategoryEntity::getApplicationId, applicationId);
        parentWrapper.eq(ApplicationCategoryEntity::getDeleted, Boolean.FALSE);
        List<ApplicationCategoryEntity> applicationCategoryEntityList =
                applicationCategoryMapper.selectList(parentWrapper);
        if (CollectionUtils.isNotEmpty(applicationCategoryEntityList)) {
            throw new ServiceException(ServiceResultCode.CURRENT_CATEGORY_DELETED_FAIL);
        }
        LambdaQueryWrapper<ApplicationCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationCategoryEntity::getId, id);
        queryWrapper.eq(ApplicationCategoryEntity::getApplicationId, applicationId);
        ApplicationCategoryEntity applicationCategoryEntity = applicationCategoryMapper.selectOne(queryWrapper);
        applicationCategoryEntity.setModifier(UserUtils.getUser().getNickName());
        applicationCategoryEntity.setDeleted(Boolean.TRUE);
        LambdaQueryWrapper<ApplicationCategoryEntity> update = new LambdaQueryWrapper<>();
        update.eq(ApplicationCategoryEntity::getId, id);
        update.eq(ApplicationCategoryEntity::getApplicationId, applicationId);
        applicationCategoryMapper.update(applicationCategoryEntity, update);
        if (ApplicationCategoryCategoryTypeEnum.getExistFormList()
                .contains(applicationCategoryEntity.getCategoryType()) ||
                ApplicationCategoryCategoryTypeEnum.VIEW.name().equals(applicationCategoryEntity.getCategoryType())) {
            formService.delete(id, applicationId);
            if (CollectionUtils.isNotEmpty(info.getFormQuoteInfoQuoteList())) {
                formQuoteService.delete(id, applicationId, null);
            }
        }
        if (ApplicationCategoryCategoryTypeEnum.getFormType().contains(applicationCategoryEntity.getCategoryType())) {
            LambdaQueryWrapper<ApplicationCategoryEntity> viewWrapper = new LambdaQueryWrapper<>();
            viewWrapper.eq(ApplicationCategoryEntity::getSourceId, id);
            viewWrapper.eq(ApplicationCategoryEntity::getApplicationId, applicationId);
            List<ApplicationCategoryEntity> viewList = applicationCategoryMapper.selectList(viewWrapper);
            for (ApplicationCategoryEntity applicationCategory : viewList) {
                deleteCategory(applicationCategory.getId(), applicationCategory.getApplicationId());
            }
        }
        formUseTimeService.deleteByForm(id, applicationId);
    }

    @Override
    public List<ApplicationCategoryVO> selectTree(List<String> applicationId, Boolean published) {
        if (CollectionUtils.isEmpty(applicationId)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<ApplicationCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ApplicationCategoryEntity::getApplicationId, applicationId);
        queryWrapper.eq(ApplicationCategoryEntity::getDeleted, Boolean.FALSE);
        if (published) {
            queryWrapper.eq(ApplicationCategoryEntity::getPublished, published);
            queryWrapper.eq(ApplicationCategoryEntity::getShowType,
                    ApplicationCategoryCategoryShowTypeEnum.SHOW.name());
        }
        queryWrapper.orderByDesc(ApplicationCategoryEntity::getSortNum);
        queryWrapper.orderByAsc(ApplicationCategoryEntity::getCreateTime);
        List<ApplicationCategoryEntity> applicationCategoryEntityList =
                applicationCategoryMapper.selectList(queryWrapper);
        List<ApplicationCategoryEntity> parentList =
                applicationCategoryEntityList.stream().filter(c -> c.getParentId().equals("0"))
                        .collect(Collectors.toList());
        List<ApplicationCategoryVO> applicationCategoryVOList =
                parentList.stream().map(AbstractApplicationCategoryConverter.INSTANCE::toVO)
                        .collect(Collectors.toList());
        buildChildren(applicationCategoryVOList, applicationCategoryEntityList);
        return applicationCategoryVOList;
    }

    @Override
    public List<ApplicationCategoryVO> quoteList(List<String> applicationId, Boolean published) {
        if (CollectionUtils.isEmpty(applicationId)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<ApplicationCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ApplicationCategoryEntity::getApplicationId, applicationId);
        queryWrapper.eq(ApplicationCategoryEntity::getDeleted, Boolean.FALSE);
        queryWrapper.ne(ApplicationCategoryEntity::getCategoryType, ApplicationCategoryCategoryTypeEnum.WEB.name());
        if (published) {
            queryWrapper.eq(ApplicationCategoryEntity::getPublished, published);
            queryWrapper.eq(ApplicationCategoryEntity::getShowType,
                    ApplicationCategoryCategoryShowTypeEnum.SHOW.name());
        }
        queryWrapper.orderByDesc(ApplicationCategoryEntity::getSortNum);
        queryWrapper.orderByAsc(ApplicationCategoryEntity::getCreateTime);
        List<ApplicationCategoryEntity> applicationCategoryEntityList =
                applicationCategoryMapper.selectList(queryWrapper);
        List<ApplicationCategoryEntity> parentList =
                applicationCategoryEntityList.stream().filter(c -> c.getParentId().equals("0"))
                        .collect(Collectors.toList());
        List<ApplicationCategoryVO> applicationCategoryVOList =
                parentList.stream().map(AbstractApplicationCategoryConverter.INSTANCE::toVO)
                        .collect(Collectors.toList());
        buildChildren(applicationCategoryVOList, applicationCategoryEntityList);
        return applicationCategoryVOList;
    }

    @Override
    public List<ApplicationCategoryVO> selectTreePrivilege(String applicationId, Boolean published) {
        LambdaQueryWrapper<ApplicationCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationCategoryEntity::getApplicationId, applicationId);
        queryWrapper.eq(ApplicationCategoryEntity::getDeleted, Boolean.FALSE);
        // queryWrapper.eq(ApplicationCategoryEntity::getPublished, published);
        queryWrapper.eq(ApplicationCategoryEntity::getShowType, ApplicationCategoryCategoryShowTypeEnum.SHOW.name());
        queryWrapper.orderByDesc(ApplicationCategoryEntity::getSortNum);
        queryWrapper.orderByAsc(ApplicationCategoryEntity::getCreateTime);
        List<String> categoryIdList =
                formPrivilegeService.getExistPrivilegeList(Collections.singletonList(applicationId));
        if (CollectionUtils.isEmpty(categoryIdList)) {
            queryWrapper.eq(ApplicationCategoryEntity::getCategoryType,
                    ApplicationCategoryCategoryTypeEnum.FILE.name());
        } else {
            queryWrapper.and(c -> c.eq(ApplicationCategoryEntity::getCategoryType,
                            ApplicationCategoryCategoryTypeEnum.FILE.name()).or()
                    .in(ApplicationCategoryEntity::getId, categoryIdList));
        }
        List<ApplicationCategoryEntity> applicationCategoryEntityList =
                applicationCategoryMapper.selectList(queryWrapper);
        List<ApplicationCategoryEntity> parentList =
                applicationCategoryEntityList.stream().filter(c -> c.getParentId().equals("0"))
                        .collect(Collectors.toList());
        List<ApplicationCategoryVO> applicationCategoryVOList =
                parentList.stream().map(AbstractApplicationCategoryConverter.INSTANCE::toVO)
                        .collect(Collectors.toList());
        buildChildren(applicationCategoryVOList, applicationCategoryEntityList);
        return applicationCategoryVOList;
    }

    @Override
    public List<ApplicationCategoryVO> selectPrivilege(List<String> applicationIdList, Boolean published) {
        LambdaQueryWrapper<ApplicationCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ApplicationCategoryEntity::getApplicationId, applicationIdList);
        queryWrapper.eq(ApplicationCategoryEntity::getDeleted, Boolean.FALSE);
        queryWrapper.eq(ApplicationCategoryEntity::getPublished, published);
        queryWrapper.eq(ApplicationCategoryEntity::getShowType, ApplicationCategoryCategoryShowTypeEnum.SHOW.name());
        queryWrapper.orderByDesc(ApplicationCategoryEntity::getSortNum);
        queryWrapper.orderByAsc(ApplicationCategoryEntity::getCreateTime);
        if (!UserUtils.getUser().getAdminUser()) {
            List<String> categoryIdList = formPrivilegeService.getExistPrivilegeList(applicationIdList);
            if (CollectionUtils.isEmpty(categoryIdList)) {
                queryWrapper.eq(ApplicationCategoryEntity::getCategoryType,
                        ApplicationCategoryCategoryTypeEnum.FILE.name());
            } else {
                queryWrapper.and(c -> c.eq(ApplicationCategoryEntity::getCategoryType,
                                ApplicationCategoryCategoryTypeEnum.FILE.name()).or()
                        .in(ApplicationCategoryEntity::getId, categoryIdList));
            }
        }
        List<ApplicationCategoryEntity> applicationCategoryEntityList =
                applicationCategoryMapper.selectList(queryWrapper);

        return applicationCategoryEntityList.stream().map(AbstractApplicationCategoryConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationCategoryVO> getCategoryFlowableForm(List<String> applicationIdList, String showType,
                                                               Boolean exitPrivilege) {
        if (CollectionUtils.isEmpty(applicationIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<ApplicationCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ApplicationCategoryEntity::getApplicationId, applicationIdList);
        queryWrapper.eq(ApplicationCategoryEntity::getDeleted, Boolean.FALSE);
        if (StringUtils.isNotEmpty(showType)) {
            queryWrapper.eq(ApplicationCategoryEntity::getShowType, showType);
        }
        queryWrapper.eq(ApplicationCategoryEntity::getCategoryType,
                ApplicationCategoryCategoryTypeEnum.FLOWABLE_FORM.name());
        List<ApplicationCategoryEntity> applicationCategoryEntityList =
                applicationCategoryMapper.selectList(queryWrapper);
        List<FormPrivilegeVO> formPrivilegeVOS = formPrivilegeService.getCreateList(applicationIdList);
        List<String> formApplicationIdList =
                formPrivilegeVOS.stream().map(c -> c.getCategoryId() + "_" + c.getApplicationId())
                        .collect(Collectors.toList());
        List<ApplicationCategoryVO> applicationCategoryVOList = new ArrayList<>();
        for (ApplicationCategoryEntity applicationCategoryEntity : applicationCategoryEntityList) {
            String formApplicationId =
                    applicationCategoryEntity.getId() + "_" + applicationCategoryEntity.getApplicationId();
            if (exitPrivilege && !formApplicationIdList.contains(formApplicationId)) {
                continue;
            }
            ApplicationCategoryVO applicationCategoryVO =
                    AbstractApplicationCategoryConverter.INSTANCE.toVO(applicationCategoryEntity);
            applicationCategoryVOList.add(applicationCategoryVO);
        }
        return applicationCategoryVOList;
    }

    @Override
    public List<ApplicationCategoryVO> selectList(String applicationId) {
        LambdaQueryWrapper<ApplicationCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationCategoryEntity::getApplicationId, applicationId);
        queryWrapper.eq(ApplicationCategoryEntity::getDeleted, Boolean.FALSE);
        queryWrapper.orderByDesc(ApplicationCategoryEntity::getSortNum);
        queryWrapper.orderByAsc(ApplicationCategoryEntity::getCreateTime);
        List<ApplicationCategoryEntity> applicationCategoryEntityList =
                applicationCategoryMapper.selectList(queryWrapper);
        return applicationCategoryEntityList.stream().map(AbstractApplicationCategoryConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationCategoryVO> latestUseForm(Integer limitCount) {
        List<FormUseTimeEntity> formUseTimeEntities = formUseTimeService.getLatestForm(limitCount + 7);
        List<String> formIdList =
                formUseTimeEntities.stream().map(FormUseTimeEntity::getFormId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(formIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<ApplicationCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ApplicationCategoryEntity::getId, formIdList);
        queryWrapper.eq(ApplicationCategoryEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(ApplicationCategoryEntity::getDeleted, Boolean.FALSE);

        List<ApplicationCategoryEntity> applicationCategoryEntityList =
                applicationCategoryMapper.selectList(queryWrapper);
        List<ApplicationCategoryVO> applicationCategoryVOS = new ArrayList<>();
        Map<String, ApplicationCategoryEntity> idToMap = applicationCategoryEntityList.stream()
                .collect(Collectors.toMap(c -> c.getId() + "_" + c.getApplicationId(), c -> c));
        List<String> formApplicationIdList =
                formUseTimeEntities.stream().map(c -> c.getFormId() + "_" + c.getApplicationId())
                        .collect(Collectors.toList());
        for (String formApplicationId : formApplicationIdList) {
            ApplicationCategoryEntity applicationCategoryEntity = idToMap.get(formApplicationId);
            if (applicationCategoryEntity != null) {
                ApplicationCategoryVO applicationCategoryVO =
                        AbstractApplicationCategoryConverter.INSTANCE.toVO(applicationCategoryEntity);
                applicationCategoryVOS.add(applicationCategoryVO);
            }
            if (applicationCategoryVOS.size() >= limitCount) {
                break;
            }
        }
        return applicationCategoryVOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String copy(String formId, String applicationId) {
        LambdaQueryWrapper<ApplicationCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationCategoryEntity::getId, formId);
        queryWrapper.eq(ApplicationCategoryEntity::getApplicationId, applicationId);
        ApplicationCategoryEntity applicationCategoryEntity = applicationCategoryMapper.selectOne(queryWrapper);
        ApplicationCategoryCreateRequest applicationCategoryCreateRequest =
                AbstractApplicationCategoryConverter.INSTANCE.toRequest(applicationCategoryEntity);
        applicationCategoryCreateRequest.setCategoryName(applicationCategoryCreateRequest.getCategoryName() + "_拷贝");
        String newFormId = create(applicationCategoryCreateRequest, false);
        formService.copy(formId, newFormId, applicationId);
        Map<String, String> dataStreamIdMap = new HashMap<>();
        if (ApplicationCategoryCategoryTypeEnum.DASH.name().equals(applicationCategoryEntity.getCategoryType())) {
            formModuleService.copyForm(applicationId, newFormId, formId);
        } else {
            FormModelVO formModelVO = formModelService.info(formId, applicationId);
            if (formModelVO != null) {
                ModelRequest modelRequest = new ModelRequest();
                modelRequest.setKey("form_process_" + newFormId + "_" + applicationId);
                modelRequest.setName(applicationCategoryEntity.getCategoryName() + "流程模型");
                modelRequest.setApplicationId(applicationCategoryEntity.getApplicationId());
                modelRequest.setTenantId(UserUtils.getUser().getCompanyId().toString());
                modelRequest.setModelId(formModelVO.getModelId());
                modelManageService.copyModel(modelRequest, false, Boolean.FALSE);
                FormModelEntity formModelEntity = new FormModelEntity();
                formModelEntity.setModelId(formModelVO.getModelId());
                formModelEntity.setBusinessType(modelRequest.getKey());
                formModelEntity.setFormId(newFormId);
                formModelEntity.setApplicationId(applicationId);
                formModelService.save(formModelEntity);
            }
            formInfoService.copy(formId, newFormId, applicationId);
            dataStreamIdMap = formDataStreamService.copy(applicationId, formId, newFormId);
        }
        Map<String, String> copy = formPrivilegeService.copy(applicationId, formId, newFormId);
        formExtraFunctionServiceImpl.copy(formId, newFormId, applicationId, copy, dataStreamIdMap);
        return newFormId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sortCategory(ApplicationCategorySortRequest applicationCategorySortRequest) {
        LambdaQueryWrapper<ApplicationCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationCategoryEntity::getId, applicationCategorySortRequest.getSortId());
        queryWrapper.eq(ApplicationCategoryEntity::getApplicationId, applicationCategorySortRequest.getApplicationId());
        ApplicationCategoryEntity sortEntity = applicationCategoryMapper.selectOne(queryWrapper);
        if (StringUtils.isEmpty(applicationCategorySortRequest.getPreviousId()) &&
                StringUtils.isNotEmpty(applicationCategorySortRequest.getNextId())) {
            LambdaQueryWrapper<ApplicationCategoryEntity> sortWrapper = new LambdaQueryWrapper<>();
            sortWrapper.eq(ApplicationCategoryEntity::getApplicationId,
                    applicationCategorySortRequest.getApplicationId());
            sortWrapper.eq(ApplicationCategoryEntity::getParentId, applicationCategorySortRequest.getParentId());
            sortWrapper.orderByDesc(ApplicationCategoryEntity::getSortNum);
            sortWrapper.last(" limit 1");
            ApplicationCategoryEntity maxSort = applicationCategoryMapper.selectOne(sortWrapper);
            sortEntity.setSortNum(maxSort.getSortNum() + 1);
            sortEntity.setParentId(applicationCategorySortRequest.getParentId());
            sortEntity.setModifier(UserUtils.getUser().getUserId());
        } else if (StringUtils.isNotEmpty(applicationCategorySortRequest.getPreviousId()) &&
                StringUtils.isEmpty(applicationCategorySortRequest.getNextId())) {
            LambdaQueryWrapper<ApplicationCategoryEntity> sortWrapper = new LambdaQueryWrapper<>();
            sortWrapper.eq(ApplicationCategoryEntity::getApplicationId,
                    applicationCategorySortRequest.getApplicationId());
            sortWrapper.eq(ApplicationCategoryEntity::getParentId, applicationCategorySortRequest.getParentId());
            sortWrapper.orderByDesc(ApplicationCategoryEntity::getSortNum);
            sortWrapper.last(" limit 1");
            ApplicationCategoryEntity minSort = applicationCategoryMapper.selectOne(sortWrapper);
            sortEntity.setSortNum(minSort.getSortNum() - 1);
            sortEntity.setParentId(applicationCategorySortRequest.getParentId());
            sortEntity.setModifier(UserUtils.getUser().getUserId());
        } else if (StringUtils.isEmpty(applicationCategorySortRequest.getPreviousId()) &&
                StringUtils.isEmpty(applicationCategorySortRequest.getNextId())) {
            sortEntity.setParentId(applicationCategorySortRequest.getParentId());
            sortEntity.setModifier(UserUtils.getUser().getUserId());
        } else {
            LambdaQueryWrapper<ApplicationCategoryEntity> previousWrapper = new LambdaQueryWrapper<>();
            previousWrapper.eq(ApplicationCategoryEntity::getId, applicationCategorySortRequest.getPreviousId());
            previousWrapper.eq(ApplicationCategoryEntity::getApplicationId,
                    applicationCategorySortRequest.getApplicationId());
            ApplicationCategoryEntity previousEntity = applicationCategoryMapper.selectOne(previousWrapper);
            sortEntity.setSortNum(previousEntity.getSortNum());
            sortEntity.setModifier(UserUtils.getUser().getUserId());
            sortEntity.setParentId(applicationCategorySortRequest.getParentId());
            applicationCategoryMapper.updateSort(previousEntity.getSortNum(), previousEntity.getCreateTime(),
                    UserUtils.getUser().getUserId(), applicationCategorySortRequest.getParentId());
        }
        applicationCategoryMapper.update(sortEntity, queryWrapper);
    }

    @Override
    public List<ApplicationCategoryVO> getByAppIdList(List<String> appIdList) {
        LambdaQueryWrapper<ApplicationCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationCategoryEntity::getApplicationId, appIdList);
        List<ApplicationCategoryEntity> applicationCategoryEntityList =
                applicationCategoryMapper.selectList(queryWrapper);
        return applicationCategoryEntityList.stream().map(AbstractApplicationCategoryConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public void transToFlowable(ApplicationCategoryTypeTransRequest applicationCategoryTypeTrans) {
        LambdaQueryWrapper<ApplicationCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationCategoryEntity::getApplicationId, applicationCategoryTypeTrans.getApplicationId());
        queryWrapper.eq(ApplicationCategoryEntity::getId, applicationCategoryTypeTrans.getFormId());
        ApplicationCategoryEntity applicationCategoryEntity = applicationCategoryMapper.selectOne(queryWrapper);
        applicationCategoryEntity.setCategoryType(ApplicationCategoryCategoryTypeEnum.FLOWABLE_FORM.name());
        applicationCategoryMapper.update(applicationCategoryEntity, queryWrapper);
        FormUpdateRequest formUpdateRequest = new FormUpdateRequest();
        formUpdateRequest.setApplicationId(applicationCategoryEntity.getApplicationId());
        formUpdateRequest.setId(applicationCategoryEntity.getId());
        formUpdateRequest.setFormType(applicationCategoryEntity.getCategoryType());
        formService.updateForm(formUpdateRequest);
    }

    @Override
    public Long categoryCount(List<String> applicationIdList) {
        if (CollectionUtils.isEmpty(applicationIdList)) {
            return 0L;
        }
        LambdaQueryWrapper<ApplicationCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ApplicationCategoryEntity::getApplicationId, applicationIdList);
        queryWrapper.eq(ApplicationCategoryEntity::getDeleted, Boolean.FALSE);
        queryWrapper.ne(ApplicationCategoryEntity::getCategoryType, ApplicationCategoryCategoryTypeEnum.FILE.name());
        Long count = applicationCategoryMapper.selectCount(queryWrapper);
        return count == null ? 0L : count;
    }

    @Override
    public List<ApplicationCategoryStatisticVO> statisticDetail(List<String> applicationIdList) {
        if (CollectionUtils.isEmpty(applicationIdList)) {
            return new ArrayList<>();
        }
        QueryWrapper<ApplicationCategoryEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", Boolean.FALSE);
        queryWrapper.in("application_id", applicationIdList);
        queryWrapper.ne("category_type", ApplicationCategoryCategoryTypeEnum.FILE.name());
        queryWrapper.groupBy("application_id");
        return applicationCategoryMapper.applicationStatistic(queryWrapper);
    }

    @Override
    public ApplicationCategoryVO info(String id, String applicationId) {
        LambdaQueryWrapper<ApplicationCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationCategoryEntity::getId, id);
        queryWrapper.eq(ApplicationCategoryEntity::getApplicationId, applicationId);
        ApplicationCategoryEntity applicationCategoryEntity = applicationCategoryMapper.selectOne(queryWrapper);
        return AbstractApplicationCategoryConverter.INSTANCE.toVO(applicationCategoryEntity);
    }

    @Override
    public List<ApplicationCategoryVO> getByIdList(List<String> idList, String applicationId) {
        if (CollectionUtils.isEmpty(idList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<ApplicationCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(applicationId), ApplicationCategoryEntity::getApplicationId,
                applicationId);
        queryWrapper.in(ApplicationCategoryEntity::getId, idList);
        List<ApplicationCategoryEntity> applicationCategoryEntityList =
                applicationCategoryMapper.selectList(queryWrapper);
        return applicationCategoryEntityList.stream().map(AbstractApplicationCategoryConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationCategoryVO> getByIdList(List<String> idList, List<String> applicationIds) {
        if (CollectionUtils.isEmpty(idList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<ApplicationCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ApplicationCategoryEntity::getId, idList);
        queryWrapper.in(CollectionUtils.isNotEmpty(applicationIds), ApplicationCategoryEntity::getApplicationId,
                applicationIds);
        List<ApplicationCategoryEntity> applicationCategoryEntityList =
                applicationCategoryMapper.selectList(queryWrapper);
        return applicationCategoryEntityList.stream().map(AbstractApplicationCategoryConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publish(String id, String applicationId) {
        LambdaQueryWrapper<ApplicationCategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationCategoryEntity::getId, id);
        queryWrapper.eq(ApplicationCategoryEntity::getApplicationId, applicationId);
        ApplicationCategoryEntity applicationCategoryEntity = new ApplicationCategoryEntity();
        applicationCategoryEntity.setPublished(Boolean.TRUE);
        applicationCategoryEntity.setModifier(UserUtils.getUser().getNickName());
        applicationCategoryMapper.update(applicationCategoryEntity, queryWrapper);
        formService.publish(id, applicationId);
    }

    private void buildChildren(List<ApplicationCategoryVO> parentList,
                               List<ApplicationCategoryEntity> applicationCategoryEntityList) {
        for (ApplicationCategoryVO applicationCategoryVO : parentList) {
            List<ApplicationCategoryEntity> children = applicationCategoryEntityList.stream()
                    .filter(c -> c.getParentId().equals(applicationCategoryVO.getId()) &&
                            c.getApplicationId().equals(applicationCategoryVO.getApplicationId()))
                    .collect(Collectors.toList());
            List<ApplicationCategoryVO> applicationCategoryVOList =
                    children.stream().map(AbstractApplicationCategoryConverter.INSTANCE::toVO)
                            .collect(Collectors.toList());
            applicationCategoryVO.setChildren(applicationCategoryVOList);
            buildChildren(applicationCategoryVOList, applicationCategoryEntityList);
        }
    }
}