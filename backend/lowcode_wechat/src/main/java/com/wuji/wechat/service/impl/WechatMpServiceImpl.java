package com.wuji.wechat.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.wuji.common.exception.BizException;
import com.wuji.common.utils.IdUtils;
import com.wuji.wechat.model.vo.QrUrlVO;
import com.wuji.wechat.model.vo.WechatMpSignatureVO;
import com.wuji.wechat.service.WechatMpService;
import com.wuji.wechat.utils.WechatMpShaUtils;
import com.wuji.wechat.wx.mp.config.WxMpConfiguration;
import com.wuji.wechat.wx.mp.config.WxMpProperties;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.enums.TicketType;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
public class WechatMpServiceImpl implements WechatMpService {

    @Autowired
    private WxMpProperties wxMpProperties;

    @Override
    public QrUrlVO getQrUrl() {
        try {
            final WxMpService mpService = WxMpConfiguration.getMpService(wxMpProperties.getConfigs().get(0).getAppid());
            final WxMpQrCodeTicket wxMpQrCodeTicket =
                    mpService.getQrcodeService().qrCodeCreateTmpTicket(IdUtils.simpleUUID(), 600);
            final String qrUrl = mpService.getQrcodeService().qrCodePictureUrl(wxMpQrCodeTicket.getTicket());
            QrUrlVO qrUrlDto = new QrUrlVO();
            qrUrlDto.setTicket(wxMpQrCodeTicket.getTicket());
            qrUrlDto.setQrUrl(qrUrl);
            return qrUrlDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new BizException("生成二维码失败");
        }
    }

    public String getUserUnionId(String openId) {
        try {
            WxMpService mpService = WxMpConfiguration.getMpService(wxMpProperties.getConfigs().get(0).getAppid());
            WxMpUser wxMpUser = mpService.getUserService().userInfo(openId);
            log.info(JSON.toJSONString(wxMpUser));
            return wxMpUser.getUnionId();
        } catch (WxErrorException e) {
            log.error(Throwables.getStackTraceAsString(e));
        }
        return "";
    }

    @Override
    public WechatMpSignatureVO getSignature(String url) {
        WxMpService mpService = WxMpConfiguration.getMpService(wxMpProperties.getConfigs().get(0).getAppid());
        WechatMpSignatureVO wechatMpSignatureVO = new WechatMpSignatureVO();
        try {
            String jsapi = mpService.getTicket(TicketType.JSAPI);
            long time = new Date().getTime();
            String nonestr = RandomStringUtils.randomAlphanumeric(16);
            String encrypt = WechatMpShaUtils.encrypt(time, jsapi, url, nonestr);
            wechatMpSignatureVO.setAppId(wxMpProperties.getConfigs().get(0).getAppid());
            wechatMpSignatureVO.setTimestamp(time);
            wechatMpSignatureVO.setNonceStr(nonestr);
            wechatMpSignatureVO.setSignature(encrypt);
        } catch (Exception e) {
            log.error("获取ticket失败", e);
        }
        return wechatMpSignatureVO;
    }
}
