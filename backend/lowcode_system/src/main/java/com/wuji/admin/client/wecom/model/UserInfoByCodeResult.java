package com.wuji.admin.client.wecom.model;

import com.wuji.admin.client.wecom.WeComResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserInfoByCodeResult extends WeComResult {
    private String openid;

    private String userid;

    private String external_userid;

    private String user_ticket;

    private String corpid;

    private String session_key;
}
