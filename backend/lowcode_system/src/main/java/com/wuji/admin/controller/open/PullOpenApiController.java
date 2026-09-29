package com.wuji.admin.controller.open;

import com.alibaba.fastjson.JSONObject;
import com.wuji.admin.cache.WeComCache;
import com.wuji.admin.enums.CompanyDataSourceEnum;
import com.wuji.admin.handler.PullDataContext;
import com.wuji.admin.model.vo.CompanyVO;
import com.wuji.admin.model.vo.JsapiAuthVO;
import com.wuji.admin.service.CompanyService;
import com.wuji.admin.service.DingTalkService;
import com.wuji.admin.service.LarkService;
import com.wuji.admin.service.WeComService;
import com.wuji.common.model.DingResponse;
import com.wuji.common.model.Response;
import com.wuji.common.model.WeComResponse;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/open/api")
@Slf4j
public class PullOpenApiController {

    @Autowired
    private PullDataContext pullDataContext;

    @Autowired
    private LarkService larkService;

    @Autowired
    private DingTalkService dingTalkService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private WeComService weComServiceImpl;


    @ApiOperation("接收钉钉信息")
    @PostMapping("/ding/talk/{clientId}/subscribe")
    public DingResponse subscribe(HttpServletRequest request,
                                  @RequestParam(value = "msg_signature", required = false) String msg_signature,
                                  @RequestParam(value = "timestamp", required = false) String timeStamp,
                                  @RequestParam(value = "nonce", required = false) String nonce,
                                  @RequestBody(required = false) JSONObject json, @PathVariable String clientId) {
        log.info("msg_signature:" + msg_signature);
        log.info("timestamp:" + timeStamp);
        log.info("nonce:" + nonce);
        log.info("json:" + json);
        return DingResponse.success(pullDataContext.getHandler(CompanyDataSourceEnum.DING_TALK.name())
                .subscribe(msg_signature, timeStamp, nonce, json, clientId));
    }


    @ApiOperation("接收飞书信息")
    @PostMapping("/lark/{secretId}/subscribe")
    public DingResponse larkSubscribe(HttpServletRequest request,
                                      @RequestHeader(value = "X-Lark-Signature", required = false) String msg_signature,
                                      @RequestHeader(value = "X-Lark-Request-Timestamp", required = false)
                                      String timeStamp,
                                      @RequestHeader(value = "X-Lark-Request-Nonce", required = false) String nonce,
                                      @RequestBody(required = false) JSONObject json, @PathVariable String secretId) {
        return DingResponse.success(pullDataContext.getHandler(CompanyDataSourceEnum.LARK.name())
                .subscribe(msg_signature, timeStamp, nonce, json, secretId));
    }

    @ApiOperation("鉴权")
    @GetMapping("/jsapiAuth/{secretId}")
    public JsapiAuthVO larkSubscribe(@PathVariable String secretId, @RequestParam("url") String url) {
        return larkService.jsapiAuth(url, secretId);
    }

    @ApiOperation("鉴权")
    @GetMapping("/dingTalk/jsapiAuth/{secretId}")
    public JsapiAuthVO dingTalkSubscribe(@PathVariable String secretId) {
        return dingTalkService.jsapiAuth(secretId);
    }


    @ApiOperation("接收企业微信信息")
    @GetMapping("/weCom/{secretId}/subscribe")
    public WeComResponse weComSubscribe(@RequestParam(value = "msg_signature", required = false) String msg_signature,
                                        @RequestParam(value = "timestamp", required = false) String timestamp,
                                        @RequestParam(value = "echostr", required = false) String echostr,
                                        @RequestParam(value = "nonce") String nonce, @PathVariable String secretId) {
        JSONObject json = new JSONObject();
        json.put("echostr", echostr);
        return WeComResponse.success(Long.valueOf(pullDataContext.getHandler(CompanyDataSourceEnum.WECOM.name())
                .subscribe(msg_signature, timestamp, nonce, json, secretId).get("decrypt").toString()));
    }

    @ApiOperation("接收企业微信信息")
    @PostMapping("/weCom/{secretId}/subscribe")
    public void weComSubscribePost(@RequestParam(value = "msg_signature", required = false) String msg_signature,
                                   @RequestParam(value = "timestamp", required = false) String timestamp,
                                   @RequestBody(required = false) String echostr,
                                   @RequestParam(value = "nonce") String nonce, @PathVariable String secretId) {
        JSONObject json = new JSONObject();
        json.put("echostr", echostr);
        weComServiceImpl.receive(msg_signature, timestamp, nonce, json, secretId);
    }

    @ApiOperation("接收企业微信信息")
    @GetMapping("/weCom/token/{secretId}")
    public Response<String> weComSubscribePost(@PathVariable String secretId) {
        CompanyVO companyVO = companyService.getBySecretId(secretId);
        return Response.success(WeComCache.getAccessToken(companyVO.getCompanyId()));
    }
}
