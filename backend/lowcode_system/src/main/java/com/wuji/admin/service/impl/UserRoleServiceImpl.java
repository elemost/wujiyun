package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.wuji.admin.converter.AbstractUserRoleConverter;
import com.wuji.admin.enums.InitRoleEnum;
import com.wuji.admin.mapper.UserRoleMapper;
import com.wuji.admin.model.entity.UserRoleEntity;
import com.wuji.admin.model.vo.RoleVO;
import com.wuji.admin.service.RoleService;
import com.wuji.admin.service.UserRoleService;
import com.wuji.common.model.vo.UserRoleVO;
import com.wuji.common.utils.UserUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * @since 2024-04-22
 */
@Service
@DS("slave")
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRoleEntity> implements UserRoleService {

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleService roleService;

    @Override
    public void save(Long userId, List<Long> roleIdList, Boolean deleted) {
        if (deleted) {
            LambdaQueryWrapper<UserRoleEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserRoleEntity::getUserId, userId);
            queryWrapper.eq(UserRoleEntity::getCompanyId, UserUtils.getUser().getCompanyId());
            queryWrapper.eq(UserRoleEntity::getRoleId, 107L);
            userRoleMapper.delete(queryWrapper);
        }
        if (CollectionUtils.isEmpty(roleIdList)) {
            return;
        }
        List<UserRoleEntity> userRoleEntityList = new ArrayList<>();
        for (Long roleId : roleIdList) {
            UserRoleEntity userRoleEntity = new UserRoleEntity();
            userRoleEntity.setRoleId(roleId);
            userRoleEntity.setUserId(userId);
            userRoleEntity.setCompanyId(UserUtils.getUser().getCompanyId());
            userRoleEntityList.add(userRoleEntity);
        }
        saveBatch(userRoleEntityList);
    }

    @Override
    public List<Long> getCurrentUserRole() {
        LambdaQueryWrapper<UserRoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRoleEntity::getUserId, UserUtils.getUser().getUserId());
        queryWrapper.eq(UserRoleEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        return userRoleMapper.selectList(queryWrapper).stream().map(UserRoleEntity::getRoleId)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRoleVO> getUserRoleByUserIdList(List<Long> userIdList) {
        LambdaQueryWrapper<UserRoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserRoleEntity::getUserId, userIdList);
        queryWrapper.eq(UserRoleEntity::getCompanyId, UserUtils.getUser().getCompanyId());
        final List<UserRoleEntity> userRoleEntityList = userRoleMapper.selectList(queryWrapper);
        final List<Long> roleIdList =
                userRoleEntityList.stream().map(UserRoleEntity::getRoleId).collect(Collectors.toList());
        final List<RoleVO> roleList = roleService.queryByIdList(roleIdList);
        final Map<Long, RoleVO> idToRoleMap = roleList.stream().collect(Collectors.toMap(RoleVO::getRoleId, c -> c));
        List<UserRoleVO> userRoleList = new ArrayList<>();
        for (UserRoleEntity userRoleEntity : userRoleEntityList) {
            final RoleVO roleVO = idToRoleMap.get(userRoleEntity.getRoleId());
            if (roleVO == null) {
                continue;
            }
            final UserRoleVO userRoleVO = AbstractUserRoleConverter.INSTANCE.toVO(roleVO);
            userRoleVO.setUserId(userRoleEntity.getUserId());
            userRoleList.add(userRoleVO);
        }
        return userRoleList;
    }

    @Override
    public List<UserRoleVO> getManageList() {
        LambdaQueryWrapper<UserRoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRoleEntity::getRoleId, 107L);
        return userRoleMapper.selectList(queryWrapper).stream().map(AbstractUserRoleConverter.INSTANCE::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRoleVO> getUserRoleByRoleList(List<Long> roleList) {
        LambdaQueryWrapper<UserRoleEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(UserRoleEntity::getRoleId, roleList);
        final List<UserRoleEntity> userRoleEntityList = userRoleMapper.selectList(queryWrapper);
        return userRoleEntityList.stream().map(AbstractUserRoleConverter.INSTANCE::toVO).collect(Collectors.toList());
    }

    @Override
    public void insertWhilePull(List<Long> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return;
        }
        List<UserRoleEntity> userRoleEntityList = new ArrayList<>();
        final List<Long> roleIdList = InitRoleEnum.getPullRole();
        for (Long userId : userIdList) {
            for (Long roleId : roleIdList) {
                UserRoleEntity userRoleEntity = new UserRoleEntity();
                userRoleEntity.setUserId(userId);
                userRoleEntity.setRoleId(roleId);
                userRoleEntity.setCompanyId(UserUtils.getUser().getCompanyId());
                userRoleEntityList.add(userRoleEntity);
            }
        }
        saveBatch(userRoleEntityList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertWhileRegister(Long userId, Long companyId) {
        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setRoleId(100L);
        userRole.setCompanyId(companyId);
        userRole.setUserId(userId);
        UserRoleEntity userRole2 = new UserRoleEntity();
        userRole2.setRoleId(107L);
        userRole2.setCompanyId(companyId);
        userRole2.setUserId(userId);
        saveBatch(Lists.newArrayList(userRole, userRole2));
    }
}
