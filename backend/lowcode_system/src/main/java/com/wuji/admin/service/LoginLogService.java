package com.wuji.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wuji.admin.model.entity.LoginLogEntity;
import com.wuji.admin.model.request.LoginLogCreateRequest;

/**
 * <p>
 * 登录日志 服务类
 * </p>
 *
 * @author hzm
 * @since 2024-05-28
 */
public interface LoginLogService extends IService<LoginLogEntity> {


    void insert(LoginLogCreateRequest loginLogCreateRequest);

    void insertLog(String username, String ip, String state, String msg);
}
