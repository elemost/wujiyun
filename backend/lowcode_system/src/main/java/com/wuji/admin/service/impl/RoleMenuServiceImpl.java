package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wuji.admin.model.entity.RoleMenuEntity;
import com.wuji.admin.mapper.RoleMenuMapper;
import com.wuji.admin.service.RoleMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.service.UserRoleService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-04-22
 */
@Service
@DS("slave")
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenuEntity> implements RoleMenuService {

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private UserRoleService userRoleService;

    @Override
    public void save(List<Long> menuIdList, Long roleId, Boolean deleted) {
        if (deleted) {
            LambdaQueryWrapper<RoleMenuEntity> delete = new LambdaQueryWrapper<>();
            delete.eq(RoleMenuEntity::getRoleId, roleId);
            roleMenuMapper.delete(delete);
        }
        if (CollectionUtils.isEmpty(menuIdList)) {
            return;
        }
        List<RoleMenuEntity> roleMenuEntityList = new ArrayList<>();
        for (Long menuId : menuIdList) {
            RoleMenuEntity roleMenuEntity = new RoleMenuEntity();
            roleMenuEntity.setMenuId(menuId);
            roleMenuEntity.setRoleId(roleId);
            roleMenuEntityList.add(roleMenuEntity);
        }
        saveBatch(roleMenuEntityList);
    }

    @Override
    public List<Long> getCurrentUserMenu() {
        final List<Long> roleIdList = userRoleService.getCurrentUserRole();
        LambdaQueryWrapper<RoleMenuEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(RoleMenuEntity::getRoleId, roleIdList);
        return roleMenuMapper.selectList(queryWrapper).stream().map(RoleMenuEntity::getMenuId).collect(Collectors.toList());
    }

    @Override
    public List<Long> queryByRoleIdList(List<Long> roleIdList) {
        LambdaQueryWrapper<RoleMenuEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(RoleMenuEntity::getRoleId, roleIdList);
        return roleMenuMapper.selectList(queryWrapper).stream().map(RoleMenuEntity::getMenuId).collect(Collectors.toList());
    }
}
