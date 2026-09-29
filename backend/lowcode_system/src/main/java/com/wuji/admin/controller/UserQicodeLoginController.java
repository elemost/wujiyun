package com.wuji.admin.controller;

import com.wuji.admin.service.UserQicodeLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户扫码登录表 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-02-20
 */
@RestController
@RequestMapping("/userQicodeLoginEntity")
public class UserQicodeLoginController {
    @Autowired
    private UserQicodeLoginService userQicodeLoginService;
}
