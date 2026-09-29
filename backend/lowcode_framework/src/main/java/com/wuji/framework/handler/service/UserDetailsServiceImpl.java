package com.wuji.framework.handler.service;


import com.wuji.admin.enums.UserStateEnum;
import com.wuji.admin.service.UserService;
import com.wuji.common.exception.BizException;
import com.wuji.common.model.vo.UserVO;
import com.wuji.framework.model.domain.LoginUserDomain;
import com.wuji.framework.security.service.SysPasswordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service("userDetailsByPassword")
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private SysPasswordService passwordService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserVO user = userService.queryByUserName(username);
        if (user == null) {
            throw new BizException("登录用户：" + username + " 不存在");
        } else if (UserStateEnum.FREEZE.getCode().equals(user.getStatus())) {
            log.info("登录用户：{} 已被删除.", user.getUserName());
            throw new BizException("对不起，您的账号：" + user.getUserName() + " 已停用");
        }
        passwordService.validate(user);
        return createLoginUser(user);
    }

    public UserDetails createLoginUser(UserVO user) {
        Set<String> perms = new HashSet<String>();

        return new LoginUserDomain(user.getUserId(), user.getDeptId(), user, perms);
    }
}
