package com.wuji.open.model.request;

import lombok.Data;

@Data
public class FormOpenCommonRequest {

    private String timestamp;

    private String appKey;

    private String appSecret;

    private String nonce;

    private String msgEncrypt;

    private String msgSignature;

    private String dataCreator;
}
