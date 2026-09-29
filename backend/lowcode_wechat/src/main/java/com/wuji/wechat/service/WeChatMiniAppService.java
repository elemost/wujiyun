package com.wuji.wechat.service;

import com.wuji.wechat.model.request.WeChatQrCodeRequest;
import com.wuji.wechat.model.vo.WeChatMiniAppLoginVO;

import javax.servlet.http.HttpServletResponse;

public interface WeChatMiniAppService {
    WeChatMiniAppLoginVO getLoginInfo(String code);

    String getMobile(String phoneCode);

    void getQrcode(WeChatQrCodeRequest weChatQrCodeRequest, HttpServletResponse httpServletResponse);
}
