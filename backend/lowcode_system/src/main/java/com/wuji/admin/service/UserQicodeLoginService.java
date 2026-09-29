package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.UserQicodeLoginEntity;
import com.wuji.admin.model.vo.UserQicodeLoginVO;

/**
 * <p>
 * 用户扫码登录表 服务类
 * </p>
 *
 * @author hzm
 * @since 2025-02-20
 */
public interface UserQicodeLoginService extends IService<UserQicodeLoginEntity> {
    UserQicodeLoginVO getByTicket(String ticket);

    void used(Long id);

}
