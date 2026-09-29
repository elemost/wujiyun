package com.wuji.admin.client.lark.model;

import lombok.Data;

@Data
public class LarkUserAssessTokenRequest {
    private String grant_type = "authorization_code";

    private String client_id;

    private String client_secret;

    private String code;

    private String redirect_uri;

    // private String code_verifier = "TxYmzM4PHLBlqm5NtnCmwxMH8mFlRWl_ipie3O0aVzo";
}
