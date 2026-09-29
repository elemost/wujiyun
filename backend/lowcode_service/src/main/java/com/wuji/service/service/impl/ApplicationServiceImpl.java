package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.entity.BaseUuidEntity;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.utils.PageUtils;
import com.wuji.common.utils.SnowFlakeIdUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.cache.ApplicationCache;
import com.wuji.service.cache.ManageCache;
import com.wuji.service.converter.AbstractApplicationConverter;
import com.wuji.service.enums.ApplicationCategoryCategoryShowTypeEnum;
import com.wuji.service.enums.ApplicationCategoryCategoryTypeEnum;
import com.wuji.service.enums.ApplicationDataScopeEnum;
import com.wuji.service.enums.ApplicationInfoKeyEnum;
import com.wuji.service.enums.ApplicationNatureEnum;
import com.wuji.service.enums.ApplicationSortTypeEnum;
import com.wuji.service.enums.ApplicationStateEnum;
import com.wuji.service.enums.PrivilegeBusinessType;
import com.wuji.service.mapper.ApplicationMapper;
import com.wuji.service.model.domain.ApplicationDomain;
import com.wuji.service.model.entity.ApplicationEntity;
import com.wuji.service.model.info.ApplicationIcon;
import com.wuji.service.model.request.ApplicationCreateRequest;
import com.wuji.service.model.request.ApplicationOwnerRequest;
import com.wuji.service.model.request.ApplicationQueryRequest;
import com.wuji.service.model.request.ApplicationUpdateRequest;
import com.wuji.service.model.vo.ApplicationCategoryVO;
import com.wuji.service.model.vo.ApplicationCountVO;
import com.wuji.service.model.vo.ApplicationFlowableVO;
import com.wuji.service.model.vo.ApplicationInfoVO;
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.model.vo.CategoryPrivilegeVO;
import com.wuji.service.model.vo.ManageApplicationVO;
import com.wuji.service.model.vo.ManageVO;
import com.wuji.service.service.ApplicationCategoryService;
import com.wuji.service.service.ApplicationInfoService;
import com.wuji.service.service.ApplicationService;
import com.wuji.service.service.ApplicationUseTimeService;
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
 * 应用 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-08-19
 */
@Service
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, ApplicationEntity>
        implements ApplicationService {

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private ApplicationUseTimeService applicationUseTimeService;

    @Autowired
    private ApplicationCategoryService applicationCategoryService;

    @Autowired
    private ApplicationInfoService applicationInfoService;

    @Override
    public String create(ApplicationCreateRequest applicationCreateRequest) {
        UserDomain user = UserUtils.getUser();
        ApplicationEntity applicationEntity = AbstractApplicationConverter.INSTANCE.toEntity(applicationCreateRequest);
        applicationEntity.setCreator(user.getUserId());
        applicationEntity.setModifier(user.getUserId());
        applicationEntity.setId(SnowFlakeIdUtils.generateStr());
        applicationEntity.setState(ApplicationStateEnum.UP.name());
        applicationEntity.setCompanyId(user.getCompanyId());
        applicationMapper.insert(applicationEntity);
        return applicationEntity.getId();
    }

    @Override
    public void update(ApplicationUpdateRequest applicationUpdateRequest) {
        UserDomain user = UserUtils.getUser();
        ApplicationEntity applicationEntity = AbstractApplicationConverter.INSTANCE.toEntity(applicationUpdateRequest);
        applicationEntity.setModifier(user.getUserId());
        applicationMapper.updateById(applicationEntity);
        ApplicationCache.clear(applicationUpdateRequest.getId());
    }

    @Override
    public ApplicationVO detail(String id) {
        ApplicationVO applicationVO = AbstractApplicationConverter.INSTANCE.toVO(getById(id));
        ApplicationInfoVO applicationInfoVO =
                applicationInfoService.getByApplicationAndKey(id, ApplicationInfoKeyEnum.EXPIRE_TIME.name());
        if (applicationInfoVO != null && StringUtils.isNotEmpty(applicationInfoVO.getInfoValue())) {
            applicationVO.setExpireTime(Long.valueOf(applicationInfoVO.getInfoValue()));
        }
        return applicationVO;
    }

    @Override
    public QueryPageVO<ApplicationVO> queryList(ApplicationQueryRequest applicationQueryRequest) {
        QueryWrapper<ApplicationEntity> queryWrapper = new QueryWrapper<>();
        if (ApplicationDataScopeEnum.MY_CREATE.name().equals(applicationQueryRequest.getDataScope())) {
            queryWrapper.eq("creator", UserUtils.getUser().getUserId());
        }
        queryWrapper.eq("company_id", UserUtils.getUser().getCompanyId());
        queryWrapper.eq(StringUtils.isNotEmpty(applicationQueryRequest.getState()), "state",
                applicationQueryRequest.getState());
        queryWrapper.like(StringUtils.isNotEmpty(applicationQueryRequest.getApplicationName()),
                "LOWER(application_name)", applicationQueryRequest.getApplicationName().toLowerCase());
        queryWrapper.eq("deleted", Boolean.FALSE);
        if (ApplicationSortTypeEnum.CREATE_TIME.name().equals(applicationQueryRequest.getSortType())) {
            queryWrapper.orderByDesc("create_time");
        } else if (ApplicationSortTypeEnum.UPDATE_TIME.name().equals(applicationQueryRequest.getSortType())) {
            queryWrapper.orderByDesc("modify_time");
        } else {
            queryWrapper.orderByDesc("create_time");
        }

        Page<ApplicationEntity> applicationEntityPage = applicationMapper.selectPage(
                new Page<>(applicationQueryRequest.getPageNum(), applicationQueryRequest.getPageSize()), queryWrapper);
        List<ApplicationEntity> records = applicationEntityPage.getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return PageUtils.toQueryPage(applicationEntityPage, new ArrayList<>());
        }
        List<ApplicationVO> applicationVOList = buildReturn(records);
        return PageUtils.toQueryPage(applicationEntityPage, applicationVOList);
    }

    private List<ApplicationVO> buildReturn(List<ApplicationEntity> records) {
        List<String> applicationIdList = records.stream().map(BaseUuidEntity::getId).collect(Collectors.toList());
        List<ApplicationInfoVO> applicationInfoVOS = applicationInfoService.getByApplicationIdList(applicationIdList);
        Map<String, List<ApplicationInfoVO>> applicationIdMap =
                applicationInfoVOS.stream().collect(Collectors.groupingBy(ApplicationInfoVO::getApplicationId));
        List<ApplicationVO> applicationVOList = new ArrayList<>();
        for (ApplicationEntity applicationEntity : records) {
            ApplicationVO applicationVO = AbstractApplicationConverter.INSTANCE.toVO(applicationEntity);
            List<ApplicationInfoVO> applicationInfoVOList = applicationIdMap.get(applicationEntity.getId());
            if (CollectionUtils.isNotEmpty(applicationInfoVOList)) {
                ApplicationInfoVO applicationInfoVO = applicationInfoVOList.stream()
                        .filter(a -> ApplicationInfoKeyEnum.EXPIRE_TIME.name().equals(a.getInfoKey())).findFirst()
                        .orElse(null);
                if (applicationInfoVO != null && StringUtils.isNotEmpty(applicationInfoVO.getInfoValue())) {
                    applicationVO.setExpireTime(Long.valueOf(applicationInfoVO.getInfoValue()));
                }
            }
            applicationVOList.add(applicationVO);
        }
        return applicationVOList;
    }

    @Override
    public List<ApplicationVO> getApplicationByIdList(List<String> applicationIdList) {
        if (CollectionUtils.isEmpty(applicationIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<ApplicationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ApplicationEntity::getId, applicationIdList);
        return applicationMapper.selectList(queryWrapper).stream().map(AbstractApplicationConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationVO> getAllNormalApplication() {
        LambdaQueryWrapper<ApplicationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationEntity::getDeleted, Boolean.FALSE);
        queryWrapper.eq(ApplicationEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(ApplicationEntity::getApplicationNature, ApplicationNatureEnum.NORMAL.name());
        return applicationMapper.selectList(queryWrapper).stream().map(AbstractApplicationConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationVO> getAllList() {
        LambdaQueryWrapper<ApplicationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationEntity::getDeleted, Boolean.FALSE);
        queryWrapper.eq(ApplicationEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.orderByDesc(ApplicationEntity::getCreateTime);
        return applicationMapper.selectList(queryWrapper).stream().map(AbstractApplicationConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationVO> latestUseApplication(Integer limitCount) {
        List<String> latestApplicationList = applicationUseTimeService.getLatestApplication(limitCount);
        if (CollectionUtils.isEmpty(latestApplicationList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<ApplicationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ApplicationEntity::getId, latestApplicationList);
        queryWrapper.eq(ApplicationEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(ApplicationEntity::getDeleted, Boolean.FALSE);
        List<ApplicationEntity> applicationEntities = applicationMapper.selectList(queryWrapper);
        List<ApplicationVO> applicationVOList = new ArrayList<>();
        for (ApplicationEntity applicationEntity : applicationEntities) {
            ApplicationVO applicationVO = AbstractApplicationConverter.INSTANCE.toVO(applicationEntity);
            applicationVOList.add(applicationVO);
        }
        return applicationVOList;
    }

    @Override
    public List<ApplicationVO> getMyApplication(ApplicationOwnerRequest applicationOwnerRequest) {
        LambdaQueryWrapper<ApplicationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationEntity::getCreator, UserUtils.getUser().getUserId());
        queryWrapper.eq(ApplicationEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(ApplicationEntity::getDeleted, Boolean.FALSE);
        queryWrapper.like(StringUtils.isNotEmpty(applicationOwnerRequest.getApplicationName()),
                ApplicationEntity::getApplicationName, applicationOwnerRequest.getApplicationName());
        queryWrapper.orderByDesc(ApplicationEntity::getCreateTime);
        List<ApplicationEntity> applicationEntities = applicationMapper.selectList(queryWrapper);
        return buildReturn(applicationEntities);
    }

    @Override
    public void delete(String id) {
        ApplicationEntity applicationEntity = new ApplicationEntity();
        applicationEntity.setId(id);
        applicationEntity.setDeleted(Boolean.TRUE);
        applicationEntity.setModifier(UserUtils.getUser().getUserId());
        applicationMapper.updateById(applicationEntity);
        applicationUseTimeService.deleteByApplication(id);
    }

    @Override
    public void updateState(String id, String state) {
        ApplicationEntity applicationEntity = new ApplicationEntity();
        applicationEntity.setId(id);
        applicationEntity.setState(state);
        applicationEntity.setModifier(UserUtils.getUser().getUserId());
        applicationMapper.updateById(applicationEntity);
    }

    @Override
    public List<ApplicationFlowableVO> getAllApplicationFlowable() {
        UserDomain user = UserUtils.getUser();
        QueryWrapper<ApplicationDomain> queryWrapper = new QueryWrapper<>();
        buildUserFilter(user, queryWrapper);
        queryWrapper.eq("a.company_id", user.getCompanyId());
        queryWrapper.orderByDesc("a.create_time");
        queryWrapper.eq("a.deleted", Boolean.FALSE);
        queryWrapper.groupBy("a.id");
        List<ApplicationDomain> applicationEntities = applicationMapper.selectLists(queryWrapper);
        List<String> applicationIdList =
                applicationEntities.stream().map(ApplicationDomain::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(applicationIdList)) {
            return new ArrayList<>();
        }
        List<ApplicationCategoryVO> applicationCategoryList =
                applicationCategoryService.getCategoryFlowableForm(applicationIdList,
                        ApplicationCategoryCategoryShowTypeEnum.SHOW.name(), true);
        Map<String, List<ApplicationCategoryVO>> applicationIdToListMap = applicationCategoryList.stream()
                .collect(Collectors.groupingBy(ApplicationCategoryVO::getApplicationId));
        List<ApplicationFlowableVO> applicationFlowableVOList = new ArrayList<>();
        for (ApplicationDomain applicationEntity : applicationEntities) {
            ApplicationFlowableVO applicationFlowableVO =
                    AbstractApplicationConverter.INSTANCE.toFlowableVO(applicationEntity);
            applicationFlowableVO.setApplicationCategoryList(applicationIdToListMap.get(applicationEntity.getId()));
            if (CollectionUtils.isNotEmpty(applicationFlowableVO.getApplicationCategoryList())) {
                applicationFlowableVOList.add(applicationFlowableVO);
            }
        }
        return applicationFlowableVOList;
    }

    @Override
    public List<ApplicationFlowableVO> getAllApplicationForm() {
        LambdaQueryWrapper<ApplicationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(ApplicationEntity::getDeleted, Boolean.FALSE);
        List<ApplicationEntity> applicationEntityList = applicationMapper.selectList(queryWrapper);
        List<String> applicationIdList =
                applicationEntityList.stream().map(BaseUuidEntity::getId).collect(Collectors.toList());
        List<ApplicationCategoryVO> applicationCategoryVOList =
                applicationCategoryService.selectTree(applicationIdList, Boolean.FALSE);
        Map<String, List<ApplicationCategoryVO>> applicationIdMap = applicationCategoryVOList.stream()
                .collect(Collectors.groupingBy(ApplicationCategoryVO::getApplicationId));
        List<ApplicationFlowableVO> applicationFlowableList = new ArrayList<>();
        for (ApplicationEntity applicationEntity : applicationEntityList) {
            ApplicationFlowableVO applicationFlowableVO =
                    AbstractApplicationConverter.INSTANCE.toFlowableVO(applicationEntity);
            applicationFlowableVO.setApplicationCategoryList(applicationIdMap.get(applicationEntity.getId()));
            applicationFlowableList.add(applicationFlowableVO);
        }
        return applicationFlowableList;
    }

    @Override
    public List<CategoryPrivilegeVO> getMyPrivilege() {
        ApplicationQueryRequest applicationQueryRequest = new ApplicationQueryRequest();
        applicationQueryRequest.setPageSize(100);
        QueryPageVO<ApplicationVO> myApplication = getApplicationListPrivilege(applicationQueryRequest);
        List<String> applicationIdList =
                myApplication.getList().stream().map(ApplicationVO::getId).collect(Collectors.toList());
        List<ApplicationCategoryVO> applicationCategoryVOList =
                applicationCategoryService.selectPrivilege(applicationIdList, Boolean.TRUE);
        List<CategoryPrivilegeVO> categoryPrivilegeVOS = new ArrayList<>();
        for (ApplicationVO applicationVO : myApplication.getList()) {
            CategoryPrivilegeVO categoryPrivilegeVO = new CategoryPrivilegeVO();
            categoryPrivilegeVO.setApplicationId(applicationVO.getId());
            categoryPrivilegeVO.setName(applicationVO.getApplicationName());
            categoryPrivilegeVO.setType("APPLICATION");
            categoryPrivilegeVO.setIcon(applicationVO.getIcon());
            categoryPrivilegeVOS.add(categoryPrivilegeVO);
        }
        for (ApplicationCategoryVO categoryVO : applicationCategoryVOList) {
            CategoryPrivilegeVO categoryPrivilegeVO = new CategoryPrivilegeVO();
            categoryPrivilegeVO.setApplicationId(categoryVO.getApplicationId());
            categoryPrivilegeVO.setFormId(categoryVO.getId());
            categoryPrivilegeVO.setName(categoryVO.getCategoryName());
            categoryPrivilegeVO.setType(categoryVO.getCategoryType());
            categoryPrivilegeVOS.add(categoryPrivilegeVO);
        }
        return categoryPrivilegeVOS.stream()
                .filter(c -> !ApplicationCategoryCategoryTypeEnum.FILE.name().equals(c.getType()))
                .collect(Collectors.toList());
    }

    @Override
    public QueryPageVO<ApplicationVO> getApplicationListPrivilege(ApplicationQueryRequest applicationQueryRequest) {
        UserDomain user = UserUtils.getUser();
        QueryWrapper<ApplicationDomain> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(applicationQueryRequest.getApplicationName())) {
            queryWrapper.like("LOWER(a.application_name)", applicationQueryRequest.getApplicationName().toLowerCase());
        }
        if (!UserUtils.getUser().getAdminUser()) {
            buildUserFilter(user, queryWrapper);
        }
        queryWrapper.eq("a.company_id", UserUtils.getUser().getCompanyId());
        queryWrapper.eq("a.deleted", Boolean.FALSE);
        queryWrapper.orderByDesc("a.create_time");
        queryWrapper.groupBy("a.id");
        IPage<ApplicationDomain> applicationDomainIPage = applicationMapper.selectPages(
                new Page<>(applicationQueryRequest.getPageNum(), applicationQueryRequest.getPageSize()), queryWrapper);
        List<ApplicationDomain> applicationDomainList = applicationDomainIPage.getRecords();
        if (CollectionUtils.isEmpty(applicationDomainList)) {
            return PageUtils.toQueryPage(applicationQueryRequest, (int) applicationDomainIPage.getTotal(),
                    new ArrayList<>());
        }
        List<ApplicationVO> applicationVOList = new ArrayList<>();
        ManageVO config = ManageCache.getConfig(UserUtils.getUser().getCompanyId(), user.getUserIdLongValue());
        List<String> privilegeApplicationList = new ArrayList<>();
        boolean canDelete = false;
        if (config != null) {
            if (CollectionUtils.isNotEmpty(config.getManageApplicationList())) {
                privilegeApplicationList =
                        config.getManageApplicationList().stream().map(ManageApplicationVO::getApplicationId)
                                .collect(Collectors.toList());
            }
            if (config.getSuperManage()) {
                canDelete = true;
            } else {
                canDelete = config.getAppUpdate();
            }
        }
        for (ApplicationDomain applicationDomain : applicationDomainList) {
            ApplicationVO applicationVO = AbstractApplicationConverter.INSTANCE.toVO(applicationDomain);
            applicationVOList.add(applicationVO);
            List<String> buttonList = applicationVO.getButtonList();
            if (config != null && config.getSuperManage()) {
                buttonList.add("DELETE");
                buttonList.add("UPDATE");
                continue;
            }
            if (user.getUserId().equals(applicationVO.getCreator())) {
                buttonList.add("DELETE");
                buttonList.add("UPDATE");
                continue;
            }
            if (privilegeApplicationList.contains(applicationVO.getId())) {
                buttonList.add("UPDATE");
                if (canDelete) {
                    buttonList.add("DELETE");
                }
            }
        }
        return PageUtils.toQueryPage(applicationDomainIPage, applicationVOList);
    }

    @Override
    public List<ApplicationVO> getLatestApplicationPrivilege(Integer limitCount) {
        UserDomain user = UserUtils.getUser();
        QueryWrapper<ApplicationDomain> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("aut.use_time");
        queryWrapper.eq("a.company_id", user.getCompanyId());
        queryWrapper.eq("aut.user_id", user.getUserId());
        queryWrapper.last(" limit " + limitCount);
        queryWrapper.groupBy("a.id");
        buildUserFilter(user, queryWrapper);
        List<ApplicationDomain> latestApplication = applicationMapper.getLatestApplicationPrivilege(queryWrapper);
        return latestApplication.stream().map(AbstractApplicationConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    @Override
    public Long useTimes(String templateApplicationId) {
        LambdaQueryWrapper<ApplicationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationEntity::getTemplateId, templateApplicationId);
        queryWrapper.eq(ApplicationEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        return applicationMapper.selectCount(queryWrapper);
    }

    @Override
    public void dealIcon() {
        List<ApplicationEntity> applicationEntities = applicationMapper.selectList(new LambdaQueryWrapper<>());
        List<ApplicationEntity> updateList = new ArrayList<>();
        for (ApplicationEntity applicationEntity : applicationEntities) {
            if (StringUtils.isEmpty(applicationEntity.getIcon())) {
                continue;
            }
            ApplicationIcon applicationIcon = new ApplicationIcon();
            applicationIcon.setUrl(applicationEntity.getIcon());
            applicationEntity.setIcon(JSONObject.toJSONString(applicationIcon));
            updateList.add(applicationEntity);
        }
        if (CollectionUtils.isNotEmpty(updateList)) {
            updateBatchById(updateList);
        }

    }

    @Override
    public List<ApplicationCountVO> applicationCount(List<Long> companyIdList) {
        if (CollectionUtils.isEmpty(companyIdList)) {
            return new ArrayList<>();
        }
        QueryWrapper<ApplicationCountVO> queryWrapper = new QueryWrapper<>();
        queryWrapper.groupBy("company_id");
        queryWrapper.eq("deleted", Boolean.FALSE);
        queryWrapper.in("company_id", companyIdList);
        return applicationMapper.applicationCount(queryWrapper);
    }

    @Override
    public ApplicationVO getByTemplateId(String templateId) {
        LambdaQueryWrapper<ApplicationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationEntity::getTemplateId, templateId);
        queryWrapper.eq(ApplicationEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(ApplicationEntity::getDeleted, Boolean.FALSE);
        queryWrapper.last(" limit 1 ");
        ApplicationEntity applicationEntity = applicationMapper.selectOne(queryWrapper);
        return AbstractApplicationConverter.INSTANCE.toVO(applicationEntity);
    }

    private static void buildUserFilter(UserDomain user, QueryWrapper<ApplicationDomain> queryWrapper) {
        ManageVO config = ManageCache.getConfig(user.getCompanyId(), user.getUserIdLongValue());
        List<String> applicationIdList = new ArrayList<>();
        if (config != null) {
            if (config.getSuperManage()) {
                return;
            }
            List<ManageApplicationVO> manageApplicationList = config.getManageApplicationList();
            if (CollectionUtils.isNotEmpty(manageApplicationList)) {
                applicationIdList = manageApplicationList.stream().map(ManageApplicationVO::getApplicationId)
                        .collect(Collectors.toList());
            }
        } else {
            applicationIdList = new ArrayList<>();
        }
        List<String> finalApplicationIdList = applicationIdList;
        queryWrapper.and(c -> c.and(d -> d.eq("ap.business_id", user.getUserId())
                        .eq("ap.business_type", PrivilegeBusinessType.USER.getType()))
                .or(CollectionUtils.isNotEmpty(user.getDataScopeDeptIdList()),
                        e -> e.in("ap.business_id", user.getDataScopeDeptIdList())
                                .eq("ap.business_type", PrivilegeBusinessType.DEPT.getType()))
                .or(CollectionUtils.isNotEmpty(user.getPostIdList()), d -> d.in("ap.business_id", user.getPostIdList())
                        .eq("ap.business_type", PrivilegeBusinessType.POST.getType()))
                .or(d -> d.eq("a.creator", user.getUserId()))
                .or(CollectionUtils.isNotEmpty(finalApplicationIdList), d -> d.in("a.id", finalApplicationIdList)));
    }
}
