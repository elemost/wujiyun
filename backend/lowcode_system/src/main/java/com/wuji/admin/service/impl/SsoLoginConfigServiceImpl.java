package com.wuji.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.converter.AbstractSsoLoginConverter;
import com.wuji.admin.mapper.SsoLoginConfigMapper;
import com.wuji.admin.model.entity.SsoLoginConfigEntity;
import com.wuji.admin.model.request.SsoLoginConfigSaveRequest;
import com.wuji.admin.model.vo.SsoLoginConfigInfoVO;
import com.wuji.admin.service.SsoLoginConfigService;
import com.wuji.common.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2026-09-16
 */
@Service
public class SsoLoginConfigServiceImpl extends ServiceImpl<SsoLoginConfigMapper, SsoLoginConfigEntity>
        implements SsoLoginConfigService {

    @Autowired
    private SsoLoginConfigMapper ssoLoginConfigMapper;

    @Override
    public Long saveOrUpdate(SsoLoginConfigSaveRequest ssoLoginConfigSaveRequest) {
        SsoLoginConfigEntity ssoLoginConfigEntity =
                AbstractSsoLoginConverter.INSTANCE.convert(ssoLoginConfigSaveRequest);
        ssoLoginConfigEntity.setCompanyId(UserUtils.getUser().getCompanyId());
        if (ssoLoginConfigEntity.getId() == null) {
            ssoLoginConfigEntity.setCreateBy(UserUtils.getUser().getUserName());
            ssoLoginConfigEntity.setUpdateBy(UserUtils.getUser().getUserName());
            ssoLoginConfigEntity.setCreateTime(new Date());
            ssoLoginConfigEntity.setUpdateTime(new Date());
        } else {
            ssoLoginConfigEntity.setUpdateBy(UserUtils.getUser().getUserName());
            ssoLoginConfigEntity.setUpdateTime(new Date());
        }
        saveOrUpdate(ssoLoginConfigEntity);
        return ssoLoginConfigEntity.getId();
    }

    @Override
    public SsoLoginConfigInfoVO info(String configType) {
        LambdaQueryWrapper<SsoLoginConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SsoLoginConfigEntity::getConfigType, configType);
        queryWrapper.eq(SsoLoginConfigEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        SsoLoginConfigEntity ssoLoginConfigEntity = ssoLoginConfigMapper.selectOne(queryWrapper);
        return AbstractSsoLoginConverter.INSTANCE.convert(ssoLoginConfigEntity);
    }
}
