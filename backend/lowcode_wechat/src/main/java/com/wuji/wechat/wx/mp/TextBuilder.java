package com.wuji.wechat.wx.mp;


import com.wuji.wechat.wx.mp.config.WxMpConfiguration;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;

/**
 * 
 * @author Binary Wang
 *
 */
public class TextBuilder extends AbstractBuilder {

  @Override
  public WxMpXmlOutMessage build(String content, WxMpXmlMessage wxMessage,
                                 WxMpConfiguration service)   {
    WxMpXmlOutTextMessage m = WxMpXmlOutMessage.TEXT().content(content)
        .fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser())
        .build();
    return m;
  }

}
