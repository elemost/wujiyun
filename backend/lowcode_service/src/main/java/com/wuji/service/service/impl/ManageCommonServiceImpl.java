package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wuji.common.service.ManageCommonService;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.cache.ManageCache;
import com.wuji.service.mapper.ManageUserMapper;
import com.wuji.service.model.entity.ManageUserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class ManageCommonServiceImpl implements ManageCommonService {

    @Autowired
    private ManageUserMapper manageUserMapper;

    @Override
    @Async
    public void deleteManage(Long companyId, Long userId) {
        LambdaQueryWrapper<ManageUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ManageUserEntity::getCompanyId, companyId);
        queryWrapper.eq(ManageUserEntity::getUserId, userId);
        manageUserMapper.delete(queryWrapper);
        ManageCache.clear(UserUtils.getUser().getCompanyId());
    }
}
