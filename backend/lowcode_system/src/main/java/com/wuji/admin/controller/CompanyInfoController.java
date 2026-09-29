package com.wuji.admin.controller;

import com.wuji.admin.model.request.CompanyInfoSaveRequest;
import com.wuji.admin.service.CompanyInfoService;
import com.wuji.common.utils.UserUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author hzm
 * @since 2025-04-07
 */
@RestController
@RequestMapping("/company/info")
public class CompanyInfoController {

    @Autowired
    private CompanyInfoService companyInfoService;

    @ApiOperation("修改数据")
    @PutMapping("/save")
    public void save(@RequestBody CompanyInfoSaveRequest companyInfoSaveRequest) {
        companyInfoService.save(UserUtils.getUser().getCompanyId(), companyInfoSaveRequest);
    }
}
