package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.UserRoleEntity;
import com.wuji.common.model.vo.UserRoleVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2024-04-22
 */
public interface UserRoleService extends IService<UserRoleEntity> {
    void save(Long userId, List<Long> roleIdList, Boolean deleted);

    /**
     * 获取当前用户角色
     *
     * @return
     */
    List<Long> getCurrentUserRole();

    List<UserRoleVO> getUserRoleByUserIdList(List<Long> userIdList);

    List<UserRoleVO> getManageList();

    List<UserRoleVO> getUserRoleByRoleList(List<Long> roleList);

    void insertWhilePull(List<Long> userIdList);

    void insertWhileRegister(Long userId, Long companyId);
}
