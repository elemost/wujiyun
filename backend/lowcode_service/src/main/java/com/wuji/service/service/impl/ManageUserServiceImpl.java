package com.wuji.service.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.service.UserRoleService;
import com.wuji.admin.service.UserService;
import com.wuji.common.model.vo.UserRoleVO;
import com.wuji.common.utils.UserUtils;
import com.wuji.service.cache.ManageCache;
import com.wuji.service.constant.Constants;
import com.wuji.service.converter.AbstractManageUserConverter;
import com.wuji.service.mapper.ManageUserMapper;
import com.wuji.service.model.entity.ManageUserEntity;
import com.wuji.service.model.vo.ManageUserVO;
import com.wuji.service.service.ManageUserService;
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
 * 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2025-08-11
 */
@Service
public class ManageUserServiceImpl extends ServiceImpl<ManageUserMapper, ManageUserEntity>
        implements ManageUserService {

    @Autowired
    private ManageUserMapper manageUserMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Override
    public void delete(String groupId) {
        LambdaQueryWrapper<ManageUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ManageUserEntity::getGroupId, groupId);
        queryWrapper.eq(ManageUserEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        manageUserMapper.delete(queryWrapper);
    }

    @Override
    public List<Long> update(String groupId, List<Long> userIdList) {
        if (CollectionUtils.isNotEmpty(userIdList)) {
            LambdaQueryWrapper<ManageUserEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(ManageUserEntity::getUserId, userIdList);
            queryWrapper.eq(ManageUserEntity::getCompanyId, UserUtils.getUser().getCompanyId());
            queryWrapper.ne(ManageUserEntity::getGroupId, groupId);
            List<ManageUserEntity> manageUserEntityList = manageUserMapper.selectList(queryWrapper);
            if (CollectionUtils.isNotEmpty(manageUserEntityList)) {
                return manageUserEntityList.stream().map(ManageUserEntity::getUserId).collect(Collectors.toList());
            }
        }
        delete(groupId);
        if (CollectionUtils.isEmpty(userIdList)) {
            ManageCache.clear(UserUtils.getUser().getCompanyId());
            return new ArrayList<>();
        }
        List<ManageUserEntity> manageUserEntities = new ArrayList<>();
        for (Long userId : userIdList) {
            ManageUserEntity manageUserEntity = new ManageUserEntity();
            manageUserEntity.setUserId(userId);
            manageUserEntity.setCompanyId(UserUtils.getUser().getCompanyId());
            manageUserEntity.setGroupId(groupId);
            manageUserEntities.add(manageUserEntity);
        }
        saveBatch(manageUserEntities);
        ManageCache.clear(UserUtils.getUser().getCompanyId());
        return new ArrayList<>();
    }

    @Override
    public List<ManageUserVO> getByGroupIds(List<String> groupIds) {
        if (CollectionUtils.isEmpty(groupIds)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<ManageUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ManageUserEntity::getGroupId, groupIds);
        queryWrapper.eq(ManageUserEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        List<ManageUserEntity> manageUserEntities = manageUserMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(manageUserEntities)) {
            return new ArrayList<>();
        }
        List<ManageUserVO> manageUserList = new ArrayList<>();
        List<Long> userIdList =
                manageUserEntities.stream().map(ManageUserEntity::getUserId).collect(Collectors.toList());
        Map<Long, String> idToNameMap = userService.getIdToNameMap(userIdList);
        for (ManageUserEntity manageUserEntity : manageUserEntities) {
            ManageUserVO manageUserVO = new ManageUserVO();
            manageUserVO.setUserId(manageUserEntity.getUserId());
            String userName = idToNameMap.get(manageUserEntity.getUserId());
            if (StringUtils.isEmpty(userName)) {
                continue;
            }
            manageUserVO.setUserName(userName);
            manageUserVO.setGroupId(manageUserEntity.getGroupId());
            manageUserList.add(manageUserVO);
        }
        return manageUserList;
    }

    @Override
    public List<String> getByUserIdList(List<Long> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<ManageUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ManageUserEntity::getUserId, userIdList);
        queryWrapper.eq(ManageUserEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        return manageUserMapper.selectList(queryWrapper).stream().map(ManageUserEntity::getGroupId)
                .collect(Collectors.toList());
    }

    @Override
    public ManageUserVO getCurrentUserGroup() {
        LambdaQueryWrapper<ManageUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ManageUserEntity::getUserId, UserUtils.getUser().getUserId());
        queryWrapper.eq(ManageUserEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        ManageUserEntity manageUserEntity = manageUserMapper.selectOne(queryWrapper);
        if (manageUserEntity == null) {
            return null;
        }
        ManageUserVO manageUserVO = new ManageUserVO();
        manageUserVO.setUserId(manageUserEntity.getUserId());
        manageUserVO.setGroupId(manageUserEntity.getGroupId());
        return manageUserVO;
    }

    @Override
    public List<ManageUserVO> allUser() {
        LambdaQueryWrapper<ManageUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ManageUserEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        List<ManageUserEntity> manageUserEntityList = manageUserMapper.selectList(queryWrapper);
        return manageUserEntityList.stream().map(AbstractManageUserConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public void dealData() {
        List<UserRoleVO> manageList = userRoleService.getManageList();
        List<ManageUserEntity> manageUserEntityList = new ArrayList<>();
        for (UserRoleVO userRoleVO : manageList) {
            ManageUserEntity manageUserEntity = new ManageUserEntity();
            manageUserEntity.setGroupId(Constants.SUPER_MANAGE);
            manageUserEntity.setUserId(userRoleVO.getUserId());
            manageUserEntity.setCompanyId(userRoleVO.getCompanyId());
            manageUserEntityList.add(manageUserEntity);
        }
        if (CollectionUtils.isNotEmpty(manageUserEntityList)) {
            saveBatch(manageUserEntityList);
        }
    }

}
