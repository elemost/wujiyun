package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.RoleEntity;
import com.wuji.admin.model.request.RoleCreateRequest;
import com.wuji.admin.model.request.RoleListRequest;
import com.wuji.admin.model.request.RoleUpdateRequest;
import com.wuji.admin.model.vo.RoleDetailVO;
import com.wuji.admin.model.vo.RoleVO;
import com.wuji.common.model.vo.QueryPageVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2024-04-22
 */
public interface RoleService extends IService<RoleEntity> {
    void create(RoleCreateRequest roleCreateRequest);

    void update(RoleUpdateRequest roleUpdateRequest);

    QueryPageVO<RoleVO> queryList(RoleListRequest roleListRequest);

    List<RoleVO> queryByIdList(List<Long> roleIdList);

    RoleDetailVO queryById(Long id);

    List<RoleVO> getAllRole();

    void deleted(Long id);

    void initRole();
}
