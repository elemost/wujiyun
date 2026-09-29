package com.wuji.framework.handler;


import com.wuji.common.model.vo.LoginVO;
import com.wuji.framework.model.request.LoginRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录策略
 * 登录策略接口
 *
 * @author yizhi
 * @date 2023/12/20
 */
public interface LoginStrategy {


    /**
     * 类型
     */

    String getLoginType();

    /**
     * 登录
     *
     * @param loginRequest
     * @param request
     * @return
     */
    LoginVO login(LoginRequest loginRequest, HttpServletRequest request);

}
