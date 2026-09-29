package com.wuji.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.mapper.MainCompanyMapper;
import com.wuji.admin.model.entity.MainCompanyEntity;
import com.wuji.admin.service.MainCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-11-10
 */
@Service
public class MainCompanyServiceImpl extends ServiceImpl<MainCompanyMapper, MainCompanyEntity>
        implements MainCompanyService {

    @Autowired
    private MainCompanyMapper mainCompanyMapper;

    @Override
    public Long checkAndSave(String companyName) {
        LambdaQueryWrapper<MainCompanyEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MainCompanyEntity::getCompanyName, companyName);
        MainCompanyEntity mainCompanyEntity = mainCompanyMapper.selectOne(queryWrapper);
        if (mainCompanyEntity == null) {
            mainCompanyEntity = new MainCompanyEntity();
            mainCompanyEntity.setCompanyName(companyName);
            mainCompanyEntity.setCreateTime(new Date());
            mainCompanyEntity.setUpdateTime(new Date());
            mainCompanyMapper.insert(mainCompanyEntity);
        }
        return mainCompanyEntity.getId();
    }
}
