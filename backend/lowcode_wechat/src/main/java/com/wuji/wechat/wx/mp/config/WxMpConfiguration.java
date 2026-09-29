package com.wuji.wechat.wx.mp.config;


import com.google.common.collect.Maps;
import com.wuji.wechat.wx.mp.KfSessionHandler;
import com.wuji.wechat.wx.mp.LocationMpHandler;
import com.wuji.wechat.wx.mp.LogMpHandler;
import com.wuji.wechat.wx.mp.MenuMpHandler;
import com.wuji.wechat.wx.mp.MsgMpHandler;
import com.wuji.wechat.wx.mp.NullMpHandler;
import com.wuji.wechat.wx.mp.StoreCheckNotifyMpHandler;
import com.wuji.wechat.wx.mp.SubscribeMpHandler;
import com.wuji.wechat.wx.mp.UnsubscribeMpHandler;
import lombok.val;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.kefu.result.WxMpKfOnlineList;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import me.chanjar.weixin.mp.constant.WxMpEventConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties(WxMpProperties.class)
public class WxMpConfiguration extends WxMpServiceImpl {

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @Autowired
        protected LogMpHandler logHandler;

        @Autowired
        protected NullMpHandler nullHandler;

        @Autowired
        protected KfSessionHandler kfSessionHandler;

        @Autowired
        protected StoreCheckNotifyMpHandler storeCheckNotifyHandler;

        @Autowired
        private WxMpProperties wxMpProperties;

        @Autowired
        private LocationMpHandler locationHandler;

        @Autowired
        private MenuMpHandler menuHandler;

        @Autowired
        private MsgMpHandler msgHandler;

        @Autowired
        private UnsubscribeMpHandler unsubscribeHandler;

        @Autowired
        private SubscribeMpHandler subscribeHandler;

        private WxMpMessageRouter router;

    @Autowired
    public WxMpConfiguration(LogMpHandler logHandler, NullMpHandler nullHandler, LocationMpHandler locationHandler,
                             MenuMpHandler menuHandler, MsgMpHandler msgHandler, UnsubscribeMpHandler unsubscribeHandler,
                             SubscribeMpHandler subscribeHandler, WxMpProperties properties,KfSessionHandler kfSessionHandler) {
        this.logHandler = logHandler;
        this.nullHandler = nullHandler;
        this.locationHandler = locationHandler;
        this.menuHandler = menuHandler;
        this.msgHandler = msgHandler;
        this.unsubscribeHandler = unsubscribeHandler;
        this.subscribeHandler = subscribeHandler;
        this.kfSessionHandler = kfSessionHandler;
        this.wxMpProperties = properties;
    }

    private static Map<String, WxMpMessageRouter> routers = Maps.newHashMap();
    private static Map<String, WxMpService> mpServices = Maps.newHashMap();


    public static Map<String, WxMpMessageRouter> getRouters() {
        return routers;
    }

    public static WxMpService getMpService(String appId) {
        return mpServices.get(appId);
    }

    @PostConstruct
    public void initServices() {
        mpServices = this.wxMpProperties.getConfigs().stream().map(a -> {
//      val configStorage = new WxMaLettuceRedisConfigImpl(stringRedisTemplate);
            val configStorage = new WxMpDefaultConfigImpl();
            configStorage.setAppId(a.getAppid());
            configStorage.setSecret(a.getSecret());
            configStorage.setToken(a.getToken());
            configStorage.setAesKey(a.getAesKey());
            val service = new WxMpServiceImpl();
            service.setWxMpConfigStorage(configStorage);
            routers.put(a.getAppid(), this.refreshRouter(service));
            return service;
        }).collect(Collectors.toMap(service -> service.getWxMpConfigStorage().getAppId(), a -> a));
    }

        private WxMpMessageRouter refreshRouter(WxMpService wxMpService) {
            final WxMpMessageRouter newRouter = new WxMpMessageRouter(this);

            // 记录所有事件的日志
            newRouter.rule().handler(this.logHandler).next();

            // 接收客服会话管理事件
            newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                    .event(WxMpEventConstants.CustomerService.KF_CREATE_SESSION)
                    .handler(this.kfSessionHandler).end();
            newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                    .event(WxMpEventConstants.CustomerService.KF_CLOSE_SESSION)
                    .handler(this.kfSessionHandler).end();
            newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                    .event(WxMpEventConstants.CustomerService.KF_SWITCH_SESSION)
                    .handler(this.kfSessionHandler).end();

            // 门店审核事件
            newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                    .event(WxMpEventConstants.POI_CHECK_NOTIFY)
                    .handler(this.storeCheckNotifyHandler)
                    .end();

            // 自定义菜单事件
            newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                    .event(WxConsts.MenuButtonType.CLICK).handler(this.menuHandler).end();

            // 点击菜单连接事件
            newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                    .event(WxConsts.MenuButtonType.VIEW).handler(this.nullHandler).end();

            // 关注事件
            newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                    .event(WxConsts.EventType.SUBSCRIBE).handler(this.subscribeHandler)
                    .end();

            // 取消关注事件
            newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                    .event(WxConsts.EventType.UNSUBSCRIBE).handler(this.unsubscribeHandler)
                    .end();

            // 上报地理位置事件
            newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                    .event(WxConsts.EventType.LOCATION).handler(this.locationHandler).end();

            // 接收地理位置消息
            newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.LOCATION)
                    .handler(this.locationHandler).end();

            // 扫码事件
            newRouter.rule().async(false).msgType(WxConsts.XmlMsgType.EVENT)
                    .event(WxConsts.EventType.SCAN).handler(null).end();

            // 默认
            newRouter.rule().async(false).handler(this.msgHandler).end();

            return newRouter;
        }


        public WxMpXmlOutMessage route(WxMpXmlMessage message) {
            try {
                return this.router.route(message);
            } catch (Exception e) {
                this.logger.error(e.getMessage(), e);
            }

            return null;
        }

        public boolean hasKefuOnline() {
            try {
                WxMpKfOnlineList kfOnlineList = this.getKefuService().kfOnlineList();
                return kfOnlineList != null && kfOnlineList.getKfOnlineList().size() > 0;
            } catch (Exception e) {
                this.logger.error("获取客服在线状态异常: " + e.getMessage(), e);
            }

            return false;
        }


}
