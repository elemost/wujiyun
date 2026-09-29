package com.wuji.wechat.controller;

import com.wuji.wechat.model.request.WeChatQrCodeRequest;
import com.wuji.wechat.service.WeChatMiniAppService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RequestMapping("/mini/app")
@RestController
public class WeChatMiniAppController {

    @Autowired
    private WeChatMiniAppService weChatMiniAppService;

    @ApiOperation("获取微信二维码")
    @PostMapping("/getQrcode")
    public void getQrcode(@RequestBody WeChatQrCodeRequest weChatQrCodeRequest,
                          HttpServletResponse httpServletResponse) {
        weChatMiniAppService.getQrcode(weChatQrCodeRequest, httpServletResponse);
    }
}
