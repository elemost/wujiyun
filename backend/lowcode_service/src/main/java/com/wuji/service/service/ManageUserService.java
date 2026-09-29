package com.wuji.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.service.model.entity.ManageUserEntity;
import com.wuji.service.model.vo.ManageUserVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-08-11
 */
public interface ManageUserService extends IService<ManageUserEntity> {

    void delete(String groupId);

    List<Long> update(String groupId, List<Long> userIdList);

    List<ManageUserVO> getByGroupIds(List<String> groupIds);

    List<String> getByUserIdList(List<Long> userIdList);

    ManageUserVO getCurrentUserGroup();

    List<ManageUserVO> allUser();

    void dealData();
}
