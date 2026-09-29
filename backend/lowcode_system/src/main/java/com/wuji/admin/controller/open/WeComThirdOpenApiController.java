package com.wuji.admin.controller.open;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.enums.AdminResultCode;
import com.wuji.admin.exception.AdminException;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.SuiteAccessInfoVO;
import com.wuji.admin.model.vo.WeComConfigVO;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.WeComThirdService;
import com.wuji.common.constant.Constants;
import com.wuji.common.model.WeComResponse;
import com.wuji.common.properties.WeComProperties;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/open/api")
@Slf4j
public class WeComThirdOpenApiController {

    @Autowired
    private WeComThirdService weComThirdServiceImpl;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private WeComProperties weComProperties;

    @ApiOperation("用户登录获取用户信息")
    @GetMapping("/userInfoThird/auth")
    public SuiteAccessInfoVO auth(@RequestParam("code") String code, @RequestParam("suiteId") String suiteId) {
        CompanyVO suitIdVO = new CompanyVO();
        suitIdVO.setSuiteId(suiteId);
        String userAssessToken = weComThirdServiceImpl.getUserAssessToken(code, null, suitIdVO);
        JSONObject jsonObject = JSONObject.parseObject(userAssessToken);
        SuiteAccessInfoVO suiteAccessInfoVO = new SuiteAccessInfoVO();
        suiteAccessInfoVO.setUserId(jsonObject.getString("userId"));
        suiteAccessInfoVO.setCorpId(jsonObject.getString("corpId"));
        log.info("企业微信授权信息" + userAssessToken);
        CompanyVO companyVO =
                companyService.getBySecretId(Constants.getThirdWeComSecret(suiteAccessInfoVO.getCorpId(), suiteId));
        if (companyVO == null) {
            throw new AdminException(AdminResultCode.COMPANY_NOT_AUTH);
        }
        suiteAccessInfoVO.setExist(Boolean.TRUE);
        suiteAccessInfoVO.setCompanyId(companyVO.getCompanyId());
        return suiteAccessInfoVO;
    }

    @ApiOperation("thirdConfig")
    @GetMapping("/qy/wechat/third/app/getConfig")
    public WeComConfigVO thirdConfig() {
        WeComProperties.WeComConfig weComConfig = weComProperties.getBySuiteId("ww84e169692e6fd7b5");
        WeComConfigVO weComConfigVO = new WeComConfigVO();
        weComConfigVO.setSuiteId(weComConfig.getSuiteId());
        weComConfigVO.setCorpId(weComConfig.getCorpId());
        return weComConfigVO;
    }

    @ApiOperation("接收企业微信信息")
    @GetMapping("/weCom/suiteTicket")
    public WeComResponse suiteTicket(@RequestParam(value = "msg_signature", required = false) String msg_signature,
                                     @RequestParam(value = "timestamp", required = false) String timestamp,
                                     @RequestParam(value = "echostr", required = false) String echostr,
                                     @RequestParam(value = "nonce") String nonce) {
        JSONObject json = new JSONObject();
        json.put("echostr", echostr);
        log.info("msg_signature : " + msg_signature);
        log.info("timestamp : " + timestamp);
        log.info("echostr : " + echostr);
        log.info("nonce : " + nonce);
        return WeComResponse.success(
                (Long) weComThirdServiceImpl.subscribe(msg_signature, timestamp, nonce, json, "ww84e169692e6fd7b5")
                        .get("success"));
    }

    @ApiOperation("接收企业微信信息")
    @PostMapping(value = "/weCom/suiteTicket")
    public void suiteTicketPost(@RequestParam(value = "msg_signature", required = false) String msg_signature,
                                @RequestParam(value = "timestamp", required = false) String timestamp,
                                @RequestBody(required = false) String echostr,
                                @RequestParam(value = "nonce") String nonce, HttpServletResponse response)
            throws IOException {
        JSONObject json = new JSONObject();
        json.put("echostr", echostr);
        weComThirdServiceImpl.receive(msg_signature, timestamp, nonce, json, "ww84e169692e6fd7b5");
        response.getWriter().write("success");
    }

    @ApiOperation("接收企业微信信息")
    @GetMapping("/weCom/suiteTicket/{suiteId}")
    public WeComResponse suiteTicketSuiteId(
            @RequestParam(value = "msg_signature", required = false) String msg_signature,
            @RequestParam(value = "timestamp", required = false) String timestamp,
            @RequestParam(value = "echostr", required = false) String echostr,
            @RequestParam(value = "nonce") String nonce, @PathVariable String suiteId) {
        JSONObject json = new JSONObject();
        json.put("echostr", echostr);
        return WeComResponse.success(
                (Long) weComThirdServiceImpl.subscribe(msg_signature, timestamp, nonce, json, suiteId).get("success"));
    }

    @ApiOperation("接收企业微信信息")
    @PostMapping(value = "/weCom/suiteTicket/{suiteId}")
    public void suiteTicketSuiteIdPost(@RequestParam(value = "msg_signature", required = false) String msg_signature,
                                       @RequestParam(value = "timestamp", required = false) String timestamp,
                                       @RequestBody(required = false) String echostr,
                                       @RequestParam(value = "nonce") String nonce, HttpServletResponse response,
                                       @PathVariable String suiteId) throws IOException {
        JSONObject json = new JSONObject();
        json.put("echostr", echostr);
        weComThirdServiceImpl.receive(msg_signature, timestamp, nonce, json, suiteId);
        response.getWriter().write("success");
    }
}
