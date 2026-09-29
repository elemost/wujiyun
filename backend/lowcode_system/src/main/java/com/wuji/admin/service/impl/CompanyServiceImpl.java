package com.wuji.admin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.cache.CompanyCache;
import com.wuji.admin.constant.Constants;
import com.wuji.admin.constant.UserConstants;
import com.wuji.admin.converter.AbstractCompanyConverter;
import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.enums.CompanyAppEquityEnum;
import com.wuji.admin.enums.CompanyChannelTypeEnum;
import com.wuji.admin.enums.CompanyDataSourceEnum;
import com.wuji.admin.enums.CompanyInfoKeyEnum;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.mapper.CompanyMapper;
import com.wuji.admin.model.entity.CompanyEntity;
import com.wuji.admin.model.entity.DepartmentEntity;
import com.wuji.admin.model.entity.PostEntity;
import com.wuji.admin.model.request.CompanyInfoSaveRequest;
import com.wuji.admin.model.request.CompanyPullConfigSaveRequest;
import com.wuji.admin.model.request.CompanyRelationSaveRequest;
import com.wuji.admin.model.request.CompanyRequest;
import com.wuji.admin.model.request.CompanySaveRequest;
import com.wuji.admin.model.vo.CompanyInfoVO;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.DictDataVO;
import com.wuji.admin.service.CompanyInfoService;
import com.wuji.admin.service.CompanyPullConfigService;
import com.wuji.admin.service.CompanyRelationService;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.DepartmentService;
import com.wuji.admin.service.DictDataService;
import com.wuji.admin.service.MainCompanyService;
import com.wuji.admin.service.PostService;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.common.enums.DictDataTypeEnum;
import com.wuji.common.model.info.DingTalkConfig;
import com.wuji.common.model.info.LarkConfig;
import com.wuji.common.model.info.WeComConfig;
import com.wuji.common.utils.GuidUtils;
import com.wuji.common.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
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
 * @since 2024-04-15
 */
@Service
@DS("slave")
public class CompanyServiceImpl extends ServiceImpl<CompanyMapper, CompanyEntity> implements CompanyService {

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private UserCompanyService userCompanyService;

    @Autowired
    private CompanyRelationService companyRelationService;

    @Autowired
    private CompanyInfoService companyInfoService;

    @Autowired
    private DictDataService dictDataService;

    @Autowired
    private PostService postService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private MainCompanyService mainCompanyService;

    @Autowired
    private CompanyPullConfigService companyPullConfigService;

    @Override
    public List<CompanyVO> getAllList() {
        final List<CompanyEntity> companyEntityList = companyMapper.selectList(new LambdaQueryWrapper<>());
        List<CompanyVO> returnList = new ArrayList<>();
        for (CompanyEntity companyEntity : companyEntityList) {
            CompanyVO companyVO = AbstractCompanyConverter.INSTANCE.toVO(companyEntity);
            companyVO.setPullConfig(null);
            companyVO.setSecretId(null);
            companyVO.setDataSource(null);
            returnList.add(companyVO);
        }
        return returnList;
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public CompanyVO info(Long companyId) {
        LambdaQueryWrapper<CompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyEntity::getId, companyId);
        CompanyEntity companyEntity = companyMapper.selectOne(queryWrapper);
        CompanyVO companyVO = AbstractCompanyConverter.INSTANCE.toVO(companyEntity);
        if (companyVO == null) {
            return null;
        }
        buildInfo(companyVO);
        return companyVO;
    }

    private void buildInfo(CompanyVO companyVO) {
        List<CompanyInfoVO> companyInfoVOS = companyInfoService.getByCompanyId(companyVO.getCompanyId());
        Map<String, String> configKeyMap = companyInfoVOS.stream()
                .collect(Collectors.toMap(CompanyInfoVO::getConfigKey, CompanyInfoVO::getConfigValue));
        companyVO.setInfoMap(configKeyMap);
        CompanyInfoKeyEnum[] values = CompanyInfoKeyEnum.values();
        Class<? extends CompanyVO> clazz = companyVO.getClass();
        for (CompanyInfoKeyEnum companyInfoKeyEnum : values) {
            if (!companyInfoKeyEnum.getKeyExist()) {
                continue;
            }
            try {
                String configValue = configKeyMap.get(companyInfoKeyEnum.name());
                if (StringUtils.isNotEmpty(configValue)) {
                    String methodName = "set" + companyInfoKeyEnum.getKey().substring(0, 1).toUpperCase() +
                            companyInfoKeyEnum.getKey().substring(1);
                    Method method = clazz.getMethod(methodName, String.class);
                    method.invoke(companyVO, configValue);
                }
            } catch (Exception e) {
                log.error("获取字段值失败", e);
            }
        }
    }

    @Override
    public CompanyVO infoByUuid(String companyUuid) {
        LambdaQueryWrapper<CompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyEntity::getCompanyUuid, companyUuid);
        CompanyEntity companyEntity = companyMapper.selectOne(queryWrapper);
        if (companyEntity == null) {
            return new CompanyVO();
        }
        CompanyVO companyVO = AbstractCompanyConverter.INSTANCE.toVO(companyEntity);
        companyVO.setDataSource(null);
        companyVO.setSecretId(null);
        companyVO.setPullConfig(null);
        buildInfo(companyVO);
        return companyVO;
    }

    @Override
    public CompanyVO getBySecretId(String secretId) {
        LambdaQueryWrapper<CompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyEntity::getSecretId, secretId);
        CompanyEntity companyEntity = companyMapper.selectOne(queryWrapper);
        return AbstractCompanyConverter.INSTANCE.toVO(companyEntity);
    }

    @Override
    public void savePullConfig(CompanyPullConfigSaveRequest companyPullConfigSaveRequest) {
        CompanyEntity companyEntity = AbstractCompanyConverter.INSTANCE.toEntity(companyPullConfigSaveRequest);
        companyEntity.setId(UserUtils.getUser().getCompanyId());
        companyEntity.setPullConfig(companyPullConfigSaveRequest.getPullConfig());
        companyEntity.setDataSource(companyPullConfigSaveRequest.getConfigType());
        if (CompanyDataSourceEnum.DING_TALK.name().equals(companyPullConfigSaveRequest.getConfigType())) {
            DingTalkConfig dingTalkConfig =
                    JSONObject.parseObject(companyPullConfigSaveRequest.getPullConfig(), DingTalkConfig.class);
            companyEntity.setSecretId(dingTalkConfig.getClientId());
        } else if (CompanyDataSourceEnum.LARK.name().equals(companyPullConfigSaveRequest.getConfigType())) {
            LarkConfig larkConfig =
                    JSONObject.parseObject(companyPullConfigSaveRequest.getPullConfig(), LarkConfig.class);
            companyEntity.setSecretId(larkConfig.getClientId());
        } else if (CompanyDataSourceEnum.WECOM.name().equals(companyPullConfigSaveRequest.getConfigType())) {
            WeComConfig weComConfig =
                    JSONObject.parseObject(companyPullConfigSaveRequest.getPullConfig(), WeComConfig.class);
            companyEntity.setSecretId(weComConfig.getCorpId());
        }
        companyMapper.updateById(companyEntity);
    }

    @Override
    public List<CompanyVO> userCompany() {
        List<Long> companyIdList = userCompanyService.getUserCompany(Long.valueOf(UserUtils.getUser().getUserId()));
        if (CollectionUtils.isEmpty(companyIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<CompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CompanyEntity::getId, companyIdList);
        List<CompanyVO> returnList = new ArrayList<>();
        List<CompanyEntity> companyEntities = companyMapper.selectList(queryWrapper);
        for (CompanyEntity companyEntity : companyEntities) {
            CompanyVO companyVO = AbstractCompanyConverter.INSTANCE.toVO(companyEntity);
            companyVO.setPullConfig(null);
            companyVO.setSecretId(null);
            companyVO.setDataSource(null);
            returnList.add(companyVO);
        }
        return returnList;
    }

    @Override
    public void update(CompanySaveRequest companySaveRequest) {
        CompanyEntity companyEntity = AbstractCompanyConverter.INSTANCE.toEntity(companySaveRequest);
        LambdaQueryWrapper<CompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyEntity::getCompanyUuid, companyEntity.getCompanyUuid());
        companyMapper.update(companyEntity, queryWrapper);
        CompanyEntity exist = companyMapper.selectOne(queryWrapper);
        if (StringUtils.isNotEmpty(companySaveRequest.getLogo())) {
            CompanyInfoSaveRequest companyInfoSaveRequest = new CompanyInfoSaveRequest();
            companyInfoSaveRequest.setConfigKey(CompanyInfoKeyEnum.LOGO.name());
            companyInfoSaveRequest.setConfigValue(companySaveRequest.getLogo());
            companyInfoService.save(exist.getId(), companyInfoSaveRequest);
        }
        CompanyCache.clear(exist.getId(), exist.getCompanyUuid());
    }

    @Override
    public Long createCompany(String companyName, String parentUuid, String channelType) {
        Long mainId = mainCompanyService.checkAndSave(companyName);
        LambdaQueryWrapper<CompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyEntity::getCompanyName, companyName);
        queryWrapper.eq(CompanyEntity::getChannelType, channelType);
        queryWrapper.eq(CompanyEntity::getStatus, Constants.NU_DELETED);
        CompanyEntity companyEntity = companyMapper.selectOne(queryWrapper);
        if (companyEntity != null) {
            throw new AdminException(AdminResultCode.COMPANY_EXIST);
        }
        LambdaQueryWrapper<CompanyEntity> parentWrapper = new LambdaQueryWrapper<>();
        parentWrapper.eq(CompanyEntity::getCompanyUuid, parentUuid);
        companyEntity = new CompanyEntity();
        companyEntity.setCompanyName(companyName);
        companyEntity.setStatus((short) 0);
        companyEntity.setCreateBy(UserUtils.getUser().getUserName());
        companyEntity.setCompanyUuid(GuidUtils.getGuid());
        companyEntity.setMainId(mainId);
        companyEntity.setUpdateBy(UserUtils.getUser().getUserName());
        companyMapper.insert(companyEntity);

        CompanyEntity parent = companyMapper.selectOne(parentWrapper);
        if (parent != null) {
            CompanyRelationSaveRequest companyRelationSaveRequest = new CompanyRelationSaveRequest();
            companyRelationSaveRequest.setCompanyId(parent.getId());
            companyRelationSaveRequest.setRelatedCompany(companyEntity.getId());
            companyRelationSaveRequest.setRelatedType("agency");
            companyRelationService.saveRelation(companyRelationSaveRequest);
        }
        initBaseData(companyEntity);
        return companyEntity.getId();
    }

    private void initBaseData(CompanyEntity companyEntity) {
        List<DictDataVO> defaultRoleList = dictDataService.getByType("default_role");
        List<PostEntity> sysPosts = new ArrayList<>();
        for (DictDataVO sysDictData : defaultRoleList) {
            PostEntity sysPost = new PostEntity();
            sysPost.setPostName(sysDictData.getDictLabel());
            sysPost.setPostCode(sysDictData.getDictValue());
            sysPost.setCompanyId(companyEntity.getId());
            sysPost.setStatus("0");
            sysPost.setPostSort(0);
            sysPosts.add(sysPost);
        }
        postService.saveBatch(sysPosts);

        List<DictDataVO> defaultDept = dictDataService.getByType("default_dept");
        List<DepartmentEntity> sysDepts = new ArrayList<>();
        for (DictDataVO sysDictData : defaultDept) {
            DepartmentEntity sysDept = new DepartmentEntity();
            sysDept.setParentId(0L);
            sysDept.setDeptName(sysDictData.getDictLabel());
            sysDept.setStatus("0");
            sysDept.setCompanyId(companyEntity.getId());
            sysDept.setCreateTime(new Date());
            sysDepts.add(sysDept);
        }
        departmentService.saveBatch(sysDepts);
        companyInfoService.saveDefaultKey(companyEntity.getId(), DictDataTypeEnum.ELECLOUD.name().toLowerCase(),
                CompanyAppEquityEnum.EP_COMMUNITY.name().toLowerCase());
    }

    @Override
    public Map<String, CompanyVO> saveBatchCompany(List<String> companyNameList) {
        if (CollectionUtils.isEmpty(companyNameList)) {
            return new HashMap<>();
        }
        List<CompanyInfoSaveRequest> companyInfoSaveRequests = new ArrayList<>();
        List<CompanyEntity> companyEntityList = new ArrayList<>();
        for (String companyName : companyNameList) {
            Long mainId = mainCompanyService.checkAndSave(companyName);
            CompanyEntity companyEntity = new CompanyEntity();
            companyEntity.setCompanyName(companyName);
            companyEntity.setStatus((short) 0);
            companyEntity.setMainId(mainId);
            companyEntity.setCreateBy(UserUtils.getUser().getUserName());
            companyEntity.setCompanyUuid(GuidUtils.getGuid());
            companyEntity.setChannelType(CompanyChannelTypeEnum.SYSTEM.name());
            companyEntity.setUpdateBy(UserUtils.getUser().getUserName());
            companyEntityList.add(companyEntity);
        }
        saveBatch(companyEntityList);
        List<DictDataVO> defaultRoleList = dictDataService.getByType("default_role");
        List<DictDataVO> defaultDept = dictDataService.getByType("default_dept");
        List<DepartmentEntity> sysDepts = new ArrayList<>();
        List<PostEntity> sysPosts = new ArrayList<>();
        for (CompanyEntity companyEntity : companyEntityList) {
            for (DictDataVO sysDictData : defaultRoleList) {
                PostEntity sysPost = new PostEntity();
                sysPost.setPostName(sysDictData.getDictLabel());
                sysPost.setPostCode(sysDictData.getDictValue());
                sysPost.setCompanyId(companyEntity.getId());
                sysPost.setStatus("0");
                sysPost.setPostSort(0);
                sysPosts.add(sysPost);
            }
            for (DictDataVO sysDictData : defaultDept) {
                DepartmentEntity sysDept = new DepartmentEntity();
                sysDept.setParentId(0L);
                sysDept.setDeptName(sysDictData.getDictLabel());
                sysDept.setStatus("0");
                sysDept.setCompanyId(companyEntity.getId());
                sysDept.setCreateTime(new Date());
                sysDepts.add(sysDept);
            }

            // CompanyInfoSaveRequest companyInfoSaveRequest = new CompanyInfoSaveRequest();
            // companyInfoSaveRequest.setCompanyId(companyEntity.getId());
            // companyInfoSaveRequest.setConfigKey(CompanyInfoKeyEnum.userLimit.getKey());
            // companyInfoSaveRequest.setConfigValue("30");
            // companyInfoSaveRequests.add(companyInfoSaveRequest);
        }
        departmentService.saveBatch(sysDepts);
        postService.saveBatch(sysPosts);
        companyInfoService.save(companyInfoSaveRequests,
                Collections.singletonList(CompanyInfoKeyEnum.userLimit.getKey()));
        return companyEntityList.stream().map(AbstractCompanyConverter.INSTANCE::toVO)
                .collect(Collectors.toMap(CompanyVO::getCompanyName, c -> c));
    }

    @Override
    public void createCompany(CompanyRequest companyRequest) {
        LambdaQueryWrapper<CompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyEntity::getSecretId, companyRequest.getSecretId());
        CompanyEntity sameSecretId = companyMapper.selectOne(queryWrapper);
        if (sameSecretId != null) {
            CompanyEntity companyEntity = AbstractCompanyConverter.INSTANCE.toEntity(companyRequest);
            companyEntity.setCompanyName(null);
            companyEntity.setId(sameSecretId.getId());
            companyEntity.setChannelType(null);
            companyMapper.updateById(companyEntity);
            weComAuth(companyRequest, sameSecretId);
            return;
        }
        Long mainId = mainCompanyService.checkAndSave(companyRequest.getCompanyName());
        CompanyEntity companyEntity = AbstractCompanyConverter.INSTANCE.toEntity(companyRequest);
        companyEntity.setStatus((short) 0);
        companyEntity.setMainId(mainId);
        companyEntity.setCompanyUuid(GuidUtils.getGuid());
        companyEntity.setUpdateTime(new Date());
        companyEntity.setCreateTime(new Date());
        companyEntity.setStatus((short) 0);
        companyMapper.insert(companyEntity);
        weComAuth(companyRequest, companyEntity);
        initBaseData(companyEntity);
    }

    private void weComAuth(CompanyRequest companyRequest, CompanyEntity companyEntity) {
        CompanyPullConfigSaveRequest companyPullConfigSaveRequest = new CompanyPullConfigSaveRequest();
        companyPullConfigSaveRequest.setCompanyId(companyEntity.getId());
        companyPullConfigSaveRequest.setPullConfig(companyRequest.getPullConfig());
        companyPullConfigSaveRequest.setAppId(companyRequest.getCorpId());
        companyPullConfigSaveRequest.setConfigType(companyRequest.getDataSource());
        companyPullConfigSaveRequest.setSourceAppId(companyRequest.getSuiteId());
        companyPullConfigService.weComAuth(companyPullConfigSaveRequest);
    }

    @Override
    public void checkCompanyExist(String companyName) {
        LambdaQueryWrapper<CompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyEntity::getCompanyName, companyName);
        CompanyEntity companyEntity = companyMapper.selectOne(queryWrapper);
        if (companyEntity != null) {
            throw new AdminException(AdminResultCode.COMPANY_EXIST);
        }
    }

    @Override
    public List<CompanyVO> getByIds(List<Long> companyIdList) {
        if (CollectionUtils.isEmpty(companyIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<CompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CompanyEntity::getId, companyIdList);
        return companyMapper.selectList(queryWrapper).stream().map(AbstractCompanyConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public CompanyVO getByCompanyName(String companyName, String channelType) {
        LambdaQueryWrapper<CompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyEntity::getCompanyName, companyName);
        queryWrapper.eq(CompanyEntity::getStatus, UserConstants.NORMAL);
        queryWrapper.eq(StringUtils.isNotEmpty(channelType), CompanyEntity::getChannelType, channelType);
        CompanyEntity companyEntity = companyMapper.selectOne(queryWrapper);
        return AbstractCompanyConverter.INSTANCE.toVO(companyEntity);
    }

    @Override
    public List<CompanyVO> getByCompanyNames(List<String> companyNameList, String channelType) {
        if (CollectionUtils.isEmpty(companyNameList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<CompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CompanyEntity::getCompanyName, companyNameList);
        queryWrapper.eq(CompanyEntity::getStatus, UserConstants.NORMAL);
        queryWrapper.eq(StringUtils.isNotEmpty(channelType), CompanyEntity::getChannelType, channelType);
        List<CompanyEntity> companyEntities = companyMapper.selectList(queryWrapper);
        return companyEntities.stream().map(AbstractCompanyConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    @Override
    public String getPullConfig(String companyUuid) {
        LambdaQueryWrapper<CompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyEntity::getCompanyUuid, companyUuid);
        CompanyEntity companyEntity = companyMapper.selectOne(queryWrapper);
        if (companyEntity == null) {
            return null;
        }
        return companyEntity.getPullConfig();
    }

    @Override
    public Long initCompany() {
        Long count = companyMapper.selectCount(new LambdaQueryWrapper<>());
        if (count > 0) {
            return null;
        }
        CompanyEntity companyEntity = new CompanyEntity();
        companyEntity.setCompanyName("默认公司");
        companyEntity.setCompanyUuid(GuidUtils.getGuid());
        companyEntity.setChannelType("SYSTEM");
        companyEntity.setCreateTime(new Date());
        companyEntity.setUpdateTime(new Date());
        companyMapper.insert(companyEntity);
        initBaseData(companyEntity);
        return companyEntity.getId();
    }

}
