package com.wuji.admin.controller;

import com.wuji.admin.aspect.corp.annotation.SyncCompany;
import com.wuji.admin.cache.CompanyPullConfigCache;
import com.wuji.admin.enums.CompanyDataSourceEnum;
import com.wuji.admin.model.request.CompanyCheckExistRequest;
import com.wuji.admin.model.request.CompanySaveRequest;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.service.CompanyService;
import com.wuji.common.model.Response;
import com.wuji.common.model.vo.CompanyPullConfigVO;
import com.wuji.common.utils.UserUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @ApiOperation("获取公司列表")
    @GetMapping("/user/company/list")
    public List<CompanyVO> userCompany() {
        return companyService.userCompany();
    }

    @ApiOperation("修改数据")
    @PutMapping("/update")
    @SyncCompany
    public void update(@RequestBody CompanySaveRequest companySaveRequest) {
        companyService.update(companySaveRequest);
    }

    @ApiOperation("当前公司")
    @GetMapping("/currentCompany")
    public CompanyVO currentCompany() {
        CompanyVO info = companyService.info(UserUtils.getUser().getCompanyId());
        if (CompanyDataSourceEnum.WECOM_THIRD.name().equals(info.getDataSource())) {
            CompanyPullConfigVO value = CompanyPullConfigCache.getValue(UserUtils.getUser().getCompanyId(),
                    UserUtils.getUser().getSuiteId());
            info.setPullConfig(value.getPullConfig());
        }
        return info;
    }

    @ApiOperation("当前公司")
    @GetMapping("/getPullConfig/{uuid}")
    public Response<String> getPullConfig(@PathVariable String uuid) {
        return Response.success(companyService.getPullConfig(uuid));
    }

    @ApiOperation("通过uuid获取用户信息")
    @GetMapping("/info/{uuid}")
    public CompanyVO info(@PathVariable String uuid) {
        return companyService.infoByUuid(uuid);
    }

    @ApiOperation("通过uuid获取用户信息")
    @PostMapping("/checkCompanyExist")
    public void checkCompanyExist(@RequestBody CompanyCheckExistRequest companyCheckExistRequest) {
        companyService.checkCompanyExist(companyCheckExistRequest.getCompanyName());
    }
}
