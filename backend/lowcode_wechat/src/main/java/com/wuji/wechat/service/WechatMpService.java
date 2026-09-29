package com.wuji.wechat.service;

import com.wuji.wechat.model.vo.QrUrlVO;
import com.wuji.wechat.model.vo.WechatMpSignatureVO;

public interface WechatMpService {
    QrUrlVO getQrUrl();

    String getUserUnionId(String openId);

    WechatMpSignatureVO getSignature(String url);
}
