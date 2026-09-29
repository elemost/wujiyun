package com.wuji.admin.controller;

import com.wuji.admin.cache.CompanyAppCache;
import com.wuji.admin.model.vo.CompanyAppDetailVO;
import com.wuji.admin.service.CompanyAppService;
import com.wuji.common.utils.UserUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 企业使用产品限制 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-03-19
 */
@RestController
@RequestMapping("/company/app")
public class CompanyAppController {

    @Autowired
    private CompanyAppService companyAppService;

    @ApiOperation("获取当前公司的应用")
    @GetMapping("/getCurrent")
    public CompanyAppDetailVO getCurrent() {
        CompanyAppCache.refresh(UserUtils.getUser().getCompanyId());
        return companyAppService.getCurrentClient();
    }
}
