package com.wuji.wechat.wx.mp;


import com.wuji.wechat.wx.mp.config.WxMpConfiguration;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Binary Wang
 */
@Component
public class MsgMpHandler extends AbstractMpHandler {

  @Override
  public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                  Map<String, Object> context, WxMpService wxMpService,
                                  WxSessionManager sessionManager) {

    WxMpConfiguration weixinService = (WxMpConfiguration) wxMpService;

    if (!wxMessage.getMsgType().equals(WxConsts.XmlMsgType.EVENT)) {
    }

    //当用户输入关键词如“你好”，“客服”等，并且有客服在线时，把消息转发给在线客服
    if (StringUtils.startsWithAny(wxMessage.getContent(), "你好", "客服")
        && weixinService.hasKefuOnline()) {
      return WxMpXmlOutMessage
          .TRANSFER_CUSTOMER_SERVICE().fromUser(wxMessage.getToUser())
          .toUser(wxMessage.getFromUser()).build();
    }

    String content = "回复信息内容";
    return new TextBuilder().build(content, wxMessage, weixinService);

  }

}
