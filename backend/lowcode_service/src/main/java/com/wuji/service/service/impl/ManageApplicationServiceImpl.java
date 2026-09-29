package com.wuji.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.cache.ManageCache;
import com.wuji.service.mapper.ManageApplicationMapper;
import com.wuji.service.model.entity.ManageApplicationEntity;
import com.wuji.service.model.vo.ApplicationVO;
import com.wuji.service.model.vo.ManageApplicationVO;
import com.wuji.service.service.ApplicationService;
import com.wuji.service.service.ManageApplicationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-08-11
 */
@Service
public class ManageApplicationServiceImpl extends ServiceImpl<ManageApplicationMapper, ManageApplicationEntity>
        implements ManageApplicationService {

    @Autowired
    private ManageApplicationMapper manageApplicationMapper;

    @Autowired
    private ApplicationService applicationService;

    @Override
    public void delete(String groupId) {
        LambdaQueryWrapper<ManageApplicationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ManageApplicationEntity::getGroupId, groupId);
        queryWrapper.eq(ManageApplicationEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        manageApplicationMapper.delete(queryWrapper);
    }

    @Override
    public void update(String groupId, List<String> applicationIdList) {
        delete(groupId);
        if (CollectionUtils.isEmpty(applicationIdList)) {
            return;
        }
        List<ManageApplicationEntity> manageApplicationEntities = new ArrayList<>();
        for (String applicationId : applicationIdList) {
            ManageApplicationEntity manageApplicationEntity = new ManageApplicationEntity();
            manageApplicationEntity.setApplicationId(applicationId);
            manageApplicationEntity.setCompanyId(UserUtils.getUser().getCompanyId());
            manageApplicationEntity.setGroupId(groupId);
            manageApplicationEntities.add(manageApplicationEntity);
        }
        saveBatch(manageApplicationEntities);
        ManageCache.clear(UserUtils.getUser().getCompanyId());
    }

    @Override
    public List<ManageApplicationVO> getByGroupIds(List<String> groupIds) {
        if (CollectionUtils.isEmpty(groupIds)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<ManageApplicationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ManageApplicationEntity::getGroupId, groupIds);
        queryWrapper.eq(ManageApplicationEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        List<ManageApplicationEntity> manageApplicationEntities = manageApplicationMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(manageApplicationEntities)) {
            return new ArrayList<>();
        }
        List<String> applicationIdList =
                manageApplicationEntities.stream().map(ManageApplicationEntity::getApplicationId)
                        .collect(Collectors.toList());
        List<ApplicationVO> applicationVOS = applicationService.getApplicationByIdList(applicationIdList);
        Map<String, String> applicationIdToNameMap = applicationVOS.stream()
                .collect(Collectors.toMap(ApplicationVO::getId, ApplicationVO::getApplicationName));
        List<ManageApplicationVO> manageApplicationVOList = new ArrayList<>();
        for (ManageApplicationEntity manageApplicationEntity : manageApplicationEntities) {
            ManageApplicationVO manageApplicationVO = new ManageApplicationVO();
            manageApplicationVO.setApplicationName(
                    applicationIdToNameMap.get(manageApplicationEntity.getApplicationId()));
            manageApplicationVO.setApplicationId(manageApplicationEntity.getApplicationId());
            manageApplicationVO.setGroupId(manageApplicationEntity.getGroupId());
            manageApplicationVOList.add(manageApplicationVO);
        }
        return manageApplicationVOList;
    }

    @Override
    public List<String> getByApplicationIdList(List<String> applicationList) {
        if (CollectionUtils.isEmpty(applicationList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<ManageApplicationEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ManageApplicationEntity::getApplicationId, applicationList);
        queryWrapper.eq(ManageApplicationEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        return manageApplicationMapper.selectList(queryWrapper).stream().map(ManageApplicationEntity::getGroupId)
                .collect(Collectors.toList());
    }
}
