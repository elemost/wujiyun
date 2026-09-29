package com.wuji.admin.client.wecom.model;

import com.wuji.admin.client.wecom.WeComResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserInfoThirdVO extends WeComResult {
    private String corpid;

    private String userid;

    private String open_userid;

    private String user_ticket;

    private String openid;
}
