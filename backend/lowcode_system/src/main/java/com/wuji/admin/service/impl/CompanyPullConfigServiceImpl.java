package com.wuji.admin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.cache.CompanyPullConfigCache;
import com.wuji.admin.converter.AbstractCompanyPullConfigConverter;
import com.wuji.admin.enums.CompanyDataSourceEnum;
import com.wuji.admin.mapper.CompanyPullConfigMapper;
import com.wuji.admin.model.entity.CompanyPullConfigEntity;
import com.wuji.admin.model.request.CompanyPullConfigSaveRequest;
import com.wuji.admin.service.CompanyPullConfigService;
import com.wuji.admin.service.CompanyService;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.model.info.DingTalkConfig;
import com.wuji.common.model.info.LarkConfig;
import com.wuji.common.model.vo.CompanyPullConfigVO;
import com.wuji.common.utils.ObjectId;
import com.wuji.common.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-11-29
 */
@Service
@DS("slave")
public class CompanyPullConfigServiceImpl extends ServiceImpl<CompanyPullConfigMapper, CompanyPullConfigEntity>
        implements CompanyPullConfigService {

    @Autowired
    private CompanyPullConfigMapper companyPullConfigMapper;

    @Autowired
    private CompanyService companyService;

    @Override
    public void save(CompanyPullConfigSaveRequest companyPullConfigSaveRequest) {
        LambdaQueryWrapper<CompanyPullConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyPullConfigEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(CompanyPullConfigEntity::getConfigType, companyPullConfigSaveRequest.getConfigType());
        CompanyPullConfigEntity companyPullConfigEntity = companyPullConfigMapper.selectOne(queryWrapper);
        if (companyPullConfigEntity == null) {
            companyPullConfigEntity = new CompanyPullConfigEntity();
        }
        companyPullConfigEntity.setCompanyId(UserUtils.getUser().getCompanyId());
        companyPullConfigEntity.setConfigType(companyPullConfigSaveRequest.getConfigType());
        companyPullConfigEntity.setPullConfig(companyPullConfigSaveRequest.getPullConfig());
        companyPullConfigEntity.setCreator(UserUtils.getUser().getNickName());
        companyPullConfigEntity.setModifier(UserUtils.getUser().getNickName());
        companyPullConfigEntity.setId(ObjectId.getGuid());
        if (CompanyDataSourceEnum.DING_TALK.name().equals(companyPullConfigSaveRequest.getConfigType())) {
            DingTalkConfig dingTalkConfig =
                    JSONObject.parseObject(companyPullConfigSaveRequest.getPullConfig(), DingTalkConfig.class);
            companyPullConfigEntity.setAppId(dingTalkConfig.getClientId());
        } else if (CompanyDataSourceEnum.LARK.name().equals(companyPullConfigSaveRequest.getConfigType())) {
            LarkConfig larkConfig =
                    JSONObject.parseObject(companyPullConfigSaveRequest.getPullConfig(), LarkConfig.class);
            companyPullConfigEntity.setAppId(larkConfig.getClientId());
        }

        saveOrUpdate(companyPullConfigEntity);
        companyService.savePullConfig(companyPullConfigSaveRequest);
    }

    @Override
    public void weComAuth(CompanyPullConfigSaveRequest companyPullConfigSaveRequest) {
        UserDomain user = UserUtils.getUser() == null ? new UserDomain() : UserUtils.getUser();
        LambdaQueryWrapper<CompanyPullConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyPullConfigEntity::getCompanyId, companyPullConfigSaveRequest.getCompanyId());
        queryWrapper.eq(CompanyPullConfigEntity::getConfigType, companyPullConfigSaveRequest.getConfigType());
        queryWrapper.eq(CompanyPullConfigEntity::getSourceAppId, companyPullConfigSaveRequest.getSourceAppId());
        CompanyPullConfigEntity companyPullConfigEntity = companyPullConfigMapper.selectOne(queryWrapper);
        if (companyPullConfigEntity == null) {
            companyPullConfigEntity = new CompanyPullConfigEntity();
            companyPullConfigEntity.setId(ObjectId.getGuid());
        }
        companyPullConfigEntity.setCompanyId(companyPullConfigSaveRequest.getCompanyId());
        companyPullConfigEntity.setConfigType(companyPullConfigSaveRequest.getConfigType());
        companyPullConfigEntity.setPullConfig(companyPullConfigSaveRequest.getPullConfig());
        companyPullConfigEntity.setAppId(companyPullConfigSaveRequest.getAppId());
        companyPullConfigEntity.setSourceAppId(companyPullConfigSaveRequest.getSourceAppId());
        companyPullConfigEntity.setCreator(user.getNickName());
        companyPullConfigEntity.setModifier(user.getNickName());
        saveOrUpdate(companyPullConfigEntity);
        CompanyPullConfigCache.clear(companyPullConfigEntity.getCompanyId(), companyPullConfigEntity.getSourceAppId());
    }


    @Override
    public List<CompanyPullConfigVO> getPullConfig() {
        LambdaQueryWrapper<CompanyPullConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyPullConfigEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        List<CompanyPullConfigEntity> companyPullConfigEntities = companyPullConfigMapper.selectList(queryWrapper);
        return companyPullConfigEntities.stream().map(AbstractCompanyPullConfigConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyPullConfigVO getByType(Long companyId, String configType) {
        LambdaQueryWrapper<CompanyPullConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyPullConfigEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        queryWrapper.eq(CompanyPullConfigEntity::getConfigType, configType);
        CompanyPullConfigEntity companyPullConfigEntity = companyPullConfigMapper.selectOne(queryWrapper);
        return AbstractCompanyPullConfigConverter.INSTANCE.toVO(companyPullConfigEntity);
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public CompanyPullConfigVO getByCompanyIdAndSuiteId(Long companyId, String suiteId) {
        LambdaQueryWrapper<CompanyPullConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CompanyPullConfigEntity::getCompanyId, companyId);
        queryWrapper.eq(CompanyPullConfigEntity::getSourceAppId, suiteId);
        CompanyPullConfigEntity companyPullConfigEntity = companyPullConfigMapper.selectOne(queryWrapper);
        return AbstractCompanyPullConfigConverter.INSTANCE.toVO(companyPullConfigEntity);
    }
}
