package com.wuji.admin.service;

import com.wuji.admin.model.entity.RoleMenuEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2024-04-22
 */
public interface RoleMenuService extends IService<RoleMenuEntity> {
    void save(List<Long> menuIdList, Long roleId, Boolean deleted);

    List<Long> getCurrentUserMenu();

    List<Long> queryByRoleIdList(List<Long> roleIdList);
}
