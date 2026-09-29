package com.wuji.admin.client.wecom.model;

import lombok.Data;

@Data
public class WeComCreateOrderRequest {
    private String corpid;

    private String buyer_userid;

    private AccountCount account_count;

    private AccountDuration account_duration;



    @Data
    public static class AccountCount {
        private Integer base_count;

        private Integer external_contact_count;
    }

    @Data
    public static class AccountDuration {
        private Integer months;

        private Integer days;
    }
}
