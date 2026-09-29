package com.wuji.admin.model.vo;

import lombok.Data;

/**
 * 支付
 * @author caijiquan
 */
@Data
public class PayVO {
    /**
     * APP,JSAPI,NATIVE,MWEB
     */
    private String tradeType;
    private Integer companyId;
    private String channelId;
    private String companyOrderNo;
    private String openId;
    private Long amount;
    private String clientIp;
    /**
     * 商品标题
     *
     * @mbggenerated
     */
    private String subject;

    /**
     * 商品描述信息
     *
     * @mbggenerated
     */
    private String body;

    /**
     * 特定渠道发起时额外参数
     *
     * @mbggenerated
     */
    private String extra;

    /**
     * 交易起始时间
     */
    private String timeStart;

    /**
     * 交易结束时间
     */
    private String timeExpire;

}
