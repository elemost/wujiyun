package com.wuji.admin.client.wecom.model;

import com.wuji.admin.client.wecom.WeComResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrderInfoVO extends WeComResult {
    private String orderid;

    private Integer order_status;

    private Integer order_type;

    private String paid_corpid;

    private String operator_id;

    private String suiteid;

    private String edition_id;

    private String edition_name;

    private BigDecimal price;

    private Integer user_count;

    private Integer order_period;

    private Long order_time;

    private Long paid_time;

    private Long begin_time;

    private Long end_time;

    private Integer order_from;

    private String operator_corpid;

    private BigDecimal service_share_amount;

    private BigDecimal platform_share_amount;

    private BigDecimal dealer_share_amount;

    private Corp dealer_corp_info;

    @Data
    public static class Corp {
        private String corpid;

        private String corp_name;
    }

}


