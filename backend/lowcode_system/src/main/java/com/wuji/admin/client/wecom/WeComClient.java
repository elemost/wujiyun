package com.wuji.admin.client.wecom;


import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.client.wecom.model.AppMessageRequest;
import com.wuji.admin.client.wecom.model.AuthInfoRequest;
import com.wuji.admin.client.wecom.model.AuthInfoVO;
import com.wuji.admin.client.wecom.model.CorpTokenRequest;
import com.wuji.admin.client.wecom.model.CorpTokenVO;
import com.wuji.admin.client.wecom.model.DepartmentInfoResult;
import com.wuji.admin.client.wecom.model.DepartmentListResult;
import com.wuji.admin.client.wecom.model.JsApiTicketVO;
import com.wuji.admin.client.wecom.model.OrderInfoRequest;
import com.wuji.admin.client.wecom.model.OrderInfoVO;
import com.wuji.admin.client.wecom.model.PermanentInfoVO;
import com.wuji.admin.client.wecom.model.SuiteTokenRequest;
import com.wuji.admin.client.wecom.model.SuiteTokenVO;
import com.wuji.admin.client.wecom.model.UserInfoByCodeResult;
import com.wuji.admin.client.wecom.model.UserInfoThirdVO;
import com.wuji.admin.client.wecom.model.UserListResult;
import com.wuji.admin.client.wecom.model.WeComCreateOrderRequest;
import com.wuji.admin.client.wecom.model.WeComCreateOrderVO;
import com.wuji.admin.client.wecom.model.WeComUserIdRequest;
import com.wuji.admin.client.wecom.model.WeComUserIdVO;
import com.wuji.admin.client.wecom.model.WeComUserInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "weComClient", url = "${wecom.server:https://qyapi.weixin.qq.com}",
        configuration = WeComFeignConfiguration.class)
public interface WeComClient {

    @GetMapping("/cgi-bin/gettoken")
    WeComResult getAccessToken(@RequestParam("corpid") String corpid, @RequestParam("corpsecret") String corpsecret);

    @GetMapping("/cgi-bin/department/list")
    DepartmentListResult departmentList(@RequestParam("access_token") String access_token,
                                        @RequestParam("id") String id);

    @GetMapping("/cgi-bin/department/get")
    DepartmentInfoResult departmentInfo(@RequestParam("access_token") String access_token,
                                        @RequestParam("id") String id);

    @GetMapping("/cgi-bin/user/list")
    UserListResult userList(@RequestParam("access_token") String access_token,
                            @RequestParam("department_id") String id);


    @GetMapping("/cgi-bin/user/get")
    WeComUserInfoVO userInfo(@RequestParam("access_token") String access_token, @RequestParam("userid") String userid);


    @GetMapping("/cgi-bin/auth/getuserinfo")
    UserInfoByCodeResult getInfoByAccessToken(@RequestParam("access_token") String access_token,
                                              @RequestParam("code") String code);

    @GetMapping("/cgi-bin/service/miniprogram/jscode2session")
    UserInfoByCodeResult getInfoBySuiteAccessToken(@RequestParam("suite_access_token") String suite_access_token,
                                                   @RequestParam("js_code") String js_code,
                                                   @RequestParam("grant_type") String authorization_code);

    @PostMapping("/cgi-bin/message/send")
    JSONObject sendAppMessage(@RequestParam("access_token") String access_token,
                              @RequestBody AppMessageRequest appMessageRequest);

    @PostMapping("/cgi-bin/service/v2/get_permanent_code")
    PermanentInfoVO getPermanentCode(@RequestParam("suite_access_token") String suite_access_token,
                                     @RequestBody Map<String, String> permanentMap);


    @PostMapping("/cgi-bin/service/v2/get_auth_info")
    AuthInfoVO getAuthInfo(@RequestParam("suite_access_token") String suite_access_token,
                           @RequestBody AuthInfoRequest authInfoRequest);


    @PostMapping("/cgi-bin/service/get_suite_token")
    SuiteTokenVO getSuiteToken(@RequestBody SuiteTokenRequest suiteTokenRequest);

    @PostMapping("/cgi-bin/service/get_corp_token")
    CorpTokenVO getCorpToken(@RequestParam("suite_access_token") String suite_access_token,
                             @RequestBody CorpTokenRequest corpTokenRequest);

    @GetMapping("/cgi-bin/service/auth/getuserinfo3rd")
    UserInfoThirdVO userInfoThird(@RequestParam("suite_access_token") String suite_access_token,
                                  @RequestParam("code") String code);


    @PostMapping("/cgi-bin/ticket/get")
    JsApiTicketVO getTicket(@RequestParam("access_token") String access_token, @RequestParam("type") String type);

    @GetMapping("/cgi-bin/get_jsapi_ticket")
    JsApiTicketVO getCompanyJsApiTicket(@RequestParam("access_token") String access_token);

    @PostMapping("/cgi-bin/service/get_order")
    OrderInfoVO getOrderInfo(@RequestBody OrderInfoRequest orderInfoRequest, @RequestParam String suite_access_token);

    @PostMapping("/cgi-bin/user/getuserid")
    WeComUserIdVO getUserId(@RequestBody WeComUserIdRequest weComUserIdRequest,
                            @RequestParam("access_token") String access_token);

    @PostMapping("/cgi-bin/license/create_new_order")
    WeComCreateOrderVO crateNewOrder(@RequestBody WeComCreateOrderRequest weComCreateOrderRequest,
                                     @RequestParam("provider_access_token") String provider_access_token);
}
