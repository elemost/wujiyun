package com.wuji.admin.controller.open;

import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.SuiteAccessInfoVO;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.UserCompanyService;
import com.wuji.admin.service.WeComService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open/api")
@Slf4j
public class WeComOpenApiController {
    @Autowired
    private WeComService weComServiceImpl;

    @Autowired
    private UserCompanyService userCompanyService;

    @Autowired
    private CompanyService companyService;

    @ApiOperation("用户登录获取用户信息")
    @GetMapping("/wecom/userInfo/auth")
    public SuiteAccessInfoVO auth(@RequestParam("code") String code, @RequestParam("secretId") String secretId) {
        CompanyVO companyVO = companyService.getBySecretId(secretId);
        String weComUserId = weComServiceImpl.getUserAssessToken(code, null, companyVO);
        SuiteAccessInfoVO suiteAccessInfoVO = new SuiteAccessInfoVO();
        suiteAccessInfoVO.setUserId(weComUserId);
        suiteAccessInfoVO.setCorpId(secretId);
        // UserCompanyVO userCompanyVO =
        //         userCompanyService.getByCompanyIdAndSecret(suiteAccessInfoVO.getUserId(), companyVO.getCompanyId());
        suiteAccessInfoVO.setExist(Boolean.TRUE);
        suiteAccessInfoVO.setCompanyId(companyVO.getCompanyId());
        return suiteAccessInfoVO;
    }
}
