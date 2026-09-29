package com.wuji.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "pay")
public class PayConfig {
    /**
     * 支付http
     */
    private static String url;

    /**
     * 渠道编号
     */
    private static String channelCode;

    public static String getChannelCode() {
        return channelCode;
    }

    public static void setChannelCode(String channelCode) {
        PayConfig.channelCode = channelCode;
    }

    public static String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        PayConfig.url = url;
    }

    /**
     * 微信支付统一下单并返回支付配置/api/v1/pay/wx/config
     */
    public static String payUrl()
    {
        return getUrl() + "/v1/pay/wx/config";
    }

    /**
     * 微信支付统一下单并返回支付配置/api/v1/pay/wx/config
     */
    public static String getChannelId()
    {
        return getChannelCode();
    }

}
