package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.UserInfoEntity;
import com.wuji.admin.model.request.UserInfoSaveRequest;
import com.wuji.common.model.vo.UserInfoVO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-12-13
 */
public interface UserInfoService extends IService<UserInfoEntity> {
    void save(Long userId, List<UserInfoSaveRequest> userInfoSaveList);

    void save(List<UserInfoSaveRequest> userInfoSaveList);

    List<UserInfoVO> getByUserId(List<Long> userIdList);
}
