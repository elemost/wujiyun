package com.wuji.admin.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuji.admin.converter.AbstractLoginLogConverter;
import com.wuji.admin.mapper.LoginLogMapper;
import com.wuji.admin.model.entity.LoginLogEntity;
import com.wuji.admin.model.request.LoginLogCreateRequest;
import com.wuji.admin.service.LoginLogService;
import com.wuji.common.constant.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 登录日志 服务实现类
 * </p>
 *
 * @author hzm
 * @since 2024-05-28
 */
@Service
@DS("slave")
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLogEntity> implements LoginLogService {

    @Autowired
    private LoginLogMapper loginLogMapper;

    @Override
    public void insert(LoginLogCreateRequest loginLogCreateRequest) {
        // LoginLogEntity loginLogEntity = AbstractLoginLogConverter.INSTANCE.toEntity(loginLogCreateRequest);
        // loginLogMapper.insert(loginLogEntity);
    }

    @Override
    @Async
    public void insertLog(String username, String ip, String status, String msg) {
        LoginLogEntity loginLogEntity = new LoginLogEntity();
        loginLogEntity.setUserName(username);
        loginLogEntity.setIpAddr(ip);
        loginLogEntity.setMsg(msg);
        // 日志状态
        if (StringUtils.equalsAny(status, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER))
        {
            loginLogEntity.setState(Boolean.TRUE);
        }
        else if (Constants.LOGIN_FAIL.equals(status))
        {
            loginLogEntity.setState(Boolean.FALSE);
        }
        loginLogMapper.insert(loginLogEntity);
    }
}
