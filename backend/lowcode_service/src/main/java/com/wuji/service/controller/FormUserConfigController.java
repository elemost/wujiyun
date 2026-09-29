package com.wuji.service.controller;

import com.wuji.service.model.request.FormUserConfigSaveRequest;
import com.wuji.service.model.vo.FormUserConfigVO;
import com.wuji.service.service.FormUserConfigService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-04-18
 */
@RestController
@RequestMapping("/form/user/config")
public class FormUserConfigController {

    @Autowired
    private FormUserConfigService formUserConfigService;

    @ApiOperation("保存配置")
    @PostMapping("/save")
    public void save(@RequestBody FormUserConfigSaveRequest formUserConfigSaveRequest) {
        formUserConfigService.save(formUserConfigSaveRequest);
    }


    @ApiOperation("配置详情")
    @GetMapping("/info")
    public FormUserConfigVO info(@RequestParam("formId") String formId,
                                 @RequestParam("applicationId") String applicationId,
                                 @RequestParam("configType") String configType) {
        return formUserConfigService.getInfo(formId, applicationId, configType);
    }
}
