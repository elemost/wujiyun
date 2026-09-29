package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.mapper.ApplicationUseTimeMapper;
import com.wuji.service.model.entity.ApplicationUseTimeEntity;
import com.wuji.service.model.vo.ApplicationCompanyUseVO;
import com.wuji.service.service.ApplicationUseTimeService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-09-04
 */
@Service
public class ApplicationUseTimeServiceImpl extends ServiceImpl<ApplicationUseTimeMapper, ApplicationUseTimeEntity>
        implements ApplicationUseTimeService {

    @Autowired
    private ApplicationUseTimeMapper applicationUseTimeMapper;

    @Override
    public void save(String applicationId) {
        ApplicationUseTimeEntity applicationUseTimeEntity = new ApplicationUseTimeEntity();
        applicationUseTimeEntity.setUserId(UserUtils.getUser().getUserId());
        applicationUseTimeEntity.setCompanyId(UserUtils.getUser().getCompanyId());
        applicationUseTimeEntity.setApplicationId(applicationId);
        applicationUseTimeEntity.setUseTime(new Date());
        applicationUseTimeMapper.insert(applicationUseTimeEntity);
    }

    @Override
    public List<String> getLatestApplication(Integer limitCount) {
        LambdaQueryWrapper<ApplicationUseTimeEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationUseTimeEntity::getUserId, UserUtils.getUser().getUserId());
        queryWrapper.orderByDesc(ApplicationUseTimeEntity::getUseTime);
        queryWrapper.last(" limit " + limitCount);
        return applicationUseTimeMapper.selectList(queryWrapper).stream()
                .map(ApplicationUseTimeEntity::getApplicationId).collect(Collectors.toList());
    }

    @Override
    public void deleteByApplication(String applicationId) {
        LambdaQueryWrapper<ApplicationUseTimeEntity> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(ApplicationUseTimeEntity::getApplicationId, applicationId);
        applicationUseTimeMapper.delete(deleteWrapper);
    }

    @Override
    public List<ApplicationCompanyUseVO> applicationCompanyUse(List<Long> companyIdList) {
        if (CollectionUtils.isEmpty(companyIdList)) {
            return new ArrayList<>();
        }
        QueryWrapper<ApplicationCompanyUseVO> queryWrapper = new QueryWrapper<>();
        queryWrapper.groupBy("company_id");
        queryWrapper.in("company_id", companyIdList);
        return applicationUseTimeMapper.applicationUseTime(queryWrapper);
    }
}
