package com.wuji.admin.controller;

import com.wuji.admin.handler.WeComContext;
import com.wuji.admin.model.request.WeComSignatureRequest;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.WeComSignatureVO;
import com.wuji.admin.model.vo.pull.DepartmentPullVO;
import com.wuji.admin.model.vo.pull.UserPullVO;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.WeComCommonService;
import com.wuji.admin.service.WeComThirdService;
import com.wuji.common.model.Response;
import com.wuji.common.utils.UserUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/wecom")
public class WeComController {

    @Autowired
    private WeComThirdService weComThirdServiceImpl;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private WeComContext weComContext;

    @ApiOperation("获取应用signature")
    @PostMapping("/geAppSignature")
    public WeComSignatureVO geAppSignature(@RequestBody WeComSignatureRequest weComSignatureRequest) {
        CompanyVO info = companyService.info(UserUtils.getUser().getCompanyId());
        WeComCommonService handler = weComContext.getHandler(info.getDataSource());
        return handler.geAppSignature(weComSignatureRequest);
    }

    @ApiOperation("获取企业signature")
    @PostMapping("/getCompanySignature")
    public WeComSignatureVO getCompanySignature(@RequestBody WeComSignatureRequest weComSignatureRequest) {
        CompanyVO info = companyService.info(UserUtils.getUser().getCompanyId());
        WeComCommonService handler = weComContext.getHandler(info.getDataSource());
        return handler.getCompanySignature(weComSignatureRequest);
    }

    @GetMapping(value = "/getDeptChildList/{deptId}")
    public List<DepartmentPullVO> getDeptChildList(@PathVariable String deptId) {
        CompanyVO info = companyService.info(UserUtils.getUser().getCompanyId());
        return weComThirdServiceImpl.getDeptChildList(deptId, info);
    }

    @GetMapping(value = "/getUserDetailById/{userId}")
    public List<UserPullVO> getUserDetailById(@PathVariable String userId) {
        CompanyVO info = companyService.info(UserUtils.getUser().getCompanyId());
        return weComThirdServiceImpl.getUserDetailById(Collections.singletonList(userId), info);
    }

    @GetMapping(value = "/getThirdId/{phoneNumber}")
    public Response<String> getThirdId(@PathVariable String phoneNumber) {
        return Response.success(weComThirdServiceImpl.getThirdId(phoneNumber));
    }
}
