package com.wuji.admin.controller;

import com.wuji.admin.model.request.CompanyPullConfigSaveRequest;
import com.wuji.admin.service.CompanyPullConfigService;
import com.wuji.common.model.vo.CompanyPullConfigVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2024-11-29
 */
@RestController
@RequestMapping("/company/pull/config")
public class CompanyPullConfigController {

    @Autowired
    private CompanyPullConfigService companyPullConfigService;

    @ApiOperation("保存拉取数据配置")
    @PostMapping("/savePullConfig")
    public void savePullConfig(@RequestBody CompanyPullConfigSaveRequest companyPullConfigSaveRequest) {
        companyPullConfigService.save(companyPullConfigSaveRequest);
    }



    @ApiOperation("配置列表")
    @GetMapping("/getPullConfig")
    public List<CompanyPullConfigVO> getPullConfig() {
        return companyPullConfigService.getPullConfig();
    }
}
