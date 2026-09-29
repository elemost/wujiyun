package com.wuji.wechat.controller;

import com.wuji.wechat.model.vo.WechatMpSignatureVO;
import com.wuji.wechat.service.WechatMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/wechat/mp")
@RestController
public class WechatMpSignatureController {

    @Autowired
    private WechatMpService wechatMpService;

    @GetMapping("/signature")
    public WechatMpSignatureVO getSignature(@RequestParam("url") String url) {
        return wechatMpService.getSignature(url);
    }
}
