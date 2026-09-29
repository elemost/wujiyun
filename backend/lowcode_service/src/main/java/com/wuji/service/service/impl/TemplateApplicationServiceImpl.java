package com.wuji.service.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.cache.ConfigCache;
import com.wuji.common.enums.ConfigEnum;
import com.wuji.common.enums.SuiteApplicationEnum;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.vo.QueryPageVO;
import com.wuji.common.trans.MultiTransactional;
import com.wuji.common.utils.PageUtils;
import com.wuji.common.utils.SnowFlakeIdUtils;
import com.wuji.common.utils.TimeUtils;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractApplicationConverter;
import com.wuji.service.converter.AbstractTemplateApplicationConverter;
import com.wuji.service.enums.ApplicationInfoKeyEnum;
import com.wuji.service.enums.ApplicationNatureEnum;
import com.wuji.service.enums.ApplicationStateEnum;
import com.wuji.service.enums.TemplateApplicationTagTypeEnum;
import com.wuji.service.mapper.TemplateApplicationMapper;
import com.wuji.service.model.domain.TemplateApplicationDomain;
import com.wuji.service.model.entity.ApplicationEntity;
import com.wuji.service.model.entity.TemplateApplicationEntity;
import com.wuji.service.model.info.ApplicationIcon;
import com.wuji.service.model.request.TemplateApplicationRequest;
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.model.vo.TemplateApplicationImgVO;
import com.wuji.service.model.vo.TemplateApplicationTagVO;
import com.wuji.service.model.vo.TemplateApplicationVO;
import com.wuji.service.service.ApplicationInfoService;
import com.wuji.service.service.ApplicationPrivilegeService;
import com.wuji.service.service.ApplicationService;
import com.wuji.service.service.TemplateApplicationCategoryService;
import com.wuji.service.service.TemplateApplicationImgService;
import com.wuji.service.service.TemplateApplicationService;
import com.wuji.service.service.TemplateApplicationTagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 应用 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-09-18
 */
@Service
@Slf4j
public class TemplateApplicationServiceImpl extends ServiceImpl<TemplateApplicationMapper, TemplateApplicationEntity>
        implements TemplateApplicationService {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private TemplateApplicationMapper templateApplicationMapper;

    @Autowired
    private TemplateApplicationCategoryService templateApplicationCategoryService;

    @Autowired
    private ApplicationPrivilegeService applicationPrivilegeService;

    @Autowired
    private TemplateApplicationImgService templateApplicationImgService;

    @Autowired
    private TemplateApplicationTagService templateApplicationTagService;

    @Autowired
    private ApplicationInfoService applicationInfoService;


    @Override
    @MultiTransactional(value = {"mybatisTransactionManager"})
    public String generateTemplate(String applicationId) {
        UserDomain user = UserUtils.getUser() == null ? new UserDomain() : UserUtils.getUser();
        String templateApplicationId = SnowFlakeIdUtils.generateStr();
        ApplicationVO applicationVO = applicationService.detail(applicationId);
        LambdaQueryWrapper<TemplateApplicationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TemplateApplicationEntity::getSourceApplicationId, applicationId);
        queryWrapper.eq(TemplateApplicationEntity::getDeleted, Boolean.FALSE);
        queryWrapper.last(" limit 1");
        queryWrapper.orderByDesc(TemplateApplicationEntity::getCreateTime);
        TemplateApplicationEntity templateApplicationEntity = templateApplicationMapper.selectOne(queryWrapper);
        Boolean exist = templateApplicationEntity != null;
        if (templateApplicationEntity == null) {
            templateApplicationEntity = AbstractTemplateApplicationConverter.INSTANCE.toEntity(applicationVO);
            templateApplicationEntity.setId(templateApplicationId);
            templateApplicationEntity.setSourceApplicationId(applicationId);
            templateApplicationEntity.setCreator(user.getUserId());
            templateApplicationEntity.setModifier(user.getUserId());
            save(templateApplicationEntity);
        } else {
            templateApplicationEntity.setApplicationName(applicationVO.getApplicationName());
            templateApplicationEntity.setIcon(applicationVO.getIcon());
            templateApplicationMapper.updateById(templateApplicationEntity);
        }
        templateApplicationCategoryService.generateTemplate(applicationId, templateApplicationEntity.getId(), exist);
        log.info("模板生成完成" + applicationVO.getApplicationName());
        return templateApplicationEntity.getId();
    }

    @Override
    public String generateTemplateByTemplateId(String templateId) {
        UserDomain user = UserUtils.getUser() == null ? new UserDomain() : UserUtils.getUser();
        LambdaQueryWrapper<TemplateApplicationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TemplateApplicationEntity::getId, templateId);
        queryWrapper.eq(TemplateApplicationEntity::getDeleted, Boolean.FALSE);
        queryWrapper.last(" limit 1");
        queryWrapper.orderByDesc(TemplateApplicationEntity::getCreateTime);
        TemplateApplicationEntity templateApplicationEntity = templateApplicationMapper.selectOne(queryWrapper);
        templateApplicationCategoryService.generateTemplate(templateApplicationEntity.getSourceApplicationId(),
                templateApplicationEntity.getId(), Boolean.TRUE);
        ApplicationVO applicationVO = applicationService.detail(templateApplicationEntity.getSourceApplicationId());
        log.info("模板生成完成" + applicationVO.getApplicationName());
        return templateApplicationEntity.getId();
    }

    @Override
    @MultiTransactional(value = {"mybatisTransactionManager"})
    public String useTemplate(String templateApplicationId, Boolean needData) {
        UserDomain user = UserUtils.getUser() == null ? new UserDomain() : UserUtils.getUser();
        String applicationId = SnowFlakeIdUtils.generateStr();
        TemplateApplicationEntity templateApplicationEntity =
                templateApplicationMapper.selectById(templateApplicationId);
        ApplicationEntity applicationEntity = AbstractApplicationConverter.INSTANCE.toEntity(templateApplicationEntity);
        applicationEntity.setId(applicationId);
        Long useTimes = applicationService.useTimes(templateApplicationId);
        if (useTimes != null && useTimes != 0) {
            useTimes = useTimes + 1;
            applicationEntity.setApplicationName(applicationEntity.getApplicationName() + "(" + useTimes + ")");
        }
        applicationEntity.setCompanyId(user.getCompanyId());
        applicationEntity.setCreator(user.getUserId());
        applicationEntity.setModifier(user.getUserId());
        applicationEntity.setCreateTime(new Date());
        applicationEntity.setTemplateId(templateApplicationEntity.getId());
        applicationEntity.setModifyTime(new Date());
        applicationService.save(applicationEntity);
        templateApplicationCategoryService.useTemplate(applicationId, templateApplicationId,
                templateApplicationEntity.getSourceApplicationId(), needData);
        applicationPrivilegeService.saveDefaultPrivilege(applicationId);
        templateApplicationEntity.setDownloadCount(templateApplicationEntity.getDownloadCount() + 1);
        templateApplicationMapper.updateById(templateApplicationEntity);
        String suiteId = SuiteApplicationEnum.getSuiteIdByAppId(templateApplicationId);
        if (StringUtils.isNotEmpty(suiteId)) {
            addApplicationInfo(applicationId);
            applicationEntity.setApplicationNature(ApplicationNatureEnum.CHARGE.name());
        }
        return applicationId;
    }

    private void addApplicationInfo(String applicationId) {
        int day = Integer.parseInt(
                Objects.requireNonNull(ConfigCache.getValue(ConfigEnum.APPLICATION_EXPIRE_DAY.name())));
        long time = TimeUtils.getDataZero(new Date(), day).getTime();
        // 免费试用用户 和 免费用户 模版应用30天试用
        applicationInfoService.insert(applicationId, ApplicationInfoKeyEnum.EXPIRE_TIME.name(), Long.toString(time));
    }

    @Override
    public QueryPageVO<TemplateApplicationVO> queryList(TemplateApplicationRequest templateApplicationRequest) {
        QueryWrapper<TemplateApplicationDomain> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotEmpty(templateApplicationRequest.getApplicationName())) {
            queryWrapper.like(StringUtils.isNotEmpty(templateApplicationRequest.getApplicationName()),
                    "LOWER(ta.application_name)", templateApplicationRequest.getApplicationName().toLowerCase());
        }
        if (StringUtils.isNotBlank(templateApplicationRequest.getIndustry())) {
            queryWrapper.and(c -> c.eq("tat.tag_type", TemplateApplicationTagTypeEnum.INDUSTRY.name())
                    .like("tat.tag_value", templateApplicationRequest.getIndustry()));
        }
        if (StringUtils.isNotBlank(templateApplicationRequest.getScene())) {
            queryWrapper.and(c -> c.eq("tat.tag_type", TemplateApplicationTagTypeEnum.SCENE.name())
                    .like("tat.tag_value", templateApplicationRequest.getScene()));
        }
        if (StringUtils.isNotBlank(templateApplicationRequest.getTag())) {
            queryWrapper.and(c -> c.eq("tat.tag_type", TemplateApplicationTagTypeEnum.HOT_TAG.name())
                    .like("tat.tag_value", templateApplicationRequest.getTag()));
        }
        queryWrapper.eq("ta.application_nature", "NORMAL");
        queryWrapper.eq("ta.state", ApplicationStateEnum.UP.name());
        queryWrapper.groupBy("ta.id");
        if ("downloadCount".equals(templateApplicationRequest.getSortType())) {
            queryWrapper.orderByDesc("ta.recommend", "ta.download_count");
        } else {
            queryWrapper.orderByAsc("ta.create_time");
        }
        IPage<TemplateApplicationDomain> templateApplicationDomainIPage = templateApplicationMapper.selectPages(
                new Page<>(templateApplicationRequest.getPageNum(), templateApplicationRequest.getPageSize()),
                queryWrapper);
        List<TemplateApplicationDomain> records = templateApplicationDomainIPage.getRecords();
        List<String> applictionIdList =
                records.stream().map(TemplateApplicationDomain::getId).collect(Collectors.toList());
        List<TemplateApplicationTagVO> templateApplicationTagVOList =
                templateApplicationTagService.getByApplicationIdList(applictionIdList);
        Map<String, List<TemplateApplicationTagVO>> applicationIdMap = templateApplicationTagVOList.stream()
                .collect(Collectors.groupingBy(TemplateApplicationTagVO::getApplicationId));
        List<TemplateApplicationVO> templateApplicationVOList = new ArrayList<>();
        for (TemplateApplicationDomain templateApplicationDomain : records) {
            TemplateApplicationVO templateApplicationVO =
                    AbstractTemplateApplicationConverter.INSTANCE.toVO(templateApplicationDomain);
            List<TemplateApplicationTagVO> templateApplicationTagVOS =
                    applicationIdMap.get(templateApplicationDomain.getId());
            if (CollectionUtils.isNotEmpty(templateApplicationTagVOS)) {
                Map<String, List<TemplateApplicationTagVO>> tagTypeMap = templateApplicationTagVOS.stream()
                        .collect(Collectors.groupingBy(TemplateApplicationTagVO::getTagType));
                templateApplicationVO.setTags(tagTypeMap.get(TemplateApplicationTagTypeEnum.TAG.name()));
                templateApplicationVO.setHotTags(tagTypeMap.get(TemplateApplicationTagTypeEnum.HOT_TAG.name()));
            }
            templateApplicationVOList.add(templateApplicationVO);
        }
        return PageUtils.toQueryPage(templateApplicationDomainIPage, templateApplicationVOList);
    }

    @Override
    public TemplateApplicationVO info(String applicationId) {
        TemplateApplicationEntity templateApplicationEntity = templateApplicationMapper.selectById(applicationId);
        TemplateApplicationVO templateApplicationVO =
                AbstractTemplateApplicationConverter.INSTANCE.toVO(templateApplicationEntity);
        List<TemplateApplicationImgVO> templateApplicationImgVOS =
                templateApplicationImgService.listById(applicationId);
        templateApplicationVO.setImgList(templateApplicationImgVOS);
        List<TemplateApplicationTagVO> templateApplicationTagVOList =
                templateApplicationTagService.getByApplicationIdList(Collections.singletonList(applicationId));
        Map<String, List<TemplateApplicationTagVO>> tagTypeMap = templateApplicationTagVOList.stream()
                .collect(Collectors.groupingBy(TemplateApplicationTagVO::getTagType));
        templateApplicationVO.setTags(tagTypeMap.get(TemplateApplicationTagTypeEnum.TAG.name()));
        templateApplicationVO.setHotTags(tagTypeMap.get(TemplateApplicationTagTypeEnum.HOT_TAG.name()));

        return templateApplicationVO;
    }

    @Override
    public void dealIcon() {
        List<TemplateApplicationEntity> templateApplicationEntityList =
                templateApplicationMapper.selectList(new LambdaQueryWrapper<>());
        List<TemplateApplicationEntity> updateList = new ArrayList<>();
        for (TemplateApplicationEntity applicationEntity : templateApplicationEntityList) {
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
}
