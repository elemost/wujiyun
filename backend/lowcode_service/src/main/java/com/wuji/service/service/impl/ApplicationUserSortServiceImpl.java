package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.model.domain.UserDomain;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.converter.AbstractApplicationUserSortConverter;
import com.wuji.service.mapper.ApplicationUserSortMapper;
import com.wuji.service.model.entity.ApplicationUserSortEntity;
import com.wuji.service.model.request.ApplicationUserSortSaveRequest;
import com.wuji.service.model.vo.ApplicationUserSortVO;
import com.wuji.service.service.ApplicationUserSortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-12-02
 */
@Service
public class ApplicationUserSortServiceImpl extends ServiceImpl<ApplicationUserSortMapper, ApplicationUserSortEntity>
        implements ApplicationUserSortService {

    @Autowired
    private ApplicationUserSortMapper applicationUserSortMapper;

    @Override
    public void saveSort(ApplicationUserSortSaveRequest applicationUserSortSaveRequest) {
        UserDomain user = UserUtils.getUser();
        LambdaQueryWrapper<ApplicationUserSortEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationUserSortEntity::getUserId, user.getUserId());
        queryWrapper.eq(ApplicationUserSortEntity::getCompanyId, user.getCompanyId());
        applicationUserSortMapper.delete(queryWrapper);
        Integer sort = 0;
        List<ApplicationUserSortEntity> applicationUserSortEntities = new ArrayList<>();
        for (String applicationId : applicationUserSortSaveRequest.getApplicationIdList()) {
            ApplicationUserSortEntity applicationUserSortEntity = new ApplicationUserSortEntity();
            applicationUserSortEntity.setUserId(user.getUserId());
            applicationUserSortEntity.setCompanyId(user.getCompanyId());
            applicationUserSortEntity.setApplicationId(applicationId);
            applicationUserSortEntity.setSort(sort);
            sort++;
            applicationUserSortEntities.add(applicationUserSortEntity);
        }
        saveBatch(applicationUserSortEntities);
    }


    @Override
    public List<ApplicationUserSortVO> ownList() {
        UserDomain user = UserUtils.getUser();
        LambdaQueryWrapper<ApplicationUserSortEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ApplicationUserSortEntity::getUserId, user.getUserId());
        queryWrapper.eq(ApplicationUserSortEntity::getCompanyId, user.getCompanyId());
        List<ApplicationUserSortEntity> applicationUserSortEntityList =
                applicationUserSortMapper.selectList(queryWrapper);
        return applicationUserSortEntityList.stream().map(AbstractApplicationUserSortConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }
}
