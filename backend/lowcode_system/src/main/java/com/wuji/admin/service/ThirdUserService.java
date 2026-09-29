package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.ThirdUserEntity;
import com.wuji.admin.model.vo.ThirdUserVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hzm
 * @since 2025-02-20
 */
public interface ThirdUserService extends IService<ThirdUserEntity> {
    ThirdUserVO getByOpenId(String openId, Short thirdType);

    ThirdUserVO getByUnionId(String unionId, Short thirdType);

    void saveOpenId(Long userId, String openId, Short thirdType, String unionId);
}
