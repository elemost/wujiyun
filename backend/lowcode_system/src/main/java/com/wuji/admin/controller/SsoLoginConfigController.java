package com.wuji.admin.controller;

import com.wuji.admin.model.request.SsoLoginConfigSaveRequest;
import com.wuji.admin.model.vo.SsoLoginConfigInfoVO;
import com.wuji.admin.service.SsoLoginConfigService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2026-09-16
 */
@RestController
@RequestMapping("/ssoLoginEntity")
public class SsoLoginConfigController {

    @Autowired
    private SsoLoginConfigService ssoLoginConfigService;

    @ApiOperation("创建sso登录配置")
    @PostMapping("/create")
    public Long create(@RequestBody SsoLoginConfigSaveRequest ssoLoginConfigSaveRequest) {
        return ssoLoginConfigService.saveOrUpdate(ssoLoginConfigSaveRequest);
    }

    @ApiOperation("获取sso登录配置信息")
    @GetMapping("/info/{configType}")
    public SsoLoginConfigInfoVO info(@PathVariable String configType) {
        return ssoLoginConfigService.info(configType);
    }
}
