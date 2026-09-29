package com.wuji.wechat.service.impl;


import cn.binarywang.wx.miniapp.api.WxMaQrcodeService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import com.wuji.common.enums.ResultCode;
import com.wuji.common.exception.BizException;
import com.wuji.wechat.model.request.WeChatQrCodeRequest;
import com.wuji.wechat.model.vo.WeChatMiniAppLoginVO;
import com.wuji.wechat.service.WeChatMiniAppService;
import com.wuji.wechat.wx.miniapp.config.WxMaConfiguration;
import com.wuji.wechat.wx.miniapp.config.WxMaProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;

@Service
@Slf4j
public class WeChatMiniAppServiceImpl implements WeChatMiniAppService {

    @Autowired
    private WxMaProperties wxMaProperties;

    @Override
    public WeChatMiniAppLoginVO getLoginInfo(String code) {
        WxMaProperties.Config config = wxMaProperties.getConfigs().get(0);
        final WxMaService wxService = WxMaConfiguration.getCpService(config.getAppid());
        WeChatMiniAppLoginVO weChatMiniAppLoginVO = new WeChatMiniAppLoginVO();
        try {
            WxMaJscode2SessionResult session = wxService.getUserService().getSessionInfo(code);
            String openid = session.getOpenid();
            String unionid = session.getUnionid();
            weChatMiniAppLoginVO.setOpenId(openid);
            weChatMiniAppLoginVO.setUnionId(unionid);
        } catch (Exception e) {
            log.error("解析企业微信失败", e);
            throw new BizException(ResultCode.WX_LOGIN_ERROR);
        }
        return weChatMiniAppLoginVO;
    }

    @Override
    public String getMobile(String phoneCode) {
        try {
            WxMaProperties.Config config = wxMaProperties.getConfigs().get(0);
            final WxMaService wxService = WxMaConfiguration.getCpService(config.getAppid());
            WxMaPhoneNumberInfo phoneNoInfo = wxService.getUserService().getPhoneNoInfo(phoneCode);
            return phoneNoInfo.getPhoneNumber();
        } catch (Exception e) {
            log.error("解析企业微信失败", e);
        }
        return null;
    }

    @Override
    public void getQrcode(WeChatQrCodeRequest weChatQrCodeRequest, HttpServletResponse httpServletResponse) {
        try {
            WxMaProperties.Config config = wxMaProperties.getConfigs().get(0);
            final WxMaService wxService = WxMaConfiguration.getCpService(config.getAppid());
            WxMaQrcodeService qrcodeService = wxService.getQrcodeService();
            byte[] wxaCodeUnlimitBytes = qrcodeService.createWxaCodeUnlimitBytes(weChatQrCodeRequest.getScene(),
                    weChatQrCodeRequest.getPage(), weChatQrCodeRequest.getCheck_path(),
                    weChatQrCodeRequest.getEnv_version(), 430, false, null, false);
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(wxaCodeUnlimitBytes);) {
                IOUtils.copy(inputStream, httpServletResponse.getOutputStream());
            }
        } catch (Exception e) {
            log.error("获取二维码失败", e);
        }

    }
}
