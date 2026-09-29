package com.wuji.admin.controller;

import com.wuji.common.cache.ConfigCache;
import com.wuji.common.model.Response;
import com.wuji.common.model.request.ConfigSaveRequest;
import com.wuji.common.model.vo.ConfigVO;
import com.wuji.common.service.ConfigService;
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
 * @since 2024-05-09
 */
@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private ConfigService configService;


    @ApiOperation("配置列表")
    @GetMapping("/getValue/{configKey}")
    public Response<String> getValue(@PathVariable String configKey) {
        return Response.success(ConfigCache.getValue(configKey));
    }

    @ApiOperation("config修改")
    @PostMapping("/save")
    public void saveOrUpdate(@RequestBody ConfigSaveRequest configSaveRequest) {
        configService.saveOrUpdate(configSaveRequest);
    }

    @ApiOperation("config详情")
    @GetMapping("/detailByKey/{configKey}")
    public ConfigVO detailByKey(@PathVariable String configKey) {
        return configService.detailByKey(configKey);
    }

}
